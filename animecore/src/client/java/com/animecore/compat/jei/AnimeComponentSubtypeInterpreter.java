package com.animecore.compat.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Subtype key = the values of the item's animecore data components (records with value equality), so every
 * scroll ability / infusion affinity / badge faction is a distinct JEI entry. This is JEI 19's replacement for the
 * deprecated {@code IIngredientSubtypeInterpreter}; the legacy string is still provided for old bookmarks.
 */
public final class AnimeComponentSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    private final List<ComponentType<?>> components;

    public AnimeComponentSubtypeInterpreter(List<ComponentType<?>> components) {
        this.components = List.copyOf(components);
    }

    @Override
    public @Nullable Object getSubtypeData(ItemStack stack, UidContext context) {
        List<Object> values = new ArrayList<>(components.size());
        boolean any = false;
        for (ComponentType<?> type : components) {
            Object value = stack.get(type);
            any |= value != null;
            values.add(value);
        }
        return any ? values : null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
        Object data = getSubtypeData(stack, context);
        return data == null ? "" : Objects.toString(data);
    }
}
