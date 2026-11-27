package com.animecore.manifestation;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.registry.AnimeRegistries;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * {@code /manifestation set <player> <id>} (ops) gives a player a manifestation (its system's component),
 * {@code /manifestation toggle} calls yours out / back, {@code /manifestation clear <player>} removes them all.
 */
public final class ManifestationCommands {
    private ManifestationCommands() {}

    public static void register(CommandDispatcher<ServerCommandSource> d) {
        d.register(CommandManager.literal("manifestation")
                .then(CommandManager.literal("toggle").executes(c -> {
                    ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
                    for (AnimeModContext ctx : AnimeModRegistry.all()) {
                        var power = ctx.maybePower(p).orElse(null);
                        if (power != null && power.manifestation().isPresent()) {
                            ManifestationService.toggle(p, power);
                            return 1;
                        }
                    }
                    return 0;
                }))
                .then(CommandManager.literal("set").requires(s -> s.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("id", IdentifierArgumentType.identifier())
                                        .suggests((c, b) -> {
                                            c.getSource().getRegistryManager().get(AnimeRegistries.MANIFESTATION).getIds().forEach(i -> b.suggest(i.toString()));
                                            return b.buildFuture();
                                        })
                                        .executes(ManifestationCommands::set))))
                .then(CommandManager.literal("clear").requires(s -> s.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player()).executes(c -> {
                            ServerPlayerEntity p = EntityArgumentType.getPlayer(c, "player");
                            for (AnimeModContext ctx : AnimeModRegistry.all()) ctx.maybePower(p).ifPresent(power -> ManifestationService.assign(p, power, null));
                            return 1;
                        }))));
    }

    private static int set(CommandContext<ServerCommandSource> c) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity p = EntityArgumentType.getPlayer(c, "player");
        Identifier id = IdentifierArgumentType.getIdentifier(c, "id");
        var def = AnimeRegistries.manifestation(c.getSource().getRegistryManager(), id).orElse(null);
        var ctx = def == null ? null : AnimeModRegistry.get(def.system()).orElse(null);
        if (ctx == null) {
            c.getSource().sendError(Text.translatable("commands.animecore.manifestation.none", id.toString()));
            return 0;
        }
        var power = ctx.power(p);
        ManifestationService.assign(p, power, id);
        ManifestationService.setManifested(p, power, true);
        c.getSource().sendFeedback(() -> Text.translatable("commands.animecore.manifestation.set", p.getDisplayName(), def.name()), true);
        return 1;
    }
}
