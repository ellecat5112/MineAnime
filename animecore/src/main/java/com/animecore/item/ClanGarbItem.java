package com.animecore.item;

import com.animecore.clan.ClanRollService;
import com.animecore.platform.Ids;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.List;

/**
 * Clan clothing (Uchiha high-collar shirt, Hyūga robes, Aburame coat...). One item for every clan: the
 * {@code animecore:clan_binding} component names the clan and {@code minecraft:dyed_color} carries the clan colour,
 * so the armor layer and the icon are tinted per clan without per-clan textures. Add the item to
 * {@code #minecraft:dyeable} so the vanilla armor renderer applies the tint.
 */
public class ClanGarbItem extends ArmorItem implements AnimeComponentItem {
    public ClanGarbItem(RegistryEntry<ArmorMaterial> material, Type type, Item.Settings settings) {
        super(material, type, settings.maxCount(1));
    }

    /**
     * Registers a light, dyeable cloth armor material {@code <id>} whose layers are
     * {@code <ns>:textures/models/armor/<path>_layer_1.png} (dyed) and {@code ..._layer_1_overlay.png} (undyed trim).
     */
    public static RegistryEntry<ArmorMaterial> registerMaterial(Identifier id) {
        EnumMap<Type, Integer> defense = new EnumMap<>(Type.class);
        defense.put(Type.HELMET, 1);
        defense.put(Type.CHESTPLATE, 3);
        defense.put(Type.LEGGINGS, 2);
        defense.put(Type.BOOTS, 1);
        defense.put(Type.BODY, 3);
        return Registry.registerReference(Registries.ARMOR_MATERIAL, id, new ArmorMaterial(defense, 18,
                SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, () -> Ingredient.ofItems(Items.LEATHER),
                List.of(new ArmorMaterial.Layer(id, "", true), new ArmorMaterial.Layer(id, "_overlay", false)), 0.5f, 0f));
    }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.CLAN_BINDING);
    }

    @Override
    public Text getName(ItemStack stack) {
        return ClanRollService.binding(stack)
                .<Text>map(id -> Text.translatable("item.animecore.clan_item.named", super.getName(stack),
                        Text.translatable(Ids.translationKey("lineage", id))))
                .orElseGet(() -> super.getName(stack));
    }
}
