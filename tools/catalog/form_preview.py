"""Offline preview of the form models (assets/narutorpg/form_models/*.json): painter's-algorithm render with face colours
sampled from the texture, applying the part hierarchy the way Minecraft's ModelPart does (pivot, then Z-Y-X rotation).
python3 tools/catalog/form_preview.py out.png [model ...]"""
import json
import math
import os
import sys

from PIL import Image, ImageDraw

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
ASSETS = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg')


def matmul(a, b):
    return [[sum(a[i][k] * b[k][j] for k in range(4)) for j in range(4)] for i in range(4)]


def translate(x, y, z):
    return [[1, 0, 0, x], [0, 1, 0, y], [0, 0, 1, z], [0, 0, 0, 1]]


def rx(a):
    c, s = math.cos(a), math.sin(a)
    return [[1, 0, 0, 0], [0, c, -s, 0], [0, s, c, 0], [0, 0, 0, 1]]


def ry(a):
    c, s = math.cos(a), math.sin(a)
    return [[c, 0, s, 0], [0, 1, 0, 0], [-s, 0, c, 0], [0, 0, 0, 1]]


def rz(a):
    c, s = math.cos(a), math.sin(a)
    return [[c, -s, 0, 0], [s, c, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]]


def apply(m, p):
    return tuple(m[i][0] * p[0] + m[i][1] * p[1] + m[i][2] * p[2] + m[i][3] for i in range(3))


def face_colours(tex, uv, size):
    u, v = uv
    w, h, d = size
    rects = {'up': (u + d, v, w, d), 'down': (u + d + w, v, w, d), 'west': (u, v + d, d, h), 'north': (u + d, v + d, w, h),
             'east': (u + d + w, v + d, d, h), 'south': (u + 2 * d + w, v + d, w, h)}
    out = {}
    for k, (x, y, fw, fh) in rects.items():
        if fw <= 0 or fh <= 0:
            out[k] = None
            continue
        crop = tex.crop((x, y, x + fw, y + fh)).convert('RGBA')
        out[k] = crop
    return out


def render(model_id, yaw_deg=210, pitch_deg=-18, size=360):
    spec = json.load(open(os.path.join(ASSETS, 'form_models', model_id + '.json')))
    tex = Image.open(os.path.join(ASSETS, 'textures', 'entity', 'form', model_id + '.png')).convert('RGBA')
    parts = {p['name']: p for p in spec['parts']}
    mats = {}

    def world(name):
        if name in mats:
            return mats[name]
        p = parts[name]
        parent = world(p['parent']) if p.get('parent') else [[1, 0, 0, 0], [0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]]
        r = p.get('rotation', [0, 0, 0])
        m = matmul(parent, translate(*p['pivot']))
        m = matmul(m, rz(r[2]))
        m = matmul(m, ry(r[1]))
        m = matmul(m, rx(r[0]))
        mats[name] = m
        return m

    quads = []
    for name, p in parts.items():
        m = world(name)
        for c in p['cubes']:
            x, y, z = c['from']
            w, h, d = c['size']
            inf = c.get('inflate', 0)
            x0, y0, z0, x1, y1, z1 = x - inf, y - inf, z - inf, x + w + inf, y + h + inf, z + d + inf
            P = lambda a, b, cc: apply(m, (a, b, cc))
            cols = face_colours(tex, c['uv'], c['size'])
            fs = {'north': [P(x0, y0, z0), P(x1, y0, z0), P(x1, y1, z0), P(x0, y1, z0)],
                  'south': [P(x1, y0, z1), P(x0, y0, z1), P(x0, y1, z1), P(x1, y1, z1)],
                  'west': [P(x0, y0, z1), P(x0, y0, z0), P(x0, y1, z0), P(x0, y1, z1)],
                  'east': [P(x1, y0, z0), P(x1, y0, z1), P(x1, y1, z1), P(x1, y1, z0)],
                  'up': [P(x0, y0, z1), P(x1, y0, z1), P(x1, y0, z0), P(x0, y0, z0)],
                  'down': [P(x0, y1, z0), P(x1, y1, z0), P(x1, y1, z1), P(x0, y1, z1)]}
            for k, pts in fs.items():
                if cols[k] is not None:
                    quads.append((pts, cols[k]))
    ya, pa = math.radians(yaw_deg), math.radians(pitch_deg)

    def view(p):
        x, y, z = p[0], -p[1] + 24, p[2]          # model y is down; flip so up is up, ground at 0
        x, z = x * math.cos(ya) - z * math.sin(ya), x * math.sin(ya) + z * math.cos(ya)
        y, z = y * math.cos(pa) - z * math.sin(pa), y * math.sin(pa) + z * math.cos(pa)
        return x, y, z

    projected = []
    allpts = []
    for pts, img in quads:
        vp = [view(p) for p in pts]
        allpts += vp
        projected.append((sum(p[2] for p in vp) / 4, vp, img))
    minx, maxx = min(p[0] for p in allpts), max(p[0] for p in allpts)
    miny, maxy = min(p[1] for p in allpts), max(p[1] for p in allpts)
    k = (size - 20) / max(maxx - minx, maxy - miny)
    out = Image.new('RGBA', (size, size), (235, 235, 240, 255))
    for _, vp, img in sorted(projected, key=lambda q: -q[0]):
        poly = [((p[0] - minx) * k + 10, size - ((p[1] - miny) * k + 10)) for p in vp]
        # flat-shade with the face's mean colour, then draw the texture's details by sampling a coarse grid
        n = 4
        a, b, c, dd = poly
        tw, th = img.size
        for i in range(n):
            for j in range(n):
                def lerp(p, q, t):
                    return (p[0] + (q[0] - p[0]) * t, p[1] + (q[1] - p[1]) * t)
                def at(s, t):
                    return lerp(lerp(a, b, s), lerp(dd, c, s), t)
                cell = [at(i / n, j / n), at((i + 1) / n, j / n), at((i + 1) / n, (j + 1) / n), at(i / n, (j + 1) / n)]
                px = img.getpixel((min(tw - 1, int((i + 0.5) / n * tw)), min(th - 1, int((j + 0.5) / n * th))))
                if px[3] == 0:
                    continue
                ImageDraw.Draw(out).polygon(cell, fill=px[:3] + (255,))
    return out


def main():
    out = sys.argv[1]
    ids = sys.argv[2:] or sorted(f[:-5] for f in os.listdir(os.path.join(ASSETS, 'form_models')))
    tiles = []
    for mid in ids:
        a = render(mid, 30, -15)
        b = render(mid, 90, -8)
        t = Image.new('RGBA', (720, 380), (255, 255, 255, 255))
        t.paste(a, (0, 20)); t.paste(b, (360, 20))
        ImageDraw.Draw(t).text((6, 4), mid, fill=(0, 0, 0, 255))
        tiles.append(t)
    cols = 2
    sheet = Image.new('RGBA', (720 * cols, 380 * ((len(tiles) + cols - 1) // cols)), (255, 255, 255, 255))
    for i, t in enumerate(tiles):
        sheet.paste(t, ((i % cols) * 720, (i // cols) * 380))
    sheet.save(out)


if __name__ == '__main__':
    main()
