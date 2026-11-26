package com.animecore.item;

import com.animecore.platform.Ids;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.List;

/** Village uniform (flak jacket / outfit): dyed in the village colour, bound with {@code animecore:faction_badge}. */
public class FactionGarbItem extends ArmorItem implements AnimeComponentItem {
    public FactionGarbItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings.maxCount(1));
    }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.FACTION_BADGE);
    }

    @Override
    public Text getName(ItemStack stack) {
        AnimeDataComponents.FactionBadge badge = stack.get(AnimeDataComponents.FACTION_BADGE);
        return badge == null ? super.getName(stack) : Text.translatable("item.animecore.clan_item.named", super.getName(stack),
                Text.translatable(Ids.translationKey("faction", badge.faction())));
    }
}
