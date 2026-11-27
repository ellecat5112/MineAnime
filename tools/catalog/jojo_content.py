"""JoJoRPG content catalog (JoJo's Bizarre Encyclopedia, jojowiki.com: Category:Part 1 + Category:Part 2, recursively).

Two stages, like story.py:
  python3 tools/catalog/jojo_content.py --extract <raw crawl.json>
      condenses the crawl into docs/wiki/jojowiki_parts_1_2.json: the category tree, every character with its categories
      and its {{Tech}} techniques (name, description, debut chapter), story arcs with their chapters, chapter titles /
      summaries / casts, Part 1-2 items.
  python3 tools/catalog/jojo_content.py
      writes tools/catalog/generated/jojo_catalog.json (affinities, fighting styles, techniques, forms + lang),
      jojorpg/src/main/resources/assets/jojorpg/jojo_items.json (awakening items), item sprites and
      docs/wiki/jojowiki_parts_1_2.md. Story mode is tools/catalog/jojo_story.py (run after this).

Fighting styles (engine lineages, learned through items and trained with the Style Manual):
  Hamon (the Ripple)  - breathing refills the Ripple, blows sear the undead, Ripple techniques
  Vampire             - Stone Mask; drinks blood, regenerates, burns in sunlight, Space Ripper Stingy Eyes...
  Pillar Man          - ancient, absorbs living things, turns to stone in sunlight; Wind / Flame / Light Modes
  Cyborg              - German science (Stroheim): armour, machine guns, the ultraviolet lamp
  Brawler             - street fighting and weapons (Speedwagon, Bruford's sword)
"""
import json
import math
import os
import re
import sys
from collections import Counter, OrderedDict

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
NS = 'jojorpg'
DOC_JSON = os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_parts_1_2.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'jojo_catalog.json')
ITEMS_JSON = os.path.join(ROOT, 'jojorpg', 'src', 'main', 'resources', 'assets', 'jojorpg', 'jojo_items.json')
ITEM_DIR = os.path.join(ROOT, 'jojorpg', 'src', 'main', 'resources', 'assets', 'jojorpg', 'textures', 'item')
RIPPLE, STAMINA, BLOOD = f'{NS}:ripple', f'{NS}:stamina', f'{NS}:blood'


def ns(p):
    return f'{NS}:{p}'


def slug(text):
    t = text.lower()
    for a, b in (('ō', 'o'), ('ū', 'u'), ('é', 'e'), ('ä', 'a'), ('ö', 'o'), ('ü', 'u'), ('&', 'and')):
        t = t.replace(a, b)
    return re.sub(r'[^a-z0-9]+', '_', t).strip('_')


def text(key, fallback):
    return {'translate': key, 'fallback': fallback}


# ======================================================================================================================
# extraction
# ======================================================================================================================

def clean(t):
    t = re.sub(r'<ref[^>]*/>', '', t)
    t = re.sub(r'<ref[^>]*>.*?</ref>', '', t, flags=re.S)
    out, i = [], 0
    while i < len(t):
        if t.startswith('{{', i):
            j, d = i + 2, 1
            while j < len(t) and d:
                if t.startswith('{{', j): d += 1; j += 2
                elif t.startswith('}}', j): d -= 1; j += 2
                else: j += 1
            inner = t[i + 2:j - 2]
            parts = inner.split('|')
            name = parts[0].strip().lower()
            if name in ('w',) and len(parts) > 1:
                out.append(parts[-1])
            elif name in ('nihongo', 'ruby', 'ch', 'ch2', 'ep', 'ep2', 'stand') and len(parts) > 1:
                out.append(re.sub(r"'''?", '', parts[1]))
            i = j
            continue
        out.append(t[i]); i += 1
    t = ''.join(out)
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
    t = re.sub(r'\[\[([^\]|]*)\|([^\]]*)\]\]', r'\2', t)
    t = re.sub(r'\[\[([^\]]*)\]\]', r'\1', t)
    t = re.sub(r"'''?", '', t)
    t = re.sub(r'<[^>]+>', '', t)
    t = re.sub(r'[ \t]+', ' ', t)
    t = re.sub(r'\n\s*\n+', '\n', t)
    return t.strip()


def section(t, name):
    m = re.search(r'^==\s*' + re.escape(name) + r'\s*==\s*$(.*?)(?=^==[^=]|\Z)', t, re.M | re.S)
    return m.group(1) if m else ''


def techs(t):
    out = []
    for m in re.finditer(r'\{\{Tech\|(.*?)\n\}\}', t, re.S):
        body = m.group(1)
        f = {}
        for k in ('Part', 'Name', 'Desc', 'Debut'):
            mm = re.search(r'(?:^|\|)\s*' + k + r'\s*=\s*(.*?)(?=\n\s*\||\Z)', body, re.S)
            if mm: f[k.lower()] = mm.group(1).strip()
        if not f.get('name'): continue
        name = clean(f['name']).strip()
        if not name or name.startswith('{'): continue
        ch = re.search(r'Chapter (\d+)', f.get('debut', ''))
        out.append({'name': name, 'part': f.get('part', '').strip(), 'desc': clean(f.get('desc', ''))[:600],
                    'debut': int(ch.group(1)) if ch else None})
    return out


def extract(raw_path):
    raw = json.load(open(raw_path, encoding='utf-8'))
    tree, texts = raw['category_tree'], raw['texts']
    cats_of = {}
    for c, v in tree.items():
        for p in v['pages']:
            cats_of.setdefault(p, []).append(c.replace('Category:', ''))
    chars = {}
    char_cats = {c for c in tree if 'Characters' in c or c.endswith(('Allies', 'Antagonists', 'Users', 'Vampires', 'Pillar Men', 'Nazis',
                                                                    'Speedwagon Foundation', "Jonathan's Group", 'Agents of DIO'))}
    for c in char_cats:
        for p in tree[c]['pages']:
            if re.search(r'/[a-z]{2}', p) or p.startswith(('Minor ', 'Unnamed ')): continue
            t = texts.get(p) or ''
            chars[p] = {'categories': sorted(set(cats_of.get(p, []))), 'techniques': techs(t),
                        'lead': clean(re.split(r'^==', re.sub(r'^\{\{.*?\n\}\}', '', t, flags=re.S | re.M), flags=re.M)[0])[:500]}
    general = {}
    for page in ('Hamon', 'Vampire', 'Pillar Men', 'Zombie'):
        general[page] = {'techniques': techs(texts.get(page) or ''), 'lead': clean((texts.get(page) or '')[:3000])[:500]}
    arcs = []
    for part, cat in (('Phantom Blood', 'Category:Part 1 Story Arcs'), ('Battle Tendency', 'Category:Part 2 Story Arcs')):
        for a in tree.get(cat, {}).get('pages', []):
            t = texts.get(a) or ''
            chs = [int(x) for x in re.findall(r'\{\{Ch2\|Chapter (\d+)\}\}', section(t, 'Chapters'))]
            arcs.append({'title': re.sub(r'\s*\(Story Arc\)$', '', a), 'part': part, 'chapters': sorted(set(chs)),
                         'summary': clean(section(t, 'Summary'))[:800]})
    arcs.sort(key=lambda a: (a['chapters'][0] if a['chapters'] else 999))
    chapters = {}
    for c in ('Category:Part 1 Chapters', 'Category:Part 2 Chapters'):
        for p in tree.get(c, {}).get('pages', []):
            m = re.match(r'Chapter (\d+)$', p)
            if not m: continue
            t = texts.get(p) or ''
            title = re.search(r"\{\{Nihongo\|'''(.*?)'''", t)
            cast = []
            for cm in re.finditer(r'\{\{Char\|Name=([^|}]+)(?:\|[^}]*?Status=([^|}]*))?', section(t, 'Appearances')):
                cast.append({'name': cm.group(1).strip(), 'flags': re.findall(r'\{\{(\w+)\}\}', cm.group(2) or '')})
            chapters[int(m.group(1))] = {'title': title.group(1) if title else p, 'summary': clean(section(t, 'Summary'))[:2200], 'cast': cast}
    items = {}
    for p in tree.get('Category:Items', {}).get('pages', []):
        t = texts.get(p) or ''
        if re.search(r'Phantom Blood|Battle Tendency|Part 1|Part 2', t[:6000]) and not re.search(r'/[a-z]{2}', p):
            items[p] = clean(re.split(r'^==', re.sub(r'^\{\{.*?\n\}\}', '', t, flags=re.S | re.M), flags=re.M)[0])[:400]
    out = {'source': raw['source'], 'crawled': raw['crawled'], 'method': raw['method'], 'category_tree': tree,
           'characters': chars, 'general': general, 'arcs': arcs, 'chapters': {str(k): v for k, v in sorted(chapters.items())},
           'items': items}
    with open(DOC_JSON, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False)
    print(f"extracted {len(chars)} characters, {sum(len(c['techniques']) for c in chars.values())} techniques, {len(arcs)} arcs, "
          f"{len(chapters)} chapters, {len(items)} items")


# ======================================================================================================================
# styles, techniques, forms
# ======================================================================================================================

PASSIVE = re.compile(r'superhuman|^super |slowing aging|anti-aging|senses|intelligence|regeneration|toughness|agility|^speed|strength'
                     r'|familial|disc$|cyborg body|^absorption$|body manipulation|^\[\[|^ripple$|biology manipulation|athleticism|'
                     r'immortality|^final ability$|enhanced sensory|advanced intelligence|^blood sucking$|bodily mastery|anti-aging', re.I)
OWNER_STYLE = {'Rudol von Stroheim': 'cyborg', 'Robert E. O. Speedwagon': 'brawler', 'Amato': 'brawler', 'Bruford': 'undead',
               'Tarkus': 'undead', 'Adams': 'undead', 'Jack the Ripper': 'undead', 'Doobie': 'undead', 'Caineghis': 'undead',
               'Winzaleo': 'undead', 'Eijkman': 'undead', 'Page, Jones, Plant, and Bornnam': 'undead', 'Zombie': 'undead',
               'Wang Chan': 'vampire', 'Wired Beck': 'vampire', 'Dio Brando': 'vampire', 'Vampire': 'vampire', 'Straizo': 'vampire',
               'Wamuu': 'pillar_man', 'Esidisi': 'pillar_man', 'Kars': 'pillar_man', 'Santana': 'pillar_man', 'Pillar Men': 'pillar_man'}
OWNER_AFF = {'Wamuu': 'wind', 'Esidisi': 'heat', 'Kars': 'light'}
STYLE_TOKEN = {'hamon': 'ripple', 'vampire': 'vampire', 'undead': 'vampire', 'pillar_man': 'pillar_men', 'cyborg': 'science', 'brawler': 'martial'}
STYLE_POOL = {'hamon': RIPPLE, 'vampire': BLOOD, 'undead': BLOOD, 'pillar_man': BLOOD, 'cyborg': STAMINA, 'brawler': STAMINA}
STYLE_AFF = {'hamon': 'ripple', 'vampire': 'vampirism', 'undead': 'vampirism', 'pillar_man': None, 'cyborg': None, 'brawler': None}
NOT_TECHNIQUES = {   # {{Tech}} entries that are not something a fighter casts
    'stands': 'Stands are Part 3 onwards', 'zombie': 'being a zombie is the undead trait, not a technique',
    'vampirism': 'the Vampire fighting style itself', 'elemental_modes': 'the Wind / Flame / Light Mode forms',
    'fusion': 'a passive Pillar Man / vampire biology note', 'ripple_assisted_suicide': 'Straizo\'s death, not a fighting move'}
EXCLUDED_TECH_OWNERS = {'DISC', 'Steel Ball', 'Kars (JORGE JOESTAR)', 'Dio Brando (JORGE JOESTAR)'}   # other parts / spin-offs


def style_of(owner, cats, tech_name):
    if owner in OWNER_STYLE:
        st = OWNER_STYLE[owner]
        if st == 'vampire' and re.search(r'ripple|overdrive|satiporoja', tech_name, re.I):   # Straizo's Ripple days
            return 'hamon'
        return st
    if 'Ripple Users' in cats or owner == 'Hamon': return 'hamon'
    if 'Vampires' in cats: return 'vampire'
    if 'Pillar Men' in cats: return 'pillar_man'
    return 'hamon' if re.search(r'ripple|overdrive', tech_name, re.I) else 'brawler'


def effect(name, duration=100, amp=0):
    return {'effect': f'minecraft:{name}', 'duration': duration, 'amplifier': amp}


def dash(dmg, distance=4.0, hits=1, lift=0.0, knockback=1.0, radius=1.4):
    d = {'type': 'animecore:dash_strike', 'mode': 'look', 'distance': distance, 'damage': round(dmg, 1), 'hit_radius': radius, 'knockback': knockback}
    if hits > 1: d['hits'] = hits
    if lift: d['lift'] = lift
    return d


def burst(dmg, radius=3.5, knockback=1.2, forward=0.0, fire=0, color=0xFFFFFF, lift=0.0):
    d = {'type': 'animecore:aoe_sphere', 'radius': radius, 'damage': round(dmg, 1), 'knockback': knockback, 'forward_offset': forward,
         'fire_ticks': fire, 'color': color}
    if lift: d['lift'] = lift
    return d


def shot(dmg, count=1, speed=1.8, size=0.4, aoe=0.0, life=30, spread=8.0, color=0xFFFFFF, fire=0, trail=None):
    d = {'type': 'animecore:projectile', 'speed': speed, 'damage': round(dmg if count == 1 else dmg * 2.0 / count, 1), 'size': size,
         'aoe_radius': aoe, 'count': count, 'spread_degrees': spread if count > 1 else 0.0, 'lifetime_ticks': life, 'color': color,
         'fire_ticks': fire}
    if trail: d['trail_particle'] = trail
    return d


def beam(dmg, rng=12.0, width=0.5, pierce=True, color=0xFFFFFF, fire=0):
    return {'type': 'animecore:beam_raycast', 'range': rng, 'damage': round(dmg, 1), 'width': width, 'pierce': pierce, 'color': color, 'fire_ticks': fire}


def area(dmg, radius=7.0, cone=70.0, effects=(), pull=0.0, color=0xFFFFFF, targets=None):
    d = {'type': 'animecore:area_effect', 'radius': radius, 'cone_degrees': cone, 'damage': round(dmg, 1), 'pull': pull, 'color': color,
         'effects': list(effects)}
    if targets: d['targets'] = targets
    return d


def buff(*effects, heal=0.0):
    d = {'type': 'animecore:self_buff', 'effects': list(effects)}
    if heal: d['heal'] = heal
    return d


YELLOW, RED, BLUE, SILVER, ORANGE = 0xFFD54F, 0xE53935, 0x4FC3F7, 0xCFD8DC, 0xFF7043


def actions_for(name, desc, style, dmg):
    n, d = name.lower(), (name + ' ' + desc).lower()
    if n in ('healing', 'medicinal usage'): return [buff(effect('regeneration', 100, 1), heal=dmg)], 'focus'
    if 'stone form' in n: return [buff(effect('resistance', 200, 3), effect('slowness', 200, 2))], 'stance'
    if 'cyborg eyes' in n: return [area(0, 32, 360, [effect('glowing', 300)], color=0xE53935)], 'focus'
    if 'wind suit' in n: return [buff(effect('invisibility', 160), effect('speed', 160, 1))], 'stance'
    if 'flesh transmutation' in n: return [buff(effect('strength', 300, 1), effect('resistance', 300, 1))], 'stance'
    if n == 'luck' or 'head twisting' in n: return [dash(dmg * 1.2, 3.5, knockback=1.6)], 'sweep'
    if 'zoom punch' in n: return [beam(dmg, 7, 0.6, False, YELLOW)], 'punch'
    if 'sunlight yellow' in n: return [dash(dmg, 4, knockback=1.6), burst(dmg * 0.8, 3.5, 1.4, 1.5, color=YELLOW)], 'punch'
    if 'scarlet' in n: return [dash(dmg, 4, knockback=1.2), burst(dmg * 0.5, 2.5, 0.8, 1.5, fire=80, color=0xFF5722)], 'punch'
    if 'metal silver' in n: return [beam(dmg * 1.2, 6, 0.8, True, SILVER)], 'thrust'
    if 'turquoise blue' in n: return [shot(dmg, speed=1.6, size=0.6, aoe=1.5, color=0x26C6DA, trail='minecraft:splash')], 'thrust'
    if 'sendō' in n or 'sendo' in n:
        if 'kick' in n: return [dash(dmg, 4.5, knockback=1.8, lift=1.0)], 'kick'
        return [burst(dmg, 4.0, 2.4, 2.0, color=YELLOW, lift=1.2)], 'slam'
    if 'barrage' in d or 'beat' in n or 'rush' in n: return [dash(dmg * 2.2 / 5, 4, hits=5, knockback=1.4)], 'barrage'
    if 'kick' in n or 'split' in n: return [dash(dmg, 4.5, hits=2 if 'split' in n else 1, knockback=1.6)], 'kick'
    if 'cutter' in n: return [shot(dmg, speed=2.6, size=0.25, color=0x80DEEA, trail='minecraft:splash', life=20)], 'thrust'
    if 'bubble' in n:
        if 'barrier' in n: return [{'type': 'animecore:raise_wall', 'kind': 'barrier', 'width': 5, 'height': 3, 'distance': 2, 'duration_ticks': 160}], 'focus'
        if 'cutter' in n or 'lens' in n: return [shot(dmg, count=3, speed=2.2, size=0.4, color=0xB3E5FC)], 'thrust'
        return [shot(dmg, count=6, speed=1.0, size=0.5, aoe=1.0, color=0xE1F5FE, spread=12.0)], 'sweep'
    if 'clacker' in n: return [shot(dmg, count=2, speed=1.9, size=0.4, aoe=1.0, color=YELLOW, spread=5.0)], 'thrust'
    if 'hair' in n: return [area(dmg, 6, 60, [effect('slowness', 60, 3)], pull=1.0, color=YELLOW)], 'sweep'
    if 'hypnosis' in n: return [area(dmg * 0.3, 8, 50, [effect('nausea', 120), effect('slowness', 80, 2)], color=YELLOW)], 'gaze'
    if 'detector' in n or 'precognition' in n:
        return [area(0, 32, 360, [effect('glowing', 300)], color=YELLOW), buff(effect('night_vision', 300))], 'focus'
    if re.search(r'muffler|cloth|missanga|scarf|wire', n): return [area(dmg, 7, 50, [effect('slowness', 100, 4)], pull=1.5, color=YELLOW)], 'sweep'
    if 'sticking' in n or 'repelling' in n or 'teleférico' in n or 'ropeway' in n:
        if 'ropeway' in n or 'teleférico' in n: return [dash(dmg, 14, knockback=1.0)], 'lunge'
        return [buff(effect('jump_boost', 300, 2), effect('slow_falling', 300), effect('resistance', 300))], 'stance'
    if 'presence' in n or 'afterimage' in n or 'intangib' in n or 'light body' in n:
        return [buff(effect('invisibility', 120), effect('speed', 160, 2))], 'stance'
    if 'space ripper' in n: return [beam(dmg * 1.6, 24, 0.3, True, 0xE0F7FA)], 'gaze'
    if 'freez' in n: return [area(dmg, 4, 360, [effect('slowness', 120, 5), effect('mining_fatigue', 120, 2)], color=0xB3E5FC)], 'thrust'
    if 'zombie' in d and 'creat' in d: return [{'type': 'animecore:summon_entity', 'entity': 'minecraft:zombie', 'count': 3, 'radius': 2.5}], 'slam'
    if 'blood' in n or 'meat invade' in n or 'absorb' in n: return [dash(dmg, 4, knockback=0.6), buff(heal=dmg * 0.6)], 'lunge'
    if re.search(r'spike|needle|rib|blade', n):
        if 'light blade' in n or 'brilliant' in n: return [beam(dmg * 1.2, 6, 1.4, True, 0xFFF9C4)], 'sweep'
        return [burst(dmg, 3.2, 1.0, 1.0, color=0xEFEBE9)], 'burst'
    if 'divine sandstorm' in n or 'sandstorm' in n: return [beam(dmg * 1.8, 22, 2.0, True, 0xB2DFDB)], 'thrust'
    if 'winback' in n or 'anti-ripple' in n: return [buff(effect('resistance', 300, 1), effect('speed', 300, 1))], 'stance'
    if 'blaze' in n or 'cartwheel' in n or 'flame' in n: return [shot(dmg, count=5, speed=1.5, size=0.4, color=ORANGE, fire=80, trail='minecraft:flame')], 'thrust'
    if 'machine gun' in n: return [shot(dmg, count=8, speed=2.8, size=0.2, color=0xFFE082, spread=6.0, life=25)], 'thrust'
    if 'ultraviolet' in n: return [beam(dmg * 1.4, 18, 0.9, True, 0xE1BEE7)], 'point'
    if 'projectile hand' in n or 'saw-hat' in n: return [shot(dmg * 1.2, speed=2.0, size=0.6, color=SILVER)], 'thrust'
    if 'tongue' in n: return [beam(dmg, 6, 0.4, False, 0xF48FB1)], 'thrust'
    if 'scalpel' in n: return [shot(dmg, count=3, speed=2.4, size=0.2, color=SILVER)], 'thrust'
    if 'snake' in n and 'venom' in d: return [area(dmg * 0.6, 7, 60, [effect('poison', 120, 1)], color=0x689F38)], 'sweep'
    if 'snake kill' in n or 'chain' in d: return [area(dmg, 6, 50, [effect('slowness', 80, 5)], pull=1.5, color=0x8D6E63)], 'sweep'
    if 'halberd' in n: return [beam(dmg, 7, 1.0, True, 0x80D8FF)], 'sweep'
    if re.search(r'axe|sledgehammer|scimitar|sword|luck and pluck|pincer|claw', n): return [dash(dmg * 1.2, 3.5, knockback=1.6)], 'sweep'
    if 'kempo' in n: return [dash(dmg * 2.2 / 3, 4, hits=3)], 'barrage'
    if 'ultimate thing' in n: return [buff(effect('strength', 400, 3), effect('resistance', 400, 2), effect('regeneration', 400, 2))], 'focus'
    if 'overdrive' in n or 'ripple' in d:
        return [dash(dmg, 3.5, knockback=1.3), burst(dmg * 0.4, 2.0, 0.6, 1.2, color=YELLOW)], 'punch'
    return [dash(dmg, 4)], 'punch'


def build_techniques(data):
    """name-keyed: {id: (name, style, owner, tier, desc)}"""
    found = OrderedDict()
    owners = dict(data['characters'])
    owners.update({k: {'categories': [], 'techniques': v['techniques']} for k, v in data['general'].items()})
    skipped_passive, skipped_other = [], []
    for owner, c in owners.items():
        if owner in EXCLUDED_TECH_OWNERS:
            skipped_other += [(owner, t['name']) for t in c['techniques']]
            continue
        for t in c['techniques']:
            name = re.sub(r'\s*/\s*Ripple Overdrive$', '', t['name']).strip()
            if PASSIVE.search(name):
                skipped_passive.append((owner, name))
                continue
            tid = slug(name)
            if tid in NOT_TECHNIQUES:
                skipped_other.append((owner, f'{name} - {NOT_TECHNIQUES[tid]}'))
                continue
            if (t.get('debut') or 0) > 113:
                skipped_other.append((owner, f'{name} - debuts after Part 2 (chapter {t["debut"]})'))
                continue
            if tid in found:
                found[tid]['owners'].append(owner)
                continue
            st = style_of(owner, c['categories'], name)
            debut = t.get('debut') or (20 if st == 'hamon' else 40)
            tier = max(1, min(10, 1 + debut // 12))
            found[tid] = {'name': name, 'style': st, 'owners': [owner], 'tier': tier, 'desc': t['desc'], 'aff': OWNER_AFF.get(owner)}
    return found, skipped_passive, skipped_other


def ability(tid, t, lang):
    st = t['style']
    dmg = 4 + 1.2 * t['tier']
    acts, anim = actions_for(t['name'], t['desc'], st, dmg)
    aff = t['aff'] or STYLE_AFF[st]
    if 'ultraviolet' in t['name'].lower(): aff = 'ultraviolet'
    d = {'system': NS, 'name': text(f'ability.{NS}.{tid}', t['name']), 'cast_type': 'instant',
         'cost': {'pool': STYLE_POOL[st], 'amount': round(max(8.0, dmg * 2.2), 1)}, 'cooldown_ticks': int(40 + dmg * 8),
         'required_tier': t['tier'], 'actions': acts, 'token': ns(STYLE_TOKEN[st]), 'cast_animation': f'animecore:cast_{anim}'}
    if aff: d['affinity'] = ns(aff)
    if t['desc']:
        lang[f'ability.{NS}.{tid}.desc'] = t['desc'][:300]
    lang[f'ability.{NS}.{tid}'] = t['name']
    return d


def affinities(lang):
    A = OrderedDict()

    def aff(aid, name, color, style, strong=(), undead=1.0):
        A[aid] = {'system': NS, 'name': text(f'affinity.{NS}.{aid}', name), 'color': color, 'strong_against': [ns(s) for s in strong],
                  'advantage_multiplier': 1.5, 'vfx_style': style, 'undead_multiplier': undead}
        lang[f'affinity.{NS}.{aid}'] = name
    aff('ripple', 'The Ripple (Hamon)', 0xFFD54F, 'light', strong=('vampirism',), undead=3.0)
    aff('vampirism', 'Vampirism', 0xB71C1C, 'blood')
    aff('wind', 'Wind Mode', 0x80CBC4, 'wind')
    aff('heat', 'Flame Mode', 0xFF7043, 'flame')
    aff('light', 'Light Mode', 0xFFF59D, 'light', strong=('ripple',))
    aff('ultraviolet', 'Ultraviolet', 0xCE93D8, 'light', undead=4.0)
    return A


def attr(a, amount, op='add_value'):
    return {'type': 'animecore:attribute', 'attribute': f'minecraft:generic.{a}', 'amount': amount, 'operation': op}


def power(mult, affinity=None):
    d = {'type': 'animecore:ability_power', 'multiplier': mult}
    if affinity: d['affinity'] = ns(affinity)
    return d


STYLES = {
    'hamon': ('Hamon (the Ripple)', 0xFFD54F, ['ripple'],
              [attr('attack_damage', 1), power(1.2, 'ripple'), {'type': 'animecore:energy_regen', 'pool': RIPPLE, 'multiplier': 1.5}],
              [{'type': 'animecore:breathing', 'pool': RIPPLE, 'bonus': 3.0}, {'type': 'animecore:undead_bane', 'multiplier': 1.0, 'fire_ticks': 40}]),
    'vampire': ('Vampire', 0xB71C1C, ['vampirism'],
                [attr('attack_damage', 4), attr('movement_speed', 0.15, 'add_multiplied_base'), attr('max_health', 8), power(1.25)],
                [{'type': 'animecore:undead'}, {'type': 'animecore:sunlight_weakness', 'damage_per_second': 4.0, 'effect': 'burn'},
                 {'type': 'animecore:lifesteal', 'heal_fraction': 0.3, 'pool': BLOOD, 'energy': 6.0},
                 {'type': 'animecore:regeneration', 'amount': 1.0, 'below_health_ratio': 1.0},
                 {'type': 'animecore:passive_effect', 'effect': 'minecraft:night_vision', 'amplifier': 0}]),
    'pillar_man': ('Pillar Man', 0x8D6E63, [],
                   [attr('attack_damage', 7), attr('armor', 6), attr('max_health', 16), attr('movement_speed', 0.2, 'add_multiplied_base'),
                    attr('scale', 0.12), power(1.4)],
                   [{'type': 'animecore:undead'}, {'type': 'animecore:sunlight_weakness', 'damage_per_second': 6.0, 'effect': 'petrify'},
                    {'type': 'animecore:lifesteal', 'heal_fraction': 0.5, 'pool': BLOOD, 'energy': 10.0},
                    {'type': 'animecore:regeneration', 'amount': 1.5, 'below_health_ratio': 1.0}]),
    'cyborg': ('Cyborg (German Science)', 0x90A4AE, [],
               [attr('armor', 10), attr('armor_toughness', 4), attr('attack_damage', 3), attr('knockback_resistance', 0.6), power(1.2)],
               [{'type': 'animecore:evasion', 'chance': 0.1, 'projectiles_only': True}]),
    'brawler': ('Brawler', 0xFF8A65, [],
                [attr('attack_damage', 2), attr('movement_speed', 0.08, 'add_multiplied_base'), attr('max_health', 4)],
                [{'type': 'animecore:on_hit_effect', 'effect': 'minecraft:slowness', 'duration': 30, 'amplifier': 0, 'chance': 0.25}]),
}
STYLE_DESC = {
    'hamon': 'Rhythmic breathing turns the body into a battery of sunlight energy. Breathe (stand still or crouch) to refill the '
             'Ripple; your blows sear vampires, zombies and Pillar Men. You can walk on water.',
    'vampire': 'The Stone Mask made you immortal: monstrous strength, regeneration and blood that heals you with every bite - but '
               'the sun turns you to ash and the Ripple burns.',
    'pillar_man': 'An ancient being above vampires: you absorb living things through your skin and master the Modes - but sunlight '
                  'turns you to stone until the Red Stone of Aja makes you the Ultimate Life Form.',
    'cyborg': 'German science is the best in the world! An armoured body with built-in machine guns and an ultraviolet lamp.',
    'brawler': 'Ogre Street toughness and gentleman boxing: no powers, just fists, grit and whatever weapon is at hand.',
}


def forms(lang, techniques):
    F = OrderedDict()

    def form(fid, name, tier, mods, traits, granted, aura, cloak, req=None, drain=0.12, act=20.0, nxt=None):
        d = {'system': NS, 'name': text(f'transformation.{NS}.{fid}', name), 'pool': BLOOD, 'activation_cost': act,
             'drain_per_tick': drain, 'cooldown_ticks': 200, 'required_tier': tier, 'modifiers': mods, 'traits': traits,
             'granted_abilities': [ns(g) for g in granted if g in techniques], 'token': ns('modes'),
             'visuals': {'aura_color': aura, 'aura_intensity': 1.0, 'cloak_color': cloak, 'cloak_alpha': 0.35}}
        if req: d['required_lineage'] = ns(req)
        lang[f'transformation.{NS}.{fid}'] = name
        F[fid] = d
    by_owner = lambda o: [k for k, t in techniques.items() if o in t['owners']]
    form('wind_mode', 'Wind Mode (Wamuu)', 6, [power(1.6, 'wind'), attr('movement_speed', 0.3, 'add_multiplied_base')],
         [{'type': 'animecore:evasion', 'chance': 0.25, 'projectiles_only': True}], by_owner('Wamuu'), 0x80CBC4, 0xB2DFDB, 'pillar_man')
    form('flame_mode', 'Flame Mode (Esidisi)', 6, [power(1.6, 'heat'), attr('attack_damage', 4)],
         [{'type': 'animecore:on_hit_effect', 'effect': 'minecraft:wither', 'duration': 60, 'amplifier': 1, 'chance': 0.4}],
         by_owner('Esidisi'), 0xFF7043, 0xFF8A65, 'pillar_man')
    form('light_mode', 'Light Mode (Kars)', 8, [power(1.7, 'light'), attr('attack_damage', 6), attr('movement_speed', 0.25, 'add_multiplied_base')],
         [{'type': 'animecore:evasion', 'chance': 0.2, 'projectiles_only': False}], by_owner('Kars'), 0xFFF59D, 0xFFF9C4, 'pillar_man')
    form('ultimate_life_form', 'Ultimate Life Form', 10,
         [power(2.5), attr('attack_damage', 16), attr('armor', 12), attr('max_health', 40), attr('movement_speed', 0.35, 'add_multiplied_base'),
          attr('scale', 0.1)],
         [{'type': 'animecore:sunlight_immunity'}, {'type': 'animecore:regeneration', 'amount': 3.0, 'below_health_ratio': 1.0},
          {'type': 'animecore:evasion', 'chance': 0.3, 'projectiles_only': False}],
         by_owner('Kars') + by_owner('Wamuu')[:2] + by_owner('Esidisi')[:2] + ['overdrive'], 0xFFFFFF, 0xF3E5F5, 'pillar_man', drain=0.05, act=60)
    return F


def lineages(lang, techniques, form_ids):
    L = OrderedDict()
    for sid, (name, color, innate, mods, traits) in STYLES.items():
        own = sorted([k for k, t in techniques.items() if t['style'] == sid or (sid == 'vampire' and t['style'] == 'undead')], key=lambda k: (techniques[k]['tier'], k))
        first = [k for k in GRANTED.get(sid, []) if k in own] or own[:2]
        granted, rest = first, [k for k in own if k not in first]
        span = 48 if len(rest) > 20 else max(10, 2 * len(rest))
        prog = [{'ability': ns(k), 'skill_level': 2 + round(i * (span - 2) / max(1, len(rest) - 1)), 'required_tier': techniques[k]['tier']}
                for i, k in enumerate(rest)]
        if sid == 'pillar_man':
            for lvl, f, tier in ((8, 'wind_mode', 6), (14, 'flame_mode', 6), (20, 'light_mode', 8)):
                prog.append({'transformation': ns(f), 'skill_level': lvl, 'required_tier': tier})
        L[sid] = {'system': NS, 'name': text(f'lineage.{NS}.{sid}', name), 'description': text(f'lineage.{NS}.{sid}.desc', STYLE_DESC[sid]),
                  'innate_affinities': [ns(a) for a in innate], 'traits': mods, 'clan_traits': traits,
                  'granted_abilities': [ns(k) for k in granted], 'progression': prog, 'selectable': False, 'weight': 0, 'color': color,
                  'max_skill_level': 50 if sid in ('hamon', 'vampire', 'pillar_man') else 20, 'clan_items': []}
        lang[f'lineage.{NS}.{sid}'] = name
        lang[f'lineage.{NS}.{sid}.desc'] = STYLE_DESC[sid]
    return L


GRANTED = {'hamon': ['overdrive', 'zoom_punch'], 'vampire': ['claws', 'hypnosis'], 'pillar_man': ['ribs_blades', 'meat_invade'],
           'cyborg': ['abdominal_machine_gun', 'cyborg_eyes'], 'brawler': ['saw_hat', 'eastern_mystery_chinese_kempo']}

ITEMS = [  # id, name, kind, target, lore, sprite
    ('breathing_mask', 'Ripple Breathing Mask', 'lineage', 'hamon',
     'A training mask that only lets you breathe the Ripple way. Wear it long enough and the breath becomes yours.', 'mask'),
    ('stone_mask', 'Stone Mask', 'lineage', 'vampire',
     'An Aztec relic. Blood on its face drives bone spikes into the wearer\'s brain - and unlocks it.', 'stone_mask'),
    ('pillar_remains', 'Pillar of the Ancients', 'lineage', 'pillar_man',
     'A fragment of the pillar the Pillar Men slept in for 12,000 years.', 'pillar'),
    ('cybernetic_overhaul', 'Cybernetic Overhaul', 'lineage', 'cyborg',
     'Blueprints of the German Army\'s finest science - the body of Rudol von Stroheim.', 'gear'),
    ('boxing_gloves', 'Ogre Street Gloves', 'lineage', 'brawler', 'Worn knuckles from the slums of London.', 'gloves'),
    ('red_stone_of_aja', 'Red Stone of Aja', 'form', 'ultimate_life_form',
     'The Super Aja: focused through the Stone Mask, it makes a Pillar Man the Ultimate Life Form.', 'red_stone'),
]


def write_items(lang):
    with open(ITEMS_JSON, 'w', encoding='utf-8') as f:
        json.dump([{'item': i, 'kind': k, 'target': ns(t), 'lore': f'item.{NS}.{i}.lore'} for i, _n, k, t, _l, _s in ITEMS], f, indent=1)
    for i, n, _k, _t, lore, _s in ITEMS:
        lang[f'item.{NS}.{i}'] = n
        lang[f'item.{NS}.{i}.lore'] = lore


# ======================================================================================================================
# sprites
# ======================================================================================================================

def rgb(h, a=255):
    return ((h >> 16) & 255, (h >> 8) & 255, h & 255, a)


def sprites():
    from PIL import Image, ImageDraw
    os.makedirs(ITEM_DIR, exist_ok=True)

    def new():
        im = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
        return im, ImageDraw.Draw(im)
    # awakening items
    im, d = new()   # breathing mask: white mask with a breathing ring
    d.ellipse([7, 6, 25, 26], fill=(236, 239, 241, 255)); d.ellipse([11, 15, 21, 25], fill=(120, 144, 156, 255))
    d.ellipse([13, 17, 19, 23], fill=(38, 50, 56, 255)); d.rectangle([4, 13, 8, 15], fill=(84, 110, 122, 255)); d.rectangle([24, 13, 28, 15], fill=(84, 110, 122, 255))
    im.save(os.path.join(ITEM_DIR, 'breathing_mask.png'))
    im, d = new()   # Stone Mask: grey face, dark eyes, fanged mouth, forehead ridges
    d.polygon([(16, 2), (27, 8), (26, 22), (16, 30), (6, 22), (5, 8)], fill=(158, 158, 150, 255))
    d.polygon([(16, 2), (27, 8), (26, 22), (16, 30), (6, 22), (5, 8)], outline=(97, 97, 90, 255))
    d.ellipse([9, 11, 14, 15], fill=(33, 33, 33, 255)); d.ellipse([18, 11, 23, 15], fill=(33, 33, 33, 255))
    d.rectangle([11, 21, 21, 23], fill=(33, 33, 33, 255))
    for x in (12, 15, 18, 20): d.point((x, 22), fill=(238, 238, 238, 255))
    for x in (10, 16, 22): d.line([(x, 4), (x, 9)], fill=(110, 110, 100, 255))
    im.save(os.path.join(ITEM_DIR, 'stone_mask.png'))
    im, d = new()   # pillar fragment
    d.rectangle([10, 3, 22, 29], fill=(188, 170, 140, 255))
    for y in (8, 14, 20, 26): d.line([(10, y), (22, y)], fill=(141, 110, 99, 255))
    d.polygon([(12, 10), (20, 10), (16, 18)], fill=(93, 64, 55, 255))
    im.save(os.path.join(ITEM_DIR, 'pillar_remains.png'))
    im, d = new()   # cybernetic gear
    d.ellipse([6, 6, 26, 26], fill=(144, 164, 174, 255))
    for k in range(8):
        a = k * math.pi / 4
        d.rectangle([16 + 11 * math.cos(a) - 2, 16 + 11 * math.sin(a) - 2, 16 + 11 * math.cos(a) + 2, 16 + 11 * math.sin(a) + 2], fill=(120, 144, 156, 255))
    d.ellipse([12, 12, 20, 20], fill=(229, 57, 53, 255))
    im.save(os.path.join(ITEM_DIR, 'cybernetic_overhaul.png'))
    im, d = new()   # boxing gloves
    d.ellipse([3, 8, 15, 22], fill=(183, 28, 28, 255)); d.ellipse([17, 8, 29, 22], fill=(183, 28, 28, 255))
    d.rectangle([5, 20, 13, 26], fill=(245, 245, 245, 255)); d.rectangle([19, 20, 27, 26], fill=(245, 245, 245, 255))
    im.save(os.path.join(ITEM_DIR, 'boxing_gloves.png'))
    im, d = new()   # Red Stone of Aja
    d.polygon([(16, 3), (26, 12), (22, 27), (10, 27), (6, 12)], fill=(198, 40, 40, 255))
    d.polygon([(16, 7), (22, 12), (16, 22), (10, 12)], fill=(255, 82, 82, 255)); d.point((14, 10), fill=(255, 235, 238, 255))
    im.save(os.path.join(ITEM_DIR, 'red_stone_of_aja.png'))
    # style manual + journal
    im, d = new()
    d.rectangle([7, 4, 25, 28], fill=(93, 64, 55, 255)); d.rectangle([9, 6, 24, 26], fill=(255, 213, 79, 255))
    d.line([(12, 11), (21, 11)], fill=(93, 64, 55, 255)); d.line([(12, 15), (21, 15)], fill=(93, 64, 55, 255)); d.line([(12, 19), (18, 19)], fill=(93, 64, 55, 255))
    im.save(os.path.join(ITEM_DIR, 'style_manual.png'))
    im, d = new()
    d.rectangle([6, 3, 26, 29], fill=(74, 20, 140, 255)); d.rectangle([8, 5, 24, 27], fill=(106, 27, 154, 255))
    d.text((11, 9), 'JJ', fill=(255, 213, 79, 255)); d.rectangle([6, 3, 8, 29], fill=(255, 213, 79, 255))
    im.save(os.path.join(ITEM_DIR, 'quest_scroll.png'))
    # technique items: medallions with an emblem colour
    TOK = {'ripple_arts': (0xFFB300, 0xFFF59D, 'ripple'), 'vampire_arts': (0x7F0000, 0xE53935, 'fang'), 'pillar_men_arts': (0x5D4037, 0xBCAAA4, 'pillar'),
           'science_arts': (0x37474F, 0x90A4AE, 'gear'), 'martial_arts': (0xBF360C, 0xFF8A65, 'fist'), 'modes_arts': (0x4A148C, 0xE1BEE7, 'star')}
    for name, (rim, inner, glyph) in TOK.items():
        im, d = new()
        d.ellipse([2, 3, 29, 30], fill=rgb(rim)); d.ellipse([5, 6, 26, 27], fill=rgb(inner)); d.rectangle([14, 0, 17, 3], fill=(200, 170, 90, 255))
        g = rgb(rim)
        if glyph == 'ripple':
            for r in (3, 6, 9): d.ellipse([16 - r, 16 - r, 16 + r, 16 + r], outline=g)
        elif glyph == 'fang':
            d.polygon([(10, 9), (14, 9), (12, 22)], fill=(255, 255, 255, 255)); d.polygon([(18, 9), (22, 9), (20, 22)], fill=(255, 255, 255, 255))
        elif glyph == 'pillar':
            d.rectangle([12, 8, 20, 24], fill=g)
        elif glyph == 'gear':
            d.ellipse([10, 10, 22, 22], outline=g, width=2); d.ellipse([14, 14, 18, 18], fill=g)
        elif glyph == 'fist':
            d.rectangle([10, 11, 22, 21], fill=g); d.line([(13, 11), (13, 16)], fill=rgb(inner)); d.line([(17, 11), (17, 16)], fill=rgb(inner))
        else:
            for k in range(5):
                a = -math.pi / 2 + k * 4 * math.pi / 5
                b = -math.pi / 2 + (k + 1) * 4 * math.pi / 5
                d.line([(16 + 8 * math.cos(a), 16 + 8 * math.sin(a)), (16 + 8 * math.cos(b), 16 + 8 * math.sin(b))], fill=g, width=2)
        im.save(os.path.join(ITEM_DIR, name + '.png'))
    # admin seals (gold-framed talismans)
    for k in ('rank_up', 'clan_level', 'mastery', 'skill_points', 'natures', 'reputation', 'refill', 'max_all', 'story'):
        im, d = new()
        d.rectangle([5, 2, 26, 29], fill=(212, 160, 23, 255)); d.rectangle([6, 3, 25, 28], fill=(40, 16, 60, 255))
        d.text((10, 10), {'max_all': 'JO', 'story': 'ST', 'refill': 'RP'}.get(k, k[:2].upper()), fill=(255, 213, 79, 255))
        im.save(os.path.join(ITEM_DIR, f'admin_seal_{k}.png'))
    # mod icon
    im = Image.new('RGBA', (128, 128), (74, 20, 140, 255)); d = ImageDraw.Draw(im)
    d.polygon([(64, 12), (104, 34), (100, 90), (64, 118), (28, 90), (24, 34)], fill=(158, 158, 150, 255))
    d.ellipse([40, 46, 56, 58], fill=(33, 33, 33, 255)); d.ellipse([72, 46, 88, 58], fill=(33, 33, 33, 255))
    d.rectangle([44, 86, 84, 92], fill=(33, 33, 33, 255))
    for r in (54, 60): d.ellipse([64 - r, 64 - r, 64 + r, 64 + r], outline=(255, 213, 79, 255), width=3)
    im.resize((32, 32), Image.LANCZOS).save(os.path.join(ROOT, 'jojorpg', 'src', 'main', 'resources', 'assets', 'jojorpg', 'icon.png'))   # asset budget: 32px


# ======================================================================================================================

def write_doc(data, techniques, skipped_passive, skipped_other, styles, form_ids):
    tree = data['category_tree']
    L = ['# JoJo\'s Bizarre Encyclopedia coverage: Part 1 (Phantom Blood) and Part 2 (Battle Tendency)', '',
         f"Source: {data['source']} - crawled {data['crawled']}. Method: {data['method']}.", '',
         '## Category tree', '', '| category | pages | subcategories | in game |', '|---|---:|---:|---|']
    for c, v in tree.items():
        note = v.get('skipped') or ''
        L.append(f"| {c} | {len(v['pages'])} | {len(v['subcats'])} | {note or 'covered'} |")
    L += ['', 'Translation subcategories (`/es`, `/fr`...) and image galleries (book / volume / chapter covers, illustrations, '
          'tailpieces, interviews) are not game content and were not crawled. `Category:Items` is filtered to the items of '
          'Parts 1-2.', '',
          f"## Techniques ({len(techniques)})", '',
          'Every `{{Tech}}` entry on the character pages (and the Hamon, Vampire and Pillar Men pages) is a technique on its '
          'style\'s technique item, unlocked by fighting-style mastery and rank.', '',
          '| technique | style | users | rank |', '|---|---|---|---:|']
    for tid, t in techniques.items():
        L.append(f"| {t['name']} | {t['style']} | {', '.join(t['owners'][:4])} | {t['tier']} |")
    L += ['', f'### Passive abilities ({len(skipped_passive)})', '',
          'Entries describing what a being *is* (superhuman strength, regeneration, absorption, anti-aging...) are not techniques; '
          'they are the fighting styles\' traits instead:', '',
          ', '.join(sorted({f'{n} ({o})' for o, n in skipped_passive}))]
    if skipped_other:
        L += ['', f'### Not from Parts 1-2 ({len(skipped_other)})', '', ', '.join(f'{n} ({o})' for o, n in skipped_other)]
    L += ['', '## Fighting styles', '', '| style | how to learn | traits |', '|---|---|---|']
    how = {i[3]: i[1] for i in ITEMS if i[2] == 'lineage'}
    for sid, (name, *_r) in STYLES.items():
        L.append(f"| {name} | {how.get(sid, '-')} | {STYLE_DESC[sid]} |")
    L += ['', '## Forms', ''] + [f'- {f}' for f in form_ids]
    L += ['', '## Items of Parts 1-2 on the wiki', ''] + [f'- **{k}**: {v[:160]}' for k, v in data['items'].items()]
    with open(os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_parts_1_2.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')


def main():
    data = json.load(open(DOC_JSON, encoding='utf-8'))
    lang = OrderedDict()
    techniques, skipped_passive, skipped_other = build_techniques(data)
    abilities = OrderedDict((tid, ability(tid, t, lang)) for tid, t in techniques.items())
    F = forms(lang, techniques)
    L = lineages(lang, techniques, list(F))
    A = affinities(lang)
    write_items(lang)
    out = {'generated_from': os.path.relpath(DOC_JSON, ROOT), 'crawled': data['crawled'], 'affinities': A, 'lineages': L,
           'abilities': abilities, 'transformations': F, 'lang': lang}
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    with open(os.path.join(ROOT, 'tools', 'catalog', 'generated', 'jojo_techniques.json'), 'w', encoding='utf-8') as f:
        json.dump(techniques, f, indent=1, ensure_ascii=False)
    sprites()
    write_doc(data, techniques, skipped_passive, skipped_other, STYLES, list(F))
    print(f'jojo: {len(abilities)} techniques {dict(Counter(t["style"] for t in techniques.values()))}, {len(L)} styles, '
          f'{len(F)} forms, {len(ITEMS)} awakening items')


if __name__ == '__main__':
    if len(sys.argv) > 2 and sys.argv[1] == '--extract':
        extract(sys.argv[2])
    else:
        main()
