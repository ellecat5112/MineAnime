package com.narutorpg.test;

import com.animecore.item.AbilityScrollItem;
import com.animecore.item.AnimeDataComponents;
import com.animecore.item.AnimeItemVariants;
import com.animecore.item.AnimeWeaponItem;
import com.animecore.loot.AnimeLootHelper;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.narutorpg.content.NarutoContent.*;

/** Phase 3: data-component items, Lootr-safe loot functions, injection. */
@SuppressWarnings("removal") // createMockCreativeServerPlayerInWorld is the 1.21.1 API
public final class Phase3GameTests implements FabricGameTest {

    private static ServerPlayerEntity uchiha(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        power.setLineage(UCHIHA.getValue());
        power.setClanSkill(UCHIHA.getValue(), 3, 0); // Sharingan unlocks at clan skill level 3
        com.animecore.clan.ClanAbilityManager.evaluate(player, power);
        power.serverTick();
        return player;
    }

    private static List<ItemStack> roll(TestContext ctx, RegistryKey<LootTable> key, long seed) {
        LootTable table = ctx.getWorld().getServer().getReloadableRegistries().getLootTable(key);
        LootContextParameterSet params = new LootContextParameterSet.Builder(ctx.getWorld())
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(ctx.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN)))
                .build(LootContextTypes.CHEST);
        return table.generateLoot(params, seed);
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void jutsuScrollTeachesAbility(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ItemStack scroll = AnimeItemVariants.scroll(NarutoItems.JUTSU_SCROLL, FIREBALL_JUTSU.getValue());
        scroll.setCount(2);
        player.setStackInHand(Hand.MAIN_HAND, scroll);
        NarutoItems.JUTSU_SCROLL.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(power.unlockedAbilities().contains(FIREBALL_JUTSU.getValue()), "scroll did not teach fireball");
        ctx.assertTrue(scroll.getCount() == 1, "scroll not consumed");
        NarutoItems.JUTSU_SCROLL.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(scroll.getCount() == 1, "an already-known scroll must not be consumed");
        // Servers have no mod translations: check the translatable structure instead of the rendered string.
        var content = scroll.getName().getContent();
        ctx.assertTrue(content instanceof net.minecraft.text.TranslatableTextContent t
                && java.util.Arrays.toString(t.getArgs()).contains("ability.narutorpg.fireball_jutsu"), "scroll name should embed the ability");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void shinobiCacheRollsComponentsPerSeed(TestContext ctx) {
        Set<Identifier> abilities = new HashSet<>();
        Set<Identifier> affinities = new HashSet<>();
        var rm = ctx.getWorld().getRegistryManager();
        for (long seed = 0; seed < 200; seed++) { // Lootr = one seed per player
            for (ItemStack stack : roll(ctx, NarutoItems.SHINOBI_CACHE, seed)) {
                if (stack.isOf(NarutoItems.JUTSU_SCROLL)) {
                    Identifier id = AbilityScrollItem.ability(stack).orElse(null);
                    ctx.assertTrue(id != null, "scroll without ability component");
                    var def = AnimeRegistries.ability(rm, id).orElseThrow().value();
                    ctx.assertTrue(def.system().equals("narutorpg") && def.requiredTier() <= 3, "bad scroll " + id);
                    abilities.add(id);
                }
                if (stack.getItem() instanceof AnimeWeaponItem) {
                    var infusion = AnimeWeaponItem.infusion(stack);
                    ctx.assertTrue(infusion.affinity().isPresent() && !infusion.active(), "weapon infusion not rolled");
                    affinities.add(infusion.affinity().get());
                }
            }
        }
        ctx.assertTrue(abilities.size() >= 2, "expected scroll variety across seeds, got " + abilities);
        ctx.assertTrue(affinities.size() >= 4, "expected affinity variety across seeds, got " + affinities);
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void vanillaDungeonGetsShinobiLoot(TestContext ctx) {
        int hits = 0;
        for (long seed = 0; seed < 150; seed++) {
            for (ItemStack stack : roll(ctx, AnimeLootHelper.vanilla("chests/simple_dungeon"), seed)) {
                if (Registries.ITEM.getId(stack.getItem()).getNamespace().equals("narutorpg")) hits++;
            }
        }
        ctx.assertTrue(hits > 5, "shinobi cache was not injected into simple_dungeon (hits=" + hits + ")");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void infusedChakraBladeBonusDamageAndCost(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        AnimeWeaponItem blade = (AnimeWeaponItem) NarutoItems.CHAKRA_BLADE;
        ItemStack stack = new ItemStack(blade);
        player.setStackInHand(Hand.MAIN_HAND, stack);
        ZombieEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 1, 2, 1);

        var source = player.getDamageSources().playerAttack(player);
        ctx.assertTrue(blade.getBonusAttackDamage(zombie, 0f, source) == 0f, "dormant weapon gives no bonus");
        float before = power.pool(CHAKRA).orElseThrow().current();
        ctx.assertTrue(blade.toggleInfusion(player, stack), "infusion failed");
        ctx.assertTrue(FIRE.getValue().equals(AnimeWeaponItem.infusion(stack).affinity().orElse(null)), "should adopt uchiha fire");
        ctx.assertTrue(Math.abs(before - power.pool(CHAKRA).orElseThrow().current() - 10f) < 1e-3, "toggle cost");
        float bonus = blade.getBonusAttackDamage(zombie, 0f, source);
        ctx.assertTrue(Math.abs(bonus - 4f * 1.15f) < 1e-3, "expected 4.6 bonus (4 x uchiha fire), got " + bonus);
        float mid = power.pool(CHAKRA).orElseThrow().current();
        blade.postHit(stack, zombie, player);
        ctx.assertTrue(Math.abs(mid - power.pool(CHAKRA).orElseThrow().current() - 2f) < 1e-3, "hit cost not paid");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void infusedBladeDeflectsFrontalArrows(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        NarutoRPG.CTX.power(player).setLineage(NarutoRPG.CTX.id("akimichi")); // no random evasion trait: deterministic hits
        Vec3d at = ctx.getAbsolute(new Vec3d(0.5, 100, 0.5));
        player.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f); // facing +Z
        ItemStack stack = AnimeItemVariants.infused(NarutoItems.CHAKRA_BLADE, FIRE.getValue(), true);
        player.setStackInHand(Hand.MAIN_HAND, stack);

        ArrowEntity front = new ArrowEntity(EntityType.ARROW, ctx.getWorld());
        front.setPosition(at.add(0, 1.5, 1.5));
        front.setVelocity(0, 0, -2);
        boolean allowed = ServerLivingEntityEvents.ALLOW_DAMAGE.invoker()
                .allowDamage(player, player.getDamageSources().arrow(front, null), 4f);
        ctx.assertTrue(!allowed, "frontal arrow should be deflected");
        ctx.assertTrue(front.getVelocity().z > 0, "arrow should be sent back");

        ArrowEntity back = new ArrowEntity(EntityType.ARROW, ctx.getWorld());
        back.setPosition(at.add(0, 1.5, -1.5));
        ctx.assertTrue(ServerLivingEntityEvents.ALLOW_DAMAGE.invoker()
                .allowDamage(player, player.getDamageSources().arrow(back, null), 4f), "arrows from behind must hit");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void chargedSlashHitsTargetsInFront(TestContext ctx) {
        ServerPlayerEntity player = uchiha(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ZombieEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 1, 2, 3);
        zombie.setAiDisabled(true);
        Vec3d z = zombie.getPos();
        player.refreshPositionAndAngles(z.x, z.y, z.z - 3, 0f, 0f);
        ItemStack stack = AnimeItemVariants.infused(NarutoItems.CHAKRA_BLADE, FIRE.getValue(), true);
        float health = zombie.getHealth();
        ((AnimeWeaponItem) NarutoItems.CHAKRA_BLADE).chargeSlash(ctx.getWorld(), player, power, stack, 1f);
        ctx.assertTrue(zombie.getHealth() < health, "charged slash missed (health " + zombie.getHealth() + ")");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void leafHeadbandPledgesFaction(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        ItemStack headband = new ItemStack(NarutoItems.LEAF_HEADBAND);
        ctx.assertTrue(headband.get(AnimeDataComponents.FACTION_BADGE) != null, "default badge component missing");
        player.setStackInHand(Hand.MAIN_HAND, headband);
        NarutoItems.LEAF_HEADBAND.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        ctx.assertTrue(HIDDEN_LEAF.getValue().equals(power.faction().orElse(null)), "faction not set");
        ctx.assertTrue(power.unlockedAbilities().contains(SUBSTITUTION_JUTSU.getValue()), "faction starting ability");
        ctx.complete();
    }
}
