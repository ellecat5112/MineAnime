package com.animecore.client.vfx;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * Procedural, texture-free geometry for technique effects (POSITION_COLOR quads; used with
 * {@code RenderLayer.getLightning()} for additive glow and {@code getDebugQuads()} for solid/dark matter).
 * All positions are camera-relative.
 */
public final class VfxDraw {
    private VfxDraw() {}

    public static int argb(int rgb, float alpha) {
        return (MathHelper.clamp((int) (alpha * 255), 0, 255) << 24) | (rgb & 0xFFFFFF);
    }

    public static int lerpRgb(int a, int b, float t) {
        t = MathHelper.clamp(t, 0f, 1f);
        int r = (int) MathHelper.lerp(t, (a >> 16) & 0xFF, (b >> 16) & 0xFF);
        int g = (int) MathHelper.lerp(t, (a >> 8) & 0xFF, (b >> 8) & 0xFF);
        int bl = (int) MathHelper.lerp(t, a & 0xFF, b & 0xFF);
        return (r << 16) | (g << 8) | bl;
    }

    public static void quad(VertexConsumer vc, Matrix4f m, Vec3d a, Vec3d b, Vec3d c, Vec3d d, int ca, int cb, int cc, int cd) {
        vc.vertex(m, (float) a.x, (float) a.y, (float) a.z).color(ca);
        vc.vertex(m, (float) b.x, (float) b.y, (float) b.z).color(cb);
        vc.vertex(m, (float) c.x, (float) c.y, (float) c.z).color(cc);
        vc.vertex(m, (float) d.x, (float) d.y, (float) d.z).color(cd);
    }

    /** Double-sided (lightning layer has no culling, debug quads do) quad. */
    public static void quad2(VertexConsumer vc, Matrix4f m, Vec3d a, Vec3d b, Vec3d c, Vec3d d, int ca, int cb, int cc, int cd) {
        quad(vc, m, a, b, c, d, ca, cb, cc, cd);
        quad(vc, m, d, c, b, a, cd, cc, cb, ca);
    }

    /** Camera-facing ribbon segment from p to q with half-widths wp / wq. */
    public static void ribbon(VertexConsumer vc, Matrix4f m, Vec3d p, Vec3d q, double wp, double wq, int cp, int cq) {
        Vec3d dir = q.subtract(p);
        Vec3d mid = p.add(q).multiply(0.5);
        Vec3d side = dir.crossProduct(mid).normalize(); // camera at origin: perpendicular to view and direction
        if (!Double.isFinite(side.x) || side.lengthSquared() < 1e-8) side = new Vec3d(0, 1, 0);
        quad2(vc, m, p.add(side.multiply(wp)), p.subtract(side.multiply(wp)), q.subtract(side.multiply(wq)), q.add(side.multiply(wq)),
                cp, cp, cq, cq);
    }

    /** Camera-facing disc (fan of quads) at c. */
    public static void disc(VertexConsumer vc, Matrix4f m, Vec3d c, double r, int center, int edge, int segments) {
        Vec3d n = c.normalize().multiply(-1);
        if (!Double.isFinite(n.x)) n = new Vec3d(0, 0, 1);
        Vec3d u = perpendicular(n), v = n.crossProduct(u);
        for (int i = 0; i < segments; i++) {
            double a0 = Math.PI * 2 * i / segments, a1 = Math.PI * 2 * (i + 1) / segments;
            Vec3d p0 = c.add(u.multiply(Math.cos(a0) * r)).add(v.multiply(Math.sin(a0) * r));
            Vec3d p1 = c.add(u.multiply(Math.cos(a1) * r)).add(v.multiply(Math.sin(a1) * r));
            quad2(vc, m, c, p0, p1, c, center, edge, edge, center);
        }
    }

    /** Flat annulus around c in the plane spanned by u / v (unit). */
    public static void ring(VertexConsumer vc, Matrix4f m, Vec3d c, Vec3d u, Vec3d v, double r, double width, int inner, int outer, int segments) {
        for (int i = 0; i < segments; i++) {
            double a0 = Math.PI * 2 * i / segments, a1 = Math.PI * 2 * (i + 1) / segments;
            Vec3d d0 = u.multiply(Math.cos(a0)).add(v.multiply(Math.sin(a0)));
            Vec3d d1 = u.multiply(Math.cos(a1)).add(v.multiply(Math.sin(a1)));
            double ri = Math.max(0, r - width / 2), ro = r + width / 2;
            quad2(vc, m, c.add(d0.multiply(ri)), c.add(d0.multiply(ro)), c.add(d1.multiply(ro)), c.add(d1.multiply(ri)), inner, outer, outer, inner);
        }
    }

    /** Small tumbling cube (rock / ice / wood chunk) - use with a non-additive layer. */
    public static void cube(VertexConsumer vc, Matrix4f m, Vec3d c, double h, double rx, double ry, int rgb, float alpha) {
        Vec3d[] p = new Vec3d[8];
        int k = 0;
        for (int x = -1; x <= 1; x += 2) for (int y = -1; y <= 1; y += 2) for (int z = -1; z <= 1; z += 2) {
            p[k++] = rotate(new Vec3d(x * h, y * h, z * h), rx, ry).add(c);
        }
        int[][] faces = {{0, 1, 3, 2}, {4, 6, 7, 5}, {0, 4, 5, 1}, {2, 3, 7, 6}, {0, 2, 6, 4}, {1, 5, 7, 3}};
        float[] shade = {0.7f, 0.85f, 0.6f, 1f, 0.8f, 0.9f};
        for (int f = 0; f < 6; f++) {
            int col = argb(lerpRgb(0, rgb, shade[f]), alpha);
            int[] q = faces[f];
            quad2(vc, m, p[q[0]], p[q[1]], p[q[2]], p[q[3]], col, col, col, col);
        }
    }

    public static Vec3d rotate(Vec3d v, double rx, double ry) {
        double cy = Math.cos(ry), sy = Math.sin(ry), cx = Math.cos(rx), sx = Math.sin(rx);
        double x = v.x * cy + v.z * sy, z = -v.x * sy + v.z * cy;
        double y = v.y * cx - z * sx;
        z = v.y * sx + z * cx;
        return new Vec3d(x, y, z);
    }

    public static Vec3d perpendicular(Vec3d n) {
        Vec3d a = Math.abs(n.y) < 0.9 ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
        return n.crossProduct(a).normalize();
    }
}
