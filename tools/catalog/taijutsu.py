"""Taijutsu catalog (Narutopedia "Jutsu classification: Taijutsu") for NarutoRPG.

Input : docs/wiki/narutopedia_taijutsu.json (crawl: Category:Taijutsu tree + the Semantic MediaWiki grouping
        [[Jutsu classification::Taijutsu]] with every page's infobox fields)
        tools/catalog/generated/{clan,village,nature,fanon_genjutsu}_catalog.json (abilities that already exist)
Output: tools/catalog/generated/taijutsu_catalog.json (AbilityDefinition entries in codec shape + lang),
        docs/wiki/narutopedia_taijutsu.md

Unlike the generic jutsu mapping in clans.py, every taijutsu is matched to a move archetype from its name (punch, kick,
spinning kick, rising launcher, heel drop, barrage, charge, air-pressure palm, slash, grapple, stance, gate) so the
techniques play differently: multi-hit follow-ups, launches, spins, long charges and pressure waves, each with its own
cast animation (tools/catalog/animations.py). Pure taijutsu costs stamina, not chakra. Run after natures.py.
"""
import json
import os
import re
from collections import Counter

import clans

ROOT = clans.ROOT
NS = clans.NS
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_taijutsu.json')
GEN = os.path.join(ROOT, 'tools', 'catalog', 'generated')
OUT = os.path.join(GEN, 'taijutsu_catalog.json')
EARLIER = ['clan_catalog.json', 'village_catalog.json', 'nature_catalog.json', 'fanon_genjutsu_catalog.json']

WHITE = 0xF5F5F5
GATE_GREEN = 0x66BB6A
GATES = ['opening', 'healing', 'life', 'pain', 'limit', 'view', 'wonder', 'death']


def ac(name):
    return f'animecore:cast_{name}'


def dash(dmg, distance=4.0, hits=1, lift=0.0, knockback=1.0, radius=1.4):
    d = {'type': 'animecore:dash_strike', 'mode': 'look', 'distance': distance, 'damage': round(dmg, 1),
         'hit_radius': radius, 'knockback': knockback}
    if hits > 1:
        d['hits'] = hits
    if lift:
        d['lift'] = lift
    return d


def burst(dmg, radius=3.5, knockback=1.2, forward=0.0, lift=0.0, color=WHITE):
    d = {'type': 'animecore:aoe_sphere', 'radius': radius, 'damage': round(dmg, 1), 'knockback': knockback,
         'forward_offset': forward, 'color': color}
    if lift:
        d['lift'] = lift
    return d


def pressure(dmg, count=1, size=0.9, speed=1.6, life=16, aoe=1.5, color=WHITE, fire=0, spread=10.0):
    d = {'type': 'animecore:projectile', 'speed': speed, 'damage': round(dmg if count == 1 else dmg * 2.0 / count, 1),
         'size': size, 'aoe_radius': aoe, 'count': count, 'spread_degrees': spread if count > 1 else 0.0,
         'lifetime_ticks': life, 'color': color, 'trail_particle': 'minecraft:cloud'}
    if fire:
        d['fire_ticks'] = fire
    return d


def buff(*effects):
    return {'type': 'animecore:self_buff', 'effects': list(effects)}


E = clans.effect

# Might Guy's Eight Gates techniques and the best-known taijutsu, hand-tuned
SPECIAL = {
    'Morning Peacock': lambda d: ([pressure(d * 1.3, count=6, size=0.6, speed=1.9, life=20, aoe=1.2, color=0xFF7043, fire=40,
                                            spread=6.0)], 'barrage', {'style': 'flame'}),
    'Daytime Tiger': lambda d: ([pressure(d * 1.6, size=2.2, speed=1.2, life=30, aoe=4.5, color=WHITE)], 'thrust',
                                {'style': 'wind', 'color': 0xE8F5E9}),
    'Evening Elephant': lambda d: ([{'type': 'animecore:beam_raycast', 'range': 14.0, 'damage': round(d * 0.6, 1), 'width': 2.2,
                                     'pierce': True, 'color': WHITE}, burst(d * 0.6, radius=5.0, knockback=2.4, forward=4.0, lift=1.2)],
                                   'barrage', {'style': 'wind'}),
    'Night Guy': lambda d: ([dash(d * 2.4, distance=22.0, knockback=3.0, radius=2.2),
                             burst(d * 0.8, radius=4.0, knockback=2.0, forward=0.0)], 'lunge', {'style': 'flame', 'color': 0xD50000}),
    'Front Lotus': lambda d: ([dash(d * 0.7, distance=3.0, lift=2.4), burst(d * 0.8, radius=2.5, knockback=0.6, forward=1.0)],
                              'uppercut', None),
    'Reverse Lotus': lambda d: ([dash(d * 0.45, distance=3.0, hits=6, lift=2.0)], 'uppercut', {'style': 'flame', 'color': GATE_GREEN}),
    'Leaf Hurricane': lambda d: ([burst(d, radius=3.5, knockback=1.6)], 'spin', None),
    'Dynamic Entry': lambda d: ([dash(d, distance=14.0, knockback=2.0)], 'kick', None),
    'Eight Trigrams Sixty-Four Palms': lambda d: ([dash(d * 0.3, distance=3.0, hits=8, knockback=1.4)], 'barrage',
                                                   {'style': 'light', 'color': 0x90CAF9}),
    'Eight Trigrams Palms Revolving Heaven': lambda d: ([burst(d, radius=4.5, knockback=2.6)], 'spin', {'style': 'light', 'color': 0x90CAF9}),
}

KW = [  # (archetype, regex) in priority order
    ('lotus', r'lotus'),
    ('gate', r'\bgates?\b(?! palm)|gate of '),
    ('spin', r'rotat|revolv|spin|whirl|hurricane|tornado|cyclone|twister|twist|turning|wheel|windmill'),
    ('rise', r'rising|\brise\b|upper|soaring|ascend|launch|aerial|shadow of the dancing leaf|\bsky\b'),
    ('drop', r'drop|heel|falling|\bfall\b|crash|meteor|slam|burial|shatter|quak|pulveri|crush|smash|avalanche|landing|stomp|'
             r'earth-tearing|eruption|ground'),
    ('barrage', r'barrage|combo|rapid|multiple|connected|flurry|hundred|thousand|wild|furious|rumble|dance|storm|profusion|'
                r'consecutive|sixty|thirty|sixteen|seventeen|eight twists|frenzy|rampage|great combo'),
    ('charge', r'bullet|tank|charge|tackle|ramm|entry|rush|dash|pounce|lunge|flash|speed|instant|swallow|boulder|piston|missile'),
    ('pressure', r'vacuum|\bair\b|pressure|gale|shock ?wave|roar|shout|wail|blast|cannon|beam|wind|breaching|empty palm|'
                 r'hot wind|sound|kāma'),
    ('slash', r'slash|blade|\bcut|sword|behead|sever|fang|claw|scythe|chop|shank|sabre|saber|cleav|drill|horn|spear|thrust|'
              r'pierc|bite|nail'),
    ('grapple', r'throw|grapple|lasso|embrace|\bhold\b|catch|lock|suplex|lariat|clothesline|won.t let you go|pinch|seize|'
                r'grab|binding|capture|wrap|coil'),
    ('kick', r'kick|\bfoot\b|\bfeet\b|\blegs?\b|knee'),
    ('palm', r'palm|fist|punch|strike|elbow|headbutt|jab|hammer|slap|blow|hit\b|attack|impact|smash'),
]
KW = [(k, re.compile(r, re.I)) for k, r in KW]
STANCE = re.compile(r'stance|style|kata|guard|block|counter|defen|parry|evasion|dodge|form\b|fist$|art of|technique$', re.I)


def archetype(title, info):
    lt = title.lower()
    if title in SPECIAL:
        return 'special'
    types = set(clans.split(info['jutsu class type']))
    for name, rx in KW:
        if rx.search(lt):
            return name
    if 'Offensive' not in types:
        return 'stance'
    c = clans.classes(info)
    if c & {'Kenjutsu', 'Bukijutsu'}:
        return 'slash'
    if c & {'Shurikenjutsu'}:
        return 'volley'
    return 'palm'


def gate_number(lt):
    if 'eight gates' in lt or 'death' in lt:
        return 8
    for i, g in enumerate(GATES):
        if g in lt:
            return i + 1
    return 4


def build(title, info):
    c = clans.classes(info)
    rank = clans.estimated_rank(info)
    dmg = float(clans.RANK_DAMAGE[rank])
    big = rank in 'ABS'
    lt = title.lower()
    kind = archetype(title, info)
    vis = {'style': 'wind', 'color': WHITE}
    if kind == 'special':
        actions, anim, v = SPECIAL[title](dmg)
        vis = v or vis
    elif kind == 'lotus':
        actions, anim = [dash(dmg * 0.5, distance=3.0, hits=5 if big else 3, lift=2.2)], 'uppercut'
        vis = {'style': 'flame', 'color': GATE_GREEN} if 'reverse' in lt or 'hidden' in lt else vis
    elif kind == 'gate':
        n = gate_number(lt)
        actions = [buff(E('strength', 400, min(4, n // 2)), E('speed', 400, min(3, (n + 1) // 3)), E('resistance', 400, 1 if n >= 4 else 0),
                        E('jump_boost', 400, 1), *([E('health_boost', 400, 2)] if n >= 7 else []), E('hunger', 200, n // 3))]
        anim, vis = 'focus', {'style': 'flame', 'color': 0xD50000 if n >= 8 else GATE_GREEN}
    elif kind == 'spin':
        actions, anim = [burst(dmg, radius=4.0 if big else 3.2, knockback=1.6)], 'spin'
    elif kind == 'rise':
        actions, anim = [dash(dmg, distance=3.5, lift=2.0, knockback=0.4)], 'uppercut'
    elif kind == 'drop':
        actions, anim = [dash(dmg * 0.4, distance=4.0, knockback=0.2), burst(dmg, radius=3.5 if big else 2.8, knockback=1.0,
                                                                             forward=2.5, lift=0.6, color=0x8D6E63)], 'slam'
        vis = {'style': 'earth'}
    elif kind == 'barrage':
        hits = 6 if big else 4
        actions, anim = [dash(dmg * 2.2 / hits, distance=4.0, hits=hits, knockback=1.4)], 'barrage'
    elif kind == 'charge':
        actions, anim = [dash(dmg, distance=14.0 if big else 11.0, knockback=1.8, radius=1.6)], 'lunge'
    elif kind == 'pressure':
        actions, anim = [pressure(dmg, size=1.3 if big else 0.9, aoe=2.5 if big else 1.5)], 'thrust'
    elif kind == 'slash':
        actions, anim = [{'type': 'animecore:beam_raycast', 'range': 6.0, 'damage': dmg, 'width': 1.2, 'pierce': True, 'color': WHITE},
                         dash(0.0, distance=2.5, knockback=0.0)], 'sweep'
    elif kind == 'grapple':
        actions = [{'type': 'animecore:area_effect', 'radius': 5.0, 'cone_degrees': 60.0, 'damage': dmg, 'pull': 1.2, 'color': WHITE,
                    'effects': [E('slowness', 60, 3)]}, burst(0.0, radius=2.0, knockback=0.0, forward=1.0, lift=0.9)]
        anim = 'punch'
    elif kind == 'kick':
        actions, anim = [dash(dmg, distance=4.5, knockback=1.6)], 'kick'
    elif kind == 'volley':
        actions, anim = [pressure(dmg, count=5, size=0.3, speed=2.0, life=30, aoe=0.0, spread=8.0)], 'thrust'
        vis = None
    elif kind == 'stance':
        defensive = STANCE.search(lt) or 'Defensive' in clans.split(info['jutsu class type'])
        actions = [buff(E('resistance', 300, 1), E('strength', 300, 0), E('slowness', 40, 0))] if defensive else \
            [buff(E('speed', 300, 1), E('jump_boost', 300, 1))]
        anim = 'stance'
    else:  # palm / punch
        actions, anim = [dash(dmg, distance=3.5, knockback=1.4)], 'punch'
        if big:
            actions.append(burst(dmg * 0.5, radius=2.5, knockback=1.2, forward=1.5))

    ninjutsu = bool(c & {'Ninjutsu', 'Senjutsu', 'Chakra Flow', 'Kekkei Genkai', 'Shinjutsu'})
    pool = clans.CHAKRA if ninjutsu else clans.STAMINA
    d = {'system': clans.SYSTEM, 'name': None, 'cast_type': 'instant',
         'cost': {'pool': pool, 'amount': round(max(8.0, dmg * 2.5), 1)},
         'cooldown_ticks': int(40 + dmg * 8), 'required_tier': clans.RANK_TIER[rank], 'actions': actions,
         'cast_animation': ac(anim), 'token': clans.ns(clans.token_for(c, []) or 'taijutsu')}
    if d['token'] not in (clans.ns('taijutsu'), clans.ns('genjutsu'), clans.ns('dojutsu')):
        d['token'] = clans.ns('taijutsu')
    if vis:
        d['visuals'] = vis
    return d, kind


def display_name(title, titles):
    m = re.match(r'^(.*?) \((?:game|jutsu|[A-Z][\w ]*)\)$', title)
    if m and m.group(1) not in titles:
        return m.group(1)
    return title


def main():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    existing = {'fireball_jutsu', 'substitution_jutsu'}
    for f in EARLIER:
        existing |= set(json.load(open(os.path.join(GEN, f), encoding='utf-8'))['abilities'])
    jutsu = crawl['jutsu']
    out = {'generated_from': os.path.relpath(CRAWL, ROOT), 'crawled': crawl['crawled'], 'abilities': {}, 'lang': {}}
    kinds, already = Counter(), []
    for title in sorted(jutsu):
        aid = clans.ALIASES.get(title) or clans.slug(title)
        if aid in existing or aid in out['abilities']:
            already.append(title)
            continue
        d, kind = build(title, jutsu[title])
        name = display_name(title, jutsu)
        d['name'] = clans.text(f'ability.{NS}.{aid}', name)
        out['abilities'][aid] = d
        out['lang'][f'ability.{NS}.{aid}'] = name
        kinds[kind] += 1
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    print(f"taijutsu: {len(jutsu)} on the wiki, {len(out['abilities'])} new abilities, {len(already)} already existed; {dict(kinds)}")
    write_doc(crawl, out, already, kinds)


def write_doc(crawl, cat, already, kinds):
    L = ['# Narutopedia Taijutsu coverage', '',
         f"Source: {crawl['source']} - crawled {crawl['crawled']}. Method: {crawl['method']}.", '',
         '## Category tree', '']
    for c, v in crawl['category_tree'].items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories")
    L += ['', '`Category:Taijutsu` does not exist on Narutopedia (the API reports the category page as missing and it has no '
          'members). Narutopedia groups taijutsu only through the Semantic MediaWiki property **Jutsu classification = '
          'Taijutsu** (the "Classification" drill-down filter), so that grouping is the category for this crawl, per the '
          'project rule. Every page in it is covered below.', '',
          f"**{len(crawl['jutsu'])} taijutsu pages.** {len(cat['abilities'])} are new abilities on the Taijutsu token; "
          f"{len(already)} already existed (clan, village or nature-release jutsu such as Leaf Hurricane, Primary Lotus or "
          "10 Hit Combo keep their existing unlock route). None were excluded.", '',
          '### Pages intentionally not turned into taijutsu', '',
          f"- The drill-down value **Fighting Style** also contains {len(crawl.get('fighting_style_not_taijutsu', []))} pages "
          'that are not classified Taijutsu (ninjutsu styles such as Explosive Clay, Puppet Technique, Six Paths of Pain, '
          'Iaidō). They are fighting styles of other schools, not taijutsu, so they are out of scope here: '
          + ', '.join(crawl.get('fighting_style_not_taijutsu', [])) + '.',
          '- The article **Taijutsu** itself is a topic page, not a technique.', '',
          '## How taijutsu play', '',
          '- Every taijutsu sits on the **Taijutsu** technique item. Hold it while defeating enemies to raise its mastery; each '
          'mastery level unlocks the next taijutsu in rank order (rank still gates them). Pure taijutsu costs **stamina**; '
          'taijutsu that are also ninjutsu / senjutsu / chakra-flow techniques cost chakra.',
          '- Each technique is matched to a move archetype from its name, with its own cast animation:', '',
          '| archetype | matches | plays as | count |', '|---|---|---|---:|']
    rows = {
        'special': ('Morning Peacock, Daytime Tiger, Evening Elephant, Night Guy, Front / Reverse Lotus, Leaf Hurricane, '
                    'Dynamic Entry, Sixty-Four Palms, Revolving Heaven', 'hand-tuned'),
        'lotus': ('lotus', 'launch the target and follow with a multi-hit combo'),
        'gate': ('gates', 'Eight Gates power-up scaling with the gate number (strength, speed, resistance; hunger cost)'),
        'spin': ('rotation, whirlwind, hurricane, spin...', 'spinning burst knocking back everything around you'),
        'rise': ('rising, soaring, upper...', 'short dash that launches the target upward'),
        'drop': ('drop, heel, crash, slam, shatter...', 'dash + ground-shaking impact in front'),
        'barrage': ('barrage, combo, multiple, dance, wild...', 'dash with 4-6 rapid follow-up blows'),
        'charge': ('bullet, tank, rush, flash, swallow...', 'long charge through enemies'),
        'pressure': ('vacuum, air, gale, roar, wind...', 'air-pressure wave fired forward'),
        'slash': ('slash, blade, fang, claw, drill...', 'piercing close-range cut'),
        'grapple': ('throw, lasso, hold, embrace...', 'grab: pull in, slow and toss up'),
        'kick': ('kick, foot, leg, knee', 'kicking dash with heavy knockback'),
        'palm': ('punch, fist, palm, strike...', 'striking dash (plus a shockwave at rank A/S)'),
        'volley': ('shurikenjutsu', '5-shot volley'),
        'stance': ('defensive / supplementary', 'fighting stance buff (defence or mobility)'),
    }
    for k, (m, p) in rows.items():
        L.append(f'| {k} | {m} | {p} | {kinds.get(k, 0)} |')
    L += ['', '## All taijutsu', '', '| technique | rank (estimated) | class type | in game as |', '|---|---|---|---|']
    have = set(cat['abilities'])
    for t in sorted(crawl['jutsu']):
        info = crawl['jutsu'][t]
        aid = clans.ALIASES.get(t) or clans.slug(t)
        L.append(f"| [{t}](https://naruto.fandom.com/wiki/{t.replace(' ', '_')}) | {clans.estimated_rank(info)} | "
                 f"{info['jutsu class type'] or '-'} | `{NS}:{aid}`{'' if aid in have else ' (existing)'} |")
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_taijutsu.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutopedia_taijutsu.md')


if __name__ == '__main__':
    main()
