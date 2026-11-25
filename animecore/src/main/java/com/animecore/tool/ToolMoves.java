package com.animecore.tool;

import com.animecore.api.AnimeCoreApi;
import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.api.EnergyPoolSpec;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.entity.AnimeEntities;
import com.animecore.entity.EnergyProjectileEntity;
import com.animecore.entity.PuppetEntity;
import com.animecore.entity.ThrownToolEntity;
import com.animecore.item.AnimeDataComponents;
import com.animecore.platform.AnimeParticles;
import com.animecore.ward.ChakraWallBlock;
import com.animecore.ward.WardSystem;
import com.animecore.ward.WardTagBlock;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Server-side implementation of every {@link MoveType}. */
public final class ToolMoves {
    private ToolMoves() {}

    /** Flying Thunder God marks: player -> thrown marker entity. */
    private static final Map<UUID, UUID> MARKS = new HashMap<>();
    private static final String[] RANDOM_ELEMENTS = {"fire", "wind", "lightning", "earth", "water"};
    private static final int[] ELEMENT_COLORS = {0xFF6A00, 0xB2FF59, 0xCDE7FF, 0x8D6E63, 0x2979FF};

    // =====================================================================================================
    // energy
    // =====================================================================================================

    public static boolean pay(PlayerEntity player, AnimeModContext ctx, float cost) {
        if (cost <= 0 || player.isCreative()) return true;
        Optional<EnergyPoolSpec> pool = ctx.pool(EnergyPoolSpec.Role.PRIMARY);
        if (pool.isEmpty()) return true;
        boolean ok = ctx.power(player).tryConsume(pool.get().id(), cost);
        if (!ok) player.sendMessage(Text.translatable("message.animecore.cast.no_energy").formatted(Formatting.RED), true);
        return ok;
    }

    private static void restorePrimary(PlayerEntity player, float amount) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            ctx.pool(EnergyPoolSpec.Role.PRIMARY).ifPresent(spec -> ctx.power(player).restore(spec.id(), amount));
        }
    }

    // =====================================================================================================
    // dispatch (instant moves; held moves finish in onRelease / finishConsume)
    // =====================================================================================================

    /** @return true if the move fired (cooldown / item consumption apply) */
    public static boolean execute(ServerPlayerEntity player, ItemStack stack, Hand hand, ToolHolder holder, MoveSpec m) {
        ServerWorld world = player.getServerWorld();
        AnimeModContext ctx = holder.context();
        if (m.type() != MoveType.CHARGE_SLASH && m.type() != MoveType.CONSUME && !pay(player, ctx, m.cost())) return false;
        return switch (m.type()) {
            case THROW -> throwTool(world, player, stack, m);
            case TRAP -> trap(world, player, stack, m);
            case WARD -> false; // needs a block: handled by useOnBlock
            case CONSUME, GUARD, CHARGE_SLASH -> false; // held
            case DASH -> dash(world, player, m);
            case CHAIN -> chain(world, player, stack, m);
            case GUST -> gust(world, player, m);
            case BLAST -> blast(world, player, m);
            case SEAL -> seal(world, player, m);
            case SUMMON -> summon(world, player, ctx, m);
            case SAND -> sand(world, player, m);
            case MARK -> mark(world, player, stack, m);
            case RECALL -> recall(world, player);
            case ANCHOR -> anchor(world, player, stack, m);
            case VORTEX -> vortex(world, player, m);
            case GLIDE -> glide(world, player, m);
            case SONIC -> sonic(world, player, m);
            case SCAN -> scan(world, player, m);
            case BUFF -> buff(world, player, m);
            case WALL -> wall(world, player, m);
            case SLAM -> slam(world, player, m);
            case INFUSE -> infuse(player, stack);
        };
    }

    // =====================================================================================================
    // THROW / TRAP / MARK / CHAIN
    // =====================================================================================================

    private static ThrownToolEntity spawnThrown(ServerWorld world, PlayerEntity player, ItemStack stack, MoveSpec m, float yawOffset) {
        ThrownToolEntity proj = new ThrownToolEntity(world, player, stack);
        proj.damage = m.f("damage", 4);
        proj.gravity = m.f("gravity", 0.03f);
        proj.pierce = m.i("pierce", 0);
        proj.returns = m.flag("returns");
        proj.dropOnLand = m.flag("drop") && m.flag("consume");
        proj.fuse = m.i("fuse", 0);
        proj.radius = m.f("radius", 2);
        proj.impact = m.variantOr("");
        proj.effects = m.effects();
        if (m.color() != 0xFFFFFF) proj.setTint(m.color());
        proj.setVelocity(player, player.getPitch(), player.getYaw() + yawOffset, 0f, m.f("speed", 1.6f), 1.0f);
        return proj;
    }

    private static boolean throwTool(ServerWorld world, ServerPlayerEntity player, ItemStack stack, MoveSpec m) {
        int count = Math.max(1, m.i("count", 1));
        float spread = m.f("spread", 0);
        // Explosive kunai: an explosive tag in the other hand rides along on the first projectile.
        ItemStack other = player.getStackInHand(player.getActiveHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        boolean explosive = m.variant().isEmpty() && net.minecraft.registry.Registries.ITEM.getId(other.getItem()).getPath().equals("explosive_tag");
        for (int k = 0; k < count; k++) {
            float yaw = count == 1 ? 0 : spread * (k - (count - 1) / 2f);
            ThrownToolEntity proj = spawnThrown(world, player, stack, m, yaw);
            if (explosive && k == 0) {
                proj.impact = "explode";
                proj.radius = 3;
                proj.dropOnLand = false;
                other.decrementUnlessCreative(1, player);
            }
            world.spawnEntity(proj);
        }
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.6f, 1.4f);
        return true;
    }

    private static boolean trap(ServerWorld world, ServerPlayerEntity player, ItemStack stack, MoveSpec m) {
        int count = m.i("count", 6);
        for (int k = 0; k < count; k++) {
            ThrownToolEntity t = new ThrownToolEntity(world, player, stack);
            t.trap = true;
            t.damage = m.f("damage", 3);
            t.trapLife = m.i("duration", 400);
            t.effects = m.effects();
            double a = world.random.nextDouble() * Math.PI * 2;
            Vec3d dir = player.getRotationVec(1f);
            t.setPosition(player.getX(), player.getEyeY() - 0.4, player.getZ());
            t.setVelocity(dir.x * 0.5 + Math.cos(a) * 0.25, 0.25, dir.z * 0.5 + Math.sin(a) * 0.25);
            world.spawnEntity(t);
        }
        return true;
    }

    private static boolean mark(ServerWorld world, ServerPlayerEntity player, ItemStack stack, MoveSpec m) {
        Optional.ofNullable(MARKS.remove(player.getUuid())).map(world::getEntity).ifPresent(Entity::discard);
        ThrownToolEntity proj = spawnThrown(world, player, stack, m, 0);
        proj.mark = true;
        proj.dropOnLand = false;
        world.spawnEntity(proj);
        MARKS.put(player.getUuid(), proj.getUuid());
        player.sendMessage(Text.translatable("message.animecore.tool.marked").formatted(Formatting.YELLOW), true);
        return true;
    }

    private static boolean recall(ServerWorld world, ServerPlayerEntity player) {
        UUID id = MARKS.get(player.getUuid());
        Entity marker = id == null ? null : world.getEntity(id);
        if (!(marker instanceof ThrownToolEntity t)) {
            player.sendMessage(Text.translatable("message.animecore.tool.no_mark").formatted(Formatting.RED), true);
            return false;
        }
        Vec3d from = player.getPos();
        world.spawnParticles(ParticleTypes.FLASH, from.x, from.y + 1, from.z, 1, 0, 0, 0, 0);
        player.networkHandler.requestTeleport(t.getX(), t.getY() + 0.1, t.getZ(), player.getYaw(), player.getPitch());
        player.fallDistance = 0;
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, t.getX(), t.getY() + 1, t.getZ(), 20, 0.4, 0.8, 0.4, 0.2);
        world.playSound(null, t.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.7f, 1.6f);
        if (!player.isCreative()) player.getInventory().offerOrDrop(t.getStack().copy());
        t.discard();
        MARKS.remove(player.getUuid());
        return true;
    }

    public static @Nullable UUID markOf(PlayerEntity player) {
        return MARKS.get(player.getUuid());
    }

    private static boolean chain(ServerWorld world, ServerPlayerEntity player, ItemStack stack, MoveSpec m) {
        ThrownToolEntity hook = spawnThrown(world, player, stack, m, 0);
        hook.chainMode = m.variantOr("pull");
        hook.chainRange = m.f("range", 16);
        hook.gravity = 0.01;
        hook.dropOnLand = false;
        hook.setVelocity(player, player.getPitch(), player.getYaw(), 0f, 2.4f, 0.5f);
        world.spawnEntity(hook);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_CHAIN_STEP, SoundCategory.PLAYERS, 1f, 1.4f);
        return true;
    }

    // =====================================================================================================
    // WARD (needs a clicked block)
    // =====================================================================================================

    public static boolean ward(ServerWorld world, ServerPlayerEntity player, BlockPos clicked, Direction side, MoveSpec m, String toolId) {
        BlockPos pos = world.getBlockState(clicked).isReplaceable() ? clicked : clicked.offset(side);
        WardTagBlock.Mode mode = toolId.contains("explosive") ? WardTagBlock.Mode.EXPLOSIVE
                : toolId.contains("smoke") ? WardTagBlock.Mode.SMOKE : WardTagBlock.Mode.BARRIER;
        return WardSystem.placeTag(world, player, pos, mode, m.i("duration", 200));
    }

    // =====================================================================================================
    // melee-ish: DASH / CHARGE_SLASH / SLAM / GUST
    // =====================================================================================================

    private static void elementHit(ServerWorld world, PlayerEntity player, LivingEntity target, String element) {
        switch (element) {
            case "fire" -> target.setOnFireForTicks(80);
            case "lightning" -> {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 2));
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getBodyY(0.5), target.getZ(), 10, 0.3, 0.5, 0.3, 0.1);
            }
            case "water" -> {
                target.extinguish();
                Vec3d push = target.getPos().subtract(player.getPos()).normalize();
                target.takeKnockback(0.9, -push.x, -push.z);
            }
            case "wind" -> target.addVelocity(0, 0.4, 0);
            case "earth" -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
            case "ice" -> target.setFrozenTicks(Math.max(target.getFrozenTicks(), 140));
            case "blood" -> player.heal(1f);
            default -> {}
        }
    }

    private static boolean dash(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        float distance = m.f("distance", 6);
        Vec3d dir = player.getRotationVec(1f).multiply(1, 0.3, 1).normalize();
        Vec3d start = player.getPos();
        Vec3d end = com.animecore.movement.MotionUtil.clipDestination(world, player, start, start.add(dir.multiply(distance)));
        String element = m.variantOr("");
        for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().union(player.getBoundingBox().offset(end.subtract(start))).expand(1),
                e -> e != player && e.isAlive())) {
            e.damage(world.getDamageSources().playerAttack(player), m.f("damage", 6));
            elementHit(world, player, e, element);
        }
        for (int k = 0; k < 10; k++) {
            Vec3d p = start.lerp(end, k / 10.0);
            world.spawnParticles(AnimeParticles.dust(m.color(), 1.2f), p.x, p.y + 1, p.z, 2, 0.2, 0.4, 0.2, 0);
        }
        player.networkHandler.requestTeleport(end.x, end.y, end.z, player.getYaw(), player.getPitch());
        player.fallDistance = 0;
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1f, 1.3f);
        return true;
    }

    /** Released after holding: ratio 0..1. */
    public static boolean chargeRelease(ServerWorld world, ServerPlayerEntity player, ToolHolder holder, MoveSpec m, float ratio) {
        if (ratio < 0.2f) return false;
        if (!pay(player, holder.context(), m.cost() * ratio)) return false;
        float damage = m.f("damage", 8) * (0.5f + ratio);
        float range = m.f("range", 5) * (0.6f + 0.4f * ratio);
        String element = m.variantOr("");
        for (LivingEntity e : ToolTargeting.cone(player, range, 0.45)) {
            e.damage(world.getDamageSources().playerAttack(player), damage);
            elementHit(world, player, e, element);
        }
        Vec3d look = player.getRotationVec(1f);
        Vec3d eye = player.getEyePos();
        for (double d = 1; d < range; d += 0.35) {
            Vec3d p = eye.add(look.multiply(d));
            world.spawnParticles(AnimeParticles.dust(m.color(), 1.3f), p.x, p.y - 0.2, p.z, 3, 0.4, 0.05, 0.4, 0);
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1f, 0.7f);
        return true;
    }

    private static boolean slam(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        float radius = m.f("radius", 3);
        BlockHitResult hit = ToolTargeting.blockRay(player, 4.5);
        Vec3d at = hit.getType() == HitResult.Type.MISS ? player.getPos().add(player.getRotationVec(1f).multiply(2)) : hit.getPos();
        if (m.flag("explode")) world.createExplosion(player, at.x, at.y, at.z, Math.max(1.5f, radius * 0.6f), World.ExplosionSourceType.NONE);
        for (LivingEntity e : ToolTargeting.sphere(player, at, radius)) {
            e.damage(world.getDamageSources().playerAttack(player), m.f("damage", 7));
            Vec3d push = e.getPos().subtract(at).normalize();
            e.takeKnockback(m.f("knockback", 1f), -push.x, -push.z);
            e.addVelocity(0, 0.25, 0);
            for (ToolEffect fx : m.effects()) fx.apply(e);
        }
        if (m.flag("drill") && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos base = hit.getBlockPos();
            Direction into = hit.getSide().getOpposite();
            for (int k = 0; k < 3; k++) for (int h = 0; h < 2; h++) {
                BlockPos p = base.offset(into, k).up(h);
                BlockState s = world.getBlockState(p);
                if (!s.isAir() && s.getHardness(world, p) >= 0 && s.getHardness(world, p) < 10 && world.canPlayerModifyAt(player, p)) {
                    world.breakBlock(p, true, player);
                }
            }
        }
        BlockState ground = world.getBlockState(BlockPos.ofFloored(at).down());
        if (!ground.isAir()) world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, ground), at.x, at.y + 0.1, at.z, 40, radius / 2, 0.2, radius / 2, 0.2);
        world.spawnParticles(AnimeParticles.dust(m.color(), 1.5f), at.x, at.y + 0.3, at.z, 20, radius / 2, 0.3, radius / 2, 0);
        world.playSound(null, BlockPos.ofFloored(at), SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS, 0.5f, 1.4f);
        return true;
    }

    private static boolean gust(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        float range = m.f("range", 7);
        Vec3d look = player.getRotationVec(1f);
        String element = m.variantOr("");
        for (LivingEntity e : ToolTargeting.cone(player, range, 0.5)) {
            if (m.f("damage", 0) > 0) e.damage(world.getDamageSources().playerAttack(player), m.f("damage", 2));
            Vec3d v = look.multiply(m.f("power", 1.6f)).add(0, 0.35, 0);
            e.setVelocity(e.getVelocity().add(v));
            e.velocityModified = true;
            elementHit(world, player, e, element);
            for (ToolEffect fx : m.effects()) fx.apply(e);
        }
        Vec3d eye = player.getEyePos();
        for (double d = 1; d < range; d += 0.5) {
            Vec3d p = eye.add(look.multiply(d));
            world.spawnParticles(element.equals("fire") ? ParticleTypes.FLAME : ParticleTypes.CLOUD, p.x, p.y - 0.3, p.z, 3, d * 0.12, d * 0.08, d * 0.12, 0.02);
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BREEZE_SHOOT, SoundCategory.PLAYERS, 1f, 1f);
        return true;
    }

    // =====================================================================================================
    // BLAST
    // =====================================================================================================

    private static boolean blast(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        String[] v = m.variantOr("projectile").split(":", 2);
        String mode = v[0];
        String element = v.length > 1 ? v[1] : "";
        int color = m.color();
        if (element.equals("random")) {
            int k = world.random.nextInt(RANDOM_ELEMENTS.length);
            element = RANDOM_ELEMENTS[k];
            color = ELEMENT_COLORS[k];
        }
        float damage = m.f("damage", 6);
        int fire = m.i("fire", 0);
        switch (mode) {
            case "beam" -> {
                float range = m.f("range", 24);
                Vec3d eye = player.getEyePos();
                Vec3d end = ToolTargeting.lookPoint(player, range);
                Vec3d dir = end.subtract(eye).normalize();
                for (Entity e : world.getOtherEntities(player, new net.minecraft.util.math.Box(eye, end).expand(1),
                        e -> e instanceof LivingEntity && e.isAlive() && e.getBoundingBox().expand(0.5).raycast(eye, end).isPresent())) {
                    LivingEntity living = (LivingEntity) e;
                    living.damage(world.getDamageSources().indirectMagic(player, player), damage);
                    if (fire > 0) living.setOnFireForTicks(fire);
                    elementHit(world, player, living, element);
                    for (ToolEffect fx : m.effects()) fx.apply(living);
                }
                for (double d = 0.5; d < eye.distanceTo(end); d += 0.35) {
                    Vec3d p = eye.add(dir.multiply(d));
                    world.spawnParticles(AnimeParticles.dust(color, 1.1f), p.x, p.y - 0.1, p.z, 1, 0.03, 0.03, 0.03, 0);
                }
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 0.8f, 1.8f);
            }
            case "cone" -> {
                float range = m.f("range", 6);
                for (LivingEntity e : ToolTargeting.cone(player, range, 0.7)) {
                    e.damage(world.getDamageSources().indirectMagic(player, player), damage);
                    if (fire > 0) e.setOnFireForTicks(fire);
                    elementHit(world, player, e, element);
                    for (ToolEffect fx : m.effects()) fx.apply(e);
                }
                Vec3d look = player.getRotationVec(1f), eye = player.getEyePos();
                for (double d = 0.8; d < range; d += 0.4) {
                    Vec3d p = eye.add(look.multiply(d));
                    world.spawnParticles(element.equals("water") ? ParticleTypes.SPLASH : ParticleTypes.FLAME, p.x, p.y - 0.2, p.z, 4, d * 0.1, d * 0.1, d * 0.1, 0.01);
                }
                world.playSound(null, player.getBlockPos(), element.equals("water") ? SoundEvents.ENTITY_GENERIC_SPLASH : SoundEvents.ITEM_FIRECHARGE_USE,
                        SoundCategory.PLAYERS, 1f, 1f);
            }
            case "lightning" -> {
                Vec3d at = ToolTargeting.lookPoint(player, m.f("range", 24));
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
                if (bolt != null) {
                    bolt.refreshPositionAfterTeleport(at);
                    bolt.setCosmetic(true);
                    world.spawnEntity(bolt);
                }
                for (LivingEntity e : ToolTargeting.sphere(player, at, 3)) {
                    e.damage(world.getDamageSources().lightningBolt(), damage);
                    elementHit(world, player, e, "lightning");
                }
            }
            default -> {
                int count = Math.max(1, m.i("count", 1));
                for (int k = 0; k < count; k++) {
                    float yaw = player.getYaw() + (count == 1 ? 0 : 6f * (k - (count - 1) / 2f));
                    Vec3d dir = Vec3d.fromPolar(player.getPitch(), yaw);
                    EnergyProjectileEntity p = new EnergyProjectileEntity(world, player);
                    p.configure(damage, null, m.f("radius", 0), fire, 60, color, m.f("size", 0.5f),
                            element.equals("fire") ? net.minecraft.util.Identifier.ofVanilla("flame") : null);
                    Vec3d spawn = player.getEyePos().add(dir.multiply(0.8)).subtract(0, 0.15, 0);
                    p.refreshPositionAndAngles(spawn.x, spawn.y, spawn.z, yaw, player.getPitch());
                    p.setVelocity(dir.multiply(m.f("speed", 1.6f)));
                    world.spawnEntity(p);
                }
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.7f, 1.4f);
            }
        }
        return true;
    }

    // =====================================================================================================
    // SEAL / SUMMON / SAND / VORTEX
    // =====================================================================================================

    private static boolean seal(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        LivingEntity target = ToolTargeting.lookEntity(player, m.f("range", 5));
        if (target == null) {
            player.sendMessage(Text.translatable("message.animecore.tool.no_target").formatted(Formatting.GRAY), true);
            return false;
        }
        float ratio = target.getHealth() / target.getMaxHealth();
        boolean sealable = !(target instanceof PlayerEntity) && !target.getType().isIn(ConventionalEntityTypeTags.BOSSES)
                && !(target instanceof PuppetEntity p && p.isAlly(player)) && ratio <= m.f("threshold", 0.3f);
        world.spawnParticles(ParticleTypes.ENCHANT, target.getX(), target.getBodyY(0.5), target.getZ(), 40, 0.5, 0.8, 0.5, 0.6);
        if (sealable) {
            world.spawnParticles(ParticleTypes.SOUL, target.getX(), target.getBodyY(0.5), target.getZ(), 20, 0.3, 0.5, 0.3, 0.05);
            world.spawnEntity(new ExperienceOrbEntity(world, target.getX(), target.getY(), target.getZ(), 5 + (int) target.getMaxHealth() / 4));
            target.discard();
            restorePrimary(player, 15);
            world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.PLAYERS, 1f, 0.6f);
            player.sendMessage(Text.translatable("message.animecore.tool.sealed", target.getName()).formatted(Formatting.GOLD), true);
        } else {
            List<ToolEffect> fx = m.effects().isEmpty()
                    ? List.of(new ToolEffect(StatusEffects.WEAKNESS, 120, 1), new ToolEffect(StatusEffects.SLOWNESS, 120, 2))
                    : m.effects();
            fx.forEach(e -> e.apply(target));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 120));
            world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.PLAYERS, 1f, 0.8f);
        }
        return true;
    }

    private static boolean summon(ServerWorld world, ServerPlayerEntity player, AnimeModContext ctx, MoveSpec m) {
        String[] v = m.variantOr("wood:melee").split(":", 2);
        int count = Math.max(1, m.i("count", 1));
        for (int k = 0; k < count; k++) {
            PuppetEntity puppet = AnimeEntities.PUPPET.create(world);
            if (puppet == null) return false;
            double a = (k + 0.5) * Math.PI * 2 / count + player.getYaw() * Math.PI / 180;
            Vec3d at = player.getPos().add(Math.cos(a) * 2, 0, Math.sin(a) * 2);
            puppet.refreshPositionAndAngles(at.x, at.y + (v.length > 1 && v[1].equals("flying") ? 1.5 : 0), at.z, player.getYaw(), 0);
            puppet.configure(player, ctx.modId(), v[0], v.length > 1 ? v[1] : "melee", m.f("health", 20), m.f("damage", 4),
                    m.i("lifetime", 600), m.f("upkeep", 0.05f), m.f("speed", 0.3f));
            world.spawnEntity(puppet);
            world.spawnParticles(ParticleTypes.POOF, at.x, at.y + 1, at.z, 15, 0.3, 0.6, 0.3, 0.03);
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, SoundCategory.PLAYERS, 0.6f, 1.6f);
        return true;
    }

    private static boolean sand(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        float range = m.f("range", 10);
        Vec3d eye = player.getEyePos();
        Vec3d end = ToolTargeting.lookPoint(player, range);
        Vec3d dir = end.subtract(eye).normalize();
        BlockState dust = switch (m.variantOr("sand")) {
            case "iron" -> net.minecraft.block.Blocks.BLACK_CONCRETE_POWDER.getDefaultState();
            case "gold" -> net.minecraft.block.Blocks.GOLD_BLOCK.getDefaultState();
            default -> net.minecraft.block.Blocks.SAND.getDefaultState();
        };
        java.util.Set<LivingEntity> struck = new java.util.HashSet<>();
        for (double d = 1; d <= eye.distanceTo(end) + 0.5; d += 1) {
            Vec3d p = eye.add(dir.multiply(d));
            world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, dust), p.x, p.y - 0.5, p.z, 8, 0.6, 0.6, 0.6, 0);
            struck.addAll(ToolTargeting.sphere(player, p, 1.8));
        }
        for (LivingEntity e : struck) {
            e.damage(world.getDamageSources().playerAttack(player), m.f("damage", 5));
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 3));
            e.setVelocity(0, 0.5, 0);
            e.velocityModified = true;
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SAND_BREAK, SoundCategory.PLAYERS, 1.5f, 0.6f);
        return true;
    }

    private static boolean vortex(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        Vec3d center = ToolTargeting.lookPoint(player, 16);
        float radius = m.f("radius", 6);
        float damage = m.f("damage", 2);
        ToolScheduler.run(m.i("duration", 60), t -> {
            if (!player.isAlive()) return false;
            for (LivingEntity e : ToolTargeting.sphere(player, center, radius)) {
                Vec3d pull = center.subtract(e.getPos()).normalize().multiply(0.25);
                e.setVelocity(e.getVelocity().multiply(0.6).add(pull));
                e.velocityModified = true;
                if (t % 10 == 0) e.damage(world.getDamageSources().indirectMagic(player, player), damage);
            }
            world.spawnParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y + 0.5, center.z, 15, radius / 3, 0.5, radius / 3, 0.2);
            world.spawnParticles(ParticleTypes.SQUID_INK, center.x, center.y + 0.5, center.z, 3, 0.2, 0.2, 0.2, 0.01);
            return true;
        });
        world.playSound(null, BlockPos.ofFloored(center), SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 0.4f, 1.8f);
        return true;
    }

    // =====================================================================================================
    // GLIDE / SONIC / SCAN / BUFF / WALL / ANCHOR / INFUSE
    // =====================================================================================================

    private static boolean glide(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        String variant = m.variantOr("wings");
        int duration = m.i("duration", 100);
        float boost = m.f("boost", 1.2f);
        Vec3d look = player.getRotationVec(1f);
        switch (variant) {
            case "umbrella" -> {}
            case "float" -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 30, 0, false, false));
            case "board" -> player.setVelocity(player.getVelocity().add(look.x * boost, 0.2, look.z * boost));
            default -> player.setVelocity(look.x * boost, Math.max(0.6, look.y * boost + 0.5), look.z * boost);
        }
        player.velocityModified = true;
        player.fallDistance = 0;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, duration, 0, false, false, true));
        for (ToolEffect fx : m.effects()) fx.apply(player);
        if (variant.equals("bomber")) {
            ToolScheduler.run(duration, t -> {
                if (!player.isAlive() || player.isOnGround()) return false;
                if (t % 20 == 10) {
                    ThrownToolEntity bomb = new ThrownToolEntity(world, player, new ItemStack(Items.TNT));
                    bomb.setPosition(player.getX(), player.getY() - 0.5, player.getZ());
                    bomb.impact = "explode";
                    bomb.radius = 3;
                    bomb.setVelocity(0, -0.4, 0);
                    world.spawnEntity(bomb);
                }
                return true;
            });
        }
        world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 15, 0.4, 0.1, 0.4, 0.05);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 1f, 1f);
        return true;
    }

    private static boolean sonic(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        float range = m.f("range", 8);
        boolean allies = m.variantOr("enemies").equals("allies");
        if (allies) {
            for (PlayerEntity p : world.getEntitiesByClass(PlayerEntity.class, player.getBoundingBox().expand(range), PlayerEntity::isAlive)) {
                m.effects().forEach(fx -> fx.apply(p));
            }
        } else {
            for (LivingEntity e : ToolTargeting.cone(player, range, 0.2)) {
                if (m.f("damage", 0) > 0) e.damage(world.getDamageSources().sonicBoom(player), m.f("damage", 0));
                m.effects().forEach(fx -> fx.apply(e));
            }
        }
        for (int k = 0; k < 16; k++) {
            double a = k * Math.PI / 8;
            world.spawnParticles(ParticleTypes.NOTE, player.getX() + Math.cos(a) * 2, player.getEyeY(), player.getZ() + Math.sin(a) * 2, 1, 0, 0, 0, k / 16.0);
        }
        if (m.f("damage", 0) > 0) {
            Vec3d p = player.getEyePos().add(player.getRotationVec(1f).multiply(2));
            world.spawnParticles(ParticleTypes.SONIC_BOOM, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), SoundCategory.PLAYERS, 1.2f, allies ? 1.6f : 0.6f);
        return true;
    }

    private static boolean scan(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        float radius = m.f("radius", 24);
        int duration = m.i("duration", 200);
        int found = 0;
        for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius), e -> e != player && e.isAlive())) {
            e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, duration, 0, false, false));
            found++;
        }
        m.effects().forEach(fx -> fx.apply(player));
        LivingEntity target = ToolTargeting.lookEntity(player, Math.min(64, radius * 2));
        if (target != null) {
            MutableText info = Text.empty().append(target.getName().copy().formatted(Formatting.GOLD))
                    .append(Text.literal(String.format("  %.0f/%.0f❤", target.getHealth(), target.getMaxHealth())).formatted(Formatting.RED));
            if (target instanceof PlayerEntity tp) {
                for (AnimeModContext c : AnimeModRegistry.all()) {
                    PlayerPowerComponent pw = c.power(tp);
                    info.append(Text.literal("  [" + c.modId() + " T" + pw.tier() + "]").formatted(Formatting.AQUA));
                    pw.lineage().ifPresent(l -> info.append(Text.literal(" " + l.getPath()).formatted(Formatting.GRAY)));
                }
                if (!AnimeCoreApi.affinitiesOf(tp).isEmpty()) info.append(Text.literal(" " + AnimeCoreApi.affinitiesOf(tp)).formatted(Formatting.DARK_GRAY));
            }
            player.sendMessage(info, false);
        }
        player.sendMessage(Text.translatable("message.animecore.tool.scan", found).formatted(Formatting.GREEN), true);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1f, 1.4f);
        return true;
    }

    private static boolean buff(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        m.effects().forEach(fx -> fx.apply(player));
        world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1, player.getZ(), 12, 0.4, 0.6, 0.4, 0.02);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 0.7f, 1.2f);
        return true;
    }

    private static boolean wall(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        Direction facing = player.getHorizontalFacing();
        ChakraWallBlock.Kind kind = switch (m.variantOr("crystal")) {
            case "stone" -> ChakraWallBlock.Kind.STONE;
            case "mirror" -> ChakraWallBlock.Kind.MIRROR;
            case "barrier" -> ChakraWallBlock.Kind.BARRIER;
            default -> ChakraWallBlock.Kind.CRYSTAL;
        };
        return WardSystem.raiseWall(world, player.getBlockPos().offset(facing, 2), facing.rotateYClockwise(), m.i("width", 5), m.i("height", 3),
                kind, m.i("duration", 200)) > 0;
    }

    private static boolean anchor(ServerWorld world, ServerPlayerEntity player, ItemStack stack, MoveSpec m) {
        if (m.variantOr("set").equals("set")) {
            stack.set(AnimeDataComponents.TOOL_ANCHOR, GlobalPos.create(world.getRegistryKey(), player.getBlockPos()));
            player.sendMessage(Text.translatable("message.animecore.tool.anchor_set").formatted(Formatting.LIGHT_PURPLE), true);
            world.spawnParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 30, 0.4, 0.8, 0.4, 0.3);
            return true;
        }
        GlobalPos anchor = stack.get(AnimeDataComponents.TOOL_ANCHOR);
        if (anchor == null || !anchor.dimension().equals(world.getRegistryKey())) {
            player.sendMessage(Text.translatable("message.animecore.tool.no_anchor").formatted(Formatting.RED), true);
            return false;
        }
        BlockPos p = anchor.pos();
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1, player.getZ(), 40, 0.4, 0.8, 0.4, 0.3);
        player.networkHandler.requestTeleport(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, player.getYaw(), player.getPitch());
        world.playSound(null, p, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 0.8f);
        return true;
    }

    private static boolean infuse(ServerPlayerEntity player, ItemStack stack) {
        AnimeDataComponents.WeaponInfusion current = stack.getOrDefault(AnimeDataComponents.WEAPON_INFUSION, AnimeDataComponents.WeaponInfusion.NONE);
        AnimeDataComponents.WeaponInfusion next = current.withActive(!current.active());
        if (next.active() && next.affinity().isEmpty()) {
            Optional<net.minecraft.util.Identifier> own = AnimeCoreApi.affinitiesOf(player).stream().findFirst();
            if (own.isPresent()) next = next.withAffinity(own.get());
        }
        stack.set(AnimeDataComponents.WEAPON_INFUSION, next);
        player.sendMessage(Text.translatable(next.active() ? "message.animecore.weapon.infused" : "message.animecore.weapon.dormant",
                next.affinity().map(a -> (Text) Text.translatable(com.animecore.platform.Ids.translationKey("affinity", a)))
                        .orElse(Text.translatable("tooltip.animecore.pure_energy"))).formatted(Formatting.AQUA), true);
        return true;
    }

    // =====================================================================================================
    // CONSUME (finish) / GUARD (damage hook)
    // =====================================================================================================

    public static void finishConsume(ServerWorld world, ServerPlayerEntity player, MoveSpec m) {
        List<PlayerEntity> targets = m.f("aoe", 0) > 0
                ? world.getEntitiesByClass(PlayerEntity.class, player.getBoundingBox().expand(m.f("aoe", 0)), PlayerEntity::isAlive)
                : List.of(player);
        for (PlayerEntity p : targets) {
            if (m.flag("cleanse")) {
                p.getStatusEffects().stream().filter(e -> !e.getEffectType().value().isBeneficial()).map(StatusEffectInstance::getEffectType)
                        .toList().forEach(p::removeStatusEffect);
                p.extinguish();
            }
            if (m.f("heal", 0) > 0) p.heal(m.f("heal", 0));
            if (m.f("food", 0) > 0) p.getHungerManager().add(m.i("food", 0), 0.6f);
            if (m.f("chakra", 0) > 0) restorePrimary(p, m.f("chakra", 0));
            m.effects().forEach(fx -> fx.apply(p));
        }
        world.playSound(null, player.getBlockPos(), m.variantOr("eat").equals("drink") ? SoundEvents.ENTITY_GENERIC_DRINK : SoundEvents.ENTITY_PLAYER_BURP,
                SoundCategory.PLAYERS, 0.7f, 1f);
    }

    /** @return true if the hit was fully blocked by a held GUARD move */
    public static boolean tryGuard(ServerPlayerEntity player, net.minecraft.entity.damage.DamageSource source, float amount) {
        if (!player.isUsingItem() || !(player.getActiveItem().getItem() instanceof ToolHolder holder)) return false;
        MoveSpec m = holder.activeHeld(player).orElse(null);
        if (m == null || m.type() != MoveType.GUARD) return false;
        if (source.isIn(DamageTypeTags.BYPASSES_SHIELD) || source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        Vec3d from = source.getPosition();
        boolean absorb = m.flag("absorb");
        boolean energy = source.isIn(DamageTypeTags.IS_PROJECTILE) || source.isIn(DamageTypeTags.WITCH_RESISTANT_TO) || source.getSource() instanceof EnergyProjectileEntity;
        if (from != null && !(absorb && energy)) {
            Vec3d to = from.subtract(player.getEyePos()).normalize();
            if (to.dotProduct(player.getRotationVec(1f)) < 0.2) return false;
        }
        if (!pay(player, holder.context(), m.cost())) return false;
        ServerWorld world = player.getServerWorld();
        if (m.flag("reflect") && source.getSource() instanceof net.minecraft.entity.projectile.ProjectileEntity proj) {
            proj.setVelocity(proj.getVelocity().multiply(-0.9));
            proj.setOwner(player);
        }
        if (absorb && energy) restorePrimary(player, amount * 2);
        if (m.f("knockback", 0) > 0 && source.getAttacker() instanceof LivingEntity attacker && attacker.squaredDistanceTo(player) < 16) {
            Vec3d push = attacker.getPos().subtract(player.getPos()).normalize();
            attacker.takeKnockback(m.f("knockback", 0), -push.x, -push.z);
        }
        world.playSound(null, player.getBlockPos(), absorb ? SoundEvents.BLOCK_BEACON_POWER_SELECT : SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1f, 1.2f);
        world.spawnParticles(absorb ? ParticleTypes.ENCHANT : ParticleTypes.CRIT, player.getX(), player.getEyeY(), player.getZ(), 10, 0.4, 0.4, 0.4, 0.2);
        return true;
    }
}
