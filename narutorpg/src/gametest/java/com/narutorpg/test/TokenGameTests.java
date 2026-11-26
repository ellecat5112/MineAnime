package com.narutorpg.test;

import com.animecore.clan.ClanAbilityManager;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueTokenItem;
import com.animecore.technique.TechniqueTokens;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoTokens;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;

import static com.narutorpg.content.NarutoContent.CHAKRA;
import static com.narutorpg.content.NarutoContent.FIREBALL_JUTSU;
import static com.narutorpg.content.NarutoContent.SHARINGAN_1;
import static com.narutorpg.content.NarutoContent.SUBSTITUTION_JUTSU;
import static com.narutorpg.content.NarutoContent.UCHIHA;

/** AHZNB-style jutsu items: token resolution, hand-out on unlock, switching, casting and eye toggling. */
@SuppressWarnings("removal")
public final class TokenGameTests implements FabricGameTest {

    private static int count(ServerPlayerEntity p, net.minecraft.item.Item item) {
        int n = 0;
        for (int i = 0; i < p.getInventory().size(); i++) if (p.getInventory().getStack(i).isOf(item)) n++;
        return n;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void everyJutsuLandsOnAToken(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        for (var e : rm.get(AnimeRegistries.ABILITY).getEntrySet()) {
            if (!e.getValue().system().equals(NarutoRPG.MOD_ID)) continue;
            var token = TechniqueTokens.tokenOf(e.getValue());
            ctx.assertTrue(TechniqueTokens.item(NarutoRPG.MOD_ID, token).isPresent(), e.getKey().getValue() + " -> no token item for " + token);
        }
        var fireball = AnimeRegistries.ability(rm, FIREBALL_JUTSU.getValue()).orElseThrow().value();
        ctx.assertTrue(TechniqueTokens.tokenOf(fireball).equals(NarutoRPG.CTX.id("fire")), "fireball on Fire Release");
        var sub = AnimeRegistries.ability(rm, SUBSTITUTION_JUTSU.getValue()).orElseThrow().value();
        ctx.assertTrue(TechniqueTokens.tokenOf(sub).equals(NarutoRPG.CTX.id("ninjutsu")), "no affinity -> Ninjutsu");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void learningGivesTokenOnceAndRightClickCasts(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        PlayerPowerComponent power = NarutoRPG.CTX.power(p);
        ClanAbilityManager.assign(p, power, UCHIHA.getValue(), ClanAbilityManager.Reason.COMMAND);
        var fire = NarutoTokens.item("fire");
        ctx.assertTrue(count(p, fire) == 1, "Fire Release token handed out with the Great Fireball");
        power.unlockAbility(NarutoRPG.CTX.id("fire_release_great_fire_annihilation"));
        ctx.assertTrue(count(p, fire) == 1, "only one token per release");

        ItemStack token = new ItemStack(fire);
        p.setStackInHand(Hand.MAIN_HAND, token);
        TechniqueTokenItem item = (TechniqueTokenItem) fire;
        int size = item.entries(p).size();   // 2 Uchiha fire jutsu + the first Fire Release mastery jutsu (innate nature)
        ctx.assertTrue(size >= 2, "Fire Release jutsu on the token, got " + size);
        var first = item.selected(p, token).orElseThrow().id();
        item.cycle(p, token, 1);
        ctx.assertTrue(!token.get(AnimeDataComponents.TOKEN_SELECTION).equals(first), "sneak+scroll switched the jutsu");
        for (int i = 1; i < size; i++) item.cycle(p, token, 1);
        ctx.assertTrue(token.get(AnimeDataComponents.TOKEN_SELECTION).equals(first), "wraps around");

        // select the instant / charged fireball and right-click: charged -> charging, release fires
        token.set(AnimeDataComponents.TOKEN_SELECTION, FIREBALL_JUTSU.getValue());
        power.setEnergy(CHAKRA, 100f);
        fire.use(ctx.getWorld(), p, Hand.MAIN_HAND);
        ctx.assertTrue(power.chargingAbility().map(FIREBALL_JUTSU.getValue()::equals).orElse(false), "hold right-click charges the fireball");
        fire.onStoppedUsing(token, ctx.getWorld(), p, 60);
        ctx.assertTrue(power.isOnCooldown(FIREBALL_JUTSU.getValue()), "releasing right-click fires it");

        // an INSTANT jutsu fires on the click itself
        var destruction = NarutoRPG.CTX.id("fire_release_great_fire_destruction");
        power.unlockAbility(destruction);
        token.set(AnimeDataComponents.TOKEN_SELECTION, destruction);
        power.setEnergy(CHAKRA, 100f);
        fire.use(ctx.getWorld(), p, Hand.MAIN_HAND);
        ctx.assertTrue(power.isOnCooldown(destruction), "instant jutsu cast on right-click");
        ctx.assertTrue(power.pool(CHAKRA).orElseThrow().current() < 100f, "chakra spent: " + power.pool(CHAKRA).orElseThrow().current());

        // the Dojutsu token toggles the eyes
        power.setClanSkill(UCHIHA.getValue(), 3, 0);
        ClanAbilityManager.evaluate(p, power);
        var dojutsu = NarutoTokens.item("dojutsu");
        ctx.assertTrue(count(p, dojutsu) == 1, "Dojutsu token handed out with the Sharingan");
        ItemStack eyes = new ItemStack(dojutsu);
        eyes.set(AnimeDataComponents.TOKEN_SELECTION, SHARINGAN_1.getValue());
        p.setStackInHand(Hand.MAIN_HAND, eyes);
        dojutsu.use(ctx.getWorld(), p, Hand.MAIN_HAND);
        ctx.assertTrue(power.activeTransformation().map(SHARINGAN_1.getValue()::equals).orElse(false), "right-click activates the Sharingan");
        ctx.complete();
    }
}
