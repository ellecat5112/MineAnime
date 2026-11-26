package com.animecore.command;

import com.animecore.vfx.AbilityVfx;
import com.animecore.vfx.ScreenEffect;
import com.animecore.vfx.VfxKind;
import com.animecore.vfx.VfxStyle;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/** {@code /vfx play <kind> <style>} and {@code /vfx screen <effect> [style]}: preview technique effects (op level 2). */
public final class VfxCommands {
    private VfxCommands() {}

    private static SuggestionProvider<ServerCommandSource> names(Enum<?>[] values) {
        return (c, b) -> CommandSource.suggestMatching(Arrays.stream(values).map(v -> v.name().toLowerCase(Locale.ROOT)), b);
    }

    public static void register(CommandDispatcher<ServerCommandSource> d) {
        d.register(literal("vfx").requires(s -> s.hasPermissionLevel(2))
                .then(literal("play").then(argument("kind", StringArgumentType.word()).suggests(names(VfxKind.values()))
                        .then(argument("style", StringArgumentType.word()).suggests(names(VfxStyle.values())).executes(VfxCommands::play))))
                .then(literal("genjutsu").then(argument("ability", net.minecraft.command.argument.IdentifierArgumentType.identifier())
                        .suggests((c, bld) -> CommandSource.suggestIdentifiers(c.getSource().getRegistryManager().get(com.animecore.registry.AnimeRegistries.ABILITY)
                                .getEntrySet().stream().filter(e -> e.getValue().visuals().illusion().isPresent()).map(e -> e.getKey().getValue()), bld))
                        .executes(VfxCommands::genjutsu)))
                .then(literal("screen").then(argument("effect", StringArgumentType.word()).suggests(names(ScreenEffect.values()))
                        .executes(c -> screen(c, VfxStyle.ILLUSION))
                        .then(argument("style", StringArgumentType.word()).suggests(names(VfxStyle.values()))
                                .executes(c -> screen(c, VfxStyle.valueOf(StringArgumentType.getString(c, "style").toUpperCase(Locale.ROOT))))))));
    }

    private static int play(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
        VfxKind kind = VfxKind.valueOf(StringArgumentType.getString(c, "kind").toUpperCase(Locale.ROOT));
        VfxStyle style = VfxStyle.valueOf(StringArgumentType.getString(c, "style").toUpperCase(Locale.ROOT));
        Vec3d look = p.getRotationVec(1f);
        Vec3d front = p.getPos().add(look.x * 3, 1, look.z * 3);
        switch (kind) {
            case BEAM -> AbilityVfx.emit(p.getServerWorld(), p, kind, style, style.mid(), p.getEyePos().add(look), look, 16f, 0.5f);
            case MARK -> AbilityVfx.emit(p.getServerWorld(), p, kind, style, style.mid(), front, Vec3d.ZERO, 100f, p.getId());
            case BUFF, CAST -> AbilityVfx.emit(p.getServerWorld(), p, kind, style, style.mid(), p.getPos().add(look), look, 2f, 0f);
            case WALL -> AbilityVfx.emit(p.getServerWorld(), p, kind, style, style.mid(), front.add(0, -1, 0), new Vec3d(-look.z, 0, look.x), 5f, 3f);
            case AREA -> AbilityVfx.emit(p.getServerWorld(), p, kind, style, style.mid(), p.getPos().add(0, 0.9, 0), look, 9f, 70f);
            default -> AbilityVfx.emit(p.getServerWorld(), p, kind, style, style.mid(), front, look, 4f, 0f);
        }
        return 1;
    }

    /** Shows a technique's own illusion: the victim's vision on your screen and its world effect around you. */
    private static int genjutsu(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        ServerPlayerEntity p = c.getSource().getPlayerOrThrow();
        var id = net.minecraft.command.argument.IdentifierArgumentType.getIdentifier(c, "ability");
        var def = com.animecore.registry.AnimeRegistries.ability(p.getRegistryManager(), id).map(e -> e.value()).orElse(null);
        if (def == null || def.visuals().illusion().isEmpty()) return 0;
        var pr = def.visuals().illusion().get();
        AbilityVfx.screen(p, ScreenEffect.ILLUSION, VfxStyle.ILLUSION, pr.color(), 160, pr);
        AbilityVfx.emit(p.getServerWorld(), p, VfxKind.ILLUSION, VfxStyle.ILLUSION, pr.color(), p.getPos().add(0, 0.9, 0),
                new Vec3d(pr.pattern().ordinal(), pr.count(), pr.speed()), 8f, pr.color2());
        c.getSource().sendFeedback(() -> def.name().copy().append(" (" + pr.pattern().asString() + ")"), false);
        return 1;
    }

    private static int screen(CommandContext<ServerCommandSource> c, VfxStyle style) throws CommandSyntaxException {
        ScreenEffect effect = ScreenEffect.valueOf(StringArgumentType.getString(c, "effect").toUpperCase(Locale.ROOT));
        AbilityVfx.screen(c.getSource().getPlayerOrThrow(), effect, style, style == VfxStyle.ILLUSION ? 0xC62828 : style.mid(), 160);
        return 1;
    }
}
