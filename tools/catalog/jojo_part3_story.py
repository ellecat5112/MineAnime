"""JoJoRPG story mode, Part 3: Stardust Crusaders, chapter by chapter (152 quests in the wiki's 28 story arcs).

Reads docs/wiki/jojowiki_part_3.json (jojo_stands.py --extract) and the Stand catalog (jojo_stands.py), writes
tools/catalog/generated/jojo_part3_story_catalog.json: quests chained after Battle Tendency, story characters who fight
with their real Stands - each Stand user calls out their manifestation (an entity) in battle and casts its abilities -
Joestar Group allies fighting at the player's side with theirs, night encounters with DIO's agents and vampires, and
the Part 3 section of docs/wiki/jojowiki_part_3.md.

Sides come from the wiki categories (Part 3 Allies / Antagonists / Joestar Group / Egypt 9 Glory Gods); Kakyoin and
Polnareff are DIO's flesh-bud assassins until their own arcs end. Rewards: the Stand Arrow at the start (the player's
own Stand awakens with Holy's), and each arc boss's Stand Disc at its end.
"""
import json
import os
import re
import sys
from collections import Counter, OrderedDict

sys.path.insert(0, os.path.dirname(__file__))
from jojo_story import slug, h, sentences, short  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
NS = 'jojorpg'
DOC_JSON = os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_part_3.json')
STANDS_JSON = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'jojo_stands_catalog.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'jojo_part3_story_catalog.json')
DOC_MD = os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_part_3.md')
SERIES = 'stardust_crusaders'
PREVIOUS = f'{NS}:battle_tendency_113'     # Part 3 continues after the last chapter of Battle Tendency

DEATH = {'Gray Fly': 123, 'Imposter Captain Tennille': 129, 'Forever': 132, 'Devo the Cursed': 135, 'J. Geil': 145, 'Nena': 149,
         'Enya the Hag': 159, 'Steely Dan': 165, 'Arabia Fats': 167, 'Mannish Boy': 173, 'Cameo': 178, 'Midler': 182, "N'Doul": 185,
         'Pet Shop': 226, 'Muhammad Avdol': 243, 'Iggy': 245, 'Vanilla Ice': 246, 'Noriaki Kakyoin': 255, 'Dio Brando': 265, 'DIO': 265,
         'Chérie Polnareff': 0, 'Wilson Phillips': 262, 'Kenny G.': 237, 'Nukesaku': 238}
ENEMY_UNTIL = {'Noriaki Kakyoin': 121, 'Jean Pierre Polnareff': 126}      # flesh buds removed in their arcs
ALLY = {'Jotaro Kujo', 'Joseph Joestar', 'Muhammad Avdol', 'Noriaki Kakyoin', 'Jean Pierre Polnareff', 'Iggy', 'Holy Kujo', 'Merlai Anne'}
NEUTRAL = {'Sadao Kujo', 'Malèna', 'Roses', 'Suzi Q', 'Chérie Polnareff', 'Wilson Phillips', 'Nukesaku', 'Joestar Group', 'Agents of DIO',
           'Egypt 9 Glory Gods', 'Jonathan Joestar'}
MENTORS = ['Joseph Joestar', 'Jotaro Kujo', 'Muhammad Avdol', 'Noriaki Kakyoin', 'Jean Pierre Polnareff', 'Holy Kujo']
ALIASES = {'DIO': 'Dio Brando'}
BOSS_ALWAYS = {'Dio Brando', 'Vanilla Ice', 'Pet Shop', "Telence T. D'Arby", "Daniel J. D'Arby", 'Enya the Hag', "N'Doul", 'J. Geil'}

LOOKS = {
    'Jotaro Kujo': dict(hair=0x111111, hair_style='short', outfit='jacket', top=0x1A1A2E, accent=0xFFD54F, pants=0x1A1A2E, headband=0x111111),
    'Joseph Joestar': dict(hair=0xBDBDBD, hair_style='short', outfit='jacket', top=0x6D4C41, accent=0xA1887F, pants=0x4E342E),
    'Muhammad Avdol': dict(skin=0x6D4C41, hair=0x212121, hair_style='ponytail', outfit='robe', top=0xC62828, accent=0xFFD54F, pants=0x8E1B1B, headband=0xFFD54F),
    'Noriaki Kakyoin': dict(hair=0xC62828, hair_style='short', outfit='jacket', top=0x2E7D32, accent=0xFFD54F, pants=0x1B5E20),
    'Jean Pierre Polnareff': dict(hair=0xECEFF1, hair_style='spiky', outfit='shirt', top=0x263238, accent=0xFFD54F, pants=0x37474F),
    'Iggy': dict(skin=0x3E2723, hair=0x212121, hair_style='wild', outfit='bodysuit', top=0x5D4037, accent=0xD7CCC8, pants=0x5D4037),
    'Holy Kujo': dict(hair=0xFFE082, hair_style='long', outfit='robe', top=0xF48FB1, accent=0xFFFFFF, pants=0xF48FB1),
    'Dio Brando': dict(hair=0xFFD54F, hair_style='short', outfit='bodysuit', top=0xFFD54F, accent=0x2E7D32, pants=0xFBC02D, eyes=0xC62828, mark='red_eyes'),
    'Vanilla Ice': dict(hair=0xB0BEC5, hair_style='long', outfit='bodysuit', top=0x6A1B9A, accent=0xFFD54F, pants=0x4A148C),
    'Hol Horse': dict(hair=0x5D4037, hair_style='long', outfit='jacket', top=0x8D6E63, accent=0x3E2723, pants=0x5D4037, headband=0x5D4037),
    'J. Geil': dict(hair=0x212121, hair_style='long', outfit='jacket', top=0x424242, accent=0x212121, pants=0x212121),
    'Enya the Hag': dict(hair=0xE0E0E0, hair_style='long', outfit='robe', top=0x4A148C, accent=0xFFD54F, pants=0x4A148C),
    "N'Doul": dict(skin=0x8D6E63, hair=0x212121, hair_style='short', outfit='robe', top=0xD7CCC8, accent=0x8D6E63, pants=0xBCAAA4, mark='glasses'),
    'Pet Shop': dict(skin=0x8D6E63, hair=0x5D4037, hair_style='spiky', outfit='bodysuit', top=0x795548, accent=0xFFD54F, pants=0x5D4037),
    'Mariah': dict(hair=0xE0E0E0, hair_style='long', outfit='bodysuit', top=0x212121, accent=0x9E9E9E, pants=0x212121),
    'Alessi': dict(hair=0x212121, hair_style='bald', outfit='jacket', top=0x5D4037, accent=0x3E2723, pants=0x4E342E, mark='scar'),
    "Daniel J. D'Arby": dict(hair=0x212121, hair_style='short', outfit='jacket', top=0x424242, accent=0xFFD54F, pants=0x212121),
    "Telence T. D'Arby": dict(hair=0x8D6E63, hair_style='short', outfit='jacket', top=0x1565C0, accent=0xFFFFFF, pants=0x0D47A1, mark='glasses'),
    'Kenny G.': dict(hair=0x5D4037, hair_style='long', outfit='robe', top=0x6A1B9A, accent=0xFFD54F, pants=0x4A148C),
    'Rubber Soul': dict(hair=0xFFE082, hair_style='short', outfit='shirt', top=0x1565C0, accent=0xFFFFFF, pants=0x263238),
    'Steely Dan': dict(hair=0x5D4037, hair_style='short', outfit='jacket', top=0xECEFF1, accent=0x212121, pants=0xECEFF1),
    'Nena': dict(hair=0x212121, hair_style='long', outfit='robe', top=0xF8BBD0, accent=0xFFFFFF, pants=0xF8BBD0),
    'Midler': dict(hair=0x5D4037, hair_style='long', outfit='robe', top=0x1565C0, accent=0xFFD54F, pants=0x1565C0, mark='mask'),
    'Oingo': dict(hair=0x212121, hair_style='bald', outfit='shirt', top=0xA1887F, accent=0x5D4037, pants=0x4E342E),
    'Boingo': dict(hair=0x212121, hair_style='bowl', outfit='robe', top=0xFFD54F, accent=0xC62828, pants=0x5D4037),
    'Cameo': dict(hair=0x212121, hair_style='short', outfit='robe', top=0x8D6E63, accent=0x5D4037, pants=0x5D4037),
    'Mannish Boy': dict(hair=0x8D6E63, hair_style='bald', outfit='robe', top=0xFFFFFF, accent=0x90CAF9, pants=0xFFFFFF),
    'Arabia Fats': dict(skin=0x8D6E63, hair=0x212121, hair_style='bald', outfit='robe', top=0xFFB300, accent=0xFFFFFF, pants=0x5D4037),
    'Gray Fly': dict(hair=0x9E9E9E, hair_style='bald', outfit='jacket', top=0x616161, accent=0x424242, pants=0x424242),
    'Forever': dict(skin=0xE65100, hair=0xBF360C, hair_style='wild', outfit='bodysuit', top=0xE65100, accent=0xBF360C, pants=0xE65100),
    'Devo the Cursed': dict(skin=0x8D6E63, hair=0x212121, hair_style='wild', outfit='robe', top=0x4E342E, accent=0xFFD54F, pants=0x3E2723, mark='paint'),
    'Imposter Captain Tennille': dict(hair=0xE0E0E0, hair_style='short', outfit='jacket', top=0xFFFFFF, accent=0x1565C0, pants=0x0D47A1),
    'ZZ': dict(hair=0x212121, hair_style='short', outfit='shirt', top=0x8D6E63, accent=0x5D4037, pants=0x37474F),
    'Chaka': dict(hair=0x212121, hair_style='short', outfit='robe', top=0xD7CCC8, accent=0x8D6E63, pants=0xBCAAA4),
    'Khan': dict(hair=0x212121, hair_style='short', outfit='robe', top=0xBCAAA4, accent=0x5D4037, pants=0xBCAAA4),
    'Merlai Anne': dict(hair=0x212121, hair_style='ponytail', outfit='shirt', top=0xFFFFFF, accent=0xC62828, pants=0x1565C0),
}
DEFAULT_LOOK = dict(hair=0x2B2B2B, hair_style='short', outfit='jacket', top=0x455A64, accent=0x263238, pants=0x3E2723)
AGENT_LOOK = dict(hair=0x3E2723, hair_style='wild', outfit='jacket', top=0x37474F, accent=0x6A1B9A, pants=0x263238, mark='scar')
VAMPIRE_LOOK = dict(skin=0xE8DCD2, eyes=0xC62828, hair=0x212121, hair_style='short', outfit='cloak', top=0x212121, accent=0x7F0000, pants=0x212121, mark='red_eyes')
FIGHT = re.compile(r'\b(attack|fight|battle|punch|kill|strike|ambush|assassin|stab|beat|struggle|charges?|shoot)\b', re.I)


def tier_of(ch):
    return 10 + round((ch - 114) * 6 / 151)


def main():
    data = json.load(open(DOC_JSON, encoding='utf-8'))
    stands = json.load(open(STANDS_JSON, encoding='utf-8'))
    man, abilities = stands['manifestations'], stands['abilities']
    page_of = {v['name']['fallback']: k for k, v in man.items()}
    chars = data['characters']
    chapters = {int(k): v for k, v in data['chapters'].items()}
    stand_pages = set(data['stands'])
    quests, npcs, spawns, lang = OrderedDict(), OrderedDict(), OrderedDict(), OrderedDict()

    def t(key, txt):
        lang[key] = txt
        return {'translate': key, 'fallback': txt}

    def stand_of(name):
        s = chars.get(name, {}).get('stand', '')
        for page, sid in sorted(page_of.items(), key=lambda kv: -len(kv[0])):
            if s.startswith(page): return sid
        return {'Holy Kujo': 'holys_stand'}.get(name)

    def side(name, ch):
        if name in NEUTRAL or name in stand_pages: return 'other'
        if name in ENEMY_UNTIL: return 'enemy' if ch <= ENEMY_UNTIL[name] else 'ally'
        if name in ALLY: return 'ally'
        c = set(chars.get(name, {}).get('categories', []))
        if 'Part 3 Antagonists' in c or 'Egypt 9 Glory Gods' in c: return 'enemy'
        if 'Part 3 Allies' in c: return 'ally'
        return 'other'

    def present(name, ch):
        if name == 'Dio Brando' and ch < 247: return False   # DIO only appears in person in DIO's World (he watches from afar before)
        return DEATH.get(name, 999) >= ch and name in chars

    def kit(sid, tier):
        if not sid: return []
        return [a for a in man[sid]['abilities'][1:] if abilities[a.split(':')[1]]['required_tier'] <= max(tier, 4)]

    def npc(name, role, tier, ch):
        base = slug(name)
        nid = base if role == 'giver' else f'{base}_{"ally" if role == "ally" else "foe"}_t{tier}'
        if name in ENEMY_UNTIL and role != 'giver' and ch <= ENEMY_UNTIL[name]: nid = f'{base}_flesh_bud'
        if nid in npcs: return nid
        sid = stand_of(name)
        d = {'system': NS, 'name': t(f'npc.{NS}.{base}', name), 'role': role, 'appearance': dict(LOOKS.get(name, DEFAULT_LOOK)),
             'abilities': [] if role == 'giver' else kit(sid, tier)}
        if sid: d['manifestation'] = f'{NS}:{sid}'
        if name in ENEMY_UNTIL and ch <= ENEMY_UNTIL[name]: d['appearance']['mark'] = 'forehead'   # DIO's flesh bud
        if role in ('enemy', 'boss'):
            boss = role == 'boss'
            mult = (2.4 if boss else 1.0) * (1.6 if name == 'Dio Brando' else 1.0)
            d.update(health=round((30 + 18 * tier) * mult), attack=round(3 + 0.9 * tier * (1.3 if boss else 1.0), 1),
                     power=round(0.9 + 0.1 * tier, 2), cast_interval=45 if boss else 70, speed=0.33,
                     title=t(f'npc.{NS}.title.{"boss" if boss else "foe"}', '☠' if boss else ''))
            if name in ('Dio Brando', 'Vanilla Ice'): d['undead'] = True
        elif role == 'ally':
            d.update(health=round(60 + 14 * tier), attack=round(4 + tier, 1), power=round(1.0 + 0.1 * tier, 2), cast_interval=60, speed=0.34)
        else:
            d.update(health=200, attack=0)
        if name in ('Iggy', 'Pet Shop'): d['scale'] = 0.55
        if name == 'Forever': d['scale'] = 1.15
        npcs[nid] = d
        return nid

    def minion(kind, tier):
        nid = f'{kind}_t{tier}'
        if nid in npcs: return nid
        name, look, abil, undead = {
            'agent': ("DIO's Agent", AGENT_LOOK, ['jojorpg:razor_frenzy'] if 'razor_frenzy' in abilities else [], False),
            'puppet': ("Enya's Puppet", dict(AGENT_LOOK, mark='red_eyes', skin=0xBCAAA4), [], False),
            'vampire': ('Vampire', VAMPIRE_LOOK, ['jojorpg:claws', 'jojorpg:hypnosis'], True),
        }[kind]
        d = {'system': NS, 'name': t(f'npc.{NS}.{slug(name)}', name), 'role': 'enemy', 'appearance': dict(look), 'abilities': abil,
             'health': round(18 + 9 * tier), 'attack': round(2 + 0.7 * tier, 1), 'power': round(0.8 + 0.1 * tier, 2), 'cast_interval': 90,
             'speed': 0.3, 'title': t(f'npc.{NS}.title.foe', '')}
        if undead: d['undead'] = True
        npcs[nid] = d
        return nid

    prev = PREVIOUS
    arc_rows = []
    arcs = [a for a in data['arcs'] if a['chapters']]
    for ai, a in enumerate(arcs):
        arc_slug = slug(a['title'])[:48]
        arc_name = t(f'quest.{NS}.arc.{arc_slug}', a['title'])
        chs = [c for c in a['chapters'] if c in chapters]
        foes = Counter()
        for c in chs:
            for x in chapters[c]['cast']:
                n = ALIASES.get(x['name'], x['name'])
                if side(n, c) == 'enemy' and present(n, c): foes[n] += 1 + chapters[c]['summary'].count(n.split()[0])
        top = foes.most_common(1)[0][0] if foes else None
        row = {'arc': a['title'], 'chapters': chs, 'quests': [], 'boss': top}
        for bi, c in enumerate(chs):
            ch = chapters[c]
            tier = tier_of(c)
            qid = f'{SERIES}_{c:03d}'
            title = ch['title'] if not ch['title'].startswith('{{') else f"{a['title']}, Part {bi + 1}"
            cast = [ALIASES.get(x['name'], x['name']) for x in ch['cast']]
            summ = ch['summary']
            here = [n for n in OrderedDict.fromkeys(cast) if present(n, c)]
            allies = [n for n in here if side(n, c) == 'ally']
            first = lambda n: n.split()[0].rstrip(',').replace('Dio', 'DIO') if n == 'Dio Brando' else n.split()[0].rstrip(',')
            enemies = [n for n in here if side(n, c) == 'enemy' and (n == top or summ.count(first(n)) >= 2)]
            mentors = [m for m in MENTORS if m in allies]
            giver_name = (mentors or ['Joseph Joestar'])[0]
            giver = npc(giver_name, 'giver', tier, c)
            fighters = [n for n in allies if n != giver_name and n not in ('Holy Kujo', 'Merlai Anne')][:2]
            sents = sentences(summ)
            objectives = [{'type': 'talk', 'target': f'{NS}:{giver}', 'text': t(f'quest.{NS}.{qid}.o1', f'Talk to {giver_name}')}]
            groups = [k for x, k in (("Enya's Puppets", 'puppet'),) if x in cast]
            if enemies or groups:
                objectives.append({'type': 'travel', 'count': 50 + h(qid, 40), 'text': t(f'quest.{NS}.{qid}.travel', short(f'Head out: {title}', 70))})
                for kind in groups:
                    objectives.append({'type': 'defeat', 'target': f'{NS}:{minion(kind, tier)}', 'count': 3,
                                       'text': t(f'quest.{NS}.minion.{kind}', "Defeat Enya's puppets")})
                for n in sorted(enemies[:3], key=lambda n: (n == top, foes[n])):
                    role = 'boss' if (n == top or n in BOSS_ALWAYS) else 'enemy'
                    nid = npc(n, role, tier, c)
                    objectives.append({'type': 'defeat', 'target': f'{NS}:{nid}', 'text': t(f'quest.{NS}.defeat.{nid}', f'Defeat {n}')})
            else:
                fight = any(FIGHT.search(s) for s in sents)
                others = [n for n in allies if n != giver_name]
                objectives.append({'type': 'travel', 'count': 40 + h(qid, 30), 'text': t(f'quest.{NS}.{qid}.travel', short(f'Go: {title}', 70))})
                if fight:
                    kind = 'vampire' if c >= 247 else 'agent'
                    objectives.append({'type': 'defeat', 'target': f'{NS}:{minion(kind, tier)}', 'count': 3,
                                       'text': t(f'quest.{NS}.minion.{kind}', {'vampire': "Defeat DIO's vampires", 'agent': "Defeat DIO's agents"}[kind])})
                elif others:
                    nid = npc(others[0], 'giver', tier, c)
                    objectives.append({'type': 'talk', 'target': f'{NS}:{nid}', 'text': t(f'quest.{NS}.{qid}.o3', f'Meet {others[0]}')})
                else:
                    objectives.append({'type': 'use_ability', 'count': 3, 'text': t(f'quest.{NS}.train_stand', 'Train your Stand: use 3 Stand techniques')})
            intro = [short(s, 220) for s in sents[:3]] or [short(a['summary'] or title, 220)]
            outro = [short(s, 220) for s in sents[-2:]] if len(sents) > 3 else []
            q = {'system': NS, 'name': t(f'quest.{NS}.{qid}', short(f'{c}. {title}', 64)),
                 'description': t(f'quest.{NS}.{qid}.desc', short(' '.join(sents[:3]) if sents else a['summary'], 420)),
                 'series': SERIES, 'arc': arc_name, 'order': 300000 + c * 10, 'requires': [prev], 'recommended_tier': tier,
                 'giver': f'{NS}:{giver}',
                 'intro': [t(f'quest.{NS}.{qid}.intro{i}', s) for i, s in enumerate(intro)],
                 'outro': [t(f'quest.{NS}.{qid}.outro{i}', s) for i, s in enumerate(outro)] or [t(f'quest.{NS}.generic.outro', 'To be continued...')],
                 'objectives': objectives, 'rewards': {'skill_points': 1, 'experience': 20 + 3 * tier}}
            if (enemies or groups) and fighters:
                q['allies'] = [f'{NS}:{npc(n, "ally", tier, c)}' for n in fighters]
            quests[qid] = q
            row['quests'].append(qid)
            prev = f'{NS}:{qid}'
        first_q, last_q = quests[row['quests'][0]], quests[row['quests'][-1]]
        nxt = tier_of(arcs[ai + 1]['chapters'][0]) if ai + 1 < len(arcs) else 17
        last_q['rewards']['tier'] = max(nxt, tier_of(chs[-1]))
        last_q['rewards']['skill_points'] = 3
        if ai == 0:
            first_q['rewards'].setdefault('items', []).extend([{'id': f'{NS}:stand_arrow', 'count': 1}, {'id': f'{NS}:stand_arts', 'count': 1}])
        boss_stand = stand_of(top) if top else None
        if boss_stand and side(top, chs[-1]) == 'enemy':
            last_q['rewards'].setdefault('items', []).append({'id': f'{NS}:disc_{boss_stand}', 'count': 1})
            row['disc'] = boss_stand
        arc_rows.append(row)

    def spawn(sid, npc_id, weight, group, night=True, light=7, tier=10, nearby=5):
        spawns[sid] = {'system': NS, 'npc': f'{NS}:{npc_id}', 'weight': weight, 'group': group, 'night_only': night, 'max_light': light,
                       'min_tier': tier, 'max_nearby': nearby}
    spawn('dio_agent', minion('agent', 11), 4, [1, 3], night=False, light=15, tier=10, nearby=3)
    spawn('dio_vampire', minion('vampire', 14), 4, [1, 2], tier=12, nearby=4)
    lang[f'quest.series.{NS}.{SERIES}'] = 'Stardust Crusaders'
    out = {'generated_from': os.path.relpath(DOC_JSON, ROOT), 'crawled': data['crawled'], 'quests': quests, 'npcs': npcs, 'spawns': spawns,
           'lang': lang}
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    write_doc(arc_rows, quests, npcs)
    stand_users = sum(1 for n in npcs.values() if 'manifestation' in n)
    print(f"part 3 story: {len(arc_rows)} arcs, {len(quests)} quests, {len(npcs)} characters ({stand_users} with Stands) "
          f"{dict(Counter(n['role'] for n in npcs.values()))}")


def write_doc(arc_rows, quests, npcs):
    marker = '\n## Story mode'
    doc = open(DOC_MD, encoding='utf-8').read().split(marker)[0].rstrip('\n')
    L = [doc, '', '## Story mode', '',
         f'{len(quests)} quests - one per chapter of Stardust Crusaders (114-265) - in the {len(arc_rows)} story arcs of the wiki, '
         'continuing after Battle Tendency. Every Stand user fights with their Stand (it manifests as an entity and casts its '
         'abilities); Joestar Group members join your fights with theirs. Chapter 114 gives the Stand Arrow; each arc boss drops '
         'their Stand Disc.', '', '| arc | chapters | quests | boss | Stand Disc |', '|---|---|---:|---|---|']
    for r in arc_rows:
        L.append(f"| {r['arc']} | {r['chapters'][0]}-{r['chapters'][-1]} | {len(r['quests'])} | {r['boss'] or '-'} | {r.get('disc', '-')} |")
    L += ['', f'### Story characters ({len(npcs)})', '',
          ', '.join(sorted({n['name']['fallback'] + ' (' + n['role'] + (', ' + n['manifestation'].split(':')[1] if 'manifestation' in n else '') + ')'
                            for n in npcs.values()}))]
    open(DOC_MD, 'w', encoding='utf-8').write('\n'.join(L) + '\n')


if __name__ == '__main__':
    main()
