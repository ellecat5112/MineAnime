package com.animecore.client.vfx;

import com.animecore.vfx.IllusionProfile;
import com.animecore.vfx.IllusionProfile.Pattern;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

/**
 * Procedural genjutsu visions. Every {@link Pattern} has a full-screen version (what the victim sees) and a world version
 * (what everyone sees around the caster); colours, element count, speed and density come from the technique's
 * {@link IllusionProfile}, so two illusions never look alike. Texture-free (POSITION_COLOR geometry only).
 */
public final class IllusionRenderer {
    private IllusionRenderer() {}

    // =====================================================================================================
    // Screen (2D, HUD space)
    // =====================================================================================================

    /** @param env 0..1 fade envelope, t ticks since start */
    public static void screen(BufferBuilder b, Matrix4f m, IllusionProfile p, int w, int h, float t, float env) {
        float cx = w / 2f, cy = h / 2f, R = (float) Math.hypot(cx, cy);
        int c1 = p.color(), c2 = p.color2(), n = p.count();
        float sp = p.speed(), dens = p.density();
        long seed = (long) p.pattern().ordinal() * 7919L + c1 * 31L + c2 + n;
        // shared base: tint + coloured vignette (strength varies per pattern)
        float tint = switch (p.pattern()) { case BLOOD_MOON, STARS, VORTEX, TENDRILS -> 0.35f; case FOG, SNOW -> 0.25f; default -> 0.12f; };
        rect(b, m, 0, 0, w, h, VfxDraw.argb(p.pattern() == Pattern.BLOOD_MOON ? 0x3A0000 : c2, tint * env));
        radial(b, m, cx, cy, R * 0.5f, R * 1.05f, VfxDraw.argb(c2, 0f), VfxDraw.argb(VfxDraw.lerpRgb(c2, 0, 0.4f), 0.7f * env), 40);
        Random r = Random.create(seed);
        switch (p.pattern()) {
            case SPIRAL -> {
                for (int k = 0; k < n; k++) {
                    float base = t * 0.03f * sp + k * MathHelper.TAU / n;
                    arm(b, m, cx, cy, R, base, 2.4f, 0.55f, 0.07f, c1, 0.34f * env);
                }
            }
            case BLOOD_MOON -> {
                float mx = cx, my = h * 0.26f, mr = Math.min(w, h) * 0.17f;
                disc(b, m, mx, my, mr * 1.8f, VfxDraw.argb(c1, 0.25f * env), VfxDraw.argb(c1, 0f), 32);
                disc(b, m, mx, my, mr, VfxDraw.argb(c1, 0.9f * env), VfxDraw.argb(VfxDraw.lerpRgb(c1, 0, 0.3f), 0.9f * env), 32);
                for (int k = 0; k < 4; k++) disc(b, m, mx + (r.nextFloat() - 0.5f) * mr, my + (r.nextFloat() - 0.5f) * mr, mr * (0.12f + r.nextFloat() * 0.12f),
                        VfxDraw.argb(VfxDraw.lerpRgb(c1, 0, 0.45f), 0.6f * env), VfxDraw.argb(c1, 0f), 12);
                for (int k = 0; k < n; k++) {   // drifting black cloud bands
                    float yy = my + (k - n / 2f) * mr * 0.45f, xx = ((t * 0.6f * sp + k * 97) % (w + 200)) - 100;
                    ellipse(b, m, xx, yy, mr * 1.4f, mr * 0.18f, VfxDraw.argb(0x050000, 0.75f * env), 20);
                }
            }
            case CROWS -> {
                int birds = n * 3;
                for (int k = 0; k < birds; k++) {
                    float phase = r.nextFloat() * 1000, speed = (0.6f + r.nextFloat()) * sp;
                    float x = ((t * speed * 2 + phase) % (w + 80)) - 40, y = h * (0.15f + 0.6f * r.nextFloat()) + MathHelper.sin(t * 0.1f + k) * 12;
                    crow(b, m, x, y, 6 + r.nextFloat() * 8, MathHelper.sin(t * 0.5f * sp + k), VfxDraw.argb(c1, 0.95f * env));
                }
                for (int k = 0; k < 20 * dens; k++) {   // falling feathers
                    float x = (r.nextFloat() * w + MathHelper.sin(t * 0.05f + k) * 20), y = ((t * (0.5f + r.nextFloat()) + r.nextFloat() * h) % h);
                    diamond(b, m, x, y, 2, 6, t * 0.05f + k, VfxDraw.argb(c1, 0.7f * env));
                }
            }
            case PETALS, SNOW -> {
                int count = (int) (60 * dens);
                boolean snow = p.pattern() == Pattern.SNOW;
                for (int k = 0; k < count; k++) {
                    float speed = (0.4f + r.nextFloat()) * sp, x0 = r.nextFloat() * w;
                    float y = ((t * speed + r.nextFloat() * h) % (h + 20)) - 10, x = x0 + MathHelper.sin(t * 0.04f * sp + k) * 25;
                    int col = VfxDraw.argb(k % 2 == 0 ? c1 : c2, (snow ? 0.85f : 0.8f) * env);
                    if (snow) star(b, m, x, y, 3 + r.nextFloat() * 3, 3, t * 0.02f, col);
                    else diamond(b, m, x, y, 3 + r.nextFloat() * 2, 6 + r.nextFloat() * 3, t * 0.06f * sp + k, col);
                }
            }
            case SHARDS -> {
                float ox = cx + (r.nextFloat() - 0.5f) * w * 0.3f, oy = cy + (r.nextFloat() - 0.5f) * h * 0.3f;
                int cracks = n * 3;
                for (int k = 0; k < cracks; k++) {
                    float a0 = k * MathHelper.TAU / cracks + r.nextFloat() * 0.2f, a1 = (k + 1) * MathHelper.TAU / cracks;
                    float shift = MathHelper.sin(t * 0.08f * sp + k) * 4;
                    tri(b, m, ox, oy, ox + MathHelper.cos(a0) * R * 1.4f + shift, oy + MathHelper.sin(a0) * R * 1.4f,
                            ox + MathHelper.cos(a1) * R * 1.4f + shift, oy + MathHelper.sin(a1) * R * 1.4f,
                            VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.05f * env), VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.22f * env), VfxDraw.argb(c2, 0.12f * env));
                    line(b, m, ox, oy, ox + MathHelper.cos(a0) * R * 1.4f, oy + MathHelper.sin(a0) * R * 1.4f, 1.2f, VfxDraw.argb(0xFFFFFF, 0.6f * env));
                }
            }
            case FOG -> {
                for (int k = 0; k < 14 * dens; k++) {
                    float x = ((t * (0.2f + r.nextFloat() * 0.4f) * sp + r.nextFloat() * w) % (w + 200)) - 100, y = r.nextFloat() * h;
                    disc(b, m, x, y, 60 + r.nextFloat() * 90, VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.22f * env), VfxDraw.argb(c1, 0f), 20);
                }
            }
            case CHAINS -> {
                for (int k = 0; k < n + 2; k++) {
                    float a = k * MathHelper.TAU / (n + 2) + 0.3f, sway = MathHelper.sin(t * 0.06f * sp + k) * 0.08f;
                    float sx = cx + MathHelper.cos(a + sway) * R, sy = cy + MathHelper.sin(a + sway) * R;
                    float reach = 0.35f + 0.1f * MathHelper.sin(t * 0.04f + k);
                    float ex = MathHelper.lerp(1 - reach, sx, cx), ey = MathHelper.lerp(1 - reach, sy, cy);
                    int links = 14;
                    for (int l = 0; l < links; l++) {
                        float f = l / (float) links, x = MathHelper.lerp(f, sx, ex), y = MathHelper.lerp(f, sy, ey);
                        float ang = (float) Math.atan2(ey - sy, ex - sx) + (l % 2 == 0 ? 0 : MathHelper.HALF_PI);
                        ellipseRing(b, m, x, y, 9, 5, ang, 2f, VfxDraw.argb(c1, 0.85f * env));
                    }
                }
            }
            case EYES -> {
                for (int k = 0; k < n; k++) {
                    float a = k * MathHelper.TAU / n + 0.4f, x = cx + MathHelper.cos(a) * R * 0.55f, y = cy + MathHelper.sin(a) * R * 0.38f;
                    float open = MathHelper.clamp(Math.abs(MathHelper.sin(t * 0.03f * sp + k * 1.7f)) * 1.4f, 0.05f, 1f);
                    eye(b, m, x, y, 26 + (k % 3) * 6, open, c1, env);
                }
            }
            case TENDRILS -> darkness(b, m, cx, cy, R, t * sp, env, c2, n);
            case FLAMES -> {
                int tongues = (int) (24 * dens) + n;
                for (int k = 0; k < tongues; k++) {
                    float x = (k + 0.5f) * w / tongues, hh = h * (0.12f + 0.12f * Math.abs(MathHelper.sin(t * 0.2f * sp + k * 1.3f)));
                    tri(b, m, x - w / tongues * 0.8f, h, x + w / tongues * 0.8f, h, x + MathHelper.sin(t * 0.3f + k) * 6, h - hh,
                            VfxDraw.argb(c1, 0.85f * env), VfxDraw.argb(c1, 0.85f * env), VfxDraw.argb(c2, 0f));
                }
                rect(b, m, 0, 0, w, h, VfxDraw.argb(c1, 0.08f * env * (0.5f + 0.5f * MathHelper.sin(t * 0.3f))));
            }
            case STARS -> {
                for (int k = 0; k < 90 * dens; k++) {
                    float x = r.nextFloat() * w, y = r.nextFloat() * h, tw = 0.4f + 0.6f * Math.abs(MathHelper.sin(t * 0.07f * sp + k));
                    star(b, m, x, y, 1.5f + r.nextFloat() * 2.5f, 4, 0, VfxDraw.argb(k % 5 == 0 ? c1 : 0xFFFFFF, tw * env));
                }
                float rot = t * 0.004f * sp;   // a constellation of n stars
                float px = 0, py = 0;
                for (int k = 0; k <= n; k++) {
                    float a = rot + k * 1.1f, x = cx + MathHelper.cos(a) * R * (0.2f + 0.05f * k), y = cy + MathHelper.sin(a * 1.3f) * R * 0.25f;
                    if (k > 0) line(b, m, px, py, x, y, 1f, VfxDraw.argb(c1, 0.6f * env));
                    star(b, m, x, y, 4, 4, 0, VfxDraw.argb(c1, env));
                    px = x; py = y;
                }
            }
            case WEB -> {
                float ox = (seed & 1) == 0 ? 0 : w, oy = 0, sway = MathHelper.sin(t * 0.03f * sp) * 6;
                int spokes = n + 6;
                for (int k = 0; k <= spokes; k++) {
                    float a = (ox == 0 ? 0 : MathHelper.HALF_PI) + k * MathHelper.HALF_PI / spokes;
                    line(b, m, ox, oy, ox + MathHelper.cos(a) * R * 1.3f * (ox == 0 ? 1 : -1) + sway, oy + MathHelper.sin(a) * R * 1.3f, 1f, VfxDraw.argb(c1, 0.7f * env));
                }
                for (int ring = 1; ring < 9; ring++) {
                    float rr = ring * R * 0.13f;
                    for (int k = 0; k < spokes; k++) {
                        float a0 = (ox == 0 ? 0 : MathHelper.HALF_PI) + k * MathHelper.HALF_PI / spokes, a1 = a0 + MathHelper.HALF_PI / spokes;
                        float dx = ox == 0 ? 1 : -1;
                        line(b, m, ox + MathHelper.cos(a0) * rr * dx, oy + MathHelper.sin(a0) * rr, ox + MathHelper.cos(a1) * rr * dx, oy + MathHelper.sin(a1) * rr,
                                0.8f, VfxDraw.argb(c1, 0.55f * env));
                    }
                }
            }
            case RINGS -> {
                for (int k = 0; k < n + 2; k++) {
                    float ph = ((t * 0.015f * sp) + k / (float) (n + 2)) % 1f, rr = R * ph;
                    radial(b, m, cx, cy, rr, rr + 4 + 8 * ph, VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.45f * env * (1 - ph)), VfxDraw.argb(c1, 0f), 48);
                }
                int bars = 24;
                for (int k = 0; k < bars; k++) {   // equaliser along the bottom (sound genjutsu)
                    float bh = h * 0.1f * Math.abs(MathHelper.sin(t * 0.25f * sp + k * 0.7f));
                    rect(b, m, k * w / (float) bars + 2, h - bh, (k + 1) * w / (float) bars - 2, h, VfxDraw.argb(c1, 0.5f * env));
                }
            }
            case RAIN -> {
                for (int k = 0; k < 120 * dens; k++) {
                    float x = r.nextFloat() * w, y = ((t * (6 + r.nextFloat() * 4) * sp + r.nextFloat() * h) % (h + 40)) - 20;
                    line(b, m, x, y, x - 4, y + 14, 1f, VfxDraw.argb(c1, 0.5f * env));
                }
            }
            case VORTEX -> {
                for (int k = 0; k < n + 3; k++) arm(b, m, cx, cy, R, -t * 0.05f * sp + k * MathHelper.TAU / (n + 3), 5f, 0.9f, 0.05f, k % 2 == 0 ? c1 : c2, 0.35f * env);
                disc(b, m, cx, cy, R * (0.08f + 0.04f * MathHelper.sin(t * 0.1f)), VfxDraw.argb(0x000000, 0.95f * env), VfxDraw.argb(c2, 0.4f * env), 24);
            }
            case LOTUS -> {
                int petals = n * 2;
                for (int ring = 0; ring < 3; ring++) {
                    float len = R * (0.35f - ring * 0.08f), rot = t * 0.01f * sp * (ring % 2 == 0 ? 1 : -1) + ring * 0.3f;
                    for (int k = 0; k < petals; k++) {
                        float a = rot + k * MathHelper.TAU / petals;
                        petal(b, m, cx, cy, a, len, len * 0.28f, VfxDraw.argb(ring == 1 ? c2 : c1, (0.28f + 0.08f * ring) * env));
                    }
                }
                disc(b, m, cx, cy, R * 0.06f, VfxDraw.argb(0xFFF59D, 0.8f * env), VfxDraw.argb(c1, 0.2f * env), 16);
            }
            case SERPENTS -> {
                for (int k = 0; k < n; k++) {
                    float y0 = h * (k + 0.5f) / n, phase = t * 0.08f * sp + k * 2.1f;
                    float px = -40, py = y0;
                    int seg = 40;
                    float head = ((t * 3 * sp + k * 150) % (w + 200)) - 100;
                    for (int s = 1; s <= seg; s++) {
                        float x = head - s * 9, y = y0 + MathHelper.sin(phase - s * 0.35f) * 22;
                        if (s > 1) line(b, m, px, py, x, y, 7f * (1 - s / (float) seg) + 1, VfxDraw.argb(s % 4 < 2 ? c1 : c2, 0.75f * env));
                        px = x; py = y;
                    }
                }
            }
            case VEINS -> {
                float pulse = 0.5f + 0.5f * MathHelper.sin(t * 0.15f * sp);
                for (int k = 0; k < n + 4; k++) {
                    float a = r.nextFloat() * MathHelper.TAU, x = cx + MathHelper.cos(a) * R, y = cy + MathHelper.sin(a) * R;
                    float dir = a + MathHelper.PI;
                    for (int s = 0; s < 9; s++) {
                        dir += (r.nextFloat() - 0.5f) * 0.9f;
                        float len = R * 0.06f, nx = x + MathHelper.cos(dir) * len, ny = y + MathHelper.sin(dir) * len;
                        line(b, m, x, y, nx, ny, 3.5f - s * 0.3f, VfxDraw.argb(c1, (0.5f + 0.4f * pulse) * env));
                        if (r.nextFloat() < 0.35f) {
                            float bd = dir + (r.nextBoolean() ? 0.8f : -0.8f);
                            line(b, m, nx, ny, nx + MathHelper.cos(bd) * len * 0.8f, ny + MathHelper.sin(bd) * len * 0.8f, 1.5f, VfxDraw.argb(c2, 0.6f * env));
                        }
                        x = nx; y = ny;
                    }
                }
            }
            case KALEIDOSCOPE -> {
                int wedges = n * 2;
                float rot = t * 0.02f * sp;
                for (int k = 0; k < wedges; k++) {
                    float a0 = rot + k * MathHelper.TAU / wedges, a1 = a0 + MathHelper.TAU / wedges;
                    tri(b, m, cx, cy, cx + MathHelper.cos(a0) * R, cy + MathHelper.sin(a0) * R, cx + MathHelper.cos(a1) * R, cy + MathHelper.sin(a1) * R,
                            VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.05f * env), VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.25f * env), VfxDraw.argb(k % 2 == 0 ? c2 : c1, 0.25f * env));
                }
                for (int k = 0; k < wedges; k++) {
                    float a = -rot * 1.5f + k * MathHelper.TAU / wedges;
                    diamond(b, m, cx + MathHelper.cos(a) * R * 0.3f, cy + MathHelper.sin(a) * R * 0.3f, 6, 12, a, VfxDraw.argb(0xFFFFFF, 0.5f * env));
                }
            }
        }
    }

    /** World particle accent while the vision lasts (spawned around the victim). */
    public static ParticleEffect particle(IllusionProfile p) {
        if (p.particle().isPresent() && Registries.PARTICLE_TYPE.get(p.particle().get()) instanceof net.minecraft.particle.SimpleParticleType s) return s;
        return switch (p.pattern()) {
            case PETALS, LOTUS -> ParticleTypes.CHERRY_LEAVES;
            case CROWS, TENDRILS, VORTEX -> ParticleTypes.SQUID_INK;
            case SNOW -> ParticleTypes.SNOWFLAKE;
            case RAIN -> ParticleTypes.FALLING_WATER;
            case FLAMES -> ParticleTypes.FLAME;
            case STARS, KALEIDOSCOPE -> ParticleTypes.END_ROD;
            case VEINS, BLOOD_MOON -> ParticleTypes.DAMAGE_INDICATOR;
            case FOG -> ParticleTypes.CAMPFIRE_COSY_SMOKE;
            case RINGS -> ParticleTypes.NOTE;
            case EYES, SHARDS -> ParticleTypes.ENCHANT;
            case WEB -> ParticleTypes.WHITE_ASH;
            case SERPENTS -> ParticleTypes.SPORE_BLOSSOM_AIR;
            case CHAINS -> ParticleTypes.CRIT;
            default -> ParticleTypes.WITCH;
        };
    }

    // =====================================================================================================
    // World (3D, camera-relative, lightning layer)
    // =====================================================================================================

    public static void world(VertexConsumer vc, Matrix4f m, Vec3d o, IllusionProfile p, float radius, float age, float a) {
        int c1 = p.color(), c2 = p.color2(), n = p.count();
        float sp = p.speed(), r = Math.max(2f, radius);
        Vec3d ground = o.add(0, -0.85, 0), ex = new Vec3d(1, 0, 0), ez = new Vec3d(0, 0, 1);
        double spin = age * 0.05 * sp;
        // every illusion marks the ground with a thin double ring in its colours
        VfxDraw.ring(vc, m, ground, ex, ez, r, 0.1, VfxDraw.argb(c1, 0.7f * a), VfxDraw.argb(c2, 0.5f * a), 40);
        switch (p.pattern()) {
            case SPIRAL, KALEIDOSCOPE -> {
                int wedges = Math.max(3, n * (p.pattern() == Pattern.KALEIDOSCOPE ? 2 : 1));
                for (int k = 0; k < wedges; k++) {
                    double a0 = spin + k * Math.PI * 2 / wedges, a1 = a0 + Math.PI / wedges;
                    VfxDraw.quad2(vc, m, ground, ground.add(Math.cos(a0) * r, 0.02, Math.sin(a0) * r), ground.add(Math.cos(a1) * r, 0.02, Math.sin(a1) * r), ground,
                            VfxDraw.argb(c1, 0.35f * a), VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.1f * a), VfxDraw.argb(c2, 0.1f * a), VfxDraw.argb(c1, 0.35f * a));
                }
            }
            case BLOOD_MOON -> {
                Vec3d moon = o.add(0, 6 + r * 0.3, 0);
                VfxDraw.disc(vc, m, moon, 2.2, VfxDraw.argb(c1, 0.9f * a), VfxDraw.argb(VfxDraw.lerpRgb(c1, 0, 0.4f), 0.6f * a), 28);
                VfxDraw.disc(vc, m, moon, 4.0, VfxDraw.argb(c1, 0.25f * a), VfxDraw.argb(c1, 0f), 28);
            }
            case CROWS -> {
                for (int k = 0; k < n * 2; k++) {
                    double ang = spin * 2 + k * Math.PI * 2 / (n * 2), h = 1.5 + (k % 3) * 0.7;
                    Vec3d body = o.add(Math.cos(ang) * r * 0.7, h, Math.sin(ang) * r * 0.7);
                    Vec3d tangent = new Vec3d(-Math.sin(ang), 0, Math.cos(ang));
                    double flap = Math.sin(age * 0.6 + k) * 0.35;
                    Vec3d side = new Vec3d(Math.cos(ang), 0, Math.sin(ang));
                    VfxDraw.ribbon(vc, m, body, body.add(side.multiply(0.6)).add(0, flap, 0), 0.12, 0.02, VfxDraw.argb(c1, 0.95f * a), VfxDraw.argb(c1, 0.6f * a));
                    VfxDraw.ribbon(vc, m, body, body.subtract(side.multiply(0.6)).add(0, flap, 0), 0.12, 0.02, VfxDraw.argb(c1, 0.95f * a), VfxDraw.argb(c1, 0.6f * a));
                    VfxDraw.ribbon(vc, m, body.subtract(tangent.multiply(0.25)), body.add(tangent.multiply(0.25)), 0.08, 0.04, VfxDraw.argb(c1, a), VfxDraw.argb(c2, a));
                }
            }
            case PETALS, SNOW, STARS -> {
                Random rr = Random.create(c1 * 13L + n);
                for (int k = 0; k < 40; k++) {
                    double ang = rr.nextDouble() * Math.PI * 2 + spin * (0.5 + rr.nextDouble()), rad = rr.nextDouble() * r;
                    double h = (rr.nextDouble() * 3 + age * 0.05 * sp) % 3.5;
                    Vec3d at = o.add(Math.cos(ang) * rad, h - 0.5, Math.sin(ang) * rad);
                    VfxDraw.disc(vc, m, at, p.pattern() == Pattern.STARS ? 0.08 : 0.12, VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.9f * a), VfxDraw.argb(c1, 0.2f * a), 5);
                }
            }
            case SHARDS, EYES -> {
                for (int k = 0; k < n + 2; k++) {
                    double ang = spin + k * Math.PI * 2 / (n + 2);
                    Vec3d at = o.add(Math.cos(ang) * r * 0.55, 1.2 + Math.sin(age * 0.08 + k) * 0.3, Math.sin(ang) * r * 0.55);
                    if (p.pattern() == Pattern.EYES) {
                        VfxDraw.disc(vc, m, at, 0.35, VfxDraw.argb(0xFFFFFF, 0.85f * a), VfxDraw.argb(0xFFFFFF, 0.5f * a), 14);
                        VfxDraw.disc(vc, m, at.multiply(0.98), 0.18, VfxDraw.argb(c1, a), VfxDraw.argb(c2, a), 12);
                    } else {
                        Vec3d u = new Vec3d(Math.cos(ang + 1.2), 0, Math.sin(ang + 1.2)).multiply(0.4);
                        VfxDraw.quad2(vc, m, at.add(u).add(0, 0.5, 0), at.subtract(u).add(0, 0.4, 0), at.subtract(u).add(0, -0.4, 0), at.add(u).add(0, -0.5, 0),
                                VfxDraw.argb(c1, 0.4f * a), VfxDraw.argb(0xFFFFFF, 0.6f * a), VfxDraw.argb(c2, 0.4f * a), VfxDraw.argb(c1, 0.2f * a));
                    }
                }
            }
            case CHAINS -> {
                for (int k = 0; k < n + 1; k++) {
                    double ang = k * Math.PI * 2 / (n + 1);
                    Vec3d base = ground.add(Math.cos(ang) * r * 0.8, 0, Math.sin(ang) * r * 0.8);
                    double top = 3 * Math.min(1, age / 15.0);
                    for (double y = 0; y < top; y += 0.35) {
                        boolean flip = ((int) (y / 0.35)) % 2 == 0;
                        Vec3d c = base.add(Math.sin(age * 0.05 + y) * 0.1, y, 0);
                        Vec3d u = flip ? ex : ez;
                        VfxDraw.ring(vc, m, c, u, new Vec3d(0, 1, 0), 0.16, 0.05, VfxDraw.argb(c1, 0.9f * a), VfxDraw.argb(c2, 0.7f * a), 10);
                    }
                }
            }
            case TENDRILS, SERPENTS, VEINS -> {
                Random rr = Random.create(c2 * 7L + n);
                for (int k = 0; k < n + 3; k++) {
                    double ang = rr.nextDouble() * Math.PI * 2;
                    Vec3d prev = ground;
                    for (int s = 1; s <= 12; s++) {
                        double f = s / 12.0;
                        double wig = Math.sin(age * 0.2 * sp - s * 0.8 + k) * 0.3;
                        Vec3d q = p.pattern() == Pattern.TENDRILS
                                ? ground.add(Math.cos(ang) * r * 0.5 * f, f * 2.5, Math.sin(ang) * r * 0.5 * f).add(wig, 0, -wig)
                                : ground.add(Math.cos(ang + wig) * r * f, 0.03, Math.sin(ang + wig) * r * f);
                        VfxDraw.ribbon(vc, m, prev, q, 0.12 * (1 - f) + 0.02, 0.12 * (1 - f), VfxDraw.argb(s % 3 == 0 ? c2 : c1, 0.8f * a), VfxDraw.argb(c1, 0.6f * a));
                        prev = q;
                    }
                }
            }
            case FLAMES -> {
                int tongues = 18 + n;
                for (int k = 0; k < tongues; k++) {
                    double a0 = k * Math.PI * 2 / tongues, a1 = (k + 1) * Math.PI * 2 / tongues;
                    double hh = 0.8 + 0.6 * Math.abs(Math.sin(age * 0.3 * sp + k));
                    Vec3d p0 = ground.add(Math.cos(a0) * r, 0, Math.sin(a0) * r), p1 = ground.add(Math.cos(a1) * r, 0, Math.sin(a1) * r);
                    VfxDraw.quad2(vc, m, p0, p1, p1.add(0, hh * 0.6, 0), p0.add(0, hh, 0), VfxDraw.argb(c1, 0.8f * a), VfxDraw.argb(c1, 0.8f * a),
                            VfxDraw.argb(c2, 0f), VfxDraw.argb(c2, 0f));
                }
            }
            case WEB, LOTUS -> {
                int spokes = n * 2 + (p.pattern() == Pattern.WEB ? 6 : 0);
                for (int k = 0; k < spokes; k++) {
                    double ang = spin * (p.pattern() == Pattern.LOTUS ? 1 : 0) + k * Math.PI * 2 / spokes;
                    Vec3d tip = ground.add(Math.cos(ang) * r, p.pattern() == Pattern.LOTUS ? 0.6 : 0.02, Math.sin(ang) * r);
                    VfxDraw.ribbon(vc, m, ground.add(0, 0.02, 0), tip, p.pattern() == Pattern.LOTUS ? 0.35 : 0.03, 0.03,
                            VfxDraw.argb(c1, 0.7f * a), VfxDraw.argb(c2, 0.5f * a));
                }
                if (p.pattern() == Pattern.WEB) for (int ring = 1; ring < 5; ring++)
                    VfxDraw.ring(vc, m, ground.add(0, 0.03, 0), ex, ez, r * ring / 5.0, 0.03, VfxDraw.argb(c1, 0.6f * a), VfxDraw.argb(c1, 0.6f * a), spokes);
            }
            case RINGS, FOG -> {
                for (int k = 0; k < n + 1; k++) {
                    double ph = ((age * 0.04 * sp) + k / (double) (n + 1)) % 1.0;
                    VfxDraw.ring(vc, m, ground.add(0, ph * 2.5, 0), ex, ez, r * ph, 0.2, VfxDraw.argb(k % 2 == 0 ? c1 : c2, 0.6f * a * (float) (1 - ph)),
                            VfxDraw.argb(c1, 0f), 32);
                }
            }
            case RAIN -> {
                Random rr = Random.create(n * 31L);
                for (int k = 0; k < 50; k++) {
                    double ang = rr.nextDouble() * Math.PI * 2, rad = rr.nextDouble() * r, y = 4 - ((age * 0.5 * sp + rr.nextDouble() * 4) % 4);
                    Vec3d top = o.add(Math.cos(ang) * rad, y, Math.sin(ang) * rad);
                    VfxDraw.ribbon(vc, m, top, top.add(0, -0.5, 0), 0.02, 0.02, VfxDraw.argb(c1, 0.6f * a), VfxDraw.argb(c1, 0.2f * a));
                }
            }
            case VORTEX -> {
                for (int arm = 0; arm < n + 2; arm++) {
                    Vec3d prev = null;
                    for (int s = 0; s <= 20; s++) {
                        double f = s / 20.0, ang = -spin * 3 + arm * Math.PI * 2 / (n + 2) + f * 6;
                        Vec3d q = o.add(Math.cos(ang) * r * (1 - f), f * 3 - 0.8, Math.sin(ang) * r * (1 - f));
                        if (prev != null) VfxDraw.ribbon(vc, m, prev, q, 0.1, 0.06, VfxDraw.argb(arm % 2 == 0 ? c1 : c2, 0.7f * a), VfxDraw.argb(c1, 0.5f * a));
                        prev = q;
                    }
                }
            }
        }
    }

    // =====================================================================================================
    // 2D helpers
    // =====================================================================================================

    static void tri(BufferBuilder b, Matrix4f m, float x0, float y0, float x1, float y1, float x2, float y2, int c0, int c1, int c2) {
        b.vertex(m, x0, y0, 0).color(c0);
        b.vertex(m, x1, y1, 0).color(c1);
        b.vertex(m, x2, y2, 0).color(c2);
    }

    static void rect(BufferBuilder b, Matrix4f m, float x0, float y0, float x1, float y1, int c) {
        tri(b, m, x0, y0, x0, y1, x1, y1, c, c, c);
        tri(b, m, x0, y0, x1, y1, x1, y0, c, c, c);
    }

    static void radial(BufferBuilder b, Matrix4f m, float cx, float cy, float r0, float r1, int c0, int c1, int segs) {
        for (int i = 0; i < segs; i++) {
            float t0 = MathHelper.TAU * i / segs, t1 = MathHelper.TAU * (i + 1) / segs;
            float ax = cx + MathHelper.cos(t0) * r0, ay = cy + MathHelper.sin(t0) * r0, bx = cx + MathHelper.cos(t1) * r0, by = cy + MathHelper.sin(t1) * r0;
            float cxo = cx + MathHelper.cos(t0) * r1, cyo = cy + MathHelper.sin(t0) * r1, dx = cx + MathHelper.cos(t1) * r1, dy = cy + MathHelper.sin(t1) * r1;
            tri(b, m, ax, ay, cxo, cyo, dx, dy, c0, c1, c1);
            tri(b, m, ax, ay, dx, dy, bx, by, c0, c1, c0);
        }
    }

    static void disc(BufferBuilder b, Matrix4f m, float cx, float cy, float r, int center, int edge, int segs) {
        for (int i = 0; i < segs; i++) {
            float t0 = MathHelper.TAU * i / segs, t1 = MathHelper.TAU * (i + 1) / segs;
            tri(b, m, cx, cy, cx + MathHelper.cos(t0) * r, cy + MathHelper.sin(t0) * r, cx + MathHelper.cos(t1) * r, cy + MathHelper.sin(t1) * r, center, edge, edge);
        }
    }

    static void ellipse(BufferBuilder b, Matrix4f m, float cx, float cy, float rx, float ry, int c, int segs) {
        for (int i = 0; i < segs; i++) {
            float t0 = MathHelper.TAU * i / segs, t1 = MathHelper.TAU * (i + 1) / segs;
            tri(b, m, cx, cy, cx + MathHelper.cos(t0) * rx, cy + MathHelper.sin(t0) * ry, cx + MathHelper.cos(t1) * rx, cy + MathHelper.sin(t1) * ry, c, c, c);
        }
    }

    static void ellipseRing(BufferBuilder b, Matrix4f m, float cx, float cy, float rx, float ry, float rot, float width, int c) {
        int segs = 12;
        float cr = MathHelper.cos(rot), sr = MathHelper.sin(rot);
        for (int i = 0; i < segs; i++) {
            float t0 = MathHelper.TAU * i / segs, t1 = MathHelper.TAU * (i + 1) / segs;
            float x0 = MathHelper.cos(t0) * rx, y0 = MathHelper.sin(t0) * ry, x1 = MathHelper.cos(t1) * rx, y1 = MathHelper.sin(t1) * ry;
            line(b, m, cx + x0 * cr - y0 * sr, cy + x0 * sr + y0 * cr, cx + x1 * cr - y1 * sr, cy + x1 * sr + y1 * cr, width, c);
        }
    }

    static void line(BufferBuilder b, Matrix4f m, float x0, float y0, float x1, float y1, float width, int c) {
        float dx = x1 - x0, dy = y1 - y0, len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 1e-3) return;
        float nx = -dy / len * width / 2, ny = dx / len * width / 2;
        tri(b, m, x0 + nx, y0 + ny, x0 - nx, y0 - ny, x1 - nx, y1 - ny, c, c, c);
        tri(b, m, x0 + nx, y0 + ny, x1 - nx, y1 - ny, x1 + nx, y1 + ny, c, c, c);
    }

    static void diamond(BufferBuilder b, Matrix4f m, float x, float y, float rw, float rh, float rot, int c) {
        float cr = MathHelper.cos(rot), sr = MathHelper.sin(rot);
        float ax = x - sr * rh, ay = y + cr * rh, bx = x + cr * rw, by = y + sr * rw, cx = x + sr * rh, cy = y - cr * rh, dx = x - cr * rw, dy = y - sr * rw;
        tri(b, m, ax, ay, bx, by, cx, cy, c, c, c);
        tri(b, m, ax, ay, cx, cy, dx, dy, c, c, c);
    }

    static void star(BufferBuilder b, Matrix4f m, float x, float y, float r, int points, float rot, int c) {
        for (int k = 0; k < points; k++) {
            float a = rot + k * MathHelper.PI / points;
            line(b, m, x - MathHelper.cos(a) * r, y - MathHelper.sin(a) * r, x + MathHelper.cos(a) * r, y + MathHelper.sin(a) * r, 1.2f, c);
        }
    }

    static void petal(BufferBuilder b, Matrix4f m, float cx, float cy, float a, float len, float wid, int c) {
        float tx = cx + MathHelper.cos(a) * len, ty = cy + MathHelper.sin(a) * len;
        float mx = cx + MathHelper.cos(a) * len * 0.5f, my = cy + MathHelper.sin(a) * len * 0.5f;
        float nx = -MathHelper.sin(a) * wid, ny = MathHelper.cos(a) * wid;
        tri(b, m, cx, cy, mx + nx, my + ny, tx, ty, VfxDraw.argb(c, ((c >>> 24) & 255) / 255f * 0.4f), c, c);
        tri(b, m, cx, cy, tx, ty, mx - nx, my - ny, VfxDraw.argb(c, ((c >>> 24) & 255) / 255f * 0.4f), c, c);
    }

    static void arm(BufferBuilder b, Matrix4f m, float cx, float cy, float R, float base, float sweep, float reach, float width, int color, float alpha) {
        int steps = 28;
        for (int i = 0; i < steps; i++) {
            float t0 = i / (float) steps, t1 = (i + 1) / (float) steps;
            float a0 = base + t0 * sweep, a1 = base + t1 * sweep, r0 = R * (0.06f + reach * t0), r1 = R * (0.06f + reach * t1);
            float w0 = R * width * (1 - t0) + 2, w1 = R * width * (1 - t1) + 2;
            int c0 = VfxDraw.argb(color, alpha * (1 - t0 * 0.7f)), c1 = VfxDraw.argb(color, alpha * (1 - t1 * 0.7f));
            float x0 = cx + MathHelper.cos(a0) * r0, y0 = cy + MathHelper.sin(a0) * r0, x1 = cx + MathHelper.cos(a1) * r1, y1 = cy + MathHelper.sin(a1) * r1;
            float nx0 = MathHelper.cos(a0), ny0 = MathHelper.sin(a0), nx1 = MathHelper.cos(a1), ny1 = MathHelper.sin(a1);
            tri(b, m, x0 - nx0 * w0, y0 - ny0 * w0, x0 + nx0 * w0, y0 + ny0 * w0, x1 + nx1 * w1, y1 + ny1 * w1, c0, c0, c1);
            tri(b, m, x0 - nx0 * w0, y0 - ny0 * w0, x1 + nx1 * w1, y1 + ny1 * w1, x1 - nx1 * w1, y1 - ny1 * w1, c0, c1, c1);
        }
    }

    static void crow(BufferBuilder b, Matrix4f m, float x, float y, float s, float flap, int c) {
        ellipse(b, m, x, y, s * 0.55f, s * 0.22f, c, 10);
        float wy = y - flap * s * 0.8f;
        tri(b, m, x - s * 0.1f, y, x - s * 1.3f, wy, x - s * 0.4f, y + s * 0.1f, c, c, c);
        tri(b, m, x + s * 0.1f, y, x + s * 1.3f, wy, x + s * 0.4f, y + s * 0.1f, c, c, c);
        tri(b, m, x + s * 0.5f, y, x + s * 0.8f, y - s * 0.12f, x + s * 0.8f, y + s * 0.12f, c, c, c);
    }

    static void eye(BufferBuilder b, Matrix4f m, float x, float y, float s, float open, int iris, float env) {
        int white = VfxDraw.argb(0xF5F5F5, 0.85f * env), ir = VfxDraw.argb(iris, 0.95f * env);
        ellipse(b, m, x, y, s, s * 0.45f * open, white, 18);
        if (open > 0.3f) {
            disc(b, m, x, y, s * 0.32f * Math.min(1, open * 1.5f), ir, ir, 14);
            disc(b, m, x, y, s * 0.12f, VfxDraw.argb(0x000000, env), VfxDraw.argb(0x000000, env), 10);
        }
        ellipseRing(b, m, x, y, s, s * 0.45f * open + 1, 0, 2f, VfxDraw.argb(0x000000, 0.8f * env));
    }

    static void darkness(BufferBuilder b, Matrix4f m, float cx, float cy, float R, float time, float env, int color, int lobes) {
        int segs = 64;
        float inner = R * (1.0f - 0.55f * env);
        for (int i = 0; i < segs; i++) {
            float a0 = MathHelper.TAU * i / segs, a1 = MathHelper.TAU * (i + 1) / segs;
            float w0 = 1 + 0.18f * MathHelper.sin(a0 * (lobes + 4) + time * 0.2f) + 0.1f * MathHelper.sin(a0 * 13 - time * 0.33f);
            float w1 = 1 + 0.18f * MathHelper.sin(a1 * (lobes + 4) + time * 0.2f) + 0.1f * MathHelper.sin(a1 * 13 - time * 0.33f);
            float ri0 = inner * w0, ri1 = inner * w1, ro = R * 1.2f;
            int edge = VfxDraw.argb(VfxDraw.lerpRgb(color, 0, 0.85f), 0.92f * env), soft = VfxDraw.argb(color, 0f);
            float ix0 = cx + MathHelper.cos(a0) * ri0, iy0 = cy + MathHelper.sin(a0) * ri0, ix1 = cx + MathHelper.cos(a1) * ri1, iy1 = cy + MathHelper.sin(a1) * ri1;
            float sx0 = cx + MathHelper.cos(a0) * ri0 * 0.8f, sy0 = cy + MathHelper.sin(a0) * ri0 * 0.8f;
            float sx1 = cx + MathHelper.cos(a1) * ri1 * 0.8f, sy1 = cy + MathHelper.sin(a1) * ri1 * 0.8f;
            float ox0 = cx + MathHelper.cos(a0) * ro, oy0 = cy + MathHelper.sin(a0) * ro, ox1 = cx + MathHelper.cos(a1) * ro, oy1 = cy + MathHelper.sin(a1) * ro;
            tri(b, m, ix0, iy0, ox0, oy0, ox1, oy1, edge, edge, edge);
            tri(b, m, ix0, iy0, ox1, oy1, ix1, iy1, edge, edge, edge);
            tri(b, m, sx0, sy0, ix0, iy0, ix1, iy1, soft, edge, edge);
            tri(b, m, sx0, sy0, ix1, iy1, sx1, sy1, soft, edge, soft);
        }
    }
}
