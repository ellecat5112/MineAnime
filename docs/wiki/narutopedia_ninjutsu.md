# Narutopedia Ninjutsu coverage

Source: https://naruto.fandom.com/wiki/Category:Ninjutsu - crawled 2026-11-26. Method: list=categorymembers (recursive) on Category:Ninjutsu (missing on the wiki); action=ask [[Jutsu classification::<group>]] (Semantic MediaWiki drill-down) for Ninjutsu and each ninjutsu sub-type, with infobox printouts.

## Category tree and groupings

- `Category:Ninjutsu`: 0 pages, 0 subcategories

`Category:Ninjutsu` does not exist on Narutopedia (the API reports the category page as missing). Ninjutsu are grouped by the Semantic MediaWiki property **Jutsu classification**, so its value *Ninjutsu* and every ninjutsu sub-type value are treated as the category and its subcategories, per the project rule:

| grouping (Jutsu classification) | pages |
|---|---:|
| Barrier Ninjutsu | 73 |
| Chakra Absorption Techniques | 38 |
| Chakra Flow | 94 |
| Clone Techniques | 118 |
| Collaboration Techniques | 261 |
| Cooperation Ninjutsu | 261 |
| Medical Ninjutsu | 45 |
| Ninjutsu | 2361 |
| Regeneration Techniques | 11 |
| Reincarnation Ninjutsu | 7 |
| Shape Transformation | 1 |
| Space–Time Ninjutsu | 161 |

**2455 unique ninjutsu pages.** 859 are new abilities; 1576 already existed as clan, village, nature-release or taijutsu techniques and keep their existing unlock route.

- 20 pages are natures or kekkei genkai, not techniques, and are already in the game as natures (affinities with a release token) or clan bloodlines, so they were not turned into abilities: Boil Release, Crystal Release, Dark Release, Dust Release, Explosion Release, Iburi Clan's Kekkei Genkai, Ice Release, Jūgo's Clan's Kekkei Genkai, Lava Release, Magnet Release, Rinha Clan's Kekkei Genkai, Ryūzetsu's Kekkei Genkai, Sakon and Ukon's Kekkei Genkai, Scorch Release, Shikotsumyaku, Steel Release, Storm Release, Swift Release, Typhoon Release, Wood Release.

### Pages intentionally not turned into ninjutsu

Narutopedia lists these as separate jutsu schools, not ninjutsu sub-types, so pages carrying only these classifications are out of scope here (pages that are *also* classified Ninjutsu are included above):

- **Fūinjutsu** (24 pages not tagged Ninjutsu): Dark Sealing Method, Destruction of Evil Seal, Dharma Power Seal, Eight Trigrams Sealing Style, Evil Sealing Method, Five Elements Seal, Five Elements Unseal, Four Symbols Seal, Iron Armour Seal, Kāma, Sealing Tag Barrier, Sealing Technique: Four Limbs Weighting Seal, Sealing Technique: Spirit Sealing, Sealing Technique: Vitality Seal Formation, Sealing Trap Explosion, Secret Technique: Sealing Release, Shrine Seal, Shrine Seal: Release, Strength of a Hundred Seal, Torii Seal, Uzumaki Sealing Technique, Yin Seal: Release, Zero-Tails Sealing Technique, Ōtsutsuki Teleportation
- **Juinjutsu** (10 pages not tagged Ninjutsu): Animal Cursed Seal, Cursed Seal of Earth, Cursed Seal of Heaven, Forbidden Technique: Gedō Seal, Gedō Seal: Release, Genjutsu Cursed Seal, Hyūga Main Family's Juinjutsu, Orochimaru's Juinjutsu, Prisoner Cursed Seal, Sound Four's Cursed Seals
- **Scientific Ninja Tool Techniques** (20 pages not tagged Ninjutsu): Aerobatic Strike, Burial Arm Attack, Destruction Beam, Eruption Legs, Great Burial Bullet, Great Crushing Funeral, Kāma: Great Burial Wave, Labyrinth Genjutsu, Reverse Scale, Revolving Blade Dance, Rising Thorn, Runway Booster, Scale Fragment, Self-Destruction, Shape Transformation: Pressure, Shape Transformation: Strike, Sukunahikona: God Hunter, Sukunahikona: Scale Fragment, Thorn Explosion, Violent Arm

The articles *Ninjutsu* and the sub-type articles themselves are topic pages, not techniques.

## Where the new ninjutsu go

| technique item | new ninjutsu |
|---|---:|
| `narutorpg:ninjutsu` | 529 |
| `narutorpg:bukijutsu` | 107 |
| `narutorpg:fuinjutsu` | 100 |
| `narutorpg:hiden` | 44 |
| `narutorpg:dojutsu` | 37 |
| `narutorpg:medical_ninjutsu` | 33 |
| `narutorpg:taijutsu` | 9 |

Each is generated from its infobox (classification, rank, range, class type, hand signs, nature) by the shared mapping in `tools/catalog/clans.py`: attacks become projectiles / beams / bursts by range and nature, binds and prisons hold targets, barriers raise walls, clones and space-time techniques move you, medical ninjutsu heal. Hold the item while defeating enemies to master the next technique (rank still gates them).

## All ninjutsu

| technique | rank (estimated) | class type | in game as |
|---|---|---|---|
| [Absolute: Fang Passing Fang](https://naruto.fandom.com/wiki/Absolute:_Fang_Passing_Fang) | B | Offensive | `narutorpg:absolute_fang_passing_fang` (existing) |
| [Absorption Sphere](https://naruto.fandom.com/wiki/Absorption_Sphere) | D | Defensive | `narutorpg:absorption_sphere` |
| [Accelerated Armed Revolving Heaven](https://naruto.fandom.com/wiki/Accelerated_Armed_Revolving_Heaven) | S | Offensive | `narutorpg:accelerated_armed_revolving_heaven` (existing) |
| [Acid Permeation](https://naruto.fandom.com/wiki/Acid_Permeation) | C | Offensive | `narutorpg:acid_permeation` (existing) |
| [Acidic Sludge](https://naruto.fandom.com/wiki/Acidic_Sludge) | C | Offensive | `narutorpg:acidic_sludge` |
| [Adamantine Power: Acala](https://naruto.fandom.com/wiki/Adamantine_Power:_Acala) | B | Offensive | `narutorpg:adamantine_power_acala` (existing) |
| [Adamantine Prison Wall](https://naruto.fandom.com/wiki/Adamantine_Prison_Wall) | D | Defensive, Supplementary | `narutorpg:adamantine_prison_wall` |
| [Adamantine Seal: Monkey Yang Suppression](https://naruto.fandom.com/wiki/Adamantine_Seal:_Monkey_Yang_Suppression) | B | Supplementary | `narutorpg:adamantine_seal_monkey_yang_suppression` |
| [Adamantine Sealing Chains](https://naruto.fandom.com/wiki/Adamantine_Sealing_Chains) | A | Offensive, Supplementary | `narutorpg:adamantine_sealing_chains` (existing) |
| [Adamantine Sealing Chains: Spiral Formation](https://naruto.fandom.com/wiki/Adamantine_Sealing_Chains:_Spiral_Formation) | A | Offensive | `narutorpg:adamantine_sealing_chains_spiral_formation` |
| [Adamantine Technique: Cranium Crusher](https://naruto.fandom.com/wiki/Adamantine_Technique:_Cranium_Crusher) | B | Offensive | `narutorpg:adamantine_technique_cranium_crusher` |
| [Afterimage Clone](https://naruto.fandom.com/wiki/Afterimage_Clone) | D | Supplementary | `narutorpg:afterimage_clone` |
| [Agonizing Thorn Technique (Simply Put a Kanchō)](https://naruto.fandom.com/wiki/Agonizing_Thorn_Technique_(Simply_Put_a_Kanchō)) | C | Offensive | `narutorpg:agonizing_thorn_technique_simply_put_a_kancho` (existing) |
| [Air Gold Dust Protective Wall](https://naruto.fandom.com/wiki/Air_Gold_Dust_Protective_Wall) | B | Defensive | `narutorpg:air_gold_dust_protective_wall` (existing) |
| [Air Raid Shot](https://naruto.fandom.com/wiki/Air_Raid_Shot) | C | Offensive | `narutorpg:air_raid_shot` |
| [Air Sand Protective Wall](https://naruto.fandom.com/wiki/Air_Sand_Protective_Wall) | D | Defensive | `narutorpg:air_sand_protective_wall` (existing) |
| [Akuta](https://naruto.fandom.com/wiki/Akuta) | S | Supplementary | `narutorpg:akuta` (existing) |
| [All Directions Shuriken](https://naruto.fandom.com/wiki/All_Directions_Shuriken) | A | Offensive | `narutorpg:all_directions_shuriken` |
| [All Weapons Above Heaven](https://naruto.fandom.com/wiki/All_Weapons_Above_Heaven) | D | Supplementary | `narutorpg:all_weapons_above_heaven` |
| [All is Suffering](https://naruto.fandom.com/wiki/All_is_Suffering) | B | Offensive | `narutorpg:all_is_suffering` (existing) |
| [All-Killing Ash Bones](https://naruto.fandom.com/wiki/All-Killing_Ash_Bones) | A | Offensive | `narutorpg:all_killing_ash_bones` |
| [Allied Shinobi Forces Technique](https://naruto.fandom.com/wiki/Allied_Shinobi_Forces_Technique) | B | Offensive, Defensive, Supplementary | `narutorpg:allied_shinobi_forces_technique` (existing) |
| [Altering Terrain Technique](https://naruto.fandom.com/wiki/Altering_Terrain_Technique) | C | Offensive, Supplementary | `narutorpg:altering_terrain_technique` (existing) |
| [Amanohabaya](https://naruto.fandom.com/wiki/Amanohabaya) | C | Offensive | `narutorpg:amanohabaya` (existing) |
| [Amaterasu](https://naruto.fandom.com/wiki/Amaterasu) | B | Offensive | `narutorpg:amaterasu` (existing) |
| [Amaterasu: Dual Layer](https://naruto.fandom.com/wiki/Amaterasu:_Dual_Layer) | B | Offensive | `narutorpg:amaterasu_dual_layer` (existing) |
| [Amaterasu: Flame Wrapping Fire](https://naruto.fandom.com/wiki/Amaterasu:_Flame_Wrapping_Fire) | B | Defensive | `narutorpg:amaterasu_flame_wrapping_fire` (existing) |
| [Amaterasu: Lingering Fire](https://naruto.fandom.com/wiki/Amaterasu:_Lingering_Fire) | B | Offensive, Supplementary | `narutorpg:amaterasu_lingering_fire` (existing) |
| [Amaterasu: Wailing Sky](https://naruto.fandom.com/wiki/Amaterasu:_Wailing_Sky) | B | Offensive | `narutorpg:amaterasu_wailing_sky` (existing) |
| [Ame no Ohabari](https://naruto.fandom.com/wiki/Ame_no_Ohabari) | B | Offensive | `narutorpg:ame_no_ohabari` |
| [Amenomihashira](https://naruto.fandom.com/wiki/Amenomihashira) | C | Offensive | `narutorpg:amenomihashira` |
| [Amenominaka](https://naruto.fandom.com/wiki/Amenominaka) | S | Supplementary | `narutorpg:amenominaka` |
| [Amenosubaruboshinomikoto](https://naruto.fandom.com/wiki/Amenosubaruboshinomikoto) | C | Offensive, Supplementary | `narutorpg:amenosubaruboshinomikoto` (existing) |
| [Amenosubaruboshinomikoto: Gyokkō](https://naruto.fandom.com/wiki/Amenosubaruboshinomikoto:_Gyokkō) | C | Offensive | `narutorpg:amenosubaruboshinomikoto_gyokko` (existing) |
| [Amenosubaruboshinomikoto: Ryūgū](https://naruto.fandom.com/wiki/Amenosubaruboshinomikoto:_Ryūgū) | C | Offensive | `narutorpg:amenosubaruboshinomikoto_ryugu` (existing) |
| [Amenotejikara](https://naruto.fandom.com/wiki/Amenotejikara) | A | Supplementary | `narutorpg:amenotejikara` (existing) |
| [Amenotejikara: Chidori](https://naruto.fandom.com/wiki/Amenotejikara:_Chidori) | B | Offensive | `narutorpg:amenotejikara_chidori` (existing) |
| [Amenotejikara: Instant](https://naruto.fandom.com/wiki/Amenotejikara:_Instant) | A | Offensive | `narutorpg:amenotejikara_instant` (existing) |
| [Amplification Summoning Technique](https://naruto.fandom.com/wiki/Amplification_Summoning_Technique) | B | Supplementary | `narutorpg:amplification_summoning_technique` |
| [Animal Path](https://naruto.fandom.com/wiki/Animal_Path) | B | Offensive, Defensive, Supplementary | `narutorpg:animal_path` (existing) |
| [Ankorodō Hiden Ninjutsu: Mochi Release: Infinite Dumplings](https://naruto.fandom.com/wiki/Ankorodō_Hiden_Ninjutsu:_Mochi_Release:_Infinite_Dumplings) | C | Supplementary | `narutorpg:ankorodo_hiden_ninjutsu_mochi_release_infinite_dumplings` (existing) |
| [Annihilation](https://naruto.fandom.com/wiki/Annihilation) | A | Offensive | `narutorpg:annihilation` (existing) |
| [Antlion Ninja Arts: Ephemeral](https://naruto.fandom.com/wiki/Antlion_Ninja_Arts:_Ephemeral) | S | Offensive | `narutorpg:antlion_ninja_arts_ephemeral` (existing) |
| [Anātman](https://naruto.fandom.com/wiki/Anātman) | C | Offensive | `narutorpg:anatman` |
| [Apricot Blossom Droop](https://naruto.fandom.com/wiki/Apricot_Blossom_Droop) | C | Offensive | `narutorpg:apricot_blossom_droop` (existing) |
| [Aqua Shoot](https://naruto.fandom.com/wiki/Aqua_Shoot) | C | Offensive | `narutorpg:aqua_shoot` (existing) |
| [Area Scanning Technique](https://naruto.fandom.com/wiki/Area_Scanning_Technique) | D | Supplementary | `narutorpg:area_scanning_technique` (existing) |
| [Arm Growth Technique](https://naruto.fandom.com/wiki/Arm_Growth_Technique) | D | Supplementary | `narutorpg:arm_growth_technique` |
| [Arm of Shukaku](https://naruto.fandom.com/wiki/Arm_of_Shukaku) | C | Offensive, Defensive | `narutorpg:arm_of_shukaku` |
| [Armageddon Countdown Clock](https://naruto.fandom.com/wiki/Armageddon_Countdown_Clock) | D | Supplementary | `narutorpg:armageddon_countdown_clock` |
| [Armour of Sand](https://naruto.fandom.com/wiki/Armour_of_Sand) | D | Defensive | `narutorpg:armour_of_sand` |
| [Armour of Sticky Gold](https://naruto.fandom.com/wiki/Armour_of_Sticky_Gold) | C | Defensive | `narutorpg:armour_of_sticky_gold` |
| [Armour-Eater](https://naruto.fandom.com/wiki/Armour-Eater) | D | Supplementary | `narutorpg:armour_eater` |
| [Art (jutsu)](https://naruto.fandom.com/wiki/Art_(jutsu)) | B | Offensive | `narutorpg:art_jutsu` (existing) |
| [Ascension and Fall](https://naruto.fandom.com/wiki/Ascension_and_Fall) | A | Offensive | `narutorpg:ascension_and_fall` (existing) |
| [Assimilate All Creation Technique](https://naruto.fandom.com/wiki/Assimilate_All_Creation_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:assimilate_all_creation_technique` |
| [Assimilation: Rock Blizzard](https://naruto.fandom.com/wiki/Assimilation:_Rock_Blizzard) | C | Offensive | `narutorpg:assimilation_rock_blizzard` (existing) |
| [Assimilation: Sand Binding Coffin](https://naruto.fandom.com/wiki/Assimilation:_Sand_Binding_Coffin) | C | Offensive | `narutorpg:assimilation_sand_binding_coffin` (existing) |
| [Assimilation: Sand Waterfall Funeral](https://naruto.fandom.com/wiki/Assimilation:_Sand_Waterfall_Funeral) | C | Offensive | `narutorpg:assimilation_sand_waterfall_funeral` (existing) |
| [Asura Attack](https://naruto.fandom.com/wiki/Asura_Attack) | B | Offensive | `narutorpg:asura_attack` (existing) |
| [Asura Path](https://naruto.fandom.com/wiki/Asura_Path) | B | Offensive, Defensive | `narutorpg:asura_path` (existing) |
| [Attack of the Twin Demons](https://naruto.fandom.com/wiki/Attack_of_the_Twin_Demons) | B | Offensive, Defensive, Supplementary | `narutorpg:attack_of_the_twin_demons` |
| [Auger](https://naruto.fandom.com/wiki/Auger) | C | Offensive | `narutorpg:auger` |
| [Azure Stream](https://naruto.fandom.com/wiki/Azure_Stream) | C | Offensive | `narutorpg:azure_stream` (existing) |
| [Ball of Light Technique](https://naruto.fandom.com/wiki/Ball_of_Light_Technique) | D | Supplementary | `narutorpg:ball_of_light_technique` |
| [Bando's Chakra Armour](https://naruto.fandom.com/wiki/Bando's_Chakra_Armour) | D | Defensive, Supplementary | `narutorpg:bando_s_chakra_armour` |
| [Banshō Kokuin](https://naruto.fandom.com/wiki/Banshō_Kokuin) | B | Offensive, Supplementary | `narutorpg:bansho_kokuin` |
| [Banshō Ten'in](https://naruto.fandom.com/wiki/Banshō_Ten'in) | B | Supplementary | `narutorpg:bansho_ten_in` (existing) |
| [Barrier Gate Five Seals Technique: Castle Closing Eight Gates](https://naruto.fandom.com/wiki/Barrier_Gate_Five_Seals_Technique:_Castle_Closing_Eight_Gates) | C | Defensive | `narutorpg:barrier_gate_five_seals_technique_castle_closing_eight_gates` |
| [Barrier Method Formation](https://naruto.fandom.com/wiki/Barrier_Method_Formation) | B | Offensive | `narutorpg:barrier_method_formation` |
| [Barrier Method Formation — Triple Formula](https://naruto.fandom.com/wiki/Barrier_Method_Formation_—_Triple_Formula) | C | Offensive | `narutorpg:barrier_method_formation_triple_formula` |
| [Barrier Releasing Technique](https://naruto.fandom.com/wiki/Barrier_Releasing_Technique) | C | Supplementary | `narutorpg:barrier_releasing_technique` |
| [Barrier Shattering Technique](https://naruto.fandom.com/wiki/Barrier_Shattering_Technique) | B | Supplementary | `narutorpg:barrier_shattering_technique` |
| [Barrier Talisman: Armoured Eye](https://naruto.fandom.com/wiki/Barrier_Talisman:_Armoured_Eye) | C | Defensive | `narutorpg:barrier_talisman_armoured_eye` |
| [Barrier: Canopy Method Formation](https://naruto.fandom.com/wiki/Barrier:_Canopy_Method_Formation) | B | Supplementary | `narutorpg:barrier_canopy_method_formation` |
| [Barrier: Reclose Technique](https://naruto.fandom.com/wiki/Barrier:_Reclose_Technique) | B | Supplementary | `narutorpg:barrier_reclose_technique` |
| [Barrier: Toad Gourd Prison](https://naruto.fandom.com/wiki/Barrier:_Toad_Gourd_Prison) | B | Supplementary | `narutorpg:barrier_toad_gourd_prison` |
| [Baryon Bullet Barrage](https://naruto.fandom.com/wiki/Baryon_Bullet_Barrage) | C | Offensive | `narutorpg:baryon_bullet_barrage` (existing) |
| [Baryon Mode](https://naruto.fandom.com/wiki/Baryon_Mode) | C | Offensive, Defensive, Supplementary | `narutorpg:baryon_mode` |
| [Baryon Rasengan](https://naruto.fandom.com/wiki/Baryon_Rasengan) | C | Offensive | `narutorpg:baryon_rasengan` |
| [Baryon Rasengan: Heaven Pierce](https://naruto.fandom.com/wiki/Baryon_Rasengan:_Heaven_Pierce) | C | Offensive | `narutorpg:baryon_rasengan_heaven_pierce` |
| [Baryon Tail Breaker](https://naruto.fandom.com/wiki/Baryon_Tail_Breaker) | C | Offensive, Defensive, Supplementary | `narutorpg:baryon_tail_breaker` |
| [Baryon: Scarlet Flash](https://naruto.fandom.com/wiki/Baryon:_Scarlet_Flash) | C | Offensive | `narutorpg:baryon_scarlet_flash` (existing) |
| [Bashōsen: Coil of Earth](https://naruto.fandom.com/wiki/Bashōsen:_Coil_of_Earth) | C | Offensive | `narutorpg:bashosen_coil_of_earth` (existing) |
| [Bashōsen: Coil of Fire](https://naruto.fandom.com/wiki/Bashōsen:_Coil_of_Fire) | C | Offensive | `narutorpg:bashosen_coil_of_fire` (existing) |
| [Bashōsen: Coil of Lightning](https://naruto.fandom.com/wiki/Bashōsen:_Coil_of_Lightning) | C | Offensive | `narutorpg:bashosen_coil_of_lightning` (existing) |
| [Bashōsen: Coil of Water](https://naruto.fandom.com/wiki/Bashōsen:_Coil_of_Water) | C | Offensive | `narutorpg:bashosen_coil_of_water` (existing) |
| [Bashōsen: Coil of Wind](https://naruto.fandom.com/wiki/Bashōsen:_Coil_of_Wind) | C | Offensive | `narutorpg:bashosen_coil_of_wind` (existing) |
| [Bat Controlling](https://naruto.fandom.com/wiki/Bat_Controlling) | D | Supplementary | `narutorpg:bat_controlling` |
| [Bat Controlling: Ultrasonic Mind Waves](https://naruto.fandom.com/wiki/Bat_Controlling:_Ultrasonic_Mind_Waves) | D | Supplementary | `narutorpg:bat_controlling_ultrasonic_mind_waves` |
| [Beast Human Clone](https://naruto.fandom.com/wiki/Beast_Human_Clone) | D | Supplementary | `narutorpg:beast_human_clone` (existing) |
| [Beast Sealing Technique](https://naruto.fandom.com/wiki/Beast_Sealing_Technique) | C | Supplementary | `narutorpg:beast_sealing_technique` |
| [Beast Tearing Gale Palm](https://naruto.fandom.com/wiki/Beast_Tearing_Gale_Palm) | C | Offensive, Defensive, Supplementary | `narutorpg:beast_tearing_gale_palm` (existing) |
| [Beast Tearing Palm](https://naruto.fandom.com/wiki/Beast_Tearing_Palm) | C | Offensive | `narutorpg:beast_tearing_palm` (existing) |
| [Beast-Human Needle Senbon](https://naruto.fandom.com/wiki/Beast-Human_Needle_Senbon) | C | Offensive | `narutorpg:beast_human_needle_senbon` (existing) |
| [Bee Bomb Technique](https://naruto.fandom.com/wiki/Bee_Bomb_Technique) | C | Offensive | `narutorpg:bee_bomb_technique` (existing) |
| [Bee Honey Technique](https://naruto.fandom.com/wiki/Bee_Honey_Technique) | C | Offensive, Supplementary | `narutorpg:bee_honey_technique` (existing) |
| [Beeswax Clone](https://naruto.fandom.com/wiki/Beeswax_Clone) | C | Supplementary | `narutorpg:beeswax_clone` (existing) |
| [Bell Barrier Technique](https://naruto.fandom.com/wiki/Bell_Barrier_Technique) | C | Defensive | `narutorpg:bell_barrier_technique` |
| [Bell-Flowered Lightning Blade](https://naruto.fandom.com/wiki/Bell-Flowered_Lightning_Blade) | C | Offensive | `narutorpg:bell_flowered_lightning_blade` (existing) |
| [Big Ball Rasengan](https://naruto.fandom.com/wiki/Big_Ball_Rasengan) | A | Offensive | `narutorpg:big_ball_rasengan` (existing) |
| [Big Ball Rasengan: Rising Attack](https://naruto.fandom.com/wiki/Big_Ball_Rasengan:_Rising_Attack) | C | Offensive | `narutorpg:big_ball_rasengan_rising_attack` (existing) |
| [Big Ball Rasenshuriken](https://naruto.fandom.com/wiki/Big_Ball_Rasenshuriken) | S | Offensive | `narutorpg:big_ball_rasenshuriken` (existing) |
| [Big Ball Spiralling Serial Spheres](https://naruto.fandom.com/wiki/Big_Ball_Spiralling_Serial_Spheres) | C | Offensive | `narutorpg:big_ball_spiralling_serial_spheres` |
| [Big Ball Spiralling Serial Zone Spheres](https://naruto.fandom.com/wiki/Big_Ball_Spiralling_Serial_Zone_Spheres) | A | Offensive | `narutorpg:big_ball_spiralling_serial_zone_spheres` |
| [Big Ball: Tailed Beast Rasenshuriken](https://naruto.fandom.com/wiki/Big_Ball:_Tailed_Beast_Rasenshuriken) | S | Offensive | `narutorpg:big_ball_tailed_beast_rasenshuriken` (existing) |
| [Big Truth-Seeking Ball](https://naruto.fandom.com/wiki/Big_Truth-Seeking_Ball) | A | Offensive | `narutorpg:big_truth_seeking_ball` (existing) |
| [Biguro](https://naruto.fandom.com/wiki/Biguro) | S | Offensive | `narutorpg:biguro` |
| [Binding Smoke Prison](https://naruto.fandom.com/wiki/Binding_Smoke_Prison) | D | Supplementary | `narutorpg:binding_smoke_prison` |
| [Binding Snake Glare Spell](https://naruto.fandom.com/wiki/Binding_Snake_Glare_Spell) | C | Offensive, Supplementary | `narutorpg:binding_snake_glare_spell` |
| [Black Iron Fist](https://naruto.fandom.com/wiki/Black_Iron_Fist) | B | Offensive, Defensive | `narutorpg:black_iron_fist` (existing) |
| [Black Lightning](https://naruto.fandom.com/wiki/Black_Lightning) | C | Offensive | `narutorpg:black_lightning` (existing) |
| [Black Needle Stab](https://naruto.fandom.com/wiki/Black_Needle_Stab) | B | Offensive | `narutorpg:black_needle_stab` |
| [Black Passing Secret Fang Technique Machine One Shot](https://naruto.fandom.com/wiki/Black_Passing_Secret_Fang_Technique_Machine_One_Shot) | B | Offensive | `narutorpg:black_passing_secret_fang_technique_machine_one_shot` (existing) |
| [Black Rain](https://naruto.fandom.com/wiki/Black_Rain) | C | Offensive | `narutorpg:black_rain` |
| [Black Receiver](https://naruto.fandom.com/wiki/Black_Receiver) | B | Offensive, Supplementary | `narutorpg:black_receiver` (existing) |
| [Black Secret Technique Flame Filling Flower](https://naruto.fandom.com/wiki/Black_Secret_Technique_Flame_Filling_Flower) | C | Offensive | `narutorpg:black_secret_technique_flame_filling_flower` |
| [Black Secret Technique Machine One Shot](https://naruto.fandom.com/wiki/Black_Secret_Technique_Machine_One_Shot) | B | Offensive | `narutorpg:black_secret_technique_machine_one_shot` |
| [Black Secret Technique Machine Two Shot](https://naruto.fandom.com/wiki/Black_Secret_Technique_Machine_Two_Shot) | C | Offensive | `narutorpg:black_secret_technique_machine_two_shot` |
| [Black Secret Technique Manipulated Flame Scorpion Play](https://naruto.fandom.com/wiki/Black_Secret_Technique_Manipulated_Flame_Scorpion_Play) | C | Offensive | `narutorpg:black_secret_technique_manipulated_flame_scorpion_play` |
| [Black Secret Technique Revolving Flame Arson](https://naruto.fandom.com/wiki/Black_Secret_Technique_Revolving_Flame_Arson) | C | Offensive | `narutorpg:black_secret_technique_revolving_flame_arson` |
| [Black Secret Technique — Machine Triple Shot](https://naruto.fandom.com/wiki/Black_Secret_Technique_—_Machine_Triple_Shot) | A | Offensive | `narutorpg:black_secret_technique_machine_triple_shot` |
| [Black Secret Technique: Black Iron](https://naruto.fandom.com/wiki/Black_Secret_Technique:_Black_Iron) | B | Supplementary | `narutorpg:black_secret_technique_black_iron` (existing) |
| [Black Secret Technique: Salamander](https://naruto.fandom.com/wiki/Black_Secret_Technique:_Salamander) | D | Defensive, Supplementary | `narutorpg:black_secret_technique_salamander` |
| [Black Secret Technique: Three Great Kyōgen](https://naruto.fandom.com/wiki/Black_Secret_Technique:_Three_Great_Kyōgen) | C | Offensive | `narutorpg:black_secret_technique_three_great_kyogen` |
| [Black Secret Technique: Three Great Tragedies](https://naruto.fandom.com/wiki/Black_Secret_Technique:_Three_Great_Tragedies) | C | Offensive | `narutorpg:black_secret_technique_three_great_tragedies` |
| [Black Spider Lily](https://naruto.fandom.com/wiki/Black_Spider_Lily) | C | Offensive, Supplementary | `narutorpg:black_spider_lily` (existing) |
| [Black Tornado](https://naruto.fandom.com/wiki/Black_Tornado) | B | Offensive | `narutorpg:black_tornado` (existing) |
| [Blade of Wind](https://naruto.fandom.com/wiki/Blade_of_Wind) | A | Offensive | `narutorpg:blade_of_wind` (existing) |
| [Blade of Wind Barrage](https://naruto.fandom.com/wiki/Blade_of_Wind_Barrage) | C | Offensive | `narutorpg:blade_of_wind_barrage` (existing) |
| [Blade of Wind Hurricane](https://naruto.fandom.com/wiki/Blade_of_Wind_Hurricane) | C | Offensive | `narutorpg:blade_of_wind_hurricane` (existing) |
| [Blasting Ice Crystal](https://naruto.fandom.com/wiki/Blasting_Ice_Crystal) | B | Offensive | `narutorpg:blasting_ice_crystal` (existing) |
| [Blaze Release: Dragon Flame Release Song Technique](https://naruto.fandom.com/wiki/Blaze_Release:_Dragon_Flame_Release_Song_Technique) | B | Offensive | `narutorpg:blaze_release_dragon_flame_release_song_technique` (existing) |
| [Blaze Release: Flame Formation Wall Technique](https://naruto.fandom.com/wiki/Blaze_Release:_Flame_Formation_Wall_Technique) | B | Offensive, Defensive | `narutorpg:blaze_release_flame_formation_wall_technique` (existing) |
| [Blaze Release: Great Fireball Technique](https://naruto.fandom.com/wiki/Blaze_Release:_Great_Fireball_Technique) | B | Offensive | `narutorpg:blaze_release_great_fireball_technique` (existing) |
| [Blaze Release: Honoikazuchi](https://naruto.fandom.com/wiki/Blaze_Release:_Honoikazuchi) | B | Offensive, Defensive | `narutorpg:blaze_release_honoikazuchi` (existing) |
| [Blaze Release: Kagutsuchi](https://naruto.fandom.com/wiki/Blaze_Release:_Kagutsuchi) | B | Offensive, Defensive | `narutorpg:blaze_release_kagutsuchi` (existing) |
| [Blaze Release: Kagutsuchi — Flying Flame](https://naruto.fandom.com/wiki/Blaze_Release:_Kagutsuchi_—_Flying_Flame) | B | Offensive | `narutorpg:blaze_release_kagutsuchi_flying_flame` (existing) |
| [Blaze Release: Kagutsuchi — Heaven Step](https://naruto.fandom.com/wiki/Blaze_Release:_Kagutsuchi_—_Heaven_Step) | B | Offensive | `narutorpg:blaze_release_kagutsuchi_heaven_step` (existing) |
| [Blaze Release: Kagutsuchi — Porcupine](https://naruto.fandom.com/wiki/Blaze_Release:_Kagutsuchi_—_Porcupine) | B | Offensive | `narutorpg:blaze_release_kagutsuchi_porcupine` (existing) |
| [Blaze Release: Susanoo Kagutsuchi](https://naruto.fandom.com/wiki/Blaze_Release:_Susanoo_Kagutsuchi) | B | Offensive | `narutorpg:blaze_release_susanoo_kagutsuchi` (existing) |
| [Blaze Release: Susanoo Ōdachi](https://naruto.fandom.com/wiki/Blaze_Release:_Susanoo_Ōdachi) | A | Offensive | `narutorpg:blaze_release_susanoo_odachi` (existing) |
| [Blaze Release: Yasaka Magatama](https://naruto.fandom.com/wiki/Blaze_Release:_Yasaka_Magatama) | B | Offensive | `narutorpg:blaze_release_yasaka_magatama` (existing) |
| [Blazing Lightning Kunai](https://naruto.fandom.com/wiki/Blazing_Lightning_Kunai) | C | Offensive | `narutorpg:blazing_lightning_kunai` (existing) |
| [Blazing Shuriken Dance](https://naruto.fandom.com/wiki/Blazing_Shuriken_Dance) | B | Offensive, Defensive | `narutorpg:blazing_shuriken_dance` (existing) |
| [Blinding Bubbles Technique](https://naruto.fandom.com/wiki/Blinding_Bubbles_Technique) | C | Offensive, Supplementary | `narutorpg:blinding_bubbles_technique` (existing) |
| [Blinding Wide Sand Formation: Noise](https://naruto.fandom.com/wiki/Blinding_Wide_Sand_Formation:_Noise) | C | Supplementary | `narutorpg:blinding_wide_sand_formation_noise` |
| [Blocking Technique Absorption Seal](https://naruto.fandom.com/wiki/Blocking_Technique_Absorption_Seal) | B | Defensive | `narutorpg:blocking_technique_absorption_seal` (existing) |
| [Blood Dragon Ascension](https://naruto.fandom.com/wiki/Blood_Dragon_Ascension) | B | Offensive | `narutorpg:blood_dragon_ascension` (existing) |
| [Blood Dragon Bullet](https://naruto.fandom.com/wiki/Blood_Dragon_Bullet) | B | Offensive | `narutorpg:blood_dragon_bullet` |
| [Blood Needle](https://naruto.fandom.com/wiki/Blood_Needle) | C | Offensive | `narutorpg:blood_needle` |
| [Blood Transformation Technique](https://naruto.fandom.com/wiki/Blood_Transformation_Technique) | B | Supplementary | `narutorpg:blood_transformation_technique` (existing) |
| [Bloody Mist Absorption Technique](https://naruto.fandom.com/wiki/Bloody_Mist_Absorption_Technique) | D | Supplementary | `narutorpg:bloody_mist_absorption_technique` (existing) |
| [Bloody Mist Sword Art: Bone Mutilation](https://naruto.fandom.com/wiki/Bloody_Mist_Sword_Art:_Bone_Mutilation) | C | Offensive, Supplementary | `narutorpg:bloody_mist_sword_art_bone_mutilation` |
| [Body Alteration](https://naruto.fandom.com/wiki/Body_Alteration) | D | Supplementary | `narutorpg:body_alteration` |
| [Body Controlling Technique](https://naruto.fandom.com/wiki/Body_Controlling_Technique) | B | Supplementary | `narutorpg:body_controlling_technique` |
| [Body Elimination Technique](https://naruto.fandom.com/wiki/Body_Elimination_Technique) | D | Supplementary | `narutorpg:body_elimination_technique` |
| [Body Flicker Technique](https://naruto.fandom.com/wiki/Body_Flicker_Technique) | D | Supplementary | `narutorpg:body_flicker_technique` (existing) |
| [Body Flicker Technique: Flame Slash](https://naruto.fandom.com/wiki/Body_Flicker_Technique:_Flame_Slash) | C | Offensive | `narutorpg:body_flicker_technique_flame_slash` (existing) |
| [Body Fluid Shedding Technique](https://naruto.fandom.com/wiki/Body_Fluid_Shedding_Technique) | D | Supplementary | `narutorpg:body_fluid_shedding_technique` |
| [Body Oxidation Technique](https://naruto.fandom.com/wiki/Body_Oxidation_Technique) | B | Offensive | `narutorpg:body_oxidation_technique` (existing) |
| [Body Pathway Derangement](https://naruto.fandom.com/wiki/Body_Pathway_Derangement) | A | Supplementary | `narutorpg:body_pathway_derangement` (existing) |
| [Body Recreation Technique](https://naruto.fandom.com/wiki/Body_Recreation_Technique) | D | Supplementary | `narutorpg:body_recreation_technique` |
| [Body Replacement Technique](https://naruto.fandom.com/wiki/Body_Replacement_Technique) | E | Supplementary | `narutorpg:substitution_jutsu` (existing) |
| [Body Revival Technique](https://naruto.fandom.com/wiki/Body_Revival_Technique) | D | Supplementary | `narutorpg:body_revival_technique` |
| [Boil Release](https://naruto.fandom.com/wiki/Boil_Release) | B | - | `narutorpg:boil_release` (existing) |
| [Boil Release: Flower Repentance Technique](https://naruto.fandom.com/wiki/Boil_Release:_Flower_Repentance_Technique) | B | Offensive | `narutorpg:boil_release_flower_repentance_technique` (existing) |
| [Boil Release: Skilled Mist Technique](https://naruto.fandom.com/wiki/Boil_Release:_Skilled_Mist_Technique) | B | Offensive, Supplementary | `narutorpg:boil_release_skilled_mist_technique` (existing) |
| [Boil Release: Steam Explosion](https://naruto.fandom.com/wiki/Boil_Release:_Steam_Explosion) | B | Offensive | `narutorpg:boil_release_steam_explosion` (existing) |
| [Boil Release: Unrivalled Strength](https://naruto.fandom.com/wiki/Boil_Release:_Unrivalled_Strength) | B | Supplementary | `narutorpg:boil_release_unrivalled_strength` (existing) |
| [Bolting Blossom](https://naruto.fandom.com/wiki/Bolting_Blossom) | B | Offensive | `narutorpg:bolting_blossom` (existing) |
| [Bomb Setting Huge Success!](https://naruto.fandom.com/wiki/Bomb_Setting_Huge_Success!) | C | Offensive | `narutorpg:bomb_setting_huge_success` (existing) |
| [Booby Trap](https://naruto.fandom.com/wiki/Booby_Trap) | C | Offensive | `narutorpg:booby_trap` |
| [Boruto Rush](https://naruto.fandom.com/wiki/Boruto_Rush) | C | Offensive | `narutorpg:boruto_rush` (existing) |
| [Boruto Stream](https://naruto.fandom.com/wiki/Boruto_Stream) | D | Supplementary | `narutorpg:boruto_stream` (existing) |
| [Boruto Stream Rasengan](https://naruto.fandom.com/wiki/Boruto_Stream_Rasengan) | C | Offensive | `narutorpg:boruto_stream_rasengan` (existing) |
| [Boruto Strike](https://naruto.fandom.com/wiki/Boruto_Strike) | C | Offensive | `narutorpg:boruto_strike` (existing) |
| [Bouncy Bouncy Technique](https://naruto.fandom.com/wiki/Bouncy_Bouncy_Technique) | B | Supplementary | `narutorpg:bouncy_bouncy_technique` |
| [Broadaxe](https://naruto.fandom.com/wiki/Broadaxe) | A | Offensive | `narutorpg:broadaxe` (existing) |
| [Bubble Clone Technique](https://naruto.fandom.com/wiki/Bubble_Clone_Technique) | D | Supplementary | `narutorpg:bubble_clone_technique` (existing) |
| [Bubblegum Ninjutsu](https://naruto.fandom.com/wiki/Bubblegum_Ninjutsu) | C | Offensive, Supplementary | `narutorpg:bubblegum_ninjutsu` |
| [Burning Legs](https://naruto.fandom.com/wiki/Burning_Legs) | B | Offensive | `narutorpg:burning_legs` (existing) |
| [Butterfly Mode](https://naruto.fandom.com/wiki/Butterfly_Mode) | C | Supplementary | `narutorpg:butterfly_mode` (existing) |
| [Bō Shadow Clone Technique](https://naruto.fandom.com/wiki/Bō_Shadow_Clone_Technique) | C | Offensive | `narutorpg:bo_shadow_clone_technique` |
| [Bōjutsu: Three-Section Staff](https://naruto.fandom.com/wiki/Bōjutsu:_Three-Section_Staff) | C | Offensive, Defensive | `narutorpg:bojutsu_three_section_staff` |
| [C0](https://naruto.fandom.com/wiki/C0) | B | Offensive | `narutorpg:c0` (existing) |
| [C1](https://naruto.fandom.com/wiki/C1) | B | Offensive, Supplementary, Defensive | `narutorpg:c1` (existing) |
| [C2](https://naruto.fandom.com/wiki/C2) | B | Offensive, Defensive, Supplementary | `narutorpg:c2` (existing) |
| [C3](https://naruto.fandom.com/wiki/C3) | B | Offensive | `narutorpg:c3` (existing) |
| [C4](https://naruto.fandom.com/wiki/C4) | B | Offensive | `narutorpg:c4` (existing) |
| [Caged Heat Arson Prison](https://naruto.fandom.com/wiki/Caged_Heat_Arson_Prison) | C | Offensive | `narutorpg:caged_heat_arson_prison` (existing) |
| [Calamity Flame](https://naruto.fandom.com/wiki/Calamity_Flame) | B | Offensive, Defensive | `narutorpg:calamity_flame` (existing) |
| [Calling on Tears Technique](https://naruto.fandom.com/wiki/Calling_on_Tears_Technique) | C | Offensive, Supplementary | `narutorpg:calling_on_tears_technique` |
| [Calorie Control](https://naruto.fandom.com/wiki/Calorie_Control) | C | Supplementary | `narutorpg:calorie_control` (existing) |
| [Car Mode](https://naruto.fandom.com/wiki/Car_Mode) | D | Supplementary | `narutorpg:car_mode` |
| [Carbon Armour](https://naruto.fandom.com/wiki/Carbon_Armour) | C | Offensive, Defensive | `narutorpg:carbon_armour` |
| [Carbon Control](https://naruto.fandom.com/wiki/Carbon_Control) | C | Offensive, Defensive, Supplementary | `narutorpg:carbon_control` |
| [Carbon Ring](https://naruto.fandom.com/wiki/Carbon_Ring) | C | Offensive, Defensive | `narutorpg:carbon_ring` |
| [Carbon Whip](https://naruto.fandom.com/wiki/Carbon_Whip) | C | Offensive, Defensive | `narutorpg:carbon_whip` |
| [Carpet Bombing](https://naruto.fandom.com/wiki/Carpet_Bombing) | B | Offensive | `narutorpg:carpet_bombing` (existing) |
| [Casualty Puppet](https://naruto.fandom.com/wiki/Casualty_Puppet) | S | - | `narutorpg:casualty_puppet` (existing) |
| [Cat Chakra Claw](https://naruto.fandom.com/wiki/Cat_Chakra_Claw) | C | Offensive | `narutorpg:cat_chakra_claw` (existing) |
| [Cat Covering](https://naruto.fandom.com/wiki/Cat_Covering) | C | Supplementary | `narutorpg:cat_covering` (existing) |
| [Cat Fire Bowl](https://naruto.fandom.com/wiki/Cat_Fire_Bowl) | C | Offensive | `narutorpg:cat_fire_bowl` (existing) |
| [Cat Flame Roaring Fire](https://naruto.fandom.com/wiki/Cat_Flame_Roaring_Fire) | C | Offensive | `narutorpg:cat_flame_roaring_fire` (existing) |
| [Cat Food Clone](https://naruto.fandom.com/wiki/Cat_Food_Clone) | D | Supplementary | `narutorpg:cat_food_clone` |
| [Cat God Possession: Monster Cat Beckoning Technique](https://naruto.fandom.com/wiki/Cat_God_Possession:_Monster_Cat_Beckoning_Technique) | C | Offensive, Supplementary | `narutorpg:cat_god_possession_monster_cat_beckoning_technique` (existing) |
| [Cat Surprise](https://naruto.fandom.com/wiki/Cat_Surprise) | C | Supplementary | `narutorpg:cat_surprise` (existing) |
| [Cell Activation Technique](https://naruto.fandom.com/wiki/Cell_Activation_Technique) | D | Supplementary | `narutorpg:cell_activation_technique` |
| [Cellular Regeneration Absorption](https://naruto.fandom.com/wiki/Cellular_Regeneration_Absorption) | A | Offensive, Supplementary | `narutorpg:cellular_regeneration_absorption` (existing) |
| [Cellular Regeneration Ejection](https://naruto.fandom.com/wiki/Cellular_Regeneration_Ejection) | A | Supplementary | `narutorpg:cellular_regeneration_ejection` (existing) |
| [Certain-Kill Ice Spears](https://naruto.fandom.com/wiki/Certain-Kill_Ice_Spears) | B | Offensive | `narutorpg:certain_kill_ice_spears` (existing) |
| [Chain](https://naruto.fandom.com/wiki/Chain) | A | Offensive, Supplementary | `narutorpg:chain` (existing) |
| [Chain Pull](https://naruto.fandom.com/wiki/Chain_Pull) | A | Offensive | `narutorpg:chain_pull` (existing) |
| [Chain Rope](https://naruto.fandom.com/wiki/Chain_Rope) | A | Offensive | `narutorpg:chain_rope` (existing) |
| [Chained Heaven](https://naruto.fandom.com/wiki/Chained_Heaven) | A | Offensive | `narutorpg:chained_heaven` (existing) |
| [Chained Prison Explosion](https://naruto.fandom.com/wiki/Chained_Prison_Explosion) | C | Offensive | `narutorpg:chained_prison_explosion` (existing) |
| [Chakra Absorbing Snakes](https://naruto.fandom.com/wiki/Chakra_Absorbing_Snakes) | C | Offensive | `narutorpg:chakra_absorbing_snakes` |
| [Chakra Blade: Straight Line](https://naruto.fandom.com/wiki/Chakra_Blade:_Straight_Line) | C | Offensive | `narutorpg:chakra_blade_straight_line` |
| [Chakra Blade: Wind Sword](https://naruto.fandom.com/wiki/Chakra_Blade:_Wind_Sword) | C | Offensive | `narutorpg:chakra_blade_wind_sword` (existing) |
| [Chakra Burning](https://naruto.fandom.com/wiki/Chakra_Burning) | A | Offensive | `narutorpg:chakra_burning` |
| [Chakra Control: Bind](https://naruto.fandom.com/wiki/Chakra_Control:_Bind) | C | Supplementary | `narutorpg:chakra_control_bind` |
| [Chakra Disguise](https://naruto.fandom.com/wiki/Chakra_Disguise) | D | Supplementary | `narutorpg:chakra_disguise` |
| [Chakra Draining Seal](https://naruto.fandom.com/wiki/Chakra_Draining_Seal) | C | Supplementary | `narutorpg:chakra_draining_seal` |
| [Chakra Edible Creation](https://naruto.fandom.com/wiki/Chakra_Edible_Creation) | B | Offensive, Supplementary | `narutorpg:chakra_edible_creation` (existing) |
| [Chakra Enhancing Music](https://naruto.fandom.com/wiki/Chakra_Enhancing_Music) | D | Supplementary | `narutorpg:chakra_enhancing_music` (existing) |
| [Chakra Entity](https://naruto.fandom.com/wiki/Chakra_Entity) | D | Supplementary | `narutorpg:chakra_entity` |
| [Chakra Hair Trap Technique](https://naruto.fandom.com/wiki/Chakra_Hair_Trap_Technique) | D | Supplementary | `narutorpg:chakra_hair_trap_technique` |
| [Chakra Levitation Technique](https://naruto.fandom.com/wiki/Chakra_Levitation_Technique) | D | Supplementary | `narutorpg:chakra_levitation_technique` (existing) |
| [Chakra Liquid](https://naruto.fandom.com/wiki/Chakra_Liquid) | C | Offensive, Supplementary | `narutorpg:chakra_liquid` |
| [Chakra Lock-On](https://naruto.fandom.com/wiki/Chakra_Lock-On) | D | Supplementary | `narutorpg:chakra_lock_on` |
| [Chakra Needle](https://naruto.fandom.com/wiki/Chakra_Needle) | C | Offensive, Supplementary | `narutorpg:chakra_needle` |
| [Chakra Scalpel](https://naruto.fandom.com/wiki/Chakra_Scalpel) | C | Offensive, Supplementary | `narutorpg:chakra_scalpel` |
| [Chakra Scalpel Snake Crush](https://naruto.fandom.com/wiki/Chakra_Scalpel_Snake_Crush) | B | Offensive | `narutorpg:chakra_scalpel_snake_crush` (existing) |
| [Chakra Scalpel: Cruelty](https://naruto.fandom.com/wiki/Chakra_Scalpel:_Cruelty) | C | Offensive | `narutorpg:chakra_scalpel_cruelty` |
| [Chakra Scalpel: Destruction](https://naruto.fandom.com/wiki/Chakra_Scalpel:_Destruction) | C | Offensive | `narutorpg:chakra_scalpel_destruction` |
| [Chakra Scalpel: Sever](https://naruto.fandom.com/wiki/Chakra_Scalpel:_Sever) | C | Offensive | `narutorpg:chakra_scalpel_sever` |
| [Chakra Shock Slash](https://naruto.fandom.com/wiki/Chakra_Shock_Slash) | C | Offensive | `narutorpg:chakra_shock_slash` |
| [Chakra Shockwave Slash](https://naruto.fandom.com/wiki/Chakra_Shockwave_Slash) | C | Offensive | `narutorpg:chakra_shockwave_slash` |
| [Chakra Slicers](https://naruto.fandom.com/wiki/Chakra_Slicers) | C | Offensive | `narutorpg:chakra_slicers` (existing) |
| [Chakra Suppression Technique](https://naruto.fandom.com/wiki/Chakra_Suppression_Technique) | D | Supplementary | `narutorpg:chakra_suppression_technique` (existing) |
| [Chakra Sword: Great Intersecting Wound](https://naruto.fandom.com/wiki/Chakra_Sword:_Great_Intersecting_Wound) | B | Offensive | `narutorpg:chakra_sword_great_intersecting_wound` |
| [Chakra Threads (Fūma)](https://naruto.fandom.com/wiki/Chakra_Threads_(Fūma)) | S | Offensive | `narutorpg:chakra_threads_fuma` (existing) |
| [Chakra Transfer Technique](https://naruto.fandom.com/wiki/Chakra_Transfer_Technique) | S | Supplementary | `narutorpg:chakra_transfer_technique` (existing) |
| [Character Bind Technique](https://naruto.fandom.com/wiki/Character_Bind_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:character_bind_technique` (existing) |
| [Charge! Konohamaru Army Corps!](https://naruto.fandom.com/wiki/Charge!_Konohamaru_Army_Corps!) | B | Offensive | `narutorpg:charge_konohamaru_army_corps` |
| [Charge! Konohamaru Platoon](https://naruto.fandom.com/wiki/Charge!_Konohamaru_Platoon) | B | Offensive | `narutorpg:charge_konohamaru_platoon` (existing) |
| [Charging Bullet](https://naruto.fandom.com/wiki/Charging_Bullet) | C | Offensive | `narutorpg:charging_bullet` |
| [Cherry Blossom Clone Technique](https://naruto.fandom.com/wiki/Cherry_Blossom_Clone_Technique) | D | Supplementary | `narutorpg:cherry_blossom_clone_technique` |
| [Cherry Blossom Wild Dance: Chidori](https://naruto.fandom.com/wiki/Cherry_Blossom_Wild_Dance:_Chidori) | C | Offensive | `narutorpg:cherry_blossom_wild_dance_chidori` (existing) |
| [Chibaku Tensei](https://naruto.fandom.com/wiki/Chibaku_Tensei) | B | Offensive, Supplementary | `narutorpg:chibaku_tensei` (existing) |
| [Chibaku Tensei: Heavenly Fall](https://naruto.fandom.com/wiki/Chibaku_Tensei:_Heavenly_Fall) | B | Offensive | `narutorpg:chibaku_tensei_heavenly_fall` (existing) |
| [Chibaku Tensei: Kirin Twin Birth](https://naruto.fandom.com/wiki/Chibaku_Tensei:_Kirin_Twin_Birth) | B | Offensive | `narutorpg:chibaku_tensei_kirin_twin_birth` (existing) |
| [Chidori](https://naruto.fandom.com/wiki/Chidori) | A | Offensive | `narutorpg:chidori` (existing) |
| [Chidori Current](https://naruto.fandom.com/wiki/Chidori_Current) | A | Offensive, Defensive | `narutorpg:chidori_current` (existing) |
| [Chidori Current: Black Heaven Collision](https://naruto.fandom.com/wiki/Chidori_Current:_Black_Heaven_Collision) | C | Offensive | `narutorpg:chidori_current_black_heaven_collision` (existing) |
| [Chidori Katana: Crescent Shadow](https://naruto.fandom.com/wiki/Chidori_Katana:_Crescent_Shadow) | C | Offensive | `narutorpg:chidori_katana_crescent_shadow` (existing) |
| [Chidori Light Sword](https://naruto.fandom.com/wiki/Chidori_Light_Sword) | C | Offensive, Defensive | `narutorpg:chidori_light_sword` (existing) |
| [Chidori Senbon](https://naruto.fandom.com/wiki/Chidori_Senbon) | A | Offensive | `narutorpg:chidori_senbon` (existing) |
| [Chidori Sharp Spear](https://naruto.fandom.com/wiki/Chidori_Sharp_Spear) | A | Offensive | `narutorpg:chidori_sharp_spear` (existing) |
| [Chidori Sharp Spear: Heaven Step](https://naruto.fandom.com/wiki/Chidori_Sharp_Spear:_Heaven_Step) | C | Offensive | `narutorpg:chidori_sharp_spear_heaven_step` (existing) |
| [Chidori Sharp Spear: Lightning Flower](https://naruto.fandom.com/wiki/Chidori_Sharp_Spear:_Lightning_Flower) | C | Offensive | `narutorpg:chidori_sharp_spear_lightning_flower` (existing) |
| [Chidori True Spear](https://naruto.fandom.com/wiki/Chidori_True_Spear) | C | Offensive | `narutorpg:chidori_true_spear` (existing) |
| [Chidori: Chakra Blade](https://naruto.fandom.com/wiki/Chidori:_Chakra_Blade) | C | Offensive | `narutorpg:chidori_chakra_blade` (existing) |
| [Chidori: Cry of the Thundergod](https://naruto.fandom.com/wiki/Chidori:_Cry_of_the_Thundergod) | C | Offensive | `narutorpg:chidori_cry_of_the_thundergod` (existing) |
| [Chidori: Fall](https://naruto.fandom.com/wiki/Chidori:_Fall) | C | Offensive | `narutorpg:chidori_fall` (existing) |
| [Chidori: Lightning Strike](https://naruto.fandom.com/wiki/Chidori:_Lightning_Strike) | C | Offensive | `narutorpg:chidori_lightning_strike` (existing) |
| [Chidori: Star Crusher](https://naruto.fandom.com/wiki/Chidori:_Star_Crusher) | B | Offensive | `narutorpg:chidori_star_crusher` (existing) |
| [Chidori: Thunder](https://naruto.fandom.com/wiki/Chidori:_Thunder) | C | Offensive | `narutorpg:chidori_thunder` (existing) |
| [Chidori: Thunderclap](https://naruto.fandom.com/wiki/Chidori:_Thunderclap) | C | Offensive | `narutorpg:chidori_thunderclap` (existing) |
| [Chimera Technique](https://naruto.fandom.com/wiki/Chimera_Technique) | S | Offensive, Supplementary | `narutorpg:chimera_technique` |
| [Claw Creation Technique](https://naruto.fandom.com/wiki/Claw_Creation_Technique) | C | Offensive, Supplementary | `narutorpg:claw_creation_technique` |
| [Clawing Swift Wolf Fang](https://naruto.fandom.com/wiki/Clawing_Swift_Wolf_Fang) | B | Offensive, Defensive | `narutorpg:clawing_swift_wolf_fang` (existing) |
| [Clay Clone](https://naruto.fandom.com/wiki/Clay_Clone) | C | Supplementary | `narutorpg:clay_clone` (existing) |
| [Climbing Sparrow](https://naruto.fandom.com/wiki/Climbing_Sparrow) | C | Offensive | `narutorpg:climbing_sparrow` (existing) |
| [Cloak of Invisibility Technique](https://naruto.fandom.com/wiki/Cloak_of_Invisibility_Technique) | E | Supplementary | `narutorpg:cloak_of_invisibility_technique` |
| [Clone Body Blow](https://naruto.fandom.com/wiki/Clone_Body_Blow) | A | Offensive | `narutorpg:clone_body_blow` (existing) |
| [Clone Flicker](https://naruto.fandom.com/wiki/Clone_Flicker) | C | Offensive | `narutorpg:clone_flicker` |
| [Clone Flying Arrow](https://naruto.fandom.com/wiki/Clone_Flying_Arrow) | C | Offensive | `narutorpg:clone_flying_arrow` |
| [Clone Great Explosion](https://naruto.fandom.com/wiki/Clone_Great_Explosion) | A | Offensive | `narutorpg:clone_great_explosion` |
| [Clone Ladder](https://naruto.fandom.com/wiki/Clone_Ladder) | C | Offensive | `narutorpg:clone_ladder` (existing) |
| [Clone Lasso](https://naruto.fandom.com/wiki/Clone_Lasso) | C | Offensive | `narutorpg:clone_lasso` (existing) |
| [Clone Snakes](https://naruto.fandom.com/wiki/Clone_Snakes) | D | Supplementary | `narutorpg:clone_snakes` |
| [Clone Snakes: Serpent Technique](https://naruto.fandom.com/wiki/Clone_Snakes:_Serpent_Technique) | C | Offensive | `narutorpg:clone_snakes_serpent_technique` |
| [Clone Spinning Heel Drop](https://naruto.fandom.com/wiki/Clone_Spinning_Heel_Drop) | C | Offensive | `narutorpg:clone_spinning_heel_drop` (existing) |
| [Clone Technique](https://naruto.fandom.com/wiki/Clone_Technique) | E | Supplementary | `narutorpg:clone_technique` (existing) |
| [Clone Trap](https://naruto.fandom.com/wiki/Clone_Trap) | D | Supplementary | `narutorpg:clone_trap` (existing) |
| [Cloth Binding Technique](https://naruto.fandom.com/wiki/Cloth_Binding_Technique) | C | Supplementary | `narutorpg:cloth_binding_technique` |
| [Cloud-Style Crescent Moon Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Crescent_Moon_Beheading) | C | Offensive | `narutorpg:cloud_style_crescent_moon_beheading` (existing) |
| [Cloud-Style Deception Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Deception_Beheading) | C | Offensive | `narutorpg:cloud_style_deception_beheading` |
| [Cloud-Style Flame Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Flame_Beheading) | C | Offensive | `narutorpg:cloud_style_flame_beheading` (existing) |
| [Cloud-Style Flying Sword](https://naruto.fandom.com/wiki/Cloud-Style_Flying_Sword) | C | Offensive | `narutorpg:cloud_style_flying_sword` (existing) |
| [Cloud-Style Front Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Front_Beheading) | C | Offensive | `narutorpg:cloud_style_front_beheading` (existing) |
| [Cloud-Style Reverse Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Reverse_Beheading) | C | Offensive | `narutorpg:cloud_style_reverse_beheading` (existing) |
| [Cloud-Style Rising Dragon](https://naruto.fandom.com/wiki/Cloud-Style_Rising_Dragon) | C | Offensive | `narutorpg:cloud_style_rising_dragon` (existing) |
| [Club Technique](https://naruto.fandom.com/wiki/Club_Technique) | C | Offensive | `narutorpg:club_technique` |
| [Cocoon](https://naruto.fandom.com/wiki/Cocoon) | D | Defensive, Supplementary | `narutorpg:cocoon` (existing) |
| [Cold Attack of the Demon Blade](https://naruto.fandom.com/wiki/Cold_Attack_of_the_Demon_Blade) | B | Offensive | `narutorpg:cold_attack_of_the_demon_blade` (existing) |
| [Cold Needles](https://naruto.fandom.com/wiki/Cold_Needles) | C | Offensive | `narutorpg:cold_needles` (existing) |
| [Collaboration: Ultimate Art](https://naruto.fandom.com/wiki/Collaboration:_Ultimate_Art) | B | Offensive | `narutorpg:collaboration_ultimate_art` (existing) |
| [Colour Course Change](https://naruto.fandom.com/wiki/Colour_Course_Change) | D | Supplementary | `narutorpg:colour_course_change` |
| [Combination Art](https://naruto.fandom.com/wiki/Combination_Art) | B | Offensive | `narutorpg:combination_art` (existing) |
| [Combination Secret Technique](https://naruto.fandom.com/wiki/Combination_Secret_Technique) | B | Offensive | `narutorpg:combination_secret_technique` (existing) |
| [Combination Transformation](https://naruto.fandom.com/wiki/Combination_Transformation) | B | Supplementary | `narutorpg:combination_transformation` |
| [Combined Killing Art](https://naruto.fandom.com/wiki/Combined_Killing_Art) | B | Offensive, Defensive | `narutorpg:combined_killing_art` (existing) |
| [Combo Attack: Shinra Tensei](https://naruto.fandom.com/wiki/Combo_Attack:_Shinra_Tensei) | B | Offensive | `narutorpg:combo_attack_shinra_tensei` (existing) |
| [Combo Small Birds](https://naruto.fandom.com/wiki/Combo_Small_Birds) | C | Offensive | `narutorpg:combo_small_birds` |
| [Coming of the Lightning Fire Phoenix Technique](https://naruto.fandom.com/wiki/Coming_of_the_Lightning_Fire_Phoenix_Technique) | B | Offensive, Supplementary | `narutorpg:coming_of_the_lightning_fire_phoenix_technique` (existing) |
| [Complete Body Susanoo: Kishin](https://naruto.fandom.com/wiki/Complete_Body_Susanoo:_Kishin) | B | Offensive | `narutorpg:complete_body_susanoo_kishin` |
| [Compression Rasengan](https://naruto.fandom.com/wiki/Compression_Rasengan) | S | Offensive | `narutorpg:compression_rasengan` |
| [Concealed Sand Picture Cat](https://naruto.fandom.com/wiki/Concealed_Sand_Picture_Cat) | D | Supplementary | `narutorpg:concealed_sand_picture_cat` |
| [Concealment](https://naruto.fandom.com/wiki/Concealment) | C | Offensive | `narutorpg:concealment` (existing) |
| [Conch Cannon](https://naruto.fandom.com/wiki/Conch_Cannon) | B | Offensive | `narutorpg:conch_cannon` |
| [Conch Whip](https://naruto.fandom.com/wiki/Conch_Whip) | B | Offensive, Supplementary | `narutorpg:conch_whip` |
| [Concussive Chakra Cannon](https://naruto.fandom.com/wiki/Concussive_Chakra_Cannon) | C | Offensive | `narutorpg:concussive_chakra_cannon` (existing) |
| [Confinement Barrier](https://naruto.fandom.com/wiki/Confinement_Barrier) | B | Supplementary | `narutorpg:confinement_barrier` |
| [Continuous Tailed Beast Balls](https://naruto.fandom.com/wiki/Continuous_Tailed_Beast_Balls) | S | Offensive | `narutorpg:continuous_tailed_beast_balls` |
| [Contract Seal](https://naruto.fandom.com/wiki/Contract_Seal) | C | Supplementary | `narutorpg:contract_seal` |
| [Cooperation Tailed Beast Ball](https://naruto.fandom.com/wiki/Cooperation_Tailed_Beast_Ball) | B | Offensive | `narutorpg:cooperation_tailed_beast_ball` |
| [Copy Ninjutsu](https://naruto.fandom.com/wiki/Copy_Ninjutsu) | B | Offensive | `narutorpg:copy_ninjutsu` (existing) |
| [Coral Palm](https://naruto.fandom.com/wiki/Coral_Palm) | C | Offensive, Supplementary | `narutorpg:coral_palm` (existing) |
| [Coral Ripples Rising](https://naruto.fandom.com/wiki/Coral_Ripples_Rising) | C | Offensive | `narutorpg:coral_ripples_rising` |
| [Coral Rising Attack](https://naruto.fandom.com/wiki/Coral_Rising_Attack) | C | Offensive | `narutorpg:coral_rising_attack` |
| [Corpse Clone Technique](https://naruto.fandom.com/wiki/Corpse_Clone_Technique) | S | Supplementary | `narutorpg:corpse_clone_technique` |
| [Crane Wing Formation](https://naruto.fandom.com/wiki/Crane_Wing_Formation) | B | Supplementary | `narutorpg:crane_wing_formation` (existing) |
| [Crane Wing Formation (Eight Gates)](https://naruto.fandom.com/wiki/Crane_Wing_Formation_(Eight_Gates)) | S | Offensive | `narutorpg:crane_wing_formation_eight_gates` (existing) |
| [Crazy Hair Technique](https://naruto.fandom.com/wiki/Crazy_Hair_Technique) | A | Offensive | `narutorpg:crazy_hair_technique` (existing) |
| [Creation Rebirth](https://naruto.fandom.com/wiki/Creation_Rebirth) | S | Supplementary | `narutorpg:creation_rebirth` |
| [Creation of All Things Technique](https://naruto.fandom.com/wiki/Creation_of_All_Things_Technique) | D | Supplementary | `narutorpg:creation_of_all_things_technique` (existing) |
| [Creative Destruction](https://naruto.fandom.com/wiki/Creative_Destruction) | B | Offensive | `narutorpg:creative_destruction` (existing) |
| [Crescent Moon Rasengan](https://naruto.fandom.com/wiki/Crescent_Moon_Rasengan) | C | Offensive | `narutorpg:crescent_moon_rasengan` |
| [Crimson Earth](https://naruto.fandom.com/wiki/Crimson_Earth) | C | Offensive, Supplementary | `narutorpg:crimson_earth` |
| [Crimson Flash Colliding Fist](https://naruto.fandom.com/wiki/Crimson_Flash_Colliding_Fist) | C | Offensive | `narutorpg:crimson_flash_colliding_fist` (existing) |
| [Crimson Impact](https://naruto.fandom.com/wiki/Crimson_Impact) | C | Offensive | `narutorpg:crimson_impact` (existing) |
| [Crimson Iron Cannon Blade](https://naruto.fandom.com/wiki/Crimson_Iron_Cannon_Blade) | C | Offensive | `narutorpg:crimson_iron_cannon_blade` (existing) |
| [Crimson Lotus Lightning Fire Technique](https://naruto.fandom.com/wiki/Crimson_Lotus_Lightning_Fire_Technique) | B | Offensive | `narutorpg:crimson_lotus_lightning_fire_technique` (existing) |
| [Crimson Mist Barrier Technique](https://naruto.fandom.com/wiki/Crimson_Mist_Barrier_Technique) | C | Supplementary | `narutorpg:crimson_mist_barrier_technique` (existing) |
| [Cross Lariat](https://naruto.fandom.com/wiki/Cross_Lariat) | B | Offensive | `narutorpg:cross_lariat` (existing) |
| [Crow Clone Technique](https://naruto.fandom.com/wiki/Crow_Clone_Technique) | C | Supplementary | `narutorpg:crow_clone_technique` |
| [Cruelty](https://naruto.fandom.com/wiki/Cruelty) | A | Offensive | `narutorpg:cruelty` |
| [Crushing Rage: Rasengan](https://naruto.fandom.com/wiki/Crushing_Rage:_Rasengan) | A | Offensive | `narutorpg:crushing_rage_rasengan` (existing) |
| [Cry of the Raijin](https://naruto.fandom.com/wiki/Cry_of_the_Raijin) | C | Offensive | `narutorpg:cry_of_the_raijin` (existing) |
| [Crystal Armour](https://naruto.fandom.com/wiki/Crystal_Armour) | B | Offensive, Defensive | `narutorpg:crystal_armour` (existing) |
| [Crystal Barrier Technique](https://naruto.fandom.com/wiki/Crystal_Barrier_Technique) | C | Supplementary | `narutorpg:crystal_barrier_technique` (existing) |
| [Crystal Release](https://naruto.fandom.com/wiki/Crystal_Release) | B | - | `narutorpg:crystal_release` (existing) |
| [Crystal Release: Crimson Fruit](https://naruto.fandom.com/wiki/Crystal_Release:_Crimson_Fruit) | B | Defensive, Supplementary | `narutorpg:crystal_release_crimson_fruit` (existing) |
| [Crystal Release: Crystal Encampment Wall](https://naruto.fandom.com/wiki/Crystal_Release:_Crystal_Encampment_Wall) | B | Defensive | `narutorpg:crystal_release_crystal_encampment_wall` (existing) |
| [Crystal Release: Crystal Imprisonment Wave](https://naruto.fandom.com/wiki/Crystal_Release:_Crystal_Imprisonment_Wave) | B | Supplementary | `narutorpg:crystal_release_crystal_imprisonment_wave` (existing) |
| [Crystal Release: Crystal Lance](https://naruto.fandom.com/wiki/Crystal_Release:_Crystal_Lance) | B | Offensive | `narutorpg:crystal_release_crystal_lance` (existing) |
| [Crystal Release: Crystal Needles](https://naruto.fandom.com/wiki/Crystal_Release:_Crystal_Needles) | B | Offensive, Supplementary | `narutorpg:crystal_release_crystal_needles` (existing) |
| [Crystal Release: Crystal Pentagonal Prison](https://naruto.fandom.com/wiki/Crystal_Release:_Crystal_Pentagonal_Prison) | B | Supplementary | `narutorpg:crystal_release_crystal_pentagonal_prison` (existing) |
| [Crystal Release: Crystal Wheel](https://naruto.fandom.com/wiki/Crystal_Release:_Crystal_Wheel) | B | Supplementary | `narutorpg:crystal_release_crystal_wheel` (existing) |
| [Crystal Release: Growing Crystal Thorns](https://naruto.fandom.com/wiki/Crystal_Release:_Growing_Crystal_Thorns) | B | Offensive | `narutorpg:crystal_release_growing_crystal_thorns` (existing) |
| [Crystal Release: Jade Crystal Blade](https://naruto.fandom.com/wiki/Crystal_Release:_Jade_Crystal_Blade) | B | Offensive | `narutorpg:crystal_release_jade_crystal_blade` (existing) |
| [Crystal Release: Jade Crystal Hexagonal Pillars](https://naruto.fandom.com/wiki/Crystal_Release:_Jade_Crystal_Hexagonal_Pillars) | B | Supplementary | `narutorpg:crystal_release_jade_crystal_hexagonal_pillars` (existing) |
| [Crystal Release: Jade Crystal Labyrinth Technique](https://naruto.fandom.com/wiki/Crystal_Release:_Jade_Crystal_Labyrinth_Technique) | B | Supplementary | `narutorpg:crystal_release_jade_crystal_labyrinth_technique` (existing) |
| [Crystal Release: Jade Crystal Mirror](https://naruto.fandom.com/wiki/Crystal_Release:_Jade_Crystal_Mirror) | B | Supplementary | `narutorpg:crystal_release_jade_crystal_mirror` (existing) |
| [Crystal Release: Jade Crystal Prison Technique](https://naruto.fandom.com/wiki/Crystal_Release:_Jade_Crystal_Prison_Technique) | B | Supplementary | `narutorpg:crystal_release_jade_crystal_prison_technique` (existing) |
| [Crystal Release: Jade Crystal Wall Eighth Formation](https://naruto.fandom.com/wiki/Crystal_Release:_Jade_Crystal_Wall_Eighth_Formation) | B | Offensive, Supplementary | `narutorpg:crystal_release_jade_crystal_wall_eighth_formation` (existing) |
| [Crystal Release: Shuriken Wild Dance](https://naruto.fandom.com/wiki/Crystal_Release:_Shuriken_Wild_Dance) | B | Offensive | `narutorpg:crystal_release_shuriken_wild_dance` (existing) |
| [Crystal Release: String of Glory](https://naruto.fandom.com/wiki/Crystal_Release:_String_of_Glory) | B | Offensive, Defensive | `narutorpg:crystal_release_string_of_glory` (existing) |
| [Crystal Release: Tearing Crystal Falling Dragon](https://naruto.fandom.com/wiki/Crystal_Release:_Tearing_Crystal_Falling_Dragon) | B | Offensive, Supplementary | `narutorpg:crystal_release_tearing_crystal_falling_dragon` (existing) |
| [Crystal Release: The Gods' Crossings Technique](https://naruto.fandom.com/wiki/Crystal_Release:_The_Gods'_Crossings_Technique) | B | Offensive, Supplementary | `narutorpg:crystal_release_the_gods_crossings_technique` (existing) |
| [Crystal: Giant Hexagonal Shuriken](https://naruto.fandom.com/wiki/Crystal:_Giant_Hexagonal_Shuriken) | B | Offensive | `narutorpg:crystal_giant_hexagonal_shuriken` (existing) |
| [Crystal: Hexagonal Shuriken: Wild Dance](https://naruto.fandom.com/wiki/Crystal:_Hexagonal_Shuriken:_Wild_Dance) | B | Offensive | `narutorpg:crystal_hexagonal_shuriken_wild_dance` (existing) |
| [Culminated Feelings: Wind Lightning Cherry Blossom Formation](https://naruto.fandom.com/wiki/Culminated_Feelings:_Wind_Lightning_Cherry_Blossom_Formation) | S | Offensive | `narutorpg:culminated_feelings_wind_lightning_cherry_blossom_formation` (existing) |
| [Curse Mandala](https://naruto.fandom.com/wiki/Curse_Mandala) | S | - | `narutorpg:curse_mandala` (existing) |
| [Curse Technique: Death Controlling Possessed Blood](https://naruto.fandom.com/wiki/Curse_Technique:_Death_Controlling_Possessed_Blood) | C | Offensive | `narutorpg:curse_technique_death_controlling_possessed_blood` |
| [Cursed Doll](https://naruto.fandom.com/wiki/Cursed_Doll) | D | Supplementary | `narutorpg:cursed_doll` |
| [Cursed Sand Puppet Technique](https://naruto.fandom.com/wiki/Cursed_Sand_Puppet_Technique) | C | Offensive, Supplementary | `narutorpg:cursed_sand_puppet_technique` |
| [Cursed Seal: Killing Shout](https://naruto.fandom.com/wiki/Cursed_Seal:_Killing_Shout) | A | Offensive | `narutorpg:cursed_seal_killing_shout` (existing) |
| [Cursed Strength](https://naruto.fandom.com/wiki/Cursed_Strength) | C | Offensive, Defensive | `narutorpg:cursed_strength` (existing) |
| [Cursed Tongue Eradication Seal](https://naruto.fandom.com/wiki/Cursed_Tongue_Eradication_Seal) | C | Supplementary | `narutorpg:cursed_tongue_eradication_seal` (existing) |
| [Daihakoten](https://naruto.fandom.com/wiki/Daihakoten) | S | Offensive, Supplementary | `narutorpg:daihakoten` (existing) |
| [Daikokuten](https://naruto.fandom.com/wiki/Daikokuten) | S | Supplementary | `narutorpg:daikokuten` (existing) |
| [Daikokuten: Collapse](https://naruto.fandom.com/wiki/Daikokuten:_Collapse) | S | Offensive | `narutorpg:daikokuten_collapse` (existing) |
| [Daikokuten: Disaster](https://naruto.fandom.com/wiki/Daikokuten:_Disaster) | S | Offensive | `narutorpg:daikokuten_disaster` |
| [Daikokuten: Divine Descent](https://naruto.fandom.com/wiki/Daikokuten:_Divine_Descent) | S | Offensive | `narutorpg:daikokuten_divine_descent` (existing) |
| [Daikokuten: Falling Star](https://naruto.fandom.com/wiki/Daikokuten:_Falling_Star) | S | Offensive | `narutorpg:daikokuten_falling_star` (existing) |
| [Daikokuten: Karma Flame](https://naruto.fandom.com/wiki/Daikokuten:_Karma_Flame) | S | - | `narutorpg:daikokuten_karma_flame` (existing) |
| [Daikokuten: Sukunahikona: Twin Crash](https://naruto.fandom.com/wiki/Daikokuten:_Sukunahikona:_Twin_Crash) | S | Offensive | `narutorpg:daikokuten_sukunahikona_twin_crash` (existing) |
| [Daikokuten: Triple](https://naruto.fandom.com/wiki/Daikokuten:_Triple) | S | Offensive | `narutorpg:daikokuten_triple` |
| [Dance Performance: Second Step](https://naruto.fandom.com/wiki/Dance_Performance:_Second_Step) | C | Offensive | `narutorpg:dance_performance_second_step` (existing) |
| [Dance of the Crescent Moon](https://naruto.fandom.com/wiki/Dance_of_the_Crescent_Moon) | A | Offensive | `narutorpg:dance_of_the_crescent_moon` |
| [Dance of the Fireball](https://naruto.fandom.com/wiki/Dance_of_the_Fireball) | C | Offensive | `narutorpg:dance_of_the_fireball` (existing) |
| [Dance of the Rising Dragon](https://naruto.fandom.com/wiki/Dance_of_the_Rising_Dragon) | C | Offensive | `narutorpg:dance_of_the_rising_dragon` |
| [Dance of the Seedling Fern](https://naruto.fandom.com/wiki/Dance_of_the_Seedling_Fern) | B | Offensive | `narutorpg:dance_of_the_seedling_fern` (existing) |
| [Dance of the Shikigami](https://naruto.fandom.com/wiki/Dance_of_the_Shikigami) | B | Offensive, Supplementary | `narutorpg:dance_of_the_shikigami` |
| [Dance of the Shikigami: Explosion](https://naruto.fandom.com/wiki/Dance_of_the_Shikigami:_Explosion) | C | Offensive | `narutorpg:dance_of_the_shikigami_explosion` |
| [Dance of the Shikigami: Punishment](https://naruto.fandom.com/wiki/Dance_of_the_Shikigami:_Punishment) | C | Offensive, Supplementary | `narutorpg:dance_of_the_shikigami_punishment` |
| [Dance of the Shikigami: Storm](https://naruto.fandom.com/wiki/Dance_of_the_Shikigami:_Storm) | C | Offensive | `narutorpg:dance_of_the_shikigami_storm` |
| [Dark Cloud](https://naruto.fandom.com/wiki/Dark_Cloud) | C | Offensive, Supplementary | `narutorpg:dark_cloud` |
| [Dark Flames Technique](https://naruto.fandom.com/wiki/Dark_Flames_Technique) | C | Offensive | `narutorpg:dark_flames_technique` (existing) |
| [Dark Medicine](https://naruto.fandom.com/wiki/Dark_Medicine) | C | Offensive | `narutorpg:dark_medicine` |
| [Dark Release](https://naruto.fandom.com/wiki/Dark_Release) | B | - | `narutorpg:dark_release` (existing) |
| [Dark Release: Inhaling Maw](https://naruto.fandom.com/wiki/Dark_Release:_Inhaling_Maw) | B | Offensive, Defensive | `narutorpg:dark_release_inhaling_maw` (existing) |
| [Dark Release: Judgment](https://naruto.fandom.com/wiki/Dark_Release:_Judgment) | B | Offensive | `narutorpg:dark_release_judgment` (existing) |
| [Dark Sealing Method](https://naruto.fandom.com/wiki/Dark_Sealing_Method) | B | Supplementary | `narutorpg:dark_sealing_method` |
| [Dark Smoke Sphere](https://naruto.fandom.com/wiki/Dark_Smoke_Sphere) | C | Offensive, Supplementary | `narutorpg:dark_smoke_sphere` |
| [Darkness Wave](https://naruto.fandom.com/wiki/Darkness_Wave) | C | Offensive | `narutorpg:darkness_wave` |
| [Dead Demon Consuming Seal](https://naruto.fandom.com/wiki/Dead_Demon_Consuming_Seal) | S | Offensive | `narutorpg:dead_demon_consuming_seal` (existing) |
| [Dead Soul Technique](https://naruto.fandom.com/wiki/Dead_Soul_Technique) | B | Supplementary | `narutorpg:dead_soul_technique` |
| [Death Technique: Heart Sever](https://naruto.fandom.com/wiki/Death_Technique:_Heart_Sever) | C | Offensive | `narutorpg:death_technique_heart_sever` |
| [Death Technique: Returning Soul Play](https://naruto.fandom.com/wiki/Death_Technique:_Returning_Soul_Play) | C | Offensive | `narutorpg:death_technique_returning_soul_play` |
| [Decapitating Airwaves](https://naruto.fandom.com/wiki/Decapitating_Airwaves) | D | Offensive, Defensive | `narutorpg:decapitating_airwaves` |
| [Decapitating Reciprocation](https://naruto.fandom.com/wiki/Decapitating_Reciprocation) | C | Offensive | `narutorpg:decapitating_reciprocation` |
| [Deep Crimson Spiral](https://naruto.fandom.com/wiki/Deep_Crimson_Spiral) | C | Offensive | `narutorpg:deep_crimson_spiral` |
| [Deer Butterfly Seal Binding](https://naruto.fandom.com/wiki/Deer_Butterfly_Seal_Binding) | B | Offensive | `narutorpg:deer_butterfly_seal_binding` (existing) |
| [Delicate Illness Extraction Technique](https://naruto.fandom.com/wiki/Delicate_Illness_Extraction_Technique) | B | Supplementary | `narutorpg:delicate_illness_extraction_technique` |
| [Demonic Flute: Floating Sound](https://naruto.fandom.com/wiki/Demonic_Flute:_Floating_Sound) | C | Offensive | `narutorpg:demonic_flute_floating_sound` |
| [Demonic Flute: Illusionary Warriors Manipulating Melody](https://naruto.fandom.com/wiki/Demonic_Flute:_Illusionary_Warriors_Manipulating_Melody) | B | Supplementary | `narutorpg:demonic_flute_illusionary_warriors_manipulating_melody` |
| [Demonic Flute: Phantom Spirit Wave](https://naruto.fandom.com/wiki/Demonic_Flute:_Phantom_Spirit_Wave) | C | Offensive | `narutorpg:demonic_flute_phantom_spirit_wave` |
| [Demonic Flute: Three Subordinates Interment Melody](https://naruto.fandom.com/wiki/Demonic_Flute:_Three_Subordinates_Interment_Melody) | C | Offensive | `narutorpg:demonic_flute_three_subordinates_interment_melody` (existing) |
| [Demonic Illusion: Dancing Blossom Swarm Technique](https://naruto.fandom.com/wiki/Demonic_Illusion:_Dancing_Blossom_Swarm_Technique) | C | Offensive | `narutorpg:demonic_illusion_dancing_blossom_swarm_technique` (existing) |
| [Demonic Illusion: Flying Shadow](https://naruto.fandom.com/wiki/Demonic_Illusion:_Flying_Shadow) | B | Offensive | `narutorpg:demonic_illusion_flying_shadow` (existing) |
| [Demonic Illusion: Lingering Phantom Blossom Technique](https://naruto.fandom.com/wiki/Demonic_Illusion:_Lingering_Phantom_Blossom_Technique) | C | Offensive, Defensive | `narutorpg:demonic_illusion_lingering_phantom_blossom_technique` (existing) |
| [Demonic Illusion: Steaming Multistoried Building](https://naruto.fandom.com/wiki/Demonic_Illusion:_Steaming_Multistoried_Building) | B | Supplementary | `narutorpg:demonic_illusion_steaming_multistoried_building` (existing) |
| [Demonic Illusion: Toad Confrontation Chant](https://naruto.fandom.com/wiki/Demonic_Illusion:_Toad_Confrontation_Chant) | A | Offensive, Supplementary | `narutorpg:demonic_illusion_toad_confrontation_chant` (existing) |
| [Demonic Illusion: Tree Binding Wind Blade](https://naruto.fandom.com/wiki/Demonic_Illusion:_Tree_Binding_Wind_Blade) | B | Offensive | `narutorpg:demonic_illusion_tree_binding_wind_blade` (existing) |
| [Demonic Mirror Clone Technique](https://naruto.fandom.com/wiki/Demonic_Mirror_Clone_Technique) | B | Supplementary | `narutorpg:demonic_mirror_clone_technique` (existing) |
| [Demonic Mirroring Ice Crystals](https://naruto.fandom.com/wiki/Demonic_Mirroring_Ice_Crystals) | B | Offensive | `narutorpg:demonic_mirroring_ice_crystals` (existing) |
| [Demonic Mirroring Ice Murder](https://naruto.fandom.com/wiki/Demonic_Mirroring_Ice_Murder) | B | Offensive, Defensive | `narutorpg:demonic_mirroring_ice_murder` (existing) |
| [Demonic Mirroring Ice Wall](https://naruto.fandom.com/wiki/Demonic_Mirroring_Ice_Wall) | B | Offensive, Defensive | `narutorpg:demonic_mirroring_ice_wall` (existing) |
| [Demonic Mirroring Thin Ice](https://naruto.fandom.com/wiki/Demonic_Mirroring_Thin_Ice) | B | Offensive | `narutorpg:demonic_mirroring_thin_ice` (existing) |
| [Demonic Statue Chains](https://naruto.fandom.com/wiki/Demonic_Statue_Chains) | B | Supplementary | `narutorpg:demonic_statue_chains` (existing) |
| [Demonic Statue of the Outer Path (jutsu)](https://naruto.fandom.com/wiki/Demonic_Statue_of_the_Outer_Path_(jutsu)) | B | Offensive | `narutorpg:demonic_statue_of_the_outer_path_jutsu` |
| [Demonic Statue of the Outer Path: Palm](https://naruto.fandom.com/wiki/Demonic_Statue_of_the_Outer_Path:_Palm) | A | Offensive | `narutorpg:demonic_statue_of_the_outer_path_palm` (existing) |
| [Desert Attack: Destruction Funeral](https://naruto.fandom.com/wiki/Desert_Attack:_Destruction_Funeral) | C | Offensive | `narutorpg:desert_attack_destruction_funeral` |
| [Desert Attack: Heavenly Funeral](https://naruto.fandom.com/wiki/Desert_Attack:_Heavenly_Funeral) | C | Offensive | `narutorpg:desert_attack_heavenly_funeral` |
| [Desert Layered Imperial Funeral Seal](https://naruto.fandom.com/wiki/Desert_Layered_Imperial_Funeral_Seal) | A | Supplementary | `narutorpg:desert_layered_imperial_funeral_seal` (existing) |
| [Desert Suspension](https://naruto.fandom.com/wiki/Desert_Suspension) | D | Supplementary | `narutorpg:desert_suspension` (existing) |
| [Desert Wave](https://naruto.fandom.com/wiki/Desert_Wave) | D | Defensive, Supplementary | `narutorpg:desert_wave` (existing) |
| [Desert: Hand](https://naruto.fandom.com/wiki/Desert:_Hand) | D | Supplementary | `narutorpg:desert_hand` (existing) |
| [Destroying Chaotic Snakes](https://naruto.fandom.com/wiki/Destroying_Chaotic_Snakes) | A | Offensive | `narutorpg:destroying_chaotic_snakes` |
| [Destroying Smell Smoke Prison](https://naruto.fandom.com/wiki/Destroying_Smell_Smoke_Prison) | D | Supplementary | `narutorpg:destroying_smell_smoke_prison` |
| [Destruction of Evil Seal](https://naruto.fandom.com/wiki/Destruction_of_Evil_Seal) | B | Supplementary | `narutorpg:destruction_of_evil_seal` |
| [Destructive Beauty](https://naruto.fandom.com/wiki/Destructive_Beauty) | B | Offensive | `narutorpg:destructive_beauty` (existing) |
| [Destructive Jinrai](https://naruto.fandom.com/wiki/Destructive_Jinrai) | C | Offensive | `narutorpg:destructive_jinrai` (existing) |
| [Destructive Shock](https://naruto.fandom.com/wiki/Destructive_Shock) | A | Offensive | `narutorpg:destructive_shock` (existing) |
| [Deva Path](https://naruto.fandom.com/wiki/Deva_Path) | B | - | `narutorpg:deva_path` (existing) |
| [Devouring Barrier Sphere Trap](https://naruto.fandom.com/wiki/Devouring_Barrier_Sphere_Trap) | C | Offensive, Supplementary | `narutorpg:devouring_barrier_sphere_trap` |
| [Dharma Power Barrier: Auspicious Sign](https://naruto.fandom.com/wiki/Dharma_Power_Barrier:_Auspicious_Sign) | C | Offensive, Defensive | `narutorpg:dharma_power_barrier_auspicious_sign` |
| [Dharma Power Sealing Technique: Sen no Rikyū](https://naruto.fandom.com/wiki/Dharma_Power_Sealing_Technique:_Sen_no_Rikyū) | C | Supplementary | `narutorpg:dharma_power_sealing_technique_sen_no_rikyu` (existing) |
| [Dharma Power: Silhouette](https://naruto.fandom.com/wiki/Dharma_Power:_Silhouette) | C | Offensive, Supplementary | `narutorpg:dharma_power_silhouette` |
| [Discharge Lightning Calamity Technique](https://naruto.fandom.com/wiki/Discharge_Lightning_Calamity_Technique) | B | Offensive | `narutorpg:discharge_lightning_calamity_technique` (existing) |
| [Divine Might: Thousand Blades of Hearts - Instant Falcon Thunder Burial](https://naruto.fandom.com/wiki/Divine_Might:_Thousand_Blades_of_Hearts_-_Instant_Falcon_Thunder_Burial) | A | Offensive | `narutorpg:divine_might_thousand_blades_of_hearts_instant_falcon_thunder_burial` (existing) |
| [Divine Retribution](https://naruto.fandom.com/wiki/Divine_Retribution) | C | Offensive | `narutorpg:divine_retribution` (existing) |
| [Divine Sword: Sword of Nunoboko](https://naruto.fandom.com/wiki/Divine_Sword:_Sword_of_Nunoboko) | A | Offensive, Defensive, Supplementary | `narutorpg:divine_sword_sword_of_nunoboko` (existing) |
| [Diving Snake Attack](https://naruto.fandom.com/wiki/Diving_Snake_Attack) | A | Offensive | `narutorpg:diving_snake_attack` |
| [Dog Wall Formation](https://naruto.fandom.com/wiki/Dog_Wall_Formation) | B | Defensive, Supplementary | `narutorpg:dog_wall_formation` (existing) |
| [Double Black Panther](https://naruto.fandom.com/wiki/Double_Black_Panther) | C | Offensive | `narutorpg:double_black_panther` (existing) |
| [Double Dynamic Entry](https://naruto.fandom.com/wiki/Double_Dynamic_Entry) | B | Offensive | `narutorpg:double_dynamic_entry` (existing) |
| [Double Heavenly Foot of Pain](https://naruto.fandom.com/wiki/Double_Heavenly_Foot_of_Pain) | B | Offensive | `narutorpg:double_heavenly_foot_of_pain` (existing) |
| [Double Human Bullet Tank](https://naruto.fandom.com/wiki/Double_Human_Bullet_Tank) | B | Offensive | `narutorpg:double_human_bullet_tank` (existing) |
| [Double Rasengan](https://naruto.fandom.com/wiki/Double_Rasengan) | B | Offensive | `narutorpg:double_rasengan` (existing) |
| [Dragon Flame Wild Dance](https://naruto.fandom.com/wiki/Dragon_Flame_Wild_Dance) | B | Offensive | `narutorpg:dragon_flame_wild_dance` (existing) |
| [Dragon Life Reincarnation](https://naruto.fandom.com/wiki/Dragon_Life_Reincarnation) | S | Supplementary | `narutorpg:dragon_life_reincarnation` |
| [Dragon Spying Technique](https://naruto.fandom.com/wiki/Dragon_Spying_Technique) | D | Supplementary | `narutorpg:dragon_spying_technique` (existing) |
| [Drilling Bee](https://naruto.fandom.com/wiki/Drilling_Bee) | A | Offensive | `narutorpg:drilling_bee` (existing) |
| [Drilling Hand of Sage Lightning](https://naruto.fandom.com/wiki/Drilling_Hand_of_Sage_Lightning) | A | Offensive | `narutorpg:drilling_hand_of_sage_lightning` (existing) |
| [Drilling Needles](https://naruto.fandom.com/wiki/Drilling_Needles) | B | Offensive | `narutorpg:drilling_needles` (existing) |
| [Drilling Sky Dancing Fan](https://naruto.fandom.com/wiki/Drilling_Sky_Dancing_Fan) | C | Offensive | `narutorpg:drilling_sky_dancing_fan` (existing) |
| [Drowning Bubble Technique](https://naruto.fandom.com/wiki/Drowning_Bubble_Technique) | C | Offensive | `narutorpg:drowning_bubble_technique` (existing) |
| [Drowning Water Blob Technique](https://naruto.fandom.com/wiki/Drowning_Water_Blob_Technique) | C | Offensive | `narutorpg:drowning_water_blob_technique` (existing) |
| [Dummy Balloon](https://naruto.fandom.com/wiki/Dummy_Balloon) | C | Offensive | `narutorpg:dummy_balloon` |
| [Dust Cloud Ignition Technique](https://naruto.fandom.com/wiki/Dust_Cloud_Ignition_Technique) | B | Offensive | `narutorpg:dust_cloud_ignition_technique` (existing) |
| [Dust Cloud Sandstorm](https://naruto.fandom.com/wiki/Dust_Cloud_Sandstorm) | C | Offensive, Defensive | `narutorpg:dust_cloud_sandstorm` |
| [Dust Release](https://naruto.fandom.com/wiki/Dust_Release) | D | - | `narutorpg:dust_release` (existing) |
| [Dust Release: Detachment of the Primitive World Technique](https://naruto.fandom.com/wiki/Dust_Release:_Detachment_of_the_Primitive_World_Technique) | C | Offensive | `narutorpg:dust_release_detachment_of_the_primitive_world_technique` (existing) |
| [Dust Release: Detachment of the Primitive World Technique Area](https://naruto.fandom.com/wiki/Dust_Release:_Detachment_of_the_Primitive_World_Technique_Area) | C | Offensive | `narutorpg:dust_release_detachment_of_the_primitive_world_technique_area` (existing) |
| [Dust Release: Split Detachment of the Primitive World Technique](https://naruto.fandom.com/wiki/Dust_Release:_Split_Detachment_of_the_Primitive_World_Technique) | C | Offensive | `narutorpg:dust_release_split_detachment_of_the_primitive_world_technique` (existing) |
| [Dust Wind Technique](https://naruto.fandom.com/wiki/Dust_Wind_Technique) | D | Defensive, Supplementary | `narutorpg:dust_wind_technique` (existing) |
| [Dustless Bewildering Cover](https://naruto.fandom.com/wiki/Dustless_Bewildering_Cover) | B | Supplementary | `narutorpg:dustless_bewildering_cover` (existing) |
| [Dōjutsu: Inferno](https://naruto.fandom.com/wiki/Dōjutsu:_Inferno) | B | Offensive | `narutorpg:dojutsu_inferno` (existing) |
| [Dōjutsu: Prison Flame Annihilation](https://naruto.fandom.com/wiki/Dōjutsu:_Prison_Flame_Annihilation) | B | Offensive, Supplementary | `narutorpg:dojutsu_prison_flame_annihilation` (existing) |
| [Early Summer Rain](https://naruto.fandom.com/wiki/Early_Summer_Rain) | C | Offensive | `narutorpg:early_summer_rain` |
| [Earth Dragon Bullet Technique (game)](https://naruto.fandom.com/wiki/Earth_Dragon_Bullet_Technique_(game)) | B | Offensive | `narutorpg:earth_dragon_bullet_technique_game` (existing) |
| [Earth Grudge Fear](https://naruto.fandom.com/wiki/Earth_Grudge_Fear) | S | Supplementary | `narutorpg:earth_grudge_fear` |
| [Earth Grudge Fear: Final Shot](https://naruto.fandom.com/wiki/Earth_Grudge_Fear:_Final_Shot) | S | Offensive | `narutorpg:earth_grudge_fear_final_shot` (existing) |
| [Earth Release Barrier: Earth Prison Dome of Magnificent Nothingness](https://naruto.fandom.com/wiki/Earth_Release_Barrier:_Earth_Prison_Dome_of_Magnificent_Nothingness) | B | Offensive, Supplementary | `narutorpg:earth_release_barrier_earth_prison_dome_of_magnificent_nothingness` (existing) |
| [Earth Release Resurrection Technique: Corpse Soil](https://naruto.fandom.com/wiki/Earth_Release_Resurrection_Technique:_Corpse_Soil) | S | Offensive, Supplementary | `narutorpg:earth_release_resurrection_technique_corpse_soil` (existing) |
| [Earth Release: Added-Weight Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Added-Weight_Rock_Technique) | B | Offensive, Supplementary | `narutorpg:earth_release_added_weight_rock_technique` (existing) |
| [Earth Release: Antlion Technique](https://naruto.fandom.com/wiki/Earth_Release:_Antlion_Technique) | C | Offensive | `narutorpg:earth_release_antlion_technique` (existing) |
| [Earth Release: Bedrock Coffin](https://naruto.fandom.com/wiki/Earth_Release:_Bedrock_Coffin) | C | Offensive | `narutorpg:earth_release_bedrock_coffin` (existing) |
| [Earth Release: Bog](https://naruto.fandom.com/wiki/Earth_Release:_Bog) | C | Offensive, Supplementary | `narutorpg:earth_release_bog` (existing) |
| [Earth Release: Bottomless Swamp](https://naruto.fandom.com/wiki/Earth_Release:_Bottomless_Swamp) | D | Supplementary | `narutorpg:earth_release_bottomless_swamp` (existing) |
| [Earth Release: Bullet Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Bullet_Rock_Technique) | C | Offensive | `narutorpg:earth_release_bullet_rock_technique` (existing) |
| [Earth Release: Demonic Rock Spider Armour](https://naruto.fandom.com/wiki/Earth_Release:_Demonic_Rock_Spider_Armour) | C | Offensive, Defensive, Supplementary | `narutorpg:earth_release_demonic_rock_spider_armour` (existing) |
| [Earth Release: Devouring Earth](https://naruto.fandom.com/wiki/Earth_Release:_Devouring_Earth) | C | Offensive | `narutorpg:earth_release_devouring_earth` (existing) |
| [Earth Release: Double Suicide Decapitation Technique](https://naruto.fandom.com/wiki/Earth_Release:_Double_Suicide_Decapitation_Technique) | D | Supplementary | `narutorpg:earth_release_double_suicide_decapitation_technique` (existing) |
| [Earth Release: Drone](https://naruto.fandom.com/wiki/Earth_Release:_Drone) | C | Offensive, Supplementary | `narutorpg:earth_release_drone` (existing) |
| [Earth Release: Dropping Lid](https://naruto.fandom.com/wiki/Earth_Release:_Dropping_Lid) | A | Offensive | `narutorpg:earth_release_dropping_lid` (existing) |
| [Earth Release: Dust Storm](https://naruto.fandom.com/wiki/Earth_Release:_Dust_Storm) | C | Offensive | `narutorpg:earth_release_dust_storm` (existing) |
| [Earth Release: Earth Corridor](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Corridor) | D | Supplementary | `narutorpg:earth_release_earth_corridor` (existing) |
| [Earth Release: Earth Crust Tearing Impact](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Crust_Tearing_Impact) | C | Offensive | `narutorpg:earth_release_earth_crust_tearing_impact` (existing) |
| [Earth Release: Earth Dragon Bullet](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Dragon_Bullet) | C | Offensive | `narutorpg:earth_release_earth_dragon_bullet` (existing) |
| [Earth Release: Earth Flow Divide](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Flow_Divide) | B | Offensive, Supplementary | `narutorpg:earth_release_earth_flow_divide` (existing) |
| [Earth Release: Earth Flow River](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Flow_River) | C | Offensive | `narutorpg:earth_release_earth_flow_river` (existing) |
| [Earth Release: Earth Flow Spears](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Flow_Spears) | C | Offensive | `narutorpg:earth_release_earth_flow_spears` (existing) |
| [Earth Release: Earth Flow Wave](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Flow_Wave) | C | Offensive, Defensive | `narutorpg:earth_release_earth_flow_wave` (existing) |
| [Earth Release: Earth Mausoleum Dumpling](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Mausoleum_Dumpling) | B | Offensive | `narutorpg:earth_release_earth_mausoleum_dumpling` (existing) |
| [Earth Release: Earth Pillar Explosion](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Pillar_Explosion) | C | Offensive | `narutorpg:earth_release_earth_pillar_explosion` (existing) |
| [Earth Release: Earth Quartz Technique](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Quartz_Technique) | D | - | `narutorpg:earth_release_earth_quartz_technique` (existing) |
| [Earth Release: Earth Shore Return](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Shore_Return) | C | Defensive | `narutorpg:earth_release_earth_shore_return` (existing) |
| [Earth Release: Earth Spear](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Spear) | B | Supplementary | `narutorpg:earth_release_earth_spear` (existing) |
| [Earth Release: Earth Wave Technique](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Wave_Technique) | D | Supplementary | `narutorpg:earth_release_earth_wave_technique` (existing) |
| [Earth Release: Earth and Stone Bamboo Shoot](https://naruto.fandom.com/wiki/Earth_Release:_Earth_and_Stone_Bamboo_Shoot) | C | Offensive | `narutorpg:earth_release_earth_and_stone_bamboo_shoot` (existing) |
| [Earth Release: Earth and Stone Dragon](https://naruto.fandom.com/wiki/Earth_Release:_Earth_and_Stone_Dragon) | C | Offensive | `narutorpg:earth_release_earth_and_stone_dragon` (existing) |
| [Earth Release: Earth-Style Rampart](https://naruto.fandom.com/wiki/Earth_Release:_Earth-Style_Rampart) | B | Defensive | `narutorpg:earth_release_earth_style_rampart` (existing) |
| [Earth Release: Earth-Style Wall](https://naruto.fandom.com/wiki/Earth_Release:_Earth-Style_Wall) | B | Defensive | `narutorpg:earth_release_earth_style_wall` (existing) |
| [Earth Release: Earth-Style Wall of Ten Thousand Ri](https://naruto.fandom.com/wiki/Earth_Release:_Earth-Style_Wall_of_Ten_Thousand_Ri) | B | Defensive | `narutorpg:earth_release_earth_style_wall_of_ten_thousand_ri` (existing) |
| [Earth Release: Erupting Rock Mountain Cannon](https://naruto.fandom.com/wiki/Earth_Release:_Erupting_Rock_Mountain_Cannon) | C | Offensive | `narutorpg:earth_release_erupting_rock_mountain_cannon` (existing) |
| [Earth Release: Fighting Blade Rod](https://naruto.fandom.com/wiki/Earth_Release:_Fighting_Blade_Rod) | C | Offensive, Supplementary | `narutorpg:earth_release_fighting_blade_rod` (existing) |
| [Earth Release: Fissure](https://naruto.fandom.com/wiki/Earth_Release:_Fissure) | C | Offensive | `narutorpg:earth_release_fissure` (existing) |
| [Earth Release: Fist Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Fist_Rock_Technique) | C | Offensive | `narutorpg:earth_release_fist_rock_technique` (existing) |
| [Earth Release: Floating Ring Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Floating_Ring_Rock_Technique) | C | Offensive, Defensive | `narutorpg:earth_release_floating_ring_rock_technique` (existing) |
| [Earth Release: Flying Thrown Stones](https://naruto.fandom.com/wiki/Earth_Release:_Flying_Thrown_Stones) | C | Offensive | `narutorpg:earth_release_flying_thrown_stones` (existing) |
| [Earth Release: Golem Technique](https://naruto.fandom.com/wiki/Earth_Release:_Golem_Technique) | C | Offensive, Defensive | `narutorpg:earth_release_golem_technique` (existing) |
| [Earth Release: Golem Technique Boulder Wave](https://naruto.fandom.com/wiki/Earth_Release:_Golem_Technique_Boulder_Wave) | C | Offensive | `narutorpg:earth_release_golem_technique_boulder_wave` (existing) |
| [Earth Release: Golem Technique Earth Flattener](https://naruto.fandom.com/wiki/Earth_Release:_Golem_Technique_Earth_Flattener) | C | Offensive | `narutorpg:earth_release_golem_technique_earth_flattener` (existing) |
| [Earth Release: Golem Technique Stone Prayer](https://naruto.fandom.com/wiki/Earth_Release:_Golem_Technique_Stone_Prayer) | C | Offensive | `narutorpg:earth_release_golem_technique_stone_prayer` (existing) |
| [Earth Release: Gravel](https://naruto.fandom.com/wiki/Earth_Release:_Gravel) | D | Supplementary | `narutorpg:earth_release_gravel` (existing) |
| [Earth Release: Great Moving Earth Core](https://naruto.fandom.com/wiki/Earth_Release:_Great_Moving_Earth_Core) | C | Offensive, Defensive, Supplementary | `narutorpg:earth_release_great_moving_earth_core` (existing) |
| [Earth Release: Ground Bedrock](https://naruto.fandom.com/wiki/Earth_Release:_Ground_Bedrock) | D | Supplementary | `narutorpg:earth_release_ground_bedrock` (existing) |
| [Earth Release: Hardening Technique](https://naruto.fandom.com/wiki/Earth_Release:_Hardening_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:earth_release_hardening_technique` (existing) |
| [Earth Release: Hiding Like a Mole Technique](https://naruto.fandom.com/wiki/Earth_Release:_Hiding_Like_a_Mole_Technique) | C | Supplementary | `narutorpg:earth_release_hiding_like_a_mole_technique` (existing) |
| [Earth Release: Hiding in Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Hiding_in_Rock_Technique) | D | Supplementary | `narutorpg:earth_release_hiding_in_rock_technique` (existing) |
| [Earth Release: Iron Fist Prison](https://naruto.fandom.com/wiki/Earth_Release:_Iron_Fist_Prison) | D | Supplementary | `narutorpg:earth_release_iron_fist_prison` (existing) |
| [Earth Release: Light-Weight Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Light-Weight_Rock_Technique) | B | Offensive, Supplementary | `narutorpg:earth_release_light_weight_rock_technique` (existing) |
| [Earth Release: Lithic Press](https://naruto.fandom.com/wiki/Earth_Release:_Lithic_Press) | C | Offensive | `narutorpg:earth_release_lithic_press` (existing) |
| [Earth Release: Moving Earth Core](https://naruto.fandom.com/wiki/Earth_Release:_Moving_Earth_Core) | C | Offensive, Defensive, Supplementary | `narutorpg:earth_release_moving_earth_core` (existing) |
| [Earth Release: Mud Body Technique](https://naruto.fandom.com/wiki/Earth_Release:_Mud_Body_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:earth_release_mud_body_technique` (existing) |
| [Earth Release: Mud Spore](https://naruto.fandom.com/wiki/Earth_Release:_Mud_Spore) | C | Offensive | `narutorpg:earth_release_mud_spore` (existing) |
| [Earth Release: Mud Wolves](https://naruto.fandom.com/wiki/Earth_Release:_Mud_Wolves) | C | Offensive, Supplementary | `narutorpg:earth_release_mud_wolves` (existing) |
| [Earth Release: Mudslide](https://naruto.fandom.com/wiki/Earth_Release:_Mudslide) | C | Offensive | `narutorpg:earth_release_mudslide` (existing) |
| [Earth Release: Multiple Earth-Style Wall](https://naruto.fandom.com/wiki/Earth_Release:_Multiple_Earth-Style_Wall) | D | Defensive, Supplementary | `narutorpg:earth_release_multiple_earth_style_wall` (existing) |
| [Earth Release: Mutability](https://naruto.fandom.com/wiki/Earth_Release:_Mutability) | A | Defensive, Supplementary | `narutorpg:earth_release_mutability` (existing) |
| [Earth Release: Opening Earth Rising Excavation](https://naruto.fandom.com/wiki/Earth_Release:_Opening_Earth_Rising_Excavation) | B | Offensive, Supplementary | `narutorpg:earth_release_opening_earth_rising_excavation` (existing) |
| [Earth Release: Practice Brick Technique](https://naruto.fandom.com/wiki/Earth_Release:_Practice_Brick_Technique) | D | Defensive | `narutorpg:earth_release_practice_brick_technique` (existing) |
| [Earth Release: Rending Drill Fang](https://naruto.fandom.com/wiki/Earth_Release:_Rending_Drill_Fang) | C | Offensive | `narutorpg:earth_release_rending_drill_fang` (existing) |
| [Earth Release: Reverse Antlion](https://naruto.fandom.com/wiki/Earth_Release:_Reverse_Antlion) | D | Supplementary | `narutorpg:earth_release_reverse_antlion` (existing) |
| [Earth Release: Rising Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Rising_Rock_Technique) | C | Offensive | `narutorpg:earth_release_rising_rock_technique` (existing) |
| [Earth Release: Rock Brick Cane](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Brick_Cane) | C | Offensive | `narutorpg:earth_release_rock_brick_cane` (existing) |
| [Earth Release: Rock Collapse](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Collapse) | B | Offensive | `narutorpg:earth_release_rock_collapse` (existing) |
| [Earth Release: Rock Droop Ice Technique](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Droop_Ice_Technique) | C | Offensive | `narutorpg:earth_release_rock_droop_ice_technique` (existing) |
| [Earth Release: Rock Gun Technique](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Gun_Technique) | C | Offensive | `narutorpg:earth_release_rock_gun_technique` (existing) |
| [Earth Release: Rock Hammer Technique](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Hammer_Technique) | C | Offensive | `narutorpg:earth_release_rock_hammer_technique` (existing) |
| [Earth Release: Rock Lodging Destruction](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Lodging_Destruction) | B | Offensive | `narutorpg:earth_release_rock_lodging_destruction` (existing) |
| [Earth Release: Rock Pillar Prison Technique](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Pillar_Prison_Technique) | D | Defensive | `narutorpg:earth_release_rock_pillar_prison_technique` (existing) |
| [Earth Release: Rock Pillar Spears](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Pillar_Spears) | C | Offensive | `narutorpg:earth_release_rock_pillar_spears` (existing) |
| [Earth Release: Rock Section Cane](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Section_Cane) | C | Offensive | `narutorpg:earth_release_rock_section_cane` (existing) |
| [Earth Release: Rock Shelter](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Shelter) | D | Defensive | `narutorpg:earth_release_rock_shelter` (existing) |
| [Earth Release: Rock Shuriken](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Shuriken) | C | Offensive | `narutorpg:earth_release_rock_shuriken` (existing) |
| [Earth Release: Rock Trail](https://naruto.fandom.com/wiki/Earth_Release:_Rock_Trail) | C | Offensive | `narutorpg:earth_release_rock_trail` (existing) |
| [Earth Release: Rock-Tatami Technique](https://naruto.fandom.com/wiki/Earth_Release:_Rock-Tatami_Technique) | C | Offensive | `narutorpg:earth_release_rock_tatami_technique` (existing) |
| [Earth Release: Sand Dance](https://naruto.fandom.com/wiki/Earth_Release:_Sand_Dance) | D | Supplementary | `narutorpg:earth_release_sand_dance` (existing) |
| [Earth Release: Sandwich Technique](https://naruto.fandom.com/wiki/Earth_Release:_Sandwich_Technique) | B | Offensive | `narutorpg:earth_release_sandwich_technique` (existing) |
| [Earth Release: Shadow Clone](https://naruto.fandom.com/wiki/Earth_Release:_Shadow_Clone) | D | Supplementary | `narutorpg:earth_release_shadow_clone` (existing) |
| [Earth Release: Stamping Rock Explosion Technique](https://naruto.fandom.com/wiki/Earth_Release:_Stamping_Rock_Explosion_Technique) | C | Offensive, Supplementary | `narutorpg:earth_release_stamping_rock_explosion_technique` (existing) |
| [Earth Release: Sticky Earth Drop](https://naruto.fandom.com/wiki/Earth_Release:_Sticky_Earth_Drop) | A | Supplementary | `narutorpg:earth_release_sticky_earth_drop` (existing) |
| [Earth Release: Subterranean Voyage](https://naruto.fandom.com/wiki/Earth_Release:_Subterranean_Voyage) | C | Offensive, Supplementary | `narutorpg:earth_release_subterranean_voyage` (existing) |
| [Earth Release: Super Fist Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Super_Fist_Rock_Technique) | C | Offensive | `narutorpg:earth_release_super_fist_rock_technique` (existing) |
| [Earth Release: Swamp of the Underworld](https://naruto.fandom.com/wiki/Earth_Release:_Swamp_of_the_Underworld) | A | Offensive | `narutorpg:earth_release_swamp_of_the_underworld` (existing) |
| [Earth Release: Tearing Earth Turning Palm](https://naruto.fandom.com/wiki/Earth_Release:_Tearing_Earth_Turning_Palm) | C | Offensive | `narutorpg:earth_release_tearing_earth_turning_palm` (existing) |
| [Earth Release: Tracking Projectiles Technique](https://naruto.fandom.com/wiki/Earth_Release:_Tracking_Projectiles_Technique) | C | Offensive | `narutorpg:earth_release_tracking_projectiles_technique` (existing) |
| [Earth Release: Tunnelling Technique](https://naruto.fandom.com/wiki/Earth_Release:_Tunnelling_Technique) | D | Supplementary | `narutorpg:earth_release_tunnelling_technique` (existing) |
| [Earth Release: Ultra-Added-Weight Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Ultra-Added-Weight_Rock_Technique) | C | Offensive, Supplementary | `narutorpg:earth_release_ultra_added_weight_rock_technique` (existing) |
| [Earth Release: Ultralight-Weight Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Ultralight-Weight_Rock_Technique) | D | Supplementary | `narutorpg:earth_release_ultralight_weight_rock_technique` (existing) |
| [Earth Release: Underground Projection Fish Technique](https://naruto.fandom.com/wiki/Earth_Release:_Underground_Projection_Fish_Technique) | C | Supplementary | `narutorpg:earth_release_underground_projection_fish_technique` (existing) |
| [Earth Release: Underground Split](https://naruto.fandom.com/wiki/Earth_Release:_Underground_Split) | C | Offensive | `narutorpg:earth_release_underground_split` (existing) |
| [Earth-Tearing Erupting Flame](https://naruto.fandom.com/wiki/Earth-Tearing_Erupting_Flame) | C | Offensive | `narutorpg:earth_tearing_erupting_flame` (existing) |
| [Echo Technique](https://naruto.fandom.com/wiki/Echo_Technique) | D | Supplementary | `narutorpg:echo_technique` |
| [Echolocation Technique](https://naruto.fandom.com/wiki/Echolocation_Technique) | D | Supplementary | `narutorpg:echolocation_technique` |
| [Eight Branches Technique](https://naruto.fandom.com/wiki/Eight_Branches_Technique) | S | Offensive, Defensive, Supplementary | `narutorpg:eight_branches_technique` |
| [Eight Trigrams Grid Palm](https://naruto.fandom.com/wiki/Eight_Trigrams_Grid_Palm) | B | Offensive, Supplementary | `narutorpg:eight_trigrams_grid_palm` (existing) |
| [Eight Trigrams Spirit Sixty-Four Palms](https://naruto.fandom.com/wiki/Eight_Trigrams_Spirit_Sixty-Four_Palms) | B | Offensive | `narutorpg:eight_trigrams_spirit_sixty_four_palms` (existing) |
| [Eight Trigrams Twin Lions Crumbling Attack](https://naruto.fandom.com/wiki/Eight_Trigrams_Twin_Lions_Crumbling_Attack) | B | Offensive, Defensive, Supplementary | `narutorpg:eight_trigrams_twin_lions_crumbling_attack` (existing) |
| [Electric Eye Laser](https://naruto.fandom.com/wiki/Electric_Eye_Laser) | D | Supplementary | `narutorpg:electric_eye_laser` |
| [Empty Cicada Shell Technique](https://naruto.fandom.com/wiki/Empty_Cicada_Shell_Technique) | D | Supplementary | `narutorpg:empty_cicada_shell_technique` (existing) |
| [Empty Flaming Prison of Coiled Snakes](https://naruto.fandom.com/wiki/Empty_Flaming_Prison_of_Coiled_Snakes) | B | Offensive | `narutorpg:empty_flaming_prison_of_coiled_snakes` |
| [Enclosing Technique](https://naruto.fandom.com/wiki/Enclosing_Technique) | C | Supplementary | `narutorpg:enclosing_technique` |
| [Enma Summoning: Adamantine Monkey Dance](https://naruto.fandom.com/wiki/Enma_Summoning:_Adamantine_Monkey_Dance) | A | Offensive | `narutorpg:enma_summoning_adamantine_monkey_dance` |
| [Ensnaring Exploding Flame](https://naruto.fandom.com/wiki/Ensnaring_Exploding_Flame) | C | Offensive | `narutorpg:ensnaring_exploding_flame` (existing) |
| [Erupting Dust Sand](https://naruto.fandom.com/wiki/Erupting_Dust_Sand) | C | Offensive, Defensive | `narutorpg:erupting_dust_sand` |
| [Erupting Falling Stomp](https://naruto.fandom.com/wiki/Erupting_Falling_Stomp) | B | Offensive | `narutorpg:erupting_falling_stomp` (existing) |
| [Erupting Formation Body](https://naruto.fandom.com/wiki/Erupting_Formation_Body) | B | Offensive | `narutorpg:erupting_formation_body` (existing) |
| [Erupting Interment Dust](https://naruto.fandom.com/wiki/Erupting_Interment_Dust) | C | Offensive | `narutorpg:erupting_interment_dust` |
| [Erupting Propulsion Fist](https://naruto.fandom.com/wiki/Erupting_Propulsion_Fist) | B | Offensive | `narutorpg:erupting_propulsion_fist` (existing) |
| [Erupting Revolving Arm](https://naruto.fandom.com/wiki/Erupting_Revolving_Arm) | B | Offensive | `narutorpg:erupting_revolving_arm` (existing) |
| [Erupting Revolving Foot](https://naruto.fandom.com/wiki/Erupting_Revolving_Foot) | B | Offensive | `narutorpg:erupting_revolving_foot` (existing) |
| [Erupting Strong Foot](https://naruto.fandom.com/wiki/Erupting_Strong_Foot) | B | Offensive | `narutorpg:erupting_strong_foot` (existing) |
| [Erupting Thrust Cannon](https://naruto.fandom.com/wiki/Erupting_Thrust_Cannon) | B | Offensive | `narutorpg:erupting_thrust_cannon` (existing) |
| [Eternal Lightning](https://naruto.fandom.com/wiki/Eternal_Lightning) | C | Offensive | `narutorpg:eternal_lightning` (existing) |
| [Eternal Rival (Ultimate Ninja 3)](https://naruto.fandom.com/wiki/Eternal_Rival_(Ultimate_Ninja_3)) | B | Offensive, Defensive | `narutorpg:eternal_rival_ultimate_ninja_3` (existing) |
| [Eternal Rival (Ultimate Ninja Storm Revolution)](https://naruto.fandom.com/wiki/Eternal_Rival_(Ultimate_Ninja_Storm_Revolution)) | S | Offensive | `narutorpg:eternal_rival_ultimate_ninja_storm_revolution` (existing) |
| [Evasion Technique](https://naruto.fandom.com/wiki/Evasion_Technique) | B | Supplementary | `narutorpg:evasion_technique` |
| [Evening Shower Heavenly Crush](https://naruto.fandom.com/wiki/Evening_Shower_Heavenly_Crush) | C | Offensive | `narutorpg:evening_shower_heavenly_crush` (existing) |
| [Event Recording](https://naruto.fandom.com/wiki/Event_Recording) | D | Supplementary | `narutorpg:event_recording` |
| [Evil Deeds](https://naruto.fandom.com/wiki/Evil_Deeds) | A | Offensive | `narutorpg:evil_deeds` |
| [Evil Disturbance Waltz](https://naruto.fandom.com/wiki/Evil_Disturbance_Waltz) | B | Offensive | `narutorpg:evil_disturbance_waltz` (existing) |
| [Evil Greatness](https://naruto.fandom.com/wiki/Evil_Greatness) | A | Offensive | `narutorpg:evil_greatness` |
| [Evil Releasing Method](https://naruto.fandom.com/wiki/Evil_Releasing_Method) | C | Supplementary | `narutorpg:evil_releasing_method` |
| [Evilest Assassins](https://naruto.fandom.com/wiki/Evilest_Assassins) | B | Offensive, Defensive | `narutorpg:evilest_assassins` (existing) |
| [Exclusive Summoning: Zero-Tails](https://naruto.fandom.com/wiki/Exclusive_Summoning:_Zero-Tails) | C | Offensive | `narutorpg:exclusive_summoning_zero_tails` |
| [Execution by Kiss](https://naruto.fandom.com/wiki/Execution_by_Kiss) | C | Offensive | `narutorpg:execution_by_kiss` |
| [Expansive Truth-Seeking Ball](https://naruto.fandom.com/wiki/Expansive_Truth-Seeking_Ball) | A | Offensive | `narutorpg:expansive_truth_seeking_ball` (existing) |
| [Exploding Circle](https://naruto.fandom.com/wiki/Exploding_Circle) | C | Offensive | `narutorpg:exploding_circle` |
| [Exploding Dragon Strike](https://naruto.fandom.com/wiki/Exploding_Dragon_Strike) | C | Offensive | `narutorpg:exploding_dragon_strike` |
| [Exploding Flame Shot](https://naruto.fandom.com/wiki/Exploding_Flame_Shot) | C | Offensive | `narutorpg:exploding_flame_shot` (existing) |
| [Exploding Human Technique](https://naruto.fandom.com/wiki/Exploding_Human_Technique) | B | Offensive, Supplementary | `narutorpg:exploding_human_technique` (existing) |
| [Exploding Insect](https://naruto.fandom.com/wiki/Exploding_Insect) | C | Offensive | `narutorpg:exploding_insect` |
| [Exploding Mucus Shot](https://naruto.fandom.com/wiki/Exploding_Mucus_Shot) | C | Offensive, Defensive | `narutorpg:exploding_mucus_shot` |
| [Exploding Sand Boulder](https://naruto.fandom.com/wiki/Exploding_Sand_Boulder) | C | Offensive, Defensive | `narutorpg:exploding_sand_boulder` |
| [Exploding Sasumata Attack](https://naruto.fandom.com/wiki/Exploding_Sasumata_Attack) | C | Offensive | `narutorpg:exploding_sasumata_attack` |
| [Exploding Tree Spear](https://naruto.fandom.com/wiki/Exploding_Tree_Spear) | B | Offensive | `narutorpg:exploding_tree_spear` (existing) |
| [Explosion Release](https://naruto.fandom.com/wiki/Explosion_Release) | B | - | `narutorpg:explosion_release` (existing) |
| [Explosion Release: Defence Technique](https://naruto.fandom.com/wiki/Explosion_Release:_Defence_Technique) | B | Defensive, Supplementary | `narutorpg:explosion_release_defence_technique` (existing) |
| [Explosion Release: Impact](https://naruto.fandom.com/wiki/Explosion_Release:_Impact) | B | Offensive, Supplementary | `narutorpg:explosion_release_impact` (existing) |
| [Explosion Release: Instantaneous Incineration](https://naruto.fandom.com/wiki/Explosion_Release:_Instantaneous_Incineration) | B | Offensive | `narutorpg:explosion_release_instantaneous_incineration` (existing) |
| [Explosion Release: Landmine Fist](https://naruto.fandom.com/wiki/Explosion_Release:_Landmine_Fist) | B | Offensive | `narutorpg:explosion_release_landmine_fist` (existing) |
| [Explosion Release: Quick Explosive Kick](https://naruto.fandom.com/wiki/Explosion_Release:_Quick_Explosive_Kick) | B | Offensive | `narutorpg:explosion_release_quick_explosive_kick` (existing) |
| [Explosive Bubble](https://naruto.fandom.com/wiki/Explosive_Bubble) | C | Offensive | `narutorpg:explosive_bubble` (existing) |
| [Explosive Clay](https://naruto.fandom.com/wiki/Explosive_Clay) | B | Offensive, Defensive, Supplementary | `narutorpg:explosive_clay` (existing) |
| [Explosive Clay Dolls](https://naruto.fandom.com/wiki/Explosive_Clay_Dolls) | B | Offensive, Supplementary | `narutorpg:explosive_clay_dolls` (existing) |
| [Explosive Clay: Snake](https://naruto.fandom.com/wiki/Explosive_Clay:_Snake) | B | Offensive | `narutorpg:explosive_clay_snake` (existing) |
| [Explosive Clay: Twin Birds](https://naruto.fandom.com/wiki/Explosive_Clay:_Twin_Birds) | B | Offensive, Supplementary | `narutorpg:explosive_clay_twin_birds` (existing) |
| [Explosive Flower](https://naruto.fandom.com/wiki/Explosive_Flower) | C | Offensive | `narutorpg:explosive_flower` |
| [Explosive Great Ball Roll](https://naruto.fandom.com/wiki/Explosive_Great_Ball_Roll) | C | Offensive | `narutorpg:explosive_great_ball_roll` |
| [Explosive Landmines](https://naruto.fandom.com/wiki/Explosive_Landmines) | B | Offensive | `narutorpg:explosive_landmines` (existing) |
| [Extinction: Still Water Slash](https://naruto.fandom.com/wiki/Extinction:_Still_Water_Slash) | C | Offensive | `narutorpg:extinction_still_water_slash` (existing) |
| [Extreme Decapitating Airwaves](https://naruto.fandom.com/wiki/Extreme_Decapitating_Airwaves) | C | Offensive | `narutorpg:extreme_decapitating_airwaves` |
| [Eye Mind Reading](https://naruto.fandom.com/wiki/Eye_Mind_Reading) | D | Supplementary | `narutorpg:eye_mind_reading` (existing) |
| [Face Copying Technique](https://naruto.fandom.com/wiki/Face_Copying_Technique) | C | - | `narutorpg:face_copying_technique` (existing) |
| [Fairy Tale Destruction](https://naruto.fandom.com/wiki/Fairy_Tale_Destruction) | B | Offensive | `narutorpg:fairy_tale_destruction` |
| [Falling Aerial Blossom Explosion](https://naruto.fandom.com/wiki/Falling_Aerial_Blossom_Explosion) | C | Offensive | `narutorpg:falling_aerial_blossom_explosion` |
| [Falling Fire Scatter](https://naruto.fandom.com/wiki/Falling_Fire_Scatter) | C | Offensive | `narutorpg:falling_fire_scatter` (existing) |
| [Falling Vortex Interment](https://naruto.fandom.com/wiki/Falling_Vortex_Interment) | C | Offensive | `narutorpg:falling_vortex_interment` |
| [Fan Wind Roaring Wall](https://naruto.fandom.com/wiki/Fan_Wind_Roaring_Wall) | C | Offensive | `narutorpg:fan_wind_roaring_wall` (existing) |
| [Fang Beheading Fang](https://naruto.fandom.com/wiki/Fang_Beheading_Fang) | B | Offensive, Defensive | `narutorpg:fang_beheading_fang` (existing) |
| [Fang Heaven Wolf](https://naruto.fandom.com/wiki/Fang_Heaven_Wolf) | B | Offensive | `narutorpg:fang_heaven_wolf` (existing) |
| [Fang Insects Fang](https://naruto.fandom.com/wiki/Fang_Insects_Fang) | B | Offensive | `narutorpg:fang_insects_fang` (existing) |
| [Fang Passing Fang](https://naruto.fandom.com/wiki/Fang_Passing_Fang) | C | Offensive | `narutorpg:fang_passing_fang` (existing) |
| [Fang Rotating Fang](https://naruto.fandom.com/wiki/Fang_Rotating_Fang) | B | Offensive | `narutorpg:fang_rotating_fang` (existing) |
| [Fang Violent Fang](https://naruto.fandom.com/wiki/Fang_Violent_Fang) | B | Offensive | `narutorpg:fang_violent_fang` (existing) |
| [Fang Wolf Fang](https://naruto.fandom.com/wiki/Fang_Wolf_Fang) | B | Offensive | `narutorpg:fang_wolf_fang` (existing) |
| [Fang's Rush](https://naruto.fandom.com/wiki/Fang's_Rush) | C | Offensive | `narutorpg:fang_s_rush` (existing) |
| [Fangs of Lightning](https://naruto.fandom.com/wiki/Fangs_of_Lightning) | C | Offensive | `narutorpg:fangs_of_lightning` (existing) |
| [Fangs of the Monkey King](https://naruto.fandom.com/wiki/Fangs_of_the_Monkey_King) | B | Offensive | `narutorpg:fangs_of_the_monkey_king` (existing) |
| [Fanned Calamity Flames](https://naruto.fandom.com/wiki/Fanned_Calamity_Flames) | C | Offensive | `narutorpg:fanned_calamity_flames` (existing) |
| [Fanned Flame Great Fire](https://naruto.fandom.com/wiki/Fanned_Flame_Great_Fire) | C | Offensive | `narutorpg:fanned_flame_great_fire` (existing) |
| [Fanned Flame Great Fire: Circle Pocket](https://naruto.fandom.com/wiki/Fanned_Flame_Great_Fire:_Circle_Pocket) | C | Offensive | `narutorpg:fanned_flame_great_fire_circle_pocket` (existing) |
| [Favourable Wind](https://naruto.fandom.com/wiki/Favourable_Wind) | A | Offensive | `narutorpg:favourable_wind` |
| [Feather Bombardment](https://naruto.fandom.com/wiki/Feather_Bombardment) | C | Offensive | `narutorpg:feather_bombardment` |
| [Feather Launching](https://naruto.fandom.com/wiki/Feather_Launching) | C | Offensive | `narutorpg:feather_launching` (existing) |
| [Feigning Sleep Technique](https://naruto.fandom.com/wiki/Feigning_Sleep_Technique) | D | Supplementary | `narutorpg:feigning_sleep_technique` |
| [Fierce Attack Wind Sphere](https://naruto.fandom.com/wiki/Fierce_Attack_Wind_Sphere) | C | Offensive | `narutorpg:fierce_attack_wind_sphere` |
| [Fierce Snake](https://naruto.fandom.com/wiki/Fierce_Snake) | C | Offensive | `narutorpg:fierce_snake` (existing) |
| [Fierce Snake Trample](https://naruto.fandom.com/wiki/Fierce_Snake_Trample) | A | Offensive | `narutorpg:fierce_snake_trample` |
| [Fiery Paper Star of God](https://naruto.fandom.com/wiki/Fiery_Paper_Star_of_God) | B | Offensive | `narutorpg:fiery_paper_star_of_god` |
| [Fighting Tongue Bind](https://naruto.fandom.com/wiki/Fighting_Tongue_Bind) | A | Offensive, Supplementary | `narutorpg:fighting_tongue_bind` |
| [Fighting Tongue Slash](https://naruto.fandom.com/wiki/Fighting_Tongue_Slash) | A | Offensive | `narutorpg:fighting_tongue_slash` |
| [Final Move](https://naruto.fandom.com/wiki/Final_Move) | B | Offensive | `narutorpg:final_move` (existing) |
| [Finger Carving Seal](https://naruto.fandom.com/wiki/Finger_Carving_Seal) | C | Supplementary | `narutorpg:finger_carving_seal` |
| [Fire Escape Technique](https://naruto.fandom.com/wiki/Fire_Escape_Technique) | D | Supplementary | `narutorpg:fire_escape_technique` (existing) |
| [Fire Leaping Windhole](https://naruto.fandom.com/wiki/Fire_Leaping_Windhole) | A | Offensive | `narutorpg:fire_leaping_windhole` (existing) |
| [Fire Rat](https://naruto.fandom.com/wiki/Fire_Rat) | C | Offensive | `narutorpg:fire_rat` (existing) |
| [Fire Rat's Fur Coat](https://naruto.fandom.com/wiki/Fire_Rat's_Fur_Coat) | A | Offensive, Defensive | `narutorpg:fire_rat_s_fur_coat` |
| [Fire Release Bullet: "Blaze"](https://naruto.fandom.com/wiki/Fire_Release_Bullet:_"Blaze") | C | Offensive | `narutorpg:fire_release_bullet_blaze` (existing) |
| [Fire Release Chakra Mode](https://naruto.fandom.com/wiki/Fire_Release_Chakra_Mode) | C | Offensive, Supplementary | `narutorpg:fire_release_chakra_mode` (existing) |
| [Fire Release Cooperation Technique: Garuda](https://naruto.fandom.com/wiki/Fire_Release_Cooperation_Technique:_Garuda) | B | Offensive | `narutorpg:fire_release_cooperation_technique_garuda` (existing) |
| [Fire Release: Ash Pile Burning](https://naruto.fandom.com/wiki/Fire_Release:_Ash_Pile_Burning) | B | Offensive | `narutorpg:fire_release_ash_pile_burning` (existing) |
| [Fire Release: Big Flame Bullet](https://naruto.fandom.com/wiki/Fire_Release:_Big_Flame_Bullet) | B | Offensive | `narutorpg:fire_release_big_flame_bullet` (existing) |
| [Fire Release: Blast Wave Wild Dance](https://naruto.fandom.com/wiki/Fire_Release:_Blast_Wave_Wild_Dance) | B | Offensive | `narutorpg:fire_release_blast_wave_wild_dance` (existing) |
| [Fire Release: Blazing Meteors](https://naruto.fandom.com/wiki/Fire_Release:_Blazing_Meteors) | C | Offensive | `narutorpg:fire_release_blazing_meteors` (existing) |
| [Fire Release: Burning Circle](https://naruto.fandom.com/wiki/Fire_Release:_Burning_Circle) | C | Offensive, Defensive | `narutorpg:fire_release_burning_circle` (existing) |
| [Fire Release: Chaotic Petals](https://naruto.fandom.com/wiki/Fire_Release:_Chaotic_Petals) | C | Offensive | `narutorpg:fire_release_chaotic_petals` (existing) |
| [Fire Release: Crimson Earth Spider](https://naruto.fandom.com/wiki/Fire_Release:_Crimson_Earth_Spider) | C | Offensive | `narutorpg:fire_release_crimson_earth_spider` (existing) |
| [Fire Release: Deep Flame Explosion](https://naruto.fandom.com/wiki/Fire_Release:_Deep_Flame_Explosion) | C | Offensive, Supplementary | `narutorpg:fire_release_deep_flame_explosion` (existing) |
| [Fire Release: Demon Lantern](https://naruto.fandom.com/wiki/Fire_Release:_Demon_Lantern) | C | Offensive | `narutorpg:fire_release_demon_lantern` (existing) |
| [Fire Release: Dragon Fire Technique](https://naruto.fandom.com/wiki/Fire_Release:_Dragon_Fire_Technique) | C | Offensive | `narutorpg:fire_release_dragon_fire_technique` (existing) |
| [Fire Release: Dragon Flame Release Song Technique](https://naruto.fandom.com/wiki/Fire_Release:_Dragon_Flame_Release_Song_Technique) | C | Offensive | `narutorpg:fire_release_dragon_flame_release_song_technique` (existing) |
| [Fire Release: Exploding Flame Formation](https://naruto.fandom.com/wiki/Fire_Release:_Exploding_Flame_Formation) | C | Offensive | `narutorpg:fire_release_exploding_flame_formation` (existing) |
| [Fire Release: Extinguishing Curtain Field](https://naruto.fandom.com/wiki/Fire_Release:_Extinguishing_Curtain_Field) | C | Offensive | `narutorpg:fire_release_extinguishing_curtain_field` (existing) |
| [Fire Release: Fire Dragon Bullet](https://naruto.fandom.com/wiki/Fire_Release:_Fire_Dragon_Bullet) | C | Offensive | `narutorpg:fire_release_fire_dragon_bullet` (existing) |
| [Fire Release: Fire Dragon Flame Bullet](https://naruto.fandom.com/wiki/Fire_Release:_Fire_Dragon_Flame_Bullet) | B | Offensive | `narutorpg:fire_release_fire_dragon_flame_bullet` (existing) |
| [Fire Release: Fire Dragon Wall](https://naruto.fandom.com/wiki/Fire_Release:_Fire_Dragon_Wall) | D | Supplementary | `narutorpg:fire_release_fire_dragon_wall` (existing) |
| [Fire Release: Fire Wind Wild Blade](https://naruto.fandom.com/wiki/Fire_Release:_Fire_Wind_Wild_Blade) | C | Offensive | `narutorpg:fire_release_fire_wind_wild_blade` (existing) |
| [Fire Release: Fired Pottery Technique](https://naruto.fandom.com/wiki/Fire_Release:_Fired_Pottery_Technique) | C | Offensive | `narutorpg:fire_release_fired_pottery_technique` (existing) |
| [Fire Release: Flame Attack](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Attack) | C | Offensive | `narutorpg:fire_release_flame_attack` (existing) |
| [Fire Release: Flame Bullet](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Bullet) | C | Offensive | `narutorpg:fire_release_flame_bullet` (existing) |
| [Fire Release: Flame Capture](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Capture) | C | Offensive | `narutorpg:fire_release_flame_capture` (existing) |
| [Fire Release: Flame Murdering Boundary](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Murdering_Boundary) | C | Offensive | `narutorpg:fire_release_flame_murdering_boundary` (existing) |
| [Fire Release: Flame Spear](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Spear) | D | - | `narutorpg:fire_release_flame_spear` (existing) |
| [Fire Release: Flame Tiger Bombing](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Tiger_Bombing) | C | Offensive | `narutorpg:fire_release_flame_tiger_bombing` (existing) |
| [Fire Release: Flame Wave Technique](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Wave_Technique) | C | Offensive | `narutorpg:fire_release_flame_wave_technique` (existing) |
| [Fire Release: Flame Whirlwind](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Whirlwind) | C | Offensive | `narutorpg:fire_release_flame_whirlwind` (existing) |
| [Fire Release: Flame Wrapping Sickle Dance](https://naruto.fandom.com/wiki/Fire_Release:_Flame_Wrapping_Sickle_Dance) | C | Offensive | `narutorpg:fire_release_flame_wrapping_sickle_dance` (existing) |
| [Fire Release: Flaming Hail](https://naruto.fandom.com/wiki/Fire_Release:_Flaming_Hail) | C | Offensive | `narutorpg:fire_release_flaming_hail` (existing) |
| [Fire Release: Flint Yagura Technique](https://naruto.fandom.com/wiki/Fire_Release:_Flint_Yagura_Technique) | C | Offensive | `narutorpg:fire_release_flint_yagura_technique` (existing) |
| [Fire Release: Flying Flame Bullet](https://naruto.fandom.com/wiki/Fire_Release:_Flying_Flame_Bullet) | C | Offensive, Defensive | `narutorpg:fire_release_flying_flame_bullet` (existing) |
| [Fire Release: Fox Fire](https://naruto.fandom.com/wiki/Fire_Release:_Fox_Fire) | C | Offensive | `narutorpg:fire_release_fox_fire` (existing) |
| [Fire Release: Great Blaze Ball](https://naruto.fandom.com/wiki/Fire_Release:_Great_Blaze_Ball) | C | Offensive | `narutorpg:fire_release_great_blaze_ball` (existing) |
| [Fire Release: Great Dragon Fire Technique](https://naruto.fandom.com/wiki/Fire_Release:_Great_Dragon_Fire_Technique) | B | Offensive, Supplementary | `narutorpg:fire_release_great_dragon_fire_technique` (existing) |
| [Fire Release: Great Fire Annihilation](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Annihilation) | B | Offensive | `narutorpg:fire_release_great_fire_annihilation` (existing) |
| [Fire Release: Great Fire Annihilation: Dust](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Annihilation:_Dust) | B | Offensive | `narutorpg:fire_release_great_fire_annihilation_dust` (existing) |
| [Fire Release: Great Fire Annihilation: Earth](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Annihilation:_Earth) | B | Offensive | `narutorpg:fire_release_great_fire_annihilation_earth` (existing) |
| [Fire Release: Great Fire Annihilation: Heaven](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Annihilation:_Heaven) | B | Offensive | `narutorpg:fire_release_great_fire_annihilation_heaven` (existing) |
| [Fire Release: Great Fire Destruction](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Destruction) | C | Offensive | `narutorpg:fire_release_great_fire_destruction` (existing) |
| [Fire Release: Great Fire Heavenly Prison](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Heavenly_Prison) | C | Supplementary | `narutorpg:fire_release_great_fire_heavenly_prison` (existing) |
| [Fire Release: Great Fire Wall Technique](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Wall_Technique) | C | Offensive, Defensive | `narutorpg:fire_release_great_fire_wall_technique` (existing) |
| [Fire Release: Great Fire Whirlwind](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Whirlwind) | C | Offensive, Defensive | `narutorpg:fire_release_great_fire_whirlwind` (existing) |
| [Fire Release: Great Fireball Technique](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique) | C | Offensive | `narutorpg:fireball_jutsu` (existing) |
| [Fire Release: Great Fireball Technique — Revolving Flame](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique_—_Revolving_Flame) | C | Offensive | `narutorpg:fire_release_great_fireball_technique_revolving_flame` (existing) |
| [Fire Release: Great Fireball Technique: Falling Fire](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique:_Falling_Fire) | C | Offensive | `narutorpg:fire_release_great_fireball_technique_falling_fire` (existing) |
| [Fire Release: Great Fireball Technique: First Step](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique:_First_Step) | C | Offensive | `narutorpg:fire_release_great_fireball_technique_first_step` (existing) |
| [Fire Release: Great Fireball Technique: Onibi](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique:_Onibi) | C | Offensive | `narutorpg:fire_release_great_fireball_technique_onibi` (existing) |
| [Fire Release: Great Flame Flower](https://naruto.fandom.com/wiki/Fire_Release:_Great_Flame_Flower) | C | Offensive | `narutorpg:fire_release_great_flame_flower` (existing) |
| [Fire Release: Great Flame Rasengan](https://naruto.fandom.com/wiki/Fire_Release:_Great_Flame_Rasengan) | C | Offensive | `narutorpg:fire_release_great_flame_rasengan` (existing) |
| [Fire Release: Great Flame Technique](https://naruto.fandom.com/wiki/Fire_Release:_Great_Flame_Technique) | C | Offensive | `narutorpg:fire_release_great_flame_technique` (existing) |
| [Fire Release: Heavenly Prison](https://naruto.fandom.com/wiki/Fire_Release:_Heavenly_Prison) | C | Supplementary | `narutorpg:fire_release_heavenly_prison` (existing) |
| [Fire Release: Hiding in Ash and Dust Technique](https://naruto.fandom.com/wiki/Fire_Release:_Hiding_in_Ash_and_Dust_Technique) | C | Offensive, Supplementary | `narutorpg:fire_release_hiding_in_ash_and_dust_technique` (existing) |
| [Fire Release: Hiding in Fire Technique](https://naruto.fandom.com/wiki/Fire_Release:_Hiding_in_Fire_Technique) | D | Supplementary | `narutorpg:fire_release_hiding_in_fire_technique` (existing) |
| [Fire Release: Hurricane Oil Flame Bullet](https://naruto.fandom.com/wiki/Fire_Release:_Hurricane_Oil_Flame_Bullet) | C | Offensive | `narutorpg:fire_release_hurricane_oil_flame_bullet` (existing) |
| [Fire Release: Intelligent Hard Work](https://naruto.fandom.com/wiki/Fire_Release:_Intelligent_Hard_Work) | B | Offensive | `narutorpg:fire_release_intelligent_hard_work` (existing) |
| [Fire Release: Mist Blaze Dance Technique](https://naruto.fandom.com/wiki/Fire_Release:_Mist_Blaze_Dance_Technique) | C | Offensive | `narutorpg:fire_release_mist_blaze_dance_technique` (existing) |
| [Fire Release: Nail Crimson Crow Leap](https://naruto.fandom.com/wiki/Fire_Release:_Nail_Crimson_Crow_Leap) | C | Offensive | `narutorpg:fire_release_nail_crimson_crow_leap` (existing) |
| [Fire Release: Phoenix Sage Fire Technique](https://naruto.fandom.com/wiki/Fire_Release:_Phoenix_Sage_Fire_Technique) | C | Offensive | `narutorpg:fire_release_phoenix_sage_fire_technique` (existing) |
| [Fire Release: Phoenix Sage Flower Nail Crimson](https://naruto.fandom.com/wiki/Fire_Release:_Phoenix_Sage_Flower_Nail_Crimson) | B | Offensive | `narutorpg:fire_release_phoenix_sage_flower_nail_crimson` (existing) |
| [Fire Release: Phoenix Sage Formation Technique](https://naruto.fandom.com/wiki/Fire_Release:_Phoenix_Sage_Formation_Technique) | B | Offensive | `narutorpg:fire_release_phoenix_sage_formation_technique` (existing) |
| [Fire Release: Prison Flame Mask](https://naruto.fandom.com/wiki/Fire_Release:_Prison_Flame_Mask) | C | Offensive | `narutorpg:fire_release_prison_flame_mask` (existing) |
| [Fire Release: Roaring Flame Technique](https://naruto.fandom.com/wiki/Fire_Release:_Roaring_Flame_Technique) | C | Offensive | `narutorpg:fire_release_roaring_flame_technique` (existing) |
| [Fire Release: Running Fire](https://naruto.fandom.com/wiki/Fire_Release:_Running_Fire) | C | Offensive | `narutorpg:fire_release_running_fire` (existing) |
| [Fire Release: Smokescreen Gale](https://naruto.fandom.com/wiki/Fire_Release:_Smokescreen_Gale) | C | Offensive | `narutorpg:fire_release_smokescreen_gale` (existing) |
| [Fire Release: Soaring Flame Bullet Technique](https://naruto.fandom.com/wiki/Fire_Release:_Soaring_Flame_Bullet_Technique) | C | Offensive | `narutorpg:fire_release_soaring_flame_bullet_technique` (existing) |
| [Fire Release: Toad Oil Flame Bullet](https://naruto.fandom.com/wiki/Fire_Release:_Toad_Oil_Flame_Bullet) | B | Offensive | `narutorpg:fire_release_toad_oil_flame_bullet` (existing) |
| [Fire Release: Two-Headed Dragon Fire: Heaven](https://naruto.fandom.com/wiki/Fire_Release:_Two-Headed_Dragon_Fire:_Heaven) | A | Offensive | `narutorpg:fire_release_two_headed_dragon_fire_heaven` (existing) |
| [Fire Release: Uchiha Great Flame Sphere](https://naruto.fandom.com/wiki/Fire_Release:_Uchiha_Great_Flame_Sphere) | B | Offensive | `narutorpg:fire_release_uchiha_great_flame_sphere` (existing) |
| [Fire Release: Water Mist](https://naruto.fandom.com/wiki/Fire_Release:_Water_Mist) | D | - | `narutorpg:fire_release_water_mist` (existing) |
| [Fire Release: Wind Release: Blazing Wind Wild Wave](https://naruto.fandom.com/wiki/Fire_Release:_Wind_Release:_Blazing_Wind_Wild_Wave) | C | Offensive | `narutorpg:fire_release_wind_release_blazing_wind_wild_wave` (existing) |
| [Fire Sealing Method](https://naruto.fandom.com/wiki/Fire_Sealing_Method) | B | Supplementary | `narutorpg:fire_sealing_method` |
| [Fire Whirlwind Vortex Technique](https://naruto.fandom.com/wiki/Fire_Whirlwind_Vortex_Technique) | C | Offensive | `narutorpg:fire_whirlwind_vortex_technique` (existing) |
| [Fission Technique](https://naruto.fandom.com/wiki/Fission_Technique) | A | Supplementary | `narutorpg:fission_technique` |
| [Five Release Great Combo Technique](https://naruto.fandom.com/wiki/Five_Release_Great_Combo_Technique) | C | Offensive | `narutorpg:five_release_great_combo_technique` (existing) |
| [Five-Mountain Jump](https://naruto.fandom.com/wiki/Five-Mountain_Jump) | B | Offensive | `narutorpg:five_mountain_jump` (existing) |
| [Five-Seal Barrier](https://naruto.fandom.com/wiki/Five-Seal_Barrier) | C | Supplementary | `narutorpg:five_seal_barrier` |
| [Five-Seal Barrier Pass Technique](https://naruto.fandom.com/wiki/Five-Seal_Barrier_Pass_Technique) | D | Supplementary | `narutorpg:five_seal_barrier_pass_technique` |
| [Flame Circuit](https://naruto.fandom.com/wiki/Flame_Circuit) | C | Offensive, Defensive | `narutorpg:flame_circuit` (existing) |
| [Flame Combo](https://naruto.fandom.com/wiki/Flame_Combo) | C | Offensive | `narutorpg:flame_combo` |
| [Flaming Arrow Warhead](https://naruto.fandom.com/wiki/Flaming_Arrow_Warhead) | B | Offensive | `narutorpg:flaming_arrow_warhead` (existing) |
| [Flaming Arrow of Amazing Ability](https://naruto.fandom.com/wiki/Flaming_Arrow_of_Amazing_Ability) | B | Offensive | `narutorpg:flaming_arrow_of_amazing_ability` (existing) |
| [Flaming Fang: Petrification Poison](https://naruto.fandom.com/wiki/Flaming_Fang:_Petrification_Poison) | C | Offensive | `narutorpg:flaming_fang_petrification_poison` |
| [Flash](https://naruto.fandom.com/wiki/Flash) | C | Offensive | `narutorpg:flash` |
| [Flash Cross Sever](https://naruto.fandom.com/wiki/Flash_Cross_Sever) | C | Offensive | `narutorpg:flash_cross_sever` (existing) |
| [Flash Mode](https://naruto.fandom.com/wiki/Flash_Mode) | D | Supplementary | `narutorpg:flash_mode` |
| [Flash Swallow Dance: Heaven Step](https://naruto.fandom.com/wiki/Flash_Swallow_Dance:_Heaven_Step) | A | Offensive | `narutorpg:flash_swallow_dance_heaven_step` (existing) |
| [Flashing Bell Beams](https://naruto.fandom.com/wiki/Flashing_Bell_Beams) | C | Offensive | `narutorpg:flashing_bell_beams` |
| [Flawless Three](https://naruto.fandom.com/wiki/Flawless_Three) | B | Offensive | `narutorpg:flawless_three` (existing) |
| [Flea Shuriken](https://naruto.fandom.com/wiki/Flea_Shuriken) | D | - | `narutorpg:flea_shuriken` |
| [Flight](https://naruto.fandom.com/wiki/Flight) | C | Offensive | `narutorpg:flight` (existing) |
| [Flight Mode](https://naruto.fandom.com/wiki/Flight_Mode) | D | Supplementary | `narutorpg:flight_mode` |
| [Flight of the Obliterating Water](https://naruto.fandom.com/wiki/Flight_of_the_Obliterating_Water) | C | Offensive | `narutorpg:flight_of_the_obliterating_water` (existing) |
| [Floating Bugs](https://naruto.fandom.com/wiki/Floating_Bugs) | C | Offensive | `narutorpg:floating_bugs` |
| [Flower Dance](https://naruto.fandom.com/wiki/Flower_Dance) | C | Offensive | `narutorpg:flower_dance` |
| [Flower Ninja Art](https://naruto.fandom.com/wiki/Flower_Ninja_Art) | C | Offensive | `narutorpg:flower_ninja_art` |
| [Flower Ninja Art: Many Blooming Flowers](https://naruto.fandom.com/wiki/Flower_Ninja_Art:_Many_Blooming_Flowers) | C | Offensive | `narutorpg:flower_ninja_art_many_blooming_flowers` (existing) |
| [Flower Ninja Art: Many Releasing Flowers](https://naruto.fandom.com/wiki/Flower_Ninja_Art:_Many_Releasing_Flowers) | C | Offensive | `narutorpg:flower_ninja_art_many_releasing_flowers` |
| [Flower Shuriken: Falling Blossoms and Scattering Leaves](https://naruto.fandom.com/wiki/Flower_Shuriken:_Falling_Blossoms_and_Scattering_Leaves) | C | Offensive | `narutorpg:flower_shuriken_falling_blossoms_and_scattering_leaves` |
| [Flower–Fruit Mountain](https://naruto.fandom.com/wiki/Flower–Fruit_Mountain) | B | Offensive | `narutorpg:flowerfruit_mountain` (existing) |
| [Flowing Blade](https://naruto.fandom.com/wiki/Flowing_Blade) | C | Offensive | `narutorpg:flowing_blade` (existing) |
| [Flowing Wall Sand](https://naruto.fandom.com/wiki/Flowing_Wall_Sand) | C | Offensive | `narutorpg:flowing_wall_sand` |
| [Flying Attack](https://naruto.fandom.com/wiki/Flying_Attack) | C | Offensive | `narutorpg:flying_attack` (existing) |
| [Flying Nail Mist Soaring](https://naruto.fandom.com/wiki/Flying_Nail_Mist_Soaring) | C | Offensive | `narutorpg:flying_nail_mist_soaring` |
| [Flying Swallow](https://naruto.fandom.com/wiki/Flying_Swallow) | B | Offensive | `narutorpg:flying_swallow` (existing) |
| [Flying Swallow Barrage](https://naruto.fandom.com/wiki/Flying_Swallow_Barrage) | C | Offensive | `narutorpg:flying_swallow_barrage` (existing) |
| [Flying Swallow: Cross Cut](https://naruto.fandom.com/wiki/Flying_Swallow:_Cross_Cut) | C | Offensive | `narutorpg:flying_swallow_cross_cut` (existing) |
| [Flying Thunder Formation Technique](https://naruto.fandom.com/wiki/Flying_Thunder_Formation_Technique) | A | Supplementary | `narutorpg:flying_thunder_formation_technique` |
| [Flying Thunder God Mutually Instantaneous Revolving Technique](https://naruto.fandom.com/wiki/Flying_Thunder_God_Mutually_Instantaneous_Revolving_Technique) | A | Supplementary | `narutorpg:flying_thunder_god_mutually_instantaneous_revolving_technique` |
| [Flying Thunder God Slash](https://naruto.fandom.com/wiki/Flying_Thunder_God_Slash) | B | Offensive | `narutorpg:flying_thunder_god_slash` |
| [Flying Thunder God Technique](https://naruto.fandom.com/wiki/Flying_Thunder_God_Technique) | S | Supplementary | `narutorpg:flying_thunder_god_technique` |
| [Flying Thunder God Technique (Boruto)](https://naruto.fandom.com/wiki/Flying_Thunder_God_Technique_(Boruto)) | A | Supplementary | `narutorpg:flying_thunder_god_technique_boruto` |
| [Flying Thunder God — Second Step](https://naruto.fandom.com/wiki/Flying_Thunder_God_—_Second_Step) | A | Offensive | `narutorpg:flying_thunder_god_second_step` |
| [Flying Thunder God: Guiding Thunder](https://naruto.fandom.com/wiki/Flying_Thunder_God:_Guiding_Thunder) | A | Defensive, Supplementary | `narutorpg:flying_thunder_god_guiding_thunder` |
| [Flying Thunder God: Sequential Steps](https://naruto.fandom.com/wiki/Flying_Thunder_God:_Sequential_Steps) | A | Offensive | `narutorpg:flying_thunder_god_sequential_steps` (existing) |
| [Flying Thunder God: Space–Time Hurricane Flash Sequential Steps: Style Zero](https://naruto.fandom.com/wiki/Flying_Thunder_God:_Space–Time_Hurricane_Flash_Sequential_Steps:_Style_Zero) | A | Offensive | `narutorpg:flying_thunder_god_spacetime_hurricane_flash_sequential_steps_style_zero` |
| [Forbidden Individual Curse Tag](https://naruto.fandom.com/wiki/Forbidden_Individual_Curse_Tag) | C | Supplementary | `narutorpg:forbidden_individual_curse_tag` |
| [Forbidden Summoning Technique: Fire Rats Cometh](https://naruto.fandom.com/wiki/Forbidden_Summoning_Technique:_Fire_Rats_Cometh) | S | Offensive | `narutorpg:forbidden_summoning_technique_fire_rats_cometh` |
| [Forbidden Technique: Dead Soul Technique](https://naruto.fandom.com/wiki/Forbidden_Technique:_Dead_Soul_Technique) | C | Offensive | `narutorpg:forbidden_technique_dead_soul_technique` |
| [Forbidden Technique: Impure World Seal Destruction](https://naruto.fandom.com/wiki/Forbidden_Technique:_Impure_World_Seal_Destruction) | S | Offensive | `narutorpg:forbidden_technique_impure_world_seal_destruction` |
| [Forbidden Technique: Mud Golem](https://naruto.fandom.com/wiki/Forbidden_Technique:_Mud_Golem) | S | Offensive | `narutorpg:forbidden_technique_mud_golem` (existing) |
| [Formation Ino–Shika–Chō](https://naruto.fandom.com/wiki/Formation_Ino–Shika–Chō) | B | Offensive, Supplementary | `narutorpg:formation_inoshikacho` (existing) |
| [Formation of Ten Thousand Snakes](https://naruto.fandom.com/wiki/Formation_of_Ten_Thousand_Snakes) | B | Offensive, Defensive | `narutorpg:formation_of_ten_thousand_snakes` |
| [Four Black Fogs Formation](https://naruto.fandom.com/wiki/Four_Black_Fogs_Formation) | B | Supplementary | `narutorpg:four_black_fogs_formation` |
| [Four Black Snakes Formation](https://naruto.fandom.com/wiki/Four_Black_Snakes_Formation) | B | Offensive | `narutorpg:four_black_snakes_formation` |
| [Four Corner Seal Teleportation](https://naruto.fandom.com/wiki/Four_Corner_Seal_Teleportation) | A | Defensive, Supplementary | `narutorpg:four_corner_seal_teleportation` |
| [Four Legs Technique](https://naruto.fandom.com/wiki/Four_Legs_Technique) | D | Supplementary | `narutorpg:four_legs_technique` (existing) |
| [Four Red Yang Formation](https://naruto.fandom.com/wiki/Four_Red_Yang_Formation) | A | Defensive, Supplementary | `narutorpg:four_red_yang_formation` |
| [Four Snakes Intimidating Thrust](https://naruto.fandom.com/wiki/Four_Snakes_Intimidating_Thrust) | C | Offensive | `narutorpg:four_snakes_intimidating_thrust` |
| [Four Symbols Puppet Shinobi](https://naruto.fandom.com/wiki/Four_Symbols_Puppet_Shinobi) | D | Supplementary | `narutorpg:four_symbols_puppet_shinobi` |
| [Four Violet Flames Formation](https://naruto.fandom.com/wiki/Four_Violet_Flames_Formation) | B | Supplementary | `narutorpg:four_violet_flames_formation` |
| [Four-Corner Sealing Barrier](https://naruto.fandom.com/wiki/Four-Corner_Sealing_Barrier) | B | Supplementary | `narutorpg:four_corner_sealing_barrier` |
| [Freight Bundle Bubble](https://naruto.fandom.com/wiki/Freight_Bundle_Bubble) | C | Offensive | `narutorpg:freight_bundle_bubble` (existing) |
| [Frog Kata: Dual Gale](https://naruto.fandom.com/wiki/Frog_Kata:_Dual_Gale) | A | Offensive | `narutorpg:frog_kata_dual_gale` (existing) |
| [Frog Kata: Frog Throw](https://naruto.fandom.com/wiki/Frog_Kata:_Frog_Throw) | A | Offensive | `narutorpg:frog_kata_frog_throw` (existing) |
| [Frog Kata: Heaven Return](https://naruto.fandom.com/wiki/Frog_Kata:_Heaven_Return) | A | Offensive | `narutorpg:frog_kata_heaven_return` (existing) |
| [Frog Kata: Roaring Attack](https://naruto.fandom.com/wiki/Frog_Kata:_Roaring_Attack) | A | Offensive | `narutorpg:frog_kata_roaring_attack` (existing) |
| [Front Lotus of Love and Youth](https://naruto.fandom.com/wiki/Front_Lotus_of_Love_and_Youth) | S | Offensive | `narutorpg:front_lotus_of_love_and_youth` (existing) |
| [Front Lotus: Eight Trigrams Palms Revolving Heaven](https://naruto.fandom.com/wiki/Front_Lotus:_Eight_Trigrams_Palms_Revolving_Heaven) | S | Offensive, Defensive | `narutorpg:front_lotus_eight_trigrams_palms_revolving_heaven` (existing) |
| [Frying Pan Shield](https://naruto.fandom.com/wiki/Frying_Pan_Shield) | D | Defensive | `narutorpg:frying_pan_shield` |
| [Furious Smelting](https://naruto.fandom.com/wiki/Furious_Smelting) | C | Offensive, Defensive, Supplementary | `narutorpg:furious_smelting` (existing) |
| [Furious Smelting (game)](https://naruto.fandom.com/wiki/Furious_Smelting_(game)) | C | Offensive | `narutorpg:furious_smelting_game` |
| [Furious Smelting: Scythe](https://naruto.fandom.com/wiki/Furious_Smelting:_Scythe) | C | Offensive | `narutorpg:furious_smelting_scythe` |
| [Furious Smelting: Whirlwind](https://naruto.fandom.com/wiki/Furious_Smelting:_Whirlwind) | C | Offensive | `narutorpg:furious_smelting_whirlwind` |
| [Fury](https://naruto.fandom.com/wiki/Fury) | S | Offensive | `narutorpg:fury` (existing) |
| [Fūinjutsu Trap](https://naruto.fandom.com/wiki/Fūinjutsu_Trap) | C | Supplementary | `narutorpg:fuinjutsu_trap` |
| [Fūma Shuriken Shadow Clone](https://naruto.fandom.com/wiki/Fūma_Shuriken_Shadow_Clone) | C | Offensive | `narutorpg:fuma_shuriken_shadow_clone` |
| [Gamaken's Clumsy Shake-Down](https://naruto.fandom.com/wiki/Gamaken's_Clumsy_Shake-Down) | A | Offensive | `narutorpg:gamaken_s_clumsy_shake_down` (existing) |
| [Gelel Rasengan](https://naruto.fandom.com/wiki/Gelel_Rasengan) | B | Offensive | `narutorpg:gelel_rasengan` |
| [Genes of the First](https://naruto.fandom.com/wiki/Genes_of_the_First) | B | Offensive | `narutorpg:genes_of_the_first` (existing) |
| [Genjutsu Ball Trap](https://naruto.fandom.com/wiki/Genjutsu_Ball_Trap) | C | Defensive, Supplementary | `narutorpg:genjutsu_ball_trap` (existing) |
| [Genjutsu Protect](https://naruto.fandom.com/wiki/Genjutsu_Protect) | D | Supplementary | `narutorpg:genjutsu_protect` |
| [Genjutsu: Bell Sound Clone](https://naruto.fandom.com/wiki/Genjutsu:_Bell_Sound_Clone) | D | Supplementary | `narutorpg:genjutsu_bell_sound_clone` (existing) |
| [Genjutsu: Yaksha Shark and Birds](https://naruto.fandom.com/wiki/Genjutsu:_Yaksha_Shark_and_Birds) | B | Offensive | `narutorpg:genjutsu_yaksha_shark_and_birds` (existing) |
| [Gentle Fist Instruction](https://naruto.fandom.com/wiki/Gentle_Fist_Instruction) | B | Offensive | `narutorpg:gentle_fist_instruction` (existing) |
| [Gentle Phoenix Spiralling Twin Lion Fists](https://naruto.fandom.com/wiki/Gentle_Phoenix_Spiralling_Twin_Lion_Fists) | B | Offensive | `narutorpg:gentle_phoenix_spiralling_twin_lion_fists` (existing) |
| [Gentle Step Tailed Beast Twin Lion Fists](https://naruto.fandom.com/wiki/Gentle_Step_Tailed_Beast_Twin_Lion_Fists) | B | Offensive | `narutorpg:gentle_step_tailed_beast_twin_lion_fists` (existing) |
| [Gentle Step Twin Lion Fists](https://naruto.fandom.com/wiki/Gentle_Step_Twin_Lion_Fists) | B | Offensive, Defensive, Supplementary | `narutorpg:gentle_step_twin_lion_fists` (existing) |
| [Gentle Wind Spiralling Twin Lion Fists: Snow White](https://naruto.fandom.com/wiki/Gentle_Wind_Spiralling_Twin_Lion_Fists:_Snow_White) | B | Offensive, Defensive, Supplementary | `narutorpg:gentle_wind_spiralling_twin_lion_fists_snow_white` (existing) |
| [Giant Insect Fang: Sixty-Four Palms](https://naruto.fandom.com/wiki/Giant_Insect_Fang:_Sixty-Four_Palms) | B | Offensive | `narutorpg:giant_insect_fang_sixty_four_palms` (existing) |
| [Giant Sphere](https://naruto.fandom.com/wiki/Giant_Sphere) | C | Offensive | `narutorpg:giant_sphere` (existing) |
| [Giant Spider Drop](https://naruto.fandom.com/wiki/Giant_Spider_Drop) | A | Offensive | `narutorpg:giant_spider_drop` |
| [Giant Stick Rampage](https://naruto.fandom.com/wiki/Giant_Stick_Rampage) | C | Offensive | `narutorpg:giant_stick_rampage` |
| [Giant Tree Strong Drop](https://naruto.fandom.com/wiki/Giant_Tree_Strong_Drop) | B | Offensive | `narutorpg:giant_tree_strong_drop` (existing) |
| [God of Tides](https://naruto.fandom.com/wiki/God_of_Tides) | C | Offensive | `narutorpg:god_of_tides` |
| [God: Nativity of a World of Trees](https://naruto.fandom.com/wiki/God:_Nativity_of_a_World_of_Trees) | A | Offensive, Supplementary | `narutorpg:god_nativity_of_a_world_of_trees` |
| [Goemon Hellfire](https://naruto.fandom.com/wiki/Goemon_Hellfire) | A | Offensive | `narutorpg:goemon_hellfire` (existing) |
| [Gold Dust Cloud Spear: Thrust Away](https://naruto.fandom.com/wiki/Gold_Dust_Cloud_Spear:_Thrust_Away) | B | Offensive | `narutorpg:gold_dust_cloud_spear_thrust_away` (existing) |
| [Gold Dust Drizzle](https://naruto.fandom.com/wiki/Gold_Dust_Drizzle) | B | Offensive | `narutorpg:gold_dust_drizzle` (existing) |
| [Gold Dust Imperial Funeral](https://naruto.fandom.com/wiki/Gold_Dust_Imperial_Funeral) | B | Offensive | `narutorpg:gold_dust_imperial_funeral` (existing) |
| [Gold Dust Press](https://naruto.fandom.com/wiki/Gold_Dust_Press) | B | Offensive | `narutorpg:gold_dust_press` (existing) |
| [Gold Dust Strong Spear](https://naruto.fandom.com/wiki/Gold_Dust_Strong_Spear) | B | Offensive | `narutorpg:gold_dust_strong_spear` (existing) |
| [Gold Dust Twin Spear](https://naruto.fandom.com/wiki/Gold_Dust_Twin_Spear) | B | Offensive | `narutorpg:gold_dust_twin_spear` (existing) |
| [Gold Dust World Method Ball](https://naruto.fandom.com/wiki/Gold_Dust_World_Method_Ball) | B | Offensive | `narutorpg:gold_dust_world_method_ball` (existing) |
| [Gold and Silver Training: Treasured Tools Wild Dance](https://naruto.fandom.com/wiki/Gold_and_Silver_Training:_Treasured_Tools_Wild_Dance) | C | Offensive | `narutorpg:gold_and_silver_training_treasured_tools_wild_dance` (existing) |
| [Golden Wheel Reincarnation Explosion](https://naruto.fandom.com/wiki/Golden_Wheel_Reincarnation_Explosion) | B | Offensive | `narutorpg:golden_wheel_reincarnation_explosion` (existing) |
| [Gospel of the Angel](https://naruto.fandom.com/wiki/Gospel_of_the_Angel) | C | Offensive | `narutorpg:gospel_of_the_angel` |
| [Gozu Tennō](https://naruto.fandom.com/wiki/Gozu_Tennō) | A | Supplementary | `narutorpg:gozu_tenno` |
| [Great Burial Bullet](https://naruto.fandom.com/wiki/Great_Burial_Bullet) | S | Offensive | `narutorpg:great_burial_bullet` |
| [Great Fire Lightning Flash](https://naruto.fandom.com/wiki/Great_Fire_Lightning_Flash) | C | Offensive | `narutorpg:great_fire_lightning_flash` (existing) |
| [Great Gunpowder Attack](https://naruto.fandom.com/wiki/Great_Gunpowder_Attack) | C | Offensive | `narutorpg:great_gunpowder_attack` (existing) |
| [Great Harem Technique](https://naruto.fandom.com/wiki/Great_Harem_Technique) | C | Offensive | `narutorpg:great_harem_technique` |
| [Great Roar](https://naruto.fandom.com/wiki/Great_Roar) | C | Offensive, Defensive | `narutorpg:great_roar` |
| [Great Sand Downpour](https://naruto.fandom.com/wiki/Great_Sand_Downpour) | C | Offensive | `narutorpg:great_sand_downpour` |
| [Great Sand Eater](https://naruto.fandom.com/wiki/Great_Sand_Eater) | A | Offensive | `narutorpg:great_sand_eater` |
| [Great Sandstorm Technique](https://naruto.fandom.com/wiki/Great_Sandstorm_Technique) | B | Offensive | `narutorpg:great_sandstorm_technique` (existing) |
| [Great Sickle Weasel Technique](https://naruto.fandom.com/wiki/Great_Sickle_Weasel_Technique) | B | Offensive, Defensive | `narutorpg:great_sickle_weasel_technique` (existing) |
| [Great Snake Binding](https://naruto.fandom.com/wiki/Great_Snake_Binding) | C | Offensive | `narutorpg:great_snake_binding` |
| [Great Snake Charmer Technique](https://naruto.fandom.com/wiki/Great_Snake_Charmer_Technique) | C | Offensive | `narutorpg:great_snake_charmer_technique` |
| [Great Snake Thrust](https://naruto.fandom.com/wiki/Great_Snake_Thrust) | A | Offensive | `narutorpg:great_snake_thrust` |
| [Great Spiralling Ring](https://naruto.fandom.com/wiki/Great_Spiralling_Ring) | C | Offensive | `narutorpg:great_spiralling_ring` |
| [Great Tree Spear](https://naruto.fandom.com/wiki/Great_Tree_Spear) | B | Offensive | `narutorpg:great_tree_spear` (existing) |
| [Great Tree World Method](https://naruto.fandom.com/wiki/Great_Tree_World_Method) | B | Offensive, Defensive | `narutorpg:great_tree_world_method` (existing) |
| [Great Twin Circles](https://naruto.fandom.com/wiki/Great_Twin_Circles) | A | Offensive | `narutorpg:great_twin_circles` (existing) |
| [Great Vacuum Cannon](https://naruto.fandom.com/wiki/Great_Vacuum_Cannon) | C | Offensive | `narutorpg:great_vacuum_cannon` (existing) |
| [Great Wind Violent Wall](https://naruto.fandom.com/wiki/Great_Wind_Violent_Wall) | C | Offensive | `narutorpg:great_wind_violent_wall` (existing) |
| [Green Egg Bombs](https://naruto.fandom.com/wiki/Green_Egg_Bombs) | C | Offensive | `narutorpg:green_egg_bombs` |
| [Green Impact](https://naruto.fandom.com/wiki/Green_Impact) | C | Offensive, Defensive | `narutorpg:green_impact` (existing) |
| [Green Rain Ground Levelling](https://naruto.fandom.com/wiki/Green_Rain_Ground_Levelling) | D | Supplementary | `narutorpg:green_rain_ground_levelling` |
| [Ground Splitting Technique](https://naruto.fandom.com/wiki/Ground_Splitting_Technique) | C | Offensive | `narutorpg:ground_splitting_technique` (existing) |
| [Grudge Rain](https://naruto.fandom.com/wiki/Grudge_Rain) | B | Supplementary | `narutorpg:grudge_rain` (existing) |
| [Grudge Sand Cloud Rend](https://naruto.fandom.com/wiki/Grudge_Sand_Cloud_Rend) | C | Offensive, Defensive | `narutorpg:grudge_sand_cloud_rend` |
| [Grudge Sand Cloud Tear](https://naruto.fandom.com/wiki/Grudge_Sand_Cloud_Tear) | C | Offensive, Defensive | `narutorpg:grudge_sand_cloud_tear` |
| [Gunbai Fanned Wind](https://naruto.fandom.com/wiki/Gunbai_Fanned_Wind) | C | Offensive | `narutorpg:gunbai_fanned_wind` (existing) |
| [Gunpowder Fist](https://naruto.fandom.com/wiki/Gunpowder_Fist) | C | Offensive | `narutorpg:gunpowder_fist` (existing) |
| [Guts Rasengan](https://naruto.fandom.com/wiki/Guts_Rasengan) | C | Offensive | `narutorpg:guts_rasengan` |
| [Hair Binding Technique](https://naruto.fandom.com/wiki/Hair_Binding_Technique) | C | Offensive, Supplementary | `narutorpg:hair_binding_technique` |
| [Hair Camouflage](https://naruto.fandom.com/wiki/Hair_Camouflage) | D | Supplementary | `narutorpg:hair_camouflage` |
| [Hair Needle Senbon](https://naruto.fandom.com/wiki/Hair_Needle_Senbon) | C | Offensive | `narutorpg:hair_needle_senbon` |
| [Hands of Bonds](https://naruto.fandom.com/wiki/Hands_of_Bonds) | C | Offensive, Defensive, Supplementary | `narutorpg:hands_of_bonds` |
| [Hands of Flame](https://naruto.fandom.com/wiki/Hands_of_Flame) | C | Offensive, Defensive, Supplementary | `narutorpg:hands_of_flame` |
| [Harem Technique](https://naruto.fandom.com/wiki/Harem_Technique) | A | Supplementary | `narutorpg:harem_technique` |
| [Haze Clone Technique](https://naruto.fandom.com/wiki/Haze_Clone_Technique) | D | Supplementary | `narutorpg:haze_clone_technique` (existing) |
| [Haze Ninja Art: Forbidden Technique: Afterlife Opening Gate](https://naruto.fandom.com/wiki/Haze_Ninja_Art:_Forbidden_Technique:_Afterlife_Opening_Gate) | S | Supplementary | `narutorpg:haze_ninja_art_forbidden_technique_afterlife_opening_gate` |
| [Haze Ninja Art: Forbidden Technique: Ephemeral Life](https://naruto.fandom.com/wiki/Haze_Ninja_Art:_Forbidden_Technique:_Ephemeral_Life) | S | Supplementary | `narutorpg:haze_ninja_art_forbidden_technique_ephemeral_life` |
| [Haze Ninja Art: Forbidden Technique: Hellish Obliteration](https://naruto.fandom.com/wiki/Haze_Ninja_Art:_Forbidden_Technique:_Hellish_Obliteration) | S | Offensive, Supplementary | `narutorpg:haze_ninja_art_forbidden_technique_hellish_obliteration` |
| [Haze Ninja Art: Forbidden Technique: Red Spider Lily](https://naruto.fandom.com/wiki/Haze_Ninja_Art:_Forbidden_Technique:_Red_Spider_Lily) | S | Offensive | `narutorpg:haze_ninja_art_forbidden_technique_red_spider_lily` |
| [Hazy Moon](https://naruto.fandom.com/wiki/Hazy_Moon) | A | Offensive | `narutorpg:hazy_moon` |
| [Heal Bite](https://naruto.fandom.com/wiki/Heal_Bite) | D | Supplementary | `narutorpg:heal_bite` (existing) |
| [Healing Resuscitation Regeneration Technique](https://naruto.fandom.com/wiki/Healing_Resuscitation_Regeneration_Technique) | A | Supplementary | `narutorpg:healing_resuscitation_regeneration_technique` |
| [Healing Technique](https://naruto.fandom.com/wiki/Healing_Technique) | D | Supplementary | `narutorpg:healing_technique` |
| [Heart Acceleration Technique](https://naruto.fandom.com/wiki/Heart_Acceleration_Technique) | D | Supplementary | `narutorpg:heart_acceleration_technique` |
| [Heaven Gold](https://naruto.fandom.com/wiki/Heaven_Gold) | C | Offensive | `narutorpg:heaven_gold` |
| [Heaven Twin Fang](https://naruto.fandom.com/wiki/Heaven_Twin_Fang) | B | Offensive, Defensive | `narutorpg:heaven_twin_fang` (existing) |
| [Heaven and Earth Exploding Funeral](https://naruto.fandom.com/wiki/Heaven_and_Earth_Exploding_Funeral) | A | Offensive | `narutorpg:heaven_and_earth_exploding_funeral` |
| [Heaven and Earth Lightning Roar](https://naruto.fandom.com/wiki/Heaven_and_Earth_Lightning_Roar) | C | Offensive | `narutorpg:heaven_and_earth_lightning_roar` (existing) |
| [Heaven-Bending Bomb Assault](https://naruto.fandom.com/wiki/Heaven-Bending_Bomb_Assault) | A | Offensive | `narutorpg:heaven_bending_bomb_assault` |
| [Heavenly Advent](https://naruto.fandom.com/wiki/Heavenly_Advent) | A | Offensive | `narutorpg:heavenly_advent` |
| [Heavenly Chidori: Cherry Blossom](https://naruto.fandom.com/wiki/Heavenly_Chidori:_Cherry_Blossom) | A | Offensive | `narutorpg:heavenly_chidori_cherry_blossom` (existing) |
| [Heavenly Foot of Pain: Kirin](https://naruto.fandom.com/wiki/Heavenly_Foot_of_Pain:_Kirin) | B | Offensive | `narutorpg:heavenly_foot_of_pain_kirin` (existing) |
| [Heavenly Ice Chamber](https://naruto.fandom.com/wiki/Heavenly_Ice_Chamber) | A | Offensive, Defensive, Supplementary | `narutorpg:heavenly_ice_chamber` |
| [Heavenly Insertion Hammering Star](https://naruto.fandom.com/wiki/Heavenly_Insertion_Hammering_Star) | B | Offensive | `narutorpg:heavenly_insertion_hammering_star` (existing) |
| [Heavenly Lightning](https://naruto.fandom.com/wiki/Heavenly_Lightning) | C | Offensive | `narutorpg:heavenly_lightning` (existing) |
| [Heavenly Prison Fire Sword](https://naruto.fandom.com/wiki/Heavenly_Prison_Fire_Sword) | C | Offensive | `narutorpg:heavenly_prison_fire_sword` (existing) |
| [Heavenly Punishment](https://naruto.fandom.com/wiki/Heavenly_Punishment) | C | Offensive | `narutorpg:heavenly_punishment` (existing) |
| [Heavenly Transfer Technique](https://naruto.fandom.com/wiki/Heavenly_Transfer_Technique) | D | Supplementary | `narutorpg:heavenly_transfer_technique` |
| [Heavenly Weeping](https://naruto.fandom.com/wiki/Heavenly_Weeping) | C | Offensive | `narutorpg:heavenly_weeping` (existing) |
| [Heavy Hammer Spark](https://naruto.fandom.com/wiki/Heavy_Hammer_Spark) | C | Offensive | `narutorpg:heavy_hammer_spark` (existing) |
| [Hell Dark Formation](https://naruto.fandom.com/wiki/Hell_Dark_Formation) | B | Offensive, Supplementary | `narutorpg:hell_dark_formation` |
| [Hell Stab Four-Finger Spear Hand: Sky Drop](https://naruto.fandom.com/wiki/Hell_Stab_Four-Finger_Spear_Hand:_Sky_Drop) | C | Offensive | `narutorpg:hell_stab_four_finger_spear_hand_sky_drop` (existing) |
| [Hell Stab One-Finger Nukite: Ultimate](https://naruto.fandom.com/wiki/Hell_Stab_One-Finger_Nukite:_Ultimate) | C | Offensive | `narutorpg:hell_stab_one_finger_nukite_ultimate` (existing) |
| [Hell Stab Three-Finger Spear Hand: Side Swipe](https://naruto.fandom.com/wiki/Hell_Stab_Three-Finger_Spear_Hand:_Side_Swipe) | C | Offensive | `narutorpg:hell_stab_three_finger_spear_hand_side_swipe` (existing) |
| [Hellfire Gale](https://naruto.fandom.com/wiki/Hellfire_Gale) | C | Offensive | `narutorpg:hellfire_gale` (existing) |
| [Hero of the Hidden Sand](https://naruto.fandom.com/wiki/Hero_of_the_Hidden_Sand) | D | Supplementary | `narutorpg:hero_of_the_hidden_sand` (existing) |
| [Hero of the Sharingan](https://naruto.fandom.com/wiki/Hero_of_the_Sharingan) | C | Offensive | `narutorpg:hero_of_the_sharingan` (existing) |
| [Hidden Doorway Technique](https://naruto.fandom.com/wiki/Hidden_Doorway_Technique) | D | Supplementary | `narutorpg:hidden_doorway_technique` |
| [Hidden Rock Trap](https://naruto.fandom.com/wiki/Hidden_Rock_Trap) | C | Offensive, Supplementary | `narutorpg:hidden_rock_trap` (existing) |
| [Hidden Shadow Snake Burial](https://naruto.fandom.com/wiki/Hidden_Shadow_Snake_Burial) | C | Offensive | `narutorpg:hidden_shadow_snake_burial` |
| [Hidden Shadow Snake Hands](https://naruto.fandom.com/wiki/Hidden_Shadow_Snake_Hands) | C | Offensive | `narutorpg:hidden_shadow_snake_hands` |
| [Hidden Shadow Wild Snake Hands](https://naruto.fandom.com/wiki/Hidden_Shadow_Wild_Snake_Hands) | A | Offensive | `narutorpg:hidden_shadow_wild_snake_hands` |
| [Hiden: Hiding in Scale Powder Technique](https://naruto.fandom.com/wiki/Hiden:_Hiding_in_Scale_Powder_Technique) | C | Supplementary | `narutorpg:hiden_hiding_in_scale_powder_technique` |
| [Hiden: Scale Powder Enfolding Attack Technique](https://naruto.fandom.com/wiki/Hiden:_Scale_Powder_Enfolding_Attack_Technique) | C | Offensive | `narutorpg:hiden_scale_powder_enfolding_attack_technique` |
| [Hiden: Scale Powder Technique](https://naruto.fandom.com/wiki/Hiden:_Scale_Powder_Technique) | C | Offensive | `narutorpg:hiden_scale_powder_technique` |
| [Hideout Destruction Trap](https://naruto.fandom.com/wiki/Hideout_Destruction_Trap) | C | Offensive, Supplementary | `narutorpg:hideout_destruction_trap` |
| [Hiding in Black Fog Technique](https://naruto.fandom.com/wiki/Hiding_in_Black_Fog_Technique) | D | Supplementary | `narutorpg:hiding_in_black_fog_technique` |
| [Hiding in Cloth Technique](https://naruto.fandom.com/wiki/Hiding_in_Cloth_Technique) | D | Defensive, Supplementary | `narutorpg:hiding_in_cloth_technique` |
| [Hiding in Frost Technique](https://naruto.fandom.com/wiki/Hiding_in_Frost_Technique) | D | Supplementary | `narutorpg:hiding_in_frost_technique` (existing) |
| [Hiding in Mist Technique](https://naruto.fandom.com/wiki/Hiding_in_Mist_Technique) | D | Supplementary | `narutorpg:hiding_in_mist_technique` (existing) |
| [Hiding in Shadow Technique](https://naruto.fandom.com/wiki/Hiding_in_Shadow_Technique) | D | Supplementary | `narutorpg:hiding_in_shadow_technique` |
| [Hiding in Surface Technique](https://naruto.fandom.com/wiki/Hiding_in_Surface_Technique) | D | Supplementary | `narutorpg:hiding_in_surface_technique` (existing) |
| [Hiding in a Toad Technique](https://naruto.fandom.com/wiki/Hiding_in_a_Toad_Technique) | C | Supplementary | `narutorpg:hiding_in_a_toad_technique` |
| [Hiding with Camouflage Technique](https://naruto.fandom.com/wiki/Hiding_with_Camouflage_Technique) | A | Supplementary | `narutorpg:hiding_with_camouflage_technique` (existing) |
| [Hinokagutsuchi](https://naruto.fandom.com/wiki/Hinokagutsuchi) | B | Offensive | `narutorpg:hinokagutsuchi` (existing) |
| [Hog Release: Crazed Fireworks](https://naruto.fandom.com/wiki/Hog_Release:_Crazed_Fireworks) | B | Offensive | `narutorpg:hog_release_crazed_fireworks` |
| [Hog Release: Tonton](https://naruto.fandom.com/wiki/Hog_Release:_Tonton) | B | Offensive | `narutorpg:hog_release_tonton` |
| [Hokage-Style Sixty-Year-Old Technique — Kakuan Entering Society with Bliss-Bringing Hands](https://naruto.fandom.com/wiki/Hokage-Style_Sixty-Year-Old_Technique_—_Kakuan_Entering_Society_with_Bliss-Bringing_Hands) | B | Supplementary | `narutorpg:hokage_style_sixty_year_old_technique_kakuan_entering_society_with_bliss_bringing_hands` (existing) |
| [Honey Spit Trap](https://naruto.fandom.com/wiki/Honey_Spit_Trap) | D | Supplementary | `narutorpg:honey_spit_trap` |
| [Horizon Meteor](https://naruto.fandom.com/wiki/Horizon_Meteor) | B | Offensive, Supplementary | `narutorpg:horizon_meteor` (existing) |
| [Horror of Akatsuki](https://naruto.fandom.com/wiki/Horror_of_Akatsuki) | S | Offensive | `narutorpg:horror_of_akatsuki` (existing) |
| [Host Transformation](https://naruto.fandom.com/wiki/Host_Transformation) | C | Offensive | `narutorpg:host_transformation` (existing) |
| [Hot Sand Waterfall Funeral](https://naruto.fandom.com/wiki/Hot_Sand_Waterfall_Funeral) | C | Offensive | `narutorpg:hot_sand_waterfall_funeral` (existing) |
| [Hot-Blooded Dynamic Entry](https://naruto.fandom.com/wiki/Hot-Blooded_Dynamic_Entry) | B | Offensive | `narutorpg:hot_blooded_dynamic_entry` (existing) |
| [Howling Hair Sound Wave](https://naruto.fandom.com/wiki/Howling_Hair_Sound_Wave) | C | Offensive, Supplementary | `narutorpg:howling_hair_sound_wave` |
| [Human Beast Combination Transformation: Double-Headed Wolf](https://naruto.fandom.com/wiki/Human_Beast_Combination_Transformation:_Double-Headed_Wolf) | B | Supplementary | `narutorpg:human_beast_combination_transformation_double_headed_wolf` (existing) |
| [Human Beast Mixture Transformation — Three-Headed Wolf](https://naruto.fandom.com/wiki/Human_Beast_Mixture_Transformation_—_Three-Headed_Wolf) | B | Supplementary | `narutorpg:human_beast_mixture_transformation_three_headed_wolf` (existing) |
| [Human Bullet Yo-Yo](https://naruto.fandom.com/wiki/Human_Bullet_Yo-Yo) | B | Offensive, Defensive | `narutorpg:human_bullet_yo_yo` (existing) |
| [Human Cocoon Technique](https://naruto.fandom.com/wiki/Human_Cocoon_Technique) | C | Supplementary | `narutorpg:human_cocoon_technique` (existing) |
| [Human Path](https://naruto.fandom.com/wiki/Human_Path) | B | Offensive, Supplementary | `narutorpg:human_path` (existing) |
| [Human Shadow Simulation Technique](https://naruto.fandom.com/wiki/Human_Shadow_Simulation_Technique) | C | Offensive | `narutorpg:human_shadow_simulation_technique` (existing) |
| [Hundred Puppet Soul Binding](https://naruto.fandom.com/wiki/Hundred_Puppet_Soul_Binding) | D | Supplementary | `narutorpg:hundred_puppet_soul_binding` |
| [Hungry Shark Drop](https://naruto.fandom.com/wiki/Hungry_Shark_Drop) | C | Offensive | `narutorpg:hungry_shark_drop` (existing) |
| [Hunt](https://naruto.fandom.com/wiki/Hunt) | A | Offensive | `narutorpg:hunt` (existing) |
| [Hurricane Thunderclap](https://naruto.fandom.com/wiki/Hurricane_Thunderclap) | B | Offensive | `narutorpg:hurricane_thunderclap` |
| [Hurricane Thunderclap — Majestic Attire Sword Stroke](https://naruto.fandom.com/wiki/Hurricane_Thunderclap_—_Majestic_Attire_Sword_Stroke) | B | Offensive | `narutorpg:hurricane_thunderclap_majestic_attire_sword_stroke` (existing) |
| [Husband and Wife Rasengan](https://naruto.fandom.com/wiki/Husband_and_Wife_Rasengan) | A | Offensive | `narutorpg:husband_and_wife_rasengan` (existing) |
| [Hydrification Technique](https://naruto.fandom.com/wiki/Hydrification_Technique) | C | Supplementary | `narutorpg:hydrification_technique` (existing) |
| [Hyūga Great Combo Palm](https://naruto.fandom.com/wiki/Hyūga_Great_Combo_Palm) | B | Offensive | `narutorpg:hyuga_great_combo_palm` (existing) |
| [Hyūga: Great Revolving Heaven](https://naruto.fandom.com/wiki/Hyūga:_Great_Revolving_Heaven) | B | Offensive, Defensive | `narutorpg:hyuga_great_revolving_heaven` (existing) |
| [Iburi Clan's Kekkei Genkai](https://naruto.fandom.com/wiki/Iburi_Clan's_Kekkei_Genkai) | B | - | `narutorpg:iburi_clan_s_kekkei_genkai` (existing) |
| [Ibuse's Poison Mist](https://naruto.fandom.com/wiki/Ibuse's_Poison_Mist) | A | Offensive | `narutorpg:ibuse_s_poison_mist` |
| [Ice Disk Technique](https://naruto.fandom.com/wiki/Ice_Disk_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:ice_disk_technique` |
| [Ice Prison Technique](https://naruto.fandom.com/wiki/Ice_Prison_Technique) | C | Offensive, Defensive | `narutorpg:ice_prison_technique` (existing) |
| [Ice Release](https://naruto.fandom.com/wiki/Ice_Release) | B | - | `narutorpg:ice_release` (existing) |
| [Ice Release: Black Dragon Blizzard](https://naruto.fandom.com/wiki/Ice_Release:_Black_Dragon_Blizzard) | C | Offensive | `narutorpg:ice_release_black_dragon_blizzard` (existing) |
| [Ice Release: Crystal Wall](https://naruto.fandom.com/wiki/Ice_Release:_Crystal_Wall) | B | Supplementary | `narutorpg:ice_release_crystal_wall` (existing) |
| [Ice Release: Earthen Consecutive Chains of Ice](https://naruto.fandom.com/wiki/Ice_Release:_Earthen_Consecutive_Chains_of_Ice) | B | Supplementary | `narutorpg:ice_release_earthen_consecutive_chains_of_ice` (existing) |
| [Ice Release: Frozen Capturing Field](https://naruto.fandom.com/wiki/Ice_Release:_Frozen_Capturing_Field) | B | Supplementary | `narutorpg:ice_release_frozen_capturing_field` (existing) |
| [Ice Release: Ice Peak Crumbling](https://naruto.fandom.com/wiki/Ice_Release:_Ice_Peak_Crumbling) | B | Offensive | `narutorpg:ice_release_ice_peak_crumbling` (existing) |
| [Ice Release: Ice Rock Dome of Magnificent Nothingness](https://naruto.fandom.com/wiki/Ice_Release:_Ice_Rock_Dome_of_Magnificent_Nothingness) | B | Defensive | `narutorpg:ice_release_ice_rock_dome_of_magnificent_nothingness` (existing) |
| [Ice Release: Ice-Breaking Sledgehammer](https://naruto.fandom.com/wiki/Ice_Release:_Ice-Breaking_Sledgehammer) | B | Offensive | `narutorpg:ice_release_ice_breaking_sledgehammer` (existing) |
| [Ice Release: Icicle Combo](https://naruto.fandom.com/wiki/Ice_Release:_Icicle_Combo) | B | Offensive | `narutorpg:ice_release_icicle_combo` (existing) |
| [Ice Release: Icicle Swallow](https://naruto.fandom.com/wiki/Ice_Release:_Icicle_Swallow) | B | Offensive | `narutorpg:ice_release_icicle_swallow` (existing) |
| [Ice Release: One Horned White Whale](https://naruto.fandom.com/wiki/Ice_Release:_One_Horned_White_Whale) | C | Offensive, Defensive, Supplementary | `narutorpg:ice_release_one_horned_white_whale` (existing) |
| [Ice Release: Swallow Snow Storm](https://naruto.fandom.com/wiki/Ice_Release:_Swallow_Snow_Storm) | C | Offensive | `narutorpg:ice_release_swallow_snow_storm` (existing) |
| [Ice Release: Tearing Dragon Fierce Tiger](https://naruto.fandom.com/wiki/Ice_Release:_Tearing_Dragon_Fierce_Tiger) | C | Offensive | `narutorpg:ice_release_tearing_dragon_fierce_tiger` (existing) |
| [Ice Release: Ten Thousand Ice Petals](https://naruto.fandom.com/wiki/Ice_Release:_Ten_Thousand_Ice_Petals) | B | Offensive, Defensive | `narutorpg:ice_release_ten_thousand_ice_petals` (existing) |
| [Ice Release: Twin Dragon Blizzard](https://naruto.fandom.com/wiki/Ice_Release:_Twin_Dragon_Blizzard) | C | Offensive | `narutorpg:ice_release_twin_dragon_blizzard` (existing) |
| [Ice Release: Whirlwind](https://naruto.fandom.com/wiki/Ice_Release:_Whirlwind) | B | Offensive | `narutorpg:ice_release_whirlwind` (existing) |
| [Ice Release: Wolf Fang Avalanche Technique](https://naruto.fandom.com/wiki/Ice_Release:_Wolf_Fang_Avalanche_Technique) | C | Offensive | `narutorpg:ice_release_wolf_fang_avalanche_technique` (existing) |
| [Ice Sword Technique](https://naruto.fandom.com/wiki/Ice_Sword_Technique) | B | Offensive | `narutorpg:ice_sword_technique` (existing) |
| [Ice-Breaking Fist](https://naruto.fandom.com/wiki/Ice-Breaking_Fist) | B | Offensive, Defensive | `narutorpg:ice_breaking_fist` (existing) |
| [Immortal Body](https://naruto.fandom.com/wiki/Immortal_Body) | C | Supplementary | `narutorpg:immortal_body` |
| [Impaling Spear Tree](https://naruto.fandom.com/wiki/Impaling_Spear_Tree) | B | Offensive | `narutorpg:impaling_spear_tree` (existing) |
| [Impermanence of Worldly Things](https://naruto.fandom.com/wiki/Impermanence_of_Worldly_Things) | B | Offensive | `narutorpg:impermanence_of_worldly_things` |
| [Improvised Secret Technique: Lightning Ball Shuriken Technique](https://naruto.fandom.com/wiki/Improvised_Secret_Technique:_Lightning_Ball_Shuriken_Technique) | B | Offensive | `narutorpg:improvised_secret_technique_lightning_ball_shuriken_technique` (existing) |
| [Indra's Arrow](https://naruto.fandom.com/wiki/Indra's_Arrow) | B | Offensive | `narutorpg:indra_s_arrow` (existing) |
| [Infinite Embrace](https://naruto.fandom.com/wiki/Infinite_Embrace) | B | Offensive | `narutorpg:infinite_embrace` (existing) |
| [Ink Clone Technique](https://naruto.fandom.com/wiki/Ink_Clone_Technique) | C | Supplementary | `narutorpg:ink_clone_technique` |
| [Ink Creation](https://naruto.fandom.com/wiki/Ink_Creation) | D | Supplementary | `narutorpg:ink_creation` (existing) |
| [Ink Flush](https://naruto.fandom.com/wiki/Ink_Flush) | D | Supplementary | `narutorpg:ink_flush` |
| [Ink Mist Technique](https://naruto.fandom.com/wiki/Ink_Mist_Technique) | C | Supplementary | `narutorpg:ink_mist_technique` |
| [Inner Sakura](https://naruto.fandom.com/wiki/Inner_Sakura) | C | Offensive | `narutorpg:inner_sakura` (existing) |
| [Insect Clone Technique](https://naruto.fandom.com/wiki/Insect_Clone_Technique) | C | Supplementary | `narutorpg:insect_clone_technique` (existing) |
| [Insect Deception Technique](https://naruto.fandom.com/wiki/Insect_Deception_Technique) | C | Offensive, Supplementary | `narutorpg:insect_deception_technique` (existing) |
| [Insect Jamming Technique](https://naruto.fandom.com/wiki/Insect_Jamming_Technique) | C | Supplementary | `narutorpg:insect_jamming_technique` (existing) |
| [Insect Jar Technique](https://naruto.fandom.com/wiki/Insect_Jar_Technique) | C | Defensive | `narutorpg:insect_jar_technique` (existing) |
| [Insect Pillar](https://naruto.fandom.com/wiki/Insect_Pillar) | C | - | `narutorpg:insect_pillar` |
| [Insect Pillar Technique](https://naruto.fandom.com/wiki/Insect_Pillar_Technique) | C | Offensive, Defensive | `narutorpg:insect_pillar_technique` |
| [Insect Rope Technique](https://naruto.fandom.com/wiki/Insect_Rope_Technique) | C | Supplementary | `narutorpg:insect_rope_technique` (existing) |
| [Insect Spying Technique](https://naruto.fandom.com/wiki/Insect_Spying_Technique) | D | Supplementary | `narutorpg:insect_spying_technique` |
| [Insect Webbing](https://naruto.fandom.com/wiki/Insect_Webbing) | D | Supplementary | `narutorpg:insect_webbing` |
| [Instant Ice Encasing Technique](https://naruto.fandom.com/wiki/Instant_Ice_Encasing_Technique) | C | Offensive, Supplementary | `narutorpg:instant_ice_encasing_technique` |
| [Instant Lightning: Combination Tag](https://naruto.fandom.com/wiki/Instant_Lightning:_Combination_Tag) | A | Offensive | `narutorpg:instant_lightning_combination_tag` (existing) |
| [Instant Path](https://naruto.fandom.com/wiki/Instant_Path) | A | Offensive, Supplementary | `narutorpg:instant_path` (existing) |
| [Instant Water](https://naruto.fandom.com/wiki/Instant_Water) | C | Offensive | `narutorpg:instant_water` (existing) |
| [Intelligence Transmission](https://naruto.fandom.com/wiki/Intelligence_Transmission) | D | Supplementary | `narutorpg:intelligence_transmission` |
| [Intense Passion: Ferocious Tiger Lotus Peacock Attack](https://naruto.fandom.com/wiki/Intense_Passion:_Ferocious_Tiger_Lotus_Peacock_Attack) | S | Offensive | `narutorpg:intense_passion_ferocious_tiger_lotus_peacock_attack` (existing) |
| [Internal Weapon](https://naruto.fandom.com/wiki/Internal_Weapon) | B | Offensive | `narutorpg:internal_weapon` (existing) |
| [Inukaitakerunomikoto](https://naruto.fandom.com/wiki/Inukaitakerunomikoto) | C | Offensive, Supplementary | `narutorpg:inukaitakerunomikoto` |
| [Inuzuka Clan Glory](https://naruto.fandom.com/wiki/Inuzuka_Clan_Glory) | B | Offensive | `narutorpg:inuzuka_clan_glory` (existing) |
| [Invariant Connection](https://naruto.fandom.com/wiki/Invariant_Connection) | S | Offensive, Defensive | `narutorpg:invariant_connection` (existing) |
| [Iron Ball Trap](https://naruto.fandom.com/wiki/Iron_Ball_Trap) | C | Offensive | `narutorpg:iron_ball_trap` |
| [Iron Mountain Leaning](https://naruto.fandom.com/wiki/Iron_Mountain_Leaning) | C | Offensive | `narutorpg:iron_mountain_leaning` (existing) |
| [Iron Sand Body](https://naruto.fandom.com/wiki/Iron_Sand_Body) | B | Supplementary | `narutorpg:iron_sand_body` (existing) |
| [Iron Sand Chain World](https://naruto.fandom.com/wiki/Iron_Sand_Chain_World) | B | Offensive | `narutorpg:iron_sand_chain_world` (existing) |
| [Iron Sand Clone](https://naruto.fandom.com/wiki/Iron_Sand_Clone) | B | Supplementary | `narutorpg:iron_sand_clone` (existing) |
| [Iron Sand Drizzle](https://naruto.fandom.com/wiki/Iron_Sand_Drizzle) | B | Offensive | `narutorpg:iron_sand_drizzle` (existing) |
| [Iron Sand Funeral](https://naruto.fandom.com/wiki/Iron_Sand_Funeral) | B | Offensive | `narutorpg:iron_sand_funeral` (existing) |
| [Iron Sand Gathering Assault](https://naruto.fandom.com/wiki/Iron_Sand_Gathering_Assault) | B | Offensive | `narutorpg:iron_sand_gathering_assault` (existing) |
| [Iron Sand Gathering Assault: Drop](https://naruto.fandom.com/wiki/Iron_Sand_Gathering_Assault:_Drop) | B | Offensive | `narutorpg:iron_sand_gathering_assault_drop` (existing) |
| [Iron Sand Gathering Assault: Giant Hammer](https://naruto.fandom.com/wiki/Iron_Sand_Gathering_Assault:_Giant_Hammer) | B | Offensive | `narutorpg:iron_sand_gathering_assault_giant_hammer` (existing) |
| [Iron Sand Gathering Assault: Wedge](https://naruto.fandom.com/wiki/Iron_Sand_Gathering_Assault:_Wedge) | B | Offensive | `narutorpg:iron_sand_gathering_assault_wedge` (existing) |
| [Iron Sand Marking](https://naruto.fandom.com/wiki/Iron_Sand_Marking) | B | Supplementary | `narutorpg:iron_sand_marking` (existing) |
| [Iron Sand Mountain Range](https://naruto.fandom.com/wiki/Iron_Sand_Mountain_Range) | B | Offensive | `narutorpg:iron_sand_mountain_range` (existing) |
| [Iron Sand Scattered Showers](https://naruto.fandom.com/wiki/Iron_Sand_Scattered_Showers) | B | Offensive | `narutorpg:iron_sand_scattered_showers` (existing) |
| [Iron Sand Sky Wings](https://naruto.fandom.com/wiki/Iron_Sand_Sky_Wings) | B | Offensive | `narutorpg:iron_sand_sky_wings` (existing) |
| [Iron Sand Wall](https://naruto.fandom.com/wiki/Iron_Sand_Wall) | B | Defensive | `narutorpg:iron_sand_wall` (existing) |
| [Iron Sand Wave](https://naruto.fandom.com/wiki/Iron_Sand_Wave) | B | Offensive | `narutorpg:iron_sand_wave` (existing) |
| [Iron Sand Weaponry](https://naruto.fandom.com/wiki/Iron_Sand_Weaponry) | B | Offensive | `narutorpg:iron_sand_weaponry` (existing) |
| [Iron Sand World Method](https://naruto.fandom.com/wiki/Iron_Sand_World_Method) | B | Offensive | `narutorpg:iron_sand_world_method` (existing) |
| [Iron Sand World Method: Earth Break](https://naruto.fandom.com/wiki/Iron_Sand_World_Method:_Earth_Break) | B | Offensive | `narutorpg:iron_sand_world_method_earth_break` (existing) |
| [Iron Sand: Black Iron Wings](https://naruto.fandom.com/wiki/Iron_Sand:_Black_Iron_Wings) | B | Offensive, Supplementary | `narutorpg:iron_sand_black_iron_wings` (existing) |
| [Iwagakure Kinjutsu](https://naruto.fandom.com/wiki/Iwagakure_Kinjutsu) | S | Supplementary | `narutorpg:iwagakure_kinjutsu` |
| [Jade Crystal Clone Technique](https://naruto.fandom.com/wiki/Jade_Crystal_Clone_Technique) | B | Supplementary | `narutorpg:jade_crystal_clone_technique` (existing) |
| [Jar of Poison Technique](https://naruto.fandom.com/wiki/Jar_of_Poison_Technique) | S | Offensive | `narutorpg:jar_of_poison_technique` (existing) |
| [Jet Booster Jump](https://naruto.fandom.com/wiki/Jet_Booster_Jump) | A | Supplementary | `narutorpg:jet_booster_jump` (existing) |
| [Jūgo's Clan's Kekkei Genkai](https://naruto.fandom.com/wiki/Jūgo's_Clan's_Kekkei_Genkai) | B | - | `narutorpg:jugo_s_clan_s_kekkei_genkai` (existing) |
| [Kagura Shuriken](https://naruto.fandom.com/wiki/Kagura_Shuriken) | C | Offensive | `narutorpg:kagura_shuriken` |
| [Kagura Square Formation](https://naruto.fandom.com/wiki/Kagura_Square_Formation) | C | Offensive, Defensive | `narutorpg:kagura_square_formation` |
| [Kagutsuchi Rasengan](https://naruto.fandom.com/wiki/Kagutsuchi_Rasengan) | S | Offensive | `narutorpg:kagutsuchi_rasengan` (existing) |
| [Kaima Form](https://naruto.fandom.com/wiki/Kaima_Form) | D | Supplementary | `narutorpg:kaima_form` |
| [Kamui](https://naruto.fandom.com/wiki/Kamui) | A | Offensive, Defensive, Supplementary | `narutorpg:kamui` (existing) |
| [Kamui Lightning Cutter](https://naruto.fandom.com/wiki/Kamui_Lightning_Cutter) | A | Offensive | `narutorpg:kamui_lightning_cutter` (existing) |
| [Kamui Running](https://naruto.fandom.com/wiki/Kamui_Running) | A | Defensive, Supplementary | `narutorpg:kamui_running` (existing) |
| [Kamui Running: Brutal](https://naruto.fandom.com/wiki/Kamui_Running:_Brutal) | A | Offensive, Supplementary | `narutorpg:kamui_running_brutal` (existing) |
| [Kamui Shuriken](https://naruto.fandom.com/wiki/Kamui_Shuriken) | A | Offensive | `narutorpg:kamui_shuriken` (existing) |
| [Kanzeon Lotus King](https://naruto.fandom.com/wiki/Kanzeon_Lotus_King) | B | Offensive | `narutorpg:kanzeon_lotus_king` (existing) |
| [Kara Savagery](https://naruto.fandom.com/wiki/Kara_Savagery) | S | Offensive | `narutorpg:kara_savagery` (existing) |
| [Katsuyu: Immense Network Healing](https://naruto.fandom.com/wiki/Katsuyu:_Immense_Network_Healing) | A | Supplementary | `narutorpg:katsuyu_immense_network_healing` |
| [Kick Attack Crash](https://naruto.fandom.com/wiki/Kick_Attack_Crash) | A | Offensive | `narutorpg:kick_attack_crash` (existing) |
| [Kido's Juinjutsu](https://naruto.fandom.com/wiki/Kido's_Juinjutsu) | C | Supplementary | `narutorpg:kido_s_juinjutsu` |
| [Killing Cursed Seal](https://naruto.fandom.com/wiki/Killing_Cursed_Seal) | D | Supplementary | `narutorpg:killing_cursed_seal` |
| [Kirin](https://naruto.fandom.com/wiki/Kirin) | S | Offensive | `narutorpg:kirin` (existing) |
| [Kongukan](https://naruto.fandom.com/wiki/Kongukan) | S | Offensive | `narutorpg:kongukan` (existing) |
| [Konoha Jōseki Formation](https://naruto.fandom.com/wiki/Konoha_Jōseki_Formation) | B | Offensive, Supplementary | `narutorpg:konoha_joseki_formation` (existing) |
| [Konoha: True Thousand Hokage Hands](https://naruto.fandom.com/wiki/Konoha:_True_Thousand_Hokage_Hands) | A | Offensive | `narutorpg:konoha_true_thousand_hokage_hands` (existing) |
| [Konohamaru Army Corps! Reach the Goal!](https://naruto.fandom.com/wiki/Konohamaru_Army_Corps!_Reach_the_Goal!) | A | Offensive | `narutorpg:konohamaru_army_corps_reach_the_goal` (existing) |
| [Kunoichi Rumble](https://naruto.fandom.com/wiki/Kunoichi_Rumble) | B | Offensive | `narutorpg:kunoichi_rumble` (existing) |
| [Kurama Arm Attack](https://naruto.fandom.com/wiki/Kurama_Arm_Attack) | D | Supplementary | `narutorpg:kurama_arm_attack` |
| [Kurama Claw Slice](https://naruto.fandom.com/wiki/Kurama_Claw_Slice) | C | Offensive | `narutorpg:kurama_claw_slice` |
| [Kurama Great Rotating Attack](https://naruto.fandom.com/wiki/Kurama_Great_Rotating_Attack) | C | Offensive | `narutorpg:kurama_great_rotating_attack` |
| [Kurama Twin Palms Attack](https://naruto.fandom.com/wiki/Kurama_Twin_Palms_Attack) | C | Offensive | `narutorpg:kurama_twin_palms_attack` |
| [Kurama's Shroud](https://naruto.fandom.com/wiki/Kurama's_Shroud) | C | Offensive | `narutorpg:kurama_s_shroud` |
| [Kuramitsuha](https://naruto.fandom.com/wiki/Kuramitsuha) | C | Offensive | `narutorpg:kuramitsuha` |
| [Kāma](https://naruto.fandom.com/wiki/Kāma) | S | Supplementary | `narutorpg:kama` (existing) |
| [Kāma Rasengan](https://naruto.fandom.com/wiki/Kāma_Rasengan) | S | Offensive | `narutorpg:kama_rasengan` (existing) |
| [Kāma: Spiral](https://naruto.fandom.com/wiki/Kāma:_Spiral) | S | Offensive | `narutorpg:kama_spiral` |
| [Labyrinth Barrier](https://naruto.fandom.com/wiki/Labyrinth_Barrier) | C | Supplementary | `narutorpg:labyrinth_barrier` |
| [Landmine Field Technique](https://naruto.fandom.com/wiki/Landmine_Field_Technique) | C | Offensive | `narutorpg:landmine_field_technique` (existing) |
| [Landmine Great Plain Technique](https://naruto.fandom.com/wiki/Landmine_Great_Plain_Technique) | C | Offensive | `narutorpg:landmine_great_plain_technique` (existing) |
| [Landmine Lance-ification Technique](https://naruto.fandom.com/wiki/Landmine_Lance-ification_Technique) | B | Offensive | `narutorpg:landmine_lance_ification_technique` (existing) |
| [Lava Release](https://naruto.fandom.com/wiki/Lava_Release) | B | - | `narutorpg:lava_release` (existing) |
| [Lava Release Chakra Mode](https://naruto.fandom.com/wiki/Lava_Release_Chakra_Mode) | B | Offensive, Defensive | `narutorpg:lava_release_chakra_mode` (existing) |
| [Lava Release: Ash Stone Seal Technique](https://naruto.fandom.com/wiki/Lava_Release:_Ash_Stone_Seal_Technique) | B | Supplementary | `narutorpg:lava_release_ash_stone_seal_technique` (existing) |
| [Lava Release: Dissolving Exploding Acid](https://naruto.fandom.com/wiki/Lava_Release:_Dissolving_Exploding_Acid) | B | Offensive | `narutorpg:lava_release_dissolving_exploding_acid` (existing) |
| [Lava Release: Expanding Flame Rings](https://naruto.fandom.com/wiki/Lava_Release:_Expanding_Flame_Rings) | B | Offensive, Supplementary | `narutorpg:lava_release_expanding_flame_rings` (existing) |
| [Lava Release: Fiery Rock Bullet](https://naruto.fandom.com/wiki/Lava_Release:_Fiery_Rock_Bullet) | B | Offensive | `narutorpg:lava_release_fiery_rock_bullet` (existing) |
| [Lava Release: Melting Apparition Technique](https://naruto.fandom.com/wiki/Lava_Release:_Melting_Apparition_Technique) | B | Offensive, Supplementary | `narutorpg:lava_release_melting_apparition_technique` (existing) |
| [Lava Release: Melting Apparition Technique: Flowing Crest](https://naruto.fandom.com/wiki/Lava_Release:_Melting_Apparition_Technique:_Flowing_Crest) | B | Offensive | `narutorpg:lava_release_melting_apparition_technique_flowing_crest` (existing) |
| [Lava Release: Planet-Branding Blast](https://naruto.fandom.com/wiki/Lava_Release:_Planet-Branding_Blast) | B | Offensive, Supplementary | `narutorpg:lava_release_planet_branding_blast` (existing) |
| [Lava Release: Quicklime Congealing Technique](https://naruto.fandom.com/wiki/Lava_Release:_Quicklime_Congealing_Technique) | B | Offensive, Supplementary | `narutorpg:lava_release_quicklime_congealing_technique` (existing) |
| [Lava Release: Rubber Ball](https://naruto.fandom.com/wiki/Lava_Release:_Rubber_Ball) | B | Defensive | `narutorpg:lava_release_rubber_ball` (existing) |
| [Lava Release: Rubber Defence](https://naruto.fandom.com/wiki/Lava_Release:_Rubber_Defence) | B | Defensive | `narutorpg:lava_release_rubber_defence` (existing) |
| [Lava Release: Rubber Rope](https://naruto.fandom.com/wiki/Lava_Release:_Rubber_Rope) | B | Supplementary | `narutorpg:lava_release_rubber_rope` (existing) |
| [Lava Release: Rubber Wall](https://naruto.fandom.com/wiki/Lava_Release:_Rubber_Wall) | B | Defensive | `narutorpg:lava_release_rubber_wall` (existing) |
| [Lava Release: Scorching Armoured Fist](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Armoured_Fist) | B | Offensive | `narutorpg:lava_release_scorching_armoured_fist` (existing) |
| [Lava Release: Scorching Flow Fist](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Flow_Fist) | B | Offensive | `narutorpg:lava_release_scorching_flow_fist` (existing) |
| [Lava Release: Scorching Flow Peak Rock](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Flow_Peak_Rock) | B | Offensive | `narutorpg:lava_release_scorching_flow_peak_rock` (existing) |
| [Lava Release: Scorching Flower Great Crumbling](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Flower_Great_Crumbling) | B | Offensive | `narutorpg:lava_release_scorching_flower_great_crumbling` (existing) |
| [Lava Release: Scorching Kick](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Kick) | B | Offensive | `narutorpg:lava_release_scorching_kick` (existing) |
| [Lava Release: Scorching Stream Rock Technique](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Stream_Rock_Technique) | B | Offensive | `narutorpg:lava_release_scorching_stream_rock_technique` (existing) |
| [Leaf Coiling Whirlwind](https://naruto.fandom.com/wiki/Leaf_Coiling_Whirlwind) | B | Offensive | `narutorpg:leaf_coiling_whirlwind` (existing) |
| [Leaf-Style: Empty Cicada](https://naruto.fandom.com/wiki/Leaf-Style:_Empty_Cicada) | C | Offensive | `narutorpg:leaf_style_empty_cicada` (existing) |
| [Leaping Flame Blowing Step](https://naruto.fandom.com/wiki/Leaping_Flame_Blowing_Step) | C | Offensive | `narutorpg:leaping_flame_blowing_step` (existing) |
| [Leech Gap](https://naruto.fandom.com/wiki/Leech_Gap) | C | Offensive | `narutorpg:leech_gap` (existing) |
| [Legend of the White Fang](https://naruto.fandom.com/wiki/Legend_of_the_White_Fang) | C | Offensive | `narutorpg:legend_of_the_white_fang` (existing) |
| [Legend's Portion](https://naruto.fandom.com/wiki/Legend's_Portion) | A | Offensive, Defensive | `narutorpg:legend_s_portion` |
| [Liger Two Platoons Bomb](https://naruto.fandom.com/wiki/Liger_Two_Platoons_Bomb) | B | Offensive | `narutorpg:liger_two_platoons_bomb` (existing) |
| [Lightning (jutsu)](https://naruto.fandom.com/wiki/Lightning_(jutsu)) | C | Offensive | `narutorpg:lightning_jutsu` (existing) |
| [Lightning Ball](https://naruto.fandom.com/wiki/Lightning_Ball) | C | Offensive | `narutorpg:lightning_ball` (existing) |
| [Lightning Beast Running Technique: Twin](https://naruto.fandom.com/wiki/Lightning_Beast_Running_Technique:_Twin) | C | Offensive | `narutorpg:lightning_beast_running_technique_twin` (existing) |
| [Lightning Blade Kunai Cutter](https://naruto.fandom.com/wiki/Lightning_Blade_Kunai_Cutter) | C | Offensive | `narutorpg:lightning_blade_kunai_cutter` (existing) |
| [Lightning Bolt](https://naruto.fandom.com/wiki/Lightning_Bolt) | C | Offensive | `narutorpg:lightning_bolt` (existing) |
| [Lightning Burial: Banquet of Lightning](https://naruto.fandom.com/wiki/Lightning_Burial:_Banquet_of_Lightning) | C | Offensive | `narutorpg:lightning_burial_banquet_of_lightning` (existing) |
| [Lightning Cutter](https://naruto.fandom.com/wiki/Lightning_Cutter) | S | Offensive | `narutorpg:lightning_cutter` (existing) |
| [Lightning Cutter Combo](https://naruto.fandom.com/wiki/Lightning_Cutter_Combo) | C | Offensive | `narutorpg:lightning_cutter_combo` (existing) |
| [Lightning Cutter One Flash](https://naruto.fandom.com/wiki/Lightning_Cutter_One_Flash) | C | Offensive | `narutorpg:lightning_cutter_one_flash` (existing) |
| [Lightning Cutter Seal](https://naruto.fandom.com/wiki/Lightning_Cutter_Seal) | C | Offensive, Supplementary | `narutorpg:lightning_cutter_seal` (existing) |
| [Lightning Cutter Two Sequential Crashes](https://naruto.fandom.com/wiki/Lightning_Cutter_Two_Sequential_Crashes) | C | Offensive | `narutorpg:lightning_cutter_two_sequential_crashes` (existing) |
| [Lightning Cutter: Double Lightning Quake](https://naruto.fandom.com/wiki/Lightning_Cutter:_Double_Lightning_Quake) | C | Offensive | `narutorpg:lightning_cutter_double_lightning_quake` (existing) |
| [Lightning Cutter: One Apex](https://naruto.fandom.com/wiki/Lightning_Cutter:_One_Apex) | C | Offensive | `narutorpg:lightning_cutter_one_apex` (existing) |
| [Lightning Cutter: Swift](https://naruto.fandom.com/wiki/Lightning_Cutter:_Swift) | A | Offensive | `narutorpg:lightning_cutter_swift` (existing) |
| [Lightning Cutter: Twin Drilling Lights](https://naruto.fandom.com/wiki/Lightning_Cutter:_Twin_Drilling_Lights) | C | Offensive | `narutorpg:lightning_cutter_twin_drilling_lights` (existing) |
| [Lightning Dragon Tornado](https://naruto.fandom.com/wiki/Lightning_Dragon_Tornado) | C | Offensive | `narutorpg:lightning_dragon_tornado` (existing) |
| [Lightning Fish](https://naruto.fandom.com/wiki/Lightning_Fish) | C | Offensive | `narutorpg:lightning_fish` (existing) |
| [Lightning Flash](https://naruto.fandom.com/wiki/Lightning_Flash) | C | Offensive | `narutorpg:lightning_flash` (existing) |
| [Lightning Flash Attack: Flight](https://naruto.fandom.com/wiki/Lightning_Flash_Attack:_Flight) | C | Offensive | `narutorpg:lightning_flash_attack_flight` (existing) |
| [Lightning Flash Lion Combo](https://naruto.fandom.com/wiki/Lightning_Flash_Lion_Combo) | C | Offensive | `narutorpg:lightning_flash_lion_combo` (existing) |
| [Lightning Flash Palm](https://naruto.fandom.com/wiki/Lightning_Flash_Palm) | C | Offensive | `narutorpg:lightning_flash_palm` (existing) |
| [Lightning Flash Sword](https://naruto.fandom.com/wiki/Lightning_Flash_Sword) | C | Offensive | `narutorpg:lightning_flash_sword` (existing) |
| [Lightning Kunai](https://naruto.fandom.com/wiki/Lightning_Kunai) | C | Offensive | `narutorpg:lightning_kunai` (existing) |
| [Lightning Rat Violent Quake](https://naruto.fandom.com/wiki/Lightning_Rat_Violent_Quake) | C | Offensive | `narutorpg:lightning_rat_violent_quake` (existing) |
| [Lightning Release Bullet: Powerful Breath](https://naruto.fandom.com/wiki/Lightning_Release_Bullet:_Powerful_Breath) | C | Offensive | `narutorpg:lightning_release_bullet_powerful_breath` (existing) |
| [Lightning Release Chakra Mode](https://naruto.fandom.com/wiki/Lightning_Release_Chakra_Mode) | B | Offensive, Defensive, Supplementary | `narutorpg:lightning_release_chakra_mode` (existing) |
| [Lightning Release Co-Operation: Thunder Bomb](https://naruto.fandom.com/wiki/Lightning_Release_Co-Operation:_Thunder_Bomb) | B | Offensive | `narutorpg:lightning_release_co_operation_thunder_bomb` (existing) |
| [Lightning Release Wall](https://naruto.fandom.com/wiki/Lightning_Release_Wall) | C | Offensive, Defensive, Supplementary | `narutorpg:lightning_release_wall` (existing) |
| [Lightning Release: Black Panther](https://naruto.fandom.com/wiki/Lightning_Release:_Black_Panther) | B | Offensive | `narutorpg:lightning_release_black_panther` (existing) |
| [Lightning Release: Black Thunder](https://naruto.fandom.com/wiki/Lightning_Release:_Black_Thunder) | C | Offensive, Defensive | `narutorpg:lightning_release_black_thunder` (existing) |
| [Lightning Release: Depth Charge](https://naruto.fandom.com/wiki/Lightning_Release:_Depth_Charge) | C | Offensive | `narutorpg:lightning_release_depth_charge` (existing) |
| [Lightning Release: Double Lariat](https://naruto.fandom.com/wiki/Lightning_Release:_Double_Lariat) | A | Offensive | `narutorpg:lightning_release_double_lariat` (existing) |
| [Lightning Release: Electric Shock Needles](https://naruto.fandom.com/wiki/Lightning_Release:_Electric_Shock_Needles) | D | Supplementary | `narutorpg:lightning_release_electric_shock_needles` (existing) |
| [Lightning Release: Electromagnetic Murder](https://naruto.fandom.com/wiki/Lightning_Release:_Electromagnetic_Murder) | C | Offensive | `narutorpg:lightning_release_electromagnetic_murder` (existing) |
| [Lightning Release: Elekiter](https://naruto.fandom.com/wiki/Lightning_Release:_Elekiter) | C | Offensive | `narutorpg:lightning_release_elekiter` (existing) |
| [Lightning Release: False Darkness](https://naruto.fandom.com/wiki/Lightning_Release:_False_Darkness) | B | Offensive | `narutorpg:lightning_release_false_darkness` (existing) |
| [Lightning Release: Four Pillar Bind](https://naruto.fandom.com/wiki/Lightning_Release:_Four_Pillar_Bind) | C | Offensive, Supplementary | `narutorpg:lightning_release_four_pillar_bind` (existing) |
| [Lightning Release: Heavenly Formation Lightning](https://naruto.fandom.com/wiki/Lightning_Release:_Heavenly_Formation_Lightning) | C | Offensive | `narutorpg:lightning_release_heavenly_formation_lightning` (existing) |
| [Lightning Release: Lighting Flash](https://naruto.fandom.com/wiki/Lightning_Release:_Lighting_Flash) | B | Offensive | `narutorpg:lightning_release_lighting_flash` (existing) |
| [Lightning Release: Lightning Beast Tracking Fang](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Beast_Tracking_Fang) | C | Offensive, Supplementary | `narutorpg:lightning_release_lightning_beast_tracking_fang` (existing) |
| [Lightning Release: Lightning Bind](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Bind) | C | Offensive | `narutorpg:lightning_release_lightning_bind` (existing) |
| [Lightning Release: Lightning Bullet](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Bullet) | C | Offensive | `narutorpg:lightning_release_lightning_bullet` (existing) |
| [Lightning Release: Lightning Eel](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Eel) | C | Offensive, Supplementary | `narutorpg:lightning_release_lightning_eel` (existing) |
| [Lightning Release: Lightning Fang](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Fang) | C | Offensive | `narutorpg:lightning_release_lightning_fang` (existing) |
| [Lightning Release: Lightning Flash](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Flash) | C | Offensive | `narutorpg:lightning_release_lightning_flash` (existing) |
| [Lightning Release: Lightning Net Technique](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Net_Technique) | C | Offensive | `narutorpg:lightning_release_lightning_net_technique` (existing) |
| [Lightning Release: Lightning Quake Flash](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Quake_Flash) | C | Offensive | `narutorpg:lightning_release_lightning_quake_flash` (existing) |
| [Lightning Release: Lightning Rod](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Rod) | C | Offensive | `narutorpg:lightning_release_lightning_rod` (existing) |
| [Lightning Release: Lightning Signal Fire](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Signal_Fire) | C | Offensive, Supplementary | `narutorpg:lightning_release_lightning_signal_fire` (existing) |
| [Lightning Release: Lightning Slash](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Slash) | C | Offensive | `narutorpg:lightning_release_lightning_slash` (existing) |
| [Lightning Release: Lightning Violent Soak](https://naruto.fandom.com/wiki/Lightning_Release:_Lightning_Violent_Soak) | C | Offensive | `narutorpg:lightning_release_lightning_violent_soak` (existing) |
| [Lightning Release: Overdrive](https://naruto.fandom.com/wiki/Lightning_Release:_Overdrive) | D | Supplementary | `narutorpg:lightning_release_overdrive` (existing) |
| [Lightning Release: Pulverising Lightning Rod](https://naruto.fandom.com/wiki/Lightning_Release:_Pulverising_Lightning_Rod) | D | Supplementary | `narutorpg:lightning_release_pulverising_lightning_rod` (existing) |
| [Lightning Release: Purple Electricity](https://naruto.fandom.com/wiki/Lightning_Release:_Purple_Electricity) | C | Offensive | `narutorpg:lightning_release_purple_electricity` (existing) |
| [Lightning Release: Reverse Lift](https://naruto.fandom.com/wiki/Lightning_Release:_Reverse_Lift) | C | Offensive | `narutorpg:lightning_release_reverse_lift` (existing) |
| [Lightning Release: Shadow Clone Technique](https://naruto.fandom.com/wiki/Lightning_Release:_Shadow_Clone_Technique) | A | Offensive, Supplementary | `narutorpg:lightning_release_shadow_clone_technique` (existing) |
| [Lightning Release: Sixteen Pillar Bind](https://naruto.fandom.com/wiki/Lightning_Release:_Sixteen_Pillar_Bind) | D | Supplementary | `narutorpg:lightning_release_sixteen_pillar_bind` (existing) |
| [Lightning Release: Snake Lightning](https://naruto.fandom.com/wiki/Lightning_Release:_Snake_Lightning) | C | Offensive, Supplementary | `narutorpg:lightning_release_snake_lightning` (existing) |
| [Lightning Release: Spider Web](https://naruto.fandom.com/wiki/Lightning_Release:_Spider_Web) | C | Offensive | `narutorpg:lightning_release_spider_web` (existing) |
| [Lightning Release: Thunder](https://naruto.fandom.com/wiki/Lightning_Release:_Thunder) | C | Offensive | `narutorpg:lightning_release_thunder` (existing) |
| [Lightning Release: Thunder Binding](https://naruto.fandom.com/wiki/Lightning_Release:_Thunder_Binding) | C | Defensive, Supplementary | `narutorpg:lightning_release_thunder_binding` (existing) |
| [Lightning Release: Thunder Gate](https://naruto.fandom.com/wiki/Lightning_Release:_Thunder_Gate) | C | Offensive | `narutorpg:lightning_release_thunder_gate` (existing) |
| [Lightning Release: Thunder Wedge](https://naruto.fandom.com/wiki/Lightning_Release:_Thunder_Wedge) | C | Offensive | `narutorpg:lightning_release_thunder_wedge` (existing) |
| [Lightning Release: Thunderbolt](https://naruto.fandom.com/wiki/Lightning_Release:_Thunderbolt) | C | Offensive | `narutorpg:lightning_release_thunderbolt` (existing) |
| [Lightning Release: Thunderclap Arrow](https://naruto.fandom.com/wiki/Lightning_Release:_Thunderclap_Arrow) | C | Offensive | `narutorpg:lightning_release_thunderclap_arrow` (existing) |
| [Lightning Release: Thunderclap Wave](https://naruto.fandom.com/wiki/Lightning_Release:_Thunderclap_Wave) | C | Offensive | `narutorpg:lightning_release_thunderclap_wave` (existing) |
| [Lightning Release: Thunderstorm](https://naruto.fandom.com/wiki/Lightning_Release:_Thunderstorm) | B | Offensive | `narutorpg:lightning_release_thunderstorm` (existing) |
| [Lightning Release: Tiger Scissors](https://naruto.fandom.com/wiki/Lightning_Release:_Tiger_Scissors) | C | Defensive, Supplementary | `narutorpg:lightning_release_tiger_scissors` (existing) |
| [Lightning Release: Wave of Inspiration](https://naruto.fandom.com/wiki/Lightning_Release:_Wave_of_Inspiration) | C | Offensive | `narutorpg:lightning_release_wave_of_inspiration` (existing) |
| [Lightning Release: Whip Dance of Fools](https://naruto.fandom.com/wiki/Lightning_Release:_Whip_Dance_of_Fools) | C | Offensive | `narutorpg:lightning_release_whip_dance_of_fools` (existing) |
| [Lightning Speed (Kakashi)](https://naruto.fandom.com/wiki/Lightning_Speed_(Kakashi)) | C | Offensive, Defensive | `narutorpg:lightning_speed_kakashi` (existing) |
| [Lightning Speed (Minato)](https://naruto.fandom.com/wiki/Lightning_Speed_(Minato)) | A | Offensive | `narutorpg:lightning_speed_minato` (existing) |
| [Lightning Strike](https://naruto.fandom.com/wiki/Lightning_Strike) | C | Offensive | `narutorpg:lightning_strike` (existing) |
| [Lightning Strike Armour](https://naruto.fandom.com/wiki/Lightning_Strike_Armour) | C | Offensive, Defensive, Supplementary | `narutorpg:lightning_strike_armour` (existing) |
| [Lightning Surge](https://naruto.fandom.com/wiki/Lightning_Surge) | C | Offensive | `narutorpg:lightning_surge` (existing) |
| [Lightning Sword](https://naruto.fandom.com/wiki/Lightning_Sword) | B | Offensive | `narutorpg:lightning_sword` (existing) |
| [Lightning Tackle](https://naruto.fandom.com/wiki/Lightning_Tackle) | C | Offensive | `narutorpg:lightning_tackle` (existing) |
| [Lightning Thrust](https://naruto.fandom.com/wiki/Lightning_Thrust) | B | Offensive | `narutorpg:lightning_thrust` (existing) |
| [Lightning Transmission](https://naruto.fandom.com/wiki/Lightning_Transmission) | B | Offensive, Supplementary | `narutorpg:lightning_transmission` (existing) |
| [Lightning Water Dragon Bullet](https://naruto.fandom.com/wiki/Lightning_Water_Dragon_Bullet) | B | Offensive | `narutorpg:lightning_water_dragon_bullet` (existing) |
| [Lightning Wood Flow Palm](https://naruto.fandom.com/wiki/Lightning_Wood_Flow_Palm) | B | Offensive | `narutorpg:lightning_wood_flow_palm` (existing) |
| [Like a Whirlwind](https://naruto.fandom.com/wiki/Like_a_Whirlwind) | C | Offensive | `narutorpg:like_a_whirlwind` (existing) |
| [Limbo: Border Jail](https://naruto.fandom.com/wiki/Limbo:_Border_Jail) | B | Offensive, Defensive, Supplementary | `narutorpg:limbo_border_jail` (existing) |
| [Limbo: Tengai Shinsei](https://naruto.fandom.com/wiki/Limbo:_Tengai_Shinsei) | A | Offensive | `narutorpg:limbo_tengai_shinsei` (existing) |
| [Limelight](https://naruto.fandom.com/wiki/Limelight) | B | Offensive | `narutorpg:limelight` (existing) |
| [Lion Flame Bullet](https://naruto.fandom.com/wiki/Lion_Flame_Bullet) | C | Offensive | `narutorpg:lion_flame_bullet` (existing) |
| [Living Corpse Reincarnation](https://naruto.fandom.com/wiki/Living_Corpse_Reincarnation) | S | Supplementary | `narutorpg:living_corpse_reincarnation` |
| [Localised Reincarnation Explosion](https://naruto.fandom.com/wiki/Localised_Reincarnation_Explosion) | B | Offensive | `narutorpg:localised_reincarnation_explosion` (existing) |
| [Long Range Mind's Eye of the Kagura](https://naruto.fandom.com/wiki/Long_Range_Mind's_Eye_of_the_Kagura) | C | Supplementary | `narutorpg:long_range_mind_s_eye_of_the_kagura` |
| [Long-Distance Bombing](https://naruto.fandom.com/wiki/Long-Distance_Bombing) | C | Offensive | `narutorpg:long_distance_bombing` |
| [Lorentz Gun](https://naruto.fandom.com/wiki/Lorentz_Gun) | D | - | `narutorpg:lorentz_gun` (existing) |
| [Love Roar](https://naruto.fandom.com/wiki/Love_Roar) | C | Offensive | `narutorpg:love_roar` (existing) |
| [Magatsuhi no Kami](https://naruto.fandom.com/wiki/Magatsuhi_no_Kami) | C | Offensive | `narutorpg:magatsuhi_no_kami` |
| [Magic Lantern Body Technique](https://naruto.fandom.com/wiki/Magic_Lantern_Body_Technique) | D | Supplementary | `narutorpg:magic_lantern_body_technique` |
| [Magnet Iron Grain Seal Palm](https://naruto.fandom.com/wiki/Magnet_Iron_Grain_Seal_Palm) | B | Supplementary | `narutorpg:magnet_iron_grain_seal_palm` (existing) |
| [Magnet Ninja Art](https://naruto.fandom.com/wiki/Magnet_Ninja_Art) | C | Offensive, Supplementary | `narutorpg:magnet_ninja_art` |
| [Magnet Ninja Art: Electromagnetic Vision](https://naruto.fandom.com/wiki/Magnet_Ninja_Art:_Electromagnetic_Vision) | D | Supplementary | `narutorpg:magnet_ninja_art_electromagnetic_vision` |
| [Magnet Ninja Art: Infinite Meteors](https://naruto.fandom.com/wiki/Magnet_Ninja_Art:_Infinite_Meteors) | C | Offensive | `narutorpg:magnet_ninja_art_infinite_meteors` |
| [Magnet Release](https://naruto.fandom.com/wiki/Magnet_Release) | B | - | `narutorpg:magnet_release` (existing) |
| [Magnet Release: Body Control](https://naruto.fandom.com/wiki/Magnet_Release:_Body_Control) | B | Offensive, Supplementary | `narutorpg:magnet_release_body_control` (existing) |
| [Magnet Release: Conserving Bee Twin Blades](https://naruto.fandom.com/wiki/Magnet_Release:_Conserving_Bee_Twin_Blades) | B | Offensive, Supplementary | `narutorpg:magnet_release_conserving_bee_twin_blades` (existing) |
| [Magnet Release: Gold Dust Imperial Funeral](https://naruto.fandom.com/wiki/Magnet_Release:_Gold_Dust_Imperial_Funeral) | B | Offensive | `narutorpg:magnet_release_gold_dust_imperial_funeral` (existing) |
| [Magnet Release: Gold Dust Imperial Funeral Sending](https://naruto.fandom.com/wiki/Magnet_Release:_Gold_Dust_Imperial_Funeral_Sending) | B | Offensive | `narutorpg:magnet_release_gold_dust_imperial_funeral_sending` (existing) |
| [Magnet Release: Lightning God Self](https://naruto.fandom.com/wiki/Magnet_Release:_Lightning_God_Self) | B | Offensive, Defensive, Supplementary | `narutorpg:magnet_release_lightning_god_self` (existing) |
| [Magnet Release: Spiral Jet Black](https://naruto.fandom.com/wiki/Magnet_Release:_Spiral_Jet_Black) | A | Offensive, Supplementary | `narutorpg:magnet_release_spiral_jet_black` (existing) |
| [Magnetic Iron Sand Sealing Blade](https://naruto.fandom.com/wiki/Magnetic_Iron_Sand_Sealing_Blade) | B | Offensive, Supplementary | `narutorpg:magnetic_iron_sand_sealing_blade` (existing) |
| [Mail Manipulation Technique](https://naruto.fandom.com/wiki/Mail_Manipulation_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:mail_manipulation_technique` |
| [Majestic Attire: Susanoo](https://naruto.fandom.com/wiki/Majestic_Attire:_Susanoo) | B | Supplementary | `narutorpg:majestic_attire_susanoo` (existing) |
| [Mangekyō Shark Sword](https://naruto.fandom.com/wiki/Mangekyō_Shark_Sword) | B | Offensive | `narutorpg:mangekyo_shark_sword` (existing) |
| [Manipulated Shuriken Technique](https://naruto.fandom.com/wiki/Manipulated_Shuriken_Technique) | D | Supplementary | `narutorpg:manipulated_shuriken_technique` (existing) |
| [Manipulated Shuriken Wire Binding](https://naruto.fandom.com/wiki/Manipulated_Shuriken_Wire_Binding) | D | Supplementary | `narutorpg:manipulated_shuriken_wire_binding` |
| [Manipulated Tools: Binding Meteor](https://naruto.fandom.com/wiki/Manipulated_Tools:_Binding_Meteor) | C | Offensive | `narutorpg:manipulated_tools_binding_meteor` |
| [Manipulated Tools: Blade Kick](https://naruto.fandom.com/wiki/Manipulated_Tools:_Blade_Kick) | C | Offensive | `narutorpg:manipulated_tools_blade_kick` |
| [Manipulated Tools: Deadly Spheres Strike](https://naruto.fandom.com/wiki/Manipulated_Tools:_Deadly_Spheres_Strike) | C | Offensive, Defensive | `narutorpg:manipulated_tools_deadly_spheres_strike` |
| [Manipulated Tools: Exploding Sphere](https://naruto.fandom.com/wiki/Manipulated_Tools:_Exploding_Sphere) | C | Offensive | `narutorpg:manipulated_tools_exploding_sphere` |
| [Manipulated Tools: Floating Blade Wall](https://naruto.fandom.com/wiki/Manipulated_Tools:_Floating_Blade_Wall) | C | Offensive | `narutorpg:manipulated_tools_floating_blade_wall` |
| [Manipulated Tools: Gigantic Iron Ball](https://naruto.fandom.com/wiki/Manipulated_Tools:_Gigantic_Iron_Ball) | C | Offensive | `narutorpg:manipulated_tools_gigantic_iron_ball` |
| [Manipulated Tools: Great Iron Lump](https://naruto.fandom.com/wiki/Manipulated_Tools:_Great_Iron_Lump) | C | Offensive | `narutorpg:manipulated_tools_great_iron_lump` |
| [Manipulated Tools: Heavenly Chain Disaster](https://naruto.fandom.com/wiki/Manipulated_Tools:_Heavenly_Chain_Disaster) | C | Offensive | `narutorpg:manipulated_tools_heavenly_chain_disaster` |
| [Manipulated Tools: Heavenly Steel Disaster](https://naruto.fandom.com/wiki/Manipulated_Tools:_Heavenly_Steel_Disaster) | C | Offensive | `narutorpg:manipulated_tools_heavenly_steel_disaster` |
| [Manipulated Tools: One Thousand Blades Attack](https://naruto.fandom.com/wiki/Manipulated_Tools:_One_Thousand_Blades_Attack) | C | Offensive | `narutorpg:manipulated_tools_one_thousand_blades_attack` |
| [Manipulated Tools: Spinning Swallow Show](https://naruto.fandom.com/wiki/Manipulated_Tools:_Spinning_Swallow_Show) | C | Offensive | `narutorpg:manipulated_tools_spinning_swallow_show` |
| [Manipulated Tools: Steel Needles Wild Flower](https://naruto.fandom.com/wiki/Manipulated_Tools:_Steel_Needles_Wild_Flower) | C | Offensive | `narutorpg:manipulated_tools_steel_needles_wild_flower` |
| [Manipulated Tools: Ten Thousand Wild Heavenly Blades](https://naruto.fandom.com/wiki/Manipulated_Tools:_Ten_Thousand_Wild_Heavenly_Blades) | C | Offensive | `narutorpg:manipulated_tools_ten_thousand_wild_heavenly_blades` |
| [Manipulated Tools: Thousand Blades Attack Performance](https://naruto.fandom.com/wiki/Manipulated_Tools:_Thousand_Blades_Attack_Performance) | C | Offensive | `narutorpg:manipulated_tools_thousand_blades_attack_performance` |
| [Manipulating Attack Blades](https://naruto.fandom.com/wiki/Manipulating_Attack_Blades) | C | Offensive | `narutorpg:manipulating_attack_blades` (existing) |
| [Manipulating Attack Blades Explosion](https://naruto.fandom.com/wiki/Manipulating_Attack_Blades_Explosion) | C | Offensive | `narutorpg:manipulating_attack_blades_explosion` |
| [Manipulating Windmill Triple Blades](https://naruto.fandom.com/wiki/Manipulating_Windmill_Triple_Blades) | C | Offensive | `narutorpg:manipulating_windmill_triple_blades` (existing) |
| [Many Hidden Shadow Snake Hands](https://naruto.fandom.com/wiki/Many_Hidden_Shadow_Snake_Hands) | B | Offensive | `narutorpg:many_hidden_shadow_snake_hands` |
| [Married Couple Combination: Adamantine Impact](https://naruto.fandom.com/wiki/Married_Couple_Combination:_Adamantine_Impact) | B | Offensive | `narutorpg:married_couple_combination_adamantine_impact` (existing) |
| [Master and Student's Move](https://naruto.fandom.com/wiki/Master_and_Student's_Move) | B | Offensive | `narutorpg:master_and_student_s_move` |
| [May Rain Blood River](https://naruto.fandom.com/wiki/May_Rain_Blood_River) | C | Offensive | `narutorpg:may_rain_blood_river` |
| [Mayfly](https://naruto.fandom.com/wiki/Mayfly) | B | Supplementary | `narutorpg:mayfly` (existing) |
| [Mayfly: Antlion](https://naruto.fandom.com/wiki/Mayfly:_Antlion) | B | Offensive | `narutorpg:mayfly_antlion` (existing) |
| [Mechanical Eight Trigrams Formation](https://naruto.fandom.com/wiki/Mechanical_Eight_Trigrams_Formation) | D | Supplementary | `narutorpg:mechanical_eight_trigrams_formation` |
| [Mechanical Light Shield Block](https://naruto.fandom.com/wiki/Mechanical_Light_Shield_Block) | B | Defensive | `narutorpg:mechanical_light_shield_block` (existing) |
| [Mechanical Wings](https://naruto.fandom.com/wiki/Mechanical_Wings) | C | Offensive | `narutorpg:mechanical_wings` |
| [Medical Empress Gambler](https://naruto.fandom.com/wiki/Medical_Empress_Gambler) | B | Offensive | `narutorpg:medical_empress_gambler` (existing) |
| [Medical Mode](https://naruto.fandom.com/wiki/Medical_Mode) | D | Supplementary | `narutorpg:medical_mode` |
| [Medical Ninjutsu: Strong Flash](https://naruto.fandom.com/wiki/Medical_Ninjutsu:_Strong_Flash) | C | Offensive | `narutorpg:medical_ninjutsu_strong_flash` (existing) |
| [Medical Technique: Triple Unity of Death](https://naruto.fandom.com/wiki/Medical_Technique:_Triple_Unity_of_Death) | C | Offensive | `narutorpg:medical_technique_triple_unity_of_death` |
| [Medical Water Release: Jellyfish](https://naruto.fandom.com/wiki/Medical_Water_Release:_Jellyfish) | D | Supplementary | `narutorpg:medical_water_release_jellyfish` (existing) |
| [Medical Water Release: Water Mosquito](https://naruto.fandom.com/wiki/Medical_Water_Release:_Water_Mosquito) | D | Supplementary | `narutorpg:medical_water_release_water_mosquito` (existing) |
| [Medical Water Release: Water Scorpion](https://naruto.fandom.com/wiki/Medical_Water_Release:_Water_Scorpion) | C | Offensive | `narutorpg:medical_water_release_water_scorpion` (existing) |
| [Melting Lightning Burst](https://naruto.fandom.com/wiki/Melting_Lightning_Burst) | B | Offensive | `narutorpg:melting_lightning_burst` (existing) |
| [Memory Blocking Technique](https://naruto.fandom.com/wiki/Memory_Blocking_Technique) | C | Supplementary | `narutorpg:memory_blocking_technique` (existing) |
| [Memory Erasing Seal](https://naruto.fandom.com/wiki/Memory_Erasing_Seal) | C | Supplementary | `narutorpg:memory_erasing_seal` |
| [Memory Erasing Technique](https://naruto.fandom.com/wiki/Memory_Erasing_Technique) | D | Supplementary | `narutorpg:memory_erasing_technique` (existing) |
| [Memory-Concealing Manipulative Sand Technique](https://naruto.fandom.com/wiki/Memory-Concealing_Manipulative_Sand_Technique) | A | Supplementary | `narutorpg:memory_concealing_manipulative_sand_technique` |
| [Menacing Shark](https://naruto.fandom.com/wiki/Menacing_Shark) | C | Offensive | `narutorpg:menacing_shark` (existing) |
| [Mental Replacement Device](https://naruto.fandom.com/wiki/Mental_Replacement_Device) | D | Supplementary | `narutorpg:mental_replacement_device` |
| [Mental Resistance Technique](https://naruto.fandom.com/wiki/Mental_Resistance_Technique) | D | Supplementary | `narutorpg:mental_resistance_technique` |
| [Meteorite Rasengan](https://naruto.fandom.com/wiki/Meteorite_Rasengan) | C | Offensive | `narutorpg:meteorite_rasengan` |
| [Miasma Stroke](https://naruto.fandom.com/wiki/Miasma_Stroke) | C | Offensive | `narutorpg:miasma_stroke` |
| [Middle-Aged Attack](https://naruto.fandom.com/wiki/Middle-Aged_Attack) | C | Offensive | `narutorpg:middle_aged_attack` (existing) |
| [Mifune-Style First Tachi: Blaze](https://naruto.fandom.com/wiki/Mifune-Style_First_Tachi:_Blaze) | C | Offensive | `narutorpg:mifune_style_first_tachi_blaze` |
| [Mimetic Insects: Crawl](https://naruto.fandom.com/wiki/Mimetic_Insects:_Crawl) | C | Offensive, Defensive | `narutorpg:mimetic_insects_crawl` |
| [Mind Body Confusion Technique](https://naruto.fandom.com/wiki/Mind_Body_Confusion_Technique) | C | Offensive, Supplementary | `narutorpg:mind_body_confusion_technique` |
| [Mind Body Disturbance Technique](https://naruto.fandom.com/wiki/Mind_Body_Disturbance_Technique) | B | Supplementary | `narutorpg:mind_body_disturbance_technique` (existing) |
| [Mind Body Switch Technique](https://naruto.fandom.com/wiki/Mind_Body_Switch_Technique) | C | Supplementary | `narutorpg:mind_body_switch_technique` (existing) |
| [Mind Body Transmission Formation](https://naruto.fandom.com/wiki/Mind_Body_Transmission_Formation) | C | Supplementary | `narutorpg:mind_body_transmission_formation` (existing) |
| [Mind Body Transmission Technique](https://naruto.fandom.com/wiki/Mind_Body_Transmission_Technique) | C | Supplementary | `narutorpg:mind_body_transmission_technique` (existing) |
| [Mind Clone Switch Technique](https://naruto.fandom.com/wiki/Mind_Clone_Switch_Technique) | C | Supplementary | `narutorpg:mind_clone_switch_technique` (existing) |
| [Mind Disturbance Dance Performance Technique](https://naruto.fandom.com/wiki/Mind_Disturbance_Dance_Performance_Technique) | C | Offensive | `narutorpg:mind_disturbance_dance_performance_technique` (existing) |
| [Mind Mystical Palm Transmission Technique](https://naruto.fandom.com/wiki/Mind_Mystical_Palm_Transmission_Technique) | C | Supplementary | `narutorpg:mind_mystical_palm_transmission_technique` |
| [Mind Puppet Switch Cursed Seal Technique](https://naruto.fandom.com/wiki/Mind_Puppet_Switch_Cursed_Seal_Technique) | C | Offensive, Supplementary | `narutorpg:mind_puppet_switch_cursed_seal_technique` (existing) |
| [Mind's Eye of the Kagura](https://naruto.fandom.com/wiki/Mind's_Eye_of_the_Kagura) | C | Supplementary | `narutorpg:mind_s_eye_of_the_kagura` |
| [Mingling Arm Flying Thrust](https://naruto.fandom.com/wiki/Mingling_Arm_Flying_Thrust) | C | Offensive | `narutorpg:mingling_arm_flying_thrust` (existing) |
| [Mini-Rasenshuriken](https://naruto.fandom.com/wiki/Mini-Rasenshuriken) | S | Offensive | `narutorpg:mini_rasenshuriken` (existing) |
| [Miniature Tailed Beast Ball Sniper Shot](https://naruto.fandom.com/wiki/Miniature_Tailed_Beast_Ball_Sniper_Shot) | C | Offensive | `narutorpg:miniature_tailed_beast_ball_sniper_shot` |
| [Mirrored Sudden Attacker Technique](https://naruto.fandom.com/wiki/Mirrored_Sudden_Attacker_Technique) | A | Offensive | `narutorpg:mirrored_sudden_attacker_technique` |
| [Missile](https://naruto.fandom.com/wiki/Missile) | C | Offensive | `narutorpg:missile` |
| [Mist Servant Technique](https://naruto.fandom.com/wiki/Mist_Servant_Technique) | D | Supplementary | `narutorpg:mist_servant_technique` (existing) |
| [Molten Earth Wild Eruption Technique](https://naruto.fandom.com/wiki/Molten_Earth_Wild_Eruption_Technique) | B | Offensive | `narutorpg:molten_earth_wild_eruption_technique` (existing) |
| [Molten Wave](https://naruto.fandom.com/wiki/Molten_Wave) | A | Offensive | `narutorpg:molten_wave` |
| [Monkey Flame Arson](https://naruto.fandom.com/wiki/Monkey_Flame_Arson) | C | Offensive | `narutorpg:monkey_flame_arson` (existing) |
| [Monkey Rock](https://naruto.fandom.com/wiki/Monkey_Rock) | C | Offensive | `narutorpg:monkey_rock` (existing) |
| [Monstrous Sand Arm](https://naruto.fandom.com/wiki/Monstrous_Sand_Arm) | C | Offensive | `narutorpg:monstrous_sand_arm` |
| [Monstrous Sand Arm: Breakthrough](https://naruto.fandom.com/wiki/Monstrous_Sand_Arm:_Breakthrough) | C | Offensive | `narutorpg:monstrous_sand_arm_breakthrough` |
| [Monstrous Sand Arm: Gripping Crush](https://naruto.fandom.com/wiki/Monstrous_Sand_Arm:_Gripping_Crush) | C | Offensive | `narutorpg:monstrous_sand_arm_gripping_crush` |
| [Moon Shadow Technique](https://naruto.fandom.com/wiki/Moon_Shadow_Technique) | D | Supplementary | `narutorpg:moon_shadow_technique` |
| [Morning Moon](https://naruto.fandom.com/wiki/Morning_Moon) | A | Offensive | `narutorpg:morning_moon` (existing) |
| [Mortality of All Creation](https://naruto.fandom.com/wiki/Mortality_of_All_Creation) | A | Offensive | `narutorpg:mortality_of_all_creation` (existing) |
| [Moulded Beauty](https://naruto.fandom.com/wiki/Moulded_Beauty) | B | Offensive | `narutorpg:moulded_beauty` (existing) |
| [Mouse Hairball](https://naruto.fandom.com/wiki/Mouse_Hairball) | C | Offensive | `narutorpg:mouse_hairball` (existing) |
| [Mud Release: Bottomless Mud Hole](https://naruto.fandom.com/wiki/Mud_Release:_Bottomless_Mud_Hole) | B | - | `narutorpg:mud_release_bottomless_mud_hole` (existing) |
| [Mud Release: Landslide](https://naruto.fandom.com/wiki/Mud_Release:_Landslide) | B | - | `narutorpg:mud_release_landslide` (existing) |
| [Mud Release: Mud Doll](https://naruto.fandom.com/wiki/Mud_Release:_Mud_Doll) | B | - | `narutorpg:mud_release_mud_doll` (existing) |
| [Mud Release: Muddy Water Wall](https://naruto.fandom.com/wiki/Mud_Release:_Muddy_Water_Wall) | B | Offensive | `narutorpg:mud_release_muddy_water_wall` (existing) |
| [Mudshot Technique](https://naruto.fandom.com/wiki/Mudshot_Technique) | C | Offensive | `narutorpg:mudshot_technique` (existing) |
| [Multi-Size Technique](https://naruto.fandom.com/wiki/Multi-Size_Technique) | C | Supplementary | `narutorpg:multi_size_technique` (existing) |
| [Multiple Connected Feet](https://naruto.fandom.com/wiki/Multiple_Connected_Feet) | B | Offensive | `narutorpg:multiple_connected_feet` (existing) |
| [Multiple Connected Fists](https://naruto.fandom.com/wiki/Multiple_Connected_Fists) | B | Offensive | `narutorpg:multiple_connected_fists` (existing) |
| [Multiple Connected Fists: Rakshasa](https://naruto.fandom.com/wiki/Multiple_Connected_Fists:_Rakshasa) | A | Offensive | `narutorpg:multiple_connected_fists_rakshasa` (existing) |
| [Multiple Connected Whirlwinds](https://naruto.fandom.com/wiki/Multiple_Connected_Whirlwinds) | B | Offensive, Defensive | `narutorpg:multiple_connected_whirlwinds` (existing) |
| [Multiple Infinite Embraces](https://naruto.fandom.com/wiki/Multiple_Infinite_Embraces) | B | Offensive | `narutorpg:multiple_infinite_embraces` |
| [Multiple Lotus Nonself Connected Cannons](https://naruto.fandom.com/wiki/Multiple_Lotus_Nonself_Connected_Cannons) | A | Offensive | `narutorpg:multiple_lotus_nonself_connected_cannons` (existing) |
| [Multiple Phantom Shuriken](https://naruto.fandom.com/wiki/Multiple_Phantom_Shuriken) | B | Offensive | `narutorpg:multiple_phantom_shuriken` |
| [Multiple Shadow Clone Technique](https://naruto.fandom.com/wiki/Multiple_Shadow_Clone_Technique) | A | Supplementary | `narutorpg:multiple_shadow_clone_technique` |
| [Multiple Shadow Clone Technique: Lightning Cutter](https://naruto.fandom.com/wiki/Multiple_Shadow_Clone_Technique:_Lightning_Cutter) | S | Offensive | `narutorpg:multiple_shadow_clone_technique_lightning_cutter` (existing) |
| [Multiple Shadows Attack Combo](https://naruto.fandom.com/wiki/Multiple_Shadows_Attack_Combo) | S | Offensive | `narutorpg:multiple_shadows_attack_combo` (existing) |
| [Multiple Smoke Clone](https://naruto.fandom.com/wiki/Multiple_Smoke_Clone) | D | Supplementary | `narutorpg:multiple_smoke_clone` |
| [Multiple String Light Formation](https://naruto.fandom.com/wiki/Multiple_String_Light_Formation) | B | Supplementary | `narutorpg:multiple_string_light_formation` (existing) |
| [Multiple Wood Release Clone Technique](https://naruto.fandom.com/wiki/Multiple_Wood_Release_Clone_Technique) | B | Supplementary | `narutorpg:multiple_wood_release_clone_technique` (existing) |
| [Murderous Grasp](https://naruto.fandom.com/wiki/Murderous_Grasp) | C | Offensive | `narutorpg:murderous_grasp` |
| [Mutability: Killing Spiders](https://naruto.fandom.com/wiki/Mutability:_Killing_Spiders) | A | Offensive | `narutorpg:mutability_killing_spiders` (existing) |
| [Mutually Multiplying Explosive Tags](https://naruto.fandom.com/wiki/Mutually_Multiplying_Explosive_Tags) | A | Offensive | `narutorpg:mutually_multiplying_explosive_tags` |
| [Mysterious Peacock Method](https://naruto.fandom.com/wiki/Mysterious_Peacock_Method) | S | Offensive, Supplementary | `narutorpg:mysterious_peacock_method` |
| [Mysterious Storm Technique](https://naruto.fandom.com/wiki/Mysterious_Storm_Technique) | B | Offensive | `narutorpg:mysterious_storm_technique` (existing) |
| [Mystic Safety Bell Seal: Release](https://naruto.fandom.com/wiki/Mystic_Safety_Bell_Seal:_Release) | C | Offensive, Supplementary | `narutorpg:mystic_safety_bell_seal_release` |
| [Mystical Palm Technique](https://naruto.fandom.com/wiki/Mystical_Palm_Technique) | A | Supplementary | `narutorpg:mystical_palm_technique` (existing) |
| [Nadeshiko-Style Deep Crimson Dance Performance](https://naruto.fandom.com/wiki/Nadeshiko-Style_Deep_Crimson_Dance_Performance) | C | Offensive | `narutorpg:nadeshiko_style_deep_crimson_dance_performance` (existing) |
| [Nadeshiko-Style Hardliner Gale Fist](https://naruto.fandom.com/wiki/Nadeshiko-Style_Hardliner_Gale_Fist) | C | Offensive | `narutorpg:nadeshiko_style_hardliner_gale_fist` (existing) |
| [Nadeshiko-Style Hardliner Revolving Cut](https://naruto.fandom.com/wiki/Nadeshiko-Style_Hardliner_Revolving_Cut) | C | Offensive | `narutorpg:nadeshiko_style_hardliner_revolving_cut` (existing) |
| [Naka Shrine Pass Technique](https://naruto.fandom.com/wiki/Naka_Shrine_Pass_Technique) | C | Supplementary | `narutorpg:naka_shrine_pass_technique` (existing) |
| [Naraka Path](https://naruto.fandom.com/wiki/Naraka_Path) | B | Offensive, Supplementary | `narutorpg:naraka_path` (existing) |
| [Narutimate Blaster](https://naruto.fandom.com/wiki/Narutimate_Blaster) | C | Offensive | `narutorpg:narutimate_blaster` |
| [Narutimate Tornado](https://naruto.fandom.com/wiki/Narutimate_Tornado) | C | Offensive | `narutorpg:narutimate_tornado` (existing) |
| [Naruto Beam](https://naruto.fandom.com/wiki/Naruto_Beam) | C | Offensive | `narutorpg:naruto_beam` |
| [Naruto Cannon](https://naruto.fandom.com/wiki/Naruto_Cannon) | D | Supplementary | `narutorpg:naruto_cannon` |
| [Naruto Region Combo](https://naruto.fandom.com/wiki/Naruto_Region_Combo) | A | Offensive | `narutorpg:naruto_region_combo` (existing) |
| [Naruto Uzumaki Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Combo) | B | Offensive | `narutorpg:naruto_uzumaki_combo` (existing) |
| [Naruto Uzumaki Combo Attack](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Combo_Attack) | C | Offensive | `narutorpg:naruto_uzumaki_combo_attack` (existing) |
| [Naruto Uzumaki Five Thousand Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Five_Thousand_Combo) | S | Offensive | `narutorpg:naruto_uzumaki_five_thousand_combo` (existing) |
| [Naruto Uzumaki Spiralling Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Spiralling_Combo) | C | Offensive | `narutorpg:naruto_uzumaki_spiralling_combo` (existing) |
| [Naruto Uzumaki Two Thousand Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Two_Thousand_Combo) | A | Offensive | `narutorpg:naruto_uzumaki_two_thousand_combo` (existing) |
| [Naruto Uzumaki's Shinobi Handbook (Ultimate Ninja 3)](https://naruto.fandom.com/wiki/Naruto_Uzumaki's_Shinobi_Handbook_(Ultimate_Ninja_3)) | S | Offensive | `narutorpg:naruto_uzumaki_s_shinobi_handbook_ultimate_ninja_3` (existing) |
| [Naruto Uzumaki's Shinobi Handbook (Ultimate Ninja Storm)](https://naruto.fandom.com/wiki/Naruto_Uzumaki's_Shinobi_Handbook_(Ultimate_Ninja_Storm)) | S | Offensive | `narutorpg:naruto_uzumaki_s_shinobi_handbook_ultimate_ninja_storm` |
| [Naruto and Shion's Super Chakra Rasengan](https://naruto.fandom.com/wiki/Naruto_and_Shion's_Super_Chakra_Rasengan) | B | Offensive | `narutorpg:naruto_and_shion_s_super_chakra_rasengan` |
| [Needle Hell](https://naruto.fandom.com/wiki/Needle_Hell) | C | Offensive | `narutorpg:needle_hell` |
| [Needle Jizō](https://naruto.fandom.com/wiki/Needle_Jizō) | B | Offensive, Defensive | `narutorpg:needle_jizo` |
| [Negative Emotions Sensing](https://naruto.fandom.com/wiki/Negative_Emotions_Sensing) | D | Supplementary | `narutorpg:negative_emotions_sensing` (existing) |
| [Net Shadow Paralysis Technique](https://naruto.fandom.com/wiki/Net_Shadow_Paralysis_Technique) | C | Supplementary | `narutorpg:net_shadow_paralysis_technique` (existing) |
| [Net-Shaped Prison](https://naruto.fandom.com/wiki/Net-Shaped_Prison) | C | Offensive, Supplementary | `narutorpg:net_shaped_prison` (existing) |
| [New Naruto Uzumaki Combo](https://naruto.fandom.com/wiki/New_Naruto_Uzumaki_Combo) | C | Offensive | `narutorpg:new_naruto_uzumaki_combo` (existing) |
| [New Sexy Technique](https://naruto.fandom.com/wiki/New_Sexy_Technique) | D | Supplementary | `narutorpg:new_sexy_technique` |
| [New: Summoning Three-Way Deadlock](https://naruto.fandom.com/wiki/New:_Summoning_Three-Way_Deadlock) | A | Offensive | `narutorpg:new_summoning_three_way_deadlock` (existing) |
| [Newly Invented: Lightning Swift Rasengan](https://naruto.fandom.com/wiki/Newly_Invented:_Lightning_Swift_Rasengan) | B | Offensive | `narutorpg:newly_invented_lightning_swift_rasengan` (existing) |
| [Nine Masked Beasts](https://naruto.fandom.com/wiki/Nine_Masked_Beasts) | C | Supplementary | `narutorpg:nine_masked_beasts` |
| [Nine-Tails Beam](https://naruto.fandom.com/wiki/Nine-Tails_Beam) | C | Offensive | `narutorpg:nine_tails_beam` |
| [Nine-Tails Chakra Bomb](https://naruto.fandom.com/wiki/Nine-Tails_Chakra_Bomb) | C | Offensive | `narutorpg:nine_tails_chakra_bomb` |
| [Nine-Tails Chakra Mode](https://naruto.fandom.com/wiki/Nine-Tails_Chakra_Mode) | C | Offensive, Defensive, Supplementary | `narutorpg:nine_tails_chakra_mode` |
| [Nine-Tails Swirling Orbs](https://naruto.fandom.com/wiki/Nine-Tails_Swirling_Orbs) | C | Offensive | `narutorpg:nine_tails_swirling_orbs` |
| [Nine-Tails Twister](https://naruto.fandom.com/wiki/Nine-Tails_Twister) | C | Offensive | `narutorpg:nine_tails_twister` (existing) |
| [Ninja Art Creation Rebirth — Strength of a Hundred Technique](https://naruto.fandom.com/wiki/Ninja_Art_Creation_Rebirth_—_Strength_of_a_Hundred_Technique) | S | Supplementary | `narutorpg:ninja_art_creation_rebirth_strength_of_a_hundred_technique` |
| [Ninja Art: Fierce Dragon Palm](https://naruto.fandom.com/wiki/Ninja_Art:_Fierce_Dragon_Palm) | C | Offensive | `narutorpg:ninja_art_fierce_dragon_palm` |
| [Ninja Art: Hollow Deception Projection](https://naruto.fandom.com/wiki/Ninja_Art:_Hollow_Deception_Projection) | D | Supplementary | `narutorpg:ninja_art_hollow_deception_projection` |
| [Ninja Art: Hurricane](https://naruto.fandom.com/wiki/Ninja_Art:_Hurricane) | C | Offensive, Defensive | `narutorpg:ninja_art_hurricane` (existing) |
| [Ninja Art: Moonlight Beauty](https://naruto.fandom.com/wiki/Ninja_Art:_Moonlight_Beauty) | C | Offensive, Defensive | `narutorpg:ninja_art_moonlight_beauty` (existing) |
| [Ninja Art: New Moon Flower Wheel](https://naruto.fandom.com/wiki/Ninja_Art:_New_Moon_Flower_Wheel) | C | Offensive | `narutorpg:ninja_art_new_moon_flower_wheel` |
| [Ninja Art: Shuriken Giant Body Technique](https://naruto.fandom.com/wiki/Ninja_Art:_Shuriken_Giant_Body_Technique) | C | Offensive | `narutorpg:ninja_art_shuriken_giant_body_technique` |
| [Ninja Art: Ten Thousand Flowers Profusion](https://naruto.fandom.com/wiki/Ninja_Art:_Ten_Thousand_Flowers_Profusion) | C | Offensive | `narutorpg:ninja_art_ten_thousand_flowers_profusion` |
| [Ninja Tools Barrier: Reverse Fish Scales Formation](https://naruto.fandom.com/wiki/Ninja_Tools_Barrier:_Reverse_Fish_Scales_Formation) | B | Supplementary | `narutorpg:ninja_tools_barrier_reverse_fish_scales_formation` (existing) |
| [Ninken Ninja Art: Dance of the Dust Cloud](https://naruto.fandom.com/wiki/Ninken_Ninja_Art:_Dance_of_the_Dust_Cloud) | B | Supplementary | `narutorpg:ninken_ninja_art_dance_of_the_dust_cloud` (existing) |
| [Ninken Ninja Art: Hound Clone](https://naruto.fandom.com/wiki/Ninken_Ninja_Art:_Hound_Clone) | D | Supplementary | `narutorpg:ninken_ninja_art_hound_clone` |
| [Ninshū Flame](https://naruto.fandom.com/wiki/Ninshū_Flame) | C | Offensive | `narutorpg:ninshu_flame` (existing) |
| [Noisy Riot: Super Beast Imitating Drawing Picture Scroll](https://naruto.fandom.com/wiki/Noisy_Riot:_Super_Beast_Imitating_Drawing_Picture_Scroll) | B | Offensive | `narutorpg:noisy_riot_super_beast_imitating_drawing_picture_scroll` (existing) |
| [Nose Water Tree](https://naruto.fandom.com/wiki/Nose_Water_Tree) | C | Offensive, Supplementary | `narutorpg:nose_water_tree` (existing) |
| [Object Extraction Technique](https://naruto.fandom.com/wiki/Object_Extraction_Technique) | D | Supplementary | `narutorpg:object_extraction_technique` |
| [Object Manipulation Technique](https://naruto.fandom.com/wiki/Object_Manipulation_Technique) | B | Offensive, Defensive, Supplementary | `narutorpg:object_manipulation_technique` (existing) |
| [Octopus Leg Clone Technique](https://naruto.fandom.com/wiki/Octopus_Leg_Clone_Technique) | D | Supplementary | `narutorpg:octopus_leg_clone_technique` |
| [Odour-Concealing Bubbles](https://naruto.fandom.com/wiki/Odour-Concealing_Bubbles) | D | Supplementary | `narutorpg:odour_concealing_bubbles` |
| [Offering Flowers Funeral](https://naruto.fandom.com/wiki/Offering_Flowers_Funeral) | C | Offensive | `narutorpg:offering_flowers_funeral` |
| [One's Own Life Reincarnation](https://naruto.fandom.com/wiki/One's_Own_Life_Reincarnation) | S | Supplementary | `narutorpg:one_s_own_life_reincarnation` |
| [Onikuma Clan's Hijutsu](https://naruto.fandom.com/wiki/Onikuma_Clan's_Hijutsu) | C | Offensive, Supplementary | `narutorpg:onikuma_clan_s_hijutsu` (existing) |
| [Opening and Closing of the Universe](https://naruto.fandom.com/wiki/Opening_and_Closing_of_the_Universe) | A | Offensive | `narutorpg:opening_and_closing_of_the_universe` |
| [Orbs of Light](https://naruto.fandom.com/wiki/Orbs_of_Light) | C | Offensive | `narutorpg:orbs_of_light` |
| [Origami Replacement Technique](https://naruto.fandom.com/wiki/Origami_Replacement_Technique) | B | Supplementary | `narutorpg:origami_replacement_technique` (existing) |
| [Origami Skewer](https://naruto.fandom.com/wiki/Origami_Skewer) | C | Offensive | `narutorpg:origami_skewer` |
| [Origin of Ninshū](https://naruto.fandom.com/wiki/Origin_of_Ninshū) | A | Offensive | `narutorpg:origin_of_ninshu` (existing) |
| [Orochimaru-Style Body Replacement Technique](https://naruto.fandom.com/wiki/Orochimaru-Style_Body_Replacement_Technique) | D | Supplementary | `narutorpg:orochimaru_style_body_replacement_technique` |
| [Outer Path](https://naruto.fandom.com/wiki/Outer_Path) | B | Supplementary | `narutorpg:outer_path` (existing) |
| [Outer Path — Samsara of Heavenly Life Technique](https://naruto.fandom.com/wiki/Outer_Path_—_Samsara_of_Heavenly_Life_Technique) | S | Supplementary | `narutorpg:outer_path_samsara_of_heavenly_life_technique` (existing) |
| [Overwhelm: Rasengan](https://naruto.fandom.com/wiki/Overwhelm:_Rasengan) | A | Offensive | `narutorpg:overwhelm_rasengan` (existing) |
| [Overwind Raid](https://naruto.fandom.com/wiki/Overwind_Raid) | C | Offensive | `narutorpg:overwind_raid` (existing) |
| [Owl Guardian Pursuit](https://naruto.fandom.com/wiki/Owl_Guardian_Pursuit) | C | Offensive | `narutorpg:owl_guardian_pursuit` (existing) |
| [Pachinko Technique](https://naruto.fandom.com/wiki/Pachinko_Technique) | B | Offensive | `narutorpg:pachinko_technique` |
| [Pair of Maidens](https://naruto.fandom.com/wiki/Pair_of_Maidens) | C | Offensive | `narutorpg:pair_of_maidens` |
| [Pair's Kamui](https://naruto.fandom.com/wiki/Pair's_Kamui) | A | Offensive | `narutorpg:pair_s_kamui` (existing) |
| [Palm Wave](https://naruto.fandom.com/wiki/Palm_Wave) | C | Offensive | `narutorpg:palm_wave` (existing) |
| [Paper Chakram](https://naruto.fandom.com/wiki/Paper_Chakram) | C | Offensive | `narutorpg:paper_chakram` |
| [Paper Clone](https://naruto.fandom.com/wiki/Paper_Clone) | C | Offensive, Supplementary | `narutorpg:paper_clone` |
| [Paper Drizzle](https://naruto.fandom.com/wiki/Paper_Drizzle) | C | Offensive | `narutorpg:paper_drizzle` |
| [Paper Person of God Technique](https://naruto.fandom.com/wiki/Paper_Person_of_God_Technique) | A | Offensive, Supplementary | `narutorpg:paper_person_of_god_technique` |
| [Paper Shuriken](https://naruto.fandom.com/wiki/Paper_Shuriken) | D | Offensive | `narutorpg:paper_shuriken` |
| [Parachute](https://naruto.fandom.com/wiki/Parachute) | C | Offensive | `narutorpg:parachute` (existing) |
| [Paralyzing Chakra Field](https://naruto.fandom.com/wiki/Paralyzing_Chakra_Field) | C | Offensive, Supplementary | `narutorpg:paralyzing_chakra_field` |
| [Parasite Clone](https://naruto.fandom.com/wiki/Parasite_Clone) | D | Supplementary | `narutorpg:parasite_clone` |
| [Parasite Demon Demolition Technique](https://naruto.fandom.com/wiki/Parasite_Demon_Demolition_Technique) | B | Offensive | `narutorpg:parasite_demon_demolition_technique` |
| [Parasite Technique](https://naruto.fandom.com/wiki/Parasite_Technique) | C | Offensive, Supplementary | `narutorpg:parasite_technique` |
| [Parasitic Bugs](https://naruto.fandom.com/wiki/Parasitic_Bugs) | C | Offensive, Supplementary | `narutorpg:parasitic_bugs` |
| [Parasitic Destruction Insect Fang](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insect_Fang) | B | Offensive | `narutorpg:parasitic_destruction_insect_fang` (existing) |
| [Parasitic Destruction Insect Technique](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insect_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:parasitic_destruction_insect_technique` (existing) |
| [Parasitic Destruction Insects: Arrowhead Swarm](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Arrowhead_Swarm) | C | Offensive, Defensive | `narutorpg:parasitic_destruction_insects_arrowhead_swarm` |
| [Parasitic Destruction Insects: Bind](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Bind) | C | Offensive | `narutorpg:parasitic_destruction_insects_bind` |
| [Parasitic Destruction Insects: Eclipse](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Eclipse) | C | Offensive, Defensive | `narutorpg:parasitic_destruction_insects_eclipse` |
| [Parasitic Destruction Insects: Hail](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Hail) | C | Offensive, Defensive | `narutorpg:parasitic_destruction_insects_hail` |
| [Parasitic Destruction Insects: Hollow](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Hollow) | C | Offensive, Defensive | `narutorpg:parasitic_destruction_insects_hollow` |
| [Parasitic Destruction Insects: Pupa](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Pupa) | C | Offensive, Supplementary | `narutorpg:parasitic_destruction_insects_pupa` |
| [Parasitic Destruction Insects: Round Mass](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Round_Mass) | C | Offensive | `narutorpg:parasitic_destruction_insects_round_mass` |
| [Parasitic Destruction Insects: Spear](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Spear) | C | Offensive, Defensive | `narutorpg:parasitic_destruction_insects_spear` |
| [Parasitic Destruction Insects: Whirl](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insects:_Whirl) | C | Offensive, Defensive | `narutorpg:parasitic_destruction_insects_whirl` |
| [Parasitic Giant Insect — Bug Bite](https://naruto.fandom.com/wiki/Parasitic_Giant_Insect_—_Bug_Bite) | C | Offensive | `narutorpg:parasitic_giant_insect_bug_bite` (existing) |
| [Parent and Child Rasengan](https://naruto.fandom.com/wiki/Parent_and_Child_Rasengan) | B | Offensive | `narutorpg:parent_and_child_rasengan` |
| [Partial Multi-Size Leg Technique](https://naruto.fandom.com/wiki/Partial_Multi-Size_Leg_Technique) | C | Offensive, Supplementary | `narutorpg:partial_multi_size_leg_technique` (existing) |
| [Partial Multi-Size Technique](https://naruto.fandom.com/wiki/Partial_Multi-Size_Technique) | C | Supplementary | `narutorpg:partial_multi_size_technique` (existing) |
| [Partial Super Multi-Size Technique](https://naruto.fandom.com/wiki/Partial_Super_Multi-Size_Technique) | C | Supplementary | `narutorpg:partial_super_multi_size_technique` (existing) |
| [Past Vision Sharing](https://naruto.fandom.com/wiki/Past_Vision_Sharing) | D | Supplementary | `narutorpg:past_vision_sharing` |
| [Peacock Whirlwind](https://naruto.fandom.com/wiki/Peacock_Whirlwind) | C | Offensive | `narutorpg:peacock_whirlwind` (existing) |
| [Peacock Whirlwind Formation](https://naruto.fandom.com/wiki/Peacock_Whirlwind_Formation) | D | Defensive, Supplementary | `narutorpg:peacock_whirlwind_formation` (existing) |
| [Peacock Wisdom Queen](https://naruto.fandom.com/wiki/Peacock_Wisdom_Queen) | D | - | `narutorpg:peacock_wisdom_queen` |
| [Peerless Great Flame Bullet](https://naruto.fandom.com/wiki/Peerless_Great_Flame_Bullet) | C | Offensive | `narutorpg:peerless_great_flame_bullet` (existing) |
| [Peerless Great Wind Formation](https://naruto.fandom.com/wiki/Peerless_Great_Wind_Formation) | C | Offensive, Defensive | `narutorpg:peerless_great_wind_formation` (existing) |
| [Perfume Spray](https://naruto.fandom.com/wiki/Perfume_Spray) | C | Offensive | `narutorpg:perfume_spray` |
| [Petal Diversion: Chakra Rope](https://naruto.fandom.com/wiki/Petal_Diversion:_Chakra_Rope) | C | Offensive | `narutorpg:petal_diversion_chakra_rope` |
| [Petal Diversion: Flower Fireworks](https://naruto.fandom.com/wiki/Petal_Diversion:_Flower_Fireworks) | C | Offensive | `narutorpg:petal_diversion_flower_fireworks` |
| [Pheasant Hunter](https://naruto.fandom.com/wiki/Pheasant_Hunter) | C | Offensive | `narutorpg:pheasant_hunter` (existing) |
| [Piercing Cloud Spear](https://naruto.fandom.com/wiki/Piercing_Cloud_Spear) | C | Offensive | `narutorpg:piercing_cloud_spear` |
| [Piercing Separation Spring](https://naruto.fandom.com/wiki/Piercing_Separation_Spring) | C | Offensive | `narutorpg:piercing_separation_spring` (existing) |
| [Pinching Rage: Rasengan](https://naruto.fandom.com/wiki/Pinching_Rage:_Rasengan) | C | Offensive | `narutorpg:pinching_rage_rasengan` (existing) |
| [Pink Lightning](https://naruto.fandom.com/wiki/Pink_Lightning) | C | Offensive | `narutorpg:pink_lightning` (existing) |
| [Piston Fist](https://naruto.fandom.com/wiki/Piston_Fist) | A | Offensive | `narutorpg:piston_fist` (existing) |
| [Piston Fist: Style One](https://naruto.fandom.com/wiki/Piston_Fist:_Style_One) | A | Offensive, Supplementary | `narutorpg:piston_fist_style_one` (existing) |
| [Piston Fist: Style Zero](https://naruto.fandom.com/wiki/Piston_Fist:_Style_Zero) | A | Offensive | `narutorpg:piston_fist_style_zero` (existing) |
| [Planetary Rasengan](https://naruto.fandom.com/wiki/Planetary_Rasengan) | A | Offensive | `narutorpg:planetary_rasengan` |
| [Poison Cloud](https://naruto.fandom.com/wiki/Poison_Cloud) | C | Offensive | `narutorpg:poison_cloud` |
| [Poison Cloud Technique](https://naruto.fandom.com/wiki/Poison_Cloud_Technique) | C | Offensive | `narutorpg:poison_cloud_technique` (existing) |
| [Poison Missile](https://naruto.fandom.com/wiki/Poison_Missile) | C | Offensive | `narutorpg:poison_missile` |
| [Poison Mist](https://naruto.fandom.com/wiki/Poison_Mist) | B | Offensive | `narutorpg:poison_mist` (existing) |
| [Poison Mist Hell: Hundred Continuous Firing](https://naruto.fandom.com/wiki/Poison_Mist_Hell:_Hundred_Continuous_Firing) | C | Offensive | `narutorpg:poison_mist_hell_hundred_continuous_firing` |
| [Poison Mist Needle Shot](https://naruto.fandom.com/wiki/Poison_Mist_Needle_Shot) | C | Offensive | `narutorpg:poison_mist_needle_shot` |
| [Polarised Sand Formation: Hot Sand Camouflage](https://naruto.fandom.com/wiki/Polarised_Sand_Formation:_Hot_Sand_Camouflage) | D | Supplementary | `narutorpg:polarised_sand_formation_hot_sand_camouflage` |
| [Possession](https://naruto.fandom.com/wiki/Possession) | C | Offensive, Supplementary | `narutorpg:possession` |
| [Power of the Sage](https://naruto.fandom.com/wiki/Power_of_the_Sage) | C | Offensive | `narutorpg:power_of_the_sage` (existing) |
| [Prepared Needle Shot](https://naruto.fandom.com/wiki/Prepared_Needle_Shot) | C | Offensive | `narutorpg:prepared_needle_shot` |
| [Prepared Puppet: Eight Waves of Needles](https://naruto.fandom.com/wiki/Prepared_Puppet:_Eight_Waves_of_Needles) | C | Offensive | `narutorpg:prepared_puppet_eight_waves_of_needles` |
| [Pressure Points of Harm and Death](https://naruto.fandom.com/wiki/Pressure_Points_of_Harm_and_Death) | D | Supplementary | `narutorpg:pressure_points_of_harm_and_death` |
| [Preta Path](https://naruto.fandom.com/wiki/Preta_Path) | B | - | `narutorpg:preta_path` (existing) |
| [Prison Flame Funeral](https://naruto.fandom.com/wiki/Prison_Flame_Funeral) | B | Offensive | `narutorpg:prison_flame_funeral` (existing) |
| [Prison Sand Burial](https://naruto.fandom.com/wiki/Prison_Sand_Burial) | C | Offensive | `narutorpg:prison_sand_burial` |
| [Problem Children of the Academy](https://naruto.fandom.com/wiki/Problem_Children_of_the_Academy) | B | Offensive, Supplementary | `narutorpg:problem_children_of_the_academy` |
| [Projection Technique](https://naruto.fandom.com/wiki/Projection_Technique) | D | Supplementary | `narutorpg:projection_technique` |
| [Protective Cloth Dome](https://naruto.fandom.com/wiki/Protective_Cloth_Dome) | D | Defensive | `narutorpg:protective_cloth_dome` |
| [Protective Sphere of Light](https://naruto.fandom.com/wiki/Protective_Sphere_of_Light) | C | Defensive, Supplementary | `narutorpg:protective_sphere_of_light` |
| [Psycho Mind Transmission](https://naruto.fandom.com/wiki/Psycho_Mind_Transmission) | C | Supplementary | `narutorpg:psycho_mind_transmission` |
| [Puppet Curse](https://naruto.fandom.com/wiki/Puppet_Curse) | D | Supplementary | `narutorpg:puppet_curse` |
| [Puppet Imitation Technique](https://naruto.fandom.com/wiki/Puppet_Imitation_Technique) | D | Supplementary | `narutorpg:puppet_imitation_technique` |
| [Puppet Performance: One in Body and Soul](https://naruto.fandom.com/wiki/Puppet_Performance:_One_in_Body_and_Soul) | C | Offensive | `narutorpg:puppet_performance_one_in_body_and_soul` |
| [Puppet Performance: Skilful Achievement with a Human Body](https://naruto.fandom.com/wiki/Puppet_Performance:_Skilful_Achievement_with_a_Human_Body) | A | Supplementary | `narutorpg:puppet_performance_skilful_achievement_with_a_human_body` (existing) |
| [Puppet Reincarnation](https://naruto.fandom.com/wiki/Puppet_Reincarnation) | B | Offensive, Supplementary | `narutorpg:puppet_reincarnation` (existing) |
| [Puppet Scatter Shot](https://naruto.fandom.com/wiki/Puppet_Scatter_Shot) | C | Offensive | `narutorpg:puppet_scatter_shot` |
| [Puppet Smoke Bombs](https://naruto.fandom.com/wiki/Puppet_Smoke_Bombs) | C | Offensive | `narutorpg:puppet_smoke_bombs` |
| [Puppet Technique](https://naruto.fandom.com/wiki/Puppet_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:puppet_technique` (existing) |
| [Puppet Technique: Bishamon](https://naruto.fandom.com/wiki/Puppet_Technique:_Bishamon) | C | Offensive | `narutorpg:puppet_technique_bishamon` |
| [Puppet Technique: Combo Attack](https://naruto.fandom.com/wiki/Puppet_Technique:_Combo_Attack) | C | Offensive | `narutorpg:puppet_technique_combo_attack` |
| [Puppet Technique: Heavy Blade Warehouse](https://naruto.fandom.com/wiki/Puppet_Technique:_Heavy_Blade_Warehouse) | C | Offensive | `narutorpg:puppet_technique_heavy_blade_warehouse` |
| [Puppet Technique: Prosthetic Arm Senbon](https://naruto.fandom.com/wiki/Puppet_Technique:_Prosthetic_Arm_Senbon) | C | Offensive | `narutorpg:puppet_technique_prosthetic_arm_senbon` |
| [Puppet Technique: Spear](https://naruto.fandom.com/wiki/Puppet_Technique:_Spear) | C | Offensive | `narutorpg:puppet_technique_spear` |
| [Puppet Technique: Sword](https://naruto.fandom.com/wiki/Puppet_Technique:_Sword) | C | Offensive | `narutorpg:puppet_technique_sword` |
| [Puppet Water Release: Dance of Mystic Rain](https://naruto.fandom.com/wiki/Puppet_Water_Release:_Dance_of_Mystic_Rain) | C | Offensive | `narutorpg:puppet_water_release_dance_of_mystic_rain` (existing) |
| [Puppet-Cursing Sphere](https://naruto.fandom.com/wiki/Puppet-Cursing_Sphere) | C | Offensive, Supplementary | `narutorpg:puppet_cursing_sphere` |
| [Puppet: Burning Blade](https://naruto.fandom.com/wiki/Puppet:_Burning_Blade) | C | Offensive | `narutorpg:puppet_burning_blade` |
| [Puppet: Corpse Crow](https://naruto.fandom.com/wiki/Puppet:_Corpse_Crow) | C | Offensive | `narutorpg:puppet_corpse_crow` |
| [Puppet: Dance of the Cherry Blossom](https://naruto.fandom.com/wiki/Puppet:_Dance_of_the_Cherry_Blossom) | B | Offensive | `narutorpg:puppet_dance_of_the_cherry_blossom` (existing) |
| [Puppet: Degeneration](https://naruto.fandom.com/wiki/Puppet:_Degeneration) | C | Offensive | `narutorpg:puppet_degeneration` |
| [Puppet: Demon Castle Mask](https://naruto.fandom.com/wiki/Puppet:_Demon_Castle_Mask) | C | Offensive | `narutorpg:puppet_demon_castle_mask` |
| [Puppet: Early Summer Rain](https://naruto.fandom.com/wiki/Puppet:_Early_Summer_Rain) | C | Offensive | `narutorpg:puppet_early_summer_rain` |
| [Puppet: Father and Mother](https://naruto.fandom.com/wiki/Puppet:_Father_and_Mother) | C | Offensive | `narutorpg:puppet_father_and_mother` |
| [Puppet: Inexhaustible Explosive Blades](https://naruto.fandom.com/wiki/Puppet:_Inexhaustible_Explosive_Blades) | C | Offensive, Defensive | `narutorpg:puppet_inexhaustible_explosive_blades` |
| [Puppet: Iron Sand Prison Mass](https://naruto.fandom.com/wiki/Puppet:_Iron_Sand_Prison_Mass) | B | Offensive, Defensive | `narutorpg:puppet_iron_sand_prison_mass` (existing) |
| [Puppet: Revolving Blade Heavenly Drill](https://naruto.fandom.com/wiki/Puppet:_Revolving_Blade_Heavenly_Drill) | C | Offensive, Defensive | `narutorpg:puppet_revolving_blade_heavenly_drill` |
| [Puppet: Rotation Attack](https://naruto.fandom.com/wiki/Puppet:_Rotation_Attack) | C | Offensive | `narutorpg:puppet_rotation_attack` (existing) |
| [Puppet: Shadow Actor](https://naruto.fandom.com/wiki/Puppet:_Shadow_Actor) | C | Offensive | `narutorpg:puppet_shadow_actor` (existing) |
| [Puppet: Spewing Bullet](https://naruto.fandom.com/wiki/Puppet:_Spewing_Bullet) | C | Offensive | `narutorpg:puppet_spewing_bullet` |
| [Puppet: Spring Punishing Strike](https://naruto.fandom.com/wiki/Puppet:_Spring_Punishing_Strike) | B | Offensive | `narutorpg:puppet_spring_punishing_strike` (existing) |
| [Quarantine Wall](https://naruto.fandom.com/wiki/Quarantine_Wall) | C | Defensive | `narutorpg:quarantine_wall` |
| [Quicksand Waterfall Flow](https://naruto.fandom.com/wiki/Quicksand_Waterfall_Flow) | C | Offensive | `narutorpg:quicksand_waterfall_flow` |
| [Quicksand Waterfall Flow: Poison](https://naruto.fandom.com/wiki/Quicksand_Waterfall_Flow:_Poison) | B | Offensive | `narutorpg:quicksand_waterfall_flow_poison` |
| [Raging Pounce](https://naruto.fandom.com/wiki/Raging_Pounce) | B | Offensive | `narutorpg:raging_pounce` (existing) |
| [Raging Snakes](https://naruto.fandom.com/wiki/Raging_Snakes) | C | Offensive | `narutorpg:raging_snakes` |
| [Rain Tiger at Will Technique](https://naruto.fandom.com/wiki/Rain_Tiger_at_Will_Technique) | D | Supplementary | `narutorpg:rain_tiger_at_will_technique` (existing) |
| [Rain of Spiders](https://naruto.fandom.com/wiki/Rain_of_Spiders) | B | Offensive | `narutorpg:rain_of_spiders` |
| [Rapid Fire](https://naruto.fandom.com/wiki/Rapid_Fire) | C | Offensive | `narutorpg:rapid_fire` |
| [Rasengan](https://naruto.fandom.com/wiki/Rasengan) | A | Offensive | `narutorpg:rasengan` (existing) |
| [Rasengan Connection](https://naruto.fandom.com/wiki/Rasengan_Connection) | C | Offensive, Defensive | `narutorpg:rasengan_connection` |
| [Rasengan: "Ensemble"](https://naruto.fandom.com/wiki/Rasengan:_"Ensemble") | S | Offensive | `narutorpg:rasengan_ensemble` (existing) |
| [Rasengan: "Unison"](https://naruto.fandom.com/wiki/Rasengan:_"Unison") | S | Offensive | `narutorpg:rasengan_unison` |
| [Rasengan: "Uzuhiko"](https://naruto.fandom.com/wiki/Rasengan:_"Uzuhiko") | C | Offensive, Supplementary | `narutorpg:rasengan_uzuhiko` |
| [Rasengan: Aerial Attack](https://naruto.fandom.com/wiki/Rasengan:_Aerial_Attack) | C | Offensive | `narutorpg:rasengan_aerial_attack` (existing) |
| [Rasengan: Crown of the Head](https://naruto.fandom.com/wiki/Rasengan:_Crown_of_the_Head) | C | Offensive | `narutorpg:rasengan_crown_of_the_head` (existing) |
| [Rasengan: Flash](https://naruto.fandom.com/wiki/Rasengan:_Flash) | C | Offensive, Supplementary | `narutorpg:rasengan_flash` (existing) |
| [Rasengan: Hail](https://naruto.fandom.com/wiki/Rasengan:_Hail) | C | Offensive | `narutorpg:rasengan_hail` (existing) |
| [Rasengan: Nosedive](https://naruto.fandom.com/wiki/Rasengan:_Nosedive) | C | Offensive | `narutorpg:rasengan_nosedive` (existing) |
| [Rasengan: Parachute](https://naruto.fandom.com/wiki/Rasengan:_Parachute) | C | Offensive | `narutorpg:rasengan_parachute` (existing) |
| [Rasenshuriken: Orange: Pursuit Attack](https://naruto.fandom.com/wiki/Rasenshuriken:_Orange:_Pursuit_Attack) | S | Offensive | `narutorpg:rasenshuriken_orange_pursuit_attack` (existing) |
| [Ravaging Chakra Knives](https://naruto.fandom.com/wiki/Ravaging_Chakra_Knives) | C | Offensive | `narutorpg:ravaging_chakra_knives` |
| [Red Flash](https://naruto.fandom.com/wiki/Red_Flash) | A | Offensive | `narutorpg:red_flash` |
| [Red Secret Technique: Machinery Triangles](https://naruto.fandom.com/wiki/Red_Secret_Technique:_Machinery_Triangles) | C | Offensive | `narutorpg:red_secret_technique_machinery_triangles` |
| [Red Secret Technique: Performance of a Hundred Puppets](https://naruto.fandom.com/wiki/Red_Secret_Technique:_Performance_of_a_Hundred_Puppets) | S | Offensive, Defensive | `narutorpg:red_secret_technique_performance_of_a_hundred_puppets` |
| [Red Secret Technique: Red Sea](https://naruto.fandom.com/wiki/Red_Secret_Technique:_Red_Sea) | C | Offensive | `narutorpg:red_secret_technique_red_sea` |
| [Red-Banded Snake](https://naruto.fandom.com/wiki/Red-Banded_Snake) | A | Offensive | `narutorpg:red_banded_snake` |
| [Redaku Reincarnation Ninjutsu](https://naruto.fandom.com/wiki/Redaku_Reincarnation_Ninjutsu) | S | - | `narutorpg:redaku_reincarnation_ninjutsu` |
| [Repulsion Technique](https://naruto.fandom.com/wiki/Repulsion_Technique) | C | Offensive, Defensive | `narutorpg:repulsion_technique` |
| [Resonance Blast](https://naruto.fandom.com/wiki/Resonance_Blast) | B | Offensive | `narutorpg:resonance_blast` (existing) |
| [Resonating Echo Drill](https://naruto.fandom.com/wiki/Resonating_Echo_Drill) | C | Offensive | `narutorpg:resonating_echo_drill` |
| [Return of the Devil](https://naruto.fandom.com/wiki/Return_of_the_Devil) | C | Offensive | `narutorpg:return_of_the_devil` (existing) |
| [Reverse Four Symbols Sealing](https://naruto.fandom.com/wiki/Reverse_Four_Symbols_Sealing) | S | Offensive, Supplementary | `narutorpg:reverse_four_symbols_sealing` (existing) |
| [Reverse Mist Beheading](https://naruto.fandom.com/wiki/Reverse_Mist_Beheading) | C | Offensive | `narutorpg:reverse_mist_beheading` |
| [Reverse Summoning Technique](https://naruto.fandom.com/wiki/Reverse_Summoning_Technique) | A | Supplementary | `narutorpg:reverse_summoning_technique` |
| [Reverse White Secret Technique: The Uematsu Collection of Ten Puppets](https://naruto.fandom.com/wiki/Reverse_White_Secret_Technique:_The_Uematsu_Collection_of_Ten_Puppets) | C | Offensive | `narutorpg:reverse_white_secret_technique_the_uematsu_collection_of_ten_puppets` |
| [Reversible Technique](https://naruto.fandom.com/wiki/Reversible_Technique) | C | Offensive | `narutorpg:reversible_technique` |
| [Revival Fist](https://naruto.fandom.com/wiki/Revival_Fist) | C | Offensive | `narutorpg:revival_fist` |
| [Revolt of the Demon World](https://naruto.fandom.com/wiki/Revolt_of_the_Demon_World) | B | Offensive | `narutorpg:revolt_of_the_demon_world` |
| [Revolving Throw](https://naruto.fandom.com/wiki/Revolving_Throw) | C | Offensive | `narutorpg:revolving_throw` (existing) |
| [Rinha Clan's Kekkei Genkai](https://naruto.fandom.com/wiki/Rinha_Clan's_Kekkei_Genkai) | B | - | `narutorpg:rinha_clan_s_kekkei_genkai` (existing) |
| [Rinne Six Paths](https://naruto.fandom.com/wiki/Rinne_Six_Paths) | B | Offensive | `narutorpg:rinne_six_paths` (existing) |
| [Rising Bomber](https://naruto.fandom.com/wiki/Rising_Bomber) | C | Offensive | `narutorpg:rising_bomber` (existing) |
| [Rising Sun](https://naruto.fandom.com/wiki/Rising_Sun) | B | Offensive | `narutorpg:rising_sun` (existing) |
| [Rivalling Wills](https://naruto.fandom.com/wiki/Rivalling_Wills) | B | Offensive | `narutorpg:rivalling_wills` (existing) |
| [Rock Armour](https://naruto.fandom.com/wiki/Rock_Armour) | C | Offensive, Defensive | `narutorpg:rock_armour` (existing) |
| [Rock Avalanche](https://naruto.fandom.com/wiki/Rock_Avalanche) | C | Offensive | `narutorpg:rock_avalanche` (existing) |
| [Rock Avalanche Formation](https://naruto.fandom.com/wiki/Rock_Avalanche_Formation) | B | Offensive | `narutorpg:rock_avalanche_formation` (existing) |
| [Rock Clone Technique](https://naruto.fandom.com/wiki/Rock_Clone_Technique) | D | Supplementary | `narutorpg:rock_clone_technique` (existing) |
| [Rock Hive](https://naruto.fandom.com/wiki/Rock_Hive) | C | Supplementary | `narutorpg:rock_hive` (existing) |
| [Rocket Rasengan](https://naruto.fandom.com/wiki/Rocket_Rasengan) | C | Offensive | `narutorpg:rocket_rasengan` |
| [Roof Tile Shuriken](https://naruto.fandom.com/wiki/Roof_Tile_Shuriken) | C | Offensive | `narutorpg:roof_tile_shuriken` |
| [Root Explosive Entombment](https://naruto.fandom.com/wiki/Root_Explosive_Entombment) | B | Offensive | `narutorpg:root_explosive_entombment` (existing) |
| [Rope Escape Technique](https://naruto.fandom.com/wiki/Rope_Escape_Technique) | E | Supplementary | `narutorpg:rope_escape_technique` |
| [Rotating Ferocious Wind](https://naruto.fandom.com/wiki/Rotating_Ferocious_Wind) | C | Offensive | `narutorpg:rotating_ferocious_wind` (existing) |
| [Rotating Funeral](https://naruto.fandom.com/wiki/Rotating_Funeral) | A | Offensive | `narutorpg:rotating_funeral` |
| [Rough Sea Barrier](https://naruto.fandom.com/wiki/Rough_Sea_Barrier) | A | Defensive, Supplementary | `narutorpg:rough_sea_barrier` |
| [Rough Sea Splash](https://naruto.fandom.com/wiki/Rough_Sea_Splash) | C | Offensive | `narutorpg:rough_sea_splash` (existing) |
| [Round Moon](https://naruto.fandom.com/wiki/Round_Moon) | C | Offensive | `narutorpg:round_moon` (existing) |
| [Ruining Sand Waterfall Burial](https://naruto.fandom.com/wiki/Ruining_Sand_Waterfall_Burial) | C | Offensive, Defensive | `narutorpg:ruining_sand_waterfall_burial` |
| [Runt Ball Rasengan](https://naruto.fandom.com/wiki/Runt_Ball_Rasengan) | C | Offensive | `narutorpg:runt_ball_rasengan` |
| [Rupture](https://naruto.fandom.com/wiki/Rupture) | C | Offensive | `narutorpg:rupture` |
| [Ryūmyaku Chakra Control](https://naruto.fandom.com/wiki/Ryūmyaku_Chakra_Control) | D | Supplementary | `narutorpg:ryumyaku_chakra_control` |
| [Ryūmyaku: Great Dragon Fire](https://naruto.fandom.com/wiki/Ryūmyaku:_Great_Dragon_Fire) | C | Offensive | `narutorpg:ryumyaku_great_dragon_fire` (existing) |
| [Ryūmyaku: Super Great Dragon Fire Technique](https://naruto.fandom.com/wiki/Ryūmyaku:_Super_Great_Dragon_Fire_Technique) | C | Offensive | `narutorpg:ryumyaku_super_great_dragon_fire_technique` (existing) |
| [Ryūzetsu's Kekkei Genkai](https://naruto.fandom.com/wiki/Ryūzetsu's_Kekkei_Genkai) | B | - | `narutorpg:ryuzetsu_s_kekkei_genkai` (existing) |
| [Sadistic Combination](https://naruto.fandom.com/wiki/Sadistic_Combination) | C | Offensive | `narutorpg:sadistic_combination` (existing) |
| [Sage Art Wood Release: True Several Thousand Hands](https://naruto.fandom.com/wiki/Sage_Art_Wood_Release:_True_Several_Thousand_Hands) | A | Offensive, Defensive | `narutorpg:sage_art_wood_release_true_several_thousand_hands` (existing) |
| [Sage Art: Amphibian Technique](https://naruto.fandom.com/wiki/Sage_Art:_Amphibian_Technique) | A | Supplementary | `narutorpg:sage_art_amphibian_technique` |
| [Sage Art: Big Ball Spiralling Drop: Instant Lightning](https://naruto.fandom.com/wiki/Sage_Art:_Big_Ball_Spiralling_Drop:_Instant_Lightning) | A | Offensive | `narutorpg:sage_art_big_ball_spiralling_drop_instant_lightning` |
| [Sage Art: Five Fire Gods' Fan of Flames](https://naruto.fandom.com/wiki/Sage_Art:_Five_Fire_Gods'_Fan_of_Flames) | A | Offensive, Supplementary | `narutorpg:sage_art_five_fire_gods_fan_of_flames` (existing) |
| [Sage Art: Five Fire Gods' Fan of Flames: True Fire](https://naruto.fandom.com/wiki/Sage_Art:_Five_Fire_Gods'_Fan_of_Flames:_True_Fire) | A | Offensive | `narutorpg:sage_art_five_fire_gods_fan_of_flames_true_fire` (existing) |
| [Sage Art: Frog Call](https://naruto.fandom.com/wiki/Sage_Art:_Frog_Call) | A | Supplementary | `narutorpg:sage_art_frog_call` |
| [Sage Art: Frog Call Spiral](https://naruto.fandom.com/wiki/Sage_Art:_Frog_Call_Spiral) | S | Offensive | `narutorpg:sage_art_frog_call_spiral` (existing) |
| [Sage Art: Gate of the Great God](https://naruto.fandom.com/wiki/Sage_Art:_Gate_of_the_Great_God) | A | Supplementary | `narutorpg:sage_art_gate_of_the_great_god` |
| [Sage Art: Goemon](https://naruto.fandom.com/wiki/Sage_Art:_Goemon) | A | Offensive | `narutorpg:sage_art_goemon` (existing) |
| [Sage Art: Great Snake Lightning](https://naruto.fandom.com/wiki/Sage_Art:_Great_Snake_Lightning) | A | Offensive | `narutorpg:sage_art_great_snake_lightning` (existing) |
| [Sage Art: Great Snake Lightning: White Snake](https://naruto.fandom.com/wiki/Sage_Art:_Great_Snake_Lightning:_White_Snake) | A | Offensive | `narutorpg:sage_art_great_snake_lightning_white_snake` (existing) |
| [Sage Art: Ink-Splatter Rasenshuriken](https://naruto.fandom.com/wiki/Sage_Art:_Ink-Splatter_Rasenshuriken) | S | Offensive | `narutorpg:sage_art_ink_splatter_rasenshuriken` (existing) |
| [Sage Art: Inorganic Reincarnation](https://naruto.fandom.com/wiki/Sage_Art:_Inorganic_Reincarnation) | A | Offensive, Supplementary | `narutorpg:sage_art_inorganic_reincarnation` |
| [Sage Art: Lava Release Rasenshuriken](https://naruto.fandom.com/wiki/Sage_Art:_Lava_Release_Rasenshuriken) | S | Offensive | `narutorpg:sage_art_lava_release_rasenshuriken` (existing) |
| [Sage Art: Lightning Cage Border Jail](https://naruto.fandom.com/wiki/Sage_Art:_Lightning_Cage_Border_Jail) | A | Offensive | `narutorpg:sage_art_lightning_cage_border_jail` (existing) |
| [Sage Art: Magnet Release Rasengan](https://naruto.fandom.com/wiki/Sage_Art:_Magnet_Release_Rasengan) | A | Offensive | `narutorpg:sage_art_magnet_release_rasengan` (existing) |
| [Sage Art: Many Ultra-Big Ball Spiralling Serial Spheres](https://naruto.fandom.com/wiki/Sage_Art:_Many_Ultra-Big_Ball_Spiralling_Serial_Spheres) | A | Offensive | `narutorpg:sage_art_many_ultra_big_ball_spiralling_serial_spheres` |
| [Sage Art: Scattering Rage Technique](https://naruto.fandom.com/wiki/Sage_Art:_Scattering_Rage_Technique) | A | Offensive | `narutorpg:sage_art_scattering_rage_technique` |
| [Sage Art: Storm Release Light Fang](https://naruto.fandom.com/wiki/Sage_Art:_Storm_Release_Light_Fang) | A | Offensive | `narutorpg:sage_art_storm_release_light_fang` (existing) |
| [Sage Art: Super Tailed Beast Rasenshuriken](https://naruto.fandom.com/wiki/Sage_Art:_Super_Tailed_Beast_Rasenshuriken) | A | Offensive | `narutorpg:sage_art_super_tailed_beast_rasenshuriken` (existing) |
| [Sage Art: White Rage Technique](https://naruto.fandom.com/wiki/Sage_Art:_White_Rage_Technique) | A | Offensive, Supplementary | `narutorpg:sage_art_white_rage_technique` |
| [Sage Art: Wind Release Dust Cloud](https://naruto.fandom.com/wiki/Sage_Art:_Wind_Release_Dust_Cloud) | A | Supplementary | `narutorpg:sage_art_wind_release_dust_cloud` (existing) |
| [Sage Art: Wood Release: Ground Pulse](https://naruto.fandom.com/wiki/Sage_Art:_Wood_Release:_Ground_Pulse) | A | Offensive | `narutorpg:sage_art_wood_release_ground_pulse` (existing) |
| [Sage Art: Wood Release: Wood Dragon Dance Performance](https://naruto.fandom.com/wiki/Sage_Art:_Wood_Release:_Wood_Dragon_Dance_Performance) | A | Offensive | `narutorpg:sage_art_wood_release_wood_dragon_dance_performance` (existing) |
| [Sage Art: Yin Release Lightning Dispatch](https://naruto.fandom.com/wiki/Sage_Art:_Yin_Release_Lightning_Dispatch) | A | Offensive, Supplementary | `narutorpg:sage_art_yin_release_lightning_dispatch` (existing) |
| [Sage Combo](https://naruto.fandom.com/wiki/Sage_Combo) | A | Offensive | `narutorpg:sage_combo` (existing) |
| [Sakon and Ukon's Kekkei Genkai](https://naruto.fandom.com/wiki/Sakon_and_Ukon's_Kekkei_Genkai) | B | - | `narutorpg:sakon_and_ukon_s_kekkei_genkai` (existing) |
| [Sakura Blizzard Technique](https://naruto.fandom.com/wiki/Sakura_Blizzard_Technique) | D | Supplementary | `narutorpg:sakura_blizzard_technique` |
| [Sakura Special Shuriken Throwing Technique](https://naruto.fandom.com/wiki/Sakura_Special_Shuriken_Throwing_Technique) | C | Offensive | `narutorpg:sakura_special_shuriken_throwing_technique` |
| [Samehada Colliding Explosion](https://naruto.fandom.com/wiki/Samehada_Colliding_Explosion) | C | Offensive | `narutorpg:samehada_colliding_explosion` (existing) |
| [Samurai Sabre Technique](https://naruto.fandom.com/wiki/Samurai_Sabre_Technique) | C | Offensive | `narutorpg:samurai_sabre_technique` |
| [Sand Binding Coffin](https://naruto.fandom.com/wiki/Sand_Binding_Coffin) | C | Offensive | `narutorpg:sand_binding_coffin` |
| [Sand Binding Prison](https://naruto.fandom.com/wiki/Sand_Binding_Prison) | B | Defensive | `narutorpg:sand_binding_prison` |
| [Sand Blade Double Attack](https://naruto.fandom.com/wiki/Sand_Blade_Double_Attack) | C | Offensive | `narutorpg:sand_blade_double_attack` |
| [Sand Body](https://naruto.fandom.com/wiki/Sand_Body) | D | Supplementary, Defensive | `narutorpg:sand_body` |
| [Sand Bullet](https://naruto.fandom.com/wiki/Sand_Bullet) | C | Offensive | `narutorpg:sand_bullet` |
| [Sand Castle Wolf Horn](https://naruto.fandom.com/wiki/Sand_Castle_Wolf_Horn) | C | Offensive, Supplementary | `narutorpg:sand_castle_wolf_horn` |
| [Sand Characters](https://naruto.fandom.com/wiki/Sand_Characters) | D | Supplementary | `narutorpg:sand_characters` |
| [Sand Cloak Technique](https://naruto.fandom.com/wiki/Sand_Cloak_Technique) | D | Supplementary | `narutorpg:sand_cloak_technique` |
| [Sand Clone](https://naruto.fandom.com/wiki/Sand_Clone) | D | Supplementary | `narutorpg:sand_clone` |
| [Sand Cloud](https://naruto.fandom.com/wiki/Sand_Cloud) | C | Offensive | `narutorpg:sand_cloud` |
| [Sand Cloud Circle](https://naruto.fandom.com/wiki/Sand_Cloud_Circle) | C | Offensive | `narutorpg:sand_cloud_circle` |
| [Sand Cloud Dancing Rasengan](https://naruto.fandom.com/wiki/Sand_Cloud_Dancing_Rasengan) | B | Offensive | `narutorpg:sand_cloud_dancing_rasengan` |
| [Sand Cloud Dark Coffin](https://naruto.fandom.com/wiki/Sand_Cloud_Dark_Coffin) | C | Offensive | `narutorpg:sand_cloud_dark_coffin` |
| [Sand Cruel Pursuit Interment](https://naruto.fandom.com/wiki/Sand_Cruel_Pursuit_Interment) | C | Offensive | `narutorpg:sand_cruel_pursuit_interment` |
| [Sand Drizzle](https://naruto.fandom.com/wiki/Sand_Drizzle) | C | Offensive | `narutorpg:sand_drizzle` |
| [Sand Encampment Wall](https://naruto.fandom.com/wiki/Sand_Encampment_Wall) | C | Offensive, Defensive | `narutorpg:sand_encampment_wall` |
| [Sand Exploding Sand Cluster](https://naruto.fandom.com/wiki/Sand_Exploding_Sand_Cluster) | C | Offensive | `narutorpg:sand_exploding_sand_cluster` |
| [Sand Formation Combo](https://naruto.fandom.com/wiki/Sand_Formation_Combo) | C | Offensive | `narutorpg:sand_formation_combo` (existing) |
| [Sand Formation: Shisa](https://naruto.fandom.com/wiki/Sand_Formation:_Shisa) | C | Offensive | `narutorpg:sand_formation_shisa` |
| [Sand Hail](https://naruto.fandom.com/wiki/Sand_Hail) | C | Offensive | `narutorpg:sand_hail` |
| [Sand Hell](https://naruto.fandom.com/wiki/Sand_Hell) | C | Offensive | `narutorpg:sand_hell` |
| [Sand Lightning Needles](https://naruto.fandom.com/wiki/Sand_Lightning_Needles) | C | Offensive | `narutorpg:sand_lightning_needles` |
| [Sand Mesh](https://naruto.fandom.com/wiki/Sand_Mesh) | D | Supplementary | `narutorpg:sand_mesh` |
| [Sand Pillar](https://naruto.fandom.com/wiki/Sand_Pillar) | D | Supplementary | `narutorpg:sand_pillar` |
| [Sand Prison Spear Attack](https://naruto.fandom.com/wiki/Sand_Prison_Spear_Attack) | C | Offensive | `narutorpg:sand_prison_spear_attack` |
| [Sand Restraining Technique](https://naruto.fandom.com/wiki/Sand_Restraining_Technique) | C | Offensive, Supplementary | `narutorpg:sand_restraining_technique` |
| [Sand Shuriken](https://naruto.fandom.com/wiki/Sand_Shuriken) | C | Offensive | `narutorpg:sand_shuriken` |
| [Sand Stream](https://naruto.fandom.com/wiki/Sand_Stream) | D | Supplementary | `narutorpg:sand_stream` |
| [Sand Swift Spear Attack](https://naruto.fandom.com/wiki/Sand_Swift_Spear_Attack) | C | Offensive | `narutorpg:sand_swift_spear_attack` |
| [Sand Sword](https://naruto.fandom.com/wiki/Sand_Sword) | C | Offensive | `narutorpg:sand_sword` |
| [Sand Taijutsu Tsunami](https://naruto.fandom.com/wiki/Sand_Taijutsu_Tsunami) | B | Offensive | `narutorpg:sand_taijutsu_tsunami` (existing) |
| [Sand Waterfall Funeral](https://naruto.fandom.com/wiki/Sand_Waterfall_Funeral) | C | Offensive | `narutorpg:sand_waterfall_funeral` |
| [Sand Waterfall Imperial Funeral](https://naruto.fandom.com/wiki/Sand_Waterfall_Imperial_Funeral) | C | Offensive | `narutorpg:sand_waterfall_imperial_funeral` |
| [Sand Waterfall Mass Attack](https://naruto.fandom.com/wiki/Sand_Waterfall_Mass_Attack) | C | Offensive | `narutorpg:sand_waterfall_mass_attack` |
| [Sand Waterfall Mass Defence](https://naruto.fandom.com/wiki/Sand_Waterfall_Mass_Defence) | D | Defensive | `narutorpg:sand_waterfall_mass_defence` |
| [Sand Whip](https://naruto.fandom.com/wiki/Sand_Whip) | C | Offensive | `narutorpg:sand_whip` |
| [Sandstone Fist](https://naruto.fandom.com/wiki/Sandstone_Fist) | C | Offensive, Defensive | `narutorpg:sandstone_fist` |
| [Sandstorm Barrier](https://naruto.fandom.com/wiki/Sandstorm_Barrier) | C | Offensive, Supplementary | `narutorpg:sandstorm_barrier` |
| [Sandstorm Technique](https://naruto.fandom.com/wiki/Sandstorm_Technique) | C | Offensive, Supplementary | `narutorpg:sandstorm_technique` (existing) |
| [Sannin Mode](https://naruto.fandom.com/wiki/Sannin_Mode) | D | Supplementary | `narutorpg:sannin_mode` |
| [Sasuke Uchiha's Space–Time Dōjutsu](https://naruto.fandom.com/wiki/Sasuke_Uchiha's_Space–Time_Dōjutsu) | A | Supplementary | `narutorpg:sasuke_uchiha_s_spacetime_dojutsu` (existing) |
| [Scale Powder](https://naruto.fandom.com/wiki/Scale_Powder) | C | Supplementary | `narutorpg:scale_powder` |
| [Scale Powder Blasting](https://naruto.fandom.com/wiki/Scale_Powder_Blasting) | C | Offensive | `narutorpg:scale_powder_blasting` |
| [Scale Senbon](https://naruto.fandom.com/wiki/Scale_Senbon) | C | Offensive | `narutorpg:scale_senbon` |
| [Scale Wrapping Rotating Flight](https://naruto.fandom.com/wiki/Scale_Wrapping_Rotating_Flight) | C | Offensive | `narutorpg:scale_wrapping_rotating_flight` (existing) |
| [Scarlet Invitation](https://naruto.fandom.com/wiki/Scarlet_Invitation) | C | Offensive | `narutorpg:scarlet_invitation` (existing) |
| [Scattering Tailed Beast Balls](https://naruto.fandom.com/wiki/Scattering_Tailed_Beast_Balls) | C | Offensive | `narutorpg:scattering_tailed_beast_balls` |
| [Scattering Thousand Crows Technique](https://naruto.fandom.com/wiki/Scattering_Thousand_Crows_Technique) | C | Offensive, Supplementary | `narutorpg:scattering_thousand_crows_technique` |
| [Scent Blossom Colouring](https://naruto.fandom.com/wiki/Scent_Blossom_Colouring) | C | Offensive | `narutorpg:scent_blossom_colouring` (existing) |
| [Scientific Ninja Tool: Ever-Changing](https://naruto.fandom.com/wiki/Scientific_Ninja_Tool:_Ever-Changing) | C | Offensive | `narutorpg:scientific_ninja_tool_ever_changing` (existing) |
| [Scientific Ninja Tool: Orca and Powerful Breath](https://naruto.fandom.com/wiki/Scientific_Ninja_Tool:_Orca_and_Powerful_Breath) | C | Offensive | `narutorpg:scientific_ninja_tool_orca_and_powerful_breath` (existing) |
| [Scientific Ninja Tool: Purple Electricity](https://naruto.fandom.com/wiki/Scientific_Ninja_Tool:_Purple_Electricity) | C | Offensive | `narutorpg:scientific_ninja_tool_purple_electricity` (existing) |
| [Scorch Release](https://naruto.fandom.com/wiki/Scorch_Release) | B | - | `narutorpg:scorch_release` (existing) |
| [Scorch Release: Extremely Steaming Murder](https://naruto.fandom.com/wiki/Scorch_Release:_Extremely_Steaming_Murder) | B | Offensive | `narutorpg:scorch_release_extremely_steaming_murder` (existing) |
| [Scorch Release: Fireball Barrage](https://naruto.fandom.com/wiki/Scorch_Release:_Fireball_Barrage) | B | Offensive | `narutorpg:scorch_release_fireball_barrage` (existing) |
| [Scorch Release: Fireball Dance](https://naruto.fandom.com/wiki/Scorch_Release:_Fireball_Dance) | B | Offensive | `narutorpg:scorch_release_fireball_dance` (existing) |
| [Scorch Release: Great Steaming Explosive Blast](https://naruto.fandom.com/wiki/Scorch_Release:_Great_Steaming_Explosive_Blast) | B | Offensive | `narutorpg:scorch_release_great_steaming_explosive_blast` (existing) |
| [Scorch Release: Halo Hurricane Jet Black Arrow Style Zero](https://naruto.fandom.com/wiki/Scorch_Release:_Halo_Hurricane_Jet_Black_Arrow_Style_Zero) | S | Offensive | `narutorpg:scorch_release_halo_hurricane_jet_black_arrow_style_zero` (existing) |
| [Scorching Heat Sand Binding Coffin](https://naruto.fandom.com/wiki/Scorching_Heat_Sand_Binding_Coffin) | B | Offensive | `narutorpg:scorching_heat_sand_binding_coffin` (existing) |
| [Scorching River Great Eruption](https://naruto.fandom.com/wiki/Scorching_River_Great_Eruption) | B | Offensive | `narutorpg:scorching_river_great_eruption` (existing) |
| [Scroll Communication Technique](https://naruto.fandom.com/wiki/Scroll_Communication_Technique) | D | Supplementary | `narutorpg:scroll_communication_technique` |
| [Sea of Trees Erosion](https://naruto.fandom.com/wiki/Sea_of_Trees_Erosion) | B | Offensive, Defensive | `narutorpg:sea_of_trees_erosion` (existing) |
| [Sealed Binding Formation](https://naruto.fandom.com/wiki/Sealed_Binding_Formation) | C | Supplementary | `narutorpg:sealed_binding_formation` |
| [Sealed Iron Wall](https://naruto.fandom.com/wiki/Sealed_Iron_Wall) | B | Defensive | `narutorpg:sealed_iron_wall` |
| [Sealing Release Technique](https://naruto.fandom.com/wiki/Sealing_Release_Technique) | C | Supplementary | `narutorpg:sealing_release_technique` |
| [Sealing Tag Barrier](https://naruto.fandom.com/wiki/Sealing_Tag_Barrier) | C | Supplementary | `narutorpg:sealing_tag_barrier` |
| [Sealing Technique: Black Aura](https://naruto.fandom.com/wiki/Sealing_Technique:_Black_Aura) | C | Offensive | `narutorpg:sealing_technique_black_aura` (existing) |
| [Sealing Technique: Crimson Seal](https://naruto.fandom.com/wiki/Sealing_Technique:_Crimson_Seal) | C | Offensive, Supplementary | `narutorpg:sealing_technique_crimson_seal` (existing) |
| [Sealing Technique: Five Elements](https://naruto.fandom.com/wiki/Sealing_Technique:_Five_Elements) | C | Offensive, Supplementary | `narutorpg:sealing_technique_five_elements` (existing) |
| [Sealing Technique: Frog Suppression](https://naruto.fandom.com/wiki/Sealing_Technique:_Frog_Suppression) | C | Supplementary | `narutorpg:sealing_technique_frog_suppression` |
| [Sealing Technique: Lion Closing Roar](https://naruto.fandom.com/wiki/Sealing_Technique:_Lion_Closing_Roar) | A | Supplementary | `narutorpg:sealing_technique_lion_closing_roar` |
| [Sealing Technique: Octopus Hold](https://naruto.fandom.com/wiki/Sealing_Technique:_Octopus_Hold) | B | Supplementary | `narutorpg:sealing_technique_octopus_hold` |
| [Sealing Technique: Phantom Dragons Nine Consuming Seals](https://naruto.fandom.com/wiki/Sealing_Technique:_Phantom_Dragons_Nine_Consuming_Seals) | S | Supplementary | `narutorpg:sealing_technique_phantom_dragons_nine_consuming_seals` |
| [Sealing Technique: Release](https://naruto.fandom.com/wiki/Sealing_Technique:_Release) | C | Supplementary | `narutorpg:sealing_technique_release` |
| [Sealing Technique: Rōen Kodon](https://naruto.fandom.com/wiki/Sealing_Technique:_Rōen_Kodon) | C | Supplementary | `narutorpg:sealing_technique_roen_kodon` (existing) |
| [Sealing Technique: Three Directions Seal](https://naruto.fandom.com/wiki/Sealing_Technique:_Three_Directions_Seal) | C | Offensive | `narutorpg:sealing_technique_three_directions_seal` |
| [Sealing Technique: Tiger Vision Staring Bullet](https://naruto.fandom.com/wiki/Sealing_Technique:_Tiger_Vision_Staring_Bullet) | B | Supplementary | `narutorpg:sealing_technique_tiger_vision_staring_bullet` |
| [Secret Anaesthesia](https://naruto.fandom.com/wiki/Secret_Anaesthesia) | D | Supplementary | `narutorpg:secret_anaesthesia` |
| [Secret Sword: Flowing Shadow](https://naruto.fandom.com/wiki/Secret_Sword:_Flowing_Shadow) | C | Offensive | `narutorpg:secret_sword_flowing_shadow` |
| [Secret Sword: Snowstorm Beheading](https://naruto.fandom.com/wiki/Secret_Sword:_Snowstorm_Beheading) | C | Offensive, Defensive | `narutorpg:secret_sword_snowstorm_beheading` |
| [Secret Taijutsu: Super Fire Release Illusion Great Shuriken Two-Stage Technique](https://naruto.fandom.com/wiki/Secret_Taijutsu:_Super_Fire_Release_Illusion_Great_Shuriken_Two-Stage_Technique) | B | Offensive | `narutorpg:secret_taijutsu_super_fire_release_illusion_great_shuriken_two_stage_technique` (existing) |
| [Secret Taijutsu: Wild Stone Dance Flame Killing Kunai Endless Style Multi-Shot Technique](https://naruto.fandom.com/wiki/Secret_Taijutsu:_Wild_Stone_Dance_Flame_Killing_Kunai_Endless_Style_Multi-Shot_Technique) | B | Offensive | `narutorpg:secret_taijutsu_wild_stone_dance_flame_killing_kunai_endless_style_multi_shot_technique` (existing) |
| [Secret Technique Stone Needles](https://naruto.fandom.com/wiki/Secret_Technique_Stone_Needles) | C | Supplementary | `narutorpg:secret_technique_stone_needles` |
| [Secret Technique: Insect Arm](https://naruto.fandom.com/wiki/Secret_Technique:_Insect_Arm) | C | Offensive | `narutorpg:secret_technique_insect_arm` |
| [Secret Technique: Insect Bog](https://naruto.fandom.com/wiki/Secret_Technique:_Insect_Bog) | C | Offensive | `narutorpg:secret_technique_insect_bog` |
| [Secret Technique: Insect Cocoon](https://naruto.fandom.com/wiki/Secret_Technique:_Insect_Cocoon) | C | Supplementary | `narutorpg:secret_technique_insect_cocoon` (existing) |
| [Secret Technique: Insect Gathering](https://naruto.fandom.com/wiki/Secret_Technique:_Insect_Gathering) | C | Supplementary | `narutorpg:secret_technique_insect_gathering` (existing) |
| [Secret Technique: Insect Sphere](https://naruto.fandom.com/wiki/Secret_Technique:_Insect_Sphere) | C | Offensive | `narutorpg:secret_technique_insect_sphere` (existing) |
| [Secret Technique: Insect Tornado](https://naruto.fandom.com/wiki/Secret_Technique:_Insect_Tornado) | C | Offensive | `narutorpg:secret_technique_insect_tornado` (existing) |
| [Secret Technique: Insect Wall](https://naruto.fandom.com/wiki/Secret_Technique:_Insect_Wall) | C | Defensive | `narutorpg:secret_technique_insect_wall` (existing) |
| [Secret Technique: Mist Rain](https://naruto.fandom.com/wiki/Secret_Technique:_Mist_Rain) | C | Supplementary | `narutorpg:secret_technique_mist_rain` (existing) |
| [Secret Technique: Mouth Seal Technique](https://naruto.fandom.com/wiki/Secret_Technique:_Mouth_Seal_Technique) | C | Supplementary | `narutorpg:secret_technique_mouth_seal_technique` |
| [Secret Technique: Summoning: Mugen Castle](https://naruto.fandom.com/wiki/Secret_Technique:_Summoning:_Mugen_Castle) | A | Supplementary | `narutorpg:secret_technique_summoning_mugen_castle` |
| [Secret Technique: Tree Blood Barrier](https://naruto.fandom.com/wiki/Secret_Technique:_Tree_Blood_Barrier) | C | Supplementary | `narutorpg:secret_technique_tree_blood_barrier` |
| [Self-Cursing Seal](https://naruto.fandom.com/wiki/Self-Cursing_Seal) | C | Supplementary | `narutorpg:self_cursing_seal` (existing) |
| [Self-Healing Technique](https://naruto.fandom.com/wiki/Self-Healing_Technique) | D | Supplementary | `narutorpg:self_healing_technique` |
| [Self-Repairing Barrier](https://naruto.fandom.com/wiki/Self-Repairing_Barrier) | B | Supplementary | `narutorpg:self_repairing_barrier` |
| [Self-Seal Technique](https://naruto.fandom.com/wiki/Self-Seal_Technique) | S | Supplementary | `narutorpg:self_seal_technique` (existing) |
| [Senbon Shower](https://naruto.fandom.com/wiki/Senbon_Shower) | C | Offensive | `narutorpg:senbon_shower` (existing) |
| [Sensing Barrier](https://naruto.fandom.com/wiki/Sensing_Barrier) | C | Supplementary | `narutorpg:sensing_barrier` |
| [Sensing Barrier Pass Technique](https://naruto.fandom.com/wiki/Sensing_Barrier_Pass_Technique) | D | Supplementary | `narutorpg:sensing_barrier_pass_technique` |
| [Sensing Hexagram Seal](https://naruto.fandom.com/wiki/Sensing_Hexagram_Seal) | D | Supplementary | `narutorpg:sensing_hexagram_seal` |
| [Sensing Sand Sphere](https://naruto.fandom.com/wiki/Sensing_Sand_Sphere) | D | Supplementary | `narutorpg:sensing_sand_sphere` |
| [Sensing Technique](https://naruto.fandom.com/wiki/Sensing_Technique) | D | Supplementary | `narutorpg:sensing_technique` |
| [Sensing Transmission](https://naruto.fandom.com/wiki/Sensing_Transmission) | C | Supplementary | `narutorpg:sensing_transmission` (existing) |
| [Sensing Water Sphere](https://naruto.fandom.com/wiki/Sensing_Water_Sphere) | D | Supplementary | `narutorpg:sensing_water_sphere` (existing) |
| [Seven Lightnings of the Heavenly Realm](https://naruto.fandom.com/wiki/Seven_Lightnings_of_the_Heavenly_Realm) | B | Offensive | `narutorpg:seven_lightnings_of_the_heavenly_realm` (existing) |
| [Seven Swords of the Bloody Mist](https://naruto.fandom.com/wiki/Seven_Swords_of_the_Bloody_Mist) | B | Offensive | `narutorpg:seven_swords_of_the_bloody_mist` (existing) |
| [Seven-Coloured Rasengan](https://naruto.fandom.com/wiki/Seven-Coloured_Rasengan) | C | Offensive | `narutorpg:seven_coloured_rasengan` |
| [Sexy Reverse Harem Technique](https://naruto.fandom.com/wiki/Sexy_Reverse_Harem_Technique) | D | Supplementary | `narutorpg:sexy_reverse_harem_technique` |
| [Sexy Technique](https://naruto.fandom.com/wiki/Sexy_Technique) | E | Supplementary | `narutorpg:sexy_technique` |
| [Sexy Technique: Pole Dance and Nice Body](https://naruto.fandom.com/wiki/Sexy_Technique:_Pole_Dance_and_Nice_Body) | D | Supplementary | `narutorpg:sexy_technique_pole_dance_and_nice_body` |
| [Sexy: Boy on Boy Technique](https://naruto.fandom.com/wiki/Sexy:_Boy_on_Boy_Technique) | D | Supplementary | `narutorpg:sexy_boy_on_boy_technique` |
| [Sexy: Girl on Girl Technique](https://naruto.fandom.com/wiki/Sexy:_Girl_on_Girl_Technique) | D | Supplementary | `narutorpg:sexy_girl_on_girl_technique` |
| [Shadow Arms](https://naruto.fandom.com/wiki/Shadow_Arms) | C | Offensive, Supplementary | `narutorpg:shadow_arms` |
| [Shadow Bind Combo](https://naruto.fandom.com/wiki/Shadow_Bind_Combo) | B | Offensive | `narutorpg:shadow_bind_combo` (existing) |
| [Shadow Bind Technique: Check](https://naruto.fandom.com/wiki/Shadow_Bind_Technique:_Check) | A | Offensive | `narutorpg:shadow_bind_technique_check` (existing) |
| [Shadow Bind: Crush](https://naruto.fandom.com/wiki/Shadow_Bind:_Crush) | C | Offensive | `narutorpg:shadow_bind_crush` (existing) |
| [Shadow Cage Technique](https://naruto.fandom.com/wiki/Shadow_Cage_Technique) | C | Supplementary | `narutorpg:shadow_cage_technique` (existing) |
| [Shadow Clone Charging Bullet](https://naruto.fandom.com/wiki/Shadow_Clone_Charging_Bullet) | C | Offensive | `narutorpg:shadow_clone_charging_bullet` |
| [Shadow Clone Elite Bullet](https://naruto.fandom.com/wiki/Shadow_Clone_Elite_Bullet) | C | Offensive | `narutorpg:shadow_clone_elite_bullet` (existing) |
| [Shadow Clone Kick](https://naruto.fandom.com/wiki/Shadow_Clone_Kick) | C | Offensive | `narutorpg:shadow_clone_kick` (existing) |
| [Shadow Clone Roundhouse Kick](https://naruto.fandom.com/wiki/Shadow_Clone_Roundhouse_Kick) | C | Offensive | `narutorpg:shadow_clone_roundhouse_kick` (existing) |
| [Shadow Clone Technique](https://naruto.fandom.com/wiki/Shadow_Clone_Technique) | B | Supplementary | `narutorpg:shadow_clone_technique` (existing) |
| [Shadow Clone: Great Rotation](https://naruto.fandom.com/wiki/Shadow_Clone:_Great_Rotation) | C | Offensive | `narutorpg:shadow_clone_great_rotation` |
| [Shadow Clone: Super Tailed Beast Rasenshuriken](https://naruto.fandom.com/wiki/Shadow_Clone:_Super_Tailed_Beast_Rasenshuriken) | A | Offensive, Supplementary | `narutorpg:shadow_clone_super_tailed_beast_rasenshuriken` (existing) |
| [Shadow Clone: Twin Lightning Cutter](https://naruto.fandom.com/wiki/Shadow_Clone:_Twin_Lightning_Cutter) | C | Offensive | `narutorpg:shadow_clone_twin_lightning_cutter` (existing) |
| [Shadow Clone: Upper](https://naruto.fandom.com/wiki/Shadow_Clone:_Upper) | C | Offensive | `narutorpg:shadow_clone_upper` (existing) |
| [Shadow Clutch Technique](https://naruto.fandom.com/wiki/Shadow_Clutch_Technique) | A | Supplementary | `narutorpg:shadow_clutch_technique` (existing) |
| [Shadow Demon Binding Technique](https://naruto.fandom.com/wiki/Shadow_Demon_Binding_Technique) | C | Offensive | `narutorpg:shadow_demon_binding_technique` (existing) |
| [Shadow Gathering Technique](https://naruto.fandom.com/wiki/Shadow_Gathering_Technique) | C | Supplementary | `narutorpg:shadow_gathering_technique` (existing) |
| [Shadow Hell Technique](https://naruto.fandom.com/wiki/Shadow_Hell_Technique) | C | Offensive | `narutorpg:shadow_hell_technique` (existing) |
| [Shadow Human Bullet Tank](https://naruto.fandom.com/wiki/Shadow_Human_Bullet_Tank) | B | Offensive | `narutorpg:shadow_human_bullet_tank` (existing) |
| [Shadow Imitation Field Technique](https://naruto.fandom.com/wiki/Shadow_Imitation_Field_Technique) | C | Supplementary | `narutorpg:shadow_imitation_field_technique` (existing) |
| [Shadow Imitation Shadow Bind Technique](https://naruto.fandom.com/wiki/Shadow_Imitation_Shadow_Bind_Technique) | C | Supplementary | `narutorpg:shadow_imitation_shadow_bind_technique` (existing) |
| [Shadow Imitation Shuriken Technique](https://naruto.fandom.com/wiki/Shadow_Imitation_Shuriken_Technique) | C | Supplementary | `narutorpg:shadow_imitation_shuriken_technique` (existing) |
| [Shadow Imitation Technique](https://naruto.fandom.com/wiki/Shadow_Imitation_Technique) | C | Supplementary | `narutorpg:shadow_imitation_technique` (existing) |
| [Shadow Imitation Technique: Ryūō](https://naruto.fandom.com/wiki/Shadow_Imitation_Technique:_Ryūō) | C | Offensive | `narutorpg:shadow_imitation_technique_ryuo` (existing) |
| [Shadow Mirror Body Changing Method](https://naruto.fandom.com/wiki/Shadow_Mirror_Body_Changing_Method) | C | Supplementary | `narutorpg:shadow_mirror_body_changing_method` |
| [Shadow Network Technique](https://naruto.fandom.com/wiki/Shadow_Network_Technique) | C | Offensive, Defensive | `narutorpg:shadow_network_technique` (existing) |
| [Shadow Pressure Palm Technique](https://naruto.fandom.com/wiki/Shadow_Pressure_Palm_Technique) | C | Offensive | `narutorpg:shadow_pressure_palm_technique` (existing) |
| [Shadow Rope Technique](https://naruto.fandom.com/wiki/Shadow_Rope_Technique) | B | Supplementary | `narutorpg:shadow_rope_technique` |
| [Shadow Senbon](https://naruto.fandom.com/wiki/Shadow_Senbon) | C | Offensive | `narutorpg:shadow_senbon` |
| [Shadow Sewing Technique](https://naruto.fandom.com/wiki/Shadow_Sewing_Technique) | C | Offensive, Supplementary | `narutorpg:shadow_sewing_technique` (existing) |
| [Shadow Sewing Technique: Piece Set](https://naruto.fandom.com/wiki/Shadow_Sewing_Technique:_Piece_Set) | C | Offensive | `narutorpg:shadow_sewing_technique_piece_set` (existing) |
| [Shadow Sewing Technique: Round Shadow](https://naruto.fandom.com/wiki/Shadow_Sewing_Technique:_Round_Shadow) | C | Offensive | `narutorpg:shadow_sewing_technique_round_shadow` (existing) |
| [Shadow Sheep Cloud Technique](https://naruto.fandom.com/wiki/Shadow_Sheep_Cloud_Technique) | C | Supplementary | `narutorpg:shadow_sheep_cloud_technique` (existing) |
| [Shadow Shuriken Technique](https://naruto.fandom.com/wiki/Shadow_Shuriken_Technique) | D | Offensive | `narutorpg:shadow_shuriken_technique` (existing) |
| [Shadow Smokescreen Technique](https://naruto.fandom.com/wiki/Shadow_Smokescreen_Technique) | C | Defensive | `narutorpg:shadow_smokescreen_technique` (existing) |
| [Shadow Stabbing Technique](https://naruto.fandom.com/wiki/Shadow_Stabbing_Technique) | C | Offensive | `narutorpg:shadow_stabbing_technique` (existing) |
| [Shadow Stabbing Technique: Narigin](https://naruto.fandom.com/wiki/Shadow_Stabbing_Technique:_Narigin) | C | Offensive | `narutorpg:shadow_stabbing_technique_narigin` (existing) |
| [Shadow Sword](https://naruto.fandom.com/wiki/Shadow_Sword) | C | Offensive | `narutorpg:shadow_sword` |
| [Shadow–Neck Binding Technique](https://naruto.fandom.com/wiki/Shadow–Neck_Binding_Technique) | C | Offensive | `narutorpg:shadowneck_binding_technique` (existing) |
| [Shapeshifting Technique](https://naruto.fandom.com/wiki/Shapeshifting_Technique) | D | Supplementary | `narutorpg:shapeshifting_technique` |
| [Sharp Hair Spear: Might](https://naruto.fandom.com/wiki/Sharp_Hair_Spear:_Might) | C | Offensive | `narutorpg:sharp_hair_spear_might` |
| [Shield of Sand](https://naruto.fandom.com/wiki/Shield_of_Sand) | D | Defensive | `narutorpg:shield_of_sand` |
| [Shifting Body Technique](https://naruto.fandom.com/wiki/Shifting_Body_Technique) | D | Supplementary | `narutorpg:shifting_body_technique` |
| [Shikotsumyaku](https://naruto.fandom.com/wiki/Shikotsumyaku) | B | - | `narutorpg:shikotsumyaku` (existing) |
| [Shin Uchiha's Space–Time Dōjutsu](https://naruto.fandom.com/wiki/Shin_Uchiha's_Space–Time_Dōjutsu) | A | Defensive, Supplementary | `narutorpg:shin_uchiha_s_spacetime_dojutsu` (existing) |
| [Shinra Tensei](https://naruto.fandom.com/wiki/Shinra_Tensei) | B | Offensive, Defensive | `narutorpg:shinra_tensei` (existing) |
| [Short Range Mind's Eye of the Kagura](https://naruto.fandom.com/wiki/Short_Range_Mind's_Eye_of_the_Kagura) | C | Supplementary | `narutorpg:short_range_mind_s_eye_of_the_kagura` |
| [Shrike](https://naruto.fandom.com/wiki/Shrike) | C | Offensive | `narutorpg:shrike` (existing) |
| [Shuriken Shadow Clone Technique](https://naruto.fandom.com/wiki/Shuriken_Shadow_Clone_Technique) | A | Offensive | `narutorpg:shuriken_shadow_clone_technique` (existing) |
| [Shuriken: Double Knockdown Technique](https://naruto.fandom.com/wiki/Shuriken:_Double_Knockdown_Technique) | C | Offensive | `narutorpg:shuriken_double_knockdown_technique` (existing) |
| [Sickle Mist Technique](https://naruto.fandom.com/wiki/Sickle_Mist_Technique) | A | Offensive | `narutorpg:sickle_mist_technique` |
| [Sickle Weasel Explosion](https://naruto.fandom.com/wiki/Sickle_Weasel_Explosion) | B | Offensive | `narutorpg:sickle_weasel_explosion` (existing) |
| [Sickle Weasel Technique](https://naruto.fandom.com/wiki/Sickle_Weasel_Technique) | C | Offensive | `narutorpg:sickle_weasel_technique` (existing) |
| [Silver Wheel Reincarnation Explosion](https://naruto.fandom.com/wiki/Silver_Wheel_Reincarnation_Explosion) | B | Offensive | `narutorpg:silver_wheel_reincarnation_explosion` (existing) |
| [Six Paths Chakra: Rebirth](https://naruto.fandom.com/wiki/Six_Paths_Chakra:_Rebirth) | D | Supplementary | `narutorpg:six_paths_chakra_rebirth` (existing) |
| [Six Paths Sage Mode](https://naruto.fandom.com/wiki/Six_Paths_Sage_Mode) | A | Offensive, Defensive, Supplementary | `narutorpg:six_paths_sage_mode` |
| [Six Paths Technique](https://naruto.fandom.com/wiki/Six_Paths_Technique) | B | Offensive, Defensive, Supplementary | `narutorpg:six_paths_technique` (existing) |
| [Six Paths Ten-Tails Coffin Seal](https://naruto.fandom.com/wiki/Six_Paths_Ten-Tails_Coffin_Seal) | B | Supplementary | `narutorpg:six_paths_ten_tails_coffin_seal` (existing) |
| [Six Paths Yang Power](https://naruto.fandom.com/wiki/Six_Paths_Yang_Power) | D | Supplementary | `narutorpg:six_paths_yang_power` (existing) |
| [Six Paths Yin Power](https://naruto.fandom.com/wiki/Six_Paths_Yin_Power) | D | Supplementary | `narutorpg:six_paths_yin_power` (existing) |
| [Six Paths of Pain](https://naruto.fandom.com/wiki/Six_Paths_of_Pain) | B | Offensive, Defensive, Supplementary | `narutorpg:six_paths_of_pain` (existing) |
| [Six Paths — Chibaku Tensei](https://naruto.fandom.com/wiki/Six_Paths_—_Chibaku_Tensei) | B | Offensive, Supplementary | `narutorpg:six_paths_chibaku_tensei` (existing) |
| [Six Paths: Cross Lantern](https://naruto.fandom.com/wiki/Six_Paths:_Cross_Lantern) | A | Offensive, Defensive, Supplementary | `narutorpg:six_paths_cross_lantern` (existing) |
| [Six Paths: Five Balls](https://naruto.fandom.com/wiki/Six_Paths:_Five_Balls) | A | Offensive | `narutorpg:six_paths_five_balls` (existing) |
| [Six Paths: Flame Extinction Hand](https://naruto.fandom.com/wiki/Six_Paths:_Flame_Extinction_Hand) | C | Offensive | `narutorpg:six_paths_flame_extinction_hand` |
| [Six Paths: Kunitsukami](https://naruto.fandom.com/wiki/Six_Paths:_Kunitsukami) | A | Offensive, Defensive, Supplementary | `narutorpg:six_paths_kunitsukami` |
| [Six Paths: Shinra Tenkyō](https://naruto.fandom.com/wiki/Six_Paths:_Shinra_Tenkyō) | S | Offensive | `narutorpg:six_paths_shinra_tenkyo` (existing) |
| [Six Paths: Super Tailed Beast Rasenshuriken](https://naruto.fandom.com/wiki/Six_Paths:_Super_Tailed_Beast_Rasenshuriken) | S | Offensive, Supplementary | `narutorpg:six_paths_super_tailed_beast_rasenshuriken` (existing) |
| [Six Paths: Super Truth-Seeking Serial Balls](https://naruto.fandom.com/wiki/Six_Paths:_Super_Truth-Seeking_Serial_Balls) | A | - | `narutorpg:six_paths_super_truth_seeking_serial_balls` (existing) |
| [Six Paths: True Several Rasenshuriken](https://naruto.fandom.com/wiki/Six_Paths:_True_Several_Rasenshuriken) | A | Offensive | `narutorpg:six_paths_true_several_rasenshuriken` (existing) |
| [Six Paths: Ultra-Big Ball Rasenshuriken](https://naruto.fandom.com/wiki/Six_Paths:_Ultra-Big_Ball_Rasenshuriken) | S | Offensive | `narutorpg:six_paths_ultra_big_ball_rasenshuriken` (existing) |
| [Six Red Yang Formation](https://naruto.fandom.com/wiki/Six_Red_Yang_Formation) | B | Defensive, Supplementary | `narutorpg:six_red_yang_formation` (existing) |
| [Six Red Yang Formation: Blazing Annihilation](https://naruto.fandom.com/wiki/Six_Red_Yang_Formation:_Blazing_Annihilation) | B | Offensive | `narutorpg:six_red_yang_formation_blazing_annihilation` (existing) |
| [Sky Spiralling Drop](https://naruto.fandom.com/wiki/Sky_Spiralling_Drop) | B | Offensive | `narutorpg:sky_spiralling_drop` |
| [Sky-Slashing Wave](https://naruto.fandom.com/wiki/Sky-Slashing_Wave) | C | Offensive | `narutorpg:sky_slashing_wave` |
| [Slashing Bandages](https://naruto.fandom.com/wiki/Slashing_Bandages) | C | Offensive | `narutorpg:slashing_bandages` |
| [Slashing Shadow](https://naruto.fandom.com/wiki/Slashing_Shadow) | B | Offensive | `narutorpg:slashing_shadow` |
| [Sleeping Snake Blade Slash](https://naruto.fandom.com/wiki/Sleeping_Snake_Blade_Slash) | A | Offensive | `narutorpg:sleeping_snake_blade_slash` |
| [Slicing Crimson Wave](https://naruto.fandom.com/wiki/Slicing_Crimson_Wave) | C | Offensive | `narutorpg:slicing_crimson_wave` |
| [Slithering Snake Mode](https://naruto.fandom.com/wiki/Slithering_Snake_Mode) | D | Supplementary | `narutorpg:slithering_snake_mode` |
| [Slug Crusher](https://naruto.fandom.com/wiki/Slug_Crusher) | A | Offensive | `narutorpg:slug_crusher` |
| [Slug Great Division](https://naruto.fandom.com/wiki/Slug_Great_Division) | D | Supplementary | `narutorpg:slug_great_division` |
| [Small Package](https://naruto.fandom.com/wiki/Small_Package) | C | Offensive | `narutorpg:small_package` |
| [Smell Sphere](https://naruto.fandom.com/wiki/Smell_Sphere) | C | Offensive, Supplementary | `narutorpg:smell_sphere` |
| [Smoke Clone](https://naruto.fandom.com/wiki/Smoke_Clone) | D | Supplementary | `narutorpg:smoke_clone` |
| [Smoke Dragon](https://naruto.fandom.com/wiki/Smoke_Dragon) | D | Supplementary | `narutorpg:smoke_dragon` |
| [Snake (jutsu)](https://naruto.fandom.com/wiki/Snake_(jutsu)) | A | Offensive | `narutorpg:snake_jutsu` (existing) |
| [Snake Charmer Technique](https://naruto.fandom.com/wiki/Snake_Charmer_Technique) | C | Offensive | `narutorpg:snake_charmer_technique` |
| [Snake Clone Technique](https://naruto.fandom.com/wiki/Snake_Clone_Technique) | D | Supplementary | `narutorpg:snake_clone_technique` |
| [Snake Mouth Bind](https://naruto.fandom.com/wiki/Snake_Mouth_Bind) | A | Offensive | `narutorpg:snake_mouth_bind` |
| [Snake Mouth Poison Mist Technique](https://naruto.fandom.com/wiki/Snake_Mouth_Poison_Mist_Technique) | C | Offensive | `narutorpg:snake_mouth_poison_mist_technique` |
| [Snake Reconnection Technique](https://naruto.fandom.com/wiki/Snake_Reconnection_Technique) | D | Supplementary | `narutorpg:snake_reconnection_technique` |
| [Snake Skin Sword Stroke](https://naruto.fandom.com/wiki/Snake_Skin_Sword_Stroke) | C | Offensive | `narutorpg:snake_skin_sword_stroke` |
| [Snake Spying Technique](https://naruto.fandom.com/wiki/Snake_Spying_Technique) | D | Supplementary | `narutorpg:snake_spying_technique` |
| [Snake Steel Net](https://naruto.fandom.com/wiki/Snake_Steel_Net) | C | Offensive, Supplementary | `narutorpg:snake_steel_net` |
| [Snake Wild Strike](https://naruto.fandom.com/wiki/Snake_Wild_Strike) | C | Offensive | `narutorpg:snake_wild_strike` (existing) |
| [Soap Bubble Ninjutsu](https://naruto.fandom.com/wiki/Soap_Bubble_Ninjutsu) | C | Offensive, Defensive, Supplementary | `narutorpg:soap_bubble_ninjutsu` (existing) |
| [Soap Bubble Slime](https://naruto.fandom.com/wiki/Soap_Bubble_Slime) | D | Supplementary | `narutorpg:soap_bubble_slime` (existing) |
| [Soaring Shine Falling Bullet](https://naruto.fandom.com/wiki/Soaring_Shine_Falling_Bullet) | C | Offensive | `narutorpg:soaring_shine_falling_bullet` (existing) |
| [Soaring Strings](https://naruto.fandom.com/wiki/Soaring_Strings) | C | Offensive | `narutorpg:soaring_strings` |
| [Soft Neck Throw](https://naruto.fandom.com/wiki/Soft_Neck_Throw) | C | Offensive | `narutorpg:soft_neck_throw` (existing) |
| [Soft Physique Modification](https://naruto.fandom.com/wiki/Soft_Physique_Modification) | C | Supplementary | `narutorpg:soft_physique_modification` |
| [Sonic Blast](https://naruto.fandom.com/wiki/Sonic_Blast) | C | Offensive | `narutorpg:sonic_blast` (existing) |
| [Sonic Shock](https://naruto.fandom.com/wiki/Sonic_Shock) | C | Offensive | `narutorpg:sonic_shock` |
| [Soul–Body Separation Technique](https://naruto.fandom.com/wiki/Soul–Body_Separation_Technique) | C | Supplementary | `narutorpg:soulbody_separation_technique` |
| [Sound Four: Summoning: Four Beasts Encirclement Formation](https://naruto.fandom.com/wiki/Sound_Four:_Summoning:_Four_Beasts_Encirclement_Formation) | A | Offensive, Defensive, Supplementary | `narutorpg:sound_four_summoning_four_beasts_encirclement_formation` |
| [Sound Wave Technique](https://naruto.fandom.com/wiki/Sound_Wave_Technique) | C | Offensive | `narutorpg:sound_wave_technique` |
| [Space–Time Ninjutsu: Godspeed](https://naruto.fandom.com/wiki/Space–Time_Ninjutsu:_Godspeed) | A | Offensive | `narutorpg:spacetime_ninjutsu_godspeed` |
| [Space–Time Technique Formula: Underworld Turnover](https://naruto.fandom.com/wiki/Space–Time_Technique_Formula:_Underworld_Turnover) | S | Supplementary | `narutorpg:spacetime_technique_formula_underworld_turnover` (existing) |
| [Space–Time: Spiralling Serial Spheres](https://naruto.fandom.com/wiki/Space–Time:_Spiralling_Serial_Spheres) | A | Offensive | `narutorpg:spacetime_spiralling_serial_spheres` |
| [Spear of Heaven Nine-Tails Beam](https://naruto.fandom.com/wiki/Spear_of_Heaven_Nine-Tails_Beam) | C | Offensive | `narutorpg:spear_of_heaven_nine_tails_beam` |
| [Special Attack Rasengan](https://naruto.fandom.com/wiki/Special_Attack_Rasengan) | C | Offensive, Defensive | `narutorpg:special_attack_rasengan` (existing) |
| [Spider Bind](https://naruto.fandom.com/wiki/Spider_Bind) | C | Supplementary | `narutorpg:spider_bind` |
| [Spider Cocoon](https://naruto.fandom.com/wiki/Spider_Cocoon) | C | Supplementary | `narutorpg:spider_cocoon` |
| [Spider Drop](https://naruto.fandom.com/wiki/Spider_Drop) | C | Supplementary | `narutorpg:spider_drop` |
| [Spider Scatter Technique](https://naruto.fandom.com/wiki/Spider_Scatter_Technique) | A | Offensive | `narutorpg:spider_scatter_technique` |
| [Spider Sticking Spit](https://naruto.fandom.com/wiki/Spider_Sticking_Spit) | C | Supplementary | `narutorpg:spider_sticking_spit` |
| [Spider Sticky Gold](https://naruto.fandom.com/wiki/Spider_Sticky_Gold) | C | Supplementary | `narutorpg:spider_sticky_gold` |
| [Spider War Bow: Terrible Split](https://naruto.fandom.com/wiki/Spider_War_Bow:_Terrible_Split) | C | Offensive | `narutorpg:spider_war_bow_terrible_split` |
| [Spider Web Area](https://naruto.fandom.com/wiki/Spider_Web_Area) | C | Supplementary | `narutorpg:spider_web_area` |
| [Spider Web Flower](https://naruto.fandom.com/wiki/Spider_Web_Flower) | C | Supplementary | `narutorpg:spider_web_flower` |
| [Spider Web Unrolling](https://naruto.fandom.com/wiki/Spider_Web_Unrolling) | C | Supplementary | `narutorpg:spider_web_unrolling` |
| [Spindle Formation](https://naruto.fandom.com/wiki/Spindle_Formation) | C | Offensive | `narutorpg:spindle_formation` (existing) |
| [Spinning Snake Thorn](https://naruto.fandom.com/wiki/Spinning_Snake_Thorn) | D | - | `narutorpg:spinning_snake_thorn` |
| [Spinning Top Swing](https://naruto.fandom.com/wiki/Spinning_Top_Swing) | C | Offensive | `narutorpg:spinning_top_swing` (existing) |
| [Spiral Boruto Storm](https://naruto.fandom.com/wiki/Spiral_Boruto_Storm) | C | Offensive | `narutorpg:spiral_boruto_storm` (existing) |
| [Spiralling Absorption Sphere](https://naruto.fandom.com/wiki/Spiralling_Absorption_Sphere) | A | Offensive | `narutorpg:spiralling_absorption_sphere` |
| [Spiralling Bullet](https://naruto.fandom.com/wiki/Spiralling_Bullet) | C | Offensive | `narutorpg:spiralling_bullet` |
| [Spiralling Combo](https://naruto.fandom.com/wiki/Spiralling_Combo) | C | Offensive | `narutorpg:spiralling_combo` |
| [Spiralling Flash Super Round Dance Howl Style Three](https://naruto.fandom.com/wiki/Spiralling_Flash_Super_Round_Dance_Howl_Style_Three) | A | Offensive | `narutorpg:spiralling_flash_super_round_dance_howl_style_three` |
| [Spiralling Great Fire Sphere](https://naruto.fandom.com/wiki/Spiralling_Great_Fire_Sphere) | B | Offensive | `narutorpg:spiralling_great_fire_sphere` (existing) |
| [Spiralling Ring](https://naruto.fandom.com/wiki/Spiralling_Ring) | C | Offensive | `narutorpg:spiralling_ring` |
| [Spiralling Serial Spheres](https://naruto.fandom.com/wiki/Spiralling_Serial_Spheres) | A | Offensive | `narutorpg:spiralling_serial_spheres` |
| [Spiralling Strife Spheres](https://naruto.fandom.com/wiki/Spiralling_Strife_Spheres) | A | Offensive | `narutorpg:spiralling_strife_spheres` |
| [Spiralling Tower Technique](https://naruto.fandom.com/wiki/Spiralling_Tower_Technique) | B | Supplementary | `narutorpg:spiralling_tower_technique` (existing) |
| [Spirit Reflection Technique](https://naruto.fandom.com/wiki/Spirit_Reflection_Technique) | S | - | `narutorpg:spirit_reflection_technique` (existing) |
| [Spirit Transformation Soul Killing Technique](https://naruto.fandom.com/wiki/Spirit_Transformation_Soul_Killing_Technique) | C | Offensive | `narutorpg:spirit_transformation_soul_killing_technique` |
| [Spirit Transformation Technique](https://naruto.fandom.com/wiki/Spirit_Transformation_Technique) | S | Offensive, Supplementary | `narutorpg:spirit_transformation_technique` |
| [Spontaneous Tree Summoning](https://naruto.fandom.com/wiki/Spontaneous_Tree_Summoning) | B | Defensive | `narutorpg:spontaneous_tree_summoning` (existing) |
| [Spore Technique](https://naruto.fandom.com/wiki/Spore_Technique) | D | Supplementary | `narutorpg:spore_technique` |
| [Stagnate Snake Confinement](https://naruto.fandom.com/wiki/Stagnate_Snake_Confinement) | C | Offensive | `narutorpg:stagnate_snake_confinement` |
| [Stake Chains](https://naruto.fandom.com/wiki/Stake_Chains) | A | Offensive | `narutorpg:stake_chains` (existing) |
| [Starch Syrup Binding Rope](https://naruto.fandom.com/wiki/Starch_Syrup_Binding_Rope) | D | Supplementary | `narutorpg:starch_syrup_binding_rope` (existing) |
| [Starch Syrup Gun](https://naruto.fandom.com/wiki/Starch_Syrup_Gun) | A | Offensive | `narutorpg:starch_syrup_gun` (existing) |
| [Stealth Snake Hand](https://naruto.fandom.com/wiki/Stealth_Snake_Hand) | A | Offensive | `narutorpg:stealth_snake_hand` (existing) |
| [Steam Dash](https://naruto.fandom.com/wiki/Steam_Dash) | B | Offensive | `narutorpg:steam_dash` (existing) |
| [Steaming Danger Tyranny](https://naruto.fandom.com/wiki/Steaming_Danger_Tyranny) | C | Offensive | `narutorpg:steaming_danger_tyranny` (existing) |
| [Steaming Danger Tyranny: Sleeve Cutter](https://naruto.fandom.com/wiki/Steaming_Danger_Tyranny:_Sleeve_Cutter) | C | Offensive | `narutorpg:steaming_danger_tyranny_sleeve_cutter` (existing) |
| [Steaming Danger Tyranny: Steaming Danger Explosion](https://naruto.fandom.com/wiki/Steaming_Danger_Tyranny:_Steaming_Danger_Explosion) | C | Offensive | `narutorpg:steaming_danger_tyranny_steaming_danger_explosion` (existing) |
| [Steel Blades Technique](https://naruto.fandom.com/wiki/Steel_Blades_Technique) | B | Offensive | `narutorpg:steel_blades_technique` (existing) |
| [Steel Release](https://naruto.fandom.com/wiki/Steel_Release) | B | - | `narutorpg:steel_release` (existing) |
| [Steel Release: Impervious Armour](https://naruto.fandom.com/wiki/Steel_Release:_Impervious_Armour) | B | Defensive | `narutorpg:steel_release_impervious_armour` (existing) |
| [Steel Release: Steel Projectile](https://naruto.fandom.com/wiki/Steel_Release:_Steel_Projectile) | B | Offensive | `narutorpg:steel_release_steel_projectile` (existing) |
| [Steel Release: Steel Shield Technique](https://naruto.fandom.com/wiki/Steel_Release:_Steel_Shield_Technique) | B | Defensive | `narutorpg:steel_release_steel_shield_technique` (existing) |
| [Storm Release](https://naruto.fandom.com/wiki/Storm_Release) | B | - | `narutorpg:storm_release` (existing) |
| [Storm Release Secret Technique: Demon Dragon Storm](https://naruto.fandom.com/wiki/Storm_Release_Secret_Technique:_Demon_Dragon_Storm) | B | Offensive | `narutorpg:storm_release_secret_technique_demon_dragon_storm` (existing) |
| [Storm Release: Azure Eagle Blade](https://naruto.fandom.com/wiki/Storm_Release:_Azure_Eagle_Blade) | B | Offensive | `narutorpg:storm_release_azure_eagle_blade` (existing) |
| [Storm Release: Black Hunting](https://naruto.fandom.com/wiki/Storm_Release:_Black_Hunting) | B | Offensive | `narutorpg:storm_release_black_hunting` (existing) |
| [Storm Release: Laser Circus](https://naruto.fandom.com/wiki/Storm_Release:_Laser_Circus) | B | Offensive | `narutorpg:storm_release_laser_circus` (existing) |
| [Storm Release: Thunder Cloud Inner Wave](https://naruto.fandom.com/wiki/Storm_Release:_Thunder_Cloud_Inner_Wave) | B | Offensive, Defensive | `narutorpg:storm_release_thunder_cloud_inner_wave` (existing) |
| [Strange Mask Exploding Flame](https://naruto.fandom.com/wiki/Strange_Mask_Exploding_Flame) | S | Offensive | `narutorpg:strange_mask_exploding_flame` (existing) |
| [Strange Transmission Distant Shadow](https://naruto.fandom.com/wiki/Strange_Transmission_Distant_Shadow) | A | Offensive, Supplementary | `narutorpg:strange_transmission_distant_shadow` |
| [Strengthening Prescription: Chakra Injection](https://naruto.fandom.com/wiki/Strengthening_Prescription:_Chakra_Injection) | D | Supplementary | `narutorpg:strengthening_prescription_chakra_injection` |
| [String Light Formation](https://naruto.fandom.com/wiki/String_Light_Formation) | C | Supplementary | `narutorpg:string_light_formation` (existing) |
| [Strong Spear Tree](https://naruto.fandom.com/wiki/Strong_Spear_Tree) | B | Offensive | `narutorpg:strong_spear_tree` (existing) |
| [Strong Sword: Helmet Breaking](https://naruto.fandom.com/wiki/Strong_Sword:_Helmet_Breaking) | C | Offensive | `narutorpg:strong_sword_helmet_breaking` |
| [Student and Teacher: Rasengan](https://naruto.fandom.com/wiki/Student_and_Teacher:_Rasengan) | B | Offensive | `narutorpg:student_and_teacher_rasengan` |
| [Substitute Technique](https://naruto.fandom.com/wiki/Substitute_Technique) | D | Supplementary | `narutorpg:substitute_technique` |
| [Successive Shots Iron Sand Drizzle](https://naruto.fandom.com/wiki/Successive_Shots_Iron_Sand_Drizzle) | B | Offensive | `narutorpg:successive_shots_iron_sand_drizzle` (existing) |
| [Successive Shots Sand Drizzle](https://naruto.fandom.com/wiki/Successive_Shots_Sand_Drizzle) | C | Offensive | `narutorpg:successive_shots_sand_drizzle` |
| [Suicide Bombing Clone](https://naruto.fandom.com/wiki/Suicide_Bombing_Clone) | B | Offensive, Supplementary | `narutorpg:suicide_bombing_clone` (existing) |
| [Summoning Rinnegan](https://naruto.fandom.com/wiki/Summoning_Rinnegan) | A | Supplementary | `narutorpg:summoning_rinnegan` (existing) |
| [Summoning Technique](https://naruto.fandom.com/wiki/Summoning_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:summoning_technique` (existing) |
| [Summoning Technique — Fall](https://naruto.fandom.com/wiki/Summoning_Technique_—_Fall) | B | Offensive | `narutorpg:summoning_technique_fall` |
| [Summoning: Blue Snake Ring Prison](https://naruto.fandom.com/wiki/Summoning:_Blue_Snake_Ring_Prison) | A | Offensive, Defensive | `narutorpg:summoning_blue_snake_ring_prison` |
| [Summoning: Crushing Toad Stomach](https://naruto.fandom.com/wiki/Summoning:_Crushing_Toad_Stomach) | A | Offensive | `narutorpg:summoning_crushing_toad_stomach` |
| [Summoning: Demonic Statue of the Outer Path](https://naruto.fandom.com/wiki/Summoning:_Demonic_Statue_of_the_Outer_Path) | A | Offensive, Supplementary | `narutorpg:summoning_demonic_statue_of_the_outer_path` (existing) |
| [Summoning: Dual-Headed Snakes](https://naruto.fandom.com/wiki/Summoning:_Dual-Headed_Snakes) | A | Offensive | `narutorpg:summoning_dual_headed_snakes` |
| [Summoning: Earth Release: Tracking Fang Technique](https://naruto.fandom.com/wiki/Summoning:_Earth_Release:_Tracking_Fang_Technique) | B | Offensive | `narutorpg:summoning_earth_release_tracking_fang_technique` (existing) |
| [Summoning: Fan Dance Drop](https://naruto.fandom.com/wiki/Summoning:_Fan_Dance_Drop) | A | Offensive | `narutorpg:summoning_fan_dance_drop` |
| [Summoning: Fiddler Crab](https://naruto.fandom.com/wiki/Summoning:_Fiddler_Crab) | C | Supplementary | `narutorpg:summoning_fiddler_crab` |
| [Summoning: Food Cart Destroyer Technique](https://naruto.fandom.com/wiki/Summoning:_Food_Cart_Destroyer_Technique) | B | Offensive | `narutorpg:summoning_food_cart_destroyer_technique` (existing) |
| [Summoning: Giant Clam: Boiling Break](https://naruto.fandom.com/wiki/Summoning:_Giant_Clam:_Boiling_Break) | A | Offensive | `narutorpg:summoning_giant_clam_boiling_break` (existing) |
| [Summoning: Ibuse](https://naruto.fandom.com/wiki/Summoning:_Ibuse) | A | Offensive | `narutorpg:summoning_ibuse` |
| [Summoning: Ibuse Poisoning](https://naruto.fandom.com/wiki/Summoning:_Ibuse_Poisoning) | C | Offensive | `narutorpg:summoning_ibuse_poisoning` (existing) |
| [Summoning: Impure World Reincarnation](https://naruto.fandom.com/wiki/Summoning:_Impure_World_Reincarnation) | S | Supplementary | `narutorpg:summoning_impure_world_reincarnation` |
| [Summoning: Impure World Reincarnation Hokage](https://naruto.fandom.com/wiki/Summoning:_Impure_World_Reincarnation_Hokage) | S | Offensive | `narutorpg:summoning_impure_world_reincarnation_hokage` (existing) |
| [Summoning: Iron Maiden](https://naruto.fandom.com/wiki/Summoning:_Iron_Maiden) | A | Offensive, Supplementary | `narutorpg:summoning_iron_maiden` |
| [Summoning: Kamatari](https://naruto.fandom.com/wiki/Summoning:_Kamatari) | A | Offensive, Defensive | `narutorpg:summoning_kamatari` |
| [Summoning: Katsuyu](https://naruto.fandom.com/wiki/Summoning:_Katsuyu) | A | Offensive | `narutorpg:summoning_katsuyu` |
| [Summoning: Lightning Flash Blade Creation](https://naruto.fandom.com/wiki/Summoning:_Lightning_Flash_Blade_Creation) | B | Supplementary | `narutorpg:summoning_lightning_flash_blade_creation` |
| [Summoning: Log Rolling](https://naruto.fandom.com/wiki/Summoning:_Log_Rolling) | A | Offensive | `narutorpg:summoning_log_rolling` |
| [Summoning: Nine-Tails](https://naruto.fandom.com/wiki/Summoning:_Nine-Tails) | A | Offensive | `narutorpg:summoning_nine_tails` |
| [Summoning: Quick Beheading Dance](https://naruto.fandom.com/wiki/Summoning:_Quick_Beheading_Dance) | B | Offensive | `narutorpg:summoning_quick_beheading_dance` (existing) |
| [Summoning: Quintuple Rashōmon](https://naruto.fandom.com/wiki/Summoning:_Quintuple_Rashōmon) | A | Defensive | `narutorpg:summoning_quintuple_rashomon` |
| [Summoning: Rainwater Gate](https://naruto.fandom.com/wiki/Summoning:_Rainwater_Gate) | A | Defensive | `narutorpg:summoning_rainwater_gate` |
| [Summoning: Rashōmon](https://naruto.fandom.com/wiki/Summoning:_Rashōmon) | B | Defensive | `narutorpg:summoning_rashomon` |
| [Summoning: Rashōmon Hell](https://naruto.fandom.com/wiki/Summoning:_Rashōmon_Hell) | A | Offensive | `narutorpg:summoning_rashomon_hell` |
| [Summoning: Ten-Tails](https://naruto.fandom.com/wiki/Summoning:_Ten-Tails) | A | Offensive | `narutorpg:summoning_ten_tails` |
| [Summoning: Toad Mouth Bind](https://naruto.fandom.com/wiki/Summoning:_Toad_Mouth_Bind) | A | Offensive | `narutorpg:summoning_toad_mouth_bind` |
| [Summoning: Toad Shop Technique](https://naruto.fandom.com/wiki/Summoning:_Toad_Shop_Technique) | B | Offensive | `narutorpg:summoning_toad_shop_technique` |
| [Summoning: Torture Chamber](https://naruto.fandom.com/wiki/Summoning:_Torture_Chamber) | A | Offensive, Supplementary | `narutorpg:summoning_torture_chamber` |
| [Summoning: Triple Rashōmon](https://naruto.fandom.com/wiki/Summoning:_Triple_Rashōmon) | A | Defensive | `narutorpg:summoning_triple_rashomon` |
| [Summoning: True Fire of the Flaming Mountain](https://naruto.fandom.com/wiki/Summoning:_True_Fire_of_the_Flaming_Mountain) | A | Offensive | `narutorpg:summoning_true_fire_of_the_flaming_mountain` |
| [Super Beast Flying Combo](https://naruto.fandom.com/wiki/Super_Beast_Flying_Combo) | B | Offensive | `narutorpg:super_beast_flying_combo` |
| [Super Beast Flying Hammer](https://naruto.fandom.com/wiki/Super_Beast_Flying_Hammer) | B | Offensive | `narutorpg:super_beast_flying_hammer` (existing) |
| [Super Beast Imitating Drawing](https://naruto.fandom.com/wiki/Super_Beast_Imitating_Drawing) | B | Offensive, Supplementary | `narutorpg:super_beast_imitating_drawing` |
| [Super Beast Imitating Drawing: A Hundred Demons Wandering at Night](https://naruto.fandom.com/wiki/Super_Beast_Imitating_Drawing:_A_Hundred_Demons_Wandering_at_Night) | C | Offensive | `narutorpg:super_beast_imitating_drawing_a_hundred_demons_wandering_at_night` |
| [Super Beast Imitating Drawing: Armour Collection](https://naruto.fandom.com/wiki/Super_Beast_Imitating_Drawing:_Armour_Collection) | C | Offensive | `narutorpg:super_beast_imitating_drawing_armour_collection` |
| [Super Beast Imitating Drawing: Dragon](https://naruto.fandom.com/wiki/Super_Beast_Imitating_Drawing:_Dragon) | C | Offensive, Defensive | `narutorpg:super_beast_imitating_drawing_dragon` |
| [Super Beast Imitating Drawing: Flight](https://naruto.fandom.com/wiki/Super_Beast_Imitating_Drawing:_Flight) | C | Offensive | `narutorpg:super_beast_imitating_drawing_flight` |
| [Super Beast Imitating Drawing: White Sketch](https://naruto.fandom.com/wiki/Super_Beast_Imitating_Drawing:_White_Sketch) | C | Offensive | `narutorpg:super_beast_imitating_drawing_white_sketch` |
| [Super Compression Rasengan](https://naruto.fandom.com/wiki/Super_Compression_Rasengan) | S | Offensive | `narutorpg:super_compression_rasengan` |
| [Super Fang Wolf Fang](https://naruto.fandom.com/wiki/Super_Fang_Wolf_Fang) | B | Offensive | `narutorpg:super_fang_wolf_fang` (existing) |
| [Super God Imitating Drawing](https://naruto.fandom.com/wiki/Super_God_Imitating_Drawing) | B | Offensive | `narutorpg:super_god_imitating_drawing` |
| [Super Mini-Tailed Beast Ball](https://naruto.fandom.com/wiki/Super_Mini-Tailed_Beast_Ball) | S | Offensive | `narutorpg:super_mini_tailed_beast_ball` |
| [Super Multi-Size Technique](https://naruto.fandom.com/wiki/Super_Multi-Size_Technique) | C | Supplementary | `narutorpg:super_multi_size_technique` (existing) |
| [Super Open Hand Slap](https://naruto.fandom.com/wiki/Super_Open_Hand_Slap) | B | Offensive | `narutorpg:super_open_hand_slap` (existing) |
| [Super Revival Fist](https://naruto.fandom.com/wiki/Super_Revival_Fist) | C | Offensive | `narutorpg:super_revival_fist` |
| [Super Tailed Beast Big Ball Rasenshuriken](https://naruto.fandom.com/wiki/Super_Tailed_Beast_Big_Ball_Rasenshuriken) | S | Offensive | `narutorpg:super_tailed_beast_big_ball_rasenshuriken` (existing) |
| [Super-Ultra-Big Ball Rasengan](https://naruto.fandom.com/wiki/Super-Ultra-Big_Ball_Rasengan) | C | Offensive | `narutorpg:super_ultra_big_ball_rasengan` |
| [Superb Turn](https://naruto.fandom.com/wiki/Superb_Turn) | B | Offensive, Defensive | `narutorpg:superb_turn` (existing) |
| [Supercooled Water Technique](https://naruto.fandom.com/wiki/Supercooled_Water_Technique) | D | Supplementary | `narutorpg:supercooled_water_technique` (existing) |
| [Supervibrato Lightning Release Swords](https://naruto.fandom.com/wiki/Supervibrato_Lightning_Release_Swords) | D | Supplementary | `narutorpg:supervibrato_lightning_release_swords` (existing) |
| [Supreme Ultimate Rasengan](https://naruto.fandom.com/wiki/Supreme_Ultimate_Rasengan) | B | Offensive | `narutorpg:supreme_ultimate_rasengan` |
| [Susanoo](https://naruto.fandom.com/wiki/Susanoo) | B | Offensive, Defensive | `narutorpg:susanoo` (existing) |
| [Susanoo: Attack](https://naruto.fandom.com/wiki/Susanoo:_Attack) | B | Offensive | `narutorpg:susanoo_attack` (existing) |
| [Susanoo: Blade of Yachihoko](https://naruto.fandom.com/wiki/Susanoo:_Blade_of_Yachihoko) | B | Offensive | `narutorpg:susanoo_blade_of_yachihoko` |
| [Susanoo: Captive Slash](https://naruto.fandom.com/wiki/Susanoo:_Captive_Slash) | B | Offensive | `narutorpg:susanoo_captive_slash` (existing) |
| [Susanoo: Chidori](https://naruto.fandom.com/wiki/Susanoo:_Chidori) | B | Offensive | `narutorpg:susanoo_chidori` (existing) |
| [Susanoo: Crush](https://naruto.fandom.com/wiki/Susanoo:_Crush) | B | Offensive | `narutorpg:susanoo_crush` (existing) |
| [Susanoo: Dance of Two Swords](https://naruto.fandom.com/wiki/Susanoo:_Dance_of_Two_Swords) | B | Offensive | `narutorpg:susanoo_dance_of_two_swords` |
| [Susanoo: Dance of the Sword](https://naruto.fandom.com/wiki/Susanoo:_Dance_of_the_Sword) | B | Offensive | `narutorpg:susanoo_dance_of_the_sword` |
| [Susanoo: Destruction](https://naruto.fandom.com/wiki/Susanoo:_Destruction) | B | Offensive | `narutorpg:susanoo_destruction` |
| [Susanoo: Fist](https://naruto.fandom.com/wiki/Susanoo:_Fist) | B | Offensive | `narutorpg:susanoo_fist` (existing) |
| [Susanoo: Great Fireball Technique](https://naruto.fandom.com/wiki/Susanoo:_Great_Fireball_Technique) | B | Offensive | `narutorpg:susanoo_great_fireball_technique` (existing) |
| [Susanoo: Kamui Lightning Cutter](https://naruto.fandom.com/wiki/Susanoo:_Kamui_Lightning_Cutter) | A | Offensive | `narutorpg:susanoo_kamui_lightning_cutter` (existing) |
| [Susanoo: Lightning Battle Formation](https://naruto.fandom.com/wiki/Susanoo:_Lightning_Battle_Formation) | B | Offensive | `narutorpg:susanoo_lightning_battle_formation` (existing) |
| [Susanoo: Lightning Flame Flash](https://naruto.fandom.com/wiki/Susanoo:_Lightning_Flame_Flash) | B | Offensive | `narutorpg:susanoo_lightning_flame_flash` (existing) |
| [Susanoo: Rotating Flame](https://naruto.fandom.com/wiki/Susanoo:_Rotating_Flame) | B | Offensive | `narutorpg:susanoo_rotating_flame` (existing) |
| [Susanoo: Sever](https://naruto.fandom.com/wiki/Susanoo:_Sever) | B | Offensive | `narutorpg:susanoo_sever` (existing) |
| [Susanoo: Slash](https://naruto.fandom.com/wiki/Susanoo:_Slash) | B | Offensive | `narutorpg:susanoo_slash` (existing) |
| [Susanoo: Smash](https://naruto.fandom.com/wiki/Susanoo:_Smash) | B | Offensive | `narutorpg:susanoo_smash` |
| [Susanoo: Tear](https://naruto.fandom.com/wiki/Susanoo:_Tear) | B | Offensive | `narutorpg:susanoo_tear` |
| [Susanoo: Tsukumo](https://naruto.fandom.com/wiki/Susanoo:_Tsukumo) | B | Offensive | `narutorpg:susanoo_tsukumo` (existing) |
| [Susanoo: Twin Flames](https://naruto.fandom.com/wiki/Susanoo:_Twin_Flames) | B | Offensive | `narutorpg:susanoo_twin_flames` (existing) |
| [Susanoo: Twin Gods Lightning Advent](https://naruto.fandom.com/wiki/Susanoo:_Twin_Gods_Lightning_Advent) | B | Offensive | `narutorpg:susanoo_twin_gods_lightning_advent` (existing) |
| [Swarming Art](https://naruto.fandom.com/wiki/Swarming_Art) | B | Offensive | `narutorpg:swarming_art` (existing) |
| [Swift Release](https://naruto.fandom.com/wiki/Swift_Release) | B | - | `narutorpg:swift_release` (existing) |
| [Swift Release: Shadowless Flight](https://naruto.fandom.com/wiki/Swift_Release:_Shadowless_Flight) | B | Offensive, Defensive, Supplementary | `narutorpg:swift_release_shadowless_flight` (existing) |
| [Swift Wind Wall](https://naruto.fandom.com/wiki/Swift_Wind_Wall) | D | Defensive | `narutorpg:swift_wind_wall` (existing) |
| [Sword of Kagutsuchi](https://naruto.fandom.com/wiki/Sword_of_Kagutsuchi) | B | Offensive | `narutorpg:sword_of_kagutsuchi` (existing) |
| [Sword of Kagutsuchi: Sever](https://naruto.fandom.com/wiki/Sword_of_Kagutsuchi:_Sever) | B | Offensive | `narutorpg:sword_of_kagutsuchi_sever` (existing) |
| [Sword of Kagutsuchi: Stabbing Flame](https://naruto.fandom.com/wiki/Sword_of_Kagutsuchi:_Stabbing_Flame) | B | Offensive | `narutorpg:sword_of_kagutsuchi_stabbing_flame` (existing) |
| [Sword of Kagutsuchi: Violent](https://naruto.fandom.com/wiki/Sword_of_Kagutsuchi:_Violent) | B | Offensive | `narutorpg:sword_of_kagutsuchi_violent` (existing) |
| [Sword of Kusanagi: Blades from All Directions](https://naruto.fandom.com/wiki/Sword_of_Kusanagi:_Blades_from_All_Directions) | A | Offensive | `narutorpg:sword_of_kusanagi_blades_from_all_directions` |
| [Sword of Kusanagi: Chidori Katana](https://naruto.fandom.com/wiki/Sword_of_Kusanagi:_Chidori_Katana) | B | Supplementary | `narutorpg:sword_of_kusanagi_chidori_katana` (existing) |
| [Sword of Kusanagi: Longsword of the Sky](https://naruto.fandom.com/wiki/Sword_of_Kusanagi:_Longsword_of_the_Sky) | B | Offensive | `narutorpg:sword_of_kusanagi_longsword_of_the_sky` |
| [Sword of Kusanagi: One-Stab Death](https://naruto.fandom.com/wiki/Sword_of_Kusanagi:_One-Stab_Death) | A | Offensive | `narutorpg:sword_of_kusanagi_one_stab_death` |
| [Sword of Nunoboko: Mow](https://naruto.fandom.com/wiki/Sword_of_Nunoboko:_Mow) | A | Offensive | `narutorpg:sword_of_nunoboko_mow` (existing) |
| [Sword of Totsuka: Serial Slashes](https://naruto.fandom.com/wiki/Sword_of_Totsuka:_Serial_Slashes) | B | Offensive | `narutorpg:sword_of_totsuka_serial_slashes` (existing) |
| [Sword of Totsuka: Slashing Rise](https://naruto.fandom.com/wiki/Sword_of_Totsuka:_Slashing_Rise) | B | Offensive | `narutorpg:sword_of_totsuka_slashing_rise` (existing) |
| [Tail Chasing Fang Fang Rotating Fang](https://naruto.fandom.com/wiki/Tail_Chasing_Fang_Fang_Rotating_Fang) | B | Offensive | `narutorpg:tail_chasing_fang_fang_rotating_fang` (existing) |
| [Tail Path Foolishness](https://naruto.fandom.com/wiki/Tail_Path_Foolishness) | C | Offensive | `narutorpg:tail_path_foolishness` |
| [Tail Releasing Method](https://naruto.fandom.com/wiki/Tail_Releasing_Method) | C | Supplementary | `narutorpg:tail_releasing_method` |
| [Tailed Beast Ball](https://naruto.fandom.com/wiki/Tailed_Beast_Ball) | S | Offensive | `narutorpg:tailed_beast_ball` (existing) |
| [Tailed Beast Ball Rasenshuriken](https://naruto.fandom.com/wiki/Tailed_Beast_Ball_Rasenshuriken) | S | Offensive | `narutorpg:tailed_beast_ball_rasenshuriken` (existing) |
| [Tailed Beast Chakra Arms](https://naruto.fandom.com/wiki/Tailed_Beast_Chakra_Arms) | C | Offensive, Defensive, Supplementary | `narutorpg:tailed_beast_chakra_arms` |
| [Tailed Beast Combo](https://naruto.fandom.com/wiki/Tailed_Beast_Combo) | A | Offensive | `narutorpg:tailed_beast_combo` |
| [Tailed Beast Full Charge](https://naruto.fandom.com/wiki/Tailed_Beast_Full_Charge) | B | Offensive | `narutorpg:tailed_beast_full_charge` (existing) |
| [Tailed Beast Heavy Cannon](https://naruto.fandom.com/wiki/Tailed_Beast_Heavy_Cannon) | C | Offensive | `narutorpg:tailed_beast_heavy_cannon` |
| [Tailed Beast Planetary Rasenshuriken](https://naruto.fandom.com/wiki/Tailed_Beast_Planetary_Rasenshuriken) | C | Offensive | `narutorpg:tailed_beast_planetary_rasenshuriken` (existing) |
| [Tailed Beast Rasengan](https://naruto.fandom.com/wiki/Tailed_Beast_Rasengan) | C | Offensive | `narutorpg:tailed_beast_rasengan` |
| [Tailed Beast Removal Technique](https://naruto.fandom.com/wiki/Tailed_Beast_Removal_Technique) | C | Supplementary | `narutorpg:tailed_beast_removal_technique` |
| [Tailed Beast Replication](https://naruto.fandom.com/wiki/Tailed_Beast_Replication) | D | Supplementary | `narutorpg:tailed_beast_replication` (existing) |
| [Tailed Beast Sealing](https://naruto.fandom.com/wiki/Tailed_Beast_Sealing) | C | Supplementary | `narutorpg:tailed_beast_sealing` |
| [Tailed Beast Seventeen Whirlwinds Attack](https://naruto.fandom.com/wiki/Tailed_Beast_Seventeen_Whirlwinds_Attack) | B | Offensive | `narutorpg:tailed_beast_seventeen_whirlwinds_attack` (existing) |
| [Tailed Beast Spiralling Double: Style One](https://naruto.fandom.com/wiki/Tailed_Beast_Spiralling_Double:_Style_One) | C | Offensive | `narutorpg:tailed_beast_spiralling_double_style_one` |
| [Tailed Beast Telepathy](https://naruto.fandom.com/wiki/Tailed_Beast_Telepathy) | D | Supplementary | `narutorpg:tailed_beast_telepathy` |
| [Tailed Beast Transfer Technique](https://naruto.fandom.com/wiki/Tailed_Beast_Transfer_Technique) | C | Supplementary | `narutorpg:tailed_beast_transfer_technique` |
| [Taisa's Transformation Technique](https://naruto.fandom.com/wiki/Taisa's_Transformation_Technique) | D | Supplementary | `narutorpg:taisa_s_transformation_technique` (existing) |
| [Takamimusubinokami](https://naruto.fandom.com/wiki/Takamimusubinokami) | B | Offensive, Supplementary | `narutorpg:takamimusubinokami` (existing) |
| [Takemikazuchi](https://naruto.fandom.com/wiki/Takemikazuchi) | B | Offensive, Supplementary | `narutorpg:takemikazuchi` (existing) |
| [Takigakure Style: Watercutting Sword](https://naruto.fandom.com/wiki/Takigakure_Style:_Watercutting_Sword) | C | Offensive | `narutorpg:takigakure_style_watercutting_sword` (existing) |
| [Team-work is Outstanding](https://naruto.fandom.com/wiki/Team-work_is_Outstanding) | B | Offensive | `narutorpg:team_work_is_outstanding` (existing) |
| [Tearing Air Heavenly Light Slashing Flash Style Zero](https://naruto.fandom.com/wiki/Tearing_Air_Heavenly_Light_Slashing_Flash_Style_Zero) | A | Offensive | `narutorpg:tearing_air_heavenly_light_slashing_flash_style_zero` |
| [Telegram Flash](https://naruto.fandom.com/wiki/Telegram_Flash) | B | Offensive | `narutorpg:telegram_flash` (existing) |
| [Teleportation Seal](https://naruto.fandom.com/wiki/Teleportation_Seal) | A | Supplementary | `narutorpg:teleportation_seal` |
| [Telescope Technique](https://naruto.fandom.com/wiki/Telescope_Technique) | A | Supplementary | `narutorpg:telescope_technique` (existing) |
| [Temporal Rewind](https://naruto.fandom.com/wiki/Temporal_Rewind) | B | Defensive, Supplementary | `narutorpg:temporal_rewind` (existing) |
| [Temporary Paralysis Technique](https://naruto.fandom.com/wiki/Temporary_Paralysis_Technique) | D | Supplementary | `narutorpg:temporary_paralysis_technique` |
| [Tengai Shinsei](https://naruto.fandom.com/wiki/Tengai_Shinsei) | B | Offensive | `narutorpg:tengai_shinsei` (existing) |
| [Tengai Shinsei: Pierce](https://naruto.fandom.com/wiki/Tengai_Shinsei:_Pierce) | B | Offensive | `narutorpg:tengai_shinsei_pierce` |
| [Tenhōjimetsu](https://naruto.fandom.com/wiki/Tenhōjimetsu) | B | Offensive | `narutorpg:tenhojimetsu` (existing) |
| [Tenpenchii](https://naruto.fandom.com/wiki/Tenpenchii) | C | Offensive | `narutorpg:tenpenchii` |
| [Tenrō-Style Ninjutsu: Beast-Human Transformation Technique](https://naruto.fandom.com/wiki/Tenrō-Style_Ninjutsu:_Beast-Human_Transformation_Technique) | C | Supplementary | `narutorpg:tenro_style_ninjutsu_beast_human_transformation_technique` (existing) |
| [Tenseigan Chakra Mode](https://naruto.fandom.com/wiki/Tenseigan_Chakra_Mode) | B | Offensive, Defensive, Supplementary | `narutorpg:tenseigan_chakra_mode` (existing) |
| [The Immortals Rushing Madly Ahead](https://naruto.fandom.com/wiki/The_Immortals_Rushing_Madly_Ahead) | S | Offensive | `narutorpg:the_immortals_rushing_madly_ahead` (existing) |
| [The Inheritance of the Will of Fire](https://naruto.fandom.com/wiki/The_Inheritance_of_the_Will_of_Fire) | A | Offensive | `narutorpg:the_inheritance_of_the_will_of_fire` (existing) |
| [Thick Steam Flow Technique](https://naruto.fandom.com/wiki/Thick_Steam_Flow_Technique) | B | Offensive | `narutorpg:thick_steam_flow_technique` (existing) |
| [Third Eye](https://naruto.fandom.com/wiki/Third_Eye) | D | Supplementary | `narutorpg:third_eye` (existing) |
| [Thousand Bee Stings Technique](https://naruto.fandom.com/wiki/Thousand_Bee_Stings_Technique) | A | Offensive | `narutorpg:thousand_bee_stings_technique` (existing) |
| [Thousand Flashes Beheading Dance](https://naruto.fandom.com/wiki/Thousand_Flashes_Beheading_Dance) | C | Offensive | `narutorpg:thousand_flashes_beheading_dance` |
| [Thousand Flying Water Needles of Death](https://naruto.fandom.com/wiki/Thousand_Flying_Water_Needles_of_Death) | B | Offensive | `narutorpg:thousand_flying_water_needles_of_death` (existing) |
| [Thousand Hands Manipulation Force](https://naruto.fandom.com/wiki/Thousand_Hands_Manipulation_Force) | C | Offensive | `narutorpg:thousand_hands_manipulation_force` |
| [Thousand Hands Manipulation Force: Thrust](https://naruto.fandom.com/wiki/Thousand_Hands_Manipulation_Force:_Thrust) | C | Offensive | `narutorpg:thousand_hands_manipulation_force_thrust` |
| [Thousand Needles of Death](https://naruto.fandom.com/wiki/Thousand_Needles_of_Death) | C | Offensive | `narutorpg:thousand_needles_of_death` |
| [Threaded Tree](https://naruto.fandom.com/wiki/Threaded_Tree) | B | Offensive | `narutorpg:threaded_tree` (existing) |
| [Three Colouring](https://naruto.fandom.com/wiki/Three_Colouring) | C | Offensive | `narutorpg:three_colouring` |
| [Three Demon Gods](https://naruto.fandom.com/wiki/Three_Demon_Gods) | C | Offensive | `narutorpg:three_demon_gods` (existing) |
| [Three Jewels Suction Crushing](https://naruto.fandom.com/wiki/Three_Jewels_Suction_Crushing) | B | Offensive | `narutorpg:three_jewels_suction_crushing` |
| [Three-Seal Barrier](https://naruto.fandom.com/wiki/Three-Seal_Barrier) | C | Supplementary | `narutorpg:three_seal_barrier` |
| [Three-Tailed Rasengan](https://naruto.fandom.com/wiki/Three-Tailed_Rasengan) | C | Offensive | `narutorpg:three_tailed_rasengan` |
| [Three-Tails Chakra: Release](https://naruto.fandom.com/wiki/Three-Tails_Chakra:_Release) | D | - | `narutorpg:three_tails_chakra_release` |
| [Three-Tails Mini Clone](https://naruto.fandom.com/wiki/Three-Tails_Mini_Clone) | D | - | `narutorpg:three_tails_mini_clone` |
| [Three-Tails' Rampage](https://naruto.fandom.com/wiki/Three-Tails'_Rampage) | C | Offensive | `narutorpg:three_tails_rampage` (existing) |
| [Thrusting Shark](https://naruto.fandom.com/wiki/Thrusting_Shark) | C | Offensive | `narutorpg:thrusting_shark` (existing) |
| [Thunder Laser Scan](https://naruto.fandom.com/wiki/Thunder_Laser_Scan) | D | Supplementary | `narutorpg:thunder_laser_scan` |
| [Thunder of the Dead](https://naruto.fandom.com/wiki/Thunder_of_the_Dead) | A | Offensive | `narutorpg:thunder_of_the_dead` (existing) |
| [Thunderclap Drill](https://naruto.fandom.com/wiki/Thunderclap_Drill) | C | Offensive | `narutorpg:thunderclap_drill` (existing) |
| [Thunderswords Technique: Remote Control](https://naruto.fandom.com/wiki/Thunderswords_Technique:_Remote_Control) | C | Offensive, Supplementary | `narutorpg:thunderswords_technique_remote_control` (existing) |
| [Thunderswords Technique: Thunderbolt](https://naruto.fandom.com/wiki/Thunderswords_Technique:_Thunderbolt) | C | Offensive | `narutorpg:thunderswords_technique_thunderbolt` (existing) |
| [Tight Binding Golden Chain Technique](https://naruto.fandom.com/wiki/Tight_Binding_Golden_Chain_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:tight_binding_golden_chain_technique` (existing) |
| [Time Reservoir](https://naruto.fandom.com/wiki/Time_Reservoir) | A | Supplementary | `narutorpg:time_reservoir` |
| [Time-Release Technique](https://naruto.fandom.com/wiki/Time-Release_Technique) | C | Supplementary | `narutorpg:time_release_technique` |
| [Toad Flatness — Shadow Manipulation Technique](https://naruto.fandom.com/wiki/Toad_Flatness_—_Shadow_Manipulation_Technique) | B | Supplementary | `narutorpg:toad_flatness_shadow_manipulation_technique` |
| [Toad Oil Bullet](https://naruto.fandom.com/wiki/Toad_Oil_Bullet) | C | Offensive, Supplementary | `narutorpg:toad_oil_bullet` (existing) |
| [Toad Smoke Technique](https://naruto.fandom.com/wiki/Toad_Smoke_Technique) | D | Supplementary | `narutorpg:toad_smoke_technique` |
| [Toad Spying Technique](https://naruto.fandom.com/wiki/Toad_Spying_Technique) | D | Supplementary | `narutorpg:toad_spying_technique` |
| [Toad Three Slashes](https://naruto.fandom.com/wiki/Toad_Three_Slashes) | C | Offensive | `narutorpg:toad_three_slashes` (existing) |
| [Tongue Tooth Sticky Acid](https://naruto.fandom.com/wiki/Tongue_Tooth_Sticky_Acid) | C | Offensive | `narutorpg:tongue_tooth_sticky_acid` |
| [Tonton Combo](https://naruto.fandom.com/wiki/Tonton_Combo) | B | Offensive, Supplementary | `narutorpg:tonton_combo` |
| [Top Transformed Buddha](https://naruto.fandom.com/wiki/Top_Transformed_Buddha) | B | Offensive | `narutorpg:top_transformed_buddha` (existing) |
| [Tornado High](https://naruto.fandom.com/wiki/Tornado_High) | C | Offensive | `narutorpg:tornado_high` (existing) |
| [Tornado Rasengan](https://naruto.fandom.com/wiki/Tornado_Rasengan) | C | Offensive | `narutorpg:tornado_rasengan` |
| [Tornado of Water](https://naruto.fandom.com/wiki/Tornado_of_Water) | C | Offensive, Defensive, Supplementary | `narutorpg:tornado_of_water` (existing) |
| [Tornado of Water Technique: Magnum](https://naruto.fandom.com/wiki/Tornado_of_Water_Technique:_Magnum) | C | Offensive | `narutorpg:tornado_of_water_technique_magnum` (existing) |
| [Torpedo Nails](https://naruto.fandom.com/wiki/Torpedo_Nails) | C | Offensive | `narutorpg:torpedo_nails` |
| [Torrent: Hydrification Technique](https://naruto.fandom.com/wiki/Torrent:_Hydrification_Technique) | C | Offensive | `narutorpg:torrent_hydrification_technique` (existing) |
| [Tracking Technique](https://naruto.fandom.com/wiki/Tracking_Technique) | B | Supplementary | `narutorpg:tracking_technique` |
| [Transcription Seal: Amaterasu](https://naruto.fandom.com/wiki/Transcription_Seal:_Amaterasu) | B | Supplementary | `narutorpg:transcription_seal_amaterasu` (existing) |
| [Transcription Seal: Izanagi](https://naruto.fandom.com/wiki/Transcription_Seal:_Izanagi) | B | Supplementary | `narutorpg:transcription_seal_izanagi` (existing) |
| [Transformation Technique](https://naruto.fandom.com/wiki/Transformation_Technique) | E | Supplementary | `narutorpg:transformation_technique` (existing) |
| [Transformation: Adamantine Staff](https://naruto.fandom.com/wiki/Transformation:_Adamantine_Staff) | D | Supplementary | `narutorpg:transformation_adamantine_staff` |
| [Transmission Wood](https://naruto.fandom.com/wiki/Transmission_Wood) | B | Supplementary | `narutorpg:transmission_wood` (existing) |
| [Transparent Escape Technique](https://naruto.fandom.com/wiki/Transparent_Escape_Technique) | D | Supplementary | `narutorpg:transparent_escape_technique` |
| [Trinity](https://naruto.fandom.com/wiki/Trinity) | A | Offensive | `narutorpg:trinity` (existing) |
| [Triple Strike: Breaking Through the Clouds](https://naruto.fandom.com/wiki/Triple_Strike:_Breaking_Through_the_Clouds) | C | Offensive | `narutorpg:triple_strike_breaking_through_the_clouds` (existing) |
| [True Fire of Samadhi](https://naruto.fandom.com/wiki/True_Fire_of_Samadhi) | C | Offensive | `narutorpg:true_fire_of_samadhi` (existing) |
| [True Rasengan](https://naruto.fandom.com/wiki/True_Rasengan) | C | Offensive, Defensive | `narutorpg:true_rasengan` |
| [True Sand Waterfall Funeral](https://naruto.fandom.com/wiki/True_Sand_Waterfall_Funeral) | C | Offensive, Defensive, Supplementary | `narutorpg:true_sand_waterfall_funeral` |
| [True Several Thousand Hands: Spiralling Praying Palms](https://naruto.fandom.com/wiki/True_Several_Thousand_Hands:_Spiralling_Praying_Palms) | A | Offensive | `narutorpg:true_several_thousand_hands_spiralling_praying_palms` (existing) |
| [True Shadow Seizing Technique](https://naruto.fandom.com/wiki/True_Shadow_Seizing_Technique) | C | Offensive | `narutorpg:true_shadow_seizing_technique` (existing) |
| [True Sickle Weasel Technique](https://naruto.fandom.com/wiki/True_Sickle_Weasel_Technique) | A | Offensive | `narutorpg:true_sickle_weasel_technique` (existing) |
| [True: Chibaku Tensei](https://naruto.fandom.com/wiki/True:_Chibaku_Tensei) | B | Offensive, Supplementary | `narutorpg:true_chibaku_tensei` (existing) |
| [True: Supreme Ultimate Rasengan](https://naruto.fandom.com/wiki/True:_Supreme_Ultimate_Rasengan) | B | Offensive | `narutorpg:true_supreme_ultimate_rasengan` |
| [True: Tailed Beast Rasenshuriken](https://naruto.fandom.com/wiki/True:_Tailed_Beast_Rasenshuriken) | A | Offensive | `narutorpg:true_tailed_beast_rasenshuriken` (existing) |
| [Trump Card of Pair](https://naruto.fandom.com/wiki/Trump_Card_of_Pair) | B | Offensive, Defensive | `narutorpg:trump_card_of_pair` (existing) |
| [Truth-Seeking Ball](https://naruto.fandom.com/wiki/Truth-Seeking_Ball) | A | Offensive, Defensive, Supplementary | `narutorpg:truth_seeking_ball` (existing) |
| [Truth-Seeking Ball: Cycle](https://naruto.fandom.com/wiki/Truth-Seeking_Ball:_Cycle) | A | Defensive | `narutorpg:truth_seeking_ball_cycle` (existing) |
| [Truth-Seeking Body Flicker](https://naruto.fandom.com/wiki/Truth-Seeking_Body_Flicker) | A | Offensive, Supplementary | `narutorpg:truth_seeking_body_flicker` (existing) |
| [Truth-Seeking Chaos Bullets](https://naruto.fandom.com/wiki/Truth-Seeking_Chaos_Bullets) | A | Offensive | `narutorpg:truth_seeking_chaos_bullets` (existing) |
| [Truth-Seeking Kick](https://naruto.fandom.com/wiki/Truth-Seeking_Kick) | A | Offensive | `narutorpg:truth_seeking_kick` (existing) |
| [Truth-Seeking Ruined Earth](https://naruto.fandom.com/wiki/Truth-Seeking_Ruined_Earth) | A | Offensive | `narutorpg:truth_seeking_ruined_earth` (existing) |
| [Truth-Seeking Scorched Earth](https://naruto.fandom.com/wiki/Truth-Seeking_Scorched_Earth) | C | Offensive | `narutorpg:truth_seeking_scorched_earth` (existing) |
| [Truth-Seeking Umbrella](https://naruto.fandom.com/wiki/Truth-Seeking_Umbrella) | A | Offensive | `narutorpg:truth_seeking_umbrella` (existing) |
| [Truth-Seeking: Pure Shadow](https://naruto.fandom.com/wiki/Truth-Seeking:_Pure_Shadow) | A | Offensive | `narutorpg:truth_seeking_pure_shadow` (existing) |
| [Truth-Seeking: Saving Light](https://naruto.fandom.com/wiki/Truth-Seeking:_Saving_Light) | A | Offensive | `narutorpg:truth_seeking_saving_light` (existing) |
| [Truth-Seeking: Sever](https://naruto.fandom.com/wiki/Truth-Seeking:_Sever) | A | Offensive | `narutorpg:truth_seeking_sever` (existing) |
| [Truth-Seeking: Shining Deity](https://naruto.fandom.com/wiki/Truth-Seeking:_Shining_Deity) | A | Offensive | `narutorpg:truth_seeking_shining_deity` (existing) |
| [Tsuchigumo Style: Forbidden Life Technique Release: Creation of Heaven and Earth](https://naruto.fandom.com/wiki/Tsuchigumo_Style:_Forbidden_Life_Technique_Release:_Creation_of_Heaven_and_Earth) | C | Supplementary | `narutorpg:tsuchigumo_style_forbidden_life_technique_release_creation_of_heaven_and_earth` (existing) |
| [Tsukuyomi Mode](https://naruto.fandom.com/wiki/Tsukuyomi_Mode) | B | Supplementary | `narutorpg:tsukuyomi_mode` (existing) |
| [Turbulent Four Seasons](https://naruto.fandom.com/wiki/Turbulent_Four_Seasons) | B | Offensive | `narutorpg:turbulent_four_seasons` (existing) |
| [Turning into a Frog Technique](https://naruto.fandom.com/wiki/Turning_into_a_Frog_Technique) | C | Supplementary | `narutorpg:turning_into_a_frog_technique` |
| [Twin Metal Rods Shootdown](https://naruto.fandom.com/wiki/Twin_Metal_Rods_Shootdown) | C | Offensive, Defensive | `narutorpg:twin_metal_rods_shootdown` |
| [Twin Pole Heavely Drop](https://naruto.fandom.com/wiki/Twin_Pole_Heavely_Drop) | B | Offensive | `narutorpg:twin_pole_heavely_drop` (existing) |
| [Twin Rising Dragons](https://naruto.fandom.com/wiki/Twin_Rising_Dragons) | C | Offensive | `narutorpg:twin_rising_dragons` |
| [Twin Rising Dragons: Second Scroll](https://naruto.fandom.com/wiki/Twin_Rising_Dragons:_Second_Scroll) | C | Offensive | `narutorpg:twin_rising_dragons_second_scroll` |
| [Twin Snakes Mutual Death Technique](https://naruto.fandom.com/wiki/Twin_Snakes_Mutual_Death_Technique) | A | Offensive | `narutorpg:twin_snakes_mutual_death_technique` |
| [Twin Swallow Cut](https://naruto.fandom.com/wiki/Twin_Swallow_Cut) | C | Offensive | `narutorpg:twin_swallow_cut` |
| [Twin Swift Rasengan](https://naruto.fandom.com/wiki/Twin_Swift_Rasengan) | C | Offensive | `narutorpg:twin_swift_rasengan` |
| [Twinsword Technique: Crushing Hammer](https://naruto.fandom.com/wiki/Twinsword_Technique:_Crushing_Hammer) | C | Offensive | `narutorpg:twinsword_technique_crushing_hammer` |
| [Twinsword Technique: Cut to Pieces](https://naruto.fandom.com/wiki/Twinsword_Technique:_Cut_to_Pieces) | C | Offensive | `narutorpg:twinsword_technique_cut_to_pieces` |
| [Twisted Truth-Seeking](https://naruto.fandom.com/wiki/Twisted_Truth-Seeking) | A | Offensive | `narutorpg:twisted_truth_seeking` (existing) |
| [Two Calm Kusanagi](https://naruto.fandom.com/wiki/Two_Calm_Kusanagi) | B | Offensive | `narutorpg:two_calm_kusanagi` |
| [Two Lights Beams of Kumogakure](https://naruto.fandom.com/wiki/Two_Lights_Beams_of_Kumogakure) | C | Offensive | `narutorpg:two_lights_beams_of_kumogakure` (existing) |
| [Two Rays of Light Among the Clouds](https://naruto.fandom.com/wiki/Two_Rays_of_Light_Among_the_Clouds) | B | Offensive | `narutorpg:two_rays_of_light_among_the_clouds` (existing) |
| [Two-Tailed Cat Flame Incineration Slash](https://naruto.fandom.com/wiki/Two-Tailed_Cat_Flame_Incineration_Slash) | B | Offensive | `narutorpg:two_tailed_cat_flame_incineration_slash` (existing) |
| [Two-Tails Exploding Orbs](https://naruto.fandom.com/wiki/Two-Tails_Exploding_Orbs) | C | Offensive | `narutorpg:two_tails_exploding_orbs` |
| [Typhoon Flame Formation](https://naruto.fandom.com/wiki/Typhoon_Flame_Formation) | C | Offensive | `narutorpg:typhoon_flame_formation` (existing) |
| [Typhoon Impact](https://naruto.fandom.com/wiki/Typhoon_Impact) | C | Offensive | `narutorpg:typhoon_impact` (existing) |
| [Typhoon Release](https://naruto.fandom.com/wiki/Typhoon_Release) | B | - | `narutorpg:typhoon_release` (existing) |
| [Typhoon Release: Consecutive Bursting Strong Winds](https://naruto.fandom.com/wiki/Typhoon_Release:_Consecutive_Bursting_Strong_Winds) | B | Offensive | `narutorpg:typhoon_release_consecutive_bursting_strong_winds` (existing) |
| [Typhoon Release: Great Consecutive Bursting Extreme Winds](https://naruto.fandom.com/wiki/Typhoon_Release:_Great_Consecutive_Bursting_Extreme_Winds) | B | Offensive | `narutorpg:typhoon_release_great_consecutive_bursting_extreme_winds` (existing) |
| [Typhoon Rotating](https://naruto.fandom.com/wiki/Typhoon_Rotating) | C | Offensive | `narutorpg:typhoon_rotating` (existing) |
| [Typhoon Water Vortex Technique](https://naruto.fandom.com/wiki/Typhoon_Water_Vortex_Technique) | B | Offensive, Defensive | `narutorpg:typhoon_water_vortex_technique` (existing) |
| [Uchiha Blaze Sword: Fire Possession](https://naruto.fandom.com/wiki/Uchiha_Blaze_Sword:_Fire_Possession) | C | Offensive | `narutorpg:uchiha_blaze_sword_fire_possession` (existing) |
| [Uchiha Cherry Blossom Thunderclap](https://naruto.fandom.com/wiki/Uchiha_Cherry_Blossom_Thunderclap) | B | Offensive | `narutorpg:uchiha_cherry_blossom_thunderclap` (existing) |
| [Uchiha Family: Trinity Technique](https://naruto.fandom.com/wiki/Uchiha_Family:_Trinity_Technique) | B | Offensive | `narutorpg:uchiha_family_trinity_technique` (existing) |
| [Uchiha Flame Formation](https://naruto.fandom.com/wiki/Uchiha_Flame_Formation) | B | Defensive | `narutorpg:uchiha_flame_formation` (existing) |
| [Uchiha Imprisonment Sealing](https://naruto.fandom.com/wiki/Uchiha_Imprisonment_Sealing) | C | Offensive, Supplementary | `narutorpg:uchiha_imprisonment_sealing` |
| [Uchiha Return](https://naruto.fandom.com/wiki/Uchiha_Return) | B | Offensive, Defensive | `narutorpg:uchiha_return` (existing) |
| [Uchiha Shuriken Formation: Spring Lightning](https://naruto.fandom.com/wiki/Uchiha_Shuriken_Formation:_Spring_Lightning) | C | Offensive | `narutorpg:uchiha_shuriken_formation_spring_lightning` (existing) |
| [Uchiha Shurikenjutsu: Spring Storm](https://naruto.fandom.com/wiki/Uchiha_Shurikenjutsu:_Spring_Storm) | D | - | `narutorpg:uchiha_shurikenjutsu_spring_storm` |
| [Uchiha Style Blade Technique: Hagoromo](https://naruto.fandom.com/wiki/Uchiha_Style_Blade_Technique:_Hagoromo) | C | Offensive, Supplementary | `narutorpg:uchiha_style_blade_technique_hagoromo` |
| [Uchiha Style Shurikenjutsu: "Lightning": Triple](https://naruto.fandom.com/wiki/Uchiha_Style_Shurikenjutsu:_"Lightning":_Triple) | C | Offensive | `narutorpg:uchiha_style_shurikenjutsu_lightning_triple` (existing) |
| [Uchiha Style Shurikenjutsu: Hazy Wheel](https://naruto.fandom.com/wiki/Uchiha_Style_Shurikenjutsu:_Hazy_Wheel) | C | Offensive | `narutorpg:uchiha_style_shurikenjutsu_hazy_wheel` |
| [Uchiha Style: Dance of the Sun Halo](https://naruto.fandom.com/wiki/Uchiha_Style:_Dance_of_the_Sun_Halo) | C | Offensive | `narutorpg:uchiha_style_dance_of_the_sun_halo` (existing) |
| [Uchiha Style: Dance of the Sun Halo – Flame Formation](https://naruto.fandom.com/wiki/Uchiha_Style:_Dance_of_the_Sun_Halo_–_Flame_Formation) | B | Offensive | `narutorpg:uchiha_style_dance_of_the_sun_halo_flame_formation` (existing) |
| [Uchiha Style: Four Directions Slash](https://naruto.fandom.com/wiki/Uchiha_Style:_Four_Directions_Slash) | C | Offensive | `narutorpg:uchiha_style_four_directions_slash` (existing) |
| [Ultimate Art (game)](https://naruto.fandom.com/wiki/Ultimate_Art_(game)) | B | Offensive | `narutorpg:ultimate_art_game` (existing) |
| [Ultimate Death Beyond Terror](https://naruto.fandom.com/wiki/Ultimate_Death_Beyond_Terror) | S | Offensive | `narutorpg:ultimate_death_beyond_terror` (existing) |
| [Ultimate Poison Scattering](https://naruto.fandom.com/wiki/Ultimate_Poison_Scattering) | C | Offensive | `narutorpg:ultimate_poison_scattering` |
| [Ultimately Hard Absolute Attack: Sand Waterfall Spear Interment](https://naruto.fandom.com/wiki/Ultimately_Hard_Absolute_Attack:_Sand_Waterfall_Spear_Interment) | C | Offensive | `narutorpg:ultimately_hard_absolute_attack_sand_waterfall_spear_interment` |
| [Ultimately Hard Absolute Attack: Spear of Shukaku](https://naruto.fandom.com/wiki/Ultimately_Hard_Absolute_Attack:_Spear_of_Shukaku) | C | Offensive | `narutorpg:ultimately_hard_absolute_attack_spear_of_shukaku` |
| [Ultimately Hard Absolute Defence: Shield of Shukaku](https://naruto.fandom.com/wiki/Ultimately_Hard_Absolute_Defence:_Shield_of_Shukaku) | D | Defensive | `narutorpg:ultimately_hard_absolute_defence_shield_of_shukaku` |
| [Ultra-Big Ball Rasengan](https://naruto.fandom.com/wiki/Ultra-Big_Ball_Rasengan) | C | Offensive | `narutorpg:ultra_big_ball_rasengan` |
| [Ultra-Big Ball Rasengan: Heaven Destruction](https://naruto.fandom.com/wiki/Ultra-Big_Ball_Rasengan:_Heaven_Destruction) | S | Offensive | `narutorpg:ultra_big_ball_rasengan_heaven_destruction` |
| [Ultra-Fast Regeneration Technique](https://naruto.fandom.com/wiki/Ultra-Fast_Regeneration_Technique) | D | Supplementary | `narutorpg:ultra_fast_regeneration_technique` |
| [Ultra-Many Spiralling Serial Spheres](https://naruto.fandom.com/wiki/Ultra-Many_Spiralling_Serial_Spheres) | A | Offensive | `narutorpg:ultra_many_spiralling_serial_spheres` |
| [Ultra-Viscous Slime Battering Ram](https://naruto.fandom.com/wiki/Ultra-Viscous_Slime_Battering_Ram) | C | Offensive, Supplementary | `narutorpg:ultra_viscous_slime_battering_ram` |
| [Umbrella Shock Wave](https://naruto.fandom.com/wiki/Umbrella_Shock_Wave) | C | Offensive, Defensive | `narutorpg:umbrella_shock_wave` (existing) |
| [Unite](https://naruto.fandom.com/wiki/Unite) | D | Supplementary | `narutorpg:unite` |
| [Unknown Ability](https://naruto.fandom.com/wiki/Unknown_Ability) | A | Offensive | `narutorpg:unknown_ability` |
| [Unsealing Technique](https://naruto.fandom.com/wiki/Unsealing_Technique) | C | Supplementary | `narutorpg:unsealing_technique` |
| [Unsealing Technique: Paper Snowstorm](https://naruto.fandom.com/wiki/Unsealing_Technique:_Paper_Snowstorm) | C | Offensive, Supplementary | `narutorpg:unsealing_technique_paper_snowstorm` |
| [Unsealing Technique: Segmented Iron Dome](https://naruto.fandom.com/wiki/Unsealing_Technique:_Segmented_Iron_Dome) | C | Defensive | `narutorpg:unsealing_technique_segmented_iron_dome` |
| [Unsealing Technique: Shuriken](https://naruto.fandom.com/wiki/Unsealing_Technique:_Shuriken) | C | Offensive | `narutorpg:unsealing_technique_shuriken` |
| [Uzumaki Formation](https://naruto.fandom.com/wiki/Uzumaki_Formation) | D | Defensive | `narutorpg:uzumaki_formation` |
| [Uzumaki Twin Striking Palms](https://naruto.fandom.com/wiki/Uzumaki_Twin_Striking_Palms) | B | Offensive | `narutorpg:uzumaki_twin_striking_palms` (existing) |
| [Vacuum Sword](https://naruto.fandom.com/wiki/Vacuum_Sword) | C | Offensive, Defensive | `narutorpg:vacuum_sword` (existing) |
| [Vanishing Facial Copy Technique](https://naruto.fandom.com/wiki/Vanishing_Facial_Copy_Technique) | B | Supplementary | `narutorpg:vanishing_facial_copy_technique` |
| [Vanishing Many Spiralling Serial Spheres](https://naruto.fandom.com/wiki/Vanishing_Many_Spiralling_Serial_Spheres) | S | Offensive | `narutorpg:vanishing_many_spiralling_serial_spheres` (existing) |
| [Vanishing Rasengan](https://naruto.fandom.com/wiki/Vanishing_Rasengan) | C | Offensive | `narutorpg:vanishing_rasengan` (existing) |
| [Vanishing Rasengan: Waxing and Waning](https://naruto.fandom.com/wiki/Vanishing_Rasengan:_Waxing_and_Waning) | C | Offensive | `narutorpg:vanishing_rasengan_waxing_and_waning` (existing) |
| [Very Hiden: Venomous Insect Venom](https://naruto.fandom.com/wiki/Very_Hiden:_Venomous_Insect_Venom) | C | Offensive, Supplementary | `narutorpg:very_hiden_venomous_insect_venom` (existing) |
| [Violent Heavenly Earth Needle](https://naruto.fandom.com/wiki/Violent_Heavenly_Earth_Needle) | C | Offensive | `narutorpg:violent_heavenly_earth_needle` (existing) |
| [Violent Stabbing Face Slap](https://naruto.fandom.com/wiki/Violent_Stabbing_Face_Slap) | C | Offensive | `narutorpg:violent_stabbing_face_slap` (existing) |
| [Violent Whirlwind](https://naruto.fandom.com/wiki/Violent_Whirlwind) | C | Offensive | `narutorpg:violent_whirlwind` (existing) |
| [Viscous Water Mass](https://naruto.fandom.com/wiki/Viscous_Water_Mass) | D | Defensive, Supplementary | `narutorpg:viscous_water_mass` (existing) |
| [Watch Your Steps!](https://naruto.fandom.com/wiki/Watch_Your_Steps!) | C | Offensive, Supplementary | `narutorpg:watch_your_steps` |
| [Watchful Eye's Torrent](https://naruto.fandom.com/wiki/Watchful_Eye's_Torrent) | S | Offensive | `narutorpg:watchful_eye_s_torrent` (existing) |
| [Water Attacking Gorgon](https://naruto.fandom.com/wiki/Water_Attacking_Gorgon) | C | Offensive | `narutorpg:water_attacking_gorgon` (existing) |
| [Water Balloon Technique](https://naruto.fandom.com/wiki/Water_Balloon_Technique) | C | Offensive | `narutorpg:water_balloon_technique` (existing) |
| [Water Characters](https://naruto.fandom.com/wiki/Water_Characters) | D | Supplementary | `narutorpg:water_characters` (existing) |
| [Water Clone Combo](https://naruto.fandom.com/wiki/Water_Clone_Combo) | C | Offensive | `narutorpg:water_clone_combo` (existing) |
| [Water Clone Technique](https://naruto.fandom.com/wiki/Water_Clone_Technique) | C | Supplementary | `narutorpg:water_clone_technique` (existing) |
| [Water Droplet Reflection Sight](https://naruto.fandom.com/wiki/Water_Droplet_Reflection_Sight) | B | Supplementary | `narutorpg:water_droplet_reflection_sight` |
| [Water Flow Sensing Technique](https://naruto.fandom.com/wiki/Water_Flow_Sensing_Technique) | D | Supplementary | `narutorpg:water_flow_sensing_technique` (existing) |
| [Water Gun Technique](https://naruto.fandom.com/wiki/Water_Gun_Technique) | B | Offensive | `narutorpg:water_gun_technique` (existing) |
| [Water Gun Technique: One-Hand Magnum](https://naruto.fandom.com/wiki/Water_Gun_Technique:_One-Hand_Magnum) | C | Offensive | `narutorpg:water_gun_technique_one_hand_magnum` (existing) |
| [Water Gun Technique: Two-Handed Magnum](https://naruto.fandom.com/wiki/Water_Gun_Technique:_Two-Handed_Magnum) | C | Offensive | `narutorpg:water_gun_technique_two_handed_magnum` (existing) |
| [Water Gun: Quick Shot](https://naruto.fandom.com/wiki/Water_Gun:_Quick_Shot) | C | Offensive | `narutorpg:water_gun_quick_shot` (existing) |
| [Water Gun: Two Guns](https://naruto.fandom.com/wiki/Water_Gun:_Two_Guns) | C | Offensive | `narutorpg:water_gun_two_guns` (existing) |
| [Water Mirror Colliding Bullets](https://naruto.fandom.com/wiki/Water_Mirror_Colliding_Bullets) | C | Offensive | `narutorpg:water_mirror_colliding_bullets` (existing) |
| [Water Needle](https://naruto.fandom.com/wiki/Water_Needle) | B | Offensive | `narutorpg:water_needle` (existing) |
| [Water Palm Blade](https://naruto.fandom.com/wiki/Water_Palm_Blade) | C | Offensive | `narutorpg:water_palm_blade` (existing) |
| [Water Pillar Thrust](https://naruto.fandom.com/wiki/Water_Pillar_Thrust) | C | Offensive | `narutorpg:water_pillar_thrust` (existing) |
| [Water Prison Bead Binding Technique](https://naruto.fandom.com/wiki/Water_Prison_Bead_Binding_Technique) | D | - | `narutorpg:water_prison_bead_binding_technique` (existing) |
| [Water Prison Shark Dance Technique](https://naruto.fandom.com/wiki/Water_Prison_Shark_Dance_Technique) | S | Supplementary | `narutorpg:water_prison_shark_dance_technique` (existing) |
| [Water Prison Technique](https://naruto.fandom.com/wiki/Water_Prison_Technique) | C | Supplementary | `narutorpg:water_prison_technique` (existing) |
| [Water Release Bullet: Orca](https://naruto.fandom.com/wiki/Water_Release_Bullet:_Orca) | C | Offensive | `narutorpg:water_release_bullet_orca` (existing) |
| [Water Release Genjutsu: Demonic Phantom Fog Prison](https://naruto.fandom.com/wiki/Water_Release_Genjutsu:_Demonic_Phantom_Fog_Prison) | B | Supplementary | `narutorpg:water_release_genjutsu_demonic_phantom_fog_prison` (existing) |
| [Water Release: A Thousand Feeding Sharks](https://naruto.fandom.com/wiki/Water_Release:_A_Thousand_Feeding_Sharks) | B | Offensive | `narutorpg:water_release_a_thousand_feeding_sharks` (existing) |
| [Water Release: Angry Bitter Rain](https://naruto.fandom.com/wiki/Water_Release:_Angry_Bitter_Rain) | D | Supplementary | `narutorpg:water_release_angry_bitter_rain` (existing) |
| [Water Release: Archerfish](https://naruto.fandom.com/wiki/Water_Release:_Archerfish) | D | Supplementary | `narutorpg:water_release_archerfish` (existing) |
| [Water Release: Azure Dragon Palm](https://naruto.fandom.com/wiki/Water_Release:_Azure_Dragon_Palm) | C | Offensive | `narutorpg:water_release_azure_dragon_palm` (existing) |
| [Water Release: Black Rain Technique](https://naruto.fandom.com/wiki/Water_Release:_Black_Rain_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_black_rain_technique` (existing) |
| [Water Release: Blood Conversion Technique](https://naruto.fandom.com/wiki/Water_Release:_Blood_Conversion_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_blood_conversion_technique` (existing) |
| [Water Release: Bubbles Technique](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique) | C | Offensive | `narutorpg:water_release_bubbles_technique` (existing) |
| [Water Release: Bubbles Technique — Broad Defence](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Broad_Defence) | D | Defensive | `narutorpg:water_release_bubbles_technique_broad_defence` (existing) |
| [Water Release: Bubbles Technique — Combo](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Combo) | C | Offensive | `narutorpg:water_release_bubbles_technique_combo` (existing) |
| [Water Release: Bubbles Technique — Drift](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Drift) | C | Offensive, Supplementary | `narutorpg:water_release_bubbles_technique_drift` (existing) |
| [Water Release: Bubbles Technique — Galaxy](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Galaxy) | C | Offensive | `narutorpg:water_release_bubbles_technique_galaxy` (existing) |
| [Water Release: Bubbles Technique — Great River](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Great_River) | C | Offensive | `narutorpg:water_release_bubbles_technique_great_river` (existing) |
| [Water Release: Bubbles Technique — Ink](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Ink) | C | Offensive | `narutorpg:water_release_bubbles_technique_ink` (existing) |
| [Water Release: Bubbles Technique — Mountain Wind](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Mountain_Wind) | C | Offensive | `narutorpg:water_release_bubbles_technique_mountain_wind` (existing) |
| [Water Release: Bubbles Technique — Ripples](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Ripples) | C | Offensive | `narutorpg:water_release_bubbles_technique_ripples` (existing) |
| [Water Release: Bubbles Technique — Rise](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Rise) | C | Offensive | `narutorpg:water_release_bubbles_technique_rise` (existing) |
| [Water Release: Bubbles Technique — Twist](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Twist) | C | Offensive | `narutorpg:water_release_bubbles_technique_twist` (existing) |
| [Water Release: Bubbles Technique — Wrap](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Wrap) | C | Offensive | `narutorpg:water_release_bubbles_technique_wrap` (existing) |
| [Water Release: Clear Mirror Still Water](https://naruto.fandom.com/wiki/Water_Release:_Clear_Mirror_Still_Water) | D | Defensive | `narutorpg:water_release_clear_mirror_still_water` (existing) |
| [Water Release: Cold Sky Water Attack](https://naruto.fandom.com/wiki/Water_Release:_Cold_Sky_Water_Attack) | C | Offensive | `narutorpg:water_release_cold_sky_water_attack` (existing) |
| [Water Release: Drizzle](https://naruto.fandom.com/wiki/Water_Release:_Drizzle) | C | Offensive | `narutorpg:water_release_drizzle` (existing) |
| [Water Release: Droplet Spear](https://naruto.fandom.com/wiki/Water_Release:_Droplet_Spear) | C | Offensive | `narutorpg:water_release_droplet_spear` (existing) |
| [Water Release: Exploding Water Colliding Wave](https://naruto.fandom.com/wiki/Water_Release:_Exploding_Water_Colliding_Wave) | B | Offensive, Supplementary | `narutorpg:water_release_exploding_water_colliding_wave` (existing) |
| [Water Release: Falling Rain Needles](https://naruto.fandom.com/wiki/Water_Release:_Falling_Rain_Needles) | C | Offensive | `narutorpg:water_release_falling_rain_needles` (existing) |
| [Water Release: Five Feeding Sharks](https://naruto.fandom.com/wiki/Water_Release:_Five_Feeding_Sharks) | B | Offensive | `narutorpg:water_release_five_feeding_sharks` (existing) |
| [Water Release: Five Gluttonous Sharks](https://naruto.fandom.com/wiki/Water_Release:_Five_Gluttonous_Sharks) | C | Offensive, Defensive | `narutorpg:water_release_five_gluttonous_sharks` (existing) |
| [Water Release: Four Sharks Rain](https://naruto.fandom.com/wiki/Water_Release:_Four_Sharks_Rain) | C | Offensive | `narutorpg:water_release_four_sharks_rain` (existing) |
| [Water Release: Furious Roaring Tornado](https://naruto.fandom.com/wiki/Water_Release:_Furious_Roaring_Tornado) | C | Offensive | `narutorpg:water_release_furious_roaring_tornado` (existing) |
| [Water Release: Great Exploding Water Colliding Wave](https://naruto.fandom.com/wiki/Water_Release:_Great_Exploding_Water_Colliding_Wave) | C | Offensive, Supplementary | `narutorpg:water_release_great_exploding_water_colliding_wave` (existing) |
| [Water Release: Great Gunshot](https://naruto.fandom.com/wiki/Water_Release:_Great_Gunshot) | C | Offensive | `narutorpg:water_release_great_gunshot` (existing) |
| [Water Release: Great Shark Bullet Technique](https://naruto.fandom.com/wiki/Water_Release:_Great_Shark_Bullet_Technique) | A | Offensive | `narutorpg:water_release_great_shark_bullet_technique` (existing) |
| [Water Release: Great Water Arm Technique](https://naruto.fandom.com/wiki/Water_Release:_Great_Water_Arm_Technique) | B | Supplementary | `narutorpg:water_release_great_water_arm_technique` (existing) |
| [Water Release: Great Water Arm Technique: Half-Moon Cut](https://naruto.fandom.com/wiki/Water_Release:_Great_Water_Arm_Technique:_Half-Moon_Cut) | C | Offensive | `narutorpg:water_release_great_water_arm_technique_half_moon_cut` (existing) |
| [Water Release: Great Water Drill](https://naruto.fandom.com/wiki/Water_Release:_Great_Water_Drill) | C | Offensive | `narutorpg:water_release_great_water_drill` (existing) |
| [Water Release: Great Water Leg Technique](https://naruto.fandom.com/wiki/Water_Release:_Great_Water_Leg_Technique) | C | Supplementary | `narutorpg:water_release_great_water_leg_technique` (existing) |
| [Water Release: Great Water Mass Bullet](https://naruto.fandom.com/wiki/Water_Release:_Great_Water_Mass_Bullet) | C | Offensive | `narutorpg:water_release_great_water_mass_bullet` (existing) |
| [Water Release: Great Waterfall Flow Technique](https://naruto.fandom.com/wiki/Water_Release:_Great_Waterfall_Flow_Technique) | C | Offensive | `narutorpg:water_release_great_waterfall_flow_technique` (existing) |
| [Water Release: Great Waterfall Technique](https://naruto.fandom.com/wiki/Water_Release:_Great_Waterfall_Technique) | A | Offensive | `narutorpg:water_release_great_waterfall_technique` (existing) |
| [Water Release: Great Whirlpool Enfolding Technique](https://naruto.fandom.com/wiki/Water_Release:_Great_Whirlpool_Enfolding_Technique) | C | Offensive | `narutorpg:water_release_great_whirlpool_enfolding_technique` (existing) |
| [Water Release: Gunshot](https://naruto.fandom.com/wiki/Water_Release:_Gunshot) | C | Offensive | `narutorpg:water_release_gunshot` (existing) |
| [Water Release: Hail](https://naruto.fandom.com/wiki/Water_Release:_Hail) | C | Offensive | `narutorpg:water_release_hail` (existing) |
| [Water Release: Hail Barrage](https://naruto.fandom.com/wiki/Water_Release:_Hail_Barrage) | C | Offensive | `narutorpg:water_release_hail_barrage` (existing) |
| [Water Release: Hard Whirlpool Water Blade](https://naruto.fandom.com/wiki/Water_Release:_Hard_Whirlpool_Water_Blade) | C | Offensive | `narutorpg:water_release_hard_whirlpool_water_blade` (existing) |
| [Water Release: Hiding in Drizzle Technique](https://naruto.fandom.com/wiki/Water_Release:_Hiding_in_Drizzle_Technique) | D | Supplementary | `narutorpg:water_release_hiding_in_drizzle_technique` (existing) |
| [Water Release: Hiding in Rain Technique](https://naruto.fandom.com/wiki/Water_Release:_Hiding_in_Rain_Technique) | D | Supplementary | `narutorpg:water_release_hiding_in_rain_technique` (existing) |
| [Water Release: Hiding in Water Technique](https://naruto.fandom.com/wiki/Water_Release:_Hiding_in_Water_Technique) | D | Supplementary | `narutorpg:water_release_hiding_in_water_technique` (existing) |
| [Water Release: Hydrification Exploding Funeral Technique](https://naruto.fandom.com/wiki/Water_Release:_Hydrification_Exploding_Funeral_Technique) | C | Offensive | `narutorpg:water_release_hydrification_exploding_funeral_technique` (existing) |
| [Water Release: Imminent Danger](https://naruto.fandom.com/wiki/Water_Release:_Imminent_Danger) | C | Supplementary, Offensive | `narutorpg:water_release_imminent_danger` (existing) |
| [Water Release: Infinite Sharks](https://naruto.fandom.com/wiki/Water_Release:_Infinite_Sharks) | C | Offensive | `narutorpg:water_release_infinite_sharks` (existing) |
| [Water Release: Jadeite](https://naruto.fandom.com/wiki/Water_Release:_Jadeite) | C | Offensive | `narutorpg:water_release_jadeite` (existing) |
| [Water Release: Large Projectile](https://naruto.fandom.com/wiki/Water_Release:_Large_Projectile) | C | Offensive | `narutorpg:water_release_large_projectile` (existing) |
| [Water Release: Lotus Light Rupture](https://naruto.fandom.com/wiki/Water_Release:_Lotus_Light_Rupture) | C | Offensive | `narutorpg:water_release_lotus_light_rupture` (existing) |
| [Water Release: Mouth Shot](https://naruto.fandom.com/wiki/Water_Release:_Mouth_Shot) | C | Offensive | `narutorpg:water_release_mouth_shot` (existing) |
| [Water Release: Multiple Water Spirit Wave](https://naruto.fandom.com/wiki/Water_Release:_Multiple_Water_Spirit_Wave) | D | - | `narutorpg:water_release_multiple_water_spirit_wave` (existing) |
| [Water Release: Pillar Encirclement](https://naruto.fandom.com/wiki/Water_Release:_Pillar_Encirclement) | D | Defensive | `narutorpg:water_release_pillar_encirclement` (existing) |
| [Water Release: Raging Jellyfish](https://naruto.fandom.com/wiki/Water_Release:_Raging_Jellyfish) | C | Offensive | `narutorpg:water_release_raging_jellyfish` (existing) |
| [Water Release: Rampaging Water](https://naruto.fandom.com/wiki/Water_Release:_Rampaging_Water) | C | Offensive | `narutorpg:water_release_rampaging_water` (existing) |
| [Water Release: Rapid Thunder Whip Technique](https://naruto.fandom.com/wiki/Water_Release:_Rapid_Thunder_Whip_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_rapid_thunder_whip_technique` (existing) |
| [Water Release: Rising Water Slicer](https://naruto.fandom.com/wiki/Water_Release:_Rising_Water_Slicer) | C | Offensive | `narutorpg:water_release_rising_water_slicer` (existing) |
| [Water Release: Running Water Spear](https://naruto.fandom.com/wiki/Water_Release:_Running_Water_Spear) | C | Offensive | `narutorpg:water_release_running_water_spear` (existing) |
| [Water Release: Running Water Whip](https://naruto.fandom.com/wiki/Water_Release:_Running_Water_Whip) | C | Offensive | `narutorpg:water_release_running_water_whip` (existing) |
| [Water Release: Scattering Bullets Technique](https://naruto.fandom.com/wiki/Water_Release:_Scattering_Bullets_Technique) | C | Offensive | `narutorpg:water_release_scattering_bullets_technique` (existing) |
| [Water Release: Snake's Mouth](https://naruto.fandom.com/wiki/Water_Release:_Snake's_Mouth) | C | Offensive | `narutorpg:water_release_snake_s_mouth` (existing) |
| [Water Release: Starch Syrup Capturing Field](https://naruto.fandom.com/wiki/Water_Release:_Starch_Syrup_Capturing_Field) | C | Supplementary | `narutorpg:water_release_starch_syrup_capturing_field` (existing) |
| [Water Release: Stormy Blockade](https://naruto.fandom.com/wiki/Water_Release:_Stormy_Blockade) | C | Offensive | `narutorpg:water_release_stormy_blockade` (existing) |
| [Water Release: Submerged Shark Scraping Destruction](https://naruto.fandom.com/wiki/Water_Release:_Submerged_Shark_Scraping_Destruction) | C | Offensive | `narutorpg:water_release_submerged_shark_scraping_destruction` (existing) |
| [Water Release: Surfing Strike](https://naruto.fandom.com/wiki/Water_Release:_Surfing_Strike) | C | Offensive | `narutorpg:water_release_surfing_strike` (existing) |
| [Water Release: Surging Sea](https://naruto.fandom.com/wiki/Water_Release:_Surging_Sea) | C | Offensive | `narutorpg:water_release_surging_sea` (existing) |
| [Water Release: Tate Eboshi](https://naruto.fandom.com/wiki/Water_Release:_Tate_Eboshi) | C | Offensive, Defensive | `narutorpg:water_release_tate_eboshi` (existing) |
| [Water Release: Tearing Torrent](https://naruto.fandom.com/wiki/Water_Release:_Tearing_Torrent) | C | Offensive, Defensive | `narutorpg:water_release_tearing_torrent` (existing) |
| [Water Release: Torrent Bullet](https://naruto.fandom.com/wiki/Water_Release:_Torrent_Bullet) | C | Offensive | `narutorpg:water_release_torrent_bullet` (existing) |
| [Water Release: Transform-at-Will](https://naruto.fandom.com/wiki/Water_Release:_Transform-at-Will) | C | Offensive, Supplementary | `narutorpg:water_release_transform_at_will` (existing) |
| [Water Release: Water Armour](https://naruto.fandom.com/wiki/Water_Release:_Water_Armour) | C | Offensive, Defensive | `narutorpg:water_release_water_armour` (existing) |
| [Water Release: Water Army Water Demon Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Army_Water_Demon_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_water_army_water_demon_technique` (existing) |
| [Water Release: Water Beast](https://naruto.fandom.com/wiki/Water_Release:_Water_Beast) | C | Offensive | `narutorpg:water_release_water_beast` (existing) |
| [Water Release: Water Binding](https://naruto.fandom.com/wiki/Water_Release:_Water_Binding) | C | Offensive, Supplementary | `narutorpg:water_release_water_binding` (existing) |
| [Water Release: Water Blade Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Blade_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_water_blade_technique` (existing) |
| [Water Release: Water Body](https://naruto.fandom.com/wiki/Water_Release:_Water_Body) | D | Supplementary, Defensive | `narutorpg:water_release_water_body` (existing) |
| [Water Release: Water Bowl](https://naruto.fandom.com/wiki/Water_Release:_Water_Bowl) | C | Offensive | `narutorpg:water_release_water_bowl` (existing) |
| [Water Release: Water Buckshot](https://naruto.fandom.com/wiki/Water_Release:_Water_Buckshot) | C | Offensive | `narutorpg:water_release_water_buckshot` (existing) |
| [Water Release: Water Bullet Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Bullet_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_water_bullet_technique` (existing) |
| [Water Release: Water Colliding Wave](https://naruto.fandom.com/wiki/Water_Release:_Water_Colliding_Wave) | C | Offensive, Supplementary | `narutorpg:water_release_water_colliding_wave` (existing) |
| [Water Release: Water Current Bridge](https://naruto.fandom.com/wiki/Water_Release:_Water_Current_Bridge) | D | Supplementary | `narutorpg:water_release_water_current_bridge` (existing) |
| [Water Release: Water Dragon Biting Explosion](https://naruto.fandom.com/wiki/Water_Release:_Water_Dragon_Biting_Explosion) | C | Offensive | `narutorpg:water_release_water_dragon_biting_explosion` (existing) |
| [Water Release: Water Dragon Bullet Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Dragon_Bullet_Technique) | B | Offensive | `narutorpg:water_release_water_dragon_bullet_technique` (existing) |
| [Water Release: Water Dragon Shot](https://naruto.fandom.com/wiki/Water_Release:_Water_Dragon_Shot) | C | Offensive | `narutorpg:water_release_water_dragon_shot` (existing) |
| [Water Release: Water Dragon Triple Step Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Dragon_Triple_Step_Technique) | C | Offensive | `narutorpg:water_release_water_dragon_triple_step_technique` (existing) |
| [Water Release: Water Dragon Whip](https://naruto.fandom.com/wiki/Water_Release:_Water_Dragon_Whip) | C | Offensive | `narutorpg:water_release_water_dragon_whip` (existing) |
| [Water Release: Water Dragon Whirlpool Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Dragon_Whirlpool_Technique) | C | Offensive | `narutorpg:water_release_water_dragon_whirlpool_technique` (existing) |
| [Water Release: Water Fang Bullet](https://naruto.fandom.com/wiki/Water_Release:_Water_Fang_Bullet) | B | Offensive | `narutorpg:water_release_water_fang_bullet` (existing) |
| [Water Release: Water Forge Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Forge_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_water_forge_technique` (existing) |
| [Water Release: Water Formation Pillar](https://naruto.fandom.com/wiki/Water_Release:_Water_Formation_Pillar) | D | Defensive | `narutorpg:water_release_water_formation_pillar` (existing) |
| [Water Release: Water Formation Wall](https://naruto.fandom.com/wiki/Water_Release:_Water_Formation_Wall) | B | Defensive | `narutorpg:water_release_water_formation_wall` (existing) |
| [Water Release: Water Gun](https://naruto.fandom.com/wiki/Water_Release:_Water_Gun) | B | Offensive | `narutorpg:water_release_water_gun` (existing) |
| [Water Release: Water Hail](https://naruto.fandom.com/wiki/Water_Release:_Water_Hail) | C | Offensive | `narutorpg:water_release_water_hail` (existing) |
| [Water Release: Water Lock Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Lock_Technique) | C | Offensive | `narutorpg:water_release_water_lock_technique` (existing) |
| [Water Release: Water Mass Crumbling Bullet](https://naruto.fandom.com/wiki/Water_Release:_Water_Mass_Crumbling_Bullet) | C | Offensive | `narutorpg:water_release_water_mass_crumbling_bullet` (existing) |
| [Water Release: Water Mirror Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Mirror_Technique) | C | Offensive, Defensive | `narutorpg:water_release_water_mirror_technique` (existing) |
| [Water Release: Water Palm Wave](https://naruto.fandom.com/wiki/Water_Release:_Water_Palm_Wave) | C | Offensive | `narutorpg:water_release_water_palm_wave` (existing) |
| [Water Release: Water Pillar](https://naruto.fandom.com/wiki/Water_Release:_Water_Pillar) | D | Supplementary | `narutorpg:water_release_water_pillar` (existing) |
| [Water Release: Water Pillar Method Attack](https://naruto.fandom.com/wiki/Water_Release:_Water_Pillar_Method_Attack) | C | Offensive | `narutorpg:water_release_water_pillar_method_attack` (existing) |
| [Water Release: Water Quaking Pillar](https://naruto.fandom.com/wiki/Water_Release:_Water_Quaking_Pillar) | C | Offensive, Defensive | `narutorpg:water_release_water_quaking_pillar` (existing) |
| [Water Release: Water Scales](https://naruto.fandom.com/wiki/Water_Release:_Water_Scales) | C | Offensive | `narutorpg:water_release_water_scales` (existing) |
| [Water Release: Water Severing Slash](https://naruto.fandom.com/wiki/Water_Release:_Water_Severing_Slash) | C | Offensive | `narutorpg:water_release_water_severing_slash` (existing) |
| [Water Release: Water Severing Wave](https://naruto.fandom.com/wiki/Water_Release:_Water_Severing_Wave) | C | Offensive | `narutorpg:water_release_water_severing_wave` (existing) |
| [Water Release: Water Shark Blade](https://naruto.fandom.com/wiki/Water_Release:_Water_Shark_Blade) | C | Offensive | `narutorpg:water_release_water_shark_blade` (existing) |
| [Water Release: Water Shark Breakthrough](https://naruto.fandom.com/wiki/Water_Release:_Water_Shark_Breakthrough) | C | Offensive, Defensive | `narutorpg:water_release_water_shark_breakthrough` (existing) |
| [Water Release: Water Shark Bullet Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Shark_Bullet_Technique) | B | Offensive | `narutorpg:water_release_water_shark_bullet_technique` (existing) |
| [Water Release: Water Shark Shot Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Shark_Shot_Technique) | C | Offensive | `narutorpg:water_release_water_shark_shot_technique` (existing) |
| [Water Release: Water Shark Wild Sphere Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Shark_Wild_Sphere_Technique) | A | Offensive | `narutorpg:water_release_water_shark_wild_sphere_technique` (existing) |
| [Water Release: Water Shield](https://naruto.fandom.com/wiki/Water_Release:_Water_Shield) | C | Offensive, Defensive | `narutorpg:water_release_water_shield` (existing) |
| [Water Release: Water Shuriken](https://naruto.fandom.com/wiki/Water_Release:_Water_Shuriken) | C | Offensive | `narutorpg:water_release_water_shuriken` (existing) |
| [Water Release: Water Snake Threads](https://naruto.fandom.com/wiki/Water_Release:_Water_Snake_Threads) | D | Supplementary | `narutorpg:water_release_water_snake_threads` (existing) |
| [Water Release: Water Spikes](https://naruto.fandom.com/wiki/Water_Release:_Water_Spikes) | C | Offensive, Supplementary | `narutorpg:water_release_water_spikes` (existing) |
| [Water Release: Water Spirit Wave](https://naruto.fandom.com/wiki/Water_Release:_Water_Spirit_Wave) | C | Offensive | `narutorpg:water_release_water_spirit_wave` (existing) |
| [Water Release: Water Spring Rush](https://naruto.fandom.com/wiki/Water_Release:_Water_Spring_Rush) | C | Offensive | `narutorpg:water_release_water_spring_rush` (existing) |
| [Water Release: Water Suicide Bomb](https://naruto.fandom.com/wiki/Water_Release:_Water_Suicide_Bomb) | D | Supplementary | `narutorpg:water_release_water_suicide_bomb` (existing) |
| [Water Release: Water Trumpet](https://naruto.fandom.com/wiki/Water_Release:_Water_Trumpet) | C | Offensive | `narutorpg:water_release_water_trumpet` (existing) |
| [Water Release: Water Weapons](https://naruto.fandom.com/wiki/Water_Release:_Water_Weapons) | C | Offensive, Defensive | `narutorpg:water_release_water_weapons` (existing) |
| [Water Release: Water Whip](https://naruto.fandom.com/wiki/Water_Release:_Water_Whip) | C | Offensive, Supplementary | `narutorpg:water_release_water_whip` (existing) |
| [Water Release: Waterfall Basin Technique](https://naruto.fandom.com/wiki/Water_Release:_Waterfall_Basin_Technique) | C | Offensive, Supplementary | `narutorpg:water_release_waterfall_basin_technique` (existing) |
| [Water Release: Waterfall Prison](https://naruto.fandom.com/wiki/Water_Release:_Waterfall_Prison) | C | Offensive, Defensive | `narutorpg:water_release_waterfall_prison` (existing) |
| [Water Release: Water–Heavens Convergence](https://naruto.fandom.com/wiki/Water_Release:_Water–Heavens_Convergence) | D | Defensive, Supplementary | `narutorpg:water_release_waterheavens_convergence` (existing) |
| [Water Release: Wild Bubble Wave](https://naruto.fandom.com/wiki/Water_Release:_Wild_Bubble_Wave) | C | Offensive, Supplementary | `narutorpg:water_release_wild_bubble_wave` (existing) |
| [Water Release: Wild Water Wave](https://naruto.fandom.com/wiki/Water_Release:_Wild_Water_Wave) | C | Offensive | `narutorpg:water_release_wild_water_wave` (existing) |
| [Water Replacement](https://naruto.fandom.com/wiki/Water_Replacement) | D | Defensive, Supplementary | `narutorpg:water_replacement` (existing) |
| [Water Retain](https://naruto.fandom.com/wiki/Water_Retain) | A | Offensive | `narutorpg:water_retain` (existing) |
| [Water Vortex Thunderclap](https://naruto.fandom.com/wiki/Water_Vortex_Thunderclap) | C | Offensive | `narutorpg:water_vortex_thunderclap` (existing) |
| [Waterfall Binding Sand](https://naruto.fandom.com/wiki/Waterfall_Binding_Sand) | C | Offensive | `narutorpg:waterfall_binding_sand` |
| [Wave Break](https://naruto.fandom.com/wiki/Wave_Break) | C | Offensive | `narutorpg:wave_break` (existing) |
| [Wave Transmission Technique](https://naruto.fandom.com/wiki/Wave_Transmission_Technique) | D | Supplementary | `narutorpg:wave_transmission_technique` (existing) |
| [Waving Pursuit Sand](https://naruto.fandom.com/wiki/Waving_Pursuit_Sand) | C | Offensive | `narutorpg:waving_pursuit_sand` |
| [Waving Sand Waterfall Spread](https://naruto.fandom.com/wiki/Waving_Sand_Waterfall_Spread) | C | Offensive | `narutorpg:waving_sand_waterfall_spread` |
| [Weapon Reverse Control Technique](https://naruto.fandom.com/wiki/Weapon_Reverse_Control_Technique) | D | Defensive | `narutorpg:weapon_reverse_control_technique` |
| [Weather Manipulation](https://naruto.fandom.com/wiki/Weather_Manipulation) | C | Offensive, Defensive, Supplementary | `narutorpg:weather_manipulation` (existing) |
| [Welcoming Approach: Thousand-Armed Murder](https://naruto.fandom.com/wiki/Welcoming_Approach:_Thousand-Armed_Murder) | C | Offensive, Defensive | `narutorpg:welcoming_approach_thousand_armed_murder` |
| [Whac-A-Mole Technique](https://naruto.fandom.com/wiki/Whac-A-Mole_Technique) | C | Offensive, Supplementary | `narutorpg:whac_a_mole_technique` (existing) |
| [Whirling Snake Tail](https://naruto.fandom.com/wiki/Whirling_Snake_Tail) | A | Offensive | `narutorpg:whirling_snake_tail` (existing) |
| [Whirlwind Drop](https://naruto.fandom.com/wiki/Whirlwind_Drop) | C | Offensive | `narutorpg:whirlwind_drop` (existing) |
| [Whirlwind Gun](https://naruto.fandom.com/wiki/Whirlwind_Gun) | C | Offensive | `narutorpg:whirlwind_gun` |
| [Whirlwind Pinching Wall](https://naruto.fandom.com/wiki/Whirlwind_Pinching_Wall) | C | Offensive | `narutorpg:whirlwind_pinching_wall` (existing) |
| [Whirlwind Wild Strike](https://naruto.fandom.com/wiki/Whirlwind_Wild_Strike) | C | Offensive | `narutorpg:whirlwind_wild_strike` (existing) |
| [White Secret Technique Secret Art: Collection of Illusions](https://naruto.fandom.com/wiki/White_Secret_Technique_Secret_Art:_Collection_of_Illusions) | C | Offensive | `narutorpg:white_secret_technique_secret_art_collection_of_illusions` |
| [White Secret Technique: The Chikamatsu Collection of Ten Puppets](https://naruto.fandom.com/wiki/White_Secret_Technique:_The_Chikamatsu_Collection_of_Ten_Puppets) | S | Offensive, Defensive, Supplementary | `narutorpg:white_secret_technique_the_chikamatsu_collection_of_ten_puppets` |
| [White Secret Technique: The Chikamatsu Collection of Ten Puppets: Earth Attack](https://naruto.fandom.com/wiki/White_Secret_Technique:_The_Chikamatsu_Collection_of_Ten_Puppets:_Earth_Attack) | C | Offensive, Defensive, Supplementary | `narutorpg:white_secret_technique_the_chikamatsu_collection_of_ten_puppets_earth_attack` |
| [White Secret Technique: The Chikamatsu Collection of Ten Puppets: Heaven Attack](https://naruto.fandom.com/wiki/White_Secret_Technique:_The_Chikamatsu_Collection_of_Ten_Puppets:_Heaven_Attack) | C | Offensive | `narutorpg:white_secret_technique_the_chikamatsu_collection_of_ten_puppets_heaven_attack` |
| [White Snake](https://naruto.fandom.com/wiki/White_Snake) | S | Offensive | `narutorpg:white_snake` |
| [White Snake Charmer Technique](https://naruto.fandom.com/wiki/White_Snake_Charmer_Technique) | C | Offensive | `narutorpg:white_snake_charmer_technique` |
| [White Snake Possession](https://naruto.fandom.com/wiki/White_Snake_Possession) | D | Supplementary | `narutorpg:white_snake_possession` |
| [White: Assimilation: Poison Mist](https://naruto.fandom.com/wiki/White:_Assimilation:_Poison_Mist) | C | Offensive | `narutorpg:white_assimilation_poison_mist` |
| [Whole Body One Shot](https://naruto.fandom.com/wiki/Whole_Body_One_Shot) | C | Offensive | `narutorpg:whole_body_one_shot` (existing) |
| [Wide Healing](https://naruto.fandom.com/wiki/Wide_Healing) | D | Supplementary | `narutorpg:wide_healing` |
| [Wide Range Bombing](https://naruto.fandom.com/wiki/Wide_Range_Bombing) | B | Offensive | `narutorpg:wide_range_bombing` (existing) |
| [Wild Dance](https://naruto.fandom.com/wiki/Wild_Dance) | C | Offensive | `narutorpg:wild_dance` |
| [Wild Lion's Mane Technique](https://naruto.fandom.com/wiki/Wild_Lion's_Mane_Technique) | B | Offensive, Supplementary | `narutorpg:wild_lion_s_mane_technique` |
| [Wild Serpent](https://naruto.fandom.com/wiki/Wild_Serpent) | C | Offensive | `narutorpg:wild_serpent` (existing) |
| [Will Materialisation](https://naruto.fandom.com/wiki/Will_Materialisation) | D | Supplementary | `narutorpg:will_materialisation` |
| [Will of Fire: Traditions](https://naruto.fandom.com/wiki/Will_of_Fire:_Traditions) | C | Offensive | `narutorpg:will_of_fire_traditions` (existing) |
| [Wind Cutter Technique](https://naruto.fandom.com/wiki/Wind_Cutter_Technique) | D | Defensive | `narutorpg:wind_cutter_technique` (existing) |
| [Wind Cyclone of Lightning](https://naruto.fandom.com/wiki/Wind_Cyclone_of_Lightning) | S | Offensive | `narutorpg:wind_cyclone_of_lightning` (existing) |
| [Wind Demon Rotating Special Attack Bullet](https://naruto.fandom.com/wiki/Wind_Demon_Rotating_Special_Attack_Bullet) | S | Offensive | `narutorpg:wind_demon_rotating_special_attack_bullet` |
| [Wind Demon Special Attack Bullet](https://naruto.fandom.com/wiki/Wind_Demon_Special_Attack_Bullet) | C | Offensive | `narutorpg:wind_demon_special_attack_bullet` |
| [Wind Demon Special Attack Transformation](https://naruto.fandom.com/wiki/Wind_Demon_Special_Attack_Transformation) | C | Offensive, Defensive | `narutorpg:wind_demon_special_attack_transformation` |
| [Wind Dust Violent Wave](https://naruto.fandom.com/wiki/Wind_Dust_Violent_Wave) | C | Offensive | `narutorpg:wind_dust_violent_wave` (existing) |
| [Wind God and Thunder God Technique](https://naruto.fandom.com/wiki/Wind_God_and_Thunder_God_Technique) | B | Offensive | `narutorpg:wind_god_and_thunder_god_technique` (existing) |
| [Wind Release Trap](https://naruto.fandom.com/wiki/Wind_Release_Trap) | C | Offensive | `narutorpg:wind_release_trap` |
| [Wind Release: Air Bullets](https://naruto.fandom.com/wiki/Wind_Release:_Air_Bullets) | C | Offensive | `narutorpg:wind_release_air_bullets` (existing) |
| [Wind Release: Air Current Wild Dance](https://naruto.fandom.com/wiki/Wind_Release:_Air_Current_Wild_Dance) | D | Supplementary | `narutorpg:wind_release_air_current_wild_dance` (existing) |
| [Wind Release: Breakthrough](https://naruto.fandom.com/wiki/Wind_Release:_Breakthrough) | C | Offensive, Supplementary | `narutorpg:wind_release_breakthrough` (existing) |
| [Wind Release: Bursting Compressed Air](https://naruto.fandom.com/wiki/Wind_Release:_Bursting_Compressed_Air) | C | Offensive | `narutorpg:wind_release_bursting_compressed_air` (existing) |
| [Wind Release: Cast Net](https://naruto.fandom.com/wiki/Wind_Release:_Cast_Net) | B | Offensive | `narutorpg:wind_release_cast_net` (existing) |
| [Wind Release: Divine Mountain Wind](https://naruto.fandom.com/wiki/Wind_Release:_Divine_Mountain_Wind) | C | Offensive, Supplementary | `narutorpg:wind_release_divine_mountain_wind` (existing) |
| [Wind Release: Divine Wind](https://naruto.fandom.com/wiki/Wind_Release:_Divine_Wind) | C | Offensive | `narutorpg:wind_release_divine_wind` (existing) |
| [Wind Release: Dragon Blade Rasengan](https://naruto.fandom.com/wiki/Wind_Release:_Dragon_Blade_Rasengan) | C | Offensive | `narutorpg:wind_release_dragon_blade_rasengan` (existing) |
| [Wind Release: Drilling Air Bullet](https://naruto.fandom.com/wiki/Wind_Release:_Drilling_Air_Bullet) | C | Offensive | `narutorpg:wind_release_drilling_air_bullet` (existing) |
| [Wind Release: Drilling Air Bullet Pellet](https://naruto.fandom.com/wiki/Wind_Release:_Drilling_Air_Bullet_Pellet) | C | Offensive, Defensive | `narutorpg:wind_release_drilling_air_bullet_pellet` (existing) |
| [Wind Release: Dust Cloud Technique](https://naruto.fandom.com/wiki/Wind_Release:_Dust_Cloud_Technique) | C | Offensive | `narutorpg:wind_release_dust_cloud_technique` (existing) |
| [Wind Release: Flag Current](https://naruto.fandom.com/wiki/Wind_Release:_Flag_Current) | C | Offensive | `narutorpg:wind_release_flag_current` (existing) |
| [Wind Release: Flashing Fireworks](https://naruto.fandom.com/wiki/Wind_Release:_Flashing_Fireworks) | C | Offensive | `narutorpg:wind_release_flashing_fireworks` (existing) |
| [Wind Release: Flower Scattering Dance](https://naruto.fandom.com/wiki/Wind_Release:_Flower_Scattering_Dance) | C | Offensive | `narutorpg:wind_release_flower_scattering_dance` (existing) |
| [Wind Release: Flower Storm](https://naruto.fandom.com/wiki/Wind_Release:_Flower_Storm) | D | Supplementary | `narutorpg:wind_release_flower_storm` (existing) |
| [Wind Release: Flying Locust Wind](https://naruto.fandom.com/wiki/Wind_Release:_Flying_Locust_Wind) | D | Supplementary | `narutorpg:wind_release_flying_locust_wind` (existing) |
| [Wind Release: Gale Palm](https://naruto.fandom.com/wiki/Wind_Release:_Gale_Palm) | C | Offensive, Supplementary | `narutorpg:wind_release_gale_palm` (existing) |
| [Wind Release: Great Balloon](https://naruto.fandom.com/wiki/Wind_Release:_Great_Balloon) | C | Offensive, Supplementary | `narutorpg:wind_release_great_balloon` (existing) |
| [Wind Release: Great Breakthrough](https://naruto.fandom.com/wiki/Wind_Release:_Great_Breakthrough) | C | Offensive | `narutorpg:wind_release_great_breakthrough` (existing) |
| [Wind Release: Great Cast Net](https://naruto.fandom.com/wiki/Wind_Release:_Great_Cast_Net) | B | Offensive | `narutorpg:wind_release_great_cast_net` (existing) |
| [Wind Release: Great Explosion Flame](https://naruto.fandom.com/wiki/Wind_Release:_Great_Explosion_Flame) | C | Offensive | `narutorpg:wind_release_great_explosion_flame` (existing) |
| [Wind Release: Great Sickle Weasel Technique](https://naruto.fandom.com/wiki/Wind_Release:_Great_Sickle_Weasel_Technique) | C | Offensive | `narutorpg:wind_release_great_sickle_weasel_technique` (existing) |
| [Wind Release: Great Slashing Tornado](https://naruto.fandom.com/wiki/Wind_Release:_Great_Slashing_Tornado) | C | Offensive | `narutorpg:wind_release_great_slashing_tornado` (existing) |
| [Wind Release: Great Task of the Dragon](https://naruto.fandom.com/wiki/Wind_Release:_Great_Task_of_the_Dragon) | C | Offensive, Defensive | `narutorpg:wind_release_great_task_of_the_dragon` (existing) |
| [Wind Release: Great Wind Mask](https://naruto.fandom.com/wiki/Wind_Release:_Great_Wind_Mask) | C | Offensive | `narutorpg:wind_release_great_wind_mask` (existing) |
| [Wind Release: Great Wind Protective Wall](https://naruto.fandom.com/wiki/Wind_Release:_Great_Wind_Protective_Wall) | C | Offensive | `narutorpg:wind_release_great_wind_protective_wall` (existing) |
| [Wind Release: Heaven and Earth Net](https://naruto.fandom.com/wiki/Wind_Release:_Heaven_and_Earth_Net) | C | Offensive | `narutorpg:wind_release_heaven_and_earth_net` (existing) |
| [Wind Release: Infinite Sand Cloud — Great Breakthrough](https://naruto.fandom.com/wiki/Wind_Release:_Infinite_Sand_Cloud_—_Great_Breakthrough) | C | Offensive, Defensive | `narutorpg:wind_release_infinite_sand_cloud_great_breakthrough` (existing) |
| [Wind Release: Multi-Layered Gale](https://naruto.fandom.com/wiki/Wind_Release:_Multi-Layered_Gale) | D | Defensive | `narutorpg:wind_release_multi_layered_gale` (existing) |
| [Wind Release: Obscuring Paper](https://naruto.fandom.com/wiki/Wind_Release:_Obscuring_Paper) | D | Supplementary | `narutorpg:wind_release_obscuring_paper` (existing) |
| [Wind Release: Passing Typhoon](https://naruto.fandom.com/wiki/Wind_Release:_Passing_Typhoon) | D | Supplementary | `narutorpg:wind_release_passing_typhoon` (existing) |
| [Wind Release: Pressure Damage](https://naruto.fandom.com/wiki/Wind_Release:_Pressure_Damage) | B | Offensive | `narutorpg:wind_release_pressure_damage` (existing) |
| [Wind Release: Rasengan](https://naruto.fandom.com/wiki/Wind_Release:_Rasengan) | C | Offensive | `narutorpg:wind_release_rasengan` (existing) |
| [Wind Release: Rasenshuriken](https://naruto.fandom.com/wiki/Wind_Release:_Rasenshuriken) | S | Offensive | `narutorpg:wind_release_rasenshuriken` (existing) |
| [Wind Release: Repeated Rasenshuriken](https://naruto.fandom.com/wiki/Wind_Release:_Repeated_Rasenshuriken) | S | Offensive | `narutorpg:wind_release_repeated_rasenshuriken` (existing) |
| [Wind Release: Rotating Shuriken](https://naruto.fandom.com/wiki/Wind_Release:_Rotating_Shuriken) | C | Offensive | `narutorpg:wind_release_rotating_shuriken` (existing) |
| [Wind Release: Sand Buckshot](https://naruto.fandom.com/wiki/Wind_Release:_Sand_Buckshot) | B | Offensive, Supplementary | `narutorpg:wind_release_sand_buckshot` (existing) |
| [Wind Release: Sand Cloud Breakthrough](https://naruto.fandom.com/wiki/Wind_Release:_Sand_Cloud_Breakthrough) | D | Defensive | `narutorpg:wind_release_sand_cloud_breakthrough` (existing) |
| [Wind Release: Sand Cloud Whirlwind](https://naruto.fandom.com/wiki/Wind_Release:_Sand_Cloud_Whirlwind) | C | Offensive, Supplementary | `narutorpg:wind_release_sand_cloud_whirlwind` (existing) |
| [Wind Release: Scattering Blossoms](https://naruto.fandom.com/wiki/Wind_Release:_Scattering_Blossoms) | C | Offensive | `narutorpg:wind_release_scattering_blossoms` (existing) |
| [Wind Release: Shakujō Twister](https://naruto.fandom.com/wiki/Wind_Release:_Shakujō_Twister) | C | Offensive | `narutorpg:wind_release_shakujo_twister` (existing) |
| [Wind Release: Shield of the Wind Count](https://naruto.fandom.com/wiki/Wind_Release:_Shield_of_the_Wind_Count) | D | Defensive, Supplementary | `narutorpg:wind_release_shield_of_the_wind_count` (existing) |
| [Wind Release: Spiralling Barrier](https://naruto.fandom.com/wiki/Wind_Release:_Spiralling_Barrier) | D | Defensive | `narutorpg:wind_release_spiralling_barrier` (existing) |
| [Wind Release: Spiralling Wind Ball](https://naruto.fandom.com/wiki/Wind_Release:_Spiralling_Wind_Ball) | C | Offensive | `narutorpg:wind_release_spiralling_wind_ball` (existing) |
| [Wind Release: Sword of the Wind Count](https://naruto.fandom.com/wiki/Wind_Release:_Sword_of_the_Wind_Count) | C | Offensive | `narutorpg:wind_release_sword_of_the_wind_count` (existing) |
| [Wind Release: Toad Gun](https://naruto.fandom.com/wiki/Wind_Release:_Toad_Gun) | B | Offensive | `narutorpg:wind_release_toad_gun` (existing) |
| [Wind Release: Toad Oil Bullet](https://naruto.fandom.com/wiki/Wind_Release:_Toad_Oil_Bullet) | B | Offensive | `narutorpg:wind_release_toad_oil_bullet` (existing) |
| [Wind Release: Toad Oil Flame Bullet](https://naruto.fandom.com/wiki/Wind_Release:_Toad_Oil_Flame_Bullet) | B | Offensive | `narutorpg:wind_release_toad_oil_flame_bullet` (existing) |
| [Wind Release: Ultra-Big Ball Rasenshuriken](https://naruto.fandom.com/wiki/Wind_Release:_Ultra-Big_Ball_Rasenshuriken) | S | Offensive | `narutorpg:wind_release_ultra_big_ball_rasenshuriken` (existing) |
| [Wind Release: Vacuum Blade](https://naruto.fandom.com/wiki/Wind_Release:_Vacuum_Blade) | C | Offensive | `narutorpg:wind_release_vacuum_blade` (existing) |
| [Wind Release: Vacuum Gale: Single](https://naruto.fandom.com/wiki/Wind_Release:_Vacuum_Gale:_Single) | C | Offensive | `narutorpg:wind_release_vacuum_gale_single` (existing) |
| [Wind Release: Vacuum Great Sphere](https://naruto.fandom.com/wiki/Wind_Release:_Vacuum_Great_Sphere) | B | Offensive | `narutorpg:wind_release_vacuum_great_sphere` (existing) |
| [Wind Release: Vacuum Serial Waves](https://naruto.fandom.com/wiki/Wind_Release:_Vacuum_Serial_Waves) | B | Offensive | `narutorpg:wind_release_vacuum_serial_waves` (existing) |
| [Wind Release: Vacuum Sphere](https://naruto.fandom.com/wiki/Wind_Release:_Vacuum_Sphere) | B | Offensive | `narutorpg:wind_release_vacuum_sphere` (existing) |
| [Wind Release: Vacuum Wave](https://naruto.fandom.com/wiki/Wind_Release:_Vacuum_Wave) | B | Offensive | `narutorpg:wind_release_vacuum_wave` (existing) |
| [Wind Release: Verdant Mountain Gale](https://naruto.fandom.com/wiki/Wind_Release:_Verdant_Mountain_Gale) | C | Offensive | `narutorpg:wind_release_verdant_mountain_gale` (existing) |
| [Wind Release: Whirlwind Fist](https://naruto.fandom.com/wiki/Wind_Release:_Whirlwind_Fist) | C | Offensive | `narutorpg:wind_release_whirlwind_fist` (existing) |
| [Wind Release: Wind Cutter Technique](https://naruto.fandom.com/wiki/Wind_Release:_Wind_Cutter_Technique) | C | Offensive | `narutorpg:wind_release_wind_cutter_technique` (existing) |
| [Wind Return](https://naruto.fandom.com/wiki/Wind_Return) | D | Defensive | `narutorpg:wind_return` |
| [Wind Spider Technique](https://naruto.fandom.com/wiki/Wind_Spider_Technique) | D | Supplementary | `narutorpg:wind_spider_technique` (existing) |
| [Wind Water Wild Wave Technique](https://naruto.fandom.com/wiki/Wind_Water_Wild_Wave_Technique) | B | Offensive | `narutorpg:wind_water_wild_wave_technique` (existing) |
| [Wind Waves](https://naruto.fandom.com/wiki/Wind_Waves) | C | Offensive | `narutorpg:wind_waves` (existing) |
| [Wind and Lightning Release: Typhoon Lightning Spiral](https://naruto.fandom.com/wiki/Wind_and_Lightning_Release:_Typhoon_Lightning_Spiral) | B | Offensive | `narutorpg:wind_and_lightning_release_typhoon_lightning_spiral` (existing) |
| [Winding Smoke Technique](https://naruto.fandom.com/wiki/Winding_Smoke_Technique) | D | Supplementary | `narutorpg:winding_smoke_technique` |
| [Windmill](https://naruto.fandom.com/wiki/Windmill) | B | Offensive | `narutorpg:windmill` (existing) |
| [Windmill (Hanabi)](https://naruto.fandom.com/wiki/Windmill_(Hanabi)) | C | Offensive | `narutorpg:windmill_hanabi` |
| [Wing Beheading](https://naruto.fandom.com/wiki/Wing_Beheading) | C | Offensive | `narutorpg:wing_beheading` |
| [Wing Rotation](https://naruto.fandom.com/wiki/Wing_Rotation) | C | Offensive | `narutorpg:wing_rotation` |
| [Wire Bind](https://naruto.fandom.com/wiki/Wire_Bind) | C | Offensive, Supplementary | `narutorpg:wire_bind` |
| [Wire Cage](https://naruto.fandom.com/wiki/Wire_Cage) | D | Defensive | `narutorpg:wire_cage` |
| [Wisdom Wolf Decay](https://naruto.fandom.com/wiki/Wisdom_Wolf_Decay) | C | Offensive | `narutorpg:wisdom_wolf_decay` |
| [Wolf River](https://naruto.fandom.com/wiki/Wolf_River) | B | Offensive, Defensive | `narutorpg:wolf_river` (existing) |
| [Wood Clone Technique](https://naruto.fandom.com/wiki/Wood_Clone_Technique) | B | Supplementary | `narutorpg:wood_clone_technique` (existing) |
| [Wood Clone: Kick](https://naruto.fandom.com/wiki/Wood_Clone:_Kick) | B | Offensive, Defensive | `narutorpg:wood_clone_kick` (existing) |
| [Wood Pillar Climb](https://naruto.fandom.com/wiki/Wood_Pillar_Climb) | B | Offensive, Defensive | `narutorpg:wood_pillar_climb` (existing) |
| [Wood Pillar Sky Kick](https://naruto.fandom.com/wiki/Wood_Pillar_Sky_Kick) | B | Offensive, Defensive | `narutorpg:wood_pillar_sky_kick` (existing) |
| [Wood Pillar Spin Kick](https://naruto.fandom.com/wiki/Wood_Pillar_Spin_Kick) | B | Offensive, Defensive | `narutorpg:wood_pillar_spin_kick` (existing) |
| [Wood Release](https://naruto.fandom.com/wiki/Wood_Release) | B | - | `narutorpg:wood_release` (existing) |
| [Wood Release Secret Technique: Nativity of a World of Trees](https://naruto.fandom.com/wiki/Wood_Release_Secret_Technique:_Nativity_of_a_World_of_Trees) | B | Offensive, Defensive, Supplementary | `narutorpg:wood_release_secret_technique_nativity_of_a_world_of_trees` (existing) |
| [Wood Release: Adamantine Beckoning Tree](https://naruto.fandom.com/wiki/Wood_Release:_Adamantine_Beckoning_Tree) | B | Offensive, Defensive | `narutorpg:wood_release_adamantine_beckoning_tree` (existing) |
| [Wood Release: Adamantine Fist](https://naruto.fandom.com/wiki/Wood_Release:_Adamantine_Fist) | B | Offensive, Defensive | `narutorpg:wood_release_adamantine_fist` (existing) |
| [Wood Release: Advent of a World of Flowering Trees](https://naruto.fandom.com/wiki/Wood_Release:_Advent_of_a_World_of_Flowering_Trees) | B | Offensive, Supplementary | `narutorpg:wood_release_advent_of_a_world_of_flowering_trees` (existing) |
| [Wood Release: All Creation](https://naruto.fandom.com/wiki/Wood_Release:_All_Creation) | B | Offensive | `narutorpg:wood_release_all_creation` (existing) |
| [Wood Release: Beams](https://naruto.fandom.com/wiki/Wood_Release:_Beams) | B | Offensive | `narutorpg:wood_release_beams` (existing) |
| [Wood Release: Circular Pillar Prison Technique](https://naruto.fandom.com/wiki/Wood_Release:_Circular_Pillar_Prison_Technique) | B | Offensive | `narutorpg:wood_release_circular_pillar_prison_technique` (existing) |
| [Wood Release: Complete Capture](https://naruto.fandom.com/wiki/Wood_Release:_Complete_Capture) | B | Offensive | `narutorpg:wood_release_complete_capture` (existing) |
| [Wood Release: Cutting Technique](https://naruto.fandom.com/wiki/Wood_Release:_Cutting_Technique) | B | Offensive | `narutorpg:wood_release_cutting_technique` (existing) |
| [Wood Release: Cypress Nail](https://naruto.fandom.com/wiki/Wood_Release:_Cypress_Nail) | B | Offensive | `narutorpg:wood_release_cypress_nail` (existing) |
| [Wood Release: Firmament Splinter](https://naruto.fandom.com/wiki/Wood_Release:_Firmament_Splinter) | B | Offensive | `narutorpg:wood_release_firmament_splinter` (existing) |
| [Wood Release: Foo Dog Heads](https://naruto.fandom.com/wiki/Wood_Release:_Foo_Dog_Heads) | B | Supplementary | `narutorpg:wood_release_foo_dog_heads` (existing) |
| [Wood Release: Four-Pillar House Technique](https://naruto.fandom.com/wiki/Wood_Release:_Four-Pillar_House_Technique) | B | Supplementary | `narutorpg:wood_release_four_pillar_house_technique` (existing) |
| [Wood Release: Four-Pillar Prison Technique](https://naruto.fandom.com/wiki/Wood_Release:_Four-Pillar_Prison_Technique) | B | Supplementary | `narutorpg:wood_release_four_pillar_prison_technique` (existing) |
| [Wood Release: Great Forest Crumbling](https://naruto.fandom.com/wiki/Wood_Release:_Great_Forest_Crumbling) | B | Offensive | `narutorpg:wood_release_great_forest_crumbling` (existing) |
| [Wood Release: Great Forest Technique](https://naruto.fandom.com/wiki/Wood_Release:_Great_Forest_Technique) | B | Offensive, Defensive, Supplementary | `narutorpg:wood_release_great_forest_technique` (existing) |
| [Wood Release: Great Spear Tree](https://naruto.fandom.com/wiki/Wood_Release:_Great_Spear_Tree) | B | Offensive | `narutorpg:wood_release_great_spear_tree` (existing) |
| [Wood Release: Great Tree Crumbling Attack](https://naruto.fandom.com/wiki/Wood_Release:_Great_Tree_Crumbling_Attack) | B | Offensive | `narutorpg:wood_release_great_tree_crumbling_attack` (existing) |
| [Wood Release: Great Tree Roaring Nativity](https://naruto.fandom.com/wiki/Wood_Release:_Great_Tree_Roaring_Nativity) | B | Offensive | `narutorpg:wood_release_great_tree_roaring_nativity` (existing) |
| [Wood Release: Great Wooden Mallet](https://naruto.fandom.com/wiki/Wood_Release:_Great_Wooden_Mallet) | B | Offensive | `narutorpg:wood_release_great_wooden_mallet` (existing) |
| [Wood Release: Hand Tool Manipulation](https://naruto.fandom.com/wiki/Wood_Release:_Hand_Tool_Manipulation) | B | Defensive, Supplementary | `narutorpg:wood_release_hand_tool_manipulation` (existing) |
| [Wood Release: Hotei Technique](https://naruto.fandom.com/wiki/Wood_Release:_Hotei_Technique) | B | Offensive, Defensive, Supplementary | `narutorpg:wood_release_hotei_technique` (existing) |
| [Wood Release: Hōbi Technique](https://naruto.fandom.com/wiki/Wood_Release:_Hōbi_Technique) | B | Defensive | `narutorpg:wood_release_hobi_technique` (existing) |
| [Wood Release: Kakashi](https://naruto.fandom.com/wiki/Wood_Release:_Kakashi) | B | Offensive | `narutorpg:wood_release_kakashi` (existing) |
| [Wood Release: Nativity of Trees](https://naruto.fandom.com/wiki/Wood_Release:_Nativity_of_Trees) | B | Offensive | `narutorpg:wood_release_nativity_of_trees` (existing) |
| [Wood Release: Nativity of a Sea of Trees](https://naruto.fandom.com/wiki/Wood_Release:_Nativity_of_a_Sea_of_Trees) | B | Supplementary | `narutorpg:wood_release_nativity_of_a_sea_of_trees` (existing) |
| [Wood Release: Pillar Crush](https://naruto.fandom.com/wiki/Wood_Release:_Pillar_Crush) | B | Offensive | `narutorpg:wood_release_pillar_crush` (existing) |
| [Wood Release: Revolt of Hell](https://naruto.fandom.com/wiki/Wood_Release:_Revolt_of_Hell) | B | Offensive | `narutorpg:wood_release_revolt_of_hell` (existing) |
| [Wood Release: Ruptured Earth](https://naruto.fandom.com/wiki/Wood_Release:_Ruptured_Earth) | B | Offensive, Supplementary | `narutorpg:wood_release_ruptured_earth` (existing) |
| [Wood Release: Serial Pillar Houses Technique](https://naruto.fandom.com/wiki/Wood_Release:_Serial_Pillar_Houses_Technique) | B | Supplementary | `narutorpg:wood_release_serial_pillar_houses_technique` (existing) |
| [Wood Release: Smothering Binding Technique](https://naruto.fandom.com/wiki/Wood_Release:_Smothering_Binding_Technique) | B | Offensive, Supplementary | `narutorpg:wood_release_smothering_binding_technique` (existing) |
| [Wood Release: Stocks](https://naruto.fandom.com/wiki/Wood_Release:_Stocks) | B | Supplementary | `narutorpg:wood_release_stocks` (existing) |
| [Wood Release: Thousand Core Piercing Technique](https://naruto.fandom.com/wiki/Wood_Release:_Thousand_Core_Piercing_Technique) | B | Offensive | `narutorpg:wood_release_thousand_core_piercing_technique` (existing) |
| [Wood Release: Transformation](https://naruto.fandom.com/wiki/Wood_Release:_Transformation) | B | Supplementary | `narutorpg:wood_release_transformation` (existing) |
| [Wood Release: Tree Bind Flourishing Burial](https://naruto.fandom.com/wiki/Wood_Release:_Tree_Bind_Flourishing_Burial) | B | Offensive, Supplementary | `narutorpg:wood_release_tree_bind_flourishing_burial` (existing) |
| [Wood Release: Tree of Flight](https://naruto.fandom.com/wiki/Wood_Release:_Tree_of_Flight) | B | Offensive, Defensive | `narutorpg:wood_release_tree_of_flight` (existing) |
| [Wood Release: Wild Picket Technique](https://naruto.fandom.com/wiki/Wood_Release:_Wild_Picket_Technique) | B | Offensive | `narutorpg:wood_release_wild_picket_technique` (existing) |
| [Wood Release: Wood Dragon Technique](https://naruto.fandom.com/wiki/Wood_Release:_Wood_Dragon_Technique) | B | Offensive, Defensive | `narutorpg:wood_release_wood_dragon_technique` (existing) |
| [Wood Release: Wood Fetters Technique](https://naruto.fandom.com/wiki/Wood_Release:_Wood_Fetters_Technique) | B | Offensive | `narutorpg:wood_release_wood_fetters_technique` (existing) |
| [Wood Release: Wood Human Technique](https://naruto.fandom.com/wiki/Wood_Release:_Wood_Human_Technique) | B | Offensive, Defensive | `narutorpg:wood_release_wood_human_technique` (existing) |
| [Wood Release: Wood Locking Wall](https://naruto.fandom.com/wiki/Wood_Release:_Wood_Locking_Wall) | B | Offensive, Defensive | `narutorpg:wood_release_wood_locking_wall` (existing) |
| [Wood Release: Wood Spikes Ring](https://naruto.fandom.com/wiki/Wood_Release:_Wood_Spikes_Ring) | B | Supplementary | `narutorpg:wood_release_wood_spikes_ring` (existing) |
| [Wood Release: Wooden Mallet](https://naruto.fandom.com/wiki/Wood_Release:_Wooden_Mallet) | B | Offensive | `narutorpg:wood_release_wooden_mallet` (existing) |
| [Wood Release: Wooden Spear Heavenly Tear](https://naruto.fandom.com/wiki/Wood_Release:_Wooden_Spear_Heavenly_Tear) | B | Offensive | `narutorpg:wood_release_wooden_spear_heavenly_tear` (existing) |
| [Wood Release: World of Trees Wall](https://naruto.fandom.com/wiki/Wood_Release:_World_of_Trees_Wall) | B | Defensive | `narutorpg:wood_release_world_of_trees_wall` (existing) |
| [Wood Stab Palm](https://naruto.fandom.com/wiki/Wood_Stab_Palm) | B | Offensive, Defensive | `narutorpg:wood_stab_palm` (existing) |
| [Wooden Men Possession Turnover Manipulation Technique](https://naruto.fandom.com/wiki/Wooden_Men_Possession_Turnover_Manipulation_Technique) | D | Supplementary | `narutorpg:wooden_men_possession_turnover_manipulation_technique` |
| [Wooden Puppet Manipulation Technique](https://naruto.fandom.com/wiki/Wooden_Puppet_Manipulation_Technique) | C | Offensive, Defensive, Supplementary | `narutorpg:wooden_puppet_manipulation_technique` |
| [Yachihoko no Kami](https://naruto.fandom.com/wiki/Yachihoko_no_Kami) | A | Offensive | `narutorpg:yachihoko_no_kami` |
| [Yachihoko: Dazzling Flames of Rebellion](https://naruto.fandom.com/wiki/Yachihoko:_Dazzling_Flames_of_Rebellion) | B | Offensive | `narutorpg:yachihoko_dazzling_flames_of_rebellion` (existing) |
| [Yakusaikazuchinokami](https://naruto.fandom.com/wiki/Yakusaikazuchinokami) | C | Offensive | `narutorpg:yakusaikazuchinokami` (existing) |
| [Yasaka Magatama](https://naruto.fandom.com/wiki/Yasaka_Magatama) | B | Offensive | `narutorpg:yasaka_magatama` (existing) |
| [Yasaka Magatama: Tailed Beast Noka](https://naruto.fandom.com/wiki/Yasaka_Magatama:_Tailed_Beast_Noka) | B | Offensive | `narutorpg:yasaka_magatama_tailed_beast_noka` (existing) |
| [Yellow Flash (Naruto)](https://naruto.fandom.com/wiki/Yellow_Flash_(Naruto)) | D | Supplementary | `narutorpg:yellow_flash_naruto` |
| [Yellow Flash (jutsu)](https://naruto.fandom.com/wiki/Yellow_Flash_(jutsu)) | A | Offensive | `narutorpg:yellow_flash_jutsu` |
| [Yellow Flash Rasengan](https://naruto.fandom.com/wiki/Yellow_Flash_Rasengan) | A | Offensive | `narutorpg:yellow_flash_rasengan` (existing) |
| [Yin Healing Wound Destruction](https://naruto.fandom.com/wiki/Yin_Healing_Wound_Destruction) | A | Supplementary | `narutorpg:yin_healing_wound_destruction` |
| [Yin–Yang Release (jutsu)](https://naruto.fandom.com/wiki/Yin–Yang_Release_(jutsu)) | A | Defensive | `narutorpg:yinyang_release_jutsu` (existing) |
| [Yomito's Transformation Technique](https://naruto.fandom.com/wiki/Yomito's_Transformation_Technique) | D | Supplementary | `narutorpg:yomito_s_transformation_technique` (existing) |
| [Yomotsu Hirasaka](https://naruto.fandom.com/wiki/Yomotsu_Hirasaka) | A | Supplementary | `narutorpg:yomotsu_hirasaka` (existing) |
| [Young Lightning](https://naruto.fandom.com/wiki/Young_Lightning) | C | Offensive | `narutorpg:young_lightning` (existing) |
| [Youth Roar](https://naruto.fandom.com/wiki/Youth_Roar) | D | Supplementary | `narutorpg:youth_roar` |
| [Zeus](https://naruto.fandom.com/wiki/Zeus) | C | Offensive | `narutorpg:zeus` (existing) |
| [Ōdachi Mow Down](https://naruto.fandom.com/wiki/Ōdachi_Mow_Down) | C | Offensive | `narutorpg:odachi_mow_down` (existing) |
| [Ōhirume](https://naruto.fandom.com/wiki/Ōhirume) | B | Supplementary, Offensive | `narutorpg:ohirume` (existing) |
| [Ōtsutsuki Teleportation](https://naruto.fandom.com/wiki/Ōtsutsuki_Teleportation) | S | Supplementary | `narutorpg:otsutsuki_teleportation` |
