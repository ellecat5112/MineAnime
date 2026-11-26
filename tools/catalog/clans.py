"""Clan / bloodline catalog for NarutoRPG.

Input : docs/wiki/narutopedia_clans.json   (MediaWiki crawl of Category:Clans, see docs/wiki/narutopedia_clans.md)
        narutorpg/.../ninja_tools.json        (to link clan tools to existing ninja tool items)
Output: tools/catalog/generated/clan_catalog.json
        Every entry is already in the engine codec shape (LineageDefinition, AbilityDefinition,
        TransformationDefinition, AffinityDefinition). NarutoContentBootstrap decodes it with the codecs during
        DataGen, so a malformed entry fails the datagen run instead of a player's world.

Run:   python3 tools/catalog/clans.py      (then ./gradlew :narutorpg:runDatagen)
"""
import json
import os
import re
import unicodedata

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_clans.json')
TOOLS = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg', 'ninja_tools.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'clan_catalog.json')

NS = 'narutorpg'
SYSTEM = NS
CHAKRA = f'{NS}:chakra'
STAMINA = f'{NS}:stamina'


def ns(path):
    return f'{NS}:{path}'


def slug(text):
    t = unicodedata.normalize('NFKD', text).encode('ascii', 'ignore').decode()
    t = re.sub(r"[^A-Za-z0-9]+", '_', t).strip('_').lower()
    return re.sub(r'_+', '_', t)


def text(key, fallback):
    return {'translate': key, 'fallback': fallback}


# ------------------------------------------------------------------ natures / affinities

BASE = {'Fire Release': 'fire', 'Wind Release': 'wind', 'Lightning Release': 'lightning',
        'Earth Release': 'earth', 'Water Release': 'water'}
# advanced natures added as fusion affinities: id -> (name, color, fusion_of, strong_against, required_lineage)
ADV_STYLE = {'wood': 'plant', 'ice': 'ice', 'lava': 'lava', 'magnet': 'sand', 'storm': 'lightning', 'boil': 'water',
             'scorch': 'flame', 'explosion': 'flame', 'dust': 'light', 'yin': 'shadow', 'yang': 'light', 'mud': 'earth',
             'yin_yang': 'energy', 'crystal': 'ice', 'dark': 'shadow', 'steel': 'earth', 'swift': 'wind', 'typhoon': 'wind'}
ADVANCED = {
    'wood': ('Wood Release', 0x6D8B3A, ['earth', 'water'], ['earth'], 'senju'),
    'ice': ('Ice Release', 0xA5F2F3, ['wind', 'water'], ['water'], 'yuki'),
    'lava': ('Lava Release', 0xE65100, ['fire', 'earth'], ['wood'], None),
    'magnet': ('Magnet Release', 0xC2A26B, ['wind', 'earth'], ['wind'], 'kazekage'),
    'storm': ('Storm Release', 0x26C6DA, ['water', 'lightning'], ['earth'], None),
    'boil': ('Boil Release', 0xFFAB91, ['fire', 'water'], ['ice'], None),
    'scorch': ('Scorch Release', 0xFF7043, ['fire', 'wind'], ['ice'], None),
    'explosion': ('Explosion Release', 0xFFCA28, ['earth', 'lightning'], ['earth'], None),
    'dust': ('Dust Release', 0xECEFF1, ['earth', 'wind', 'fire'], ['wood'], None),
    'yin': ('Yin Release', 0x6A1B9A, [], ['yang'], None),
    'yang': ('Yang Release', 0xFFD54F, [], ['yin'], None),
    'mud': ('Mud Release', 0x6D4C41, ['earth', 'water'], ['lightning'], None),
    'yin_yang': ('Yin–Yang Release', 0xE0E0E0, ['yin', 'yang'], ['yin', 'yang'], None),
    'crystal': ('Crystal Release', 0xF48FB1, [], ['earth'], None),
    'dark': ('Dark Release', 0x311B92, [], ['yang'], None),
    'steel': ('Steel Release', 0x90A4AE, [], ['earth'], None),
    'swift': ('Swift Release', 0xFFF176, [], ['wind'], None),
    'typhoon': ('Typhoon Release', 0x80CBC4, [], ['fire'], None),
}
# kekkei genkai natures without a two-nature recipe awaken from training their source nature (wiki "Basic Natures"
# where listed: Crystal <- Earth, Typhoon <- Wind; lore choice otherwise: Dark <- Yin, Steel <- Earth, Swift <- Lightning)
AWAKEN = {'crystal': ('earth', 30), 'typhoon': ('wind', 30), 'dark': ('yin', 30), 'steel': ('earth', 40), 'swift': ('lightning', 30)}
NATURE_IDS = dict(BASE)
NATURE_IDS.update({v[0]: k for k, v in ADVANCED.items()})
COLORS = {'fire': 0xFF6A00, 'wind': 0xB2FF59, 'lightning': 0x80D8FF, 'earth': 0x8D6E63, 'water': 0x2979FF}
COLORS.update({k: v[1] for k, v in ADVANCED.items()})

# ------------------------------------------------------------------ hand signs (tokens with animations)

SIGNS = {'Tiger': 'tiger', 'Ram': 'ram', 'Snake': 'snake', 'Bird': 'bird', 'Boar': 'boar', 'Horse': 'horse'}
RESERVED_COMBOS = {('snake', 'ram', 'boar', 'horse', 'tiger'), ('ram', 'boar', 'snake')}  # fireball / substitution

# jutsu that already exist as hand-written abilities
ALIASES = {'Fire Release: Great Fireball Technique': 'fireball_jutsu',
           'Body Replacement Technique': 'substitution_jutsu'}

# ------------------------------------------------------------------ dōjutsu forms (transformations)

DOJUTSU = {  # kekkei genkai page -> (id, name, aura, power, speed, activation, drain, tier)
    'Byakugan': ('byakugan', 'Byakugan', 0xE8EAF6, 1.08, 0.06, 3, 0.05, 1),
    'Ketsuryūgan': ('ketsuryugan', 'Ketsuryūgan', 0xB71C1C, 1.12, 0.06, 4, 0.07, 3),
    'Rinnegan': ('rinnegan', 'Rinnegan', 0x9575CD, 1.35, 0.10, 12, 0.20, 8),
    'Tenseigan': ('tenseigan', 'Tenseigan', 0x81D4FA, 1.30, 0.10, 12, 0.20, 8),
    'Kokugan': ('kokugan', 'Kokugan', 0x212121, 1.25, 0.08, 10, 0.15, 6),
    'Jōgan': ('jogan', 'Jōgan', 0x4FC3F7, 1.15, 0.08, 6, 0.08, 4),
    'Senrigan': ('senrigan', 'Senrigan', 0xFFF59D, 1.15, 0.06, 6, 0.08, 4),
}
SHARINGAN = [('sharingan_one_tomoe', 3, 1), ('sharingan_two_tomoe', 10, 3),
             ('sharingan_three_tomoe', 20, 5), ('mangekyo_sharingan', 35, 10)]

# ------------------------------------------------------------------ per-clan design (lore -> traits)


def attr(attribute, amount, op='add_value'):
    return {'type': 'animecore:attribute', 'attribute': f'minecraft:generic.{attribute}', 'amount': amount, 'operation': op}


def power(mult, affinity=None):
    d = {'type': 'animecore:ability_power', 'multiplier': mult}
    if affinity:
        d['affinity'] = ns(affinity)
    return d


def capacity(pool, flat, mult=1.0):
    return {'type': 'animecore:energy_capacity', 'pool': pool, 'flat': flat, 'multiplier': mult}


def regen(pool, mult):
    return {'type': 'animecore:energy_regen', 'pool': pool, 'multiplier': mult}


def evasion(chance, projectiles=False):
    d = {'type': 'animecore:evasion', 'chance': chance}
    if projectiles:
        d['projectiles_only'] = True
    return d


def sense(radius, hostile=True):
    return {'type': 'animecore:sense', 'radius': radius, 'hostile_only': hostile}


def on_hit(effect, duration=60, amp=0, chance=1.0):
    return {'type': 'animecore:on_hit_effect', 'effect': f'minecraft:{effect}', 'duration': duration, 'amplifier': amp, 'chance': chance}


def passive(effect, amp=0):
    return {'type': 'animecore:passive_effect', 'effect': f'minecraft:{effect}', 'amplifier': amp}


def heal(amount, below=1.0):
    return {'type': 'animecore:regeneration', 'amount': amount, 'below_health_ratio': below}


def discount(mult, pool=CHAKRA):
    return {'type': 'animecore:energy_discount', 'pool': pool, 'multiplier': mult}


def xp_boost(mult):
    return {'type': 'animecore:skill_experience_boost', 'multiplier': mult}


def fatal_save(cooldown, heal_to, cid):
    return {'type': 'animecore:fatal_save', 'cooldown_ticks': cooldown, 'heal_to': heal_to, 'cooldown_id': ns(f'fatal_save/{cid}')}


# clan id -> color, weight, natures, traits (ITraitModifier), clan_traits (IClanTrait), selectable
CLANS = {
    'aburame': (0x4E5B31, 8, [], [regen(CHAKRA, 1.1)], [sense(16), on_hit('poison', 60, 0, 0.35)]),
    'akimichi': (0xC0392B, 8, ['yang'], [attr('max_health', 4.0), attr('knockback_resistance', 0.2), capacity(STAMINA, 30)], [heal(0.25, 0.5)]),
    'amagiri': (0x7E57C2, 6, ['lightning'], [power(1.1, 'lightning')], [evasion(0.05)]),
    'chinoike': (0x8B0000, 3, ['water', 'yin'], [power(1.1)], [on_hit('wither', 40, 0, 0.25)]),
    'funato': (0x0277BD, 8, ['water'], [attr('water_movement_efficiency', 0.5), power(1.08, 'water')], [passive('water_breathing')]),
    'fuma': (0x37474F, 7, ['wind'], [attr('attack_damage', 1.0)], [on_hit('slowness', 40, 0, 0.2)]),
    'fuma_sound': (0x5D4037, 7, ['earth', 'fire'], [attr('armor', 2.0)], [evasion(0.05)]),
    'hagoromo': (0x90A4AE, 9, [], [attr('attack_speed', 0.1, 'add_multiplied_base')], [xp_boost(1.1)]),
    'hatake': (0xB0BEC5, 5, ['lightning'], [power(1.15, 'lightning'), attr('movement_speed', 0.05, 'add_multiplied_base')], [xp_boost(1.25)]),
    'hirasaka': (0x5E35B1, 6, [], [power(1.05)], [sense(12)]),
    'hoshigaki': (0x26A69A, 5, ['water'], [attr('max_health', 2.0), capacity(CHAKRA, 40), regen(CHAKRA, 1.15)], [passive('water_breathing')]),
    'hyuga': (0xD1C4E9, 3, ['fire'], [attr('attack_damage', 1.5)], [sense(24, False), on_hit('weakness', 60, 0, 0.3)]),
    'hoki': (0x8D6E63, 8, ['wind'], [attr('jump_strength', 0.08)], [evasion(0.04)]),
    'hozuki': (0x4FC3F7, 4, ['water'], [power(1.1, 'water')], [evasion(0.12), passive('water_breathing')]),
    'iburi': (0x9E9E9E, 4, [], [attr('max_health', -2.0)], [evasion(0.15)]),
    'inuzuka': (0x795548, 8, ['earth'], [attr('movement_speed', 0.08, 'add_multiplied_base'), attr('attack_damage', 1.0)], [passive('night_vision'), sense(12)]),
    'izuno': (0x5C6BC0, 8, [], [power(1.05)], [xp_boost(1.05)]),
    'jugo': (0xFF8F00, 3, ['wind', 'earth'], [attr('attack_damage', 2.0), attr('max_health', 4.0)], [fatal_save(18000, 8.0, 'jugo'), heal(0.5, 0.5)]),
    'kagetsu': (0x81C784, 12, [], [attr('luck', 1.0)], [heal(0.25, 0.75)]),
    'kaguya': (0xEEEEEE, 3, [], [attr('armor', 4.0), attr('attack_damage', 2.0)], [on_hit('slowness', 40, 0, 0.2)]),
    'kamizuru': (0xFBC02D, 6, ['earth'], [], [on_hit('poison', 80, 0, 0.4)]),
    'karatachi': (0x546E7A, 7, ['water', 'wind'], [attr('attack_speed', 0.1, 'add_multiplied_base')], [evasion(0.05)]),
    'kazekage': (0xD7A86E, 4, ['wind', 'magnet'], [attr('armor', 2.0)], [evasion(0.10, True)]),
    'kedoin': (0x8E24AA, 8, [], [], [evasion(0.05), xp_boost(1.1)]),
    'kodon': (0x6A1B9A, 7, ['yin'], [power(1.05, 'yin')], [on_hit('nausea', 60, 0, 0.2)]),
    'kohaku': (0xFFB300, 9, [], [attr('max_health', 2.0)], [heal(0.25, 0.4)]),
    'kumanoi': (0x1E88E5, 9, ['fire'], [power(1.1, 'fire')], []),
    'kurama': (0x7B1FA2, 4, ['yin'], [power(1.1)], [on_hit('blindness', 40, 0, 0.15)]),
    'lee': (0x2E7D32, 8, [], [attr('attack_damage', 2.0), attr('movement_speed', 0.1, 'add_multiplied_base'),
                               capacity(CHAKRA, -30), capacity(STAMINA, 50), regen(STAMINA, 1.3)], [xp_boost(1.2)]),
    'nara': (0x33691E, 8, ['yin'], [regen(CHAKRA, 1.1)], [discount(0.85), xp_boost(1.1)]),
    'onikuma': (0x4E342E, 8, [], [attr('attack_damage', 1.0)], [heal(0.25, 0.3)]),
    'rinha': (0x00897B, 5, ['earth'], [attr('armor', 1.0), power(1.05, 'earth')], []),
    'ryu': (0x00695C, 6, ['water', 'wind'], [power(1.1, 'water')], [evasion(0.04)]),
    'sarutobi': (0xEF6C00, 6, ['fire', 'wind'], [power(1.1, 'fire'), capacity(CHAKRA, 20)], [xp_boost(1.15)]),
    'sendo': (0x424242, 9, [], [attr('attack_damage', 1.0)], []),
    'senju': (0x2E7D32, 3, ['earth', 'water', 'wood'], [capacity(CHAKRA, 40), attr('max_health', 4.0)], [heal(0.5, 0.6)]),
    'shiin': (0x6D4C41, 7, ['yin'], [], [sense(16)]),
    'shimura': (0x455A64, 7, ['wind'], [power(1.15, 'wind')], []),
    'shirogane': (0xCFD8DC, 6, [], [attr('attack_speed', 0.15, 'add_multiplied_base')], []),
    'taketori': (0x9CCC65, 8, [], [attr('armor', 1.0)], [sense(8)]),
    'tenro': (0x8D6E63, 6, [], [attr('movement_speed', 0.05, 'add_multiplied_base')], [evasion(0.05)]),
    'tsuchigumo': (0x6D4C41, 6, ['earth'], [attr('armor', 2.0)], [discount(0.9)]),
    'uchiha': (0xC62828, 3, ['fire'], [power(1.15, 'fire'), capacity(CHAKRA, 20), attr('attack_damage', 1.0)], [evasion(0.08)]),
    'uzumaki': (0xD32F2F, 3, ['water'], [capacity(CHAKRA, 60), regen(CHAKRA, 1.25)], [fatal_save(24000, 6.0, 'uzumaki'), heal(0.25, 0.5)]),
    'wagarashi': (0xFFA726, 12, [], [attr('luck', 1.0)], []),
    'wasabi': (0x66BB6A, 12, [], [attr('movement_speed', 0.06, 'add_multiplied_base')], []),
    'yamanaka': (0x8E24AA, 8, ['yin'], [], [sense(20, False), discount(0.9)]),
    'yoimura': (0x5C6BC0, 12, [], [attr('max_health', 2.0)], []),
    'yota': (0x29B6F6, 5, ['water'], [power(1.1, 'water')], [heal(0.25, 0.5)]),
    'yotsuki': (0xFFEB3B, 6, ['lightning'], [power(1.15, 'lightning'), attr('attack_damage', 1.0)], []),
    'yuki': (0xB3E5FC, 3, ['water', 'wind', 'ice'], [power(1.15, 'ice')], [on_hit('slowness', 60, 0, 0.35)]),
    'otsutsuki': (0xE0E0E0, 1, ['yin', 'yang'], [capacity(CHAKRA, 100), regen(CHAKRA, 1.5), attr('max_health', 6.0)],
                  [heal(0.5), sense(32), xp_boost(0.6)]),
}
NOT_SELECTABLE = {'otsutsuki'}
CLAN_ID_OVERRIDES = {'Fūma Clan (Land of Sound)': 'fuma_sound', "Jūgo's Clan": 'jugo', "Yota's Clan": 'yota'}
PREFERRED_DEFAULT = {'uchiha': 'Fire Release: Great Fireball Technique', 'nara': 'Shadow Imitation Technique',
                     'hyuga': 'Gentle Fist', 'akimichi': 'Partial Multi-Size Technique', 'yamanaka': 'Mind Body Switch Technique',
                     'inuzuka': 'Four Legs Technique', 'aburame': 'Parasitic Destruction Insect Technique',
                     'senju': 'Wood Release Secret Technique: Nativity of a World of Trees', 'uzumaki': 'Adamantine Sealing Chains'}

RANK_ORDER = {'E': 0, 'D': 1, 'C': 2, 'B': 3, 'A': 4, 'S': 5}
RANK_DAMAGE = {'E': 3, 'D': 4, 'C': 6, 'B': 8, 'A': 11, 'S': 14}
RANK_TIER = {'E': 1, 'D': 1, 'C': 1, 'B': 3, 'A': 6, 'S': 10}


def split(v):
    v = re.sub(r'<[^>]+>', '', v or '')
    return [x.strip() for x in re.split(r',\s*', v) if x.strip()]


def classes(info):
    return {x.split('~')[0].strip() for x in split(info['jutsu classification'])}


def estimated_rank(info):
    r = info['jutsu rank'].strip()[:1].upper()
    if r in RANK_ORDER:
        return r
    c = classes(info)
    if c & {'Kinjutsu', 'Shinjutsu', 'Ninshū', 'Reincarnation Ninjutsu'}:
        return 'S'
    if c & {'Senjutsu', 'Space–Time Ninjutsu', 'Kekkei Mōra'}:
        return 'A'
    if c & {'Kekkei Genkai', 'Dōjutsu', 'Cooperation Ninjutsu', 'Collaboration Techniques'}:
        return 'B'
    if 'Hiden' in c or 'Fūinjutsu' in c or 'Barrier Ninjutsu' in c:
        return 'C'
    return 'C' if 'Offensive' in info['jutsu class type'] else 'D'


def effect(name, duration=100, amp=0):
    return {'effect': f'minecraft:{name}', 'duration': duration, 'amplifier': amp}


def nature_of(info):
    for part in split(info['jutsu type']):
        if part in NATURE_IDS:
            return NATURE_IDS[part]
    return None


# Attack words in a technique's name. Many wiki infoboxes leave out the "Offensive" class type, which used to turn
# attacks like Big Flame Bullet or Earth Spear into a harmless speed buff.
ATTACK_NAME = re.compile(r'bullet|spear|blast|bomb|cannon|rasen|chidori|raikiri|drill|missile|slash|strike|fist(?! prison)|'
                         r'kick|punch|lariat|explo|crush|burial|decapitat|needle|arrow|fang|shot|blade|sword|katana|odachi|ōdachi|'
                         r'incinerat|fireball|flame|dragon|cutter|scythe|lance|stake|bullet|barrage|meteor|bolt|thunder|'
                         r'hammer|smash|tornado|vortex|typhoon|wave|storm|shark|wolf|tiger|lion|hound|beast|kunai|shuriken|'
                         r'senbon|wheel|cutting|piercing|severing|destruction|annihilat|breaker|impact|dance|assault|rampage|'
                         r'great ball|bijū|tailed beast ball|jet|gun|rain|hail|torrent|whirl|split|tearing|pillar|spike|thorn|horn|'
                         r'laser|roar|yellow flash|gravel|landslide|archerfish|water arm|rush|locust|pulveris|lightning flash|sea of trees|earth drop',
                         re.I)
BIND_NAME = re.compile(r'prison|imprison|binding|bind|swamp|antlion|coffin|entomb|capture|capturing|restrain|cage|stocks|rope|mud hole|threads', re.I)
UTILITY_NAME = re.compile(r'hiding|tunnel|projection|spying|corridor|transformation|clone|substitution|'
                          r'ultralight|weight|jump|transmission|mode|luster|armou?r|wall|dome|shield|barrier|shelter|rampart|defen', re.I)


def build_ability(title, info, clan_id, kg_of_jutsu, used_combos):
    c = classes(info)
    types = set(split(info['jutsu class type']))
    ranges = set(split(info['jutsu range']))
    nature = nature_of(info)
    rank = estimated_rank(info)
    dmg = float(RANK_DAMAGE[rank])
    color = COLORS.get(nature, 0xC62828 if 'Dōjutsu' in c else 0x90CAF9)
    jtype = info['jutsu type']
    lt = title.lower()
    named_attack = bool(ATTACK_NAME.search(lt)) and not UTILITY_NAME.search(lt)
    offensive = 'Offensive' in types or (named_attack and 'Defensive' not in types)
    defensive = 'Defensive' in types and not offensive
    binding = bool(BIND_NAME.search(lt))
    actions = []
    cast = 'instant'
    anim = None

    def proj(speed=1.3, size=0.6, aoe=0.0, count=1, spread=0.0, life=40, fire=0):
        # every shot of a volley deals its own damage (ability hits ignore the hurt cooldown), so a volley that lands
        # completely deals about twice a single shot
        shot = dmg if count == 1 else round(dmg * 2.0 / count, 1)
        d = {'type': 'animecore:projectile', 'speed': speed, 'damage': shot, 'size': size, 'aoe_radius': aoe,
             'fire_ticks': fire, 'count': count, 'spread_degrees': spread, 'lifetime_ticks': life, 'color': color}
        if nature == 'fire':
            d['trail_particle'] = 'minecraft:flame'
        elif nature in ('water', 'ice', 'storm', 'boil'):
            d['trail_particle'] = 'minecraft:splash'
        elif nature in ('wind', 'scorch', 'dust'):
            d['trail_particle'] = 'minecraft:cloud'
        return d

    fire_ticks = 60 if nature in ('fire', 'lava', 'scorch') else 0

    if 'Genjutsu' in c or (clan_id == 'kurama'):
        actions.append({'type': 'animecore:area_effect', 'radius': 10.0, 'cone_degrees': 70.0, 'damage': dmg * 0.4, 'color': 0x8E24AA,
                        'effects': [effect('nausea', 120), effect('slowness', 80, 1), effect('darkness', 80)]})
    elif 'Dōjutsu' in c and not offensive:
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('night_vision', 400)]})
        actions.append({'type': 'animecore:area_effect', 'radius': 24.0, 'color': color,
                        'effects': [effect('glowing', 200)]})
    elif (c & {'Medical Ninjutsu', 'Regeneration Techniques'} and not offensive) or 'heal' in lt or 'mystical palm' in lt:
        actions.append({'type': 'animecore:self_buff', 'heal': 6.0, 'effects': [effect('regeneration', 120, 1)]})
    elif 'Space–Time Ninjutsu' in c and offensive and re.search(r'thunder god|teleport|flicker|flash|instant|warp', lt):
        # teleport strikes (Flying Thunder God): blink up to 18 blocks and hit everything along the way
        actions.append({'type': 'animecore:dash_strike', 'mode': 'look', 'distance': 18.0, 'damage': dmg, 'hit_radius': 1.6, 'knockback': 1.2})
    elif 'Space–Time Ninjutsu' in c and not offensive:
        actions.append({'type': 'animecore:dash_strike', 'mode': 'look', 'distance': 14.0, 'damage': 0.0, 'hit_radius': 1.0, 'knockback': 0.0})
    elif 'Clone Techniques' in c:
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('invisibility', 60), effect('speed', 80, 1)]})
        actions.append({'type': 'animecore:area_effect', 'radius': 5.0, 'damage': dmg * 0.5 if offensive else 0.0, 'color': 0xFFFFFF,
                        'effects': [effect('blindness', 30)]})
    elif 'Chakra Absorption Techniques' in c:
        actions.append({'type': 'animecore:area_effect', 'radius': 5.0, 'damage': dmg * 0.6, 'color': 0x7E57C2,
                        'effects': [effect('weakness', 100), effect('hunger', 100, 1)]})
        actions.append({'type': 'animecore:self_buff', 'heal': 4.0})
    elif defensive or c & {'Barrier Ninjutsu'} or 'wall' in lt or 'dome' in lt or 'shield' in lt or 'rotation' in lt:
        if 'rotation' in lt or 'heavenly spin' in lt:
            actions.append({'type': 'animecore:aoe_sphere', 'radius': 4.0, 'damage': dmg * 0.6, 'knockback': 1.6, 'color': color})
        else:
            kind = {'earth': 'stone', 'wood': 'stone', 'lava': 'stone', 'ice': 'mirror', 'water': 'crystal',
                    'magnet': 'stone', 'storm': 'crystal'}.get(nature, 'barrier')
            actions.append({'type': 'animecore:raise_wall', 'kind': kind, 'width': 5, 'height': 3, 'distance': 2,
                            'duration_ticks': 120 + int(dmg) * 10})
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('resistance', 100, 1 if rank in 'ABS' else 0)]})
    elif c & {'Taijutsu', 'Fighting Style', 'Fighting style', 'Kenjutsu', 'Bukijutsu'} and offensive:
        actions.append({'type': 'animecore:dash_strike', 'mode': 'look', 'distance': 6.0, 'damage': dmg, 'hit_radius': 1.4, 'knockback': 1.0})
        if rank in 'ABS':
            actions.append({'type': 'animecore:aoe_sphere', 'radius': 3.0, 'damage': dmg * 0.5, 'knockback': 1.2,
                            'forward_offset': 1.5, 'color': color})
    elif binding and not defensive:
        # prisons, swamps and binds: crush and hold every target in front of the caster
        actions.append({'type': 'animecore:area_effect', 'radius': 8.0, 'cone_degrees': 80.0, 'damage': dmg, 'color': color,
                        'effects': [effect('slowness', 100, 4), effect('mining_fatigue', 100, 1)]})
    elif (c & {'Taijutsu', 'Fighting Style', 'Fighting style', 'Senjutsu'} and not offensive) or 'gate' in lt or 'mode' in lt:
        amp = 1 if rank in 'ABS' else 0
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('strength', 300, amp), effect('speed', 300, amp),
                                                                  effect('resistance', 300, 0)]})
    elif 'Shurikenjutsu' in c:
        actions.append(proj(speed=2.0, size=0.3, count=5, spread=8.0, life=30))
    elif c & {'Fūinjutsu', 'Juinjutsu'} and offensive or 'chain' in lt or 'seal' in lt and offensive:
        actions.append({'type': 'animecore:area_effect', 'radius': 7.0, 'cone_degrees': 50.0, 'damage': dmg * 0.6, 'pull': 1.5,
                        'color': 0xFFD54F, 'effects': [effect('slowness', 100, 3), effect('mining_fatigue', 100, 1)]})
    elif clan_id == 'aburame' or 'insect' in lt or 'bug' in lt or clan_id == 'kamizuru' or 'bee' in lt:
        actions.append({'type': 'animecore:area_effect', 'radius': 8.0, 'cone_degrees': 90.0, 'damage': dmg * 0.5, 'color': 0x33691E,
                        'effects': [effect('poison', 100), effect('weakness', 100)]})
    elif nature == 'yin' and (clan_id in ('nara', 'yamanaka', 'shiin', 'kodon') or 'shadow' in lt or 'mind' in lt):
        if 'mind' in lt:
            actions.append({'type': 'animecore:area_effect', 'radius': 12.0, 'cone_degrees': 25.0, 'damage': dmg * 0.4, 'color': 0x8E24AA,
                            'effects': [effect('nausea', 160), effect('slowness', 100, 4), effect('blindness', 60)]})
        else:
            actions.append({'type': 'animecore:area_effect', 'radius': 10.0, 'cone_degrees': 60.0, 'damage': dmg * 0.4 if offensive else 0.0,
                            'pull': 0.0, 'color': 0x212121, 'effects': [effect('slowness', 100, 5), effect('mining_fatigue', 100, 2)]})
    elif nature == 'yang' and not offensive:
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('strength', 300, 1), effect('resistance', 300, 1),
                                                                  effect('health_boost', 300, 1)]})
    elif offensive:
        if ranges and ranges <= {'Short'}:
            actions.append({'type': 'animecore:aoe_sphere', 'radius': 3.0 + (1.0 if rank in 'ABS' else 0.0), 'damage': dmg,
                            'knockback': 0.8, 'fire_ticks': fire_ticks, 'forward_offset': 2.0, 'color': color})
        elif nature in ('lightning', 'storm') and 'Long' in ranges:
            actions.append({'type': 'animecore:beam_raycast', 'range': 28.0, 'damage': dmg, 'width': 0.6, 'pierce': True,
                            'fire_ticks': 0, 'color': color})
        elif 'Dōjutsu' in c and nature in (None, 'fire'):
            actions.append({'type': 'animecore:beam_raycast', 'range': 24.0, 'damage': dmg, 'width': 0.5, 'pierce': False,
                            'fire_ticks': 100 if 'amaterasu' in lt or nature == 'fire' else 0, 'color': 0x212121 if 'amaterasu' in lt else color})
        else:
            big = rank in 'ABS'
            life = 25 if 'Long' not in ranges else 50
            actions.append(proj(speed=1.1 if big else 1.4, size=1.0 if big else 0.6, aoe=3.0 if big else 1.5, life=life, fire=fire_ticks))
            if big:
                cast = 'charged'
            if nature in ('fire', 'water', 'wind') and 'Release' in jtype:
                anim = ns('fireball_exhale')
    elif re.search(r'sens|mind.s eye|echo|lock-on|vision|spying|intelligence|transmission|recording|communication|detect|search', lt):
        # sensing: reveal everything nearby
        actions.append({'type': 'animecore:area_effect', 'radius': 32.0, 'color': 0x81D4FA, 'effects': [effect('glowing', 300)]})
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('night_vision', 300)]})
    elif re.search(r'hiding|camouflage|invisib|transparent|conceal|disguise|escape|vanish|fog|mist|smoke|shifting body', lt):
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('invisibility', 160), effect('speed', 160, 1)]})
    elif re.search(r'trap|web|spit|paraly|cocoon|mesh|stick|flatness|honey|net\b', lt):
        actions.append({'type': 'animecore:area_effect', 'radius': 7.0, 'cone_degrees': 70.0, 'damage': dmg * 0.5, 'color': color,
                        'effects': [effect('slowness', 120, 5), effect('mining_fatigue', 120, 1)]})
    elif re.search(r'possess|mind|soul|curse|memory|puppet|manipulat|control|replacement|psycho|melody|flute', lt):
        actions.append({'type': 'animecore:area_effect', 'radius': 10.0, 'cone_degrees': 40.0, 'damage': dmg * 0.4, 'color': 0x8E24AA,
                        'effects': [effect('nausea', 140), effect('slowness', 100, 3), effect('weakness', 100, 1)]})
    elif re.search(r'reincarnation|immortal|\blife\b|revive|resurrect|heart', lt):
        actions.append({'type': 'animecore:self_buff', 'heal': 10.0, 'effects': [effect('regeneration', 200, 1), effect('absorption', 400, 1)]})
    elif re.search(r'seal|sealing|suppress|release technique|releasing|barrier|unite|contract', lt):
        actions.append({'type': 'animecore:area_effect', 'radius': 6.0, 'color': 0xFFD54F,
                        'effects': [effect('weakness', 160, 1), effect('mining_fatigue', 160, 1), effect('slowness', 60, 1)]})
    elif re.search(r'arm|body|growth|alteration|modification|form\b|cloak|entity|materiali|armour|staff', lt):
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('strength', 300, 1), effect('resistance', 300, 0)]})
    else:  # supplementary fallback
        actions.append({'type': 'animecore:self_buff', 'effects': [effect('speed', 200, 0), effect('jump_boost', 200, 0)]})

    amount = round(max(10.0, dmg * 3.0), 1)
    cost = {'pool': CHAKRA if clan_id != 'lee' or 'Taijutsu' not in c else STAMINA, 'amount': amount}
    if cast == 'charged':
        cost['charge_drain_per_tick'] = 0.25
    combo = [SIGNS[s] for s in split(info['hand signs']) if s in SIGNS]
    combo_t = tuple(combo)
    if len(combo) < 2 or combo_t in used_combos:
        combo = []
    else:
        used_combos.add(combo_t)
    d = {'system': SYSTEM, 'name': None, 'cast_type': cast, 'cost': cost,
         'cooldown_ticks': int(60 + dmg * 12), 'required_tier': RANK_TIER[rank], 'actions': actions}
    if combo:
        d['combo'] = [ns(s) for s in combo]
    if cast == 'charged':
        d['max_charge_ticks'] = 40
    if nature:
        d['affinity'] = ns(nature)
    if anim:
        d['cast_animation'] = anim
    kgs = kg_of_jutsu.get(title, [])
    # nature-release jutsu sit on their release token automatically (affinity); the rest by school.
    # Genjutsu and dojutsu techniques always go on their own tokens (Amaterasu is a Dojutsu technique, not Fire Release).
    tok = token_for(c, kgs)
    if tok and (not nature or tok in ('genjutsu', 'dojutsu')):
        d['token'] = ns(tok)
    vis = visuals_for(title, c, clan_id, kgs, nature)
    if vis:
        d['visuals'] = vis
    if 'Mangekyō Sharingan' in kgs and 'Sharingan' not in kgs:
        d['required_transformation'] = ns('mangekyo_sharingan')
    return d, rank


DOJUTSU_KG = {'Sharingan', 'Mangekyō Sharingan', 'Byakugan', 'Rinnegan', 'Tenseigan', 'Ketsuryūgan', 'Kokugan', 'Jōgan', 'Senrigan'}


def token_for(c, kgs):
    """Technique token (item) for a jutsu without a nature release; None = the default Ninjutsu token."""
    if 'Genjutsu' in c:
        return 'genjutsu'
    if 'Dōjutsu' in c or set(kgs) & DOJUTSU_KG:
        return 'dojutsu'
    if c & {'Medical Ninjutsu', 'Regeneration Techniques'}:
        return 'medical_ninjutsu'
    if c & {'Fūinjutsu', 'Juinjutsu', 'Barrier Ninjutsu'}:
        return 'fuinjutsu'
    if c & {'Taijutsu', 'Fighting Style', 'Fighting style', 'Kenjutsu', 'Bukijutsu', 'Shurikenjutsu'}:
        return 'taijutsu'
    if 'Hiden' in c:
        return 'hiden'
    return None


def visuals_for(title, c, clan_id, kgs, nature):
    """Elemental look override; abilities without one are styled from their affinity by the engine."""
    lt = title.lower()
    v = {}
    if 'Genjutsu' in c or clan_id == 'kurama':
        v = {'style': 'illusion', 'screen_effect': 'illusion'}
        if 'Sharingan' in kgs or 'Mangekyō Sharingan' in kgs or 'sharingan' in lt:
            v['color'] = 0xC62828
    elif 'amaterasu' in lt or 'blaze release' in lt:
        v = {'style': 'flame', 'color': 0x1A1A1A}
    elif 'Sharingan' in kgs or 'Mangekyō Sharingan' in kgs or 'susanoo' in lt:
        v = {'style': 'illusion', 'color': 0xC62828}
    elif 'Byakugan' in kgs or 'Tenseigan' in kgs or 'Jōgan' in kgs:
        v = {'style': 'light'}
    elif 'Rinnegan' in kgs or 'Kokugan' in kgs or 'Space–Time Ninjutsu' in c:
        v = {'style': 'space'}
    elif 'Ketsuryūgan' in kgs or clan_id == 'chinoike' or 'blood' in lt:
        v = {'style': 'blood'}
    elif clan_id in ('aburame',) or 'insect' in lt or 'bug' in lt:
        v = {'style': 'insect'}
    elif clan_id == 'kamizuru' or 'bee' in lt or 'poison' in lt or 'venom' in lt:
        v = {'style': 'poison'}
    elif 'Shikotsumyaku' in kgs:
        v = {'style': 'light', 'color': 0xF5F5F5}
    elif 'Clone Techniques' in c or 'transformation technique' in lt or 'smoke' in lt:
        v = {'style': 'smoke'}
    elif c & {'Medical Ninjutsu', 'Regeneration Techniques'} or 'mystical palm' in lt:
        v = {'style': 'light', 'color': 0x76FF03}
    elif c & {'Fūinjutsu', 'Juinjutsu', 'Barrier Ninjutsu'} and nature is None:
        v = {'style': 'light', 'color': 0xFFD54F}
    elif 'gate' in lt and clan_id == 'lee':
        v = {'style': 'energy', 'color': 0x43A047}
    elif c & {'Taijutsu', 'Fighting Style', 'Fighting style'} and nature is None:
        v = {'style': 'wind'}
    elif 'Senjutsu' in c and nature is None:
        v = {'style': 'energy', 'color': 0xFF9800}
    elif clan_id == 'otsutsuki' and nature is None:
        v = {'style': 'space'}
    return v


def power_score(rank, info):
    return RANK_ORDER[rank] * 10 + len(classes(info))


def main():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    tools = json.load(open(TOOLS, encoding='utf-8'))
    tool_ids = {}
    for t in tools:
        tool_ids[t['wiki']] = t['id']
        tool_ids[t['name']] = t['id']
    jutsu = crawl['jutsu']
    kg_of_jutsu = {}
    for kg, v in crawl['kekkei_genkai'].items():
        for j in v['jutsu']:
            kg_of_jutsu.setdefault(j, []).append(kg)

    # generic tools (used by many clans) are not clan signature items
    from collections import Counter
    tool_freq = Counter(t for c in crawl['clans'].values() for t in set(c['member_tools']) if t in tool_ids)

    out = {'generated_from': os.path.relpath(CRAWL, ROOT), 'crawled': crawl['crawled'],
           'affinities': {}, 'transformations': {}, 'abilities': {}, 'lineages': {}, 'lang': {}}
    lang = out['lang']

    for aid, (name, color, fusion, strong, lineage) in ADVANCED.items():
        a = {'system': SYSTEM, 'name': text(f'affinity.{NS}.{aid}', name), 'color': color,
             'strong_against': [ns(s) for s in strong], 'advantage_multiplier': 1.5, 'fusion_of': [ns(f) for f in fusion],
             'vfx_style': ADV_STYLE[aid]}
        if lineage:
            a['required_lineage'] = ns(lineage)
        if aid in AWAKEN:
            a['awaken_from'], a['awaken_level'] = ns(AWAKEN[aid][0]), AWAKEN[aid][1]
        out['affinities'][aid] = a
        lang[f'affinity.{NS}.{aid}'] = name

    for kg, (tid, name, aura, pw, speed, act, drain, tier) in DOJUTSU.items():
        out['transformations'][tid] = {
            'system': SYSTEM, 'name': text(f'transformation.{NS}.{tid}', name), 'pool': CHAKRA,
            'activation_cost': float(act), 'drain_per_tick': drain, 'cooldown_ticks': 100, 'required_tier': tier,
            'modifiers': [power(pw), attr('movement_speed', speed, 'add_multiplied_base')],
            'visuals': {'aura_color': aura, 'aura_intensity': 0.5}}
        lang[f'transformation.{NS}.{tid}'] = name

    used_combos = set(RESERVED_COMBOS)
    abilities = out['abilities']
    ranks = {}

    def ability_id(title):
        return ALIASES.get(title) or slug(title)

    clan_records = []
    for clan_title, c in sorted(crawl['clans'].items()):
        cid = CLAN_ID_OVERRIDES.get(clan_title) or slug(re.sub(r"\s+(Clan|Family)$", '', clan_title))
        clan_records.append((cid, clan_title, c))

    for cid, clan_title, c in clan_records:
        for title in c['jutsu']:
            aid = ability_id(title)
            if title in ALIASES or aid in abilities:
                ranks.setdefault(aid, (estimated_rank(jutsu[title]), jutsu[title]))
                continue
            d, rank = build_ability(title, jutsu[title], cid, kg_of_jutsu, used_combos)
            d['name'] = text(f'ability.{NS}.{aid}', jutsu[title]['title'])
            abilities[aid] = d
            ranks[aid] = (rank, jutsu[title])
            lang[f'ability.{NS}.{aid}'] = jutsu[title]['title']

    for cid, clan_title, c in clan_records:
        color, weight, natures, traits, clan_traits = CLANS[cid]
        # Sharingan-line jutsu need the Uchiha-only Sharingan forms; other clans sharing the kekkei genkai page skip them.
        titles = [t for t, src in c['jutsu'].items()
                  if cid == 'uchiha' or src not in ('kg:Sharingan', 'kg:Mangekyō Sharingan')]
        ids = []
        for t in titles:
            aid = ability_id(t)
            if aid not in ids:
                ids.append(aid)
        pref = PREFERRED_DEFAULT.get(cid)
        if pref and ability_id(pref) in ids:
            default = ability_id(pref)
        else:
            # lowest rank first, clan techniques before kekkei genkai ones, then wiki order
            order = {aid: i for i, aid in enumerate(ids)}
            src = {ability_id(t): s for t, s in c['jutsu'].items()}
            default = min(ids, key=lambda a: (RANK_ORDER[ranks[a][0]], 0 if src.get(a) in ('clan', 'curated') else 1, order[a]))
        rest = sorted([a for a in ids if a != default], key=lambda a: (power_score(*ranks[a]), ids.index(a)))
        step = min(5.0, 48.0 / max(1, len(rest)))
        progression = [{'skill_level': 2 + int(i * step), 'ability': ns(a)} for i, a in enumerate(rest)]
        forms = []
        if cid == 'uchiha':
            forms = [{'skill_level': lvl, 'transformation': ns(t), 'required_tier': tier} for t, lvl, tier in SHARINGAN]
        else:
            base_level = {0: 2, 1: 12, 2: 20, 3: 28, 4: 34, 5: 40}
            n = 0
            for kg in c['kekkei_genkai']:
                if kg in DOJUTSU:
                    tid, *_rest, tier = DOJUTSU[kg]
                    forms.append({'skill_level': base_level.get(n, 40), 'transformation': ns(tid), 'required_tier': tier})
                    n += 1
        # jutsu of a dojutsu never unlock before the eye itself (Amaterasu after the Mangekyo, Rinnegan jutsu after the Rinnegan)
        form_level = {e['transformation']: e['skill_level'] for e in forms}
        kg_form = {kg: ns(v[0]) for kg, v in DOJUTSU.items()}
        kg_form['Sharingan'] = ns('sharingan_one_tomoe')
        kg_form['Mangekyō Sharingan'] = ns('mangekyo_sharingan')
        title_of = {ability_id(t): t for t in titles}
        for e in progression:
            for kg in kg_of_jutsu.get(title_of.get(e['ability'].split(':')[1], ''), []):
                lvl = form_level.get(kg_form.get(kg))
                if lvl and e['skill_level'] < lvl:
                    e['skill_level'] = lvl
        progression = sorted(progression + forms, key=lambda e: (e['skill_level'], 'transformation' not in e))
        max_level = max([50] + [e['skill_level'] for e in progression])

        sig = [t for t in c['member_tools'] if t in tool_ids and tool_freq[t] <= 6]
        clan_items = [ns(tool_ids[t]) for t in sig][:8]
        if not clan_items:
            gen = [t for t in c['member_tools'] if t in tool_ids]
            clan_items = [ns(tool_ids[t]) for t in gen[:1]]

        name = re.sub(r'\s*\(Land of Sound\)', ' (Sound)', clan_title)
        desc = c['summary'] or f'The {clan_title}.'
        if len(desc) > 220:
            desc = desc[:217].rsplit(' ', 1)[0] + '...'
        affin = [ns(n) for n in natures]
        rec = {'system': SYSTEM, 'name': text(f'lineage.{NS}.{cid}', name),
               'description': text(f'lineage.{NS}.{cid}.desc', desc),
               'innate_affinities': affin, 'traits': traits, 'granted_abilities': [ns(default)],
               'selectable': cid not in NOT_SELECTABLE, 'weight': weight, 'color': color,
               'progression': progression, 'clan_traits': clan_traits, 'clan_items': clan_items,
               'max_skill_level': max_level}
        out['lineages'][cid] = rec
        lang[f'lineage.{NS}.{cid}'] = name
        lang[f'lineage.{NS}.{cid}.desc'] = desc

    # Clanless fallback: academy basics.
    basics = ['clone_technique', 'transformation_technique', 'body_flicker_technique', 'substitution_jutsu', 'shadow_clone_technique']
    missing = [b for b in basics if b not in abilities and b != 'substitution_jutsu']
    assert not missing, missing
    out['lineages']['clanless'] = {
        'system': SYSTEM, 'name': text(f'lineage.{NS}.clanless', 'Clanless'),
        'description': text(f'lineage.{NS}.clanless.desc', 'No bloodline - only the academy basics and hard work.'),
        'innate_affinities': [], 'traits': [], 'granted_abilities': [ns(basics[0])], 'selectable': False, 'weight': 0,
        'color': 0x9E9E9E, 'progression': [{'skill_level': 2 + 3 * i, 'ability': ns(b)} for i, b in enumerate(basics[1:])],
        'clan_traits': [xp_boost(1.25)], 'clan_items': [], 'max_skill_level': 50, 'fallback': True}
    lang[f'lineage.{NS}.clanless'] = 'Clanless'
    lang[f'lineage.{NS}.clanless.desc'] = 'No bloodline - only the academy basics and hard work.'

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    print(f"clans: {len(out['lineages'])}  abilities: {len(abilities)}  forms: {len(out['transformations'])}  "
          f"affinities: {len(out['affinities'])}  combos: {len(used_combos) - len(RESERVED_COMBOS)}")


if __name__ == '__main__':
    main()


# ------------------------------------------------------------------ sprites (grayscale, tinted per clan at runtime)

def build_sprites():
    from PIL import Image, ImageDraw
    import math
    import random
    res = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg', 'textures')
    item_dir = os.path.join(res, 'item')
    armor_dir = os.path.join(res, 'models', 'armor')
    os.makedirs(item_dir, exist_ok=True)
    os.makedirs(armor_dir, exist_ok=True)
    rnd = random.Random(52)

    def img(w=32, h=32):
        i = Image.new('RGBA', (w, h), (0, 0, 0, 0))
        return i, ImageDraw.Draw(i)

    # Emblem: layer0 = bronze frame + cord (untinted), layer1 = inner disc (tinted with the clan colour)
    frame, d = img()
    d.ellipse([3, 4, 28, 29], fill=(92, 64, 38, 255))
    d.ellipse([4, 5, 27, 28], fill=(176, 132, 72, 255))
    d.ellipse([6, 7, 25, 26], fill=(110, 78, 44, 255))
    d.arc([4, 5, 27, 28], 200, 300, fill=(232, 196, 120, 255))
    d.rectangle([14, 0, 17, 4], fill=(150, 30, 30, 255))
    d.line([(15, 0), (15, 3)], fill=(210, 70, 60, 255))
    frame.save(os.path.join(item_dir, 'clan_emblem.png'), optimize=True)
    disc, d = img()
    d.ellipse([7, 8, 24, 25], fill=(200, 200, 200, 255))
    d.ellipse([9, 10, 22, 23], fill=(232, 232, 232, 255))
    # stylised clan crest: three-comma swirl
    cx, cy = 15.5, 16.5
    for k in range(3):
        a = k * 2 * math.pi / 3
        x, y = cx + math.cos(a) * 3.6, cy + math.sin(a) * 3.6
        d.ellipse([x - 1.8, y - 1.8, x + 1.8, y + 1.8], fill=(120, 120, 120, 255))
        tx, ty = cx + math.cos(a + 0.9) * 5.2, cy + math.sin(a + 0.9) * 5.2
        d.line([(x, y), (tx, ty)], fill=(120, 120, 120, 255), width=1)
    d.ellipse([cx - 1.3, cy - 1.3, cx + 1.3, cy + 1.3], fill=(150, 150, 150, 255))
    d.arc([9, 10, 22, 23], 190, 280, fill=(255, 255, 255, 255))
    disc.save(os.path.join(item_dir, 'clan_emblem_overlay.png'), optimize=True)

    # Garb icon: layer0 = cloth (tinted), layer1 = collar, sash and trim (untinted)
    cloth, d = img()
    body = [(9, 7), (13, 5), (19, 5), (23, 7), (29, 12), (26, 16), (23, 14), (23, 28), (9, 28), (9, 14), (6, 16), (3, 12)]
    d.polygon(body, fill=(214, 214, 214, 255))
    for y in range(8, 28, 3):
        d.line([(10, y), (22, y)], fill=(196, 196, 196, 255))
    d.line([(16, 9), (16, 27)], fill=(184, 184, 184, 255))
    cloth.save(os.path.join(item_dir, 'clan_garb.png'), optimize=True)
    trim, d = img()
    d.polygon([(12, 5), (16, 10), (20, 5), (19, 4), (16, 7), (13, 4)], fill=(40, 40, 46, 255))
    d.rectangle([9, 20, 23, 22], fill=(44, 44, 52, 255))
    d.line([(9, 21), (23, 21)], fill=(90, 90, 104, 255))
    d.line([(3, 12), (6, 16)], fill=(40, 40, 46, 255))
    d.line([(29, 12), (26, 16)], fill=(40, 40, 46, 255))
    d.line([(9, 28), (23, 28)], fill=(40, 40, 46, 255))
    trim.save(os.path.join(item_dir, 'clan_garb_overlay.png'), optimize=True)

    # Bloodline scroll: crimson-sealed scroll with a blood drop
    sc, d = img()
    d.rounded_rectangle([5, 9, 26, 22], 2, fill=(236, 222, 186, 255))
    d.rectangle([5, 9, 26, 10], fill=(196, 176, 132, 255))
    d.rectangle([3, 8, 6, 23], fill=(120, 72, 40, 255))
    d.rectangle([25, 8, 28, 23], fill=(120, 72, 40, 255))
    d.rectangle([4, 7, 5, 24], fill=(80, 46, 24, 255))
    d.rectangle([26, 7, 27, 24], fill=(80, 46, 24, 255))
    d.rectangle([14, 9, 17, 22], fill=(170, 24, 32, 255))
    d.polygon([(15.5, 12), (13.2, 16.5), (15.5, 19), (17.8, 16.5)], fill=(220, 40, 48, 255))
    d.point((15, 15), fill=(255, 140, 140, 255))
    for y in (13, 16, 19):
        d.line([(8, y), (12, y)], fill=(90, 80, 70, 255))
        d.line([(19, y), (23, y)], fill=(90, 80, 70, 255))
    sc.save(os.path.join(item_dir, 'bloodline_scroll.png'), optimize=True)

    # Armor layer (64x32, vanilla biped UV): cloth body + arms (dyed) and an undyed collar / sash / cuffs overlay.
    def cloth_px(x, y):
        v = 206 + (8 if (x + y) % 4 == 0 else 0) - (10 if y % 3 == 0 else 0) + rnd.randint(-4, 4)
        return (v, v, v, 255)
    layer, _ = img(64, 32)
    overlay, od = img(64, 32)
    regions = [(16, 20, 40, 32), (20, 16, 36, 20), (40, 20, 56, 32), (44, 16, 52, 20)]
    for x0, y0, x1, y1 in regions:
        for x in range(x0, x1):
            for y in range(y0, y1):
                layer.putpixel((x, y), cloth_px(x, y))
    dark, mid = (38, 38, 46, 255), (88, 88, 100, 255)
    for x in range(20, 28):              # collar (front)
        overlay.putpixel((x, 20), dark)
    for x in (22, 23, 24, 25):
        overlay.putpixel((x, 21), dark)
    for x in range(32, 40):              # collar (back)
        overlay.putpixel((x, 20), dark)
    for x in range(16, 40):              # sash around the waist
        overlay.putpixel((x, 28), dark)
        overlay.putpixel((x, 29), mid)
    for x in range(40, 56):              # sleeve cuffs
        overlay.putpixel((x, 31), dark)
    layer.save(os.path.join(armor_dir, 'clan_garb_layer_1.png'), optimize=True)
    overlay.save(os.path.join(armor_dir, 'clan_garb_layer_1_overlay.png'), optimize=True)
    print('clan sprites ->', item_dir)


if __name__ == '__main__' and os.environ.get('CLAN_SPRITES', '1') == '1':
    build_sprites()


# ------------------------------------------------------------------ coverage doc (CLAUDE.md wiki rule)

def write_doc():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    cat = json.load(open(OUT, encoding='utf-8'))
    tree = crawl['category_tree']
    L = ['# Narutopedia `Category:Clans` coverage', '',
         f"Source: {crawl['source']} - crawled {crawl['crawled']} via the MediaWiki API "
         "(`action=query&list=categorymembers&cmtype=page|subcat`, recursing into ns=14), then `action=parse` for every clan, "
         "kekkei genkai and clan-member infobox and `prop=revisions` for every jutsu infobox.", '',
         '## Category tree', '']
    for c, v in tree.items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories")
    L += ['', 'The category has **no subcategories** and no Semantic MediaWiki drill-down grouping (the clan infobox exposes no '
          'classification filter), so the 52 member pages are the complete set. **Every page became a playable clan**; no clan '
          'page was excluded (there are no index/list pages in this category).', '',
          '## How clan data was derived', '',
          '1. **Clan jutsu** = the clan infobox `Jutsu` list, plus every jutsu listed on the clan\'s kekkei genkai pages '
          '(`Kekkei Genkai` -> page infobox `Jutsu`, only when that page names the clan in its `Clan` field).',
          '2. Clans with fewer than 3 jutsu after step 1 take jutsu used by 2+ known members, then the members\' most common jutsu.',
          '3. Clans whose pages and members list no jutsu at all (non-shinobi families and barely documented clans) get '
          'thematic basics chosen from their wiki summary; these are marked `curated` below.',
          '4. Jutsu that come only from the Sharingan / Mangekyō Sharingan pages stay with the Uchiha: they require the Uchiha-only '
          'Sharingan forms, so the Ōtsutsuki (who share those kekkei genkai pages) could never cast them.',
          '5. Linked pages without a jutsu infobox were dropped (not jutsu): ' +
          ', '.join(f"`{j}` ({c})" for c, v in crawl['clans'].items() for j in v['dropped_non_jutsu']) + '.',
          '6. **Clan items** = tools from the members\' infobox `Tools` lists that exist in the ninja tool catalog, minus generic '
          'tools used by more than six clans; every clan also gets the Clan Emblem (skill item) and Clan Garb (clothing) in its colour.',
          '7. One jutsu is unlocked by default (lowest rank, clan technique first, signature technique where obvious); the rest unlock '
          'by **clan skill level**, gained by holding the clan emblem in either hand while defeating enemies (their experience becomes '
          'clan skill experience instead of orbs).', '',
          '## Clans', '',
          '| Clan | id | weight | natures | default jutsu | unlockable | forms | clan items | jutsu sources |',
          '|---|---|---:|---|---|---:|---|---:|---|']
    titles = {}
    for clan_title, c in sorted(crawl['clans'].items()):
        cid = CLAN_ID_OVERRIDES.get(clan_title) or slug(re.sub(r"\s+(Clan|Family)$", '', clan_title))
        titles[cid] = clan_title
        lin = cat['lineages'][cid]
        srcs = {}
        for s in c['jutsu'].values():
            key = s.split(':')[0]
            srcs[key] = srcs.get(key, 0) + 1
        forms = [e['transformation'].split(':')[1] for e in lin['progression'] if 'transformation' in e]
        nab = sum(1 for e in lin['progression'] if 'ability' in e)
        d = lin['granted_abilities'][0].split(':')[1]
        dname = cat['lang'].get(f'ability.{NS}.{d}', d)
        L.append(f"| [{clan_title}](https://naruto.fandom.com/wiki/{clan_title.replace(' ', '_')}) | `{cid}` | {lin['weight']} | "
                 f"{', '.join(a.split(':')[1] for a in lin['innate_affinities']) or '-'} | {dname} | {nab} | {', '.join(forms) or '-'} | "
                 f"{len(lin['clan_items'])} | {', '.join(f'{k} {v}' for k, v in sorted(srcs.items()))} |")
    cl = cat['lineages']['clanless']
    L += [f"| Clanless (fallback, not a wiki page) | `clanless` | 0 | - | Clone Technique | {len(cl['progression'])} | - | 0 | academy basics |",
          '', f"Total: {len(cat['lineages'])} lineages, {len(cat['abilities'])} generated clan jutsu "
          f"(+ the hand-written Great Fireball and Substitution), {len(cat['transformations'])} dojutsu forms, "
          f"{len(cat['affinities'])} advanced natures.", '',
          '## Jutsu -> engine mapping', '',
          'Generated from each jutsu infobox: `jutsu classification`, `jutsu type` (nature), `jutsu class type`, `jutsu range`, '
          '`jutsu rank` and `hand signs`. Rank sets damage (E 3 ... S 14), chakra cost (3x damage) and required rank tier; blank '
          'ranks are estimated from the classification (kinjutsu/shinjutsu S, senjutsu/space-time A, kekkei genkai B...). Hand signs '
          'available as animated combo tokens (tiger, ram, snake, bird, boar, horse) become the cast combo when unique.', '',
          '| Wiki signal | Action |', '|---|---|',
          '| Genjutsu | `area_effect` cone: nausea, slowness, darkness |',
          '| Dōjutsu (supplementary) | night vision + 24-block glowing pulse |',
          '| Medical / regeneration | heal + regeneration |',
          '| Space–Time (supplementary) | 14-block teleport dash |',
          '| Clone techniques | invisibility + speed, blinding burst |',
          '| Chakra absorption | draining `area_effect` + self heal |',
          '| Defensive / barrier / wall / dome | `raise_wall` (stone, crystal, mirror or barrier by nature) + resistance |',
          '| Offensive taijutsu / kenjutsu | `dash_strike` (+ shockwave for A/S) |',
          '| Taijutsu / senjutsu buffs, gates, modes | strength + speed + resistance |',
          '| Fūinjutsu / chains (offensive) | pulling binding cone |',
          '| Insects / bees | poison swarm cone |',
          '| Yin shadow / mind | binding or mind-control cone |',
          '| Yang (supplementary) | strength, resistance, health boost |',
          '| Offensive, short range | forward `aoe_sphere` |',
          '| Offensive lightning/storm, long range | `beam_raycast` |',
          '| Offensive dōjutsu (Amaterasu...) | burning `beam_raycast` |',
          '| Other offensive | nature-coloured `projectile` (charged for A/S, exhale animation for fire/water/wind) |', '',
          '## Clan jutsu by clan', '']
    for cid, lin in sorted(cat['lineages'].items()):
        if cid == 'clanless':
            continue
        entries = [f"**{cat['lang'][f'ability.{NS}.' + lin['granted_abilities'][0].split(':')[1]] if f'ability.{NS}.' + lin['granted_abilities'][0].split(':')[1] in cat['lang'] else lin['granted_abilities'][0]}** (default)"]
        for e in lin['progression']:
            key = e.get('ability') or e.get('transformation')
            p = key.split(':')[1]
            nm = cat['lang'].get(f'ability.{NS}.{p}') or cat['lang'].get(f'transformation.{NS}.{p}') or p.replace('_', ' ').title()
            entries.append(f"{nm} (Lv {e['skill_level']})")
        L.append(f"- **{titles[cid]}**: " + ', '.join(entries))
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_clans.md'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutopedia_clans.md')


if __name__ == '__main__':
    write_doc()
