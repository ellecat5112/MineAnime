package com.animecore.item;

import com.animecore.ability.AbilityDamage;
import com.animecore.affinity.AffinityMatrix;
import com.animecore.api.AnimeCoreApi;
import com.animecore.api.AnimeModRegistry;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.AnimeParticles;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AffinityDefinition;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Energy-infusable melee weapon (Kunai / Chakra Blades, Zanpakuto, Power Pole...). State lives in the
 * {@link AnimeDataComponents#WEAPON_INFUSION} data component.
 * <ul>
 *   <li><b>Sneak + use</b>: toggle infusion. A blank weapon adopts the wielder's first affinity of this system.</li>
 *   <li><b>Infused hits</b>: bonus elemental damage (affinity clash applies) for {@code hitCost} energy.</li>
 *   <li><b>Deflection</b>: infused weapons in the main hand bat away incoming projectiles from the front.</li>
 *   <li><b>Hold use</b> while infused: charge a forward energy slash, released on key-up.</li>
 * </ul>
 */
public class AnimeWeaponItem extends SwordItem implements AnimeComponentItem {
    /**
     * @param system owning power system (mod id)
     * @param pool   energy pool paying for infusion, hits, deflection and charge
     */
    public record WeaponSpec(String system, Identifier pool, float bonusDamage, float hitCost, float toggleCost,
                             float chargeDamage, float chargeCostPerTick, int maxChargeTicks, float chargeRange, boolean deflects) {}

    private static final int MIN_CHARGE_TICKS = 6;
    private final WeaponSpec spec;

    public AnimeWeaponItem(ToolMaterial material, int attackDamage, float attackSpeed, WeaponSpec spec, Settings settings) {
        super(material, settings
                .attributeModifiers(SwordItem.createAttributeModifiers(material, attackDamage, attackSpeed))
                .component(AnimeDataComponents.WEAPON_INFUSION, AnimeDataComponents.WeaponInfusion.NONE));
        this.spec = spec;
    }

    public WeaponSpec spec() { return spec; }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.WEAPON_INFUSION);
    }

    public static AnimeDataComponents.WeaponInfusion infusion(ItemStack stack) {
        return stack.getOrDefault(AnimeDataComponents.WEAPON_INFUSION, AnimeDataComponents.WeaponInfusion.NONE);
    }

    private Optional<PlayerPowerComponent> power(Entity entity) {
        return entity instanceof PlayerEntity player
                ? AnimeModRegistry.get(spec.system()).map(ctx -> ctx.power(player)) : Optional.empty();
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return infusion(stack).active() || super.hasGlint(stack);
    }

    // ------------------------------------------------------------------ toggle / charge

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (user instanceof ServerPlayerEntity player) toggleInfusion(player, stack);
            return TypedActionResult.success(stack, world.isClient());
        }
        if (infusion(stack).active()) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.pass(stack);
    }

    /** @return true if the infusion is now active */
    public boolean toggleInfusion(ServerPlayerEntity player, ItemStack stack) {
        AnimeDataComponents.WeaponInfusion current = infusion(stack);
        if (current.active()) {
            stack.set(AnimeDataComponents.WEAPON_INFUSION, current.withActive(false));
            player.sendMessage(Text.translatable("message.animecore.weapon.dormant").formatted(Formatting.GRAY), true);
            return false;
        }
        PlayerPowerComponent power = power(player).orElse(null);
        if (power == null || !power.tryConsume(spec.pool(), spec.toggleCost())) {
            player.sendMessage(Text.translatable("message.animecore.cast.no_energy").formatted(Formatting.RED), true);
            return false;
        }
        AnimeDataComponents.WeaponInfusion next = current;
        if (current.affinity().isEmpty()) {
            Optional<Identifier> own = power.effectiveAffinities().stream().findFirst();
            if (own.isPresent()) next = next.withAffinity(own.get());
        }
        stack.set(AnimeDataComponents.WEAPON_INFUSION, next.withActive(true));
        player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 0.8f, 1.5f);
        Text element = next.affinity().<Text>map(a -> Text.translatable(Ids.translationKey("affinity", a)))
                .orElse(Text.translatable("tooltip.animecore.pure_energy"));
        player.sendMessage(Text.translatable("message.animecore.weapon.infused", element).formatted(Formatting.AQUA), true);
        return true;
    }

    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.SPEAR; }

    @Override public int getMaxUseTime(ItemStack stack, LivingEntity user) { return 72000; }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof ServerPlayerEntity player)) return;
        int ticks = Math.min(spec.maxChargeTicks(), getMaxUseTime(stack, user) - remainingUseTicks);
        if (ticks < MIN_CHARGE_TICKS) return;
        PlayerPowerComponent power = power(player).orElse(null);
        if (power == null || !power.tryConsume(spec.pool(), spec.chargeCostPerTick() * ticks)) {
            player.sendMessage(Text.translatable("message.animecore.cast.no_energy").formatted(Formatting.RED), true);
            return;
        }
        chargeSlash(serverWorld, player, power, stack, ticks / (float) spec.maxChargeTicks());
    }

    /** Forward energy slash; damage and reach scale with charge. */
    public void chargeSlash(ServerWorld world, ServerPlayerEntity player, PlayerPowerComponent power, ItemStack stack, float ratio) {
        Identifier affinity = infusion(stack).affinity().orElse(null);
        float damage = spec.chargeDamage() * (0.5f + ratio) * power.traits().abilityPower(affinity);
        float range = spec.chargeRange() * (0.6f + 0.4f * ratio);
        Vec3d look = player.getRotationVec(1f);
        Vec3d start = player.getEyePos();
        Box box = new Box(start, start.add(look.multiply(range))).expand(1.2);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive()
                && e.getPos().subtract(player.getPos()).normalize().dotProduct(look) > 0.3)) {
            AbilityDamage.apply(world, player, player, target, damage, affinity, 0, 0.8f, player.getPos());
        }
        int color = color(world.getRegistryManager(), affinity);
        for (double d = 1; d < range; d += 0.35) {
            Vec3d p = start.add(look.multiply(d));
            world.spawnParticles(AnimeParticles.dust(color, 1.2f), p.x, p.y - 0.2, p.z, 3, 0.35, 0.05, 0.35, 0);
        }
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1f, 0.8f);
        player.getItemCooldownManager().set(this, 20);
    }

    // ------------------------------------------------------------------ infused hits

    /** 1.21 hook used by the Mace: adds damage to the vanilla swing (no second damage call, no i-frame issue). */
    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        ItemStack stack = damageSource.getWeaponStack();
        if (stack == null || !infusion(stack).active() || !(damageSource.getAttacker() instanceof PlayerEntity player)) return 0f;
        PlayerPowerComponent power = power(player).orElse(null);
        if (power == null || power.pool(spec.pool()).map(p -> p.current() < spec.hitCost()).orElse(true)) return 0f;
        Identifier affinity = infusion(stack).affinity().orElse(null);
        float clash = affinity == null ? 1f
                : AffinityMatrix.clash(AnimeRegistries.affinities(target.getWorld().getRegistryManager()), affinity, AnimeCoreApi.affinitiesOf(target));
        return spec.bonusDamage() * power.traits().abilityPower(affinity) * clash;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld() instanceof ServerWorld world && infusion(stack).active()) {
            PlayerPowerComponent power = power(attacker).orElse(null);
            if (power == null || !power.tryConsume(spec.pool(), spec.hitCost())) {
                stack.set(AnimeDataComponents.WEAPON_INFUSION, infusion(stack).withActive(false));
                if (attacker instanceof PlayerEntity p) p.sendMessage(Text.translatable("message.animecore.weapon.dormant").formatted(Formatting.GRAY), true);
            } else {
                int color = color(world.getRegistryManager(), infusion(stack).affinity().orElse(null));
                world.spawnParticles(AnimeParticles.dust(color, 1.4f), target.getX(), target.getBodyY(0.5), target.getZ(), 10, 0.3, 0.4, 0.3, 0.02);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    // ------------------------------------------------------------------ deflection (wired from AnimeCore ALLOW_DAMAGE)

    /** @return true if the projectile was deflected and the damage must be cancelled */
    public static boolean tryDeflect(ServerPlayerEntity player, DamageSource source) {
        if (!source.isIn(DamageTypeTags.IS_PROJECTILE) || !(source.getSource() instanceof ProjectileEntity projectile)) return false;
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof AnimeWeaponItem weapon) || !weapon.spec.deflects() || !infusion(stack).active()) return false;
        Vec3d toProjectile = projectile.getPos().subtract(player.getEyePos()).normalize();
        if (toProjectile.dotProduct(player.getRotationVec(1f)) < 0.35) return false; // only from the front
        PlayerPowerComponent power = weapon.power(player).orElse(null);
        if (power == null || !power.tryConsume(weapon.spec.pool(), weapon.spec.hitCost() * 2)) return false;
        projectile.setVelocity(projectile.getVelocity().multiply(-0.8));
        projectile.setOwner(player);
        player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1f, 1.6f);
        player.getServerWorld().spawnParticles(net.minecraft.particle.ParticleTypes.CRIT,
                projectile.getX(), projectile.getY(), projectile.getZ(), 8, 0.1, 0.1, 0.1, 0.2);
        return true;
    }

    // ------------------------------------------------------------------ tooltip

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        AnimeDataComponents.WeaponInfusion inf = infusion(stack);
        Text element = inf.affinity().<Text>map(a -> Text.translatable(Ids.translationKey("affinity", a)))
                .orElse(Text.translatable("tooltip.animecore.pure_energy"));
        tooltip.add(Text.translatable(inf.active() ? "tooltip.animecore.weapon.active" : "tooltip.animecore.weapon.dormant", element)
                .formatted(inf.active() ? Formatting.AQUA : Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.animecore.weapon.bonus", (int) spec.bonusDamage(), (int) spec.hitCost()).formatted(Formatting.DARK_AQUA));
        tooltip.add(Text.translatable("tooltip.animecore.weapon.controls").formatted(Formatting.DARK_GRAY));
    }

    private static int color(DynamicRegistryManager rm, @Nullable Identifier affinity) {
        if (affinity == null) return 0xB3E5FC;
        return AnimeRegistries.affinities(rm).getOrEmpty(affinity).map(AffinityDefinition::color).orElse(0xB3E5FC);
    }
}
