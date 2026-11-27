package com.narutorpg.test;

import com.animecore.ability.AbilityExecutor;
import com.animecore.ability.CastResult;
import com.animecore.clan.ClanTraitHooks;
import com.animecore.item.AwakeningItem;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueTokens;
import com.animecore.transformation.TransformationService;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoForms;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

/** Tailed beasts, curse marks, Kāma, modes and bloodline forms; school jutsu; medical healing of allies. */
@SuppressWarnings("removal")
public final class FormsGameTests implements FabricGameTest {
    private static Identifier id(String p) { return NarutoRPG.CTX.id(p); }

    private static ServerPlayerEntity player(TestContext ctx, int tier) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        NarutoRPG.CTX.power(p).setTier(tier);
        return p;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void formsAndItemsRegistered(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        for (String f : new String[]{"shukaku_version_1", "kurama_tailed_beast_transformation", "nine_tails_chakra_mode", "baryon_mode_form",
                "ten_tails_jinchuriki", "sage_mode", "six_paths_sage_mode", "cursed_seal_of_heaven_level_2", "kama_otsutsuki_awakening",
                "susanoo", "rinne_sharingan", "tenseigan_chakra_mode", "shikotsumyaku", "smoke_body"}) {
            ctx.assertTrue(AnimeRegistries.transformation(rm, id(f)).isPresent(), "missing form " + f);
        }
        ctx.assertTrue(NarutoForms.ITEMS.size() >= 28, "awakening items " + NarutoForms.ITEMS.size());
        for (var item : NarutoForms.ITEMS.values()) {
            Identifier form = ((AwakeningItem) item).form();
            ctx.assertTrue(AnimeRegistries.transformation(rm, form).isPresent(), item + " awakens unknown " + form);
        }
        for (String a : new String[]{"mystical_palm_technique", "dance_of_the_camellia"}) {
            ctx.assertTrue(AnimeRegistries.ability(rm, id(a)).isPresent(), "missing technique " + a);
        }
        ctx.complete();
    }

    /** A tailed-beast seal makes you a jinchūriki: the cloak form appears on the Jinchūriki item and makes you stronger. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void sealAwakensJinchuriki(TestContext ctx) {
        ServerPlayerEntity p = player(ctx, 3);
        var power = NarutoRPG.CTX.power(p);
        p.setStackInHand(Hand.MAIN_HAND, new ItemStack(NarutoForms.item("shukaku_seal")));
        p.getMainHandStack().use(p.getWorld(), p, Hand.MAIN_HAND);
        ctx.assertTrue(power.unlockedTransformations().contains(id("shukaku_version_1")), "seal did not awaken the cloak");
        var entries = TechniqueTokens.entries(p.getRegistryManager(), power, id("jinchuriki"));
        ctx.assertTrue(entries.stream().anyMatch(e -> e.form() && e.id().equals(id("shukaku_version_1"))), "form not on the Jinchūriki item: " + entries);
        double before = p.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        ctx.assertTrue(TransformationService.activate(p, power, id("shukaku_version_1")), "activate failed");
        power.forceRefreshTraits(p);   // applied on the next player tick in game; mock players do not tick
        ctx.assertTrue(p.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) > before, "cloak gives no strength");
        ctx.assertTrue(!ClanTraitHooks.traits(p, power).isEmpty(), "cloak traits (regeneration) not active");
        ctx.complete();
    }

    /** The full tailed-beast transformation turns you into a giant. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void fullBeastIsGiant(TestContext ctx) {
        ServerPlayerEntity p = player(ctx, 10);
        var power = NarutoRPG.CTX.power(p);
        power.unlockTransformation(id("kurama_tailed_beast_transformation"));
        ctx.assertTrue(TransformationService.activate(p, power, id("kurama_tailed_beast_transformation")), "activate failed");
        power.forceRefreshTraits(p);
        ctx.assertTrue(p.getAttributeValue(EntityAttributes.GENERIC_SCALE) >= 4.0, "scale " + p.getAttributeValue(EntityAttributes.GENERIC_SCALE));
        ctx.complete();
    }

    /** Medical ninjutsu heal the allies around the medic. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void medicalJutsuHealsAllies(TestContext ctx) {
        ServerPlayerEntity medic = player(ctx, 10);
        ServerPlayerEntity hurt = player(ctx, 1);
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 1)));
        medic.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        hurt.refreshPositionAndAngles(at.x + 2, at.y, at.z, 0f, 0f);
        hurt.setHealth(6f);
        var power = NarutoRPG.CTX.power(medic);
        power.unlockAbility(id("mystical_palm_technique"));
        var entry = AnimeRegistries.ability(medic.getRegistryManager(), id("mystical_palm_technique")).orElseThrow();
        ctx.assertTrue(AbilityExecutor.cast(medic, entry, power, 0f, null) == CastResult.SUCCESS, "cast failed");
        ctx.assertTrue(hurt.getHealth() > 6f, "the ally was not healed: " + hurt.getHealth());
        ctx.complete();
    }

    /** Full tailed-beast transformations, Susanoo and the story's tailed beasts use their low-poly models. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void beastsHaveModels(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        for (String b : new String[]{"shukaku", "matatabi", "isobu", "son_goku", "kokuo", "saiken", "chomei", "gyuki", "kurama"}) {
            var form = AnimeRegistries.transformation(rm, id(b + "_tailed_beast_transformation")).orElseThrow().value();
            ctx.assertTrue(form.visuals().model().map(id(b)::equals).orElse(false), b + " full form has no model");
        }
        ctx.assertTrue(AnimeRegistries.transformation(rm, id("susanoo")).orElseThrow().value().visuals().model().isPresent(), "Susanoo model");
        var shukaku = AnimeRegistries.storyNpc(rm, id("shukaku_foe_naruto")).orElseThrow();
        ctx.assertTrue(shukaku.appearance().model().map(id("shukaku")::equals).orElse(false), "story Shukaku has no model");
        ctx.complete();
    }
}
