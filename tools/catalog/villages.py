"""Village origins & backgrounds catalog for NarutoRPG.

Input : docs/wiki/narutopedia_villages.json  (MediaWiki crawl of Category:Villages + member / tool affiliations)
        narutorpg/.../ninja_tools.json         (tool items)
        tools/catalog/generated/clan_catalog.json (abilities that already exist)
Output: tools/catalog/generated/village_catalog.json - FactionDefinition / BackgroundDefinition / AbilityDefinition entries in
        engine codec shape (decoded by NarutoContentBootstrap during DataGen), plus lang.
        docs/wiki/narutopedia_villages.md, village gear sprites.

Run:   python3 tools/catalog/villages.py   (after clans.py; build.py runs both)
"""
import json
import os
import re
from collections import Counter

import clans

ROOT = clans.ROOT
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_villages.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'village_catalog.json')
NS = clans.NS
ns, text, slug, attr, power, capacity, regen = clans.ns, clans.text, clans.slug, clans.attr, clans.power, clans.capacity, clans.regen

GAKURE = {  # page -> (id, short English)
    'Amegakure': ('hidden_rain', 'Hidden Rain'), 'Hoshigakure': ('hidden_star', 'Hidden Star'),
    'Ishigakure': ('hidden_rock', 'Hidden Rock'), 'Iwagakure': ('hidden_stone', 'Hidden Stone'),
    'Kirigakure': ('hidden_mist', 'Hidden Mist'), 'Konohagakure': ('hidden_leaf', 'Hidden Leaf'),
    'Kumogakure': ('hidden_cloud', 'Hidden Cloud'), 'Kusagakure': ('hidden_grass', 'Hidden Grass'),
    'Otogakure': ('hidden_sound', 'Hidden Sound'), 'Shimogakure': ('hidden_frost', 'Hidden Frost'),
    'Sunagakure': ('hidden_sand', 'Hidden Sand'), 'Takigakure': ('hidden_waterfall', 'Hidden Waterfall'),
    'Tanigakure': ('hidden_valley', 'Hidden Valley'), 'Tsukigakure': ('hidden_moon', 'Hidden Moon'),
    'Uzushiogakure': ('hidden_whirlpool', 'Hidden Whirlpool'), 'Yugakure': ('hidden_hot_water', 'Hidden Hot Water'),
    'Yukigakure': ('hidden_snow', 'Hidden Snow'), 'Yumegakure': ('hidden_dream', 'Hidden Dream'),
}
SAGE_REALMS = {'Mount Myōboku', 'Ryūchi Cave', 'Shikkotsu Forest'}
GREAT_FIVE = {'hidden_leaf', 'hidden_sand', 'hidden_mist', 'hidden_cloud', 'hidden_stone'}

COLORS = {'hidden_leaf': 0x4CAF50, 'hidden_sand': 0xD7A86E, 'hidden_mist': 0x4FC3F7, 'hidden_cloud': 0xECEFF1,
          'hidden_stone': 0x8D6E63, 'hidden_rain': 0x607D8B, 'hidden_sound': 0x7E57C2, 'hidden_waterfall': 0x26A69A,
          'hidden_grass': 0x689F38, 'hidden_snow': 0xB3E5FC, 'hidden_star': 0xFFD54F, 'hidden_whirlpool': 0xE64A19,
          'hidden_hot_water': 0xFF8A65, 'hidden_dream': 0xBA68C8, 'hidden_moon': 0x9FA8DA, 'hidden_frost': 0x80DEEA,
          'hidden_valley': 0x795548, 'hidden_rock': 0x9E9E9E, 'missing_nin': 0x263238}

# village perks: nature mastery and terrain (stat modifiers only - removed when leaving the village)
PERKS = {
    'hidden_leaf': [regen(clans.CHAKRA, 1.1), power(1.05, 'fire')],
    'hidden_sand': [attr('armor', 1.0), power(1.1, 'wind')],
    'hidden_mist': [power(1.1, 'water'), attr('water_movement_efficiency', 0.4)],
    'hidden_cloud': [power(1.1, 'lightning'), attr('movement_speed', 0.04, 'add_multiplied_base')],
    'hidden_stone': [power(1.1, 'earth'), attr('knockback_resistance', 0.15)],
    'hidden_rain': [power(1.05, 'water'), capacity(clans.CHAKRA, 10)],
    'hidden_sound': [attr('attack_speed', 0.08, 'add_multiplied_base')],
    'hidden_waterfall': [power(1.05, 'water'), attr('max_health', 2.0)],
    'hidden_grass': [regen(clans.STAMINA, 1.15)],
    'hidden_snow': [power(1.1, 'ice')],
    'hidden_whirlpool': [capacity(clans.CHAKRA, 30)],
    'hidden_hot_water': [attr('max_health', 2.0)],
    'missing_nin': [attr('movement_speed', 0.05, 'add_multiplied_base'), attr('attack_damage', 1.0)],
}
SAGE_PERK = [regen(clans.CHAKRA, 1.2), attr('max_health', 2.0)]
CIVILIAN_PERK = [attr('luck', 1.0)]

# starting standings (allied +, rival -); everybody starts at +100 with their own village
RELATIONS = {
    'hidden_leaf': {'hidden_sand': 50, 'hidden_whirlpool': 50, 'hidden_mist': 0, 'hidden_cloud': -25, 'hidden_stone': -25, 'hidden_sound': -50},
    'hidden_sand': {'hidden_leaf': 50, 'hidden_mist': 0, 'hidden_cloud': -10, 'hidden_stone': -10, 'hidden_sound': -50},
    'hidden_mist': {'hidden_leaf': 0, 'hidden_sand': 0, 'hidden_cloud': -10, 'hidden_stone': -10},
    'hidden_cloud': {'hidden_leaf': -25, 'hidden_stone': -25, 'hidden_mist': -10, 'hidden_sand': -10},
    'hidden_stone': {'hidden_leaf': -25, 'hidden_cloud': -25, 'hidden_rain': -10},
    'hidden_sound': {'hidden_leaf': -50, 'hidden_sand': -50},
    'hidden_rain': {'hidden_stone': -10, 'hidden_leaf': -10},
    'hidden_whirlpool': {'hidden_leaf': 50},
}
STANDARD_ISSUE = {'Kunai', 'Shuriken', 'Senbon', 'Explosive Tag', 'Smoke Bomb', 'Makibishi', 'Barrier Tag', 'Military Rations Pill',
                  'Blood Increasing Pill', 'Flash Bomb', 'Wire Strings', 'Smoke-Flash Bomb', 'Weights', 'Three Coloured Pills'}
EXCLUDED_CLASSES = {'Hiden', 'Kekkei Genkai', 'Dōjutsu', 'Kekkei Mōra', 'Kekkei Tōta', 'Shinjutsu'}


def stack(item, count=1, components=None):
    d = {'id': ns(item) if ':' not in item else item, 'count': count}
    if components:
        d['components'] = components
    return d


def badge(fid, color=None, scratched=False):
    c = {'animecore:faction_badge': {'faction': ns(fid), 'scratched': scratched}}
    if color is not None:
        c['minecraft:dyed_color'] = {'rgb': color & 0xFFFFFF, 'show_in_tooltip': False}
    return c


def village_id(page):
    if page in GAKURE:
        return GAKURE[page][0]
    return slug(page)


def display_name(page):
    return f'{page} ({GAKURE[page][1]})' if page in GAKURE else page


def kind_of(page):
    if page in GAKURE:
        return 'hidden_village'
    if page in SAGE_REALMS:
        return 'sage_realm'
    return 'village'


def main():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    tools = json.load(open(clans.TOOLS, encoding='utf-8'))
    clan_cat = json.load(open(clans.OUT, encoding='utf-8'))
    tool_ids = {}
    for t in tools:
        tool_ids[t['wiki']] = t['id']
        tool_ids[t['name']] = t['id']
    existing_abilities = set(clan_cat['abilities']) | {'fireball_jutsu', 'substitution_jutsu'}
    jutsu = crawl['jutsu']
    villages = crawl['villages']

    # ---- jutsu ownership: the village whose members use it most (share >= 50 % of hidden-village users) ----
    per_village = {p: Counter(v['member_jutsu_counts']) for p, v in villages.items()}
    totals = Counter()
    for c in per_village.values():
        totals.update(c)

    def eligible(title):
        info = jutsu.get(title)
        if not info:
            return False
        cls = clans.classes(info)
        if cls & EXCLUDED_CLASSES or 'Tailed Beast' in title or 'Tailed Beast' in info['jutsu type']:
            return False
        if any(n in info['jutsu type'] for n in ('Wood Release', 'Ice Release', 'Magnet Release', 'Lava Release', 'Storm Release',
                                                  'Boil Release', 'Scorch Release', 'Explosion Release', 'Dust Release')):
            return False   # kekkei genkai natures belong to clans
        return True

    owner = {}
    for title, total in totals.items():
        if not eligible(title):
            continue
        best = max(per_village, key=lambda p: per_village[p][title])
        if per_village[best][title] / total >= 0.5:
            owner[title] = best

    out = {'generated_from': os.path.relpath(CRAWL, ROOT), 'crawled': crawl['crawled'],
           'factions': {}, 'backgrounds': {}, 'abilities': {}, 'lang': {}}
    lang = out['lang']
    used_combos = set(clans.RESERVED_COMBOS)
    for a in clan_cat['abilities'].values():
        if a.get('combo'):
            used_combos.add(tuple(x.split(':')[1] for x in a['combo']))
    kg_none = {}

    def ability_id(title):
        return clans.ALIASES.get(title) or slug(title)

    def ensure_ability(title, vid):
        aid = ability_id(title)
        if aid in existing_abilities or aid in out['abilities']:
            return aid
        d, _rank = clans.build_ability(title, jutsu[title], vid, kg_none, used_combos)
        d['name'] = text(f'ability.{NS}.{aid}', jutsu[title]['title'])
        vis = clans.visuals_for(title, clans.classes(jutsu[title]), vid, [], clans.nature_of(jutsu[title]))
        if vis:
            d['visuals'] = vis
        out['abilities'][aid] = d
        lang[f'ability.{NS}.{aid}'] = jutsu[title]['title']
        return aid

    # Shared basics are never village-owned: clan defaults, Clanless progression, background / village starting techniques.
    protected = {'substitution_jutsu', 'body_flicker_technique', 'clone_technique', 'transformation_technique'}
    for lin in clan_cat['lineages'].values():
        protected |= {x.split(':')[1] for x in lin['granted_abilities']}
        if lin.get('fallback'):
            protected |= {e['ability'].split(':')[1] for e in lin['progression'] if 'ability' in e}
    owner = {t: v for t, v in owner.items() if ability_id(t) not in protected}

    hidden_ids = [village_id(p) for p in villages if p in GAKURE]
    for page, v in sorted(villages.items()):
        vid = village_id(page)
        kind = kind_of(page)
        cap = 12 if vid in GREAT_FIVE else 8 if kind == 'hidden_village' else 6
        pool_titles = [t for t, _ in per_village[page].most_common() if owner.get(t) == page][:cap]
        pool = [ns(ensure_ability(t, vid)) for t in pool_titles]
        if not pool and kind == 'village':
            pool = [ns('transformation_technique'), ns('clone_technique')]
        exclusive_items = sorted({ns(tool_ids[t]) for t, vill in crawl['tool_villages'].items()
                                  if vill == page and t in tool_ids and t not in STANDARD_ISSUE})
        color = COLORS.get(vid, (__import__('zlib').crc32(vid.encode()) & 0x7F7F7F) + 0x404040)
        perks = PERKS.get(vid, SAGE_PERK if kind == 'sage_realm' else CIVILIAN_PERK if kind == 'village' else [])
        rep = {ns(o): s for o, s in RELATIONS.get(vid, {}).items() if o in hidden_ids}
        rep[ns(vid)] = 100
        if kind == 'hidden_village':
            weapons = [stack('kunai', 8), stack('shuriken', 16), stack('explosive_tag', 2)]
            clothing = [stack('forehead_protector', 1, badge(vid, color)), stack('flak_jacket', 1, badge(vid, color))]
        elif kind == 'sage_realm':
            weapons = [stack('kunai', 4)]
            clothing = [stack('flak_jacket', 1, badge(vid, color))]
        else:
            weapons = [stack('kunai', 2), stack('military_rations_pill', 2)]
            clothing = [stack('flak_jacket', 1, badge(vid, color))]
        summary = v['summary'] or f'{page}.'
        summary = re.sub(r'^The (is|was) ', f'{page} \\1 ', summary)
        if len(summary) > 260:
            summary = summary[:257].rsplit(' ', 1)[0] + '...'
        name = display_name(page)
        out['factions'][vid] = {
            'system': NS, 'name': text(f'faction.{NS}.{vid}', name), 'color': color, 'traits': perks,
            'starting_abilities': [ns('substitution_jutsu')] if kind == 'hidden_village' else [],
            'starting_reputation': 100, 'selectable': kind != 'sage_realm',
            'description': text(f'faction.{NS}.{vid}.desc', summary), 'kind': kind,
            'kit': {'weapons': weapons, 'clothing': clothing}, 'jutsu_pool': pool,
            'exclusive': {'items': exclusive_items, 'abilities': [], 'allied_reputation': 50, 'outsider_tier': 5},
            'reputation': rep, 'rogue': False}
        lang[f'faction.{NS}.{vid}'] = name
        lang[f'faction.{NS}.{vid}.desc'] = summary

    # ---- Missing-nin (not a wiki village page: the rogue alignment every village can fall into) ----
    rogue_rep = {ns(h): -50 for h in hidden_ids}
    rogue_rep[ns('missing_nin')] = 100
    out['factions']['missing_nin'] = {
        'system': NS, 'name': text(f'faction.{NS}.missing_nin', 'Missing-nin'), 'color': COLORS['missing_nin'],
        'traits': PERKS['missing_nin'], 'starting_abilities': [], 'starting_reputation': 100, 'selectable': True,
        'description': text(f'faction.{NS}.missing_nin.desc', 'Shinobi who abandoned their village. Free, feared, and hunted by every hidden village.'),
        'kind': 'rogue',
        'kit': {'weapons': [stack('kunai', 6), stack('shuriken', 8), stack('smoke_bomb', 2)],
                'clothing': [stack('forehead_protector', 1, badge('missing_nin', 0x37474F, True)),
                             stack('flak_jacket', 1, badge('missing_nin', COLORS['missing_nin'], True))]},
        'jutsu_pool': [], 'exclusive': {'items': [], 'abilities': [], 'allied_reputation': 50, 'outsider_tier': 5},
        'reputation': rogue_rep, 'rogue': True}
    lang[f'faction.{NS}.missing_nin'] = 'Missing-nin'
    lang[f'faction.{NS}.missing_nin.desc'] = 'Shinobi who abandoned their village. Free, feared, and hunted by every hidden village.'

    # ---- Backgrounds ----
    shinobi = [ns(h) for h in hidden_ids]

    def bg(bid, name, desc, allowed, traits, items, abilities, sp, tier, weight=10):
        out['backgrounds'][bid] = {'system': NS, 'name': text(f'background.{NS}.{bid}', name),
                                   'description': text(f'background.{NS}.{bid}.desc', desc), 'allowed_factions': allowed,
                                   'traits': traits, 'bonus_items': items, 'starting_abilities': [ns(a) for a in abilities],
                                   'starting_skill_points': sp, 'starting_tier_bonus': tier, 'selectable': True, 'weight': weight}
        lang[f'background.{NS}.{bid}'] = name
        lang[f'background.{NS}.{bid}.desc'] = desc

    bg('academy_prodigy', 'Academy Prodigy',
       'Top of your Academy class. Deep chakra reserves, a head start in rank and the textbook techniques mastered early.',
       shinobi, [capacity(clans.CHAKRA, 20), regen(clans.CHAKRA, 1.15)],
       [stack('explosive_tag', 3), stack('military_rations_pill', 2)], ['clone_technique', 'transformation_technique'], 3, 2)
    bg('shinobi_orphan', 'Shinobi Orphan',
       'Raised by nobody, you learned to survive on the streets: tougher, quicker on your feet, never without a spare blade.',
       [], [attr('max_health', 2.0), attr('movement_speed', 0.05, 'add_multiplied_base'), capacity(clans.STAMINA, 20)],
       [stack('kunai', 4), stack('military_rations_pill', 3)], ['body_flicker_technique'], 1, 0)
    bg('anbu_trainee', 'Anbu Trainee',
       'Pulled from the Academy into the black-ops program. Precise, lethal and trained in silent infiltration.',
       shinobi, [attr('attack_damage', 1.0), attr('attack_speed', 0.05, 'add_multiplied_base')],
       [stack('tanto', 1), stack('smoke_bomb', 3)], ['body_flicker_technique'], 2, 1, 6)
    bg('wandering_mercenary', 'Wandering Mercenary',
       'You sold your blade from one war-torn country to the next. Armoured, hard to kill, but with little chakra training.',
       [], [attr('armor', 2.0), attr('max_health', 4.0), capacity(clans.CHAKRA, -10)],
       [stack('sword', 1), stack('military_rations_pill', 4), stack('flash_bomb', 2)], [], 1, 0, 8)
    bg('merchant_scion', 'Merchant / Artisan Scion',
       'Heir of a trading or crafting house. Lucky, well connected, and well supplied with the finest ninja tools money can buy.',
       [], [attr('luck', 1.5)],
       [stack('minecraft:emerald', 16), stack('explosive_tag', 4), stack('smoke_bomb', 2), stack('wire_strings', 2),
        stack('blood_increasing_pill', 2)], [], 0, 0, 8)

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    pools = sum(len(f['jutsu_pool']) for f in out['factions'].values())
    print(f"factions: {len(out['factions'])}  backgrounds: {len(out['backgrounds'])}  new village jutsu: {len(out['abilities'])}  "
          f"pool entries: {pools}  exclusive items: {sum(len(f['exclusive']['items']) for f in out['factions'].values())}")
    write_doc(crawl, out)


def write_doc(crawl, cat):
    L = ['# Narutopedia `Category:Villages` coverage', '',
         f"Source: {crawl['source']} - crawled {crawl['crawled']} via the MediaWiki API (`list=categorymembers`, "
         "`cmtype=page|subcat`, recursing into ns=14), `action=parse` for every village infobox and for every character that "
         "uses a ninja tool or clan/village jutsu (their `Affiliation` field), and jutsu infoboxes for village techniques.", '',
         '## Category tree', '']
    for c, v in crawl['category_tree'].items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories")
    L += ['', 'No subcategories and no Semantic MediaWiki drill-down grouping exist for villages, so the 40 pages are the complete set. '
          '**Every page became a faction** - none were excluded (the category contains no index/list pages). '
          'One extra faction, **Missing-nin**, is not a wiki village page: it is the rogue alignment players fall into by '
          'scratching their headband (or can start in).', '',
          '## How village data was derived', '',
          '1. **Kind**: `-gakure` pages are hidden villages (shinobi gear, selectable), Mount Myōboku / Ryūchi Cave / Shikkotsu Forest '
          'are sage realms (not selectable at the start), the rest are civilian villages (selectable, no forehead protector).',
          '2. **Village jutsu pool**: techniques listed on the infoboxes of characters affiliated with the village, ranked by how many '
          'members know them; a technique belongs to the village whose members make up at least half of its users. Clan-locked '
          'techniques (hiden, kekkei genkai natures, dōjutsu) and tailed-beast techniques stay with their clans.',
          '3. **Exclusive gear**: ninja tools whose every known (village-affiliated) user comes from that one village; standard-issue '
          'supplies (kunai, shuriken, explosive tags, pills...) are never exclusive.',
          '4. **Standings**: canon alliances and rivalries of the Five Great Nations (e.g. Leaf-Sand alliance, Leaf/Cloud/Stone '
          'rivalry); every hidden village starts at -50 with missing-nin.', '',
          '## Villages', '', '| Village | id | kind | jutsu pool | exclusive gear | perks |', '|---|---|---|---:|---:|---:|']
    for page in sorted(crawl['villages']):
        vid = village_id(page)
        f = cat['factions'][vid]
        L.append(f"| [{page}](https://naruto.fandom.com/wiki/{page.replace(' ', '_')}) | `{vid}` | {f['kind']} | "
                 f"{len(f['jutsu_pool'])} | {len(f['exclusive']['items'])} | {len(f['traits'])} |")
    L.append(f"| Missing-nin (rogue alignment, not a wiki page) | `missing_nin` | rogue | 0 | 0 | {len(cat['factions']['missing_nin']['traits'])} |")
    L += ['', '## Village techniques and gear', '']
    lang = dict(cat['lang'])
    clan_lang = json.load(open(clans.OUT, encoding='utf-8'))['lang']
    lang.update(clan_lang)
    tools = {f"{NS}:{t['id']}": t['name'] for t in json.load(open(clans.TOOLS, encoding='utf-8'))}
    for vid, f in sorted(cat['factions'].items()):
        if not f['jutsu_pool'] and not f['exclusive']['items']:
            continue
        js = ', '.join(lang.get(f'ability.{NS}.' + a.split(':')[1], a) for a in f['jutsu_pool']) or '-'
        gear = ', '.join(tools.get(i, i) for i in f['exclusive']['items']) or '-'
        L.append(f"- **{lang[f'faction.{NS}.{vid}']}** - jutsu: {js}; gear: {gear}")
    L += ['', '## Backgrounds', '']
    for bid, b in cat['backgrounds'].items():
        allowed = 'any village' if not b['allowed_factions'] else 'hidden villages'
        L.append(f"- **{b['name']['fallback']}** ({allowed}): {b['description']['fallback']}")
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_villages.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutopedia_villages.md')


# ------------------------------------------------------------------ sprites (cloth tinted per village at runtime)

def build_sprites():
    from PIL import Image, ImageDraw
    res = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg', 'textures')
    item_dir = os.path.join(res, 'item')
    armor_dir = os.path.join(res, 'models', 'armor')

    def img(w=32, h=32):
        i = Image.new('RGBA', (w, h), (0, 0, 0, 0))
        return i, ImageDraw.Draw(i)

    # forehead protector: layer0 cloth band with trailing ties (tinted), layer1 steel plate with an engraved mark
    cloth, d = img()
    d.rectangle([3, 12, 28, 19], fill=(214, 214, 214, 255))
    d.line([(3, 13), (28, 13)], fill=(236, 236, 236, 255))
    d.line([(3, 18), (28, 18)], fill=(186, 186, 186, 255))
    d.polygon([(26, 17), (31, 25), (29, 26), (25, 19)], fill=(200, 200, 200, 255))
    d.polygon([(24, 18), (27, 27), (25, 28), (23, 19)], fill=(190, 190, 190, 255))
    cloth.save(os.path.join(item_dir, 'forehead_protector.png'), optimize=True)
    plate, d = img()
    d.rounded_rectangle([8, 10, 23, 21], 2, fill=(150, 160, 170, 255))
    d.rounded_rectangle([9, 11, 22, 20], 1, fill=(196, 206, 214, 255))
    d.line([(10, 11), (21, 11)], fill=(236, 242, 246, 255))
    for x, y in ((9, 12), (22, 12), (9, 19), (22, 19)):
        d.point((x, y), fill=(110, 118, 126, 255))
    d.arc([12, 12, 19, 19], 30, 330, fill=(90, 98, 108, 255))       # generic engraved village mark
    d.line([(15, 15), (19, 13)], fill=(90, 98, 108, 255))
    plate.save(os.path.join(item_dir, 'forehead_protector_overlay.png'), optimize=True)
    scratched = plate.copy()
    sd = ImageDraw.Draw(scratched)
    sd.line([(9, 13), (22, 19)], fill=(60, 62, 66, 255), width=1)
    sd.line([(9, 14), (22, 20)], fill=(232, 236, 240, 255), width=1)
    scratched.save(os.path.join(item_dir, 'forehead_protector_scratched_overlay.png'), optimize=True)

    # flak jacket: layer0 vest body (tinted), layer1 pouches / collar (untinted)
    vest, d = img()
    d.polygon([(10, 5), (14, 4), (18, 4), (22, 5), (27, 9), (27, 28), (5, 28), (5, 9)], fill=(206, 206, 206, 255))
    d.line([(16, 6), (16, 28)], fill=(176, 176, 176, 255))
    vest.save(os.path.join(item_dir, 'flak_jacket.png'), optimize=True)
    trim, d = img()
    d.polygon([(11, 4), (16, 9), (21, 4), (23, 6), (16, 12), (9, 6)], fill=(70, 74, 60, 255))
    for x0 in (7, 18):
        d.rectangle([x0, 14, x0 + 6, 19], fill=(150, 146, 120, 255))
        d.rectangle([x0, 14, x0 + 6, 15], fill=(120, 116, 94, 255))
        d.rectangle([x0, 21, x0 + 6, 25], fill=(150, 146, 120, 255))
    d.rectangle([5, 26, 27, 28], fill=(60, 62, 52, 255))
    trim.save(os.path.join(item_dir, 'flak_jacket_overlay.png'), optimize=True)

    # armor layers (64x32 biped sheet): headband = cloth band around the head (rows 4-6) + plate on the front face
    band, _ = img(64, 32)
    band_o, _ = img(64, 32)
    for x in range(0, 32):
        for y in range(11, 14):
            band.putpixel((x, y), (214, 214, 214, 255) if y != 13 else (188, 188, 188, 255))
    for x in range(10, 14):   # front plate (head front face is x 8-15, y 8-15)
        for y in range(10, 14):
            band_o.putpixel((x, y), (196, 206, 214, 255) if y > 10 else (236, 242, 246, 255))
    band_o.putpixel((9, 11), (150, 160, 170, 255))
    band_o.putpixel((14, 11), (150, 160, 170, 255))
    band.save(os.path.join(armor_dir, 'forehead_protector_layer_1.png'), optimize=True)
    band_o.save(os.path.join(armor_dir, 'forehead_protector_layer_1_overlay.png'), optimize=True)

    vest_l, _ = img(64, 32)
    vest_o, _ = img(64, 32)
    for x0, y0, x1, y1 in [(16, 20, 40, 32), (20, 16, 36, 20)]:
        for x in range(x0, x1):
            for y in range(y0, y1):
                vest_l.putpixel((x, y), (206, 206, 206, 255) if (x + y) % 5 else (190, 190, 190, 255))
    for x in range(20, 28):
        vest_o.putpixel((x, 20), (70, 74, 60, 255))
        vest_o.putpixel((x, 21), (70, 74, 60, 255) if x in (20, 21, 26, 27) else (0, 0, 0, 0))
    for x0 in (20, 24):
        for x in range(x0, x0 + 3):
            for y in range(24, 27):
                vest_o.putpixel((x, y), (150, 146, 120, 255))
    for x in range(16, 40):
        vest_o.putpixel((x, 30), (60, 62, 52, 255))
    vest_l.save(os.path.join(armor_dir, 'flak_jacket_layer_1.png'), optimize=True)
    vest_o.save(os.path.join(armor_dir, 'flak_jacket_layer_1_overlay.png'), optimize=True)
    # forged papers: folded travel papers with a red seal stamp
    pap, d = img()
    d.polygon([(6, 5), (22, 4), (26, 26), (9, 28)], fill=(236, 226, 196, 255))
    d.polygon([(6, 5), (22, 4), (23, 9), (7, 10)], fill=(214, 202, 168, 255))
    for y in (13, 16, 19):
        d.line([(10, y), (21, y - 1)], fill=(110, 100, 86, 255))
    d.ellipse([16, 20, 23, 27], fill=(178, 30, 36, 255))
    d.ellipse([18, 22, 21, 25], fill=(222, 70, 70, 255))
    pap.save(os.path.join(item_dir, 'forged_papers.png'), optimize=True)
    print('village sprites ->', item_dir)


if __name__ == '__main__':
    main()
    build_sprites()
