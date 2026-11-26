"""Story NPC skin layers for animecore (anime-agnostic).

Writes grayscale 64x64 player-skin layers to animecore/src/main/resources/assets/animecore/textures/entity/story_npc/.
StoryNpcRenderer draws the player model once per layer, tinted with the colours of the character's
StoryNpcDefinition.Appearance, so any number of story characters costs no extra textures.

Layers: skin, face (untinted: sclera + mouth), eyes (iris), pants, outfit_<style> + outfit_<style>_accent,
hair_<style>, headband (+ headband_plate, untinted metal), mark_<name> (untinted face marks), empty.
Run: python3 tools/catalog/npc_skins.py
"""
import os
import random

from PIL import Image

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
OUT = os.path.join(ROOT, 'animecore', 'src', 'main', 'resources', 'assets', 'animecore', 'textures', 'entity', 'story_npc')

# player skin UV boxes: (u, v, w, h, d) -> faces
PARTS = {
    'head': (0, 0, 8, 8, 8), 'hat': (32, 0, 8, 8, 8),
    'body': (16, 16, 8, 12, 4), 'jacket': (16, 32, 8, 12, 4),
    'rarm': (40, 16, 4, 12, 4), 'rsleeve': (40, 32, 4, 12, 4),
    'larm': (32, 48, 4, 12, 4), 'lsleeve': (48, 48, 4, 12, 4),
    'rleg': (0, 16, 4, 12, 4), 'rpants': (0, 32, 4, 12, 4),
    'lleg': (16, 48, 4, 12, 4), 'lpants': (0, 48, 4, 12, 4),
}


def faces(part):
    u, v, w, h, d = PARTS[part]
    return {'top': (u + d, v, w, d), 'bottom': (u + d + w, v, w, d), 'right': (u, v + d, d, h),
            'front': (u + d, v + d, w, h), 'left': (u + d + w, v + d, d, h), 'back': (u + d + w + d, v + d, w, h)}


def new():
    return Image.new('RGBA', (64, 64), (0, 0, 0, 0))


def shade(x, y, base=228, var=14, seed=0):
    r = random.Random(x * 131 + y * 71 + seed)
    g = max(0, min(255, base - r.randint(0, var)))
    return (g, g, g, 255)


def fill(img, part, which=('top', 'bottom', 'right', 'front', 'left', 'back'), rows=None, base=228, seed=0, value=None):
    """Fill faces of a part; rows=(start, end) limits the vertical span (side faces) as fractions 0..1."""
    for name, (x, y, w, h) in faces(part).items():
        if name not in which:
            continue
        y0, y1 = y, y + h
        if rows and name not in ('top', 'bottom'):
            y0, y1 = y + int(round(h * rows[0])), y + int(round(h * rows[1]))
        for py in range(y0, y1):
            for px in range(x, x + w):
                img.putpixel((px, py), value if value else shade(px, py, base, seed=seed))


def px(img, pts, color):
    for p in pts:
        img.putpixel(p, color)


def save(img, name):
    img.save(os.path.join(OUT, name + '.png'))


def main():
    os.makedirs(OUT, exist_ok=True)
    save(new(), 'empty')

    # ---- skin: every base part, slightly shaded; darker soles and a hint of shadow under the chin
    img = new()
    for p in ('head', 'body', 'rarm', 'larm', 'rleg', 'lleg'):
        fill(img, p, base=236, seed=1)
    fx, fy = faces('head')['front'][:2]
    px(img, [(fx + 3, fy + 6), (fx + 4, fy + 6)], (190, 190, 190, 255))   # mouth shading
    save(img, 'skin')

    # ---- face: sclera (white) + mouth, untinted
    img = new()
    px(img, [(fx + 1, fy + 4), (fx + 2, fy + 4), (fx + 5, fy + 4), (fx + 6, fy + 4)], (250, 250, 250, 255))
    px(img, [(fx + 3, fy + 6), (fx + 4, fy + 6)], (120, 60, 60, 255))
    save(img, 'face')

    # ---- eyes: iris pixels
    img = new()
    px(img, [(fx + 2, fy + 4), (fx + 5, fy + 4)], (255, 255, 255, 255))
    save(img, 'eyes')

    # ---- pants: legs + lower torso band, sandals left as skin (bottom 2 rows)
    img = new()
    for p in ('rleg', 'lleg'):
        fill(img, p, which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.83), base=225, seed=2)
    fill(img, 'body', which=('right', 'front', 'left', 'back'), rows=(0.75, 1.0), base=225, seed=2)
    save(img, 'pants')

    outfits()
    hair()
    headband()
    marks()
    print('story npc layers ->', OUT)


def outfits():
    # shirt: torso + short sleeves
    img, acc = new(), new()
    fill(img, 'body', which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.75), base=226, seed=3)
    for p in ('rarm', 'larm'):
        fill(img, p, which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.33), base=226, seed=3)
    x, y, w, h = faces('body')['front']
    for i in range(w):
        acc.putpixel((x + i, y), (255, 255, 255, 255))          # collar
    save(img, 'outfit_shirt'); save(acc, 'outfit_shirt_accent')

    # jacket (flak vest over long sleeves): base long sleeves + torso, accent = vest on the jacket layer
    img, acc = new(), new()
    fill(img, 'body', which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.75), base=222, seed=4)
    for p in ('rarm', 'larm'):
        fill(img, p, which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.85), base=222, seed=4)
    fill(acc, 'jacket', which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.72), base=235, seed=5)
    x, y, w, h = faces('jacket')['front']
    for yy in range(y + 3, y + 6):      # scroll pouches
        for xx in (x + 1, x + 2, x + 5, x + 6):
            acc.putpixel((xx, yy), (200, 200, 200, 255))
    for yy in range(y, y + int(h * 0.72)):
        acc.putpixel((x + 3, yy), (175, 175, 175, 255))
        acc.putpixel((x + 4, yy), (175, 175, 175, 255))
    save(img, 'outfit_jacket'); save(acc, 'outfit_jacket_accent')

    # cloak: long coat on the outer layers (torso, sleeves, down over the legs), accent = cloud pattern + collar
    img, acc = new(), new()
    fill(img, 'body', base=218, seed=6)
    fill(img, 'jacket', which=('top', 'right', 'front', 'left', 'back'), base=214, seed=6)
    for p in ('rsleeve', 'lsleeve'):
        fill(img, p, which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.9), base=214, seed=6)
    for p in ('rpants', 'lpants'):
        fill(img, p, which=('right', 'front', 'left', 'back'), rows=(0, 0.7), base=214, seed=6)
    r = random.Random(9)
    for part in ('jacket', 'rpants', 'lpants', 'rsleeve', 'lsleeve'):
        for name in ('front', 'back', 'left', 'right'):
            x, y, w, h = faces(part)[name]
            for _ in range(max(1, w * h // 22)):
                cx, cy = x + r.randrange(w), y + r.randrange(max(1, int(h * 0.7)))
                for dx, dy in ((0, 0), (1, 0), (0, 1), (-1, 0)):
                    if x <= cx + dx < x + w and y <= cy + dy < y + h:
                        acc.putpixel((cx + dx, cy + dy), (255, 255, 255, 255))
    x, y, w, h = faces('jacket')['front']
    for i in range(w):
        acc.putpixel((x + i, y), (255, 255, 255, 255)); acc.putpixel((x + i, y + 1), (255, 255, 255, 255))
    save(img, 'outfit_cloak'); save(acc, 'outfit_cloak_accent')

    # robe: long robe (kage, monks, priestesses), accent = sash + trim
    img, acc = new(), new()
    fill(img, 'body', base=230, seed=7)
    for p in ('rarm', 'larm'):
        fill(img, p, which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.9), base=230, seed=7)
    for p in ('rpants', 'lpants'):
        fill(img, p, which=('right', 'front', 'left', 'back'), rows=(0, 0.92), base=230, seed=7)
    for name in ('front', 'back', 'left', 'right'):
        x, y, w, h = faces('body')[name]
        for i in range(w):
            acc.putpixel((x + i, y + 7), (255, 255, 255, 255)); acc.putpixel((x + i, y + 8), (255, 255, 255, 255))
    x, y, w, h = faces('body')['front']
    for yy in range(y, y + 7):
        acc.putpixel((x + 3 + (yy - y) // 4, yy), (255, 255, 255, 255))
    save(img, 'outfit_robe'); save(acc, 'outfit_robe_accent')

    # bodysuit (green spandex, Anbu, Sound): whole body; accent = leg warmers / armour plates on the jacket layer
    img, acc = new(), new()
    for p in ('body', 'rarm', 'larm', 'rleg', 'lleg'):
        fill(img, p, which=('top', 'right', 'front', 'left', 'back'), base=224, seed=8)
    for p in ('rpants', 'lpants'):
        fill(acc, p, which=('right', 'front', 'left', 'back'), rows=(0.6, 0.85), base=236, seed=8)
    fill(acc, 'jacket', which=('front', 'back'), rows=(0.1, 0.55), base=236, seed=8)
    save(img, 'outfit_bodysuit'); save(acc, 'outfit_bodysuit_accent')

    # armor (samurai / Warring States): plates on the outer layers
    img, acc = new(), new()
    fill(img, 'body', base=220, seed=10)
    for p in ('rarm', 'larm'):
        fill(img, p, which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.9), base=220, seed=10)
    fill(acc, 'jacket', which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.75), base=238, seed=11)
    for p in ('rsleeve', 'lsleeve'):
        fill(acc, p, which=('top', 'right', 'front', 'left', 'back'), rows=(0, 0.3), base=238, seed=11)
    for p in ('rpants', 'lpants'):
        fill(acc, p, which=('right', 'front', 'left', 'back'), rows=(0, 0.35), base=238, seed=11)
    save(img, 'outfit_armor'); save(acc, 'outfit_armor_accent')


def hair():
    def head_cap(img, rows_front=2, rows_side=4, rows_back=8, seed=20):
        fill(img, 'head', which=('top',), base=230, seed=seed)
        for name, rows in (('front', rows_front), ('back', rows_back), ('left', rows_side), ('right', rows_side)):
            x, y, w, h = faces('head')[name]
            for yy in range(y, y + rows):
                for xx in range(x, x + w):
                    img.putpixel((xx, yy), shade(xx, yy, 230, seed=seed))

    # short
    img = new(); head_cap(img); save(img, 'hair_short')
    # spiky: cap + jagged spikes on the hat layer
    img = new(); head_cap(img, 2, 3, 6, seed=21)
    fill(img, 'hat', which=('top',), base=232, seed=21)
    for name in ('front', 'back', 'left', 'right'):
        x, y, w, h = faces('hat')[name]
        for xx in range(x, x + w):
            depth = 1 + (xx * 7 % 3)
            for yy in range(y, y + depth):
                img.putpixel((xx, yy), shade(xx, yy, 232, seed=21))
    save(img, 'hair_spiky')
    # long: cap + hair down the back and sides (hat layer + jacket back)
    img = new(); head_cap(img, 2, 7, 8, seed=22)
    for name in ('back', 'left', 'right'):
        x, y, w, h = faces('hat')[name]
        for yy in range(y + 1, y + h):
            for xx in range(x, x + w):
                img.putpixel((xx, yy), shade(xx, yy, 228, seed=22))
    x, y, w, h = faces('jacket')['back']
    for yy in range(y, y + 6):
        for xx in range(x + 1, x + w - 1):
            img.putpixel((xx, yy), shade(xx, yy, 228, seed=22))
    save(img, 'hair_long')
    # ponytail / topknot (Nara): cap + a tuft on top of the hat layer
    img = new(); head_cap(img, 2, 4, 6, seed=23)
    x, y, w, h = faces('hat')['top']
    for yy in range(y + 4, y + 8):
        for xx in range(x + 3, x + 5):
            img.putpixel((xx, yy), shade(xx, yy, 230, seed=23))
    xb, yb, wb, hb = faces('hat')['back']
    for yy in range(yb, yb + 3):
        for xx in range(xb + 3, xb + 5):
            img.putpixel((xx, yy), shade(xx, yy, 230, seed=23))
    save(img, 'hair_ponytail')
    # bowl cut (Guy / Lee): full fringe and sides
    img = new(); head_cap(img, 3, 6, 7, seed=24); save(img, 'hair_bowl')
    # buns (Tenten): cap + two buns on the hat layer
    img = new(); head_cap(img, 2, 4, 7, seed=25)
    x, y, w, h = faces('hat')['top']
    for (bx, by) in ((x + 1, y + 2), (x + 5, y + 2)):
        for yy in range(by, by + 3):
            for xx in range(bx, bx + 2):
                img.putpixel((xx, yy), shade(xx, yy, 232, seed=25))
    save(img, 'hair_buns')
    # wild / long spiky mane (Madara, Jiraiya): long + spikes
    img = Image.open(os.path.join(OUT, 'hair_long.png')).copy()
    spk = Image.open(os.path.join(OUT, 'hair_spiky.png'))
    img.alpha_composite(spk)
    save(img, 'hair_wild')
    save(new(), 'hair_bald')


def headband():
    img, plate = new(), new()
    for name in ('front', 'back', 'left', 'right'):
        x, y, w, h = faces('head')[name]
        for xx in range(x, x + w):
            img.putpixel((xx, y + 2), (235, 235, 235, 255))
            img.putpixel((xx, y + 3), (215, 215, 215, 255))
    x, y, w, h = faces('head')['front']
    for xx in range(x + 1, x + w - 1):
        plate.putpixel((xx, y + 2), (200, 205, 215, 255))
        plate.putpixel((xx, y + 3), (170, 175, 185, 255))
    plate.putpixel((x + 4, y + 2), (90, 95, 105, 255))
    save(img, 'headband'); save(plate, 'headband_plate')


def marks():
    x, y, w, h = faces('head')['front']
    img = new()   # whisker marks
    for (xx, yy) in ((x + 1, y + 5), (x + 1, y + 6), (x + 6, y + 5), (x + 6, y + 6)):
        img.putpixel((xx, yy), (70, 50, 40, 255))
    save(img, 'mark_whiskers')
    img = new()   # red fang marks (Inuzuka)
    for (xx, yy) in ((x + 1, y + 5), (x + 6, y + 5)):
        img.putpixel((xx, yy), (190, 30, 30, 255))
    save(img, 'mark_fangs')
    img = new()   # forehead seal (Byakugō)
    img.putpixel((x + 3, y + 2), (140, 60, 170, 255)); img.putpixel((x + 4, y + 2), (140, 60, 170, 255))
    save(img, 'mark_forehead')
    img = new()   # Sharingan / red eyes overlay
    img.putpixel((x + 2, y + 4), (210, 20, 20, 255)); img.putpixel((x + 5, y + 4), (210, 20, 20, 255))
    save(img, 'mark_red_eyes')
    img = new()   # Byakugan / pale eyes overlay
    img.putpixel((x + 2, y + 4), (225, 220, 240, 255)); img.putpixel((x + 5, y + 4), (225, 220, 240, 255))
    save(img, 'mark_pale_eyes')
    img = new()   # Rinnegan
    img.putpixel((x + 2, y + 4), (170, 140, 210, 255)); img.putpixel((x + 5, y + 4), (170, 140, 210, 255))
    save(img, 'mark_rinnegan')
    img = new()   # face mask (Kakashi)
    for yy in range(y + 5, y + 8):
        for xx in range(x, x + w):
            img.putpixel((xx, yy), (40, 45, 60, 255))
    for name in ('left', 'right'):
        fx, fy, fw, fh = faces('head')[name]
        for yy in range(fy + 5, fy + 8):
            for xx in range(fx, fx + fw):
                img.putpixel((xx, yy), (40, 45, 60, 255))
    save(img, 'mark_mask')
    img = new()   # face paint (Kankurō, Akimichi swirls)
    for (xx, yy) in ((x + 1, y + 5), (x + 6, y + 5), (x + 3, y + 1), (x + 4, y + 1)):
        img.putpixel((xx, yy), (120, 40, 150, 255))
    save(img, 'mark_paint')
    img = new()   # scar / stitches (Ibiki, Kakuzu)
    for i in range(4):
        img.putpixel((x + 2 + i, y + 3 + i // 2), (120, 40, 40, 255))
    save(img, 'mark_scar')
    img = new()   # tattoo ring (Gaara's "love" kanji on the forehead)
    for (xx, yy) in ((x + 5, y + 1), (x + 6, y + 1), (x + 5, y + 2)):
        img.putpixel((xx, yy), (160, 30, 30, 255))
    save(img, 'mark_forehead_kanji')
    img = new()   # glasses
    for xx in (x + 1, x + 2, x + 5, x + 6):
        img.putpixel((xx, y + 3), (40, 40, 40, 255))
    img.putpixel((x + 3, y + 4), (40, 40, 40, 255)); img.putpixel((x + 4, y + 4), (40, 40, 40, 255))
    save(img, 'mark_glasses')


if __name__ == '__main__':
    main()
