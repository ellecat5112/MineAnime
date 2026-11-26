"""Build NarutoRPG ninja tools: JSON catalog + 32x32 sprites.  python3 tools/catalog/build.py"""
import json, os, sys
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import catalog
import spritegen
import model3d

ROOT = os.path.abspath(os.path.join(HERE, '..', '..'))
WIKI = json.load(open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_tools.json'), encoding='utf-8'))
RES = os.path.join(ROOT, 'narutorpg', 'src', 'main', 'resources', 'assets', 'narutorpg')

def complete(e):
    """Guarantee every tool has an attack and a secondary move (owner's rule). Derived from the primary move."""
    from dsl import move, melee, throw, chain, guard, dash, slam, blast, consume, sonic, scan, buff
    if e.get('armor') is None and e['attack'] == {"damage": 0, "speed": 0}:
        e['attack'] = melee(1, -1.0)
    if e['secondary'] is not None or e['primary'] is None:
        return
    p = e['primary']; t = p['type']; prm = p.get('params', {}); fx = p.get('effects'); col = p.get('color')
    v = p.get('variant') or ''
    if t == 'summon':
        sec = chain(1, 12, cd=40, mode='bind', effects=[{"id": "minecraft:slowness", "duration": 80, "amplifier": 3}])   # chakra threads
    elif t == 'throw' and v in ('cloud', 'flash', 'explode', 'explode_water', 'ice', 'spikes', 'fire', 'bubble', 'sonic', 'seal'):
        sec = dict(p); sec['params'] = dict(prm, fuse=60.0, speed=0.8); sec['cooldown'] = p.get('cooldown', 20) + 10   # cook & stick
    elif t == 'throw' and prm.get('consume', 1) == 0:
        sec = dict(p); sec['params'] = dict(prm, count=prm.get('count', 1) + 2, spread=8.0); sec['cooldown'] = p.get('cooldown', 20) * 3  # burst
    elif t == 'throw':
        sec = dash(max(3, prm.get('damage', 4)), 5, cost=2, cd=40)
    elif t == 'consume':
        sec = dict(p); sec['params'] = dict(prm, aoe=5.0)  # share with allies
    elif t == 'blast':
        mode = v.split(':')[0]; elem = v.split(':')[1] if ':' in v else None
        alt = {'beam': 'cone', 'cone': 'beam', 'projectile': 'beam', 'lightning': 'cone'}.get(mode, 'beam')
        sec = blast(alt, prm.get('damage', 6) * 0.7, col or 0xFFFFFF, cost=p.get('cost', 8), cd=p.get('cooldown', 30) + 20, element=elem, rng=prm.get('range', 12), fire=prm.get('fire', 0))
    elif t == 'glide':
        sec = slam(6, 3, cost=4, cd=60)          # dive-bomb landing
    elif t in ('seal',):
        sec = throw(1, 1.6, impact='seal', effects=fx or [{"id": "minecraft:slowness", "duration": 120, "amplifier": 2}], drop=0, consume=0, cd=60)
    elif t == 'sonic':
        sec = sonic([{"id": "minecraft:speed", "duration": 200, "amplifier": 1}, {"id": "minecraft:resistance", "duration": 200}], prm.get('range', 8), allies=1, cost=6)
    elif t == 'scan':
        sec = buff([{"id": "minecraft:night_vision", "duration": 600}, {"id": "minecraft:speed", "duration": 200}], cost=4, cd=300)
    elif t == 'buff':
        sec = sonic(fx or [], 8, allies=1, cost=p.get('cost', 10) * 1.5, cd=p.get('cooldown', 200))   # share the blessing
    elif t == 'slam':
        sec = dash(max(4, prm.get('damage', 6) * 0.8), 5, cost=2, cd=40)
    elif t == 'dash':
        sec = guard(cost=1)
    elif t == 'chain':
        sec = throw(max(2, prm.get('damage', 3)), 2.0, returns=1, consume=0, cd=30)
    elif t == 'guard':
        sec = slam(4, 2, cost=1, cd=40, knock=1.4)    # shield bash
    elif t == 'wall':
        sec = guard(cost=1)
    elif t == 'trap':
        sec = throw(3, 1.8, cd=10)
    elif t == 'vortex':
        sec = blast('projectile', 8, 0x2A0A3A, cost=12, cd=80, radius=3, size=0.9)
    elif t == 'charge_slash':
        sec = dash(prm.get('damage', 8) * 0.7, 6, cost=3)
    elif t == 'gust':
        sec = guard(reflect=1, cost=1)
    elif t == 'sand':
        sec = guard(cost=1)
    else:
        sec = guard(cost=1)
    e['secondary'] = sec
    e['auto_secondary'] = True


def main():
    pages = {p['title']: p for p in WIKI['pages']}
    entries = {e['wiki']: e for e in catalog.T_}
    missing = [t for t in pages if t not in entries and t not in catalog.EXCLUDED]
    unknown = [t for t in entries if t not in pages]
    if missing or unknown:
        sys.exit(f"Catalog out of sync with the wiki crawl.\n missing: {missing}\n unknown: {unknown}")
    ids = [e['id'] for e in catalog.T_]
    dup = {i for i in ids if ids.count(i) > 1}
    if dup:
        sys.exit(f"duplicate ids {dup}")

    HELD = {'consume', 'guard', 'charge_slash'}
    def held(m):
        return m and m['type'] in HELD and not (m['type'] == 'consume' and m.get('params', {}).get('time', 32) <= 1)
    for e in catalog.T_:
        p, s2 = e['primary'], e['secondary']
        if held(p) and held(s2) and p['type'] != s2['type']:
            sys.exit(f"{e['id']}: primary and secondary are different held moves ({p['type']} / {s2['type']})")
        if not p and not s2:
            sys.exit(f"{e['id']}: no moves")
    for e in catalog.T_:
        complete(e)
    out = []
    os.makedirs(os.path.join(RES, 'textures', 'item'), exist_ok=True)
    for e in catalog.T_:
        spec = {k: v for k, v in e.items() if k not in ('sprite',) and v not in (None, [], '')}
        spec['name'] = e['wiki'].replace('Bow & Arrow', 'Bow and Arrow')
        spec['classification'] = pages[e['wiki']]['classification']
        spec['existing'] = e['id'] in catalog.EXISTING
        out.append(spec)
        if e['id'] not in catalog.EXISTING:
            img = spritegen.render(e['sprite'], e['id'])
            img.save(os.path.join(RES, 'textures', 'item', e['id'] + '.png'), optimize=True)
            model, pal = model3d.build(e['sprite']['shape'], e['id'], e['sprite']['colors'])
            if model:
                os.makedirs(os.path.join(RES, 'models', 'item', '3d'), exist_ok=True)
                os.makedirs(os.path.join(RES, 'textures', 'item', '3d'), exist_ok=True)
                with open(os.path.join(RES, 'models', 'item', '3d', e['id'] + '.json'), 'w') as f:
                    json.dump(model, f, separators=(',', ':'))
                pal.save(os.path.join(RES, 'textures', 'item', '3d', e['id'] + '.png'), optimize=True)
                spec['model3d'] = True
    with open(os.path.join(RES, 'ninja_tools.json'), 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=1, ensure_ascii=False)
    write_doc(out, pages)
    print(f"{len(out)} tools ({len(catalog.EXCLUDED)} index pages excluded) -> {RES}")
    # Clans / bloodlines (Category:Clans): catalog for datagen + clan item sprites.
    import clans
    clans.main()
    clans.build_sprites()
    clans.write_doc()
    # Village origins (Category:Villages): factions, backgrounds, village jutsu, gear sprites.
    import villages
    villages.main()
    villages.build_sprites()
    # Technique tokens (AHZNB-style jutsu items) and default cast animations.
    import tokens
    tokens.build()
    import animations
    animations.main()


def mv(m):
    if not m:
        return '-'
    p = m.get('params', {})
    bits = [m['type'] + (f"({m['variant']})" if m.get('variant') else '')]
    for k in ('damage', 'count', 'radius', 'range', 'heal', 'chakra'):
        if p.get(k):
            bits.append(f"{k} {p[k]:g}")
    if m.get('cost'):
        bits.append(f"{m['cost']:g} chakra")
    return ' '.join(bits)


def write_doc(out, pages):
    lines = ["# Narutopedia `Category:Tools` coverage", "",
             f"Source: {WIKI['source']} - crawled {WIKI['crawled']} via {WIKI['method']}.", "",
             f"MediaWiki subcategories: none (`subcats: 0`). The wiki's drill-down groups pages by **Tool classification**; "
             "those groups are treated as the subcategories and each becomes a creative tab.", "",
             "| Group | Tools |", "|---|---|"]
    groups = {}
    for t in out:
        for c in t['classification']:
            groups[c] = groups.get(c, 0) + 1
    for g in ['Offensive', 'Defensive', 'Supplementary', 'Unclassified', 'Generic']:
        lines.append(f"| {g} | {groups.get(g, 0)} |")
    lines += ["", f"**{len(out)} implemented** + {len(catalog.EXCLUDED)} index pages intentionally not turned into items:", ""]
    for t, why in catalog.EXCLUDED.items():
        lines.append(f"- *{t}*: {why}")
    lines += ["", "Controls: LEFT = melee attack, RIGHT = primary move, SNEAK + RIGHT = secondary move "
              "(wearables: RIGHT equips). `*` = secondary derived automatically from the primary move.", "",
              "| Wiki page | Item | Classification | Attack | Primary | Secondary |", "|---|---|---|---|---|---|"]
    for t in sorted(out, key=lambda x: x['wiki'].lower()):
        a = t.get('attack', {})
        atk = 'wearable' if t.get('armor') else f"{a.get('damage', 0):g} dmg" + (f", +{a['reach']:g} reach" if a.get('reach') else '') \
            + ''.join(f", {h['kind']}" for h in a.get('on_hit', []))
        lines.append(f"| {t['wiki']} | `narutorpg:{t['id']}` | {', '.join(t['classification'])} | {atk} | {mv(t.get('primary'))} | "
                     f"{mv(t.get('secondary'))}{' *' if t.get('auto_secondary') else ''} |")
    with open(os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_tools.md'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines) + '\n')

if __name__ == '__main__':
    main()
