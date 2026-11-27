"""JoJoRPG Part 3 (Stardust Crusaders): every Stand of Category:Part 3 Stands as a real, manifesting entity.

  python3 tools/catalog/jojo_stands.py --extract <raw Part 3 crawl.json>
      condenses jojowiki.com Category:Part 3 (recursively) + Category:Stands into docs/wiki/jojowiki_part_3.json: the
      category tree, every Part 3 Stand (user, Stand types, stats, ability headings, {{Tech}} techniques, lead),
      Part 3 characters, story arcs with their chapters, chapter titles / summaries / casts.
  python3 tools/catalog/jojo_stands.py
      writes tools/catalog/generated/jojo_stands_catalog.json (animecore:manifestation entries + their abilities + lang),
      jojorpg/src/main/resources/assets/jojorpg/jojo_stand_items.json (a Stand Disc per Stand) and the disc / arrow sprites.

Each Stand keeps its wiki stats (Power, Speed, Range, Stamina, Precision, Potential); Power sets its punch and technique
damage, Range how far it reaches. Its kit is built from the abilities its page names (headings and {{Tech}} entries):
Star Platinum's Ora Ora rush, Star Finger and Star Platinum: The World; The World's Muda Muda rush, time stop, knives and
road roller; Magician's Red's Crossfire Hurricane, Red Bind and Life Detector; Hierophant Green's Emerald Splash...
Stand models: humanoid Stands use the tinted character layers, the others a low-poly model from stand_models.py.
"""
import json
import os
import re
import sys
from collections import OrderedDict

sys.path.insert(0, os.path.dirname(__file__))
from jojo_content import clean, section, techs  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
NS = 'jojorpg'
DOC_JSON = os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_part_3.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'jojo_stands_catalog.json')
ITEMS_JSON = os.path.join(ROOT, 'jojorpg', 'src', 'main', 'resources', 'assets', 'jojorpg', 'jojo_stand_items.json')
ITEM_DIR = os.path.join(ROOT, 'jojorpg', 'src', 'main', 'resources', 'assets', 'jojorpg', 'textures', 'item')
STAND_POOL = f'{NS}:stand'
NOT_STANDS = {'List of Unnamed Stands': 'an index page listing minor unnamed Stands (each unnamed Stand is a one-panel mention)'}


def ns(p):
    return f'{NS}:{p}'


def slug(text):
    t = text.lower()
    for a, b in (('ō', 'o'), ('ū', 'u'), ('é', 'e'), ('è', 'e'), ('&', 'and'), ("'", '')):
        t = t.replace(a, b)
    return re.sub(r'[^a-z0-9]+', '_', t).strip('_')


def text(key, fallback):
    return {'translate': key, 'fallback': fallback}


# ======================================================================================================================
# extraction
# ======================================================================================================================

def lead(t):
    body = re.sub(r'^\{\{.*?\n\}\}', '', t, flags=re.S | re.M)
    return clean(re.split(r'^==', body, flags=re.M)[0])[:600]


def extract(raw_path):
    raw = json.load(open(raw_path, encoding='utf-8'))
    tree, texts = raw['category_tree'], raw['texts']
    cats_of = {}
    for c, v in tree.items():
        for p in v['pages']:
            cats_of.setdefault(p, []).append(c.replace('Category:', ''))
    stands = OrderedDict()
    for p in tree['Category:Part 3 Stands']['pages']:
        if re.search(r'/[a-z]{2}$', p) or p in NOT_STANDS: continue
        t = texts.get(p) or ''
        info = t.split('{{Quote')[0]
        user = re.search(r'\|user\s*=\s*(.*)', info)
        stats = re.search(r'\{\{Stats\|([^}]*)\}\}', info)
        st = dict(re.findall(r'(power|speed|range|stamina|precision|potential)=([A-E∞?]|None|Complete)', stats.group(1))) if stats else {}
        abil = section(t, 'Abilities')
        links = [l for l in re.findall(r'\[\[([^\]|#]+)', user.group(1)) if not l.startswith(('Minor', 'Unnamed', 'File'))] if user else []
        stands[p] = {'user': links[0] if links else (clean(user.group(1)).split('(')[0].strip() if user else ''),
                     'types': re.findall(r'\{\{ST\|([^}|]*)', info.split('|stats')[0]),
                     'stats': st, 'categories': sorted(set(cats_of.get(p, []))),
                     'abilities': [h.strip() for h in re.findall(r'^===([^=]+)===', abil, re.M)],
                     'techniques': [x for x in techs(t) if (x['debut'] or 0) <= 265],
                     'summary': clean(re.split(r'^===', abil, flags=re.M)[0])[:700], 'lead': lead(t)}
    chars = OrderedDict()
    for c in ('Category:Part 3 Characters', 'Category:Part 3 Allies', 'Category:Joestar Group', 'Category:Part 3 Antagonists',
              'Category:Egypt 9 Glory Gods', 'Category:Speedwagon Foundation'):
        for p in tree.get(c, {}).get('pages', []):
            if re.search(r'/[a-z]{2}$', p) or p.startswith(('Minor ', 'Unnamed ')) or p in stands: continue
            t = texts.get(p) or ''
            stand = re.search(r'\|stand\s*=\s*(.*)', t[:6000])
            chars[p] = {'categories': sorted(set(cats_of.get(p, []))), 'stand': clean(stand.group(1)) if stand else '', 'lead': lead(t)[:400]}
    arcs = []
    for a in tree['Category:Part 3 Story Arcs']['pages']:
        t = texts.get(a) or ''
        chs = sorted({int(x) for x in re.findall(r'\{\{Ch2\|Chapter (\d+)\}\}', section(t, 'Chapters'))})
        arcs.append({'title': re.sub(r'\s*\(Story Arc\)$', '', a), 'part': 'Stardust Crusaders', 'chapters': chs, 'summary': clean(section(t, 'Summary'))[:800]})
    arcs.sort(key=lambda a: a['chapters'][0] if a['chapters'] else 999)
    chapters = {}
    for p in tree['Category:Part 3 Chapters']['pages']:
        m = re.match(r'Chapter (\d+)$', p)
        if not m: continue
        t = texts.get(p) or ''
        title = re.search(r"\{\{Nihongo\|'''(.*?)'''", t)
        cast = [{'name': cm.group(1).strip()} for cm in re.finditer(r'\{\{Char\|Name=([^|}]+)', section(t, 'Appearances'))]
        chapters[int(m.group(1))] = {'title': title.group(1) if title else p, 'summary': clean(section(t, 'Summary'))[:2200], 'cast': cast}
    out = {'source': raw['source'], 'crawled': raw['crawled'], 'method': raw['method'], 'category_tree': tree, 'stands': stands,
           'not_stands': NOT_STANDS, 'characters': chars, 'arcs': arcs, 'chapters': {str(k): v for k, v in sorted(chapters.items())}}
    with open(DOC_JSON, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False)
    print(f'extracted {len(stands)} Stands, {len(chars)} characters, {len(arcs)} arcs, {len(chapters)} chapters')


# ======================================================================================================================
# action helpers
# ======================================================================================================================

def effect(name, duration=100, amp=0):
    return {'effect': f'minecraft:{name}', 'duration': duration, 'amplifier': amp}


def rush(hits, dmg, shout=None, reach=2.2, radius=1.6, interval=2, knockback=1.8):
    d = {'type': 'animecore:rush', 'hits': hits, 'damage': round(dmg, 2), 'reach': reach, 'radius': radius, 'interval_ticks': interval,
         'knockback': knockback}
    if shout: d['shout'] = {'text': shout}
    return d


def strike(dmg, rng=16.0, speed=1.5, teleport=False, effects=(), fire=0, radius=0.0):
    d = {'type': 'animecore:remote_strike', 'range': rng, 'damage': round(dmg, 1), 'speed': speed, 'teleport': teleport,
         'effects': list(effects), 'fire_ticks': fire}
    if radius: d['radius'] = radius
    return d


def shot(dmg, count=1, speed=1.8, size=0.4, aoe=0.0, life=40, spread=8.0, color=0xFFFFFF, fire=0, trail=None, homing=0.0):
    d = {'type': 'animecore:projectile', 'speed': speed, 'damage': round(dmg if count == 1 else dmg * 2.2 / count, 1), 'size': size,
         'aoe_radius': aoe, 'count': count, 'spread_degrees': spread if count > 1 else 0.0, 'lifetime_ticks': life, 'color': color,
         'fire_ticks': fire, 'size_scales_with_charge': False}
    if trail: d['trail_particle'] = trail
    if homing: d['homing'] = homing
    return d


def beam(dmg, rng=12.0, width=0.5, pierce=True, color=0xFFFFFF, fire=0):
    return {'type': 'animecore:beam_raycast', 'range': rng, 'damage': round(dmg, 1), 'width': width, 'pierce': pierce, 'color': color, 'fire_ticks': fire}


def area(dmg, radius=7.0, cone=70.0, effects=(), pull=0.0, color=0xFFFFFF):
    return {'type': 'animecore:area_effect', 'radius': radius, 'cone_degrees': cone, 'damage': round(dmg, 1), 'pull': pull, 'color': color,
            'effects': list(effects)}


def burst(dmg, radius=3.5, knockback=1.2, forward=0.0, fire=0, color=0xFFFFFF, lift=0.0):
    d = {'type': 'animecore:aoe_sphere', 'radius': radius, 'damage': round(dmg, 1), 'knockback': knockback, 'forward_offset': forward,
         'fire_ticks': fire, 'color': color}
    if lift: d['lift'] = lift
    return d


def dash(dmg, distance=5.0, hits=1, knockback=1.2, radius=1.4):
    d = {'type': 'animecore:dash_strike', 'mode': 'look', 'distance': distance, 'damage': round(dmg, 1), 'hit_radius': radius, 'knockback': knockback}
    if hits > 1: d['hits'] = hits
    return d


def buff(*effects, heal=0.0):
    d = {'type': 'animecore:self_buff', 'effects': list(effects)}
    if heal: d['heal'] = heal
    return d


def charm(radius=10.0, cone=360.0, ticks=300, n=4, dmg=0.0):
    return {'type': 'animecore:charm', 'radius': radius, 'cone_degrees': cone, 'duration_ticks': ticks, 'max_targets': n, 'damage': dmg}


def shift(attr, amount, ticks=300, radius=8.0, cone=360.0, op='add_multiplied_total', self_=False):
    return {'type': 'animecore:attribute_shift', 'attribute': f'minecraft:generic.{attr}', 'amount': amount, 'operation': op,
            'duration_ticks': ticks, 'radius': radius, 'cone_degrees': cone, 'self': self_}


def summon(entity, count, ticks):
    return {'type': 'animecore:summon_entity', 'entity': f'minecraft:{entity}', 'count': count, 'radius': 2.5, 'allied_ticks': ticks}


def wall(width=5, height=3, distance=2, ticks=160):
    return {'type': 'animecore:raise_wall', 'kind': 'barrier', 'width': width, 'height': height, 'distance': distance, 'duration_ticks': ticks}


def time_stop(ticks, shout, radius=48.0):
    return {'type': 'animecore:time_stop', 'duration_ticks': ticks, 'radius': radius, 'shout': {'text': shout}}


def void(ticks=80, speed=0.45, radius=1.6, dmg=14.0):
    return {'type': 'animecore:void', 'duration_ticks': ticks, 'speed': speed, 'radius': radius, 'damage': dmg, 'erase_blocks': True}


def link(rng=24.0, ticks=600):
    return {'type': 'animecore:damage_link', 'range': rng, 'duration_ticks': ticks, 'ratio': 1.0}


def trait(t, **kw):
    return dict({'type': f'animecore:{t}'}, **kw)


GRADE = {'A': 1.0, 'B': 0.82, 'C': 0.65, 'D': 0.48, 'E': 0.32}
RANGE_M = {'A': 60.0, 'B': 20.0, 'C': 10.0, 'D': 4.0, 'E': 2.0}

# ======================================================================================================================
# the Stands (kits from the abilities each page names)
# ======================================================================================================================
# S(page, kind, look, abilities, ...): abilities are (name, rank, cost, cooldown, animation, [actions], description)
# with power-scaled damage: P = the Stand's Power grade factor * 18 (a rank-12 technique's damage).

STANDS = OrderedDict()


def S(page, kind, look, abilities, color, scale=1.0, alpha=0.75, punch=None, traits=(), weight=5, model=None):
    STANDS[page] = dict(kind=kind, look=look, abilities=abilities, color=color, scale=scale, alpha=alpha, punch=punch,
                        traits=list(traits), weight=weight, model=model)


def P(grade):
    return GRADE.get(grade, 0.5) * 18.0


def build_stands(data):
    st = {k: v['stats'] for k, v in data['stands'].items()}

    def pw(page): return P(st.get(page, {}).get('power', 'C'))

    p = pw('Star Platinum')
    S('Star Platinum', 'close_range',
      dict(skin=0x7E57C2, eyes=0x00E5FF, hair=0x1A1A2E, hair_style='long', outfit='bodysuit', top=0x5E35B1, accent=0xFFC107, pants=0x4527A0, headband=0xFFC107),
      [('Ora Ora', 1, 30, 120, 'barrage', [rush(16, p * 2.4 / 16, 'ORA ORA ORA ORA ORA!')], 'An extremely fast, precise barrage of punches.'),
       ('Star Finger', 1, 16, 70, 'thrust', [beam(p * 0.9, 6, 0.35, True, 0xB39DDB)], 'Extends two fingers to stab from just outside its reach.'),
       ('Superhuman Lungs', 3, 20, 200, 'gaze', [area(0, 9, 70, [effect('slowness', 60, 3)], pull=2.5, color=0xB39DDB)],
        'Inhales with superhuman lung capacity, dragging enemies (and fog Stands) in.'),
       ('Bullet Catch', 2, 12, 200, 'stance', [buff(effect('resistance', 120, 1))], 'Catches bullets and knives between two fingers.'),
       ('Star Platinum: The World', 14, 70, 1200, 'focus', [time_stop(50, 'Star Platinum: The World!')],
        'Stops time for about two seconds - and moves inside stopped time.')],
      0x7E57C2, scale=1.05, punch=p * 0.5, traits=[trait('evasion', chance=0.35, projectiles_only=True), trait('time_immune')], weight=2)

    p = pw('The World')
    S('The World', 'close_range',
      dict(skin=0xF5CF3B, eyes=0x76FF03, hair=0xF5CF3B, hair_style='bald', outfit='armor', top=0xF5CF3B, accent=0x2E7D32, pants=0xE6B422, headband=0x2E7D32, mark='mask'),
      [('Muda Muda', 1, 30, 120, 'barrage', [rush(16, p * 2.4 / 16, 'MUDA MUDA MUDA MUDA MUDA!')], 'A flurry of punches stronger and faster than Star Platinum\'s.'),
       ('Time Stop', 6, 60, 900, 'focus', [time_stop(100, 'ZA WARUDO! Toki wo tomare!')], 'Stops time for five seconds; only DIO moves.'),
       ('Knife Throw', 3, 22, 100, 'thrust', [shot(p * 1.3, 8, 2.2, 0.25, 0, 50, 6, 0xECEFF1)], 'A fan of silver knives - they hang in stopped time.'),
       ('Road Roller', 10, 55, 900, 'slam', [burst(p * 2.2, 5.0, 2.5, 4.0, 0, 0xFBC02D, 0.8)], 'ROAD ROLLER DA! Drops a steamroller on the enemy.'),
       ('Piercing Chop', 2, 18, 80, 'thrust', [dash(p * 1.1, 4.5, knockback=0.8)], 'Drives a hand straight through the opponent.')],
      0xF5CF3B, scale=1.1, punch=p * 0.5, traits=[trait('time_immune')], weight=1)

    p = pw("Magician's Red")
    S("Magician's Red", 'close_range', dict(), [
        ('Crossfire Hurricane', 1, 24, 80, 'thrust', [shot(p * 1.2, 1, 1.6, 0.9, 3.0, 50, 0, 0xFF5722, 100, 'minecraft:flame')],
         'An ankh-shaped blast of flame.'),
        ('Crossfire Hurricane Special', 4, 36, 160, 'sweep', [shot(p * 1.6, 6, 1.4, 0.6, 2.0, 50, 14, 0xFF7043, 100, 'minecraft:flame')],
         'Splits the ankh into a spread of flames that surround the target.'),
        ('Red Bind', 2, 22, 140, 'sweep', [area(p * 0.4, 7, 60, [effect('slowness', 120, 5), effect('weakness', 120, 1)], pull=1.2, color=0xFF3D00),
                                          burst(0, 2.0, 0, 3.0, 80, 0xFF3D00)], 'Ropes of fire bind the target.'),
        ('Life Detector', 3, 14, 300, 'focus', [area(0, 40, 360, [effect('glowing', 300)], color=0xFF8A65)],
         'A flame compass reveals every living thing nearby.'),
        ('Pyrokinesis', 2, 26, 120, 'burst', [burst(p * 0.9, 4.0, 1.4, 0, 120, 0xFF5722)], 'Engulfs everything around in heat that can melt steel.')],
      0xFF5722, punch=p * 0.4, traits=[trait('passive_effect', effect='minecraft:fire_resistance', amplifier=0)], model='magicians_red')

    p = pw('Hierophant Green')
    S('Hierophant Green', 'long_range',
      dict(skin=0x2E7D32, eyes=0xFFEB3B, hair=0x1B5E20, hair_style='bald', outfit='bodysuit', top=0x43A047, accent=0xC0CA33, pants=0x388E3C, mark='mask'),
      [('Emerald Splash', 1, 24, 80, 'thrust', [shot(p * 1.3, 8, 2.0, 0.3, 0, 50, 7, 0x76FF03, trail='minecraft:happy_villager')],
        'Fires a torrent of energy shaped like emeralds.'),
       ('Coiled Body', 1, 18, 100, 'point', [strike(p * 0.8, 24, 2.0, effects=[effect('slowness', 100, 4)])],
        'Unravels into tentacles that ensnare a distant target.'),
       ('Anatomy Control', 5, 30, 400, 'gaze', [charm(10, 40, 200, 1)], 'Slips inside a body and controls it like a puppet.'),
       ('20-Meter Radius Emerald Splash', 10, 70, 1000, 'focus', [area(p * 1.2, 20, 360, [effect('glowing', 200)], color=0x76FF03)],
        'Strings stretched in a 20-metre radius fire Emerald Splash at whatever touches them.')],
      0x43A047, punch=p * 0.3, weight=4)

    p = pw('Silver Chariot')
    S('Silver Chariot', 'close_range',
      dict(skin=0xCFD8DC, eyes=0x263238, hair=0xB0BEC5, hair_style='spiky', outfit='armor', top=0xB0BEC5, accent=0x78909C, pants=0x90A4AE),
      [('Rapier Barrage', 1, 26, 100, 'barrage', [rush(22, p * 2.5 / 22, 'Hora hora hora hora!', 2.6, 1.5, 1)], 'Barrages of thrusts before the enemy can react.'),
       ('Armor Off', 3, 30, 600, 'stance', [buff(effect('speed', 300, 3), effect('jump_boost', 300, 1)), shift('armor', -0.5, 300, self_=True)],
        'Sheds its armour to reach full speed, leaving afterimages.'),
       ('Shooting Star Rapier', 2, 22, 120, 'thrust', [shot(p * 1.4, 1, 3.0, 0.25, 0, 30, 0, 0xECEFF1)], 'Launches the rapier\'s blade like a bullet.'),
       ('Flame Skewer', 2, 18, 90, 'thrust', [beam(p * 1.0, 5, 0.6, True, 0xECEFF1)], 'Skewers coins and flames in a single thrust.'),
       ('"Silver Chariot" Plus "Anubis" Two-Sword Technique', 9, 50, 400, 'barrage', [rush(30, p * 3.2 / 30, None, 2.8, 1.8, 1)],
        'Dual-wielding Anubis, it overwhelms even Star Platinum.')],
      0xB0BEC5, punch=p * 0.45, traits=[trait('evasion', chance=0.25, projectiles_only=True)], weight=4)

    p = pw('Hermit Purple')
    S('Hermit Purple', 'close_range', dict(), [
        ('Vine Bind', 1, 16, 100, 'point', [strike(p * 0.8, 12, 1.6, effects=[effect('slowness', 100, 5)])], 'Thorny vines lash out and entangle.'),
        ('Divination', 1, 20, 400, 'focus', [area(0, 48, 360, [effect('glowing', 400)], color=0x9C27B0)],
         'Spirit photography and thoughtography reveal where enemies are.'),
        ('Hermit Purple Overdrive', 3, 24, 120, 'point', [{**strike(p * 1.3, 12, 2.0), 'fire_ticks': 40}],
         'Channels the Ripple through the vines - deadly to vampires.')],
      0x9C27B0, punch=p * 0.3, model='hermit_purple', weight=4)

    p = pw('The Fool')
    S('The Fool', 'bound', dict(), [
        ('Sand Blast', 1, 18, 80, 'thrust', [shot(p * 1.0, 5, 1.6, 0.4, 0, 30, 12, 0xD7B98E, trail='minecraft:white_ash'),
                                            area(0, 6, 60, [effect('blindness', 60)], color=0xD7B98E)], 'A blast of sand that blinds.'),
        ('Sand Glider', 1, 16, 300, 'stance', [buff(effect('slow_falling', 300), effect('jump_boost', 300, 2), effect('speed', 300, 1))],
         'Spreads into wings of sand to glide.'),
        ('Sand Decoy', 2, 22, 400, 'stance', [buff(effect('invisibility', 140), effect('speed', 140, 1))], 'Moulds a sand double while Iggy slips away.'),
        ('Sand Dome', 3, 26, 300, 'focus', [wall(6, 4, 1, 200)], 'A wall of hardened sand.')],
      0xD7B98E, punch=p * 0.4, model='the_fool')

    p = pw('Tower of Gray')
    S('Tower of Gray', 'long_range', dict(), [
        ('Tower Needle', 1, 16, 50, 'point', [strike(p * 1.8, 40, 4.0, effects=[effect('wither', 80, 1)])],
         'Pierces with its needle tongue at blinding speed.'),
        ('High Speed Flight', 1, 12, 300, 'stance', [buff(effect('speed', 200, 3), effect('jump_boost', 200, 1))], 'Too fast to be hit.')],
      0x9E9E9E, scale=0.6, punch=p * 0.6, traits=[trait('evasion', chance=0.3, projectiles_only=False)], model='tower_of_gray')

    p = pw('Dark Blue Moon')
    S('Dark Blue Moon', 'close_range',
      dict(skin=0x1565C0, eyes=0xFFEB3B, hair=0x0D47A1, hair_style='spiky', outfit='bodysuit', top=0x0D47A1, accent=0x80DEEA, pants=0x1565C0),
      [('Razor Scales', 1, 18, 80, 'sweep', [shot(p * 1.0, 6, 2.0, 0.25, 0, 30, 10, 0x80DEEA)], 'Hurls sharp scales.'),
       ('Whirlpool', 2, 24, 200, 'focus', [area(p * 0.4, 8, 360, [effect('slowness', 100, 3)], pull=2.0, color=0x29B6F6)], 'Spins up a whirlpool.'),
       ('Barnacles', 2, 22, 240, 'gaze', [area(0, 7, 60, [effect('hunger', 200, 2), effect('weakness', 200, 1), effect('slowness', 200, 1)], color=0x80DEEA)],
        'Barnacles that drain the victim\'s strength.'),
       ('Underwater Rush', 1, 22, 120, 'barrage', [rush(10, p * 2.0 / 10)], 'Lightning-fast strikes, strongest in the water.')],
      0x1565C0, punch=p * 0.45, traits=[trait('passive_effect', effect='minecraft:water_breathing', amplifier=0),
                                         trait('passive_effect', effect='minecraft:dolphins_grace', amplifier=0)])

    p = pw('Strength')
    S('Strength', 'bound', dict(), [
        ('Ship Manipulation', 1, 26, 200, 'focus', [area(p * 0.4, 10, 360, [effect('slowness', 120, 4)], pull=1.5, color=0x8D6E63)],
         'The ship itself - pipes, chains and rigging close in on the crew.'),
        ('Iron Crush', 2, 30, 160, 'slam', [burst(p * 1.6, 3.5, 2.0, 1.5)], 'Crushes the prey with the ship\'s hull.'),
        ('Hull Fortress', 2, 22, 300, 'focus', [wall(7, 4, 1, 200), buff(effect('resistance', 200, 1))], 'Walls of steel.')],
      0x8D6E63, punch=p * 0.4, model='strength')

    p = pw('Ebony Devil')
    S('Ebony Devil', 'long_range',
      dict(skin=0x8D6E63, eyes=0xE53935, hair=0x3E2723, hair_style='wild', outfit='robe', top=0x5D4037, accent=0x3E2723, pants=0x4E342E, mark='fangs'),
      [('Doll Possession', 1, 20, 80, 'point', [strike(p * 1.4, 30, 1.8, effects=[effect('slowness', 60, 1)])],
        'Possesses a doll and stalks its prey with a razor.'),
       ('Anger Empowerment', 1, 16, 400, 'focus', [buff(effect('strength', 300, 2), effect('resistance', 300, 0))],
        'The more Devo hates, the stronger it becomes.'),
       ('Razor Frenzy', 2, 20, 120, 'barrage', [rush(8, p * 1.6 / 8)], 'A frenzy of razor slashes.')],
      0x6D4C41, scale=0.45, alpha=0.9, punch=p * 0.4)

    p = pw('Yellow Temperance')
    S('Yellow Temperance', 'bound', dict(), [
        ('Assimilation', 1, 22, 100, 'lunge', [dash(p * 1.2, 4), buff(heal=p * 0.6)], 'Engulfs and digests living flesh.'),
        ('Physical Imperviousness', 1, 24, 400, 'stance', [buff(effect('resistance', 300, 2), effect('absorption', 300, 3), effect('fire_resistance', 300))],
         'Absorbs every blow - an armour of flesh-eating jelly.'),
        ('Appearance Replication', 2, 18, 600, 'stance', [buff(effect('invisibility', 300))], 'Takes on the appearance of anyone.'),
        ('Acid Engulf', 2, 24, 160, 'burst', [area(p * 0.5, 4, 360, [effect('poison', 120, 1)], color=0xFFEB3B)], 'Splashes digestive acid.')],
      0xFFEB3B, punch=p * 0.4, traits=[trait('regeneration', amount=1.0, below_health_ratio=1.0)], model='yellow_temperance')

    p = pw('Hanged Man')
    S('Hanged Man', 'long_range',
      dict(skin=0xBCAAA4, eyes=0xE53935, hair=0x8D6E63, hair_style='bald', outfit='robe', top=0xD7CCC8, accent=0x8D6E63, pants=0xBCAAA4, mark='mask'),
      [('Light Manifestation', 1, 24, 100, 'point', [strike(p * 1.8, 32, 1.0, teleport=True)],
        'Travels between reflections at the speed of light and strikes from the mirror.'),
       ('Wrist Knives', 1, 20, 80, 'barrage', [rush(6, p * 1.4 / 6)], 'Slashes with the blade hidden in its wrist.'),
       ('Reflection Ambush', 4, 28, 240, 'point', [strike(p * 1.4, 32, 1.0, teleport=True, effects=[effect('blindness', 60)])],
        'Leaps out of the victim\'s own eye.')],
      0xBCAAA4, alpha=0.6, punch=p * 0.4)

    p = pw('Emperor')
    S('Emperor', 'bound', dict(), [
        ('Homing Bullets', 1, 18, 60, 'point', [shot(p * 1.4, 3, 2.6, 0.2, 0, 60, 6, 0xFFD54F, homing=0.25)],
         'Bullets that change course in mid-air.'),
        ('Point-Blank Summon', 1, 16, 80, 'point', [beam(p * 1.2, 8, 0.4, False, 0xFFD54F)], 'Summons the gun and fires point-blank.'),
        ('Rapid Fire', 3, 26, 160, 'point', [shot(p * 1.8, 7, 2.4, 0.2, 0, 60, 10, 0xFFD54F, homing=0.15)], 'Empties the chamber.')],
      0xB0BEC5, punch=p * 0.3, model='emperor')

    p = pw('Empress')
    S('Empress', 'bound',
      dict(skin=0xD7A88A, eyes=0x212121, hair=0x5D4037, hair_style='bald', outfit='bodysuit', top=0xD7A88A, accent=0x8D6E63, pants=0xBC8F74, mark='fangs'),
      [('Parasitic Growth', 1, 22, 200, 'point', [area(0, 8, 40, [effect('wither', 240, 0), effect('poison', 240, 0), effect('hunger', 240, 1)], color=0xBC8F74)],
        'Grows out of the victim\'s body, feeding on them.'),
       ('Crushing Grip', 1, 18, 100, 'point', [strike(p * 1.3, 10, 1.4, effects=[effect('slowness', 80, 2)])], 'Grabs and crushes.'),
       ('Vein Bite', 2, 20, 160, 'lunge', [dash(p * 1.0, 4), buff(heal=p * 0.4)], 'Bites and drinks from the host.')],
      0xD7A88A, scale=0.4, alpha=0.95, punch=p * 0.3)

    p = pw('Wheel of Fortune')
    S('Wheel of Fortune', 'bound', dict(), [
        ('Car Ram', 1, 24, 100, 'lunge', [dash(p * 1.6, 10, knockback=2.0, radius=1.8)], 'Rams the enemy with the car.'),
        ('Gasoline Spray', 2, 22, 160, 'sweep', [area(p * 0.3, 8, 50, [effect('slowness', 60, 1)], color=0xFDD835), burst(p * 0.8, 3, 1, 4, 120, 0xFF6D00)],
         'Sprays gasoline that bursts into flame.'),
        ('Iron Spikes', 2, 24, 140, 'burst', [burst(p * 1.2, 3.5, 1.6, 0, 0, 0x90A4AE)], 'The car sprouts spikes and claws.'),
        ('Climbing Tires', 1, 14, 300, 'stance', [buff(effect('speed', 300, 2), effect('jump_boost', 300, 2))], 'Drives up sheer cliffs.')],
      0x546E7A, punch=p * 0.4, model='wheel_of_fortune')

    p = pw('Justice')
    S('Justice', 'long_range', dict(), [
        ('Fog Puppetry', 1, 26, 300, 'focus', [charm(12, 360, 400, 6, p * 0.3)], 'Controls anyone with an open wound through the fog.'),
        ('Mist Curtain', 1, 20, 240, 'focus', [area(0, 14, 360, [effect('blindness', 140), effect('slowness', 140, 1)], color=0xCFD8DC)],
         'Blankets the area in fog.'),
        ('Wound Strings', 3, 24, 200, 'point', [area(p * 0.6, 10, 50, [effect('weakness', 160, 1), effect('nausea', 160)], color=0xCFD8DC)],
         'Pierces holes through the victim\'s body.')],
      0xCFD8DC, scale=2.0, alpha=0.45, punch=p * 0.2, traits=[trait('evasion', chance=0.4, projectiles_only=False)], model='justice')

    p = pw('Lovers')
    S('Lovers', 'long_range',
      dict(skin=0x9E9E9E, eyes=0xE53935, hair=0x616161, hair_style='bald', outfit='bodysuit', top=0x757575, accent=0xBDBDBD, pants=0x616161, mark='mask'),
      [('Pain Link', 1, 30, 800, 'point', [link(24, 600)], 'Burrows into the brain: any pain Steely Dan feels, the host feels.'),
       ('Brain Spores', 2, 22, 240, 'point', [area(p * 0.8, 10, 40, [effect('nausea', 160), effect('weakness', 160, 1)], color=0x9E9E9E)],
        'Builds pain-spawning clones in the victim\'s brain.')],
      0x9E9E9E, scale=0.2, alpha=0.95, punch=0)

    p = pw('Sun')
    S('Sun', 'long_range', dict(), [
        ('Solar Heat', 1, 24, 200, 'focus', [area(p * 0.4, 16, 360, [effect('hunger', 200, 2), effect('weakness', 200, 1)], color=0xFF9800)],
         'Scorches the desert like a second sun.'),
        ('Light Ray', 1, 20, 80, 'point', [beam(p * 1.3, 30, 0.5, True, 0xFFEB3B, 80)], 'Fires rays of light from the sky.'),
        ('Solar Flare', 3, 30, 200, 'slam', [burst(p * 1.2, 5, 1.0, 5, 120, 0xFF9800)], 'A flare that sets the sand ablaze.')],
      0xFF9800, scale=1.4, alpha=0.9, punch=0, model='sun')

    p = pw('Death Thirteen')
    S('Death Thirteen', 'close_range', dict(), [
        ('Dream World', 1, 28, 400, 'gaze', [area(0, 10, 60, [effect('blindness', 120), effect('slowness', 120, 5), effect('mining_fatigue', 120, 3)], color=0x7E57C2)],
         'Drags sleepers into its dream world, where no other Stand exists.'),
        ('Scythe Reap', 1, 22, 100, 'sweep', [dash(p * 1.4, 4, hits=2), area(0, 4, 90, [effect('wither', 80, 1)], color=0x4A148C)],
         'Its scythe reaps in the dream.'),
        ('Nightmare Lock', 3, 26, 300, 'gaze', [area(p * 0.4, 8, 60, [effect('darkness', 160), effect('nausea', 160)], color=0x311B92)],
         'The dream turns to nightmare.')],
      0x4A148C, punch=p * 0.4, model='death_thirteen')

    p = pw('Judgement')
    S('Judgement', 'close_range',
      dict(skin=0x90A4AE, eyes=0xFFEB3B, hair=0x607D8B, hair_style='bald', outfit='armor', top=0x78909C, accent=0xFFD54F, pants=0x607D8B, mark='mask'),
      [('Three Wishes', 1, 30, 600, 'focus', [summon('zombie', 3, 600)], 'Grants a "wish": clay copies of the departed rise to fight.'),
       ('Genie Strike', 1, 22, 100, 'barrage', [rush(10, p * 2.0 / 10)], 'Pummels with its massive fists.'),
       ('Clay Imitation', 3, 26, 500, 'focus', [summon('husk', 2, 500)], 'Moulds clay into fighters.')],
      0x78909C, scale=1.2, punch=p * 0.45)

    p = pw('High Priestess')
    S('High Priestess', 'long_range',
      dict(skin=0x90A4AE, eyes=0xE53935, hair=0x455A64, hair_style='long', outfit='bodysuit', top=0x607D8B, accent=0xB0BEC5, pants=0x546E7A),
      [('Mimicry Ambush', 1, 22, 100, 'point', [strike(p * 1.5, 24, 2.0, teleport=True, effects=[effect('slowness', 60, 1)])],
        'Hides as any mineral object and strikes from inside it.'),
       ('Diamond Teeth', 1, 22, 120, 'barrage', [rush(8, p * 1.8 / 8)], 'Teeth claimed to be as hard as diamond.'),
       ('Razor Transformation', 2, 20, 100, 'thrust', [shot(p * 1.2, 4, 2.2, 0.25, 0, 30, 10, 0xB0BEC5)], 'Turns into blades and razors.')],
      0x607D8B, scale=0.7, punch=p * 0.4)

    p = pw('Geb')
    S('Geb', 'long_range', dict(), [
        ('Water Claw', 1, 20, 70, 'point', [strike(p * 1.5, 48, 3.0, effects=[effect('slowness', 40, 1)])],
         'Water slashes from a distance, faster than a bullet.'),
        ('Vibration Sonar', 1, 16, 400, 'focus', [area(0, 48, 360, [effect('glowing', 300)], color=0x29B6F6)],
         'Reads every footstep through the sand.'),
        ('Drowning Grip', 3, 26, 200, 'point', [area(p * 0.6, 8, 50, [effect('slowness', 100, 4)], pull=1.0, color=0x29B6F6)],
         'Seizes the victim and drags them down.')],
      0x29B6F6, alpha=0.6, punch=p * 0.3, model='geb')

    p = pw('Khnum')
    S('Khnum', 'bound',
      dict(skin=0xA1887F, eyes=0x212121, hair=0x3E2723, hair_style='short', outfit='robe', top=0x6D4C41, accent=0xFFD54F, pants=0x4E342E),
      [('Transfiguration', 1, 20, 600, 'stance', [buff(effect('invisibility', 400), effect('speed', 400, 0))], 'Changes Oingo\'s face into anyone else\'s.'),
       ('Bomb Orange', 2, 22, 200, 'thrust', [shot(p * 2.0, 1, 1.0, 0.4, 3.0, 60, 0, 0xFF9800)], 'A booby-trapped orange.')],
      0xA1887F, alpha=0.3, punch=0, weight=4)

    p = pw('Tohth')
    S('Tohth', 'bound', dict(), [
        ('Read the Prophecy', 1, 20, 400, 'focus', [buff(effect('luck', 300, 2), effect('speed', 300, 1), effect('resistance', 300, 1))],
         'The comic book\'s prophecies always come true.'),
        ('Fateful Link', 2, 22, 300, 'point', [area(0, 10, 40, [effect('glowing', 300), effect('unluck', 300, 2), effect('weakness', 300, 1)], color=0xFFEB3B)],
         'The prophecy seals the target\'s fate.')],
      0xFFEB3B, scale=0.6, punch=0, traits=[trait('evasion', chance=0.3, projectiles_only=False)], model='tohth', weight=4)

    p = pw('Anubis')
    S('Anubis', 'automatic', dict(), [
        ('Sword Possession', 1, 20, 400, 'stance', [buff(effect('strength', 300, 2), effect('speed', 300, 1))],
         'Possesses whoever draws it and makes them a master swordsman.'),
        ('Learning Blade', 1, 26, 120, 'barrage', [rush(10, p * 2.2 / 10, None, 2.8, 1.6, 2)], 'Remembers every attack - it never loses twice.'),
        ('Selective Intangibility', 2, 22, 100, 'thrust', [beam(p * 1.4, 7, 0.6, True, 0xFFD54F)], 'Its blade passes through walls to cut what is behind.')],
      0xFFD54F, punch=p * 0.5, model='anubis')

    p = pw('Bastet')
    S('Bastet', 'long_range', dict(), [
        ('Magnetic Attraction', 1, 22, 200, 'point', [area(0, 12, 60, [effect('slowness', 140, 3)], pull=3.0, color=0x90A4AE)],
         'Whoever touches it becomes a magnet; metal flies at them.'),
        ('Magnetized Debris', 2, 22, 100, 'thrust', [shot(p * 1.4, 6, 2.0, 0.3, 0, 40, 12, 0x90A4AE, homing=0.2)], 'Metal objects rocket towards the target.'),
        ('Socket Trap', 1, 16, 160, 'burst', [area(p * 0.6, 4, 360, [effect('slowness', 60, 8)], color=0x90A4AE)], 'Disguised as a power outlet.')],
      0x90A4AE, scale=0.5, punch=0, model='bastet')

    p = pw('Sethan')
    S('Sethan', 'bound',
      dict(skin=0x111111, eyes=0xE53935, hair=0x111111, hair_style='wild', outfit='cloak', top=0x111111, accent=0x212121, pants=0x111111, mark='red_eyes'),
      [('Age Reversal', 1, 30, 600, 'gaze', [shift('scale', -0.5, 400, 12, 70), shift('attack_damage', -0.5, 400, 12, 70)],
        'Its shadow turns whoever it touches back into a child.'),
       ('Shadow Reflection', 1, 18, 200, 'focus', [area(0, 8, 360, [effect('slowness', 100, 2), effect('darkness', 100)], color=0x111111)],
        'The shadow stretches across the ground.')],
      0x111111, alpha=0.55, punch=p * 0.3)

    p = pw('Osiris')
    S('Osiris', 'automatic',
      dict(skin=0x6D4C41, eyes=0xFFD54F, hair=0x3E2723, hair_style='bald', outfit='robe', top=0x4E342E, accent=0xFFD54F, pants=0x3E2723, mark='mask'),
      [('Soul Steal', 1, 34, 600, 'point', [strike(p * 3.0, 6, 1.2, effects=[effect('weakness', 300, 2), effect('slowness', 300, 2)])],
        'Tears out the soul of whoever loses a bet and seals it in a poker chip.'),
       ('The Wager', 1, 18, 300, 'gaze', [area(0, 8, 60, [effect('mining_fatigue', 200, 2), effect('unluck', 200, 2)], color=0xFFD54F)],
        'Challenges the opponent to a game.')],
      0xFFD54F, punch=0)

    p = pw('Horus')
    S('Horus', 'close_range', dict(), [
        ('Ice Missiles', 1, 20, 70, 'thrust', [shot(p * 1.3, 5, 2.2, 0.35, 0, 40, 8, 0xB3E5FC, trail='minecraft:snowflake')], 'Fires missiles of ice.'),
        ('Ice Prison', 2, 26, 240, 'focus', [area(p * 0.5, 6, 360, [effect('slowness', 140, 6), effect('mining_fatigue', 140, 3)], color=0xB3E5FC)],
         'Freezes everything around in ice.'),
        ('Icicle Rain', 3, 28, 200, 'slam', [burst(p * 1.3, 4.5, 1.2, 4, 0, 0xE1F5FE)], 'Drops giant icicles on the enemy.')],
      0xB3E5FC, scale=0.8, alpha=0.85, punch=p * 0.3, model='horus')

    p = pw('Atum')
    S('Atum', 'close_range',
      dict(skin=0xA1887F, eyes=0xFFD54F, hair=0x3E2723, hair_style='bowl', outfit='robe', top=0x5D4037, accent=0xD4AF37, pants=0x4E342E),
      [('Binary Soul Reading', 1, 20, 300, 'focus', [area(0, 20, 360, [effect('glowing', 300), effect('weakness', 200, 0)], color=0xD4AF37)],
        'Reads the soul - a yes or no answer can never be hidden.'),
       ('Soul Steal', 2, 34, 600, 'point', [strike(p * 2.6, 6, 1.2, effects=[effect('weakness', 300, 2)])], 'Draws the soul into a doll.')],
      0xD4AF37, punch=p * 0.3)

    p = pw('Cream')
    S('Cream', 'close_range', dict(), [
        ('Void Sphere', 4, 60, 600, 'focus', [void(80, 0.45, 1.6, p * 0.9)],
         'Swallows Vanilla Ice and itself into the void and erases everything in their path.'),
        ('Void Bite', 1, 26, 120, 'barrage', [rush(6, p * 2.0 / 6, None, 2.2, 1.6, 3)], 'Its mouth leads into the void.'),
        ('Decapitating Chop', 1, 22, 100, 'lunge', [dash(p * 1.6, 4.5, knockback=0.6)], 'A chop that can take a head off.')],
      0x4E342E, punch=p * 0.5, model='cream', weight=2)

    p = pw('Tenore Sax')
    S('Tenore Sax', 'bound', dict(skin=0xB39DDB, hair=0x7E57C2, hair_style='bald', outfit='robe', top=0x9575CD, accent=0xFFD54F, pants=0x7E57C2),
      [('Illusion Maze', 1, 24, 300, 'focus', [area(0, 16, 360, [effect('nausea', 200), effect('blindness', 100), effect('slowness', 200, 1)], color=0xB39DDB)],
        'Turns DIO\'s mansion into an endless maze of illusions.'),
       ('Phantom Walls', 1, 18, 300, 'focus', [wall(6, 4, 2, 200)], 'Corridors and walls that are not really there.')],
      0xB39DDB, alpha=0.25, punch=0, weight=4)

    S("Holy's Stand", 'bound', dict(), [
        ('Thorny Vines', 1, 16, 200, 'focus', [area(p * 0.2, 4, 360, [effect('slowness', 100, 1), effect('poison', 60, 0)], color=0x6A1B9A)],
         'Vines without a fighting spirit to control them; they weaken their user.')],
      0x6A1B9A, punch=0, model='hermit_purple', weight=0)

    S("Jonathan's Stand", 'close_range', dict(), [
        ('Spirit Photography', 1, 18, 400, 'focus', [area(0, 48, 360, [effect('glowing', 400)], color=0x6A1B9A)],
         'DIO divines with the vines of Jonathan\'s body, like Hermit Purple.')],
      0x6A1B9A, punch=pw('Hermit Purple') * 0.3, model='hermit_purple', weight=0)


ANIM = {'barrage': 'barrage', 'thrust': 'thrust', 'point': 'thrust', 'gaze': 'focus', 'stance': 'stance', 'focus': 'focus', 'sweep': 'sweep',
        'slam': 'slam', 'burst': 'burst', 'lunge': 'lunge'}


def write_items(lang, stand_ids):
    items = [{'item': 'stand_arrow', 'kind': 'arrow', 'lore': f'item.{NS}.stand_arrow.lore'}]
    lang[f'item.{NS}.stand_arrow'] = 'Stand Arrow'
    lang[f'item.{NS}.stand_arrow.lore'] = 'An arrow carved from a meteorite. Those it pierces awaken a Stand - or die.'
    for page, sid in stand_ids.items():
        items.append({'item': f'disc_{sid}', 'kind': 'disc', 'target': ns(sid), 'lore': f'item.{NS}.disc.lore', 'color': STANDS[page]['color']})
        lang[f'item.{NS}.disc_{sid}'] = f'Stand Disc: {page}'
    lang[f'item.{NS}.disc.lore'] = 'A memory disc holding a Stand. Push it into your head to make the Stand yours.'
    with open(ITEMS_JSON, 'w', encoding='utf-8') as f:
        json.dump(items, f, indent=1)
    return items


def sprites(items):
    from PIL import Image, ImageDraw
    for it in items:
        im = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        if it['kind'] == 'arrow':   # golden beetle-headed arrow
            d.line([(5, 27), (24, 8)], fill=(141, 110, 99, 255), width=2)
            d.polygon([(22, 4), (29, 3), (28, 10), (24, 9), (23, 8)], fill=(255, 193, 7, 255))
            d.ellipse([22, 5, 27, 10], fill=(255, 213, 79, 255))
            d.polygon([(4, 24), (2, 30), (8, 28)], fill=(198, 40, 40, 255))
        else:                        # a shiny disc tinted by the Stand
            c = it['color']
            col = ((c >> 16) & 255, (c >> 8) & 255, c & 255, 255)
            d.ellipse([2, 2, 29, 29], fill=(220, 220, 230, 255))
            d.ellipse([5, 5, 26, 26], fill=col)
            d.ellipse([12, 12, 19, 19], fill=(240, 240, 245, 255))
            d.ellipse([14, 14, 17, 17], fill=(0, 0, 0, 0))
            d.arc([6, 6, 25, 25], 200, 250, fill=(255, 255, 255, 255), width=2)
        im.save(os.path.join(ITEM_DIR, it['item'] + '.png'))
    im = Image.new('RGBA', (32, 32), (0, 0, 0, 0))   # the Stand technique item: purple star medallion
    d = ImageDraw.Draw(im)
    d.ellipse([2, 3, 29, 30], fill=(74, 20, 140, 255)); d.ellipse([5, 6, 26, 27], fill=(179, 157, 219, 255))
    d.rectangle([14, 0, 17, 3], fill=(200, 170, 90, 255))
    import math
    pts = []
    for k in range(10):
        a = -math.pi / 2 + k * math.pi / 5
        r = 9 if k % 2 == 0 else 4
        pts.append((16 + r * math.cos(a), 16.5 + r * math.sin(a)))
    d.polygon(pts, fill=(255, 213, 79, 255))
    im.save(os.path.join(ITEM_DIR, 'stand_arts.png'))


def main():
    data = json.load(open(DOC_JSON, encoding='utf-8'))
    build_stands(data)
    missing = [p for p in data['stands'] if p not in STANDS]
    if missing: raise SystemExit(f'Stands without a kit: {missing}')
    lang = OrderedDict()
    manifestations, abilities, stand_ids = OrderedDict(), OrderedDict(), OrderedDict()
    for page, s in STANDS.items():
        sid = slug(page)
        stand_ids[page] = sid
        info = data['stands'][page]
        stats = info['stats']
        ids = []
        man = f'{sid}_manifest'
        abilities[man] = {'system': NS, 'name': text(f'ability.{NS}.{man}', f'{page}!'), 'cast_type': 'instant',
                          'cost': {'pool': STAND_POOL, 'amount': 0}, 'cooldown_ticks': 20, 'required_tier': 1,
                          'actions': [{'type': 'animecore:manifest'}], 'token': ns('stand'), 'cast_animation': 'animecore:cast_focus'}
        lang[f'ability.{NS}.{man}'] = f'{page}!'
        lang[f'ability.{NS}.{man}.desc'] = f'Calls {page} out - or back.'
        ids.append(ns(man))
        for name, rank, cost, cd, anim, acts, desc in s['abilities']:
            aid = f'{sid}_{slug(name)}' if not slug(name).startswith(sid) else slug(name)
            abilities[aid] = {'system': NS, 'name': text(f'ability.{NS}.{aid}', name), 'cast_type': 'instant',
                              'cost': {'pool': STAND_POOL, 'amount': cost}, 'cooldown_ticks': cd, 'required_tier': rank,
                              'actions': acts, 'token': ns('stand'), 'cast_animation': f'animecore:cast_{ANIM.get(anim, "thrust")}'}
            if 'Overdrive' in name: abilities[aid]['affinity'] = ns('ripple')
            if sid == 'magicians_red': abilities[aid]['affinity'] = ns('heat')
            lang[f'ability.{NS}.{aid}'] = name
            lang[f'ability.{NS}.{aid}.desc'] = desc
            ids.append(ns(aid))
        rng = RANGE_M.get(stats.get('range', 'C'), 10.0)
        user = info['user'] or 'unknown'
        desc = f'{page} - Stand of {user}. ' + ' · '.join(f'{k.title()} {v}' for k, v in stats.items())
        look = dict(s['look'])
        if s['model']: look['model'] = ns(f'stand/{s["model"]}')
        m = {'system': NS, 'name': text(f'manifestation.{NS}.{sid}', page), 'description': text(f'manifestation.{NS}.{sid}.desc', desc),
             'kind': s['kind'], 'appearance': look, 'scale': s['scale'], 'alpha': s['alpha'], 'color': s['color'], 'range': rng,
             'punch': round(s['punch'] if s['punch'] is not None else 3.0, 1), 'stats': stats, 'abilities': ids, 'traits': s['traits'],
             'weight': s['weight']}
        manifestations[sid] = m
        lang[f'manifestation.{NS}.{sid}'] = page
        lang[f'manifestation.{NS}.{sid}.desc'] = desc
    items = write_items(lang, stand_ids)
    sprites(items)
    out = {'generated_from': os.path.relpath(DOC_JSON, ROOT), 'crawled': data['crawled'], 'manifestations': manifestations,
           'abilities': abilities, 'lang': lang}
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    write_doc(data, manifestations, abilities)
    print(f'stands: {len(manifestations)} Stands, {len(abilities)} Stand abilities, {len(items)} items')


def write_doc(data, manifestations, abilities):
    tree = data['category_tree']
    L = ["# JoJo's Bizarre Encyclopedia coverage: Part 3 (Stardust Crusaders)", '',
         f"Source: {data['source']} - crawled {data['crawled']}. Method: {data['method']}.", '',
         '## Category tree', '', '| category | pages | subcategories | in game |', '|---|---:|---:|---|']
    out_of_scope = re.compile(r'Part [4-9] Stands|Light Novel|El Aleph|Golden Heart|Novel\)|JORGE|Lonesome|Purple Haze|Synchronicity|The Book|Thus Spoke|Morioh|Japan|Season 2|OVA|Locations')
    for c, v in tree.items():
        note = v.get('skipped') or ('out of scope: not Part 3 (other parts, novels, spin-offs, anime episode lists, locations)' if out_of_scope.search(c)
                                    else 'Stand classification - used for the Stand kinds' if c.startswith('Category:') and 'Stands' in c and 'Part 3' not in c
                                    else 'covered')
        L.append(f"| {c} | {len(v['pages'])} | {len(v['subcats'])} | {note} |")
    L += ['', '`Category:Stands` holds the Stands of every part; only its Part 3 members are game content here. Its classification',
          'subcategories (Close-Range, Long Distance, Automatic, Bound, Sentient, Multi-Unit...) decide each Stand\'s kind',
          '(close range: fights at the user\'s side; long range: flies out; automatic; bound to an object or substance).', '',
          f"Not turned into Stands: {', '.join(f'{k} ({v})' for k, v in data['not_stands'].items())}; translation pages (`/es`...).", '',
          f'## Stands ({len(manifestations)})', '', '| Stand | user | kind | stats | game abilities |', '|---|---|---|---|---|']
    for sid, m in manifestations.items():
        page = m['name']['fallback']
        info = data['stands'][page]
        ab = [abilities[a.split(':')[1]]['name']['fallback'] for a in m['abilities'][1:]]
        stats = ' '.join(f"{k[:3].title()} {v}" for k, v in info['stats'].items()) or '-'
        L.append(f"| {page} | {info['user']} | {m['kind']} | {stats} | {', '.join(ab)} |")
    L += ['', '### Wiki abilities per Stand', '']
    for page, info in data['stands'].items():
        names = info['abilities'] + [t['name'] for t in info['techniques']]
        names = [n for n in names if not re.search(r'Color Schemes|\(Android|\(PS|All-Star|JORGE|Development|Background|Puzzle|Monster Strike|Diamond Records|Ora Ora Overdrive|Stardust Crusaders \(1989\)|Over Heaven', n)]
        L.append(f"- **{page}**: {', '.join(dict.fromkeys(names)) or '(abilities described in prose)'} - {info['summary'][:220]}")
    with open(os.path.join(ROOT, 'docs', 'wiki', 'jojowiki_part_3.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')


if __name__ == '__main__':
    if len(sys.argv) > 2 and sys.argv[1] == '--extract':
        extract(sys.argv[2])
    else:
        main()
