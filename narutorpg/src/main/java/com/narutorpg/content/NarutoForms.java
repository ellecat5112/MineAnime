package com.narutorpg.content;

import com.animecore.item.AwakeningItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.narutorpg.NarutoRPG;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Awakening items for the forms of tools/catalog/forms.py (tailed-beast seals, curse marks, Kāma, sage scroll, dōjutsu and
 * bloodline awakenings). The list is generated into assets/narutorpg/awakening_items.json; the forms themselves are data.
 */
public final class NarutoForms {
    private NarutoForms() {}

    public static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    static {
        try (Reader r = new InputStreamReader(NarutoForms.class.getResourceAsStream("/assets/narutorpg/awakening_items.json"), StandardCharsets.UTF_8)) {
            JsonArray list = JsonParser.parseReader(r).getAsJsonArray();
            for (JsonElement e : list) {
                JsonObject o = e.getAsJsonObject();
                String id = o.get("item").getAsString();
                Item item = Registry.register(Registries.ITEM, NarutoRPG.CTX.id(id), new AwakeningItem(NarutoRPG.CTX,
                        Identifier.of(o.get("form").getAsString()), o.get("lore").getAsString(), new Item.Settings().rarity(Rarity.EPIC)));
                ITEMS.put(id, item);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("awakening_items.json missing - run python3 tools/catalog/forms.py", ex);
        }
    }

    public static Item item(String id) { return ITEMS.get(id); }

    public static void init() {}
}
