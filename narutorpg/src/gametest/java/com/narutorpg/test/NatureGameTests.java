package com.narutorpg.test;

import com.animecore.component.PlayerPowerComponent;
import com.animecore.origin.OriginSelectionService;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueMastery;
import com.animecore.technique.TechniqueTokens;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.List;

import static com.narutorpg.content.NarutoContent.FIREBALL_JUTSU;

/** Nature releases: all 23 natures, mastery lists, starting nature, training, attunement and awakening. */
@SuppressWarnings("removal")
public final class NatureGameTests implements FabricGameTest {
    private static final String[] NATURES = {"fire", "wind", "lightning", "earth", "water", "wood", "ice", "lava", "magnet", "storm",
            "boil", "scorch", "explosion", "dust", "yin", "yang", "mud", "yin_yang", "crystal", "dark", "steel", "swift", "typhoon"};

    private static ServerPlayerEntity player(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        return p;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void allTwentyThreeNaturesHaveTokensAndJutsu(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        for (String n : NATURES) {
            Identifier id = NarutoRPG.CTX.id(n);
            ctx.assertTrue(AnimeRegistries.affinities(rm).containsId(id), "affinity " + n);
            ctx.assertTrue(TechniqueTokens.item(NarutoRPG.MOD_ID, id).isPresent(), "release token " + n);
        }
        List<Identifier> fire = TechniqueMastery.list(rm, NarutoRPG.CTX.id("fire"));
        ctx.assertTrue(fire.size() > 150, "Fire Release mastery list, got " + fire.size());
        ctx.assertTrue(!fire.contains(FIREBALL_JUTSU.getValue()), "clan jutsu (Uchiha default) are not part of nature mastery");
        long total = rm.get(AnimeRegistries.ABILITY).getEntrySet().stream().filter(e -> e.getValue().system().equals(NarutoRPG.MOD_ID)).count();
        ctx.assertTrue(total > 1500, "every nature jutsu registered, total abilities " + total);
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void originGivesANatureAndTrainingUnlocksMore(TestContext ctx) {
        ServerPlayerEntity p = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(p);
        OriginSelectionService.select(p, power, NarutoRPG.CTX.id("hidden_mist"), NarutoRPG.CTX.id("shinobi_orphan"), false);
        var rm = p.getRegistryManager();
        Identifier nature = power.personalAffinities().stream()
                .filter(a -> AnimeRegistries.affinities(rm).get(a).basic()).findFirst().orElse(null);
        ctx.assertTrue(nature != null, "a basic nature was rolled");
        ctx.assertTrue(power.tokenLevel(nature) >= 1, "its mastery starts at level 1");
        Identifier first = TechniqueMastery.list(rm, nature).get(0);
        ctx.assertTrue(power.unlockedAbilities().contains(first), "first " + nature.getPath() + " jutsu learned: " + first);

        int before = (int) power.unlockedAbilities().stream().filter(TechniqueMastery.list(rm, nature)::contains).count();
        TechniqueMastery.addExperience(p, power, nature, 500);
        int after = (int) power.unlockedAbilities().stream().filter(TechniqueMastery.list(rm, nature)::contains).count();
        ctx.assertTrue(power.tokenLevel(nature) > 3 && after > before, "training unlocked more jutsu: " + before + " -> " + after);

        // off-affinity training is 2.5x slower; reaching level 10 attunes a basic nature
        power.forceRefreshTraits(p);   // effective affinities refresh on the player tick; mock players do not tick
        Identifier other = List.of("fire", "wind", "lightning", "earth", "water").stream().map(NarutoRPG.CTX::id)
                .filter(n -> !power.effectiveAffinities().contains(n)).findFirst().orElseThrow();
        TechniqueMastery.addExperience(p, power, other, 100);
        ctx.assertTrue(power.tokenLevel(other) < power.tokenLevel(nature), "off-affinity trains slower: " + other.getPath() + " " + power.tokenLevel(other)
                + " vs " + nature.getPath() + " " + power.tokenLevel(nature) + " personal " + power.personalAffinities() + " effective " + power.effectiveAffinities());
        TechniqueMastery.addExperience(p, power, other, 5000);
        ctx.assertTrue(power.personalAffinities().contains(other), "level 10 attunes " + other.getPath());
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void kekkeiGenkaiNaturesAwakenFromMastery(TestContext ctx) {
        ServerPlayerEntity p = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(p);
        Identifier earth = NarutoRPG.CTX.id("earth"), crystal = NarutoRPG.CTX.id("crystal");
        power.addPersonalAffinity(earth);
        power.forceRefreshTraits(p);
        power.setTokenMastery(earth, 29, 0);
        TechniqueMastery.evaluate(p, power, earth);
        ctx.assertTrue(!power.personalAffinities().contains(crystal), "not yet");
        TechniqueMastery.addExperience(p, power, earth, 2000);
        ctx.assertTrue(power.personalAffinities().contains(crystal), "Crystal Release awakens at Earth mastery 30");
        ctx.complete();
    }
}
