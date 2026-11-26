package com.animecore.technique;

import com.animecore.api.AnimeModContext;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.AnimeNetworking;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Item-based technique use (no keybinds needed): every technique lives on a <b>token</b> item - one per nature release
 * (Fire Release, Wind Release...) or school (Ninjutsu, Taijutsu, Genjutsu...). Hold the token and right-click to cast
 * the selected technique (hold to charge), sneak + scroll (or sneak + left-click) to switch.
 * <p>
 * Token resolution for an ability: its {@code token} field, else its affinity, else the system's default token - so
 * techniques added later are placed automatically. One token per system also carries the transformations (Dōjutsu).
 * Learning a technique hands out its token once.
 */
public final class TechniqueTokens {
    private TechniqueTokens() {}

    private record SystemTokens(Map<Identifier, Item> items, Identifier defaultToken, @Nullable Identifier formsToken) {}

    private static final Map<String, SystemTokens> SYSTEMS = new ConcurrentHashMap<>();

    /** One entry on a token: either an ability or a transformation. */
    public record Entry(Identifier id, boolean form) {}

    /**
     * @param tokens        token id -> its item (nature tokens use the affinity id)
     * @param defaultToken  token for abilities with neither a token nor an affinity
     * @param formsToken    token that also lists the system's transformations (null = none)
     */
    public static void configure(AnimeModContext ctx, Map<Identifier, Item> tokens, Identifier defaultToken, @Nullable Identifier formsToken) {
        SYSTEMS.put(ctx.modId(), new SystemTokens(new LinkedHashMap<>(tokens), defaultToken, formsToken));
    }

    public static void init() {
        AnimeNetworking.registerC2S(CycleTechniquePayload.ID, CycleTechniquePayload.CODEC);
        AnimeNetworking.registerServerReceiver(CycleTechniquePayload.ID, (payload, player) -> {
            ItemStack stack = player.getStackInHand(payload.hand());
            if (stack.getItem() instanceof TechniqueTokenItem token) token.cycle(player, stack, payload.delta());
        });
    }

    /** The token a form is listed on: its own {@code token}, else the system's forms token. */
    private static @Nullable Identifier formToken(DynamicRegistryManager rm, SystemTokens s, Identifier form) {
        return AnimeRegistries.transformation(rm, form).flatMap(e -> e.value().token())
                .filter(t -> s.items().containsKey(t)).orElse(s.formsToken());
    }

    public static java.util.Set<Identifier> tokenIds(String system) {
        SystemTokens s = SYSTEMS.get(system);
        return s == null ? java.util.Set.of() : s.items().keySet();
    }

    public static Optional<Item> item(String system, Identifier token) {
        SystemTokens s = SYSTEMS.get(system);
        return s == null ? Optional.empty() : Optional.ofNullable(s.items().get(token));
    }

    public static Identifier tokenOf(AbilityDefinition def) {
        if (def.token().isPresent()) return def.token().get();
        SystemTokens s = SYSTEMS.get(def.system());
        if (def.affinity().isPresent() && (s == null || s.items().containsKey(def.affinity().get()))) return def.affinity().get();
        return s == null ? def.affinity().orElse(null) : s.defaultToken();
    }

    /** Techniques available on {@code token} for this player, sorted: forms first, then by rank and name. */
    public static List<Entry> entries(DynamicRegistryManager rm, PlayerPowerComponent power, Identifier token) {
        List<Entry> out = new ArrayList<>();
        SystemTokens s = SYSTEMS.get(power.context().modId());
        if (s != null) {
            power.unlockedTransformations().stream().sorted(Comparator.comparing(Identifier::toString))
                    .filter(f -> token.equals(formToken(rm, s, f)))
                    .forEach(f -> out.add(new Entry(f, true)));
        }
        List<Map.Entry<Identifier, AbilityDefinition>> abilities = new ArrayList<>();
        for (Identifier id : power.castableAbilities()) {
            AnimeRegistries.ability(rm, id).ifPresent(e -> {
                if (token.equals(tokenOf(e.value()))) abilities.add(Map.entry(id, e.value()));
            });
        }
        abilities.sort(Comparator.comparingInt((Map.Entry<Identifier, AbilityDefinition> e) -> e.getValue().requiredTier())
                .thenComparing(e -> e.getValue().name().getString()));
        abilities.forEach(e -> out.add(new Entry(e.getKey(), false)));
        return out;
    }

    /** Hands out tokens for every technique the player knows, once per token (first unlock / first join). */
    public static void ensureTokens(ServerPlayerEntity player, PlayerPowerComponent power) {
        SystemTokens s = SYSTEMS.get(power.context().modId());
        if (s == null) return;
        DynamicRegistryManager rm = player.getRegistryManager();
        for (Identifier id : List.copyOf(power.unlockedAbilities())) {
            AnimeRegistries.ability(rm, id).ifPresent(e -> give(player, power, s, tokenOf(e.value())));
        }
        for (Identifier f : List.copyOf(power.unlockedTransformations())) {
            Identifier t = formToken(rm, s, f);
            if (t != null) give(player, power, s, t);
        }
    }

    /** {@code /jutsu tokens}: re-issues tokens the player no longer carries. */
    public static int restore(ServerPlayerEntity player, PlayerPowerComponent power) {
        SystemTokens s = SYSTEMS.get(power.context().modId());
        if (s == null) return 0;
        power.clearTokensGiven();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack st = player.getInventory().getStack(i);
            if (st.getItem() instanceof TechniqueTokenItem t && t.context() == power.context()) power.markTokenGiven(t.token());
        }
        int carried = (int) java.util.stream.IntStream.range(0, player.getInventory().size())
                .filter(i -> player.getInventory().getStack(i).getItem() instanceof TechniqueTokenItem).count();
        ensureTokens(player, power);
        return (int) java.util.stream.IntStream.range(0, player.getInventory().size())
                .filter(i -> player.getInventory().getStack(i).getItem() instanceof TechniqueTokenItem).count() - carried;
    }

    private static void give(ServerPlayerEntity player, PlayerPowerComponent power, SystemTokens s, @Nullable Identifier token) {
        if (token == null) return;
        Item item = s.items().get(token);
        if (item == null || !power.markTokenGiven(token)) return;
        ItemStack stack = new ItemStack(item);
        if (!player.getInventory().insertStack(stack)) player.dropItem(stack, false);
        player.sendMessage(Text.translatable("message.animecore.token.received", item.getName()).formatted(Formatting.AQUA), false);
    }
}
