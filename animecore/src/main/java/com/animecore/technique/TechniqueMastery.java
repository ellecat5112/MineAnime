package com.animecore.technique;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.clan.ClanSkill;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.AffinityDefinition;
import com.animecore.registry.definition.FactionDefinition;
import com.animecore.registry.definition.LineageDefinition;
import com.animecore.trait.TraitModifiers;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Nature release / school mastery (AHZNB-style training): every technique token has a mastery level. Holding the
 * token while defeating enemies turns their experience into token experience (2.5x slower without the affinity);
 * level N unlocks the first N "free" techniques of that token in rank order (clan and village techniques are excluded -
 * they come from their own systems). Rank (tier) still gates each technique.
 * <ul>
 *   <li>Origin: a starting basic nature is rolled (leaning to the village's element) and its first technique unlocked.</li>
 *   <li>Training a basic nature you do not hold to level 10 makes it one of your affinities.</li>
 *   <li>Natures with {@code awaken_from} (kekkei genkai without a two-nature recipe) awaken at {@code awaken_level} of
 *       their source; two-nature fusions appear on their own once both natures are held (AffinityMatrix).</li>
 * </ul>
 */
public final class TechniqueMastery {
    private TechniqueMastery() {}

    public static final int BASIC_ATTUNE_LEVEL = 10;
    public static final float OFF_AFFINITY_MULTIPLIER = 0.4f;   // 2.5x the experience

    private static final Map<DynamicRegistryManager, Map<Identifier, List<Identifier>>> LISTS = new WeakHashMap<>();

    public static synchronized void invalidate() { LISTS.clear(); }

    /** Free techniques of {@code token} in unlock order (rank, then name). */
    public static synchronized List<Identifier> list(DynamicRegistryManager rm, Identifier token) {
        return LISTS.computeIfAbsent(rm, TechniqueMastery::build).getOrDefault(token, List.of());
    }

    private static Map<Identifier, List<Identifier>> build(DynamicRegistryManager rm) {
        Set<Identifier> owned = new HashSet<>();
        for (LineageDefinition l : rm.get(AnimeRegistries.LINEAGE)) {
            owned.addAll(l.grantedAbilities());
            l.progression().forEach(e -> e.ability().ifPresent(owned::add));
        }
        for (FactionDefinition f : rm.get(AnimeRegistries.FACTION)) {
            owned.addAll(f.jutsuPool());
            owned.addAll(f.exclusive().abilities());
        }
        Map<Identifier, List<Map.Entry<Identifier, AbilityDefinition>>> byToken = new java.util.HashMap<>();
        for (var e : rm.get(AnimeRegistries.ABILITY).getEntrySet()) {
            Identifier id = e.getKey().getValue();
            if (owned.contains(id)) continue;
            Identifier token = TechniqueTokens.tokenOf(e.getValue());
            if (token != null) byToken.computeIfAbsent(token, t -> new ArrayList<>()).add(Map.entry(id, e.getValue()));
        }
        Map<Identifier, List<Identifier>> out = new java.util.HashMap<>();
        byToken.forEach((token, list) -> {
            list.sort(Comparator.comparingInt((Map.Entry<Identifier, AbilityDefinition> e) -> e.getValue().requiredTier())
                    .thenComparingDouble(e -> e.getValue().cost().amount())
                    .thenComparing(e -> e.getKey().toString()));
            out.put(token, list.stream().map(Map.Entry::getKey).toList());
        });
        return out;
    }

    private static boolean hasAffinity(PlayerPowerComponent power, Identifier token, DynamicRegistryManager rm) {
        // school tokens (no affinity of that id) train at full speed
        return power.effectiveAffinities().contains(token) || AnimeRegistries.affinities(rm).getOrEmpty(token).isEmpty();
    }

    /** Kill experience while holding a token. */
    public static void addExperience(ServerPlayerEntity player, PlayerPowerComponent power, Identifier token, long amount) {
        DynamicRegistryManager rm = player.getRegistryManager();
        List<Identifier> list = list(rm, token);
        if (list.isEmpty() || amount <= 0) return;
        long gained = Math.max(1, Math.round(amount * (hasAffinity(power, token, rm) ? 1f : OFF_AFFINITY_MULTIPLIER)));
        int level = power.tokenLevel(token);
        long xp = power.tokenXp(token) + gained;
        int start = level;
        while (level < list.size() && xp >= ClanSkill.experienceForNext(Math.max(1, level))) {
            xp -= ClanSkill.experienceForNext(Math.max(1, level));
            level++;
        }
        power.setTokenMastery(token, level, level >= list.size() ? 0 : xp);
        if (level > start) {
            Text name = TechniqueTokens.item(power.context().modId(), token).map(i -> (Text) i.getName()).orElse(Text.literal(token.getPath()));
            player.sendMessage(Text.translatable("message.animecore.mastery.level", name, level).formatted(Formatting.GOLD), true);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5f, 1.8f);
        }
        evaluate(player, power, token);
    }

    /** Unlocks the first {@code level} free techniques of the token (rank permitting) and checks awakenings. */
    public static void evaluate(ServerPlayerEntity player, PlayerPowerComponent power, Identifier token) {
        DynamicRegistryManager rm = player.getRegistryManager();
        List<Identifier> list = list(rm, token);
        int level = power.tokenLevel(token);
        for (int i = 0; i < Math.min(level, list.size()); i++) {
            Identifier id = list.get(i);
            int tier = AnimeRegistries.ability(rm, id).map(e -> e.value().requiredTier()).orElse(1);
            if (power.tier() < tier || power.unlockedAbilities().contains(id)) continue;
            if (power.unlockAbility(id)) {
                AnimeRegistries.ability(rm, id).ifPresent(e -> player.sendMessage(
                        Text.translatable("message.animecore.mastery.learned", e.value().name()).formatted(Formatting.AQUA), false));
            }
        }
        awaken(player, power, token, level);
    }

    public static void evaluateAll(ServerPlayerEntity player, PlayerPowerComponent power) {
        DynamicRegistryManager rm = player.getRegistryManager();
        // every held nature trains from level 1 (its first technique)
        for (Identifier aff : power.effectiveAffinities()) {
            if (power.tokenLevel(aff) < 1 && !list(rm, aff).isEmpty()) power.setTokenMastery(aff, 1, 0);
        }
        for (Identifier token : List.copyOf(tokensWithLevels(power))) evaluate(player, power, token);
    }

    private static Set<Identifier> tokensWithLevels(PlayerPowerComponent power) {
        Set<Identifier> out = new HashSet<>(power.effectiveAffinities());
        TechniqueTokens.tokenIds(power.context().modId()).forEach(t -> { if (power.tokenLevel(t) > 0) out.add(t); });
        return out;
    }

    private static void awaken(ServerPlayerEntity player, PlayerPowerComponent power, Identifier token, int level) {
        var affinities = AnimeRegistries.affinities(player.getRegistryManager());
        affinities.getOrEmpty(token).ifPresent(def -> {
            if (def.basic() && level >= BASIC_ATTUNE_LEVEL && power.addPersonalAffinity(token)) announce(player, def);
        });
        for (var e : affinities.getEntrySet()) {
            AffinityDefinition def = e.getValue();
            if (def.awakenFrom().map(token::equals).orElse(false) && level >= def.awakenLevel()
                    && def.system().equals(power.context().modId()) && power.addPersonalAffinity(e.getKey().getValue())) {
                announce(player, def);
                Identifier awakened = e.getKey().getValue();
                power.setTokenMastery(awakened, Math.max(1, power.tokenLevel(awakened)), 0);
                if (!awakened.equals(token)) evaluate(player, power, awakened);   // its first jutsu (and token) right away
            }
        }
    }

    private static void announce(ServerPlayerEntity player, AffinityDefinition def) {
        player.sendMessage(Text.translatable("message.animecore.mastery.awakened", def.name()).styled(s -> s.withColor(def.color()).withBold(true)), false);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.6f, 1.4f);
    }

    // ------------------------------------------------------------------ starting nature

    /**
     * Rolls a basic nature for a shinobi without one: 50 % the village's element (the affinity its perks empower),
     * otherwise any basic nature. The nature's token and first technique follow.
     */
    public static @Nullable Identifier rollStartingNature(ServerPlayerEntity player, PlayerPowerComponent power, @Nullable FactionDefinition village) {
        DynamicRegistryManager rm = player.getRegistryManager();
        String system = power.context().modId();
        List<Identifier> basics = new ArrayList<>();
        for (var e : AnimeRegistries.affinities(rm).getEntrySet()) {
            if (e.getValue().basic() && e.getValue().system().equals(system)) basics.add(e.getKey().getValue());
        }
        basics.sort(Comparator.comparing(Identifier::toString));
        if (basics.isEmpty()) return null;
        for (Identifier b : basics) if (power.effectiveAffinities().contains(b) || power.personalAffinities().contains(b)) return null;
        Random r = player.getRandom();
        Identifier pick = null;
        if (village != null && r.nextBoolean()) {
            for (var t : village.traits()) {
                if (t instanceof TraitModifiers.AbilityPowerTrait p && p.affinity().filter(basics::contains).isPresent()) pick = p.affinity().get();
            }
        }
        if (pick == null) pick = basics.get(r.nextInt(basics.size()));
        power.addPersonalAffinity(pick);
        power.setTokenMastery(pick, Math.max(1, power.tokenLevel(pick)), 0);
        AnimeRegistries.affinities(rm).getOrEmpty(pick).ifPresent(def -> player.sendMessage(
                Text.translatable("message.animecore.mastery.nature", def.name()).styled(s -> s.withColor(def.color())), false));
        evaluate(player, power, pick);
        return pick;
    }

    /** Join hook: players who chose an origin before natures existed get one now; held natures sync their unlocks. */
    public static void onJoin(ServerPlayerEntity player) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            PlayerPowerComponent power = ctx.power(player);
            power.forceRefreshTraits(player);
            if (power.originSelected()) {
                FactionDefinition f = power.faction().flatMap(id -> AnimeRegistries.faction(player.getRegistryManager(), id)).orElse(null);
                rollStartingNature(player, power, f);
            }
            evaluateAll(player, power);
        }
    }
}
