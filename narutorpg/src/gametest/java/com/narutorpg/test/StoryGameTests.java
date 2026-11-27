package com.narutorpg.test;

import com.animecore.entity.AnimeEntities;
import com.animecore.entity.StoryNpcEntity;
import com.animecore.item.AdminSealItem;
import com.animecore.quest.QuestDefinition;
import com.animecore.quest.QuestLog;
import com.animecore.quest.QuestManager;
import com.animecore.registry.AnimeRegistries;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoAdmin;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.List;

/** Story mode: every Narutopedia arc / movie is a quest chain, played with real story characters. */
@SuppressWarnings("removal")
public final class StoryGameTests implements FabricGameTest {

    private static Identifier id(String path) { return NarutoRPG.CTX.id(path); }

    private static void check(TestContext ctx, boolean ok, String message) {
        if (!ok) com.animecore.AnimeCore.LOGGER.error("StoryGameTests: {}", message);
        ctx.assertTrue(ok, message);
    }

    /** All quests and characters loaded; every reference resolves (characters, previous quests, character jutsu). */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void storyContentResolves(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        var quests = rm.get(AnimeRegistries.QUEST);
        var npcs = rm.get(AnimeRegistries.STORY_NPC);
        ctx.assertTrue(quests.size() >= 600, "only " + quests.size() + " quests");
        ctx.assertTrue(npcs.size() >= 400, "only " + npcs.size() + " story characters");
        int roots = 0;
        for (var e : quests.getEntrySet()) {
            QuestDefinition q = e.getValue();
            if (q.requires().isEmpty()) roots++;
            for (Identifier r : q.requires()) ctx.assertTrue(quests.containsId(r), e.getKey().getValue() + " requires unknown " + r);
            q.giver().ifPresent(g -> ctx.assertTrue(npcs.containsId(g), "unknown giver " + g));
            q.allies().forEach(a -> ctx.assertTrue(npcs.containsId(a), "unknown ally " + a));
            for (QuestDefinition.Objective o : q.objectives()) {
                if (o.type() == QuestDefinition.ObjectiveType.TALK || o.type() == QuestDefinition.ObjectiveType.DEFEAT) {
                    ctx.assertTrue(o.target().map(npcs::containsId).orElse(false), e.getKey().getValue() + " targets unknown " + o.target());
                }
            }
        }
        ctx.assertTrue(roots == 1, "the story should have exactly one starting quest, found " + roots);
        for (var e : npcs.getEntrySet()) {
            for (Identifier a : e.getValue().abilities()) ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), e.getKey().getValue() + " uses unknown " + a);
        }
        ctx.complete();
    }

    /** Graduation (Land of Waves 2): talk to Iruka, travel to the site, defeat Mizuki, get the rewards, unlock the next. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 600)
    public void questPlaysThrough(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        var power = NarutoRPG.CTX.power(p);
        QuestLog log = power.quests();
        log.forceComplete(id("prologue_land_of_waves_01"));
        int sp = power.skillPoints();
        ctx.assertTrue(QuestManager.accept(p, id("prologue_land_of_waves_02")), "accept failed");
        ctx.assertTrue(log.active().map(id("prologue_land_of_waves_02")::equals).orElse(false), "not active");

        StoryNpcEntity iruka = find(ctx, p, id("iruka_umino"));
        ctx.assertTrue(iruka != null, "Iruka did not appear");
        QuestManager.onTalk(p, iruka);
        ctx.assertTrue(log.step() == 1, "talking did not advance: step " + log.step());
        ctx.assertTrue(log.site().isPresent(), "no encounter site");

        BlockPos sitePos = log.site().get();
        Vec3d site = Vec3d.ofBottomCenter(sitePos);
        ctx.getWorld().setChunkForced(sitePos.getX() >> 4, sitePos.getZ() >> 4, true);   // the player loads it in real play
        ctx.waitAndRun(40, () -> {
            p.refreshPositionAndAngles(site.x, site.y, site.z, 0f, 0f);
            QuestManager.tickPlayer(p, 10);
            check(ctx, log.step() == 2, "arriving did not advance: step " + log.step());
            ctx.waitAndRun(60, () -> {   // the site chunk loads its entities over a few dozen ticks
                StoryNpcEntity mizuki = find(ctx, p, id("mizuki_foe_naruto"));
                check(ctx, mizuki != null, "Mizuki did not appear at the site; spawned " + log.spawned().stream()
                        .map(u -> String.valueOf(ctx.getWorld().getEntity(u))).toList() + " site " + sitePos + " player " + p.getPos() + " step " + log.step());
                mizuki.damage(p.getDamageSources().playerAttack(p), 10000f);
                ctx.waitAndRun(2, () -> {
                    ctx.getWorld().setChunkForced(sitePos.getX() >> 4, sitePos.getZ() >> 4, false);
                    check(ctx, log.active().isEmpty() && log.isCompleted(id("prologue_land_of_waves_02")), "quest not completed");
                    check(ctx, power.skillPoints() > sp, "no skill point reward");
                    check(ctx, QuestManager.available(log, QuestManager.quest(p, id("prologue_land_of_waves_03")).orElseThrow()), "next chapter locked");
                    ctx.complete();
                });
            });
        });
    }

    private static StoryNpcEntity find(TestContext ctx, ServerPlayerEntity p, Identifier npc) {
        List<StoryNpcEntity> found = ctx.getWorld().getEntitiesByClass(StoryNpcEntity.class, new Box(p.getBlockPos()).expand(200),
                e -> e.npcId().equals(npc) && e.isAlive());
        return found.isEmpty() ? null : found.get(0);
    }

    /** A story enemy fights with its real jutsu (Deidara's clay bombs hurt a golem). */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void storyEnemyCastsJutsu(TestContext ctx) {
        StoryNpcEntity deidara = AnimeEntities.STORY_NPC.create(ctx.getWorld());
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 0)));
        deidara.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        deidara.setup(id("deidara_foe_shippuden"), null, null);
        ctx.getWorld().spawnEntity(deidara);
        ctx.assertTrue(deidara.getMaxHealth() > 100f, "boss health not applied: " + deidara.getMaxHealth());
        LivingEntity golem = ctx.spawnEntity(EntityType.IRON_GOLEM, 1, 2, 6);
        ((net.minecraft.entity.mob.MobEntity) golem).setAiDisabled(true);
        var abilities = deidara.definition().orElseThrow().abilities();
        Identifier attack = abilities.stream().filter(a -> AnimeRegistries.ability(ctx.getWorld().getRegistryManager(), a)
                .map(e -> e.value().actions().stream().anyMatch(x -> x instanceof com.animecore.ability.action.ProjectileAction))
                .orElse(false)).findFirst().orElse(abilities.get(0));
        ctx.assertTrue(deidara.castAt(golem, attack, 1.5f), "cast failed");
        ctx.waitAndRun(30, () -> {
            ctx.assertTrue(golem.getHealth() < golem.getMaxHealth(), "Deidara's " + attack + " did not hurt the golem");
            ctx.complete();
        });
    }

    /** The Story Seal finishes the current quest (and its arc when sneaking) for testing / admins. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void storySealCompletesQuests(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        var power = NarutoRPG.CTX.power(p);
        var seal = (AdminSealItem) NarutoAdmin.SEALS.get(AdminSealItem.Action.STORY);
        seal.apply(p, power, false, ItemStack.EMPTY);
        ctx.assertTrue(power.quests().isCompleted(id("prologue_land_of_waves_01")), "first quest not completed");
        seal.apply(p, power, true, ItemStack.EMPTY);
        ctx.assertTrue(power.quests().isCompleted(id("prologue_land_of_waves_10")), "sneak should finish the arc");
        ctx.assertTrue(power.tier() >= 1, "arc reward rank");
        ctx.complete();
    }
}
