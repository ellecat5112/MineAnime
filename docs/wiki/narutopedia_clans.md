# Narutopedia `Category:Clans` coverage

Source: https://naruto.fandom.com/wiki/Category:Clans - crawled 2026-11-26 via the MediaWiki API (`action=query&list=categorymembers&cmtype=page|subcat`, recursing into ns=14), then `action=parse` for every clan, kekkei genkai and clan-member infobox and `prop=revisions` for every jutsu infobox.

## Category tree

- `Category:Clans`: 52 pages, 0 subcategories

The category has **no subcategories** and no Semantic MediaWiki drill-down grouping (the clan infobox exposes no classification filter), so the 52 member pages are the complete set. **Every page became a playable clan**; no clan page was excluded (there are no index/list pages in this category).

## How clan data was derived

1. **Clan jutsu** = the clan infobox `Jutsu` list, plus every jutsu listed on the clan's kekkei genkai pages (`Kekkei Genkai` -> page infobox `Jutsu`, only when that page names the clan in its `Clan` field).
2. Clans with fewer than 3 jutsu after step 1 take jutsu used by 2+ known members, then the members' most common jutsu.
3. Clans whose pages and members list no jutsu at all (non-shinobi families and barely documented clans) get thematic basics chosen from their wiki summary; these are marked `curated` below.
4. Jutsu that come only from the Sharingan / Mangekyō Sharingan pages stay with the Uchiha: they require the Uchiha-only Sharingan forms, so the Ōtsutsuki (who share those kekkei genkai pages) could never cast them.
5. Linked pages without a jutsu infobox were dropped (not jutsu): `Leech` (Funato Clan), `Nuiba` (Hirasaka Clan), `Shark` (Hoshigaki Clan), `Lightning Release: Lightning Bolt` (Yotsuki Clan).
6. **Clan items** = tools from the members' infobox `Tools` lists that exist in the ninja tool catalog, minus generic tools used by more than six clans; every clan also gets the Clan Emblem (skill item) and Clan Garb (clothing) in its colour.
7. One jutsu is unlocked by default (lowest rank, clan technique first, signature technique where obvious); the rest unlock by **clan skill level**, gained by holding the clan emblem in either hand while defeating enemies (their experience becomes clan skill experience instead of orbs).

## Clans

| Clan | id | weight | natures | default jutsu | unlockable | forms | clan items | jutsu sources |
|---|---|---:|---|---|---:|---|---:|---|
| [Aburame Clan](https://naruto.fandom.com/wiki/Aburame_Clan) | `aburame` | 8 | - | Parasitic Destruction Insect Technique | 17 | - | 3 | clan 18 |
| [Akimichi Clan](https://naruto.fandom.com/wiki/Akimichi_Clan) | `akimichi` | 8 | yang | Partial Multi-Size Technique | 13 | - | 3 | clan 14 |
| [Amagiri Clan](https://naruto.fandom.com/wiki/Amagiri_Clan) | `amagiri` | 6 | lightning | Chakra Levitation Technique | 5 | - | 1 | member 6 |
| [Chinoike Clan](https://naruto.fandom.com/wiki/Chinoike_Clan) | `chinoike` | 3 | water, yin | Blood Dragon Ascension | 3 | ketsuryugan | 0 | kg 4 |
| [Funato Clan](https://naruto.fandom.com/wiki/Funato_Clan) | `funato` | 8 | water | Water Release: Blood Conversion Technique | 6 | - | 7 | member 5, members 2 |
| [Fūma Clan](https://naruto.fandom.com/wiki/Fūma_Clan) | `fuma` | 7 | wind | Manipulated Shuriken Technique | 2 | - | 1 | curated 3 |
| [Fūma Clan (Land of Sound)](https://naruto.fandom.com/wiki/Fūma_Clan_(Land_of_Sound)) | `fuma_sound` | 7 | earth, fire | Empty Cicada Shell Technique | 7 | - | 2 | clan 2, member 6 |
| [Hagoromo Clan](https://naruto.fandom.com/wiki/Hagoromo_Clan) | `hagoromo` | 9 | - | substitution_jutsu | 2 | - | 0 | curated 3 |
| [Hatake Clan](https://naruto.fandom.com/wiki/Hatake_Clan) | `hatake` | 5 | lightning | Area Scanning Technique | 7 | - | 8 | member 8 |
| [Hirasaka Clan](https://naruto.fandom.com/wiki/Hirasaka_Clan) | `hirasaka` | 6 | - | Chakra Levitation Technique | 2 | - | 0 | member 3 |
| [Hoshigaki Clan](https://naruto.fandom.com/wiki/Hoshigaki_Clan) | `hoshigaki` | 5 | water | Silent Killing | 6 | - | 1 | member 5, members 2 |
| [Hyūga Clan](https://naruto.fandom.com/wiki/Hyūga_Clan) | `hyuga` | 3 | fire | Gentle Fist | 21 | byakugan | 8 | clan 20, kg 2 |
| [Hōki Family](https://naruto.fandom.com/wiki/Hōki_Family) | `hoki` | 8 | wind | Water Release: Water Blade Technique | 3 | - | 1 | member 4 |
| [Hōzuki Clan](https://naruto.fandom.com/wiki/Hōzuki_Clan) | `hozuki` | 4 | water | Drowning Water Blob Technique | 6 | - | 8 | clan 7 |
| [Iburi Clan](https://naruto.fandom.com/wiki/Iburi_Clan) | `iburi` | 4 | - | Body Oxidation Technique | 0 | - | 1 | kg 1 |
| [Inuzuka Clan](https://naruto.fandom.com/wiki/Inuzuka_Clan) | `inuzuka` | 8 | earth | Four Legs Technique | 10 | - | 2 | clan 11 |
| [Izuno Clan](https://naruto.fandom.com/wiki/Izuno_Clan) | `izuno` | 8 | - | Cat Covering | 2 | - | 0 | clan 3 |
| [Jūgo's Clan](https://naruto.fandom.com/wiki/Jūgo's_Clan) | `jugo` | 3 | wind, earth | Sage Transformation | 8 | - | 0 | clan 1, kg 8 |
| [Kagetsu Family](https://naruto.fandom.com/wiki/Kagetsu_Family) | `kagetsu` | 12 | - | Transformation Technique | 1 | - | 0 | curated 2 |
| [Kaguya Clan](https://naruto.fandom.com/wiki/Kaguya_Clan) | `kaguya` | 3 | - | Dance of the Camellia | 6 | - | 0 | kg 7 |
| [Kamizuru Clan](https://naruto.fandom.com/wiki/Kamizuru_Clan) | `kamizuru` | 6 | earth | Bee Bomb Technique | 4 | - | 1 | clan 5 |
| [Karatachi Family](https://naruto.fandom.com/wiki/Karatachi_Family) | `karatachi` | 7 | water, wind | Prisoner Cursed Seal | 7 | - | 3 | member 8 |
| [Kazekage Clan](https://naruto.fandom.com/wiki/Kazekage_Clan) | `kazekage` | 4 | wind, magnet | Air Gold Dust Protective Wall | 22 | - | 8 | kg 23 |
| [Kedōin Clan](https://naruto.fandom.com/wiki/Kedōin_Clan) | `kedoin` | 8 | - | Face Copying Technique | 1 | - | 0 | clan 1, curated 1 |
| [Kodon Clan](https://naruto.fandom.com/wiki/Kodon_Clan) | `kodon` | 7 | yin | Hypnotic Genjutsu: Wild Beast Communication | 1 | - | 0 | clan 1, members 1 |
| [Kohaku Clan](https://naruto.fandom.com/wiki/Kohaku_Clan) | `kohaku` | 9 | - | substitution_jutsu | 1 | - | 0 | curated 2 |
| [Kumanoi Clan](https://naruto.fandom.com/wiki/Kumanoi_Clan) | `kumanoi` | 9 | fire | Fire Release: Phoenix Sage Fire Technique | 1 | - | 0 | curated 2 |
| [Kurama Clan](https://naruto.fandom.com/wiki/Kurama_Clan) | `kurama` | 4 | yin | String Bean Binding Illusion | 1 | - | 0 | member 2 |
| [Lee Clan](https://naruto.fandom.com/wiki/Lee_Clan) | `lee` | 8 | - | Dynamic Entry | 4 | - | 3 | members 5 |
| [Nara Clan](https://naruto.fandom.com/wiki/Nara_Clan) | `nara` | 8 | yin | Shadow Imitation Technique | 12 | - | 7 | clan 13 |
| [Onikuma Clan](https://naruto.fandom.com/wiki/Onikuma_Clan) | `onikuma` | 8 | - | Onikuma Clan's Hijutsu | 2 | - | 0 | clan 1, member 2 |
| [Rinha Clan](https://naruto.fandom.com/wiki/Rinha_Clan) | `rinha` | 5 | earth | Crystal Barrier Technique | 3 | - | 0 | kg 1, member 3 |
| [Ryū Clan](https://naruto.fandom.com/wiki/Ryū_Clan) | `ryu` | 6 | water, wind | Sealing Technique: Black Aura | 1 | - | 0 | member 2 |
| [Sarutobi Clan](https://naruto.fandom.com/wiki/Sarutobi_Clan) | `sarutobi` | 6 | fire, wind | Fire Release: Great Flame Technique | 3 | - | 5 | clan 4 |
| [Sendō Clan](https://naruto.fandom.com/wiki/Sendō_Clan) | `sendo` | 9 | - | Body Flicker Technique | 1 | - | 0 | curated 2 |
| [Senju Clan](https://naruto.fandom.com/wiki/Senju_Clan) | `senju` | 3 | earth, water, wood | Wood Release Secret Technique: Nativity of a World of Trees | 36 | - | 6 | clan 1, kg 36 |
| [Shiin Clan](https://naruto.fandom.com/wiki/Shiin_Clan) | `shiin` | 7 | yin | Chakra Enhancing Music | 3 | - | 2 | clan 1, member 3 |
| [Shimura Clan](https://naruto.fandom.com/wiki/Shimura_Clan) | `shimura` | 7 | wind | Hiding in Surface Technique | 7 | - | 1 | member 8 |
| [Shirogane Clan](https://naruto.fandom.com/wiki/Shirogane_Clan) | `shirogane` | 6 | - | Transformation Technique | 1 | - | 5 | clan 1, member 1 |
| [Taketori Clan](https://naruto.fandom.com/wiki/Taketori_Clan) | `taketori` | 8 | - | substitution_jutsu | 2 | - | 0 | curated 3 |
| [Tenrō Clan](https://naruto.fandom.com/wiki/Tenrō_Clan) | `tenro` | 6 | - | Beast-Human Fury Kicks | 2 | - | 1 | clan 3 |
| [Tsuchigumo Clan](https://naruto.fandom.com/wiki/Tsuchigumo_Clan) | `tsuchigumo` | 6 | earth | Dragon Spying Technique | 7 | - | 0 | clan 2, member 6 |
| [Uchiha Clan](https://naruto.fandom.com/wiki/Uchiha_Clan) | `uchiha` | 3 | fire | fireball_jutsu | 50 | sharingan_one_tomoe, sharingan_two_tomoe, sharingan_three_tomoe, mangekyo_sharingan | 8 | clan 10, kg 41 |
| [Uzumaki Clan](https://naruto.fandom.com/wiki/Uzumaki_Clan) | `uzumaki` | 3 | water | Adamantine Sealing Chains | 6 | - | 8 | clan 7 |
| [Wagarashi Family](https://naruto.fandom.com/wiki/Wagarashi_Family) | `wagarashi` | 12 | - | Clone Technique | 1 | - | 0 | curated 2 |
| [Wasabi Family](https://naruto.fandom.com/wiki/Wasabi_Family) | `wasabi` | 12 | - | Transformation Technique | 1 | - | 1 | curated 2 |
| [Yamanaka Clan](https://naruto.fandom.com/wiki/Yamanaka_Clan) | `yamanaka` | 8 | yin | Mind Body Switch Technique | 9 | - | 7 | clan 10 |
| [Yoimura Clan](https://naruto.fandom.com/wiki/Yoimura_Clan) | `yoimura` | 12 | - | Transformation Technique | 1 | - | 0 | curated 2 |
| [Yota's Clan](https://naruto.fandom.com/wiki/Yota's_Clan) | `yota` | 5 | water | Memory Erasing Technique | 1 | - | 0 | clan 1, member 1 |
| [Yotsuki Clan](https://naruto.fandom.com/wiki/Yotsuki_Clan) | `yotsuki` | 6 | lightning | Lightning Release: Thunderclap Arrow | 1 | - | 0 | curated 2 |
| [Yuki Clan](https://naruto.fandom.com/wiki/Yuki_Clan) | `yuki` | 3 | water, wind, ice | Ice Prison Technique | 18 | - | 1 | clan 2, kg 17 |
| [Ōtsutsuki Clan](https://naruto.fandom.com/wiki/Ōtsutsuki_Clan) | `otsutsuki` | 1 | yin, yang | Chakra Suppression Technique | 113 | byakugan, tenseigan, rinnegan, kokugan, jogan, senrigan | 8 | clan 26, kg 131 |
| Clanless (fallback, not a wiki page) | `clanless` | 0 | - | Clone Technique | 4 | - | 0 | academy basics |

Total: 53 lineages, 417 generated clan jutsu (+ the hand-written Great Fireball and Substitution), 7 dojutsu forms, 18 advanced natures.

## Jutsu -> engine mapping

Generated from each jutsu infobox: `jutsu classification`, `jutsu type` (nature), `jutsu class type`, `jutsu range`, `jutsu rank` and `hand signs`. Rank sets damage (E 3 ... S 14), chakra cost (3x damage) and required rank tier; blank ranks are estimated from the classification (kinjutsu/shinjutsu S, senjutsu/space-time A, kekkei genkai B...). Hand signs available as animated combo tokens (tiger, ram, snake, bird, boar, horse) become the cast combo when unique.

| Wiki signal | Action |
|---|---|
| Genjutsu | `area_effect` cone: nausea, slowness, darkness |
| Dōjutsu (supplementary) | night vision + 24-block glowing pulse |
| Medical / regeneration | heal + regeneration |
| Space–Time (supplementary) | 14-block teleport dash |
| Clone techniques | invisibility + speed, blinding burst |
| Chakra absorption | draining `area_effect` + self heal |
| Defensive / barrier / wall / dome | `raise_wall` (stone, crystal, mirror or barrier by nature) + resistance |
| Offensive taijutsu / kenjutsu | `dash_strike` (+ shockwave for A/S) |
| Taijutsu / senjutsu buffs, gates, modes | strength + speed + resistance |
| Fūinjutsu / chains (offensive) | pulling binding cone |
| Insects / bees | poison swarm cone |
| Yin shadow / mind | binding or mind-control cone |
| Yang (supplementary) | strength, resistance, health boost |
| Offensive, short range | forward `aoe_sphere` |
| Offensive lightning/storm, long range | `beam_raycast` |
| Offensive dōjutsu (Amaterasu...) | burning `beam_raycast` |
| Other offensive | nature-coloured `projectile` (charged for A/S, exhale animation for fire/water/wind) |

## Clan jutsu by clan

- **Aburame Clan**: **Parasitic Destruction Insect Technique** (default), Human Cocoon Technique (Lv 2), Insect Jamming Technique (Lv 4), Insect Jar Technique (Lv 7), Insect Rope Technique (Lv 10), Poison Cloud Technique (Lv 13), Secret Technique: Insect Cocoon (Lv 16), Secret Technique: Insect Gathering (Lv 18), Secret Technique: Insect Wall (Lv 21), Spindle Formation (Lv 24), Insect Clone Technique (Lv 27), Insect Deception Technique (Lv 30), Parasitic Giant Insect — Bug Bite (Lv 33), Secret Technique: Insect Sphere (Lv 35), Secret Technique: Insect Tornado (Lv 38), Iron Mountain Leaning (Lv 41), Allied Shinobi Forces Technique (Lv 44), Jar of Poison Technique (Lv 47)
- **Akimichi Clan**: **Partial Multi-Size Technique** (default), Ballistic Waterwheel (Lv 2), Empty Crash (Lv 5), Human Bullet Tank (Lv 9), Spiked Human Bullet Tank (Lv 13), Multi-Size Technique (Lv 16), Super Multi-Size Technique (Lv 20), Butterfly Mode (Lv 24), Calorie Control (Lv 27), Butterfly Bullet Bombing (Lv 31), Double Human Bullet Tank (Lv 35), Super Open Hand Slap (Lv 38), Formation Ino–Shika–Chō (Lv 42), Human Bullet Yo-Yo (Lv 46)
- **Amagiri Clan**: **Chakra Levitation Technique** (default), Tailed Beast Replication (Lv 2), Taisa's Transformation Technique (Lv 7), Lightning Tackle (Lv 12), Poison Mist (Lv 17), Telescope Technique (Lv 22)
- **Chinoike Clan**: **Blood Dragon Ascension** (default), Ketsuryūgan (Lv 2), Blood Transformation Technique (Lv 2), Genjutsu: Ketsuryūgan (Lv 7), Exploding Human Technique (Lv 12)
- **Fūma Clan**: **Manipulated Shuriken Technique** (default), Shadow Shuriken Technique (Lv 2), Shuriken Shadow Clone Technique (Lv 7)
- **Fūma Clan (Land of Sound)**: **Empty Cicada Shell Technique** (default), Wind Spider Technique (Lv 2), Earth Release: Antlion Technique (Lv 7), Earth Release: Underground Projection Fish Technique (Lv 12), Casualty Puppet (Lv 17), Antlion Ninja Arts: Ephemeral (Lv 22), Chakra Threads (Fūma) (Lv 27), Curse Mandala (Lv 32)
- **Funato Clan**: **Water Release: Blood Conversion Technique** (default), Water Prison Technique (Lv 2), Water Release: Furious Roaring Tornado (Lv 7), Water Release: Imminent Danger (Lv 12), Water Release: Jadeite (Lv 17), Water Release: Raging Jellyfish (Lv 22), Water Release: Water Formation Wall (Lv 27)
- **Hagoromo Clan**: **narutorpg:substitution_jutsu** (default), Body Flicker Technique (Lv 2), Fireball Jutsu (Lv 7)
- **Hatake Clan**: **Area Scanning Technique** (default), Demonic Illusion: Hell Viewing Technique (Lv 2), Earth Release: Double Suicide Decapitation Technique (Lv 7), Earth Release: Earth Quartz Technique (Lv 12), Earth Release: Earth Wave Technique (Lv 17), Earth Release: Hiding Like a Mole Technique (Lv 22), Earth Release: Earth-Style Wall (Lv 27), Chidori (Lv 32)
- **Hirasaka Clan**: **Chakra Levitation Technique** (default), Yomito's Transformation Technique (Lv 2), Chakra Slicers (Lv 7)
- **Hōki Family**: **Water Release: Water Blade Technique** (default), Water Release: Water Shuriken (Lv 2), Wind Release: Bursting Compressed Air (Lv 7), Mystical Palm Technique (Lv 12)
- **Hoshigaki Clan**: **Silent Killing** (default), Water Prison Technique (Lv 2), Earth Release: Subterranean Voyage (Lv 7), Water Clone Technique (Lv 12), Water Release: A Thousand Feeding Sharks (Lv 17), Water Prison Shark Dance Technique (Lv 22), Chakra Transfer Technique (Lv 27)
- **Hōzuki Clan**: **Drowning Water Blob Technique** (default), Hydrification Technique (Lv 2), Water Gun: Two Guns (Lv 7), Water Release: Great Water Leg Technique (Lv 12), Water Release: Tate Eboshi (Lv 17), Water Gun Technique (Lv 22), Water Release: Great Water Arm Technique (Lv 27)
- **Hyūga Clan**: **Gentle Fist** (default), Byakugan (Lv 2), Gentle Fist Art One Blow Body (Lv 2), Eight Trigrams Mountain Crusher (Lv 4), Eight Trigrams One Hundred Twenty-Eight Palms (Lv 6), Eight Trigrams Palms Revolving Heaven (Lv 8), Eight Trigrams Sixteen Palms (Lv 11), Eight Trigrams Sixty-Four Palms (Lv 13), Eight Trigrams Thirty-Two Palms (Lv 15), Eight Trigrams Vacuum Palm (Lv 18), Eight Trigrams Vacuum Wall Palm (Lv 20), Gentle Fist: Tenketsu Needle (Lv 22), Palm Bottom (Lv 24), Protecting Eight Trigrams Sixty-Four Palms (Lv 27), Water Needle (Lv 29), Puppet Reincarnation (Lv 31), Eight Trigrams Grid Palm (Lv 34), Eight Trigrams Twin Lions Crumbling Attack (Lv 36), Gentle Step Twin Lion Fists (Lv 38), Gentle Wind Spiralling Twin Lion Fists: Snow White (Lv 40), Hyūga Main Family's Juinjutsu (Lv 43), Rabbit Hair Needle (Lv 45), Front Lotus: Eight Trigrams Palms Revolving Heaven (Lv 47)
- **Iburi Clan**: **Body Oxidation Technique** (default)
- **Inuzuka Clan**: **Four Legs Technique** (default), Dynamic Marking (Lv 2), Passing Fang (Lv 6), Beast Human Clone (Lv 11), Fang Passing Fang (Lv 16), Fang Wolf Fang (Lv 21), Human Beast Combination Transformation: Double-Headed Wolf (Lv 26), Super Fang Wolf Fang (Lv 30), Fang Rotating Fang (Lv 35), Tail Chasing Fang Fang Rotating Fang (Lv 40), Human Beast Mixture Transformation — Three-Headed Wolf (Lv 45)
- **Izuno Clan**: **Cat Covering** (default), Cat God Possession: Monster Cat Beckoning Technique (Lv 2), Cat Surprise (Lv 7)
- **Jūgo's Clan**: **Sage Transformation** (default), Cellular Regeneration Ejection (Lv 2), Chain (Lv 7), Jet Booster Jump (Lv 12), Multiple Lotus Nonself Connected Cannons (Lv 17), Broadaxe (Lv 22), Cellular Regeneration Absorption (Lv 27), Piston Fist (Lv 32), Piston Fist: Style One (Lv 37)
- **Kagetsu Family**: **Transformation Technique** (default), Mystical Palm Technique (Lv 2)
- **Kaguya Clan**: **Dance of the Camellia** (default), Dance of the Clematis: Flower (Lv 2), Dance of the Clematis: Vine (Lv 7), Dance of the Larch (Lv 12), Dance of the Seedling Fern (Lv 17), Dance of the Willow (Lv 22), Ten-Finger Drilling Bullets (Lv 27)
- **Kamizuru Clan**: **Bee Bomb Technique** (default), Bee Honey Technique (Lv 2), Rock Hive (Lv 7), Beeswax Clone (Lv 12), Thousand Bee Stings Technique (Lv 17)
- **Karatachi Family**: **Prisoner Cursed Seal** (default), Water Release: Clear Mirror Still Water (Lv 2), Wind Release: Passing Typhoon (Lv 7), Leaping Bullet Shell Tower (Lv 12), Shadow Stroke (Lv 17), Coral Palm (Lv 22), Water Release: Water Mirror Technique (Lv 27), Tailed Beast Ball (Lv 32)
- **Kazekage Clan**: **Air Gold Dust Protective Wall** (default), Black Iron Fist (Lv 2), Iron Sand Body (Lv 4), Iron Sand Drizzle (Lv 6), Iron Sand Gathering Assault (Lv 8), Iron Sand Marking (Lv 10), Iron Sand Wall (Lv 12), Iron Sand Wave (Lv 15), Iron Sand Weaponry (Lv 17), Iron Sand World Method (Lv 19), Iron Sand: Black Iron Wings (Lv 21), Magnet Release: Body Control (Lv 23), Magnet Release: Gold Dust Imperial Funeral (Lv 26), Magnet Release: Lightning God Self (Lv 28), Successive Shots Iron Sand Drizzle (Lv 30), Black Secret Technique: Black Iron (Lv 32), Iron Sand Clone (Lv 34), Magnet Release: Conserving Bee Twin Blades (Lv 36), Magnet Iron Grain Seal Palm (Lv 39), Magnetic Iron Sand Sealing Blade (Lv 41), Sage Art: Super Tailed Beast Rasenshuriken (Lv 43), Sage Art: Magnet Release Rasengan (Lv 45), Magnet Release: Spiral Jet Black (Lv 47)
- **Kedōin Clan**: **Face Copying Technique** (default), Shadow Clone Technique (Lv 2)
- **Kodon Clan**: **Hypnotic Genjutsu: Wild Beast Communication** (default), Sealing Technique: Rōen Kodon (Lv 2)
- **Kohaku Clan**: **narutorpg:substitution_jutsu** (default), Body Flicker Technique (Lv 2)
- **Kumanoi Clan**: **Fire Release: Phoenix Sage Fire Technique** (default), Fireball Jutsu (Lv 2)
- **Kurama Clan**: **String Bean Binding Illusion** (default), Demonic Illusion: Tree Binding Death (Lv 2)
- **Lee Clan**: **Dynamic Entry** (default), Eight Gates (Lv 2), Leaf Whirlwind (Lv 7), Leaf Great Whirlwind (Lv 12), Strong Fist (Lv 17)
- **Nara Clan**: **Shadow Imitation Technique** (default), Black Spider Lily (Lv 2), Net Shadow Paralysis Technique (Lv 6), Shadow Cage Technique (Lv 10), Shadow Gathering Technique (Lv 14), Shadow Imitation Field Technique (Lv 18), Shadow Imitation Shadow Bind Technique (Lv 22), Shadow Sewing Technique (Lv 26), Shadow–Neck Binding Technique (Lv 30), Shadow Imitation Shuriken Technique (Lv 34), Formation Ino–Shika–Chō (Lv 38), Human Bullet Yo-Yo (Lv 42), Shadow Clutch Technique (Lv 46)
- **Onikuma Clan**: **Onikuma Clan's Hijutsu** (default), String Light Formation (Lv 2), Multiple String Light Formation (Lv 7)
- **Ōtsutsuki Clan**: **Chakra Suppression Technique** (default), Byakugan (Lv 2), Amanohabaya (Lv 2), Amenosubaruboshinomikoto (Lv 2), Amenosubaruboshinomikoto: Gyokkō (Lv 2), Amenosubaruboshinomikoto: Ryūgū (Lv 3), Ōdachi Mow Down (Lv 3), Furious Smelting (Lv 4), Puppet Technique (Lv 4), All is Suffering (Lv 4), Hokage-Style Sixty-Year-Old Technique — Kakuan Entering Society with Bliss-Bringing Hands (Lv 5), Impaling Spear Tree (Lv 5), Kanzeon Lotus King (Lv 6), Mayfly (Lv 6), Resonance Blast (Lv 7), Sea of Trees Erosion (Lv 7), Spontaneous Tree Summoning (Lv 7), Top Transformed Buddha (Lv 8), Transmission Wood (Lv 8), Wood Release: Advent of a World of Flowering Trees (Lv 9), Wood Release: Cutting Technique (Lv 9), Wood Release: Four-Pillar House Technique (Lv 10), Wood Release: Four-Pillar Prison Technique (Lv 10), Wood Release: Great Forest Technique (Lv 10), Wood Release: Great Spear Tree (Lv 11), Wood Release: Hand Tool Manipulation (Lv 11), Tenseigan (Lv 12), Wood Release: Hotei Technique (Lv 12), Wood Release: Nativity of a Sea of Trees (Lv 12), Wood Release: Serial Pillar Houses Technique (Lv 13), Wood Release: Stocks (Lv 13), Wood Release: Transformation (Lv 13), Wood Release: Tree Bind Flourishing Burial (Lv 14), Wood Release: Wood Human Technique (Lv 14), Wood Release: Wood Locking Wall (Lv 15), Wood Release: Wood Spikes Ring (Lv 15), Wood Release: World of Trees Wall (Lv 16), Puppet Reincarnation (Lv 16), Eight Trigrams Mountain Crusher (Lv 16), Eight Trigrams One Hundred Twenty-Eight Palms (Lv 17), Eight Trigrams Palms Revolving Heaven (Lv 17), Eight Trigrams Sixteen Palms (Lv 18), Eight Trigrams Sixty-Four Palms (Lv 18), Eight Trigrams Thirty-Two Palms (Lv 18), Eight Trigrams Vacuum Palm (Lv 19), Eight Trigrams Vacuum Wall Palm (Lv 19), Rinnegan (Lv 20), Gentle Fist: Tenketsu Needle (Lv 20), Palm Bottom (Lv 20), Protecting Eight Trigrams Sixty-Four Palms (Lv 21), Water Needle (Lv 21), Animal Path (Lv 21), Asura Attack (Lv 22), Asura Path (Lv 22), Banshō Ten'in (Lv 23), Black Receiver (Lv 23), Deva Path (Lv 24), Flaming Arrow Warhead (Lv 24), Flaming Arrow of Amazing Ability (Lv 24), Human Path (Lv 25), Limbo: Border Jail (Lv 25), Naraka Path (Lv 26), Origami Replacement Technique (Lv 26), Outer Path (Lv 27), Shinra Tensei (Lv 27), Six Paths Technique (Lv 27), Kokugan (Lv 28), Takamimusubinokami (Lv 28), Temporal Rewind (Lv 28), Golden Wheel Reincarnation Explosion (Lv 29), Silver Wheel Reincarnation Explosion (Lv 29), Multiple Wood Release Clone Technique (Lv 30), Spiralling Tower Technique (Lv 30), Wood Clone Technique (Lv 30), Wood Release Secret Technique: Nativity of a World of Trees (Lv 31), Wood Release: Foo Dog Heads (Lv 31), Wood Release: Hōbi Technique (Lv 32), Wood Release: Smothering Binding Technique (Lv 32), Wood Release: Wood Dragon Technique (Lv 33), Chakra Edible Creation (Lv 33), Eight Trigrams Grid Palm (Lv 33), Jōgan (Lv 34), Eight Trigrams Twin Lions Crumbling Attack (Lv 34), Gentle Step Twin Lion Fists (Lv 34), Chibaku Tensei (Lv 35), Preta Path (Lv 35), Six Paths Ten-Tails Coffin Seal (Lv 35), Six Paths of Pain (Lv 36), True: Chibaku Tensei (Lv 36), Localised Reincarnation Explosion (Lv 37), Tenseigan Chakra Mode (Lv 37), Blocking Technique Absorption Seal (Lv 38), Demonic Statue Chains (Lv 38), Six Red Yang Formation (Lv 38), Gentle Wind Spiralling Twin Lion Fists: Snow White (Lv 39), Rabbit Hair Needle (Lv 39), Senrigan (Lv 40), Yomotsu Hirasaka (Lv 40), Sage Art Wood Release: True Several Thousand Hands (Lv 40), Amenotejikara (Lv 41), Summoning Rinnegan (Lv 41), Summoning: Demonic Statue of the Outer Path (Lv 41), Sasuke Uchiha's Space–Time Dōjutsu (Lv 42), Kāma (Lv 42), Black Holy Hammer (Lv 43), Lacquer Bloom (Lv 43), Sukunahikona (Lv 44), Sukunahikona: Instant Appearance (Lv 44), Sukunahikona: Rain of Early Summer (Lv 44), Sukunahikona: Rapid Succession (Lv 45), Daihakoten (Lv 45), Daikokuten (Lv 46), Daikokuten: Collapse (Lv 46), Daikokuten: Divine Descent (Lv 47), Daikokuten: Falling Star (Lv 47), Daikokuten: Karma Flame (Lv 47), Daikokuten: Sukunahikona: Twin Crash (Lv 48), Sukunahikona: Scale Fragment (Lv 48), Front Lotus: Eight Trigrams Palms Revolving Heaven (Lv 49), Outer Path — Samsara of Heavenly Life Technique (Lv 49)
- **Rinha Clan**: **Crystal Barrier Technique** (default), Puppet Technique (Lv 2), Self-Seal Technique (Lv 7), Spirit Reflection Technique (Lv 12)
- **Ryū Clan**: **Sealing Technique: Black Aura** (default), Sealing Technique: Five Elements (Lv 2)
- **Sarutobi Clan**: **Fire Release: Great Flame Technique** (default), Fire Release: Ash Pile Burning (Lv 2), Fire Release: Fire Dragon Flame Bullet (Lv 7), Allied Shinobi Forces Technique (Lv 12)
- **Sendō Clan**: **Body Flicker Technique** (default), Shadow Clone Technique (Lv 2)
- **Senju Clan**: **Wood Release Secret Technique: Nativity of a World of Trees** (default), All is Suffering (Lv 2), Hokage-Style Sixty-Year-Old Technique — Kakuan Entering Society with Bliss-Bringing Hands (Lv 3), Impaling Spear Tree (Lv 4), Kanzeon Lotus King (Lv 6), Mayfly (Lv 7), Resonance Blast (Lv 8), Sea of Trees Erosion (Lv 10), Spontaneous Tree Summoning (Lv 11), Top Transformed Buddha (Lv 12), Transmission Wood (Lv 14), Wood Release: Advent of a World of Flowering Trees (Lv 15), Wood Release: Cutting Technique (Lv 16), Wood Release: Four-Pillar House Technique (Lv 18), Wood Release: Four-Pillar Prison Technique (Lv 19), Wood Release: Great Forest Technique (Lv 20), Wood Release: Great Spear Tree (Lv 22), Wood Release: Hand Tool Manipulation (Lv 23), Wood Release: Hotei Technique (Lv 24), Wood Release: Nativity of a Sea of Trees (Lv 26), Wood Release: Serial Pillar Houses Technique (Lv 27), Wood Release: Stocks (Lv 28), Wood Release: Transformation (Lv 30), Wood Release: Tree Bind Flourishing Burial (Lv 31), Wood Release: Wood Human Technique (Lv 32), Wood Release: Wood Locking Wall (Lv 34), Wood Release: Wood Spikes Ring (Lv 35), Wood Release: World of Trees Wall (Lv 36), Multiple Wood Release Clone Technique (Lv 38), Spiralling Tower Technique (Lv 39), Wood Clone Technique (Lv 40), Wood Release: Foo Dog Heads (Lv 42), Wood Release: Hōbi Technique (Lv 43), Wood Release: Smothering Binding Technique (Lv 44), Wood Release: Wood Dragon Technique (Lv 46), Sage Art Wood Release: True Several Thousand Hands (Lv 47), Kamui Running: Brutal (Lv 48)
- **Shiin Clan**: **Chakra Enhancing Music** (default), Offsetting Sound (Lv 2), Paralysing Flute Genjutsu (Lv 7), Memory Blocking Technique (Lv 12)
- **Shimura Clan**: **Hiding in Surface Technique** (default), Regeneration Ability (Lv 2), Cursed Tongue Eradication Seal (Lv 7), Self-Cursing Seal (Lv 12), Genjutsu: Sharingan (Lv 17), Kotoamatsukami (Lv 22), Reverse Four Symbols Sealing (Lv 27), Izanagi (Lv 32)
- **Shirogane Clan**: **Transformation Technique** (default), Puppet Technique (Lv 2)
- **Taketori Clan**: **narutorpg:substitution_jutsu** (default), Transformation Technique (Lv 2), Hiding in Mist Technique (Lv 7)
- **Tenrō Clan**: **Beast-Human Fury Kicks** (default), Beast-Human Needle Senbon (Lv 2), Tenrō-Style Ninjutsu: Beast-Human Transformation Technique (Lv 7)
- **Tsuchigumo Clan**: **Dragon Spying Technique** (default), Water Release: Wild Water Wave (Lv 2), Hidden Rock Trap (Lv 7), Character Bind Technique (Lv 12), Tsuchigumo Style: Forbidden Life Technique Release: Creation of Heaven and Earth (Lv 17), Infinite Embrace (Lv 22), Hiding with Camouflage Technique (Lv 27), Fury (Lv 32)
- **Uchiha Clan**: **narutorpg:fireball_jutsu** (default), Fire Release: Great Fire Destruction (Lv 2), Fire Release: Great Flame Flower (Lv 2), Sharingan One Tomoe (Lv 3), Manipulating Windmill Triple Blades (Lv 3), Naka Shrine Pass Technique (Lv 4), Fire Release: Great Fire Annihilation (Lv 5), Uchiha Flame Formation (Lv 6), Genjutsu: Sharingan (Lv 7), Uchiha Return (Lv 9), Sharingan Two Tomoe (Lv 10), Sharingan Three Tomoe (Lv 20), Coercion Sharingan (Lv 30), Demonic Illusion: Mirage Crow (Lv 31), Demonic Illusion: Mirror Heaven and Earth Change (Lv 32), Demonic Illusion: Shackling Stakes Technique (Lv 33), Lightning Transmission (Lv 34), Mangekyo Sharingan (Lv 35), Susanoo: Great Fireball Technique (Lv 35), Amaterasu (Lv 35), Amaterasu: Flame Wrapping Fire (Lv 35), Blaze Release: Honoikazuchi (Lv 35), Blaze Release: Kagutsuchi (Lv 35), Blaze Release: Susanoo Kagutsuchi (Lv 35), Blaze Release: Yasaka Magatama (Lv 35), Evil Disturbance Waltz (Lv 35), Hinokagutsuchi (Lv 35), Indra's Arrow (Lv 35), Kotoamatsukami (Lv 35), Majestic Attire: Susanoo (Lv 35), Susanoo (Lv 35), Susanoo: Chidori (Lv 35), Susanoo: Crush (Lv 35), Susanoo: Fist (Lv 35), Susanoo: Tsukumo (Lv 35), Sword of Kagutsuchi (Lv 35), Tengai Shinsei (Lv 35), Tsukuyomi (Lv 35), Yasaka Magatama (Lv 35), Ōhirume (Lv 35), Object Manipulation Technique (Lv 35), Susanoo: Captive Slash (Lv 36), Transcription Seal: Amaterasu (Lv 37), Transcription Seal: Izanagi (Lv 38), Yasaka Magatama: Tailed Beast Noka (Lv 39), Hurricane Thunderclap — Majestic Attire Sword Stroke (Lv 40), Kamui Lightning Cutter (Lv 41), Kamui Running (Lv 42), Kamui Running: Brutal (Lv 43), Kamui Shuriken (Lv 44), Kamui (Lv 45), Shin Uchiha's Space–Time Dōjutsu (Lv 46), Izanagi (Lv 47), Izanami (Lv 48), Scorch Release: Halo Hurricane Jet Black Arrow Style Zero (Lv 49)
- **Uzumaki Clan**: **Adamantine Sealing Chains** (default), Heal Bite (Lv 2), Eight Trigrams Sealing Style (Lv 7), Four Symbols Seal (Lv 12), Uzumaki Sealing Technique (Lv 17), Tight Binding Golden Chain Technique (Lv 22), Dead Demon Consuming Seal (Lv 27)
- **Wagarashi Family**: **Clone Technique** (default), Substitution Jutsu (Lv 2)
- **Wasabi Family**: **Transformation Technique** (default), Body Flicker Technique (Lv 2)
- **Yamanaka Clan**: **Mind Body Switch Technique** (default), Mind Body Transmission Formation (Lv 2), Mind Body Transmission Technique (Lv 7), Mind Disturbance Dance Performance Technique (Lv 12), Sensing Transmission (Lv 17), Mind Clone Switch Technique (Lv 22), Mind Puppet Switch Cursed Seal Technique (Lv 27), Mind Body Disturbance Technique (Lv 32), Formation Ino–Shika–Chō (Lv 37), Human Bullet Yo-Yo (Lv 42)
- **Yoimura Clan**: **Transformation Technique** (default), Clone Technique (Lv 2)
- **Yota's Clan**: **Memory Erasing Technique** (default), Weather Manipulation (Lv 2)
- **Yotsuki Clan**: **Lightning Release: Thunderclap Arrow** (default), Lightning Release Chakra Mode (Lv 2)
- **Yuki Clan**: **Ice Prison Technique** (default), Ice Release: Black Dragon Blizzard (Lv 2), Ice Release: One Horned White Whale (Lv 4), Ice Release: Swallow Snow Storm (Lv 7), Ice Release: Tearing Dragon Fierce Tiger (Lv 10), Ice Release: Twin Dragon Blizzard (Lv 12), Ice Release: Wolf Fang Avalanche Technique (Lv 15), Thousand Flying Water Needles of Death (Lv 18), Certain-Kill Ice Spears (Lv 20), Ice Release: Crystal Wall (Lv 23), Ice Release: Earthen Consecutive Chains of Ice (Lv 26), Ice Release: Frozen Capturing Field (Lv 28), Ice Release: Ice Rock Dome of Magnificent Nothingness (Lv 31), Ice Release: Ice-Breaking Sledgehammer (Lv 34), Ice Release: Ten Thousand Ice Petals (Lv 36), Ice Release: Whirlwind (Lv 39), Ice Sword Technique (Lv 42), Ice-Breaking Fist (Lv 44), Demonic Mirroring Ice Crystals (Lv 47)
