package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.ward.ChakraWallBlock;
import com.animecore.ward.WardSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Temporary defensive wall in front of the caster (Earth Wall, Wood Release dome, Crystal Ice Mirrors, sand shield).
 * Reuses the engine's self-dissolving chakra wall blocks, so nothing is left behind.
 */
public record RaiseWallAction(ChakraWallBlock.Kind kind, int width, int height, int distance, int durationTicks) implements IAbilityAction {
    private static final Codec<ChakraWallBlock.Kind> KIND = StringIdentifiable.createCodec(ChakraWallBlock.Kind::values);

    public static final MapCodec<RaiseWallAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            KIND.optionalFieldOf("kind", ChakraWallBlock.Kind.STONE).forGetter(RaiseWallAction::kind),
            Codec.intRange(1, 15).optionalFieldOf("width", 5).forGetter(RaiseWallAction::width),
            Codec.intRange(1, 8).optionalFieldOf("height", 3).forGetter(RaiseWallAction::height),
            Codec.intRange(1, 8).optionalFieldOf("distance", 2).forGetter(RaiseWallAction::distance),
            Codec.intRange(20, 2400).optionalFieldOf("duration_ticks", 160).forGetter(RaiseWallAction::durationTicks)
    ).apply(i, RaiseWallAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.RAISE_WALL; }

    @Override
    public void execute(AbilityContext ctx) {
        Direction facing = ctx.caster().getHorizontalFacing();
        BlockPos center = ctx.caster().getBlockPos().offset(facing, distance);
        int w = Math.max(1, Math.round(width * (float) Math.sqrt(ctx.scale())));
        WardSystem.raiseWall(ctx.world(), center, facing.rotateYClockwise(), w, height, kind, (int) (durationTicks * ctx.scale()));
        var axis = facing.rotateYClockwise().getVector();
        com.animecore.vfx.AbilityVfx.emit(ctx, com.animecore.vfx.VfxKind.WALL, net.minecraft.util.math.Vec3d.ofBottomCenter(center),
                new net.minecraft.util.math.Vec3d(axis.getX(), 0, axis.getZ()), w, height);
    }
}
