package com.narutorpg.content;

import com.animecore.item.AdminSealItem;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

/** Admin / cheat seals (operators or creative mode only), in their own creative tab. */
public final class NarutoAdmin {
    private NarutoAdmin() {}

    public static final Map<AdminSealItem.Action, Item> SEALS = new LinkedHashMap<>();
    public static final Map<AdminSealItem.Action, String> NAMES = Map.of(
            AdminSealItem.Action.RANK_UP, "Rank Up Seal", AdminSealItem.Action.CLAN_LEVEL, "Clan Awakening Seal",
            AdminSealItem.Action.MASTERY, "Mastery Seal", AdminSealItem.Action.SKILL_POINTS, "Skill Point Seal",
            AdminSealItem.Action.NATURES, "Nature Seal", AdminSealItem.Action.REPUTATION, "Reputation Seal",
            AdminSealItem.Action.REFILL, "Chakra Seal", AdminSealItem.Action.MAX_ALL, "Kage Seal");

    static {
        for (AdminSealItem.Action a : AdminSealItem.Action.values()) {
            SEALS.put(a, Registry.register(Registries.ITEM, NarutoRPG.CTX.id("admin_seal_" + a.key()),
                    new AdminSealItem(NarutoRPG.CTX, a, new Item.Settings())));
        }
    }

    public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP, NarutoRPG.CTX.id("admin"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(SEALS.get(AdminSealItem.Action.MAX_ALL)))
                    .displayName(Text.translatable("itemGroup.narutorpg.admin"))
                    .entries((ctx, entries) -> SEALS.values().forEach(entries::add))
                    .build());

    public static void init() {}
}
