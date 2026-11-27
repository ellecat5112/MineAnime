package com.jojorpg.test;

import com.animecore.api.AnimeCoreApi;
import com.animecore.api.AnimeText;
import com.animecore.clan.ClanTraitHooks;
import com.animecore.clan.ClanTraits;
import com.animecore.entity.AnimeEntities;
import com.animecore.entity.StoryNpcEntity;
import com.animecore.item.AwakeningItem;
import com.animecore.item.LineageAwakeningItem;
import com.animecore.quest.QuestDefinition;
import com.animecore.quest.QuestLog;
import com.animecore.quest.QuestManager;
import com.animecore.registry.AnimeRegistries;
import com.animecore.transformation.TransformationService;
import com.jojorpg.JojoRPG;
import com.jojorpg.content.JojoItems;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.List;

/** JoJoRPG: fighting styles, the Ripple vs the undead, forms and the Part 1-2 story mode. */
@SuppressWarnings("removal")
public final class JojoGameTests implements FabricGameTest {

    private static Identifier id(String path) { return JojoRPG.CTX.id(path); }

    private static void check(TestContext ctx, boolean ok, String message) {
        if (!ok) com.animecore.AnimeCore.LOGGER.error("JojoGameTests: {}", message);
        ctx.assertTrue(ok, message);
    }

    private static ServerPlayerEntity player(TestContext ctx, int tier) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        JojoRPG.CTX.power(p).setTier(tier);
        return p;
    }

    private static void use(ServerPlayerEntity p, String item) {
        p.setStackInHand(Hand.MAIN_HAND, new ItemStack(JojoItems.awakening(item)));
        p.getMainHandStack().use(p.getWorld(), p, Hand.MAIN_HAND);
    }

    /** Every style, technique, form, quest, character and spawn loads and every reference resolves. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void contentResolves(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        long abilities = rm.get(AnimeRegistries.ABILITY).getIds().stream().filter(i -> i.getNamespace().equals(JojoRPG.MOD_ID)).count();
        ctx.assertTrue(abilities >= 80, "only " + abilities + " techniques");
        for (String s : new String[]{"hamon", "vampire", "pillar_man", "cyborg", "brawler"}) {
            var lineage = AnimeRegistries.lineage(rm, id(s)).orElseThrow(() -> new AssertionError("missing style " + s));
            lineage.grantedAbilities().forEach(a -> ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), s + " grants unknown " + a));
            lineage.progression().forEach(e -> {
                e.ability().ifPresent(a -> ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), s + " unlocks unknown " + a));
                e.transformation().ifPresent(f -> ctx.assertTrue(AnimeRegistries.transformation(rm, f).isPresent(), s + " unlocks unknown form " + f));
            });
        }
        for (String f : new String[]{"wind_mode", "flame_mode", "light_mode", "ultimate_life_form"}) {
            var form = AnimeRegistries.transformation(rm, id(f)).orElseThrow(() -> new AssertionError("missing form " + f)).value();
            form.grantedAbilities().forEach(a -> ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), f + " grants unknown " + a));
        }
        for (String a : new String[]{"zoom_punch", "sunlight_yellow_overdrive", "space_ripper_stingy_eyes", "divine_sandstorm",
                "erratic_blaze_king", "light_blades", "ultraviolet_radiation_apparatus", "clacker_volley", "bubble_launcher"}) {
            ctx.assertTrue(AnimeRegistries.ability(rm, id(a)).isPresent(), "missing technique " + a);
        }
        JojoItems.AWAKENING.forEach((name, item) -> {
            if (item instanceof LineageAwakeningItem l) ctx.assertTrue(AnimeRegistries.lineage(rm, l.lineage()).isPresent(), name + " -> " + l.lineage());
            if (item instanceof AwakeningItem a) ctx.assertTrue(AnimeRegistries.transformation(rm, a.form()).isPresent(), name + " -> " + a.form());
        });

        var quests = rm.get(AnimeRegistries.QUEST);
        var npcs = rm.get(AnimeRegistries.STORY_NPC);
        long jojoQuests = quests.getIds().stream().filter(i -> i.getNamespace().equals(JojoRPG.MOD_ID)).count();
        ctx.assertTrue(jojoQuests == 113 + 152, jojoQuests + " quests, expected one per chapter of Parts 1-3 (265)");
        int roots = 0;
        for (var e : quests.getEntrySet()) {
            if (!e.getKey().getValue().getNamespace().equals(JojoRPG.MOD_ID)) continue;
            QuestDefinition q = e.getValue();
            if (q.requires().isEmpty()) roots++;
            q.requires().forEach(r -> ctx.assertTrue(quests.containsId(r), e.getKey().getValue() + " requires unknown " + r));
            q.giver().ifPresent(g -> ctx.assertTrue(npcs.containsId(g), "unknown giver " + g));
            q.allies().forEach(a -> ctx.assertTrue(npcs.containsId(a), "unknown ally " + a));
            for (QuestDefinition.Objective o : q.objectives()) {
                if (o.type() == QuestDefinition.ObjectiveType.TALK || o.type() == QuestDefinition.ObjectiveType.DEFEAT) {
                    ctx.assertTrue(o.target().map(npcs::containsId).orElse(false), e.getKey().getValue() + " targets unknown " + o.target());
                }
            }
        }
        ctx.assertTrue(roots == 1, "Part 1 chapter 1 should be the only starting quest, found " + roots);
        for (var e : npcs.getEntrySet()) {
            e.getValue().abilities().forEach(a -> ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), e.getKey().getValue() + " uses unknown " + a));
        }
        var spawns = rm.get(AnimeRegistries.STORY_SPAWN);
        ctx.assertTrue(spawns.size() >= 4, "night encounters: " + spawns.size());
        spawns.forEach(s -> ctx.assertTrue(npcs.containsId(s.npc()), "spawn of unknown " + s.npc()));
        ctx.complete();
    }

    /** The Stone Mask makes you a vampire: stronger, undead, burnt by the sun; drinking blood heals you. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void stoneMaskMakesVampire(TestContext ctx) {
        ServerPlayerEntity p = player(ctx, 3);
        var power = JojoRPG.CTX.power(p);
        double before = p.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        use(p, "stone_mask");
        ctx.assertTrue(power.lineage().map(id("vampire")::equals).orElse(false), "not a vampire: " + power.lineage());
        power.forceRefreshTraits(p);
        ctx.assertTrue(p.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) > before, "no vampire strength");
        ctx.assertTrue(AnimeCoreApi.isUndead(p), "a vampire should count as undead");
        var traits = ClanTraitHooks.traits(p, power);
        ctx.assertTrue(traits.stream().anyMatch(t -> t instanceof ClanTraits.SunlightWeakness), "no sunlight weakness");

        // drinking blood: hitting a living target heals
        IronGolemEntity golem = ctx.spawnEntity(EntityType.IRON_GOLEM, 1, 2, 1);
        golem.setAiDisabled(true);
        p.setHealth(10f);
        golem.damage(ctx.getWorld().getDamageSources().playerAttack(p), 8f);
        ctx.assertTrue(p.getHealth() > 10f, "lifesteal did not heal: " + p.getHealth());

        ctx.complete();
    }

    /** The Ripple sears the undead: a Hamon user's technique does far more to a zombie than to a golem. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void rippleSearsUndead(TestContext ctx) {
        ServerPlayerEntity p = player(ctx, 3);
        var power = JojoRPG.CTX.power(p);
        use(p, "breathing_mask");
        ctx.assertTrue(power.lineage().map(id("hamon")::equals).orElse(false), "not a Ripple user: " + power.lineage());
        var world = ctx.getWorld();
        ZombieEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 1, 2, 1);
        zombie.setAiDisabled(true);
        zombie.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(200);
        zombie.setHealth(200f);
        IronGolemEntity golem = ctx.spawnEntity(EntityType.IRON_GOLEM, 3, 2, 1);
        golem.setAiDisabled(true);
        Identifier ripple = id("ripple");
        com.animecore.ability.AbilityDamage.apply(world, p, p, zombie, 4f, ripple, 0, 0f, Vec3d.ZERO);
        com.animecore.ability.AbilityDamage.apply(world, p, p, golem, 4f, ripple, 0, 0f, Vec3d.ZERO);
        float toZombie = 200f - zombie.getHealth();
        float toGolem = golem.getMaxHealth() - golem.getHealth();
        ctx.assertTrue(toZombie > toGolem * 2f, "ripple vs zombie " + toZombie + ", vs golem " + toGolem);
        // breathing refills the Ripple
        var pool = power.pool(id("ripple")).orElseThrow();
        power.setEnergy(id("ripple"), 0f);
        ClanTraitHooks.traits(p, power).stream().filter(t -> t instanceof ClanTraits.Breathing).forEach(t -> t.tick(p, power));
        ctx.assertTrue(pool.current() > 0f, "breathing did not refill the Ripple");
        ctx.complete();
    }

    /** Pillar Man forms need the style; the Red Stone of Aja makes the Ultimate Life Form, immune to the sun. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void redStoneMakesUltimateLifeForm(TestContext ctx) {
        ServerPlayerEntity p = player(ctx, 10);
        var power = JojoRPG.CTX.power(p);
        power.unlockTransformation(id("ultimate_life_form"));
        ctx.assertTrue(!TransformationService.activate(p, power, id("ultimate_life_form")), "a human should not become the Ultimate Life Form");
        use(p, "pillar_remains");
        ctx.assertTrue(power.lineage().map(id("pillar_man")::equals).orElse(false), "not a Pillar Man: " + power.lineage());
        use(p, "red_stone_of_aja");
        ctx.assertTrue(power.unlockedTransformations().contains(id("ultimate_life_form")), "Red Stone did not awaken the form");
        power.pool(id("blood")).ifPresent(b -> power.setEnergy(id("blood"), 100f));
        ctx.assertTrue(TransformationService.activate(p, power, id("ultimate_life_form")), "activate failed");
        var traits = ClanTraitHooks.traits(p, power);
        ctx.assertTrue(traits.stream().anyMatch(t -> t instanceof ClanTraits.SunlightImmunity), "Ultimate Life Form should not fear the sun");
        ctx.complete();
    }

    /** Story characters: vampire / zombie enemies are undead and fight with their techniques. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void storyVampireCasts(TestContext ctx) {
        StoryNpcEntity dio = AnimeEntities.STORY_NPC.create(ctx.getWorld());
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 0)));
        dio.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        dio.setup(id("dio_brando_foe_t5"), null, null);
        ctx.getWorld().spawnEntity(dio);
        ctx.assertTrue(AnimeCoreApi.isUndead(dio), "vampire Dio should be undead");
        ctx.assertTrue(dio.getMaxHealth() > 150f, "boss health not applied: " + dio.getMaxHealth());
        IronGolemEntity golem = ctx.spawnEntity(EntityType.IRON_GOLEM, 1, 2, 5);
        golem.setAiDisabled(true);
        ctx.assertTrue(dio.castAt(golem, id("space_ripper_stingy_eyes"), 1.5f), "cast failed");
        ctx.waitAndRun(20, () -> {
            ctx.assertTrue(golem.getHealth() < golem.getMaxHealth(), "Space Ripper Stingy Eyes did not hurt the golem");
            ctx.complete();
        });
    }

    /** Vampires burn in the sun (a story vampire standing in daylight catches fire). */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void vampiresBurnInSunlight(TestContext ctx) {
        ctx.getWorld().setTimeOfDay(6000);
        ctx.getWorld().setWeather(6000, 0, false, false);
        StoryNpcEntity vampire = AnimeEntities.STORY_NPC.create(ctx.getWorld());
        BlockPos top = ctx.getWorld().getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, ctx.getAbsolutePos(new BlockPos(1, 2, 1)));
        vampire.refreshPositionAndAngles(top.getX() + 0.5, top.getY(), top.getZ() + 0.5, 0f, 0f);
        vampire.setup(id("vampire_battle_tendency"), null, null);
        ctx.getWorld().spawnEntity(vampire);
        ctx.waitAndRun(45, () -> {
            check(ctx, !ClanTraits.SunlightWeakness.inSunlight(vampire) || vampire.isOnFire() || vampire.getHealth() < vampire.getMaxHealth(),
                    "the vampire did not burn in the sun at " + vampire.getBlockPos());
            check(ctx, ClanTraits.SunlightWeakness.inSunlight(vampire), "test setup: not in sunlight at " + vampire.getBlockPos());
            ctx.complete();
        });
    }

    /** JoJo wording: engine messages read as fighting styles, not as the default lineage text. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void jojoWording(TestContext ctx) {
        String key = AnimeText.key(JojoRPG.MOD_ID, "message.animecore.mastery.awakened");
        ctx.assertTrue(key.startsWith("message." + JojoRPG.MOD_ID + "."), "not overridden: " + key);
        ctx.assertTrue(AnimeText.key(null, "message.animecore.mastery.awakened").equals("message.animecore.mastery.awakened"), "engine key changed");
        ctx.complete();
    }

    /** Chapter 1: talk to Jonathan, travel, beat young Dio; the Ogre Street gloves make you a Brawler-to-be. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 400)
    public void firstChapterPlaysThrough(TestContext ctx) {
        ServerPlayerEntity p = player(ctx, 1);
        var power = JojoRPG.CTX.power(p);
        QuestLog log = power.quests();
        ctx.assertTrue(QuestManager.accept(p, id("phantom_blood_001")), "accept failed");
        StoryNpcEntity jonathan = find(ctx, p, id("jonathan_joestar"));
        ctx.assertTrue(jonathan != null, "Jonathan did not appear");
        QuestManager.onTalk(p, jonathan);
        ctx.assertTrue(log.step() == 1 && log.site().isPresent(), "talking did not advance: step " + log.step());
        BlockPos sitePos = log.site().get();
        Vec3d site = Vec3d.ofBottomCenter(sitePos);
        ctx.getWorld().setChunkForced(sitePos.getX() >> 4, sitePos.getZ() >> 4, true);
        ctx.waitAndRun(40, () -> {
            p.refreshPositionAndAngles(site.x, site.y, site.z, 0f, 0f);
            QuestManager.tickPlayer(p, 10);
            check(ctx, log.step() == 2, "arriving did not advance: step " + log.step());
            ctx.waitAndRun(30, () -> {
                StoryNpcEntity dio = find(ctx, p, id("dio_brando_foe_t1_human"));
                check(ctx, dio != null, "Dio did not appear at the site");
                check(ctx, !AnimeCoreApi.isUndead(dio), "Dio is not a vampire yet in chapter 1");
                dio.damage(p.getDamageSources().playerAttack(p), 10000f);
                ctx.waitAndRun(2, () -> {
                    ctx.getWorld().setChunkForced(sitePos.getX() >> 4, sitePos.getZ() >> 4, false);
                    check(ctx, log.isCompleted(id("phantom_blood_001")), "quest not completed");
                    check(ctx, p.getInventory().containsAny(s -> s.isOf(JojoItems.awakening("boxing_gloves"))), "no Ogre Street Gloves reward");
                    check(ctx, QuestManager.available(log, QuestManager.quest(p, id("phantom_blood_002")).orElseThrow()), "chapter 2 locked");
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
}
