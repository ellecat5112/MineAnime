package com.jojorpg.test;

import com.animecore.ability.AbilityContext;
import com.animecore.ability.action.IAbilityAction;
import com.animecore.entity.AnimeEntities;
import com.animecore.entity.ManifestationEntity;
import com.animecore.entity.StoryNpcEntity;
import com.animecore.manifestation.CharmManager;
import com.animecore.manifestation.ManifestationService;
import com.animecore.manifestation.TimeStopManager;
import com.animecore.registry.AnimeRegistries;
import com.jojorpg.JojoRPG;
import com.jojorpg.content.JojoItems;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

/** Part 3: Stands manifest as entities and use their lore powers; Part 3 story characters fight with theirs. */
@SuppressWarnings("removal")
public final class StandGameTests implements FabricGameTest {

    private static Identifier id(String path) { return JojoRPG.CTX.id(path); }

    private static void check(TestContext ctx, boolean ok, String message) {
        if (!ok) com.animecore.AnimeCore.LOGGER.error("StandGameTests: {}", message);
        ctx.assertTrue(ok, message);
    }

    private static ServerPlayerEntity standUser(TestContext ctx, String stand, int tier) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        var power = JojoRPG.CTX.power(p);
        power.setTier(tier);
        ManifestationService.assign(p, power, id(stand));
        ManifestationService.setManifested(p, power, true);
        return p;
    }

    /** Runs every action of {@code ability} for {@code caster} (mock players do not go through the input path). */
    private static void cast(ServerPlayerEntity caster, String ability) {
        var entry = AnimeRegistries.ability(caster.getRegistryManager(), id(ability)).orElseThrow(() -> new AssertionError("missing " + ability));
        AbilityContext c = new AbilityContext(caster, entry, JojoRPG.CTX.power(caster), 0f, null);
        for (IAbilityAction a : entry.value().actions()) a.execute(c);
    }

    private static StoryNpcEntity npc(TestContext ctx, String id, BlockPos rel) {
        StoryNpcEntity e = AnimeEntities.STORY_NPC.create(ctx.getWorld());
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(rel));
        e.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        e.setup(id(id), null, null);
        ctx.getWorld().spawnEntity(e);
        return e;
    }

    private static IronGolemEntity golem(TestContext ctx, int x, int z) {
        IronGolemEntity g = ctx.spawnEntity(EntityType.IRON_GOLEM, x, 2, z);
        g.setAiDisabled(true);
        return g;
    }

    /** All 35 Part 3 Stands, their abilities, the 152 Part 3 quests (chained after Battle Tendency) and every Stand user resolve. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void standsResolve(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        var stands = rm.get(AnimeRegistries.MANIFESTATION);
        ctx.assertTrue(stands.size() >= 35, "only " + stands.size() + " Stands");
        for (var e : stands.getEntrySet()) {
            var d = e.getValue();
            ctx.assertTrue(!d.abilities().isEmpty(), e.getKey().getValue() + " has no abilities");
            d.abilities().forEach(a -> ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), e.getKey().getValue() + " uses unknown " + a));
            d.appearance().model().ifPresent(m -> ctx.assertTrue(m.getNamespace().equals(JojoRPG.MOD_ID), "model " + m));
        }
        for (String s : new String[]{"star_platinum", "the_world", "magicians_red", "hierophant_green", "silver_chariot", "hermit_purple",
                "the_fool", "cream", "justice", "lovers", "hanged_man", "emperor", "anubis", "sethan", "osiris", "atum", "horus", "tenore_sax"}) {
            ctx.assertTrue(stands.containsId(id(s)), "missing Stand " + s);
        }
        var quests = rm.get(AnimeRegistries.QUEST);
        long part3 = quests.getIds().stream().filter(i -> i.getPath().startsWith("stardust_crusaders_")).count();
        ctx.assertTrue(part3 == 152, part3 + " Part 3 quests, expected 152 chapters");
        ctx.assertTrue(quests.get(id("stardust_crusaders_114")).requires().contains(id("battle_tendency_113")), "Part 3 must follow Part 2");
        for (var e : rm.get(AnimeRegistries.STORY_NPC).getEntrySet()) {
            e.getValue().manifestation().ifPresent(m -> ctx.assertTrue(stands.containsId(m), e.getKey().getValue() + " has unknown Stand " + m));
        }
        ctx.assertTrue(rm.get(AnimeRegistries.STORY_NPC).get(id("dio_brando_foe_t16")).manifestation().map(id("the_world")::equals).orElse(false),
                "DIO should fight with The World");
        ctx.complete();
    }

    /** The Stand Arrow awakens a Stand, which appears as an entity next to its user. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void arrowAwakensStand(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        var power = JojoRPG.CTX.power(p);
        p.setStackInHand(Hand.MAIN_HAND, new ItemStack(JojoItems.standItem("stand_arrow")));
        p.getMainHandStack().use(p.getWorld(), p, Hand.MAIN_HAND);
        ctx.assertTrue(power.manifestation().isPresent(), "the Arrow gave no Stand");
        ctx.assertTrue(power.manifested(), "the new Stand should manifest");
        ManifestationEntity stand = ManifestationService.standOf(p);   // checked at once: mock players stop counting as alive after a tick
        ctx.assertTrue(stand != null, "no Stand entity");
        ctx.assertTrue(stand.squaredDistanceTo(p) < 9, "the Stand should appear at its user's side");
        ctx.assertTrue(power.castableAbilities().containsAll(stand.definition().orElseThrow().abilities()), "Stand abilities not castable");
        ctx.complete();
    }

    /** Star Platinum's Ora Ora rush (Jotaro, a real story character) pummels what is in front of it. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void oraRushHits(TestContext ctx) {
        StoryNpcEntity jotaro = npc(ctx, "jotaro_kujo_ally_t12", new BlockPos(1, 2, 0));
        IronGolemEntity g = golem(ctx, 1, 2);
        jotaro.setTarget(g);
        ctx.waitAndRun(3, () -> {
            check(ctx, jotaro.castAt(g, id("star_platinum_ora_ora"), 1.5f), "cast failed");
            ManifestationEntity stand = ManifestationService.standOf(jotaro);
            check(ctx, stand != null && stand.definitionId().equals(id("star_platinum")), "Jotaro fought without Star Platinum");
            check(ctx, stand.state() == ManifestationEntity.State.RUSH, "Star Platinum is not rushing: " + stand.state());
            ctx.waitAndRun(40, () -> {
                check(ctx, g.getMaxHealth() - g.getHealth() > 25f, "Ora Ora dealt only " + (g.getMaxHealth() - g.getHealth()));
                ctx.complete();
            });
        });
    }

    /** DIO stops time: mobs freeze in place (velocity kept), DIO keeps moving, so does Star Platinum's user; players freeze. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 160, batchId = "time_stop")   // alone: its 48-block stop would freeze other tests
    public void timeStopFreezes(TestContext ctx) {
        StoryNpcEntity dio = npc(ctx, "dio_brando_foe_t16", new BlockPos(1, 2, 0));
        StoryNpcEntity jotaro = npc(ctx, "jotaro_kujo_ally_t16", new BlockPos(3, 2, 0));
        dio.setAiDisabled(true);      // only the scripted stop: their AI would stop time again (Jotaro has Star Platinum: The World)
        jotaro.setAiDisabled(true);
        ServerPlayerEntity other = ctx.createMockCreativeServerPlayerInWorld();
        other.changeGameMode(GameMode.SURVIVAL);
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(4, 2, 4)));
        other.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        ZombieEntity z = ctx.spawnEntity(EntityType.ZOMBIE, 2, 2, 2);
        dio.castAt(z, id("the_world_time_stop"), 1f);
        ctx.assertTrue(TimeStopManager.isFrozen(z), "the zombie is not frozen");
        ctx.assertTrue(!TimeStopManager.isFrozen(dio), "DIO must move in stopped time");
        ctx.assertTrue(!TimeStopManager.isFrozen(jotaro), "Star Platinum's user should move in stopped time");
        ctx.assertTrue(TimeStopManager.isFrozen(other), "an ordinary player should be frozen");
        z.setVelocity(0, 0.8, 0);
        double y = z.getY();
        ctx.waitAndRun(10, () -> {
            check(ctx, Math.abs(z.getY() - y) < 1e-3, "the zombie moved in stopped time");
            float hp = z.getHealth();
            z.damage(ctx.getWorld().getDamageSources().mobAttack(dio), 2f);
            z.damage(ctx.getWorld().getDamageSources().mobAttack(dio), 2f);
            check(ctx, hp - z.getHealth() >= 3.2f, "repeated blows in stopped time should all land, dealt " + (hp - z.getHealth()));
            ctx.waitAndRun(100, () -> {
                check(ctx, !TimeStopManager.isFrozen(z), "time never resumed");
                ctx.complete();
            });
        });
    }

    /** Tower of Gray flies out and stabs a distant target with Tower Needle. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void remoteStrikeReaches(TestContext ctx) {
        StoryNpcEntity grayFly = npc(ctx, "gray_fly_foe_t10", new BlockPos(1, 2, 0));
        IronGolemEntity g = golem(ctx, 1, 8);
        grayFly.setTarget(g);
        ctx.assertTrue(grayFly.castAt(g, id("tower_of_gray_tower_needle"), 1.5f), "cast failed");
        ctx.waitAndRun(30, () -> {
            check(ctx, g.getHealth() < g.getMaxHealth(), "Tower Needle did not reach the golem");
            check(ctx, ManifestationService.standOf(grayFly) != null, "Gray Fly fought without Tower of Gray");
            ctx.complete();
        });
    }

    /** Cream's void erases the blocks in its path. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void voidErases(TestContext ctx) {
        ServerPlayerEntity p = standUser(ctx, "cream", 12);
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(2, 2, 1)));
        p.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        BlockPos block = ctx.getAbsolutePos(new BlockPos(2, 3, 2));
        ctx.getWorld().setBlockState(block, Blocks.STONE.getDefaultState());
        cast(p, "cream_void_sphere");
        ctx.waitAndRun(10, () -> {
            check(ctx, ctx.getWorld().getBlockState(block).isAir(), "the void did not erase the stone");
            ctx.complete();
        });
    }

    /** Lovers: pain dealt to Steely Dan is felt by the host instead. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 60)
    public void loversLinksPain(TestContext ctx) {
        StoryNpcEntity dan = npc(ctx, "steely_dan_foe_t12", new BlockPos(1, 2, 0));
        IronGolemEntity host = golem(ctx, 1, 4);
        dan.setTarget(host);
        ctx.assertTrue(dan.castAt(host, id("lovers_pain_link"), 1f), "cast failed");
        float danHp = dan.getHealth();
        dan.damage(ctx.getWorld().getDamageSources().generic(), 6f);
        ctx.assertTrue(dan.getHealth() == danHp, "Steely Dan felt the pain himself");
        ctx.assertTrue(host.getHealth() < host.getMaxHealth(), "the host did not feel it");
        ctx.complete();
    }

    /** Justice's fog puppetry turns a mob against the hostile creatures around its controller. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 80)
    public void fogPuppetry(TestContext ctx) {
        ServerPlayerEntity enya = standUser(ctx, "justice", 12);
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 1)));
        enya.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        ZombieEntity puppet = ctx.spawnEntity(EntityType.ZOMBIE, 2, 2, 2);
        HuskEntity enemy = ctx.spawnEntity(EntityType.HUSK, 2, 2, 15);   // outside the 12-block fog, within the puppet's reach
        enemy.setAiDisabled(true);
        cast(enya, "justice_fog_puppetry");
        ctx.assertTrue(CharmManager.isCharmedBy(puppet, enya.getUuid()), "the zombie is not under control: enya " + enya.getPos() + " zombie " + puppet.getPos());
        ctx.waitAndRun(30, () -> {
            LivingEntity t = puppet.getTarget();
            check(ctx, t != enya, "the puppet attacks its controller");
            check(ctx, t == enemy, "the puppet should hunt the hostile husk, target " + t);
            ctx.complete();
        });
    }

    /** Part 3 story characters call out their Stand once they fight. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 60)
    public void storyUserManifests(TestContext ctx) {
        StoryNpcEntity hol = npc(ctx, "hol_horse_foe_t11", new BlockPos(1, 2, 0));
        IronGolemEntity g = golem(ctx, 1, 6);
        hol.setTarget(g);
        ctx.waitAndRun(5, () -> {
            ManifestationEntity stand = ManifestationService.standOf(hol);
            check(ctx, stand != null, "Hol Horse did not call out Emperor");
            check(ctx, stand.definitionId().equals(id("emperor")), "wrong Stand " + stand.definitionId());
            ctx.complete();
        });
    }
}
