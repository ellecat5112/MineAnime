package com.animecore.command;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.clan.ClanAbilityManager;
import com.animecore.clan.ClanRollService;
import com.animecore.clan.ClanSkill;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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
import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * {@code /clan info|choose|list} for players and {@code /clan roll|set|skill|level} for operators (level 2).
 */
public final class ClanCommands {
    private ClanCommands() {}

    private static final DynamicCommandExceptionType UNKNOWN = new DynamicCommandExceptionType(
            id -> Text.translatable("commands.animecore.unknown", String.valueOf(id)));
    private static final SimpleCommandExceptionType CANNOT_CHOOSE = new SimpleCommandExceptionType(
            Text.translatable("commands.animecore.clan.cannot_choose"));

    private static final SuggestionProvider<ServerCommandSource> LINEAGES = (ctx, builder) -> CommandSource.suggestIdentifiers(
            ctx.getSource().getRegistryManager().get(AnimeRegistries.LINEAGE).getIds(), builder);
    private static final SuggestionProvider<ServerCommandSource> SELECTABLE = (ctx, builder) -> CommandSource.suggestIdentifiers(
            ctx.getSource().getRegistryManager().get(AnimeRegistries.LINEAGE).getEntrySet().stream()
                    .filter(e -> e.getValue().selectable() && !e.getValue().fallback()).map(e -> e.getKey().getValue()), builder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("clan")
                .then(literal("info").executes(ctx -> info(ctx, ctx.getSource().getPlayerOrThrow()))
                        .then(argument("player", EntityArgumentType.player()).requires(s -> s.hasPermissionLevel(2))
                                .executes(ctx -> info(ctx, EntityArgumentType.getPlayer(ctx, "player")))))
                .then(literal("list").executes(ClanCommands::list))
                .then(literal("choose").then(argument("lineage", IdentifierArgumentType.identifier()).suggests(SELECTABLE)
                        .executes(ClanCommands::choose)))
                .then(literal("roll").requires(s -> s.hasPermissionLevel(2))
                        .then(argument("players", EntityArgumentType.players()).executes(ClanCommands::roll)))
                .then(literal("set").requires(s -> s.hasPermissionLevel(2))
                        .then(argument("players", EntityArgumentType.players())
                                .then(argument("lineage", IdentifierArgumentType.identifier()).suggests(LINEAGES).executes(ClanCommands::set))))
                .then(literal("skill").requires(s -> s.hasPermissionLevel(2))
                        .then(argument("players", EntityArgumentType.players())
                                .then(argument("experience", IntegerArgumentType.integer(1)).executes(ClanCommands::skill))))
                .then(literal("level").requires(s -> s.hasPermissionLevel(2))
                        .then(argument("players", EntityArgumentType.players())
                                .then(argument("level", IntegerArgumentType.integer(1, 1000)).executes(ClanCommands::level)))));
    }

    private static int info(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {
        for (AnimeModContext mod : AnimeModRegistry.all()) {
            PlayerPowerComponent p = mod.power(player);
            p.lineage().ifPresent(id -> {
                LineageDefinition def = AnimeRegistries.lineage(player.getRegistryManager(), id).orElse(null);
                if (def == null) return;
                int level = p.clanSkillLevel(id);
                ctx.getSource().sendFeedback(() -> Text.translatable("message.animecore.clan.status", def.name(), level,
                        def.maxSkillLevel(), p.clanSkillXp(id), ClanSkill.experienceForNext(level)).formatted(Formatting.GOLD), false);
                long unlocked = def.progression().stream().filter(e -> e.skillLevel() <= level && p.tier() >= e.requiredTier()).count();
                ctx.getSource().sendFeedback(() -> com.animecore.api.AnimeText.tr(p.context().modId(), "commands.animecore.clan.progress",
                        def.grantedAbilities().size() + unlocked, def.grantedAbilities().size() + def.progression().size(),
                        p.clanRerolls()).formatted(Formatting.GRAY), false);
            });
        }
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> ctx) {
        for (AnimeModContext mod : AnimeModRegistry.all()) {
            var all = ClanRollService.lineages(ctx.getSource().getRegistryManager(), mod.modId());
            int total = all.stream().mapToInt(e -> e.getValue().rollable() ? e.getValue().weight() : 0).sum();
            for (Map.Entry<Identifier, LineageDefinition> e : all) {
                LineageDefinition d = e.getValue();
                String chance = d.rollable() && total > 0 ? String.format("%.1f%%", 100f * d.weight() / total) : "-";
                ctx.getSource().sendFeedback(() -> Text.literal(e.getKey() + "  ").append(d.name().copy().styled(s -> s.withColor(d.color())))
                        .append(Text.literal("  " + chance + (d.selectable() ? "" : "  (roll only)") + (d.fallback() ? "  (fallback)" : ""))
                                .formatted(Formatting.GRAY)), false);
            }
        }
        return 1;
    }

    private static int choose(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "lineage");
        AnimeModContext mod = owner(ctx, id);
        if (!ClanRollService.choose(player, mod.power(player), id)) throw CANNOT_CHOOSE.create();
        return 1;
    }

    private static int roll(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
        for (ServerPlayerEntity player : players) {
            for (AnimeModContext mod : AnimeModRegistry.all()) {
                ClanRollService.rollAndAssign(player, mod.power(player), true, ClanAbilityManager.Reason.ROLL).ifPresent(id ->
                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.animecore.lineage", player.getDisplayName(),
                                Text.translatable(com.animecore.platform.Ids.translationKey("lineage", id))), true));
            }
        }
        return players.size();
    }

    private static int set(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(ctx, "lineage");
        AnimeModContext mod = owner(ctx, id);
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
        for (ServerPlayerEntity player : players) {
            ClanAbilityManager.assign(player, mod.power(player), id, ClanAbilityManager.Reason.COMMAND);
        }
        return players.size();
    }

    private static int skill(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int xp = IntegerArgumentType.getInteger(ctx, "experience");
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
        for (ServerPlayerEntity player : players) {
            for (AnimeModContext mod : AnimeModRegistry.all()) ClanAbilityManager.addSkillExperience(player, mod.power(player), xp);
        }
        return players.size();
    }

    private static int level(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level");
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
        for (ServerPlayerEntity player : players) {
            for (AnimeModContext mod : AnimeModRegistry.all()) {
                PlayerPowerComponent p = mod.power(player);
                p.lineage().ifPresent(id -> {
                    p.setClanSkill(id, level, 0);
                    p.revokeClanGrants();                                       // lowering the level re-locks entries
                    ClanAbilityManager.assign(player, p, id, ClanAbilityManager.Reason.COMMAND); // same clan: defaults + evaluate
                    ClanAbilityManager.refreshHeldSkillItems(player, p);
                });
            }
        }
        return players.size();
    }

    private static AnimeModContext owner(CommandContext<ServerCommandSource> ctx, Identifier id) throws CommandSyntaxException {
        LineageDefinition def = AnimeRegistries.lineage(ctx.getSource().getRegistryManager(), id).orElseThrow(() -> UNKNOWN.create(id));
        return AnimeModRegistry.get(def.system()).orElseThrow(() -> UNKNOWN.create(def.system()));
    }
}
