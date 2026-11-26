"""Jutsu-school catalog (Narutopedia "Jutsu classification": Hiden, Medical Ninjutsu, Fūinjutsu, Bukijutsu, Dōjutsu and
Kekkei Genkai / Kekkei Mōra / Kekkei Tōta techniques, with their sub-types) for NarutoRPG.

Input : docs/wiki/narutopedia_schools.json (crawl: the matching categories + Semantic MediaWiki groupings
        [[Jutsu classification::<school>]] for every school and sub-type, with every page's infobox fields)
        tools/catalog/generated/*_catalog.json from the earlier generators (abilities that already exist)
Output: tools/catalog/generated/schools_catalog.json (AbilityDefinition entries + lang), docs/wiki/narutopedia_schools.md

Most techniques of these schools already exist (clan, village, nature, taijutsu and ninjutsu catalogs); the rest are
generated here by the shared jutsu mapping (clans.build_ability) onto their school's technique item. Run after ninjutsu.py.
"""
import json
import os
from collections import Counter

import clans

ROOT = clans.ROOT
NS = clans.NS
CRAWL = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_schools.json')
GEN = os.path.join(ROOT, 'tools', 'catalog', 'generated')
OUT = os.path.join(GEN, 'schools_catalog.json')
EARLIER = ['clan_catalog.json', 'village_catalog.json', 'nature_catalog.json', 'fanon_genjutsu_catalog.json', 'taijutsu_catalog.json',
           'ninjutsu_catalog.json']


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
    print(f"schools: {len(jutsu)} on the wiki, {len(out['abilities'])} new abilities, {len(already)} already existed")
    print(' tokens:', dict(tokens.most_common(12)))
    write_doc(crawl, out, already, tokens, natures)


def write_doc(crawl, cat, already, tokens, natures):
    L = ['# Narutopedia jutsu schools coverage (Hiden, Medical, Fūinjutsu, Bukijutsu, Dōjutsu, Kekkei Genkai/Mōra/Tōta)', '',
         f"Crawled {crawl['crawled']}. Method: {crawl['method']}.", '',
         '## Categories', '']
    for c, v in crawl['category_tree'].items():
        L.append(f"- `{c}`: {len(v['pages'])} pages, {len(v['subcats'])} subcategories" + (' (does not exist on Narutopedia: the school is '
                 'grouped only through the Jutsu classification property)' if not v['pages'] and not v['subcats'] else ''))
    L += ['', 'Each school and its sub-types are Semantic MediaWiki values of **Jutsu classification**, treated as the category and its '
          'subcategories per the project rule:', '', '| school | grouping | pages |', '|---|---|---:|']
    for g, vals in crawl['groups'].items():
        for v, pages in vals.items():
            L.append(f'| {g} | {v} | {len(pages)} |')
    L += ['', f"**{len(crawl['jutsu'])} unique technique pages.** {len(cat['abilities'])} are new abilities in this catalog; {len(already)} "
          'already existed in earlier catalogs (clans, villages, natures, taijutsu, ninjutsu) and keep their unlock route.', '',
          f"- {len(natures)} pages are natures / kekkei genkai, not techniques, and are already in the game as natures or bloodline forms: "
          + (', '.join(natures) or 'none') + '.',
          '- Kekkei genkai, mōra and tōta **pages** (the bloodlines themselves: Sharingan, Shikotsumyaku, Rinne Sharingan...) are '
          'covered as forms in `narutopedia_forms.md`.', '',
          '## Where the new techniques go', '', '| technique item | new |', '|---|---:|']
    for t, n in tokens.most_common():
        L.append(f'| `{t}` | {n} |')
    L += ['', 'Medical ninjutsu heal the caster **and allies within 6-9 blocks** (players, tamed animals, allied story characters). '
          'Bukijutsu (Kenjutsu, Shurikenjutsu and weapon techniques) have their own **Bukijutsu** technique item.', '',
          '## All techniques', '', '| technique | school | rank (estimated) | in game as |', '|---|---|---|---|']
    have = set(cat['abilities'])
    school = {}
    for g, vals in crawl['groups'].items():
        for pages in vals.values():
            for t in pages: school.setdefault(t, g)
    for t in sorted(crawl['jutsu']):
        aid = clans.ALIASES.get(t) or clans.slug(t)
        L.append(f"| [{t}](https://naruto.fandom.com/wiki/{t.replace(' ', '_')}) | {school.get(t, '-')} | "
                 f"{clans.estimated_rank(crawl['jutsu'][t])} | `{NS}:{aid}`{'' if aid in have else ' (existing)'} |")
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_schools.md'), 'w', encoding='utf-8') as fh:
        fh.write('\n'.join(L) + '\n')
    print('doc -> docs/wiki/narutopedia_schools.md')


if __name__ == '__main__':
    main()
