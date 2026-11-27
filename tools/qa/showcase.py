"""Production showcase: launches the real remapped jars (prodClient) and plays a scripted scene, saving screenshots.

    python3 tools/qa/showcase.py jojorpg            # tools/qa/showcase_jojorpg.txt -> dist/qa/jojorpg/*.png + contact.png
    python3 tools/qa/showcase.py narutorpg
    python3 tools/qa/showcase.py jojorpg my_steps.txt

Part of the production standard (CLAUDE.md): every feature that players see gets a step in a showcase script, and the
screenshots are looked at before shipping. Needs Xvfb, python3-xlib (Xlib) and ImageMagick ``import`` (all in the
dev container). The world is a fresh copy of tools/qa/world_template (flat, creative, cheats on, no saved player).

Script lines (``#`` comments allowed):
    sleep <s> | cmd /<command> | key <keysym> | keydown/keyup <keysym> | click <x> <y> [button] | press/release <button>
    move <x> <y> | scroll <+1|-1> | shot <name>
Commands are typed into chat; leave a ``sleep`` before ``cmd`` lines that follow key presses.
"""
import os
import shutil
import subprocess
import sys
import time

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))


def main():
    mod = sys.argv[1] if len(sys.argv) > 1 else 'jojorpg'
    steps_file = sys.argv[2] if len(sys.argv) > 2 else os.path.join(ROOT, 'tools', 'qa', f'showcase_{mod}.txt')
    out = os.path.join(ROOT, 'dist', 'qa', mod)
    os.makedirs(out, exist_ok=True)
    saves = os.path.join(ROOT, mod, 'run-prod', 'saves', 'showcase')
    shutil.rmtree(saves, ignore_errors=True)
    os.makedirs(saves)
    shutil.copy(os.path.join(ROOT, 'tools', 'qa', 'world_template', 'level.dat'), saves)
    from Xlib import X, XK, display
    from Xlib.ext import xtest
    xvfb = subprocess.Popen(['Xvfb', ':99', '-screen', '0', '1280x720x24', '-ac'], stderr=subprocess.DEVNULL)
    time.sleep(2)
    log_path = os.path.join(out, 'client.log')
    log = open(log_path, 'w')
    game = subprocess.Popen(['./gradlew', f':{mod}:prodClient', '--console=plain', '-PprodQuickPlay=showcase'], cwd=ROOT,
                            env=dict(os.environ, DISPLAY=':99'), stdout=log, stderr=subprocess.STDOUT)
    try:
        end = time.time() + 400
        while time.time() < end and 'joined the game' not in open(log_path, errors='ignore').read():
            if game.poll() is not None: raise SystemExit('the client exited before joining - see ' + log_path)
            time.sleep(1)
        d = display.Display(':99')

        def kc(name): return d.keysym_to_keycode(XK.string_to_keysym(name))

        def key(name):
            xtest.fake_input(d, X.KeyPress, kc(name)); d.sync(); time.sleep(0.05); xtest.fake_input(d, X.KeyRelease, kc(name)); d.sync()

        names = {' ': 'space', '/': 'slash', '.': 'period', '-': 'minus', ',': 'comma', '[': 'bracketleft', ']': 'bracketright', '=': 'equal'}
        shifted = {'!': '1', '@': '2', ':': 'semicolon', '_': 'minus', '~': 'grave', '^': '6', '{': 'bracketleft', '}': 'bracketright', '"': 'apostrophe'}

        def typ(s):
            for ch in s:
                if ch.isupper() or ch in shifted:
                    xtest.fake_input(d, X.KeyPress, kc('Shift_L')); d.sync()
                    key(shifted.get(ch, ch.lower()))
                    xtest.fake_input(d, X.KeyRelease, kc('Shift_L')); d.sync()
                else:
                    key(names.get(ch, ch))
                time.sleep(0.04)

        def move(x, y): xtest.fake_input(d, X.MotionNotify, x=x, y=y); d.sync()

        def button(b, down): xtest.fake_input(d, X.ButtonPress if down else X.ButtonRelease, b); d.sync()

        shots = []
        move(640, 360)
        time.sleep(12)
        for line in open(steps_file).read().splitlines():
            st = line.split()
            if not st or st[0].startswith('#'): continue
            c = st[0]
            if c == 'sleep': time.sleep(float(st[1]))
            elif c == 'cmd':
                key('t'); time.sleep(0.6); typ(' '.join(st[1:])); time.sleep(0.2); key('Return'); time.sleep(0.15)
            elif c == 'key': key(st[1])
            elif c == 'keydown': xtest.fake_input(d, X.KeyPress, kc(st[1])); d.sync()
            elif c == 'keyup': xtest.fake_input(d, X.KeyRelease, kc(st[1])); d.sync()
            elif c == 'move': move(int(st[1]), int(st[2]))
            elif c == 'click':
                move(int(st[1]), int(st[2])); time.sleep(0.2); b = int(st[3]) if len(st) > 3 else 1
                button(b, True); time.sleep(0.08); button(b, False)
            elif c == 'press': button(int(st[1]), True)
            elif c == 'release': button(int(st[1]), False)
            elif c == 'scroll': b = 4 if int(st[1]) > 0 else 5; button(b, True); time.sleep(0.05); button(b, False)
            elif c == 'shot':
                path = os.path.join(out, st[1] + '.png')
                subprocess.run(['import', '-display', ':99', '-window', 'root', path])
                shots.append(path)
                print('shot', path, flush=True)
        contact(shots, os.path.join(out, 'contact.png'))
    finally:
        game.terminate()
        subprocess.run(['pkill', '-f', 'KnotClient'])
        time.sleep(2)
        xvfb.terminate()
    errors = [l for l in open(log_path, errors='ignore') if ('Exception' in l or 'ERROR' in l)
              and not any(n in l for n in ('yggdrasil', 'minecraftservices', 'sessionserver', 'UnknownHost', 'OpenAL', 'SoundSystem', 'ALSA',
                                           'data fixer', 'ealms'))]
    print(f'{len(shots)} screenshots in {out}; {len(errors)} unexpected error lines in client.log')
    for l in errors[:10]: print('  ', l.rstrip())


def contact(paths, out):
    """All screenshots on one sheet, two per row."""
    if not paths: return
    from PIL import Image
    ims = [Image.open(p).convert('RGB').resize((640, 360)) for p in paths]
    sheet = Image.new('RGB', (1280, 360 * ((len(ims) + 1) // 2)))
    for i, im in enumerate(ims): sheet.paste(im, ((i % 2) * 640, (i // 2) * 360))
    sheet.save(out)


if __name__ == '__main__':
    main()
