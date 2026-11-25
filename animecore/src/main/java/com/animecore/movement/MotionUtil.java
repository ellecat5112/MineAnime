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
        Vec3d waist = new Vec3d(0, 0.5, 0);
        BlockHitResult hit = world.raycast(new RaycastContext(from.add(waist), to.add(waist),
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
        Vec3d end = hit.getPos().subtract(waist);
        if (hit.getType() == BlockHitResult.Type.MISS) return end;
        Vec3d back = from.subtract(end);
        return back.lengthSquared() > 1.0E-4 ? end.add(back.normalize().multiply(0.4)) : end;
    }
}
