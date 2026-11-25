package com.animecore.command;

import com.animecore.ability.AbilityExecutor;
import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.component.EnergyPool;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.transformation.TransformationService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * {@code /animecore} debug & admin commands (permission level 2). Every content id is routed to its owning system
 * through the definition's {@code system} field, so one command tree serves every anime mod.
 */
public final class AnimeCoreCommands {
    private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType(
            id -> Text.translatable("commands.animecore.unknown", String.valueOf(id)));

    private AnimeCoreCommands() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("animecore").requires(src -> src.hasPermissionLevel(2))
                .then(literal("info").executes(ctx -> info(ctx, ctx.getSource().getPlayerOrThrow()))
                        .then(argument("player", EntityArgumentType.player())
                                .executes(ctx -> info(ctx, EntityArgumentType.getPlayer(ctx, "player")))))
                .then(literal("lineage").then(argument("player", EntityArgumentType.player())
                        .then(argument("lineage", IdentifierArgumentType.identifier()).suggests(ids(AnimeRegistries.LINEAGE))
                                .executes(AnimeCoreCommands::setLineage))))
                .then(literal("faction").then(argument("player", EntityArgumentType.player())
                        .then(argument("faction", IdentifierArgumentType.identifier()).suggests(ids(AnimeRegistries.FACTION))
                                .executes(AnimeCoreCommands::setFaction))))
                .then(literal("unlock").then(argument("player", EntityArgumentType.player())
                        .then(argument("ability", IdentifierArgumentType.identifier()).suggests(ids(AnimeRegistries.ABILITY))
                                .executes(AnimeCoreCommands::unlock))))
                .then(literal("unlockform").then(argument("player", EntityArgumentType.player())
                        .then(argument("form", IdentifierArgumentType.identifier()).suggests(ids(AnimeRegistries.TRANSFORMATION))
                                .executes(AnimeCoreCommands::unlockForm))))
                .then(literal("cast").then(argument("ability", IdentifierArgumentType.identifier()).suggests(ids(AnimeRegistries.ABILITY))
                        .executes(ctx -> cast(ctx, 0f))
                        .then(argument("charge", FloatArgumentType.floatArg(0f, 1f))
                                .executes(ctx -> cast(ctx, FloatArgumentType.getFloat(ctx, "charge"))))))
                .then(literal("transform").then(argument("form", IdentifierArgumentType.identifier()).suggests(ids(AnimeRegistries.TRANSFORMATION))
                        .executes(AnimeCoreCommands::transform)))
                .then(literal("revert").then(argument("system", StringArgumentType.word()).suggests(systems())
                        .executes(ctx -> {
                            ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                            TransformationService.revert(p, system(ctx).power(p), true);
                            return 1;
                        })))
                .then(literal("tier").then(argument("player", EntityArgumentType.player())
                        .then(argument("system", StringArgumentType.word()).suggests(systems())
                                .then(argument("tier", IntegerArgumentType.integer(1, PlayerPowerComponent.MAX_TIER))
                                        .executes(ctx -> {
                                            system(ctx).power(EntityArgumentType.getPlayer(ctx, "player"))
                                                    .setTier(IntegerArgumentType.getInteger(ctx, "tier"));
                                            return 1;
                                        })))))
                .then(literal("refill").then(argument("player", EntityArgumentType.player()).executes(ctx -> {
                    ServerPlayerEntity p = EntityArgumentType.getPlayer(ctx, "player");
                    for (AnimeModContext c : AnimeModRegistry.all()) {
                        PlayerPowerComponent power = c.power(p);
                        for (EnergyPool pool : power.pools()) power.setEnergy(pool.id(), pool.max());
                    }
                    return 1;
                })))
                .then(literal("hotbar").then(argument("player", EntityArgumentType.player())
                        .then(argument("system", StringArgumentType.word()).suggests(systems())
                                .then(argument("slot", IntegerArgumentType.integer(1, 10))
                                        .then(argument("ability", IdentifierArgumentType.identifier()).suggests(ids(AnimeRegistries.ABILITY))
                                                .executes(ctx -> {
                                                    system(ctx).power(EntityArgumentType.getPlayer(ctx, "player")).setHotbarSlot(
                                                            IntegerArgumentType.getInteger(ctx, "slot") - 1,
                                                            IdentifierArgumentType.getIdentifier(ctx, "ability"));
                                                    return 1;
                                                })))))));
    }

    // ---------------------------------------------------------------- handlers

    private static int info(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        for (AnimeModContext system : AnimeModRegistry.all()) {
            PlayerPowerComponent p = system.power(player);
            StringBuilder pools = new StringBuilder();
            for (EnergyPool pool : p.pools()) {
                pools.append(String.format(" %s=%.0f/%.0f", pool.id().getPath(), pool.current(), pool.max()));
            }
            ctx.getSource().sendFeedback(() -> Text.literal("[" + system.modId() + "] tier " + p.tier()
                    + " | lineage " + p.lineage().map(Identifier::toString).orElse("-")
                    + " | faction " + p.faction().map(Identifier::toString).orElse("-")
                    + " | form " + p.activeTransformation().map(Identifier::toString).orElse("-")
                    + " |" + pools
                    + " | affinities " + p.effectiveAffinities()
                    + " | abilities " + p.unlockedAbilities()).formatted(Formatting.YELLOW), false);
        }
        return 1;
    }

    private static int setLineage(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "lineage");
        var def = AnimeRegistries.lineage(ctx.getSource().getRegistryManager(), id).orElseThrow(() -> UNKNOWN.create(id));
        owner(def.system()).power(player).setLineage(id);
        ctx.getSource().sendFeedback(() -> Text.translatable("commands.animecore.lineage", player.getDisplayName(), def.name()), true);
        return 1;
    }

    private static int setFaction(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "faction");
        var def = AnimeRegistries.faction(ctx.getSource().getRegistryManager(), id).orElseThrow(() -> UNKNOWN.create(id));
        owner(def.system()).power(player).setFaction(id);
        ctx.getSource().sendFeedback(() -> Text.translatable("commands.animecore.faction", player.getDisplayName(), def.name()), true);
        return 1;
    }

    private static int unlock(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "ability");
        var entry = AnimeRegistries.ability(ctx.getSource().getRegistryManager(), id).orElseThrow(() -> UNKNOWN.create(id));
        owner(entry.value().system()).power(player).unlockAbility(id);
        ctx.getSource().sendFeedback(() -> Text.translatable("commands.animecore.unlock", player.getDisplayName(), entry.value().name()), true);
        return 1;
    }

    private static int unlockForm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "form");
        var entry = AnimeRegistries.transformation(ctx.getSource().getRegistryManager(), id).orElseThrow(() -> UNKNOWN.create(id));
        owner(entry.value().system()).power(player).unlockTransformation(id);
        ctx.getSource().sendFeedback(() -> Text.translatable("commands.animecore.unlock", player.getDisplayName(), entry.value().name()), true);
        return 1;
    }

    private static int cast(CommandContext<ServerCommandSource> ctx, float charge) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "ability");
        var entry = AnimeRegistries.ability(ctx.getSource().getRegistryManager(), id).orElseThrow(() -> UNKNOWN.create(id));
        var result = AbilityExecutor.cast(player, entry, owner(entry.value().system()).power(player), charge, null);
        ctx.getSource().sendFeedback(() -> Text.literal(id + " -> " + result), false);
        return result.ok() ? 1 : 0;
    }

    private static int transform(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "form");
        var entry = AnimeRegistries.transformation(ctx.getSource().getRegistryManager(), id).orElseThrow(() -> UNKNOWN.create(id));
        return TransformationService.toggle(player, owner(entry.value().system()).power(player), id) ? 1 : 0;
    }

    // ---------------------------------------------------------------- utils

    private static AnimeModContext owner(String system) throws CommandSyntaxException {
        return AnimeModRegistry.get(system).orElseThrow(() -> UNKNOWN.create(system));
    }

    private static AnimeModContext system(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return owner(StringArgumentType.getString(ctx, "system"));
    }

    private static SuggestionProvider<ServerCommandSource> systems() {
        return (ctx, builder) -> CommandSource.suggestMatching(AnimeModRegistry.all().stream().map(AnimeModContext::modId), builder);
    }

    private static <T> SuggestionProvider<ServerCommandSource> ids(RegistryKey<? extends Registry<T>> key) {
        return (ctx, builder) -> {
            Registry<T> registry = ctx.getSource().getRegistryManager().get(key);
            return CommandSource.suggestIdentifiers(registry.getIds(), builder);
        };
    }
}
