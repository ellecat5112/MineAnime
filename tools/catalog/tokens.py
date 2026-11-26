"""Technique token sprites (AHZNB-style jutsu items): one 32x32 token per nature release / school.

Run via build.py (or directly). Tokens are declared in narutorpg's NarutoTokens.java with the same ids.
"""
import math
import os

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
ITEM_DIR = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg', 'textures', 'item')

# item id -> (rim colour, inner colour, glyph)
TOKENS = {
    'fire_release': (0xB71C1C, 0xFF8A1E, 'flame'), 'wind_release': (0x2E7D32, 0xB2FF59, 'swirl'),
    'lightning_release': (0x1A237E, 0x80D8FF, 'bolt'), 'earth_release': (0x4E342E, 0xA1887F, 'mountain'),
    'water_release': (0x0D47A1, 0x40A8FF, 'drop'), 'wood_release': (0x33691E, 0x8BC34A, 'leaf'),
    'ice_release': (0x01579B, 0xB3F0FF, 'flake'), 'lava_release': (0x5D1A00, 0xFF5722, 'flame'),
    'magnet_release': (0x5D4037, 0xD7A86E, 'dots'), 'storm_release': (0x006064, 0x26C6DA, 'bolt'),
    'boil_release': (0x880E4F, 0xFFAB91, 'drop'), 'scorch_release': (0xBF360C, 0xFFCC80, 'swirl'),
    'explosion_release': (0xF57F17, 0xFFEE58, 'burst'), 'dust_release': (0x616161, 0xFFFFFF, 'cube'),
    'yin_release': (0x12001F, 0x7B1FA2, 'moon'), 'yang_release': (0xFF8F00, 0xFFE57F, 'sun'),
    'mud_release': (0x3E2723, 0x8D6E63, 'drop'), 'yin_yang_release': (0x212121, 0xFAFAFA, 'moon'),
    'crystal_release': (0x880E4F, 0xF8BBD0, 'cube'), 'dark_release': (0x0A0018, 0x5E35B1, 'moon'),
    'steel_release': (0x37474F, 0xCFD8DC, 'cube'), 'swift_release': (0xF9A825, 0xFFF59D, 'bolt'),
    'typhoon_release': (0x004D40, 0x80CBC4, 'swirl'),
    'ninjutsu': (0x263238, 0x90CAF9, 'sign'), 'taijutsu': (0x1B5E20, 0x66BB6A, 'fist'),
    'genjutsu': (0x4A148C, 0xF06292, 'eye'), 'fuinjutsu': (0x3E2723, 0xFFD54F, 'seal'),
    'medical_ninjutsu': (0x1B5E20, 0x76FF03, 'cross'), 'hiden': (0x212121, 0xBDBDBD, 'scroll'),
    'dojutsu': (0x7F0000, 0xE53935, 'tomoe'),
}


# admin seals: gold-framed black talisman with an action glyph
ADMIN = {'admin_seal_rank_up': 'chevron', 'admin_seal_clan_level': 'tomoe', 'admin_seal_mastery': 'flame', 'admin_seal_skill_points': 'dots',
         'admin_seal_natures': 'swirl', 'admin_seal_reputation': 'leaf', 'admin_seal_refill': 'drop', 'admin_seal_max_all': 'sun'}


def rgb(h, a=255):
    return ((h >> 16) & 255, (h >> 8) & 255, h & 255, a)


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3)) + (255,)


def build():
    from PIL import Image, ImageDraw
    os.makedirs(ITEM_DIR, exist_ok=True)
    for name, (rim, inner, glyph) in TOKENS.items():
        im = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        R, I = rgb(rim), rgb(inner)
        dark = mix(R, (0, 0, 0), 0.4)
        light = mix(I, (255, 255, 255), 0.45)
        # medallion: dark rim, coloured face, highlight arc, cord loop
        d.ellipse([2, 3, 29, 30], fill=dark)
        d.ellipse([3, 4, 28, 29], fill=R)
        d.ellipse([6, 7, 25, 26], fill=mix(I, R, 0.35))
        d.arc([3, 4, 28, 29], 200, 290, fill=light)
        d.rectangle([14, 0, 17, 3], fill=(200, 170, 90, 255))
        g = (250, 250, 250, 255) if sum(I[:3]) < 450 else mix(R, (0, 0, 0), 0.5)
        cx, cy = 15.5, 16.5
        if glyph == 'flame':
            d.polygon([(cx, 9), (cx + 5, 17), (cx + 3, 23), (cx - 3, 23), (cx - 5, 17)], fill=g)
            d.polygon([(cx, 14), (cx + 2, 19), (cx, 22), (cx - 2, 19)], fill=I)
        elif glyph == 'swirl':
            for k in range(3):
                a = k * 2.1
                d.arc([cx - 6 + k, cy - 6 + k, cx + 6 - k, cy + 6 - k], math.degrees(a), math.degrees(a) + 200, fill=g, width=2)
        elif glyph == 'bolt':
            d.polygon([(cx + 2, 8), (cx - 4, 17), (cx, 17), (cx - 2, 25), (cx + 5, 14), (cx + 1, 14)], fill=g)
        elif glyph == 'mountain':
            d.polygon([(8, 23), (14, 11), (18, 17), (20, 14), (24, 23)], fill=g)
        elif glyph in ('drop',):
            d.polygon([(cx, 9), (cx + 5, 18), (cx, 23), (cx - 5, 18)], fill=g)
            d.ellipse([cx - 5, 14, cx + 5, 23], fill=g)
        elif glyph == 'leaf':
            d.ellipse([10, 10, 21, 23], fill=g)
            d.line([(cx, 10), (cx, 24)], fill=I, width=1)
        elif glyph == 'flake':
            for k in range(3):
                a = math.radians(k * 60)
                d.line([(cx - 7 * math.cos(a), cy - 7 * math.sin(a)), (cx + 7 * math.cos(a), cy + 7 * math.sin(a))], fill=g, width=2)
        elif glyph == 'dots':
            for k in range(6):
                a = k * math.pi / 3
                d.ellipse([cx + 5 * math.cos(a) - 1.5, cy + 5 * math.sin(a) - 1.5, cx + 5 * math.cos(a) + 1.5, cy + 5 * math.sin(a) + 1.5], fill=g)
        elif glyph == 'burst':
            for k in range(8):
                a = k * math.pi / 4
                d.line([(cx, cy), (cx + 7 * math.cos(a), cy + 7 * math.sin(a))], fill=g, width=2)
        elif glyph == 'cube':
            d.polygon([(cx, 9), (cx + 6, 12), (cx + 6, 20), (cx, 23), (cx - 6, 20), (cx - 6, 12)], fill=g)
            d.line([(cx, 16), (cx, 23)], fill=I)
            d.line([(cx - 6, 12), (cx, 16), (cx + 6, 12)], fill=I)
        elif glyph == 'moon':
            d.ellipse([9, 10, 22, 23], fill=g)
            d.ellipse([12, 8, 25, 21], fill=mix(I, R, 0.35))
        elif glyph == 'sun':
            d.ellipse([11, 12, 20, 21], fill=g)
            for k in range(8):
                a = k * math.pi / 4
                d.line([(cx + 6 * math.cos(a), cy + 6 * math.sin(a)), (cx + 8 * math.cos(a), cy + 8 * math.sin(a))], fill=g)
        elif glyph == 'sign':     # two hands joined in a seal
            d.rectangle([12, 10, 14, 22], fill=g)
            d.rectangle([17, 10, 19, 22], fill=g)
            d.rectangle([12, 15, 19, 18], fill=g)
        elif glyph == 'fist':
            d.rounded_rectangle([10, 11, 21, 21], 2, fill=g)
            for x in (12, 15, 18):
                d.line([(x, 11), (x, 15)], fill=I)
        elif glyph == 'eye':
            d.ellipse([8, 12, 23, 21], fill=g)
            d.ellipse([12, 13, 19, 20], fill=I)
            d.ellipse([14, 15, 17, 18], fill=(20, 0, 20, 255))
        elif glyph == 'seal':
            d.rectangle([11, 9, 20, 24], fill=(240, 228, 196, 255))
            d.line([(13, 12), (18, 12)], fill=(160, 30, 30, 255))
            d.ellipse([12, 15, 19, 22], outline=(160, 30, 30, 255))
        elif glyph == 'cross':
            d.rectangle([14, 9, 17, 24], fill=g)
            d.rectangle([8, 15, 23, 18], fill=g)
        elif glyph == 'scroll':
            d.rectangle([9, 11, 22, 21], fill=(236, 226, 196, 255))
            d.rectangle([8, 10, 10, 22], fill=(120, 72, 40, 255))
            d.rectangle([21, 10, 23, 22], fill=(120, 72, 40, 255))
        elif glyph == 'tomoe':
            d.ellipse([9, 10, 22, 23], fill=I)
            d.ellipse([14, 15, 17, 18], fill=(10, 0, 0, 255))
            for k in range(3):
                a = k * 2 * math.pi / 3
                x, y = cx + 4.2 * math.cos(a), cy + 4.2 * math.sin(a)
                d.ellipse([x - 1.6, y - 1.6, x + 1.6, y + 1.6], fill=(10, 0, 0, 255))
        im.save(os.path.join(ITEM_DIR, name + '.png'), optimize=True)
    for name, glyph in ADMIN.items():
        im = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        d.rectangle([5, 2, 26, 29], fill=(212, 160, 23, 255))
        d.rectangle([6, 3, 25, 28], fill=(24, 18, 28, 255))
        d.rectangle([8, 5, 23, 26], outline=(160, 30, 40, 255))
        g = (255, 214, 90, 255)
        cx, cy = 15.5, 15.5
        if glyph == 'chevron':
            for k in range(3):
                d.line([(10, 20 - k * 5), (cx, 15 - k * 5), (21, 20 - k * 5)], fill=g, width=2)
        elif glyph == 'tomoe':
            d.ellipse([10, 10, 21, 21], outline=g, width=2)
            for k in range(3):
                a = k * 2 * math.pi / 3
                d.ellipse([cx + 3 * math.cos(a) - 1.5, cy + 3 * math.sin(a) - 1.5, cx + 3 * math.cos(a) + 1.5, cy + 3 * math.sin(a) + 1.5], fill=g)
        elif glyph == 'flame':
            d.polygon([(cx, 8), (cx + 5, 17), (cx + 3, 23), (cx - 3, 23), (cx - 5, 17)], fill=g)
        elif glyph == 'dots':
            for k in range(5):
                a = k * 2 * math.pi / 5 - math.pi / 2
                d.ellipse([cx + 5 * math.cos(a) - 1.5, cy + 5 * math.sin(a) - 1.5, cx + 5 * math.cos(a) + 1.5, cy + 5 * math.sin(a) + 1.5], fill=g)
        elif glyph == 'swirl':
            for k in range(3):
                d.arc([cx - 6 + k * 2, cy - 6 + k * 2, cx + 6 - k * 2, cy + 6 - k * 2], k * 90, k * 90 + 220, fill=g, width=2)
        elif glyph == 'leaf':
            d.ellipse([10, 9, 21, 22], fill=g)
            d.line([(cx, 9), (cx, 24)], fill=(24, 18, 28, 255))
        elif glyph == 'drop':
            d.polygon([(cx, 8), (cx + 5, 17), (cx, 22), (cx - 5, 17)], fill=(90, 190, 255, 255))
            d.ellipse([cx - 5, 13, cx + 5, 22], fill=(90, 190, 255, 255))
        elif glyph == 'sun':
            d.ellipse([11, 11, 20, 20], fill=g)
            for k in range(8):
                a = k * math.pi / 4
                d.line([(cx + 6 * math.cos(a), cy + 6 * math.sin(a)), (cx + 9 * math.cos(a), cy + 9 * math.sin(a))], fill=g, width=2)
        im.save(os.path.join(ITEM_DIR, name + '.png'), optimize=True)
    print(f'{len(TOKENS)} technique tokens + {len(ADMIN)} admin seals -> {ITEM_DIR}')


if __name__ == '__main__':
    build()
