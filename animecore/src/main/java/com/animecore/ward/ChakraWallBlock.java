package com.animecore.ward;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/** Temporary wall raised by barrier tags / wall moves. Vanishes on its scheduled tick. Drops nothing. */
public class ChakraWallBlock extends TransparentBlock {
    public enum Kind implements StringIdentifiable {
        BARRIER, CRYSTAL, STONE, MIRROR;
        @Override public String asString() { return name().toLowerCase(java.util.Locale.ROOT); }
    }

    public static final EnumProperty<Kind> KIND = EnumProperty.of("kind", Kind.class);
    public static final MapCodec<ChakraWallBlock> CODEC = createCodec(ChakraWallBlock::new);

    public ChakraWallBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(KIND, Kind.BARRIER));
    }

    @Override protected MapCodec<? extends TransparentBlock> getCodec() { return CODEC; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(KIND);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.removeBlock(pos, false);
    }
}
