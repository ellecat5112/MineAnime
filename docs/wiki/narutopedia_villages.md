# Narutopedia `Category:Villages` coverage

Source: https://naruto.fandom.com/wiki/Category:Villages - crawled 2026-11-26 via the MediaWiki API (`list=categorymembers`, `cmtype=page|subcat`, recursing into ns=14), `action=parse` for every village infobox and for every character that uses a ninja tool or clan/village jutsu (their `Affiliation` field), and jutsu infoboxes for village techniques.

## Category tree

- `Category:Villages`: 40 pages, 0 subcategories

No subcategories and no Semantic MediaWiki drill-down grouping exist for villages, so the 40 pages are the complete set. **Every page became a faction** - none were excluded (the category contains no index/list pages). One extra faction, **Missing-nin**, is not a wiki village page: it is the rogue alignment players fall into by scratching their headband (or can start in).

## How village data was derived

1. **Kind**: `-gakure` pages are hidden villages (shinobi gear, selectable), Mount Myōboku / Ryūchi Cave / Shikkotsu Forest are sage realms (not selectable at the start), the rest are civilian villages (selectable, no forehead protector).
2. **Village jutsu pool**: techniques listed on the infoboxes of characters affiliated with the village, ranked by how many members know them; a technique belongs to the village whose members make up at least half of its users. Clan-locked techniques (hiden, kekkei genkai natures, dōjutsu) and tailed-beast techniques stay with their clans.
3. **Exclusive gear**: ninja tools whose every known (village-affiliated) user comes from that one village; standard-issue supplies (kunai, shuriken, explosive tags, pills...) are never exclusive.
4. **Standings**: canon alliances and rivalries of the Five Great Nations (e.g. Leaf-Sand alliance, Leaf/Cloud/Stone rivalry); every hidden village starts at -50 with missing-nin.

## Villages

| Village | id | kind | jutsu pool | exclusive gear | perks |
|---|---|---|---:|---:|---:|
| [Amegakure](https://naruto.fandom.com/wiki/Amegakure) | `hidden_rain` | hidden_village | 8 | 3 | 2 |
| [Bamboo Village](https://naruto.fandom.com/wiki/Bamboo_Village) | `bamboo_village` | village | 2 | 0 | 1 |
| [Ceramic Village](https://naruto.fandom.com/wiki/Ceramic_Village) | `ceramic_village` | village | 2 | 0 | 1 |
| [Curtain Village](https://naruto.fandom.com/wiki/Curtain_Village) | `curtain_village` | village | 2 | 0 | 1 |
| [Daidai Village](https://naruto.fandom.com/wiki/Daidai_Village) | `daidai_village` | village | 2 | 0 | 1 |
| [Genjutsu Tree Village](https://naruto.fandom.com/wiki/Genjutsu_Tree_Village) | `genjutsu_tree_village` | village | 2 | 1 | 1 |
| [Hachō Village](https://naruto.fandom.com/wiki/Hachō_Village) | `hacho_village` | village | 2 | 0 | 1 |
| [Hoshigakure](https://naruto.fandom.com/wiki/Hoshigakure) | `hidden_star` | hidden_village | 0 | 1 | 0 |
| [Howling Wolf Village](https://naruto.fandom.com/wiki/Howling_Wolf_Village) | `howling_wolf_village` | village | 2 | 0 | 1 |
| [Inaho Village](https://naruto.fandom.com/wiki/Inaho_Village) | `inaho_village` | village | 2 | 0 | 1 |
| [Ishigakure](https://naruto.fandom.com/wiki/Ishigakure) | `hidden_rock` | hidden_village | 0 | 0 | 0 |
| [Iwagakure](https://naruto.fandom.com/wiki/Iwagakure) | `hidden_stone` | hidden_village | 5 | 2 | 2 |
| [Jōmae Village](https://naruto.fandom.com/wiki/Jōmae_Village) | `jomae_village` | village | 1 | 0 | 1 |
| [Kagerō Village](https://naruto.fandom.com/wiki/Kagerō_Village) | `kagero_village` | village | 2 | 0 | 1 |
| [Kirigakure](https://naruto.fandom.com/wiki/Kirigakure) | `hidden_mist` | hidden_village | 12 | 10 | 2 |
| [Kisaragi Village](https://naruto.fandom.com/wiki/Kisaragi_Village) | `kisaragi_village` | village | 2 | 0 | 1 |
| [Konohagakure](https://naruto.fandom.com/wiki/Konohagakure) | `hidden_leaf` | hidden_village | 12 | 30 | 2 |
| [Kumogakure](https://naruto.fandom.com/wiki/Kumogakure) | `hidden_cloud` | hidden_village | 6 | 5 | 2 |
| [Kusagakure](https://naruto.fandom.com/wiki/Kusagakure) | `hidden_grass` | hidden_village | 3 | 1 | 1 |
| [Mount Katsuragi](https://naruto.fandom.com/wiki/Mount_Katsuragi) | `mount_katsuragi` | village | 2 | 0 | 1 |
| [Mount Myōboku](https://naruto.fandom.com/wiki/Mount_Myōboku) | `mount_myoboku` | sage_realm | 2 | 2 | 2 |
| [Nadeshiko Village](https://naruto.fandom.com/wiki/Nadeshiko_Village) | `nadeshiko_village` | village | 5 | 0 | 1 |
| [Otogakure](https://naruto.fandom.com/wiki/Otogakure) | `hidden_sound` | hidden_village | 5 | 6 | 1 |
| [Ryūchi Cave](https://naruto.fandom.com/wiki/Ryūchi_Cave) | `ryuchi_cave` | sage_realm | 0 | 0 | 2 |
| [Shangri-la](https://naruto.fandom.com/wiki/Shangri-la) | `shangri_la` | village | 2 | 0 | 1 |
| [Shikkotsu Forest](https://naruto.fandom.com/wiki/Shikkotsu_Forest) | `shikkotsu_forest` | sage_realm | 0 | 0 | 2 |
| [Shimogakure](https://naruto.fandom.com/wiki/Shimogakure) | `hidden_frost` | hidden_village | 0 | 0 | 0 |
| [Sugi Village](https://naruto.fandom.com/wiki/Sugi_Village) | `sugi_village` | village | 2 | 0 | 1 |
| [Sunagakure](https://naruto.fandom.com/wiki/Sunagakure) | `hidden_sand` | hidden_village | 12 | 17 | 2 |
| [Takigakure](https://naruto.fandom.com/wiki/Takigakure) | `hidden_waterfall` | hidden_village | 6 | 1 | 2 |
| [Takumi Village](https://naruto.fandom.com/wiki/Takumi_Village) | `takumi_village` | village | 6 | 4 | 1 |
| [Tanigakure](https://naruto.fandom.com/wiki/Tanigakure) | `hidden_valley` | hidden_village | 0 | 0 | 0 |
| [Tonika Village](https://naruto.fandom.com/wiki/Tonika_Village) | `tonika_village` | village | 2 | 0 | 1 |
| [Tree Felling Village](https://naruto.fandom.com/wiki/Tree_Felling_Village) | `tree_felling_village` | village | 2 | 0 | 1 |
| [Tsuchigumo Village](https://naruto.fandom.com/wiki/Tsuchigumo_Village) | `tsuchigumo_village` | village | 3 | 0 | 1 |
| [Tsukigakure](https://naruto.fandom.com/wiki/Tsukigakure) | `hidden_moon` | hidden_village | 2 | 0 | 0 |
| [Uzushiogakure](https://naruto.fandom.com/wiki/Uzushiogakure) | `hidden_whirlpool` | hidden_village | 1 | 0 | 1 |
| [Yugakure](https://naruto.fandom.com/wiki/Yugakure) | `hidden_hot_water` | hidden_village | 0 | 2 | 1 |
| [Yukigakure](https://naruto.fandom.com/wiki/Yukigakure) | `hidden_snow` | hidden_village | 2 | 8 | 1 |
| [Yumegakure](https://naruto.fandom.com/wiki/Yumegakure) | `hidden_dream` | hidden_village | 4 | 1 | 0 |
| Missing-nin (rogue alignment, not a wiki page) | `missing_nin` | rogue | 0 | 0 | 2 |

## Village techniques and gear

- **Bamboo Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Ceramic Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Curtain Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Daidai Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Genjutsu Tree Village** - jutsu: Transformation Technique, Clone Technique; gear: Chilli Pepper Bomb
- **Hachō Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Kumogakure (Hidden Cloud)** - jutsu: Lightning Release: Lariat, Black Lightning, Ink Creation, Lightning Release Chakra Mode, Lightning Release: Double Lariat, Lightning Straight; gear: Chakra Cannon, Cleaver Sword, Kumogakure Swords, Spike Bomb, Vambraces
- **Yumegakure (Hidden Dream)** - jutsu: Chakra Slicers, Lightning Tackle, Taisa's Transformation Technique, Yomito's Transformation Technique; gear: Chakra Enhanced Triple-Bladed Claw
- **Kusagakure (Hidden Grass)** - jutsu: Feather Launching, Giant Sphere, Heal Bite; gear: Box of Ultimate Bliss
- **Yugakure (Hidden Hot Water)** - jutsu: -; gear: Double-Bladed Scythe, Retractable Spear
- **Konohagakure (Hidden Leaf)** - jutsu: Mystical Palm Technique, String Light Formation, Multiple String Light Formation, Regeneration Ability, Rasengan, Fang Passing Fang, Negative Emotions Sensing, Passing Fang, Strong Fist, Summoning Technique, Big Ball Rasengan, Eight Gates; gear: Absorbing Eye, Chain Wind Staff, Concealed Mouth Ember, Conch Shell Mace, Cursed Seal Device, Dakō, Demon Shinki, Exploding Sphere, First Hokage's Necklace, Handheld Kunai Launcher, Hyūga Clan Secret Ointment, Injection Shot, Jidanda, Kokutō, Kunai Blade, Lightning Release Suit, Liquid Crystal Wall, Metal Doll, Microscopic Scientific Ninja Tools, Mind Reading Amplification Machine, Ocarina, Sealing Arm Braces, Space-Time Gate, Spy Drone, Sword of Totsuka, Tailed Beast Drug, Tsurukame, White Light Chakra Sabre, Wrist-Mounted Senbon Launcher, Yata Mirror
- **Kirigakure (Hidden Mist)** - jutsu: Hiding in Mist Technique, Soap Bubble Ninjutsu, Water Prison Technique, First Axe Strike, Coral Palm, Earth Release: Subterranean Voyage, Leaping Bullet Shell Tower, Lightning Ball, Lightning Burial: Banquet of Lightning, Lightning Strike Armour, Shadow Stroke, Water Prison Shark Dance Technique; gear: Bubble-Utilising Pipe, Claw Club, Fire Gatling, Hiramekarei, Kabutowari, Kiba (swords), Metal Gauntlets, Mirror Drone, Nuibari, Shibuki (sword)
- **Tsukigakure (Hidden Moon)** - jutsu: Sealing Technique: Vitality Seal Formation, Sealing Trap Explosion; gear: -
- **Amegakure (Hidden Rain)** - jutsu: Senbon Shower, Haze Clone Technique, Sly Mind Affect Technique, Summoning: Ibuse Poisoning, Water Release: Rapid Thunder Whip Technique, Water Release: Water Weapons, Water Release: Water Whip, Dance of the Sickle Blade: Falling Descent Blade; gear: Artificial Sharingan, Kiseki, Smoke Tag
- **Sunagakure (Hidden Sand)** - jutsu: Puppet Technique, Puppet Performance: Skilful Achievement with a Human Body, Third Eye, Air Sand Protective Wall, Desert Layered Imperial Funeral Seal, Desert Suspension, Desert Wave, Desert: Hand, Dharma Power Seal, Dharma Power Sealing Technique: Sen no Rikyū, Manipulating Attack Blades, Mechanical Light Shield Block; gear: Araya (puppet), Binding Cloth, Double Bladed Bracers, Flamethrower, Hiruko, Karasu, Komushi (puppet), Kuroari, Lion-Headed Kannon, Mother and Father, Puppet Buzzsaw, Sand Gourd, Sanshōuo, Sasori (puppet), Third Kazekage (puppet), Water-Firing Puppets, Water Stream Shooters
- **Yukigakure (Hidden Snow)** - jutsu: Explosion: Ice Mountain Destruction, Roaring Run: Great Strength Ice Strike; gear: Chakra Armour, Chakra Disruption Device, Hexagonal Crystal, Ice Bomb, Mechanical Kunai Thrower, Pepper Balloon Earrings, Retractable Cable Arm, Snowboard
- **Otogakure (Hidden Sound)** - jutsu: Casualty Puppet, Space–Time Technique Formula: Underworld Turnover, Antlion Ninja Arts: Ephemeral, Earth Release: Antlion Technique, Wind Spider Technique; gear: Curse Mask, Demonic Flute, Resonating Echo Speaker, Scissor Pincers, Slime Suit, Wrist Launcher
- **Hoshigakure (Hidden Star)** - jutsu: -; gear: Meteorite
- **Iwagakure (Hidden Stone)** - jutsu: Earth Release: Fist Rock Technique, Earth Release: Golem Technique, Earth Release: Opening Earth Rising Excavation, Horn Breaking, Rock Clone Technique; gear: Eye Scope, Steam Armour
- **Takigakure (Hidden Waterfall)** - jutsu: Cocoon, Net-Shaped Prison, Spear Attack Shining Horn, Takigakure Whirlwind, Water Release: Lotus Light Rupture, Wave Transmission Technique; gear: Hero Water
- **Uzushiogakure (Hidden Whirlpool)** - jutsu: Uzumaki Sealing Technique; gear: -
- **Howling Wolf Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Inaho Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Jōmae Village** - jutsu: Eye Mind Reading; gear: -
- **Kagerō Village** - jutsu: Heavenly Punishment, Rockslide Genjutsu; gear: -
- **Kisaragi Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Mount Katsuragi** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Mount Myōboku** - jutsu: Summoning: Food Cart Destroyer Technique, Toad Oil Bullet; gear: Shuigu, Tenseigan (Energy Vessel)
- **Nadeshiko Village** - jutsu: Dance Performance: Second Step, Nadeshiko-Style Deep Crimson Dance Performance, Nadeshiko-Style Hardliner Gale Fist, Nadeshiko-Style Hardliner Revolving Cut, Violent Whirlwind; gear: -
- **Shangri-la** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Sugi Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Takumi Village** - jutsu: Ninja Tools Barrier: Reverse Fish Scales Formation, Rotating Ferocious Wind, Great Vacuum Cannon, Peacock Whirlwind, Peacock Whirlwind Formation, Wind Cutter Technique; gear: Fire Sword, Garian Sword, Infinite Armour, Weaknessless Soaring Shortswords
- **Tonika Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Tree Felling Village** - jutsu: Transformation Technique, Clone Technique; gear: -
- **Tsuchigumo Village** - jutsu: Character Bind Technique, Hidden Rock Trap, Infinite Embrace; gear: -

## Backgrounds

- **Academy Prodigy** (hidden villages): Top of your Academy class. Deep chakra reserves, a head start in rank and the textbook techniques mastered early.
- **Shinobi Orphan** (any village): Raised by nobody, you learned to survive on the streets: tougher, quicker on your feet, never without a spare blade.
- **Anbu Trainee** (hidden villages): Pulled from the Academy into the black-ops program. Precise, lethal and trained in silent infiltration.
- **Wandering Mercenary** (any village): You sold your blade from one war-torn country to the next. Armoured, hard to kill, but with little chakra training.
- **Merchant / Artisan Scion** (any village): Heir of a trading or crafting house. Lucky, well connected, and well supplied with the finest ninja tools money can buy.
