"""Forms catalog for NarutoRPG: tailed beasts (jinchūriki stages), curse marks, Kāma, senjutsu modes, the dōjutsu powers
missing from the clan catalog, and every kekkei genkai / kekkei mōra / kekkei tōta page of Narutopedia.

Input : docs/wiki/narutopedia_forms.json (Category:Kekkei Genkai / Kekkei Mōra / Kekkei Tōta members + the form pages
        (Jinchūriki Forms, the tailed beasts, Sage Mode, Six Paths Sage Mode, Nine-Tails Chakra Mode, Baryon Mode, Kāma,
        Cursed Seal of Heaven / Earth, Susanoo, Rinne Sharingan, Tenseigan Chakra Mode...) with their lead text)
        docs/wiki/narutopedia_story.json (which jutsu each character uses, for the powers a form grants)
        tools/catalog/generated/*_catalog.json (abilities / forms that already exist)
Output: tools/catalog/generated/forms_catalog.json (TransformationDefinitions + lang), the awakening item list
        narutorpg/src/main/resources/assets/narutorpg/awakening_items.json (registered by NarutoForms), item sprites,
        docs/wiki/narutopedia_forms.md.

Every form is a transformation on a technique item (Jinchūriki, Senjutsu, Kekkei Genkai, Dōjutsu, Fūinjutsu): stat powers
(attack, armour, speed, size for full tailed beasts...), the techniques its wiki users wield (castable while active), an
aura and a translucent chakra cloak. Later stages evolve through mastery (time spent in the form). The first stage is
awakened with an item (tailed-beast seal, curse mark, sage scroll...), found as story rewards, loot or in the admin tab.
Run after story.py.
"""
import json
import math
import os
import re

import clans

ROOT = clans.ROOT
NS = clans.NS
GEN = os.path.join(ROOT, 'tools', 'catalog', 'generated')
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_forms.json')
STORY = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_story.json')
OUT = os.path.join(GEN, 'forms_catalog.json')
ITEMS_JSON = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg', 'awakening_items.json')
ITEM_DIR = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg', 'textures', 'item')
CHAKRA = clans.CHAKRA
attr, power, regen, evasion, sense, on_hit, passive, heal, fatal_save = (clans.attr, clans.power, clans.regen, clans.evasion,
                                                                         clans.sense, clans.on_hit, clans.passive, clans.heal,
                                                                         clans.fatal_save)

# (tails, wiki page, id, colour, nature)
BEASTS = [(1, 'Shukaku', 'shukaku', 0xC8A15A, 'wind'), (2, 'Matatabi', 'matatabi', 0x2979FF, 'fire'),
          (3, 'Isobu', 'isobu', 0x26A69A, 'water'), (4, 'Son Gokū', 'son_goku', 0xE64A19, 'lava'),
          (5, 'Kokuō', 'kokuo', 0xECEFF1, 'boil'), (6, 'Saiken', 'saiken', 0xC6FF00, 'water'),
          (7, 'Chōmei', 'chomei', 0x00E676, 'wind'), (8, 'Gyūki', 'gyuki', 0x8E24AA, 'lightning'),
          (9, 'Kurama', 'kurama', 0xFF6D00, 'fire')]
BEAST_NAMES = {'shukaku': 'One-Tails', 'matatabi': 'Two-Tails', 'isobu': 'Three-Tails', 'son_goku': 'Four-Tails',
               'kokuo': 'Five-Tails', 'saiken': 'Six-Tails', 'chomei': 'Seven-Tails', 'gyuki': 'Eight-Tails', 'kurama': 'Nine-Tails'}
NATURE_PAGES = {'Boil Release', 'Crystal Release', 'Dark Release', 'Explosion Release', 'Ice Release', 'Lava Release',
                'Magnet Release', 'Mud Release', 'Scorch Release', 'Steel Release', 'Storm Release', 'Swift Release',
                'Typhoon Release', 'Wood Release', 'Dust Release'}
EXISTING_DOJUTSU = {'Byakugan': 'byakugan', 'Jōgan': 'jogan', 'Ketsuryūgan': 'ketsuryugan', 'Kokugan': 'kokugan',
                    'Mangekyō Sharingan': 'mangekyo_sharingan', 'Rinnegan': 'rinnegan', 'Senrigan': 'senrigan',
                    'Sharingan': 'sharingan_one_tomoe', 'Tenseigan': 'tenseigan'}


def load_abilities():
    ab = {}
    for f in sorted(os.listdir(GEN)):
        if f.endswith('_catalog.json') and f not in ('story_catalog.json', 'forms_catalog.json'):
            ab.update(json.load(open(os.path.join(GEN, f), encoding='utf-8')).get('abilities', {}))
    return ab


ABILITIES = load_abilities()
STORY_JUTSU = json.load(open(STORY, encoding='utf-8'))['jutsu']


def offensive(d):
    for a in d['actions']:
        t = a['type'].split(':')[1]
        if t in ('projectile', 'beam_raycast') or (t in ('aoe_sphere', 'dash_strike', 'area_effect') and a.get('damage', 4 if t == 'aoe_sphere' else 0) > 0):
            return True
    return False


def ids(*names):
    """Existing ability ids by id or regex."""
    out = []
    for n in names:
        if n in ABILITIES:
            out.append(n)
        else:
            out += sorted(k for k in ABILITIES if not k.startswith('fanon/') and re.fullmatch(n, k))
    return list(dict.fromkeys(out))


def used_by(character, limit=6, attacks=True):
    out = []
    for t in STORY_JUTSU.get(character, []):
        i = clans.ALIASES.get(t) or clans.slug(t)
        if i in ABILITIES and (not attacks or offensive(ABILITIES[i])) and i not in out:
            out.append(i)
    return out[:limit]


FORMS, ITEMS, LANG, DOC = {}, [], {}, []


def form(fid, name, token, tier, modifiers, granted=(), aura=0xFFFFFF, aura_intensity=0.6, cloak=None, cloak_alpha=0.45,
         activation=10.0, drain=0.08, nxt=None, mastery=0, page=None, cooldown=200, model=None):
    vis = {'aura_color': aura, 'aura_intensity': aura_intensity}
    if model:   # low-poly model drawn instead of the player (tools/catalog/beast_models.py)
        vis['model'] = clans.ns(model)
    if cloak is not None:
        vis['cloak_color'] = cloak
        vis['cloak_alpha'] = cloak_alpha
    # stat modifiers vs bloodline-style traits (evasion, sensing, on-hit effects, regeneration...)
    MOD_TYPES = ('animecore:attribute', 'animecore:energy_capacity', 'animecore:energy_regen', 'animecore:ability_power')
    mods = [m for m in modifiers if m['type'] in MOD_TYPES]
    traits = [m for m in modifiers if m['type'] not in MOD_TYPES]
    d = {'system': clans.SYSTEM, 'name': clans.text(f'transformation.{NS}.{fid}', name), 'pool': CHAKRA,
         'activation_cost': activation, 'drain_per_tick': drain, 'cooldown_ticks': cooldown, 'required_tier': tier,
         'modifiers': mods, 'traits': traits, 'granted_abilities': [clans.ns(g) for g in granted], 'visuals': vis, 'token': clans.ns(token)}
    if nxt:
        d['next_stage'] = clans.ns(nxt)
        d['mastery_to_evolve'] = mastery
    LANG[f'transformation.{NS}.{fid}'] = name
    FORMS[fid] = d
    DOC.append((page or name, name, token, tier, len(granted)))
    return fid


def item(iid, name, fid, lore, sprite, colour):
    ITEMS.append({'item': iid, 'form': clans.ns(fid), 'lore': f'item.{NS}.{iid}.lore', 'sprite': sprite, 'color': colour})
    LANG[f'item.{NS}.{iid}'] = name
    LANG[f'item.{NS}.{iid}.lore'] = lore


def lore(page, fallback):
    text = json.load(open(CRAWL, encoding='utf-8'))['leads'].get(page, '')
    s = clans_sentence(text) or fallback
    return s


def clans_sentence(text):
    m = re.match(r'(.{20,220}?[.!?])(\s|$)', text)
    return m.group(1) if m else text[:220]


def build():
    # ---------------------------------------------------------------- tailed beasts (Jinchūriki Forms)
    for tails, page, bid, colour, nature in BEASTS:
        title = BEAST_NAMES[bid]
        nat = nature
        beast_jutsu = used_by(page, 6)
        ball = ids('tailed_beast_ball', 'continuous_tailed_beast_balls') if tails < 9 else ids('tailed_beast_ball', 'continuous_tailed_beast_balls', 'super_mini_tailed_beast_ball')
        k = tails / 9
        v1 = f'{bid}_version_1'
        v2 = f'{bid}_version_2'
        mode = f'{bid}_tailed_beast_mode' if bid != 'kurama' else 'nine_tails_chakra_mode'
        full = f'{bid}_tailed_beast_transformation'
        form(v1, f'{title} Cloak: Version 1', 'jinchuriki', 3,
             [attr('attack_damage', 2 + 2 * k), attr('movement_speed', 0.12, 'add_multiplied_base'), attr('armor', 2),
              heal(0.5), power(1.15, nat)], beast_jutsu[:2], aura=colour, cloak=colour, cloak_alpha=0.3,
             activation=12, drain=0.05, nxt=v2, mastery=2400, page='Jinchūriki Forms')
        form(v2, f'{title} Cloak: Version 2', 'jinchuriki', 5,
             [attr('attack_damage', 4 + 3 * k), attr('movement_speed', 0.2, 'add_multiplied_base'), attr('armor', 5),
              attr('scale', 0.1), heal(1.0), power(1.3, nat), on_hit('wither', 40, 0, 0.3)],
             beast_jutsu[:4], aura=colour, aura_intensity=0.9, cloak=colour, cloak_alpha=0.6, activation=20, drain=0.1,
             nxt=mode, mastery=4800, page='Jinchūriki Forms')
        mode_name = 'Nine-Tails Chakra Mode' if bid == 'kurama' else f'{title}: Tailed Beast Mode'
        form(mode, mode_name, 'jinchuriki', 7,
             [attr('attack_damage', 6 + 4 * k), attr('movement_speed', 0.25, 'add_multiplied_base'), attr('armor', 6),
              attr('max_health', 10), heal(1.0), power(1.5), sense(24)],
             beast_jutsu + ball[:1], aura=0xFFD54F if bid == 'kurama' else colour, aura_intensity=1.2,
             cloak=0xFFB300 if bid == 'kurama' else colour, cloak_alpha=0.5, activation=30, drain=0.14, nxt=full, mastery=9600,
             page='Nine-Tails Chakra Mode' if bid == 'kurama' else 'Jinchūriki Forms')
        form(full, f'{title}: Full Tailed Beast Transformation', 'jinchuriki', 9,
             [attr('scale', 2.0 + 0.25 * tails), attr('max_health', 20 + 4 * tails), attr('attack_damage', 10 + 6 * k),
              attr('armor', 10), attr('step_height', 2.0), attr('safe_fall_distance', 20), attr('jump_strength', 0.2),
              power(1.8), heal(1.5)],
             beast_jutsu + ball, aura=colour, aura_intensity=1.6, activation=50, drain=0.3, page=page, model=bid)
        item(f'{bid}_seal', f'Tailed Beast Seal: {page} ({title})', v1,
             lore(page, f'The sealed chakra of the {title}.') + ' Becoming its jinchūriki grants the chakra cloak.',
             'beast', (colour, tails))
    # Baryon Mode (Kurama's last form) and the Ten-Tails
    form('baryon_mode_form', 'Baryon Mode', 'jinchuriki', 12,
         [attr('attack_damage', 14), attr('movement_speed', 0.35, 'add_multiplied_base'), attr('armor', 8), power(2.0),
          on_hit('wither', 60, 1, 0.5)], ids('baryon_.*'), aura=0xFF8A65, aura_intensity=1.5, cloak=0xFF7043, cloak_alpha=0.55,
         activation=60, drain=0.5, page='Baryon Mode')
    item('baryon_mode', 'Baryon Mode Scroll', 'baryon_mode_form', lore('Baryon Mode', 'Fusing the chakra of jinchūriki and beast.'),
         'baryon', (0xFF7043, 0x40C4FF))
    form('ten_tails_jinchuriki', 'Ten-Tails Jinchūriki', 'jinchuriki', 12,
         [attr('scale', 0.15), attr('attack_damage', 12), attr('armor', 10), attr('max_health', 20), power(2.0),
          evasion(0.2, True), heal(2.0)],
         ids('truth_seeking_ball', 'big_truth_seeking_ball', 'truth_seeking_chaos_bullets', 'six_paths_chibaku_tensei')
         + used_by('Ten-Tails', 3), aura=0xF5F5F5, aura_intensity=1.4, cloak=0xFAFAFA, cloak_alpha=0.35, activation=60, drain=0.35,
         page='Ten-Tails')
    item('ten_tails_seal', 'Tailed Beast Seal: Ten-Tails', 'ten_tails_jinchuriki', lore('Ten-Tails', 'The progenitor of all tailed beasts.'),
         'beast', (0xF5F5F5, 10))

    # ---------------------------------------------------------------- senjutsu modes
    form('sage_mode', 'Sage Mode', 'senjutsu', 5,
         [attr('attack_damage', 4), attr('armor', 4), attr('movement_speed', 0.1, 'add_multiplied_base'), power(1.3), sense(32),
          regen(CHAKRA, 1.5)], ids('sage_art_.*')[:10], aura=0xFFB300, aura_intensity=0.7, cloak=0xFF8F00, cloak_alpha=0.18,
         activation=15, drain=0.06, nxt='six_paths_sage_mode', mastery=12000, page='Sage Mode')
    form('six_paths_sage_mode', 'Six Paths Sage Mode', 'senjutsu', 10,
         [attr('attack_damage', 10), attr('armor', 8), attr('movement_speed', 0.3, 'add_multiplied_base'), power(1.8),
          sense(48), evasion(0.15, True), heal(1.5)],
         ids('six_paths_.*')[:10] + ids('truth_seeking_ball'), aura=0xFFF59D, aura_intensity=1.5, cloak=0xFFD54F, cloak_alpha=0.5,
         activation=40, drain=0.2, page='Six Paths Sage Mode')
    item('sage_scroll', 'Sage Training Scroll (Mount Myōboku)', 'sage_mode', lore('Sage Mode', 'Natural energy drawn into the body.'),
         'toad', (0xFFB300, 0x1B5E20))

    # ---------------------------------------------------------------- curse marks and Kāma (Fūinjutsu / Juinjutsu)
    for seal, page, colour, foc in (('heaven', 'Cursed Seal of Heaven', 0x1A1A1A, 'attack'), ('earth', 'Cursed Seal of Earth', 0x3E2723, 'armor')):
        l1, l2 = f'cursed_seal_of_{seal}_level_1', f'cursed_seal_of_{seal}_level_2'
        strong = (lambda a, b: a) if foc == 'attack' else (lambda a, b: b)
        form(l1, f'{page}: Level 1', 'fuinjutsu', 3,
             [attr('attack_damage', strong(4, 2)), attr('armor', strong(1, 4)), attr('movement_speed', 0.15, 'add_multiplied_base'),
              power(1.2), passive('hunger', 0)], ids('cursed_seal_of_' + seal), aura=0x4A148C, aura_intensity=0.5,
             cloak=colour, cloak_alpha=0.35, activation=8, drain=0.05, nxt=l2, mastery=3600, page=page)
        form(l2, f'{page}: Level 2', 'fuinjutsu', 5,
             [attr('attack_damage', strong(8, 4)), attr('armor', strong(3, 8)), attr('max_health', 6), attr('scale', 0.08),
              attr('movement_speed', 0.22, 'add_multiplied_base'), power(1.45), passive('hunger', 1)],
             ids('cursed_seal_of_' + seal, 'sound_four_s_cursed_seals'), aura=0x6A1B9A, aura_intensity=1.0,
             cloak=0x6D4C41 if seal == 'heaven' else 0x8D6E63, cloak_alpha=0.6, activation=15, drain=0.1, page=page)
        item(f'cursed_seal_of_{seal}', page, l1, lore(page, "One of Orochimaru's cursed seals."), 'mark', (colour, 0x7B1FA2))
    form('kama', 'Kāma', 'fuinjutsu', 10,
         [attr('attack_damage', 6), attr('armor', 4), power(1.4), regen(CHAKRA, 2.0), on_hit('weakness', 60, 0, 0.3)],
         ids('kama', 'kama_spiral', 'kama_rasengan', 'kama_great_burial_wave'), aura=0x212121, aura_intensity=0.8,
         cloak=0x111111, cloak_alpha=0.35, activation=20, drain=0.08, nxt='kama_otsutsuki_awakening', mastery=9600, page='Kāma')
    form('kama_otsutsuki_awakening', 'Kāma: Ōtsutsuki Awakening', 'fuinjutsu', 13,
         [attr('attack_damage', 12), attr('armor', 8), attr('movement_speed', 0.25, 'add_multiplied_base'), power(1.9), heal(1.5),
          evasion(0.15, True)], ids('daikokuten.*', 'sukunahikona.*')[:8] + ids('kama', 'kama_rasengan'), aura=0xFAFAFA,
         aura_intensity=1.3, cloak=0xF5F5F5, cloak_alpha=0.5, activation=40, drain=0.25, page='Kāma')
    item('kama_seal', 'Kāma', 'kama', lore('Kāma', 'A seal holding an Ōtsutsuki backup.'), 'kama', (0x111111, 0xFAFAFA))

    # ---------------------------------------------------------------- dōjutsu powers
    form('susanoo', 'Susanoo', 'dojutsu', 8,
         [attr('scale', 1.6), attr('armor', 14), attr('max_health', 20), attr('attack_damage', 8), attr('step_height', 1.5),
          power(1.5), attr('movement_speed', -0.15, 'add_multiplied_base')],
         ids('susanoo_.*')[:8] + ids('amaterasu', 'blaze_release_kagutsuchi_flying_flame'), aura=0x7E57C2, aura_intensity=1.5,
         activation=40, drain=0.3, page='Susanoo', model='susanoo')
    item('susanoo_scroll', 'Susanoo Awakening', 'susanoo', lore('Susanoo', 'A giant chakra warrior from the Mangekyō Sharingan.'),
         'rib', (0x7E57C2, 0xB39DDB))
    form('eternal_mangekyo_sharingan', 'Eternal Mangekyō Sharingan', 'dojutsu', 9,
         [power(1.6), sense(24), evasion(0.2, True), attr('attack_damage', 4), regen(CHAKRA, 1.3)],
         ids('amaterasu', 'amaterasu_flame_wrapping_fire', 'blaze_release_kagutsuchi_flying_flame', 'kamui', 'tsukuyomi', 'izanagi'),
         aura=0xB71C1C, aura_intensity=0.6, activation=15, drain=0.08, page='Mangekyō Sharingan')
    item('eternal_mangekyo', 'Eternal Mangekyō Sharingan', 'eternal_mangekyo_sharingan',
         lore('Mangekyō Sharingan', 'The Mangekyō that never loses its light.'), 'eye', (0xC62828, 0x111111))
    form('rinne_sharingan', 'Rinne Sharingan', 'dojutsu', 12,
         [power(2.0), sense(64), evasion(0.25, True), attr('attack_damage', 8), attr('armor', 6), heal(1.0)],
         ids('infinite_tsukuyomi', 'amenominaka', 'yomotsu_hirasaka', 'all_killing_ash_bones', 'eighty_gods_vacuum_attack',
             'expansive_truth_seeking_ball', 'rabbit_hair_needle', 'god_nativity_of_a_world_of_trees'),
         aura=0xB39DDB, aura_intensity=1.2, cloak=0xEDE7F6, cloak_alpha=0.25, activation=50, drain=0.3, page='Rinne Sharingan')
    item('rinne_sharingan_eye', 'Rinne Sharingan', 'rinne_sharingan', lore('Rinne Sharingan', 'The eye of Kaguya Ōtsutsuki.'),
         'eye', (0xB71C1C, 0xB39DDB))
    form('tenseigan_chakra_mode', 'Tenseigan Chakra Mode', 'dojutsu', 10,
         [attr('attack_damage', 8), attr('armor', 6), attr('movement_speed', 0.2, 'add_multiplied_base'), power(1.7),
          evasion(0.15, True)], ids('tenseigan_chakra_mode', 'golden_wheel_reincarnation_explosion', 'truth_seeking_ball',
                                    'silver_wheel_reincarnation_explosion'),
         aura=0x80DEEA, aura_intensity=1.3, cloak=0xE0F7FA, cloak_alpha=0.5, activation=35, drain=0.2, page='Tenseigan Chakra Mode')
    item('tenseigan_chakra', 'Tenseigan Chakra Mode Scroll', 'tenseigan_chakra_mode', lore('Tenseigan Chakra Mode', 'The power of the Tenseigan unleashed.'),
         'eye', (0x00ACC1, 0xE0F7FA))
    form('ranmaru_dojutsu', "Ranmaru's Dōjutsu", 'dojutsu', 3, [sense(48), power(1.15), evasion(0.1)], [],
         aura=0xE53935, aura_intensity=0.3, activation=6, drain=0.02, page="Ranmaru's Kekkei Genkai")
    item('ranmaru_eye', "Ranmaru's Dōjutsu", 'ranmaru_dojutsu', lore("Ranmaru's Kekkei Genkai", 'Red eyes that see through everything.'),
         'eye', (0xE53935, 0x212121))

    # ---------------------------------------------------------------- bloodline (non-eye) kekkei genkai
    bloodlines = [
        ('shikotsumyaku', 'Shikotsumyaku', 'Shikotsumyaku', 6,
         [attr('armor', 8), attr('attack_damage', 5), attr('armor_toughness', 4), power(1.3), on_hit('slowness', 40, 1, 0.4)],
         ids('dance_of_the_camellia', 'dance_of_the_clematis_flower', 'dance_of_the_clematis_vine', 'dance_of_the_larch', 'dance_of_the_willow', 'bone_pellet', 'all_killing_ash_bones') + used_by('Kimimaro', 3), 0xF5F5F5, 0xEFEBE9),
        ('sage_transformation', 'Sage Transformation (Jūgo Clan)', "Jūgo's Clan's Kekkei Genkai", 5,
         [attr('attack_damage', 7), attr('scale', 0.15), attr('armor', 4), heal(1.0), power(1.3)],
         ids('sage_transformation') + used_by('Jūgo', 4), 0x6D4C41, 0x8D6E63),
        ('smoke_body', 'Smoke Body (Iburi Clan)', "Iburi Clan's Kekkei Genkai", 4,
         [evasion(0.5, True), attr('movement_speed', 0.2, 'add_multiplied_base'), passive('slow_falling', 0)],
         ids('smoke_clone', 'binding_smoke_prison', 'dark_smoke_sphere'), 0xBDBDBD, 0xE0E0E0),
        ('kurama_clan_illusion', 'Kurama Clan Genjutsu', "Kurama Clan's Kekkei Genkai", 4,
         [power(1.5), on_hit('nausea', 80, 0, 0.5), on_hit('blindness', 30, 0, 0.2)],
         ids('demonic_illusion_.*')[:5], 0xAD1457, 0xF8BBD0),
        ('rinha_absorption', 'Rinha Clan Absorption', "Rinha Clan's Kekkei Genkai", 4,
         [regen(CHAKRA, 2.0), on_hit('weakness', 60, 0, 0.5), power(1.2)], ids('.*chakra_absorption.*', 'absorption_sphere')[:4], 0x4527A0, 0xD1C4E9),
        ('dragon_life', 'Dragon Life (Ryūzetsu)', "Ryūzetsu's Kekkei Genkai", 5,
         [fatal_save(6000, 10.0, 'dragon_life'), heal(1.0), attr('max_health', 6)], ids('dragon_life_reincarnation'), 0x26A69A, 0xB2DFDB),
        ('molecular_fusion', 'Molecular Fusion (Sakon and Ukon)', "Sakon and Ukon's Kekkei Genkai", 4,
         [heal(1.0), attr('armor', 4), evasion(0.15), attr('attack_damage', 3)], ids('ukon_s_mad_dance') + used_by('Sakon and Ukon', 3), 0x6A1B9A, 0xCE93D8),
        ('clone_link', 'Clone Link (Yanaru)', "Yanaru's Kekkei Genkai", 3,
         [sense(32), clans.xp_boost(1.25), power(1.1)], ids('shadow_clone_technique', 'multiple_shadow_clone_technique'), 0x455A64, 0xB0BEC5),
    ]
    for fid, name, page, tier, mods, granted, colour, cloak in bloodlines:
        form(fid, name, 'kekkei_genkai', tier, mods, granted, aura=colour, aura_intensity=0.7, cloak=cloak, cloak_alpha=0.3,
             activation=12, drain=0.06, page=page)
        item(f'{fid}_awakening', f'{name} Awakening', fid, lore(page, name + '.'), 'helix', (colour, cloak))


# ======================================================================================================================
# sprites
# ======================================================================================================================

def rgb(h, a=255):
    return ((h >> 16) & 255, (h >> 8) & 255, h & 255, a)


def sprites():
    from PIL import Image, ImageDraw
    os.makedirs(ITEM_DIR, exist_ok=True)
    for it in ITEMS:
        im = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        kind, col = it['sprite'], it['color']
        a, b = col if isinstance(col, (list, tuple)) else (col, 0)
        # parchment talisman with red rollers
        d.rectangle([7, 3, 24, 28], fill=(238, 224, 186, 255))
        d.rectangle([6, 2, 25, 4], fill=(150, 30, 36, 255)); d.rectangle([6, 27, 25, 29], fill=(150, 30, 36, 255))
        cx, cy = 15.5, 15.5
        if kind == 'beast':
            tails = b
            d.ellipse([10, 10, 21, 21], fill=rgb(a))
            for k in range(tails):
                ang = -math.pi / 2 + k * 2 * math.pi / max(1, tails)
                d.line([(cx + 5 * math.cos(ang), cy + 5 * math.sin(ang)), (cx + 8.5 * math.cos(ang), cy + 8.5 * math.sin(ang))],
                       fill=(40, 20, 10, 255), width=1)
            d.ellipse([13.5, 13.5, 17.5, 17.5], fill=(20, 10, 10, 255))
        elif kind == 'mark':
            for k in range(3):
                ang = k * 2 * math.pi / 3
                x, y = cx + 5 * math.cos(ang), cy + 5 * math.sin(ang)
                d.ellipse([x - 2.5, y - 2.5, x + 2.5, y + 2.5], fill=rgb(a))
                d.line([(x, y), (x + 3 * math.cos(ang + 1.2), y + 3 * math.sin(ang + 1.2))], fill=rgb(a), width=2)
        elif kind == 'kama':
            d.polygon([(cx, 8), (cx + 6, cy), (cx, 23), (cx - 6, cy)], fill=rgb(a))
            d.polygon([(cx, 11), (cx + 3, cy), (cx, 20), (cx - 3, cy)], fill=rgb(b))
        elif kind == 'eye':
            d.ellipse([8, 11, 23, 20], fill=(250, 250, 250, 255))
            d.ellipse([11.5, 11.5, 19.5, 19.5], fill=rgb(a))
            d.ellipse([14, 14, 17, 17], fill=rgb(b))
        elif kind == 'toad':
            d.ellipse([9, 11, 22, 20], fill=rgb(a))
            d.rectangle([10, 15, 21, 16], fill=(20, 10, 0, 255))
        elif kind == 'baryon':
            d.ellipse([8, 10, 16, 18], fill=rgb(a)); d.ellipse([15, 13, 23, 21], fill=rgb(b))
        elif kind == 'rib':
            for y in range(9, 23, 3):
                d.arc([9, y - 3, 22, y + 5], 200, 340, fill=rgb(a), width=2)
            d.line([(cx, 8), (cx, 23)], fill=rgb(b), width=1)
        else:   # helix
            for y in range(7, 25):
                t = (y - 7) / 17 * 2 * math.pi
                d.point((cx + 4 * math.sin(t), y), fill=rgb(a)); d.point((cx - 4 * math.sin(t), y), fill=rgb(b))
                if y % 3 == 0:
                    d.line([(cx + 4 * math.sin(t), y), (cx - 4 * math.sin(t), y)], fill=rgb(a))
        im.save(os.path.join(ITEM_DIR, it['item'] + '.png'), optimize=True)


def write_doc(crawl):
    kg = {c: v['pages'] for c, v in crawl['category_tree'].items()}
    L = ['# Narutopedia forms coverage: tailed beasts, curse marks, Kāma, modes, kekkei genkai / mōra / tōta', '',
         f"Crawled {crawl['crawled']}. Method: list=categorymembers on Category:Kekkei Genkai, Category:Kekkei Mōra and "
         'Category:Kekkei Tōta (no subcategories); the tailed beasts, Jinchūriki Forms, curse marks, Kāma and modes have no '
         'category on Narutopedia and were taken from their own pages (`narutopedia_forms.json` lists every page and its lead).', '',
         '## Kekkei genkai / mōra / tōta pages', '', '| category | page | in game as |', '|---|---|---|']
    for c, pages in kg.items():
        for p in pages:
            if p in NATURE_PAGES:
                where = 'nature release (affinity + release technique item, awakened through nature mastery)'
            elif p in EXISTING_DOJUTSU:
                where = f'dōjutsu form `{NS}:{EXISTING_DOJUTSU[p]}` (clan bloodline progression)'
            elif any(d[0] == p for d in DOC):
                where = 'form ' + ', '.join(f'**{d[1]}**' for d in DOC if d[0] == p)
            elif clans.slug(p) in ABILITIES:
                where = f'technique `{NS}:{clans.slug(p)}` (granted by Rinne Sharingan / the Ōtsutsuki forms)'
            else:
                where = '-'
            L.append(f'| {c.split(":")[1]} | {p} | {where} |')
    L += ['', '## Forms', '', '| wiki page | form | technique item | rank | techniques granted |', '|---|---|---|---:|---:|']
    for page, name, token, tier, n in DOC:
        L.append(f'| {page} | {name} | {token} | {tier} | {n} |')
    L += ['', '## Awakening items', '', 'Each item awakens the first stage of its form; later stages evolve with mastery (time spent in '
          'the form). They are story rewards (see README), rare shinobi-cache loot and in the creative tab.', '',
          '| item | awakens |', '|---|---|']
    for it in ITEMS:
        L.append(f"| {LANG['item.' + NS + '.' + it['item']]} | `{it['form']}` |")
    L += ['', '## Low-poly models', '',
          'Modelled by hand (`tools/catalog/beast_models.py`) from the infobox image of each Narutopedia page (Shukaku, Matatabi, '
          'Isobu, Son Gokū, Kokuō, Saiken, Chōmei, Gyūki, Kurama, Ten-Tails, Susanoo - Susanoo after the page image, Itachi\'s). '
          'Palettes are the dominant colours sampled from those images; the images themselves are not part of the mod. Used by '
          'the full tailed-beast transformations, Susanoo and the story\'s tailed-beast bosses.']
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_forms.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')


def main():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    build()
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump({'generated_from': os.path.relpath(CRAWL, ROOT), 'crawled': crawl['crawled'], 'transformations': FORMS,
                   'abilities': {}, 'lang': LANG}, f, indent=1, ensure_ascii=False, sort_keys=True)
    with open(ITEMS_JSON, 'w', encoding='utf-8') as f:
        json.dump([{k: v for k, v in it.items() if k in ('item', 'form', 'lore')} for it in ITEMS], f, indent=1, ensure_ascii=False)
    sprites()
    write_doc(crawl)
    print(f'forms: {len(FORMS)} transformations, {len(ITEMS)} awakening items, '
          f'{sum(len(f["granted_abilities"]) for f in FORMS.values())} granted techniques')


if __name__ == '__main__':
    main()
