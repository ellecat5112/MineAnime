"""Story mode catalog (Narutopedia Category:Arcs + Category:Movies) for NarutoRPG.

Two stages:
  python3 tools/catalog/story.py --extract <raw crawl.json>
      condenses a raw crawl (arc / movie / episode wikitext, linked characters with their Semantic MediaWiki data,
      jutsu users) into docs/wiki/narutopedia_story.json: arcs -> story beats (title, cleaned text, characters).
  python3 tools/catalog/story.py
      turns docs/wiki/narutopedia_story.json into tools/catalog/generated/story_catalog.json (quests + story characters +
      lang, read by DataGen) and docs/wiki/narutopedia_story.md. Run after ninjutsu.py (it maps character jutsu to the
      abilities those generators created).

Every beat of every arc becomes one quest: talk to the beat's quest giver, then travel to an encounter site and defeat the
beat's enemies alongside its allies (combat beats) or meet the beat's other characters / train (story beats). Canon arcs
form the main story chain Naruto -> Shippuden -> Boruto; anime-only arcs are optional side stories that open alongside the
canon arc they follow; movies open in their era. Rank rewards along the canon chain carry the player from genin to Kage.
"""
import hashlib
import json
import os
import re
import sys
from collections import Counter, OrderedDict

import clans

ROOT = clans.ROOT
NS = clans.NS
DOC_JSON = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_story.json')
GEN = os.path.join(ROOT, 'tools', 'catalog', 'generated')
OUT = os.path.join(GEN, 'story_catalog.json')
ABILITY_CATALOGS = ['clan_catalog.json', 'village_catalog.json', 'nature_catalog.json', 'fanon_genjutsu_catalog.json',
                    'taijutsu_catalog.json', 'ninjutsu_catalog.json', 'schools_catalog.json']

SERIES = ['naruto', 'shippuden', 'boruto', 'movies']
SERIES_NAMES = {'naruto': 'Naruto', 'shippuden': 'Naruto Shippuden', 'boruto': 'Boruto', 'movies': 'Movies'}
SERIES_TIERS = {'naruto': (1, 3), 'shippuden': (3, 10), 'boruto': (10, 16)}
MOVIES = ['Naruto the Movie: Ninja Clash in the Land of Snow', 'Naruto the Movie: Legend of the Stone of Gelel',
          'Naruto the Movie: Guardians of the Crescent Moon Kingdom', 'Naruto Shippūden the Movie',
          'Naruto Shippūden the Movie: Bonds', 'Naruto Shippūden the Movie: The Will of Fire',
          'Naruto Shippūden the Movie: The Lost Tower', 'Naruto the Movie: Blood Prison', 'Road to Ninja: Naruto the Movie',
          'The Last: Naruto the Movie', 'Boruto: Naruto the Movie']
INDEX_PAGES = {'Plot of Naruto': 'an index page summarising the whole series; its arcs are all covered individually'}
DEFAULT_GIVER = {'naruto': 'Hiruzen Sarutobi', 'shippuden': 'Tsunade', 'boruto': 'Naruto Uzumaki', 'movies': 'Kakashi Hatake'}

# ======================================================================================================================
# extraction (raw crawl -> beats)
# ======================================================================================================================


def strip_templates(t):
    out, depth, i = [], 0, 0
    while i < len(t):
        if t.startswith('{{', i):
            # keep the first argument of {{translation|'''X'''|...}} / {{looking for...}} dropped
            j, d = i + 2, 1
            while j < len(t) and d:
                if t.startswith('{{', j): d += 1; j += 2
                elif t.startswith('}}', j): d -= 1; j += 2
                else: j += 1
            inner = t[i + 2:j - 2]
            if inner.lower().startswith('translation|'):
                out.append(inner.split('|')[1])
            i = j
            continue
        out.append(t[i]); i += 1
    return ''.join(out)


def clean(t):
    t = re.sub(r'<ref[^>]*/>', '', t)
    t = re.sub(r'<ref[^>]*>.*?</ref>', '', t, flags=re.S)
    t = re.sub(r'<section[^>]*/>', '', t)
    t = strip_templates(t)
    # files / images (may contain nested links)
    while True:
        m = re.search(r'\[\[(?:File|Image):', t)
        if not m: break
        j, d = m.start() + 2, 1
        while j < len(t) and d:
            if t.startswith('[[', j): d += 1; j += 2
            elif t.startswith(']]', j): d -= 1; j += 2
            else: j += 1
        t = t[:m.start()] + t[j:]
    t = re.sub(r'\[\[Category:[^\]]*\]\]', '', t)
    t = re.sub(r'\[\[[a-z]{2}:[^\]]*\]\]', '', t)
    t = re.sub(r'\[\[([^\]|]*)\|([^\]]*)\]\]', r'\2', t)
    t = re.sub(r'\[\[([^\]]*)\]\]', r'\1', t)
    t = re.sub(r'\[https?://\S+ ([^\]]*)\]', r'\1', t)
    t = re.sub(r"'''?", '', t)
    t = re.sub(r'<[^>]+>', '', t)
    t = re.sub(r'__\w+__', '', t)
    t = re.sub(r'[ \t]+', ' ', t)
    t = re.sub(r'\n\s*\n+', '\n', t)
    return t.strip()


LINK = re.compile(r'\[\[([^\]|#]+)(?:#[^\]|]*)?(?:\|[^\]]*)?\]\]')


CHAR_ALIAS = {'Pain': 'Nagato', 'Six Paths of Pain': 'Nagato', 'Deva Path': 'Nagato', 'Tobi': 'Obito Uchiha',
              'Masked Man': 'Obito Uchiha', 'Kyūbi': 'Kurama', 'Nine-Tails': 'Kurama', 'Shukaku': 'Shukaku'}
LINK_LABEL = re.compile(r'\[\[([^\]|#]+)(?:#[^\]|]*)?(?:\|([^\]]*))?\]\]')


def links(raw, canon, chars):
    out = []
    for m in LINK.findall(raw):
        m = m.strip()
        if ':' in m: continue
        c = CHAR_ALIAS.get(m, canon.get(m, m))
        if c in chars and len(c) > 2: out.append(c)
    return out


def labels(raw, canon, chars):
    """How each linked character is called in the text ([[Nagato|Pain]] -> Nagato: Pain)."""
    out = {}
    for target, label in LINK_LABEL.findall(raw):
        c = CHAR_ALIAS.get(target.strip(), canon.get(target.strip(), target.strip()))
        if c in chars:
            for n in {target.strip(), (label or '').strip()} - {''}:
                out.setdefault(c, set()).add(n)
    return {k: sorted(v) for k, v in out.items()}


def section(text, names):
    """Body of the first == name == section."""
    for n in names:
        m = re.search(r'^==\s*' + re.escape(n) + r'\s*==\s*$', text, re.M)
        if m:
            rest = text[m.end():]
            n2 = re.search(r'^==[^=].*==\s*$', rest, re.M)
            return rest[:n2.start()] if n2 else rest
    return ''


def lead(text):
    m = re.search(r'<section begin=summary\s*/>(.*?)<section end=summary\s*/>', text, re.S)
    if m: return m.group(1)
    first = re.search(r'^==', text, re.M)
    return text[:first.start()] if first else text[:2000]


def extract(raw_path, era_path=None):
    raw = json.load(open(raw_path, encoding='utf-8'))
    era = json.load(open(era_path, encoding='utf-8')) if era_path else {}
    canon, chars = raw['link_canonical'], raw['characters']
    arcs = OrderedDict()
    for title in sorted(raw['arc_text']):
        text = raw['arc_text'][title] or ''
        if title in INDEX_PAGES: continue
        body = section(text, ['Summary', 'Plot', 'Synopsis'])
        subs = list(re.finditer(r'^===\s*([^=]+?)\s*===\s*$', body, re.M))
        beats = []
        if len(subs) >= 2:
            for i, m in enumerate(subs):
                chunk = body[m.end(): subs[i + 1].start() if i + 1 < len(subs) else len(body)]
                chunk = re.split(r'^====', chunk, flags=re.M)[0] if False else chunk
                beats.append({'title': clean(m.group(1)), 'raw': chunk})
        elif raw.get('arc_members', {}).get(title):
            for ep in raw['arc_members'][title]:
                et = raw['member_text'].get(ep, '')
                syn = section(et, ['Synopsis', 'Summary', 'Plot'])
                src = lead(et) if '<section begin=summary' in et else syn
                if clean(src):
                    beats.append({'title': ep, 'raw': src})
        if not beats:
            paras = [p for p in re.split(r'\n\s*\n', body) if len(clean(p)) > 120]
            if not paras and clean(lead(text)):
                paras = [lead(text)]
            n = min(5, len(paras))
            per = max(1, -(-len(paras) // max(1, n)))
            for i in range(0, len(paras), per):
                beats.append({'title': f'Part {len(beats) + 1}', 'raw': '\n\n'.join(paras[i:i + per])})
        # Narutopedia links a name only once per article: collect the arc's cast from every link, then find each member
        # in every beat by any of the names used for them
        cast = {}
        for b in beats + [{'raw': text}]:
            for c, ns in labels(b['raw'], canon, chars).items():
                cast.setdefault(c, set()).update(ns)
        for c, alias in CHAR_ALIAS.items():
            if alias in cast: cast[alias].add(c)
        out_beats = []
        for b in beats:
            body = clean(b['raw'])[:2500]
            found = []
            for c, ns in cast.items():
                keys = {c, c.split()[0]} | {n for n in ns if len(n) > 2} | {n.split()[0] for n in ns if len(n.split()[0]) > 3}
                pos = [m.start() for k in keys for m in re.finditer(r'\b' + re.escape(k) + r'\b', body)]
                if pos: found.append((min(pos), c, len(pos)))
            found.sort()
            out_beats.append({'title': b['title'], 'text': body, 'characters': [c for _, c, _ in found],
                              'mentions': {c: n for _, c, n in found}, 'names': {c: sorted(cast[c]) for _, c, _ in found}})
        arcs[title] = {
            'lead': clean(lead(text))[:1200],
            'movie': title in raw['category_tree'].get('Category:Movies', {}).get('pages', []),
            'beats': out_beats,
        }
    users = {}
    for jt, us in raw['jutsu_users'].items():
        for u in us:
            u = canon.get(u, u)
            if u in chars: users.setdefault(u, []).append(jt)
    used = {c for a in arcs.values() for b in a['beats'] for c in b['characters']} | set(DEFAULT_GIVER.values())
    out = {'source': raw['source'], 'crawled': raw['crawled'], 'method': raw['method'] + '; [[Arc::<arc>]] episode / chapter '
           'synopses for arcs without summary sub-sections',
           'category_tree': raw['category_tree'], 'excluded': INDEX_PAGES, 'arcs': arcs,
           'characters': {c: kg_clean(chars[c]) for c in sorted(used) if c in chars},
           'jutsu': {c: sorted(set(v)) for c, v in sorted(users.items()) if c in used}}
    out['jutsu_era'] = {t: era[t] for t in sorted({t for v in out['jutsu'].values() for t in v}) if t in era}
    with open(DOC_JSON, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False)
    print(f'extracted {len(arcs)} arcs, {sum(len(a["beats"]) for a in arcs.values())} beats, {len(out["characters"])} characters')


def kg_clean(c):
    c = dict(c)
    c['Kekkei Genkai'] = sorted({m for v in c.get('Kekkei Genkai', []) for m in re.findall(r"'fulltext': '([^']+)'", v)}
                                | {v for v in c.get('Kekkei Genkai', []) if not v.startswith('{')})
    return c


# ======================================================================================================================
# generation
# ======================================================================================================================

HOSTILE = {'Gatō Company', 'Akatsuki', 'Otogakure', 'Kara', 'Taka', 'Sound Four', 'Sound Five', 'Mujina Bandits', 'Byakuya Gang',
           'Ōtsutsuki Clan', 'Root', 'Kaguya Clan', 'Shinobi Union', 'Kara Inner', 'Jūgo Group', 'Gold and Silver Brothers',
           'Land of Redaku', 'Seven Ninja Swordsmen of the Mist', 'Hōzuki Castle', 'Moryō', 'Hidden Rain Orphans'}
FRIENDLY_ALWAYS = {'Naruto Uzumaki', 'Sakura Haruno', 'Kakashi Hatake', 'Hinata Hyūga', 'Shikamaru Nara', 'Rock Lee',
                   'Might Guy', 'Boruto Uzumaki', 'Sarada Uchiha', 'Mitsuki', 'Konohamaru Sarutobi', 'Tsunade', 'Jiraiya',
                   'Hiruzen Sarutobi', 'Iruka Umino', 'Sai', 'Yamato', 'Kiba Inuzuka', 'Shino Aburame', 'Chōji Akimichi',
                   'Ino Yamanaka', 'Neji Hyūga', 'Tenten', 'Minato Namikaze', 'Kushina Uzumaki', 'Killer B', 'Shizune',
                   'Asuma Sarutobi', 'Kurenai Yūhi', 'Himawari Uzumaki', 'Kawaki'}
# characters who switch sides: ally in these series / arcs regardless of their record
ALLY_IN = {'Sasuke Uchiha': {'boruto', 'movies', 'naruto', 'Fourth Shinobi World War: Climax', "Birth of the Ten-Tails' Jinchūriki",
                             'Kaguya Ōtsutsuki Strikes'},
           'Gaara': {'shippuden', 'boruto', 'movies'}, 'Kankurō': {'shippuden', 'boruto', 'movies'},
           'Temari': {'shippuden', 'boruto', 'movies'}, 'Orochimaru': {'boruto'}, 'Kabuto Yakushi': {'boruto'},
           'Itachi Uchiha': {'Fourth Shinobi World War: Confrontation'}, 'Jūgo': {'boruto'}, 'Suigetsu Hōzuki': {'boruto'},
           'Karin': {'boruto'}, 'Obito Uchiha': {'Kaguya Ōtsutsuki Strikes'}, 'Nagato': {'Fourth Shinobi World War: Confrontation'},
           'Kurama': {'shippuden', 'boruto'}}
FIGHT = re.compile(r'\b(fight|fought|battle|attack|defeat|kill|clash|confront|ambush|duel|captur|assault|against|pursu|chase|'
                   r'invad|strike|struck|overpower|subdue|kidnap|abduct|ravag|destroy)', re.I)
VILLAGE_HEADBAND = {'Konohagakure': 0x1E3A8A, 'Sunagakure': 0x5D4037, 'Kirigakure': 0x37474F, 'Kumogakure': 0x263238,
                    'Iwagakure': 0x6D4C41, 'Otogakure': 0x4A148C, 'Amegakure': 0x455A64, 'Takigakure': 0x1B5E20,
                    'Kusagakure': 0x33691E, 'Hoshigakure': 0x283593, 'Yukigakure': 0x90A4AE}
CLAN_LOOK = {
    'Uchiha Clan': dict(hair=0x111318, top=0x1C2541, accent=0xC62828, hair_style='spiky'),
    'Hyūga Clan': dict(hair=0x2A2A33, top=0xEDE7DC, accent=0x7E57C2, hair_style='long', mark='pale_eyes', eyes=0xE1DCEC),
    'Akimichi Clan': dict(hair=0x8D3B1E, top=0xB71C1C, accent=0x5D4037, outfit='armor', hair_style='spiky'),
    'Nara Clan': dict(hair=0x1E1E1E, top=0x546E7A, accent=0x33691E, hair_style='ponytail'),
    'Yamanaka Clan': dict(hair=0xF2D16B, top=0x6A1B9A, accent=0x8E24AA, hair_style='ponytail'),
    'Inuzuka Clan': dict(hair=0x4E342E, top=0x5D4037, accent=0x9E9E9E, hair_style='spiky', mark='fangs'),
    'Aburame Clan': dict(hair=0x3E2723, top=0x546E7A, accent=0x37474F, outfit='robe', mark='glasses'),
    'Uzumaki Clan': dict(hair=0xC62828, top=0xE65100, accent=0x1A237E, hair_style='long'),
    'Senju Clan': dict(hair=0x4E342E, top=0xB71C1C, accent=0x212121, outfit='armor', hair_style='long'),
    'Sarutobi Clan': dict(hair=0x3E2723, top=0x2E7D32, accent=0x1B5E20, outfit='jacket'),
    'Hatake Clan': dict(hair=0xCFD8DC, top=0x1E2A44, accent=0x4CAF50, outfit='jacket', hair_style='spiky', mark='mask'),
    'Ōtsutsuki Clan': dict(hair=0xF5F5F5, top=0xFAFAFA, accent=0xB0BEC5, outfit='robe', hair_style='long', mark='pale_eyes'),
    'Kaguya Clan': dict(hair=0xECEFF1, top=0x4A148C, accent=0x7B1FA2, outfit='bodysuit', hair_style='long'),
    'Hōzuki Clan': dict(hair=0xCFE8F5, top=0x7E57C2, accent=0x4527A0, hair_style='short'),
}
AFFIL_LOOK = {
    'Akatsuki': dict(outfit='cloak', top=0x101014, accent=0xD32F2F),
    'Kara': dict(outfit='cloak', top=0x1A1A1A, accent=0xF5F5F5),
    'Otogakure': dict(outfit='bodysuit', top=0x4E3B6E, accent=0x7E57C2),
    'Konohagakure': dict(outfit='jacket', top=0x1F3A5F, accent=0x4E7A3A),
    'Sunagakure': dict(outfit='robe', top=0xA1887F, accent=0x6D4C41),
    'Kirigakure': dict(outfit='jacket', top=0x37474F, accent=0x546E7A),
    'Kumogakure': dict(outfit='jacket', top=0xFAFAFA, accent=0xFFB300),
    'Iwagakure': dict(outfit='jacket', top=0x8D6E63, accent=0xB71C1C),
    'Root': dict(outfit='bodysuit', top=0x212121, accent=0x616161),
    'Land of Iron': dict(outfit='armor', top=0x455A64, accent=0x90A4AE),
}
# hand-tuned looks for the main cast: (hair, hair_style, outfit, top, accent, pants, eyes, mark, headband village)
MAIN = {
    'Naruto Uzumaki': (0xFFC928, 'spiky', 'jacket', 0xF57C00, 0x1A237E, 0xF57C00, 0x1E88E5, 'whiskers', 'Konohagakure'),
    'Sasuke Uchiha': (0x15171E, 'spiky', 'shirt', 0x1C2541, 0x90A4AE, 0xE0E0E0, 0x111111, None, 'Konohagakure'),
    'Sakura Haruno': (0xF48FB1, 'short', 'shirt', 0xC62828, 0xF5F5F5, 0x37474F, 0x43A047, 'forehead', 'Konohagakure'),
    'Kakashi Hatake': (0xCFD8DC, 'spiky', 'jacket', 0x1E2A44, 0x4E7A3A, 0x1E2A44, 0x111111, 'mask', 'Konohagakure'),
    'Rock Lee': (0x111111, 'bowl', 'bodysuit', 0x2E7D32, 0xFB8C00, 0x2E7D32, 0x111111, None, 'Konohagakure'),
    'Might Guy': (0x111111, 'bowl', 'bodysuit', 0x2E7D32, 0xFB8C00, 0x2E7D32, 0x111111, None, 'Konohagakure'),
    'Hinata Hyūga': (0x1E1B3A, 'long', 'jacket', 0xD1C4E9, 0x5E35B1, 0x283593, 0xE1DCEC, 'pale_eyes', 'Konohagakure'),
    'Neji Hyūga': (0x2A2A33, 'long', 'robe', 0xEDE7DC, 0x5D4037, 0x37474F, 0xE1DCEC, 'pale_eyes', 'Konohagakure'),
    'Gaara': (0xB71C1C, 'short', 'robe', 0x8D3B1E, 0x5D4037, 0x3E2723, 0x80CBC4, 'forehead_kanji', 'Sunagakure'),
    'Itachi Uchiha': (0x15171E, 'long', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0xC62828, 'red_eyes', 'Konohagakure'),
    'Orochimaru': (0x111111, 'long', 'robe', 0xF5F5F5, 0x6A1B9A, 0x6A1B9A, 0xF9A825, None, 'Otogakure'),
    'Jiraiya': (0xF5F5F5, 'wild', 'robe', 0x2E7D32, 0xC62828, 0xC62828, 0x111111, 'paint', None),
    'Tsunade': (0xF2D16B, 'ponytail', 'robe', 0x7CB342, 0x9E9E9E, 0x263238, 0x8D6E63, 'forehead', None),
    'Hiruzen Sarutobi': (0x9E9E9E, 'short', 'robe', 0xFAFAFA, 0xC62828, 0x37474F, 0x3E2723, None, None),
    'Shikamaru Nara': (0x1E1E1E, 'ponytail', 'jacket', 0x4E7A3A, 0x546E7A, 0x263238, 0x111111, None, 'Konohagakure'),
    'Zabuza Momochi': (0x1E1E1E, 'short', 'bodysuit', 0x78909C, 0x546E7A, 0x607D8B, 0x111111, 'mask', 'Kirigakure'),
    'Haku': (0x111111, 'long', 'robe', 0x8BC34A, 0x33691E, 0x2E7D32, 0x3E2723, None, None),
    'Pain': (0xE65100, 'spiky', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0xB39DDB, 'rinnegan', 'Amegakure'),
    'Nagato': (0xC62828, 'long', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0xB39DDB, 'rinnegan', 'Amegakure'),
    'Madara Uchiha': (0x111318, 'wild', 'armor', 0xB71C1C, 0x3E2723, 0x263238, 0xC62828, 'red_eyes', None),
    'Obito Uchiha': (0x111318, 'short', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0xC62828, 'red_eyes', None),
    'Kaguya Ōtsutsuki': (0xF5F5F5, 'long', 'robe', 0xFAFAFA, 0xB0BEC5, 0xFAFAFA, 0xE1DCEC, 'pale_eyes', None),
    'Boruto Uzumaki': (0xF2D16B, 'spiky', 'jacket', 0x111111, 0xC62828, 0x37474F, 0x1E88E5, 'whiskers', 'Konohagakure'),
    'Sarada Uchiha': (0x111111, 'short', 'shirt', 0xC62828, 0xFAFAFA, 0x111111, 0x111111, 'glasses', 'Konohagakure'),
    'Mitsuki': (0xB3E5FC, 'short', 'robe', 0xFAFAFA, 0x7E57C2, 0x37474F, 0xF9A825, None, 'Konohagakure'),
    'Kawaki': (0x111111, 'spiky', 'shirt', 0x212121, 0x5D4037, 0x263238, 0x9E9E9E, None, None),
    'Deidara': (0xF2D16B, 'ponytail', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0x29B6F6, None, 'Iwagakure'),
    'Sasori': (0xB71C1C, 'short', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0x795548, None, 'Sunagakure'),
    'Hidan': (0xCFD8DC, 'short', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0xB39DDB, None, 'Yugakure'),
    'Kakuzu': (0x3E2723, 'short', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0x4CAF50, 'mask', 'Takigakure'),
    'Kisame Hoshigaki': (0x1E3A8A, 'spiky', 'cloak', 0x101014, 0xD32F2F, 0x263238, 0xFAFAFA, None, 'Kirigakure'),
    'Killer B': (0xF5F5F5, 'short', 'jacket', 0xFAFAFA, 0x795548, 0x263238, 0x111111, 'glasses', 'Kumogakure'),
    'Momoshiki Ōtsutsuki': (0xF5F5F5, 'long', 'robe', 0xFAFAFA, 0xB71C1C, 0xFAFAFA, 0xB39DDB, 'pale_eyes', None),
    'Isshiki Ōtsutsuki': (0x111111, 'short', 'robe', 0xFAFAFA, 0x9E9E9E, 0x37474F, 0xE1DCEC, 'pale_eyes', None),
    'Kabuto Yakushi': (0xCFD8DC, 'ponytail', 'shirt', 0x6A1B9A, 0x9E9E9E, 0x37474F, 0x111111, 'glasses', 'Otogakure'),
    'Iruka Umino': (0x3E2723, 'ponytail', 'jacket', 0x1F3A5F, 0x4E7A3A, 0x1F3A5F, 0x111111, 'scar', 'Konohagakure'),
    'Minato Namikaze': (0xFFD54F, 'spiky', 'jacket', 0xFAFAFA, 0xC62828, 0x1F3A5F, 0x1E88E5, None, 'Konohagakure'),
}
NATURE_COLORS = {'Fire Release': 0xC62828, 'Water Release': 0x1565C0, 'Wind Release': 0x2E7D32, 'Lightning Release': 0xF9A825,
                 'Earth Release': 0x6D4C41}
HAIR_PALETTE = [0x1E1E1E, 0x3E2723, 0x5D4037, 0x8D6E63, 0xF2D16B, 0xC62828, 0x9E9E9E, 0x263238, 0x4E342E, 0xE0E0E0]
TOP_PALETTE = [0x37474F, 0x455A64, 0x5D4037, 0x6D4C41, 0x1B5E20, 0x283593, 0x4A148C, 0x880E4F, 0x263238, 0x795548]


def h(name, n):
    return int(hashlib.md5(name.encode()).hexdigest(), 16) % n


def slug(name):
    return clans.slug(name)


BEAST_MODELS = {'Shukaku': 'shukaku', 'Matatabi': 'matatabi', 'Isobu': 'isobu', 'Son Gokū': 'son_goku', 'Kokuō': 'kokuo',
                'Saiken': 'saiken', 'Chōmei': 'chomei', 'Gyūki': 'gyuki', 'Kurama': 'kurama', 'Ten-Tails': 'ten_tails'}


def appearance(name, info):
    affs = info.get('Affiliation', [])
    if name in BEAST_MODELS:   # tailed beasts are drawn with their low-poly model (tools/catalog/beast_models.py)
        return dict(model=clans.ns(BEAST_MODELS[name]))
    if name in MAIN:
        hair, style, outfit, top, accent, pants, eyes, mark, band = MAIN[name]
        a = dict(skin=0xF1C27D, eyes=eyes, hair=hair, hair_style=style, outfit=outfit, top=top, accent=accent, pants=pants)
        if band: a['headband'] = VILLAGE_HEADBAND.get(band, 0x1E3A8A)
        if mark: a['mark'] = mark
        return a
    female = 'Female' in info.get('Gender', [])
    a = dict(skin=[0xF1C27D, 0xE0AC69, 0xC68642, 0xFFDBAC][h(name + 's', 4)], eyes=[0x3B2A1A, 0x1E88E5, 0x111111, 0x43A047][h(name + 'e', 4)],
             hair=HAIR_PALETTE[h(name, len(HAIR_PALETTE))], hair_style='long' if female else ['short', 'spiky', 'short'][h(name + 'h', 3)],
             outfit='shirt', top=TOP_PALETTE[h(name + 't', len(TOP_PALETTE))], accent=TOP_PALETTE[h(name + 'a', len(TOP_PALETTE))],
             pants=[0x263238, 0x37474F, 0x3E2723, 0x1A237E][h(name + 'p', 4)])
    for aff in affs:
        if aff in AFFIL_LOOK:
            a.update(AFFIL_LOOK[aff]); break
    for clan in info.get('Clan', []):
        if clan in CLAN_LOOK:
            look = dict(CLAN_LOOK[clan])
            if 'Akatsuki' in affs or 'Kara' in affs:   # keep the organisation cloak
                for k in ('outfit', 'top', 'accent'): look.pop(k, None)
            a.update(look); break
    kg = info.get('Kekkei Genkai', [])
    if any('Rinnegan' in k for k in kg): a['mark'] = 'rinnegan'
    elif any('Sharingan' in k for k in kg): a['mark'] = 'red_eyes'
    elif any('Byakugan' in k for k in kg): a['mark'] = 'pale_eyes'
    for aff in affs:
        if aff in VILLAGE_HEADBAND:
            a['headband'] = VILLAGE_HEADBAND[aff]; break
    if 'Samurai' in info.get('Classification', []): a.update(outfit='armor', top=0x455A64, accent=0x90A4AE); a.pop('headband', None)
    return a


def is_hostile_record(name, info):
    affs = set(info.get('Affiliation', []))
    return bool(affs & HOSTILE) or 'Missing-nin' in info.get('Classification', [])


def in_fight(name, beat_text, names=()):
    keys = {name.split()[0]} | {n.split()[0] for n in names if len(n) > 2}
    return any(FIGHT.search(sentence) and any(k in sentence for k in keys) for sentence in re.split(r'(?<=[.!?])\s+', beat_text))


SHINOBI = set()   # characters with known jutsu (filled in main)
# antagonists of an arc whose wiki record is not hostile (they are enemies there, whatever the sentence wording)
ENEMY_IN = {'Gatō': {'Prologue — Land of Waves'}, 'Haku': {'Prologue — Land of Waves'}, 'Zabuza Momochi': {'Prologue — Land of Waves'},
            'Gaara': {'Chūnin Exams (Arc)', 'Konoha Crush (Arc)'}, 'Kankurō': {'Chūnin Exams (Arc)', 'Konoha Crush (Arc)'},
            'Temari': {'Chūnin Exams (Arc)', 'Konoha Crush (Arc)'}, 'Neji Hyūga': {'Chūnin Exams (Arc)'},
            'Kimimaro': {'Sasuke Recovery Mission'}, 'Sasuke Uchiha': {'Sasuke Recovery Mission'}}


def side(name, info, series, arc, beat_text, names=()):
    """'ally', 'enemy' or 'none' (mentioned only, e.g. a villain in a flashback) for this character in this beat."""
    if name in ENEMY_IN and arc in ENEMY_IN[name]: return 'enemy' if in_fight(name, beat_text, names) else 'none'
    if name in FRIENDLY_ALWAYS: return 'ally'
    if name in ALLY_IN and (series in ALLY_IN[name] or arc in ALLY_IN[name]): return 'ally'
    fights = in_fight(name, beat_text, names)
    if is_hostile_record(name, info): return 'enemy' if fights else 'none'
    affs = set(info.get('Affiliation', []))
    friendly = {'Konohagakure', 'Land of Fire'} | ({'Allied Shinobi Forces', 'Sunagakure', 'Kumogakure', 'Kirigakure', 'Iwagakure'}
                                                     if series in ('shippuden', 'boruto', 'movies') else set())
    if affs & friendly: return 'ally'
    if fights and name in SHINOBI: return 'enemy'
    return 'ally'


def human(info):
    return info.get('Species', ['Human'])[:1] in (['Human'], [])


MENTORS = {'naruto': ['Kakashi Hatake', 'Iruka Umino', 'Hiruzen Sarutobi', 'Jiraiya', 'Tsunade', 'Might Guy', 'Asuma Sarutobi',
                      'Kurenai Yūhi', 'Shikamaru Nara'],
           'shippuden': ['Tsunade', 'Kakashi Hatake', 'Yamato', 'Jiraiya', 'Shikamaru Nara', 'Gaara', 'Might Guy', 'Naruto Uzumaki'],
           'boruto': ['Naruto Uzumaki', 'Konohamaru Sarutobi', 'Sasuke Uchiha', 'Shikamaru Nara', 'Sakura Uchiha'],
           'movies': ['Kakashi Hatake', 'Tsunade', 'Naruto Uzumaki', 'Jiraiya']}
ERA = {'naruto': 0, 'shippuden': 1, 'boruto': 2}


def sentences(text):
    return [s.strip() for s in re.split(r'(?<=[.!?])\s+(?=[A-Z"])', text.replace('\n', ' ')) if len(s.strip()) > 20]


def short(text, limit):
    if len(text) <= limit: return text
    cut = text[:limit].rsplit(' ', 1)[0]
    return cut.rstrip(',;:') + '...'


# manga side stories with no anime episode order of their own: (series, order)
PLACE = {'Kakashi Gaiden': ('shippuden', 118),
         'Sasuke Retsuden: The Uchiha Descendants and the Heavenly Stardust (manga)': ('boruto', 281)}
CANON = {'Kakashi Gaiden', 'Naruto Gaiden: The Seventh Hokage and the Scarlet Spring',
         'Sasuke Retsuden: The Uchiha Descendants and the Heavenly Stardust (manga)'}


def series_of(arc, a):
    if a['movie']: return 'movies'
    if arc in PLACE: return PLACE[arc][0]
    L = a['lead']
    if 'Boruto' in L or 'Two Blue Vortex' in L or 'Seventh Hokage and the Scarlet Spring' in arc: return 'boruto'
    if 'Shippūden' in L or 'Part II' in L or 'Shippuden' in L: return 'shippuden'
    return 'naruto'


def arc_order(arc, a, series):
    L = a['lead']
    if series == 'movies': return MOVIES.index(arc) if arc in MOVIES else 99
    if arc in PLACE: return PLACE[arc][1]
    if 'Seventh Hokage and the Scarlet Spring' in arc: return -1
    ep = re.search(r'episodes?\s+(\d+)', L)
    ch = re.search(r'chapters?\s+(\d+)', L)
    if series == 'boruto' and L.lstrip().startswith('This is an arc of Boruto: Two Blue Vortex'): return 1000 + (int(ch.group(1)) if ch else 0)
    if ep: return int(ep.group(1))
    if ch: return (300 if series == 'boruto' else 0) + int(ch.group(1))
    return 900


def is_filler(a, series):
    if series == 'movies' or a.get('title') in CANON: return False
    L = a['lead']
    return 'anime-only' in L or 'anime-exclusive' in L or ('chapter' not in L and 'novel' not in L.lower())


def load_abilities():
    ab = {}
    for f in ABILITY_CATALOGS:
        p = os.path.join(GEN, f)
        if os.path.exists(p): ab.update(json.load(open(p, encoding='utf-8'))['abilities'])
    return ab


def offensive(defn):
    for a in defn['actions']:
        t = a['type'].split(':')[1]
        if t in ('projectile', 'beam_raycast') or (t in ('aoe_sphere', 'dash_strike', 'area_effect') and a.get('damage', 4 if t == 'aoe_sphere' else 0) > 0):
            return True
    return False


def main():
    data = json.load(open(DOC_JSON, encoding='utf-8'))
    chars, jutsu = data['characters'], data['jutsu']
    abilities = load_abilities()
    jutsu_ids, jutsu_era = {}, {}
    for t, e in data.get('jutsu_era', {}).items():
        jutsu_era[clans.ALIASES.get(t) or clans.slug(t)] = e
    for name, titles in jutsu.items():
        ids = [clans.ALIASES.get(t) or clans.slug(t) for t in titles]
        jutsu_ids[name] = [i for i in ids if i in abilities]
        if jutsu_ids[name]: SHINOBI.add(name)
    nature_attacks = {}
    for aid, d in abilities.items():
        aff = d.get('affinity')
        if aff and offensive(d): nature_attacks.setdefault(aff, []).append((d.get('required_tier', 1), aid))
    for v in nature_attacks.values(): v.sort()

    # ---- arcs in story order
    arcs = []
    for arc, a in data['arcs'].items():
        if not a['beats']: continue
        a['title'] = arc
        s = series_of(arc, a)
        arcs.append((SERIES.index(s), arc_order(arc, a, s), arc, s, a))
    arcs.sort()
    canon_by_series = {s: [x for x in arcs if x[3] == s and not is_filler(x[4], s)] for s in SERIES}

    quests, npcs, lang = OrderedDict(), OrderedDict(), OrderedDict()
    arc_rows = []
    last_canon_quest = {}          # series -> last quest id of the canon chain so far
    first_canon_arc_end = {}       # series -> last quest of its first canon arc
    prev_canon_tail = None         # previous canon quest (chains across series)
    series_tail = {}
    npc_series_tier = {}

    def tier_for(series, arc):
        if series == 'movies':
            return None
        lo, hi = SERIES_TIERS[series]
        canon = [x[2] for x in canon_by_series[series]]
        if arc in canon:
            i = canon.index(arc)
        else:   # filler: tier of the canon arc it follows
            order = [x for x in arcs if x[3] == series]
            idx = [x[2] for x in order].index(arc)
            prior = [x[2] for x in order[:idx] if x[2] in canon]
            i = canon.index(prior[-1]) if prior else 0
        return round(lo + (hi - lo) * i / max(1, len(canon) - 1))

    def npc(name, role, series, tier):
        info = chars.get(name, {})
        base = slug(name)
        if role == 'giver': nid = base
        elif role == 'ally': nid = f'{base}_ally_{series}'
        else: nid = f'{base}_foe_{series}'
        if nid in npcs: return nid
        major = (role in ('enemy', 'boss') and ({'S-rank', 'Sannin', 'Jinchūriki', 'Tailed Beast'} & set(info.get('Classification', []))
                 or set(info.get('Affiliation', [])) & {'Akatsuki', 'Ōtsutsuki Clan', 'Kara'} or 'Ōtsutsuki' in name))
        if role == 'boss' or major: role = 'boss'
        beast = 'Tailed Beast' in info.get('Classification', []) or info.get('Species', ['Human'])[0:1] == ['Tailed Beast']
        era = ERA.get(series, 0 if tier < 3 else (1 if tier < 11 else 2))
        own = [i for i in jutsu_ids.get(name, []) if jutsu_era.get(i, 'naruto') in ERA and ERA[jutsu_era[i] if i in jutsu_era else 'naruto'] <= era]
        attacks = [i for i in own if offensive(abilities[i]) and abilities[i].get('required_tier', 1) <= max(tier, 3) + 3]
        buffs = [i for i in own if not offensive(abilities[i])]
        picks = attacks[:5] + buffs[:1]
        if not attacks:
            for nat in info.get('Chakra Nature', []):
                aff = clans.ns(clans.NATURE_IDS.get(nat, '')) if nat in clans.NATURE_IDS else None
                for t, aid in nature_attacks.get(aff, []):
                    if t <= max(1, tier) and aid not in picks:
                        picks.append(aid)
                        if len(picks) >= 2: break
        if not picks: picks = ['leaf_rock_destroying_rise' if role != 'giver' else None]
        picks = [clans.ns(p) for p in picks if p]
        d = {'system': clans.SYSTEM, 'name': clans.text(f'npc.{NS}.{base}', name), 'role': role,
             'appearance': appearance(name, info), 'abilities': picks if role != 'giver' else []}
        lang[f'npc.{NS}.{base}'] = name
        if role in ('enemy', 'boss'):
            boss = role == 'boss'
            d.update(health=round((30 + 18 * tier) * (2.4 if boss else 1.0) * (2.0 if beast else 1.0)),
                     attack=round(3 + 0.9 * tier * (1.3 if boss else 1.0), 1), power=round(0.9 + 0.12 * tier, 2),
                     cast_interval=40 if boss else 70, speed=0.32)
            d['title'] = clans.text(f'npc.{NS}.title.{"boss" if boss else "foe"}', '☠' if boss else '')
            lang[f'npc.{NS}.title.boss'] = '☠'; lang[f'npc.{NS}.title.foe'] = ''
        elif role == 'ally':
            d.update(health=round(60 + 14 * tier), attack=round(4 + tier, 1), power=round(1.0 + 0.12 * tier, 2), cast_interval=60, speed=0.34)
        else:
            d.update(health=200, attack=0)
        if beast or name in BEAST_MODELS:
            d['scale'] = 3.0
        npcs[nid] = d
        return nid

    def t(key, text):
        lang[key] = text
        return clans.text(key, text)

    rewards_cycle = [{'id': f'{NS}:kunai', 'count': 8}, {'id': f'{NS}:shuriken', 'count': 16},
                     {'id': f'{NS}:explosive_tag', 'count': 4}, {'id': f'{NS}:smoke_bomb', 'count': 3},
                     {'id': f'{NS}:senbon', 'count': 12}, {'id': f'{NS}:blood_increasing_pill', 'count': 2}]
    qn = 0
    for si, order, arc, series, a in arcs:
        filler = is_filler(a, series)
        tier = tier_for(series, arc)
        if series == 'movies':
            i = MOVIES.index(arc) if arc in MOVIES else 0
            tier = 2 if i < 3 else (6 if i < 9 else 11)
        arc_slug = slug(re.sub(r'\s*\(Arc\)$', '', arc))[:48]
        arc_text = re.sub(r'\s*\(Arc\)$', '', arc)
        arc_name = t(f'quest.{NS}.arc.{arc_slug}', arc_text)
        # where this arc hooks into the chain
        if series == 'movies':
            i = MOVIES.index(arc) if arc in MOVIES else 0
            hook = first_canon_arc_end.get('naruto') if i < 3 else (first_canon_arc_end.get('shippuden') if i < 9 else series_tail.get('shippuden'))
        elif filler:
            hook = last_canon_quest.get(series) or prev_canon_tail
        else:
            hook = prev_canon_tail
        prev = hook
        beats = a['beats']
        arc_enemies = Counter()
        for b in beats:
            for c in b['characters']:
                if side(c, chars.get(c, {}), series, arc, b['text'], b.get('names', {}).get(c, ())) == 'enemy': arc_enemies[c] += 1 + b['text'].count(c.split()[0])
        top_enemy = arc_enemies.most_common(1)[0][0] if arc_enemies else None
        arc_row = {'arc': arc_text, 'series': series, 'filler': filler, 'tier': tier, 'quests': []}
        for bi, b in enumerate(beats):
            qn += 1
            qid = f'{arc_slug}_{bi + 1:02d}'
            sents = sentences(b['text'])
            cs = [c for c in OrderedDict.fromkeys(b['characters']) if len(slug(c)) > 2]
            sides = {c: side(c, chars.get(c, {}), series, arc, b['text'], b.get('names', {}).get(c, ())) for c in cs}
            allies = [c for c in cs if sides[c] == 'ally' and human(chars.get(c, {}))]
            enemies = [c for c in cs if sides[c] == 'enemy' and (b.get('mentions', {}).get(c, 1) >= 2 or c == top_enemy)]
            mentors = [c for c in MENTORS[series] if c in allies]
            alive = [c for c in allies if c in FRIENDLY_ALWAYS] + [c for c in allies if 'Deceased' not in chars.get(c, {}).get('Status', [])]
            giver_name = (mentors or alive or allies or [DEFAULT_GIVER[series] if DEFAULT_GIVER[series] in chars else None])[0]
            giver = npc(giver_name, 'giver', series, tier) if giver_name else None
            fighters = [c for c in allies if c != giver_name][:2]
            objectives = []
            desc = short(' '.join(sents[:3]) if sents else a['lead'], 420)
            intro = [short(s, 220) for s in sents[:3]] or [short(a['lead'], 220)]
            outro = [short(s, 220) for s in sents[-2:]] if len(sents) > 3 else [t(f'quest.{NS}.generic.outro', 'Well done. The story continues...')]
            if giver:
                objectives.append({'type': 'talk', 'target': clans.ns(giver), 'text': t(f'quest.{NS}.{qid}.o1', f'Talk to {giver_name}')})
            if enemies:
                objectives.append({'type': 'travel', 'count': 60 + h(qid, 40),
                                   'text': t(f'quest.{NS}.{qid}.travel', short(f'Head out: {b["title"]}', 70))})
                foes = sorted(enemies[:3], key=lambda c: (c == top_enemy, arc_enemies[c]))
                for c in foes:
                    role = 'boss' if c == top_enemy else 'enemy'
                    nid = npc(c, role, series, tier)
                    objectives.append({'type': 'defeat', 'target': clans.ns(nid), 'text': t(f'quest.{NS}.defeat.{nid}', f'Defeat {c}')})
            else:
                fight = any(FIGHT.search(s) for s in sents)
                others = [c for c in allies if c != giver_name]
                objectives.append({'type': 'travel', 'count': 40 + h(qid, 30),
                                   'text': t(f'quest.{NS}.{qid}.travel', short(f'Go: {b["title"]}', 70))})
                if fight:
                    objectives.append({'type': 'defeat', 'target': clans.ns(minion(npcs, lang, series, tier)), 'count': 3,
                                       'text': t(f'quest.{NS}.minion.{series}', 'Defeat the attackers')})
                elif others:
                    nid = npc(others[0], 'giver', series, tier)
                    objectives.append({'type': 'talk', 'target': clans.ns(nid), 'text': t(f'quest.{NS}.{qid}.o3', f'Meet {others[0]}')})
                else:
                    objectives.append({'type': 'use_ability', 'count': 3, 'text': t(f'quest.{NS}.train', 'Train: use 3 techniques')})
            last_of_arc = bi == len(beats) - 1
            rewards = {'skill_points': 1, 'experience': 15 + 2 * tier}
            if last_of_arc:
                rewards['items'] = [rewards_cycle[qn % len(rewards_cycle)]]
                rewards['skill_points'] = 3
            q = {'system': clans.SYSTEM,
                 'name': t(f'quest.{NS}.{qid}', short(b['title'], 60)),
                 'description': t(f'quest.{NS}.{qid}.desc', (('[Anime-only side story] ' if filler and bi == 0 else '') + desc)),
                 'series': series, 'arc': arc_name, 'order': SERIES.index(series) * 100000 + (order + 10) * 100 + bi,
                 'requires': [clans.ns(prev)] if prev else [], 'recommended_tier': max(1, tier),
                 'intro': [t(f'quest.{NS}.{qid}.intro{i}', s) for i, s in enumerate(intro)],
                 'outro': [t(f'quest.{NS}.{qid}.outro{i}', s) if isinstance(s, str) else s for i, s in enumerate(outro)],
                 'objectives': objectives, 'rewards': rewards}
            if giver: q['giver'] = clans.ns(giver)
            if enemies and fighters: q['allies'] = [clans.ns(npc(c, 'ally', series, tier)) for c in fighters]
            quests[qid] = q
            arc_row['quests'].append(qid)
            prev = qid
        # tailed-beast boss fights where the story has them rampage
        for beast in ARC_BEASTS.get(arc, []):
            if beast in chars:
                nid = npc(beast, 'boss', series, max(tier, 4))
                quests[prev]['objectives'].append({'type': 'defeat', 'target': clans.ns(nid),
                                                   'text': t(f'quest.{NS}.defeat.{nid}', f'Subdue {beast}')})
        # sealed powers awarded where the story hands them out (tailed beasts, curse marks, Kāma, sage training...)
        for it in ARC_ITEMS.get(arc, []):
            quests[prev]['rewards'].setdefault('items', []).append({'id': f'{NS}:{it}', 'count': 1})
        # tier reward at the end of each canon arc: the next canon arc's level
        if not filler and series != 'movies':
            canon = [x[2] for x in canon_by_series[series]]
            i = canon.index(arc)
            nxt = tier_for(series, canon[i + 1]) if i + 1 < len(canon) else SERIES_TIERS[series][1]
            quests[prev]['rewards']['tier'] = nxt
            prev_canon_tail = prev
            last_canon_quest[series] = prev
            series_tail[series] = prev
            first_canon_arc_end.setdefault(series, prev)
        arc_rows.append(arc_row)

    out = {'generated_from': os.path.relpath(DOC_JSON, ROOT), 'crawled': data['crawled'],
           'quests': quests, 'npcs': npcs, 'lang': {**lang, **{f'quest.series.{NS}.{s}': n for s, n in SERIES_NAMES.items()}}}
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    roles = Counter(n['role'] for n in npcs.values())
    print(f"story: {len(arc_rows)} arcs, {len(quests)} quests, {len(npcs)} story characters {dict(roles)}")
    write_doc(data, arc_rows, quests, npcs)


ARC_BEASTS = {
    'Konoha Crush (Arc)': ['Shukaku'], 'Kazekage Rescue Mission': ['Shukaku'], "Three-Tails' Appearance": ['Isobu'],
    'Six-Tails Unleashed': ['Saiken'], 'Fated Battle Between Brothers': ['Gyūki'], "Pain's Assault (Arc)": ['Kurama'],
    'Fourth Shinobi World War: Confrontation': ['Matatabi', 'Son Gokū', 'Kokuō', 'Chōmei'],
    "Birth of the Ten-Tails' Jinchūriki": ['Ten-Tails'], 'Akatsuki Suppression Mission': ['Matatabi'],
}

ARC_ITEMS = {
    'Chūnin Exams (Arc)': ['cursed_seal_of_heaven'], 'Sasuke Recovery Mission': ['cursed_seal_of_earth'],
    'Kazekage Rescue Mission': ['shukaku_seal'], 'Akatsuki Suppression Mission': ['matatabi_seal'],
    "Three-Tails' Appearance": ['isobu_seal'], 'Tale of Jiraiya the Gallant': ['sage_scroll'],
    'Fated Battle Between Brothers': ['susanoo_scroll', 'eternal_mangekyo'], "Pain's Assault (Arc)": ['kurama_seal'],
    'Six-Tails Unleashed': ['saiken_seal'], 'Fourth Shinobi World War: Countdown': ['gyuki_seal', 'son_goku_seal'],
    'Fourth Shinobi World War: Confrontation': ['kokuo_seal', 'chomei_seal', 'shikotsumyaku_awakening', 'sage_transformation_awakening'],
    "Birth of the Ten-Tails' Jinchūriki": ['ten_tails_seal'], 'Kaguya Ōtsutsuki Strikes': ['rinne_sharingan_eye'],
    'Fourth Shinobi World War: Climax': ['ranmaru_eye'],
    'The Last: Naruto the Movie': ['tenseigan_chakra'], 'Versus Momoshiki Arc': ['kama_seal'], 'Kawaki Arc': ['baryon_mode'],
    'Naruto the Movie: Blood Prison': ['dragon_life_awakening'], 'Yakumo Kurama Rescue Mission': ['kurama_clan_illusion_awakening'],
    'Mujina Bandits Arc': ['smoke_body_awakening'], 'Kara Actuation Arc': ['molecular_fusion_awakening'],
    'Kakashi\'s Anbu Arc: The Shinobi That Lives in the Darkness': ['clone_link_awakening'],
    'Land of Rice Fields Investigation Mission': ['rinha_absorption_awakening'],
}


def minion(npcs, lang, series, tier):
    nid = f'rogue_ninja_{series}'
    if nid not in npcs:
        name = {'naruto': 'Rogue Ninja', 'shippuden': 'Rogue Ninja', 'boruto': 'Rogue Ninja', 'movies': 'Enemy Ninja'}[series]
        lang[f'npc.{NS}.rogue_ninja'] = name
        npcs[nid] = {'system': clans.SYSTEM, 'name': clans.text(f'npc.{NS}.rogue_ninja', name), 'role': 'enemy',
                     'appearance': {'hair': 0x1E1E1E, 'outfit': 'bodysuit', 'top': 0x37474F, 'accent': 0x263238, 'pants': 0x212121,
                                    'mark': 'mask', 'headband': 0x455A64},
                     'abilities': [clans.ns('shadow_shuriken_technique'), clans.ns('manipulated_shuriken_technique')], 'health': round(20 + 10 * tier),
                     'attack': round(3 + 0.7 * tier, 1), 'speed': 0.33, 'held_item': f'{NS}:kunai'}
    return nid


def write_doc(data, arc_rows, quests, npcs):
    L = ['# Narutopedia story coverage (story mode)', '',
         f"Source: {', '.join(data['source'])} - crawled {data['crawled']}. Method: {data['method']}.", '',
         '## Category tree', '']
    for c, v in data['category_tree'].items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories")
    L += ['', f"**{len(arc_rows)} arcs and movies, {len(quests)} story quests, {len(npcs)} story characters.** Every page of both "
          'categories is in the game except the index pages below. Quest texts are condensed from Narutopedia (CC BY-SA).', '',
          '### Pages intentionally not turned into quests', '']
    for p, why in data['excluded'].items():
        L.append(f'- **{p}**: {why}.')
    L += ['- Spin-offs without an arc page on Narutopedia (Rock Lee & His Ninja Pals, OVAs, games) are not part of '
          '`Category:Arcs` / `Category:Movies` and are out of scope.', '',
          '## Arcs', '', '| series | arc | canon / side | rank | quests |', '|---|---|---|---:|---:|']
    for r in arc_rows:
        L.append(f"| {SERIES_NAMES[r['series']]} | {r['arc']} | {'side story (anime-only)' if r['filler'] else ('movie' if r['series'] == 'movies' else 'canon')} | "
                 f"{r['tier'] or '-'} | {len(r['quests'])} |")
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_story.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutopedia_story.md')


if __name__ == '__main__':
    if len(sys.argv) > 2 and sys.argv[1] == '--extract':
        extract(sys.argv[2], sys.argv[3] if len(sys.argv) > 3 else None)
    else:
        main()
