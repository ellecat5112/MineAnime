"""Offline isometric preview of the low-poly JSON models (painter's algorithm).  python3 preview3d.py out.png"""
import json, math, os, sys
from PIL import Image, ImageDraw
import catalog, model3d

def rot(p, axis, ang, o):
    a = math.radians(ang); x, y, z = p[0] - o[0], p[1] - o[1], p[2] - o[2]
    c, s = math.cos(a), math.sin(a)
    if axis == 'x': y, z = y * c - z * s, y * s + z * c
    elif axis == 'y': x, z = x * c + z * s, -x * s + z * c
    else: x, y = x * c - y * s, x * s + y * c
    return (x + o[0], y + o[1], z + o[2])

FACES = {'down': (0, 1, 5, 4), 'up': (3, 2, 6, 7), 'north': (0, 3, 7, 4) , 'south': (1, 2, 6, 5), 'west': (0, 1, 2, 3), 'east': (4, 5, 6, 7)}
SHADE = {'up': 1.0, 'down': 0.5, 'north': 0.8, 'south': 0.8, 'east': 0.6, 'west': 0.6}

def render(model, pal, size=128):
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    polys = []
    yaw, pitch = math.radians(35), math.radians(20)
    def proj(p):
        x, y, z = p[0] - 8, p[1] - 10, p[2] - 8
        x, z = x * math.cos(yaw) - z * math.sin(yaw), x * math.sin(yaw) + z * math.cos(yaw)
        y, z = y * math.cos(pitch) - z * math.sin(pitch), y * math.sin(pitch) + z * math.cos(pitch)
        k = size / 50
        return (size / 2 + x * k, size / 2 - y * k, z)
    for e in model['elements']:
        f, t = e['from'], e['to']
        pts = [(f[0], f[1], f[2]), (f[0], f[1], t[2]), (f[0], t[1], t[2]), (f[0], t[1], f[2]),
               (t[0], f[1], f[2]), (t[0], f[1], t[2]), (t[0], t[1], t[2]), (t[0], t[1], f[2])]
        if 'rotation' in e:
            r = e['rotation']; pts = [rot(p, r['axis'], r['angle'], r['origin']) for p in pts]
        for name, idx in FACES.items():
            u = e['faces'][name]['uv']; col = pal.getpixel((int(u[0]) + 1, int(u[1]) + 1))
            sh = SHADE[name]; col = (int(col[0] * sh), int(col[1] * sh), int(col[2] * sh), 255)
            pp = [proj(pts[i]) for i in idx]
            polys.append((sum(p[2] for p in pp) / 4, [(p[0], p[1]) for p in pp], col))
    for _, pts, col in sorted(polys, key=lambda q: -q[0]):
        d.polygon(pts, fill=col, outline=(0, 0, 0, 90))
    return img

if __name__ == '__main__':
    items = [(e['id'], *model3d.build(e['sprite']['shape'], e['id'], e['sprite']['colors'])) for e in catalog.T_
             if e['sprite']['shape'] in model3d.BUILDERS]
    cols = 10; size = 112
    sheet = Image.new('RGBA', (cols * size, ((len(items) + cols - 1) // cols) * size), (46, 48, 58, 255))
    for k, (iid, model, pal) in enumerate(items):
        sheet.alpha_composite(render(model, pal, size), ((k % cols) * size, (k // cols) * size))
    sheet.save(sys.argv[1]); print(len(items), 'models previewed')
