package com.narutorpg.content;

import com.animecore.item.AbilityScrollItem;
import com.animecore.item.AnimeDataComponents;
import com.animecore.item.AnimeItemVariants;
import com.animecore.item.AnimeWeaponItem;
import com.animecore.item.BloodlineScrollItem;
import com.animecore.item.ClanEmblemItem;
import com.animecore.item.ClanGarbItem;
import com.animecore.clan.ClanConfig;
import com.animecore.clan.ClanRollService;
import net.minecraft.item.ArmorItem;
import com.animecore.item.FactionBadgeItem;
import com.animecore.loot.AnimeLootHelper;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.Set;

/** Shinobi gear. All behaviour comes from animecore item classes + data components. */
public final class NarutoItems {
    private NarutoItems() {}

    private static final String SYSTEM = NarutoRPG.MOD_ID;

    static { NarutoTools.init(); NarutoTokens.init(); NarutoAdmin.init(); }

    /** From the wiki catalog: stab, or throw (with an Explosive Tag in the off-hand: explosive kunai). */
    public static final Item KUNAI = NarutoTools.item("kunai");

    /** Trench-knife style chakra blades: strong infusion, long charged slash. */
    public static final Item CHAKRA_BLADE = register("chakra_blade", new AnimeWeaponItem(ToolMaterials.DIAMOND, 3, -2.0f,
            new AnimeWeaponItem.WeaponSpec(SYSTEM, NarutoContent.CHAKRA, 4f, 2f, 10f, 12f, 0.6f, 40, 7f, true),
            new Item.Settings().rarity(Rarity.RARE)));

    public static final Item JUTSU_SCROLL = register("jutsu_scroll", new AbilityScrollItem(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item LEAF_HEADBAND = register("leaf_headband", new FactionBadgeItem(new Item.Settings()
            .component(AnimeDataComponents.FACTION_BADGE, new AnimeDataComponents.FactionBadge(NarutoContent.HIDDEN_LEAF.getValue()))));

    // ---- Clans / bloodlines ----
    /** Clan skill item: hold it (either hand) while defeating enemies to level the clan and unlock its jutsu. */
    public static final Item CLAN_EMBLEM = register("clan_emblem", new ClanEmblemItem(NarutoRPG.CTX, new Item.Settings().rarity(Rarity.UNCOMMON)));
    /** Clan clothing: one item, clan colour via dye component, emblem binding via clan_binding. */
    public static final Item CLAN_GARB = register("clan_garb", new ClanGarbItem(
            ClanGarbItem.registerMaterial(NarutoRPG.CTX.id("clan_garb")), ArmorItem.Type.CHESTPLATE,
            new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(12))));
    /** Clan reroll (unsealed) or a specific bloodline (sealed by the set_lineage loot function). */
    public static final Item BLOODLINE_SCROLL = register("bloodline_scroll", new BloodlineScrollItem(NarutoRPG.CTX,
            new Item.Settings().rarity(Rarity.EPIC)));

    // ---- Village origins ----
    /** Village forehead protector (helmet, cloth dyed in the village colour, scratchable into a missing-nin headband). */
    public static final Item FOREHEAD_PROTECTOR = register("forehead_protector", new com.animecore.item.FactionHeadbandItem(
            ClanGarbItem.registerMaterial(NarutoRPG.CTX.id("forehead_protector")), new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(15))));
    /** Village flak jacket / outfit. */
    public static final Item FLAK_JACKET = register("flak_jacket", new com.animecore.item.FactionGarbItem(
            ClanGarbItem.registerMaterial(NarutoRPG.CTX.id("flak_jacket")), ArmorItem.Type.CHESTPLATE,
            new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(15))));
    /** Forged papers: reset your origin and choose again. */
    public static final Item FORGED_PAPERS = register("forged_papers", new com.animecore.item.OriginScrollItem(NarutoRPG.CTX,
            new Item.Settings().rarity(Rarity.RARE)));

    /** Village supply caches: {@code narutorpg:chests/supply/<village>} (generated, Lootr-safe). */
    public static final java.util.Map<String, RegistryKey<LootTable>> VILLAGE_CACHES = new java.util.LinkedHashMap<>();
    static {
        for (String v : new String[]{"hidden_leaf", "hidden_sand", "hidden_mist", "hidden_cloud", "hidden_stone", "hidden_rain",
                "hidden_sound", "hidden_waterfall", "hidden_grass", "hidden_snow", "hidden_star", "hidden_whirlpool", "hidden_hot_water",
                "hidden_dream", "hidden_moon", "hidden_frost", "hidden_valley", "hidden_rock"}) {
            VILLAGE_CACHES.put(v, AnimeLootHelper.table(NarutoRPG.CTX.id("chests/supply/" + v)));
        }
    }

    /** {@code data/narutorpg/loot_table/chests/shinobi_cache.json} (generated). */
    public static final RegistryKey<LootTable> SHINOBI_CACHE = AnimeLootHelper.table(NarutoRPG.CTX.id("chests/shinobi_cache"));

    public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP, NarutoRPG.CTX.id("main"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(KUNAI))
                    .displayName(Text.translatable("itemGroup.narutorpg"))
                    .entries((context, entries) -> {
                        NarutoTokens.ITEMS.values().forEach(entries::add);
                        entries.add(CHAKRA_BLADE);
                        entries.add(LEAF_HEADBAND);
                        entries.addAll(AnimeItemVariants.infusedVariants(CHAKRA_BLADE, SYSTEM, context.lookup()));
                        entries.addAll(AnimeItemVariants.scrolls(JUTSU_SCROLL, SYSTEM, context.lookup()));
                        entries.add(BLOODLINE_SCROLL);
                        entries.add(FORGED_PAPERS);
                        entries.addAll(AnimeItemVariants.factionBound(FOREHEAD_PROTECTOR, SYSTEM, context.lookup(), true));
                        entries.addAll(AnimeItemVariants.factionBound(FLAK_JACKET, SYSTEM, context.lookup(), false));
                        entries.addAll(AnimeItemVariants.clanBound(CLAN_EMBLEM, SYSTEM, context.lookup()));
                        entries.addAll(AnimeItemVariants.clanBound(CLAN_GARB, SYSTEM, context.lookup()));
                        entries.addAll(AnimeItemVariants.clanBound(BLOODLINE_SCROLL, SYSTEM, context.lookup()));
                    })
                    .build());

    private static void injectVillage(String village, String... tables) {
        injectVillage(village, 0.25f, tables);
    }

    private static void injectVillage(String village, float chance, String... tables) {
        java.util.Set<RegistryKey<LootTable>> keys = new java.util.HashSet<>();
        for (String t : tables) keys.add(AnimeLootHelper.vanilla(t));
        AnimeLootHelper.injectInto(keys, VILLAGE_CACHES.get(village), chance);
    }

    private static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, NarutoRPG.CTX.id(path), item);
    }

    public static void init() {
        // Pity: after 10 common rolls in a row the next roll is a rare (weight <= 3) bloodline.
        ClanRollService.configure(NarutoRPG.CTX, new ClanConfig(() -> CLAN_EMBLEM, () -> CLAN_GARB, 10, 3, true));
        // Shinobi caches show up in dungeons, mineshafts, village weaponsmiths and pillager outposts (Lootr-instanced).
        AnimeLootHelper.injectInto(Set.of(
                AnimeLootHelper.vanilla("chests/simple_dungeon"),
                AnimeLootHelper.vanilla("chests/abandoned_mineshaft"),
                AnimeLootHelper.vanilla("chests/village/village_weaponsmith"),
                AnimeLootHelper.vanilla("chests/pillager_outpost")), SHINOBI_CACHE, 0.4f);
        // Village supply caches, placed where each village's terrain fits (Lootr instances them per player).
        injectVillage("hidden_leaf", "chests/village/village_plains_house", "chests/village/village_weaponsmith");
        injectVillage("hidden_sand", "chests/desert_pyramid", "chests/village/village_desert_house");
        injectVillage("hidden_mist", "chests/shipwreck_supply", "chests/buried_treasure");
        injectVillage("hidden_cloud", "chests/village/village_snowy_house", "chests/igloo_chest");
        injectVillage("hidden_stone", "chests/abandoned_mineshaft", "chests/stronghold_corridor");
        injectVillage("hidden_rain", "chests/village/village_taiga_house");
        injectVillage("hidden_sound", "chests/ancient_city", "chests/simple_dungeon");
        injectVillage("hidden_waterfall", "chests/jungle_temple");
        injectVillage("hidden_grass", "chests/village/village_savanna_house");
        injectVillage("hidden_snow", "chests/igloo_chest");
        for (String v : VILLAGE_CACHES.keySet()) {
            if (!java.util.Set.of("hidden_leaf", "hidden_sand", "hidden_mist", "hidden_cloud", "hidden_stone", "hidden_rain", "hidden_sound",
                    "hidden_waterfall", "hidden_grass", "hidden_snow").contains(v)) injectVillage(v, 0.05f, "chests/simple_dungeon");
        }
    }
}
