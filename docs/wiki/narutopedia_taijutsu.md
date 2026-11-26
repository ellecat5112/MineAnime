# Narutopedia Taijutsu coverage

Source: https://naruto.fandom.com/wiki/Category:Taijutsu - crawled 2026-11-26. Method: list=categorymembers (recursive) on Category:Taijutsu; action=ask [[Jutsu classification::Taijutsu]] (Semantic MediaWiki drill-down) with infobox printouts; action=ask [[Jutsu classification::Fighting Style]] checked for pages outside the Taijutsu classification.

## Category tree

- `Category:Taijutsu`: 0 pages, 0 subcategories

`Category:Taijutsu` does not exist on Narutopedia (the API reports the category page as missing and it has no members). Narutopedia groups taijutsu only through the Semantic MediaWiki property **Jutsu classification = Taijutsu** (the "Classification" drill-down filter), so that grouping is the category for this crawl, per the project rule. Every page in it is covered below.

**657 taijutsu pages.** 475 are new abilities on the Taijutsu token; 182 already existed (clan, village or nature-release jutsu such as Leaf Hurricane, Primary Lotus or 10 Hit Combo keep their existing unlock route). None were excluded.

### Pages intentionally not turned into taijutsu

- The drill-down value **Fighting Style** also contains 31 pages that are not classified Taijutsu (ninjutsu styles such as Explosive Clay, Puppet Technique, Six Paths of Pain, Iaidō). They are fighting styles of other schools, not taijutsu, so they are out of scope here: Assassination Technique, Black Lightning, Bubblegum Ninjutsu, Butterfly Mode, Calorie Control, Character Bind Technique, Circular Formation, Dance of the Shikigami, Earth Grudge Fear, Enclosing Technique, Evernight, Explosive Clay, Flower Ninja Art, Four Legs Technique, Furious Smelting, Iaidō, Magnet Ninja Art, Mysterious Peacock Method, Parasitic Destruction Insect Technique, Puppet Technique, Reverse Scale, Sage Transformation, Samurai Sabre Technique, Sandstone Fist, Silent Killing, Six Paths of Pain, Soap Bubble Ninjutsu, Spider Sticky Gold, Super Beast Imitating Drawing, Uchiha Style Kenjutsu, Unsealing Technique.
- The article **Taijutsu** itself is a topic page, not a technique.

## How taijutsu play

- Every taijutsu sits on the **Taijutsu** technique item. Hold it while defeating enemies to raise its mastery; each mastery level unlocks the next taijutsu in rank order (rank still gates them). Pure taijutsu costs **stamina**; taijutsu that are also ninjutsu / senjutsu / chakra-flow techniques cost chakra.
- Each technique is matched to a move archetype from its name, with its own cast animation:

| archetype | matches | plays as | count |
|---|---|---|---:|
| special | Morning Peacock, Daytime Tiger, Evening Elephant, Night Guy, Front / Reverse Lotus, Leaf Hurricane, Dynamic Entry, Sixty-Four Palms, Revolving Heaven | hand-tuned | 6 |
| lotus | lotus | launch the target and follow with a multi-hit combo | 9 |
| gate | gates | Eight Gates power-up scaling with the gate number (strength, speed, resistance; hunger cost) | 3 |
| spin | rotation, whirlwind, hurricane, spin... | spinning burst knocking back everything around you | 33 |
| rise | rising, soaring, upper... | short dash that launches the target upward | 19 |
| drop | drop, heel, crash, slam, shatter... | dash + ground-shaking impact in front | 24 |
| barrage | barrage, combo, multiple, dance, wild... | dash with 4-6 rapid follow-up blows | 64 |
| charge | bullet, tank, rush, flash, swallow... | long charge through enemies | 28 |
| pressure | vacuum, air, gale, roar, wind... | air-pressure wave fired forward | 22 |
| slash | slash, blade, fang, claw, drill... | piercing close-range cut | 76 |
| grapple | throw, lasso, hold, embrace... | grab: pull in, slow and toss up | 11 |
| kick | kick, foot, leg, knee | kicking dash with heavy knockback | 14 |
| palm | punch, fist, palm, strike... | striking dash (plus a shockwave at rank A/S) | 159 |
| volley | shurikenjutsu | 5-shot volley | 4 |
| stance | defensive / supplementary | fighting stance buff (defence or mobility) | 3 |

## All taijutsu

| technique | rank (estimated) | class type | in game as |
|---|---|---|---|
| [10 Hit Combo](https://naruto.fandom.com/wiki/10_Hit_Combo) | C | Offensive | `narutorpg:10_hit_combo` (existing) |
| [100 Metre Punch](https://naruto.fandom.com/wiki/100_Metre_Punch) | C | Offensive | `narutorpg:100_metre_punch` |
| [100% Single Punch](https://naruto.fandom.com/wiki/100%_Single_Punch) | C | Offensive | `narutorpg:100_single_punch` |
| [1000 Metre Punch](https://naruto.fandom.com/wiki/1000_Metre_Punch) | C | Offensive | `narutorpg:1000_metre_punch` |
| [16 Hit Combo](https://naruto.fandom.com/wiki/16_Hit_Combo) | C | Offensive | `narutorpg:16_hit_combo` |
| [Absolute: Fang Passing Fang](https://naruto.fandom.com/wiki/Absolute:_Fang_Passing_Fang) | B | Offensive | `narutorpg:absolute_fang_passing_fang` |
| [Accelerated Armed Revolving Heaven](https://naruto.fandom.com/wiki/Accelerated_Armed_Revolving_Heaven) | S | Offensive | `narutorpg:accelerated_armed_revolving_heaven` |
| [Acrobat](https://naruto.fandom.com/wiki/Acrobat) | A | Offensive | `narutorpg:acrobat` |
| [Adamantine Power: Acala](https://naruto.fandom.com/wiki/Adamantine_Power:_Acala) | B | Offensive | `narutorpg:adamantine_power_acala` (existing) |
| [Aerobatic Strike](https://naruto.fandom.com/wiki/Aerobatic_Strike) | C | Offensive | `narutorpg:aerobatic_strike` |
| [Agonizing Thorn Technique (Simply Put a Kanchō)](https://naruto.fandom.com/wiki/Agonizing_Thorn_Technique_(Simply_Put_a_Kanchō)) | C | Offensive | `narutorpg:agonizing_thorn_technique_simply_put_a_kancho` (existing) |
| [Air Lightning Bullet](https://naruto.fandom.com/wiki/Air_Lightning_Bullet) | C | Offensive | `narutorpg:air_lightning_bullet` |
| [Air Lightning Strike](https://naruto.fandom.com/wiki/Air_Lightning_Strike) | C | Offensive | `narutorpg:air_lightning_strike` |
| [Alder](https://naruto.fandom.com/wiki/Alder) | B | Offensive | `narutorpg:alder` |
| [Allied Shinobi Forces Technique](https://naruto.fandom.com/wiki/Allied_Shinobi_Forces_Technique) | B | Offensive, Defensive, Supplementary | `narutorpg:allied_shinobi_forces_technique` (existing) |
| [Almighty Sakura](https://naruto.fandom.com/wiki/Almighty_Sakura) | C | Offensive | `narutorpg:almighty_sakura` |
| [Amputation Punishment](https://naruto.fandom.com/wiki/Amputation_Punishment) | C | Offensive | `narutorpg:amputation_punishment` |
| [Anger of the Sky and the Cherry Blossom and the Earth](https://naruto.fandom.com/wiki/Anger_of_the_Sky_and_the_Cherry_Blossom_and_the_Earth) | C | Offensive, Defensive | `narutorpg:anger_of_the_sky_and_the_cherry_blossom_and_the_earth` |
| [Annihilation](https://naruto.fandom.com/wiki/Annihilation) | A | Offensive | `narutorpg:annihilation` |
| [Apricot Blossom Droop](https://naruto.fandom.com/wiki/Apricot_Blossom_Droop) | C | Offensive | `narutorpg:apricot_blossom_droop` |
| [Arhat Fist](https://naruto.fandom.com/wiki/Arhat_Fist) | C | Offensive | `narutorpg:arhat_fist` |
| [Assimilation: Rock Tank](https://naruto.fandom.com/wiki/Assimilation:_Rock_Tank) | C | Offensive | `narutorpg:assimilation_rock_tank` (existing) |
| [Avalanche Dance](https://naruto.fandom.com/wiki/Avalanche_Dance) | C | Offensive | `narutorpg:avalanche_dance` |
| [Ballistic Waterwheel](https://naruto.fandom.com/wiki/Ballistic_Waterwheel) | C | Offensive, Defensive | `narutorpg:ballistic_waterwheel` (existing) |
| [Barrage of Gentle Fists](https://naruto.fandom.com/wiki/Barrage_of_Gentle_Fists) | B | Offensive | `narutorpg:barrage_of_gentle_fists` |
| [Baryon Bullet Barrage](https://naruto.fandom.com/wiki/Baryon_Bullet_Barrage) | C | Offensive | `narutorpg:baryon_bullet_barrage` |
| [Baryon: Scarlet Flash](https://naruto.fandom.com/wiki/Baryon:_Scarlet_Flash) | C | Offensive | `narutorpg:baryon_scarlet_flash` |
| [Beast-Human Fury Kicks](https://naruto.fandom.com/wiki/Beast-Human_Fury_Kicks) | C | Offensive | `narutorpg:beast_human_fury_kicks` (existing) |
| [Beheading Prison Chop](https://naruto.fandom.com/wiki/Beheading_Prison_Chop) | C | Offensive | `narutorpg:beheading_prison_chop` |
| [Big Ball Rasengan: Rising Attack](https://naruto.fandom.com/wiki/Big_Ball_Rasengan:_Rising_Attack) | C | Offensive | `narutorpg:big_ball_rasengan_rising_attack` |
| [Biting Cut](https://naruto.fandom.com/wiki/Biting_Cut) | C | Offensive | `narutorpg:biting_cut` |
| [Black Passing Secret Fang Technique Machine One Shot](https://naruto.fandom.com/wiki/Black_Passing_Secret_Fang_Technique_Machine_One_Shot) | B | Offensive | `narutorpg:black_passing_secret_fang_technique_machine_one_shot` |
| [Black Tornado](https://naruto.fandom.com/wiki/Black_Tornado) | B | Offensive | `narutorpg:black_tornado` |
| [Blade of Wind Barrage](https://naruto.fandom.com/wiki/Blade_of_Wind_Barrage) | C | Offensive | `narutorpg:blade_of_wind_barrage` (existing) |
| [Boil Release: Unrivalled Strength](https://naruto.fandom.com/wiki/Boil_Release:_Unrivalled_Strength) | B | Supplementary | `narutorpg:boil_release_unrivalled_strength` (existing) |
| [Bolting Blossom](https://naruto.fandom.com/wiki/Bolting_Blossom) | B | Offensive | `narutorpg:bolting_blossom` |
| [Bone Pellet](https://naruto.fandom.com/wiki/Bone_Pellet) | B | Offensive | `narutorpg:bone_pellet` |
| [Boruto Rush](https://naruto.fandom.com/wiki/Boruto_Rush) | C | Offensive | `narutorpg:boruto_rush` |
| [Boruto Strike](https://naruto.fandom.com/wiki/Boruto_Strike) | C | Offensive | `narutorpg:boruto_strike` (existing) |
| [Brace Yourself!](https://naruto.fandom.com/wiki/Brace_Yourself!) | C | Offensive | `narutorpg:brace_yourself` |
| [Branding Punishment](https://naruto.fandom.com/wiki/Branding_Punishment) | C | Offensive | `narutorpg:branding_punishment` |
| [Brilliant Fluttering Wings](https://naruto.fandom.com/wiki/Brilliant_Fluttering_Wings) | C | Offensive | `narutorpg:brilliant_fluttering_wings` |
| [Broadaxe](https://naruto.fandom.com/wiki/Broadaxe) | A | Offensive | `narutorpg:broadaxe` (existing) |
| [Bug Bite](https://naruto.fandom.com/wiki/Bug_Bite) | C | Offensive | `narutorpg:bug_bite` |
| [Burning Legs](https://naruto.fandom.com/wiki/Burning_Legs) | B | Offensive | `narutorpg:burning_legs` (existing) |
| [Burning Passion](https://naruto.fandom.com/wiki/Burning_Passion) | C | Offensive | `narutorpg:burning_passion` |
| [Burning Slap of Youth](https://naruto.fandom.com/wiki/Burning_Slap_of_Youth) | C | Offensive | `narutorpg:burning_slap_of_youth` |
| [Butterfly Bullet Bombing](https://naruto.fandom.com/wiki/Butterfly_Bullet_Bombing) | B | Offensive | `narutorpg:butterfly_bullet_bombing` (existing) |
| [By Brute Force](https://naruto.fandom.com/wiki/By_Brute_Force) | C | Offensive | `narutorpg:by_brute_force` |
| [Cat Chakra Claw](https://naruto.fandom.com/wiki/Cat_Chakra_Claw) | C | Offensive | `narutorpg:cat_chakra_claw` |
| [Cat Claw](https://naruto.fandom.com/wiki/Cat_Claw) | C | Offensive | `narutorpg:cat_claw` |
| [Cat Claw Foot](https://naruto.fandom.com/wiki/Cat_Claw_Foot) | C | Offensive | `narutorpg:cat_claw_foot` |
| [Cat Surprise](https://naruto.fandom.com/wiki/Cat_Surprise) | C | Supplementary | `narutorpg:cat_surprise` (existing) |
| [Cha Barrage](https://naruto.fandom.com/wiki/Cha_Barrage) | C | Offensive | `narutorpg:cha_barrage` |
| [Chain Pull](https://naruto.fandom.com/wiki/Chain_Pull) | A | Offensive | `narutorpg:chain_pull` |
| [Chain Rope](https://naruto.fandom.com/wiki/Chain_Rope) | A | Offensive | `narutorpg:chain_rope` |
| [Chained Heaven](https://naruto.fandom.com/wiki/Chained_Heaven) | A | Offensive | `narutorpg:chained_heaven` |
| [Chakra Enhanced Strength](https://naruto.fandom.com/wiki/Chakra_Enhanced_Strength) | C | Offensive | `narutorpg:chakra_enhanced_strength` |
| [Chaos Fan Dance](https://naruto.fandom.com/wiki/Chaos_Fan_Dance) | C | Offensive, Defensive | `narutorpg:chaos_fan_dance` |
| [Charge! Konohamaru Platoon](https://naruto.fandom.com/wiki/Charge!_Konohamaru_Platoon) | B | Offensive | `narutorpg:charge_konohamaru_platoon` |
| [Cherry Blossom Banner](https://naruto.fandom.com/wiki/Cherry_Blossom_Banner) | C | Offensive | `narutorpg:cherry_blossom_banner` |
| [Cherry Blossom Impact](https://naruto.fandom.com/wiki/Cherry_Blossom_Impact) | C | Offensive | `narutorpg:cherry_blossom_impact` |
| [Cherry Blossom Profusion Palm](https://naruto.fandom.com/wiki/Cherry_Blossom_Profusion_Palm) | C | Offensive | `narutorpg:cherry_blossom_profusion_palm` |
| [Cherry Blossom Tearing Impact](https://naruto.fandom.com/wiki/Cherry_Blossom_Tearing_Impact) | C | Offensive | `narutorpg:cherry_blossom_tearing_impact` |
| [Cherry Blossom Violent Impact](https://naruto.fandom.com/wiki/Cherry_Blossom_Violent_Impact) | C | Offensive | `narutorpg:cherry_blossom_violent_impact` |
| [Cherry Blossom Wild Dance: Chidori](https://naruto.fandom.com/wiki/Cherry_Blossom_Wild_Dance:_Chidori) | C | Offensive | `narutorpg:cherry_blossom_wild_dance_chidori` (existing) |
| [Chidori: Chakra Blade](https://naruto.fandom.com/wiki/Chidori:_Chakra_Blade) | C | Offensive | `narutorpg:chidori_chakra_blade` (existing) |
| [Chopping Punishment](https://naruto.fandom.com/wiki/Chopping_Punishment) | C | Offensive | `narutorpg:chopping_punishment` |
| [Chubby Go-Round](https://naruto.fandom.com/wiki/Chubby_Go-Round) | C | Offensive, Defensive | `narutorpg:chubby_go_round` (existing) |
| [Chubby Megaton](https://naruto.fandom.com/wiki/Chubby_Megaton) | C | Offensive, Defensive | `narutorpg:chubby_megaton` (existing) |
| [Chunk Slash](https://naruto.fandom.com/wiki/Chunk_Slash) | C | Offensive | `narutorpg:chunk_slash` |
| [Claw of Shukaku](https://naruto.fandom.com/wiki/Claw_of_Shukaku) | C | Offensive, Defensive | `narutorpg:claw_of_shukaku` |
| [Clawing Swift Wolf Fang](https://naruto.fandom.com/wiki/Clawing_Swift_Wolf_Fang) | B | Offensive, Defensive | `narutorpg:clawing_swift_wolf_fang` |
| [Clethra](https://naruto.fandom.com/wiki/Clethra) | B | Offensive | `narutorpg:clethra` |
| [Climbing Sparrow](https://naruto.fandom.com/wiki/Climbing_Sparrow) | C | Offensive | `narutorpg:climbing_sparrow` |
| [Clone Body Blow](https://naruto.fandom.com/wiki/Clone_Body_Blow) | A | Offensive | `narutorpg:clone_body_blow` |
| [Clone Combo](https://naruto.fandom.com/wiki/Clone_Combo) | C | Offensive | `narutorpg:clone_combo` |
| [Clone Ladder](https://naruto.fandom.com/wiki/Clone_Ladder) | C | Offensive | `narutorpg:clone_ladder` |
| [Clone Lasso](https://naruto.fandom.com/wiki/Clone_Lasso) | C | Offensive | `narutorpg:clone_lasso` |
| [Clone Spinning Heel Drop](https://naruto.fandom.com/wiki/Clone_Spinning_Heel_Drop) | C | Offensive | `narutorpg:clone_spinning_heel_drop` |
| [Cloud-Style Crescent Moon Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Crescent_Moon_Beheading) | C | Offensive | `narutorpg:cloud_style_crescent_moon_beheading` |
| [Cloud-Style Front Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Front_Beheading) | C | Offensive | `narutorpg:cloud_style_front_beheading` |
| [Cloud-Style Reverse Beheading](https://naruto.fandom.com/wiki/Cloud-Style_Reverse_Beheading) | C | Offensive | `narutorpg:cloud_style_reverse_beheading` |
| [Cloud-Style Rising Dragon](https://naruto.fandom.com/wiki/Cloud-Style_Rising_Dragon) | C | Offensive | `narutorpg:cloud_style_rising_dragon` |
| [Coiling Around](https://naruto.fandom.com/wiki/Coiling_Around) | C | Offensive | `narutorpg:coiling_around` |
| [Cold Needles](https://naruto.fandom.com/wiki/Cold_Needles) | C | Offensive | `narutorpg:cold_needles` |
| [Combination Secret Technique](https://naruto.fandom.com/wiki/Combination_Secret_Technique) | B | Offensive | `narutorpg:combination_secret_technique` |
| [Combo Attack: Shinra Tensei](https://naruto.fandom.com/wiki/Combo_Attack:_Shinra_Tensei) | B | Offensive | `narutorpg:combo_attack_shinra_tensei` |
| [Coming of Spring Kick](https://naruto.fandom.com/wiki/Coming_of_Spring_Kick) | C | Offensive | `narutorpg:coming_of_spring_kick` |
| [Concussive Chakra Cannon](https://naruto.fandom.com/wiki/Concussive_Chakra_Cannon) | C | Offensive | `narutorpg:concussive_chakra_cannon` |
| [Confusing Claw Instant Thrust](https://naruto.fandom.com/wiki/Confusing_Claw_Instant_Thrust) | C | Offensive | `narutorpg:confusing_claw_instant_thrust` |
| [Copy Ninjutsu](https://naruto.fandom.com/wiki/Copy_Ninjutsu) | B | Offensive | `narutorpg:copy_ninjutsu` (existing) |
| [Coral Palm](https://naruto.fandom.com/wiki/Coral_Palm) | C | Offensive, Supplementary | `narutorpg:coral_palm` (existing) |
| [Crane Wing Formation](https://naruto.fandom.com/wiki/Crane_Wing_Formation) | B | Supplementary | `narutorpg:crane_wing_formation` |
| [Crane Wing Formation (Eight Gates)](https://naruto.fandom.com/wiki/Crane_Wing_Formation_(Eight_Gates)) | S | Offensive | `narutorpg:crane_wing_formation_eight_gates` |
| [Crazy Butterfly Kick](https://naruto.fandom.com/wiki/Crazy_Butterfly_Kick) | C | Offensive | `narutorpg:crazy_butterfly_kick` |
| [Crazy Butterfly Punch](https://naruto.fandom.com/wiki/Crazy_Butterfly_Punch) | C | Offensive | `narutorpg:crazy_butterfly_punch` |
| [Crazy Hair Technique](https://naruto.fandom.com/wiki/Crazy_Hair_Technique) | A | Offensive | `narutorpg:crazy_hair_technique` |
| [Crimson Flash Colliding Fist](https://naruto.fandom.com/wiki/Crimson_Flash_Colliding_Fist) | C | Offensive | `narutorpg:crimson_flash_colliding_fist` |
| [Crimson Impact](https://naruto.fandom.com/wiki/Crimson_Impact) | C | Offensive | `narutorpg:crimson_impact` |
| [Crimson Leaf Dream](https://naruto.fandom.com/wiki/Crimson_Leaf_Dream) | C | Offensive | `narutorpg:crimson_leaf_dream` (existing) |
| [Cross Lariat](https://naruto.fandom.com/wiki/Cross_Lariat) | B | Offensive | `narutorpg:cross_lariat` (existing) |
| [Crushing Collision](https://naruto.fandom.com/wiki/Crushing_Collision) | C | Offensive | `narutorpg:crushing_collision` |
| [Crushing Rage: Rasengan](https://naruto.fandom.com/wiki/Crushing_Rage:_Rasengan) | A | Offensive | `narutorpg:crushing_rage_rasengan` |
| [Culminated Feelings: Wind Lightning Cherry Blossom Formation](https://naruto.fandom.com/wiki/Culminated_Feelings:_Wind_Lightning_Cherry_Blossom_Formation) | S | Offensive | `narutorpg:culminated_feelings_wind_lightning_cherry_blossom_formation` |
| [Cursed Seal: Killing Shout](https://naruto.fandom.com/wiki/Cursed_Seal:_Killing_Shout) | A | Offensive | `narutorpg:cursed_seal_killing_shout` |
| [Cypress](https://naruto.fandom.com/wiki/Cypress) | B | Offensive | `narutorpg:cypress` |
| [Dance of the Camellia](https://naruto.fandom.com/wiki/Dance_of_the_Camellia) | B | Offensive | `narutorpg:dance_of_the_camellia` (existing) |
| [Dance of the Clematis: Flower](https://naruto.fandom.com/wiki/Dance_of_the_Clematis:_Flower) | B | Offensive | `narutorpg:dance_of_the_clematis_flower` (existing) |
| [Dance of the Clematis: Vine](https://naruto.fandom.com/wiki/Dance_of_the_Clematis:_Vine) | B | Offensive | `narutorpg:dance_of_the_clematis_vine` (existing) |
| [Dance of the False Cleavers](https://naruto.fandom.com/wiki/Dance_of_the_False_Cleavers) | B | Offensive | `narutorpg:dance_of_the_false_cleavers` |
| [Dance of the Genji](https://naruto.fandom.com/wiki/Dance_of_the_Genji) | B | Offensive | `narutorpg:dance_of_the_genji` |
| [Dance of the Larch](https://naruto.fandom.com/wiki/Dance_of_the_Larch) | B | Offensive, Defensive | `narutorpg:dance_of_the_larch` (existing) |
| [Dance of the Willow](https://naruto.fandom.com/wiki/Dance_of_the_Willow) | B | Offensive | `narutorpg:dance_of_the_willow` (existing) |
| [Daytime Tiger](https://naruto.fandom.com/wiki/Daytime_Tiger) | A | Offensive | `narutorpg:daytime_tiger` |
| [Deer Butterfly Seal Binding](https://naruto.fandom.com/wiki/Deer_Butterfly_Seal_Binding) | B | Offensive | `narutorpg:deer_butterfly_seal_binding` (existing) |
| [Demonic Illusion: Flying Shadow](https://naruto.fandom.com/wiki/Demonic_Illusion:_Flying_Shadow) | B | Offensive | `narutorpg:demonic_illusion_flying_shadow` (existing) |
| [Demonic Illusion: Tree Binding Wind Blade](https://naruto.fandom.com/wiki/Demonic_Illusion:_Tree_Binding_Wind_Blade) | B | Offensive | `narutorpg:demonic_illusion_tree_binding_wind_blade` (existing) |
| [Demonic Rush](https://naruto.fandom.com/wiki/Demonic_Rush) | C | Offensive | `narutorpg:demonic_rush` |
| [Demonic Statue of the Outer Path: Palm](https://naruto.fandom.com/wiki/Demonic_Statue_of_the_Outer_Path:_Palm) | A | Offensive | `narutorpg:demonic_statue_of_the_outer_path_palm` |
| [Deutzia](https://naruto.fandom.com/wiki/Deutzia) | B | Offensive | `narutorpg:deutzia` |
| [Deviate Sword](https://naruto.fandom.com/wiki/Deviate_Sword) | C | Offensive | `narutorpg:deviate_sword` |
| [Disturbance Taijutsu](https://naruto.fandom.com/wiki/Disturbance_Taijutsu) | C | Offensive | `narutorpg:disturbance_taijutsu` |
| [Divine Retribution](https://naruto.fandom.com/wiki/Divine_Retribution) | C | Offensive | `narutorpg:divine_retribution` |
| [Dog Wall Formation](https://naruto.fandom.com/wiki/Dog_Wall_Formation) | B | Defensive, Supplementary | `narutorpg:dog_wall_formation` |
| [Don't Let Your Guard Down!](https://naruto.fandom.com/wiki/Don't_Let_Your_Guard_Down!) | C | Offensive | `narutorpg:don_t_let_your_guard_down` |
| [Double Dynamic Entry](https://naruto.fandom.com/wiki/Double_Dynamic_Entry) | B | Offensive | `narutorpg:double_dynamic_entry` |
| [Double Heavenly Foot of Pain](https://naruto.fandom.com/wiki/Double_Heavenly_Foot_of_Pain) | B | Offensive | `narutorpg:double_heavenly_foot_of_pain` |
| [Double Human Bullet Tank](https://naruto.fandom.com/wiki/Double_Human_Bullet_Tank) | B | Offensive | `narutorpg:double_human_bullet_tank` (existing) |
| [Dragging Punishment](https://naruto.fandom.com/wiki/Dragging_Punishment) | C | Offensive | `narutorpg:dragging_punishment` |
| [Dragon Cat](https://naruto.fandom.com/wiki/Dragon_Cat) | C | Offensive | `narutorpg:dragon_cat` |
| [Dragon Tail Foot](https://naruto.fandom.com/wiki/Dragon_Tail_Foot) | C | Offensive | `narutorpg:dragon_tail_foot` |
| [Dramingo](https://naruto.fandom.com/wiki/Dramingo) | C | Offensive | `narutorpg:dramingo` |
| [Drilling Crimson Blade](https://naruto.fandom.com/wiki/Drilling_Crimson_Blade) | C | Offensive | `narutorpg:drilling_crimson_blade` |
| [Drilling Needles](https://naruto.fandom.com/wiki/Drilling_Needles) | B | Offensive | `narutorpg:drilling_needles` |
| [Drooping Flower](https://naruto.fandom.com/wiki/Drooping_Flower) | C | Offensive | `narutorpg:drooping_flower` |
| [Drop Kick](https://naruto.fandom.com/wiki/Drop_Kick) | C | Offensive | `narutorpg:drop_kick` |
| [Drunken Fist](https://naruto.fandom.com/wiki/Drunken_Fist) | C | Offensive, Defensive | `narutorpg:drunken_fist` |
| [Dying Wail Fist](https://naruto.fandom.com/wiki/Dying_Wail_Fist) | S | Offensive | `narutorpg:dying_wail_fist` |
| [Dynamic Action](https://naruto.fandom.com/wiki/Dynamic_Action) | C | Offensive | `narutorpg:dynamic_action` |
| [Dynamic Entry](https://naruto.fandom.com/wiki/Dynamic_Entry) | D | Offensive | `narutorpg:dynamic_entry` (existing) |
| [Dynamic Marking](https://naruto.fandom.com/wiki/Dynamic_Marking) | D | Supplementary | `narutorpg:dynamic_marking` (existing) |
| [Early Sacrifice](https://naruto.fandom.com/wiki/Early_Sacrifice) | C | Offensive, Defensive | `narutorpg:early_sacrifice` |
| [Earth Cutter](https://naruto.fandom.com/wiki/Earth_Cutter) | C | Offensive | `narutorpg:earth_cutter` |
| [Earth Release: Earth Crust Tearing Impact](https://naruto.fandom.com/wiki/Earth_Release:_Earth_Crust_Tearing_Impact) | C | Offensive | `narutorpg:earth_release_earth_crust_tearing_impact` (existing) |
| [Earth Release: Fist Rock Technique](https://naruto.fandom.com/wiki/Earth_Release:_Fist_Rock_Technique) | C | Offensive | `narutorpg:earth_release_fist_rock_technique` (existing) |
| [Earth Splitting](https://naruto.fandom.com/wiki/Earth_Splitting) | C | Offensive | `narutorpg:earth_splitting` |
| [Earth-Shattering](https://naruto.fandom.com/wiki/Earth-Shattering) | C | Offensive | `narutorpg:earth_shattering` |
| [Earth-Tearing Foot](https://naruto.fandom.com/wiki/Earth-Tearing_Foot) | S | Offensive | `narutorpg:earth_tearing_foot` |
| [Eight Gates Released Formation](https://naruto.fandom.com/wiki/Eight_Gates_Released_Formation) | A | Supplementary | `narutorpg:eight_gates_released_formation` |
| [Eight Trigrams Aerial Attack](https://naruto.fandom.com/wiki/Eight_Trigrams_Aerial_Attack) | B | Offensive | `narutorpg:eight_trigrams_aerial_attack` |
| [Eight Trigrams Aiki Palm](https://naruto.fandom.com/wiki/Eight_Trigrams_Aiki_Palm) | B | Offensive | `narutorpg:eight_trigrams_aiki_palm` |
| [Eight Trigrams Breaching Vacuum Palm](https://naruto.fandom.com/wiki/Eight_Trigrams_Breaching_Vacuum_Palm) | B | Offensive | `narutorpg:eight_trigrams_breaching_vacuum_palm` |
| [Eight Trigrams Four Sky Palms](https://naruto.fandom.com/wiki/Eight_Trigrams_Four_Sky_Palms) | B | Offensive | `narutorpg:eight_trigrams_four_sky_palms` |
| [Eight Trigrams Lion Palm](https://naruto.fandom.com/wiki/Eight_Trigrams_Lion_Palm) | B | Offensive | `narutorpg:eight_trigrams_lion_palm` |
| [Eight Trigrams Mountain Crusher](https://naruto.fandom.com/wiki/Eight_Trigrams_Mountain_Crusher) | B | Offensive | `narutorpg:eight_trigrams_mountain_crusher` (existing) |
| [Eight Trigrams One Hundred Twenty-Eight Palms](https://naruto.fandom.com/wiki/Eight_Trigrams_One_Hundred_Twenty-Eight_Palms) | B | Offensive | `narutorpg:eight_trigrams_one_hundred_twenty_eight_palms` (existing) |
| [Eight Trigrams Palms Great Revolving Heaven](https://naruto.fandom.com/wiki/Eight_Trigrams_Palms_Great_Revolving_Heaven) | B | Offensive, Defensive | `narutorpg:eight_trigrams_palms_great_revolving_heaven` |
| [Eight Trigrams Palms Revolving Heaven](https://naruto.fandom.com/wiki/Eight_Trigrams_Palms_Revolving_Heaven) | B | Defensive | `narutorpg:eight_trigrams_palms_revolving_heaven` (existing) |
| [Eight Trigrams Palms Revolving Heaven: Sever](https://naruto.fandom.com/wiki/Eight_Trigrams_Palms_Revolving_Heaven:_Sever) | B | Offensive, Defensive | `narutorpg:eight_trigrams_palms_revolving_heaven_sever` |
| [Eight Trigrams Palms Storm Dance](https://naruto.fandom.com/wiki/Eight_Trigrams_Palms_Storm_Dance) | B | Offensive | `narutorpg:eight_trigrams_palms_storm_dance` |
| [Eight Trigrams Palms Twin Handed Back](https://naruto.fandom.com/wiki/Eight_Trigrams_Palms_Twin_Handed_Back) | B | Offensive | `narutorpg:eight_trigrams_palms_twin_handed_back` |
| [Eight Trigrams Palms Vacuum Heaven](https://naruto.fandom.com/wiki/Eight_Trigrams_Palms_Vacuum_Heaven) | B | Offensive, Defensive | `narutorpg:eight_trigrams_palms_vacuum_heaven` |
| [Eight Trigrams Sect Palm Wave](https://naruto.fandom.com/wiki/Eight_Trigrams_Sect_Palm_Wave) | B | Offensive | `narutorpg:eight_trigrams_sect_palm_wave` |
| [Eight Trigrams Sixteen Palms](https://naruto.fandom.com/wiki/Eight_Trigrams_Sixteen_Palms) | B | Offensive | `narutorpg:eight_trigrams_sixteen_palms` (existing) |
| [Eight Trigrams Sixty-Four Palms](https://naruto.fandom.com/wiki/Eight_Trigrams_Sixty-Four_Palms) | B | Offensive | `narutorpg:eight_trigrams_sixty_four_palms` (existing) |
| [Eight Trigrams Spirit Sixty-Four Palms](https://naruto.fandom.com/wiki/Eight_Trigrams_Spirit_Sixty-Four_Palms) | B | Offensive | `narutorpg:eight_trigrams_spirit_sixty_four_palms` |
| [Eight Trigrams Thirty-Two Palms](https://naruto.fandom.com/wiki/Eight_Trigrams_Thirty-Two_Palms) | B | Offensive | `narutorpg:eight_trigrams_thirty_two_palms` (existing) |
| [Eight Trigrams Three-Hundred-Sixty-One Style](https://naruto.fandom.com/wiki/Eight_Trigrams_Three-Hundred-Sixty-One_Style) | B | Offensive | `narutorpg:eight_trigrams_three_hundred_sixty_one_style` |
| [Eight Trigrams Twin Lions Crumbling Attack](https://naruto.fandom.com/wiki/Eight_Trigrams_Twin_Lions_Crumbling_Attack) | B | Offensive, Defensive, Supplementary | `narutorpg:eight_trigrams_twin_lions_crumbling_attack` (existing) |
| [Eight Trigrams Vacuum Lion Palm](https://naruto.fandom.com/wiki/Eight_Trigrams_Vacuum_Lion_Palm) | B | Offensive | `narutorpg:eight_trigrams_vacuum_lion_palm` |
| [Eight Trigrams Vacuum Palm](https://naruto.fandom.com/wiki/Eight_Trigrams_Vacuum_Palm) | B | Offensive | `narutorpg:eight_trigrams_vacuum_palm` (existing) |
| [Eight Trigrams Vacuum Wall Palm](https://naruto.fandom.com/wiki/Eight_Trigrams_Vacuum_Wall_Palm) | B | Offensive, Defensive | `narutorpg:eight_trigrams_vacuum_wall_palm` (existing) |
| [Eighty Gods Vacuum Attack](https://naruto.fandom.com/wiki/Eighty_Gods_Vacuum_Attack) | A | Offensive | `narutorpg:eighty_gods_vacuum_attack` |
| [Empty Crash](https://naruto.fandom.com/wiki/Empty_Crash) | C | Offensive | `narutorpg:empty_crash` (existing) |
| [Ensnaring Exploding Flame](https://naruto.fandom.com/wiki/Ensnaring_Exploding_Flame) | C | Offensive | `narutorpg:ensnaring_exploding_flame` (existing) |
| [Erupting Falling Stomp](https://naruto.fandom.com/wiki/Erupting_Falling_Stomp) | B | Offensive | `narutorpg:erupting_falling_stomp` (existing) |
| [Erupting Propulsion Fist](https://naruto.fandom.com/wiki/Erupting_Propulsion_Fist) | B | Offensive | `narutorpg:erupting_propulsion_fist` (existing) |
| [Erupting Revolving Arm](https://naruto.fandom.com/wiki/Erupting_Revolving_Arm) | B | Offensive | `narutorpg:erupting_revolving_arm` (existing) |
| [Erupting Revolving Foot](https://naruto.fandom.com/wiki/Erupting_Revolving_Foot) | B | Offensive | `narutorpg:erupting_revolving_foot` (existing) |
| [Erupting Strong Foot](https://naruto.fandom.com/wiki/Erupting_Strong_Foot) | B | Offensive | `narutorpg:erupting_strong_foot` (existing) |
| [Erupting Thrust Cannon](https://naruto.fandom.com/wiki/Erupting_Thrust_Cannon) | B | Offensive | `narutorpg:erupting_thrust_cannon` (existing) |
| [Eruption Legs](https://naruto.fandom.com/wiki/Eruption_Legs) | C | Offensive | `narutorpg:eruption_legs` |
| [Eternal Rival (Ultimate Ninja 3)](https://naruto.fandom.com/wiki/Eternal_Rival_(Ultimate_Ninja_3)) | B | Offensive, Defensive | `narutorpg:eternal_rival_ultimate_ninja_3` |
| [Eternal Rival (Ultimate Ninja Storm Revolution)](https://naruto.fandom.com/wiki/Eternal_Rival_(Ultimate_Ninja_Storm_Revolution)) | S | Offensive | `narutorpg:eternal_rival_ultimate_ninja_storm_revolution` (existing) |
| [Evening Elephant](https://naruto.fandom.com/wiki/Evening_Elephant) | A | Offensive | `narutorpg:evening_elephant` |
| [Evening Shower Heavenly Crush](https://naruto.fandom.com/wiki/Evening_Shower_Heavenly_Crush) | C | Offensive | `narutorpg:evening_shower_heavenly_crush` (existing) |
| [Explosion Release: Defence Technique](https://naruto.fandom.com/wiki/Explosion_Release:_Defence_Technique) | B | Defensive, Supplementary | `narutorpg:explosion_release_defence_technique` (existing) |
| [Explosion Release: Landmine Fist](https://naruto.fandom.com/wiki/Explosion_Release:_Landmine_Fist) | B | Offensive | `narutorpg:explosion_release_landmine_fist` (existing) |
| [Explosion Release: Quick Explosive Kick](https://naruto.fandom.com/wiki/Explosion_Release:_Quick_Explosive_Kick) | B | Offensive | `narutorpg:explosion_release_quick_explosive_kick` (existing) |
| [Explosion: Ice Mountain Destruction](https://naruto.fandom.com/wiki/Explosion:_Ice_Mountain_Destruction) | C | Offensive | `narutorpg:explosion_ice_mountain_destruction` (existing) |
| [Fairy Tale Get](https://naruto.fandom.com/wiki/Fairy_Tale_Get) | C | Offensive | `narutorpg:fairy_tale_get` |
| [Falling Chain](https://naruto.fandom.com/wiki/Falling_Chain) | C | Offensive | `narutorpg:falling_chain` |
| [Falling Wrapping Thrust](https://naruto.fandom.com/wiki/Falling_Wrapping_Thrust) | C | Offensive, Defensive | `narutorpg:falling_wrapping_thrust` |
| [Fang Beheading Fang](https://naruto.fandom.com/wiki/Fang_Beheading_Fang) | B | Offensive, Defensive | `narutorpg:fang_beheading_fang` |
| [Fang Heaven Wolf](https://naruto.fandom.com/wiki/Fang_Heaven_Wolf) | B | Offensive | `narutorpg:fang_heaven_wolf` |
| [Fang Insects Fang](https://naruto.fandom.com/wiki/Fang_Insects_Fang) | B | Offensive | `narutorpg:fang_insects_fang` |
| [Fang Passing Fang](https://naruto.fandom.com/wiki/Fang_Passing_Fang) | C | Offensive | `narutorpg:fang_passing_fang` (existing) |
| [Fang Rotating Fang](https://naruto.fandom.com/wiki/Fang_Rotating_Fang) | B | Offensive | `narutorpg:fang_rotating_fang` (existing) |
| [Fang Violent Fang](https://naruto.fandom.com/wiki/Fang_Violent_Fang) | B | Offensive | `narutorpg:fang_violent_fang` |
| [Fang Wolf Fang](https://naruto.fandom.com/wiki/Fang_Wolf_Fang) | B | Offensive | `narutorpg:fang_wolf_fang` (existing) |
| [Fangs of the Monkey King](https://naruto.fandom.com/wiki/Fangs_of_the_Monkey_King) | B | Offensive | `narutorpg:fangs_of_the_monkey_king` |
| [Fat Couple Attack](https://naruto.fandom.com/wiki/Fat_Couple_Attack) | C | Offensive | `narutorpg:fat_couple_attack` |
| [Fierce Snake](https://naruto.fandom.com/wiki/Fierce_Snake) | C | Offensive | `narutorpg:fierce_snake` |
| [Fire Leaping Windhole](https://naruto.fandom.com/wiki/Fire_Leaping_Windhole) | A | Offensive | `narutorpg:fire_leaping_windhole` (existing) |
| [Fire Release: Great Fire Annihilation: Earth](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Annihilation:_Earth) | B | Offensive | `narutorpg:fire_release_great_fire_annihilation_earth` (existing) |
| [Fire Release: Great Fire Annihilation: Heaven](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fire_Annihilation:_Heaven) | B | Offensive | `narutorpg:fire_release_great_fire_annihilation_heaven` (existing) |
| [Fire Release: Great Fireball Technique: Falling Fire](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique:_Falling_Fire) | C | Offensive | `narutorpg:fire_release_great_fireball_technique_falling_fire` (existing) |
| [Fire Release: Great Fireball Technique: First Step](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique:_First_Step) | C | Offensive | `narutorpg:fire_release_great_fireball_technique_first_step` (existing) |
| [Fire Release: Great Fireball Technique: Onibi](https://naruto.fandom.com/wiki/Fire_Release:_Great_Fireball_Technique:_Onibi) | C | Offensive | `narutorpg:fire_release_great_fireball_technique_onibi` (existing) |
| [Fist Slam](https://naruto.fandom.com/wiki/Fist_Slam) | C | Offensive | `narutorpg:fist_slam` (existing) |
| [Five-Mountain Jump](https://naruto.fandom.com/wiki/Five-Mountain_Jump) | B | Offensive | `narutorpg:five_mountain_jump` (existing) |
| [Flash Cross Sever](https://naruto.fandom.com/wiki/Flash_Cross_Sever) | C | Offensive | `narutorpg:flash_cross_sever` |
| [Flash Revolving Foot](https://naruto.fandom.com/wiki/Flash_Revolving_Foot) | C | Offensive | `narutorpg:flash_revolving_foot` |
| [Flash Swallow Dance: Heaven Step](https://naruto.fandom.com/wiki/Flash_Swallow_Dance:_Heaven_Step) | A | Offensive | `narutorpg:flash_swallow_dance_heaven_step` |
| [Flawless Three](https://naruto.fandom.com/wiki/Flawless_Three) | B | Offensive | `narutorpg:flawless_three` (existing) |
| [Flight](https://naruto.fandom.com/wiki/Flight) | C | Offensive | `narutorpg:flight` (existing) |
| [Flower–Fruit Mountain](https://naruto.fandom.com/wiki/Flower–Fruit_Mountain) | B | Offensive | `narutorpg:flowerfruit_mountain` (existing) |
| [Flowing Blade](https://naruto.fandom.com/wiki/Flowing_Blade) | C | Offensive | `narutorpg:flowing_blade` |
| [Fluttering Heaven](https://naruto.fandom.com/wiki/Fluttering_Heaven) | C | Offensive | `narutorpg:fluttering_heaven` |
| [Flying Attack](https://naruto.fandom.com/wiki/Flying_Attack) | C | Offensive | `narutorpg:flying_attack` (existing) |
| [Flying Claw](https://naruto.fandom.com/wiki/Flying_Claw) | C | Offensive | `narutorpg:flying_claw` |
| [Flying Single Foot](https://naruto.fandom.com/wiki/Flying_Single_Foot) | C | Offensive | `narutorpg:flying_single_foot` |
| [Flying Swallow](https://naruto.fandom.com/wiki/Flying_Swallow) | B | Offensive | `narutorpg:flying_swallow` |
| [Flying Swallow Barrage](https://naruto.fandom.com/wiki/Flying_Swallow_Barrage) | C | Offensive | `narutorpg:flying_swallow_barrage` |
| [Flying Swallow: Cross Cut](https://naruto.fandom.com/wiki/Flying_Swallow:_Cross_Cut) | C | Offensive | `narutorpg:flying_swallow_cross_cut` |
| [Flying Thunder God: Sequential Steps](https://naruto.fandom.com/wiki/Flying_Thunder_God:_Sequential_Steps) | A | Offensive | `narutorpg:flying_thunder_god_sequential_steps` |
| [Formation Ino–Shika–Chō](https://naruto.fandom.com/wiki/Formation_Ino–Shika–Chō) | B | Offensive, Supplementary | `narutorpg:formation_inoshikacho` (existing) |
| [Forward Trampling Jump](https://naruto.fandom.com/wiki/Forward_Trampling_Jump) | C | Offensive | `narutorpg:forward_trampling_jump` |
| [Frog](https://naruto.fandom.com/wiki/Frog) | B | Offensive | `narutorpg:frog` |
| [Frog Kata](https://naruto.fandom.com/wiki/Frog_Kata) | C | Offensive | `narutorpg:frog_kata` |
| [Frog Kata: Dual Gale](https://naruto.fandom.com/wiki/Frog_Kata:_Dual_Gale) | A | Offensive | `narutorpg:frog_kata_dual_gale` |
| [Frog Kata: Frog Throw](https://naruto.fandom.com/wiki/Frog_Kata:_Frog_Throw) | A | Offensive | `narutorpg:frog_kata_frog_throw` |
| [Frog Kata: Heaven Return](https://naruto.fandom.com/wiki/Frog_Kata:_Heaven_Return) | A | Offensive | `narutorpg:frog_kata_heaven_return` |
| [Frog Kata: Roaring Attack](https://naruto.fandom.com/wiki/Frog_Kata:_Roaring_Attack) | A | Offensive | `narutorpg:frog_kata_roaring_attack` |
| [Frog Strike](https://naruto.fandom.com/wiki/Frog_Strike) | C | Offensive | `narutorpg:frog_strike` |
| [Front Lotus](https://naruto.fandom.com/wiki/Front_Lotus) | B | Offensive | `narutorpg:front_lotus` |
| [Front Lotus Guy Version](https://naruto.fandom.com/wiki/Front_Lotus_Guy_Version) | S | Offensive | `narutorpg:front_lotus_guy_version` |
| [Front Lotus of Love and Youth](https://naruto.fandom.com/wiki/Front_Lotus_of_Love_and_Youth) | S | Offensive | `narutorpg:front_lotus_of_love_and_youth` |
| [Front Lotus: Eight Trigrams Palms Revolving Heaven](https://naruto.fandom.com/wiki/Front_Lotus:_Eight_Trigrams_Palms_Revolving_Heaven) | S | Offensive, Defensive | `narutorpg:front_lotus_eight_trigrams_palms_revolving_heaven` (existing) |
| [Furious Tail Strike](https://naruto.fandom.com/wiki/Furious_Tail_Strike) | C | Offensive | `narutorpg:furious_tail_strike` |
| [Gale Bullet](https://naruto.fandom.com/wiki/Gale_Bullet) | S | Offensive | `narutorpg:gale_bullet` |
| [Gamaken's Clumsy Shake-Down](https://naruto.fandom.com/wiki/Gamaken's_Clumsy_Shake-Down) | A | Offensive | `narutorpg:gamaken_s_clumsy_shake_down` |
| [Gentle Fist](https://naruto.fandom.com/wiki/Gentle_Fist) | C | Offensive, Defensive, Supplementary | `narutorpg:gentle_fist` (existing) |
| [Gentle Fist Art One Blow Body](https://naruto.fandom.com/wiki/Gentle_Fist_Art_One_Blow_Body) | C | Offensive | `narutorpg:gentle_fist_art_one_blow_body` (existing) |
| [Gentle Fist Art: Exorcism](https://naruto.fandom.com/wiki/Gentle_Fist_Art:_Exorcism) | B | Offensive | `narutorpg:gentle_fist_art_exorcism` |
| [Gentle Fist Instruction](https://naruto.fandom.com/wiki/Gentle_Fist_Instruction) | B | Offensive | `narutorpg:gentle_fist_instruction` |
| [Gentle Fist: Tenketsu Needle](https://naruto.fandom.com/wiki/Gentle_Fist:_Tenketsu_Needle) | B | Offensive, Supplementary | `narutorpg:gentle_fist_tenketsu_needle` (existing) |
| [Gentle Phoenix Spiralling Twin Lion Fists](https://naruto.fandom.com/wiki/Gentle_Phoenix_Spiralling_Twin_Lion_Fists) | B | Offensive | `narutorpg:gentle_phoenix_spiralling_twin_lion_fists` |
| [Gentle Step Tailed Beast Twin Lion Fists](https://naruto.fandom.com/wiki/Gentle_Step_Tailed_Beast_Twin_Lion_Fists) | B | Offensive | `narutorpg:gentle_step_tailed_beast_twin_lion_fists` |
| [Gentle Step Twin Lion Fists](https://naruto.fandom.com/wiki/Gentle_Step_Twin_Lion_Fists) | B | Offensive, Defensive, Supplementary | `narutorpg:gentle_step_twin_lion_fists` (existing) |
| [Gentle Step Twin Palms](https://naruto.fandom.com/wiki/Gentle_Step_Twin_Palms) | B | Offensive | `narutorpg:gentle_step_twin_palms` |
| [Gentle Wind Spiralling Twin Lion Fists: Snow White](https://naruto.fandom.com/wiki/Gentle_Wind_Spiralling_Twin_Lion_Fists:_Snow_White) | B | Offensive, Defensive, Supplementary | `narutorpg:gentle_wind_spiralling_twin_lion_fists_snow_white` (existing) |
| [Giant Insect Fang: Sixty-Four Palms](https://naruto.fandom.com/wiki/Giant_Insect_Fang:_Sixty-Four_Palms) | B | Offensive | `narutorpg:giant_insect_fang_sixty_four_palms` |
| [Giant Swing](https://naruto.fandom.com/wiki/Giant_Swing) | C | Offensive | `narutorpg:giant_swing` |
| [Giant Tree Strong Drop](https://naruto.fandom.com/wiki/Giant_Tree_Strong_Drop) | B | Offensive | `narutorpg:giant_tree_strong_drop` (existing) |
| [Grass Shadow-Style Emit Destroy Palm](https://naruto.fandom.com/wiki/Grass_Shadow-Style_Emit_Destroy_Palm) | C | Offensive | `narutorpg:grass_shadow_style_emit_destroy_palm` |
| [Great Barrage Strike](https://naruto.fandom.com/wiki/Great_Barrage_Strike) | C | Offensive | `narutorpg:great_barrage_strike` |
| [Great Cat Claw Attack](https://naruto.fandom.com/wiki/Great_Cat_Claw_Attack) | C | Offensive | `narutorpg:great_cat_claw_attack` |
| [Great Entrance of Burning Spirit](https://naruto.fandom.com/wiki/Great_Entrance_of_Burning_Spirit) | S | Offensive | `narutorpg:great_entrance_of_burning_spirit` |
| [Great Gunpowder Attack](https://naruto.fandom.com/wiki/Great_Gunpowder_Attack) | C | Offensive | `narutorpg:great_gunpowder_attack` |
| [Great Rotating Pushing Kick (Failed…)](https://naruto.fandom.com/wiki/Great_Rotating_Pushing_Kick_(Failed…)) | C | Offensive | `narutorpg:great_rotating_pushing_kick_failed` |
| [Great Strike Outburst](https://naruto.fandom.com/wiki/Great_Strike_Outburst) | C | Offensive | `narutorpg:great_strike_outburst` |
| [Great Trample Circle](https://naruto.fandom.com/wiki/Great_Trample_Circle) | C | Offensive | `narutorpg:great_trample_circle` |
| [Great Wild Snake](https://naruto.fandom.com/wiki/Great_Wild_Snake) | C | Offensive, Defensive | `narutorpg:great_wild_snake` |
| [Green Impact](https://naruto.fandom.com/wiki/Green_Impact) | C | Offensive, Defensive | `narutorpg:green_impact` |
| [Green Roar](https://naruto.fandom.com/wiki/Green_Roar) | C | Offensive | `narutorpg:green_roar` |
| [Ground Splitting](https://naruto.fandom.com/wiki/Ground_Splitting) | A | Offensive | `narutorpg:ground_splitting` |
| [Gunpowder Fist](https://naruto.fandom.com/wiki/Gunpowder_Fist) | C | Offensive | `narutorpg:gunpowder_fist` |
| [Hanabi Style: Eight Trigrams Sixty-Four Palms](https://naruto.fandom.com/wiki/Hanabi_Style:_Eight_Trigrams_Sixty-Four_Palms) | B | Offensive | `narutorpg:hanabi_style_eight_trigrams_sixty_four_palms` |
| [Hanabi Style: Revolving Heaven](https://naruto.fandom.com/wiki/Hanabi_Style:_Revolving_Heaven) | B | Offensive, Defensive | `narutorpg:hanabi_style_revolving_heaven` |
| [Headbutt](https://naruto.fandom.com/wiki/Headbutt) | C | Offensive | `narutorpg:headbutt` |
| [Headbutt (game)](https://naruto.fandom.com/wiki/Headbutt_(game)) | C | Offensive | `narutorpg:headbutt_game` |
| [Heaven Collapse](https://naruto.fandom.com/wiki/Heaven_Collapse) | C | Offensive | `narutorpg:heaven_collapse` |
| [Heaven Cutter](https://naruto.fandom.com/wiki/Heaven_Cutter) | C | Offensive | `narutorpg:heaven_cutter` |
| [Heaven Twin Fang](https://naruto.fandom.com/wiki/Heaven_Twin_Fang) | B | Offensive, Defensive | `narutorpg:heaven_twin_fang` |
| [Heaven's Blessing](https://naruto.fandom.com/wiki/Heaven's_Blessing) | C | Offensive, Defensive | `narutorpg:heaven_s_blessing` |
| [Heaven's Needle Barrage](https://naruto.fandom.com/wiki/Heaven's_Needle_Barrage) | C | Offensive | `narutorpg:heaven_s_needle_barrage` |
| [Heavenly Chidori: Cherry Blossom](https://naruto.fandom.com/wiki/Heavenly_Chidori:_Cherry_Blossom) | A | Offensive | `narutorpg:heavenly_chidori_cherry_blossom` (existing) |
| [Heavenly Defending Foot](https://naruto.fandom.com/wiki/Heavenly_Defending_Foot) | C | Offensive | `narutorpg:heavenly_defending_foot` |
| [Heavenly Foot of Pain](https://naruto.fandom.com/wiki/Heavenly_Foot_of_Pain) | C | Offensive | `narutorpg:heavenly_foot_of_pain` |
| [Heavenly Foot of Pain: Kirin](https://naruto.fandom.com/wiki/Heavenly_Foot_of_Pain:_Kirin) | B | Offensive | `narutorpg:heavenly_foot_of_pain_kirin` (existing) |
| [Heavenly Kick Wild Dance](https://naruto.fandom.com/wiki/Heavenly_Kick_Wild_Dance) | B | Offensive | `narutorpg:heavenly_kick_wild_dance` |
| [Heavenly Palm Wild Dance](https://naruto.fandom.com/wiki/Heavenly_Palm_Wild_Dance) | B | Offensive | `narutorpg:heavenly_palm_wild_dance` |
| [Heavenly Spear Foot](https://naruto.fandom.com/wiki/Heavenly_Spear_Foot) | C | Offensive | `narutorpg:heavenly_spear_foot` |
| [Heavy Hammer Spark](https://naruto.fandom.com/wiki/Heavy_Hammer_Spark) | C | Offensive | `narutorpg:heavy_hammer_spark` |
| [Heel Falling Attack](https://naruto.fandom.com/wiki/Heel_Falling_Attack) | C | Offensive | `narutorpg:heel_falling_attack` |
| [Hell Drop](https://naruto.fandom.com/wiki/Hell_Drop) | C | Offensive | `narutorpg:hell_drop` |
| [Hell Stab Four-Finger Spear Hand: Sky Drop](https://naruto.fandom.com/wiki/Hell_Stab_Four-Finger_Spear_Hand:_Sky_Drop) | C | Offensive | `narutorpg:hell_stab_four_finger_spear_hand_sky_drop` (existing) |
| [Hell Stab One-Finger Nukite: Ultimate](https://naruto.fandom.com/wiki/Hell_Stab_One-Finger_Nukite:_Ultimate) | C | Offensive | `narutorpg:hell_stab_one_finger_nukite_ultimate` (existing) |
| [Hell Stab Three-Finger Spear Hand: Side Swipe](https://naruto.fandom.com/wiki/Hell_Stab_Three-Finger_Spear_Hand:_Side_Swipe) | C | Offensive | `narutorpg:hell_stab_three_finger_spear_hand_side_swipe` (existing) |
| [Hero of the Sharingan](https://naruto.fandom.com/wiki/Hero_of_the_Sharingan) | C | Offensive | `narutorpg:hero_of_the_sharingan` (existing) |
| [Holly](https://naruto.fandom.com/wiki/Holly) | B | Offensive | `narutorpg:holly` |
| [Horizon Meteor](https://naruto.fandom.com/wiki/Horizon_Meteor) | B | Offensive, Supplementary | `narutorpg:horizon_meteor` (existing) |
| [Horn Breaking](https://naruto.fandom.com/wiki/Horn_Breaking) | C | Offensive | `narutorpg:horn_breaking` (existing) |
| [Horror of Akatsuki](https://naruto.fandom.com/wiki/Horror_of_Akatsuki) | S | Offensive | `narutorpg:horror_of_akatsuki` (existing) |
| [Host Transformation](https://naruto.fandom.com/wiki/Host_Transformation) | C | Offensive | `narutorpg:host_transformation` (existing) |
| [Hot Sand Waterfall Funeral](https://naruto.fandom.com/wiki/Hot_Sand_Waterfall_Funeral) | C | Offensive | `narutorpg:hot_sand_waterfall_funeral` |
| [Hot-Blooded Dynamic Entry](https://naruto.fandom.com/wiki/Hot-Blooded_Dynamic_Entry) | B | Offensive | `narutorpg:hot_blooded_dynamic_entry` |
| [Human Bullet Big Tank](https://naruto.fandom.com/wiki/Human_Bullet_Big_Tank) | C | Offensive, Defensive | `narutorpg:human_bullet_big_tank` (existing) |
| [Human Bullet Heavy Tank](https://naruto.fandom.com/wiki/Human_Bullet_Heavy_Tank) | C | Offensive | `narutorpg:human_bullet_heavy_tank` (existing) |
| [Human Bullet Tank](https://naruto.fandom.com/wiki/Human_Bullet_Tank) | C | Offensive | `narutorpg:human_bullet_tank` (existing) |
| [Human Bullet Yo-Yo](https://naruto.fandom.com/wiki/Human_Bullet_Yo-Yo) | B | Offensive, Defensive | `narutorpg:human_bullet_yo_yo` (existing) |
| [Hundred Furious Palms](https://naruto.fandom.com/wiki/Hundred_Furious_Palms) | B | Offensive | `narutorpg:hundred_furious_palms` |
| [Hurricane Head Butt](https://naruto.fandom.com/wiki/Hurricane_Head_Butt) | C | Offensive | `narutorpg:hurricane_head_butt` |
| [Hurricane Thunderclap — Majestic Attire Sword Stroke](https://naruto.fandom.com/wiki/Hurricane_Thunderclap_—_Majestic_Attire_Sword_Stroke) | B | Offensive | `narutorpg:hurricane_thunderclap_majestic_attire_sword_stroke` (existing) |
| [Husband and Wife Rasengan](https://naruto.fandom.com/wiki/Husband_and_Wife_Rasengan) | A | Offensive | `narutorpg:husband_and_wife_rasengan` |
| [Hyūga Great Combo Palm](https://naruto.fandom.com/wiki/Hyūga_Great_Combo_Palm) | B | Offensive | `narutorpg:hyuga_great_combo_palm` |
| [Hyūga: Great Revolving Heaven](https://naruto.fandom.com/wiki/Hyūga:_Great_Revolving_Heaven) | B | Offensive, Defensive | `narutorpg:hyuga_great_revolving_heaven` |
| [I Won't Let You Go!](https://naruto.fandom.com/wiki/I_Won't_Let_You_Go!) | C | Offensive | `narutorpg:i_won_t_let_you_go` |
| [Ice Release: Icicle Combo](https://naruto.fandom.com/wiki/Ice_Release:_Icicle_Combo) | B | Offensive | `narutorpg:ice_release_icicle_combo` (existing) |
| [Incursion of Clarity](https://naruto.fandom.com/wiki/Incursion_of_Clarity) | C | Offensive | `narutorpg:incursion_of_clarity` |
| [Infinite Wild Dance](https://naruto.fandom.com/wiki/Infinite_Wild_Dance) | S | Offensive | `narutorpg:infinite_wild_dance` |
| [Ink Lariat](https://naruto.fandom.com/wiki/Ink_Lariat) | C | Offensive | `narutorpg:ink_lariat` (existing) |
| [Inner Sakura](https://naruto.fandom.com/wiki/Inner_Sakura) | C | Offensive | `narutorpg:inner_sakura` (existing) |
| [Instant Earth Burial](https://naruto.fandom.com/wiki/Instant_Earth_Burial) | C | Offensive | `narutorpg:instant_earth_burial` |
| [Instant Lightning Blade](https://naruto.fandom.com/wiki/Instant_Lightning_Blade) | C | Offensive | `narutorpg:instant_lightning_blade` |
| [Instant Lightning: Combination Tag](https://naruto.fandom.com/wiki/Instant_Lightning:_Combination_Tag) | A | Offensive | `narutorpg:instant_lightning_combination_tag` (existing) |
| [Instant Water](https://naruto.fandom.com/wiki/Instant_Water) | C | Offensive | `narutorpg:instant_water` (existing) |
| [Intense Passion: Ferocious Tiger Lotus Peacock Attack](https://naruto.fandom.com/wiki/Intense_Passion:_Ferocious_Tiger_Lotus_Peacock_Attack) | S | Offensive | `narutorpg:intense_passion_ferocious_tiger_lotus_peacock_attack` |
| [Internal Weapon](https://naruto.fandom.com/wiki/Internal_Weapon) | B | Offensive | `narutorpg:internal_weapon` |
| [Intersection Method](https://naruto.fandom.com/wiki/Intersection_Method) | D | Defensive | `narutorpg:intersection_method` |
| [Inuzuka Clan Glory](https://naruto.fandom.com/wiki/Inuzuka_Clan_Glory) | B | Offensive | `narutorpg:inuzuka_clan_glory` |
| [Invariant Connection](https://naruto.fandom.com/wiki/Invariant_Connection) | S | Offensive, Defensive | `narutorpg:invariant_connection` |
| [Iron Claw](https://naruto.fandom.com/wiki/Iron_Claw) | C | Offensive | `narutorpg:iron_claw` |
| [Iron Mountain Leaning](https://naruto.fandom.com/wiki/Iron_Mountain_Leaning) | C | Offensive | `narutorpg:iron_mountain_leaning` (existing) |
| [Iron Pan Furious Attack](https://naruto.fandom.com/wiki/Iron_Pan_Furious_Attack) | C | Offensive, Defensive | `narutorpg:iron_pan_furious_attack` |
| [Jawbreaker](https://naruto.fandom.com/wiki/Jawbreaker) | C | Offensive | `narutorpg:jawbreaker` |
| [Kagutsuchi Rasengan](https://naruto.fandom.com/wiki/Kagutsuchi_Rasengan) | S | Offensive | `narutorpg:kagutsuchi_rasengan` (existing) |
| [Kara Savagery](https://naruto.fandom.com/wiki/Kara_Savagery) | S | Offensive | `narutorpg:kara_savagery` |
| [Kick Attack Crash](https://naruto.fandom.com/wiki/Kick_Attack_Crash) | A | Offensive | `narutorpg:kick_attack_crash` |
| [Konoha Jōseki Formation](https://naruto.fandom.com/wiki/Konoha_Jōseki_Formation) | B | Offensive, Supplementary | `narutorpg:konoha_joseki_formation` (existing) |
| [Kunoichi Rumble](https://naruto.fandom.com/wiki/Kunoichi_Rumble) | B | Offensive | `narutorpg:kunoichi_rumble` |
| [Kurama Great Crumbling](https://naruto.fandom.com/wiki/Kurama_Great_Crumbling) | C | Offensive | `narutorpg:kurama_great_crumbling` |
| [Kāma Rasengan](https://naruto.fandom.com/wiki/Kāma_Rasengan) | S | Offensive | `narutorpg:kama_rasengan` |
| [Kāma: Great Burial Wave](https://naruto.fandom.com/wiki/Kāma:_Great_Burial_Wave) | S | Offensive | `narutorpg:kama_great_burial_wave` |
| [Landslide Assault](https://naruto.fandom.com/wiki/Landslide_Assault) | S | Offensive | `narutorpg:landslide_assault` |
| [Last Resort: Eight Gates Crumbling Attack](https://naruto.fandom.com/wiki/Last_Resort:_Eight_Gates_Crumbling_Attack) | B | Offensive | `narutorpg:last_resort_eight_gates_crumbling_attack` |
| [Lava Release Chakra Mode](https://naruto.fandom.com/wiki/Lava_Release_Chakra_Mode) | B | Offensive, Defensive | `narutorpg:lava_release_chakra_mode` (existing) |
| [Lava Release: Scorching Armoured Fist](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Armoured_Fist) | B | Offensive | `narutorpg:lava_release_scorching_armoured_fist` (existing) |
| [Lava Release: Scorching Flow Fist](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Flow_Fist) | B | Offensive | `narutorpg:lava_release_scorching_flow_fist` (existing) |
| [Lava Release: Scorching Flower Great Crumbling](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Flower_Great_Crumbling) | B | Offensive | `narutorpg:lava_release_scorching_flower_great_crumbling` (existing) |
| [Lava Release: Scorching Kick](https://naruto.fandom.com/wiki/Lava_Release:_Scorching_Kick) | B | Offensive | `narutorpg:lava_release_scorching_kick` (existing) |
| [Leaf Broadminded](https://naruto.fandom.com/wiki/Leaf_Broadminded) | C | Offensive | `narutorpg:leaf_broadminded` |
| [Leaf Coiling Whirlwind](https://naruto.fandom.com/wiki/Leaf_Coiling_Whirlwind) | B | Offensive | `narutorpg:leaf_coiling_whirlwind` |
| [Leaf Coiling Wind](https://naruto.fandom.com/wiki/Leaf_Coiling_Wind) | D | Supplementary | `narutorpg:leaf_coiling_wind` |
| [Leaf Dragon God](https://naruto.fandom.com/wiki/Leaf_Dragon_God) | C | Offensive | `narutorpg:leaf_dragon_god` |
| [Leaf Drop](https://naruto.fandom.com/wiki/Leaf_Drop) | C | Offensive | `narutorpg:leaf_drop` |
| [Leaf Flash Kick](https://naruto.fandom.com/wiki/Leaf_Flash_Kick) | C | Offensive | `narutorpg:leaf_flash_kick` |
| [Leaf Gale](https://naruto.fandom.com/wiki/Leaf_Gale) | D | Offensive | `narutorpg:leaf_gale` |
| [Leaf Great Combo Attack](https://naruto.fandom.com/wiki/Leaf_Great_Combo_Attack) | C | Offensive | `narutorpg:leaf_great_combo_attack` |
| [Leaf Great Flash](https://naruto.fandom.com/wiki/Leaf_Great_Flash) | C | Offensive, Supplementary | `narutorpg:leaf_great_flash` |
| [Leaf Great Violent Sky](https://naruto.fandom.com/wiki/Leaf_Great_Violent_Sky) | C | Offensive | `narutorpg:leaf_great_violent_sky` |
| [Leaf Great Whirlwind](https://naruto.fandom.com/wiki/Leaf_Great_Whirlwind) | C | Offensive | `narutorpg:leaf_great_whirlwind` (existing) |
| [Leaf Gust](https://naruto.fandom.com/wiki/Leaf_Gust) | C | Offensive | `narutorpg:leaf_gust` |
| [Leaf Hot Wind](https://naruto.fandom.com/wiki/Leaf_Hot_Wind) | S | Offensive | `narutorpg:leaf_hot_wind` |
| [Leaf Hurricane Bullet](https://naruto.fandom.com/wiki/Leaf_Hurricane_Bullet) | C | Offensive | `narutorpg:leaf_hurricane_bullet` |
| [Leaf Revolving Light](https://naruto.fandom.com/wiki/Leaf_Revolving_Light) | C | Offensive | `narutorpg:leaf_revolving_light` |
| [Leaf Rising Wind](https://naruto.fandom.com/wiki/Leaf_Rising_Wind) | C | Offensive | `narutorpg:leaf_rising_wind` |
| [Leaf Roaring Flame Hurricane](https://naruto.fandom.com/wiki/Leaf_Roaring_Flame_Hurricane) | S | Offensive | `narutorpg:leaf_roaring_flame_hurricane` |
| [Leaf Rock-Destroying Rise](https://naruto.fandom.com/wiki/Leaf_Rock-Destroying_Rise) | C | Offensive | `narutorpg:leaf_rock_destroying_rise` |
| [Leaf Shuriken](https://naruto.fandom.com/wiki/Leaf_Shuriken) | C | Offensive | `narutorpg:leaf_shuriken` |
| [Leaf Springtime of Life Lotus](https://naruto.fandom.com/wiki/Leaf_Springtime_of_Life_Lotus) | S | Offensive | `narutorpg:leaf_springtime_of_life_lotus` |
| [Leaf Strong Whirlwind](https://naruto.fandom.com/wiki/Leaf_Strong_Whirlwind) | B | Offensive | `narutorpg:leaf_strong_whirlwind` |
| [Leaf Sweet Riot Car](https://naruto.fandom.com/wiki/Leaf_Sweet_Riot_Car) | C | Offensive | `narutorpg:leaf_sweet_riot_car` |
| [Leaf Taijutsu: Whirlwind Furious Feet](https://naruto.fandom.com/wiki/Leaf_Taijutsu:_Whirlwind_Furious_Feet) | C | Offensive | `narutorpg:leaf_taijutsu_whirlwind_furious_feet` |
| [Leaf Whirlwind](https://naruto.fandom.com/wiki/Leaf_Whirlwind) | D | Offensive | `narutorpg:leaf_whirlwind` (existing) |
| [Leaf Windstorm](https://naruto.fandom.com/wiki/Leaf_Windstorm) | C | Offensive | `narutorpg:leaf_windstorm` |
| [Leaf-Style: Empty Cicada](https://naruto.fandom.com/wiki/Leaf-Style:_Empty_Cicada) | C | Offensive | `narutorpg:leaf_style_empty_cicada` |
| [Leaping Bullet Shell Tower](https://naruto.fandom.com/wiki/Leaping_Bullet_Shell_Tower) | C | Offensive, Supplementary | `narutorpg:leaping_bullet_shell_tower` (existing) |
| [Leech Gap](https://naruto.fandom.com/wiki/Leech_Gap) | C | Offensive | `narutorpg:leech_gap` |
| [Legend of the White Fang](https://naruto.fandom.com/wiki/Legend_of_the_White_Fang) | C | Offensive | `narutorpg:legend_of_the_white_fang` (existing) |
| [Liger Two Platoons Bomb](https://naruto.fandom.com/wiki/Liger_Two_Platoons_Bomb) | B | Offensive | `narutorpg:liger_two_platoons_bomb` (existing) |
| [Lightning Cutter Combo](https://naruto.fandom.com/wiki/Lightning_Cutter_Combo) | C | Offensive | `narutorpg:lightning_cutter_combo` (existing) |
| [Lightning Cutter: Double Lightning Quake](https://naruto.fandom.com/wiki/Lightning_Cutter:_Double_Lightning_Quake) | C | Offensive | `narutorpg:lightning_cutter_double_lightning_quake` (existing) |
| [Lightning Flash (Game)](https://naruto.fandom.com/wiki/Lightning_Flash_(Game)) | C | Offensive | `narutorpg:lightning_flash_game` |
| [Lightning Flash Lion Combo](https://naruto.fandom.com/wiki/Lightning_Flash_Lion_Combo) | C | Offensive | `narutorpg:lightning_flash_lion_combo` (existing) |
| [Lightning Release: Double Lariat](https://naruto.fandom.com/wiki/Lightning_Release:_Double_Lariat) | A | Offensive | `narutorpg:lightning_release_double_lariat` (existing) |
| [Lightning Release: Lariat](https://naruto.fandom.com/wiki/Lightning_Release:_Lariat) | A | Offensive | `narutorpg:lightning_release_lariat` (existing) |
| [Lightning Speed (Minato)](https://naruto.fandom.com/wiki/Lightning_Speed_(Minato)) | A | Offensive | `narutorpg:lightning_speed_minato` |
| [Lion Combo](https://naruto.fandom.com/wiki/Lion_Combo) | C | Offensive | `narutorpg:lion_combo` |
| [Lion Flame Bullet](https://naruto.fandom.com/wiki/Lion_Flame_Bullet) | C | Offensive | `narutorpg:lion_flame_bullet` (existing) |
| [Little Toe Attack](https://naruto.fandom.com/wiki/Little_Toe_Attack) | S | Offensive | `narutorpg:little_toe_attack` |
| [Love Roar](https://naruto.fandom.com/wiki/Love_Roar) | C | Offensive | `narutorpg:love_roar` |
| [Married Couple Combination: Adamantine Impact](https://naruto.fandom.com/wiki/Married_Couple_Combination:_Adamantine_Impact) | B | Offensive | `narutorpg:married_couple_combination_adamantine_impact` |
| [Maximum Entry](https://naruto.fandom.com/wiki/Maximum_Entry) | C | Offensive | `narutorpg:maximum_entry` |
| [Mecha-Naruto Taijutsu Combo](https://naruto.fandom.com/wiki/Mecha-Naruto_Taijutsu_Combo) | D | Defensive | `narutorpg:mecha_naruto_taijutsu_combo` |
| [Medical Empress Gambler](https://naruto.fandom.com/wiki/Medical_Empress_Gambler) | B | Offensive | `narutorpg:medical_empress_gambler` |
| [Medical Ninjutsu: Strong Flash](https://naruto.fandom.com/wiki/Medical_Ninjutsu:_Strong_Flash) | C | Offensive | `narutorpg:medical_ninjutsu_strong_flash` |
| [Medullary Cut](https://naruto.fandom.com/wiki/Medullary_Cut) | C | Offensive | `narutorpg:medullary_cut` |
| [Middle-Aged Attack](https://naruto.fandom.com/wiki/Middle-Aged_Attack) | C | Offensive | `narutorpg:middle_aged_attack` |
| [Might Cyclone](https://naruto.fandom.com/wiki/Might_Cyclone) | C | Offensive | `narutorpg:might_cyclone` |
| [Mingling Arm Flying Thrust](https://naruto.fandom.com/wiki/Mingling_Arm_Flying_Thrust) | C | Offensive | `narutorpg:mingling_arm_flying_thrust` |
| [Moonlight](https://naruto.fandom.com/wiki/Moonlight) | C | Offensive | `narutorpg:moonlight` |
| [Morning Peacock](https://naruto.fandom.com/wiki/Morning_Peacock) | A | Offensive | `narutorpg:morning_peacock` |
| [Mountain Face](https://naruto.fandom.com/wiki/Mountain_Face) | C | Offensive | `narutorpg:mountain_face` |
| [Multiple Connected Feet](https://naruto.fandom.com/wiki/Multiple_Connected_Feet) | B | Offensive | `narutorpg:multiple_connected_feet` |
| [Multiple Connected Fists](https://naruto.fandom.com/wiki/Multiple_Connected_Fists) | B | Offensive | `narutorpg:multiple_connected_fists` |
| [Multiple Connected Fists: Rakshasa](https://naruto.fandom.com/wiki/Multiple_Connected_Fists:_Rakshasa) | A | Offensive | `narutorpg:multiple_connected_fists_rakshasa` |
| [Multiple Connected Whirlwinds](https://naruto.fandom.com/wiki/Multiple_Connected_Whirlwinds) | B | Offensive, Defensive | `narutorpg:multiple_connected_whirlwinds` |
| [Multiple Shadows Attack Combo](https://naruto.fandom.com/wiki/Multiple_Shadows_Attack_Combo) | S | Offensive | `narutorpg:multiple_shadows_attack_combo` |
| [Nagi Snake Tail](https://naruto.fandom.com/wiki/Nagi_Snake_Tail) | C | Offensive, Defensive | `narutorpg:nagi_snake_tail` |
| [Naruto Punch](https://naruto.fandom.com/wiki/Naruto_Punch) | C | Offensive | `narutorpg:naruto_punch` |
| [Naruto Region Combo](https://naruto.fandom.com/wiki/Naruto_Region_Combo) | A | Offensive | `narutorpg:naruto_region_combo` |
| [Naruto Uzumaki Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Combo) | B | Offensive | `narutorpg:naruto_uzumaki_combo` |
| [Naruto Uzumaki Combo Attack](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Combo_Attack) | C | Offensive | `narutorpg:naruto_uzumaki_combo_attack` |
| [Naruto Uzumaki Five Thousand Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Five_Thousand_Combo) | S | Offensive | `narutorpg:naruto_uzumaki_five_thousand_combo` |
| [Naruto Uzumaki Spiralling Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Spiralling_Combo) | C | Offensive | `narutorpg:naruto_uzumaki_spiralling_combo` |
| [Naruto Uzumaki Two Thousand Combo](https://naruto.fandom.com/wiki/Naruto_Uzumaki_Two_Thousand_Combo) | A | Offensive | `narutorpg:naruto_uzumaki_two_thousand_combo` |
| [Naruto Uzumaki's Shinobi Handbook (Ultimate Ninja 3)](https://naruto.fandom.com/wiki/Naruto_Uzumaki's_Shinobi_Handbook_(Ultimate_Ninja_3)) | S | Offensive | `narutorpg:naruto_uzumaki_s_shinobi_handbook_ultimate_ninja_3` |
| [New Naruto Uzumaki Combo](https://naruto.fandom.com/wiki/New_Naruto_Uzumaki_Combo) | C | Offensive | `narutorpg:new_naruto_uzumaki_combo` |
| [New: Summoning Three-Way Deadlock](https://naruto.fandom.com/wiki/New:_Summoning_Three-Way_Deadlock) | A | Offensive | `narutorpg:new_summoning_three_way_deadlock` |
| [Newly Invented: Lightning Swift Rasengan](https://naruto.fandom.com/wiki/Newly_Invented:_Lightning_Swift_Rasengan) | B | Offensive | `narutorpg:newly_invented_lightning_swift_rasengan` (existing) |
| [Night Guy](https://naruto.fandom.com/wiki/Night_Guy) | S | Offensive | `narutorpg:night_guy` |
| [Night Phoenix](https://naruto.fandom.com/wiki/Night_Phoenix) | C | Offensive | `narutorpg:night_phoenix` |
| [Nine-Tailed Fox Barrage](https://naruto.fandom.com/wiki/Nine-Tailed_Fox_Barrage) | C | Offensive | `narutorpg:nine_tailed_fox_barrage` |
| [Nine-Tailed Fox Rapid Combo](https://naruto.fandom.com/wiki/Nine-Tailed_Fox_Rapid_Combo) | C | Offensive | `narutorpg:nine_tailed_fox_rapid_combo` |
| [Nine-Tailed Naruto Wild Attack](https://naruto.fandom.com/wiki/Nine-Tailed_Naruto_Wild_Attack) | C | Offensive, Defensive | `narutorpg:nine_tailed_naruto_wild_attack` |
| [Nine-Tails Surge](https://naruto.fandom.com/wiki/Nine-Tails_Surge) | C | Offensive, Defensive | `narutorpg:nine_tails_surge` |
| [Nine-Tails' Claw: Fission](https://naruto.fandom.com/wiki/Nine-Tails'_Claw:_Fission) | C | Offensive | `narutorpg:nine_tails_claw_fission` |
| [Nine-Tails' Claw: Great Rampage](https://naruto.fandom.com/wiki/Nine-Tails'_Claw:_Great_Rampage) | C | Offensive | `narutorpg:nine_tails_claw_great_rampage` |
| [Nine-Tails' Power](https://naruto.fandom.com/wiki/Nine-Tails'_Power) | C | Offensive | `narutorpg:nine_tails_power` |
| [Ninja Art: Bare-Handed Blade Block](https://naruto.fandom.com/wiki/Ninja_Art:_Bare-Handed_Blade_Block) | D | Defensive | `narutorpg:ninja_art_bare_handed_blade_block` |
| [Ninja Art: Moonlight Beauty](https://naruto.fandom.com/wiki/Ninja_Art:_Moonlight_Beauty) | C | Offensive, Defensive | `narutorpg:ninja_art_moonlight_beauty` |
| [Ninken Ninja Art: Dance of the Dust Cloud](https://naruto.fandom.com/wiki/Ninken_Ninja_Art:_Dance_of_the_Dust_Cloud) | B | Supplementary | `narutorpg:ninken_ninja_art_dance_of_the_dust_cloud` |
| [Noisy Riot: Super Beast Imitating Drawing Picture Scroll](https://naruto.fandom.com/wiki/Noisy_Riot:_Super_Beast_Imitating_Drawing_Picture_Scroll) | B | Offensive | `narutorpg:noisy_riot_super_beast_imitating_drawing_picture_scroll` |
| [Nosedive](https://naruto.fandom.com/wiki/Nosedive) | A | Offensive | `narutorpg:nosedive` |
| [Nunchaku Combo](https://naruto.fandom.com/wiki/Nunchaku_Combo) | C | Offensive | `narutorpg:nunchaku_combo` |
| [Octopus Hug](https://naruto.fandom.com/wiki/Octopus_Hug) | C | Offensive | `narutorpg:octopus_hug` |
| [One Thousand Years of Death](https://naruto.fandom.com/wiki/One_Thousand_Years_of_Death) | E | Offensive | `narutorpg:one_thousand_years_of_death` |
| [One-Man Front Lotus](https://naruto.fandom.com/wiki/One-Man_Front_Lotus) | S | Offensive | `narutorpg:one_man_front_lotus` |
| [Oneself's Rule](https://naruto.fandom.com/wiki/Oneself's_Rule) | C | Offensive | `narutorpg:oneself_s_rule` |
| [Order of Glamour](https://naruto.fandom.com/wiki/Order_of_Glamour) | C | Offensive, Supplementary | `narutorpg:order_of_glamour` (existing) |
| [Ostrich Aerial](https://naruto.fandom.com/wiki/Ostrich_Aerial) | C | Offensive | `narutorpg:ostrich_aerial` |
| [Ostrich Meteor Blast](https://naruto.fandom.com/wiki/Ostrich_Meteor_Blast) | C | Offensive | `narutorpg:ostrich_meteor_blast` |
| [Ostrich Rising Dragon Bullet](https://naruto.fandom.com/wiki/Ostrich_Rising_Dragon_Bullet) | C | Offensive | `narutorpg:ostrich_rising_dragon_bullet` |
| [Ostrich Whirlwind Attack](https://naruto.fandom.com/wiki/Ostrich_Whirlwind_Attack) | C | Offensive | `narutorpg:ostrich_whirlwind_attack` |
| [Outcry of Earth](https://naruto.fandom.com/wiki/Outcry_of_Earth) | C | Offensive, Defensive | `narutorpg:outcry_of_earth` |
| [Overwhelm: Rasengan](https://naruto.fandom.com/wiki/Overwhelm:_Rasengan) | A | Offensive | `narutorpg:overwhelm_rasengan` |
| [Owl Drop](https://naruto.fandom.com/wiki/Owl_Drop) | C | Offensive | `narutorpg:owl_drop` |
| [Owl Guardian Pursuit](https://naruto.fandom.com/wiki/Owl_Guardian_Pursuit) | C | Offensive | `narutorpg:owl_guardian_pursuit` |
| [Pacification of the Grounds](https://naruto.fandom.com/wiki/Pacification_of_the_Grounds) | A | Offensive | `narutorpg:pacification_of_the_grounds` |
| [Pair's Kamui](https://naruto.fandom.com/wiki/Pair's_Kamui) | A | Offensive | `narutorpg:pair_s_kamui` (existing) |
| [Palm Bottom](https://naruto.fandom.com/wiki/Palm_Bottom) | B | Offensive | `narutorpg:palm_bottom` (existing) |
| [Palm Wave](https://naruto.fandom.com/wiki/Palm_Wave) | C | Offensive | `narutorpg:palm_wave` |
| [Pampas](https://naruto.fandom.com/wiki/Pampas) | B | Offensive | `narutorpg:pampas` |
| [Parachute](https://naruto.fandom.com/wiki/Parachute) | C | Offensive | `narutorpg:parachute` |
| [Parasitic Destruction Insect Fang](https://naruto.fandom.com/wiki/Parasitic_Destruction_Insect_Fang) | B | Offensive | `narutorpg:parasitic_destruction_insect_fang` |
| [Partial Multi-Size Leg Technique](https://naruto.fandom.com/wiki/Partial_Multi-Size_Leg_Technique) | C | Offensive, Supplementary | `narutorpg:partial_multi_size_leg_technique` (existing) |
| [Passing Fang](https://naruto.fandom.com/wiki/Passing_Fang) | D | Offensive | `narutorpg:passing_fang` (existing) |
| [Peerless Great Flame Bullet](https://naruto.fandom.com/wiki/Peerless_Great_Flame_Bullet) | C | Offensive | `narutorpg:peerless_great_flame_bullet` (existing) |
| [Peerless Hurricane Formation](https://naruto.fandom.com/wiki/Peerless_Hurricane_Formation) | C | Offensive | `narutorpg:peerless_hurricane_formation` |
| [Peony Snow](https://naruto.fandom.com/wiki/Peony_Snow) | C | Offensive | `narutorpg:peony_snow` |
| [Peregrine Falcon Drop](https://naruto.fandom.com/wiki/Peregrine_Falcon_Drop) | C | Offensive | `narutorpg:peregrine_falcon_drop` |
| [Petal-Dancing Flower Garden](https://naruto.fandom.com/wiki/Petal-Dancing_Flower_Garden) | C | Offensive | `narutorpg:petal_dancing_flower_garden` |
| [Piercing Burning Emotions](https://naruto.fandom.com/wiki/Piercing_Burning_Emotions) | S | Offensive | `narutorpg:piercing_burning_emotions` |
| [Piercing Funeral Foot](https://naruto.fandom.com/wiki/Piercing_Funeral_Foot) | C | Offensive | `narutorpg:piercing_funeral_foot` |
| [Piercing Stab](https://naruto.fandom.com/wiki/Piercing_Stab) | C | Offensive | `narutorpg:piercing_stab` |
| [Pinching Rage: Rasengan](https://naruto.fandom.com/wiki/Pinching_Rage:_Rasengan) | C | Offensive | `narutorpg:pinching_rage_rasengan` |
| [Piston Fist](https://naruto.fandom.com/wiki/Piston_Fist) | A | Offensive | `narutorpg:piston_fist` (existing) |
| [Piston Fist: Style One](https://naruto.fandom.com/wiki/Piston_Fist:_Style_One) | A | Offensive, Supplementary | `narutorpg:piston_fist_style_one` (existing) |
| [Piston Fist: Style Zero](https://naruto.fandom.com/wiki/Piston_Fist:_Style_Zero) | A | Offensive | `narutorpg:piston_fist_style_zero` |
| [Poisonous Claw](https://naruto.fandom.com/wiki/Poisonous_Claw) | C | Offensive | `narutorpg:poisonous_claw` |
| [Power Bomb](https://naruto.fandom.com/wiki/Power_Bomb) | C | Offensive | `narutorpg:power_bomb` |
| [Power of the Hokage](https://naruto.fandom.com/wiki/Power_of_the_Hokage) | C | Offensive | `narutorpg:power_of_the_hokage` |
| [Pressurisation](https://naruto.fandom.com/wiki/Pressurisation) | A | Offensive | `narutorpg:pressurisation` |
| [Prison Flame Funeral](https://naruto.fandom.com/wiki/Prison_Flame_Funeral) | B | Offensive | `narutorpg:prison_flame_funeral` (existing) |
| [Prison Gate Palm Bottom](https://naruto.fandom.com/wiki/Prison_Gate_Palm_Bottom) | C | Offensive | `narutorpg:prison_gate_palm_bottom` |
| [Protecting Eight Trigrams One Hundred Twenty-Eight Palms](https://naruto.fandom.com/wiki/Protecting_Eight_Trigrams_One_Hundred_Twenty-Eight_Palms) | B | Offensive, Defensive | `narutorpg:protecting_eight_trigrams_one_hundred_twenty_eight_palms` |
| [Protecting Eight Trigrams Sixty-Four Palms](https://naruto.fandom.com/wiki/Protecting_Eight_Trigrams_Sixty-Four_Palms) | B | Offensive, Defensive | `narutorpg:protecting_eight_trigrams_sixty_four_palms` (existing) |
| [Pulverisation](https://naruto.fandom.com/wiki/Pulverisation) | A | Offensive | `narutorpg:pulverisation` |
| [Puppet White Attack](https://naruto.fandom.com/wiki/Puppet_White_Attack) | B | Offensive | `narutorpg:puppet_white_attack` |
| [Puppet: Dance of the Cherry Blossom](https://naruto.fandom.com/wiki/Puppet:_Dance_of_the_Cherry_Blossom) | B | Offensive | `narutorpg:puppet_dance_of_the_cherry_blossom` |
| [Puppet: Rotation Attack](https://naruto.fandom.com/wiki/Puppet:_Rotation_Attack) | C | Offensive | `narutorpg:puppet_rotation_attack` |
| [Puppet: Shadow Actor](https://naruto.fandom.com/wiki/Puppet:_Shadow_Actor) | C | Offensive | `narutorpg:puppet_shadow_actor` |
| [Puppet: Spring Punishing Strike](https://naruto.fandom.com/wiki/Puppet:_Spring_Punishing_Strike) | B | Offensive | `narutorpg:puppet_spring_punishing_strike` |
| [Pure Land of Paradise](https://naruto.fandom.com/wiki/Pure_Land_of_Paradise) | C | Offensive | `narutorpg:pure_land_of_paradise` |
| [Quaking Leg](https://naruto.fandom.com/wiki/Quaking_Leg) | C | Offensive | `narutorpg:quaking_leg` |
| [Rabbit Hair Needle](https://naruto.fandom.com/wiki/Rabbit_Hair_Needle) | A | Offensive | `narutorpg:rabbit_hair_needle` (existing) |
| [Raging Pounce](https://naruto.fandom.com/wiki/Raging_Pounce) | B | Offensive | `narutorpg:raging_pounce` |
| [Rampaging Barrage](https://naruto.fandom.com/wiki/Rampaging_Barrage) | C | Offensive | `narutorpg:rampaging_barrage` |
| [Rasengan: "Ensemble"](https://naruto.fandom.com/wiki/Rasengan:_"Ensemble") | S | Offensive | `narutorpg:rasengan_ensemble` |
| [Rasengan: Aerial Attack](https://naruto.fandom.com/wiki/Rasengan:_Aerial_Attack) | C | Offensive | `narutorpg:rasengan_aerial_attack` |
| [Rasengan: Crown of the Head](https://naruto.fandom.com/wiki/Rasengan:_Crown_of_the_Head) | C | Offensive | `narutorpg:rasengan_crown_of_the_head` |
| [Rasengan: Hail](https://naruto.fandom.com/wiki/Rasengan:_Hail) | C | Offensive | `narutorpg:rasengan_hail` |
| [Rasengan: Nosedive](https://naruto.fandom.com/wiki/Rasengan:_Nosedive) | C | Offensive | `narutorpg:rasengan_nosedive` |
| [Rasengan: Parachute](https://naruto.fandom.com/wiki/Rasengan:_Parachute) | C | Offensive | `narutorpg:rasengan_parachute` |
| [Red Chakra](https://naruto.fandom.com/wiki/Red_Chakra) | C | Offensive | `narutorpg:red_chakra` |
| [Repent!](https://naruto.fandom.com/wiki/Repent!) | C | Offensive | `narutorpg:repent` |
| [Reverse Lotus](https://naruto.fandom.com/wiki/Reverse_Lotus) | A | Offensive | `narutorpg:reverse_lotus` |
| [Right Pinching Melody](https://naruto.fandom.com/wiki/Right_Pinching_Melody) | S | Offensive | `narutorpg:right_pinching_melody` |
| [Righteous Fist Thrust](https://naruto.fandom.com/wiki/Righteous_Fist_Thrust) | C | Offensive | `narutorpg:righteous_fist_thrust` |
| [Rinne Six Paths](https://naruto.fandom.com/wiki/Rinne_Six_Paths) | B | Offensive | `narutorpg:rinne_six_paths` |
| [Rising Bomber](https://naruto.fandom.com/wiki/Rising_Bomber) | C | Offensive | `narutorpg:rising_bomber` (existing) |
| [Roaring Run: Great Strength Ice Strike](https://naruto.fandom.com/wiki/Roaring_Run:_Great_Strength_Ice_Strike) | C | Offensive | `narutorpg:roaring_run_great_strength_ice_strike` (existing) |
| [Rock Avalanche Formation](https://naruto.fandom.com/wiki/Rock_Avalanche_Formation) | B | Offensive | `narutorpg:rock_avalanche_formation` |
| [Rough Rampage](https://naruto.fandom.com/wiki/Rough_Rampage) | C | Offensive | `narutorpg:rough_rampage` |
| [Rumoured Endeavour Star](https://naruto.fandom.com/wiki/Rumoured_Endeavour_Star) | S | Offensive | `narutorpg:rumoured_endeavour_star` |
| [Sadistic Combination](https://naruto.fandom.com/wiki/Sadistic_Combination) | C | Offensive | `narutorpg:sadistic_combination` (existing) |
| [Sage Combo](https://naruto.fandom.com/wiki/Sage_Combo) | A | Offensive | `narutorpg:sage_combo` (existing) |
| [Samehada's Gonna Shank Ya!](https://naruto.fandom.com/wiki/Samehada's_Gonna_Shank_Ya!) | C | Offensive | `narutorpg:samehada_s_gonna_shank_ya` |
| [Sand Formation Combo](https://naruto.fandom.com/wiki/Sand_Formation_Combo) | C | Offensive | `narutorpg:sand_formation_combo` (existing) |
| [Sand Taijutsu Tsunami](https://naruto.fandom.com/wiki/Sand_Taijutsu_Tsunami) | B | Offensive | `narutorpg:sand_taijutsu_tsunami` |
| [Scale Wrapping Rotating Flight](https://naruto.fandom.com/wiki/Scale_Wrapping_Rotating_Flight) | C | Offensive | `narutorpg:scale_wrapping_rotating_flight` |
| [Scarlet Invitation](https://naruto.fandom.com/wiki/Scarlet_Invitation) | C | Offensive | `narutorpg:scarlet_invitation` |
| [Scent Blossom Colouring](https://naruto.fandom.com/wiki/Scent_Blossom_Colouring) | C | Offensive | `narutorpg:scent_blossom_colouring` |
| [Seasickness Fist](https://naruto.fandom.com/wiki/Seasickness_Fist) | C | Offensive, Defensive | `narutorpg:seasickness_fist` |
| [Secret Ingredient](https://naruto.fandom.com/wiki/Secret_Ingredient) | C | Offensive | `narutorpg:secret_ingredient` |
| [Secret Taijutsu: Super Fire Release Illusion Great Shuriken Two-Stage Technique](https://naruto.fandom.com/wiki/Secret_Taijutsu:_Super_Fire_Release_Illusion_Great_Shuriken_Two-Stage_Technique) | B | Offensive | `narutorpg:secret_taijutsu_super_fire_release_illusion_great_shuriken_two_stage_technique` (existing) |
| [Secret Taijutsu: Wild Stone Dance Flame Killing Kunai Endless Style Multi-Shot Technique](https://naruto.fandom.com/wiki/Secret_Taijutsu:_Wild_Stone_Dance_Flame_Killing_Kunai_Endless_Style_Multi-Shot_Technique) | B | Offensive | `narutorpg:secret_taijutsu_wild_stone_dance_flame_killing_kunai_endless_style_multi_shot_technique` (existing) |
| [Self-Taught: Instant Attack](https://naruto.fandom.com/wiki/Self-Taught:_Instant_Attack) | C | Offensive | `narutorpg:self_taught_instant_attack` |
| [Self-Taught: Youth Kick](https://naruto.fandom.com/wiki/Self-Taught:_Youth_Kick) | C | Offensive | `narutorpg:self_taught_youth_kick` |
| [Serial Kick](https://naruto.fandom.com/wiki/Serial_Kick) | C | Offensive | `narutorpg:serial_kick` |
| [Shadow Clone Elite Bullet](https://naruto.fandom.com/wiki/Shadow_Clone_Elite_Bullet) | C | Offensive | `narutorpg:shadow_clone_elite_bullet` |
| [Shadow Clone Kick](https://naruto.fandom.com/wiki/Shadow_Clone_Kick) | C | Offensive | `narutorpg:shadow_clone_kick` |
| [Shadow Clone Roundhouse Kick](https://naruto.fandom.com/wiki/Shadow_Clone_Roundhouse_Kick) | C | Offensive | `narutorpg:shadow_clone_roundhouse_kick` |
| [Shadow Clone: Upper](https://naruto.fandom.com/wiki/Shadow_Clone:_Upper) | C | Offensive | `narutorpg:shadow_clone_upper` |
| [Shadow Stroke](https://naruto.fandom.com/wiki/Shadow_Stroke) | C | Offensive, Supplementary | `narutorpg:shadow_stroke` (existing) |
| [Shadow of the Dancing Leaf](https://naruto.fandom.com/wiki/Shadow_of_the_Dancing_Leaf) | C | Supplementary | `narutorpg:shadow_of_the_dancing_leaf` |
| [Shining Ostrich Kick](https://naruto.fandom.com/wiki/Shining_Ostrich_Kick) | C | Offensive | `narutorpg:shining_ostrich_kick` |
| [Shock Wave Blossom](https://naruto.fandom.com/wiki/Shock_Wave_Blossom) | C | Offensive | `narutorpg:shock_wave_blossom` |
| [Shock Wave Thrust](https://naruto.fandom.com/wiki/Shock_Wave_Thrust) | C | Offensive | `narutorpg:shock_wave_thrust` |
| [Shrike](https://naruto.fandom.com/wiki/Shrike) | C | Offensive | `narutorpg:shrike` |
| [Shuriken: Double Knockdown Technique](https://naruto.fandom.com/wiki/Shuriken:_Double_Knockdown_Technique) | C | Offensive | `narutorpg:shuriken_double_knockdown_technique` |
| [Silent Fist](https://naruto.fandom.com/wiki/Silent_Fist) | C | Offensive | `narutorpg:silent_fist` |
| [Single Claw Bisection](https://naruto.fandom.com/wiki/Single_Claw_Bisection) | C | Offensive | `narutorpg:single_claw_bisection` |
| [Skewering Headbutt (Me)](https://naruto.fandom.com/wiki/Skewering_Headbutt_(Me)) | C | Offensive | `narutorpg:skewering_headbutt_me` |
| [Sky Claw Double Scratch](https://naruto.fandom.com/wiki/Sky_Claw_Double_Scratch) | C | Offensive | `narutorpg:sky_claw_double_scratch` |
| [Sky Shaking Fist](https://naruto.fandom.com/wiki/Sky_Shaking_Fist) | C | Offensive | `narutorpg:sky_shaking_fist` |
| [Snake (jutsu)](https://naruto.fandom.com/wiki/Snake_(jutsu)) | A | Offensive | `narutorpg:snake_jutsu` |
| [Snake Wild Strike](https://naruto.fandom.com/wiki/Snake_Wild_Strike) | C | Offensive | `narutorpg:snake_wild_strike` |
| [Soaring Bird Crusher](https://naruto.fandom.com/wiki/Soaring_Bird_Crusher) | C | Offensive | `narutorpg:soaring_bird_crusher` |
| [Soaring Shine Falling Bullet](https://naruto.fandom.com/wiki/Soaring_Shine_Falling_Bullet) | C | Offensive | `narutorpg:soaring_shine_falling_bullet` |
| [Soaring Swallow Stepping Foot](https://naruto.fandom.com/wiki/Soaring_Swallow_Stepping_Foot) | C | Offensive | `narutorpg:soaring_swallow_stepping_foot` |
| [Soft Neck Throw](https://naruto.fandom.com/wiki/Soft_Neck_Throw) | C | Offensive | `narutorpg:soft_neck_throw` |
| [Sonic Blast](https://naruto.fandom.com/wiki/Sonic_Blast) | C | Offensive | `narutorpg:sonic_blast` |
| [Spear Attack Shining Horn](https://naruto.fandom.com/wiki/Spear_Attack_Shining_Horn) | C | Offensive | `narutorpg:spear_attack_shining_horn` (existing) |
| [Special Attack Rasengan](https://naruto.fandom.com/wiki/Special_Attack_Rasengan) | C | Offensive, Defensive | `narutorpg:special_attack_rasengan` |
| [Spiked Human Bullet Tank](https://naruto.fandom.com/wiki/Spiked_Human_Bullet_Tank) | C | Offensive | `narutorpg:spiked_human_bullet_tank` (existing) |
| [Spinning Top Swing](https://naruto.fandom.com/wiki/Spinning_Top_Swing) | C | Offensive | `narutorpg:spinning_top_swing` |
| [Spiral Boruto Storm](https://naruto.fandom.com/wiki/Spiral_Boruto_Storm) | C | Offensive | `narutorpg:spiral_boruto_storm` (existing) |
| [Spiral Lotus](https://naruto.fandom.com/wiki/Spiral_Lotus) | C | Offensive | `narutorpg:spiral_lotus` |
| [Spirit Foot](https://naruto.fandom.com/wiki/Spirit_Foot) | C | Offensive | `narutorpg:spirit_foot` |
| [Steam Dash](https://naruto.fandom.com/wiki/Steam_Dash) | B | Offensive | `narutorpg:steam_dash` (existing) |
| [Sticky Bite](https://naruto.fandom.com/wiki/Sticky_Bite) | C | Offensive | `narutorpg:sticky_bite` |
| [Stomach Reflection](https://naruto.fandom.com/wiki/Stomach_Reflection) | C | Offensive, Defensive | `narutorpg:stomach_reflection` |
| [Stone Pillar Destruction](https://naruto.fandom.com/wiki/Stone_Pillar_Destruction) | C | Offensive | `narutorpg:stone_pillar_destruction` |
| [Strong Fist](https://naruto.fandom.com/wiki/Strong_Fist) | C | Offensive | `narutorpg:strong_fist` (existing) |
| [Sukunahikona: God Hunter](https://naruto.fandom.com/wiki/Sukunahikona:_God_Hunter) | S | Offensive | `narutorpg:sukunahikona_god_hunter` |
| [Super Excellent Hyper Attack](https://naruto.fandom.com/wiki/Super_Excellent_Hyper_Attack) | C | Offensive | `narutorpg:super_excellent_hyper_attack` |
| [Super Fang Wolf Fang](https://naruto.fandom.com/wiki/Super_Fang_Wolf_Fang) | B | Offensive | `narutorpg:super_fang_wolf_fang` (existing) |
| [Super Inner Sakura](https://naruto.fandom.com/wiki/Super_Inner_Sakura) | C | Offensive | `narutorpg:super_inner_sakura` |
| [Super Light Foot](https://naruto.fandom.com/wiki/Super_Light_Foot) | C | Offensive | `narutorpg:super_light_foot` |
| [Super Open Hand Slap](https://naruto.fandom.com/wiki/Super_Open_Hand_Slap) | B | Offensive | `narutorpg:super_open_hand_slap` (existing) |
| [Super Pressure](https://naruto.fandom.com/wiki/Super_Pressure) | C | Offensive | `narutorpg:super_pressure` |
| [Super: Human Bullet Tank](https://naruto.fandom.com/wiki/Super:_Human_Bullet_Tank) | C | Offensive, Defensive | `narutorpg:super_human_bullet_tank` (existing) |
| [Surging Fire Wild Dance](https://naruto.fandom.com/wiki/Surging_Fire_Wild_Dance) | C | Offensive | `narutorpg:surging_fire_wild_dance` |
| [Susanoo: Attack](https://naruto.fandom.com/wiki/Susanoo:_Attack) | B | Offensive | `narutorpg:susanoo_attack` |
| [Susanoo: Sever](https://naruto.fandom.com/wiki/Susanoo:_Sever) | B | Offensive | `narutorpg:susanoo_sever` |
| [Sword Combo](https://naruto.fandom.com/wiki/Sword_Combo) | C | Offensive | `narutorpg:sword_combo` |
| [Sword of Kusanagi: Hawk Flash](https://naruto.fandom.com/wiki/Sword_of_Kusanagi:_Hawk_Flash) | C | Offensive | `narutorpg:sword_of_kusanagi_hawk_flash` |
| [Sōshūga](https://naruto.fandom.com/wiki/Sōshūga) | C | Offensive | `narutorpg:soshuga` |
| [Tail Chasing Fang Fang Rotating Fang](https://naruto.fandom.com/wiki/Tail_Chasing_Fang_Fang_Rotating_Fang) | B | Offensive | `narutorpg:tail_chasing_fang_fang_rotating_fang` (existing) |
| [Tail Sweep](https://naruto.fandom.com/wiki/Tail_Sweep) | C | Offensive | `narutorpg:tail_sweep` |
| [Tailed Beast Eight Twists](https://naruto.fandom.com/wiki/Tailed_Beast_Eight_Twists) | C | Offensive | `narutorpg:tailed_beast_eight_twists` |
| [Tailed Beast Flash Bullet](https://naruto.fandom.com/wiki/Tailed_Beast_Flash_Bullet) | C | Offensive | `narutorpg:tailed_beast_flash_bullet` |
| [Tailed Beast Full Charge](https://naruto.fandom.com/wiki/Tailed_Beast_Full_Charge) | B | Offensive | `narutorpg:tailed_beast_full_charge` |
| [Tailed Beast Lariat](https://naruto.fandom.com/wiki/Tailed_Beast_Lariat) | C | Offensive | `narutorpg:tailed_beast_lariat` |
| [Tailed Beast Seventeen Whirlwinds Attack](https://naruto.fandom.com/wiki/Tailed_Beast_Seventeen_Whirlwinds_Attack) | B | Offensive | `narutorpg:tailed_beast_seventeen_whirlwinds_attack` |
| [Tailed Beast Tail](https://naruto.fandom.com/wiki/Tailed_Beast_Tail) | C | Offensive | `narutorpg:tailed_beast_tail` |
| [Takigakure Whirlwind](https://naruto.fandom.com/wiki/Takigakure_Whirlwind) | C | Offensive | `narutorpg:takigakure_whirlwind` (existing) |
| [Team-work is Outstanding](https://naruto.fandom.com/wiki/Team-work_is_Outstanding) | B | Offensive | `narutorpg:team_work_is_outstanding` |
| [Tearing Fang](https://naruto.fandom.com/wiki/Tearing_Fang) | C | Offensive | `narutorpg:tearing_fang` |
| [Ten-Finger Drilling Bullets](https://naruto.fandom.com/wiki/Ten-Finger_Drilling_Bullets) | B | Offensive | `narutorpg:ten_finger_drilling_bullets` (existing) |
| [Tenketsu Blocking Strikes](https://naruto.fandom.com/wiki/Tenketsu_Blocking_Strikes) | C | Offensive | `narutorpg:tenketsu_blocking_strikes` |
| [The Inheritance of the Will of Fire](https://naruto.fandom.com/wiki/The_Inheritance_of_the_Will_of_Fire) | A | Offensive | `narutorpg:the_inheritance_of_the_will_of_fire` (existing) |
| [The Red-Hot Habanero](https://naruto.fandom.com/wiki/The_Red-Hot_Habanero) | C | Offensive | `narutorpg:the_red_hot_habanero` |
| [Thistle](https://naruto.fandom.com/wiki/Thistle) | B | Offensive | `narutorpg:thistle` |
| [Thorn](https://naruto.fandom.com/wiki/Thorn) | B | Offensive | `narutorpg:thorn` |
| [Thorn Explosion](https://naruto.fandom.com/wiki/Thorn_Explosion) | C | Offensive | `narutorpg:thorn_explosion` |
| [Threaded Tree](https://naruto.fandom.com/wiki/Threaded_Tree) | B | Offensive | `narutorpg:threaded_tree` (existing) |
| [Three-Tails' Rampage](https://naruto.fandom.com/wiki/Three-Tails'_Rampage) | C | Offensive | `narutorpg:three_tails_rampage` |
| [Thunder Sabre](https://naruto.fandom.com/wiki/Thunder_Sabre) | C | Offensive | `narutorpg:thunder_sabre` |
| [Thunderclap Drill](https://naruto.fandom.com/wiki/Thunderclap_Drill) | C | Offensive | `narutorpg:thunderclap_drill` (existing) |
| [Tiger Lily](https://naruto.fandom.com/wiki/Tiger_Lily) | B | Offensive | `narutorpg:tiger_lily` |
| [Toad Sword Beheading](https://naruto.fandom.com/wiki/Toad_Sword_Beheading) | C | Offensive | `narutorpg:toad_sword_beheading` |
| [Toad Three Slashes](https://naruto.fandom.com/wiki/Toad_Three_Slashes) | C | Offensive | `narutorpg:toad_three_slashes` |
| [Torn Castle](https://naruto.fandom.com/wiki/Torn_Castle) | C | Offensive | `narutorpg:torn_castle` |
| [Tornado High](https://naruto.fandom.com/wiki/Tornado_High) | C | Offensive | `narutorpg:tornado_high` (existing) |
| [Torsion Flower](https://naruto.fandom.com/wiki/Torsion_Flower) | B | Offensive | `narutorpg:torsion_flower` |
| [Towards the Setting Sun](https://naruto.fandom.com/wiki/Towards_the_Setting_Sun) | S | Offensive | `narutorpg:towards_the_setting_sun` |
| [Trinity](https://naruto.fandom.com/wiki/Trinity) | A | Offensive | `narutorpg:trinity` (existing) |
| [Triple Strike: Breaking Through the Clouds](https://naruto.fandom.com/wiki/Triple_Strike:_Breaking_Through_the_Clouds) | C | Offensive | `narutorpg:triple_strike_breaking_through_the_clouds` (existing) |
| [True Eight Trigrams Sixty-Four Palms](https://naruto.fandom.com/wiki/True_Eight_Trigrams_Sixty-Four_Palms) | B | Offensive, Defensive | `narutorpg:true_eight_trigrams_sixty_four_palms` |
| [True Reverse Lotus](https://naruto.fandom.com/wiki/True_Reverse_Lotus) | S | Offensive | `narutorpg:true_reverse_lotus` |
| [True: Tailed Beast Rasenshuriken](https://naruto.fandom.com/wiki/True:_Tailed_Beast_Rasenshuriken) | A | Offensive | `narutorpg:true_tailed_beast_rasenshuriken` (existing) |
| [Trump Card of Pair](https://naruto.fandom.com/wiki/Trump_Card_of_Pair) | B | Offensive, Defensive | `narutorpg:trump_card_of_pair` (existing) |
| [Twin Pole Heavely Drop](https://naruto.fandom.com/wiki/Twin_Pole_Heavely_Drop) | B | Offensive | `narutorpg:twin_pole_heavely_drop` |
| [Twinkling Maiden's Heart](https://naruto.fandom.com/wiki/Twinkling_Maiden's_Heart) | C | Offensive | `narutorpg:twinkling_maiden_s_heart` (existing) |
| [Two Lights Beams of Kumogakure](https://naruto.fandom.com/wiki/Two_Lights_Beams_of_Kumogakure) | C | Offensive | `narutorpg:two_lights_beams_of_kumogakure` |
| [Two Rays of Light Among the Clouds](https://naruto.fandom.com/wiki/Two_Rays_of_Light_Among_the_Clouds) | B | Offensive | `narutorpg:two_rays_of_light_among_the_clouds` (existing) |
| [Two-Tailed Cat Flame Incineration Slash](https://naruto.fandom.com/wiki/Two-Tailed_Cat_Flame_Incineration_Slash) | B | Offensive | `narutorpg:two_tailed_cat_flame_incineration_slash` |
| [Uchiha Cherry Blossom Thunderclap](https://naruto.fandom.com/wiki/Uchiha_Cherry_Blossom_Thunderclap) | B | Offensive | `narutorpg:uchiha_cherry_blossom_thunderclap` (existing) |
| [Uchiha Family: Trinity Technique](https://naruto.fandom.com/wiki/Uchiha_Family:_Trinity_Technique) | B | Offensive | `narutorpg:uchiha_family_trinity_technique` (existing) |
| [Uchiha Shuriken Formation: Spring Lightning](https://naruto.fandom.com/wiki/Uchiha_Shuriken_Formation:_Spring_Lightning) | C | Offensive | `narutorpg:uchiha_shuriken_formation_spring_lightning` (existing) |
| [Ukon's Mad Dance](https://naruto.fandom.com/wiki/Ukon's_Mad_Dance) | S | Offensive | `narutorpg:ukon_s_mad_dance` |
| [Ultimate Reverse Lotus](https://naruto.fandom.com/wiki/Ultimate_Reverse_Lotus) | S | Offensive | `narutorpg:ultimate_reverse_lotus` |
| [Uncontrollable Power](https://naruto.fandom.com/wiki/Uncontrollable_Power) | C | Offensive | `narutorpg:uncontrollable_power` |
| [Uzumaki Twin Striking Palms](https://naruto.fandom.com/wiki/Uzumaki_Twin_Striking_Palms) | B | Offensive | `narutorpg:uzumaki_twin_striking_palms` |
| [Very Hiden: Venomous Insect Venom](https://naruto.fandom.com/wiki/Very_Hiden:_Venomous_Insect_Venom) | C | Offensive, Supplementary | `narutorpg:very_hiden_venomous_insect_venom` |
| [Veteran Taijutsu](https://naruto.fandom.com/wiki/Veteran_Taijutsu) | C | Offensive | `narutorpg:veteran_taijutsu` |
| [Violent Arm](https://naruto.fandom.com/wiki/Violent_Arm) | C | Offensive | `narutorpg:violent_arm` |
| [Violent Headspinner](https://naruto.fandom.com/wiki/Violent_Headspinner) | C | Offensive | `narutorpg:violent_headspinner` |
| [Violent Stabbing Face Slap](https://naruto.fandom.com/wiki/Violent_Stabbing_Face_Slap) | C | Offensive | `narutorpg:violent_stabbing_face_slap` |
| [Violent — Leaf Adamantine-Strength Whirlwind](https://naruto.fandom.com/wiki/Violent_—_Leaf_Adamantine-Strength_Whirlwind) | S | Offensive | `narutorpg:violent_leaf_adamantine_strength_whirlwind` |
| [Water Clone Combo](https://naruto.fandom.com/wiki/Water_Clone_Combo) | C | Offensive | `narutorpg:water_clone_combo` (existing) |
| [Water Palm Blade](https://naruto.fandom.com/wiki/Water_Palm_Blade) | C | Offensive | `narutorpg:water_palm_blade` (existing) |
| [Water Release: Azure Dragon Palm](https://naruto.fandom.com/wiki/Water_Release:_Azure_Dragon_Palm) | C | Offensive | `narutorpg:water_release_azure_dragon_palm` (existing) |
| [Water Release: Bubbles Technique — Great River](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Great_River) | C | Offensive | `narutorpg:water_release_bubbles_technique_great_river` (existing) |
| [Water Release: Bubbles Technique — Ripples](https://naruto.fandom.com/wiki/Water_Release:_Bubbles_Technique_—_Ripples) | C | Offensive | `narutorpg:water_release_bubbles_technique_ripples` (existing) |
| [Water Release: Great Water Arm Technique: Half-Moon Cut](https://naruto.fandom.com/wiki/Water_Release:_Great_Water_Arm_Technique:_Half-Moon_Cut) | C | Offensive | `narutorpg:water_release_great_water_arm_technique_half_moon_cut` (existing) |
| [Water Release: Water Lock Technique](https://naruto.fandom.com/wiki/Water_Release:_Water_Lock_Technique) | C | Offensive | `narutorpg:water_release_water_lock_technique` (existing) |
| [Water Retain](https://naruto.fandom.com/wiki/Water_Retain) | A | Offensive | `narutorpg:water_retain` (existing) |
| [Whip of Love](https://naruto.fandom.com/wiki/Whip_of_Love) | C | Offensive | `narutorpg:whip_of_love` |
| [Whirling Snake Tail](https://naruto.fandom.com/wiki/Whirling_Snake_Tail) | A | Offensive | `narutorpg:whirling_snake_tail` |
| [Whirlwind Sweep](https://naruto.fandom.com/wiki/Whirlwind_Sweep) | C | Offensive | `narutorpg:whirlwind_sweep` |
| [Whirlwind of Feet](https://naruto.fandom.com/wiki/Whirlwind_of_Feet) | C | Supplementary, Offensive, Defensive | `narutorpg:whirlwind_of_feet` |
| [White Haze Heavenly Dance](https://naruto.fandom.com/wiki/White_Haze_Heavenly_Dance) | B | Offensive | `narutorpg:white_haze_heavenly_dance` |
| [White Spirit Release](https://naruto.fandom.com/wiki/White_Spirit_Release) | B | Offensive, Defensive | `narutorpg:white_spirit_release` |
| [Whole Body One Shot](https://naruto.fandom.com/wiki/Whole_Body_One_Shot) | C | Offensive | `narutorpg:whole_body_one_shot` |
| [Wicked Gong Mouth](https://naruto.fandom.com/wiki/Wicked_Gong_Mouth) | C | Offensive, Defensive | `narutorpg:wicked_gong_mouth` |
| [Wicked Tongue Combo](https://naruto.fandom.com/wiki/Wicked_Tongue_Combo) | C | Offensive | `narutorpg:wicked_tongue_combo` |
| [Wild Boar Spin](https://naruto.fandom.com/wiki/Wild_Boar_Spin) | C | Offensive | `narutorpg:wild_boar_spin` |
| [Wild Body Strike](https://naruto.fandom.com/wiki/Wild_Body_Strike) | C | Offensive | `narutorpg:wild_body_strike` |
| [Wild Serpent](https://naruto.fandom.com/wiki/Wild_Serpent) | C | Offensive | `narutorpg:wild_serpent` |
| [Willow](https://naruto.fandom.com/wiki/Willow) | B | Offensive | `narutorpg:willow` |
| [Wind Dust Violent Wave](https://naruto.fandom.com/wiki/Wind_Dust_Violent_Wave) | C | Offensive | `narutorpg:wind_dust_violent_wave` (existing) |
| [Windmill](https://naruto.fandom.com/wiki/Windmill) | B | Offensive | `narutorpg:windmill` |
| [Windmill Throw](https://naruto.fandom.com/wiki/Windmill_Throw) | C | Offensive | `narutorpg:windmill_throw` |
| [Wolf Fang Punishing Drill](https://naruto.fandom.com/wiki/Wolf_Fang_Punishing_Drill) | C | Offensive | `narutorpg:wolf_fang_punishing_drill` |
| [Wolf Fang Rising Fist](https://naruto.fandom.com/wiki/Wolf_Fang_Rising_Fist) | C | Offensive | `narutorpg:wolf_fang_rising_fist` |
| [Wolf River](https://naruto.fandom.com/wiki/Wolf_River) | B | Offensive, Defensive | `narutorpg:wolf_river` |
| [Wood Pillar Sky Kick](https://naruto.fandom.com/wiki/Wood_Pillar_Sky_Kick) | B | Offensive, Defensive | `narutorpg:wood_pillar_sky_kick` (existing) |
| [Wood Release: Adamantine Fist](https://naruto.fandom.com/wiki/Wood_Release:_Adamantine_Fist) | B | Offensive, Defensive | `narutorpg:wood_release_adamantine_fist` (existing) |
| [Wood Release: All Creation](https://naruto.fandom.com/wiki/Wood_Release:_All_Creation) | B | Offensive | `narutorpg:wood_release_all_creation` (existing) |
| [Wood Release: Firmament Splinter](https://naruto.fandom.com/wiki/Wood_Release:_Firmament_Splinter) | B | Offensive | `narutorpg:wood_release_firmament_splinter` (existing) |
| [Wood Release: Revolt of Hell](https://naruto.fandom.com/wiki/Wood_Release:_Revolt_of_Hell) | B | Offensive | `narutorpg:wood_release_revolt_of_hell` (existing) |
| [Wood Release: Tree of Flight](https://naruto.fandom.com/wiki/Wood_Release:_Tree_of_Flight) | B | Offensive, Defensive | `narutorpg:wood_release_tree_of_flight` (existing) |
| [Wood Release: Wooden Spear Heavenly Tear](https://naruto.fandom.com/wiki/Wood_Release:_Wooden_Spear_Heavenly_Tear) | B | Offensive | `narutorpg:wood_release_wooden_spear_heavenly_tear` (existing) |
| [Wrapping Flame Cat Fire](https://naruto.fandom.com/wiki/Wrapping_Flame_Cat_Fire) | C | Offensive | `narutorpg:wrapping_flame_cat_fire` (existing) |
| [Yellow Flash Rasengan](https://naruto.fandom.com/wiki/Yellow_Flash_Rasengan) | A | Offensive | `narutorpg:yellow_flash_rasengan` |
| [Yosaku Cut](https://naruto.fandom.com/wiki/Yosaku_Cut) | C | Offensive | `narutorpg:yosaku_cut` |
| [Young Spirited Embrace of Youth](https://naruto.fandom.com/wiki/Young_Spirited_Embrace_of_Youth) | C | Offensive | `narutorpg:young_spirited_embrace_of_youth` |
| [Youth Full Power](https://naruto.fandom.com/wiki/Youth_Full_Power) | C | Offensive | `narutorpg:youth_full_power` |
| [Youth Leaf Lotus Attack](https://naruto.fandom.com/wiki/Youth_Leaf_Lotus_Attack) | S | Offensive | `narutorpg:youth_leaf_lotus_attack` |
| [Zelkova](https://naruto.fandom.com/wiki/Zelkova) | B | Offensive | `narutorpg:zelkova` |
| [Zeus](https://naruto.fandom.com/wiki/Zeus) | C | Offensive | `narutorpg:zeus` (existing) |
