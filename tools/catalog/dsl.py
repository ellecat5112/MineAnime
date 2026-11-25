"""Tiny DSL used by catalog.py to describe ninja tools compactly."""
import re, unicodedata

def slug(title):
    t = unicodedata.normalize('NFKD', title).encode('ascii', 'ignore').decode()
    t = re.sub(r'[^a-zA-Z0-9]+', '_', t).strip('_').lower()
    return t

def eff(id_, dur=100, amp=0):
    return {"id": id_ if ':' in id_ else "minecraft:" + id_, "duration": dur, "amplifier": amp}

# ---------------------------------------------------------------- attack profiles
def melee(dmg=1, spd=-2.4, reach=0.0, *hits):
    d = {"damage": dmg, "speed": spd}
    if reach: d["reach"] = reach
    if hits: d["on_hit"] = [h for h in hits]
    return d

def hit(kind, strength=1.0, duration=60):
    return {"kind": kind, "strength": strength, "duration": duration}

FIST = melee(0, 0)

# ---------------------------------------------------------------- moves
def move(type_, cost=0, cd=20, variant=None, effects=None, color=None, **params):
    m = {"type": type_}
    if cost: m["cost"] = cost
    if cd != 20: m["cooldown"] = cd
    if params: m["params"] = {k: float(v) for k, v in params.items()}
    if effects: m["effects"] = effects
    if color is not None: m["color"] = color
    if variant: m["variant"] = variant
    return m

def throw(dmg=4, speed=1.6, count=1, spread=0, gravity=0.03, cd=10, cost=0, impact=None, effects=None, color=None,
          pierce=0, returns=0, drop=1, fuse=0, radius=0, consume=1, trap=0):
    return move("throw", cost, cd, impact, effects, color, damage=dmg, speed=speed, count=count, spread=spread,
                gravity=gravity, pierce=pierce, returns=returns, drop=drop, fuse=fuse, radius=radius, consume=consume, trap=trap)

def bomb(impact, effects=None, color=0x999999, radius=3, dmg=0, cd=20, speed=1.1, cost=0):
    return throw(dmg=dmg, speed=speed, impact=impact, effects=effects, color=color, radius=radius, drop=0, cd=cd, cost=cost)

def trap(count=6, dmg=3, dur=400, effects=None, cd=40):
    return move("trap", 0, cd, None, effects, None, count=count, damage=dmg, duration=dur)

def ward(dur=200, cd=10):
    return move("ward", 0, cd, None, None, None, duration=dur)

def consume(heal=0, chakra=0, food=0, cleanse=0, effects=None, time=32, drink=0, cd=0, consume_item=1, aoe=0):
    return move("consume", 0, cd, "drink" if drink else "eat", effects, None, heal=heal, chakra=chakra, food=food,
                cleanse=cleanse, time=time, consume=consume_item, aoe=aoe)

def charge(dmg=8, rng=5, cost=6, cd=30, color=0x9FD8FF, element=None, maxt=30):
    return move("charge_slash", cost, cd, element, None, color, damage=dmg, range=rng, max_ticks=maxt)

def dash(dmg=6, dist=6, cost=3, cd=40, color=0xFFFFFF, element=None):
    return move("dash", cost, cd, element, None, color, damage=dmg, distance=dist)

def chain(dmg=3, rng=16, cd=30, cost=0, effects=None, mode="pull"):
    return move("chain", cost, cd, mode, effects, None, damage=dmg, range=rng)

def guard(reflect=0, absorb=0, cost=1, cd=0, knock=0.0):
    return move("guard", cost, cd, None, None, None, reflect=reflect, absorb=absorb, knockback=knock)

def gust(power=1.6, dmg=2, rng=7, cost=4, cd=30, color=0xE6F7FF, effects=None, element=None):
    return move("gust", cost, cd, element, effects, color, power=power, damage=dmg, range=rng)

def blast(mode="projectile", dmg=6, color=0xFFFFFF, cost=8, cd=30, element=None, radius=0, speed=1.6, rng=24,
          count=1, fire=0, effects=None, size=0.5):
    return move("blast", cost, cd, mode + (":" + element if element else ""), effects, color, damage=dmg, radius=radius,
                speed=speed, range=rng, count=count, fire=fire, size=size)

def seal(threshold=0.3, rng=5, cost=10, cd=100, effects=None, color=0xC9A227):
    return move("seal", cost, cd, None, effects, color, threshold=threshold, range=rng)

def summon(kind="wood", hp=20, dmg=4, style="melee", life=600, count=1, cost=15, cd=200, upkeep=0.05, speed=0.3):
    return move("summon", cost, cd, kind + ":" + style, None, None, health=hp, damage=dmg, lifetime=life, count=count,
                upkeep=upkeep, speed=speed)

def sand(dmg=5, rng=10, color=0xD9C27E, cost=8, cd=40, kind="sand"):
    return move("sand", cost, cd, kind, None, color, damage=dmg, range=rng)

def mark(cd=10):
    return move("mark", 0, cd, None, None, None, damage=4, speed=2.0)

def recall(cost=5, cd=20):
    return move("recall", cost, cd)

def anchor(mode="set", cost=0, cd=20):
    return move("anchor", cost, cd, mode)

def vortex(radius=6, dmg=2, dur=60, cost=20, cd=200):
    return move("vortex", cost, cd, None, None, 0x2A0A3A, radius=radius, damage=dmg, duration=dur)

def glide(boost=1.2, dur=100, cost=4, cd=40, variant="wings", effects=None):
    return move("glide", cost, cd, variant, effects, None, boost=boost, duration=dur)

def sonic(effects, rng=8, dmg=0, cost=4, cd=60, allies=0, color=0xD0B0FF):
    return move("sonic", cost, cd, "allies" if allies else "enemies", effects, color, range=rng, damage=dmg)

def scan(radius=24, dur=200, cost=2, cd=60, effects=None):
    return move("scan", cost, cd, None, effects, None, radius=radius, duration=dur)

def buff(effects, cost=10, cd=200):
    return move("buff", cost, cd, None, effects)

def wall(width=5, height=3, dur=200, cost=6, cd=100, kind="crystal"):
    return move("wall", cost, cd, kind, None, None, width=width, height=height, duration=dur)

def slam(dmg=7, radius=3, cost=3, cd=40, explode=0, drill=0, knock=1.0, effects=None, color=0xA0A0A0):
    return move("slam", cost, cd, None, effects, color, damage=dmg, radius=radius, explode=explode, drill=drill, knockback=knock)

def infuse(cost=5):
    return move("infuse", cost, 10)

# ---------------------------------------------------------------- sprites
def spr(shape, *colors, **kw):
    d = {"shape": shape, "colors": ["#%06X" % c for c in colors]}
    d.update(kw)
    return d
