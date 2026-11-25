package com.animecore.tool;

import com.animecore.item.AnimeDataComponents;
import com.animecore.platform.Ids;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Item logic shared by {@link NinjaToolItem} and {@link NinjaArmorItem}. */
public final class ToolBehaviour {
    private ToolBehaviour() {}

    /** player -> was the secondary (sneak) move chosen for the current held use. */
    private static final Map<UUID, Boolean> ACTIVE = new ConcurrentHashMap<>();
    private static final int HELD_USE_TIME = 72000;

    // ---------------------------------------------------------------- settings

    public static Item.Settings settings(ToolSpec spec, boolean armor) {
        Item.Settings s = new Item.Settings().rarity(rarity(spec.rarity()));
        if (spec.durability() > 0) s.maxDamage(spec.durability());
        else s.maxCount(spec.maxStack());
        AttackProfile atk = spec.attack();
        if (!armor && (atk.damage() != 0 || atk.speed() != 0 || atk.reach() != 0)) {
            AttributeModifiersComponent.Builder b = AttributeModifiersComponent.builder();
            if (atk.damage() != 0) b.add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, atk.damage(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
            if (atk.speed() != 0) b.add(EntityAttributes.GENERIC_ATTACK_SPEED,
                    new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, atk.speed(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
            if (atk.reach() != 0) b.add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
                    new EntityAttributeModifier(Ids.core("tool_reach"), atk.reach(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND);
            s.attributeModifiers(b.build());
        }
        return s;
    }

    private static Rarity rarity(String name) {
        return switch (name) {
            case "uncommon" -> Rarity.UNCOMMON;
            case "rare" -> Rarity.RARE;
            case "epic" -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
    }

    // ---------------------------------------------------------------- move selection

    public static boolean isHeld(MoveSpec m) {
        return m.type().held() && !(m.type() == MoveType.CONSUME && m.f("time", 32) <= 1);
    }

    public static Optional<MoveSpec> choose(ToolSpec spec, boolean sneaking) {
        if (sneaking && spec.secondary().isPresent()) return spec.secondary();
        return spec.primary().isPresent() ? spec.primary() : spec.secondary();
    }

    private static boolean chooseSecondary(ToolSpec spec, boolean sneaking) {
        return (sneaking && spec.secondary().isPresent()) || spec.primary().isEmpty();
    }

    public static Optional<MoveSpec> activeHeld(ToolHolder holder, PlayerEntity player) {
        Boolean secondary = ACTIVE.get(player.getUuid());
        if (secondary == null) return firstHeld(holder.spec());
        return secondary ? holder.spec().secondary() : holder.spec().primary();
    }

    private static Optional<MoveSpec> firstHeld(ToolSpec spec) {
        if (spec.primary().filter(ToolBehaviour::isHeld).isPresent()) return spec.primary();
        return spec.secondary().filter(ToolBehaviour::isHeld);
    }

    // ---------------------------------------------------------------- use

    public static TypedActionResult<ItemStack> use(ToolHolder holder, Item item, World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        Optional<MoveSpec> chosen = choose(holder.spec(), user.isSneaking());
        if (chosen.isEmpty()) return TypedActionResult.pass(stack);
        MoveSpec m = chosen.get();
        if (m.type() == MoveType.WARD) return TypedActionResult.pass(stack);
        if (user.getItemCooldownManager().isCoolingDown(item)) return TypedActionResult.fail(stack);
        if (isHeld(m)) {
            ACTIVE.put(user.getUuid(), chooseSecondary(holder.spec(), user.isSneaking()));
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        if (!ToolMoves.execute(player, stack, hand, holder, m)) return TypedActionResult.fail(stack);
        afterMove(player, item, stack, m);
        return TypedActionResult.success(stack, false);
    }

    private static void afterMove(PlayerEntity player, Item item, ItemStack stack, MoveSpec m) {
        if (m.cooldown() > 0) player.getItemCooldownManager().set(item, m.cooldown());
        boolean consumes = switch (m.type()) {
            case THROW -> m.f("consume", 1) != 0 && m.variant().map(v -> !v.equals("arrow")).orElse(true);
            case TRAP, MARK -> true;
            default -> false;
        };
        if (consumes) stack.decrementUnlessCreative(1, player);
        if (stack.isDamageable() && !consumes && !player.isCreative() && m.type() != MoveType.SCAN) {
            stack.damage(1, player, player.getActiveHand() == Hand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
        }
    }

    public static ActionResult useOnBlock(ToolHolder holder, Item item, ItemUsageContext ctx) {
        PlayerEntity user = ctx.getPlayer();
        if (user == null) return ActionResult.PASS;
        Optional<MoveSpec> chosen = choose(holder.spec(), user.isSneaking());
        if (chosen.isEmpty() || chosen.get().type() != MoveType.WARD) return ActionResult.PASS;
        if (user.getItemCooldownManager().isCoolingDown(item)) return ActionResult.FAIL;
        if (!(ctx.getWorld() instanceof ServerWorld world) || !(user instanceof ServerPlayerEntity player)) return ActionResult.SUCCESS;
        if (!ToolMoves.pay(player, holder.context(), chosen.get().cost())) return ActionResult.FAIL;
        if (!ToolMoves.ward(world, player, ctx.getBlockPos(), ctx.getSide(), chosen.get(), holder.spec().id())) return ActionResult.FAIL;
        player.getItemCooldownManager().set(item, chosen.get().cooldown());
        ctx.getStack().decrementUnlessCreative(1, player);
        return ActionResult.CONSUME;
    }

    // ---------------------------------------------------------------- held moves

    public static UseAction useAction(ToolHolder holder) {
        return firstHeld(holder.spec()).map(m -> switch (m.type()) {
            case CONSUME -> m.variantOr("eat").equals("drink") ? UseAction.DRINK : UseAction.EAT;
            case GUARD -> UseAction.BLOCK;
            case CHARGE_SLASH -> UseAction.SPEAR;
            default -> UseAction.NONE;
        }).orElse(UseAction.NONE);
    }

    public static int maxUseTime(ToolHolder holder, LivingEntity user) {
        Optional<MoveSpec> m = user instanceof PlayerEntity p ? activeHeld(holder, p) : firstHeld(holder.spec());
        return m.filter(x -> x.type() == MoveType.CONSUME).map(x -> Math.max(4, x.i("time", 32))).orElse(HELD_USE_TIME);
    }

    public static ItemStack finishUsing(ToolHolder holder, Item item, ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof ServerPlayerEntity player)) return stack;
        Optional<MoveSpec> m = activeHeld(holder, player);
        ACTIVE.remove(player.getUuid());
        if (m.isEmpty() || m.get().type() != MoveType.CONSUME) return stack;
        ToolMoves.finishConsume(player.getServerWorld(), player, m.get());
        if (m.get().cooldown() > 0) player.getItemCooldownManager().set(item, m.get().cooldown());
        if (m.get().f("consume", 1) != 0) stack.decrementUnlessCreative(1, player);
        return stack;
    }

    public static void onStoppedUsing(ToolHolder holder, Item item, ItemStack stack, World world, LivingEntity user, int remaining) {
        if (!(user instanceof ServerPlayerEntity player)) return;
        Optional<MoveSpec> m = activeHeld(holder, player);
        ACTIVE.remove(player.getUuid());
        if (m.isEmpty() || m.get().type() != MoveType.CHARGE_SLASH) return;
        int used = HELD_USE_TIME - remaining;
        float ratio = Math.min(1f, used / (float) Math.max(1, m.get().i("max_ticks", 30)));
        if (ToolMoves.chargeRelease(player.getServerWorld(), player, holder, m.get(), ratio)) {
            player.getItemCooldownManager().set(item, m.get().cooldown());
        }
    }

    /** Instant consumes (time <= 1) are executed directly. */
    public static boolean instantConsume(ToolHolder holder, Item item, ServerPlayerEntity player, ItemStack stack, MoveSpec m) {
        ToolMoves.finishConsume(player.getServerWorld(), player, m);
        if (m.cooldown() > 0) player.getItemCooldownManager().set(item, m.cooldown());
        if (m.f("consume", 1) != 0) stack.decrementUnlessCreative(1, player);
        return true;
    }

    // ---------------------------------------------------------------- combat

    public static void postHit(ToolHolder holder, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for (OnHitEffect h : holder.spec().attack().onHit()) {
            int d = h.duration();
            int amp = Math.max(0, Math.round(h.strength()) - 1);
            switch (h.kind()) {
                case POISON -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, d, amp));
                case BLEED -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, d, amp));
                case PARALYZE -> {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, d, 2 + amp));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, d, 1 + amp));
                }
                case FIRE -> target.setOnFireForTicks(d);
                case LIGHTNING -> {
                    target.damage(target.getDamageSources().lightningBolt(), h.strength());
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40));
                }
                case FREEZE -> target.setFrozenTicks(Math.max(target.getFrozenTicks(), d * 2));
                case DRAIN -> {
                    if (attacker instanceof PlayerEntity p) {
                        for (var ctx : com.animecore.api.AnimeModRegistry.all()) {
                            ctx.pool(com.animecore.api.EnergyPoolSpec.Role.PRIMARY).ifPresent(spec -> ctx.power(p).restore(spec.id(), h.strength()));
                        }
                    }
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, d, 0));
                }
                case KNOCKBACK -> {
                    var push = target.getPos().subtract(attacker.getPos()).normalize();
                    target.takeKnockback(h.strength(), -push.x, -push.z);
                }
                case PIERCE -> target.damage(target.getDamageSources().magic(), h.strength());
                case BLIND -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, d, 0));
                case WEAKEN -> target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, d, amp));
                case SHIELD_BREAK -> {
                    if (target instanceof PlayerEntity p && p.isBlocking()) p.disableShield();
                }
            }
        }
        AnimeDataComponents.WeaponInfusion inf = stack.get(AnimeDataComponents.WEAPON_INFUSION);
        if (inf != null && inf.active() && attacker instanceof PlayerEntity p && !ToolMoves.pay(p, holder.context(), 1f)) {
            stack.set(AnimeDataComponents.WEAPON_INFUSION, inf.withActive(false));
        }
        if (stack.isDamageable()) stack.damage(1, attacker, EquipmentSlot.MAINHAND);
    }

    public static float infusionBonus(ItemStack stack) {
        AnimeDataComponents.WeaponInfusion inf = stack.get(AnimeDataComponents.WEAPON_INFUSION);
        return inf != null && inf.active() ? 2f : 0f;
    }

    public static void wornTick(ToolHolder holder, ItemStack stack, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof LivingEntity living) || entity.getWorld().isClient || entity.age % 40 != 0) return;
        if (living.getEquippedStack(slot) != stack) return;
        for (ToolEffect fx : holder.spec().worn()) {
            living.addStatusEffect(new StatusEffectInstance(fx.effect(), 70, fx.amplifier(), true, false, true));
        }
    }

    // ---------------------------------------------------------------- tooltip

    public static void tooltip(ToolHolder holder, List<Text> tooltip) {
        ToolSpec spec = holder.spec();
        String key = "item." + holder.context().modId() + "." + spec.id() + ".desc";
        tooltip.add(Text.translatableWithFallback(key, spec.description()).formatted(Formatting.GRAY));
        spec.primary().ifPresent(m -> tooltip.add(Text.translatable("tooltip.animecore.tool.primary",
                Text.translatable("move.animecore." + m.type().asString())).formatted(Formatting.AQUA)));
        spec.secondary().ifPresent(m -> tooltip.add(Text.translatable("tooltip.animecore.tool.secondary",
                Text.translatable("move.animecore." + m.type().asString())).formatted(Formatting.DARK_AQUA)));
        if (!spec.classification().isEmpty()) {
            tooltip.add(Text.literal(String.join(" · ", spec.classification())).formatted(Formatting.DARK_GRAY));
        }
    }
}
