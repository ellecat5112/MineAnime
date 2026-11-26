package com.animecore.loot;

import com.animecore.item.AnimeDataComponents;
import com.animecore.origin.OriginSelectionService;
import com.animecore.registry.AnimeRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Optional;

/**
 * {@code "function": "animecore:set_faction"}: binds village gear to a faction - {@code mode: "badge"} writes
 * {@code animecore:faction_badge} (headbands / uniforms, dyed in the village colour, optionally {@code scratched}),
 * {@code mode: "alignment"} writes {@code animecore:faction_alignment} (village-exclusive weapons). Either a fixed
 * {@code faction} or a random selectable hidden village of {@code system}. Uses the loot-context random, so Lootr
 * per-player containers roll independently.
 */
public final class SetFactionFunction extends ConditionalLootFunction {
    public enum Mode implements StringIdentifiable {
        BADGE, ALIGNMENT;
        public static final Codec<Mode> CODEC = StringIdentifiable.createCodec(Mode::values);
        @Override public String asString() { return name().toLowerCase(java.util.Locale.ROOT); }
    }

    public static final MapCodec<SetFactionFunction> CODEC = RecordCodecBuilder.mapCodec(i -> addConditionsField(i).and(i.group(
            Identifier.CODEC.optionalFieldOf("faction").forGetter(f -> f.faction),
            Codec.STRING.optionalFieldOf("system").forGetter(f -> f.system),
            Mode.CODEC.optionalFieldOf("mode", Mode.BADGE).forGetter(f -> f.mode),
            Codec.BOOL.optionalFieldOf("scratched", false).forGetter(f -> f.scratched)
    )).apply(i, SetFactionFunction::new));

    private final Optional<Identifier> faction;
    private final Optional<String> system;
    private final Mode mode;
    private final boolean scratched;

    private SetFactionFunction(List<LootCondition> conditions, Optional<Identifier> faction, Optional<String> system, Mode mode, boolean scratched) {
        super(conditions);
        this.faction = faction;
        this.system = system;
        this.mode = mode;
        this.scratched = scratched;
    }

    @Override public LootFunctionType<SetFactionFunction> getType() { return AnimeLootFunctions.SET_FACTION; }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        var rm = context.getWorld().getRegistryManager();
        Optional<Identifier> chosen = faction.or(() -> system.flatMap(s -> {
            List<Identifier> pool = OriginSelectionService.factions(rm, s).stream()
                    .filter(e -> e.getValue().selectable() && "hidden_village".equals(e.getValue().kind())).map(java.util.Map.Entry::getKey).toList();
            return pool.isEmpty() ? Optional.empty() : Optional.of(Util.getRandom(pool, context.getRandom()));
        }));
        chosen.ifPresent(id -> {
            int threshold = AnimeRegistries.faction(rm, id).map(d -> d.exclusive().alliedReputation()).orElse(50);
            if (mode == Mode.ALIGNMENT) {
                stack.set(AnimeDataComponents.FACTION_ALIGNMENT, new AnimeDataComponents.FactionAlignment(id, threshold, false));
            } else {
                stack.set(AnimeDataComponents.FACTION_BADGE, new AnimeDataComponents.FactionBadge(id, scratched));
                if (stack.isIn(ItemTags.DYEABLE)) AnimeRegistries.faction(rm, id).ifPresent(d ->
                        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(d.color() & 0xFFFFFF, false)));
            }
        });
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> fixed(Identifier faction, Mode mode) {
        return builder(c -> new SetFactionFunction(c, Optional.of(faction), Optional.empty(), mode, false));
    }

    public static ConditionalLootFunction.Builder<?> random(String system, Mode mode) {
        return builder(c -> new SetFactionFunction(c, Optional.empty(), Optional.of(system), mode, false));
    }
}
