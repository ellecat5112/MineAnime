package com.animecore.quest;

import com.animecore.api.AnimeModRegistry;
import com.animecore.entity.AnimeEntities;
import com.animecore.entity.StoryNpcEntity;
import com.animecore.registry.AnimeRegistries;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;

import java.util.ArrayList;
import java.util.List;

/** Spawns roaming story characters ({@link StorySpawnDefinition}) around survival players, a group at a time. */
public final class StorySpawner {
    private StorySpawner() {}

    public static final int INTERVAL = 200;

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(StorySpawner::tick);
    }

    private static void tick(MinecraftServer server) {
        if (server.getTicks() % INTERVAL != 0) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.isSpectator() || player.isCreative()) continue;
            ServerWorld world = player.getServerWorld();
            if (!world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) || world.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) continue;
            trySpawn(world, player, world.getRandom());
        }
    }

    /** One spawn attempt near {@code player}; returns how many characters appeared. */
    public static int trySpawn(ServerWorld world, ServerPlayerEntity player, Random random) {
        var reg = world.getRegistryManager().getOptional(AnimeRegistries.STORY_SPAWN).orElse(null);
        if (reg == null) return 0;
        List<StorySpawnDefinition> options = new ArrayList<>();
        int total = 0;
        for (StorySpawnDefinition d : reg) {
            if (!d.dimensions().contains(world.getRegistryKey().getValue())) continue;
            if (d.nightOnly() && world.isDay()) continue;
            int tier = AnimeModRegistry.get(d.system()).map(c -> c.power(player).tier()).orElse(0);
            if (tier < d.minTier()) continue;
            options.add(d);
            total += d.weight();
        }
        if (options.isEmpty() || random.nextInt(3) != 0) return 0;
        int pick = random.nextInt(total);
        StorySpawnDefinition chosen = options.get(0);
        for (StorySpawnDefinition d : options) {
            pick -= d.weight();
            if (pick < 0) { chosen = d; break; }
        }
        final StorySpawnDefinition def = chosen;
        int nearby = world.getEntitiesByClass(StoryNpcEntity.class, new Box(player.getBlockPos()).expand(64), e -> e.npcId().equals(def.npc())).size();
        if (nearby >= def.maxNearby()) return 0;
        double a = random.nextDouble() * MathHelper.TAU;
        int dist = 24 + random.nextInt(24);
        int x = MathHelper.floor(player.getX() + Math.cos(a) * dist), z = MathHelper.floor(player.getZ() + Math.sin(a) * dist);
        if (!world.isChunkLoaded(x >> 4, z >> 4)) return 0;
        BlockPos ground = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
        if (world.getLightLevel(ground) > def.maxLight() || !world.getBlockState(ground.down()).isSolidBlock(world, ground.down())) return 0;
        int min = def.group().isEmpty() ? 1 : def.group().get(0), max = def.group().size() > 1 ? def.group().get(1) : min;
        int n = Math.min(def.maxNearby() - nearby, min + random.nextInt(Math.max(1, max - min + 1)));
        int spawned = 0;
        for (int i = 0; i < n; i++) {
            StoryNpcEntity npc = AnimeEntities.STORY_NPC.create(world);
            if (npc == null) break;
            npc.refreshPositionAndAngles(ground.getX() + 0.5 + random.nextInt(3) - 1, ground.getY(), ground.getZ() + 0.5 + random.nextInt(3) - 1,
                    random.nextFloat() * 360f, 0f);
            npc.setup(def.npc(), null, null);
            npc.setAmbient();
            world.spawnEntity(npc);
            spawned++;
        }
        return spawned;
    }
}
