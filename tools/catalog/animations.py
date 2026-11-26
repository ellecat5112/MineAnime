"""Default technique cast animations (PlayerAnimator / GeckoLib keyframe JSON) for animecore.

Written to animecore/src/main/resources/assets/animecore/player_animations/casts.json as animecore:cast_<name>.
Abilities without an explicit cast_animation get one of these from AbilityVfx.defaultAnimation.
Run: python3 tools/catalog/animations.py
"""
import json
import os

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
OUT = os.path.join(ROOT, 'animecore', 'src', 'main', 'resources', 'assets', 'animecore', 'player_animations', 'casts.json')
Z = [0, 0, 0]


def anim(length, **bones):
    out = {}
    for bone, frames in bones.items():
        out[bone] = {'rotation': {f'{t:g}': list(rot) for t, rot in frames}}
    return {'animation_length': length, 'bones': out}


def hold(length, pose, t_in=0.12, t_out=0.25):
    """Rest -> pose -> hold -> rest."""
    return [(0.0, Z), (t_in, pose), (round(length - t_out, 3), pose), (length, Z)]


A = {
    # breath techniques: hands at the mouth, lean forward, head up
    'cast_exhale': anim(1.1,
                        head=hold(1.1, [-18, 0, 0]), body=hold(1.1, [10, 0, 0]),
                        rightArm=hold(1.1, [-105, -25, 0]), leftArm=hold(1.1, [-95, 28, 0])),
    # palm strike / ball release: both palms thrust forward
    'cast_thrust': anim(0.8,
                        body=[(0.0, Z), (0.1, [-6, 0, 0]), (0.25, [10, 0, 0]), (0.55, [10, 0, 0]), (0.8, Z)],
                        rightArm=[(0.0, Z), (0.1, [-40, 0, 10]), (0.25, [-90, -8, 0]), (0.55, [-90, -8, 0]), (0.8, Z)],
                        leftArm=[(0.0, Z), (0.1, [-40, 0, -10]), (0.25, [-90, 8, 0]), (0.55, [-90, 8, 0]), (0.8, Z)]),
    # beams: right arm extended, two fingers pointed, left hand supporting the wrist
    'cast_point': anim(1.0,
                       rightArm=hold(1.0, [-90, -5, 0]), leftArm=hold(1.0, [-75, 40, 0]), body=hold(1.0, [0, -12, 0])),
    # bursts / shockwaves: crouch, then fling the arms outward
    'cast_burst': anim(0.9,
                       body=[(0.0, Z), (0.15, [18, 0, 0]), (0.3, [-8, 0, 0]), (0.65, [-8, 0, 0]), (0.9, Z)],
                       rightArm=[(0.0, Z), (0.15, [-30, 0, 30]), (0.3, [-20, 0, 110]), (0.65, [-20, 0, 110]), (0.9, Z)],
                       leftArm=[(0.0, Z), (0.15, [-30, 0, -30]), (0.3, [-20, 0, -110]), (0.65, [-20, 0, -110]), (0.9, Z)],
                       rightLeg=hold(0.9, [-10, 0, 12]), leftLeg=hold(0.9, [10, 0, -12])),
    # illusions / dojutsu: one hand raised to the eye, head tilted, gaze locked
    'cast_gaze': anim(1.2,
                      head=hold(1.2, [-5, 0, -10]), rightArm=hold(1.2, [-140, -35, 0]), leftArm=hold(1.2, [-20, 0, -15])),
    # sweeping area techniques (swarms, mist, sand): wide horizontal arm sweep
    'cast_sweep': anim(0.9,
                       body=[(0.0, Z), (0.2, [0, 35, 0]), (0.5, [0, -30, 0]), (0.7, [0, -30, 0]), (0.9, Z)],
                       rightArm=[(0.0, Z), (0.2, [-80, 70, 0]), (0.5, [-85, -50, 0]), (0.7, [-85, -50, 0]), (0.9, Z)],
                       leftArm=hold(0.9, [-30, 0, -20])),
    # walls / summons: drop down and slam both palms on the ground
    'cast_slam': anim(1.0,
                      body=[(0.0, Z), (0.15, [-10, 0, 0]), (0.35, [40, 0, 0]), (0.75, [40, 0, 0]), (1.0, Z)],
                      head=hold(1.0, [-25, 0, 0]),
                      rightArm=[(0.0, Z), (0.15, [-170, 0, 10]), (0.35, [-60, 0, 5]), (0.75, [-60, 0, 5]), (1.0, Z)],
                      leftArm=[(0.0, Z), (0.15, [-170, 0, -10]), (0.35, [-60, 0, -5]), (0.75, [-60, 0, -5]), (1.0, Z)],
                      rightLeg=hold(1.0, [-40, 0, 0]), leftLeg=hold(1.0, [20, 0, 0])),
    # buffs / modes / gates: fists clenched at the sides, chest out, powering up
    'cast_focus': anim(1.2,
                       body=hold(1.2, [-10, 0, 0]), head=hold(1.2, [-12, 0, 0]),
                       rightArm=hold(1.2, [-25, 0, 35]), leftArm=hold(1.2, [-25, 0, -35]),
                       rightLeg=hold(1.2, [0, 0, 10]), leftLeg=hold(1.2, [0, 0, -10])),
    # dashes / taijutsu: forward lunge with a leading strike
    'cast_lunge': anim(0.7,
                       body=hold(0.7, [25, 0, 0], 0.08, 0.2), rightArm=hold(0.7, [-95, 0, 0], 0.08, 0.2),
                       leftArm=hold(0.7, [40, 0, -10], 0.08, 0.2), rightLeg=hold(0.7, [-45, 0, 0], 0.08, 0.2),
                       leftLeg=hold(0.7, [35, 0, 0], 0.08, 0.2)),
}


def main():
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, 'w') as f:
        json.dump({'format_version': '1.8.0', 'animations': A}, f, indent=1)
    print(f'{len(A)} cast animations -> {OUT}')


if __name__ == '__main__':
    main()
