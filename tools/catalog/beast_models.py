"""Low-poly form models for NarutoRPG: the nine tailed beasts, the Ten-Tails and Susanoo.

Authored by hand from the Narutopedia infobox images of each page (Shukaku, Matatabi, Isobu, Son Gokū, Kokuō, Saiken,
Chōmei, Gyūki, Kurama, Ten-Tails, Susanoo); palettes are the dominant colours sampled from those images. Each model is a
few dozen boxes with animation tags (legs walk, tails sway, wings flap, jaw snaps, head looks) read by the engine's
FormModels (animecore). Writes:
  narutorpg/src/main/resources/assets/narutorpg/form_models/<id>.json
  narutorpg/src/main/resources/assets/narutorpg/textures/entity/form/<id>.png   (128x128, painted box-UV)
Run: python3 tools/catalog/beast_models.py
Coordinates are vanilla entity-model pixels: y grows downward, the ground is y = 24, the front faces -z.
"""
import json
import math
import os
import random

from PIL import Image, ImageDraw

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
ASSETS = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg')
TEX = 128


def rgb(h, a=255):
    return ((h >> 16) & 255, (h >> 8) & 255, h & 255, a)


def mul(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + (c[3],)


class Model:
    def __init__(self, mid, mats, translucent=False):
        self.id, self.mats, self.translucent = mid, mats, translucent
        self.parts = []

    def part(self, name, pivot, cubes, parent=None, rot=(0, 0, 0), anim=None, phase=0.0, amp=1.0):
        self.parts.append(dict(name=name, parent=parent, pivot=list(pivot), rot=list(rot), cubes=cubes, anim=anim, phase=phase, amp=amp))
        return name

    def chain(self, base, parent, pivot, rot, segs, mat, anim='tail', phase=0.0, amp=1.0, curl=0.25, spikes=None):
        """A tail / tentacle: segments of (w, h, d), each a child of the previous, curling by ``curl``."""
        prev, pv, r = parent, pivot, rot
        for i, (w, h, d) in enumerate(segs):
            cubes = [cube(-w / 2, -h / 2, 0, w, h, d, mat)]
            if spikes and i < len(segs) - 1:
                cubes.append(cube(-1, -h / 2 - 3, d / 2 - 1, 2, 3, 2, spikes))
            name = self.part(f'{base}_{i}', pv, cubes, parent=prev, rot=r, anim=anim, phase=phase + i * 0.6, amp=amp)
            prev, pv, r = name, (0, 0, d - 1), (curl, 0, 0)
        return prev


def cube(x, y, z, w, h, d, mat, feat=None, inflate=0.0, mirror=False):
    return dict(frm=[x, y, z], size=[int(w), int(h), int(d)], mat=mat, feat=feat, inflate=inflate, mirror=mirror)


# ======================================================================================================================
# painting
# ======================================================================================================================

def faces(u, v, w, h, d):
    return {'top': (u + d, v, w, d), 'bottom': (u + d + w, v, w, d), 'right': (u, v + d, d, h),
            'front': (u + d, v + d, w, h), 'left': (u + d + w, v + d, d, h), 'back': (u + 2 * d + w, v + d, w, h)}


SHADE = {'top': 1.12, 'bottom': 0.72, 'front': 1.0, 'back': 0.88, 'right': 0.94, 'left': 0.94}


def paint_region(img, u, v, size, mat, feat, seed):
    w, h, d = size
    base, pattern, pc = mat['base'], mat.get('pattern'), mat.get('pc')
    alpha = mat.get('alpha', 255)
    r = random.Random(seed)
    dr = ImageDraw.Draw(img)
    for name, (x, y, fw, fh) in faces(u, v, w, h, d).items():
        if fw <= 0 or fh <= 0:
            continue
        c = mul(rgb(base, alpha), SHADE[name])
        for py in range(y, y + fh):
            for px in range(x, x + fw):
                n = 1 + (r.random() - 0.5) * 0.08
                # soft vertical gradient: darker toward the bottom of side faces
                g = 1.0 if name in ('top', 'bottom') else 1.06 - 0.16 * (py - y) / max(1, fh - 1)
                img.putpixel((px, py), mul(c, n * g))
        P = rgb(pc, alpha) if pc is not None else None
        if pattern == 'swirl' and name not in ('bottom',):        # Shukaku's sand markings
            for _ in range(max(1, fw * fh // 40)):
                cx, cy = x + r.randrange(fw), y + r.randrange(fh)
                rad = r.randint(2, 4)
                dr.arc([cx - rad, cy - rad, cx + rad, cy + rad], r.randint(0, 180), r.randint(200, 340), fill=P)
        elif pattern == 'flames' and name != 'bottom':           # Matatabi's blue fire
            for _ in range(max(1, fw * fh // 18)):
                cx, cy = x + r.randrange(fw), y + r.randrange(fh)
                ln = r.randint(2, 4)
                dr.line([(cx, cy), (cx + r.choice((-1, 0, 1)), cy - ln)], fill=P if r.random() < 0.5 else rgb(mat['pc2'], alpha))
        elif pattern == 'plates' and name in ('top', 'front', 'back', 'left', 'right'):   # Isobu's shell
            step = 4
            for gx in range(x, x + fw, step):
                dr.line([(gx, y), (gx, y + fh - 1)], fill=P)
            for gy in range(y, y + fh, step):
                dr.line([(x, gy), (x + fw - 1, gy)], fill=P)
        elif pattern == 'suckers' and name in ('bottom', 'front', 'right', 'left'):        # Gyūki's tentacles
            for gx in range(x + 1, x + fw - 1, 3):
                for gy in range(y + 1, y + fh - 1, 3):
                    dr.point((gx, gy), fill=P)
        elif pattern == 'veins':                                   # Chōmei's wings: green rim and a few veins
            dr.rectangle([x, y, x + fw - 1, y + fh - 1], outline=P)
            for k in range(2, fw - 1, 5):
                dr.line([(x + k, y + 1), (x + k + 1, y + fh - 2)], fill=P)
        elif pattern == 'cracks' and name != 'bottom':            # the Ten-Tails' bark-like skin
            for _ in range(max(1, fw * fh // 30)):
                cx, cy = x + r.randrange(fw), y + r.randrange(fh)
                dr.line([(cx, cy), (cx + r.randint(-3, 3), cy + r.randint(1, 4))], fill=P)
        elif pattern == 'ribs' and name in ('front', 'back', 'left', 'right'):  # Susanoo's chakra ribcage
            for gy in range(y + 1, y + fh, 3):
                dr.line([(x, gy), (x + fw - 1, gy)], fill=P)
            dr.line([(x + fw // 2, y), (x + fw // 2, y + fh - 1)], fill=P)
        elif pattern == 'spots' and name != 'bottom':
            for _ in range(max(1, fw * fh // 25)):
                cx, cy = x + r.randrange(fw), y + r.randrange(fh)
                dr.point((cx, cy), fill=P)
        elif pattern == 'tipped' and name in ('front', 'back', 'left', 'right'):   # lighter fur toward the tail tip
            for py in range(y + fh * 2 // 3, y + fh):
                for px in range(x, x + fw):
                    img.putpixel((px, py), mul(rgb(pc, alpha), 1.0 + (r.random() - 0.5) * 0.06))
    if feat:
        paint_face(img, faces(u, v, w, h, d)['front'], feat, alpha)


def paint_face(img, rect, f, alpha):
    x, y, w, h = rect
    d = ImageDraw.Draw(img)
    ey = y + int(h * f.get('eye_y', 0.35))
    eye = rgb(f.get('eye', 0xFFEB3B), alpha)
    pupil = rgb(f.get('pupil', 0x111111), alpha)
    if f.get('ring'):                      # dark eye rings / fox markings
        rc = rgb(f['ring'], alpha)
        d.rectangle([x + 1, ey - 1, x + w // 2 - 1, ey + 1], fill=rc)
        d.rectangle([x + w // 2 + 1, ey - 1, x + w - 2, ey + 1], fill=rc)
    if f.get('eyes', 2) == 1:
        cx = x + w // 2
        d.rectangle([cx - 1, ey - 1, cx + 1, ey + 1], fill=eye)
        d.point((cx, ey), fill=pupil)
    elif f.get('eyes', 2) == 2:
        sep = max(2, int(w * f.get('sep', 0.25)))
        for cx in (x + w // 2 - sep, x + w // 2 + sep - 1):
            d.rectangle([cx - 1, ey, cx + 1, ey], fill=eye)
            d.point((cx, ey), fill=pupil)
    if f.get('mouth'):
        my = y + int(h * f.get('mouth_y', 0.75))
        mw = max(2, int(w * f.get('mouth_w', 0.6)))
        mx = x + (w - mw) // 2
        d.rectangle([mx, my, mx + mw - 1, my + 1], fill=rgb(0x3A0A0A, alpha))
        if f['mouth'] == 'teeth':
            for tx in range(mx, mx + mw, 2):
                d.point((tx, my), fill=rgb(0xF5F5F5, alpha))
                d.point((tx + 1, my + 1), fill=rgb(0xF5F5F5, alpha))


# ======================================================================================================================
# packing + export
# ======================================================================================================================

def export(m, assets=None, ns='narutorpg', path=None):
    """Writes the model json + painted texture; ``path`` (default the model id) may contain a sub-folder (stand/emperor)."""
    assets = assets or ASSETS
    path = path or m.id
    tex_name = path.replace('/', '_')
    img = Image.new('RGBA', (TEX, TEX), (0, 0, 0, 0))
    regions = {}
    shelf_x, shelf_y, shelf_h = 0, 0, 0
    seed = 0
    out_parts = []
    # big regions first for a tight packing
    all_cubes = sorted({(tuple(c['size']), c['mat'], json.dumps(c['feat'], sort_keys=True)) for p in m.parts for c in p['cubes']},
                       key=lambda k: (-(k[0][1] + k[0][2]), k))   # tie-break: deterministic across runs
    for key in all_cubes:
        (w, h, d), mat, feat = key
        rw, rh = 2 * (d + w), d + h
        if shelf_x + rw > TEX:
            shelf_x, shelf_y, shelf_h = 0, shelf_y + shelf_h, 0
        if shelf_y + rh > TEX:
            raise SystemExit(f'{m.id}: texture full ({TEX}x{TEX})')
        regions[key] = (shelf_x, shelf_y)
        seed += 1
        paint_region(img, shelf_x, shelf_y, (w, h, d), m.mats[mat], json.loads(feat), seed)
        shelf_x += rw
        shelf_h = max(shelf_h, rh)
    for p in m.parts:
        cubes = []
        for c in p['cubes']:
            uv = regions[(tuple(c['size']), c['mat'], json.dumps(c['feat'], sort_keys=True))]
            e = {'from': [round(v, 2) for v in c['frm']], 'size': c['size'], 'uv': list(uv)}
            if c['inflate']:
                e['inflate'] = c['inflate']
            if c['mirror']:
                e['mirror'] = True
            cubes.append(e)
        q = {'name': p['name'], 'pivot': [round(v, 3) for v in p['pivot']], 'cubes': cubes}
        if p['parent']:
            q['parent'] = p['parent']
        if any(p['rot']):
            q['rotation'] = [round(v, 4) for v in p['rot']]
        if p['anim']:
            q['anim'] = p['anim']
            if p['phase']:
                q['phase'] = round(p['phase'], 3)
            if p['amp'] != 1.0:
                q['amp'] = p['amp']
        out_parts.append(q)
    os.makedirs(os.path.dirname(os.path.join(assets, 'form_models', path + '.json')), exist_ok=True)
    os.makedirs(os.path.join(assets, 'textures', 'entity', 'form'), exist_ok=True)
    img.save(os.path.join(assets, 'textures', 'entity', 'form', tex_name + '.png'), optimize=True)
    with open(os.path.join(assets, 'form_models', path + '.json'), 'w') as f:
        json.dump({'texture': f'{ns}:textures/entity/form/{tex_name}.png', 'texture_size': [TEX, TEX],
                   'translucent': m.translucent, 'parts': out_parts}, f, indent=1)
    used = shelf_y + shelf_h
    return len(out_parts), sum(len(p['cubes']) for p in m.parts), used


def legs4(m, mat, front_z, back_z, x, top_y, size, feet=None):
    w, h, d = size
    for i, (lx, lz, anim) in enumerate(((x, front_z, 'leg_a'), (-x, front_z, 'leg_b'), (x, back_z, 'leg_b'), (-x, back_z, 'leg_a'))):
        cubes = [cube(-w / 2, 0, -d / 2, w, h, d, mat)]
        if feet:
            cubes.append(cube(-w / 2 - 0.5, h - 2, -d / 2 - 1, w + 1, 2, d + 1, feet))
        m.part(f'leg{i}', (lx, top_y, lz), cubes, anim=anim)


def tails(m, n, pivot, segs, mat, spread, lift, curl=0.22, spikes=None, amp=1.0, parent=None, z_spread=0.0, stagger=0.0):
    """n tails fanned across ``spread`` radians of yaw, roots spread over ``z_spread`` px across the rump; ``stagger``
    alternates their lift so neighbouring tails separate instead of merging into one fan."""
    for i in range(n):
        t = 0.0 if n == 1 else (i / (n - 1) - 0.5)
        pv = (pivot[0] + t * z_spread, pivot[1] + (abs(t) * 2 if stagger else 0), pivot[2])
        li = lift + (stagger if i % 2 else -stagger * 0.6)
        m.chain(f'tail{i}', parent, pv, (li, t * spread, 0), segs, mat, phase=i * 0.7, amp=amp, curl=curl, spikes=spikes)


# ======================================================================================================================
# the models (from the Narutopedia images)
# ======================================================================================================================

def shukaku():
    # hulking sand tanuki: barrel body, round belly, short limbs, one huge thick tail, dark-blue swirl markings
    m = Model('shukaku', {'body': {'base': 0xB69773, 'pattern': 'swirl', 'pc': 0x2E3F78},
                          'belly': {'base': 0xCDB28C, 'pattern': 'swirl', 'pc': 0x3A4A80},
                          'dark': {'base': 0x574B42}})
    m.part('body', (0, 12, 0), [cube(-11, -12, -8, 22, 21, 18, 'body'), cube(-8, -7, -9, 16, 14, 1, 'belly')], anim='bob')
    m.part('head', (0, -11, -3), [cube(-7, -10, -7, 14, 10, 11, 'body',
                                       feat={'eyes': 2, 'eye': 0xF2C94C, 'pupil': 0x111111, 'ring': 0x1B1B2A, 'eye_y': 0.4, 'sep': 0.22,
                                             'mouth': 'teeth', 'mouth_y': 0.85, 'mouth_w': 0.55}),
                                  cube(-7, -14, -3, 4, 4, 3, 'body'), cube(3, -14, -3, 4, 4, 3, 'body'),
                                  cube(-4, -3, -9, 8, 3, 2, 'belly')], parent='body', anim='head')
    m.part('arm_r', (-11, -6, -3), [cube(-5, -1, -3, 6, 14, 6, 'body'), cube(-5, 11, -4, 6, 3, 7, 'belly')], parent='body', anim='arm_a')
    m.part('arm_l', (11, -6, -3), [cube(-1, -1, -3, 6, 14, 6, 'body'), cube(-1, 11, -4, 6, 3, 7, 'belly')], parent='body', anim='arm_b')
    for i, (x, anim) in enumerate(((-6, 'leg_a'), (6, 'leg_b'))):
        m.part(f'leg{i}', (x, 15, 3), [cube(-4, 0, -4, 8, 9, 8, 'body'), cube(-4.5, 7, -5.5, 9, 2, 9, 'belly')], anim=anim)
    m.chain('tail', None, (0, 8, 9), (0.55, 0, 0), [(10, 10, 9), (9, 9, 8), (8, 8, 8), (6, 6, 7)], 'body', curl=0.42, amp=0.6)
    return m


def matatabi():
    # lean blue-fire cat: low body, tall slender legs, two tails, mismatched eyes, open fanged mouth
    mats = {'body': {'base': 0x407BC1, 'pattern': 'flames', 'pc': 0x142232, 'pc2': 0x8FD0FF},
            'light': {'base': 0x65A6DC, 'pattern': 'flames', 'pc': 0xB3E5FF, 'pc2': 0x2C5998}}
    m = Model('matatabi', mats)
    m.part('body', (0, 9, 0), [cube(-5, -5, -10, 10, 9, 20, 'body')], anim='bob', amp=0.5)
    m.part('head', (0, -3, -10), [cube(-5, -5, -7, 10, 8, 7, 'body',
                                       feat={'eyes': 2, 'eye': 0x9CFF57, 'pupil': 0x111111, 'eye_y': 0.35, 'sep': 0.28,
                                             'mouth': 'teeth', 'mouth_y': 0.8, 'mouth_w': 0.7}),
                                  cube(-3, -1, -9, 6, 4, 2, 'light', feat={'eyes': 0, 'mouth': 'teeth', 'mouth_y': 0.5, 'mouth_w': 0.8}),
                                  cube(-5, -8, -4, 3, 3, 2, 'body'), cube(2, -8, -4, 3, 3, 2, 'body')], parent='body', anim='head')
    legs4(m, 'body', -7, 7, 3.5, 13, (4, 11, 4), feet='light')
    tails(m, 2, (0, 5, 10), [(3, 3, 8), (3, 3, 8), (2, 2, 7)], 'light', spread=0.8, lift=0.9, curl=0.15)
    return m


def isobu():
    # spiked grey turtle: domed plated shell (red under-shell), clawed legs, one-eyed spiky head, three spiked tails
    mats = {'shell': {'base': 0x8E9197, 'pattern': 'plates', 'pc': 0x403C42}, 'under': {'base': 0x8C3A3A, 'pattern': 'plates', 'pc': 0x5A2020},
            'skin': {'base': 0x7A787E, 'pattern': 'spots', 'pc': 0x403C42}, 'spike': {'base': 0xC3CAC9}}
    m = Model('isobu', mats)
    spikes = [cube(x, -14, z, 2, 4, 2, 'spike') for x, z in ((-7, -6), (5, -6), (-1, -8), (-7, 3), (5, 3), (-1, 0), (-1, 7))]
    m.part('body', (0, 13, 0), [cube(-11, -10, -11, 22, 10, 22, 'shell'), cube(-9, -14, -9, 18, 4, 18, 'shell'),
                                cube(-10, 0, -10, 20, 3, 20, 'under')] + spikes, anim='bob', amp=0.4)
    m.part('head', (0, -4, -11), [cube(-4, -4, -7, 8, 7, 7, 'skin', feat={'eyes': 1, 'eye': 0xD32F2F, 'pupil': 0x111111, 'eye_y': 0.35,
                                                                       'mouth': 'teeth', 'mouth_y': 0.8, 'mouth_w': 0.7}),
                                  cube(-1, -7, -6, 2, 3, 2, 'spike'), cube(-4, -6, -3, 2, 2, 2, 'spike'), cube(2, -6, -3, 2, 2, 2, 'spike')],
           parent='body', anim='head')
    legs4(m, 'skin', -7, 7, 9, 15, (6, 9, 6), feet='spike')
    tails(m, 3, (0, 7, 11), [(4, 4, 8), (3, 3, 8)], 'skin', spread=1.0, lift=0.35, spikes='spike', amp=0.7)
    return m


def son_goku():
    # red four-tailed gorilla: massive chest, pale belly, horned head with beard, long arms, tails with bone spikes
    mats = {'red': {'base': 0xA52103, 'pattern': 'spots', 'pc': 0x611B08}, 'belly': {'base': 0x9FA477},
            'horn': {'base': 0xD9C29A}, 'face': {'base': 0x9FA477}}
    m = Model('son_goku', mats)
    m.part('body', (0, 6, 0), [cube(-8, -10, -6, 16, 18, 12, 'red'), cube(-6, -6, -7, 12, 12, 1, 'belly')], anim='bob', amp=0.5)
    m.part('head', (0, -10, -3), [cube(-5, -8, -6, 10, 9, 8, 'red', feat={'eyes': 2, 'eye': 0xF5F5F5, 'pupil': 0xB71C1C, 'eye_y': 0.35}),
                                  cube(-4, -3, -8, 8, 4, 2, 'face', feat={'eyes': 0, 'mouth': 'teeth', 'mouth_y': 0.6, 'mouth_w': 0.8}),
                                  cube(-6, -11, -2, 2, 4, 2, 'horn'), cube(4, -11, -2, 2, 4, 2, 'horn'),
                                  cube(-7, -13, -1, 2, 3, 2, 'horn'), cube(5, -13, -1, 2, 3, 2, 'horn')], parent='body', anim='head')
    m.part('arm_r', (-9, -7, -1), [cube(-6, -1, -3, 6, 18, 6, 'red'), cube(-6, 15, -4, 6, 3, 7, 'red')], parent='body', anim='arm_a')
    m.part('arm_l', (9, -7, -1), [cube(0, -1, -3, 6, 18, 6, 'red'), cube(0, 15, -4, 6, 3, 7, 'red')], parent='body', anim='arm_b')
    m.part('leg0', (-4, 14, 2), [cube(-3, 0, -3, 6, 10, 6, 'red')], anim='leg_a')
    m.part('leg1', (4, 14, 2), [cube(-3, 0, -3, 6, 10, 6, 'red')], anim='leg_b')
    tails(m, 4, (0, 8, 6), [(3, 3, 8), (3, 3, 8), (2, 2, 7)], 'red', spread=1.4, lift=1.0, spikes='horn', z_spread=4)
    return m


def kokuo():
    # white horse-dolphin: slim body, long thin legs, long neck and dolphin snout, four horns, five broad tails
    mats = {'white': {'base': 0xE6E7F2, 'pattern': 'spots', 'pc': 0x9EABB8}, 'shade': {'base': 0xB9C4D0},
            'horn': {'base': 0xC9A877}}
    m = Model('kokuo', mats)
    m.part('body', (0, 8, 0), [cube(-5, -5, -10, 10, 9, 20, 'white')], anim='bob', amp=0.4)
    m.part('neck', (0, -3, -9), [cube(-3, -10, -4, 6, 11, 6, 'white')], parent='body', rot=(0.25, 0, 0))
    m.part('head', (0, -9, -2), [cube(-3, -4, -10, 6, 5, 10, 'white', feat={'eyes': 0}),
                                 cube(-3.5, -4, -5, 7, 2, 2, 'shade', feat={'eyes': 2, 'eye': 0x4FC3F7, 'pupil': 0xB71C1C, 'eye_y': 0.4, 'sep': 0.45}),
                                 cube(-3, -7, -3, 1, 3, 1, 'horn'), cube(2, -7, -3, 1, 3, 1, 'horn'),
                                 cube(-4, -6, -1, 1, 3, 1, 'horn'), cube(3, -6, -1, 1, 3, 1, 'horn')], parent='neck', anim='head')
    legs4(m, 'white', -7, 7, 3, 12, (3, 12, 3), feet='shade')
    tails(m, 5, (0, 4, 10), [(3, 2, 8), (3, 2, 8), (4, 1, 6)], 'white', spread=2.2, lift=0.45, curl=0.25, z_spread=8, stagger=0.35)
    return m


def saiken():
    # pale lavender slug: tall bulbous body, small head with a stub horn, little arms, six tentacle tails
    mats = {'slug': {'base': 0xB9B8CA, 'pattern': 'spots', 'pc': 0x8D89A1}, 'light': {'base': 0xCCCCDC}, 'dark': {'base': 0x6E6A80}}
    m = Model('saiken', mats)
    m.part('body', (0, 12, 0), [cube(-8, -12, -7, 16, 24, 14, 'slug'), cube(-6, -6, -8, 12, 14, 1, 'light')], anim='bob', amp=0.8)
    m.part('head', (0, -12, -2), [cube(-6, -8, -6, 12, 9, 10, 'slug', feat={'eyes': 2, 'eye': 0x2B2638, 'pupil': 0x2B2638, 'eye_y': 0.45, 'sep': 0.25,
                                                                         'mouth': True, 'mouth_y': 0.85, 'mouth_w': 0.3}),
                                  cube(-1, -11, -3, 2, 3, 2, 'dark'), cube(-0.5, -14, -2, 1, 3, 1, 'dark')], parent='body', anim='head')
    m.part('arm_r', (-8, -4, -2), [cube(-3, 0, -2, 3, 8, 4, 'slug')], parent='body', anim='arm_a', amp=0.4)
    m.part('arm_l', (8, -4, -2), [cube(0, 0, -2, 3, 8, 4, 'slug')], parent='body', anim='arm_b', amp=0.4)
    tails(m, 6, (0, 10, 6), [(4, 4, 7), (3, 3, 7), (2, 2, 6)], 'slug', spread=2.2, lift=0.3, curl=0.3, amp=1.2, z_spread=8)
    return m


def chomei():
    # rhinoceros-beetle: blue armoured body, horned head, six thin legs, six orange veined wings, one tail
    mats = {'shell': {'base': 0x4A6491, 'pattern': 'spots', 'pc': 0x2E3F5E}, 'dark': {'base': 0x2E3F5E},
            'wing': {'base': 0xB86A1E, 'pattern': 'veins', 'pc': 0x9CC37A}, 'tail': {'base': 0x788A67, 'pattern': 'spots', 'pc': 0x565134}}
    m = Model('chomei', mats)
    m.part('body', (0, 13, 0), [cube(-4, -3, -8, 8, 6, 16, 'shell'), cube(-3, -4, -4, 6, 1, 10, 'dark')], anim='bob', amp=0.6)
    m.part('head', (0, -1, -8), [cube(-3, -3, -5, 6, 5, 5, 'shell', feat={'eyes': 2, 'eye': 0xFF5252, 'pupil': 0xB71C1C, 'eye_y': 0.4, 'sep': 0.3}),
                                 cube(-1, -9, -6, 2, 7, 2, 'dark'), cube(-2, -10, -7, 4, 2, 2, 'dark'),
                                 cube(-3, 1, -6, 1, 2, 2, 'dark'), cube(2, 1, -6, 1, 2, 2, 'dark')], parent='body', anim='head')
    for i, z in enumerate((-5, 0, 5)):
        for side, sx in ((-1, -4), (1, 4)):
            m.part(f'leg{i}{side}', (sx, 14, z), [cube(-0.5, 0, -0.5, 1, 11, 1, 'dark')], rot=(0, 0, side * 0.55),
                   anim='leg_a' if (i + (side > 0)) % 2 else 'leg_b', amp=0.6)
    for i, (z, spread) in enumerate(((-4, 0.5), (0, 0.85), (4, 1.2))):
        for side in (-1, 1):
            m.part(f'wing{i}{side}', (side * 3, -4, z), [cube(0 if side > 0 else -18, -0.5, -3, 18, 1, 6, 'wing')], parent='body',
                   rot=(0, side * (i - 1) * 0.35, -side * spread), anim='wing', phase=i * 0.5 + (side > 0) * 3.14, amp=0.7)
    m.chain('tail', 'body', (0, 0, 8), (-0.1, 0, 0), [(3, 3, 10), (2, 2, 9)], 'tail', curl=0.1, amp=0.5)
    return m


def gyuki():
    # ox-headed brute: muscular humanoid torso, curved horns, toothy grin, eight octopus tentacles with suckers
    mats = {'skin': {'base': 0xA07E85, 'pattern': 'spots', 'pc': 0x694C51}, 'horn': {'base': 0xD8C9B5},
            'tent': {'base': 0x98787E, 'pattern': 'suckers', 'pc': 0xE8D8DC}}
    m = Model('gyuki', mats)
    m.part('body', (0, 6, 0), [cube(-9, -12, -6, 18, 18, 12, 'skin')], anim='bob', amp=0.5)
    m.part('head', (0, -12, -3), [cube(-5, -8, -5, 10, 8, 8, 'skin', feat={'eyes': 2, 'eye': 0xF5F5F5, 'pupil': 0x111111, 'eye_y': 0.35,
                                                                        'mouth': 'teeth', 'mouth_y': 0.75, 'mouth_w': 0.8}),
                                  cube(-3, -3, -7, 6, 3, 2, 'skin'),
                                  cube(-9, -8, -2, 4, 2, 2, 'horn'), cube(5, -8, -2, 4, 2, 2, 'horn'),
                                  cube(-11, -12, -2, 2, 5, 2, 'horn'), cube(9, -12, -2, 2, 5, 2, 'horn')], parent='body', anim='head')
    m.part('arm_r', (-10, -9, 0), [cube(-6, -1, -3, 6, 16, 6, 'skin')], parent='body', anim='arm_a')
    m.part('arm_l', (10, -9, 0), [cube(0, -1, -3, 6, 16, 6, 'skin')], parent='body', anim='arm_b')
    m.part('leg0', (-4, 14, 1), [cube(-3.5, 0, -3.5, 7, 10, 7, 'skin')], anim='leg_a')
    m.part('leg1', (4, 14, 1), [cube(-3.5, 0, -3.5, 7, 10, 7, 'skin')], anim='leg_b')
    tails(m, 8, (0, 10, 5), [(4, 4, 7), (4, 4, 7), (3, 3, 6), (2, 2, 6)], 'tent', spread=3.6, lift=0.75, curl=0.4, amp=1.3, z_spread=18,
          stagger=0.5)
    return m


def kurama():
    # nine-tailed fox: orange fur, long tall ears, black eye markings, red slit eyes, fanged snout, clawed arms, nine tails
    mats = {'fur': {'base': 0xE07A1F, 'pattern': 'spots', 'pc': 0x833A09}, 'chest': {'base': 0xEE9A45},
            'tail': {'base': 0xE07A1F, 'pattern': 'tipped', 'pc': 0xF3B060}, 'dark': {'base': 0x522406}}
    m = Model('kurama', mats)
    m.part('body', (0, 8, 0), [cube(-6, -8, -9, 12, 12, 18, 'fur'), cube(-5, -6, -10, 10, 10, 1, 'chest')], rot=(-0.15, 0, 0), anim='bob', amp=0.5)
    m.part('head', (0, -7, -9), [cube(-5, -5, -7, 10, 8, 8, 'fur', feat={'eyes': 2, 'eye': 0xD50000, 'pupil': 0x111111, 'ring': 0x111111,
                                                                      'eye_y': 0.4, 'sep': 0.27}),
                                 cube(-3, -1, -11, 6, 4, 4, 'fur', feat={'eyes': 0, 'mouth': 'teeth', 'mouth_y': 0.55, 'mouth_w': 0.9}),
                                 cube(-5, -13, -3, 3, 8, 2, 'fur'), cube(2, -13, -3, 3, 8, 2, 'fur')], parent='body', rot=(0.15, 0, 0), anim='head')
    m.part('arm_r', (-6, -1, -6), [cube(-4, 0, -2, 4, 16, 4, 'fur'), cube(-4.5, 14, -4, 5, 2, 6, 'dark')], parent='body', rot=(0.15, 0, 0), anim='arm_a')
    m.part('arm_l', (6, -1, -6), [cube(0, 0, -2, 4, 16, 4, 'fur'), cube(-0.5, 14, -4, 5, 2, 6, 'dark')], parent='body', rot=(0.15, 0, 0), anim='arm_b')
    m.part('leg0', (-4, 14, 6), [cube(-2.5, 0, -3, 5, 10, 6, 'fur')], anim='leg_a')
    m.part('leg1', (4, 14, 6), [cube(-2.5, 0, -3, 5, 10, 6, 'fur')], anim='leg_b')
    tails(m, 9, (0, 4, 9), [(4, 4, 10), (4, 4, 9), (3, 3, 8)], 'tail', spread=3.3, lift=0.75, curl=0.22, z_spread=10, stagger=0.4)
    return m


def ten_tails():
    # the Ten-Tails' first form: lumpy grey-brown mass, gaping toothed maw, a single red eye on top, spindly limbs, ten spiky tails
    mats = {'skin': {'base': 0x6A5C55, 'pattern': 'cracks', 'pc': 0x3F3837}, 'light': {'base': 0x938177, 'pattern': 'cracks', 'pc': 0x5A4E48},
            'maw': {'base': 0x2A1A18}}
    m = Model('ten_tails', mats)
    m.part('body', (0, 10, 0), [cube(-11, -12, -9, 22, 22, 18, 'skin'),
                                cube(-8, -9, -10, 16, 11, 1, 'maw', feat={'eyes': 0, 'mouth': 'teeth', 'mouth_y': 0.1, 'mouth_w': 1.0}),
                                cube(-8, -1, -10, 16, 1, 1, 'light'), cube(-8, -10, -10, 16, 1, 1, 'light')], anim='bob', amp=0.7)
    m.part('eye', (0, -12, -4), [cube(-3, -4, -3, 6, 4, 5, 'light', feat={'eyes': 1, 'eye': 0xD50000, 'pupil': 0x111111, 'eye_y': 0.5})],
           parent='body', anim='head')
    for i, (x, z, rr, anim) in enumerate(((-10, -6, -0.6, 'arm_a'), (10, -6, 0.6, 'arm_b'), (-10, 5, -0.5, 'leg_b'), (10, 5, 0.5, 'leg_a'))):
        m.part(f'limb{i}', (x, -2 + (6 if z > 0 else 0), z), [cube(-1.5, 0, -1.5, 3, 16, 3, 'light')], parent='body', rot=(0, 0, rr), anim=anim)
    tails(m, 10, (0, -8, 6), [(3, 3, 9), (3, 3, 8), (2, 2, 7)], 'skin', spread=3.6, lift=0.85, curl=0.3, spikes='light', amp=0.9,
          parent='body', z_spread=18, stagger=0.45)
    return m


def susanoo():
    # Susanoo (Itachi's, as on the Narutopedia page): a translucent red-orange chakra warrior - ribbed torso, skull-like
    # helmeted head with glowing eyes, long arms, flowing lower robe, sword in the right hand, shield on the left
    mats = {'chakra': {'base': 0xC1543A, 'pattern': 'ribs', 'pc': 0xF3A27F, 'alpha': 175},
            'dark': {'base': 0x9D3022, 'pattern': 'ribs', 'pc': 0xC1543A, 'alpha': 150},
            'glow': {'base': 0xFFD54F, 'alpha': 230}, 'blade': {'base': 0xFFF1C8, 'alpha': 200}}
    m = Model('susanoo', mats, translucent=True)
    m.part('body', (0, 0, 0), [cube(-9, -2, -5, 18, 14, 10, 'chakra'), cube(-8, 12, -4, 16, 12, 8, 'dark')], anim='sway')
    m.part('head', (0, -2, -1), [cube(-5, -10, -5, 10, 10, 9, 'chakra', feat={'eyes': 2, 'eye': 0xFFEB3B, 'pupil': 0xFFEB3B, 'eye_y': 0.45,
                                                                           'sep': 0.27, 'mouth': True, 'mouth_y': 0.8, 'mouth_w': 0.5}),
                                 cube(-6, -14, -2, 2, 5, 2, 'dark'), cube(4, -14, -2, 2, 5, 2, 'dark'), cube(-1, -12, -6, 2, 3, 2, 'dark')],
           parent='body', anim='head')
    m.part('arm_r', (-10, 0, 0), [cube(-5, -1, -3, 5, 16, 6, 'chakra'), cube(-3, 14, -18, 1, 2, 20, 'blade')], parent='body', anim='arm_a', amp=0.4)
    m.part('arm_l', (10, 0, 0), [cube(0, -1, -3, 5, 16, 6, 'chakra'), cube(5, 2, -7, 1, 14, 12, 'dark')], parent='body', anim='arm_b', amp=0.4)
    return m


MODELS = [shukaku, matatabi, isobu, son_goku, kokuo, saiken, chomei, gyuki, kurama, ten_tails, susanoo]


def main():
    for build in MODELS:
        m = build()
        parts, cubes, used = export(m)
        print(f'{m.id:10s} {parts:3d} parts {cubes:3d} boxes, texture rows used {used}/{TEX}')


if __name__ == '__main__':
    main()
