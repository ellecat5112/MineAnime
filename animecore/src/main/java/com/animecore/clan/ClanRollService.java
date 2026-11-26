package com.animecore.clan;

import com.animecore.AnimeCore;
import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clan rolling and selection, server side.
 * <ul>
 *   <li><b>First join</b>: with gamerule {@code animecoreClanAutoRoll} (default true) every new player gets a weighted
 *       roll; otherwise they start in the fallback lineage and may {@link #choose} one selectable clan once.</li>
 *   <li><b>Reroll</b>: bloodline scroll item / {@code /animecore clan reroll}. Weighted, excludes the current clan,
 *       and a pity counter guarantees a rare clan ({@code weight <= rareWeight}) after {@code pityThreshold} misses.</li>
 *   <li>Rolls are deterministic per {@link Random}, so loot functions using the loot context random give Lootr's
 *       per-player chests independent results.</li>
 * </ul>
 */
public final class ClanRollService {
    private ClanRollService() {}

    public static final GameRules.Key<GameRules.BooleanRule> AUTO_ROLL = GameRuleRegistry.register(
            "animecoreClanAutoRoll", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    private static final Map<String, ClanConfig> CONFIGS = new ConcurrentHashMap<>();

    public static void configure(AnimeModContext context, ClanConfig config) {
        CONFIGS.put(context.modId(), config);
    }

    public static Optional<ClanConfig> config(String system) {
        return Optional.ofNullable(CONFIGS.get(system));
    }

    public static void init() {}

    // ------------------------------------------------------------------ queries

    public static List<Map.Entry<Identifier, LineageDefinition>> lineages(DynamicRegistryManager rm, String system) {
        Registry<LineageDefinition> reg = rm.get(AnimeRegistries.LINEAGE);
        List<Map.Entry<Identifier, LineageDefinition>> out = new ArrayList<>();
        for (Map.Entry<RegistryKey<LineageDefinition>, LineageDefinition> e : reg.getEntrySet()) {
            if (e.getValue().system().equals(system)) out.add(Map.entry(e.getKey().getValue(), e.getValue()));
        }
        out.sort(Map.Entry.comparingByKey(Comparator.comparing(Identifier::toString)));
        return out;
    }

    public static Optional<Identifier> fallback(DynamicRegistryManager rm, String system) {
        return lineages(rm, system).stream().filter(e -> e.getValue().fallback()).map(Map.Entry::getKey).findFirst();
    }

    // ------------------------------------------------------------------ rolling

    /**
     * Weighted roll among rollable lineages of {@code system}.
     *
     * @param exclude     lineage that may not come up (current clan on reroll), may be null
     * @param forceRare   only lineages with {@code weight <= rareWeight} (pity)
     */
    public static Optional<Identifier> roll(DynamicRegistryManager rm, String system, Random random, @Nullable Identifier exclude,
                                            boolean forceRare, int rareWeight) {
        List<Map.Entry<Identifier, LineageDefinition>> pool = new ArrayList<>();
        for (var e : lineages(rm, system)) {
            if (!e.getValue().rollable() || e.getKey().equals(exclude)) continue;
            if (forceRare && e.getValue().weight() > rareWeight) continue;
            pool.add(e);
        }
        if (pool.isEmpty() && forceRare) return roll(rm, system, random, exclude, false, rareWeight);
        int total = pool.stream().mapToInt(e -> e.getValue().weight()).sum();
        if (total <= 0) return Optional.empty();
        int pick = random.nextInt(total);
        for (var e : pool) {
            pick -= e.getValue().weight();
            if (pick < 0) return Optional.of(e.getKey());
        }
        return Optional.of(pool.get(pool.size() - 1).getKey());
    }

    /** Rolls with pity bookkeeping and assigns the result. */
    public static Optional<Identifier> rollAndAssign(ServerPlayerEntity player, PlayerPowerComponent power, boolean excludeCurrent,
                                                     ClanAbilityManager.Reason reason) {
        String system = power.context().modId();
        ClanConfig cfg = config(system).orElse(new ClanConfig(() -> net.minecraft.item.Items.AIR, null, 0, 0, false));
        boolean pity = cfg.pityThreshold() > 0 && power.clanPity() >= cfg.pityThreshold();
        Optional<Identifier> result = roll(player.getRegistryManager(), system, player.getRandom(),
                excludeCurrent ? power.lineage().orElse(null) : null, pity, cfg.rareWeight());
        if (result.isEmpty()) return result;
        boolean rare = AnimeRegistries.lineage(player.getRegistryManager(), result.get())
                .map(d -> d.weight() <= cfg.rareWeight()).orElse(false);
        power.setClanPity(rare ? 0 : power.clanPity() + 1);
        if (reason == ClanAbilityManager.Reason.REROLL_ITEM) power.incrementClanRerolls();
        ClanAbilityManager.assign(player, power, result.get(), reason);
        if (rare) player.sendMessage(Text.translatable("message.animecore.clan.rare").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        return result;
    }

    /** One-time free choice of a selectable clan (players without a rolled clan, or still in the fallback). */
    public static boolean choose(ServerPlayerEntity player, PlayerPowerComponent power, Identifier lineage) {
        Optional<LineageDefinition> def = AnimeRegistries.lineage(player.getRegistryManager(), lineage);
        if (def.isEmpty() || !def.get().selectable() || !def.get().system().equals(power.context().modId())) return false;
        boolean inFallback = power.lineage().isEmpty() || power.lineage()
                .flatMap(id -> AnimeRegistries.lineage(player.getRegistryManager(), id)).map(LineageDefinition::fallback).orElse(true);
        if (!inFallback) return false;
        return ClanAbilityManager.assign(player, power, lineage, ClanAbilityManager.Reason.CHOICE);
    }

    /** First join: roll (gamerule) or fallback, once per player and system. */
    public static void onJoin(ServerPlayerEntity player) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            PlayerPowerComponent power = ctx.power(player);
            if (power.clanInitialized()) {
                ClanAbilityManager.evaluate(player, power); // datapack may have changed thresholds
                continue;
            }
            DynamicRegistryManager rm = player.getRegistryManager();
            if (lineages(rm, ctx.modId()).isEmpty()) continue;
            boolean autoRoll = player.getServerWorld().getGameRules().getBoolean(AUTO_ROLL);
            Optional<Identifier> rolled = autoRoll
                    ? rollAndAssign(player, power, false, ClanAbilityManager.Reason.FIRST_JOIN)
                    : Optional.empty();
            if (rolled.isEmpty()) {
                fallback(rm, ctx.modId()).ifPresent(f -> ClanAbilityManager.assign(player, power, f, ClanAbilityManager.Reason.FIRST_JOIN));
                if (!autoRoll) player.sendMessage(Text.translatable("message.animecore.clan.choose_hint").formatted(Formatting.YELLOW), false);
            }
            power.setClanInitialized(true);
            AnimeCore.LOGGER.debug("[{}] first-join clan for {}: {}", ctx.modId(), player.getName().getString(), power.lineage().orElse(null));
        }
    }

    // ------------------------------------------------------------------ items

    public static ItemStack boundStack(Item item, Identifier lineage, @Nullable LineageDefinition def) {
        ItemStack stack = new ItemStack(item);
        stack.set(AnimeDataComponents.CLAN_BINDING, new AnimeDataComponents.ClanBinding(lineage));
        if (def != null && stack.contains(net.minecraft.component.DataComponentTypes.DYED_COLOR) == false
                && item instanceof net.minecraft.item.ArmorItem) {
            ClanAbilityManager.dye(stack, def.color());
        }
        return stack;
    }

    public static Optional<Identifier> binding(ItemStack stack) {
        AnimeDataComponents.ClanBinding b = stack.get(AnimeDataComponents.CLAN_BINDING);
        return b == null ? Optional.empty() : Optional.of(b.lineage());
    }

    /** Is {@code stack} this system's skill item, bound to the player's current clan? */
    public static boolean isSkillItemFor(ItemStack stack, PlayerPowerComponent power) {
        if (stack.isEmpty()) return false;
        Optional<ClanConfig> cfg = config(power.context().modId());
        if (cfg.isEmpty() || !stack.isOf(cfg.get().skillItem().get())) return false;
        Optional<Identifier> bound = binding(stack);
        return bound.isPresent() && bound.equals(power.lineage());
    }
}
