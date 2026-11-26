package com.narutorpg.test;

import com.animecore.clan.ClanAbilityManager;
import com.animecore.item.AdminSealItem;
import com.animecore.technique.TechniqueMastery;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoAdmin;
import com.narutorpg.content.NarutoTokens;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;

import static com.narutorpg.content.NarutoContent.CHAKRA;
import static com.narutorpg.content.NarutoContent.SHARINGAN_1;
import static com.narutorpg.content.NarutoContent.UCHIHA;

/** Admin seals grant ranks, clan levels, mastery, natures, reputation and refills. */
@SuppressWarnings("removal")
public final class AdminSealGameTests implements FabricGameTest {
    private static AdminSealItem seal(AdminSealItem.Action a) { return (AdminSealItem) NarutoAdmin.SEALS.get(a); }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void sealsGrantProgression(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        var power = NarutoRPG.CTX.power(p);
        ClanAbilityManager.assign(p, power, UCHIHA.getValue(), ClanAbilityManager.Reason.COMMAND);
        int tier = power.tier();
        seal(AdminSealItem.Action.RANK_UP).apply(p, power, false, ItemStack.EMPTY);
        seal(AdminSealItem.Action.RANK_UP).apply(p, power, true, ItemStack.EMPTY);
        ctx.assertTrue(power.tier() == tier + 11, "rank +1 then +10");

        seal(AdminSealItem.Action.CLAN_LEVEL).apply(p, power, false, ItemStack.EMPTY);
        seal(AdminSealItem.Action.CLAN_LEVEL).apply(p, power, false, ItemStack.EMPTY);
        ctx.assertTrue(power.clanSkillLevel(UCHIHA.getValue()) == 3 && power.unlockedTransformations().contains(SHARINGAN_1.getValue()),
                "clan level 3 unlocks the Sharingan");

        var fire = NarutoRPG.CTX.id("fire");
        seal(AdminSealItem.Action.MASTERY).apply(p, power, true, new ItemStack(NarutoTokens.item("fire")));
        int size = TechniqueMastery.list(p.getRegistryManager(), fire).size();
        ctx.assertTrue(power.tokenLevel(fire) == size, "Fire Release fully mastered");

        seal(AdminSealItem.Action.NATURES).apply(p, power, true, ItemStack.EMPTY);
        ctx.assertTrue(power.personalAffinities().size() >= 23, "every nature: " + power.personalAffinities().size());

        power.setEnergy(CHAKRA, 1f);
        power.setCooldown(NarutoRPG.CTX.id("fireball_jutsu"), 1000);
        seal(AdminSealItem.Action.REFILL).apply(p, power, false, ItemStack.EMPTY);
        ctx.assertTrue(power.pool(CHAKRA).orElseThrow().ratio() >= 0.999f && !power.isOnCooldown(NarutoRPG.CTX.id("fireball_jutsu")), "refilled");

        seal(AdminSealItem.Action.MAX_ALL).apply(p, power, false, ItemStack.EMPTY);
        ctx.assertTrue(power.tier() == 100 && power.clanSkillLevel(UCHIHA.getValue()) == 50, "Kage seal maxes everything");
        ctx.complete();
    }
}
