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

    static { NarutoTools.init(); }

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

    /** {@code data/narutorpg/loot_table/chests/shinobi_cache.json} (generated). */
    public static final RegistryKey<LootTable> SHINOBI_CACHE = AnimeLootHelper.table(NarutoRPG.CTX.id("chests/shinobi_cache"));

    public static final ItemGroup GROUP = Registry.register(Registries.ITEM_GROUP, NarutoRPG.CTX.id("main"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(KUNAI))
                    .displayName(Text.translatable("itemGroup.narutorpg"))
                    .entries((context, entries) -> {
                        entries.add(CHAKRA_BLADE);
                        entries.add(LEAF_HEADBAND);
                        entries.addAll(AnimeItemVariants.infusedVariants(CHAKRA_BLADE, SYSTEM, context.lookup()));
                        entries.addAll(AnimeItemVariants.scrolls(JUTSU_SCROLL, SYSTEM, context.lookup()));
                        entries.add(BLOODLINE_SCROLL);
                        entries.addAll(AnimeItemVariants.clanBound(CLAN_EMBLEM, SYSTEM, context.lookup()));
                        entries.addAll(AnimeItemVariants.clanBound(CLAN_GARB, SYSTEM, context.lookup()));
                        entries.addAll(AnimeItemVariants.clanBound(BLOODLINE_SCROLL, SYSTEM, context.lookup()));
                    })
                    .build());

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
    }
}
