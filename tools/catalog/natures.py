"""Nature release catalog (Narutopedia Category:Chakra Natures) for NarutoRPG.

Input : docs/wiki/narutopedia_natures.json (23 natures + every jutsu tagged with each nature via Semantic MediaWiki)
        tools/catalog/generated/clan_catalog.json / village_catalog.json (abilities that already exist)
Output: tools/catalog/generated/nature_catalog.json (AbilityDefinition entries in codec shape + lang), docs/wiki/narutopedia_natures.md

Every nature jutsu becomes an ability on its release token; the free ones (not owned by a clan or village) unlock in rank
order through nature mastery (hold the release token while defeating enemies). Run after clans.py and villages.py.
"""
import json
import os

import clans

ROOT = clans.ROOT
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_natures.json')
OUT = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'nature_catalog.json')
VILLAGES = os.path.join(ROOT, 'tools', 'catalog', 'generated', 'village_catalog.json')
NS = clans.NS


def main():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    clan_cat = json.load(open(clans.OUT, encoding='utf-8'))
    vill_cat = json.load(open(VILLAGES, encoding='utf-8'))
    existing = set(clan_cat['abilities']) | set(vill_cat['abilities']) | {'fireball_jutsu', 'substitution_jutsu'}
    used_combos = set(clans.RESERVED_COMBOS)
    for cat in (clan_cat, vill_cat):
        for a in cat['abilities'].values():
            if a.get('combo'):
                used_combos.add(tuple(x.split(':')[1] for x in a['combo']))
    out = {'generated_from': os.path.relpath(CRAWL, ROOT), 'crawled': crawl['crawled'], 'abilities': {}, 'lang': {}}
    jutsu = crawl['jutsu']
    for title in sorted(jutsu):
        aid = clans.ALIASES.get(title) or clans.slug(title)
        if aid in existing or aid in out['abilities']:
            continue
        d, _rank = clans.build_ability(title, jutsu[title], '', {}, used_combos)
        d['name'] = clans.text(f'ability.{NS}.{aid}', jutsu[title]['title'])
        out['abilities'][aid] = d
        out['lang'][f'ability.{NS}.{aid}'] = jutsu[title]['title']
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    print(f"nature jutsu: {len(jutsu)} on the wiki, {len(out['abilities'])} new abilities "
          f"({len(jutsu) - len(out['abilities'])} already existed as clan / village jutsu)")
    write_doc(crawl, out, existing)


def write_doc(crawl, cat, existing):
    from collections import Counter
    reasons = Counter(crawl['excluded_pages'].values())
    L = ['# Narutopedia `Category:Chakra Natures` coverage', '',
         f"Source: {crawl['source']} - crawled {crawl['crawled']}. Method: {crawl['method']}.", '',
         '## Category tree', '']
    for c, v in crawl['category_tree'].items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories")
    L += ['', 'The category has no subcategories. Its Semantic MediaWiki grouping is the `Chakra Nature` property, which was queried '
          'for each of the 23 natures (treated as the nature\'s subcategory, per the project rule). **All 23 natures are in the game** '
          'as affinities with a release token; none were excluded.', '',
          f"Pages tagged with a nature that are **not jutsu** were not turned into abilities ({len(crawl['excluded_pages'])}): "
          + ', '.join(f'{n} {r}s' for r, n in reasons.most_common()) + ' - they carry the property because characters / '
          'kekkei genkai list their natures. The full list is in `narutopedia_natures.json` (`excluded_pages`).', '',
          '## How nature jutsu are learned', '',
          '- Every shinobi starts with one basic nature (leaning to the village element) and its first jutsu.',
          '- Hold a release token while defeating enemies: their experience becomes mastery experience for that release (2.5x slower '
          'if it is not your nature). Each mastery level unlocks the next jutsu of that release in rank order (rank still gates them).',
          '- Training a basic nature you do not hold to mastery level 10 makes it your nature; two held natures awaken their fusion '
          '(Lava = Fire + Earth, Storm = Water + Lightning, Mud = Earth + Water, Yin–Yang = Yin + Yang...). Wood, Ice and Magnet '
          'stay clan kekkei genkai (Senju, Yuki, Kazekage).',
          '- Kekkei genkai natures without a two-nature recipe awaken from mastery: Crystal (Earth 30), Typhoon (Wind 30), Dark '
          '(Yin 30), Steel (Earth 40), Swift (Lightning 30).',
          '- Clan and village techniques keep their own unlocks (clan skill / village rank) and are not part of nature mastery.', '',
          '## Natures', '', '| Nature | kekkei genkai | basic natures (wiki) | jutsu |', '|---|---|---|---:|']
    for n, v in sorted(crawl['natures'].items()):
        L.append(f"| [{n}](https://naruto.fandom.com/wiki/{n.replace(' ', '_')}) | {'yes' if v['kekkei_genkai'] else 'no'} | "
                 f"{', '.join(v['basic_natures']) or '-'} | {len(v['jutsu'])} |")
    L += ['', f"Total: {len(crawl['jutsu'])} nature jutsu ({len(cat['abilities'])} new abilities, the rest already existed as clan / "
          'village jutsu). Each is generated from its infobox (classification, rank, range, class, hand signs) by the same mapping '
          'as clan jutsu (`tools/catalog/clans.py`).']
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_natures.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutopedia_natures.md')


if __name__ == '__main__':
    main()
