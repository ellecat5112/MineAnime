package com.narutorpg.test;

import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.origin.OriginSelectionService;
import com.animecore.origin.OriginSelectionService.Result;
import com.animecore.origin.VillageAccessManager;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.FactionDefinition;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoItems;
import com.narutorpg.content.NarutoTools;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

/** Village origins & backgrounds: data, selection flow, starter kit, gear / jutsu gating, rogue scratch, persistence. */
@SuppressWarnings("removal") // createMockCreativeServerPlayerInWorld is the 1.21.1 API
public final class VillageGameTests implements FabricGameTest {
    private static final Identifier LEAF = NarutoRPG.CTX.id("hidden_leaf");
    private static final Identifier SAND = NarutoRPG.CTX.id("hidden_sand");
    private static final Identifier MIST = NarutoRPG.CTX.id("hidden_mist");
    private static final Identifier ROGUE = NarutoRPG.CTX.id("missing_nin");
    private static final Identifier PRODIGY = NarutoRPG.CTX.id("academy_prodigy");
    private static final Identifier ORPHAN = NarutoRPG.CTX.id("shinobi_orphan");

    private static ServerPlayerEntity player(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        return player;
    }

    private static PlayerPowerComponent power(ServerPlayerEntity p) { return NarutoRPG.CTX.power(p); }

    private static int count(ServerPlayerEntity p, java.util.function.Predicate<ItemStack> f) {
        int n = 0;
        for (int i = 0; i < p.getInventory().size(); i++) if (f.test(p.getInventory().getStack(i))) n += p.getInventory().getStack(i).getCount();
        return n;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void everyVillageAndBackgroundLoads(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        var factions = OriginSelectionService.factions(rm, NarutoRPG.MOD_ID);
        ctx.assertTrue(factions.size() == 41, "40 wiki villages + missing-nin, got " + factions.size());
        ctx.assertTrue(OriginSelectionService.backgrounds(rm, NarutoRPG.MOD_ID).size() == 5, "5 backgrounds");
        ctx.assertTrue(OriginSelectionService.rogueFaction(rm, NarutoRPG.MOD_ID).orElseThrow().equals(ROGUE), "rogue faction");
        for (var e : factions) {
            FactionDefinition d = e.getValue();
            for (Identifier a : d.jutsuPool()) ctx.assertTrue(AnimeRegistries.ability(rm, a).isPresent(), e.getKey() + " pool " + a);
            for (Identifier i : d.exclusive().items()) ctx.assertTrue(Registries.ITEM.containsId(i), e.getKey() + " gear " + i);
        }
        FactionDefinition sand = AnimeRegistries.faction(rm, SAND).orElseThrow();
        ctx.assertTrue(sand.kind().equals("hidden_village") && !sand.jutsuPool().isEmpty() && !sand.exclusive().items().isEmpty(), "Sand data");
        ctx.assertTrue(!AnimeRegistries.faction(rm, NarutoRPG.CTX.id("mount_myoboku")).orElseThrow().selectable(), "sage realms not selectable");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void choosingAnOriginGrantsKitStandingsAndJutsu(TestContext ctx) {
        ServerPlayerEntity p = player(ctx);
        PlayerPowerComponent power = power(p);
        ctx.assertTrue(!power.originSelected(), "new players have no origin yet");
        int tier = power.tier();
        ctx.assertTrue(OriginSelectionService.select(p, power, SAND, PRODIGY, false) == Result.OK, "select failed");
        ctx.assertTrue(power.faction().orElseThrow().equals(SAND) && power.background().orElseThrow().equals(PRODIGY), "origin stored");
        ctx.assertTrue(count(p, s -> s.isOf(NarutoItems.FOREHEAD_PROTECTOR) && s.get(AnimeDataComponents.FACTION_BADGE).faction().equals(SAND)
                && s.contains(DataComponentTypes.DYED_COLOR)) == 1, "Sand headband in the village colour");
        ctx.assertTrue(count(p, s -> s.isOf(NarutoItems.FLAK_JACKET)) == 1, "flak jacket");
        ctx.assertTrue(count(p, s -> s.isOf(NarutoTools.item("kunai"))) == 8, "starting kunai");
        ctx.assertTrue(power.reputation(LEAF) == 50 && power.reputation(SAND) == 100, "standings: Leaf ally +50, home +100");
        ctx.assertTrue(power.skillPoints() >= 3 && power.tier() == tier + 2, "prodigy bonus skill points / rank");
        ctx.assertTrue(power.unlockedAbilities().contains(NarutoRPG.CTX.id("puppet_technique")), "first village jutsu");
        ctx.assertTrue(power.unlockedAbilities().contains(NarutoRPG.CTX.id("clone_technique")), "background jutsu");
        ctx.assertTrue(OriginSelectionService.select(p, power, LEAF, ORPHAN, false) == Result.ALREADY_CHOSEN, "no re-submission");

        ServerPlayerEntity q = player(ctx);
        ctx.assertTrue(OriginSelectionService.select(q, power(q), ROGUE, PRODIGY, false) == Result.BACKGROUND_NOT_ALLOWED, "prodigy needs a village");
        ctx.assertTrue(OriginSelectionService.select(q, power(q), NarutoRPG.CTX.id("mount_myoboku"), ORPHAN, false) == Result.NOT_SELECTABLE, "sage realm");
        ctx.assertTrue(OriginSelectionService.randomize(q, power(q), false) == Result.OK, "randomize");
        ctx.assertTrue(power(q).originSelected(), "random origin applied");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void villageGearIsGatedForOutsiders(TestContext ctx) {
        ServerPlayerEntity sand = player(ctx), leaf = player(ctx), mist = player(ctx);
        OriginSelectionService.select(sand, power(sand), SAND, ORPHAN, false);
        OriginSelectionService.select(leaf, power(leaf), LEAF, ORPHAN, false);
        OriginSelectionService.select(mist, power(mist), MIST, ORPHAN, false);
        ItemStack karasu = new ItemStack(NarutoTools.item("karasu"));       // Sand-exclusive puppet (by its users' affiliation)
        ctx.assertTrue(VillageAccessManager.access(sand, karasu) == VillageAccessManager.Access.FULL, "members: full use");
        ctx.assertTrue(VillageAccessManager.access(leaf, karasu) == VillageAccessManager.Access.FULL, "allies (+50) may use it");
        ctx.assertTrue(VillageAccessManager.access(mist, karasu) == VillageAccessManager.Access.RESTRICTED, "outsiders restricted");
        ctx.assertTrue(VillageAccessManager.damageMultiplier(mist, karasu) == 0.5f, "outsiders deal half damage");
        mist.setStackInHand(Hand.MAIN_HAND, karasu);
        ctx.assertTrue(!VillageAccessManager.canUseAbilities(mist, karasu), "no moves / infusion for outsiders");
        power(mist).setReputation(SAND, 60);
        ctx.assertTrue(VillageAccessManager.access(mist, karasu) == VillageAccessManager.Access.FULL, "earned reputation unlocks it");

        // a foreign uniform is taken off by the equipment check
        ItemStack sandBand = new ItemStack(NarutoItems.FOREHEAD_PROTECTOR);
        sandBand.set(AnimeDataComponents.FACTION_BADGE, new AnimeDataComponents.FactionBadge(SAND));
        ServerPlayerEntity cloudNin = player(ctx);
        OriginSelectionService.select(cloudNin, power(cloudNin), NarutoRPG.CTX.id("hidden_cloud"), ORPHAN, false);
        cloudNin.equipStack(EquipmentSlot.HEAD, sandBand);
        ctx.assertTrue(VillageAccessManager.access(cloudNin, sandBand) == VillageAccessManager.Access.RESTRICTED, "foreign uniform restricted");
        ctx.assertTrue(VillageAccessManager.enforceEquipment(cloudNin) == 1, "one piece removed");
        ctx.assertTrue(cloudNin.getEquippedStack(EquipmentSlot.HEAD).isEmpty(), "Cloud shinobi cannot wear Sand's colours");
        ctx.assertTrue(count(cloudNin, s -> s.isOf(NarutoItems.FOREHEAD_PROTECTOR) && s.get(AnimeDataComponents.FACTION_BADGE).faction().equals(SAND)) == 1,
                "headband returned to the inventory");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void villageJutsuNeedRankOrStandingForOutsiders(TestContext ctx) {
        ServerPlayerEntity mist = player(ctx);
        PlayerPowerComponent power = power(mist);
        OriginSelectionService.select(mist, power, MIST, ORPHAN, false);
        Identifier rasengan = NarutoRPG.CTX.id("rasengan");
        ctx.assertTrue(VillageAccessManager.owner(ctx.getWorld().getRegistryManager(), rasengan).equals(LEAF), "Rasengan is a Leaf technique");
        ctx.assertTrue(!AnimeCoreEvents.ALLOW_ABILITY_UNLOCK.invoker().allow(mist, power, rasengan, "scroll"), "captured scroll needs rank");
        power.setTier(5);
        ctx.assertTrue(AnimeCoreEvents.ALLOW_ABILITY_UNLOCK.invoker().allow(mist, power, rasengan, "scroll"), "rank 5 deciphers it");
        ServerPlayerEntity leaf = player(ctx);
        OriginSelectionService.select(leaf, power(leaf), LEAF, ORPHAN, false);
        ctx.assertTrue(VillageAccessManager.allowJutsu(leaf, power(leaf), rasengan), "members always may");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void scratchingTheHeadbandMakesYouMissingNin(TestContext ctx) {
        ServerPlayerEntity p = player(ctx);
        PlayerPowerComponent power = power(p);
        OriginSelectionService.select(p, power, LEAF, ORPHAN, false);
        ItemStack band = new ItemStack(NarutoItems.FOREHEAD_PROTECTOR);
        band.set(AnimeDataComponents.FACTION_BADGE, new AnimeDataComponents.FactionBadge(LEAF));
        p.setStackInHand(Hand.MAIN_HAND, band);
        p.setStackInHand(Hand.OFF_HAND, new ItemStack(NarutoTools.item("kunai")));
        p.setSneaking(true);
        NarutoItems.FOREHEAD_PROTECTOR.use(ctx.getWorld(), p, Hand.MAIN_HAND);
        ctx.assertTrue(p.getMainHandStack().get(AnimeDataComponents.FACTION_BADGE).scratched(), "headband scratched");
        ctx.assertTrue(power.rogue() && power.faction().orElseThrow().equals(ROGUE), "now missing-nin");
        ctx.assertTrue(power.formerFaction().orElseThrow().equals(LEAF), "former village remembered");
        ctx.assertTrue(power.reputation(LEAF) == 0 && power.reputation(SAND) == 25, "Leaf -100, its ally Sand -25");
        ctx.assertTrue(VillageAccessManager.access(p, p.getMainHandStack()) == VillageAccessManager.Access.FULL, "rogues may wear the scratched band");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void originStateSurvivesRespawnCopy(TestContext ctx) {
        ServerPlayerEntity p = player(ctx);
        OriginSelectionService.select(p, power(p), SAND, ORPHAN, false);
        NbtCompound tag = new NbtCompound();
        power(p).writeToNbt(tag, ctx.getWorld().getRegistryManager());
        ServerPlayerEntity q = player(ctx);
        power(q).readFromNbt(tag, ctx.getWorld().getRegistryManager());
        ctx.assertTrue(power(q).originSelected() && power(q).background().orElseThrow().equals(ORPHAN)
                && power(q).faction().orElseThrow().equals(SAND) && power(q).reputation(LEAF) == 50, "origin copied");
        NbtCompound v2 = new NbtCompound();
        v2.putInt("schema_version", 2);
        v2.putString("faction", LEAF.toString());
        power(q).readFromNbt(v2, ctx.getWorld().getRegistryManager());
        ctx.assertTrue(power(q).originSelected(), "v2 player with a faction counts as having chosen an origin");
        ctx.complete();
    }
}
