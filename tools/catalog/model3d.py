"""
Low-poly 3D held models for NarutoRPG weapons (Minecraft JSON element models, Blockbench style).
Models are authored vertically: grip at the bottom (y ~ 0-6), tip pointing +Y, centred on x = z = 8.
Each model gets a 16x16 palette texture (4x4 px colour cells) derived from the item's sprite colours,
so models stay tiny and always match their 2D icon.
"""
import math
from PIL import Image
from spritegen import rgb, lighter, darker

# ---------------------------------------------------------------- palette
# cell index -> meaning
C0, C0L, C0D, EDGE, C1, C1L, C1D, STRIPE1, C2, C2L, C2D, STRIPE2, BLACK, STEEL, WOOD, GLOW = range(16)

def palette(colors):
    c = [rgb(x) for x in colors] + [rgb('#3A3D44'), rgb('#C3CAD3'), rgb('#8B5A2B')]
    c0, c1, c2 = c[0], c[1], c[2]
    cells = [c0, lighter(c0, .25), darker(c0, .25), lighter(c0, .6), c1, lighter(c1, .25), darker(c1, .3), None,
             c2, lighter(c2, .25), darker(c2, .3), None, (28, 28, 32), (150, 156, 166), (125, 82, 44), lighter(c0, .45)]
    img = Image.new('RGBA', (16, 16))
    for k, col in enumerate(cells):
        cx, cy = (k % 4) * 4, (k // 4) * 4
        for y in range(4):
            for x in range(4):
                if k == STRIPE1: px = c1 if y % 2 == 0 else darker(c1, .35)
                elif k == STRIPE2: px = c2 if y % 2 == 0 else darker(c2, .35)
                else: px = col
                img.putpixel((cx + x, cy + y), (*px, 255))
    return img

def uv(cell):
    return [(cell % 4) * 4, (cell // 4) * 4, (cell % 4) * 4 + 4, (cell // 4) * 4 + 4]

# ---------------------------------------------------------------- geometry
class M:
    def __init__(self):
        self.el = []

    def box(self, fr, to, cell, rot=None, sides=None, top=None):
        fr = [round(max(-16, min(32, v)), 3) for v in fr]
        to = [round(max(-16, min(32, v)), 3) for v in to]
        for i in range(3):
            if to[i] - fr[i] < 0.01: to[i] = fr[i] + 0.01
        faces = {}
        for f in ('north', 'south', 'east', 'west', 'up', 'down'):
            cl = cell
            if sides is not None and f in ('north', 'south', 'east', 'west'): cl = sides
            if top is not None and f in ('up', 'down'): cl = top
            faces[f] = {"uv": uv(cl), "texture": "#p"}
        e = {"from": fr, "to": to, "faces": faces}
        if rot:
            axis, angle, origin = rot
            e["rotation"] = {"origin": origin, "axis": axis, "angle": angle}
        self.el.append(e)
        return self

    # centred helpers (x/z centred on 8)
    def col(self, y0, y1, w, d, cell, x=8, z=8, **kw):
        return self.box([x - w / 2, y0, z - d / 2], [x + w / 2, y1, z + d / 2], cell, **kw)

    def rotcol(self, y0, y1, w, d, cell, angle, axis='z', x=8, z=8, oy=None, **kw):
        return self.col(y0, y1, w, d, cell, x, z, rot=(axis, angle, [x, oy if oy is not None else y0, z]), **kw)

T = 1.0  # default blade thickness

# ---------------------------------------------------------------- blades
def grip(m, y0, y1, w=1.6, stripe=STRIPE2, pommel=None):
    m.col(y0, y1, w, w, C2, sides=stripe)
    if pommel is not None: m.col(y0 - 0.8, y0, w + 0.6, w + 0.6, pommel)

def kunai(m, long=False):
    s = 1.35 if long else 1.0
    for (x0, y0, x1, y1) in [(6.5, -1, 9.5, -0.4), (6.5, 1.6, 9.5, 2.2), (6.5, -1, 7.1, 2.2), (8.9, -1, 9.5, 2.2)]:
        m.box([x0, y0, 7.7], [x1, y1, 8.3], C2)                                   # ring pommel
    m.col(2.2, 8, 1.5, 1.5, C1, sides=STRIPE1)                                     # bandaged handle
    m.col(8, 9, 2.4, 1.2, C0D)                                                     # blade root
    m.col(9, 9 + 2.5 * s, 3.8, T, C0)                                              # widest
    m.col(9 + 2.5 * s, 9 + 5 * s, 2.6, T, C0)
    m.col(9 + 5 * s, 9 + 7 * s, 1.4, T * 0.8, C0)
    m.col(9 + 1, 9 + 6.5 * s, 0.4, T + 0.2, EDGE)                                  # central ridge
    m.rotcol(9 + 7 * s, 9 + 8 * s, 1.0, T * 0.6, C0L, 45, oy=9 + 7 * s)            # tip

def tri_kunai(m):
    kunai(m)
    m.rotcol(10, 15, 1.4, T, C0, 45, x=6.2, oy=10)
    m.rotcol(10, 15, 1.4, T, C0, -45, x=9.8, oy=10)
    for y in (3, 5, 7): m.col(y, y + 0.6, 1.7, 1.7, C2)

def katana(m, length=20, width=1.8, curve=True, guard=C1, gripc=STRIPE2, tsuba=3.4, pommel=C1D):
    grip(m, 0, 6, 1.7, gripc, pommel)
    m.col(6, 6.7, tsuba, tsuba, guard)                                             # tsuba
    m.col(6.7, 7.6, width + 0.4, T + 0.2, C1L)                                     # habaki
    segs = 3
    seg = length / segs
    for k in range(segs):
        y0 = 7.6 + k * seg
        ang = 0 if not curve else [0, 0, -22.5][k] if k < 2 else -22.5
        if curve and k == 2:
            m.rotcol(y0, y0 + seg, width * 0.9, T * 0.8, C0, -22.5, oy=y0, x=8 + 0.0)
            m.rotcol(y0 + 0.5, y0 + seg, 0.35, T, EDGE, -22.5, oy=y0, x=8 + width * 0.35)
        else:
            m.col(y0, y0 + seg, width, T * 0.8, C0)
            m.col(y0, y0 + seg, 0.35, T, EDGE, x=8 + width * 0.35)
    tip_y = 7.6 + length
    if curve:  # continue the last (rotated) segment so the tip follows the curve
        y0 = 7.6 + 2 * seg
        m.rotcol(tip_y, tip_y + 1.6, width * 0.55, T * 0.7, C0L, -22.5, oy=y0)
    else:
        m.rotcol(tip_y, tip_y + 1.4, width * 0.6, T * 0.7, C0L, 45, oy=tip_y)

def straight_sword(m): katana(m, 20, 2.2, False, C1, STRIPE2, 4.4)
def curved_sword(m): katana(m, 19, 2.4, True)
def tanto(m): katana(m, 11, 1.6, False, C2, STRIPE1, 2.2)
def sabre_glow(m):
    katana(m, 13, 1.6, False, C2, STRIPE2, 2.2)
    m.col(8, 21, 2.6, 0.2, GLOW)                                                    # white chakra glow
def broad_sword(m):
    grip(m, -2, 4, 1.8, STRIPE2, C1)
    m.col(4, 5, 7.5, 1.6, C1)
    m.col(5, 25, 9, T * 1.1, C0)
    m.col(5, 25, 0.4, T * 1.3, EDGE, x=3.7)
    m.box([9.5, 21, 7.4], [12.5, 24, 8.6], BLACK)                                   # punched hole
    m.rotcol(25, 27.5, 6.3, T, C0, 45, oy=25, x=8)
def cleaver(m):
    grip(m, -1, 6, 1.8, STRIPE2, C1)
    m.col(6, 24, 10, T * 1.1, C0)
    for y in (11, 15.5, 20): m.col(y, y + 0.4, 10.2, T * 1.3, C0D)                  # folding hinges
    m.col(6, 24, 0.4, T * 1.3, EDGE, x=3.2)
def shark_sword(m):
    grip(m, -2, 4, 2.0, STRIPE1)
    m.col(4, 26, 7, 3, C0)
    for y in range(5, 26, 3):
        for x in (4.2, 11.8): m.col(y, y + 1.2, 1.2, 1.2, C2, x=x)                 # scales
    for y in (8, 14, 20): m.col(y, y + 1.4, 7.4, 3.4, C1)                           # bandage straps
def jagged_sword(m):
    katana(m, 19, 2.6, False, C1, STRIPE2, 3.2)
    for y in range(10, 25, 3): m.rotcol(y, y + 2, 1.2, T * 0.8, C0, 45, x=6.4, oy=y)
def lightning_sword(m):
    grip(m, 0, 6, 1.7, STRIPE2, C2)
    y = 6.5
    for k in range(6):
        a = 22.5 if k % 2 else -22.5
        m.rotcol(y, y + 4, 1.8, 1.8, C0, a, oy=y)
        m.rotcol(y + 0.5, y + 3.5, 0.6, 2.0, GLOW, a, oy=y)
        y += 3.6
def needle_sword(m):
    grip(m, 0, 6, 1.6, STRIPE1)
    m.col(6, 28, 0.9, 0.9, C0)
    m.box([7.2, 27, 7.8], [8.8, 28.6, 8.2], C0L)                                    # eye of the needle
    for k in range(5): m.col(-4 + k * 0.8, -3.2 + k * 0.8, 0.3, 0.3, C1, x=8 + (k % 2) * 0.6)  # thread
def helix_sword(m):
    grip(m, -2, 4, 2.0, STRIPE1, C0D)
    for k in range(9):
        y = 4 + k * 2.7
        m.rotcol(y, y + 2.8, 2.6, 1.2, C0 if k % 2 else C1, 22.5 if k % 2 else -22.5, axis='y', oy=y)
        m.rotcol(y, y + 2.8, 1.2, 2.6, C1 if k % 2 else C0, 22.5 if k % 2 else -22.5, axis='y', oy=y)
def stone_sword(m):
    katana(m, 18, 2.8, False, C0D, STRIPE1, 3.0)
    for (x0, y0, x1, y1) in [(6, -3, 10, -2.4), (6, -0.4, 10, 0.2), (6, -3, 6.6, 0.2), (9.4, -3, 10, 0.2)]:
        m.box([x0, y0, 7.7], [x1, y1, 8.3], C0)
def tri_sword(m):
    grip(m, 0, 6, 1.8, STRIPE2)
    m.col(6, 7, 6, 1.5, C1)
    for x, h in ((5.6, 15), (8, 20), (10.4, 15)):
        m.col(7, 7 + h, 1.4, T, C0, x=x)
        m.rotcol(7 + h, 8.4 + h, 1.0, T * 0.6, C0L, 45, x=x, oy=7 + h)
def twin_swords(m):
    for x in (6.2, 9.8):
        m.col(0, 5, 1.4, 1.4, C1, x=x, sides=STRIPE1)
        m.col(5, 5.6, 3, 1.6, C1D, x=x)
        m.col(5.6, 20, 1.6, T * 0.8, C0, x=x)
        m.rotcol(20, 21.4, 1.1, T * 0.6, C0L, 45, x=x, oy=20)
def scroll_sword(m):
    straight_sword(m)
    for y in (10, 14, 18, 22): m.col(y, y + 2, 3.0, 1.6, C0 if y % 8 == 2 else C1)
def jar_sword(m):
    m.box([5, -2, 5], [11, 4, 11], C0)                                              # sake gourd
    m.box([6, 4, 6], [10, 7, 10], C0D)
    m.col(7, 8, 2, 2, C1)
    m.col(8, 28, 2.4, 0.6, C1L)                                                     # ethereal blade
    m.col(9, 27, 1.0, 0.8, GLOW)
def scalpel(m):
    m.col(0, 7, 1.0, 0.6, C1)
    m.col(7, 10, 1.4, 0.4, C0)
    m.rotcol(10, 11.2, 0.9, 0.3, C0L, 45, oy=10)
def katar(m):
    for x in (5.5, 10.5): m.col(-1, 5, 0.8, 0.8, C1, x=x)
    m.box([5.5, 1.5, 7.6], [10.5, 2.3, 8.4], C1)                                    # cross grip
    m.col(5, 15, 3.6, T, C0)
    m.rotcol(15, 17, 2.2, T * 0.7, C0L, 45, oy=15)

# ---------------------------------------------------------------- polearms / staves (shaft y -10 .. 22)
def shaft(m, y0=-10, y1=21, w=1.3, cell=C1, bands=()):
    m.col(y0, y1, w, w, cell)
    for b in bands: m.col(b, b + 0.8, w + 0.4, w + 0.4, C1D)

def spear(m):
    shaft(m, bands=(-10, 19))
    m.col(20.5, 22, 2.2, 1.4, C0D)
    m.col(22, 26, 3.0, T, C0); m.col(26, 29, 1.8, T, C0); m.rotcol(29, 30.4, 1.0, 0.6, C0L, 45, oy=29)
def trident(m):
    shaft(m, y1=20)
    m.col(20, 21, 6, 1.2, C0)
    for x, h in ((5.4, 6), (8, 9), (10.6, 6)): m.col(21, 21 + h, 1.0, 1.0, C0, x=x)
def cross_spear(m):
    shaft(m, y1=19)
    m.col(19, 31, 1.6, 1.6, C0)
    m.box([2, 24, 7.2], [14, 25.6, 8.8], C0)
def retract_spear(m):
    for k, w in enumerate((1.8, 1.5, 1.2)): m.col(-10 + k * 10, 0 + k * 10, w, w, C0 if k % 2 == 0 else C0D)
    m.col(20, 24, 2.4, T, C0L); m.col(24, 28, 1.4, T, C0L); m.col(-11, -10, 2.2, 2.2, C1)
def harpoon(m):
    spear(m)
    for y, x, a in ((23, 5.8, 45), (23, 10.2, -45)): m.rotcol(y, y + 2.5, 0.8, 0.8, C0, a, x=x, oy=y)
def bo(m):
    m.col(-12, 28, 1.6, 1.6, C0)
    for y in (-12, 26): m.col(y, y + 2, 2.0, 2.0, C1)
def ringed_staff(m):
    shaft(m, y1=22)
    m.col(22, 29, 1.0, 1.0, C0)
    for (x0, y0, x1, y1) in [(4, 22, 12, 22.8), (4, 29, 12, 29.8), (4, 22, 4.8, 29.8), (11.2, 22, 12, 29.8)]:
        m.box([x0, y0, 7.6], [x1, y1, 8.4], C0)
    for (x, y) in ((3.4, 24), (12.6, 24), (3.4, 27.5), (12.6, 27.5)): m.box([x - 0.7, y - 0.7, 7.3], [x + 0.7, y + 0.7, 8.7], C0L)
def cane_ring(m):
    m.col(-10, 22, 1.2, 1.2, C0)
    for (x0, y0, x1, y1) in [(5, 22, 11, 22.8), (5, 28, 11, 28.8), (5, 22, 5.8, 28.8), (10.2, 22, 11, 28.8)]:
        m.box([x0, y0, 7.6], [x1, y1, 8.4], C0)
    m.box([7, 24.4, 7], [9, 26.4, 9], C1)
def scythe(m, n=1):
    shaft(m, -10, 24, 1.4, C1)
    for k in range(n):
        y = 22 - k * 3.5
        m.box([-2 + k, y, 7.5], [8, y + 1.6, 8.5], C0)                               # blade spine
        m.rotcol(y - 3, y + 1.6, 1.4, 1.0, C0, 22.5, x=-1 + k, oy=y + 1.6)            # hooked tip
        m.box([-1 + k, y - 0.4, 7.6], [8, y, 8.4], EDGE)
def kama(m):
    m.col(-2, 10, 1.4, 1.4, C1, sides=C1D)
    m.box([1, 10, 7.5], [8.5, 11.6, 8.5], C0)
    m.rotcol(7, 11.6, 1.2, 0.9, C0, 22.5, x=1.5, oy=11.6)
def kusarigama(m):
    kama(m)
    for k in range(7): m.col(-2.4 - k * 1.2, -1.6 - k * 1.2, 0.8 if k % 2 else 0.3, 0.3 if k % 2 else 0.8, C2, x=8 + (k % 3 - 1) * 0.4)
    m.box([7, -12, 7], [9, -10, 9], C2)                                             # weight
def chain(m):
    for k in range(14):
        y = -10 + k * 2.3
        if k % 2: m.box([7.2, y, 7.7], [8.8, y + 2.2, 8.3], C0)
        else: m.box([7.7, y, 7.2], [8.3, y + 2.2, 8.8], C0L)
    m.box([6.5, 22, 6.5], [9.5, 25, 9.5], C0D)
def chain_staff(m):
    m.col(0, 14, 1.8, 1.8, C1)
    for side in (-1, 1):
        y = 14 if side > 0 else -1
        for k in range(4): m.col(y + side * k * 1.4, y + side * k * 1.4 + 1.2, 0.6, 0.6, C0)
        m.box([6.8, y + side * 6, 6.8], [9.2, y + side * 6 + 2.4, 9.2], C0)
def hoe(m):
    shaft(m, -10, 22, 1.3)
    m.box([1, 20, 7.5], [8, 22.5, 8.5], C0)
def sasumata(m):
    shaft(m, -10, 20)
    m.box([3, 20, 7.4], [13, 21.2, 8.6], C0)
    for x in (3.6, 12.4): m.col(21.2, 26, 1.0, 1.0, C0, x=x)
def fishing_rod(m):
    m.col(-6, 6, 1.4, 1.4, C0); m.col(6, 18, 1.0, 1.0, C0); m.col(18, 28, 0.6, 0.6, C0L)
    m.col(28, 28.4, 0.3, 0.3, C1)
def whip(m):
    m.col(-2, 6, 1.8, 1.8, C1, sides=STRIPE1)
    y = 6
    for k in range(8):
        m.rotcol(y, y + 3, 0.9 - k * 0.07, 0.9 - k * 0.07, C0, 22.5 if k % 2 else -22.5, oy=y)
        y += 2.8
def three_section(m):
    for k in range(3):
        m.col(-10 + k * 13, 1 + k * 13, 1.6, 1.6, C0)
        m.col(-10 + k * 13, -9 + k * 13, 2.0, 2.0, C1)
def nunchaku(m):
    m.col(-6, 6, 1.8, 1.8, C0); m.col(-6, -5, 2.2, 2.2, C1)
    for k in range(3): m.col(6 + k, 6.8 + k, 0.4, 0.4, C1)
    m.rotcol(9, 21, 1.8, 1.8, C0, -22.5, oy=9)
def club(m):
    m.col(-4, 6, 1.8, 1.8, C0D)
    for k, w in enumerate((2.6, 3.4, 4.2, 4.6, 4.4)): m.col(6 + k * 3.4, 9.4 + k * 3.4, w, w, C0)
    for (x, y, z) in ((5.6, 12, 8), (10.4, 16, 8), (8, 19, 5.6), (8, 14, 10.4)): m.box([x - .5, y, z - .5], [x + .5, y + 1, z + .5], C1)
def mace(m):
    m.col(-6, 14, 1.4, 1.4, C2)
    m.box([4, 14, 4], [12, 22, 12], C0)
    m.box([5, 13, 5], [11, 23, 11], C0)
    for (x, y, z) in ((3, 18, 8), (13, 18, 8), (8, 18, 3), (8, 18, 13), (8, 24, 8)):
        m.box([x - .7, y - .7, z - .7], [x + .7, y + .7, z + .7], C1)
def claw_club(m):
    club(m)
    m.box([7, 23, 7], [9, 25, 9], C1)
    for y, x in ((10, 3.2), (16, 12.8)): m.box([x - 0.6, y, 7.6], [x + 0.6, y + 2.4, 8.4], C2)
def flail(m):
    m.col(-6, 8, 1.6, 1.6, C1)
    for k in range(4): m.col(8 + k * 1.3, 9 + k * 1.3, 0.5, 0.5, C0)
    m.box([5, 14, 5], [11, 20, 11], C0)
    for (x, y, z) in ((4, 17, 8), (12, 17, 8), (8, 21, 8), (8, 17, 4), (8, 17, 12)):
        m.box([x - .5, y - .5, z - .5], [x + .5, y + .5, z + .5], C0L)
def jidanda(m):
    for k in range(6): m.col(-10 + k * 1.6, -9 + k * 1.6, 0.6, 0.6, C1)
    m.box([2, 0, 2], [14, 12, 14], C0)
    m.box([3, -1, 3], [13, 13, 13], C0)
    for (x, y, z) in ((0.5, 6, 8), (15.5, 6, 8), (8, 6, 0.5), (8, 6, 15.5), (8, 14.5, 8), (4, 12, 4), (12, 12, 12)):
        m.box([x - 1, y - 1, z - 1], [x + 1, y + 1, z + 1], C1)
    m.box([6, 4, 1.8], [10, 8, 2.4], C2)                                            # explosive seal
def hammer_axe(m):
    shaft(m, -10, 22, 1.5, C1)
    m.box([1, 17, 7.3], [8, 24, 8.7], C0)                                           # axe blade
    m.box([0.5, 17.5, 7.4], [1.2, 23.5, 8.6], EDGE)
    m.box([8, 18.5, 6], [14, 22.5, 10], C2)                                         # hammer head
def axe(m):
    shaft(m, -8, 20, 1.5, C1)
    m.box([2, 15, 7.4], [8, 21, 8.6], C0)
    m.box([1.4, 15.4, 7.5], [2.1, 20.6, 8.5], EDGE)
def tonfa(m):
    m.col(-8, 16, 1.9, 1.9, C0)
    m.box([8, 9, 7.1], [12, 10.8, 8.9], C0)
    m.box([11, 9, 7.1], [12.6, 14, 8.9], C0D)

# ---------------------------------------------------------------- thrown / flat
def shuriken(m):
    m.box([6.5, 6.5, 7.6], [9.5, 9.5, 8.4], C0D)
    for a in (0, 90):
        m.box([1, 7.3, 7.7], [15, 8.7, 8.3], C0, rot=('z', 45 if a else -45, [8, 8, 8]))
    m.box([2, 7.4, 7.7], [14, 8.6, 8.3], C0)
    m.box([7.4, 2, 7.7], [8.6, 14, 8.3], C0)
    m.box([7.4, 7.4, 7.5], [8.6, 8.6, 8.5], BLACK)
def fuma(m):
    m.box([5.5, 5.5, 7.3], [10.5, 10.5, 8.7], C1)
    m.box([6.8, 6.8, 7.1], [9.2, 9.2, 8.9], C1D)
    for (fr, to) in (([-6, 6.8, 7.6], [8, 9.2, 8.4]), ([8, 6.8, 7.6], [22, 9.2, 8.4]), ([6.8, -6, 7.6], [9.2, 8, 8.4]), ([6.8, 8, 7.6], [9.2, 22, 8.4])):
        m.box(fr, to, C0, rot=('z', 22.5, [8, 8, 8]))
    for (fr, to) in (([-5, 7.6, 7.7], [8, 8.4, 8.3]), ([8, 7.6, 7.7], [21, 8.4, 8.3])):
        m.box(fr, to, EDGE, rot=('z', 22.5, [8, 8, 8]))
def senbon(m):
    for x in (6.8, 8, 9.2):
        m.col(0, 17, 0.35, 0.35, C0, x=x)
        m.col(17, 17.6, 0.2, 0.2, C0L, x=x)
def needles(m):
    senbon(m)
    for x in (6.8, 8, 9.2): m.col(1, 3, 0.6, 0.6, C1, x=x)
def caltrops(m):
    for (x, z) in ((5, 5), (11, 6), (8, 11)):
        for a in (-45, 0, 45):
            m.box([x - 0.25, 0, z - 0.25], [x + 0.25, 3.5, z + 0.25], C0, rot=('x' if a else 'z', a if a else 0, [x, 0, z]))
        m.box([x - 2, 0, z - 0.25], [x + 2, 0.5, z + 0.25], C0)
def buzzsaw(m):
    m.box([1, 1, 7.6], [15, 15, 8.4], C0)
    m.box([1, 1, 7.6], [15, 15, 8.4], C0L, rot=('z', 45, [8, 8, 8]))
    m.box([5, 5, 7.2], [11, 11, 8.8], C1)

# ---------------------------------------------------------------- fans / shields / armour-ish
def fan_open(m):
    m.col(-3, 2, 1.4, 1.0, C2)
    for a in (-45, -22.5, 0, 22.5, 45):
        m.box([7.4, 2, 7.6], [8.6, 18, 8.4], C2, rot=('z', a, [8, 2, 8]))
        m.box([5.8, 6, 7.8], [10.2, 18, 8.2], C0 if a in (-45, 0, 45) else C0L, rot=('z', a, [8, 2, 8]))
    for a in (-22.5, 0, 22.5): m.box([7, 13, 7.7], [9, 15, 8.3], C1, rot=('z', a, [8, 2, 8]))
def fan_closed(m):
    m.col(-2, 18, 2.6, 1.2, C0)
    m.col(-2, 18, 0.5, 1.4, C1)
    m.box([7, -3.5, 7.2], [9, -2, 8.8], C1)
def gunbai(m):
    m.col(-8, 6, 1.6, 1.6, C2 if True else C0)
    m.box([1, 6, 7.2], [15, 22, 8.8], C0)
    m.box([3, 4, 7.2], [13, 24, 8.8], C0)
    m.box([3, 8, 7.0], [13, 20, 9.0], C2)
    m.box([5.5, 14, 6.9], [10.5, 18, 9.1], C1)                                      # red half of the crest
    m.box([5.5, 10, 6.9], [10.5, 14, 9.1], EDGE)
def umbrella(m):
    m.col(-6, 20, 0.9, 0.9, C1)
    m.box([7.2, -7, 7.2], [10, -6, 8.8], C1)
    for k, w in enumerate((16, 12, 8, 4)): m.col(14 + k * 1.6, 15.6 + k * 1.6, w, w, C0 if k % 2 == 0 else C0L)
def round_shield(m):
    m.box([1, 2, 7], [15, 14, 9], C0); m.box([2, 1, 7], [14, 15, 9], C0); m.box([3, 0, 7], [13, 16, 9], C0)
    m.box([6, 6, 6.2], [10, 10, 7], C1)
    m.box([0.6, 7, 6.8], [15.4, 9, 9.2], C1)
def retract_shield(m):
    for a in (-45, -22.5, 0, 22.5, 45): m.box([5.5, 2, 7.5], [10.5, 16, 8.5], C0 if a % 45 == 0 else C0L, rot=('z', a, [8, 2, 8]))
    m.box([6, 0, 6], [10, 4, 10], C1)
def turtle_shield(m):
    m.box([2, 1, 6.5], [14, 15, 9.5], C1)
    m.box([3, 2, 5.8], [13, 14, 6.5], C0)
    for (x, y) in ((8, 8), (5, 5), (11, 5), (5, 11), (11, 11)): m.box([x - 1.2, y - 1.2, 5.4], [x + 1.2, y + 1.2, 5.8], C0D)
def gauntlet(m):
    m.box([4.5, -2, 4.5], [11.5, 7, 11.5], C0)
    m.box([4, 7, 4], [12, 11, 12], C0L)
    for x in (4.6, 6.8, 9.2, 11.4): m.box([x - .8, 11, 6], [x + .8, 15, 8.5], C0)
    for y in (1, 4): m.box([4.3, y, 4.3], [11.7, y + .6, 11.7], C1)
def drill_arm(m):
    m.box([5, -6, 5], [11, 4, 11], C1)
    m.box([4.5, 2, 4.5], [11.5, 4, 11.5], C2)
    for k, w in enumerate((6, 5, 4, 3, 2, 1)):
        m.box([8 - w / 2, 4 + k * 2.5, 8 - w / 2], [8 + w / 2, 6.5 + k * 2.5, 8 + w / 2], C0 if k % 2 == 0 else C0D, rot=('y', 22.5 if k % 2 else -22.5, [8, 4, 8]))
def claw3(m):
    m.box([5, -2, 6], [11, 6, 10], C2)
    for x in (5.6, 8, 10.4):
        m.col(6, 18, 0.9, 0.6, C0, x=x); m.rotcol(18, 19.5, 0.6, 0.4, C0L, 45, x=x, oy=18)
def bracers(m):
    for y in (0, 7): m.box([4.5, y, 4.5], [11.5, y + 5, 11.5], C0); m.box([4.3, y + 2, 4.3], [11.7, y + 2.8, 11.7], C1)
def bladed_bracers(m):
    bracers(m)
    m.box([11.5, 2, 7.6], [13, 18, 8.4], C0L)
    m.rotcol(18, 19.5, 1.2, 0.6, C0L, 45, x=12.2, oy=18)
def pincers(m):
    m.box([6, -4, 6], [10, 4, 10], C1)
    m.box([4, 4, 7.5], [7, 22, 8.5], C0, rot=('z', -22.5, [6, 4, 8]))
    m.box([9, 4, 7.5], [12, 22, 8.5], C0, rot=('z', 22.5, [10, 4, 8]))
    m.box([6.8, 3, 6.8], [9.2, 5.4, 9.2], C1D)

# ---------------------------------------------------------------- ranged
def bow(m):
    m.col(5, 11, 1.4, 1.4, C1, sides=STRIPE1)
    m.rotcol(11, 19, 1.0, 1.0, C0, -22.5, oy=11)
    m.rotcol(-3, 5, 1.0, 1.0, C0, 22.5, oy=5)
    m.box([10.6, -2, 7.9], [10.8, 18, 8.1], C1L)                                    # string
def ray_bow(m):
    bow(m)
    m.box([4, 7.6, 7.8], [14, 8.4, 8.2], GLOW)                                      # chakra arrow
def crossbow(m):
    m.box([7, -6, 7], [9, 14, 9], C0)
    m.box([0, 9, 7.4], [16, 10.4, 8.6], C1)
    m.box([7.6, 13, 6.5], [8.4, 15, 9.5], C1)
def slingshot(m):
    m.col(-6, 4, 1.6, 1.6, C0)
    m.rotcol(4, 11, 1.4, 1.4, C0, 22.5, oy=4)
    m.rotcol(4, 11, 1.4, 1.4, C0, -22.5, oy=4)
    m.box([4.5, 10, 7.9], [11.5, 10.3, 8.1], C1)
def blowpipe(m):
    m.col(-10, 26, 1.6, 1.6, C0)
    for y in (-2, 8, 18): m.col(y, y + 0.6, 1.9, 1.9, C1)
def gun(m):
    m.box([7, 0, 7], [9, 16, 9], C0)                                                 # barrel (points up = forward)
    m.box([6.5, -4, 6.5], [9.5, 6, 9.5], C2)
    m.box([7.2, -4, 9.5], [8.8, 0, 13], C0)                                          # grip
    m.box([7.4, 16, 7.4], [8.6, 17, 8.6], C1L)
def sniper(m):
    m.box([7.3, -6, 7.3], [8.7, 22, 8.7], C1)
    m.box([6.5, -10, 6.5], [9.5, 2, 9.5], C0)
    m.box([7.5, 4, 9.2], [8.5, 12, 10.6], C1D)
def cannon(m):
    m.box([4.5, -4, 4.5], [11.5, 16, 11.5], C0)
    m.box([4, 14, 4], [12, 17, 12], C2)
    m.box([6, 15, 6], [10, 17.2, 10], BLACK)
def gatling(m):
    for (x, z) in ((6.6, 6.6), (9.4, 6.6), (6.6, 9.4), (9.4, 9.4)): m.box([x - 0.8, 2, z - 0.8], [x + 0.8, 20, z + 0.8], C0)
    m.box([4.5, -4, 4.5], [11.5, 4, 11.5], C2)
    m.box([6, 18, 6], [10, 19, 10], C1)
def launcher(m):
    m.box([5, -2, 5], [11, 12, 11], C0)
    for x in (6.5, 9.5): m.box([x - .6, 12, 7.4], [x + .6, 13, 8.6], BLACK)
    m.box([7, -2, 11], [9, 3, 14], C1)
def wrist_launcher(m):
    m.box([4.5, -2, 4.5], [11.5, 10, 11.5], C0)
    for (x, z) in ((6, 6), (10, 6), (6, 10), (10, 10)): m.box([x - .7, 10, z - .7], [x + .7, 13, z + .7], C2)
    m.box([4.3, 3, 4.3], [11.7, 4, 11.7], C1)

BUILDERS = {
    'kunai': kunai, 'kunai_long': lambda m: kunai(m, True), 'tri_kunai': tri_kunai, 'katana': katana,
    'straight_sword': straight_sword, 'curved_sword': curved_sword, 'tanto': tanto, 'sabre_glow': sabre_glow,
    'broad_sword': broad_sword, 'cleaver': cleaver, 'shark_sword': shark_sword, 'jagged_sword': jagged_sword,
    'lightning_sword': lightning_sword, 'needle_sword': needle_sword, 'helix_sword': helix_sword, 'stone_sword': stone_sword,
    'tri_sword': tri_sword, 'twin_swords': twin_swords, 'scroll_sword': scroll_sword, 'jar_sword': jar_sword,
    'scalpel': scalpel, 'katar': katar,
    'spear': spear, 'trident': trident, 'cross_spear': cross_spear, 'retract_spear': retract_spear, 'harpoon': harpoon,
    'bo': bo, 'ringed_staff': ringed_staff, 'cane_ring': cane_ring, 'scythe1': lambda m: scythe(m, 1),
    'scythe2': lambda m: scythe(m, 2), 'scythe3': lambda m: scythe(m, 3), 'kama': kama, 'kusarigama': kusarigama,
    'chain': chain, 'chain_staff': chain_staff, 'hoe': hoe, 'sasumata': sasumata, 'fishing_rod': fishing_rod, 'whip': whip,
    'three_section': three_section, 'nunchaku': nunchaku, 'club': club, 'mace': mace, 'claw_club': claw_club, 'flail': flail,
    'jidanda': jidanda, 'hammer_axe': hammer_axe, 'axe': axe, 'tonfa': tonfa,
    'shuriken': shuriken, 'fuma': fuma, 'senbon': senbon, 'needles': needles, 'caltrops': caltrops, 'buzzsaw': buzzsaw,
    'fan_open': fan_open, 'fan_closed': fan_closed, 'gunbai': gunbai, 'umbrella': umbrella, 'round_shield': round_shield,
    'retract_shield': retract_shield, 'turtle_shield': turtle_shield, 'gauntlet': gauntlet, 'drill_arm': drill_arm,
    'claw3': claw3, 'bracers': bracers, 'bladed_bracers': bladed_bracers, 'pincers': pincers,
    'bow': bow, 'ray_bow': ray_bow, 'crossbow': crossbow, 'slingshot': slingshot, 'blowpipe': blowpipe, 'gun': gun,
    'sniper': sniper, 'cannon': cannon, 'gatling': gatling, 'launcher': launcher, 'wrist_launcher': wrist_launcher,
}

# ---------------------------------------------------------------- display transforms
# Derived from vanilla's own item/handheld + item/generated transforms so every model is held exactly like a
# vanilla sword (grip in the fist, blade along the same diagonal), auto-scaled from the model's bounding box.
SMALL = {'kunai', 'kunai_long', 'tri_kunai', 'tanto', 'scalpel', 'katar', 'senbon', 'needles', 'kama', 'claw3', 'sabre_glow', 'kusarigama'}
FLAT = {'shuriken', 'fuma', 'buzzsaw', 'caltrops'}
FIST = {'gauntlet', 'drill_arm', 'bracers', 'bladed_bracers', 'wrist_launcher', 'launcher', 'round_shield', 'retract_shield',
        'turtle_shield', 'pincers', 'jidanda', 'umbrella', 'gunbai', 'fan_open'}
POLE = {'spear', 'trident', 'cross_spear', 'retract_spear', 'harpoon', 'bo', 'ringed_staff', 'cane_ring', 'scythe1', 'scythe2',
        'scythe3', 'chain', 'chain_staff', 'hoe', 'sasumata', 'fishing_rod', 'three_section', 'hammer_axe', 'axe', 'blowpipe', 'sniper'}

HANDHELD = {"thirdperson_righthand": ([0, -90, 55], [0, 4, 0.5], 0.85), "thirdperson_lefthand": ([0, 90, -55], [0, 4, 0.5], 0.85),
            "firstperson_righthand": ([0, -90, 25], [1.13, 3.2, 1.13], 0.68), "firstperson_lefthand": ([0, 90, -25], [1.13, 3.2, 1.13], 0.68)}
GENERATED = {"thirdperson_righthand": ([0, 0, 0], [0, 3, 1], 0.55), "thirdperson_lefthand": ([0, 0, 0], [0, 3, 1], 0.55),
             "firstperson_righthand": ([0, -90, 25], [1.13, 3.2, 1.13], 0.68), "firstperson_lefthand": ([0, 90, -25], [1.13, 3.2, 1.13], 0.68)}

def _rx(a): c, s = math.cos(a), math.sin(a); return [[1, 0, 0], [0, c, -s], [0, s, c]]
def _ry(a): c, s = math.cos(a), math.sin(a); return [[c, 0, s], [0, 1, 0], [-s, 0, c]]
def _rz(a): c, s = math.cos(a), math.sin(a); return [[c, -s, 0], [s, c, 0], [0, 0, 1]]
def _mm(a, b): return [[sum(a[i][k] * b[k][j] for k in range(3)) for j in range(3)] for i in range(3)]
def _mv(a, v): return [sum(a[i][k] * v[k] for k in range(3)) for i in range(3)]
def _euler_to_m(e):  # JOML rotationXYZ: R = Rx * Ry * Rz
    return _mm(_mm(_rx(math.radians(e[0])), _ry(math.radians(e[1]))), _rz(math.radians(e[2])))
def _m_to_euler(r):
    b = math.asin(max(-1, min(1, r[0][2])))
    a = math.atan2(-r[1][2], r[2][2]); c = math.atan2(-r[0][1], r[0][0])
    return [round(math.degrees(a), 2), round(math.degrees(b), 2), round(math.degrees(c), 2)]

def bbox(elements):
    ys = [e['from'][1] for e in elements] + [e['to'][1] for e in elements]
    xs = [e['from'][0] for e in elements] + [e['to'][0] for e in elements]
    return min(xs), max(xs), min(ys), max(ys)

PRE_SIGN = -1.0

def display(shape, elements, pre_sign=None):
    pre_sign = PRE_SIGN if pre_sign is None else pre_sign
    x0, x1, y0, y1 = bbox(elements)
    height = max(y1 - y0, x1 - x0)
    if shape in FLAT or shape in FIST:
        base, pre, grip = GENERATED, 0.0, [0, ((y0 + y1) / 2 - 8) / 16, 0]
        fit = min(1.0, 15 / height)
    else:
        base, pre = HANDHELD, 45.0 * pre_sign
        gy = {'kunai': 4.5, 'kunai_long': 4.5, 'tri_kunai': 4.5}.get(shape, 1.5 if shape in POLE else 3.0)
        grip = [0, (gy - 8) / 16, 0]
        target = 18 if shape in SMALL else 30 if shape in POLE else 24
        fit = min(1.35, target / height)
    out = {}
    pre_m = _rz(math.radians(pre))
    vanilla_grip = [-6 / 16, -6 / 16, 0] if base is HANDHELD else [0, 0, 0]
    for mode, (rot, trans, sv) in base.items():
        rv = _euler_to_m(rot)
        r = _mm(rv, pre_m)
        s2 = sv * fit
        a = _mv(rv, [sv * c for c in vanilla_grip])
        b = _mv(r, [s2 * c for c in grip])
        t = [trans[k] + (a[k] - b[k]) * 16 for k in range(3)]
        out[mode] = {"rotation": _m_to_euler(r), "translation": [round(max(-80, min(80, v)), 3) for v in t], "scale": [round(s2, 3)] * 3}
    if base is HANDHELD:
        # First person (tuned in-game against a vanilla sword): lean toward the crosshair, grip in the corner.
        fs = round(0.68 * fit * 0.65, 3)
        out["firstperson_righthand"] = {"rotation": [0, -90, 20], "translation": [1.13, 4.5, 0.5], "scale": [fs] * 3}
        out["firstperson_lefthand"] = {"rotation": [0, 90, -20], "translation": [1.13, 4.5, 0.5], "scale": [fs] * 3}
    elif shape in FLAT:
        fs = round(0.68 * fit * 0.85, 3)   # face the camera instead of showing the thin edge
        out["firstperson_righthand"] = {"rotation": [-10, -25, 10], "translation": [1.13, 4.0, 0.0], "scale": [fs] * 3}
        out["firstperson_lefthand"] = {"rotation": [-10, 25, -10], "translation": [1.13, 4.0, 0.0], "scale": [fs] * 3}
    else:  # fist-mounted / shields / fans: smaller and slightly turned toward the camera
        fs = round(0.68 * fit * 0.55, 3)
        out["firstperson_righthand"] = {"rotation": [0, -60, 10], "translation": [1.5, 3.0, 0.5], "scale": [fs] * 3}
        out["firstperson_lefthand"] = {"rotation": [0, 60, -10], "translation": [1.5, 3.0, 0.5], "scale": [fs] * 3}
    g = min(0.5, 0.5 * 16 / height) * (1 if shape in FLAT or shape in FIST else 1.3)
    out["ground"] = {"rotation": [0, 0, pre], "translation": [0, 2, 0], "scale": [round(g, 3)] * 3}
    f = min(1.0, 15 / height) * (1 if shape in FLAT or shape in FIST else 1.25)
    out["fixed"] = {"rotation": [0, 180, -pre if pre else 0], "translation": [0, 0, 0], "scale": [round(f, 3)] * 3}
    out["head"] = {"rotation": [0, 180, 0], "translation": [0, 13, 7], "scale": [round(f, 3)] * 3}
    out["gui"] = {"rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [round(f * 0.6, 3)] * 3}
    return out

def build(shape, item_id, colors, namespace='narutorpg'):
    if shape not in BUILDERS: return None, None
    m = M(); BUILDERS[shape](m)
    model = {"credit": "NarutoRPG low-poly model (tools/catalog/model3d.py)", "texture_size": [16, 16],
             "textures": {"p": f"{namespace}:item/3d/{item_id}", "particle": f"{namespace}:item/{item_id}"},
             "elements": m.el, "display": display(shape, m.el)}
    return model, palette(colors)
