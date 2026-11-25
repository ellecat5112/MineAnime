"""
Procedural 32x32 pixel-art item sprites for NarutoRPG ninja tools.
Shapes are drawn as flat material regions; render() then adds top-left lighting, edge shading and a dark outline,
so every icon shares one consistent style. Deterministic (seeded by item id).
"""
import math, random, zlib
from PIL import Image, ImageDraw

S = 32
CLEAR = (0, 0, 0, 0)

def rgb(h):
    h = h.lstrip('#'); return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4))
def mix(a, b, t): return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))
def lighter(c, t=0.35): return mix(c, (255, 255, 255), t)
def darker(c, t=0.35): return mix(c, (0, 0, 0), t)
def A(c, a=255): return (c[0], c[1], c[2], a)

class Pen:
    def __init__(self, colors, seed):
        self.img = Image.new('RGBA', (S, S), CLEAR)
        self.d = ImageDraw.Draw(self.img)
        self.c = [rgb(x) for x in colors] + [rgb('#3A3D44'), rgb('#C3CAD3'), rgb('#8B5A2B')]
        self.r = random.Random(seed)
        self.glow = []      # pixels that should not be outlined (glows / particles)
    # --- primitives
    def poly(self, pts, c): self.d.polygon([(round(x), round(y)) for x, y in pts], fill=A(c))
    def line(self, a, b, c, w=1): self.d.line([tuple(map(round, a)), tuple(map(round, b))], fill=A(c), width=w)
    def rect(self, x0, y0, x1, y1, c): self.d.rectangle([x0, y0, x1, y1], fill=A(c))
    def ell(self, cx, cy, rx, ry, c, outline=None): self.d.ellipse([cx - rx, cy - ry, cx + rx, cy + ry], fill=A(c) if c else None, outline=A(outline) if outline else None)
    def px(self, x, y, c, a=255):
        if 0 <= x < S and 0 <= y < S: self.img.putpixel((int(x), int(y)), A(c, a))
    def clear(self, x, y):
        if 0 <= x < S and 0 <= y < S: self.img.putpixel((int(x), int(y)), CLEAR)

# ---------------------------------------------------------------- diagonal helpers (handle bottom-left -> tip top-right)
def P(t, x0=4, y0=27, x1=27, y1=4): return (x0 + (x1 - x0) * t, y0 + (y1 - y0) * t)
N = (0.7071, 0.7071)
def off(p, k): return (p[0] + N[0] * k, p[1] + N[1] * k)
def strip(p, t0, t1, w0, w1, c, curve=0.0):
    a, b = off(P(t0), curve * math.sin(t0 * math.pi)), off(P(t1), curve * math.sin(t1 * math.pi))
    p.poly([off(a, -w0 / 2), off(b, -w1 / 2), off(b, w1 / 2), off(a, w0 / 2)], c)
def leaf(p, t0, t1, wmax, c, tmid=0.4):
    a, m, b = P(t0), P(t0 + (t1 - t0) * tmid), P(t1)
    p.poly([off(a, -wmax * 0.35), off(m, -wmax / 2), b, off(m, wmax / 2), off(a, wmax * 0.35)], c)
def wrap(p, t0, t1, w, c1, c2, n=4):
    strip(p, t0, t1, w, w, c1)
    for k in range(n):
        t = t0 + (t1 - t0) * (k + 0.5) / n
        q = P(t); p.line(off(q, -w / 2), off(q, w / 2), c2)
def ring(p, t, r, c):
    x, y = P(t); p.ell(x, y, r, r, None, c)
def edge_line(p, t0, t1, k, c):
    p.line(off(P(t0), k), off(P(t1), k), c)

# ---------------------------------------------------------------- blades
def kunai(p, long=False):
    blade, wrapc, ringc = p.c[0], p.c[1], p.c[2]
    ring(p, 0.0, 2.6, ringc)
    wrap(p, 0.07, 0.36, 2.4, wrapc, darker(wrapc, 0.3), 4)
    leaf(p, 0.36, 1.0, 5.5 if not long else 4.5, blade, 0.25 if not long else 0.15)
    edge_line(p, 0.42, 0.9, -0.8, lighter(blade, 0.5))
def tri_kunai(p):
    kunai(p)
    x, y = P(0.72); p.poly([(x - 1, y - 1), (x - 6, y - 3), (x - 2, y + 1)], p.c[0]); p.poly([(x + 1, y + 1), (x + 3, y + 6), (x - 1, y + 2)], p.c[0])
    for k in range(3): p.px(*map(round, P(0.18 + 0.06 * k)), p.c[2])
def katana(p, curve=1.6, bw=2.8):
    blade, guard, grip = p.c[0], p.c[1], p.c[2]
    wrap(p, 0.02, 0.27, 2.6, grip, darker(grip, 0.4), 5)
    g = P(0.29); p.poly([off(g, -3.6), off(P(0.32), -3.6), off(P(0.32), 3.6), off(g, 3.6)], guard)
    strip(p, 0.32, 0.93, bw, bw * 0.75, blade, curve)
    p.poly([off(off(P(0.93), curve * math.sin(0.93 * math.pi)), -bw * 0.4), off(P(1.0), -0.3), off(off(P(0.93), curve * math.sin(0.93 * math.pi)), bw * 0.4)], blade)
    for k in range(10):
        t = 0.34 + k * 0.058; p.px(*map(round, off(off(P(t), curve * math.sin(t * math.pi)), -bw * 0.35)), lighter(blade, 0.6))
def straight_sword(p): katana(p, 0.0, 3.2)
def curved_sword(p): katana(p, 3.0, 3.0)
def tanto(p):
    blade, grip = p.c[0], p.c[1]
    wrap(p, 0.12, 0.42, 2.6, grip, darker(grip, 0.4), 4)
    strip(p, 0.43, 0.46, 5, 5, p.c[2]); leaf(p, 0.46, 0.92, 3.4, blade, 0.1)
def sabre_glow(p):
    for k in range(-3, 4):
        for t in [i / 30 for i in range(11, 31)]:
            x, y = off(P(t), k); p.px(round(x), round(y), p.c[1], 60 if abs(k) == 3 else 110)
    tanto(p)
def broad_sword(p):
    blade, grip, wrapc = p.c[0], p.c[1], p.c[2]
    wrap(p, 0.0, 0.25, 2.8, wrapc, darker(wrapc, 0.35), 4)
    strip(p, 0.25, 0.28, 7, 7, grip)
    strip(p, 0.28, 1.0, 8, 8, blade)
    x, y = off(P(0.86), 1.6); p.ell(x, y, 2.4, 2.4, None)  # punched hole (cleared below)
    for dx in range(-2, 3):
        for dy in range(-2, 3):
            if dx * dx + dy * dy <= 5: p.clear(round(x + dx), round(y + dy))
    edge_line(p, 0.3, 0.98, -3.6, lighter(blade, 0.5))
def cleaver(p):
    blade, grip = p.c[0], p.c[1]
    wrap(p, 0.0, 0.3, 2.8, grip, darker(grip, 0.4), 4)
    strip(p, 0.3, 0.95, 9, 9, blade)
    for k in (0.48, 0.64, 0.8): q = P(k); p.line(off(q, -4), off(q, 4), darker(blade, 0.25))
    edge_line(p, 0.3, 0.95, -4.4, lighter(blade, 0.55))
def shark_sword(p):
    body, band, scale = p.c[0], p.c[1], p.c[2]
    wrap(p, 0.0, 0.22, 2.8, darker(body, 0.3), body, 3)
    strip(p, 0.22, 1.0, 7, 6, body)
    for k in range(12):
        t = 0.25 + k * 0.062; q = P(t)
        for s in (-3.2, 3.2): p.px(*map(round, off(q, s)), scale)
    for k in range(4):
        t = 0.3 + k * 0.17; q = P(t); p.line(off(q, -3.5), off(q, 3.5), band, 2)
def jagged_sword(p):
    katana(p, 0.0, 3.0)
    for k in range(6):
        t = 0.4 + k * 0.1; q = off(P(t), -1.8); p.poly([q, off(P(t + 0.05), -1.8), off(P(t + 0.02), -4.5)], p.c[0])
    for k in range(5): p.px(*map(round, off(P(0.45 + k * 0.1), 0.0)), p.c[2])
def lightning_sword(p):
    pts = [P(0.3)]
    for k in range(1, 8):
        t = 0.3 + 0.1 * k; pts.append(off(P(t), (2.5 if k % 2 else -2.5)))
    for a, b in zip(pts, pts[1:]): p.line(a, b, p.c[0], 3)
    for a, b in zip(pts, pts[1:]): p.line(a, b, lighter(p.c[1], 0.6), 1)
    wrap(p, 0.02, 0.28, 2.6, p.c[2], darker(p.c[2], 0.4), 4)
def needle_sword(p):
    strip(p, 0.0, 0.25, 2.6, 2.6, p.c[1]); strip(p, 0.25, 1.0, 1.6, 0.4, p.c[0])
    x, y = P(0.08); p.ell(x, y, 2, 2, None, p.c[0])
    for k in range(8): p.px(round(4 + k * 2), round(29 - k * 0.6), p.c[1])
def helix_sword(p):
    strip(p, 0.0, 0.25, 2.6, 2.6, darker(p.c[0], 0.2))
    for k in range(80):
        t = 0.25 + 0.75 * k / 80; ph = t * 22
        for s in (1, -1):
            q = off(P(t), 2.8 * math.sin(ph) * s); p.px(round(q[0]), round(q[1]), p.c[0] if s > 0 else p.c[1])
def stone_sword(p):
    katana(p, 0.0, 3.4)
    x, y = P(0.02); p.ell(x, y, 3, 3, None, p.c[0])
    for k in range(5): p.px(round(x - 3 - k), round(y + 1), p.c[1])
def tri_sword(p):
    wrap(p, 0.0, 0.3, 2.6, p.c[1], darker(p.c[1], 0.3), 4)
    for s in (-2.6, 0, 2.6): strip(p, 0.3, 0.95 - abs(s) * 0.04, 1.6, 0.6, p.c[0])
    for s in (-2.6, 2.6):
        a, b = off(P(0.32), s), off(P(0.9), s); p.line(a, b, p.c[0], 2)
def twin_swords(p):
    for dx in (-3, 3):
        q = Pen.__new__(Pen)
        for k in range(16):
            t = k / 16
            x, y = P(t); p.px(round(x + dx), round(y), p.c[0])
    straight_sword(p)
    for t in range(18): x, y = P(0.3 + t / 25); p.px(round(x + 4), round(y + 4), p.c[0])
    p.px(*map(round, off(P(0.1), 3)), p.c[1])
def scroll_sword(p):
    straight_sword(p)
    for k in range(4): strip(p, 0.4 + k * 0.14, 0.47 + k * 0.14, 4.6, 4.6, p.c[0] if k % 2 == 0 else p.c[1])
def jar_sword(p):
    for k in range(-4, 5):
        for t in [i / 40 for i in range(16, 40)]:
            q = off(P(t), k * 0.8); p.px(round(q[0]), round(q[1]), p.c[1], 70 + 20 * (4 - abs(k)))
    x, y = P(0.15); p.ell(x, y, 5, 5, p.c[0]); p.ell(x + 2, y - 2, 2, 2, p.c[2])
def scalpel(p):
    strip(p, 0.15, 0.6, 1.8, 1.8, p.c[1]); leaf(p, 0.6, 0.9, 3, p.c[0], 0.3)
def katar(p):
    for s in (-3, 3): strip(p, 0.08, 0.35, 1.2, 1.2, p.c[1])
    q = P(0.08); p.line(off(q, -3.5), off(q, 3.5), p.c[1], 2)
    leaf(p, 0.35, 0.95, 5, p.c[0], 0.2)

# ---------------------------------------------------------------- staves / polearms
def shaft(p, t1=0.78, w=2.0, c=None, rings=0):
    strip(p, 0.0, t1, w, w, c or p.c[1])
    for k in range(rings): q = P(0.1 + k * (t1 - 0.15) / max(1, rings - 1)); p.line(off(q, -w / 2 - .5), off(q, w / 2 + .5), darker(c or p.c[1], 0.4))
def spear(p):
    shaft(p, 0.72); leaf(p, 0.68, 1.0, 5, p.c[0], 0.35); strip(p, 0.66, 0.69, 3.5, 3.5, darker(p.c[0], 0.3))
def trident(p):
    shaft(p, 0.68)
    q = P(0.68); p.line(off(q, -4), off(q, 4), p.c[0], 2)
    for s in (-3.5, 0, 3.5): p.line(off(q, s), off(P(0.95 if s == 0 else 0.88), s), p.c[0], 2)
def cross_spear(p):
    shaft(p, 0.66, c=p.c[1])
    c = P(0.8); p.line(off(c, -6), off(c, 6), p.c[0], 3); p.line(P(0.62), P(1.0), p.c[0], 3)
def retract_spear(p):
    shaft(p, 0.7, 2.6, p.c[0], 4); leaf(p, 0.7, 1.0, 4, lighter(p.c[0], 0.5), 0.3); strip(p, 0.0, 0.08, 3.2, 3.2, p.c[1])
def harpoon(p):
    shaft(p, 0.75); leaf(p, 0.72, 1.0, 5, p.c[0], 0.4)
    for t in (0.78, 0.86): q = P(t); p.line(off(q, 2.5), off(P(t - 0.06), 4.5), p.c[0])
def bo(p):
    strip(p, -0.05, 1.05, 2.4, 2.4, p.c[0])
    for t in (0.0, 0.97): strip(p, t, t + 0.06, 3.2, 3.2, p.c[1])
def ringed_staff(p):
    shaft(p, 0.75, c=p.c[1])
    x, y = P(0.86); p.ell(x, y, 4.5, 4.5, None, p.c[0]); p.line(P(0.75), P(1.0), p.c[0], 2)
    for a in (0.0, 1.6, 3.2, 4.8): p.ell(x + 4.5 * math.cos(a), y + 4.5 * math.sin(a), 1.2, 1.2, None, lighter(p.c[0], 0.3))
def cane_ring(p):
    shaft(p, 0.8, 1.8, p.c[0]); x, y = P(0.88); p.ell(x, y, 3.5, 3.5, None, p.c[0]); p.ell(x, y, 1.5, 1.5, p.c[1])
def scythe(p, n=1):
    shaft(p, 0.85, 2.0)
    for k in range(n):
        base = P(0.85 - k * 0.12); tip = (base[0] - 11 + k * 2, base[1] + 3 + k * 2)
        mid = (base[0] - 6, base[1] - 4 + k)
        p.poly([base, mid, tip, (mid[0] + 1, mid[1] + 3), (base[0] - 1, base[1] + 2)], p.c[0])
        p.line(mid, tip, lighter(p.c[0], 0.5))
def kama(p):
    shaft(p, 0.5, 2.2, p.c[1], 3)
    b = P(0.5); p.poly([b, (b[0] - 1, b[1] - 6), (b[0] - 9, b[1] - 4), (b[0] - 2, b[1] - 3), (b[0] + 1, b[1] + 1)], p.c[0])
def kusarigama(p):
    for k in range(9): x = 5 + k * 2; y = 26 - int(4 * math.sin(k / 2)); p.ell(x, y, 1, 1, None, p.c[2])
    kama(p)
def chain(p):
    for k in range(12):
        t = k / 11; x, y = 4 + t * 22, 16 + 9 * math.sin(t * 5)
        p.ell(x, y, 1.6, 1.0 if k % 2 else 1.6, None, p.c[0] if k % 2 else lighter(p.c[0], 0.2))
    p.ell(27, 16 + 9 * math.sin(5), 2.5, 2.5, darker(p.c[0], 0.2))
def chain_staff(p):
    strip(p, 0.25, 0.75, 2.6, 2.6, p.c[1]); chain_end = [P(0.25), P(0.75)]
    for k in range(5): p.ell(chain_end[0][0] - k * 1.5, chain_end[0][1] + k * 0.3, 1, 1, None, p.c[0])
    for k in range(5): p.ell(chain_end[1][0] + k * 0.3, chain_end[1][1] - k * 1.5, 1, 1, None, p.c[0])
    p.ell(chain_end[0][0] - 8, chain_end[0][1] + 2, 2.2, 2.2, p.c[0]); p.ell(chain_end[1][0] + 2, chain_end[1][1] - 8, 2.2, 2.2, p.c[0])
def hoe(p):
    shaft(p, 0.85); b = P(0.85); p.poly([b, (b[0] - 7, b[1] + 1), (b[0] - 7, b[1] + 4), (b[0] + 1, b[1] + 2)], p.c[0])
def sasumata(p):
    shaft(p, 0.7); q = P(0.7)
    for s in (-1, 1):
        a = off(q, 4 * s); p.line(off(q, 0), a, p.c[0], 2); p.line(a, (a[0] + 3, a[1] - 3), p.c[0], 2)
def fishing_rod(p):
    strip(p, 0.0, 0.95, 1.6, 0.8, p.c[0])
    for k in range(10): p.px(27 - int(k * 0.2), 5 + k * 2, p.c[1])
    p.px(25, 26, p.c[1]); p.px(26, 26, p.c[1]); p.px(26, 25, p.c[1])
def whip(p):
    strip(p, 0.0, 0.25, 2.4, 2.4, p.c[1])
    pts = [(10 + k * 1.8, 21 - k * 1.1 + 4 * math.sin(k / 2.2)) for k in range(12)]
    for a, b in zip(pts, pts[1:]): p.line(a, b, p.c[0], 2 if a[0] < 20 else 1)
def three_section(p):
    for k, (a, b) in enumerate([((4, 27), (11, 17)), ((13, 15), (20, 13)), ((22, 11), (27, 3))]):
        p.line(a, b, p.c[0], 3); p.line(a, (a[0] + 1, a[1] - 1), p.c[1], 3)
    for a, b in [((11, 17), (13, 15)), ((20, 13), (22, 11))]: p.line(a, b, p.c[1])
def nunchaku(p):
    p.line((5, 26), (10, 15), p.c[0], 3); p.line((19, 8), (28, 12), p.c[0], 3)
    for k in range(4): p.px(11 + k * 2, 13 - k, p.c[1])
    for a in [(5, 26), (19, 8)]: p.px(a[0], a[1], p.c[1])
def club(p):
    strip(p, 0.0, 0.3, 2.6, 2.6, darker(p.c[0], 0.2)); strip(p, 0.3, 1.0, 3.4, 7.5, p.c[0])
    for k in range(5): p.px(*map(round, off(P(0.45 + k * 0.1), (k % 2 * 2 - 1) * 2)), p.c[1])
def mace(p):
    shaft(p, 0.6, 2.2, p.c[2]); x, y = P(0.78)
    p.ell(x, y, 6, 6, p.c[0])
    for a in range(8):
        ang = a * math.pi / 4; p.line((x + 5 * math.cos(ang), y + 5 * math.sin(ang)), (x + 8 * math.cos(ang), y + 8 * math.sin(ang)), p.c[1], 2)
    p.ell(x - 1, y - 1, 2, 2, lighter(p.c[0], 0.4))
def claw_club(p):
    club(p); x, y = P(0.95); p.ell(x - 1, y + 1, 2.4, 2.4, p.c[1])
    for t in (0.5, 0.75): q = off(P(t), 4); p.line(q, (q[0] + 3, q[1] + 1), p.c[2], 2)
def flail(p):
    strip(p, 0.0, 0.4, 2.4, 2.4, p.c[1])
    for k in range(4): p.ell(P(0.42)[0] + k * 2, P(0.42)[1] - k * 1.5, 1, 1, None, p.c[0])
    x, y = 22, 9; p.ell(x, y, 4.5, 4.5, p.c[0])
    for a in range(8):
        ang = a * math.pi / 4; p.px(round(x + 6 * math.cos(ang)), round(y + 6 * math.sin(ang)), p.c[0])
def jidanda(p):
    x, y = 19, 12; p.ell(x, y, 8, 8, p.c[0])
    for a in range(10):
        ang = a * math.pi / 5; p.line((x + 7 * math.cos(ang), y + 7 * math.sin(ang)), (x + 11 * math.cos(ang), y + 11 * math.sin(ang)), p.c[1], 2)
    p.ell(x - 2, y - 2, 2.5, 2.5, lighter(p.c[0], 0.4)); p.rect(x - 1, y + 2, x + 1, y + 4, p.c[2])
    for k in range(5): p.ell(4 + k * 2, 28 - k * 2, 1, 1, None, p.c[1])
def hammer_axe(p):
    shaft(p, 0.7, 2.2, p.c[1]); h = P(0.82)
    p.poly([off(h, -1), (h[0] - 8, h[1] - 6), (h[0] - 9, h[1] + 3), off(h, 2)], p.c[0])
    p.rect(h[0] - 1, h[1] - 2, h[0] + 4, h[1] + 3, p.c[2] if len(p.c) > 2 else p.c[0])
def axe(p):
    shaft(p, 0.85, 2.2, p.c[1]); h = P(0.8)
    p.poly([(h[0] - 1, h[1] - 1), (h[0] - 9, h[1] - 7), (h[0] - 10, h[1] + 2), (h[0] + 1, h[1] + 2)], p.c[0])
    p.line((h[0] - 9, h[1] - 7), (h[0] - 10, h[1] + 2), lighter(p.c[0], 0.5))
def tonfa(p):
    strip(p, 0.05, 0.95, 2.8, 2.8, p.c[0]); q = P(0.25); p.line(q, (q[0] + 4, q[1] + 4), p.c[0], 3); p.px(round(q[0] + 4), round(q[1] + 4), p.c[1])

# ---------------------------------------------------------------- thrown
def shuriken(p, r_out=13, r_in=4.2, n=4, rot=0.0):
    cx = cy = 15.5; pts = []
    for k in range(n * 2):
        ang = rot + k * math.pi / n; r = r_out if k % 2 == 0 else r_in
        pts.append((cx + r * math.cos(ang), cy + r * math.sin(ang)))
    p.poly(pts, p.c[0])
    for k in range(n):
        ang = rot + k * 2 * math.pi / n; p.line((cx, cy), (cx + (r_out - 2) * math.cos(ang), cy + (r_out - 2) * math.sin(ang)), lighter(p.c[0], 0.35))
    p.ell(cx, cy, 2.4, 2.4, p.c[1])
    for dx in range(-1, 2):
        for dy in range(-1, 2):
            if abs(dx) + abs(dy) <= 1: p.clear(round(cx + dx), round(cy + dy))
def fuma(p):
    cx = cy = 15.5
    for k in range(4):
        ang = k * math.pi / 2 + 0.35
        tip = (cx + 15 * math.cos(ang), cy + 15 * math.sin(ang)); side = (cx + 7 * math.cos(ang + 0.9), cy + 7 * math.sin(ang + 0.9))
        p.poly([(cx, cy), side, tip, (cx + 5 * math.cos(ang - 0.4), cy + 5 * math.sin(ang - 0.4))], p.c[0])
        p.line((cx, cy), tip, lighter(p.c[0], 0.4))
    p.ell(cx, cy, 3.5, 3.5, p.c[1]); p.ell(cx, cy, 1.5, 1.5, darker(p.c[1], 0.4))
def senbon(p):
    for s in (-3, 0, 3):
        a, b = off(P(0.05), s), off(P(0.95), s); p.line(a, b, p.c[0]); p.px(round(b[0]), round(b[1]), lighter(p.c[0], 0.6))
def needles(p):
    senbon(p)
    for s in (-3, 0, 3): q = off(P(0.12), s); p.px(round(q[0]), round(q[1]), p.c[1]); p.px(round(q[0]) + 1, round(q[1]), p.c[1])
def caltrops(p):
    for (x, y) in [(9, 10), (21, 9), (15, 19), (8, 24), (23, 23)]:
        for a in range(3):
            ang = a * 2.1 + x; p.line((x, y), (x + 4 * math.cos(ang), y + 4 * math.sin(ang)), p.c[0], 1)
        p.ell(x, y, 1.2, 1.2, p.c[1])
def buzzsaw(p):
    shuriken(p, 13, 9, 12, 0.1); p.ell(15.5, 15.5, 4, 4, p.c[1])

# ---------------------------------------------------------------- round things
def orb(p, cx=15.5, cy=15.5, r=11, c=None, hi=True):
    c = c or p.c[0]; p.ell(cx, cy, r, r, c)
    p.ell(cx + r * 0.25, cy + r * 0.25, r * 0.65, r * 0.65, darker(c, 0.12))
    if hi: p.ell(cx - r * 0.35, cy - r * 0.35, r * 0.25, r * 0.25, lighter(c, 0.6))
def bomb(p):
    orb(p, 15, 18, 10)
    for k in range(3): p.line((7, 13 + k * 4), (23, 13 + k * 4), p.c[1]) if False else None
    p.rect(13, 6, 17, 9, darker(p.c[0], 0.4))
    p.line((16, 6), (19, 2), p.c[1], 1); p.px(20, 1, (255, 200, 60)); p.px(19, 1, (255, 240, 120)); p.px(20, 2, (255, 140, 40))
    for k in range(3): p.line((8 + k * 6, 14), (8 + k * 6 + 2, 24), lighter(p.c[1], 0.2))
def spike_bomb(p):
    orb(p, 15.5, 15.5, 8)
    for a in range(12):
        ang = a * math.pi / 6; p.line((15.5 + 7 * math.cos(ang), 15.5 + 7 * math.sin(ang)), (15.5 + 13 * math.cos(ang), 15.5 + 13 * math.sin(ang)), p.c[1])
def spiked_sphere(p):
    orb(p, 15.5, 15.5, 9)
    for a in range(6):
        ang = a * math.pi / 3 + 0.3; b = (15.5 + 8 * math.cos(ang), 15.5 + 8 * math.sin(ang)); e = (15.5 + 14 * math.cos(ang), 15.5 + 14 * math.sin(ang))
        p.line(b, e, p.c[1], 2)
    p.rect(12, 13, 19, 18, p.c[2]) ; p.line((13, 15), (18, 15), darker(p.c[2], 0.5))
def pill(p):
    p.ell(11, 20, 6, 6, p.c[0]); p.ell(20, 11, 6, 6, p.c[1]); p.poly([(7, 16), (16, 7), (24, 15), (15, 24)], p.c[0])
    p.poly([(16, 7), (24, 15), (20, 19), (12, 11)], p.c[1]); p.ell(18, 9, 2, 1.5, lighter(p.c[1], 0.6))
def pills3(p):
    for (x, y), c in zip([(9, 21), (16, 11), (23, 21)], p.c[:3]): orb(p, x, y, 5.5, c)
def crystal(p):
    p.poly([(16, 2), (23, 9), (21, 27), (11, 27), (9, 9)], p.c[0]); p.poly([(16, 2), (16, 27), (11, 27), (9, 9)], lighter(p.c[0], 0.25))
    p.line((16, 4), (16, 25), lighter(p.c[1], 0.5))
def hex_crystal(p):
    strip(p, 0.05, 0.95, 6, 6, p.c[0]); edge_line(p, 0.07, 0.93, -1.5, lighter(p.c[0], 0.5)); edge_line(p, 0.07, 0.93, 1.5, darker(p.c[0], 0.2))
def meteor(p):
    pts = [(15 + 12 * math.cos(a) * (0.8 + 0.25 * math.sin(a * 3)), 16 + 11 * math.sin(a) * (0.8 + 0.2 * math.cos(a * 2))) for a in [k * math.pi / 8 for k in range(16)]]
    p.poly(pts, p.c[0])
    for k in range(4): x = 9 + k * 4; p.line((x, 10 + k), (x + 3, 18 + k % 2 * 3), p.c[1])
def gem_orbs(p):
    cols = p.c[:3] + [rgb('#F2D13A'), rgb('#FFFFFF')]
    for k in range(5):
        ang = -math.pi / 2 + k * 2 * math.pi / 5; orb(p, 15.5 + 10 * math.cos(ang), 15.5 + 10 * math.sin(ang), 4.2, cols[k])
def bell(p):
    p.poly([(16, 4), (22, 9), (24, 22), (8, 22), (10, 9)], p.c[0]); p.rect(6, 22, 26, 24, darker(p.c[0], 0.2))
    p.ell(16, 26, 2.2, 2.2, p.c[1]); p.ell(16, 3, 2, 2, None, p.c[1]); p.line((12, 12), (12, 19), lighter(p.c[0], 0.5))
def ember(p):
    orb(p, 15.5, 17, 7, p.c[0])
    for k in range(6):
        x = 10 + k * 2.5; p.px(round(x), round(8 - (k % 3)), p.c[1], 200); p.px(round(x + 1), round(6 - (k % 2)), (255, 230, 120), 170)
def tenseigan(p):
    orb(p, 15.5, 15.5, 12, p.c[0])
    for r in (4, 8): p.ell(15.5, 15.5, r, r, None, lighter(p.c[0], 0.5))
    for a in range(6):
        ang = a * math.pi / 3; p.line((15.5 + 4 * math.cos(ang), 15.5 + 4 * math.sin(ang)), (15.5 + 11 * math.cos(ang + 0.4), 15.5 + 11 * math.sin(ang + 0.4)), p.c[1])
def eye(p):
    p.ell(15.5, 15.5, 13, 9, p.c[2] if len(p.c) > 2 else (240, 240, 240)); p.ell(15.5, 15.5, 7, 7, p.c[0]); p.ell(15.5, 15.5, 2.5, 2.5, p.c[1])
    for a in range(3):
        ang = a * 2.094; x, y = 15.5 + 4.5 * math.cos(ang), 15.5 + 4.5 * math.sin(ang); p.ell(x, y, 1.1, 1.1, p.c[1])
    p.px(12, 12, (255, 255, 255))

# ---------------------------------------------------------------- paper
def glyph(p, x0, y0, w, h, c):
    for k in range(5):
        x = x0 + p.r.randint(0, w - 1); y = y0 + p.r.randint(0, h - 1)
        if p.r.random() < 0.5: p.line((x, y), (x + p.r.randint(-3, 3), y), c)
        else: p.line((x, y), (x, y + p.r.randint(1, 4)), c)
def tag(p):
    p.rect(10, 3, 21, 28, p.c[0]); p.rect(10, 3, 21, 5, darker(p.c[0], 0.15)); p.rect(10, 26, 21, 28, darker(p.c[0], 0.15))
    glyph(p, 12, 8, 8, 16, p.c[1]); p.ell(15.5, 15.5, 3.5, 3.5, None, p.c[1])
def scroll(p, big=False):
    w = 9 if big else 7
    strip(p, 0.1, 0.9, w, w, p.c[0])
    for t in (0.08, 0.9): strip(p, t, t + 0.06, w + 3, w + 3, p.c[1])
    edge_line(p, 0.15, 0.85, -w / 2 + 1.5, lighter(p.c[0], 0.4))
    if big: strip(p, 0.45, 0.55, w + 0.5, w + 0.5, p.c[2] if len(p.c) > 2 else p.c[1])
    else: strip(p, 0.47, 0.53, w + 0.5, w + 0.5, p.c[1])
def big_scroll(p): scroll(p, True)
def map_scroll(p):
    p.rect(4, 7, 27, 24, p.c[0]); p.rect(2, 6, 4, 25, p.c[1]); p.rect(27, 6, 29, 25, p.c[1])
    p.poly([(8, 21), (13, 12), (17, 18), (21, 10), (25, 21)], p.c[2]); p.px(13, 12, rgb('#FFFFFF'))
    for k in range(3): p.px(10 + k * 5, 22, rgb('#C62828'))
def book(p):
    p.rect(7, 4, 25, 28, p.c[0]); p.rect(7, 4, 9, 28, darker(p.c[0], 0.35)); p.rect(23, 6, 26, 27, rgb('#EDE3C8'))
    p.ell(16.5, 15, 4, 4, None, p.c[1]); glyph(p, 13, 12, 6, 6, p.c[1])
def cards(p):
    for k, (dx, ang) in enumerate([(-5, -0.3), (0, 0), (5, 0.3)]):
        cx, cy = 16 + dx, 17
        pts = [(cx + x * math.cos(ang) - y * math.sin(ang), cy + x * math.sin(ang) + y * math.cos(ang)) for x, y in [(-5, -9), (5, -9), (5, 9), (-5, 9)]]
        p.poly(pts, darker(p.c[0], 0.1 * (2 - k)))
    glyph(p, 18, 10, 4, 10, p.c[1])

# ---------------------------------------------------------------- containers
def gourd(p, big=False):
    s = 1.15 if big else 1.0
    p.ell(16, 21, 9 * s, 8.5 * s, p.c[0]); p.ell(16, 10, 6 * s, 5.5 * s, p.c[0]); p.rect(14, 2, 18, 5, p.c[1])
    p.line((7, 13), (25, 23), p.c[1], 2); p.ell(13, 18, 2.5, 3.5, lighter(p.c[0], 0.3))
def big_gourd(p):
    gourd(p, True); p.rect(8, 14, 24, 16, p.c[1])
def pot(p):
    p.ell(16, 19, 11, 9, p.c[0]); p.rect(11, 5, 21, 10, p.c[0]); p.rect(9, 4, 23, 6, darker(p.c[0], 0.2)); p.rect(8, 15, 24, 17, p.c[1])
def vial(p):
    p.rect(13, 3, 19, 6, rgb('#8B5A2B')); p.rect(12, 7, 20, 10, p.c[2] if len(p.c) > 2 else rgb('#CFE8F0'))
    p.ell(16, 20, 8, 8, p.c[2] if len(p.c) > 2 else rgb('#CFE8F0')); p.ell(16, 22, 6.5, 5.5, p.c[0]); p.ell(13, 17, 1.5, 2, (255, 255, 255))
def bottle(p):
    p.rect(13, 2, 19, 5, p.c[1]); p.rect(12, 6, 20, 9, lighter(p.c[0], 0.3)); p.rect(9, 10, 23, 28, p.c[0])
    p.rect(9, 16, 23, 21, p.c[1]); p.rect(11, 11, 12, 26, lighter(p.c[0], 0.5))
def box(p, c=None):
    c = c or p.c[0]; p.poly([(5, 10), (16, 5), (27, 10), (16, 15)], lighter(c, 0.2)); p.poly([(5, 10), (16, 15), (16, 28), (5, 23)], c)
    p.poly([(16, 15), (27, 10), (27, 23), (16, 28)], darker(c, 0.25))
def black_box(p):
    box(p); p.line((7, 16), (14, 19), p.c[1]); p.line((18, 19), (25, 15), p.c[1])
def box_face(p):
    box(p); p.ell(10.5, 18, 2, 2, p.c[2]); p.ell(22, 17, 2, 2, p.c[2]); p.line((8, 23), (13, 24), p.c[1]); p.line((19, 23), (25, 21), p.c[1])
def kettle(p):
    p.ell(15, 19, 10, 8, p.c[0]); p.rect(10, 9, 20, 12, darker(p.c[0], 0.2)); p.ell(15, 8, 2, 1.5, p.c[1])
    p.line((24, 17), (29, 12), p.c[0], 2); p.ell(15, 9, 8, 6, None, p.c[1])
def bowl(p):
    p.poly([(4, 15), (28, 15), (24, 26), (8, 26)], p.c[0]); p.ell(16, 15, 12, 3.5, p.c[1]); p.ell(12, 14, 3, 1.5, p.c[2] if len(p.c) > 2 else p.c[1])
    for k in range(3): p.px(10 + k * 6, 9 - k % 2, (220, 220, 220), 150)
def perfume(p):
    p.ell(16, 20, 8, 8, p.c[0]); p.rect(14, 8, 18, 12, lighter(p.c[0], 0.3)); p.ell(16, 6, 3, 3, p.c[1]); p.ell(13, 17, 2, 3, lighter(p.c[0], 0.6))
def pouch(p):
    p.ell(16, 20, 10, 8, p.c[0]); p.poly([(10, 12), (22, 12), (19, 7), (13, 7)], p.c[0]); p.line((10, 12), (22, 12), p.c[1], 2)
    for k in range(4): p.px(13 + k * 2, 20 + (k % 2), p.c[1])
def jar(p):
    p.ell(16, 19, 9, 9, p.c[0]); p.rect(9, 7, 23, 11, p.c[1]); p.ell(12, 17, 2, 3, lighter(p.c[0], 0.5))
def wallet_toad(p):
    p.ell(16, 19, 11, 9, p.c[0]); p.ell(10, 10, 4, 4, p.c[0]); p.ell(22, 10, 4, 4, p.c[0])
    p.ell(10, 10, 1.6, 1.6, (20, 20, 20)); p.ell(22, 10, 1.6, 1.6, (20, 20, 20)); p.line((9, 21), (23, 21), darker(p.c[0], 0.5)); p.rect(14, 24, 18, 27, p.c[1])
def earrings(p):
    for x in (10, 22): p.ell(x, 8, 2, 2, None, p.c[1]); p.line((x, 10), (x, 13), p.c[1]); orb(p, x, 19, 5.5, p.c[0])

# ---------------------------------------------------------------- fans / shields
def fan_open(p):
    cx, cy = 16, 27
    for k in range(9):
        a0 = math.pi + k * math.pi / 9; a1 = a0 + math.pi / 9
        p.poly([(cx, cy), (cx + 14 * math.cos(a0), cy + 14 * math.sin(a0)), (cx + 14 * math.cos(a1), cy + 14 * math.sin(a1))], p.c[0] if k % 2 else lighter(p.c[0], 0.15))
        p.line((cx, cy), (cx + 14 * math.cos(a0), cy + 14 * math.sin(a0)), p.c[2] if len(p.c) > 2 else darker(p.c[0], 0.4))
    for k, a in enumerate((1.25, 1.5, 1.75)): p.ell(cx + 9 * math.cos(math.pi * a), cy + 9 * math.sin(math.pi * a), 2, 2, p.c[1])
    p.ell(cx, cy, 2, 2, darker(p.c[0], 0.5))
def fan_closed(p):
    strip(p, 0.0, 0.95, 3.0, 4.6, p.c[0]); edge_line(p, 0.05, 0.92, 0, p.c[1]); ring(p, 0.0, 1.8, p.c[1])
def gunbai(p):
    shaft(p, 0.45, 2.4, p.c[0]); x, y = P(0.72); p.ell(x, y, 10, 10, p.c[0]); p.ell(x, y, 7.5, 7.5, p.c[2] if len(p.c) > 2 else lighter(p.c[0], .3))
    p.ell(x, y - 2, 3.5, 3.5, p.c[1]); p.ell(x, y + 2.5, 3.5, 3.5, (240, 240, 240))
def umbrella(p):
    p.poly([(16, 3), (29, 14), (3, 14)], p.c[0])
    for x in (8, 16, 24): p.line((16, 3), (x, 14), darker(p.c[0], 0.3))
    p.line((16, 14), (16, 27), p.c[1], 2); p.line((16, 27), (13, 28), p.c[1], 2)
def round_shield(p):
    orb(p, 15.5, 15.5, 12, p.c[0]); p.ell(15.5, 15.5, 12, 12, None, p.c[1]); p.ell(15.5, 15.5, 3, 3, p.c[1])
def retract_shield(p):
    for k in range(5):
        ang = -0.9 + k * 0.45; p.poly([(16, 26), (16 + 13 * math.sin(ang - 0.2), 26 - 13 * math.cos(ang - 0.2)), (16 + 13 * math.sin(ang + 0.2), 26 - 13 * math.cos(ang + 0.2))], p.c[0] if k % 2 else lighter(p.c[0], 0.2))
    p.ell(16, 26, 4, 3, p.c[1])
def turtle_shield(p):
    p.ell(16, 17, 12, 10, p.c[1]); p.ell(16, 17, 10, 8, p.c[0])
    for (x, y) in [(16, 17), (11, 14), (21, 14), (11, 21), (21, 21)]: p.ell(x, y, 2.5, 2, darker(p.c[0], 0.25))
def gate(p):
    p.rect(6, 4, 9, 28, p.c[1]); p.rect(23, 4, 26, 28, p.c[1]); p.rect(4, 3, 28, 6, p.c[1])
    for k in range(6, 0, -1): p.ell(16, 17, k * 1.6, k * 2, lighter(p.c[0], 0.08 * k) if k % 2 else p.c[0])
def vortex(p):
    for k in range(120):
        t = k / 120; r = 1 + 13 * t; a = t * 12
        x, y = 15.5 + r * math.cos(a), 15.5 + r * math.sin(a) * 0.9
        p.px(round(x), round(y), p.c[1] if k % 3 else lighter(p.c[1], 0.4))
    p.ell(15.5, 15.5, 4, 4, p.c[0])
def mirror(p):
    orb(p, 15.5, 15.5, 12, p.c[2] if len(p.c) > 2 else p.c[1], hi=False); p.ell(15.5, 15.5, 9.5, 9.5, p.c[0])
    p.line((10, 11), (14, 8), (255, 255, 255)); p.line((10, 14), (17, 8), lighter(p.c[0], 0.5))
    for a in range(6):
        ang = a * math.pi / 3; p.ell(15.5 + 11 * math.cos(ang), 15.5 + 11 * math.sin(ang), 1.2, 1.2, p.c[1])

# ---------------------------------------------------------------- wearables
def headband(p):
    p.poly([(2, 12), (30, 10), (30, 20), (2, 22)], p.c[1]); p.rect(9, 9, 23, 21, p.c[0])
    p.rect(9, 9, 23, 10, lighter(p.c[0], 0.5)); p.ell(16, 15, 3.5, 3.5, None, darker(p.c[0], 0.45)); p.line((16, 15), (19, 13), darker(p.c[0], 0.45))
    for (x, y) in [(10, 10), (22, 10), (10, 20), (22, 20)]: p.px(x, y, darker(p.c[0], 0.3))
    p.line((2, 22), (0, 27), p.c[1], 2); p.line((4, 22), (3, 28), p.c[1], 2)
def vest(p):
    p.poly([(7, 6), (12, 4), (16, 9), (20, 4), (25, 6), (27, 27), (5, 27)], p.c[0]); p.line((16, 9), (16, 27), darker(p.c[0], 0.4))
    for (x, y) in [(8, 13), (19, 13), (8, 19), (19, 19)]: p.rect(x, y, x + 4, y + 3, p.c[1])
    p.rect(5, 6, 9, 9, p.c[1]); p.rect(23, 6, 27, 9, p.c[1])
def armor_plate(p):
    vest(p)
    for y in (12, 17, 22): p.line((6, y), (26, y), darker(p.c[0], 0.35))
    p.rect(13, 5, 19, 8, p.c[1])
def tiger_plate(p):
    p.ell(16, 16, 12, 12, p.c[0]); p.ell(11, 13, 2.5, 2, (20, 20, 20)); p.ell(21, 13, 2.5, 2, (20, 20, 20))
    for k in range(3): p.line((6, 9 + k * 3), (10, 10 + k * 3), p.c[1], 1); p.line((26, 9 + k * 3), (22, 10 + k * 3), p.c[1], 1)
    p.poly([(13, 19), (19, 19), (16, 22)], darker(p.c[0], 0.5)); p.line((10, 24), (22, 24), (240, 240, 240))
def oni_mask(p):
    p.ell(16, 17, 11, 12, p.c[0]); p.poly([(7, 9), (4, 1), (10, 6)], p.c[1]); p.poly([(25, 9), (28, 1), (22, 6)], p.c[1])
    p.ell(11, 15, 2.5, 2, (20, 20, 20)); p.ell(21, 15, 2.5, 2, (20, 20, 20))
    p.line((10, 23), (22, 23), (20, 20, 20)); [p.px(x, 22, (240, 240, 240)) for x in range(11, 22, 2)]
    p.line((9, 10), (13, 12), p.c[1]); p.line((23, 10), (19, 12), p.c[1])
def goggles(p):
    p.line((2, 16), (30, 16), p.c[0], 3)
    for x in (10, 22): p.ell(x, 16, 5.5, 5.5, p.c[0]); p.ell(x, 16, 3.8, 3.8, p.c[1]); p.px(x - 2, 14, (255, 255, 255))
def glasses(p):
    for x in (9, 23): p.ell(x, 16, 6, 5, p.c[0]); p.ell(x, 16, 4.5, 3.5, p.c[1])
    p.line((14, 15), (18, 15), p.c[0], 2); p.line((3, 15), (0, 13), p.c[0]); p.line((29, 15), (31, 13), p.c[0])
def hand(p):
    p.rect(11, 18, 21, 29, p.c[0]); p.rect(10, 9, 22, 20, p.c[0])
    for k in range(4): p.rect(10 + k * 3, 3 + (1 if k in (0, 3) else 0), 11 + k * 3, 9, p.c[0])
    p.rect(6, 11, 9, 17, p.c[0]); p.ell(16, 14, 2.5, 2.5, p.c[1]); p.line((11, 21), (21, 21), p.c[2] if len(p.c) > 2 else darker(p.c[0], .3))
def gauntlet(p):
    p.poly([(9, 28), (23, 28), (25, 14), (7, 14)], p.c[0]); p.rect(7, 8, 25, 15, lighter(p.c[0], 0.1))
    for k in range(4): p.rect(8 + k * 4, 4, 10 + k * 4, 8, p.c[0]); p.px(9 + k * 4, 3, lighter(p.c[0], 0.5))
    for y in (18, 23): p.line((9, y), (23, y), p.c[1])
def drill_arm(p):
    p.rect(3, 18, 13, 27, p.c[1]); p.poly([(12, 16), (12, 29), (29, 22)], p.c[0])
    for k in range(4): p.line((14 + k * 4, 17 + k * 1.3), (14 + k * 4, 27 - k * 1.3), darker(p.c[0], 0.35))
    p.rect(3, 18, 13, 19, p.c[2] if len(p.c) > 2 else p.c[1])
def bracers(p):
    for y in (8, 18): p.rect(6, y, 26, y + 7, p.c[0]); p.line((6, y + 3), (26, y + 3), p.c[1])
    p.line((26, 11), (30, 7), p.c[1], 2)
def bladed_bracers(p):
    bracers(p)
    for y in (8, 18): p.poly([(20, y), (31, y - 4), (24, y + 2)], p.c[0]); p.poly([(20, y + 7), (31, y + 11), (24, y + 5)], p.c[0])
def braces_gold(p):
    for k in range(3): p.ell(16, 8 + k * 7, 10, 3.2, p.c[0]); p.ell(16, 8 + k * 7, 7.5, 1.6, p.c[1])
def weights(p):
    p.rect(5, 9, 27, 23, p.c[0]); p.rect(5, 9, 27, 11, p.c[1]); p.rect(5, 21, 27, 23, p.c[1])
    p.rect(12, 13, 20, 19, p.c[1]); glyph(p, 13, 14, 6, 4, p.c[2] if len(p.c) > 2 else (140, 20, 20))
def suit(p):
    p.ell(16, 5, 4, 4, p.c[0]); p.poly([(9, 9), (23, 9), (25, 19), (21, 19), (21, 29), (17, 29), (16, 21), (15, 29), (11, 29), (11, 19), (7, 19)], p.c[0])
    for y in (11, 15, 22): p.line((11, y), (21, y), p.c[1])
def necklace(p):
    for k in range(17):
        a = math.pi * k / 16; p.px(round(16 - 12 * math.cos(a)), round(6 + 13 * math.sin(a)), p.c[0])
    p.poly([(16, 17), (20, 22), (16, 29), (12, 22)], p.c[1]); p.line((15, 19), (14, 24), lighter(p.c[1], 0.6))
def beads(p):
    for k in range(14):
        a = k * 2 * math.pi / 14; r = 2.6 if k % 4 == 0 else 1.8
        p.ell(16 + 10 * math.cos(a), 16 + 10 * math.sin(a), r, r, p.c[1] if k % 4 == 0 else p.c[0])
def claw3(p):
    p.rect(5, 18, 15, 28, p.c[2] if len(p.c) > 2 else p.c[1])
    for k in range(3): p.poly([(9 + k * 3, 18), (12 + k * 3, 18), (24 + k * 2, 3 + k * 2)], p.c[0]); p.line((11 + k * 3, 17), (24 + k * 2, 4 + k * 2), lighter(p.c[0], 0.5))
    for k in range(3): p.px(25 + k * 2, 3 + k * 2, p.c[1])
def pincers(p):
    p.poly([(4, 28), (8, 24), (28, 4), (22, 14)], p.c[0]); p.poly([(4, 28), (8, 24), (28, 18), (18, 22)], p.c[0])
    p.ell(9, 23, 3, 3, p.c[1])

# ---------------------------------------------------------------- puppets & creatures
def puppet(p, horns=False, eyes=2, long=False):
    skin, acc, dark = p.c[0], p.c[1], p.c[2]
    h = 13 if long else 10
    p.ell(16, 13, 10, h, skin); p.poly([(6, 6), (16, 0), (26, 6), (24, 10), (8, 10)], acc)
    for k in range(eyes):
        x = 16 if eyes == 1 else (11 + k * (10 / max(1, eyes - 1)) if eyes < 3 else [10, 16, 22][k])
        y = 12 if k != 1 or eyes < 3 else 9
        p.ell(x, y, 1.8, 1.4, dark); p.px(round(x), round(y), lighter(acc, 0.5))
    p.line((9, 19), (23, 19), dark); p.line((10, 19), (10, 23), dark); p.line((22, 19), (22, 23), dark)
    p.rect(14, 23, 18, 30, darker(skin, 0.2))
    if horns: p.poly([(8, 5), (5, 0), (11, 3)], acc); p.poly([(24, 5), (27, 0), (21, 3)], acc)
    for k in range(3): p.line((5 + k * 2, 31), (8 + k * 3, 24), (220, 220, 255))
def puppet_crow(p):
    puppet(p, eyes=3); [p.poly([(x, 4), (x + 2, -1), (x + 4, 4)], p.c[1]) for x in range(6, 26, 4)]
def puppet_ant(p):
    puppet(p, horns=True, eyes=2, long=True)
def puppet_lizard(p):
    p.ell(15, 18, 11, 7, p.c[0]); p.ell(26, 14, 5, 4, p.c[0]); p.ell(27, 13, 1.2, 1.2, (20, 20, 20))
    for x in (8, 20): p.line((x, 22), (x - 2, 28), p.c[0], 2)
    p.line((4, 18), (0, 24), p.c[0], 2)
    for k in range(5): p.px(8 + k * 3, 12 - (k % 2), p.c[1])
def puppet_scorpion(p):
    p.ell(14, 21, 11, 7, p.c[0]); p.ell(9, 16, 5, 4, p.c[0]); p.ell(8, 15, 1.2, 1.2, p.c[2])
    pts = [(23, 18), (27, 12), (27, 6), (23, 3), (19, 5)]
    for a, b in zip(pts, pts[1:]): p.line(a, b, p.c[1], 2)
    p.poly([(19, 5), (16, 3), (18, 8)], p.c[2])
    p.rect(9, 18, 20, 20, p.c[2])
def doll(p):
    p.ell(16, 9, 6, 6, p.c[0]); p.poly([(8, 15), (24, 15), (27, 29), (5, 29)], p.c[0]); p.rect(12, 18, 20, 26, p.c[1])
    p.ell(14, 9, 1, 1, p.c[1]); p.ell(18, 9, 1, 1, p.c[1])
def drone(p):
    p.ell(16, 16, 6, 4, p.c[0]); p.line((4, 10), (28, 10), p.c[1], 1); p.rect(14, 10, 18, 13, p.c[1])
    for x in (4, 28): p.ell(x, 10, 3, 1, lighter(p.c[1], 0.3))
    p.ell(16, 17, 1.6, 1.6, (255, 60, 60))
def mirror_drone(p):
    p.ell(16, 16, 12, 5, p.c[0]); p.ell(16, 15, 9, 3, lighter(p.c[0], 0.4)); p.ell(16, 16, 2, 1.4, p.c[1])
def toad(p):
    p.ell(16, 20, 12, 8, p.c[0]); p.ell(9, 11, 4, 4, p.c[0]); p.ell(23, 11, 4, 4, p.c[0])
    p.ell(9, 11, 1.8, 1.8, (20, 20, 20)); p.ell(23, 11, 1.8, 1.8, (20, 20, 20)); p.line((8, 21), (24, 21), darker(p.c[0], 0.5))
    p.rect(12, 24, 20, 27, p.c[2] if len(p.c) > 2 else p.c[1])
def bee(p):
    p.ell(17, 17, 9, 6, p.c[0])
    for x in (13, 18, 23): p.line((x, 12), (x, 22), p.c[1], 2)
    p.ell(8, 15, 4, 4, p.c[1]); p.ell(18, 8, 6, 3, (220, 235, 255)); p.ell(7, 14, 1, 1, (255, 255, 255))

# ---------------------------------------------------------------- devices
def gun(p):
    p.rect(4, 13, 26, 18, p.c[0]); p.rect(26, 14, 30, 17, p.c[2] if len(p.c) > 2 else p.c[1]); p.poly([(8, 18), (13, 18), (11, 27), (6, 27)], p.c[0])
    p.rect(14, 10, 22, 13, p.c[1]); p.px(29, 15, lighter(p.c[1], 0.6))
def sniper(p):
    p.rect(2, 14, 30, 16, p.c[1]); p.poly([(2, 16), (11, 16), (9, 22), (2, 22)], p.c[0]); p.rect(12, 10, 20, 13, darker(p.c[1], 0.3)); p.rect(15, 16, 17, 21, p.c[1])
def cannon(p):
    p.poly([(4, 22), (20, 6), (27, 13), (11, 29)], p.c[0]); p.ell(23.5, 9.5, 5, 5, p.c[2] if len(p.c) > 2 else p.c[1]); p.ell(23.5, 9.5, 2.5, 2.5, p.c[1])
    p.ell(9, 24, 5, 5, darker(p.c[0], 0.3))
def gatling(p):
    for k in range(3): p.rect(10, 10 + k * 3, 29, 11 + k * 3, p.c[0])
    p.rect(4, 8, 11, 20, p.c[2] if len(p.c) > 2 else p.c[1]); p.ell(29, 14, 2, 4, p.c[1]); p.poly([(5, 20), (10, 20), (8, 28), (4, 28)], p.c[2] if len(p.c) > 2 else p.c[1])
def launcher(p):
    p.rect(4, 12, 24, 20, p.c[0])
    for k in range(3): p.ell(25, 13.5 + k * 2.5, 1.2, 1.2, p.c[1])
    p.poly([(7, 20), (12, 20), (10, 28), (5, 28)], p.c[1])
def wrist_launcher(p):
    p.rect(4, 12, 28, 20, p.c[0])
    for k in range(4): p.ell(26, 12.5 + k * 2.4, 1.1, 1.1, p.c[2] if len(p.c) > 2 else p.c[1])
    p.line((4, 16), (28, 16), p.c[1]); p.rect(8, 20, 16, 24, p.c[1])
def blowpipe(p):
    strip(p, 0.0, 1.0, 2.6, 2.2, p.c[0])
    for t in (0.2, 0.5, 0.8): q = P(t); p.line(off(q, -1.6), off(q, 1.6), p.c[1])
def pipe(p):
    p.line((4, 24), (15, 16), p.c[0], 3); p.line((15, 16), (21, 16), p.c[0], 3); p.ell(24, 13, 5, 5, p.c[0]); p.ell(24, 12, 3, 2, darker(p.c[0], 0.4))
    p.line((15, 18), (14, 26), p.c[1], 1); p.ell(14, 27, 1.5, 1.5, p.c[1])
    for (x, y, r) in [(25, 4, 2), (29, 7, 1.5), (21, 3, 1)]: p.ell(x, y, r, r, None, (200, 235, 255))
def flute(p):
    strip(p, 0.0, 1.0, 3.0, 3.0, p.c[0])
    for k in range(6): p.px(*map(round, P(0.3 + k * 0.1)), p.c[1])
    strip(p, 0.0, 0.05, 3.6, 3.6, p.c[1])
def ocarina(p):
    p.ell(16, 18, 11, 8, p.c[0]); p.rect(24, 12, 29, 16, p.c[0])
    for (x, y) in [(10, 16), (14, 15), (18, 15), (22, 17), (12, 21)]: p.ell(x, y, 1.1, 1.1, p.c[1])
def drum(p):
    p.ell(16, 9, 12, 5, p.c[2] if len(p.c) > 2 else (240, 220, 180)); p.rect(4, 9, 28, 24, p.c[0]); p.ell(16, 24, 12, 5, p.c[0])
    for x in range(6, 28, 4): p.px(x, 12, p.c[1]); p.px(x, 21, p.c[1])
def speaker(p):
    gauntlet(p)
    for (x, y) in [(11, 20), (15, 21), (19, 20), (13, 25), (17, 25)]: p.px(x, y, (10, 10, 10))
def device(p):
    p.rect(6, 7, 26, 25, p.c[0]); p.rect(9, 10, 23, 18, p.c[1]); p.rect(10, 11, 22, 12, lighter(p.c[1], 0.5))
    for k in range(3): p.ell(11 + k * 5, 21.5, 1.3, 1.3, p.c[2] if len(p.c) > 2 else p.c[1])
def wing(p):
    p.poly([(2, 18), (16, 8), (30, 18), (16, 14)], p.c[0])
    for x in (8, 16, 24): p.line((16, 9), (x, 17), darker(p.c[0], 0.35))
    p.rect(14, 14, 18, 24, p.c[1]); p.rect(12, 24, 20, 26, p.c[2] if len(p.c) > 2 else p.c[1])
def ship(p):
    p.poly([(3, 18), (29, 18), (25, 25), (7, 25)], p.c[0]); p.rect(14, 6, 16, 18, darker(p.c[0], 0.3))
    p.poly([(16, 6), (27, 12), (16, 15)], p.c[1]); p.poly([(4, 15), (12, 12), (13, 18)], lighter(p.c[1], 0.2))
def tower(p):
    for k, w in enumerate((6, 9, 12)): p.rect(16 - w, 3 + k * 9, 16 + w, 11 + k * 9, p.c[0]); p.line((16 - w + 1, 7 + k * 9), (16 + w - 1, 7 + k * 9), p.c[1])
def board(p):
    strip(p, 0.0, 1.0, 7, 7, p.c[0]); x, y = P(0.0); p.ell(x + 1, y - 1, 3.5, 3.5, p.c[0]); x, y = P(1.0); p.ell(x - 1, y + 1, 3.5, 3.5, p.c[0])
    for t in (0.35, 0.65): q = P(t); p.line(off(q, -2.5), off(q, 2.5), p.c[1], 2)
def bow(p):
    for k in range(21):
        a = -1.2 + k * 0.12; p.px(round(8 + 14 * math.cos(a)), round(16 + 14 * math.sin(a)), p.c[0]); p.px(round(9 + 14 * math.cos(a)), round(16 + 14 * math.sin(a)), p.c[0])
    p.line((13, 3), (13, 29), p.c[1]); p.line((6, 16), (26, 16), (150, 150, 150)); p.poly([(26, 14), (30, 16), (26, 18)], (180, 180, 180))
def ray_bow(p):
    bow(p)
    for k in range(5): p.px(14 + k * 3, 16, (200, 255, 255), 200)
def crossbow(p):
    p.rect(6, 15, 28, 17, p.c[0]); p.line((18, 6), (18, 26), p.c[1], 2); p.line((18, 6), (8, 16), (150, 150, 150)); p.line((18, 26), (8, 16), (150, 150, 150))
    p.poly([(4, 16), (8, 13), (8, 19)], p.c[1])
def slingshot(p):
    p.line((16, 29), (16, 17), p.c[0], 3); p.line((16, 17), (9, 6), p.c[0], 3); p.line((16, 17), (23, 6), p.c[0], 3); p.line((9, 6), (23, 6), p.c[1])
def barrel(p):
    p.ell(16, 7, 8, 3, darker(p.c[0], 0.2)); p.rect(8, 7, 24, 26, p.c[0]); p.ell(16, 26, 8, 3, p.c[0])
    for y in (11, 21): p.line((8, y), (24, y), p.c[2] if len(p.c) > 2 else p.c[1], 2)
    p.line((14, 13), (18, 16), p.c[1]); p.line((18, 16), (14, 19), p.c[1])

# ---------------------------------------------------------------- materials / misc
def sand(p, dark=False):
    p.poly([(3, 27), (9, 17), (16, 12), (24, 17), (29, 27)], p.c[0])
    for k in range(30): p.px(p.r.randint(5, 27), p.r.randint(16, 27), p.c[1] if p.r.random() < .5 else lighter(p.c[0], .25))
    for k in range(10): p.px(p.r.randint(6, 26), p.r.randint(3, 11), p.c[0], 200)
def iron_sand(p): sand(p)
def gold_dust(p):
    sand(p)
    for k in range(8): p.px(p.r.randint(6, 26), p.r.randint(4, 24), (255, 245, 180))
def flower(p):
    p.line((16, 30), (16, 16), rgb('#2E7D32'), 2); p.ell(11, 23, 3, 1.5, rgb('#43A047'))
    for a in range(6):
        ang = a * math.pi / 3; p.ell(16 + 5 * math.cos(ang), 11 + 5 * math.sin(ang), 3.4, 3.4, p.c[0])
    p.ell(16, 11, 2.6, 2.6, p.c[1])
def herb(p):
    for k, (x, h) in enumerate([(10, 18), (16, 24), (22, 16)]):
        p.line((x, 29), (x + 2, 29 - h), p.c[1] if k % 2 else p.c[0], 1)
        for j in range(3): p.ell(x + 2 + (2 if j % 2 else -2), 29 - h + 3 + j * 5, 2.5, 1.4, p.c[0])
def mushroom(p):
    p.rect(13, 16, 19, 28, (230, 220, 200)); p.ell(16, 14, 12, 7, p.c[0])
    for (x, y) in [(10, 11), (17, 9), (22, 13)]: p.ell(x, y, 1.6, 1.6, p.c[1])
def seed(p):
    p.ell(16, 18, 8, 10, p.c[0]); p.line((16, 8), (19, 3), rgb('#43A047'), 2); p.ell(21, 4, 3, 1.5, rgb('#66BB6A'))
    for k in range(3): p.line((12 + k * 3, 12), (12 + k * 3, 24), p.c[1])
def rope(p):
    for r in (11, 8, 5): p.ell(16, 16, r, r * 0.8, None, p.c[0]); p.ell(16, 16, r - 1, r * 0.8 - 1, None, darker(p.c[0], 0.2))
    p.line((27, 16), (30, 26), p.c[0], 2); p.poly([(29, 26), (31, 31), (27, 30)], p.c[1])
def wire(p):
    for r in (12, 9, 6, 3): p.ell(16, 16, r, r * 0.8, None, p.c[0])
    p.line((28, 16), (31, 4), p.c[0])
def cloth(p):
    for k in range(26):
        x = 3 + k; y = 16 + 6 * math.sin(k / 4)
        p.line((x, y - 4), (x, y + 4), p.c[0] if (k // 3) % 2 else darker(p.c[0], 0.08))
    glyph(p, 12, 13, 8, 5, p.c[1])
def chainlink(p): chain(p)
def scales(p): pass

SHAPES = {
    'kunai': kunai, 'kunai_long': lambda p: kunai(p, True), 'tri_kunai': tri_kunai, 'katana': katana, 'straight_sword': straight_sword,
    'curved_sword': curved_sword, 'tanto': tanto, 'sabre_glow': sabre_glow, 'broad_sword': broad_sword, 'cleaver': cleaver,
    'shark_sword': shark_sword, 'jagged_sword': jagged_sword, 'lightning_sword': lightning_sword, 'needle_sword': needle_sword,
    'helix_sword': helix_sword, 'stone_sword': stone_sword, 'tri_sword': tri_sword, 'twin_swords': twin_swords,
    'scroll_sword': scroll_sword, 'jar_sword': jar_sword, 'scalpel': scalpel, 'katar': katar,
    'spear': spear, 'trident': trident, 'cross_spear': cross_spear, 'retract_spear': retract_spear, 'harpoon': harpoon, 'bo': bo,
    'ringed_staff': ringed_staff, 'cane_ring': cane_ring, 'scythe1': lambda p: scythe(p, 1), 'scythe2': lambda p: scythe(p, 2),
    'scythe3': lambda p: scythe(p, 3), 'kama': kama, 'kusarigama': kusarigama, 'chain': chain, 'chain_staff': chain_staff,
    'hoe': hoe, 'sasumata': sasumata, 'fishing_rod': fishing_rod, 'whip': whip, 'three_section': three_section,
    'nunchaku': nunchaku, 'club': club, 'mace': mace, 'claw_club': claw_club, 'flail': flail, 'jidanda': jidanda,
    'hammer_axe': hammer_axe, 'axe': axe, 'tonfa': tonfa,
    'shuriken': shuriken, 'fuma': fuma, 'senbon': senbon, 'needles': needles, 'caltrops': caltrops, 'buzzsaw': buzzsaw,
    'orb': orb, 'bomb': bomb, 'spike_bomb': spike_bomb, 'spiked_sphere': spiked_sphere, 'pill': pill, 'pills3': pills3,
    'crystal': crystal, 'hex_crystal': hex_crystal, 'meteor': meteor, 'gem_orbs': gem_orbs, 'bell': bell, 'ember': ember,
    'tenseigan': tenseigan, 'eye': eye,
    'tag': tag, 'scroll': scroll, 'big_scroll': big_scroll, 'map_scroll': map_scroll, 'book': book, 'cards': cards,
    'gourd': gourd, 'big_gourd': big_gourd, 'pot': pot, 'vial': vial, 'bottle': bottle, 'box': box, 'black_box': black_box,
    'box_face': box_face, 'kettle': kettle, 'bowl': bowl, 'perfume': perfume, 'pouch': pouch, 'jar': jar,
    'wallet_toad': wallet_toad, 'earrings': earrings,
    'fan_open': fan_open, 'fan_closed': fan_closed, 'gunbai': gunbai, 'umbrella': umbrella, 'round_shield': round_shield,
    'retract_shield': retract_shield, 'turtle_shield': turtle_shield, 'gate': gate, 'vortex': vortex, 'mirror': mirror,
    'headband': headband, 'vest': vest, 'armor_plate': armor_plate, 'tiger_plate': tiger_plate, 'oni_mask': oni_mask,
    'goggles': goggles, 'glasses': glasses, 'hand': hand, 'gauntlet': gauntlet, 'drill_arm': drill_arm, 'bracers': bracers,
    'bladed_bracers': bladed_bracers, 'braces_gold': braces_gold, 'weights': weights, 'suit': suit, 'necklace': necklace,
    'beads': beads, 'claw3': claw3, 'pincers': pincers,
    'puppet': puppet, 'puppet_crow': puppet_crow, 'puppet_ant': puppet_ant, 'puppet_lizard': puppet_lizard,
    'puppet_scorpion': puppet_scorpion, 'doll': doll, 'drone': drone, 'mirror_drone': mirror_drone, 'toad': toad, 'bee': bee,
    'gun': gun, 'sniper': sniper, 'cannon': cannon, 'gatling': gatling, 'launcher': launcher, 'wrist_launcher': wrist_launcher,
    'blowpipe': blowpipe, 'pipe': pipe, 'flute': flute, 'ocarina': ocarina, 'drum': drum, 'speaker': speaker, 'device': device,
    'wing': wing, 'ship': ship, 'tower': tower, 'board': board, 'bow': bow, 'ray_bow': ray_bow, 'crossbow': crossbow,
    'slingshot': slingshot, 'barrel': barrel,
    'sand': sand, 'iron_sand': iron_sand, 'gold_dust': gold_dust, 'flower': flower, 'herb': herb, 'mushroom': mushroom,
    'seed': seed, 'rope': rope, 'wire': wire, 'cloth': cloth,
}

# ---------------------------------------------------------------- shading pipeline
def shade(img):
    src = img.copy(); W, H = img.size; out = img.copy()
    def opaque(x, y): return 0 <= x < W and 0 <= y < H and src.getpixel((x, y))[3] > 200
    for y in range(H):
        for x in range(W):
            r, g, b, a = src.getpixel((x, y))
            if a <= 200: continue
            c = (r, g, b)
            lit = not opaque(x - 1, y) or not opaque(x, y - 1)
            shd = not opaque(x + 1, y) or not opaque(x, y + 1)
            if lit and not shd: c = lighter(c, 0.22)
            elif shd and not lit: c = darker(c, 0.25)
            out.putpixel((x, y), (*c, 255))
    final = out.copy()
    for y in range(H):
        for x in range(W):
            if src.getpixel((x, y))[3] > 0: continue
            nb = [src.getpixel((x + dx, y + dy)) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))
                  if 0 <= x + dx < W and 0 <= y + dy < H and src.getpixel((x + dx, y + dy))[3] > 200]
            if nb:
                c = nb[0][:3]; final.putpixel((x, y), (*darker(c, 0.72), 255))
    return final

def render(sprite, item_id):
    p = Pen(sprite['colors'], zlib.crc32(item_id.encode()))
    SHAPES.get(sprite['shape'], device)(p)
    return shade(p.img)

def contact_sheet(entries, path, scale=3, cols=16):
    n = len(entries); rows = (n + cols - 1) // cols
    sheet = Image.new('RGBA', (cols * (S + 4) * scale, rows * (S + 4) * scale), (48, 48, 56, 255))
    for k, (iid, sp) in enumerate(entries):
        icon = render(sp, iid).resize((S * scale, S * scale), Image.NEAREST)
        x, y = (k % cols) * (S + 4) * scale + 2 * scale, (k // cols) * (S + 4) * scale + 2 * scale
        sheet.alpha_composite(icon, (x, y))
    sheet.save(path)
