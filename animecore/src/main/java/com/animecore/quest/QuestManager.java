package com.animecore.quest;

import com.animecore.AnimeCore;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.entity.AnimeEntities;
import com.animecore.entity.StoryNpcEntity;
import com.animecore.platform.AnimeNetworking;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.technique.TechniqueTokens;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Story mode: runs {@link QuestDefinition}s for players. One quest per system is active at a time; its objectives run in
 * order. Each step is "prepared" when it starts: talk / defeat targets are spawned as {@link StoryNpcEntity}s (beside the
 * player, or at the encounter site chosen by a travel step), and allies join the player for the whole quest. Progress lives
 * in the player's {@link QuestLog}; everything spawned for a quest disappears when it ends or is abandoned.
 */
public final class QuestManager {
    private QuestManager() {}

    public static final int SITE_RADIUS = 10;

    public static void init() {
        QuestPayloads.register();
        AnimeNetworking.registerServerReceiver(QuestPayloads.Action.ID, (payload, player) -> {
            switch (payload.action()) {
                case "accept" -> accept(player, payload.quest());
                case "abandon" -> abandon(player, payload.quest());
                case "summon" -> resummon(player, payload.quest());
                default -> { }
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(QuestManager::tick);
        ServerLivingEntityEvents.AFTER_DEATH.register(QuestManager::onDeath);
        AnimeCoreEvents.POST_ABILITY_CAST.register((player, entry, context) -> onAbilityUsed(player, entry));
    }

    // =====================================================================================
    // queries
    // =====================================================================================

    private static Optional<AnimeModContext> ctx(QuestDefinition q) { return AnimeModRegistry.get(q.system()); }

    public static Optional<QuestDefinition> quest(ServerPlayerEntity player, Identifier id) {
        return AnimeRegistries.quest(player.getRegistryManager(), id);
    }

    /** Every quest of {@code system}, in story order. */
    public static List<Map.Entry<Identifier, QuestDefinition>> quests(net.minecraft.registry.DynamicRegistryManager rm, String system) {
        List<Map.Entry<Identifier, QuestDefinition>> out = new ArrayList<>();
        rm.getOptional(AnimeRegistries.QUEST).ifPresent(r -> r.getEntrySet().forEach(e -> {
            if (e.getValue().system().equals(system)) out.add(Map.entry(e.getKey().getValue(), e.getValue()));
        }));
        out.sort(Comparator.comparingInt((Map.Entry<Identifier, QuestDefinition> e) -> e.getValue().order())
                .thenComparing(e -> e.getKey().toString()));
        return out;
    }

    public static boolean available(QuestLog log, QuestDefinition q) {
        return q.requires().stream().allMatch(log::isCompleted);
    }

    // =====================================================================================
    // accept / abandon / complete
    // =====================================================================================

    public static boolean accept(ServerPlayerEntity player, Identifier id) {
        QuestDefinition q = quest(player, id).orElse(null);
        if (q == null) return false;
        AnimeModContext ctx = ctx(q).orElse(null);
        if (ctx == null) return false;
        PlayerPowerComponent power = ctx.power(player);
        QuestLog log = power.quests();
        if (log.active().isPresent()) {
            player.sendMessage(Text.translatable("message.animecore.quest.busy").formatted(Formatting.RED), false);
            return false;
        }
        if (log.isCompleted(id) || !available(log, q)) {
            player.sendMessage(Text.translatable("message.animecore.quest.locked").formatted(Formatting.RED), false);
            return false;
        }
        log.start(id);
        player.sendMessage(Text.translatable("message.animecore.quest.started", q.name()).formatted(Formatting.GOLD), false);
        com.animecore.vfx.Juice.card(player, q.system(), com.animecore.vfx.Juice.CardKind.CHAPTER, q.name(), q.arc(), 90);
        if (power.tier() < q.recommendedTier()) {
            player.sendMessage(Text.translatable("message.animecore.quest.underleveled", q.recommendedTier(), power.tier())
                    .formatted(Formatting.YELLOW), false);
        }
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1f, 1f);
        // the giver appears beside the player; the story intro plays when they talk to them (talk objective)
        q.giver().ifPresent(g -> spawn(player, log, id, g, player.getPos().add(look(player, 3))));
        if (q.giver().isEmpty() && !q.intro().isEmpty()) dialogue(player, q.name(), q.intro());
        for (Identifier ally : q.allies()) spawn(player, log, id, ally, player.getPos().add(look(player, -2)).add(player.getRandom().nextInt(3) - 1, 0, 0));
        prepare(player, q, log, id);
        return true;
    }

    public static void abandon(ServerPlayerEntity player, Identifier id) {
        quest(player, id).flatMap(QuestManager::ctx).ifPresent(ctx -> {
            QuestLog log = ctx.power(player).quests();
            if (!log.active().map(id::equals).orElse(false)) return;
            despawnAll(player.getServer(), log);
            log.finish(false);
            player.sendMessage(Text.translatable("message.animecore.quest.abandoned").formatted(Formatting.GRAY), false);
        });
    }

    /** Respawn the current objective's characters (they wandered off, got stuck, the world was reloaded...). */
    public static void resummon(ServerPlayerEntity player, Identifier id) {
        quest(player, id).ifPresent(q -> ctx(q).ifPresent(ctx -> {
            QuestLog log = ctx.power(player).quests();
            if (!log.active().map(id::equals).orElse(false)) return;
            despawnAll(player.getServer(), log);
            for (Identifier ally : q.allies()) spawn(player, log, id, ally, player.getPos().add(look(player, -2)));
            log.setStepPrepared(false);
            prepare(player, q, log, id);
        }));
    }

    /** Admin / test: finish the active quest now (rewards included). */
    public static boolean completeActive(ServerPlayerEntity player, String system) {
        Optional<AnimeModContext> ctx = AnimeModRegistry.get(system);
        if (ctx.isEmpty()) return false;
        QuestLog log = ctx.get().power(player).quests();
        Identifier id = log.active().orElse(null);
        if (id == null) return false;
        QuestDefinition q = quest(player, id).orElse(null);
        if (q == null) return false;
        complete(player, q, log, id);
        return true;
    }

    private static void advance(ServerPlayerEntity player, QuestDefinition q, QuestLog log, Identifier id) {
        log.nextStep();
        if (log.step() >= q.objectives().size()) {
            complete(player, q, log, id);
            return;
        }
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.6f, 1.4f);
        Text next = objectiveText(q.objectives().get(log.step()));
        player.sendMessage(Text.translatable("message.animecore.quest.objective", next).formatted(Formatting.YELLOW), true);
        prepare(player, q, log, id);
    }

    private static void complete(ServerPlayerEntity player, QuestDefinition q, QuestLog log, Identifier id) {
        PlayerPowerComponent power = ctx(q).orElseThrow().power(player);
        if (!q.outro().isEmpty()) {
            Text speaker = q.giver().flatMap(g -> AnimeRegistries.storyNpc(player.getRegistryManager(), g))
                    .map(StoryNpcDefinition::name).orElse(q.name());
            dialogue(player, speaker, q.outro());
        }
        despawnAll(player.getServer(), log);
        log.finish(true);
        QuestDefinition.Rewards r = q.rewards();
        if (r.tier() > power.tier()) power.setTier(r.tier());
        if (r.skillPoints() > 0) power.addSkillPoints(r.skillPoints());
        if (r.experience() > 0) player.addExperience(r.experience());
        for (ItemStack stack : r.items()) {
            ItemStack give = stack.copy();
            if (!player.getInventory().insertStack(give)) player.dropItem(give, false);
        }
        for (Identifier ability : r.abilities()) power.unlockAbility(ability);
        player.sendMessage(Text.translatable("message.animecore.quest.completed", q.name()).formatted(Formatting.GREEN, Formatting.BOLD), false);
        if (lastOfArc(player, q, id)) com.animecore.vfx.Juice.card(player, q.system(), com.animecore.vfx.Juice.CardKind.ARC_END, q.arc(), Text.empty(), 100);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.7f, 1.1f);
        // point at what comes next
        quests(player.getRegistryManager(), q.system()).stream()
                .filter(e -> !log.isCompleted(e.getKey()) && available(log, e.getValue()))
                .findFirst()
                .ifPresent(e -> player.sendMessage(Text.translatable("message.animecore.quest.next", e.getValue().name())
                        .formatted(Formatting.AQUA), false));
    }

    // =====================================================================================
    // steps
    // =====================================================================================

    /** Whether {@code q} closes its arc (no later quest of the same series and arc). */
    private static boolean lastOfArc(ServerPlayerEntity player, QuestDefinition q, Identifier id) {
        if (q.arc().getString().isEmpty()) return false;
        return quests(player.getRegistryManager(), q.system()).stream().noneMatch(e -> !e.getKey().equals(id)
                && e.getValue().series().equals(q.series()) && e.getValue().arc().getString().equals(q.arc().getString())
                && e.getValue().order() > q.order());
    }

    private static void prepare(ServerPlayerEntity player, QuestDefinition q, QuestLog log, Identifier id) {
        if (log.stepPrepared() || log.step() >= q.objectives().size()) return;
        QuestDefinition.Objective o = q.objectives().get(log.step());
        Vec3d anchor = log.site().map(Vec3d::ofBottomCenter).orElse(null);
        switch (o.type()) {
            case TRAVEL -> log.setSite(pickSite(player, Math.max(24, o.count())));
            case TALK -> o.target().ifPresent(t -> {
                if (findLoaded(player, log, t) == null) spawn(player, log, id, t, anchor != null ? anchor : player.getPos().add(look(player, 3)));
            });
            case DEFEAT -> o.target().ifPresent(t -> {
                Vec3d base = anchor != null ? anchor : player.getPos().add(look(player, 12));
                int alive = countLoaded(player, log, t);
                for (int i = alive; i < o.count() - log.progress(); i++) {
                    double a = player.getRandom().nextDouble() * MathHelper.TAU;
                    spawn(player, log, id, t, base.add(Math.cos(a) * (1 + i), 0, Math.sin(a) * (1 + i)));
                }
            });
            default -> { }
        }
        log.setStepPrepared(true);
    }

    private static BlockPos pickSite(ServerPlayerEntity player, int distance) {
        ServerWorld world = player.getServerWorld();
        double a = player.getRandom().nextDouble() * MathHelper.TAU;
        int x = MathHelper.floor(player.getX() + Math.cos(a) * distance);
        int z = MathHelper.floor(player.getZ() + Math.sin(a) * distance);
        world.getChunk(x >> 4, z >> 4);   // make sure the heightmap is generated
        int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y, z);
    }

    private static void tick(MinecraftServer server) {
        if (server.getTicks() % 10 != 0) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) tickPlayer(player, server.getTicks());
    }

    /** One progress check for {@code player} (travel arrival, items held, rank, lost characters). */
    public static void tickPlayer(ServerPlayerEntity player, int serverTicks) {
        MinecraftServer server = player.getServer();
        {
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                QuestLog log = ctx.power(player).quests();
                Identifier id = log.active().orElse(null);
                if (id == null) continue;
                QuestDefinition q = quest(player, id).orElse(null);
                if (q == null) {   // quest removed from the data: drop it
                    log.finish(false);
                    continue;
                }
                if (log.step() >= q.objectives().size()) {
                    complete(player, q, log, id);
                    continue;
                }
                prepare(player, q, log, id);
                QuestDefinition.Objective o = q.objectives().get(log.step());
                switch (o.type()) {
                    case TRAVEL -> log.site().ifPresent(site -> {
                        if (horizontalDistanceSq(player.getPos(), Vec3d.ofBottomCenter(site)) < SITE_RADIUS * SITE_RADIUS) {
                            player.sendMessage(Text.translatable("message.animecore.quest.arrived").formatted(Formatting.GOLD), true);
                            advance(player, q, log, id);
                        }
                    });
                    case COLLECT -> o.target().flatMap(Registries.ITEM::getOrEmpty).ifPresent(item -> {
                        int have = player.getInventory().count(item);
                        if (have != log.progress()) log.setProgress(Math.min(have, o.count()));
                        if (have >= o.count()) advance(player, q, log, id);
                    });
                    case RANK -> {
                        if (ctx.power(player).tier() >= o.count()) advance(player, q, log, id);
                    }
                    case TALK, DEFEAT -> {
                        // characters lost (unloaded far away, killed by something else, world reload): bring them back
                        if (serverTicks % 100 == 0 && o.target().isPresent() && findLoaded(player, log, o.target().get()) == null) {
                            Vec3d anchor = log.site().map(Vec3d::ofBottomCenter).orElse(player.getPos());
                            if (horizontalDistanceSq(player.getPos(), anchor) < 48 * 48) {
                                log.setStepPrepared(false);
                                prepare(player, q, log, id);
                            }
                        }
                    }
                    default -> { }
                }
            }
        }
    }

    // =====================================================================================
    // events
    // =====================================================================================

    /** Right-click on a story character. */
    public static void onTalk(ServerPlayerEntity player, StoryNpcEntity npc) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            QuestLog log = ctx.power(player).quests();
            Identifier id = log.active().orElse(null);
            if (id == null) continue;
            QuestDefinition q = quest(player, id).orElse(null);
            if (q == null || log.step() >= q.objectives().size()) continue;
            QuestDefinition.Objective o = q.objectives().get(log.step());
            if (o.type() == QuestDefinition.ObjectiveType.TALK && o.target().map(npc.npcId()::equals).orElse(false)) {
                List<Text> lines = new ArrayList<>();
                boolean giverIntro = log.step() == 0 && q.giver().map(npc.npcId()::equals).orElse(false);
                if (giverIntro) lines.addAll(q.intro());
                else if (!o.text().getString().isEmpty()) lines.add(o.text());
                npc.definition().ifPresent(d -> { if (lines.isEmpty()) lines.addAll(d.greeting()); });
                if (!lines.isEmpty()) dialogue(player, npc.getDisplayName(), lines);
                advance(player, q, log, id);
                return;
            }
        }
        npc.definition().ifPresent(d -> {
            if (!d.greeting().isEmpty()) dialogue(player, npc.getDisplayName(), List.of(d.greeting().get(player.getRandom().nextInt(d.greeting().size()))));
        });
    }

    /** A story character died: credit the killer (or the player whose ally killed it). */
    public static void onNpcDefeated(StoryNpcEntity npc, DamageSource source) {
        MinecraftServer server = npc.getServer();
        if (server == null) return;
        ServerPlayerEntity player = source.getAttacker() instanceof ServerPlayerEntity p ? p : null;
        if (player == null && source.getAttacker() instanceof StoryNpcEntity ally && ally.owner() != null) {
            player = server.getPlayerManager().getPlayer(ally.owner());
        }
        if (player == null && npc.owner() != null) player = server.getPlayerManager().getPlayer(npc.owner());
        if (player == null) return;
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            QuestLog log = ctx.power(player).quests();
            Identifier id = log.active().orElse(null);
            if (id == null) continue;
            QuestDefinition q = quest(player, id).orElse(null);
            if (q == null || log.step() >= q.objectives().size()) continue;
            QuestDefinition.Objective o = q.objectives().get(log.step());
            if (o.type() == QuestDefinition.ObjectiveType.DEFEAT && o.target().map(npc.npcId()::equals).orElse(false)) {
                log.setProgress(log.progress() + 1);
                if (log.progress() >= o.count()) advance(player, q, log, id);
                else player.sendMessage(Text.translatable("message.animecore.quest.progress", objectiveText(o), log.progress(), o.count()), true);
            }
        }
    }

    private static void onDeath(LivingEntity entity, DamageSource source) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity player) || entity instanceof StoryNpcEntity) return;
        Identifier type = Registries.ENTITY_TYPE.getId(entity.getType());
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            QuestLog log = ctx.power(player).quests();
            Identifier id = log.active().orElse(null);
            if (id == null) continue;
            QuestDefinition q = quest(player, id).orElse(null);
            if (q == null || log.step() >= q.objectives().size()) continue;
            QuestDefinition.Objective o = q.objectives().get(log.step());
            if (o.type() == QuestDefinition.ObjectiveType.KILL && o.target().map(type::equals).orElse(true)) {
                log.setProgress(log.progress() + 1);
                if (log.progress() >= o.count()) advance(player, q, log, id);
                else player.sendMessage(Text.translatable("message.animecore.quest.progress", objectiveText(o), log.progress(), o.count()), true);
            }
        }
    }

    private static void onAbilityUsed(ServerPlayerEntity player, RegistryEntry<AbilityDefinition> entry) {
        Identifier ability = entry.getKey().map(k -> k.getValue()).orElse(null);
        AbilityDefinition def = entry.value();
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            QuestLog log = ctx.power(player).quests();
            Identifier id = log.active().orElse(null);
            if (id == null) continue;
            QuestDefinition q = quest(player, id).orElse(null);
            if (q == null || log.step() >= q.objectives().size()) continue;
            QuestDefinition.Objective o = q.objectives().get(log.step());
            if (o.type() != QuestDefinition.ObjectiveType.USE_ABILITY) continue;
            boolean match = o.target().map(t -> t.equals(ability) || def.affinity().map(t::equals).orElse(false)
                    || t.equals(TechniqueTokens.tokenOf(def))).orElse(true);
            if (!match) continue;
            log.setProgress(log.progress() + 1);
            if (log.progress() >= o.count()) advance(player, q, log, id);
        }
    }

    // =====================================================================================
    // NPC bookkeeping
    // =====================================================================================

    private static @Nullable StoryNpcEntity spawn(ServerPlayerEntity player, QuestLog log, Identifier quest, Identifier npcId, Vec3d at) {
        if (AnimeRegistries.storyNpc(player.getRegistryManager(), npcId).isEmpty()) {
            AnimeCore.LOGGER.warn("Quest {} references unknown story character {}", quest, npcId);
            return null;
        }
        ServerWorld world = player.getServerWorld();
        StoryNpcEntity npc = AnimeEntities.STORY_NPC.create(world);
        if (npc == null) return null;
        BlockPos ground = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlockPos.ofFloored(at));
        double y = Math.abs(ground.getY() - at.y) < 6 ? ground.getY() : at.y;
        npc.refreshPositionAndAngles(at.x, y, at.z, player.getRandom().nextFloat() * 360f, 0f);
        npc.setup(npcId, player, quest);
        world.spawnEntity(npc);
        world.spawnParticles(net.minecraft.particle.ParticleTypes.POOF, at.x, y + 1, at.z, 15, 0.3, 0.6, 0.3, 0.02);
        log.spawned().add(npc.getUuid());
        return npc;
    }

    private static @Nullable StoryNpcEntity findLoaded(ServerPlayerEntity player, QuestLog log, Identifier npcId) {
        for (UUID uuid : log.spawned()) {
            Entity e = player.getServerWorld().getEntity(uuid);
            if (e instanceof StoryNpcEntity npc && npc.isAlive() && npc.npcId().equals(npcId)) return npc;
        }
        return null;
    }

    private static int countLoaded(ServerPlayerEntity player, QuestLog log, Identifier npcId) {
        int n = 0;
        for (UUID uuid : log.spawned()) {
            Entity e = player.getServerWorld().getEntity(uuid);
            if (e instanceof StoryNpcEntity npc && npc.isAlive() && npc.npcId().equals(npcId)) n++;
        }
        return n;
    }

    private static void despawnAll(@Nullable MinecraftServer server, QuestLog log) {
        if (server != null) {
            for (UUID uuid : log.spawned()) {
                for (ServerWorld world : server.getWorlds()) {
                    Entity e = world.getEntity(uuid);
                    if (e != null) {
                        world.spawnParticles(net.minecraft.particle.ParticleTypes.POOF, e.getX(), e.getBodyY(0.5), e.getZ(), 12, 0.3, 0.6, 0.3, 0.02);
                        e.discard();
                    }
                }
            }
        }
        log.spawned().clear();
    }

    /** Called by quest NPCs: false once their player has finished, abandoned or switched quests. */
    public static boolean stillNeeded(StoryNpcEntity npc) {
        if (npc.owner() == null || npc.quest() == null || npc.getServer() == null) return true;
        ServerPlayerEntity player = npc.getServer().getPlayerManager().getPlayer(npc.owner());
        if (player == null) return true;   // owner offline: keep it for when they return
        QuestDefinition q = quest(player, npc.quest()).orElse(null);
        if (q == null) return false;
        return ctx(q).map(c -> c.power(player).quests().active().map(npc.quest()::equals).orElse(false)).orElse(false);
    }

    // =====================================================================================
    // helpers
    // =====================================================================================

    public static Text objectiveText(QuestDefinition.Objective o) {
        if (!o.text().getString().isEmpty()) return o.text();
        return Text.translatable("quest.animecore.objective." + o.type().asString(), o.target().map(Identifier::toString).orElse(""), o.count());
    }

    public static void dialogue(ServerPlayerEntity player, Text speaker, List<Text> lines) {
        if (lines.isEmpty()) return;
        AnimeNetworking.send(player, new QuestPayloads.Dialogue(speaker, List.copyOf(lines)));
    }

    private static Vec3d look(ServerPlayerEntity p, double distance) {
        Vec3d dir = Vec3d.fromPolar(0, p.getYaw());
        return dir.multiply(distance);
    }

    private static double horizontalDistanceSq(Vec3d a, Vec3d b) {
        double dx = a.x - b.x, dz = a.z - b.z;
        return dx * dx + dz * dz;
    }

    // =====================================================================================
    // commands: /quest start|complete|reset|list
    // =====================================================================================

    public static void registerCommands(CommandDispatcher<ServerCommandSource> d) {
        d.register(CommandManager.literal("quest").requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("start").then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("quest", IdentifierArgumentType.identifier())
                                .executes(c -> accept(EntityArgumentType.getPlayer(c, "player"), IdentifierArgumentType.getIdentifier(c, "quest")) ? 1 : 0))))
                .then(CommandManager.literal("complete").then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(c -> {
                            ServerPlayerEntity p = EntityArgumentType.getPlayer(c, "player");
                            int n = 0;
                            for (AnimeModContext ctx : AnimeModRegistry.all()) if (completeActive(p, ctx.modId())) n++;
                            return n;
                        })))
                .then(CommandManager.literal("unlock").then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("quest", IdentifierArgumentType.identifier()).executes(c -> {
                            ServerPlayerEntity p = EntityArgumentType.getPlayer(c, "player");
                            Identifier target = IdentifierArgumentType.getIdentifier(c, "quest");
                            QuestDefinition q = quest(p, target).orElse(null);
                            if (q == null) return 0;
                            QuestLog log = ctx(q).orElseThrow().power(p).quests();
                            int n = 0;   // mark every earlier quest of the system done so the target can be started
                            for (var e : quests(p.getRegistryManager(), q.system())) {
                                if (e.getValue().order() >= q.order()) break;
                                log.forceComplete(e.getKey());
                                n++;
                            }
                            return n;
                        }))))
                .then(CommandManager.literal("reset").then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(c -> {
                            ServerPlayerEntity p = EntityArgumentType.getPlayer(c, "player");
                            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                                QuestLog log = ctx.power(p).quests();
                                despawnAll(p.getServer(), log);
                                log.reset();
                            }
                            return 1;
                        }))));
    }
}
