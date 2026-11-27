package com.animecore.command;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.origin.OriginSelectionService;
import com.animecore.registry.AnimeRegistries;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * {@code /origin open|choose|random|info} for players, {@code /origin set|reset} for operators. Content mods can mount the
 * same tree under their own root ({@code /naruto origin ...}) with {@link #tree(String, String)}.
 */
public final class OriginCommands {
    private OriginCommands() {}

    private static final SimpleCommandExceptionType FAILED = new SimpleCommandExceptionType(Text.translatable("commands.animecore.origin.failed"));

    private static SuggestionProvider<ServerCommandSource> factions(String system) {
        return (c, b) -> CommandSource.suggestIdentifiers(OriginSelectionService.factions(c.getSource().getRegistryManager(), system).stream()
                .filter(e -> system == null || e.getValue().system().equals(system)).map(java.util.Map.Entry::getKey), b);
    }

    private static SuggestionProvider<ServerCommandSource> backgrounds(String system) {
        return (c, b) -> CommandSource.suggestIdentifiers(OriginSelectionService.backgrounds(c.getSource().getRegistryManager(), system)
                .stream().map(java.util.Map.Entry::getKey), b);
    }

    public static void register(CommandDispatcher<ServerCommandSource> d) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            d.register(tree("origin", ctx.modId()));
            break; // /origin serves the first system; further systems mount their own root via tree(...)
        }
    }

    /** {@code /<root> open|choose <village> [background]|random|info|set <players> <village> [background]|reset <players>}. */
    public static LiteralArgumentBuilder<ServerCommandSource> tree(String root, String system) {
        return literal(root)
                .then(literal("open").executes(c -> { OriginSelectionService.openScreen(c.getSource().getPlayerOrThrow(), ctx(system)); return 1; }))
                .then(literal("info").executes(c -> info(c, system)))
                .then(literal("random").executes(c -> result(c, OriginSelectionService.randomize(c.getSource().getPlayerOrThrow(),
                        ctx(system).power(c.getSource().getPlayerOrThrow()), false))))
                .then(literal("choose").then(argument("village", IdentifierArgumentType.identifier()).suggests(factions(system))
                        .executes(c -> choose(c, system, false, null))
                        .then(argument("background", IdentifierArgumentType.identifier()).suggests(backgrounds(system))
                                .executes(c -> choose(c, system, false, IdentifierArgumentType.getIdentifier(c, "background"))))))
                .then(literal("set").requires(s -> s.hasPermissionLevel(2)).then(argument("players", EntityArgumentType.players())
                        .then(argument("village", IdentifierArgumentType.identifier()).suggests(factions(system))
                                .executes(c -> set(c, system, null))
                                .then(argument("background", IdentifierArgumentType.identifier()).suggests(backgrounds(system))
                                        .executes(c -> set(c, system, IdentifierArgumentType.getIdentifier(c, "background")))))))
                .then(literal("reset").requires(s -> s.hasPermissionLevel(2)).then(argument("players", EntityArgumentType.players())
                        .executes(c -> {
                            Collection<ServerPlayerEntity> ps = EntityArgumentType.getPlayers(c, "players");
                            for (ServerPlayerEntity p : ps) {
                                OriginSelectionService.reset(p, ctx(system).power(p));
                                OriginSelectionService.openScreen(p, ctx(system));
                            }
                            return ps.size();
                        })));
    }

    private static AnimeModContext ctx(String system) {
        return AnimeModRegistry.get(system).orElseThrow();
    }

    private static int choose(CommandContext<ServerCommandSource> c, String system, boolean force, Identifier background) throws CommandSyntaxException {
        ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
        return result(c, OriginSelectionService.select(p, ctx(system).power(p), IdentifierArgumentType.getIdentifier(c, "village"), background, force));
    }

    private static int set(CommandContext<ServerCommandSource> c, String system, Identifier background) throws CommandSyntaxException {
        Identifier village = IdentifierArgumentType.getIdentifier(c, "village");
        int n = 0;
        for (ServerPlayerEntity p : EntityArgumentType.getPlayers(c, "players")) {
            var r = OriginSelectionService.select(p, ctx(system).power(p), village, background, true);
            if (r == OriginSelectionService.Result.OK) n++;
            else c.getSource().sendError(Text.translatable("message.animecore.origin." + r.name().toLowerCase()));
        }
        return n;
    }

    private static int result(CommandContext<ServerCommandSource> c, OriginSelectionService.Result r) throws CommandSyntaxException {
        if (r == OriginSelectionService.Result.OK) return 1;
        c.getSource().sendError(Text.translatable("message.animecore.origin." + r.name().toLowerCase()));
        throw FAILED.create();
    }

    private static int info(CommandContext<ServerCommandSource> c, String system) throws CommandSyntaxException {
        ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
        var power = ctx(system).power(p);
        var rm = p.getRegistryManager();
        Text village = power.faction().flatMap(f -> AnimeRegistries.faction(rm, f)).map(f -> (Text) f.name()).orElse(Text.literal("-"));
        Text bg = power.background().flatMap(b -> AnimeRegistries.background(rm, b)).map(b -> (Text) b.name()).orElse(Text.literal("-"));
        c.getSource().sendFeedback(() -> com.animecore.api.AnimeText.tr(power.context().modId(), "commands.animecore.origin.info", village, bg,
                power.rogue() ? com.animecore.api.AnimeText.tr(power.context().modId(), "commands.animecore.origin.rogue") : Text.empty()).formatted(Formatting.GOLD), false);
        power.reputations().entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(8).forEach(e ->
                c.getSource().sendFeedback(() -> Text.literal("  ").append(AnimeRegistries.faction(rm, e.getKey()).map(f -> (Text) f.name())
                        .orElse(Text.literal(e.getKey().toString()))).append(": " + e.getValue())
                        .formatted(e.getValue() >= 0 ? Formatting.GREEN : Formatting.RED), false));
        return 1;
    }
}
