package com.animecore.tool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/** Ray / cone helpers shared by all tool moves. */
public final class ToolTargeting {
    private ToolTargeting() {}

    public static BlockHitResult blockRay(PlayerEntity player, double range) {
        Vec3d start = player.getEyePos();
        return player.getWorld().raycast(new RaycastContext(start, start.add(player.getRotationVec(1f).multiply(range)),
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
    }

    /** End of the look ray: the block hit, or the point at max range. */
    public static Vec3d lookPoint(PlayerEntity player, double range) {
        BlockHitResult hit = blockRay(player, range);
        return hit.getType() == HitResult.Type.MISS ? player.getEyePos().add(player.getRotationVec(1f).multiply(range)) : hit.getPos();
    }

    /** Nearest living entity under the crosshair within range (walls block the ray). */
    public static @Nullable LivingEntity lookEntity(PlayerEntity player, double range) {
        Vec3d start = player.getEyePos();
        Vec3d end = lookPoint(player, range);
        List<Entity> hits = player.getWorld().getOtherEntities(player, new Box(start, end).expand(1.0),
                e -> e instanceof LivingEntity && e.isAlive() && e.getBoundingBox().expand(0.4).raycast(start, end).isPresent());
        return hits.stream().min(Comparator.comparingDouble(e -> e.squaredDistanceTo(player))).map(e -> (LivingEntity) e).orElse(null);
    }

    /** Living entities within {@code range} whose direction from the player is within the cone (dot >= minDot). */
    public static List<LivingEntity> cone(PlayerEntity player, double range, double minDot) {
        Vec3d look = player.getRotationVec(1f);
        Vec3d eye = player.getEyePos();
        return player.getWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(range), e -> {
            if (e == player || !e.isAlive()) return false;
            Vec3d to = e.getPos().add(0, e.getHeight() / 2, 0).subtract(eye);
            double d = to.length();
            return d <= range && (d < 1.2 || to.normalize().dotProduct(look) >= minDot);
        });
    }

    public static List<LivingEntity> sphere(Entity center, Vec3d pos, double radius) {
        return center.getWorld().getEntitiesByClass(LivingEntity.class, new Box(pos, pos).expand(radius),
                e -> e != center && e.isAlive() && e.squaredDistanceTo(pos) <= radius * radius);
    }
}
