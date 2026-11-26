package com.narutorpg.test;

import com.animecore.component.PlayerPowerComponent;
import com.animecore.entity.PuppetEntity;
import com.animecore.entity.ThrownToolEntity;
import com.animecore.tool.MoveSpec;
import com.animecore.tool.MoveType;
import com.animecore.tool.ToolBehaviour;
import com.animecore.tool.ToolHolder;
import com.animecore.tool.ToolSpec;
import com.animecore.ward.WardBlocks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoTools;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.narutorpg.content.NarutoContent.CHAKRA;

/** Narutopedia Category:Tools: every tool registered, textured, translated, and both moves executable. */
@SuppressWarnings("removal") // createMockCreativeServerPlayerInWorld is the 1.21.1 API
public final class NinjaToolGameTests implements FabricGameTest {
    private static final net.minecraft.util.Identifier CHAKRA_ID = com.narutorpg.content.NarutoContent.CHAKRA;

    private static ServerPlayerEntity survival(TestContext ctx, Vec3d rel) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        Vec3d at = ctx.getAbsolute(rel);
        player.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        alliedEverywhere(ctx, player);
        return player;
    }

    /** Tool behaviour tests are about the moves, not village access: make the player welcome in every village. */
    static void alliedEverywhere(TestContext ctx, ServerPlayerEntity player) {
        var power = NarutoRPG.CTX.power(player);
        com.animecore.origin.OriginSelectionService.factions(ctx.getWorld().getRegistryManager(), NarutoRPG.MOD_ID)
                .forEach(e -> power.setReputation(e.getKey(), 100));
    }

    private static void face(ServerPlayerEntity player, Vec3d target) {
        Vec3d d = target.subtract(player.getEyePos());
        float yaw = (float) (MathHelper.atan2(d.z, d.x) * MathHelper.DEGREES_PER_RADIAN) - 90f;
        float pitch = (float) -(MathHelper.atan2(d.y, Math.sqrt(d.x * d.x + d.z * d.z)) * MathHelper.DEGREES_PER_RADIAN);
        player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), yaw, pitch);
        player.setHeadYaw(yaw);
    }

    private static ItemStack hold(ServerPlayerEntity player, String id, int count) {
        ItemStack stack = new ItemStack(NarutoTools.item(id), count);
        player.setStackInHand(Hand.MAIN_HAND, stack);
        return stack;
    }

    // ------------------------------------------------------------------ catalog integrity

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void everyWikiToolIsRegisteredTexturedAndTranslated(TestContext ctx) throws Exception {
        JsonObject lang;
        try (var in = getClass().getResourceAsStream("/assets/narutorpg/lang/en_us.json")) {
            lang = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
        ctx.assertTrue(NarutoTools.specs().size() == 244, "expected 244 catalog tools, got " + NarutoTools.specs().size());
        // 244 wiki tools; chakra blade, forehead protector and flak jacket are provided by other systems
        ctx.assertTrue(NarutoTools.items().size() == 241, "expected 241 new items (+ 3 existing), got " + NarutoTools.items().size());
        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, ToolSpec> e : NarutoTools.specs().entrySet()) {
            String id = e.getKey();
            ToolSpec spec = e.getValue();
            if (spec.secondary().isEmpty()) problems.add(id + ": no secondary move");
            if (spec.armor().isEmpty() && spec.attack().damage() <= 0) problems.add(id + ": no attack");
            if (getClass().getResource("/assets/narutorpg/textures/item/" + id + ".png") == null) problems.add(id + ": texture");
            if (getClass().getResource("/assets/narutorpg/models/item/" + id + ".json") == null) problems.add(id + ": model");
            if (!lang.has("item.narutorpg." + id)) problems.add(id + ": lang");
        }
        ctx.assertTrue(problems.isEmpty(), "catalog problems: " + problems);
        ctx.complete();
    }

    /** Fires the primary and secondary move of all 244 tools; any exception fails the test. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 400)
    public void everyToolMoveExecutesWithoutError(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld(); // creative: no costs, no consumption
        alliedEverywhere(ctx, player);
        Vec3d at = ctx.getAbsolute(new Vec3d(0.5, 60, 0.5));
        player.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 30f);
        BlockPos floor = BlockPos.ofFloored(at).down();
        ctx.getWorld().setBlockState(floor, Blocks.STONE.getDefaultState());
        List<String> failures = new ArrayList<>();
        int executed = 0;
        for (Map.Entry<String, Item> e : NarutoTools.items().entrySet()) {
            Item item = e.getValue();
            ToolHolder holder = (ToolHolder) item;
            for (boolean sneak : new boolean[]{false, true}) {
                ItemStack stack = new ItemStack(item, Math.min(4, item.getMaxCount()));
                player.setStackInHand(Hand.MAIN_HAND, stack);
                player.setSneaking(sneak);
                player.getItemCooldownManager().remove(item);
                try {
                    MoveSpec m = ToolBehaviour.choose(holder.spec(), sneak).orElse(null);
                    if (m == null) continue;
                    if (m.type() == MoveType.WARD) {
                        item.useOnBlock(new ItemUsageContext(player, Hand.MAIN_HAND,
                                new BlockHitResult(Vec3d.ofCenter(floor), Direction.UP, floor, false)));
                    } else {
                        item.use(ctx.getWorld(), player, Hand.MAIN_HAND);
                        if (player.isUsingItem()) {
                            if (m.type() == MoveType.CONSUME) item.finishUsing(stack, ctx.getWorld(), player);
                            else item.onStoppedUsing(stack, ctx.getWorld(), player, 72000 - 40);
                            player.clearActiveItem();
                        }
                    }
                    executed++;
                } catch (RuntimeException ex) {
                    failures.add(e.getKey() + (sneak ? " [sneak]" : "") + ": " + ex);
                }
            }
        }
        player.setSneaking(false);
        // (scheduled tasks expire by themselves; clearing them would break concurrently running tests)
        ctx.getWorld().getEntitiesByClass(Entity.class, new Box(BlockPos.ofFloored(at)).expand(64),
                en -> en instanceof PuppetEntity || en instanceof ThrownToolEntity).forEach(Entity::discard);
        ctx.assertTrue(failures.isEmpty(), failures.size() + " move failures: " + failures);
        ctx.assertTrue(executed >= 480, "expected ~488 moves executed, got " + executed);
        ctx.complete();
    }

    // ------------------------------------------------------------------ signature mechanics

    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void kunaiCanBeThrown(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 80, 0.5));
        ZombieEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 0, 2, 0);
        zombie.setAiDisabled(true);
        zombie.setNoGravity(true);
        zombie.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ() + 5, 0, 0);
        face(player, zombie.getPos().add(0, 1.2, 0));
        ItemStack kunai = hold(player, "kunai", 3);
        float health = zombie.getHealth();
        NarutoTools.item("kunai").use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(kunai.getCount() == 2, "thrown kunai should leave the stack");
        ctx.runAtTick(30, () -> {
            ctx.assertTrue(zombie.getHealth() < health, "thrown kunai did not hit (" + zombie.getHealth() + ")");
            ctx.complete();
        });
    }

    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 400)
    public void barrierTagsWallThenGateThenCollapse(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 100, 0.5));
        var world = ctx.getWorld();
        BlockPos base = BlockPos.ofFloored(player.getPos()).down();
        for (int x = -2; x <= 12; x++) for (int z = -2; z <= 4; z++) world.setBlockState(base.add(x, 0, z), Blocks.STONE.getDefaultState());
        ItemStack tags = hold(player, "barrier_tag", 8);
        Item item = NarutoTools.item("barrier_tag");
        java.util.function.Consumer<BlockPos> place = floor -> {
            player.getItemCooldownManager().remove(item);
            item.useOnBlock(new ItemUsageContext(player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(floor), Direction.UP, floor, false)));
        };
        place.accept(base);
        ctx.assertTrue(world.getBlockState(base.up()).isOf(WardBlocks.WARD_TAG), "tag not placed");
        long walls = BlockPos.stream(base.add(-3, 1, -3), base.add(3, 4, 3)).filter(p -> world.getBlockState(p).isOf(WardBlocks.CHAKRA_WALL)).count();
        ctx.assertTrue(walls >= 10, "single tag should raise a temporary wall, got " + walls);
        place.accept(base.add(4, 0, 0));
        place.accept(base.add(8, 0, 0));
        ctx.assertTrue(tags.getCount() == 5, "three tags should be consumed");
        long gate = BlockPos.stream(base.add(0, 1, 0), base.add(8, 4, 0)).filter(p -> world.getBlockState(p).isOf(WardBlocks.BARRIER_GATE)).count();
        ctx.assertTrue(gate >= 20, "three tags in sequence should form a Barrier Gate, got " + gate);
        world.breakBlock(base.add(4, 1, 0), false);
        ctx.runAtTick(5, () -> {
            long left = BlockPos.stream(base.add(0, 1, 0), base.add(8, 5, 0)).filter(p -> world.getBlockState(p).isOf(WardBlocks.BARRIER_GATE)).count();
            ctx.assertTrue(left == 0, "breaking a linked tag must collapse the gate, " + left + " blocks left");
        });
        ctx.runAtTick(250, () -> {
            long remaining = BlockPos.stream(base.add(-3, 1, -3), base.add(3, 4, 3)).filter(p -> world.getBlockState(p).isOf(WardBlocks.CHAKRA_WALL)).count();
            ctx.assertTrue(remaining == 0, "temporary wall should fade, " + remaining + " left");
            ctx.complete();
        });
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void rationsPillRestoresChakra(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 2, 0.5));
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        power.setEnergy(CHAKRA, 10f);
        ItemStack pill = hold(player, "military_rations_pill", 2);
        Item item = NarutoTools.item("military_rations_pill");
        item.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(player.isUsingItem(), "pill should be eaten over time");
        item.finishUsing(pill, ctx.getWorld(), player);
        ctx.assertTrue(power.pool(CHAKRA).orElseThrow().current() >= 59f, "rations pill should restore 50 chakra");
        ctx.assertTrue(pill.getCount() == 1, "pill consumed");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void gunbaiGuardsFrontOnly(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 90, 0.5));
        hold(player, "gunbai", 1);
        Item item = NarutoTools.item("gunbai");
        player.setSneaking(false);
        item.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(player.isUsingItem(), "gunbai guard should be held");
        ZombieEntity front = ctx.spawnEntity(EntityType.ZOMBIE, 0, 2, 0);
        front.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ() + 2, 0, 0);
        ZombieEntity back = ctx.spawnEntity(EntityType.ZOMBIE, 0, 2, 0);
        back.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ() - 2, 0, 0);
        ctx.assertTrue(!ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(player, player.getDamageSources().mobAttack(front), 5f), "front hit should be blocked");
        ctx.assertTrue(ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(player, player.getDamageSources().mobAttack(back), 5f), "hit from behind should land");
        player.clearActiveItem();
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void karasuSummonsLoyalPuppet(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 2, 0.5));
        // GameTest places tests at random coordinates; the puppet appears 2 blocks away, which can fall into a
        // neighbouring, unloaded chunk where entity lookups cannot see it. Stand in the middle of the loaded chunk.
        player.refreshPositionAndAngles(Math.floor(player.getX() / 16) * 16 + 8.5, player.getY(), Math.floor(player.getZ() / 16) * 16 + 8.5, 0f, 0f);
        hold(player, "karasu", 1);
        NarutoTools.item("karasu").use(ctx.getWorld(), player, Hand.MAIN_HAND);
        List<PuppetEntity> puppets = ctx.getWorld().getEntitiesByClass(PuppetEntity.class, player.getBoundingBox().expand(5), p -> true);
        var dbgPower = NarutoRPG.CTX.power(player);
        ctx.assertTrue(puppets.size() == 1, "expected one puppet, got " + puppets.size() + " chakra=" + dbgPower.pool(CHAKRA_ID).map(x -> x.current()).orElse(-1f)
                + " access=" + com.animecore.origin.VillageAccessManager.access(player, player.getMainHandStack())
                + " cd=" + player.getItemCooldownManager().isCoolingDown(NarutoTools.item("karasu")) + " sneak=" + player.isSneaking()
                + " lineage=" + dbgPower.lineage() + " all=" + ctx.getWorld().getEntitiesByClass(PuppetEntity.class, player.getBoundingBox().expand(40), q -> true).size()
                + " held=" + player.getMainHandStack());
        PuppetEntity karasu = puppets.get(0);
        ctx.assertTrue(karasu.isAlly(player) && karasu.kind().equals("crow") && karasu.style().equals("poison"), "wrong puppet setup");
        ctx.assertTrue(!karasu.damage(player.getDamageSources().playerAttack(player), 5f), "puppets ignore their owner's hits");
        karasu.discard();
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void flyingThunderGodWarpsToMark(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 70, 0.5));
        BlockPos target = BlockPos.ofFloored(player.getPos()).add(0, -1, 6);
        ctx.getWorld().setBlockState(target, Blocks.STONE.getDefaultState());
        face(player, Vec3d.ofCenter(target).add(0, 0.5, 0));
        hold(player, "flying_thunder_god_kunai", 2);
        Item item = NarutoTools.item("flying_thunder_god_kunai");
        item.use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.runAtTick(25, () -> {
            player.setSneaking(true);
            player.getItemCooldownManager().remove(item);
            item.use(ctx.getWorld(), player, Hand.MAIN_HAND);
            player.setSneaking(false);
            ctx.assertTrue(player.getPos().distanceTo(Vec3d.ofCenter(target)) < 2.5, "did not warp to the seal, at " + player.getPos());
            ctx.complete();
        });
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void sealingTagSealsWeakenedFoe(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 85, 0.5));
        ZombieEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 0, 2, 0);
        zombie.setAiDisabled(true);
        zombie.setNoGravity(true);
        zombie.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ() + 3, 0, 0);
        zombie.setHealth(2f);
        face(player, zombie.getPos().add(0, 1, 0));
        hold(player, "sealing_tag", 2);
        NarutoTools.item("sealing_tag").use(ctx.getWorld(), player, Hand.MAIN_HAND);
        ctx.assertTrue(zombie.isRemoved(), "weakened zombie should be sealed away");
        ctx.complete();
    }

    /**
     * Mock GameTest players cannot be re-resolved as projectile owners after a few ticks, so the throw and the pull
     * are verified separately: the thrown hook must hit, and the chain impact must drag the target to the user.
     */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
    public void kusarigamaHooksAndPullsTarget(TestContext ctx) {
        ServerPlayerEntity player = survival(ctx, new Vec3d(0.5, 75, 0.5));
        BlockPos floor = BlockPos.ofFloored(player.getPos()).down();
        for (int z = -2; z <= 10; z++) for (int x = -2; x <= 2; x++) ctx.getWorld().setBlockState(floor.add(x, 0, z), Blocks.STONE.getDefaultState());
        ZombieEntity hooked = ctx.spawnEntity(EntityType.ZOMBIE, 0, 2, 0);
        hooked.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SLOWNESS, 400, 10));
        hooked.refreshPositionAndAngles(player.getX() - 1.5, player.getY(), player.getZ() + 8, 0, 0);
        face(player, hooked.getPos().add(0, 1, 0));
        hold(player, "kusarigama", 1);
        NarutoTools.item("kusarigama").use(ctx.getWorld(), player, Hand.MAIN_HAND);

        ZombieEntity pulled = ctx.spawnEntity(EntityType.ZOMBIE, 0, 2, 0);
        pulled.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SLOWNESS, 400, 10));
        pulled.refreshPositionAndAngles(player.getX() + 1.5, player.getY(), player.getZ() + 8, 0, 0);
        ThrownToolEntity hook = new ThrownToolEntity(ctx.getWorld(), player, new ItemStack(NarutoTools.item("kusarigama")));
        hook.chainMode = "pull";
        hook.damage = 5;
        com.animecore.tool.ToolImpacts.chainHit(hook, ctx.getWorld(), pulled);

        ctx.runAtTick(30, () -> {
            ctx.assertTrue(hooked.getHealth() < 20f, "thrown kusarigama hook should hit its target");
            double dist = Math.sqrt(pulled.squaredDistanceTo(player));
            ctx.assertTrue(dist < 4.5 && pulled.getZ() > player.getZ() - 1, "chain should drag the target to the user's feet, dist " + dist);
            ctx.complete();
        });
    }
}
