package com.animecore.item;

import com.animecore.origin.VillageAccessManager;
import com.animecore.platform.Ids;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Village forehead protector (helmet slot, dyeable cloth in the village colour, {@code animecore:faction_badge}).
 * <ul>
 *   <li>Wear it: only members (or allies) may - outsiders have it taken off by {@link VillageAccessManager}.</li>
 *   <li>Sneak + use with a blade ({@code #animecore:scratch_tools}, else any sword) in the other hand: scratch the symbol,
 *       becoming missing-nin.</li>
 * </ul>
 */
public class FactionHeadbandItem extends ArmorItem implements AnimeComponentItem {
    public static final TagKey<net.minecraft.item.Item> SCRATCH_TOOLS = TagKey.of(net.minecraft.registry.RegistryKeys.ITEM, Ids.core("scratch_tools"));

    public FactionHeadbandItem(RegistryEntry<ArmorMaterial> material, Settings settings) {
        super(material, Type.HELMET, settings.maxCount(1));
    }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.FACTION_BADGE);
    }

    @Override
    public Text getName(ItemStack stack) {
        AnimeDataComponents.FactionBadge badge = stack.get(AnimeDataComponents.FACTION_BADGE);
        if (badge == null) return super.getName(stack);
        return Text.translatable(badge.scratched() ? "item.animecore.headband.scratched" : "item.animecore.headband.named",
                super.getName(stack), Text.translatable(Ids.translationKey("faction", badge.faction())));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            ItemStack blade = user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
            if (!blade.isEmpty() && (blade.isIn(SCRATCH_TOOLS) || blade.isIn(ItemTags.SWORDS))) {
                if (user instanceof ServerPlayerEntity sp) {
                    return VillageAccessManager.scratch(sp, stack) ? TypedActionResult.success(stack, false) : TypedActionResult.fail(stack);
                }
                return TypedActionResult.success(stack, true);
            }
        }
        return super.use(world, user, hand); // equip
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        AnimeDataComponents.FactionBadge badge = stack.get(AnimeDataComponents.FACTION_BADGE);
        if (badge == null) return;
        tooltip.add(Text.translatable(badge.scratched() ? "tooltip.animecore.headband.rogue" : "tooltip.animecore.headband.scratch_hint")
                .formatted(badge.scratched() ? Formatting.DARK_RED : Formatting.DARK_GRAY));
    }

    /** Pledge-free check used by content mods. */
    public static boolean isFor(ItemStack stack, net.minecraft.util.Identifier faction) {
        AnimeDataComponents.FactionBadge b = stack.get(AnimeDataComponents.FACTION_BADGE);
        return b != null && b.faction().equals(faction);
    }

}
