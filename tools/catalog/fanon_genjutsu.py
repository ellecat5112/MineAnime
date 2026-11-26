"""Fan genjutsu (Naruto Fanon wiki, Category:Genjutsu) for NarutoRPG - every technique with its own vision.

Input : docs/wiki/narutofanon_genjutsu.json  (recursive crawl incl. subcategories; descriptions + infoboxes)
Output: tools/catalog/generated/fanon_genjutsu_catalog.json  (AbilityDefinition entries + lang), docs/wiki/narutofanon_genjutsu.md

Each genjutsu gets an IllusionProfile chosen from its own description (crows -> crow flock, moon -> blood moon, flute ->
sound rings, mirror -> shattered shards...), colours from the colour words it uses, and per-technique counts / speeds;
no two techniques share the same visual signature. Gameplay effects follow the motif (chains bind, flames burn, vortices
pull, serpents poison, lotus restores the caster...). Ids live under narutorpg:fanon/... so they never collide with canon.
"""
import colorsys
import json
import os
import re
import zlib

import clans

ROOT = clans.ROOT
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutofanon_genjutsu.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'fanon_genjutsu_catalog.json')
NS = clans.NS

# pattern -> keywords (searched in title first, then description), default palette
PATTERNS = [
    ('blood_moon', ['tsukuyomi', 'blood moon', 'red moon', 'moon', 'lunar', 'crimson sky'], (0xD50000, 0x1A0000)),
    ('crows', ['crow', 'raven', 'bird', 'feather', 'wing'], (0x111111, 0xB71C1C)),
    ('petals', ['petal', 'sakura', 'cherry', 'blossom', 'flower', 'pollen', 'rose', 'garden', 'butterfl'], (0xF48FB1, 0x880E4F)),
    ('lotus', ['lotus', 'nirvana', 'temple', 'buddha', 'heaven', 'paradise', 'holy', 'divine'], (0xFFF59D, 0x6A1B9A)),
    ('shards', ['mirror', 'reflect', 'crystal', 'glass', 'shatter', 'prism'], (0xE1F5FE, 0x4A148C)),
    ('fog', ['fog', 'mist', 'smoke', 'haze', 'cloud', 'vapor', 'vapour', 'dust'], (0xB0BEC5, 0x37474F)),
    ('chains', ['chain', 'bind', 'shackle', 'restrain', 'immobil', 'paraly', 'prison', 'cage', 'trap'], (0xB0BEC5, 0x263238)),
    ('eyes', ['eye', 'gaze', 'stare', 'sharingan', 'byakugan', 'rinnegan', 'watch', 'sight', 'vision'], (0xC62828, 0x212121)),
    ('tendrils', ['dark', 'shadow', 'abyss', 'void', 'despair', 'nightmare', 'night', 'black'], (0x311B92, 0x050008)),
    ('flames', ['fire', 'flame', 'burn', 'hell', 'inferno', 'blaze', 'ash'], (0xFF6D00, 0xB71C1C)),
    ('stars', ['star', 'cosmos', 'galaxy', 'constellation', 'universe', 'celestial', 'sky'], (0xFFF176, 0x0D1B3E)),
    ('web', ['web', 'spider', 'thread', 'string', 'silk', 'puppet'], (0xECEFF1, 0x263238)),
    ('rings', ['sound', 'flute', 'music', 'melody', 'song', 'bell', 'voice', 'echo', 'drum', 'harp', 'whisper', 'banshee'], (0x80DEEA, 0x1A237E)),
    ('rain', ['rain', 'storm', 'drown', 'water', 'ocean', 'sea', 'wave', 'tear'], (0x64B5F6, 0x0D47A1)),
    ('vortex', ['vortex', 'black hole', 'whirl', 'spiral', 'abyss', 'swallow', 'devour', 'portal', 'dimension', 'time'], (0x7C4DFF, 0x000000)),
    ('serpents', ['snake', 'serpent', 'basilisk', 'dragon', 'viper', 'worm', 'insect', 'crawl'], (0x7CB342, 0x1B5E20)),
    ('veins', ['blood', 'vein', 'pain', 'torment', 'torture', 'agony', 'curse', 'wound', 'death'], (0xB71C1C, 0x3E0000)),
    ('kaleidoscope', ['kaleido', 'illusion world', 'false surround', 'surrounding', 'labyrinth', 'maze', 'dream', 'confus'], (0x00E5FF, 0xD500F9)),
    ('snow', ['snow', 'ice', 'frost', 'freez', 'winter', 'cold'], (0xE3F2FD, 0x4FC3F7)),
    ('spiral', [], (0xC2185B, 0x4A148C)),
]
COLOR_WORDS = [('crimson', 0xB71C1C), ('scarlet', 0xD50000), ('blood', 0x8E0000), ('red', 0xE53935), ('azure', 0x2979FF), ('blue', 0x1E88E5),
               ('cyan', 0x00E5FF), ('teal', 0x00897B), ('green', 0x43A047), ('emerald', 0x00C853), ('jade', 0x00A86B), ('gold', 0xFFC107),
               ('golden', 0xFFB300), ('yellow', 0xFFEB3B), ('amber', 0xFFAB00), ('orange', 0xFF6D00), ('purple', 0x8E24AA), ('violet', 0x7C4DFF),
               ('pink', 0xF06292), ('white', 0xFAFAFA), ('silver', 0xCFD8DC), ('grey', 0x9E9E9E), ('gray', 0x9E9E9E), ('black', 0x111111),
               ('dark', 0x1A1A2E), ('obsidian', 0x0B0B0F), ('brown', 0x6D4C41)]
PARTICLES = {'blood_moon': 'minecraft:damage_indicator', 'crows': 'minecraft:squid_ink', 'petals': 'minecraft:cherry_leaves',
             'lotus': 'minecraft:spore_blossom_air', 'shards': 'minecraft:enchant', 'fog': 'minecraft:campfire_cosy_smoke',
             'chains': 'minecraft:crit', 'eyes': 'minecraft:enchant', 'tendrils': 'minecraft:squid_ink', 'flames': 'minecraft:flame',
             'stars': 'minecraft:end_rod', 'web': 'minecraft:white_ash', 'rings': 'minecraft:note', 'rain': 'minecraft:falling_water',
             'vortex': 'minecraft:reverse_portal', 'serpents': 'minecraft:spore_blossom_air', 'veins': 'minecraft:damage_indicator',
             'kaleidoscope': 'minecraft:end_rod', 'snow': 'minecraft:snowflake', 'spiral': 'minecraft:witch'}
ALT_PARTICLES = ['minecraft:witch', 'minecraft:enchant', 'minecraft:portal', 'minecraft:ash', 'minecraft:glow', 'minecraft:sculk_soul',
                 'minecraft:soul', 'minecraft:wax_on', 'minecraft:electric_spark', 'minecraft:mycelium']


def effect(name, duration=100, amp=0):
    return {'effect': f'minecraft:{name}', 'duration': duration, 'amplifier': amp}


# motif-specific effects on the victims (+ optional caster buff)
MOTIF_EFFECTS = {
    'blood_moon': [effect('wither', 80), effect('nausea', 160), effect('slowness', 120, 2)],
    'crows': [effect('weakness', 120), effect('darkness', 80)],
    'petals': [effect('slowness', 120, 1), effect('nausea', 120)],
    'lotus': [effect('weakness', 120), effect('slowness', 100)],
    'shards': [effect('nausea', 140), effect('weakness', 80)],
    'fog': [effect('darkness', 140), effect('blindness', 60)],
    'chains': [effect('slowness', 120, 5), effect('mining_fatigue', 120, 2)],
    'eyes': [effect('blindness', 80), effect('glowing', 160)],
    'tendrils': [effect('darkness', 120), effect('slowness', 100, 3)],
    'flames': [effect('nausea', 80), effect('weakness', 60)],
    'stars': [effect('levitation', 30), effect('nausea', 120)],
    'web': [effect('slowness', 140, 6)],
    'rings': [effect('nausea', 120), effect('mining_fatigue', 100, 1)],
    'rain': [effect('slowness', 100, 1), effect('weakness', 100)],
    'vortex': [effect('nausea', 120), effect('slowness', 80, 1)],
    'serpents': [effect('poison', 100), effect('slowness', 80)],
    'veins': [effect('wither', 100), effect('weakness', 100)],
    'kaleidoscope': [effect('nausea', 160), effect('glowing', 100)],
    'snow': [effect('slowness', 120, 4), effect('mining_fatigue', 100, 1)],
    'spiral': [effect('nausea', 120), effect('slowness', 80, 1)],
}


def choose_pattern(title, desc):
    tl, dl = title.lower(), desc.lower()
    for name, kws, _ in PATTERNS:
        if any(k in tl for k in kws):
            return name
    best, score = 'spiral', 0
    for name, kws, _ in PATTERNS:
        s = sum(dl.count(k) for k in kws)
        if s > score:
            best, score = name, s
    if score == 0:   # no motif words: spread evenly over the patterns by a stable hash
        best = PATTERNS[zlib.crc32(title.encode()) % (len(PATTERNS) - 1)][0]
    return best


def palette(pattern, title, desc):
    default = next(p for p in PATTERNS if p[0] == pattern)[2]
    text = (title + ' ' + desc).lower()
    found = []
    for word, rgb in COLOR_WORDS:
        if re.search(r'\b' + word + r'\b', text) and rgb not in found:
            found.append(rgb)
    c1 = found[0] if found else default[0]
    c2 = found[1] if len(found) > 1 else default[1]
    return c1, c2


def shift_hue(rgb, amount):
    r, g, b = ((rgb >> 16) & 255) / 255, ((rgb >> 8) & 255) / 255, (rgb & 255) / 255
    h, l, s = colorsys.rgb_to_hls(r, g, b)
    r, g, b = colorsys.hls_to_rgb((h + amount) % 1.0, max(0.12, min(0.88, l)), max(0.35, s))
    return (int(r * 255) << 16) | (int(g * 255) << 8) | int(b * 255)


def info_for(page):
    ib = page['infobox']
    desc = page['description'].lower()
    rank = ib.get('jutsu rank', '')
    if not rank:
        rank = 'S' if any(k in desc for k in ('forbidden', 'kinjutsu', 'ultimate', 'strongest')) else \
            'A' if any(k in desc for k in ('powerful', 'master', 'deadly')) else 'B'
    rng = ib.get('jutsu range', '') or ('Short, Mid, Long' if any(k in desc for k in ('area', 'everyone', 'large group')) else 'Short, Mid')
    return {'title': page['title'], 'jutsu classification': ib.get('jutsu classification', 'Genjutsu') or 'Genjutsu',
            'jutsu type': ib.get('jutsu type', ''), 'jutsu class type': ib.get('jutsu class type', 'Supplementary') or 'Supplementary',
            'jutsu rank': rank, 'jutsu range': rng, 'hand signs': ib.get('hand signs', ''), 'romaji': ''}


def main():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    used_combos = set(clans.RESERVED_COMBOS)
    out = {'generated_from': os.path.relpath(CRAWL, ROOT), 'crawled': crawl['crawled'], 'abilities': {}, 'lang': {}, 'patterns': {}}
    signatures = set()
    for title in sorted(crawl['genjutsu']):
        page = crawl['genjutsu'][title]
        aid = 'fanon/' + clans.slug(title)
        if aid in out['abilities']:
            aid += '_' + format(zlib.crc32(title.encode()) & 0xFFFF, 'x')
        info = info_for(page)
        d, rank = clans.build_ability(title, info, '', {}, used_combos)
        pattern = choose_pattern(title, page['description'])
        c1, c2 = palette(pattern, title, page['description'])
        h = zlib.crc32(title.encode())
        count = 3 + h % 6
        speed = round(0.6 + (h >> 8) % 100 / 70.0, 2)
        density = round(0.6 + (h >> 16) % 100 / 100.0, 2)
        particle = PARTICLES[pattern] if (h >> 4) % 3 else ALT_PARTICLES[(h >> 12) % len(ALT_PARTICLES)]
        # guarantee a unique visual signature
        tries = 0
        while (pattern, c1, c2, count) in signatures:
            tries += 1
            c1 = shift_hue(c1, 0.07 * tries)
            count = 3 + (count + tries) % 8
        signatures.add((pattern, c1, c2, count))
        cls = clans.classes(info)
        radius = 16.0 if 'Long' in info['jutsu range'] else 10.0 if 'Mid' in info['jutsu range'] else 6.0
        cone = 360.0 if any(k in page['description'].lower() for k in ('surround', 'area', 'everyone', 'around the user', 'dome', 'field')) else 70.0
        dmg = float(clans.RANK_DAMAGE[rank])
        actions = [{'type': 'animecore:area_effect', 'radius': radius, 'cone_degrees': cone, 'damage': round(dmg * (0.6 if pattern in ('flames', 'veins', 'blood_moon') else 0.3), 1),
                    'pull': 1.5 if pattern == 'vortex' else 0.0, 'color': c1, 'effects': MOTIF_EFFECTS[pattern]}]
        if pattern == 'lotus':
            actions.append({'type': 'animecore:self_buff', 'heal': 4.0, 'effects': [effect('regeneration', 100)]})
        d['actions'] = actions
        d['token'] = ns('genjutsu')
        d['visuals'] = {'style': 'illusion', 'screen_effect': 'illusion', 'color': c1,
                        'illusion': {'pattern': pattern, 'color': c1, 'color2': c2, 'count': count, 'speed': speed,
                                     'density': density, 'particle': particle}}
        d.pop('cast_animation', None)   # genjutsu use the gaze animation
        d['name'] = clans.text(f'ability.{NS}.{aid}', page['title'])
        out['abilities'][aid] = d
        out['lang'][f'ability.{NS}.{aid}'] = page['title']
        out['patterns'][aid] = pattern
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    from collections import Counter
    c = Counter(out['patterns'].values())
    print(f"fan genjutsu: {len(out['abilities'])} abilities, {len(signatures)} unique visual signatures; patterns: {dict(c)}")
    write_doc(crawl, out, c)


def ns(p):
    return clans.ns(p)


def write_doc(crawl, cat, counts):
    from collections import Counter
    reasons = Counter(crawl['excluded_pages'].values())
    L = ['# Naruto Fanon `Category:Genjutsu` coverage', '',
         f"Source: {crawl['source']} - crawled {crawl['crawled']}. Method: {crawl['method']}.", '', '## Category tree', '']
    for c, v in crawl['category_tree'].items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories")
    L += ['', f"Every subcategory was crawled. **{len(cat['abilities'])} genjutsu** became abilities (main category plus *Path Gold Alignment "
          "Techniques* and *The Way of the Evil Path*, which are technique categories). Pages that are not techniques were not turned into "
          f"content ({len(crawl['excluded_pages'])}): " + ', '.join(f'{n} {r}s' for r, n in reasons.most_common()) +
          ' - *Genjutsu User* and *Genjutsu Specialist* list characters, and a few character / kekkei genkai pages sit in the main category. '
          'The full list is in `narutofanon_genjutsu.json` (`excluded_pages`).', '',
          '## Unique visuals', '',
          'Each genjutsu has its own `illusion` profile: a vision pattern picked from its own description (title words first), colours from '
          'the colour words it uses, and per-technique element count, speed, density and particle. A uniqueness pass guarantees no two '
          'techniques share pattern + colours + count. The victim sees the vision full-screen; everyone sees the matching world effect.', '',
          '| Pattern | genjutsu |', '|---|---:|'] + [f'| {p} | {n} |' for p, n in counts.most_common()] + [
          '', 'They live on the **Genjutsu** token (`narutorpg:fanon/...` ids) and unlock through Genjutsu mastery like other free techniques.',
          'Fan techniques are community creations from the Naruto Fanon wiki (CC BY-SA); only names and generated stats are used.']
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutofanon_genjutsu.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutofanon_genjutsu.md')


if __name__ == '__main__':
    main()
