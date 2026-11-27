"""Descriptions for every NarutoRPG jutsu (the production standard: every technique explains itself in its tooltip).

  python3 tools/catalog/jutsu_descriptions.py --crawl
      fetches the lead paragraph of every jutsu page named by the generated catalogs from Narutopedia
      (action=query&prop=revisions, 50 titles per request, redirects followed) into docs/wiki/narutopedia_jutsu_leads.json
  python3 tools/catalog/jutsu_descriptions.py
      writes tools/catalog/generated/jutsu_descriptions.json ({"lang": {"ability.narutorpg.<id>.desc": ...}}): the wiki lead
      (trimmed to two sentences), fan genjutsu from their Naruto Fanon description, and for the few jutsu without a page a
      description written from the infobox (rank, classification, range) and what the technique does in game.
Run after the other jutsu catalogs (clans.py ... forms.py).
"""
import glob
import json
import os
import re
import sys
import time
import urllib.parse
import urllib.request

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
GEN = os.path.join(ROOT, 'tools', 'catalog', 'generated')
LEADS = os.path.join(ROOT, 'docs', 'wiki', 'narutopedia_jutsu_leads.json')
OUT = os.path.join(GEN, 'jutsu_descriptions.json')
API = 'https://naruto.fandom.com/api.php'
CATALOGS = ['clan_catalog', 'village_catalog', 'nature_catalog', 'taijutsu_catalog', 'ninjutsu_catalog', 'schools_catalog', 'fanon_genjutsu_catalog']


def abilities():
    out = {}
    for c in CATALOGS:
        p = os.path.join(GEN, c + '.json')
        if not os.path.exists(p): continue
        for aid, a in json.load(open(p, encoding='utf-8')).get('abilities', {}).items():
            out[aid] = (c, a)
    return out


def q(params):
    data = urllib.parse.urlencode(dict(params, format='json')).encode()
    for i in range(6):
        try:
            req = urllib.request.Request(API, data=data, headers={'User-Agent': 'mod-research-bot/1.0'})
            return json.load(urllib.request.urlopen(req, timeout=120))
        except Exception as e:  # noqa: BLE001
            err = e
            time.sleep(4)
    raise err


def strip_templates(t):
    out, i, depth = [], 0, 0
    while i < len(t):
        if t.startswith('{{', i):
            depth += 1; i += 2; continue
        if t.startswith('}}', i) and depth:
            depth -= 1; i += 2; continue
        if depth == 0: out.append(t[i])
        i += 1
    return ''.join(out)


def lead(wikitext):
    t = re.sub(r'<ref[^>]*/>', '', wikitext)
    t = re.sub(r'<ref[^>]*>.*?</ref>', '', t, flags=re.S)
    t = re.sub(r'<!--.*?-->', '', t, flags=re.S)
    t = strip_templates(t)
    t = re.split(r'^==', t, flags=re.M)[0]
    t = re.sub(r'\[\[(?:File|Image|Category):[^\]]*\]\]', '', t)
    t = re.sub(r'\[\[([^\]|]*)\|([^\]]*)\]\]', r'\2', t)
    t = re.sub(r'\[\[([^\]]*)\]\]', r'\1', t)
    t = re.sub(r"'''?", '', t)
    t = re.sub(r'<[^>]+>', '', t)
    t = re.sub(r'\s*\([^()]*[　-鿿][^()]*\)', '', t)   # (螺旋丸, Literally meaning: ...)
    t = re.sub(r'__\w+__', '', t)
    paras = [p.strip() for p in t.split('\n') if len(p.strip()) > 40]
    return re.sub(r'\s+', ' ', paras[0]) if paras else ''


def crawl():
    titles = sorted({a['name']['fallback'] for c, a in abilities().values() if c != 'fanon_genjutsu_catalog'})
    leads = json.load(open(LEADS, encoding='utf-8')) if os.path.exists(LEADS) else {}
    todo = [t for t in titles if t not in leads]
    print(f'{len(titles)} jutsu titles, {len(todo)} to fetch')
    for i in range(0, len(todo), 50):
        chunk = todo[i:i + 50]
        d = q(dict(action='query', prop='revisions', rvprop='content', rvslots='main', redirects=1, titles='|'.join(chunk)))
        red = {r['from']: r['to'] for r in d['query'].get('redirects', [])}
        norm = {n['from']: n['to'] for n in d['query'].get('normalized', [])}
        by = {p['title']: p['revisions'][0]['slots']['main']['*'] for p in d['query']['pages'].values() if 'revisions' in p}
        for t in chunk:
            key = red.get(norm.get(t, t), norm.get(t, t))
            leads[t] = lead(by.get(key, ''))
        if i // 50 % 10 == 0: print('fetched', i + len(chunk), flush=True)
    json.dump({k: leads[k] for k in sorted(leads)}, open(LEADS, 'w', encoding='utf-8'), indent=0, ensure_ascii=False)
    print('leads with text:', sum(1 for v in leads.values() if v))


def two_sentences(text, limit=320):
    parts = re.split(r'(?<=[.!?])\s+(?=[A-Z])', text.strip())
    out = ''
    for p in parts:
        if len(out) + len(p) > limit and out: break
        out = (out + ' ' + p).strip()
    return out[:limit].rstrip() + ('' if out.endswith(('.', '!', '?')) else '.')


VERB = {'animecore:projectile': 'fires {count}projectile{s}', 'animecore:beam_raycast': 'fires a beam', 'animecore:aoe_sphere': 'blasts the area around',
        'animecore:dash_strike': 'dashes in and strikes', 'animecore:self_buff': 'empowers the user', 'animecore:area_effect': 'affects everyone nearby',
        'animecore:raise_wall': 'raises a wall', 'animecore:summon_entity': 'summons help', 'animecore:rush': 'unleashes a barrage',
        'animecore:remote_strike': 'strikes from afar', 'animecore:charm': 'controls the minds of others'}


def synthesize(a):
    """A description from the technique itself when no wiki text exists."""
    acts = []
    for x in a.get('actions', []):
        v = VERB.get(x.get('type'))
        if not v: continue
        n = x.get('count', 1)
        acts.append(v.format(count=f'{n} ' if n > 1 else 'a ', s='s' if n > 1 else ''))
    name = a['name']['fallback']
    what = ', then '.join(dict.fromkeys(acts)) or 'channels chakra into a technique'
    rank = a.get('required_tier', 1)
    return f'{name}: the user {what}. Learned from rank {rank}.'


def main():
    leads = json.load(open(LEADS, encoding='utf-8')) if os.path.exists(LEADS) else {}
    fanon = {}
    fp = os.path.join(ROOT, 'docs', 'wiki', 'narutofanon_genjutsu.json')
    if os.path.exists(fp):
        for g in json.load(open(fp, encoding='utf-8')).get('genjutsu', {}).values():
            fanon[g.get('title', '')] = g.get('description', '')
    lang, counts = {}, {'wiki': 0, 'fanon': 0, 'synthesized': 0}
    for aid, (cat, a) in sorted(abilities().items()):
        title = a['name']['fallback']
        text = fanon.get(title) if cat == 'fanon_genjutsu_catalog' else leads.get(title)
        if text:
            text = re.sub(r'\s+', ' ', text).strip()
            if not text[:1].isupper(): text = f'{title} {text}'   # Fanon descriptions drop the subject ("is a B-rank...")
            text = re.sub(r'\ban ([B-DF-HJ-NP-TV-Z])-rank', r'a \1-rank', text)
            desc = two_sentences(text)
            counts['fanon' if cat == 'fanon_genjutsu_catalog' else 'wiki'] += 1
        else:
            desc = synthesize(a)
            counts['synthesized'] += 1
        lang[f'ability.narutorpg.{aid}.desc'] = desc
    json.dump({'generated_from': os.path.relpath(LEADS, ROOT), 'lang': lang}, open(OUT, 'w', encoding='utf-8'), indent=1, ensure_ascii=False, sort_keys=True)
    print(f'jutsu descriptions: {len(lang)} ({counts})')


if __name__ == '__main__':
    crawl() if '--crawl' in sys.argv else main()
