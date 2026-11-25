package com.animecore.client.render;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * "2D icon, 3D in the world" for any anime mod's items (like the vanilla trident): inventory/GUI keeps the flat
 * sprite, while hands, the ground, item frames and thrown projectiles use a low-poly 3D model.
 */
public final class HeldModels {
    private static final Map<Item, Identifier> MODELS = new HashMap<>();
    private static boolean pluginRegistered;

    private HeldModels() {}

    /** Register a 3D model (e.g. {@code narutorpg:item/3d/kunai}) for an item. Call from a client initializer. */
    public static void register(Item item, Identifier model) {
        MODELS.put(item, model);
        if (!pluginRegistered) {
            pluginRegistered = true;
            ModelLoadingPlugin.register(ctx -> ctx.addModels(MODELS.values()));
        }
    }

    /** Model to render instead of the flat item model, or null to keep the original. */
    public static @Nullable BakedModel swap(Item item, ModelTransformationMode mode) {
        if (mode == ModelTransformationMode.GUI || MODELS.isEmpty()) return null;
        Identifier id = MODELS.get(item);
        if (id == null) return null;
        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(id);
        return model == MinecraftClient.getInstance().getBakedModelManager().getMissingModel() ? null : model;
    }

    public static int count() { return MODELS.size(); }
}
