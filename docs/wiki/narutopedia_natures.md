# Narutopedia `Category:Chakra Natures` coverage

Source: https://naruto.fandom.com/wiki/Category:Chakra_Natures - crawled 2026-11-26. Method: list=categorymembers (recursive) for the natures; action=ask [[Chakra Nature::<nature>]] (Semantic MediaWiki) for every page tagged with each nature; prop=revisions for every jutsu infobox.

## Category tree

- `Category:Chakra Natures`: 23 pages, 0 subcategories

The category has no subcategories. Its Semantic MediaWiki grouping is the `Chakra Nature` property, which was queried for each of the 23 natures (treated as the nature's subcategory, per the project rule). **All 23 natures are in the game** as affinities with a release token; none were excluded.

Pages tagged with a nature that are **not jutsu** were not turned into abilities (424): 325 character pages, 51 not a jutsu page (Looking for)s, 33 not a jutsu page (looking for)s, 13 kekkei genkai pages, 1 not a jutsu page (Infobox:Tatsunami)s, 1 not a jutsu page (infobox:White Zetsu)s - they carry the property because characters / kekkei genkai list their natures. The full list is in `narutopedia_natures.json` (`excluded_pages`).

## How nature jutsu are learned

- Every shinobi starts with one basic nature (leaning to the village element) and its first jutsu.
- Hold a release token while defeating enemies: their experience becomes mastery experience for that release (2.5x slower if it is not your nature). Each mastery level unlocks the next jutsu of that release in rank order (rank still gates them).
- Training a basic nature you do not hold to mastery level 10 makes it your nature; two held natures awaken their fusion (Lava = Fire + Earth, Storm = Water + Lightning, Mud = Earth + Water, Yin–Yang = Yin + Yang...). Wood, Ice and Magnet stay clan kekkei genkai (Senju, Yuki, Kazekage).
- Kekkei genkai natures without a two-nature recipe awaken from mastery: Crystal (Earth 30), Typhoon (Wind 30), Dark (Yin 30), Steel (Earth 40), Swift (Lightning 30).
- Clan and village techniques keep their own unlocks (clan skill / village rank) and are not part of nature mastery.

## Natures

| Nature | kekkei genkai | basic natures (wiki) | jutsu |
|---|---|---|---:|
| [Boil Release](https://naruto.fandom.com/wiki/Boil_Release) | yes | Fire Release, Water Release | 17 |
| [Crystal Release](https://naruto.fandom.com/wiki/Crystal_Release) | yes | Earth Release | 22 |
| [Dark Release](https://naruto.fandom.com/wiki/Dark_Release) | yes | - | 2 |
| [Dust Release](https://naruto.fandom.com/wiki/Dust_Release) | no | Fire Release, Wind Release, Earth Release | 3 |
| [Earth Release](https://naruto.fandom.com/wiki/Earth_Release) | no | - | 194 |
| [Explosion Release](https://naruto.fandom.com/wiki/Explosion_Release) | yes | Lightning Release, Earth Release | 29 |
| [Fire Release](https://naruto.fandom.com/wiki/Fire_Release) | no | - | 238 |
| [Ice Release](https://naruto.fandom.com/wiki/Ice_Release) | yes | Wind Release, Water Release | 27 |
| [Lava Release](https://naruto.fandom.com/wiki/Lava_Release) | yes | Fire Release, Earth Release | 29 |
| [Lightning Release](https://naruto.fandom.com/wiki/Lightning_Release) | no | - | 255 |
| [Magnet Release](https://naruto.fandom.com/wiki/Magnet_Release) | yes | Wind Release, Earth Release | 48 |
| [Mud Release](https://naruto.fandom.com/wiki/Mud_Release) | no | Earth Release, Water Release | 4 |
| [Scorch Release](https://naruto.fandom.com/wiki/Scorch_Release) | yes | Fire Release, Wind Release | 5 |
| [Steel Release](https://naruto.fandom.com/wiki/Steel_Release) | yes | - | 4 |
| [Storm Release](https://naruto.fandom.com/wiki/Storm_Release) | yes | Lightning Release, Water Release | 7 |
| [Swift Release](https://naruto.fandom.com/wiki/Swift_Release) | yes | - | 1 |
| [Typhoon Release](https://naruto.fandom.com/wiki/Typhoon_Release) | yes | Wind Release | 2 |
| [Water Release](https://naruto.fandom.com/wiki/Water_Release) | no | - | 266 |
| [Wind Release](https://naruto.fandom.com/wiki/Wind_Release) | no | - | 187 |
| [Wood Release](https://naruto.fandom.com/wiki/Wood_Release) | yes | Earth Release, Water Release | 90 |
| [Yang Release](https://naruto.fandom.com/wiki/Yang_Release) | no | - | 24 |
| [Yin Release](https://naruto.fandom.com/wiki/Yin_Release) | no | - | 139 |
| [Yin–Yang Release](https://naruto.fandom.com/wiki/Yin–Yang_Release) | no | - | 32 |

Total: 1286 nature jutsu (1078 new abilities, the rest already existed as clan / village jutsu). Each is generated from its infobox (classification, rank, range, class, hand signs) by the same mapping as clan jutsu (`tools/catalog/clans.py`).
