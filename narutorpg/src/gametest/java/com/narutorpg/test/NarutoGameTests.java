package com.narutorpg.test;

import com.animecore.ability.AbilityExecutor;
import com.animecore.ability.CastResult;
import com.animecore.affinity.AffinityMatrix;
import com.animecore.api.AnimeModContext;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.entity.EnergyProjectileEntity;
import com.animecore.registry.AnimeRegistries;
import com.animecore.transformation.TransformationService;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.HandSigns;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;

import java.util.Set;

import static com.narutorpg.content.NarutoContent.*;

/** End-to-end checks of the engine through the Naruto content (datapack JSON -> registries -> CCA -> casting). */
@SuppressWarnings("removal") // createMockCreativeServerPlayerInWorld is the 1.21.1 API
public final class NarutoGameTests implements FabricGameTest {

    private static ServerPlayerEntity uchiha(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        power.setLineage(UCHIHA.getValue());
        power.setClanSkill(UCHIHA.getValue(), 3, 0); // Sharingan unlocks at clan skill level 3
        com.animecore.clan.ClanAbilityManager.evaluate(player, power);
        power.setFaction(HIDDEN_LEAF.getValue());
        power.serverTick(); // applies traits
        return player;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void registriesLoadedFromDatapack(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        ctx.assertTrue(AnimeRegistries.ability(rm, FIREBALL_JUTSU.getValue()).isPresent(), "fireball ability missing");
        ctx.assertTrue(AnimeRegistries.lineage(rm, UCHIHA.getValue()).isPresent(), "uchiha lineage missing");
        ctx.assertTrue(AnimeRegistries.transformation(rm, MANGEKYO.getValue()).isPresent(), "mangekyo missing");
        float clash = AffinityMatrix.clash(AnimeRegistries.affinities(rm), FIRE.getValue(), Set.of(WIND.getValue()));
        float weak = AffinityMatrix.clash(AnimeRegistries.affinities(rm), FIRE.getValue(), Set.of(WATER.getValue()));
        ctx.assertTrue(Math.abs(clash - 1.5f) < 1e-4, "fire vs wind should be 1.5, was " + clash);
        ctx.assertTrue(Math.abs(weak - 1f / 1.5f) < 1e-4, "fire vs water should be 0.667, was " + weak);
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void uchihaLineageGrantsTraits(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ctx.assertTrue(power.unlockedAbilities().contains(FIREBALL_JUTSU.getValue()), "fireball not granted");
        ctx.assertTrue(power.unlockedAbilities().contains(SUBSTITUTION_JUTSU.getValue()), "substitution not granted by faction");
        ctx.assertTrue(power.unlockedTransformations().contains(SHARINGAN_1.getValue()), "sharingan not granted");
        ctx.assertTrue(power.effectiveAffinities().contains(FIRE.getValue()), "fire affinity missing");
        ctx.assertTrue(power.pool(CHAKRA).orElseThrow().max() == 120f, "chakra max should be 100+20");
        ctx.assertTrue(player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) == 2.0, "uchiha +1 attack damage");
        // Uchiha +15% Fire Release x Hidden Leaf village perk +5% Fire Release
        ctx.assertTrue(Math.abs(power.traits().abilityPower(FIRE.getValue()) - 1.15f * 1.05f) < 1e-4, "fire power multiplier");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void fireballConsumesChakraAndSpawnsProjectile(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        float before = power.pool(CHAKRA).orElseThrow().current();
        var entry = AnimeRegistries.ability(player.getRegistryManager(), FIREBALL_JUTSU.getValue()).orElseThrow();
        CastResult result = AbilityExecutor.cast(player, entry, power, 1f, null);
        ctx.assertTrue(result == CastResult.SUCCESS, "cast failed: " + result);
        float after = power.pool(CHAKRA).orElseThrow().current();
        ctx.assertTrue(Math.abs(before - after - 25f) < 1e-3, "expected 25 chakra spent, spent " + (before - after));
        ctx.assertTrue(power.isOnCooldown(FIREBALL_JUTSU.getValue()), "cooldown not applied");
        ctx.assertTrue(AbilityExecutor.cast(player, entry, power, 0f, null) == CastResult.ON_COOLDOWN, "second cast should be on cooldown");
        var projectiles = ctx.getWorld().getEntitiesByClass(EnergyProjectileEntity.class, new Box(player.getBlockPos()).expand(4), e -> true);
        ctx.assertTrue(!projectiles.isEmpty(), "no energy projectile spawned");
        ctx.assertTrue(projectiles.get(0).getSize() > 1.0f, "full charge should enlarge the fireball");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void handSignComboCastsFireball(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        AnimeModContext c = NarutoRPG.CTX;
        PlayerPowerComponent power = c.power(player);
        CastResult last = null;
        for (Identifier sign : java.util.List.of(HandSigns.SNAKE, HandSigns.RAM, HandSigns.BOAR, HandSigns.HORSE, HandSigns.TIGER)) {
            last = AbilityExecutor.handleComboInput(player, c, sign, false);
        }
        ctx.assertTrue(last == CastResult.SUCCESS, "combo did not auto-cast: " + last);
        ctx.assertTrue(power.comboBuffer().isEmpty(), "combo buffer not cleared");
        ctx.assertTrue(power.isOnCooldown(FIREBALL_JUTSU.getValue()), "fireball not cast by combo");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void sharinganDrainsChakraAndBoostsSpeed(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        double baseSpeed = player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        ctx.assertTrue(TransformationService.toggle(player, power, null), "sharingan failed to activate");
        ctx.assertTrue(SHARINGAN_1.getValue().equals(power.activeTransformation().orElse(null)), "wrong form active");
        power.setEnergy(CHAKRA, 50f);
        power.serverTick(); // traits refresh + one drain tick
        double formSpeed = player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        ctx.assertTrue(formSpeed > baseSpeed, "sharingan should increase speed");
        // regen (0.15 * 1.1 leaf bonus) - drain (0.04): net gain while transformed is smaller than base regen
        float chakra = power.pool(CHAKRA).orElseThrow().current();
        ctx.assertTrue(chakra < 50f + 0.165f, "drain not applied: " + chakra);
        ctx.assertTrue(TransformationService.toggle(player, power, null), "revert failed");
        ctx.assertTrue(power.activeTransformation().isEmpty(), "still transformed");
        power.serverTick();
        ctx.assertTrue(Math.abs(player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) - baseSpeed) < 1e-6, "speed not restored");
        ctx.complete();
    }

    /**
     * Drives Fabric's ALLOW_DAMAGE hook directly (mock players never tick off vanilla's join invulnerability,
     * so a real {@code damage()} call would be swallowed before reaching the hook).
     */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void substitutionNegatesHit(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        // Uchiha's Sharingan precognition is a random evasion trait; use an evasion-free clan so hits are deterministic.
        power.setLineage(NarutoRPG.CTX.id("akimichi"));
        ZombieEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 2, 2, 2);
        var hit = player.getDamageSources().mobAttack(zombie);

        CastResult armed = AbilityExecutor.handleInput(player, SUBSTITUTION_JUTSU.getValue(), true);
        ctx.assertTrue(armed == CastResult.ARMED, "substitution did not arm: " + armed);
        float chakra = power.pool(CHAKRA).orElseThrow().current();
        boolean allowed = ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(player, hit, 6f);
        ctx.assertTrue(!allowed, "hit was not negated");
        ctx.assertTrue(power.isOnCooldown(SUBSTITUTION_JUTSU.getValue()), "substitution not consumed");
        ctx.assertTrue(power.pool(CHAKRA).orElseThrow().current() < chakra, "substitution chakra not spent");
        ctx.assertTrue(ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(player, hit, 2f), "second hit should land");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void componentNbtRoundTrip(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        power.addExperience(500);
        NbtCompound tag = new NbtCompound();
        power.writeToNbt(tag, player.getRegistryManager());
        ctx.assertTrue(tag.getInt("schema_version") == PlayerPowerComponent.SCHEMA_VERSION, "schema version not written");

        ServerPlayerEntity other = ctx.createMockCreativeServerPlayerInWorld();
        PlayerPowerComponent copy = NarutoRPG.CTX.power(other);
        copy.readFromNbt(tag, player.getRegistryManager());
        ctx.assertTrue(copy.tier() == power.tier() && copy.tier() > 1, "tier mismatch");
        ctx.assertTrue(copy.lineage().equals(power.lineage()), "lineage mismatch");
        ctx.assertTrue(copy.unlockedAbilities().equals(power.unlockedAbilities()), "abilities mismatch");

        NbtCompound legacy = new NbtCompound(); // schema v0: single "energy" float
        legacy.putFloat("energy", 42f);
        copy.readFromNbt(legacy, player.getRegistryManager());
        ctx.assertTrue(copy.pool(CHAKRA).orElseThrow().current() == 42f, "v0 -> v1 migration failed");
        ctx.complete();
    }
}
