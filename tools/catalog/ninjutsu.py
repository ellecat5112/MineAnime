"""Ninjutsu catalog (Narutopedia "Jutsu classification: Ninjutsu" and every ninjutsu sub-type) for NarutoRPG.

Input : docs/wiki/narutopedia_ninjutsu.json (crawl: Category:Ninjutsu tree + Semantic MediaWiki groupings
        [[Jutsu classification::Ninjutsu]], Barrier / Clone / Cooperation / Medical / Reincarnation / Space–Time /
        Chakra Absorption / Chakra Flow / Regeneration / Shape Transformation, with every page's infobox fields)
        tools/catalog/generated/*_catalog.json from the earlier generators (abilities that already exist)
Output: tools/catalog/generated/ninjutsu_catalog.json (AbilityDefinition entries + lang), docs/wiki/narutopedia_ninjutsu.md

Each new ninjutsu is generated from its infobox by the shared jutsu mapping (clans.build_ability): nature-release
ninjutsu sit on their release token, the rest on the Ninjutsu token or their school's token (medical, dojutsu...),
and unlock through mastery. Run after taijutsu.py.
"""
import json
import os
from collections import Counter

import clans

ROOT = clans.ROOT
NS = clans.NS
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_ninjutsu.json')
GEN = os.path.join(ROOT, 'tools', 'catalog', 'generated')
OUT = os.path.join(GEN, 'ninjutsu_catalog.json')
EARLIER = ['clan_catalog.json', 'village_catalog.json', 'nature_catalog.json', 'fanon_genjutsu_catalog.json', 'taijutsu_catalog.json']


import re
NOT_TECHNIQUE = re.compile(r"^(\w+ Release|Shikotsumyaku|.*'s? (Clan's )?Kekkei Genkai|.* Clan's Kekkei Genkai)$")


def main():
    crawl = json.load(open(CRAWL, encoding='utf-8'))
    existing = {'fireball_jutsu', 'substitution_jutsu'}
    used_combos = set(clans.RESERVED_COMBOS)
    for f in EARLIER:
        cat = json.load(open(os.path.join(GEN, f), encoding='utf-8'))
        existing |= set(cat['abilities'])
        for a in cat['abilities'].values():
            if a.get('combo'):
                used_combos.add(tuple(x.split(':')[1] for x in a['combo']))
    jutsu = crawl['jutsu']
    out = {'generated_from': os.path.relpath(CRAWL, ROOT), 'crawled': crawl['crawled'], 'abilities': {}, 'lang': {}}
    already, natures = [], []
    for title in sorted(jutsu):
        if NOT_TECHNIQUE.search(title):   # a nature / kekkei genkai page, already in the game as an affinity / bloodline
            natures.append(title)
            continue
        aid = clans.ALIASES.get(title) or clans.slug(title)
        if aid in existing or aid in out['abilities']:
            already.append(title)
            continue
        d, _rank = clans.build_ability(title, jutsu[title], '', {}, used_combos)
        d['name'] = clans.text(f'ability.{NS}.{aid}', title)
        out['abilities'][aid] = d
        out['lang'][f'ability.{NS}.{aid}'] = title
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False, sort_keys=True)
    tokens = Counter(a.get('token') or a.get('affinity') or f'{NS}:ninjutsu' for a in out['abilities'].values())
    print(f"ninjutsu: {len(jutsu)} on the wiki, {len(out['abilities'])} new abilities, {len(already)} already existed")
    print(' tokens:', dict(tokens.most_common(12)))
    write_doc(crawl, out, already, tokens, natures)


def write_doc(crawl, cat, already, tokens, natures):
    L = ['# Narutopedia Ninjutsu coverage', '',
         f"Source: {crawl['source']} - crawled {crawl['crawled']}. Method: {crawl['method']}.", '',
         '## Category tree and groupings', '']
    for c, v in crawl['category_tree'].items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories")
    L += ['', '`Category:Ninjutsu` does not exist on Narutopedia (the API reports the category page as missing). Ninjutsu are '
          'grouped by the Semantic MediaWiki property **Jutsu classification**, so its value *Ninjutsu* and every ninjutsu '
          'sub-type value are treated as the category and its subcategories, per the project rule:', '',
          '| grouping (Jutsu classification) | pages |', '|---|---:|']
    for g, pages in crawl['groups'].items():
        L.append(f'| {g} | {len(pages)} |')
    L += ['', f"**{len(crawl['jutsu'])} unique ninjutsu pages.** {len(cat['abilities'])} are new abilities; {len(already)} already "
          'existed as clan, village, nature-release or taijutsu techniques and keep their existing unlock route.', '',
          f"- {len(natures)} pages are natures or kekkei genkai, not techniques, and are already in the game as natures "
          '(affinities with a release token) or clan bloodlines, so they were not turned into abilities: ' + ', '.join(natures) + '.', '',
          '### Pages intentionally not turned into ninjutsu', '',
          'Narutopedia lists these as separate jutsu schools, not ninjutsu sub-types, so pages carrying only these '
          'classifications are out of scope here (pages that are *also* classified Ninjutsu are included above):', '']
    for g, v in crawl['separate_schools'].items():
        L.append(f"- **{g}** ({len(v['not_tagged_ninjutsu'])} pages not tagged Ninjutsu): " + ', '.join(v['not_tagged_ninjutsu']))
    L += ['', 'The articles *Ninjutsu* and the sub-type articles themselves are topic pages, not techniques.', '',
          '## Where the new ninjutsu go', '', '| technique item | new ninjutsu |', '|---|---:|']
    for t, n in tokens.most_common():
        L.append(f'| `{t}` | {n} |')
    L += ['', 'Each is generated from its infobox (classification, rank, range, class type, hand signs, nature) by the shared '
          'mapping in `tools/catalog/clans.py`: attacks become projectiles / beams / bursts by range and nature, binds and '
          'prisons hold targets, barriers raise walls, clones and space-time techniques move you, medical ninjutsu heal. '
          'Hold the item while defeating enemies to master the next technique (rank still gates them).', '',
          '## All ninjutsu', '', '| technique | rank (estimated) | class type | in game as |', '|---|---|---|---|']
    have = set(cat['abilities'])
    for t in sorted(crawl['jutsu']):
        info = crawl['jutsu'][t]
        aid = clans.ALIASES.get(t) or clans.slug(t)
        L.append(f"| [{t}](https://naruto.fandom.com/wiki/{t.replace(' ', '_')}) | {clans.estimated_rank(info)} | "
                 f"{info['jutsu class type'] or '-'} | `{NS}:{aid}`{'' if aid in have else ' (existing)'} |")
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_ninjutsu.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutopedia_ninjutsu.md')


if __name__ == '__main__':
    main()
