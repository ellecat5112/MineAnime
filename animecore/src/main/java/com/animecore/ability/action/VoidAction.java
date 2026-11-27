package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.entity.ManifestationEntity;
import com.animecore.manifestation.VoidManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

/**
 * Cream: the caster hides inside a sphere of the void for {@code duration_ticks}, invisible and untouchable, and moves
 * along their gaze at {@code speed} blocks per tick erasing everything in a {@code radius} sphere - creatures take
 * {@code damage} every half second, blocks vanish ({@code erase_blocks}, honours {@code mobGriefing} for non-players).
 */
public record VoidAction(int durationTicks, float speed, float radius, float damage, boolean eraseBlocks) implements IAbilityAction {
    public static final MapCodec<VoidAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.intRange(1, 1200).optionalFieldOf("duration_ticks", 80).forGetter(VoidAction::durationTicks),
            Codec.floatRange(0f, 4f).optionalFieldOf("speed", 0.45f).forGetter(VoidAction::speed),
            Codec.floatRange(0.5f, 4f).optionalFieldOf("radius", 1.6f).forGetter(VoidAction::radius),
            Codec.floatRange(0f, 500f).optionalFieldOf("damage", 12f).forGetter(VoidAction::damage),
            Codec.BOOL.optionalFieldOf("erase_blocks", true).forGetter(VoidAction::eraseBlocks)
    ).apply(i, VoidAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.VOID; }

    @Override
    public void execute(AbilityContext ctx) {
        LivingEntity caster = ctx.caster();
        VoidManager.enter(caster, durationTicks);
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, durationTicks, 0, false, false, false));
        boolean blocks = eraseBlocks && (caster instanceof net.minecraft.server.network.ServerPlayerEntity
                || ctx.world().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING));
        com.animecore.tool.ToolScheduler.run(durationTicks, t -> {
            if (!caster.isAlive() || !VoidManager.inVoid(caster)) return false;
            Vec3d look = caster.getRotationVec(1f);
            caster.setVelocity(look.multiply(speed));
            caster.velocityModified = true;
            caster.fallDistance = 0f;
            Vec3d c = caster.getPos().add(0, caster.getHeight() * 0.5, 0).add(look.multiply(0.6));
            if (blocks) {
                int r = (int) Math.ceil(radius);
                BlockPos center = BlockPos.ofFloored(c);
                for (BlockPos p : BlockPos.iterate(center.add(-r, -r, -r), center.add(r, r, r))) {
                    if (p.getSquaredDistance(c) > radius * radius) continue;
                    BlockState s = ctx.world().getBlockState(p);
                    if (s.isAir() || s.getHardness(ctx.world(), p) < 0f) continue;   // bedrock / barriers survive
                    ctx.world().removeBlock(p, false);
                }
            }
            if (t % 10 == 0) {
                com.animecore.vfx.Juice.shakeAround(ctx.world(), c, 16, 0.3f, 10);
                for (LivingEntity e : ctx.world().getEntitiesByClass(LivingEntity.class, new Box(c, c).expand(radius + 0.5),
                        e -> e != caster && e.isAlive() && !(e instanceof ManifestationEntity))) {
                    ctx.damage(e, damage, 0, 0f);
                }
            }
            ctx.world().spawnParticles(ParticleTypes.SQUID_INK, c.x, c.y, c.z, 14, radius * 0.4, radius * 0.4, radius * 0.4, 0.01);
            ctx.world().spawnParticles(ParticleTypes.REVERSE_PORTAL, c.x, c.y, c.z, 6, radius * 0.5, radius * 0.5, radius * 0.5, 0.05);
            return true;
        });
    }
}
