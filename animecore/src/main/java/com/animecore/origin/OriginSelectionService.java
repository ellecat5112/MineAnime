package com.animecore.origin;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.platform.AnimeNetworking;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.BackgroundDefinition;
import com.animecore.registry.definition.FactionDefinition;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

/**
 * Origin (village + background) selection, server-authoritative.
 * <ol>
 *   <li>First join without an origin: the client is told to open the origin screen ({@link OpenOriginScreenPayload});
 *       with gamerule {@code animecoreOriginScreen=false} a random origin is rolled instead.</li>
 *   <li>The client answers with {@link SelectOriginPayload}; {@link #select} validates it strictly (selectable village,
 *       background allowed for it, no re-submission unless reset) and applies the starter kit, village techniques,
 *       background bonuses and starting reputations.</li>
 * </ol>
 * Everything is stored in the power component (CCA, copied on respawn, synced).
 */
public final class OriginSelectionService {
    private OriginSelectionService() {}

    public static final GameRules.Key<GameRules.BooleanRule> ORIGIN_SCREEN = GameRuleRegistry.register(
            "animecoreOriginScreen", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    public enum Result { OK, ALREADY_CHOSEN, UNKNOWN_FACTION, NOT_SELECTABLE, UNKNOWN_BACKGROUND, BACKGROUND_NOT_ALLOWED, WRONG_SYSTEM }

    public static void init() {
        AnimeNetworking.registerS2C(OpenOriginScreenPayload.ID, OpenOriginScreenPayload.CODEC);
        AnimeNetworking.registerC2S(SelectOriginPayload.ID, SelectOriginPayload.CODEC);
        AnimeNetworking.registerServerReceiver(SelectOriginPayload.ID, (payload, player) ->
                AnimeModRegistry.get(payload.system()).ifPresent(ctx -> {
                    PlayerPowerComponent power = ctx.power(player);
                    Result r = payload.random()
                            ? randomize(player, power, false)
                            : select(player, power, payload.faction().orElse(null), payload.background().orElse(null), false);
                    if (r != Result.OK) {
                        player.sendMessage(Text.translatable("message.animecore.origin." + r.name().toLowerCase()).formatted(Formatting.RED), false);
                        if (r != Result.ALREADY_CHOSEN) openScreen(player, ctx);
                    }
                }));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onJoin(handler.player));
    }

    // ------------------------------------------------------------------ queries

    public static List<Map.Entry<Identifier, FactionDefinition>> factions(DynamicRegistryManager rm, String system) {
        List<Map.Entry<Identifier, FactionDefinition>> out = new ArrayList<>();
        for (var e : rm.get(AnimeRegistries.FACTION).getEntrySet()) {
            if (e.getValue().system().equals(system)) out.add(Map.entry(e.getKey().getValue(), e.getValue()));
        }
        out.sort(Comparator.comparing((Map.Entry<Identifier, FactionDefinition> e) -> kindOrder(e.getValue().kind()))
                .thenComparing(e -> e.getValue().name().getString()));
        return out;
    }

    private static int kindOrder(String kind) {
        return switch (kind) { case "hidden_village" -> 0; case "rogue" -> 1; case "village" -> 2; case "sage_realm" -> 3; default -> 4; };
    }

    public static List<Map.Entry<Identifier, BackgroundDefinition>> backgrounds(DynamicRegistryManager rm, String system) {
        List<Map.Entry<Identifier, BackgroundDefinition>> out = new ArrayList<>();
        for (var e : rm.get(AnimeRegistries.BACKGROUND).getEntrySet()) {
            if (e.getValue().system().equals(system)) out.add(Map.entry(e.getKey().getValue(), e.getValue()));
        }
        out.sort(Map.Entry.comparingByKey(Comparator.comparing(Identifier::toString)));
        return out;
    }

    public static Optional<Identifier> rogueFaction(DynamicRegistryManager rm, String system) {
        return factions(rm, system).stream().filter(e -> e.getValue().rogue()).map(Map.Entry::getKey).findFirst();
    }

    // ------------------------------------------------------------------ flow

    public static void onJoin(ServerPlayerEntity player) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            PlayerPowerComponent power = ctx.power(player);
            if (power.originSelected() || factions(player.getRegistryManager(), ctx.modId()).isEmpty()) continue;
            if (player.getServerWorld().getGameRules().getBoolean(ORIGIN_SCREEN)) openScreen(player, ctx);
            else randomize(player, power, false);
        }
    }

    public static void openScreen(ServerPlayerEntity player, AnimeModContext ctx) {
        AnimeNetworking.send(player, new OpenOriginScreenPayload(ctx.modId()));
        player.sendMessage(Text.translatable("message.animecore.origin.hint").formatted(Formatting.YELLOW), true);
    }

    /**
     * Validates and applies an origin.
     * @param force operator path: ignores "already chosen" and selectability (still validates ids / combination)
     */
    public static Result select(ServerPlayerEntity player, PlayerPowerComponent power, @Nullable Identifier factionId,
                                @Nullable Identifier backgroundId, boolean force) {
        DynamicRegistryManager rm = player.getRegistryManager();
        if (power.originSelected() && !force) return Result.ALREADY_CHOSEN;
        if (factionId == null) return Result.UNKNOWN_FACTION;
        FactionDefinition faction = AnimeRegistries.faction(rm, factionId).orElse(null);
        if (faction == null) return Result.UNKNOWN_FACTION;
        if (!faction.system().equals(power.context().modId())) return Result.WRONG_SYSTEM;
        if (!faction.selectable() && !force) return Result.NOT_SELECTABLE;
        BackgroundDefinition background = null;
        if (backgroundId != null) {
            background = AnimeRegistries.background(rm, backgroundId).orElse(null);
            if (background == null || !background.system().equals(faction.system())) return Result.UNKNOWN_BACKGROUND;
            if ((!background.selectable() && !force) || !background.allows(factionId)) return Result.BACKGROUND_NOT_ALLOWED;
        }
        apply(player, power, factionId, faction, backgroundId, background);
        return Result.OK;
    }

    public static Result randomize(ServerPlayerEntity player, PlayerPowerComponent power, boolean force) {
        DynamicRegistryManager rm = player.getRegistryManager();
        String system = power.context().modId();
        Random r = player.getRandom();
        List<Map.Entry<Identifier, FactionDefinition>> villages = factions(rm, system).stream()
                .filter(e -> e.getValue().selectable() && "hidden_village".equals(e.getValue().kind())).toList();
        if (villages.isEmpty()) villages = factions(rm, system).stream().filter(e -> e.getValue().selectable()).toList();
        if (villages.isEmpty()) return Result.UNKNOWN_FACTION;
        Identifier village = villages.get(r.nextInt(villages.size())).getKey();
        List<Map.Entry<Identifier, BackgroundDefinition>> bgs = backgrounds(rm, system).stream()
                .filter(e -> e.getValue().selectable() && e.getValue().weight() > 0 && e.getValue().allows(village)).toList();
        Identifier bg = null;
        int total = bgs.stream().mapToInt(e -> e.getValue().weight()).sum();
        if (total > 0) {
            int pick = r.nextInt(total);
            for (var e : bgs) { pick -= e.getValue().weight(); if (pick < 0) { bg = e.getKey(); break; } }
        }
        return select(player, power, village, bg, force);
    }

    /** Clears the origin so it can be chosen again (reset scroll / operator). Items and learned jutsu are kept. */
    public static void reset(ServerPlayerEntity player, PlayerPowerComponent power) {
        power.setFaction(null);
        power.setBackground(null);
        power.setRogue(false);
        power.setFormerFaction(null);
        power.setOriginSelected(false);
        power.markTraitsDirty();
    }

    private static void apply(ServerPlayerEntity player, PlayerPowerComponent power, Identifier factionId, FactionDefinition faction,
                              @Nullable Identifier backgroundId, @Nullable BackgroundDefinition background) {
        power.setRogue(faction.rogue());
        power.setFormerFaction(null);
        power.setFaction(factionId);                       // starting abilities + own starting reputation
        faction.reputation().forEach(power::setReputation);
        if (!faction.reputation().containsKey(factionId)) power.setReputation(factionId, Math.max(faction.startingReputation(), 100));
        power.setBackground(backgroundId);

        for (ItemStack s : faction.kit().weapons()) give(player, s.copy());
        for (ItemStack s : faction.kit().clothing()) give(player, uniform(s.copy(), factionId, faction));
        if (background != null) {
            for (ItemStack s : background.bonusItems()) give(player, s.copy());
            background.startingAbilities().forEach(power::unlockAbility);
            power.addSkillPoints(background.startingSkillPoints());
            if (background.startingTierBonus() > 0) power.setTier(power.tier() + background.startingTierBonus());
        }
        power.setOriginSelected(true);
        power.forceRefreshTraits(player);
        VillageAccessManager.evaluatePool(player, power);
        com.animecore.technique.TechniqueMastery.rollStartingNature(player, power, faction);   // AHZNB: chakra comes with a nature
        com.animecore.technique.TechniqueMastery.evaluateAll(player, power);

        Text bgName = background == null ? Text.empty() : Text.literal(" · ").append(background.name());
        player.sendMessage(Text.translatable("message.animecore.origin.chosen", faction.name()).append(bgName)
                .styled(s -> s.withColor(faction.color())), false);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.5f, 1f);
        AnimeCoreEvents.ORIGIN_SELECTED.invoker().onSelected(player, power, factionId, backgroundId);
    }

    /** Village uniforms always carry the badge (and the village colour when dyeable). */
    private static ItemStack uniform(ItemStack stack, Identifier factionId, FactionDefinition faction) {
        if (!stack.contains(AnimeDataComponents.FACTION_BADGE)) {
            stack.set(AnimeDataComponents.FACTION_BADGE, new AnimeDataComponents.FactionBadge(factionId, faction.rogue()));
        }
        if (stack.isIn(ItemTags.DYEABLE) && !stack.contains(DataComponentTypes.DYED_COLOR)) {
            stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(faction.color() & 0xFFFFFF, false));
        }
        return stack;
    }

    /** Becomes missing-nin: rogue faction, home reputation -100, the home village's allies -25. */
    public static void goRogue(ServerPlayerEntity player, PlayerPowerComponent power, Identifier home) {
        DynamicRegistryManager rm = player.getRegistryManager();
        power.addReputation(home, -100);
        AnimeRegistries.faction(rm, home).ifPresent(def -> def.reputation().forEach((other, standing) -> {
            if (standing > 0 && !other.equals(home)) power.addReputation(other, -25);
        }));
        power.setFormerFaction(home);
        Identifier rogueId = rogueFaction(rm, power.context().modId()).orElse(null);
        if (rogueId != null) power.setFaction(rogueId);
        power.setRogue(true);
        power.markTraitsDirty();
        player.sendMessage(Text.translatable("message.animecore.village.went_rogue", VillageAccessManager.factionName(player, home))
                .formatted(Formatting.DARK_RED, Formatting.BOLD), false);
        AnimeCoreEvents.ORIGIN_SELECTED.invoker().onSelected(player, power, rogueId, power.background().orElse(null));
    }

    private static void give(ServerPlayerEntity player, ItemStack stack) {
        if (!player.getInventory().insertStack(stack)) player.dropItem(stack, false);
    }
}
