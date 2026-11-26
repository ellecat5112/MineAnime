package com.animecore.movement;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/** Shared blink/teleport helpers. */
public final class MotionUtil {
    private MotionUtil() {}

    /** Clips a blink destination before walls (raycast at waist height) so nobody teleports into blocks. */
    public static Vec3d clipDestination(World world, Entity entity, Vec3d from, Vec3d to) {
        // try the straight path, then the same path raised one and two blocks: a dash should vault over steps,
        // platforms and low walls instead of stopping dead at the first ledge (gravity settles the landing)
        Vec3d best = null;
        double bestDist = -1;
        for (int lift = 0; lift <= 2; lift++) {
            Vec3d up = new Vec3d(0, lift, 0);
            Vec3d end = clipOnce(world, entity, from.add(up), to.add(up));
            double d = end.subtract(from.add(up)).horizontalLengthSquared();
            if (d > bestDist + 0.25) {
                best = end;
                bestDist = d;
            }
            if (d >= to.subtract(from).horizontalLengthSquared() - 1e-3) break;   // unobstructed
            if (lift < 2 && !headroom(world, entity, from.add(0, lift + 1, 0))) break;     // ceiling above: cannot climb
        }
        return best;
    }

    private static boolean headroom(World world, Entity entity, Vec3d feet) {
        return world.isSpaceEmpty(entity, entity.getType().getDimensions().getBoxAt(feet).contract(0.05));
    }

    private static Vec3d clipOnce(World world, Entity entity, Vec3d from, Vec3d to) {
        Vec3d waist = new Vec3d(0, 0.5, 0);
        BlockHitResult hit = world.raycast(new RaycastContext(from.add(waist), to.add(waist),
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
        Vec3d end = hit.getPos().subtract(waist);
        if (hit.getType() == BlockHitResult.Type.MISS) return end;
        Vec3d back = from.subtract(end);
        return back.lengthSquared() > 1.0E-4 ? end.add(back.normalize().multiply(0.4)) : end;
    }
}
