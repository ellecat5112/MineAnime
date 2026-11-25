package com.animecore.tool;

import com.animecore.api.AnimeModContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * A data-driven ninja tool. LEFT = melee profile with on-hit effects, RIGHT = primary move,
 * SNEAK + RIGHT = secondary move. Hundreds of tools share this one class.
 */
public class NinjaToolItem extends Item implements ToolHolder {
    private final ToolSpec spec;
    private final AnimeModContext context;

    public NinjaToolItem(ToolSpec spec, AnimeModContext context) {
        super(ToolBehaviour.settings(spec, false));
        this.spec = spec;
        this.context = context;
    }

    @Override public ToolSpec spec() { return spec; }
    @Override public AnimeModContext context() { return context; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Optional<MoveSpec> m = ToolBehaviour.choose(spec, user.isSneaking());
        if (m.isPresent() && m.get().type() == MoveType.CONSUME && !ToolBehaviour.isHeld(m.get())) {
            ItemStack stack = user.getStackInHand(hand);
            if (user.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.fail(stack);
            if (user instanceof ServerPlayerEntity sp) ToolBehaviour.instantConsume(this, this, sp, stack, m.get());
            return TypedActionResult.success(stack, world.isClient());
        }
        return ToolBehaviour.use(this, this, world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult r = ToolBehaviour.useOnBlock(this, this, context);
        return r == ActionResult.PASS ? super.useOnBlock(context) : r;
    }

    @Override public UseAction getUseAction(ItemStack stack) { return ToolBehaviour.useAction(this); }
    @Override public int getMaxUseTime(ItemStack stack, LivingEntity user) { return ToolBehaviour.maxUseTime(this, user); }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        return ToolBehaviour.finishUsing(this, this, stack, world, user);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        ToolBehaviour.onStoppedUsing(this, this, stack, world, user, remainingUseTicks);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ToolBehaviour.postHit(this, stack, target, attacker);
        return true;
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        ItemStack weapon = damageSource.getWeaponStack();
        return weapon != null && weapon.getItem() == this ? ToolBehaviour.infusionBonus(weapon) : 0f;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return ToolBehaviour.infusionBonus(stack) > 0 || super.hasGlint(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ToolBehaviour.tooltip(this, tooltip);
    }
}
