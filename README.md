# MineAnime: AnimeCore + NarutoRPG (Minecraft 1.21.1 · Fabric · Java 21)

| Module | Package | Role | Jar |
|---|---|---|---|
| `:animecore` | `com.animecore.*` | Anime-agnostic RPG engine | ~437 KB (budget 1.5 MB) |
| `:narutorpg` | `com.narutorpg.*` | Naruto content mapped onto the engine | ~297 KB (budget 15 MB) |

Runtime deps (installed separately, **never bundled**): Fabric API, Cardinal Components API 6.1.x,
PlayerAnimator 2.0.x, GeckoLib 4.7.x. Optional: JEI, Lootr, Essential.

```bash
./gradlew build                    # builds both jars + verifyAssetBudget + verifyJarBudget (no nested jars)
./gradlew :narutorpg:runDatagen    # regenerates data/narutorpg/animecore/** + lang
./gradlew :narutorpg:runGametest   # 30 headless end-to-end tests (incl. every move of all 244 ninja tools)
python3 tools/catalog/build.py     # regenerate ninja_tools.json + 244 sprites (then runDatagen)
./gradlew :narutorpg:runClient     # dev client with both mods
```

## Namespace isolation

| Thing | Owner | Example |
|---|---|---|
| Dynamic registries (shared engine) | `animecore` | `animecore:ability`, `animecore:lineage` ... |
| Registry **entries** | content mod | `narutorpg:fireball_jutsu` → `data/narutorpg/animecore/ability/fireball_jutsu.json` |
| CCA component | content mod | `narutorpg:power` (each mod has its own `PlayerPowerComponent`) |
| Energy pools | content mod | `narutorpg:chakra`, `bleachrpg:reiatsu` |
| Attribute modifiers | content mod | `narutorpg:trait/0` |
| Keybind category / keys | content mod | `key.categories.narutorpg`, `key.narutorpg.hotbar_1` |
| Combo tokens | content mod | `narutorpg:tiger` |
| Engine payloads | `animecore` (one engine per pack), they carry the `system` id | `animecore:hotbar_cast{system="narutorpg"}` |
| Mod-specific payloads | content mod | `AnimeModContext#payloadId("...")` → `narutorpg:...` |

Every definition has a `"system"` field naming the content mod that owns it, so datapacks can add
Naruto jutsu under any namespace and they still route to `narutorpg:power`.

## `com.animecore` package tree
```
com.animecore
├── AnimeCore                      ModInitializer (registries, networking, commands, damage/death hooks)
├── platform/                      version shield: Ids, AnimeRegistryAccess, AnimeNetworking,
│                                  AnimePacketCodecs, AnimeParticles, AnimeEnvironment
├── api/                           AnimeModContext(+Builder), EnergyPoolSpec, AnimeModRegistry, AnimeCoreApi
│   └── event/AnimeCoreEvents      PRE/POST_ABILITY_CAST, ENERGY_CONSUME, TRANSFORMATION_TOGGLE,
│                                  FATAL_DAMAGE_INTERCEPT, REACTIVE_DEFENSE
├── registry/                      AnimeRegistries (5 synced dynamic registries), AnimeBuiltinRegistries
│   └── definition/                AbilityDefinition, LineageDefinition, FactionDefinition,
│                                  AffinityDefinition, TransformationDefinition, EnergyCost, CastType
├── ability/                       AbilityExecutor (quick-cast / combo / charge / reactive), AbilityContext,
│   │                              AbilityDamage, CastResult
│   └── action/                    IAbilityAction (Codec.dispatch) + projectile, aoe_sphere, beam_raycast,
│                                  dash_strike, self_buff, summon_entity
├── trait/                         ITraitModifier (Codec.dispatch) + attribute, energy_capacity,
│                                  energy_regen, ability_power; TraitAccumulator
├── affinity/AffinityMatrix        clash multipliers + fusion (Kekkei Genkai) resolution
├── component/                     PlayerPowerComponent (CCA, schema_version), EnergyPool,
│                                  AnimeComponentInitializer
├── transformation/                TransformationService (toggle, drain, revert, stage evolution)
├── animation/                     AnimLayer (BASE 1000 / UPPER_BODY 1500 / OVERLAY 2000),
│                                  AnimPlayback, AnimationService (peer broadcast + late-joiner resync)
├── entity/                        EnergyProjectileEntity (texture-free), AnimeEntities
├── network/                       AnimationSyncPayload (S2C), Cast/HotbarCast/ComboInput/Transform (C2S)
├── command/AnimeCoreCommands      /animecore info|lineage|faction|unlock|unlockform|cast|transform|...
├── datagen/                       damage type + lang
├── movement/                      MovementMode, MovementSpec, AnimeMovementHandler (adhesion, flight,
│                                  burst step), MotionUtil
├── mixin/LivingEntityMixin        walls act as ladders + water is walkable while adhesion is on
├── item/                          AnimeDataComponents (ability_scroll, weapon_infusion, faction_badge),
│                                  AnimeWeaponItem, AbilityScrollItem, FactionBadgeItem, AnimeItemVariants
├── loot/                          SetAbilityScrollFunction, SetAnimeWeaponDataFunction, AnimeLootFunctions,
│                                  AnimeLootHelper (vanilla injection + DataGen builders, Lootr-safe)
└── client/  (src/client/java, the only place MinecraftClient is referenced)
    ├── AnimeCoreClient
    ├── platform/AnimeClientNetworking
    ├── animation/AnimationLayerManager   PlayerAnimator ModifierLayer per AnimLayer, upper-body masking
    ├── network/AnimeClientPayloadHandlers
    ├── input/AnimeKeybinds               per-mod categories, press + release forwarding
    ├── hud/AnimeHudLayoutManager         per-mod panel groups stacked in TOP_LEFT / BOTTOM_LEFT
    │   └── element/                      EnergyBars, Transformation, AbilityHotbar, ComboTrail
    ├── combo/ComboWheelScreen            radial combo input with continuation hints
    └── render/                           ProceduralAuraRenderer, AfterimageEffects (texture-free)
com.animecore.compat.jei (client source set) AbstractAnimeJeiPlugin, AnimeComponentSubtypeInterpreter,
                                  AbilityScrollCategory, EnergyInfusionCategory
```

## `com.narutorpg` package tree
```
com.narutorpg
├── NarutoRPG              AnimeModContext: chakra + stamina pools, hand-sign animations
├── NarutoComponents       CCA entrypoint (3 lines)
├── content/               NarutoContent (registry keys), HandSigns, NarutoItems (kunai, chakra blade,
│                          jutsu scroll, leaf headband, creative tab, shinobi-cache loot injection)
├── mechanic/              NarutoMechanics: Fire jutsu fizzle underwater, Sharingan chakra discount
├── datagen/               NarutoDataGenerator, NarutoContentBootstrap, NarutoLangProvider,
│                          NarutoLootTableProvider, NarutoModelProvider
└── client/                NarutoRPGClient (keybinds), compat/NarutoJeiPlugin (one-line JEI plugin)
src/gametest/…             headless GameTests (not shipped)
```

## Ninja tools (Narutopedia `Category:Tools`)
All **244** tool pages (+3 index pages documented as skipped) are items, one creative tab per wiki
"Tool classification" group (Offensive / Defensive / Supplementary / Unclassified / Generic). Every tool has a
32x32 sprite, a melee attack (LEFT) with optional on-hit effect, a primary move (RIGHT) and a secondary move
(SNEAK+RIGHT), built from 24 reusable engine moves (`com.animecore.tool`). Full mapping: `docs/wiki/narutopedia_tools.md`.

- **Barrier Tag**: place one for a temporary 5x3 wall; place 3+ in sequence (within 10 blocks, 30 s) to link a lasting
  Barrier Gate that stands until a tag is broken.
- **Kunai**: stab, or throw (retrievable); with an Explosive Tag in the off-hand it becomes an explosive kunai.
- Puppets (Karasu, Kuroari, Sanshōuo, Sasori...) are allies on chakra threads; Flying Thunder God kunai mark & warp;
  sealing tags / Benihisago seal weakened foes; fans gust; Gunbai reflects; pills restore chakra; gliders fly...

![Tool sprites](docs/screenshots/p4_tool_sprites.png)

**Low-poly 3D weapons**: 99 weapons (blades, polearms, thrown, fans, shields, gauntlets, launchers...) also ship a
Blockbench-style cuboid model generated from their design (`tools/catalog/model3d.py`, palette textures derived from
the sprite). Like the vanilla trident, the inventory shows the flat icon while hands, the ground, item frames and
thrown projectiles use the 3D model (engine: `com.animecore.client.render.HeldModels`, reusable by any anime mod).
Display transforms are derived from vanilla's handheld/generated transforms and tuned in-game.

![Low-poly models](docs/screenshots/p5_lowpoly_first_person.png)

## Clans & bloodlines (Narutopedia `Category:Clans`)

All **52 wiki clans** plus a **Clanless** fallback are playable lineages (`docs/wiki/narutopedia_clans.md` has the crawl,
member list and per-clan data). The clans carry **417 clan jutsu**, generated from each jutsu's wiki infobox (classification,
nature, range, rank, hand signs), plus 7 extra dōjutsu forms and 11 advanced natures (Wood, Ice, Lava, Magnet...).

- **One jutsu unlocked by default**. The rest unlock by **clan skill level**. Hold your **Clan Emblem** in either hand while
  defeating enemies: their experience becomes clan skill XP instead of orbs. Use the emblem to see your level and next unlock.
- **First join**: a weighted clan roll (gamerule `animecoreClanAutoRoll`, default on). With it off, players start Clanless and
  can `/clan choose <clan>` once. Rarer clans (Uchiha, Senju, Uzumaki, Hyūga, Ōtsutsuki...) have low weights. A **pity counter**
  guarantees a rare bloodline after 10 common rolls.
- **Bloodline Scroll**: an unsealed scroll rerolls (never your current clan). A sealed scroll (from loot) awakens that exact clan.
  Switching removes the old clan's jutsu, forms, stat modifiers and traits but keeps abilities you learned elsewhere. Clan level
  is saved per clan.
- **Starter kit** (once per clan): bound emblem, **Clan Garb** dyed in the clan colour, and the clan's signature tools from its
  members' wiki tool lists.
- **Clan traits** (`animecore:clan_trait_type`): evasion, energy discount, sensing, on-hit effects, regeneration, fatal save,
  passive effects and skill XP boost. Uchiha is the reference clan: +15% Fire Release power, +20 chakra, +1 attack, Fire affinity,
  Great Fireball by default, Sharingan stages at clan levels 3/10/20/35 and a Sharingan-precognition evasion trait.
- Commands: `/clan info|list|choose`, plus `/clan roll|set|skill|level` for operators. Loot function `animecore:set_lineage` (Lootr-safe).
  JEI shows every clan emblem as its own entry with an info page listing the clan's jutsu and items.
- Regenerate: `python3 tools/catalog/clans.py` (catalog, sprites, doc) then `./gradlew :narutorpg:runDatagen`.

## Admin / cheat seals (creative tab "NarutoRPG Admin")

These are for testing and server staff. Only operators (permission level 2) or players in creative mode can use them,
and they're never used up. Right-click does a small step; sneak + right-click does a big one.

| Seal | Use | Sneak + use |
|---|---|---|
| Rank Up Seal | +1 rank | +10 ranks |
| Clan Awakening Seal | +1 clan skill level (unlocks follow) | max clan level |
| Mastery Seal | +1 mastery on the jutsu item in your other hand | master it fully (empty hand: all your items) |
| Skill Point Seal | +5 skill points | +50 |
| Nature Seal | all basic natures | all 23 natures |
| Reputation Seal | +50 with every village | allied with all |
| Chakra Seal | refill all energy and clear every cooldown | same |
| Kage Seal | max rank and clan level, master all your natures, refill | same |

Big unlocks post one summary chat line instead of hundreds.

## Fan genjutsu (Naruto Fanon `Category:Genjutsu`), each with its own vision

All **582 genjutsu** from the Naruto Fanon wiki are in. That covers the main category plus the *Path Gold Alignment
Techniques* and *The Way of the Evil Path* subcategories. The *Genjutsu User* and *Specialist* subcategories list
characters, so they were skipped; see `docs/wiki/narutofanon_genjutsu.md`.

- **Unique visuals per genjutsu**: each one has an `illusion` profile.
  - The pattern comes from its own description, out of 20 procedural patterns: blood moon, crow flock, petals, lotus,
    shattered mirror, fog, chains, watching eyes, darkness, flames, stars, spider web, sound rings, rain, vortex,
    serpents, blood veins, kaleidoscope, snow and spiral.
  - Colours come from the colour words in its description, and the element count, speed, density and particle differ
    per technique.
  - A uniqueness pass guarantees that no two genjutsu look alike.
- Victims see the vision full-screen; everyone nearby sees the matching world effect (crows circling, a moon overhead,
  chains rising, a lotus blooming...).
- Effects follow the motif: chains bind, flames burn, vortices pull, serpents poison, and lotus heals the caster.
- They live on the **Genjutsu** item and unlock through Genjutsu mastery. Ops can preview any one with `/vfx genjutsu <id>`.

![Fan genjutsu](docs/screenshots/fan_genjutsu.png)

## Nature releases (Narutopedia `Category:Chakra Natures`)

All **23 nature releases** are in: the 5 basic natures; Yin, Yang and Yin–Yang; Wood, Ice, Lava, Magnet, Storm, Boil, Scorch,
Explosion, Dust and Mud; and Crystal, Dark, Steel, Swift and Typhoon. Each has its own release item. They carry **1,286
nature jutsu** taken from the wiki, 1,078 of them new; the full list is in `docs/wiki/narutopedia_natures.md`.

- Choosing an origin gives you a **basic nature**, which leans toward your village's element, plus its first jutsu. Clan
  natures (Uchiha Fire, Senju Wood...) count too.
- **Nature mastery**: hold a release item while defeating enemies to gain mastery XP. Each level unlocks the next jutsu of
  that release in rank order. A release outside your natures trains 2.5× slower. Training a basic release to level 10 makes
  it your nature.
- **Fusions** awaken once you hold both natures (Lava = Fire + Earth, Storm = Water + Lightning, Mud = Earth + Water...).
  Wood, Ice and Magnet stay clan kekkei genkai. Crystal (Earth 30), Typhoon (Wind 30), Dark (Yin 30), Steel (Earth 40) and
  Swift (Lightning 30) awaken through mastery.
- Clan and village jutsu keep their own unlock systems. Scrolls still teach single jutsu.

## Village origins & backgrounds (Narutopedia `Category:Villages`)

All **40 wiki villages** are factions (18 hidden villages, 19 civilian villages, 3 sage realms), plus the **Missing-nin**
rogue alignment. There are **5 backgrounds**: Academy Prodigy, Shinobi Orphan, Anbu Trainee, Wandering Mercenary and
Merchant / Artisan Scion. The crawl and per-village data are in `docs/wiki/narutopedia_villages.md`.

- **First join** opens the *Choose Your Origin* screen: pick a village and an upbringing, or press *Randomize Origin*.
  The server validates the choice: the village must be selectable, the background must be allowed there, and you can't
  choose again without a reset. Commands are `/origin open|choose|random|info` (also `/naruto origin ...`). Ops get
  `/origin set|reset`. **Forged Papers** reset your origin. Gamerule `animecoreOriginScreen=false` rolls a random origin instead.
- **Starter kit**: forehead protector and flak jacket dyed in the village colour, kunai, shuriken and explosive tags,
  your background's bonus items, skill points and rank, the village's first techniques, and starting standings
  (e.g. Leaf-Sand +50, Leaf vs Cloud/Stone -25, every village -50 toward missing-nin).
- **Village techniques** (125 across the villages, taken from what each village's shinobi use on the wiki) unlock as your
  rank rises. Outsiders can only learn them from captured scrolls at rank 5 or with allied standing, and can't cast them
  below that.
- **Village-exclusive gear** (94 tools, e.g. the Seven Swords for Kiri and puppets for Suna): members and allies with
  reputation 50+ get full use. Outsiders deal half damage and can't use the gear's moves or chakra infusion. Foreign
  uniforms are taken off. Tooltips show the requirement.
- **Go rogue**: sneak and use your forehead protector with a blade in the other hand to scratch the symbol. You become
  missing-nin: home reputation drops by 100 and its allies by 25, and the scratched band marks you.
- Loot: village supply caches (`narutorpg:chests/supply/<village>`) placed in fitting vanilla chests, plus the
  `animecore:set_faction` loot function. JEI has an info page for each village's headband.

## Technique effects & animations (every jutsu, present and future)

Every ability is animated by the engine from its data, so new techniques from any content mod get effects automatically:
- **Style resolution**: the ability's `visuals.style`, then its affinity's `vfx_style`, then a keyword guess from the ids, then
  generic energy. There are 18 styles: flame, water, wind, lightning, earth, ice, plant, lava, sand, shadow, light, illusion, space,
  poison, blood, insect, smoke and energy.
- **Projectiles** are procedural swirling elemental balls. Spiral arms wrap in toward the core (Great Fireball), and the styles add
  lightning arcs, wind rings, tumbling rock or wood chunks and dark matter. Particles spiral into the ball as it flies.
- **Actions**: beams get glow ribbons (jagged for lightning) with helix strands, shockwaves get rings and a wave front, area techniques
  get a ground rune circle and cone sweep, walls erupt from the ground, buffs raise an aura helix, dashes leave streaks, impacts burst,
  and caught targets get a sigil above their head.
- **Screen effects**: genjutsu vision (tint, vignette, rotating three-comma spiral, ripples, petals), shadow darkness closing in,
  and a blinding flash. These are picked automatically from the ability's effects (nausea means illusion) or set by `visuals.screen_effect`.
- **Cast animations**: every ability without its own animation gets one by type: exhale, thrust, point, burst, gaze, sweep, slam,
  focus or lunge (`tools/catalog/animations.py`).
- Preview with `/vfx play <kind> <style>` and `/vfx screen <illusion|darkness|flash> [style]` (ops).

## Controls: item-based, no keybinds needed (AHZNB-style)
Jutsu live on **jutsu items (tokens)**: one per nature release (Fire, Wind, Lightning, Earth, Water, Wood, Ice, Lava, Magnet,
Storm, Boil, Scorch, Explosion, Dust, Yin, Yang) and one per school (Ninjutsu, Taijutsu, Genjutsu, Fūinjutsu, Medical
Ninjutsu, Hiden, Dōjutsu). Learning a jutsu hands you its token (once; `/jutsu tokens` replaces lost ones).

| Action | How |
|---|---|
| Use the selected jutsu | Hold the token, **right-click** (hold for charged jutsu, release to fire) |
| Switch jutsu | **Sneak + mouse wheel**, or **sneak + left-click** |
| Activate / switch eyes (Sharingan, Byakugan...) | The **Dōjutsu** token: right-click, or sneak + wheel to pick the eye |
| See what a token holds | Tooltip lists every jutsu with cost; the selected jutsu and its cooldown show above the hotbar |
| Walk on water | Automatic while you have chakra (sneak to dive) |
| Climb walls / trees | Jump at the wall while **looking up**; sneak to cling |
| Body Flicker (Shunshin) | **Sneak + jump** |

Techniques added later are placed on a token automatically: their `token` field, else their nature, else Ninjutsu.
The old keybinds (jutsu slots, hand-sign wheel, Sharingan, chakra control, Shunshin) still exist under
*Controls › NarutoRPG* but are **unbound by default** for players who prefer keys.

![Jutsu tokens](docs/screenshots/jutsu_tokens.png)
![Token cast](docs/screenshots/jutsu_token_cast.png)
![HUD](docs/screenshots/1_firstperson_hud.png)
![Aura](docs/screenshots/2_aura_front.png)
![Combo wheel](docs/screenshots/3_combo_wheel.png)
![JEI scrolls](docs/screenshots/p3_jei_scroll_category.png)
![JEI infusion](docs/screenshots/p3_jei_infusion_category.png)

## Items & loot (Phase 3)
| Item | Behaviour |
|---|---|
| Kunai / Chakra Blade | Sneak+Use infuses with your affinity (bonus elemental damage per hit, frontal projectile deflection); hold Use for a charged slash |
| Jutsu Scroll | `animecore:ability_scroll` component; use to learn the technique |
| Hidden Leaf Headband | `animecore:faction_badge`; use to join the Hidden Leaf |

`narutorpg:chests/shinobi_cache` is injected (40%) into dungeons, mineshafts, weaponsmiths and outposts.
All randomness comes from the loot context, so Lootr's per-player chests each roll their own scroll/element.

## Try it (dev client, cheats on)
```
/animecore lineage @s narutorpg:uchiha
/animecore faction @s narutorpg:hidden_leaf
Hold R to charge the Great Fireball, or hold Z and sign Snake › Ram › Boar › Horse › Tiger
Press B for the Sharingan, J for chakra control, Left Alt to Shunshin, G (Substitution) just before a hit.
/animecore info
```
