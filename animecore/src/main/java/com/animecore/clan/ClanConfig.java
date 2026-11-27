package com.animecore.clan;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Per-system clan settings a content mod hands to {@link ClanRollService#configure}.
 *
 * @param skillItem       the "clan skill" item (emblem) whose holder converts kill experience into clan skill
 * @param garbItem        optional dyeable clan clothing handed out with the starter kit
 * @param pityThreshold   after this many non-rare rolls in a row the next roll is guaranteed rare (0 = no pity)
 * @param rareWeight      lineages with {@code weight <= rareWeight} count as rare
 * @param giveStarterKit  hand out skill item, garb and {@code clan_items} once per clan on assignment
 */
public record ClanConfig(Supplier<Item> skillItem, @Nullable Supplier<Item> garbItem, int pityThreshold, int rareWeight,
                         boolean giveStarterKit, boolean rollOnJoin) {
    /** Rolls a lineage on first join (ninja clans). */
    public ClanConfig(Supplier<Item> skillItem, @Nullable Supplier<Item> garbItem, int pityThreshold, int rareWeight, boolean giveStarterKit) {
        this(skillItem, garbItem, pityThreshold, rareWeight, giveStarterKit, true);
    }
}
