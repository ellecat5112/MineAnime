"""Low-poly models for the non-humanoid Part 3 Stands (JoJoRPG), drawn by the engine's FormModels.

Shapes and palettes follow each Stand's look on its jojowiki page (infobox art): Magician's Red's bird head, Hermit Purple's
thorny vines, The Fool's sand mask on wheels, the Tower of Gray beetle, Strength's ship, the Yellow Temperance blob,
Emperor's revolver, the Wheel of Fortune car, Justice's fog skull, the Sun, Death Thirteen's reaper, Geb's water hand,
Tohth's comic book, Anubis' sword, Bastet's socket, Horus' ice bird and Cream's void maw. Humanoid Stands (Star
Platinum, The World, Silver Chariot...) use the tinted character layers instead.
Writes jojorpg/src/main/resources/assets/jojorpg/form_models/stand/<id>.json + textures/entity/form/stand_<id>.png.
Run after jojo_stands.py: python3 tools/catalog/stand_models.py
"""
import os
import sys

sys.path.insert(0, os.path.dirname(__file__))
from beast_models import Model, cube, export, TEX  # noqa: E402

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
ASSETS = os.path.join(ROOT, 'jojorpg', 'src', 'main', 'resources', 'assets', 'jojorpg')


def magicians_red():
    mats = {'body': {'base': 0xD84315, 'pattern': 'spots', 'pc': 0xFF7043}, 'feather': {'base': 0xFF5722, 'pattern': 'tipped', 'pc': 0xFFCA28},
            'beak': {'base': 0xFFB300}, 'dark': {'base': 0x6D1B0A}}
    m = Model('magicians_red', mats)
    m.part('body', (0, 2, 0), [cube(-5, 0, -3, 10, 12, 6, 'body'), cube(-4, 12, -2, 8, 10, 4, 'dark')], anim='sway')
    m.part('head', (0, 2, 0), [cube(-4, -8, -4, 8, 8, 8, 'feather', feat={'eyes': 2, 'eye': 0xFFEB3B, 'pupil': 0x111111, 'eye_y': 0.35}),
                               cube(-1.5, -5, -8, 3, 3, 4, 'beak'), cube(-1, -13, -1, 2, 6, 3, 'feather'), cube(-3, -11, 1, 2, 4, 3, 'feather'),
                               cube(1, -11, 1, 2, 4, 3, 'feather')], parent='body', anim='head')
    m.part('arm_r', (-6, 3, 0), [cube(-3, -1, -2, 3, 12, 4, 'body')], parent='body', anim='arm_a', amp=0.3)
    m.part('arm_l', (6, 3, 0), [cube(0, -1, -2, 3, 12, 4, 'body')], parent='body', anim='arm_b', amp=0.3)
    m.part('wing_r', (-4, 3, 3), [cube(-10, -2, 0, 10, 10, 1, 'feather')], parent='body', rot=(0, 0.5, 0.3), anim='wing', amp=0.6)
    m.part('wing_l', (4, 3, 3), [cube(0, -2, 0, 10, 10, 1, 'feather')], parent='body', rot=(0, -0.5, -0.3), anim='wing', phase=3.14, amp=0.6)
    return m


def hermit_purple():
    mats = {'vine': {'base': 0x7B1FA2, 'pattern': 'spots', 'pc': 0xCE93D8}, 'thorn': {'base': 0x4A148C}}
    m = Model('hermit_purple', mats, translucent=True)
    m.part('root', (0, 8, 0), [cube(-1, -1, -1, 2, 2, 2, 'vine')], anim='bob')
    for i, (yaw, lift) in enumerate(((0.0, -0.6), (2.1, -0.4), (4.2, -0.8), (1.0, 0.3), (3.2, 0.2))):
        m.chain(f'vine{i}', 'root', (0, 0, 0), (lift, yaw, 0), [(2, 2, 6), (2, 2, 6), (1, 1, 6), (1, 1, 5)], 'vine', phase=i * 0.9, amp=1.4,
                curl=0.3, spikes='thorn')
    return m


def the_fool():
    mats = {'sand': {'base': 0xC8A86B, 'pattern': 'spots', 'pc': 0xA1885A}, 'mask': {'base': 0x37474F, 'pattern': 'spots', 'pc': 0x546E7A},
            'wheel': {'base': 0x3E2723}}
    m = Model('the_fool', mats)
    m.part('body', (0, 12, 0), [cube(-5, -4, -8, 10, 8, 16, 'sand'), cube(-6, 2, -6, 2, 4, 4, 'wheel'), cube(4, 2, -6, 2, 4, 4, 'wheel'),
                                cube(-6, 2, 3, 2, 4, 4, 'wheel'), cube(4, 2, 3, 2, 4, 4, 'wheel')], anim='bob')
    m.part('head', (0, 9, -8), [cube(-4, -6, -6, 8, 8, 6, 'mask', feat={'eyes': 2, 'eye': 0xFFFFFF, 'pupil': 0x111111, 'eye_y': 0.4, 'mouth': 'teeth'}),
                                cube(-5, -9, -3, 2, 4, 2, 'mask'), cube(3, -9, -3, 2, 4, 2, 'mask')], parent='body', anim='head')
    m.chain('tail', 'body', (0, -2, 8), (0.6, 0, 0), [(3, 3, 5), (2, 2, 5), (1, 1, 4)], 'sand', amp=1.2)
    return m


def tower_of_gray():
    mats = {'shell': {'base': 0x757575, 'pattern': 'plates', 'pc': 0x424242}, 'eye': {'base': 0xE53935}, 'wing': {'base': 0xB0BEC5, 'alpha': 160},
            'needle': {'base': 0xFFCDD2}}
    m = Model('tower_of_gray', mats, translucent=True)
    m.part('body', (0, 10, 0), [cube(-4, -4, -6, 8, 7, 12, 'shell')], anim='bob')
    m.part('head', (0, 9, -6), [cube(-3, -3, -5, 6, 6, 5, 'shell', feat={'eyes': 2, 'eye': 0xE53935, 'pupil': 0xE53935, 'eye_y': 0.4}),
                                cube(-0.5, 1, -12, 1, 1, 8, 'needle')], parent='body', anim='head')
    m.part('wing_r', (-3, 6, -2), [cube(-12, 0, 0, 12, 1, 8, 'wing')], parent='body', anim='wing', amp=2.5)
    m.part('wing_l', (3, 6, -2), [cube(0, 0, 0, 12, 1, 8, 'wing')], parent='body', anim='wing', phase=3.14, amp=2.5)
    return m


def strength():
    mats = {'hull': {'base': 0x5D4037, 'pattern': 'plates', 'pc': 0x3E2723}, 'deck': {'base': 0x8D6E63}, 'mast': {'base': 0x6D4C41},
            'sail': {'base': 0xEFEBE9, 'pattern': 'spots', 'pc': 0xBCAAA4}, 'face': {'base': 0x795548}}
    m = Model('strength', mats)
    m.part('hull', (0, 18, 0), [cube(-6, -4, -14, 12, 6, 28, 'hull'), cube(-5, -5, -12, 10, 1, 24, 'deck'),
                                cube(-3, -8, -18, 6, 6, 4, 'face', feat={'eyes': 2, 'eye': 0xFFEB3B, 'pupil': 0x111111, 'mouth': 'teeth'})], anim='sway')
    m.part('mast', (0, 13, 0), [cube(-1, -24, -1, 2, 24, 2, 'mast'), cube(-8, -22, 0, 16, 12, 1, 'sail')], parent='hull', anim='sway', amp=0.5)
    return m


def yellow_temperance():
    mats = {'goo': {'base': 0xFDD835, 'pattern': 'spots', 'pc': 0xF9A825, 'alpha': 210}}
    m = Model('yellow_temperance', mats, translucent=True)
    m.part('blob', (0, 16, 0), [cube(-7, -6, -7, 14, 12, 14, 'goo', feat={'eyes': 2, 'eye': 0x6D4C41, 'pupil': 0x111111, 'mouth': True}),
                                cube(-5, -10, -5, 10, 4, 10, 'goo')], anim='bob')
    for i, (x, z) in enumerate(((-7, -4), (7, 3), (-3, 7), (5, -7))):
        m.chain(f'drip{i}', 'blob', (x, -2, z), (1.2, i * 1.6, 0), [(3, 3, 4), (2, 2, 4)], 'goo', anim='tail', phase=i, amp=0.8)
    return m


def emperor():
    mats = {'metal': {'base': 0x90A4AE, 'pattern': 'spots', 'pc': 0xCFD8DC}, 'grip': {'base': 0x5D4037}, 'gold': {'base': 0xFFC107}}
    m = Model('emperor', mats)
    m.part('gun', (0, 14, 0), [cube(-1, -2, -9, 2, 2, 10, 'metal'), cube(-2, -3, 0, 4, 4, 4, 'metal'), cube(-1, 1, 2, 2, 6, 3, 'grip'),
                               cube(-0.5, -4, 1, 1, 1, 2, 'gold')], anim='bob')
    return m


def wheel_of_fortune():
    mats = {'paint': {'base': 0x546E7A, 'pattern': 'spots', 'pc': 0x78909C}, 'glass': {'base': 0x263238, 'alpha': 220},
            'tire': {'base': 0x212121}, 'chrome': {'base': 0xCFD8DC}, 'spike': {'base': 0xB0BEC5}}
    m = Model('wheel_of_fortune', mats)
    m.part('car', (0, 16, 0), [cube(-8, -4, -16, 16, 6, 32, 'paint'), cube(-7, -9, -5, 14, 5, 14, 'glass'), cube(-8, 0, -17, 16, 2, 1, 'chrome'),
                               cube(-9, 0, -12, 1, 6, 6, 'tire'), cube(8, 0, -12, 1, 6, 6, 'tire'), cube(-9, 0, 7, 1, 6, 6, 'tire'),
                               cube(8, 0, 7, 1, 6, 6, 'tire'), cube(-1, -6, -14, 2, 2, 2, 'spike'), cube(-5, -6, -12, 2, 2, 2, 'spike'),
                               cube(3, -6, -12, 2, 2, 2, 'spike')], anim='sway', amp=0.3)
    return m


def justice():
    mats = {'fog': {'base': 0xCFD8DC, 'pattern': 'spots', 'pc': 0xECEFF1, 'alpha': 140}, 'bone': {'base': 0xECEFF1, 'alpha': 170}}
    m = Model('justice', mats, translucent=True)
    m.part('cloud', (0, 16, 0), [cube(-10, -6, -8, 20, 10, 16, 'fog'), cube(-14, -2, -4, 6, 6, 8, 'fog'), cube(8, -3, -5, 6, 6, 9, 'fog')], anim='bob')
    m.part('skull', (0, 6, -2), [cube(-6, -12, -6, 12, 12, 11, 'bone', feat={'eyes': 2, 'eye': 0x111111, 'pupil': 0x111111, 'eye_y': 0.45, 'sep': 0.3,
                                                                        'mouth': 'teeth', 'mouth_y': 0.85})], parent='cloud', anim='head')
    m.part('hand_r', (-11, 2, -6), [cube(-3, -2, -6, 5, 3, 6, 'bone')], parent='cloud', anim='sway', phase=1.0)
    m.part('hand_l', (11, 2, -6), [cube(-2, -2, -6, 5, 3, 6, 'bone')], parent='cloud', anim='sway', phase=2.0)
    return m


def sun():
    mats = {'core': {'base': 0xFFB300, 'pattern': 'spots', 'pc': 0xFFE082, 'alpha': 235}, 'ray': {'base': 0xFF6F00, 'alpha': 200}}
    m = Model('sun', mats, translucent=True)
    m.part('core', (0, 8, 0), [cube(-8, -8, -8, 16, 16, 16, 'core')], anim='bob')
    for i in range(8):
        import math
        a = i * math.pi / 4
        m.part(f'ray{i}', (0, 0, 0), [cube(-1, -14, -1, 2, 5, 2, 'ray')], parent='core', rot=(0, 0, a), anim='sway', phase=i * 0.5)
    return m


def death_thirteen():
    mats = {'cloak': {'base': 0x311B92, 'pattern': 'spots', 'pc': 0x4527A0}, 'bone': {'base': 0xEFEBE9}, 'blade': {'base': 0xB0BEC5},
            'pole': {'base': 0x4E342E}, 'hat': {'base': 0xC62828}}
    m = Model('death_thirteen', mats, translucent=False)
    m.part('body', (0, 2, 0), [cube(-5, 0, -3, 10, 14, 6, 'cloak'), cube(-6, 12, -4, 12, 10, 8, 'cloak')], anim='bob')
    m.part('head', (0, 2, 0), [cube(-4, -8, -4, 8, 8, 8, 'bone', feat={'eyes': 2, 'eye': 0x111111, 'pupil': 0xE53935, 'mouth': 'teeth'}),
                               cube(-5, -10, -5, 10, 3, 10, 'hat'), cube(-2, -13, -2, 4, 3, 4, 'hat')], parent='body', anim='head')
    m.part('arm_r', (-6, 3, 0), [cube(-3, -1, -2, 3, 11, 4, 'cloak'), cube(-2, 8, -9, 1, 1, 18, 'pole'), cube(-2, 4, -9, 1, 5, 1, 'blade'),
                                 cube(-2, 4, -9, 1, 1, 9, 'blade')], parent='body', anim='arm_a', amp=0.3)
    m.part('arm_l', (6, 3, 0), [cube(0, -1, -2, 3, 11, 4, 'cloak')], parent='body', anim='arm_b', amp=0.3)
    return m


def geb():
    mats = {'water': {'base': 0x29B6F6, 'pattern': 'spots', 'pc': 0xB3E5FC, 'alpha': 170}}
    m = Model('geb', mats, translucent=True)
    m.part('palm', (0, 14, 0), [cube(-5, -3, -4, 10, 6, 8, 'water')], anim='bob')
    for i, x in enumerate((-4, -1.5, 1, 3.5)):
        m.chain(f'finger{i}', 'palm', (x, -3, -3), (-0.5, 0, 0), [(2, 2, 4), (2, 2, 4), (1, 1, 3)], 'water', phase=i * 0.7, amp=0.6, curl=0.35)
    m.chain('thumb', 'palm', (5, 0, 0), (0, -1.2, 0), [(2, 2, 4), (2, 2, 3)], 'water', amp=0.5)
    return m


def tohth():
    mats = {'cover': {'base': 0xFDD835, 'pattern': 'spots', 'pc': 0xF57F17}, 'page': {'base': 0xFFF8E1}}
    m = Model('tohth', mats)
    m.part('book', (0, 12, 0), [cube(-6, -8, -1, 12, 16, 1, 'cover', feat={'eyes': 2, 'eye': 0x111111, 'pupil': 0xFFFFFF, 'mouth': True}),
                                cube(-5, -7, 0, 10, 14, 2, 'page'), cube(-6, -8, 2, 12, 16, 1, 'cover')], anim='bob')
    return m


def anubis():
    mats = {'blade': {'base': 0xCFD8DC, 'pattern': 'spots', 'pc': 0xECEFF1}, 'hilt': {'base': 0xFFC107}, 'grip': {'base': 0x4E342E}}
    m = Model('anubis', mats)
    m.part('sword', (0, 12, 0), [cube(-0.5, -18, -1.5, 1, 18, 3, 'blade'), cube(-3, 0, -1.5, 6, 1, 3, 'hilt'), cube(-0.5, 1, -1, 1, 5, 2, 'grip'),
                                 cube(-1, 6, -1.5, 2, 2, 3, 'hilt')], anim='sway')
    return m


def bastet():
    mats = {'plate': {'base': 0xECEFF1, 'pattern': 'spots', 'pc': 0xCFD8DC}, 'slot': {'base': 0x212121}, 'eye': {'base': 0xE53935}}
    m = Model('bastet', mats)
    m.part('socket', (0, 14, 0), [cube(-4, -6, -1, 8, 12, 2, 'plate', feat={'eyes': 1, 'eye': 0xE53935, 'pupil': 0x111111, 'eye_y': 0.2}),
                                  cube(-2, -1, -2, 1, 3, 1, 'slot'), cube(1, -1, -2, 1, 3, 1, 'slot'), cube(-1, 3, -2, 2, 1, 1, 'slot')], anim='bob')
    return m


def horus():
    mats = {'ice': {'base': 0xB3E5FC, 'pattern': 'cracks', 'pc': 0xE1F5FE, 'alpha': 210}, 'claw': {'base': 0x81D4FA, 'alpha': 220}}
    m = Model('horus', mats, translucent=True)
    m.part('body', (0, 10, 0), [cube(-4, -4, -6, 8, 8, 12, 'ice')], anim='bob')
    m.part('head', (0, 7, -6), [cube(-3, -4, -5, 6, 6, 6, 'ice', feat={'eyes': 2, 'eye': 0x01579B, 'pupil': 0x01579B}),
                                cube(-1, -1, -8, 2, 2, 3, 'claw')], parent='body', anim='head')
    m.part('wing_r', (-4, 7, -3), [cube(-16, 0, 0, 16, 1, 9, 'ice')], parent='body', anim='wing', amp=1.5)
    m.part('wing_l', (4, 7, -3), [cube(0, 0, 0, 16, 1, 9, 'ice')], parent='body', anim='wing', phase=3.14, amp=1.5)
    m.part('tail', (0, 9, 6), [cube(-3, -1, 0, 6, 1, 8, 'ice')], parent='body', anim='tail', amp=0.5)
    m.part('claws', (0, 14, 0), [cube(-3, 0, -2, 2, 4, 2, 'claw'), cube(1, 0, -2, 2, 4, 2, 'claw')], parent='body')
    return m


def cream():
    mats = {'body': {'base': 0x5D4037, 'pattern': 'spots', 'pc': 0x8D6E63}, 'void': {'base': 0x0B0B12}, 'horn': {'base': 0xD7CCC8},
            'stud': {'base': 0xFFD54F}}
    m = Model('cream', mats)
    m.part('body', (0, 2, 0), [cube(-5, 0, -3, 10, 12, 6, 'body'), cube(-4, 12, -2, 8, 10, 4, 'body')], anim='sway')
    m.part('head', (0, 2, 0), [cube(-5, -10, -5, 10, 10, 10, 'body', feat={'eyes': 2, 'eye': 0xFFFFFF, 'pupil': 0x111111, 'eye_y': 0.25}),
                               cube(-4, -5, -6, 8, 5, 1, 'void'), cube(-7, -12, -1, 2, 6, 2, 'horn'), cube(5, -12, -1, 2, 6, 2, 'horn'),
                               cube(-1, -12, -4, 2, 2, 2, 'stud')], parent='body', anim='head')
    m.part('arm_r', (-6, 3, 0), [cube(-3, -1, -2, 3, 12, 4, 'body')], parent='body', anim='arm_a', amp=0.3)
    m.part('arm_l', (6, 3, 0), [cube(0, -1, -2, 3, 12, 4, 'body')], parent='body', anim='arm_b', amp=0.3)
    return m


MODELS = [magicians_red, hermit_purple, the_fool, tower_of_gray, strength, yellow_temperance, emperor, wheel_of_fortune, justice, sun,
          death_thirteen, geb, tohth, anubis, bastet, horus, cream]


def main():
    for build in MODELS:
        m = build()
        parts, cubes, used = export(m, assets=ASSETS, ns='jojorpg', path=f'stand/{m.id}')
        print(f'{m.id:18s} {parts:3d} parts {cubes:3d} boxes, texture rows {used}/{TEX}')


if __name__ == '__main__':
    main()
