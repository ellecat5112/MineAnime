package com.narutorpg.test;

import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.clan.ClanAbilityManager;
import com.animecore.clan.ClanRollService;
import com.animecore.clan.ClanTraitHooks;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoItems;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.narutorpg.content.NarutoContent.CHAKRA;
import static com.narutorpg.content.NarutoContent.FIREBALL_JUTSU;
import static com.narutorpg.content.NarutoContent.SHARINGAN_1;
import static com.narutorpg.content.NarutoContent.UCHIHA;

/** Clans / bloodlines: data integrity, assignment & switching, clan skill, rolls, traits, items, persistence. */
@SuppressWarnings("removal") // createMockCreativeServerPlayerInWorld is the 1.21.1 API
public final class ClanGameTests implements FabricGameTest {
    private static final Identifier NARA = NarutoRPG.CTX.id("nara");
    private static final Identifier UZUMAKI = NarutoRPG.CTX.id("uzumaki");
    private static final Identifier CLANLESS = NarutoRPG.CTX.id("clanless");
    private static final Identifier SHADOW_IMITATION = NarutoRPG.CTX.id("shadow_imitation_technique");
    private static final Identifier GENTLE_FIST = NarutoRPG.CTX.id("gentle_fist");

    private static ServerPlayerEntity player(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        return player;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void everyClanIsCompleteAndConsistent(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        List<Map.Entry<Identifier, LineageDefinition>> clans = ClanRollService.lineages(rm, NarutoRPG.MOD_ID);
        ctx.assertTrue(clans.size() == 53, "expected 52 wiki clans + Clanless, got " + clans.size());
        int fallbacks = 0;
        for (var e : clans) {
            LineageDefinition d = e.getValue();
            if (d.fallback()) fallbacks++;
            ctx.assertTrue(d.grantedAbilities().size() == 1, e.getKey() + " must unlock exactly one jutsu by default");
            for (Identifier a : d.grantedAbilities())
                ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), e.getKey() + " default " + a + " missing");
            for (var p : d.progression()) {
                p.ability().ifPresent(a -> ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), e.getKey() + " -> " + a));
                p.transformation().ifPresent(t -> ctx.assertTrue(AnimeRegistries.transformation(rm, t).isPresent(), e.getKey() + " -> " + t));
                ctx.assertTrue(p.skillLevel() <= d.maxSkillLevel(), e.getKey() + " entry above max level");
            }
            for (Identifier item : d.clanItems())
                ctx.assertTrue(Registries.ITEM.containsId(item), e.getKey() + " clan item " + item + " not registered");
            for (Identifier aff : d.innateAffinities())
                ctx.assertTrue(AnimeRegistries.affinities(rm).containsId(aff), e.getKey() + " affinity " + aff);
        }
        ctx.assertTrue(fallbacks == 1, "exactly one fallback lineage");
        ctx.assertTrue(ClanRollService.fallback(rm, NarutoRPG.MOD_ID).orElseThrow().equals(CLANLESS), "Clanless is the fallback");
        LineageDefinition uchiha = AnimeRegistries.lineage(rm, UCHIHA.getValue()).orElseThrow();
        ctx.assertTrue(uchiha.progression().stream().anyMatch(p -> p.transformation().map(SHARINGAN_1.getValue()::equals).orElse(false)),
                "Uchiha progression must contain the Sharingan");
        long total = rm.get(AnimeRegistries.ABILITY).getEntrySet().stream().filter(e -> e.getValue().system().equals(NarutoRPG.MOD_ID)).count();
        ctx.assertTrue(total >= 419, "clan jutsu missing: " + total);
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void switchingClansSwapsGrantsButKeepsLearnedAbilities(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        AtomicInteger assigned = new AtomicInteger();
        AnimeCoreEvents.CLAN_ASSIGNED.register((p, pw, prev, lineage, reason) -> { if (p == player) assigned.incrementAndGet(); });

        ClanAbilityManager.assign(player, power, UCHIHA.getValue(), ClanAbilityManager.Reason.COMMAND);
        power.setClanSkill(UCHIHA.getValue(), 3, 0);
        ClanAbilityManager.evaluate(player, power);
        power.unlockAbility(GENTLE_FIST);                                  // learned from a scroll: not clan-owned
        power.serverTick();
        double uchihaDamage = player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        ctx.assertTrue(power.unlockedAbilities().contains(FIREBALL_JUTSU.getValue()), "Uchiha default jutsu");
        ctx.assertTrue(power.unlockedTransformations().contains(SHARINGAN_1.getValue()), "Sharingan at clan level 3");

        // Assigning the same clan again must not stack the +1 attack damage modifier.
        ClanAbilityManager.assign(player, power, UCHIHA.getValue(), ClanAbilityManager.Reason.COMMAND);
        power.markTraitsDirty();
        power.serverTick();
        ctx.assertTrue(Math.abs(player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) - uchihaDamage) < 1e-6, "modifier stacked");

        ClanAbilityManager.assign(player, power, NARA, ClanAbilityManager.Reason.COMMAND);
        power.serverTick();
        ctx.assertTrue(!power.unlockedAbilities().contains(FIREBALL_JUTSU.getValue()), "old clan jutsu must be removed");
        ctx.assertTrue(!power.unlockedTransformations().contains(SHARINGAN_1.getValue()), "old clan form must be removed");
        ctx.assertTrue(power.unlockedAbilities().contains(GENTLE_FIST), "unrelated learned ability must survive");
        ctx.assertTrue(power.unlockedAbilities().contains(SHADOW_IMITATION), "Nara default jutsu");
        ctx.assertTrue(player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) < uchihaDamage, "Uchiha attack bonus removed");
        ctx.assertTrue(assigned.get() == 2, "CLAN_ASSIGNED fired " + assigned.get() + " times");

        // Clan progress is kept per clan: back to Uchiha restores the Sharingan.
        ClanAbilityManager.assign(player, power, UCHIHA.getValue(), ClanAbilityManager.Reason.COMMAND);
        ctx.assertTrue(power.unlockedTransformations().contains(SHARINGAN_1.getValue()), "per-clan level restored");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void killExperienceBecomesClanSkillWhileHoldingEmblem(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ClanAbilityManager.assign(player, power, NARA, ClanAbilityManager.Reason.COMMAND);
        ZombieEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 1, 2, 1);
        player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);   // jutsu tokens in hand would take the xp as mastery
        player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);

        ctx.assertTrue(!ClanTraitHooks.convertKillExperience(zombie, player), "no emblem held: vanilla orbs");
        ItemStack foreign = ClanRollService.boundStack(NarutoItems.CLAN_EMBLEM, UCHIHA.getValue(), null);
        player.setStackInHand(Hand.OFF_HAND, foreign);
        ctx.assertTrue(!ClanTraitHooks.convertKillExperience(zombie, player), "another clan's emblem gives nothing");

        player.setStackInHand(Hand.OFF_HAND, ClanRollService.boundStack(NarutoItems.CLAN_EMBLEM, NARA, null));
        ctx.assertTrue(ClanTraitHooks.convertKillExperience(zombie, player), "off-hand emblem converts kill xp");
        ctx.assertTrue(power.clanSkillXp(NARA) > 0 || power.clanSkillLevel(NARA) > 1, "skill xp gained");
        ctx.assertTrue(player.getOffHandStack().contains(AnimeDataComponents.CLAN_SKILL), "emblem shows clan skill");

        int unlockedBefore = power.unlockedAbilities().size();
        ClanAbilityManager.addSkillExperience(player, power, 5_000);
        ctx.assertTrue(power.clanSkillLevel(NARA) >= 20, "levelled to " + power.clanSkillLevel(NARA));
        ctx.assertTrue(power.unlockedAbilities().size() > unlockedBefore + 3, "progression jutsu unlocked by clan level");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void weightedRollsExcludeCurrentAndRespectPity(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        Random random = Random.create(1234);
        Set<Identifier> seen = new HashSet<>();
        for (int i = 0; i < 2000; i++) {
            Identifier r = ClanRollService.roll(rm, NarutoRPG.MOD_ID, random, UCHIHA.getValue(), false, 3).orElseThrow();
            ctx.assertTrue(!r.equals(UCHIHA.getValue()), "excluded clan rolled");
            ctx.assertTrue(!r.equals(CLANLESS), "fallback must never be rolled");
            seen.add(r);
        }
        ctx.assertTrue(seen.size() > 35, "rolls should cover most clans, saw " + seen.size());
        for (int i = 0; i < 200; i++) {
            Identifier r = ClanRollService.roll(rm, NarutoRPG.MOD_ID, random, null, true, 3).orElseThrow();
            ctx.assertTrue(AnimeRegistries.lineage(rm, r).orElseThrow().weight() <= 3, "pity roll must be rare: " + r);
        }

        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        power.setClanPity(10);                                         // threshold reached
        Identifier rolled = ClanRollService.rollAndAssign(player, power, true, ClanAbilityManager.Reason.ROLL).orElseThrow();
        ctx.assertTrue(AnimeRegistries.lineage(rm, rolled).orElseThrow().weight() <= 3, "pity guaranteed a rare clan");
        ctx.assertTrue(power.clanPity() == 0, "pity resets after a rare");
        ctx.assertTrue(power.lineage().orElseThrow().equals(rolled), "roll assigned");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void clanTraitsHookIntoEvents(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ClanAbilityManager.assign(player, power, NARA, ClanAbilityManager.Reason.COMMAND);
        power.setEnergy(CHAKRA, 100f);
        power.tryConsume(CHAKRA, 20f);
        float left = power.pool(CHAKRA).orElseThrow().current();
        ctx.assertTrue(Math.abs(left - 83f) < 0.01f, "Nara shadow control should discount chakra 15%, left " + left);

        ClanAbilityManager.assign(player, power, UZUMAKI, ClanAbilityManager.Reason.COMMAND);
        DamageSource src = player.getDamageSources().generic();
        ctx.assertTrue(AnimeCoreEvents.FATAL_DAMAGE_INTERCEPT.invoker().intercept(player, src, 100f), "Uzumaki life force saves once");
        ctx.assertTrue(!AnimeCoreEvents.FATAL_DAMAGE_INTERCEPT.invoker().intercept(player, src, 100f), "then on cooldown");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void sealedBloodlineScrollAwakensThatClan(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ClanAbilityManager.assign(player, power, CLANLESS, ClanAbilityManager.Reason.FIRST_JOIN);
        ItemStack scroll = ClanRollService.boundStack(NarutoItems.BLOODLINE_SCROLL, UCHIHA.getValue(), null);
        player.setStackInHand(Hand.MAIN_HAND, scroll);
        NarutoItems.BLOODLINE_SCROLL.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(power.lineage().orElseThrow().equals(UCHIHA.getValue()), "sealed scroll should assign Uchiha");

        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(NarutoItems.BLOODLINE_SCROLL));
        player.getItemCooldownManager().remove(NarutoItems.BLOODLINE_SCROLL);
        NarutoItems.BLOODLINE_SCROLL.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(!power.lineage().orElseThrow().equals(UCHIHA.getValue()), "unsealed reroll never keeps the current clan");
        ctx.assertTrue(power.clanRerolls() == 1, "reroll counted");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void firstJoinAssignsFallbackWhenAutoRollIsOff(TestContext ctx) {
        // TestServerSetup turns animecoreClanAutoRoll off; mock players go through the real join path.
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ctx.assertTrue(power.clanInitialized(), "first-join assignment ran");
        ctx.assertTrue(power.lineage().map(CLANLESS::equals).orElse(false), "fallback lineage, got " + power.lineage());
        ctx.assertTrue(power.unlockedAbilities().contains(NarutoRPG.CTX.id("clone_technique")), "Clanless default jutsu");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void choosingIsOnlyAllowedWhileClanless(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ClanAbilityManager.assign(player, power, CLANLESS, ClanAbilityManager.Reason.FIRST_JOIN);
        ctx.assertTrue(!ClanRollService.choose(player, power, NarutoRPG.CTX.id("otsutsuki")), "Otsutsuki is roll-only");
        ctx.assertTrue(ClanRollService.choose(player, power, NARA), "clanless may choose once");
        ctx.assertTrue(!ClanRollService.choose(player, power, UCHIHA.getValue()), "no second free choice");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void clanStateSurvivesRespawnCopy(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ClanAbilityManager.assign(player, power, NARA, ClanAbilityManager.Reason.ROLL);
        ClanAbilityManager.addSkillExperience(player, power, 300);
        power.setClanPity(4);
        NbtCompound tag = new NbtCompound();
        power.writeToNbt(tag, ctx.getWorld().getRegistryManager());

        ServerPlayerEntity other = player(ctx);
        PlayerPowerComponent copy = NarutoRPG.CTX.power(other);
        copy.readFromNbt(tag, ctx.getWorld().getRegistryManager());   // what CCA's ALWAYS_COPY respawn strategy does
        ctx.assertTrue(copy.lineage().orElseThrow().equals(NARA), "lineage copied");
        ctx.assertTrue(copy.clanSkillLevel(NARA) == power.clanSkillLevel(NARA), "clan level copied");
        ctx.assertTrue(copy.clanGrantedAbilities().equals(power.clanGrantedAbilities()), "clan grants copied");
        ctx.assertTrue(copy.clanPity() == 4 && copy.clanInitialized(), "roll bookkeeping copied");

        // v1 save (no clan block) migrates to "initialized" without losing abilities.
        NbtCompound v1 = new NbtCompound();
        v1.putInt("schema_version", 1);
        v1.putString("lineage", UCHIHA.getValue().toString());
        copy.readFromNbt(v1, ctx.getWorld().getRegistryManager());
        ctx.assertTrue(copy.clanInitialized() && copy.clanGrantedAbilities().isEmpty(), "v1 migration");
        ctx.complete();
    }
}
