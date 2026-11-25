package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.platform.AnimeParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Comparator;
import java.util.List;

/** Instant beam along the look vector: Kamehameha (fired), Cero, Chidori Spear, Hollow Purple. */
public record BeamRaycastAction(float range, float damage, float width, boolean pierce, int fireTicks, int color)
        implements IAbilityAction {
    public static final MapCodec<BeamRaycastAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(1f, 128f).fieldOf("range").forGetter(BeamRaycastAction::range),
            Codec.FLOAT.optionalFieldOf("damage", 6f).forGetter(BeamRaycastAction::damage),
            Codec.floatRange(0.05f, 8f).optionalFieldOf("width", 0.5f).forGetter(BeamRaycastAction::width),
            Codec.BOOL.optionalFieldOf("pierce", true).forGetter(BeamRaycastAction::pierce),
            Codec.INT.optionalFieldOf("fire_ticks", 0).forGetter(BeamRaycastAction::fireTicks),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(BeamRaycastAction::color)
    ).apply(i, BeamRaycastAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.BEAM_RAYCAST; }

    @Override
    public void execute(AbilityContext ctx) {
        float w = width * ctx.scale();
        Vec3d start = ctx.eyePos();
        Vec3d dir = ctx.look();
        BlockHitResult blockHit = ctx.world().raycast(new RaycastContext(start, start.add(dir.multiply(range)),
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ctx.caster()));
        final Vec3d blockEnd = blockHit.getPos();
        Vec3d end = blockEnd;

        List<Entity> hits = ctx.world().getOtherEntities(ctx.caster(), new Box(start, blockEnd).expand(w),
                e -> e instanceof LivingEntity && e.isAlive() && e.getBoundingBox().expand(w).raycast(start, blockEnd).isPresent());
        hits.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(start)));
        for (Entity e : hits) {
            ctx.damage((LivingEntity) e, damage, fireTicks, 0.4f);
            if (!pierce) {
                end = e.getPos().add(0, e.getHeight() / 2, 0);
                break;
            }
        }

        double length = start.distanceTo(end);
        for (double d = 0.5; d < length; d += 0.4) {
            Vec3d p = start.add(dir.multiply(d));
            ctx.world().spawnParticles(AnimeParticles.dust(color, Math.min(4f, 0.8f + w)), p.x, p.y, p.z, 1, w * 0.2, w * 0.2, w * 0.2, 0);
        }
    }
}
