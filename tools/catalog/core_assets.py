"""Procedural engine assets: ward block textures/models and puppet skins.  python3 tools/catalog/core_assets.py"""
import json, os, random
from PIL import Image, ImageDraw
from spritegen import rgb, lighter, darker

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
A = os.path.join(ROOT, 'animecore', 'src', 'main', 'resources', 'assets', 'animecore')

def save(img, *path):
    p = os.path.join(A, *path); os.makedirs(os.path.dirname(p), exist_ok=True); img.save(p, optimize=True)

def jdump(obj, *path):
    p = os.path.join(A, *path); os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, 'w') as f: json.dump(obj, f, indent=1)

def glyph(d, r, x0, y0, w, h, c):
    for _ in range(6):
        x, y = x0 + r.randint(0, w - 1), y0 + r.randint(0, h - 1)
        if r.random() < 0.5: d.line([(x, y), (x + r.randint(-2, 2), y)], fill=c)
        else: d.line([(x, y), (x, y + r.randint(1, 3))], fill=c)

def tag_tex(ink, seed):
    r = random.Random(seed); img = Image.new('RGBA', (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    paper = rgb('#EDE3C8')
    d.rectangle([3, 1, 12, 14], fill=(*paper, 255)); d.rectangle([3, 1, 12, 2], fill=(*darker(paper, .15), 255)); d.rectangle([3, 13, 12, 14], fill=(*darker(paper, .15), 255))
    d.ellipse([5, 5, 10, 10], outline=(*ink, 255)); glyph(d, r, 5, 3, 6, 10, (*ink, 255))
    return img

def wall_tex(base, kind, seed):
    r = random.Random(seed); img = Image.new('RGBA', (16, 16)); d = ImageDraw.Draw(img)
    alpha = {'stone': 235, 'mirror': 210}.get(kind, 140)
    for y in range(16):
        for x in range(16):
            c = mix_noise(base, r, 0.08); img.putpixel((x, y), (*c, alpha))
    edge = lighter(base, 0.5)
    d.rectangle([0, 0, 15, 15], outline=(*edge, 220))
    if kind == 'barrier':
        for k in range(3): glyph(d, r, 3 + k * 4, 4, 3, 8, (*lighter(base, .7), 230))
    elif kind == 'crystal':
        for k in range(4): d.line([(r.randint(0, 15), 0), (r.randint(0, 15), 15)], fill=(*lighter(base, .6), 200))
    elif kind == 'stone':
        for k in range(5): d.line([(r.randint(0, 15), r.randint(0, 15)), (r.randint(0, 15), r.randint(0, 15))], fill=(*darker(base, .3), 255))
    elif kind == 'mirror':
        d.line([(3, 12), (12, 3)], fill=(255, 255, 255, 230)); d.line([(6, 13), (13, 6)], fill=(255, 255, 255, 160))
    elif kind == 'gate':
        d.rectangle([2, 2, 13, 13], outline=(*rgb('#F2D13A'), 240)); d.ellipse([5, 5, 10, 10], outline=(*rgb('#F2D13A'), 240))
    return img

def mix_noise(c, r, amt):
    k = (r.random() - 0.5) * 2 * amt
    return tuple(max(0, min(255, int(v + 255 * k))) for v in c)

def build_blocks():
    modes = {'barrier': rgb('#1E88E5'), 'explosive': rgb('#C62828'), 'smoke': rgb('#2E7D32')}
    variants = {}
    for i, (m, ink) in enumerate(modes.items()):
        save(tag_tex(ink, i), 'textures', 'block', f'ward_tag_{m}.png')
        jdump({"parent": "minecraft:block/block", "ambientocclusion": False, "textures": {"particle": f"animecore:block/ward_tag_{m}", "tag": f"animecore:block/ward_tag_{m}"},
               "elements": [{"from": [2, 0, 2], "to": [14, 0.25, 14],
                             "faces": {"up": {"uv": [0, 0, 16, 16], "texture": "#tag"}, "down": {"uv": [0, 0, 16, 16], "texture": "#tag", "cullface": "down"}}}]},
              'models', 'block', f'ward_tag_{m}.json')
        for linked in ('false', 'true'):
            variants[f'linked={linked},mode={m}'] = {"model": f"animecore:block/ward_tag_{m}"}
    jdump({"variants": variants}, 'blockstates', 'ward_tag.json')
    kinds = {'barrier': rgb('#4FC3F7'), 'crystal': rgb('#B39DDB'), 'stone': rgb('#8D8577'), 'mirror': rgb('#CFD8DC')}
    wv = {}
    for i, (k, c) in enumerate(kinds.items()):
        save(wall_tex(c, k, 10 + i), 'textures', 'block', f'chakra_wall_{k}.png')
        jdump({"parent": "minecraft:block/cube_all", "textures": {"all": f"animecore:block/chakra_wall_{k}"}}, 'models', 'block', f'chakra_wall_{k}.json')
        wv[f'kind={k}'] = {"model": f"animecore:block/chakra_wall_{k}"}
    jdump({"variants": wv}, 'blockstates', 'chakra_wall.json')
    save(wall_tex(rgb('#7E57C2'), 'gate', 99), 'textures', 'block', 'barrier_gate.png')
    jdump({"parent": "minecraft:block/cube_all", "textures": {"all": "animecore:block/barrier_gate"}}, 'models', 'block', 'barrier_gate.json')
    jdump({"variants": {"": {"model": "animecore:block/barrier_gate"}}}, 'blockstates', 'barrier_gate.json')

PUPPETS = {  # kind: (skin/wood, clothing, accent, eye)
    'wood': ('#A1724A', '#4E342E', '#D7CCC8', '#1A1A1A'), 'metal': ('#8A9099', '#3A3D44', '#C3CAD3', '#FF5252'),
    'crimson': ('#C9A07A', '#7A1C1C', '#212121', '#B71C1C'), 'crow': ('#6D4C41', '#212121', '#7E57C2', '#FFEB3B'),
    'ant': ('#212121', '#3E2723', '#C62828', '#FFFFFF'), 'lizard': ('#558B2F', '#33691E', '#F08C2E', '#FFEB3B'),
    'bee': ('#F2D13A', '#212121', '#ECEFF1', '#000000'), 'drone': ('#B0BEC5', '#455A64', '#4FC3F7', '#FF5252'),
    'demon': ('#5D1A1A', '#2B0B0B', '#E0E0E0', '#FFEB3B'), 'toad': ('#43A047', '#F08C2E', '#2E7D32', '#000000'),
}

def skin(kind, cols):
    sk, cl, ac, ey = (rgb(c) for c in cols); r = random.Random(kind)
    img = Image.new('RGBA', (64, 64), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    def box(x, y, w, h, dep, c):  # unwrap of a cuboid
        for (bx, by, bw, bh, cc) in [(x + dep, y, w, dep, lighter(c, .1)), (x + dep + w, y, w, dep, darker(c, .1)),
                                     (x, y + dep, dep, h, darker(c, .1)), (x + dep, y + dep, w, h, c),
                                     (x + dep + w, y + dep, dep, h, darker(c, .1)), (x + dep * 2 + w, y + dep, w, h, darker(c, .2))]:
            for yy in range(by, by + bh):
                for xx in range(bx, bx + bw):
                    img.putpixel((xx, yy), (*mix_noise(cc, r, 0.04), 255))
    box(0, 0, 8, 8, 8, sk)          # head
    box(16, 16, 8, 12, 4, cl)       # body
    box(40, 16, 4, 12, 4, sk)       # arm
    box(0, 16, 4, 12, 4, cl)        # leg
    for (x, y) in [(32, 48), (16, 48)]: box(x, y, 4, 12, 4, sk if x == 32 else cl)
    # face (front of head at 8..16, 8..16)
    d.rectangle([9, 11, 10, 12], fill=(*ey, 255)); d.rectangle([13, 11, 14, 12], fill=(*ey, 255))
    d.line([(9, 14), (14, 14)], fill=(*darker(sk, .5), 255)); d.line([(9, 14), (9, 15)], fill=(*darker(sk, .5), 255)); d.line([(14, 14), (14, 15)], fill=(*darker(sk, .5), 255))
    d.rectangle([8, 8, 15, 9], fill=(*ac, 255))
    # joints / threads
    for y in (22, 26): d.line([(44, y), (47, y)], fill=(*darker(sk, .35), 255)); d.line([(4, y), (7, y)], fill=(*darker(cl, .35), 255))
    d.line([(20, 24), (27, 24)], fill=(*ac, 255))
    return img

if __name__ == '__main__':
    build_blocks()
    for k, c in PUPPETS.items(): save(skin(k, c), 'textures', 'entity', 'puppet', k + '.png')
    print('core assets written')
