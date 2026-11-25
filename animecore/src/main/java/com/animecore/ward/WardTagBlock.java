package com.animecore.ward;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

/** A seal tag lying on the ground: barrier (wall / gate anchor), explosive (proximity mine) or smoke. */
public class WardTagBlock extends BlockWithEntity {
    public enum Mode implements StringIdentifiable {
        BARRIER, EXPLOSIVE, SMOKE;
        @Override public String asString() { return name().toLowerCase(java.util.Locale.ROOT); }
    }

    public static final EnumProperty<Mode> MODE = EnumProperty.of("mode", Mode.class);
    public static final BooleanProperty LINKED = BooleanProperty.of("linked");
    public static final MapCodec<WardTagBlock> CODEC = createCodec(WardTagBlock::new);
    private static final VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 0.5, 13);

    public WardTagBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(MODE, Mode.BARRIER).with(LINKED, false));
    }

    @Override protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE, LINKED);
    }

    @Override protected BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return net.minecraft.util.shape.VoxelShapes.empty();
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), Direction.UP);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world,
                                                   BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !canPlaceAt(state, world, pos)
                ? net.minecraft.block.Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world instanceof ServerWorld server && state.get(LINKED)) {
            WardSystem.collapseGate(server, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WardTagBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, WardBlocks.WARD_TAG_ENTITY, WardTagBlockEntity::serverTick);
    }
}
