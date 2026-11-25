package com.animecore.tool;

import com.animecore.api.AnimeModContext;
import com.animecore.platform.Ids;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Wearable ninja tool (forehead protectors, flak jackets, masks, weights). RIGHT equips like vanilla armour,
 * SNEAK + RIGHT performs the secondary move, worn effects apply while equipped. Uses the dyeable leather layer
 * tinted per item, so no armour textures are shipped.
 */
public class NinjaArmorItem extends ArmorItem implements ToolHolder {
    private static final Map<Integer, RegistryEntry<ArmorMaterial>> MATERIALS = new HashMap<>();
    private final ToolSpec spec;
    private final AnimeModContext context;

    public NinjaArmorItem(ToolSpec spec, AnimeModContext context) {
        super(material(spec.armor().map(ToolSpec.Armor::defense).orElse(2)), type(spec), withColor(spec));
        this.spec = spec;
        this.context = context;
    }

    private static Settings withColor(ToolSpec spec) {
        Settings s = ToolBehaviour.settings(spec, true).maxCount(1);
        spec.armor().ifPresent(a -> s.component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(a.color(), false)));
        return s;
    }

    private static Type type(ToolSpec spec) {
        return switch (spec.armor().map(ToolSpec.Armor::slot).orElse(ToolSpec.Slot.CHEST)) {
            case HEAD -> Type.HELMET;
            case CHEST -> Type.CHESTPLATE;
            case LEGS -> Type.LEGGINGS;
            case FEET -> Type.BOOTS;
        };
    }

    /** One registered armour material per defence value: {@code animecore:ninja_armor_<n>}. Must run during init. */
    private static synchronized RegistryEntry<ArmorMaterial> material(int defense) {
        return MATERIALS.computeIfAbsent(defense, d -> {
            EnumMap<Type, Integer> map = new EnumMap<>(Type.class);
            for (Type t : Type.values()) map.put(t, d);
            Identifier leather = Identifier.ofVanilla("leather");
            return Registry.registerReference(Registries.ARMOR_MATERIAL, Ids.core("ninja_armor_" + d), new ArmorMaterial(map, 12,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(leather, "", true), new ArmorMaterial.Layer(leather, "_overlay", false)),
                    d >= 7 ? 1f : 0f, 0f));
        });
    }

    @Override public ToolSpec spec() { return spec; }
    @Override public AnimeModContext context() { return context; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking() && spec.secondary().isPresent()) {
            MoveSpec m = spec.secondary().get();
            if (m.type() == MoveType.CONSUME && !ToolBehaviour.isHeld(m)) {
                ItemStack stack = user.getStackInHand(hand);
                if (user.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.fail(stack);
                if (user instanceof ServerPlayerEntity sp) ToolBehaviour.instantConsume(this, this, sp, stack, m);
                return TypedActionResult.success(stack, world.isClient());
            }
            return ToolBehaviour.use(this, this, world, user, hand);
        }
        Optional<MoveSpec> primary = spec.primary();
        if (primary.isPresent() && !user.isSneaking()) return ToolBehaviour.use(this, this, world, user, hand);
        return super.use(world, user, hand);
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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        ToolBehaviour.wornTick(this, stack, entity, getSlotType());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ToolBehaviour.tooltip(this, tooltip);
    }
}
