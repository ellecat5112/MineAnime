"""
NarutoRPG ninja-tool catalog: one entry per page of Narutopedia's Category:Tools (crawled 2026-11-25).
Run:  python3 tools/catalog/build.py   -> writes ninja_tools.json + 32x32 sprites into narutorpg resources.
Controls convention: LEFT = melee attack, RIGHT = primary move, SNEAK+RIGHT = secondary move.
"""
from dsl import *

# palette
STEEL, DARK, BLACK, SILVER = 0x8A9099, 0x3A3D44, 0x24262B, 0xC3CAD3
WOOD, DWOOD, BAMBOO = 0x8B5A2B, 0x5A3A1E, 0xC8B46A
GOLD, BRONZE, COPPER = 0xD4AF37, 0xA0662E, 0xB87333
PAPER, INK, BANDAGE = 0xEDE3C8, 0x8E1B1B, 0xD8CFB8
RED, CRIMSON, ORANGE, YELLOW = 0xC62828, 0x8B1A1A, 0xF08C2E, 0xF2D13A
BLUE, NAVY, CYAN, ICE = 0x2E5AAC, 0x1F2F5C, 0x4FC3F7, 0xB3E5FC
GREEN, DGREEN, LIME, TEAL = 0x3E8E41, 0x2E5D32, 0x8BC34A, 0x26A69A
PURPLE, VIOLET, PINK, MAGENTA = 0x7E57C2, 0x512DA8, 0xE57399, 0xC2185B
WHITE, GREY, BROWN, TAN, STONE = 0xF0F0F0, 0x9E9E9E, 0x6D4C41, 0xC9A66B, 0x7D7F7D
FLESH, SAND_C, IRON_SAND, GOLD_DUST = 0xE0B48A, 0xD9C27E, 0x2B2B30, 0xE8C547

E = eff
T_ = []
def T(title, sprite, attack, primary, secondary=None, stack=1, rarity="common", armor=None, worn=None, desc="",
      durability=0, id=None):
    T_.append(dict(wiki=title, id=id or slug(title), sprite=sprite, attack=attack, primary=primary, secondary=secondary,
                   max_stack=stack, rarity=rarity, armor=armor, worn=worn or [], description=desc, durability=durability))

def armor(slot, defense=2, color=0x3A5F3A):
    return {"slot": slot, "defense": defense, "color": color}

POISON_FX = [E("poison", 120, 1)]
SMOKE_FX = [E("blindness", 80), E("slowness", 80)]
PARALYZE_FX = [E("slowness", 80, 3), E("mining_fatigue", 80, 2)]
SLEEP_FX = [E("slowness", 120, 4), E("blindness", 100), E("weakness", 120, 1)]

# ====================================================================== A
T("Absorbing Eye", spr("eye", CYAN, WHITE, DARK), melee(1, -1.0), guard(absorb=1, cost=0),
  blast("beam", 6, CYAN, cost=8, cd=40), rarity="epic", desc="Absorbs ninjutsu while guarding; fires the stored energy as an anti-regeneration beam.")
T("Absorbing Hand", spr("hand", SILVER, CYAN, DARK), melee(4, -2.0, 0, hit("drain", 3)), guard(absorb=1, cost=0),
  slam(5, 2, cost=4), rarity="rare", desc="Prosthetic that absorbs incoming ninjutsu; punches drain chakra.")
T("Adamantine Staff", spr("bo", BLACK, GOLD), melee(6, -2.6, 1.5, hit("knockback", 1.5)), slam(8, 3, cost=6),
  guard(cost=1), rarity="epic", desc="Monkey King's staff: crushing blows, ground slam, and a solid guard.")
T("Altering Terrain Diagram Scroll", spr("map_scroll", PAPER, BROWN, GREEN), melee(1, -1), wall(5, 3, 300, cost=10, kind="stone"),
  trap(4, 4, 400, [E("slowness", 60, 2)]), rarity="rare", desc="Draw stone walls, or hide traps in the terrain.")
T("Ama no Hoko", spr("tower", BLACK, RED, DARK), melee(2, -3.0), blast("beam", 12, RED, cost=30, cd=200, rng=40),
  scan(48, 200, cost=10), rarity="epic", desc="A shard of the ancient tower: channels a devastating red beam.")
T("Antidote", spr("vial", GREEN, WHITE, GLASS:=0xBFE6D0), FIST, consume(heal=2, cleanse=1, drink=1, time=24),
  consume(cleanse=1, aoe=4, drink=1, time=24), stack=16, desc="Cures poisons. Sneak: splash nearby allies.")
T("Araya (puppet)", spr("puppet", TAN, PURPLE, DARK), melee(2, -2), summon("wood", 24, 4, "melee", cost=12),
  guard(cost=1), rarity="uncommon", desc="Body double puppet that fights in your place.")
T("Arm-mounted Drill", spr("drill_arm", STEEL, BRONZE, DARK), melee(7, -2.6, 0, hit("pierce", 3)), slam(8, 1.5, cost=3, drill=1),
  dash(7, 5, cost=4), rarity="rare", desc="Tenrō drill: armour-piercing strikes; drills through rock.")
T("Artificial Sharingan", spr("eye", RED, BLACK, WHITE), melee(1, -1), scan(32, 300, cost=4),
  buff([E("speed", 200, 1), E("haste", 200, 1)], cost=12), rarity="epic", desc="Cybernetic Sharingan: perfect vision and predictive reflexes.")
T("Autonomous Puppet", spr("puppet", WOOD, CRIMSON, DARK), melee(2, -2), summon("wood", 28, 5, "melee", life=900, upkeep=0.0, cost=20),
  None, rarity="rare", desc="Runs on stored chakra: no upkeep once summoned.")
T("Axe", spr("axe", STEEL, WOOD), melee(8, -3.0, 0, hit("shield_break")), slam(7, 2, cost=0, cd=30),
  throw(7, 1.2, gravity=0.05, cd=30), desc="Heavy chop that breaks shields; can be thrown.")
# ====================================================================== B
T("Barrier Puppets", spr("puppet", STEEL, CYAN, DARK), melee(2, -2), wall(5, 3, 300, cost=10, kind="barrier"),
  summon("metal", 30, 2, "guard", cost=15), rarity="rare", desc="Shirogane guardians: erect a barrier, or summon a guard puppet.")
T("Barrier Tag", spr("tag", PAPER, CYAN), FIST, ward(200), throw(1, 1.2, impact="wall", color=CYAN, radius=2, drop=0, cd=40),
  stack=32, desc="Place for a temporary wall. Three or more placed in sequence link into a lasting Barrier Gate.")
T("Bashōsen", spr("fan_open", GOLD, RED, DWOOD), melee(5, -2.6), blast("projectile", 9, ORANGE, cost=12, element="random", radius=3, fire=80),
  gust(2.4, 4, 10, cost=8), rarity="epic", desc="Treasured fan producing all five natures. Sneak: hurricane gust.")
T("Bell", spr("bell", GOLD, BRONZE), melee(1, -1), sonic([E("nausea", 100), E("slowness", 60)], 8, cost=3),
  throw(2, 1.4, impact="sonic", effects=[E("nausea", 80)], drop=1), stack=16, desc="Ringing genjutsu: disorients listeners.")
T("Bell (charm)", spr("bell", PINK, GOLD), melee(1, -1), blast("beam", 6, PINK, cost=8),
  buff([E("resistance", 200, 1), E("slow_falling", 200)], cost=12), rarity="rare", desc="Mystic charm: severing pink rays, or an angelic barrier.")
T("Benihisago", spr("big_gourd", BROWN, RED), melee(3, -3.0), seal(0.35, 6, cost=12),
  guard(cost=1), rarity="epic", desc="Treasured gourd that seals the spirit of anyone it records.")
T("Binding Cloth", spr("cloth", BANDAGE, INK), melee(1, -1), chain(1, 12, cd=40, effects=PARALYZE_FX, mode="bind"),
  seal(0.25, 4, cost=8), stack=8, desc="Suna cloth: binds targets; with a seal, imprisons them.")
T("Bingo Book", spr("book", BLACK, RED), melee(1, -1), scan(32, 200, cost=0),
  scan(64, 400, cost=4, effects=[E("glowing", 400)]), desc="Read the target's profile; sneak to track every hostile nearby.")
T("Blood Increasing Pill", spr("pill", RED, CRIMSON), FIST, consume(heal=8, effects=[E("regeneration", 100, 1)], time=16),
  None, stack=16, desc="Rapidly restores lost blood.")
T("Blowpipe", spr("blowpipe", BAMBOO, DWOOD), melee(1, -1), throw(4, 2.6, gravity=0.0, impact="dart", effects=[E("poison", 80)], consume=0, cd=15),
  throw(1, 2.6, gravity=0.0, impact="dart", effects=SLEEP_FX, consume=0, cd=60), desc="Silent darts: poison, or sneak for tranquiliser.")
T("Blue Fire Powder", spr("pouch", NAVY, CYAN), FIST, bomb("explode_water", color=CYAN, radius=3, dmg=6),
  None, stack=16, desc="Explodes violently on contact with water (and anything wet).")
T("Book of Gelel", spr("book", BROWN, GOLD), melee(1, -1), scan(24, 300, cost=0), buff([E("regeneration", 200, 0)], cost=8),
  rarity="rare", desc="Secrets of the Stone of Gelel.")
T("Bow & Arrow", spr("bow", WOOD, BANDAGE), melee(1, -2), throw(7, 3.0, gravity=0.05, impact="arrow", consume=0, cd=20),
  throw(4, 3.0, count=3, spread=8, gravity=0.05, impact="arrow", consume=0, cd=50), id="bow_and_arrow", desc="Precise shot; sneak for a triple volley.")
T("Box of Ultimate Bliss", spr("box_face", DWOOD, GOLD, RED), melee(2, -3), buff([E("strength", 300, 1), E("speed", 300, 1), E("regeneration", 300)], cost=20, cd=600),
  summon("demon", 40, 8, "melee", life=300, cost=30, cd=1200), rarity="epic", desc="Grants a wish of power... or releases what lurks inside.")
T("Bubble-Utilising Pipe", spr("pipe", GOLD, TEAL), melee(1, -1), throw(3, 0.6, count=5, spread=20, gravity=-0.005, impact="bubble", color=ICE, consume=0, cd=40, radius=2),
  guard(cost=1), rarity="rare", desc="Soap-bubble ninjutsu: drifting explosive bubbles; sneak for a bubble guard.")
T("Bō", spr("bo", WOOD, STEEL), melee(5, -2.2, 1.5, hit("knockback", 1.2)), slam(6, 2.5, cost=0, cd=30), guard(cost=0), id="bo", desc="Long staff: reach, sweeps and parries.")
# ====================================================================== C
T("Chain Wind Staff", spr("chain_staff", STEEL, DARK), melee(5, -2.4, 1.0), chain(4, 10, cd=25),
  slam(6, 3.5, cost=2, knock=1.6), desc="Weighted chains on both ends: lash out or spin a wide sweep.")
T("Chakra Armour", spr("vest", BLACK, PURPLE), FIST, consume(chakra=0, time=1, consume_item=0, cd=1), guard(absorb=1, cost=0),
  armor=armor("chest", 7, 0x2B2140), worn=[E("resistance", 60, 0)], rarity="epic", desc="Blocks ninjutsu and genjutsu; sneak to absorb attacks.")
T("Chakra Blade", spr("kunai_long", CYAN, BLACK, SILVER), melee(5, -2.0), charge(10, 6), infuse(), id="chakra_blade",
  desc="(existing item) Flows chakra nature into the blade.")
T("Chakra Cannon", spr("cannon", STEEL, CYAN, DARK), melee(2, -3.2), blast("beam", 14, CYAN, cost=35, cd=200, rng=48),
  blast("projectile", 8, CYAN, cost=15, cd=80, radius=3, size=1.2), rarity="epic", desc="Long-range chakra beam cannon.")
T("Chakra Disruption Device", spr("device", STEEL, PURPLE, CYAN), melee(2, -2, 0, hit("drain", 4)), seal(0.4, 5, cost=6, effects=[E("weakness", 200, 2), E("slowness", 200, 1)]),
  guard(absorb=1), rarity="rare", desc="Clamps onto a target and drains its chakra.")
T("Chakra Enhanced Triple-Bladed Claw", spr("claw3", SILVER, CYAN, DARK), melee(6, -1.8, 0, hit("bleed", 1)), charge(9, 5, element="wind"),
  infuse(), rarity="rare", desc="Tekagi-shuko that extends chakra blades.")
T("Chakra Transmission Communication Device", spr("device", DARK, LIME, STEEL), FIST, scan(64, 300, cost=0),
  buff([E("night_vision", 400)], cost=2), desc="Shares the location of every ally and foe.")
T("Chakra-Suppressing Seal", spr("tag", PAPER, RED), FIST, seal(1.0, 4, cost=6, effects=[E("weakness", 400, 2), E("slowness", 200, 1)]),
  consume(cleanse=1, time=10), stack=16, desc="Suppresses a target's chakra; sneak to calm yourself.")
T("Chikamatsu Collection", spr("big_scroll", PAPER, RED, WOOD), melee(1, -1), summon("crimson", 18, 4, "melee", count=4, life=500, cost=30, cd=600),
  None, rarity="epic", desc="Ten legendary puppets: summons a squad.")
T("Chilli Pepper Bomb", spr("bomb", RED, GREEN), FIST, bomb("cloud", [E("nausea", 140), E("blindness", 40)], RED, 3),
  consume(cleanse=1, effects=[E("fire_resistance", 200)], time=16), stack=16, desc="Fiery cloud that also snaps people out of genjutsu.")
T("Claw Club", spr("claw_club", BROWN, GREEN, DARK), melee(7, -2.8, 1.0, hit("bleed", 1)), slam(8, 3, cost=3),
  guard(reflect=1, cost=2), rarity="rare", desc="Yagura's hooked club: crushing blows and a reflecting guard.")
T("Cleaver Sword", spr("cleaver", STEEL, DARK), melee(8, -2.8, 0.5), charge(12, 7, element="lightning", color=0xCDE7FF),
  slam(8, 2.5, cost=2), rarity="rare", desc="Darui's folding cleaver, conducts Storm Release.")
T("Club", spr("club", WOOD, DWOOD), melee(6, -3.0, 0, hit("knockback", 1)), slam(6, 2, cost=0, cd=30), None, desc="Simple bludgeon.")
T("Concealed Mouth Ember", spr("ember", ORANGE, RED), FIST, throw(4, 1.6, impact="fire", color=ORANGE, radius=2, drop=0, cd=10),
  None, stack=16, desc="Spit a burning ember.")
T("Conch Shell Mace", spr("mace", PINK, WHITE, WOOD), melee(8, -3.0, 0, hit("knockback", 1.2)), slam(9, 3, cost=3),
  None, rarity="uncommon", desc="Kotetsu's spiked conch mace.")
T("Crossbow", spr("crossbow", WOOD, STEEL), melee(1, -2), throw(9, 3.5, gravity=0.03, impact="arrow", consume=0, cd=30),
  throw(5, 3.5, impact="arrow", effects=POISON_FX, consume=0, cd=40), desc="Powerful bolt; sneak for poisoned bolt.")
T("Curry of Life", spr("bowl", WHITE, ORANGE, BROWN), FIST, consume(heal=10, food=8, chakra=30, effects=[E("strength", 400, 1), E("nausea", 60)], time=48),
  None, stack=4, desc="Ferociously spicy curry that revives the body.")
T("Curse Mask", spr("oni_mask", WHITE, RED), FIST, buff([E("strength", 300, 2), E("wither", 100, 0)], cost=20, cd=600),
  None, armor=armor("head", 2, 0xEDEDED), rarity="epic", desc="Power at a price: grants great strength while it devours you.")
T("Cursed Seal Device", spr("device", BLACK, PURPLE, RED), melee(2, -2), buff([E("strength", 400, 1), E("speed", 400, 1), E("hunger", 400, 1)], cost=10, cd=400),
  None, rarity="rare", desc="Juinjutsu device: borrowed power, borrowed time.")
# ====================================================================== D
T("Dakō", spr("cross_spear", STEEL, DARK), melee(6, -2.6, 2.5), chain(6, 14, cd=30), dash(6, 7), rarity="rare",
  desc="Iō's extending cross-spear: launch the head, or lunge.")
T("Demon Shinki", spr("seed", DGREEN, CRIMSON), FIST, vortex(7, 1, 80, cost=20), consume(chakra=60, effects=[E("hunger", 200, 2)], time=24),
  rarity="epic", desc="A seed that grows into a chakra-sucking demon tree.")
T("Demonic Flute", spr("flute", BAMBOO, RED), melee(1, -1), sonic([E("nausea", 160), E("slowness", 100, 1), E("blindness", 40)], 12, cost=6),
  summon("demon", 30, 6, "melee", life=400, count=2, cost=25, cd=600), rarity="rare", desc="Genjutsu melody; sneak to summon Doki.")
T("Double Bladed Bracers", spr("bladed_bracers", STEEL, DARK), melee(5, -1.6, 0, hit("bleed", 1)), guard(cost=1, knock=0.8),
  dash(5, 4, cost=2), desc="Bladed bracers: strike and parry.")
T("Double-Bladed Scythe", spr("scythe2", CRIMSON, BLACK), melee(7, -2.6, 1.0, hit("bleed", 1)), charge(9, 4, element="blood", color=CRIMSON),
  None, rarity="rare", desc="Ritual scythe made to draw blood.")
T("Dragon Blade", spr("straight_sword", GOLD, CRIMSON, RED), melee(8, -2.2), charge(12, 7, color=GOLD),
  buff([E("strength", 200, 1), E("speed", 200)], cost=15), rarity="epic", desc="A blade that lets its wielder perform any technique.")
T("Dōjutsu Glasses", spr("glasses", DARK, PURPLE), FIST, scan(40, 300, cost=4), buff([E("night_vision", 600)], cost=4),
  armor=armor("head", 1, 0x2A2A2A), rarity="rare", desc="Borrow a dōjutsu: see through everything.")
# ====================================================================== E
T("Elemental Puppets", spr("puppet", RED, BLUE, DARK), melee(2, -2), summon("crimson", 20, 4, "flame", cost=15),
  summon("wood", 20, 4, "water", cost=15), rarity="rare", desc="Red puppet breathes fire; sneak for the blue water puppet.")
T("Exploding Sphere", spr("spiked_sphere", STEEL, SILVER, RED), FIST, bomb("explode", color=ORANGE, radius=3, dmg=8),
  None, stack=8, desc="Kunai-studded sphere with a hidden explosive tag.")
T("Explosive Tag", spr("tag", PAPER, RED), FIST, throw(1, 1.4, impact="explode", color=ORANGE, radius=3, fuse=40, drop=0, cd=10),
  ward(0, cd=10), stack=32, desc="Throw for a timed blast; sneak-place as a proximity trap. Thrown kunai carry one from your off-hand.")
T("Explosive Tag Ball", spr("bomb", PAPER, RED), FIST, bomb("explode", color=ORANGE, radius=4, dmg=10, speed=1.4),
  None, stack=8, desc="Cannonball of bundled explosive tags.")
T("Eye Medicine Plant", spr("herb", GREEN, LIME), FIST, consume(heal=2, cleanse=1, effects=[E("night_vision", 1200)], time=24),
  None, stack=16, desc="Rare herb that soothes eye strain.")
T("Eye Scope", spr("goggles", DARK, CYAN), FIST, scan(64, 200, cost=0), buff([E("night_vision", 400)], cost=0),
  armor=armor("head", 1, 0x3C3C3C), desc="Deidara's scope: spot and range distant targets.")
# ====================================================================== F
T("Fire Gatling", spr("gatling", STEEL, ORANGE, DARK), melee(2, -3), blast("projectile", 4, ORANGE, cost=10, cd=40, count=6, fire=60, speed=2.0, size=0.35),
  None, rarity="rare", desc="Rotating barrels unleash stored Fire Release.")
T("Fire Sword", spr("straight_sword", ORANGE, RED, DWOOD), melee(7, -2.4, 0, hit("fire", 1, 80)), blast("cone", 6, ORANGE, cost=8, rng=6, fire=80),
  blast("beam", 4, YELLOW, cost=4, cd=60), rarity="epic", desc="Emits and wields vast flames; sneak to send a light signal beam.")
T("First Hokage's Necklace", spr("necklace", GOLD, GREEN), FIST, buff([E("regeneration", 400, 1), E("absorption", 400, 1)], cost=15, cd=600),
  None, rarity="epic", desc="Crystal of Hashirama's chakra: vitality.")
T("Fishing rod", spr("fishing_rod", WOOD, SILVER), melee(1, -1), chain(3, 18, cd=25, effects=[E("weakness", 200, 1)], mode="hook"),
  None, id="fishing_rod", desc="Urashiki's rod: hooks a target and reels out their chakra.")
T("Five Mirrors of Hope", spr("mirror", SILVER, BLACK, GOLD), FIST, guard(reflect=1, cost=1), wall(5, 3, 200, cost=8, kind="mirror"),
  rarity="epic", desc="Tomoe mirrors that reflect attacks.")
T("Flail", spr("flail", STEEL, WOOD), melee(7, -3.0, 1.0, hit("shield_break")), slam(7, 3, cost=0, cd=40), None, desc="Spiked flail.")
T("Flak Jacket", spr("vest", DGREEN, GREEN), FIST, None, consume(heal=0, chakra=0, effects=[E("resistance", 100)], time=8, consume_item=0, cd=200),
  armor=armor("chest", 5, 0x556B2F), desc="Standard protective vest; sneak-use to brace for impact.")
T("Flamethrower", spr("gun", CRIMSON, ORANGE, DARK), melee(2, -2), blast("cone", 5, ORANGE, cost=6, cd=20, rng=7, fire=100),
  None, rarity="rare", desc="Puppet pipes that spew rock-melting fire.")
T("Flash Bomb", spr("bomb", WHITE, YELLOW), FIST, bomb("flash", [E("blindness", 100), E("glowing", 100)], YELLOW, 4),
  None, stack=16, desc="Blinding flash for a quick escape.")
T("Flying Destruction Machine", spr("wing", STEEL, RED, DARK), melee(2, -3), glide(1.4, 160, cost=8, variant="bomber"),
  blast("beam", 10, RED, cost=25, cd=200), rarity="epic", desc="Flying bomber: glide and drop bombs; sneak for the explosive beam.")
T("Flying Thunder God Kunai", spr("tri_kunai", SILVER, BLACK, INK), melee(5, -1.6), mark(), recall(), stack=16, rarity="rare",
  desc="Throw to place a seal; sneak to warp to it instantly.")
T("Forehead Protector", spr("headband", SILVER, NAVY), FIST, None, buff([E("resistance", 100, 0)], cost=2, cd=200),
  armor=armor("head", 2, 0x1F2F5C), desc="Shinobi headband: protects the forehead, shows your village.")
T("Fūma Shuriken", spr("fuma", STEEL, DARK), melee(4, -2.4), throw(9, 1.4, gravity=0.0, returns=1, pierce=3, consume=0, cd=40),
  throw(6, 1.4, count=2, spread=6, gravity=0.0, returns=1, consume=0, cd=60), rarity="uncommon", desc="Huge shuriken that boomerangs back.")
# ====================================================================== G
T("Gama-chan", spr("wallet_toad", GREEN, ORANGE), FIST, buff([E("luck", 2400)], cost=0, cd=1200),
  None, desc="Naruto's beloved toad wallet. Fortune smiles on the thrifty.")
T("Garian Sword", spr("tri_sword", SILVER, DARK), melee(6, -2.4, 1.0), chain(6, 14, cd=30, mode="pull"),
  None, rarity="rare", desc="Wire-guided three-pronged blade that can be shot at foes.")
T("Gekkōran Flower", spr("flower", WHITE, YELLOW, GREEN), FIST, consume(heal=12, cleanse=1, effects=[E("regeneration", 200, 1)], time=24),
  None, stack=8, rarity="rare", desc="Blooms once a decade; saves lives.")
T("Genjutsu Nullifying Perfume", spr("perfume", PINK, GOLD), FIST, consume(cleanse=1, effects=[E("night_vision", 600)], time=12, drink=1),
  consume(cleanse=1, aoe=6, time=12, drink=1), stack=8, desc="Breaks genjutsu through smell; sneak to spray allies.")
T("Genjutsu Pill", spr("pill", PURPLE, VIOLET), FIST, throw(1, 1.4, impact="cloud", effects=PARALYZE_FX + [E("nausea", 120)], color=PURPLE, radius=3, drop=0),
  None, stack=16, desc="Paralysing hallucinogen.")
T("Giant Folding Fan", spr("fan_open", WHITE, PURPLE, DARK), melee(6, -2.6, 1.0), gust(2.2, 4, 10, cost=6),
  guard(reflect=1, cost=1), rarity="rare", desc="Temari's war fan: cutting winds, or a reflecting guard.")
T("Gold Dust", spr("gold_dust", GOLD_DUST, GOLD), FIST, sand(7, 12, GOLD_DUST, cost=10, kind="gold"),
  guard(cost=1), rarity="epic", desc="Magnetic gold dust that buries foes.")
T("Gunbai", spr("gunbai", DWOOD, RED, WHITE), melee(6, -2.4, 0.5, hit("knockback", 1.4)), guard(reflect=1, cost=1, knock=1.0),
  gust(2.0, 5, 8, cost=8, element="fire", color=ORANGE), rarity="epic", desc="Uchiha war fan: reflects everything; sneak for a fire-wind blast.")
# ====================================================================== H
T("Handheld Kunai Launcher", spr("launcher", STEEL, DARK), melee(2, -2), throw(4, 2.4, count=5, spread=5, gravity=0.01, consume=0, cd=40, drop=0),
  None, desc="Machine-gun barrage of kunai.")
T("Hashirama's Artificial Body", spr("seed", FLESH, GREEN), FIST, consume(heal=20, effects=[E("regeneration", 600, 2), E("absorption", 600, 2)], time=40),
  None, rarity="epic", desc="Cells of the First Hokage: tremendous vitality.")
T("Hero Water", spr("bottle", CYAN, GOLD), FIST, consume(chakra=100, effects=[E("strength", 400, 2), E("wither", 300, 0)], drink=1, time=32),
  None, stack=4, rarity="epic", desc="Multiplies chakra greatly, at the cost of life force.")
T("Hexagonal Crystal", spr("hex_crystal", PURPLE, VIOLET), FIST, blast("beam", 4, PURPLE, cost=4, cd=60), scan(32, 200),
  rarity="rare", desc="Koyuki's crystal key; projects light.")
T("Hidden Kunai Mechanism", spr("bracers", DARK, STEEL), melee(5, -1.6), throw(5, 2.2, consume=0, drop=0, cd=12),
  chain(4, 10, cd=20), desc="Spring-loaded sleeve kunai: snap them out or launch on a wire.")
T("Hiramekarei", spr("broad_sword", ICE, NAVY, BANDAGE), melee(8, -2.6, 1.0), charge(14, 8, color=ICE),
  slam(9, 3, cost=6, color=ICE), rarity="epic", desc="Twin-hilted blade that stores and releases chakra.")
T("Hiruko", spr("puppet_scorpion", BROWN, DARK, CRIMSON), melee(3, -2), chain(6, 10, cd=30, effects=POISON_FX, mode="pull"),
  throw(3, 2.0, count=5, spread=10, effects=POISON_FX, impact="dart", consume=0, cd=60), armor=armor("chest", 8, 0x4E342E), rarity="epic",
  desc="Puppet armour with a poison tail and senbon launcher.")
T("History of Warring States Seals", spr("scroll", PAPER, BROWN), FIST, scan(32, 200, cost=0), None, desc="Records of every seal.")
T("Human Puppet", spr("puppet", FLESH, CRIMSON, DARK), melee(2, -2), summon("crimson", 30, 6, "melee", cost=20), None, rarity="rare",
  desc="Sasori's forbidden art.")
T("Hundred Puppet Army", spr("big_scroll", PAPER, CRIMSON, WOOD), melee(1, -1), summon("crimson", 12, 3, "melee", count=8, life=400, cost=50, cd=1200),
  None, rarity="epic", desc="Performance of a Hundred Puppets.")
T("Hyūga Clan Secret Ointment", spr("jar", WHITE, PURPLE), FIST, consume(heal=8, effects=[E("regeneration", 120, 1)], time=24),
  consume(heal=6, aoe=4, time=24), stack=8, desc="Healing cream; sneak to share with allies.")
# ====================================================================== I
T("Ice Bomb", spr("crystal", ICE, CYAN), FIST, bomb("ice", [E("slowness", 100, 3)], ICE, 3, dmg=6),
  None, stack=8, desc="Releases a torrent of ice spikes.")
T("Infinite Armour", spr("tiger_plate", GOLD, ORANGE), FIST, None, guard(cost=0),
  armor=armor("chest", 8, 0xC8A03C), worn=[E("resistance", 60, 1)], rarity="epic", desc="Tiger chest plate that absorbs nearly all damage.")
T("Injection Shot", spr("needles", SILVER, CYAN), FIST, throw(1, 2.6, gravity=0.0, impact="dart", effects=SLEEP_FX, cd=10),
  None, stack=16, desc="Knock-out darts.")
T("Injection Shot Sniper", spr("sniper", WOOD, STEEL), melee(2, -2), throw(2, 4.0, gravity=0.0, impact="dart", effects=SLEEP_FX, consume=0, cd=40),
  None, rarity="uncommon", desc="Long-range tranquiliser rifle.")
T("Insect Queen Bee Drone Puppet", spr("bee", YELLOW, BLACK), melee(1, -1), summon("bee", 10, 3, "flying", count=3, life=500, cost=15),
  None, rarity="rare", desc="Buzzing drone puppets.")
T("Iron Sand", spr("iron_sand", IRON_SAND, PURPLE), FIST, sand(8, 12, IRON_SAND, cost=12, kind="iron"),
  guard(cost=1), rarity="epic", desc="The Third Kazekage's magnetised iron sand.")
# ====================================================================== J
T("Jidanda", spr("jidanda", STEEL, DARK, RED), melee(8, -3.2, 1.0), slam(10, 4, cost=4, explode=1),
  chain(8, 12, cd=40), rarity="epic", desc="Spiked explosive iron ball on a chain.")
T("Jofuku Flower", spr("flower", PINK, WHITE, GREEN), FIST, consume(heal=6, effects=[E("regeneration", 200)], time=24),
  None, stack=8, desc="Medicinal flower with peculiar traits.")
T("Jutsu Absorption Arm", spr("hand", WHITE, CYAN, STEEL), melee(3, -2), guard(absorb=1, cost=0),
  blast("projectile", 6, CYAN, cost=10, radius=2), rarity="rare", desc="Creates ninjutsu-absorbing spheres.")
T("Jō", spr("bo", BAMBOO, DWOOD), melee(4, -2.0, 1.0), guard(cost=0), dash(4, 4, cost=0), id="jo", desc="Short staff.")
T("Jōhyō", spr("rope", BANDAGE, STEEL), melee(3, -2, 1.5), chain(5, 14, cd=20), throw(5, 2.2, consume=0, returns=1, cd=20),
  desc="Rope dart: whip it out or reel targets in.")
# ====================================================================== K
T("Kabutowari", spr("hammer_axe", STEEL, DARK, GOLD), melee(9, -3.2, 0.5, hit("shield_break")), slam(10, 3, cost=4, drill=1),
  None, rarity="epic", desc="Axe and hammer that splits any defence.")
T("Kama", spr("kama", STEEL, WOOD), melee(5, -1.8, 0, hit("bleed", 1)), throw(5, 1.6, returns=1, consume=0, cd=30), None, desc="Sickle.")
T("Karasu", spr("puppet_crow", BROWN, BLACK, PURPLE), melee(2, -2), summon("crow", 30, 6, "poison", cost=15),
  guard(cost=1), rarity="epic", desc="Four-armed puppet with poisoned blades.")
T("Katar", spr("katar", STEEL, DARK), melee(6, -1.6, 0, hit("pierce", 2)), dash(5, 4, cost=0, cd=30), None, desc="Punch dagger.")
T("Kiba (swords)", spr("jagged_sword", ICE, DARK, CYAN), melee(8, -2.0, 0, hit("lightning", 4)), dash(9, 8, cost=8, element="lightning", color=0xCDE7FF),
  blast("lightning", 10, 0xCDE7FF, cost=15, cd=100), id="kiba_swords", rarity="epic", desc="Twin lightning blades.")
T("Kiseki", spr("crystal", CYAN, WHITE), FIST, scan(32, 400, cost=2), buff([E("night_vision", 600), E("luck", 600)], cost=4),
  rarity="rare", desc="Stone of memories.")
T("Knight Sword", spr("straight_sword", SILVER, GOLD, BLUE), melee(7, -2.4, 1.5), dash(7, 8, cost=4), charge(10, 7, color=SILVER),
  rarity="rare", desc="Temujin's extending sword.")
T("Kohaku no Jōhei", spr("pot", BROWN, GOLD), melee(2, -3), seal(0.35, 8, cost=12), None, rarity="epic",
  desc="The pot seals anyone who answers its call.")
T("Kokutō", spr("katana", BLACK, DARK, BANDAGE), melee(7, -2.4, 0, hit("poison", 1, 100)), dash(7, 6, cost=3), None, rarity="rare",
  desc="Raidō's poisoned black katana.")
T("Komushi (puppet)", spr("puppet", FLESH, GREEN, DARK), melee(2, -2), summon("crimson", 24, 5, "melee", cost=15), None, rarity="rare",
  desc="Sasori's first human puppet.")
T("Konoha Chakra Blade", spr("kunai_long", SILVER, BLACK, CYAN), melee(6, -2.0), charge(9, 5), throw(6, 2.0, cd=20), rarity="uncommon",
  desc="Elongated chakra-conducting kunai.")
T("Kotarō", spr("seed", GREEN, PURPLE), FIST, consume(chakra=40, effects=[E("strength", 300, 1)], time=24), summon("demon", 30, 6, "melee", cost=20),
  rarity="rare", desc="Saigenzai: grows a controlling creature.")
T("Kubikiribōchō", spr("broad_sword", SILVER, DARK, BANDAGE), melee(10, -3.0, 1.0, hit("bleed", 2)), charge(14, 7),
  consume(heal=6, time=1, consume_item=0, cd=400), rarity="epic", desc="The Executioner's Blade. It reforges itself from blood (sneak: heal).")
T("Kumogakure Swords", spr("jagged_sword", SILVER, DARK, YELLOW), melee(7, -2.2, 0, hit("lightning", 2)), charge(10, 6, element="lightning", color=0xCDE7FF),
  None, rarity="uncommon", desc="Killer B's style blades.")
T("Kunai", spr("kunai", DARK, BANDAGE, SILVER), melee(4, -1.6), throw(5, 1.8, cd=8), infuse(), stack=16,
  desc="Stab, or throw. Throwing with an Explosive Tag in your off-hand makes an explosive kunai.")
T("Kunai Blade", spr("kunai_long", STEEL, BANDAGE, DARK), melee(6, -2.0), throw(6, 1.8, returns=1, consume=0, cd=20), None,
  desc="Oversized kunai blade.")
T("Kuroari", spr("puppet_ant", BLACK, RED, DARK), melee(2, -2), summon("ant", 36, 5, "trap", cost=15), seal(0.4, 5, cost=10, effects=PARALYZE_FX),
  rarity="epic", desc="Trap puppet: captures targets inside its body.")
T("Kurosawa", spr("katana", SILVER, DARK, BANDAGE), melee(8, -2.2), dash(8, 7, cost=3), guard(cost=1), rarity="rare",
  desc="Mifune's sword: iai strike and samurai parry.")
T("Kusari", spr("chain", STEEL, DARK), melee(3, -2, 2.0, hit("knockback", 1)), chain(3, 12, cd=20), None, desc="Weighted chain.")
T("Kusarigama", spr("kusarigama", STEEL, WOOD, DARK), melee(5, -2.0, 1.0, hit("bleed", 1)), chain(5, 14, cd=25),
  throw(5, 1.6, returns=1, consume=0, cd=30), desc="Sickle and chain: lash, entangle, or throw.")
T("Kuwa", spr("hoe", STEEL, WOOD), melee(5, -2.4), slam(5, 2, cost=0, cd=30, drill=1), guard(cost=0), desc="Farming hoe turned weapon.")
T("Kōkinjō", spr("rope", GOLD, RED), melee(1, -1, 2.0), chain(2, 10, mode="bind", effects=PARALYZE_FX), seal(0.35, 10, cost=12),
  rarity="epic", desc="Golden rope that draws out the soul.")
# ====================================================================== L
T("Lightning Barrel", spr("barrel", STEEL, YELLOW, DARK), melee(2, -3), blast("lightning", 10, 0xFFF59D, cost=12, cd=80),
  None, rarity="rare", desc="Tap to call down a bolt of lightning.")
T("Lightning Release Suit", spr("suit", BLACK, YELLOW), FIST, None, buff([E("speed", 300, 2), E("strength", 300, 0)], cost=12),
  armor=armor("legs", 5, 0x222222), worn=[E("speed", 60, 0)], rarity="epic", desc="Lightning-powered strength and speed.")
T("Lion-Headed Kannon", spr("puppet", GOLD, RED, DARK), melee(2, -2), seal(0.4, 8, cost=15), summon("wood", 30, 5, "melee", cost=15),
  rarity="epic", desc="Sealing Technique: Lion Closing Roar.")
T("Liquid Crystal Wall", spr("device", SILVER, CYAN, WHITE), FIST, wall(7, 4, 400, cost=8, kind="crystal"), None, rarity="rare",
  desc="Projects a wall of liquid crystal particles.")
T("Lizard Puppet", spr("puppet_lizard", GREEN, ORANGE), melee(2, -2), summon("lizard", 30, 5, "flame", cost=18), None, rarity="epic",
  desc="S-rank fire-breathing puppet.")
# ====================================================================== M
T("Magnum Opus", spr("device", BLACK, GOLD, CYAN), FIST, scan(48, 400, cost=4), blast("projectile", 8, GOLD, cost=12, element="random", radius=2),
  rarity="epic", desc="Archive of every ninjutsu ever recorded.")
T("Makibishi", spr("caltrops", DARK, STEEL), FIST, trap(6, 3, 400, [E("slowness", 60, 2)]), None, stack=16, desc="Scatter caltrops to block pursuit.")
T("Marlin Harpoon", spr("harpoon", SILVER, BLUE), melee(7, -2.6, 2.0), chain(7, 18, cd=30), throw(8, 2.2, returns=1, consume=0, cd=40),
  rarity="rare", desc="Sentient harpoon.")
T("Mechanical Drilling Arm", spr("drill_arm", DARK, STEEL, BRONZE), melee(7, -2.6, 0, hit("pierce", 3)), slam(7, 1.5, cost=2, drill=1),
  None, rarity="uncommon", desc="Gauntlet drill.")
T("Mechanical Eight Trigrams Formation Drum", spr("drum", RED, WOOD, GOLD), FIST, sonic([E("slowness", 100, 2)], 16, cost=8),
  sonic([E("resistance", 200, 0), E("strength", 200)], 16, allies=1, cost=8), rarity="rare", desc="Commands the village's defensive formation.")
T("Mechanical Kunai Thrower", spr("gatling", WOOD, STEEL, DARK), melee(2, -3), throw(4, 2.2, count=8, spread=8, gravity=0.01, consume=0, drop=0, cd=60),
  None, rarity="uncommon", desc="Crank-turned kunai gatling.")
T("Metal Doll", spr("doll", STEEL, DARK), melee(4, -3.2, 0, hit("knockback", 2)), summon("metal", 60, 8, "melee", cost=20, speed=0.2),
  throw(10, 0.9, gravity=0.08, cd=60), rarity="rare", desc="Huge iron doll: summon it, or hurl it.")
T("Metal Gauntlets", spr("gauntlet", STEEL, DARK), melee(6, -2.0), chain(5, 12, cd=25, effects=POISON_FX), None, rarity="uncommon",
  desc="Demon Brothers' clawed gauntlets with a shuriken chain.")
T("Meteorite", spr("meteor", STONE, ORANGE), melee(3, -3), throw(10, 0.9, gravity=0.08, impact="explode", radius=2, color=ORANGE, drop=0, cd=60),
  consume(chakra=40, time=24, consume_item=0, cd=1200), stack=4, rarity="rare", desc="Star-metal: hurl it, or draw on its chakra.")
T("Microscopic Scientific Ninja Tools", spr("device", SILVER, PINK, CYAN), FIST, consume(heal=10, effects=[E("regeneration", 400, 1)], time=24),
  None, stack=8, rarity="epic", desc="Injected micro-tools that repair tissue.")
T("Military Rations Pill", spr("pill", BROWN, TAN), FIST, consume(chakra=50, food=6, effects=[E("hunger", 60)], time=16),
  None, stack=16, desc="Restores chakra and nourishment.")
T("Mind Awakening Pill", spr("pill", BLACK, PURPLE), FIST, consume(effects=[E("strength", 400, 2), E("speed", 400, 1), E("wither", 200, 1)], time=16),
  None, stack=8, rarity="rare", desc="Forces the cursed seal to level two.")
T("Mind Reading Amplification Machine", spr("device", STEEL, GREEN, DARK), FIST, scan(16, 400, cost=4), None, rarity="rare",
  desc="Reads a target's mind.")
T("Mirror Drone", spr("mirror_drone", SILVER, CYAN), FIST, summon("drone", 12, 4, "laser", count=4, life=400, cost=15), None, rarity="rare",
  desc="Chakra-driven attack drones.")
T("Moon Transportation Device", spr("ship", SILVER, PURPLE), FIST, glide(1.0, 200, cost=2, variant="float"), None, rarity="rare",
  desc="Lunar transport: floats you gently.")
T("Mother and Father", spr("puppet", FLESH, CRIMSON, BLACK), melee(2, -2), summon("crimson", 26, 6, "melee", count=2, cost=25),
  guard(cost=1), rarity="epic", desc="Sasori's first two puppets.")
T("Moulding Mushroom", spr("mushroom", BROWN, LIME), FIST, bomb("cloud", [E("hunger", 200, 2), E("poison", 60)], LIME, 3),
  None, stack=16, desc="Spoils rations overnight.")
# ====================================================================== N
T("Nara Clan Medical Encyclopaedia", spr("book", GREEN, BROWN), FIST, consume(heal=6, aoe=5, cleanse=1, time=40, consume_item=0, cd=600),
  scan(16, 200), rarity="rare", desc="Medical knowledge of the Nara.")
T("New Moon Flower Perfume", spr("perfume", WHITE, PURPLE), FIST, consume(cleanse=1, time=12, drink=1), consume(cleanse=1, aoe=6, time=12, drink=1),
  stack=8, desc="Dispels genjutsu and hypnotism.")
T("Ninja Info Cards", spr("cards", PAPER, INK), FIST, scan(24, 200, cost=0), None, stack=16, desc="Chakra-encoded intel cards.")
T("Nuibari", spr("needle_sword", SILVER, BANDAGE), melee(7, -2.0, 1.0, hit("pierce", 2)), chain(6, 16, mode="bind", effects=PARALYZE_FX),
  throw(7, 2.6, returns=1, pierce=4, consume=0, cd=40), rarity="epic", desc="Sewing needle sword that stitches foes together.")
T("Nunchaku", spr("nunchaku", WOOD, STEEL), melee(4, -1.0), dash(4, 3, cost=0, cd=20), None, desc="Rapid strikes.")
# ====================================================================== O
T("Ocarina", spr("ocarina", BLUE, WHITE), FIST, sonic([E("strength", 300, 1), E("speed", 300, 1)], 12, allies=1, cost=6),
  consume(cleanse=1, aoe=8, time=20, consume_item=0, cd=200), rarity="rare", desc="Strengthens allies; frees them from genjutsu.")
T("Odourless Poison Mist", spr("tag", PAPER, PURPLE), FIST, bomb("cloud", [E("slowness", 160, 1), E("weakness", 160, 1), E("blindness", 60)], 0xD8C8E8, 4),
  None, stack=16, desc="Dulls the senses of anyone who breathes it.")
# ====================================================================== P
T("Palace of the Dragon King", spr("black_box", BLACK, GOLD), FIST, bomb("cloud", [E("slowness", 300, 6), E("mining_fatigue", 300, 4), E("weakness", 300, 4)], STONE, 3),
  None, stack=4, rarity="epic", desc="Black box releasing a petrifying gas.")
T("Paw Encyclopaedia", spr("book", PINK, BROWN), FIST, scan(16, 200, cost=0), None, desc="A book of cat paw prints.")
T("Pepper Balloon Earrings", spr("earrings", RED, GOLD), FIST, bomb("cloud", [E("blindness", 80), E("nausea", 80)], RED, 2, speed=1.6),
  None, stack=8, desc="Pepper spray for a quick escape.")
T("Petrification Gauntlet", spr("gauntlet", STONE, DARK), melee(6, -2.4, 0, hit("paralyze", 2)), seal(0.5, 5, cost=10, effects=[E("slowness", 200, 6), E("mining_fatigue", 200, 4)]),
  None, rarity="rare", desc="Turns targets to stone.")
T("Photon Gun", spr("gun", WHITE, CYAN, DARK), melee(2, -2), blast("beam", 9, 0xE0FFFF, cost=10, cd=40, rng=40, fire=60), None, rarity="epic",
  desc="400,000-watt laser.")
T("Poison", spr("vial", PURPLE, LIME), FIST, bomb("cloud", POISON_FX, PURPLE, 3), consume(effects=[E("strength", 200)], time=16, consume_item=0, cd=200),
  stack=16, desc="Throw a poison vial; sneak to coat your weapons (strength).")
T("Prayer Beads", spr("beads", BROWN, GOLD), FIST, throw(2, 1.6, impact="seal", effects=PARALYZE_FX, drop=1, cd=40), None, desc="Sticks to and binds the sealed.")
T("Puppet", spr("puppet", WOOD, BANDAGE, DARK), melee(2, -2), summon("wood", 20, 4, "melee", cost=10), None, desc="Basic puppet controlled with chakra threads.")
T("Puppet Buzzsaw", spr("buzzsaw", STEEL, DARK), melee(7, -1.6, 0, hit("bleed", 2)), throw(8, 1.4, gravity=0.0, returns=1, pierce=3, consume=0, cd=40),
  None, rarity="rare", desc="Spinning saw from the Third Kazekage's arm.")
T("Puppet Walker", spr("puppet", GREY, PURPLE, BLACK), melee(2, -2), summon("crimson", 30, 6, "melee", cost=18), None, rarity="rare",
  desc="Forbidden Shirogane puppet.")
# ====================================================================== R
T("Ray Bow", spr("ray_bow", CYAN, WHITE), melee(1, -2), blast("projectile", 7, CYAN, cost=6, cd=20, speed=3.0, effects=PARALYZE_FX, size=0.3),
  blast("projectile", 4, CYAN, cost=12, cd=60, speed=3.0, count=5, size=0.3), rarity="epic", desc="Chakra arrows that paralyse.")
T("Red Moon Scroll", spr("scroll", PAPER, CRIMSON), FIST, consume(cleanse=1, aoe=16, time=20, consume_item=0, cd=1200), None, rarity="epic",
  desc="Can end a genjutsu world.")
T("Resonating Echo Speaker", spr("speaker", STEEL, DARK), melee(4, -2.0, 0, hit("blind", 1)), sonic([E("nausea", 140), E("slowness", 80, 1)], 6, dmg=4, cost=4),
  guard(cost=1), rarity="rare", desc="Dosu's sound gauntlet.")
T("Retractable Cable Arm", spr("hand", DARK, STEEL, RED), melee(4, -2.4, 1.5), chain(4, 16, cd=20), None, rarity="uncommon",
  desc="Mechanical grabbing hand.")
T("Retractable Shield", spr("retract_shield", SILVER, GOLD), FIST, guard(reflect=1, cost=0), None, rarity="rare", desc="Gauntlet that opens into a shield.")
T("Retractable Spear", spr("retract_spear", BLACK, CRIMSON), melee(6, -2.4, 2.5, hit("bleed", 1)), throw(7, 2.4, returns=1, consume=0, cd=30),
  None, rarity="rare", desc="Hidan's extending spear.")
# ====================================================================== S
T("Saezuri", spr("flute", GOLD, RED), FIST, sonic([E("slowness", 120, 3)], 12, dmg=2, cost=8), None, rarity="epic", desc="Seven sounds control ancient power.")
T("Samehada", spr("shark_sword", NAVY, BANDAGE, CYAN), melee(8, -2.8, 1.0, hit("drain", 6)), guard(absorb=1, cost=0),
  consume(heal=8, chakra=30, time=1, consume_item=0, cd=400), rarity="epic", desc="Sentient blade that devours chakra.")
T("Sand", spr("sand", SAND_C, TAN), FIST, sand(5, 10), guard(cost=1), stack=16, desc="Chakra-infused sand: waves and shields.")
T("Sand Gourd", spr("gourd", SAND_C, BROWN), melee(3, -3), sand(7, 14, cost=8), guard(reflect=0, cost=0), rarity="epic", desc="Gaara's gourd: Sand Coffin; sneak for an absolute defence.")
T("Sanshōuo", spr("puppet_lizard", BROWN, GREEN), melee(2, -2), summon("lizard", 50, 3, "guard", cost=15), guard(cost=0), rarity="rare",
  desc="Salamander shield puppet.")
T("Sasori (puppet)", spr("puppet", CRIMSON, BLACK, SILVER), melee(3, -2), blast("cone", 5, ORANGE, cost=6, fire=80), summon("crimson", 30, 6, "melee", cost=20),
  id="sasori_puppet", rarity="epic", desc="Sasori's own body: flamethrowers and blades.")
T("Sasumata", spr("sasumata", STEEL, WOOD), melee(4, -2.6, 2.0, hit("knockback", 1.6)), guard(cost=0), None, desc="Man-catcher polearm.")
T("Scalpel", spr("scalpel", SILVER, DARK), melee(4, -1.0, 0, hit("bleed", 1)), throw(4, 2.0, cd=10), None, stack=16, desc="Medical blade.")
T("Scissor Pincers", spr("pincers", SILVER, DARK), melee(7, -2.6, 1.0, hit("bleed", 2)), chain(6, 10, cd=40), None, rarity="uncommon",
  desc="Kamikiri's giant pincers.")
T("Scroll", spr("scroll", PAPER, RED), FIST, throw(4, 2.0, count=6, spread=14, gravity=0.02, consume=0, drop=0, cd=80), None, stack=8, id="storage_scroll",
  desc="Storage scroll: unseal a rain of weapons.")
T("Scroll of Seals", spr("big_scroll", PAPER, GOLD, CRIMSON), FIST, buff([E("strength", 600, 1), E("resistance", 600, 1)], cost=20, cd=1200), None,
  rarity="epic", desc="Forbidden techniques.")
T("Sealing Arm Braces", spr("braces_gold", GOLD, BLACK), FIST, seal(0.3, 5, cost=10), None, rarity="rare", desc="Golden braces that seal power.")
T("Sealing Tag", spr("tag", PAPER, NAVY), FIST, seal(0.3, 5, cost=6), throw(1, 1.6, impact="seal", effects=[E("weakness", 300, 2), E("slowness", 200, 2)], drop=0),
  stack=16, desc="Seal a weakened foe, or bind one at range.")
T("Secret Treasure of the Toads", spr("tag", PAPER, GREEN), FIST, consume(heal=20, chakra=100, cleanse=1, time=20), None, stack=4, rarity="epic",
  desc="Senjutsu-infused tag that fully restores.")
T("Senbon", spr("senbon", SILVER), melee(2, -1.0, 0, hit("paralyze", 1, 30)), throw(3, 2.4, count=3, spread=4, gravity=0.01, impact="dart", cd=10),
  throw(1, 2.4, impact="dart", effects=SLEEP_FX, cd=40), stack=32, desc="Needles: fan of three, or a nerve strike (sneak).")
T("Shakujō", spr("ringed_staff", GOLD, BRONZE), melee(5, -2.4, 1.5), sonic([E("nausea", 100)], 6, cost=2), guard(cost=0), desc="Ringed monk's staff.")
T("Shibuki (sword)", spr("scroll_sword", PAPER, RED, STEEL), melee(7, -2.6), slam(10, 4, cost=4, explode=1), None, id="shibuki_sword", rarity="epic",
  desc="Explosive-tag sword.")
T("Shichiseiken", spr("broad_sword", ICE, BROWN, PAPER), melee(7, -2.4), seal(0.35, 8, cost=12), charge(10, 6, color=ICE), rarity="epic",
  desc="Treasured sword that seals by its curse.")
T("Shield", spr("round_shield", STEEL, WOOD), melee(2, -2, 0, hit("knockback", 1.4)), guard(cost=0), slam(4, 2, cost=0, cd=40), desc="Plain shield.")
T("Shinigami Mask", spr("oni_mask", WHITE, PURPLE), FIST, buff([E("strength", 300, 3), E("wither", 200, 1)], cost=30, cd=1200), None,
  armor=armor("head", 2, 0xE8E8E8), rarity="epic", desc="Borrows the death god's power.")
T("Shinobi Battle Armour", spr("armor_plate", CRIMSON, BLACK), FIST, None, guard(cost=0), armor=armor("chest", 7, 0x7A1C1C), worn=[E("resistance", 60)],
  rarity="rare", desc="Warring-era armour.")
T("Shinobi Gauntlet", spr("gauntlet", DARK, ORANGE), melee(4, -2), blast("projectile", 7, ORANGE, cost=0, cd=40, element="random", radius=2), None,
  rarity="epic", desc="Fires stored ninjutsu scrolls, no chakra needed.")
T("Shuigu", spr("cane_ring", GOLD, CYAN), melee(5, -2.4, 1.0), blast("cone", 4, CYAN, cost=4, rng=8, element="water"), consume(food=0, effects=[E("water_breathing", 1200)], time=8, consume_item=0, cd=200),
  rarity="epic", desc="Golden cane that produces limitless water.")
T("Shuriken", spr("shuriken", STEEL, DARK), melee(2, -1.0), throw(4, 2.0, cd=6), throw(3, 2.0, count=3, spread=10, cd=20), stack=32,
  desc="Throw one; sneak for a fan of three.")
T("Slime Suit", spr("suit", LIME, GREEN), FIST, None, guard(cost=0), armor=armor("chest", 4, 0x7CB342), worn=[E("slow_falling", 60)], rarity="rare",
  desc="Mucus membrane that slips every blow.")
T("Slingshot", spr("slingshot", WOOD, BROWN), FIST, throw(3, 2.0, impact="pebble", consume=0, cd=15), None, desc="Pebble shooter.")
T("Smoke Bomb", spr("bomb", GREY, WHITE), FIST, bomb("cloud", SMOKE_FX, 0xCCCCCC, 4), None, stack=16, desc="Cover for escapes.")
T("Smoke Tag", spr("tag", PAPER, GREEN), FIST, ward(0, cd=20), bomb("cloud", SMOKE_FX, 0x9CCC65, 5), stack=16, desc="Creeping green smoke.")
T("Smoke-Flash Bomb", spr("bomb", WHITE, CYAN), FIST, bomb("flash", SMOKE_FX + [E("glowing", 100)], WHITE, 5), None, stack=16, desc="Scientific smoke and flash.")
T("Snowboard", spr("board", RED, PURPLE), FIST, glide(1.0, 120, cost=0, variant="board", effects=[E("speed", 200, 2)]), None, desc="Fast travel over snow.")
T("Space-Time Gate", spr("gate", PURPLE, BLACK), FIST, anchor("set"), anchor("warp", cost=10), rarity="epic", desc="Set a gate anchor; sneak to warp back.")
T("Space–Time Hole", spr("vortex", BLACK, PURPLE), FIST, vortex(6, 2, 60, cost=20), None, rarity="epic", desc="Negative space that swallows everything.")
T("Spear", spr("spear", STEEL, WOOD), melee(6, -2.6, 2.0, hit("pierce", 1)), throw(8, 2.4, cd=20), dash(6, 6, cost=0, cd=40), desc="Thrust, throw, or charge.")
T("Spike Bomb", spr("spike_bomb", DARK, STEEL), FIST, bomb("spikes", None, STEEL, 4, dmg=4), None, stack=16, desc="Bursts into flying spikes.")
T("Spirit Orbs", spr("gem_orbs", RED, BLUE, GREEN), FIST, buff([E("strength", 300, 1), E("resistance", 300, 1), E("speed", 300)], cost=15), blast("projectile", 5, PURPLE, cost=8, count=5),
  rarity="epic", desc="Materialised will of the Master Puppet.")
T("Spy Drone", spr("drone", SILVER, DARK), FIST, scan(48, 400, cost=2), summon("drone", 12, 2, "laser", count=1, cost=10), rarity="rare", desc="Reconnaissance drone.")
T("Steam Armour", spr("armor_plate", RED, WHITE), FIST, None, slam(6, 4, cost=6, effects=[E("weakness", 60)], color=WHITE), armor=armor("chest", 7, 0xB33A3A),
  worn=[E("fire_resistance", 60)], rarity="epic", desc="Boil Release armour; sneak to vent scalding steam.")
T("Stone Swords", spr("stone_sword", STONE, DARK), melee(7, -2.4), chain(5, 10, cd=30), None, rarity="rare", desc="Myōboku swords linked by chain.")
T("Stone of Gelel", spr("crystal", GREEN, LIME), FIST, buff([E("regeneration", 400, 1), E("absorption", 400, 1)], cost=10), None, rarity="epic", desc="Life energy crystal.")
T("Sword", spr("katana", SILVER, DARK, BANDAGE), melee(6, -2.4), dash(6, 6, cost=0, cd=40), guard(cost=0), desc="Katana / ninjatō.")
T("Sword of Kusanagi (Orochimaru)", spr("katana", SILVER, GOLD, PURPLE), melee(8, -2.2, 2.0, hit("pierce", 2)), dash(8, 9, cost=5), throw(8, 2.6, returns=1, consume=0, cd=40),
  rarity="epic", desc="Extends and flies at its master's will.")
T("Sword of Kusanagi (Sasuke Uchiha)", spr("katana", SILVER, BLACK, PURPLE), melee(8, -2.0, 0, hit("lightning", 3)), charge(11, 6, element="lightning", color=0xCDE7FF),
  dash(8, 8, cost=4), rarity="epic", desc="Chidori Current flows through the blade.")
T("Sword of Kusanagi (Scientific Ninja Tool)", spr("katana", SILVER, NAVY, CYAN), melee(8, -2.0, 0, hit("lightning", 3)), charge(11, 6, element="lightning", color=0xCDE7FF),
  dash(8, 8, cost=4), rarity="epic", desc="Replacement Kusanagi.")
T("Sword of Nunoboko", spr("helix_sword", BLACK, PURPLE), melee(12, -3.0, 1.5), charge(18, 9, color=0x222222), slam(12, 4, cost=10), rarity="epic",
  desc="Truth-Seeking helix blade of the Sage.")
T("Sword of Totsuka", spr("jar_sword", RED, ORANGE, PAPER), melee(9, -2.6, 2.0), seal(0.5, 10, cost=20), None, rarity="epic",
  desc="Ethereal sealing sword of sake.")
T("Sword of the Thunder God", spr("lightning_sword", YELLOW, CYAN, DARK), melee(9, -2.0, 1.5, hit("lightning", 4)), blast("lightning", 10, 0xFFF59D, cost=15, cd=100),
  dash(9, 9, cost=6, element="lightning"), rarity="epic", desc="Tobirama's blade of pure lightning.")
# ====================================================================== T
T("Tailed Beast Drug", spr("pills3", ORANGE, RED, PURPLE), FIST, consume(effects=[E("strength", 400, 2), E("resistance", 400, 1), E("hunger", 400, 2)], time=16),
  None, stack=8, rarity="epic", desc="Capsules that trigger a tailed-beast transformation.")
T("Tantō", spr("tanto", SILVER, DARK), melee(5, -1.6), dash(5, 5, cost=0, cd=30), None, desc="Short blade for quick draws.")
T("Tea Kettle", spr("kettle", BRONZE, DARK), melee(3, -3), throw(4, 1.0, gravity=0.06, impact="cloud", effects=[E("slowness", 60)], color=0xEFEFEF, radius=2, cd=60), None,
  desc="Bunpuku's kettle: a scalding steam bomb.")
T("Tekkō", spr("gauntlet", STEEL, BLACK), melee(5, -1.4), slam(5, 1.5, cost=0, cd=20), None, desc="Knuckle guards.")
T("Tenseigan (Energy Vessel)", spr("tenseigan", CYAN, WHITE), FIST, blast("projectile", 12, CYAN, cost=25, cd=200, radius=4, size=1.0),
  vortex(5, 2, 60, cost=25), rarity="epic", desc="Hamura's chakra vessel.")
T("Tessen", spr("fan_closed", BLACK, RED), melee(5, -1.8), gust(1.4, 2, 6, cost=2), guard(cost=0), desc="Iron war fan.")
T("Third Kazekage (puppet)", spr("puppet", BLACK, PURPLE, IRON_SAND), melee(3, -2), sand(8, 12, IRON_SAND, kind="iron"), summon("crimson", 40, 7, "melee", cost=25),
  id="third_kazekage_puppet", rarity="epic", desc="Wields Iron Sand.")
T("Three Coloured Pills", spr("pills3", GREEN, YELLOW, RED), FIST, consume(effects=[E("strength", 600, 3), E("resistance", 600, 1), E("weakness", 2400, 1)], time=16),
  None, stack=3, rarity="epic", desc="Akimichi pills: overwhelming power, terrible aftermath.")
T("Three-Section Staff", spr("three_section", WOOD, STEEL), melee(5, -1.8, 1.5), slam(6, 3.5, cost=0, cd=30), guard(cost=0), desc="Flexible jointed staff.")
T("Toad (Scientific Ninja Tool)", spr("toad", GREEN, ORANGE, STEEL), FIST, summon("toad", 20, 4, "explode", count=2, cost=10), None, rarity="rare",
  desc="Mechanical toads.")
T("Tobishachimaru", spr("ship", WOOD, GOLD), FIST, glide(1.6, 200, cost=4, variant="ship"), None, rarity="rare", desc="Flying ship: soar across the land.")
T("Tonfa", spr("tonfa", WOOD, DARK), melee(4, -1.4), guard(cost=0, knock=0.6), scan(16, 100, cost=0), desc="Close-combat batons; also used for dowsing.")
T("Triple-Bladed Scythe", spr("scythe3", CRIMSON, BLACK), melee(8, -2.6, 1.5, hit("bleed", 2)), chain(7, 14, cd=30), None, rarity="epic",
  desc="Hidan's cable scythe.")
T("Tsurukame", spr("turtle_shield", STEEL, GREEN), melee(3, -2), guard(reflect=1, cost=0, knock=1.0), slam(7, 3, cost=3), rarity="rare",
  desc="Iō's offensive shield.")
# ====================================================================== U-Z, Ō
T("Umbrella", spr("umbrella", PURPLE, WOOD), melee(4, -2.0), throw(3, 1.6, count=12, spread=30, gravity=0.04, impact="dart", consume=0, drop=0, cd=80),
  glide(0.4, 100, cost=0, variant="umbrella"), desc="Senbon rain; sneak to float down.")
T("Vambraces", spr("braces_gold", GOLD, DARK), melee(6, -2.4, 0, hit("knockback", 1.2)), guard(cost=0), None, rarity="uncommon", desc="Weighted golden braces.")
T("Vampire Puppet", spr("puppet", GREY, CRIMSON, BLACK), melee(2, -2), summon("crimson", 26, 5, "drain", cost=15), None, rarity="rare",
  desc="Bloodthirsty puppet that drains its prey.")
T("Water Stream Shooters", spr("hand", DARK, CYAN, STEEL), melee(2, -2), blast("cone", 5, CYAN, cost=4, rng=10, element="water"), blast("beam", 8, CYAN, cost=8, cd=60),
  rarity="rare", desc="Pressurised water jets; sneak for a cutting stream.")
T("Water-Firing Puppets", spr("puppet", WOOD, CYAN, DARK), melee(2, -2), summon("wood", 14, 3, "water", count=2, cost=12), None, rarity="uncommon",
  desc="Box puppets that spray water.")
T("Weaknessless Soaring Shortswords", spr("twin_swords", SILVER, RED), melee(6, -1.8), guard(reflect=1, cost=1), dash(6, 7, cost=3), rarity="epic",
  desc="Twin jian that answer every attack.")
T("Weights", spr("weights", DARK, WHITE, INK), FIST, None, buff([E("speed", 300, 3), E("haste", 300, 1)], cost=0, cd=600), armor=armor("feet", 2, 0x333333),
  worn=[E("slowness", 60, 0)], desc="Training weights: slow you while worn; sneak-use to drop them for a burst of speed.")
T("Whip", spr("whip", BROWN, DARK), melee(3, -1.6, 2.5), chain(3, 10, cd=15), None, desc="Long-reach lash.")
T("White Light Chakra Sabre", spr("sabre_glow", WHITE, CYAN, DARK), melee(7, -1.8), charge(11, 7, color=WHITE), dash(7, 7, cost=4), rarity="epic",
  desc="White Fang's glowing tantō.")
T("Winged Mechanical Device", spr("wing", BROWN, STEEL), FIST, glide(1.4, 160, cost=2, variant="wings"), glide(1.0, 120, cost=4, variant="bomber"), rarity="uncommon",
  desc="Hang-glider for aerial raids.")
T("Wire Strings", spr("wire", SILVER, DARK), FIST, chain(1, 14, mode="bind", effects=PARALYZE_FX, cd=30), trap(4, 2, 600, [E("slowness", 100, 3)]), stack=16,
  desc="Bind targets or string tripwires.")
T("Wrist Launcher", spr("wrist_launcher", DARK, STEEL, GREY), melee(3, -2), bomb("cloud", SMOKE_FX, 0xBDBDBD, 4, speed=1.8, cd=40),
  bomb("cloud", POISON_FX, PURPLE, 4, speed=1.8, cd=60), rarity="uncommon", desc="Eight barrels of smoke: plain, or sneak for poison.")
T("Wrist-Mounted Senbon Launcher", spr("wrist_launcher", DARK, SILVER, PURPLE), melee(2, -2), throw(4, 2.6, count=3, spread=4, gravity=0.0, impact="dart", effects=POISON_FX, consume=0, drop=0, cd=30),
  None, rarity="uncommon", desc="Shizune's poisoned senbon launcher.")
T("Yata Mirror", spr("mirror", GOLD, ORANGE, RED), FIST, guard(reflect=1, absorb=1, cost=0), None, rarity="epic", desc="Ethereal shield that negates any attack.")
T("Ōtsutsuki Clan Puppets", spr("puppet", WHITE, CYAN, GREY), melee(2, -2), summon("metal", 30, 5, "laser", count=2, cost=20), None, rarity="epic",
  desc="Chakra-free lunar puppets.")

# Index / umbrella pages of the category that are not physical tools (documented in docs/wiki):
EXCLUDED = {
    "List of Technology": "index page listing other technology articles",
    "Treasured Tools of the Sage of Six Paths": "group page; its five tools (Bashōsen, Benihisago, Kohaku no Jōhei, Kōkinjō, Shichiseiken) are implemented individually",
    "Shinobi-Ware": "umbrella term for bionic tools; the individual Shinobi-Ware (Absorbing Eye, Spy Drone, Mirror Drone) are implemented",
}
# Wiki tools already implemented by other systems (kept as their existing items): chakra_blade (Phase 3 weapon),
# forehead_protector / flak_jacket (village origin gear: faction badge, village colour, rogue scratch).
EXISTING = {"chakra_blade", "forehead_protector", "flak_jacket"}  # the last two are the village-bound origin gear
