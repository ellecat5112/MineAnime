"""JoJoRPG story mode: Part 1 (Phantom Blood) and Part 2 (Battle Tendency), chapter by chapter.

Reads docs/wiki/jojowiki_parts_1_2.json (jojo_content.py --extract) and tools/catalog/generated/jojo_techniques.json
(jojo_content.py), writes tools/catalog/generated/jojo_story_catalog.json: one quest per manga chapter (113), grouped
by the wiki's story arcs (24), story characters (givers / allies / enemies / bosses fighting with their own techniques),
ambient night spawns (zombies, vampires) and the coverage section of docs/wiki/jojowiki_parts_1_2.md.

Sides come from the wiki categories (Part N Allies / Antagonists) with overrides for characters who switch sides
(Speedwagon, Straizo, Caesar, Stroheim). Each chapter's cast is filtered to characters actually present: alive in that
chapter (DEATH) and belonging to that part (flashbacks of Jonathan in Part 2 are not spawned).
"""
import json
import os
import re
import zlib
from collections import Counter, OrderedDict

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
NS = 'jojorpg'
DOC_JSON = os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_parts_1_2.json')
TECH_JSON = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'jojo_techniques.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'jojo_story_catalog.json')
DOC_MD = os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_parts_1_2.md')

SERIES = ['phantom_blood', 'battle_tendency']
SERIES_NAMES = {'phantom_blood': 'Phantom Blood', 'battle_tendency': 'Battle Tendency'}   # journal tab width
PART_OF = {'Phantom Blood': 'phantom_blood', 'Battle Tendency': 'battle_tendency'}

# chapter of death (later appearances are flashbacks / mentions)
DEATH = {'Danny': 4, 'George Joestar I': 12, 'Dario Brando': 6, 'Will Anthonio Zeppeli': 33, 'Tarkus': 32, 'Bruford': 30, 'Jack the Ripper': 22,
         'Dire': 37, 'Jonathan Joestar': 44, 'Wang Chan': 44, 'Dio Brando': 44, 'Straizo': 52, 'Mario Zeppeli': 60, 'Loggins': 85,
         'Mark': 63, 'Esidisi': 82, 'Caesar Anthonio Zeppeli': 93, 'Wamuu': 104, 'Wired Beck': 72, 'Zombie Commander': 72,
         'Adams': 37, 'Doobie': 38, 'Page, Jones, Plant, and Bornnam': 37, 'Caineghis': 107, 'Winzaleo': 107, 'Eijkman': 107}
NEUTRAL = {'Dario Brando', "Dio's Mother", 'Mary Joestar', 'Aztec Chief', 'Elizabeth I', 'Mary Stuart', "Zeppeli's Father", 'Father Styx',
           '"Memetaa!" Frog', "Poco's Sister", 'Inspector Archer', 'Greenberg', 'Mario Zeppeli', 'George Joestar II', 'Donovan'}
ALWAYS_ALLY = {'Robert E. O. Speedwagon', 'Amato', 'Tattoo', 'Rudol von Stroheim', 'Caesar Anthonio Zeppeli', 'Erina Pendleton',
               'Erina Joestar', 'Smokey Brown', 'Suzi Q', 'Messina', 'Lisa Lisa', 'Joseph Joestar', 'Jonathan Joestar', 'Poco'}
ENEMY_IN = {'Straizo': {'battle_tendency'}, 'Caineghis': SERIES, 'Winzaleo': SERIES, 'Eijkman': SERIES}
BOSSES = {'Dio Brando', 'Jack the Ripper', 'Tarkus', 'Bruford', 'Straizo', 'Santana', 'Esidisi', 'Wamuu', 'Kars', 'Wired Beck'}
PILLAR = {'Santana', 'Esidisi', 'Wamuu', 'Kars'}
MENTORS = {'phantom_blood': ['Will Anthonio Zeppeli', 'Jonathan Joestar', 'Robert E. O. Speedwagon', 'George Joestar I', 'Erina Pendleton',
                             'Tonpetty', 'Poco'],
           'battle_tendency': ['Lisa Lisa', 'Robert E. O. Speedwagon', 'Joseph Joestar', 'Caesar Anthonio Zeppeli', 'Rudol von Stroheim',
                               'Messina', 'Loggins', 'Smokey Brown', 'Suzi Q', 'Erina Joestar']}
DEFAULT_GIVER = {'phantom_blood': 'Jonathan Joestar', 'battle_tendency': 'Joseph Joestar'}
# unnamed groups in the casts that become minion fights
GROUPS = [(re.compile(r'zombie', re.I), 'zombie'), (re.compile(r"vampire|kars's", re.I), 'vampire'),
          (re.compile(r"bullies|vagrants|thug|cabbage", re.I), 'thug')]

# hand-tuned looks for the main cast (tinted procedural layers: skin, eyes, hair + style, outfit, colours, headband, mark)
LOOKS = {
    'Jonathan Joestar': dict(hair=0x1A237E, hair_style='short', outfit='jacket', top=0x37474F, accent=0x263238, pants=0x3E2723),
    'Dio Brando': dict(hair=0xFFD54F, hair_style='short', outfit='jacket', top=0x1B5E20, accent=0xFFD54F, pants=0x263238, eyes=0xC62828, mark='red_eyes'),
    'Will Anthonio Zeppeli': dict(hair=0xF9E79F, hair_style='long', outfit='jacket', top=0x6D4C41, accent=0xF5F5F5, pants=0x4E342E),
    'Robert E. O. Speedwagon': dict(hair=0xFFE082, hair_style='short', outfit='jacket', top=0x8D6E63, accent=0x5D4037, pants=0x3E2723, mark='scar'),
    'Erina Pendleton': dict(hair=0x5D4037, hair_style='long', outfit='robe', top=0x90CAF9, accent=0xF5F5F5, pants=0x90CAF9),
    'Erina Joestar': dict(hair=0xE0E0E0, hair_style='buns', outfit='robe', top=0x5C6BC0, accent=0xF5F5F5, pants=0x5C6BC0),
    'George Joestar I': dict(hair=0xBDBDBD, hair_style='short', outfit='jacket', top=0x3E2723, accent=0xF5F5F5, pants=0x212121),
    'Tonpetty': dict(hair=0x9E9E9E, hair_style='bald', outfit='robe', top=0x795548, accent=0xD7CCC8, pants=0x5D4037),
    'Dire': dict(hair=0x212121, hair_style='long', outfit='robe', top=0x455A64, accent=0xB0BEC5, pants=0x37474F),
    'Straizo': dict(hair=0x212121, hair_style='long', outfit='robe', top=0x4E342E, accent=0xFFD54F, pants=0x3E2723),
    'Poco': dict(hair=0x8D6E63, hair_style='bowl', outfit='shirt', top=0x8D6E63, accent=0x5D4037, pants=0x4E342E),
    'Jack the Ripper': dict(hair=0x212121, hair_style='wild', outfit='cloak', top=0x212121, accent=0x880E4F, pants=0x212121, mark='red_eyes'),
    'Tarkus': dict(hair=0x5D4037, hair_style='bald', outfit='armor', top=0x616161, accent=0x8D6E63, pants=0x424242, mark='red_eyes'),
    'Bruford': dict(hair=0xFFF9C4, hair_style='long', outfit='armor', top=0x9E9E9E, accent=0x1565C0, pants=0x616161, mark='red_eyes'),
    'Wang Chan': dict(hair=0x212121, hair_style='bald', outfit='robe', top=0x6A1B9A, accent=0xFFD54F, pants=0x4A148C, mark='red_eyes'),
    'Joseph Joestar': dict(hair=0x3E2723, hair_style='short', outfit='shirt', top=0x00897B, accent=0xFFEB3B, pants=0x5D4037),
    'Caesar Anthonio Zeppeli': dict(hair=0xFFE082, hair_style='short', outfit='shirt', top=0x7E57C2, accent=0xF8BBD0, pants=0x4527A0, headband=0xEC407A),
    'Lisa Lisa': dict(hair=0x3E2723, hair_style='long', outfit='robe', top=0x212121, accent=0xD32F2F, pants=0x212121, mark='glasses'),
    'Rudol von Stroheim': dict(hair=0xFFE082, hair_style='short', outfit='armor', top=0x556B2F, accent=0x212121, pants=0x3E4A2A),
    'Smokey Brown': dict(skin=0x6D4C41, hair=0x212121, hair_style='short', outfit='shirt', top=0xF5F5F5, accent=0xE53935, pants=0x1565C0),
    'Suzi Q': dict(hair=0xFFE082, hair_style='long', outfit='robe', top=0x81D4FA, accent=0xFFFFFF, pants=0x81D4FA),
    'Messina': dict(hair=0x5D4037, hair_style='bald', outfit='shirt', top=0x795548, accent=0x4E342E, pants=0x3E2723),
    'Loggins': dict(hair=0xEEEEEE, hair_style='long', outfit='robe', top=0x8D6E63, accent=0x5D4037, pants=0x4E342E),
    'Santana': dict(skin=0xD7B49E, hair=0x4E342E, hair_style='wild', outfit='bodysuit', top=0xD7CCC8, accent=0x8D6E63, pants=0x8D6E63, mark='forehead'),
    'Wamuu': dict(skin=0xD7B49E, hair=0x4E342E, hair_style='spiky', outfit='bodysuit', top=0x6D4C41, accent=0xFFC107, pants=0x5D4037, headband=0xFFC107, mark='paint'),
    'Esidisi': dict(skin=0xD7B49E, hair=0xFF8F00, hair_style='long', outfit='bodysuit', top=0x5D4037, accent=0xFFCA28, pants=0x4E342E, headband=0xFFCA28),
    'Kars': dict(skin=0xE0C3A8, hair=0x311B92, hair_style='long', outfit='bodysuit', top=0x3E2723, accent=0xFFD54F, pants=0x3E2723, headband=0xFF7043),
    'Wired Beck': dict(skin=0xE0E0E0, hair=0x9E9E9E, hair_style='bald', outfit='bodysuit', top=0x9E9E9E, accent=0x616161, pants=0x757575, mark='red_eyes'),
    'Mark': dict(hair=0xA1887F, hair_style='short', outfit='jacket', top=0x556B2F, accent=0x3E4A2A, pants=0x3E4A2A),
    'Donovan': dict(hair=0x5D4037, hair_style='short', outfit='jacket', top=0x5D4037, accent=0x3E2723, pants=0x3E2723, mark='scar'),
}
UNDEAD_LOOK = dict(skin=0x9DB58A, eyes=0xC62828, hair=0x3E2723, hair_style='wild', outfit='shirt', top=0x5D4037, accent=0x3E2723, pants=0x3E2723, mark='red_eyes')
VAMPIRE_LOOK = dict(skin=0xE8DCD2, eyes=0xC62828, hair=0x212121, hair_style='short', outfit='cloak', top=0x212121, accent=0x7F0000, pants=0x212121, mark='red_eyes')

# items the story hands out at the end of an arc (or on its first quest with "first:")
ARC_ITEMS = {
    'Dio Brando the Invader': ['first:boxing_gloves', 'first:martial_arts'],
    'Jack the Ripper and Zeppeli the Strange': ['breathing_mask', 'ripple_arts'],
    'Ripple Overdrive': ['ripple_arts'],
    'Fire and Ice, Jonathan and Dio': ['stone_mask', 'vampire_arts'],
    'Straizo vs. Joseph': ['ripple_arts'],
    'Ultimate Warriors from Ancient Times': ['pillar_remains', 'pillar_men_arts'],
    "Stroheim's Unit Strikes Back": ['cybernetic_overhaul', 'science_arts'],
    'Flame Mode Esidisi': ['modes_arts'],
    'The Wind, the Chariot, and Wamuu': ['modes_arts'],
    'Kars the Superbeing is Born': ['red_stone_of_aja', 'modes_arts'],
}
FIGHT = re.compile(r'\b(attack|fight|battle|punch|kill|strike|ambush|zombie|vampire|beat|struggle|charges?)\b', re.I)


def slug(text):
    t = text.lower()
    for a, b in (('ō', 'o'), ('ū', 'u'), ('é', 'e'), ('í', 'i'), ('&', 'and'), ("'", '')):
        t = t.replace(a, b)
    return re.sub(r'[^a-z0-9]+', '_', t).strip('_')


def ns(p):
    return f'{NS}:{p}'


def h(s, n):
    return zlib.crc32(s.encode()) % n


def sentences(text):
    return [s.strip() for s in re.split(r'(?<=[.!?])\s+(?=[A-Z"])', text.replace('\n', ' ')) if len(s.strip()) > 12]


def short(text, limit):
    text = text.strip()
    return text if len(text) <= limit else text[:limit - 1].rsplit(' ', 1)[0] + '…'


def tier_of(ch):
    return 1 + round((ch - 1) * 4 / 43) if ch <= 44 else 5 + round((ch - 45) * 5 / 68)


def main():
    data = json.load(open(DOC_JSON, encoding='utf-8'))
    techniques = json.load(open(TECH_JSON, encoding='utf-8'))
    chars = data['characters']
    chapters = {int(k): v for k, v in data['chapters'].items()}
    by_owner = {}
    for tid, t in techniques.items():
        for o in t['owners']:
            by_owner.setdefault(o, []).append(tid)
    style_pool = {}
    for tid, t in techniques.items():
        style_pool.setdefault(t['style'], []).append(tid)
    quests, npcs, spawns, lang = OrderedDict(), OrderedDict(), OrderedDict(), OrderedDict()

    def t(key, text):
        lang[key] = text
        return {'translate': key, 'fallback': text}

    def cats(name):
        return set(chars.get(name, {}).get('categories', []))

    def part_num(series):
        return 1 if series == 'phantom_blood' else 2

    def present(name, series, ch):
        if DEATH.get(name, 999) < ch: return False
        c = cats(name)
        if name in chars and c and not any(x.startswith(f'Part {part_num(series)}') for x in c):
            return False
        if series == 'phantom_blood' and name == 'Lisa Lisa': return False   # an infant on the ship
        if series == 'battle_tendency' and name in ('Jonathan Joestar', 'Dio Brando', 'Will Anthonio Zeppeli', 'George Joestar I', 'Erina Pendleton'):
            return False
        return True

    def side(name, series):
        if name in NEUTRAL: return 'other'
        if name in ENEMY_IN and series in ENEMY_IN[name]: return 'enemy'
        if name in ALWAYS_ALLY: return 'ally'
        p = part_num(series)
        c = cats(name)
        if f'Part {p} Antagonists' in c and f'Part {p} Allies' not in c: return 'enemy'
        if 'Vampires' in c or 'Pillar Men' in c: return 'enemy'
        return 'ally' if name in chars else 'other'

    def is_undead(name):
        c = cats(name)
        return bool({'Vampires', 'Pillar Men'} & c) or name in ('Tarkus', 'Bruford', 'Jack the Ripper', 'Adams', 'Doobie', 'Page, Jones, Plant, and Bornnam',
                                                                  'Caineghis', 'Winzaleo', 'Eijkman', 'Zombie Commander')

    def abilities_for(name, tier, ch, undead):
        own = [a for a in by_owner.get(name, []) if techniques[a]['tier'] <= max(tier, 1 + ch // 12)]
        if name == 'Straizo' and ch >= 45: own = [a for a in own if techniques[a]['style'] != 'hamon']
        if name == 'Jonathan Joestar' and ch < 18: own = [a for a in own if techniques[a]['style'] != 'hamon']
        if not own:
            if name in PILLAR: own = by_owner.get('Pillar Men', [])[:2]
            elif undead: own = by_owner.get('Zombie', []) + by_owner.get('Vampire', [])[:1]
            elif 'Ripple Users' in cats(name): own = [a for a in ('overdrive', 'zoom_punch') if a in techniques]
            else: own = style_pool.get('brawler', [])[:2]
        return [ns(a) for a in own[:6]]

    def npc(name, role, series, tier, ch):
        base = slug(name)
        human_dio = name == 'Dio Brando' and ch < 12
        if human_dio and role == 'boss': role = 'enemy'
        # one entry per rank so recurring characters grow with the story (Dio at chapter 5 vs chapter 44)
        nid = base if role == 'giver' else f'{base}_{"ally" if role == "ally" else "foe"}_t{tier}' + ('_human' if human_dio else '')
        if nid in npcs: return nid
        undead = is_undead(name) and role in ('enemy', 'boss') and not human_dio
        if name == 'Straizo': undead = series == 'battle_tendency'
        look = dict(LOOKS.get(name) or (VAMPIRE_LOOK if 'Vampires' in cats(name) else UNDEAD_LOOK if undead else
                    dict(hair=0x2B2B2B + h(name, 0x40) * 0x10101, hair_style=['short', 'long', 'wild', 'bowl'][h(name, 4)], outfit='jacket',
                         top=[0x5D4037, 0x37474F, 0x455A64, 0x6D4C41, 0x1B5E20][h(name, 5)], accent=0x263238, pants=0x3E2723)))
        if human_dio:
            look.update(eyes=0x1565C0, top=0x4E342E, accent=0xF5F5F5); look.pop('mark', None)
        if name == 'Straizo' and series == 'battle_tendency':
            look.update(hair_style='long', outfit='jacket', top=0x37474F, eyes=0xC62828, mark='red_eyes')
        d = {'system': NS, 'name': t(f'npc.{NS}.{base}', name), 'role': role, 'appearance': look,
             'abilities': [] if role == 'giver' else ([ns(a) for a in style_pool.get('brawler', [])[:2]] if human_dio else abilities_for(name, tier, ch, undead))}
        if role in ('enemy', 'boss'):
            boss = role == 'boss'
            mult = (2.4 if boss else 1.0) * (1.6 if name in PILLAR else 1.0) * (1.4 if name == 'Kars' else 1.0)
            d.update(health=round((30 + 18 * tier) * mult), attack=round(3 + 0.9 * tier * (1.3 if boss else 1.0) * (1.3 if name in PILLAR else 1.0), 1),
                     power=round(0.9 + 0.12 * tier, 2), cast_interval=40 if boss else 70, speed=0.33 if name not in PILLAR else 0.36,
                     title=t(f'npc.{NS}.title.{"boss" if boss else "foe"}', '☠' if boss else ''))
            if undead: d['undead'] = True
            if name in PILLAR or name in ('Tarkus', 'Wired Beck'): d['scale'] = 1.2
        elif role == 'ally':
            d.update(health=round(60 + 14 * tier), attack=round(4 + tier, 1), power=round(1.0 + 0.12 * tier, 2), cast_interval=60, speed=0.34)
        else:
            d.update(health=200, attack=0)
        npcs[nid] = d
        return nid

    def minion(kind, series, tier):
        nid = f'{kind}_{series}'
        if nid in npcs: return nid
        name, look, abil, undead = {
            'zombie': ('Zombie', UNDEAD_LOOK, by_owner.get('Zombie', []) + ['claws'], True),
            'vampire': ('Vampire', VAMPIRE_LOOK, ['claws', 'hypnosis', 'vaporization_freezing_technique'], True),
            'thug': ('Ogre Street Thug' if series == 'phantom_blood' else 'Street Thug',
                     dict(hair=0x3E2723, hair_style='wild', outfit='jacket', top=0x5D4037, accent=0x3E2723, pants=0x424242, mark='scar'),
                     style_pool.get('brawler', [])[:1], False),
        }[kind]
        key = f'npc.{NS}.{slug(name)}'
        d = {'system': NS, 'name': t(key, name), 'role': 'enemy', 'appearance': dict(look),
             'abilities': [ns(a) for a in abil if a in techniques][:3], 'health': round(18 + 9 * tier), 'attack': round(2 + 0.7 * tier, 1),
             'power': round(0.8 + 0.1 * tier, 2), 'cast_interval': 90, 'speed': 0.3, 'title': t(f'npc.{NS}.title.foe', '')}
        if undead: d['undead'] = True
        npcs[nid] = d
        return nid

    arcs = [a for a in data['arcs'] if a['chapters']]
    prev = None
    arc_rows = []
    qn = 0
    for ai, a in enumerate(arcs):
        series = PART_OF[a['part']]
        arc_slug = slug(a['title'])[:48]
        arc_name = t(f'quest.{NS}.arc.{arc_slug}', a['title'])
        chs = [c for c in a['chapters'] if c in chapters]
        arc_enemies = Counter()
        for c in chs:
            ch = chapters[c]
            for x in ch['cast']:
                n = x['name']
                if side(n, series) == 'enemy' and present(n, series, c):
                    arc_enemies[n] += 1 + ch['summary'].count(n.split()[0])
        top = arc_enemies.most_common(1)[0][0] if arc_enemies else None
        row = {'arc': a['title'], 'series': series, 'chapters': chs, 'quests': [], 'boss': top}
        for bi, c in enumerate(chs):
            qn += 1
            ch = chapters[c]
            tier = tier_of(c)
            qid = f'{series}_{c:03d}'
            title = ch['title'] if not ch['title'].startswith('{{') else f"{a['title']}, Part {bi + 1}"
            cast = [x['name'] for x in ch['cast']]
            summ = ch['summary']
            here = [n for n in OrderedDict.fromkeys(cast) if present(n, series, c)]
            allies = [n for n in here if side(n, series) == 'ally']
            enemies = [n for n in here if side(n, series) == 'enemy' and summ.count(n.split()[0].rstrip(',')) >= (1 if n == top else 3 if n in BOSSES else 2)]
            groups = []
            for n in cast:
                for rx, kind in GROUPS:
                    if rx.search(n) and n not in chars and kind not in groups:
                        groups.append(kind)
            mentors = [m for m in MENTORS[series] if m in allies]
            giver_name = (mentors or allies or [DEFAULT_GIVER[series]])[0]
            if not present(giver_name, series, c): giver_name = DEFAULT_GIVER[series] if present(DEFAULT_GIVER[series], series, c) else 'Robert E. O. Speedwagon'
            giver = npc(giver_name, 'giver', series, tier, c)
            fighters = [n for n in allies if n != giver_name and n not in ('Erina Pendleton', 'Erina Joestar', 'Poco', 'Suzi Q')][:2]
            sents = sentences(summ)
            objectives = [{'type': 'talk', 'target': ns(giver), 'text': t(f'quest.{NS}.{qid}.o1', f'Talk to {giver_name}')}]
            if enemies or groups:
                objectives.append({'type': 'travel', 'count': 50 + h(qid, 40), 'text': t(f'quest.{NS}.{qid}.travel', short(f'Head out: {title}', 70))})
                for kind in groups[:1]:
                    if kind == 'thug' or c >= 8:
                        objectives.append({'type': 'defeat', 'target': ns(minion(kind, series, tier)), 'count': 3,
                                           'text': t(f'quest.{NS}.minion.{kind}', {'zombie': 'Defeat the zombies', 'vampire': 'Defeat the vampires',
                                                                                   'thug': 'Beat up the thugs'}[kind])})
                for n in sorted(enemies[:3], key=lambda n: (n == top, arc_enemies[n])):
                    role = 'boss' if n in BOSSES and (n == top or n in PILLAR or n == 'Dio Brando') else 'enemy'
                    nid = npc(n, role, series, tier, c)
                    objectives.append({'type': 'defeat', 'target': ns(nid), 'text': t(f'quest.{NS}.defeat.{nid}', f'Defeat {n}')})
            else:
                fight = c >= 12 and any(FIGHT.search(s) for s in sents)
                others = [n for n in allies if n != giver_name]
                objectives.append({'type': 'travel', 'count': 40 + h(qid, 30), 'text': t(f'quest.{NS}.{qid}.travel', short(f'Go: {title}', 70))})
                if fight:
                    kind = 'vampire' if series == 'battle_tendency' and c >= 63 else ('zombie' if c >= 12 else 'thug')
                    objectives.append({'type': 'defeat', 'target': ns(minion(kind, series, tier)), 'count': 3,
                                       'text': t(f'quest.{NS}.minion.{kind}', {'zombie': 'Defeat the zombies', 'vampire': 'Defeat the vampires',
                                                                               'thug': 'Beat up the thugs'}[kind])})
                elif others:
                    nid = npc(others[0], 'giver', series, tier, c)
                    objectives.append({'type': 'talk', 'target': ns(nid), 'text': t(f'quest.{NS}.{qid}.o3', f'Meet {others[0]}')})
                elif qn > 1:
                    objectives.append({'type': 'use_ability', 'count': 3, 'text': t(f'quest.{NS}.train', 'Train: use 3 techniques')})
            intro = [short(s, 220) for s in sents[:3]] or [short(a['summary'] or title, 220)]
            outro = [short(s, 220) for s in sents[-2:]] if len(sents) > 3 else []
            q = {'system': NS, 'name': t(f'quest.{NS}.{qid}', short(f'{c}. {title}', 64)),
                 'description': t(f'quest.{NS}.{qid}.desc', short(' '.join(sents[:3]) if sents else a['summary'], 420)),
                 'series': series, 'arc': arc_name, 'order': SERIES.index(series) * 100000 + c * 10,
                 'requires': [ns(prev)] if prev else [], 'recommended_tier': tier,
                 'giver': ns(giver),
                 'intro': [t(f'quest.{NS}.{qid}.intro{i}', s) for i, s in enumerate(intro)],
                 'outro': [t(f'quest.{NS}.{qid}.outro{i}', s) for i, s in enumerate(outro)] or [t(f'quest.{NS}.generic.outro', 'To be continued...')],
                 'objectives': objectives, 'rewards': {'skill_points': 1, 'experience': 15 + 3 * tier}}
            if (enemies or groups) and fighters:
                q['allies'] = [ns(npc(n, 'ally', series, tier, c)) for n in fighters]
            quests[qid] = q
            row['quests'].append(qid)
            prev = qid
        # end of arc: rank up to the next arc's level, story items
        last, first = quests[row['quests'][-1]], quests[row['quests'][0]]
        nxt = tier_of(arcs[ai + 1]['chapters'][0]) if ai + 1 < len(arcs) else 10
        if nxt > tier_of(chs[-1]) or ai + 1 == len(arcs): last['rewards']['tier'] = nxt
        else: last['rewards']['tier'] = tier_of(chs[-1])
        last['rewards']['skill_points'] = 3
        for it in ARC_ITEMS.get(a['title'], []):
            target = first if it.startswith('first:') else last
            target['rewards'].setdefault('items', []).append({'id': ns(it.replace('first:', '')), 'count': 1})
        arc_rows.append(row)

    # ambient night encounters
    def spawn(sid, npc_id, weight, group, night=True, light=7, tier=0, nearby=6):
        spawns[sid] = {'system': NS, 'npc': ns(npc_id), 'weight': weight, 'group': group, 'night_only': night, 'max_light': light,
                       'min_tier': tier, 'max_nearby': nearby}
    spawn('zombie', minion('zombie', 'phantom_blood', 2), 10, [2, 4], tier=1)
    spawn('zombie_strong', minion('zombie', 'battle_tendency', 6), 6, [2, 3], tier=5)
    spawn('vampire', minion('vampire', 'battle_tendency', 6), 4, [1, 2], tier=3, nearby=4)
    spawn('thug', minion('thug', 'phantom_blood', 1), 2, [1, 3], night=False, light=15, tier=1, nearby=3)

    lang.update({f'quest.series.{NS}.{s}': n for s, n in SERIES_NAMES.items()})
    out = {'generated_from': os.path.relpath(DOC_JSON, ROOT), 'crawled': data['crawled'], 'quests': quests, 'npcs': npcs, 'spawns': spawns,
           'lang': lang}
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    write_doc(arc_rows, quests, npcs, spawns)
    print(f"jojo story: {len(arc_rows)} arcs, {len(quests)} quests, {len(npcs)} story characters "
          f"{dict(Counter(n['role'] for n in npcs.values()))}, {len(spawns)} ambient spawns")


def write_doc(arc_rows, quests, npcs, spawns):
    marker = '\n## Story mode'
    doc = open(DOC_MD, encoding='utf-8').read().split(marker)[0].rstrip('\n')
    L = [doc, '', '## Story mode', '',
         f'{len(quests)} quests - one per manga chapter of Parts 1-2 - in the {len(arc_rows)} story arcs of the wiki, chained in '
         'order. Each quest: talk to the chapter\'s mentor, travel to the encounter, defeat that chapter\'s antagonists (with the '
         'chapter\'s allies fighting beside you) or train. Arc finales grant rank, skill points and the story items '
         '(Ogre Street Gloves, Ripple Breathing Mask, Stone Mask, Pillar remains, Cybernetic Overhaul, Red Stone of Aja).', '',
         '| arc | part | chapters | quests | boss |', '|---|---|---|---:|---|']
    for r in arc_rows:
        L.append(f"| {r['arc']} | {r['series']} | {r['chapters'][0]}-{r['chapters'][-1]} | {len(r['quests'])} | {r['boss'] or '-'} |")
    L += ['', f'### Story characters ({len(npcs)})', '', ', '.join(sorted({n["name"]["fallback"] + " (" + n["role"] + ")" for n in npcs.values()})),
          '', '### Night encounters', ''] + [f"- {k}: {v['npc']} x{v['group'][0]}-{v['group'][1]}, rank {v['min_tier']}+" for k, v in spawns.items()]
    open(DOC_MD, 'w', encoding='utf-8').write('\n'.join(L) + '\n')


if __name__ == '__main__':
    main()
