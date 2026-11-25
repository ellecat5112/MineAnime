package com.animecore.ward;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Barrier tag logic.
 * <ul>
 *   <li>One tag: a 5x3 temporary wall rises just beyond it (perpendicular to the caster's view) and fades.</li>
 *   <li>Three or more tags placed in sequence (each within {@link #LINK_RANGE} of the previous, within
 *       {@link #SEQUENCE_WINDOW} ticks) link into a lasting {@link BarrierGateBlock} wall connecting them.
 *       The gate stands until any of its tags is broken.</li>
 * </ul>
 */
public final class WardSystem {
    public static final int LINK_RANGE = 10;
    public static final int SEQUENCE_WINDOW = 600;
    public static final int GATE_HEIGHT = 4;
    public static final int MIN_GATE_TAGS = 3;

    private record Placed(BlockPos pos, long time, World world) {}

    private static final Map<UUID, List<Placed>> SEQUENCES = new HashMap<>();

    private WardSystem() {}

    /** @return true if a tag was placed */
    public static boolean placeTag(ServerWorld world, PlayerEntity player, BlockPos pos, WardTagBlock.Mode mode, int wallDuration) {
        if (!world.getBlockState(pos).isReplaceable()) return false;
        BlockState state = WardBlocks.WARD_TAG.getDefaultState().with(WardTagBlock.MODE, mode);
        if (!state.canPlaceAt(world, pos)) return false;
        world.setBlockState(pos, state);
        if (world.getBlockEntity(pos) instanceof WardTagBlockEntity be) be.setOwner(player.getUuid());
        world.playSound(null, pos, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1f, 1.2f);
        if (mode != WardTagBlock.Mode.BARRIER) return true;

        List<Placed> seq = SEQUENCES.computeIfAbsent(player.getUuid(), u -> new ArrayList<>());
        long now = world.getTime();
        seq.removeIf(p -> p.world != world || now - p.time > SEQUENCE_WINDOW
                || !(world.getBlockState(p.pos).getBlock() instanceof WardTagBlock));
        if (!seq.isEmpty() && !seq.get(seq.size() - 1).pos.isWithinDistance(pos, LINK_RANGE)) seq.clear();
        seq.add(new Placed(pos, now, world));

        if (seq.size() >= MIN_GATE_TAGS) {
            buildGate(world, seq.stream().map(Placed::pos).toList());
            player.sendMessage(Text.translatable("message.animecore.ward.gate", seq.size()).formatted(Formatting.LIGHT_PURPLE), true);
        } else {
            Direction facing = player.getHorizontalFacing();
            raiseWall(world, pos.offset(facing), facing.rotateYClockwise(), 5, 3, ChakraWallBlock.Kind.BARRIER, wallDuration);
            player.sendMessage(Text.translatable("message.animecore.ward.wall", MIN_GATE_TAGS - seq.size()).formatted(Formatting.AQUA), true);
        }
        return true;
    }

    /** Temporary wall centred on {@code center}, extending along {@code along}. */
    public static int raiseWall(ServerWorld world, BlockPos center, Direction along, int width, int height, ChakraWallBlock.Kind kind, int duration) {
        int placed = 0;
        BlockState wall = WardBlocks.CHAKRA_WALL.getDefaultState().with(ChakraWallBlock.KIND, kind);
        for (int w = -width / 2; w <= width / 2; w++) {
            for (int h = 0; h < height; h++) {
                BlockPos p = center.offset(along, w).up(h);
                if (!world.getBlockState(p).isReplaceable()) continue;
                world.setBlockState(p, wall);
                world.scheduleBlockTick(p, WardBlocks.CHAKRA_WALL, Math.max(20, duration + world.random.nextInt(10)));
                placed++;
            }
        }
        if (placed > 0) world.playSound(null, center, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 1f, 0.8f);
        return placed;
    }

    private static void buildGate(ServerWorld world, List<BlockPos> tags) {
        BlockState gate = WardBlocks.BARRIER_GATE.getDefaultState();
        for (BlockPos tag : tags) {
            BlockState s = world.getBlockState(tag);
            if (s.getBlock() instanceof WardTagBlock) world.setBlockState(tag, s.with(WardTagBlock.LINKED, true), 2);
        }
        int baseY = tags.stream().mapToInt(BlockPos::getY).max().orElse(tags.get(0).getY());
        for (int i = 0; i < tags.size() - 1; i++) {
            BlockPos a = tags.get(i), b = tags.get(i + 1);
            int steps = Math.max(Math.abs(b.getX() - a.getX()), Math.abs(b.getZ() - a.getZ()));
            for (int s = 0; s <= steps; s++) {
                double t = steps == 0 ? 0 : s / (double) steps;
                int x = MathHelper.floor(MathHelper.lerp(t, a.getX(), b.getX()) + 0.5);
                int z = MathHelper.floor(MathHelper.lerp(t, a.getZ(), b.getZ()) + 0.5);
                for (int h = 0; h < GATE_HEIGHT; h++) {
                    BlockPos p = new BlockPos(x, baseY + h, z);
                    BlockState cur = world.getBlockState(p);
                    if (cur.getBlock() instanceof WardTagBlock) continue;
                    if (cur.isReplaceable() || cur.isOf(WardBlocks.CHAKRA_WALL)) world.setBlockState(p, gate);
                }
            }
        }
        world.playSound(null, tags.get(tags.size() - 1), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1.2f);
    }

    /** A linked tag was removed: tear down every gate block connected to it. */
    public static void collapseGate(ServerWorld world, BlockPos tagPos) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        for (int dy = 0; dy <= GATE_HEIGHT; dy++) for (Direction d : Direction.values()) queue.add(tagPos.up(dy).offset(d));
        queue.add(tagPos.up());
        int removed = 0;
        while (!queue.isEmpty() && removed < 4096) {
            BlockPos p = queue.poll();
            if (!seen.add(p) || !p.isWithinDistance(tagPos, 64)) continue;
            BlockState s = world.getBlockState(p);
            if (s.isOf(WardBlocks.BARRIER_GATE)) {
                world.removeBlock(p, false);
                removed++;
                for (Direction d : Direction.values()) queue.add(p.offset(d));
                for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) queue.add(p.add(dx, 0, dz));
            } else if (s.getBlock() instanceof WardTagBlock && s.get(WardTagBlock.LINKED)) {
                world.setBlockState(p, s.with(WardTagBlock.LINKED, false), 2);
            }
        }
        if (removed > 0) world.playSound(null, tagPos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
    }
}
