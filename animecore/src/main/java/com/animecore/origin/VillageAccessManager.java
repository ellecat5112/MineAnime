package com.animecore.origin;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.FactionDefinition;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Village access control, driven entirely by {@link FactionDefinition} data (no per-village code):
 * <ul>
 *   <li><b>Gear</b>: an item's village comes from its {@code animecore:faction_alignment} component, else an unscratched
 *       {@code animecore:faction_badge} (headbands / uniforms), else the faction's {@code exclusive.items} / tag.
 *       Members, allies (reputation &gt;= threshold) and - for scratched headbands / {@code rogue_ok} gear - missing-nin get
 *       full use; outsiders deal half damage, cannot use moves or chakra infusion, and cannot wear foreign uniforms.</li>
 *   <li><b>Jutsu</b>: {@code jutsu_pool} + {@code exclusive.abilities} unlock by rank for members; outsiders can only learn
 *       them from captured scrolls at {@code outsider_tier} or with allied reputation, and cannot cast them below it.</li>
 * </ul>
 */
public final class VillageAccessManager {
    private VillageAccessManager() {}

    public enum Access { FULL, RESTRICTED }

    /** A resolved requirement: the owning faction and the reputation an outsider needs. */
    public record Requirement(Identifier faction, int minReputation, boolean rogueOk) {}

    private record Index(Map<Item, Identifier> items, Map<Identifier, Identifier> abilities) {}

    private static final Map<DynamicRegistryManager, Index> INDEX = new WeakHashMap<>();

    public static void register() {
        AnimeCoreEvents.ALLOW_ABILITY_UNLOCK.register(VillageAccessManager::allowUnlock);
        AnimeCoreEvents.PRE_ABILITY_CAST.register((player, ability, ctx) -> {
            Identifier id = ability.getKey().map(RegistryKey::getValue).orElse(null);
            if (id == null || allowJutsu(player, ctx.power(), id)) return ActionResult.PASS;
            warn(player, "message.animecore.village.jutsu_locked", factionName(player, owner(player.getRegistryManager(), id)));
            return ActionResult.FAIL;
        });
        ServerTickEvents.END_SERVER_TICK.register(VillageAccessManager::tick);
    }

    // ------------------------------------------------------------------ index

    private static synchronized Index index(DynamicRegistryManager rm) {
        return INDEX.computeIfAbsent(rm, r -> {
            Map<Item, Identifier> items = new HashMap<>();
            Map<Identifier, Identifier> abilities = new HashMap<>();
            Registry<FactionDefinition> reg = r.get(AnimeRegistries.FACTION);
            for (var e : reg.getEntrySet()) {
                Identifier fid = e.getKey().getValue();
                FactionDefinition def = e.getValue();
                for (Identifier item : def.exclusive().items()) Registries.ITEM.getOrEmpty(item).ifPresent(i -> items.putIfAbsent(i, fid));
                def.exclusive().itemTag().ifPresent(tag -> Registries.ITEM.iterateEntries(tag).forEach(en -> items.putIfAbsent(en.value(), fid)));
                for (Identifier a : def.jutsuPool()) abilities.putIfAbsent(a, fid);
                for (Identifier a : def.exclusive().abilities()) abilities.put(a, fid);
            }
            return new Index(items, abilities);
        });
    }

    /** Call after datapack reloads change factions. */
    public static synchronized void invalidate() { INDEX.clear(); }

    /** Village owning this ability (pool or exclusive), if any. */
    public static @Nullable Identifier owner(DynamicRegistryManager rm, Identifier ability) {
        return index(rm).abilities().get(ability);
    }

    public static Optional<Requirement> requirement(DynamicRegistryManager rm, ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        AnimeDataComponents.FactionAlignment align = stack.get(AnimeDataComponents.FACTION_ALIGNMENT);
        if (align != null) return Optional.of(new Requirement(align.faction(), align.minReputation(), align.rogueOk()));
        AnimeDataComponents.FactionBadge badge = stack.get(AnimeDataComponents.FACTION_BADGE);
        if (badge != null) return Optional.of(new Requirement(badge.faction(), threshold(rm, badge.faction()), badge.scratched()));
        Identifier owner = index(rm).items().get(stack.getItem());
        return owner == null ? Optional.empty() : Optional.of(new Requirement(owner, threshold(rm, owner), false));
    }

    private static int threshold(DynamicRegistryManager rm, Identifier faction) {
        return AnimeRegistries.faction(rm, faction).map(d -> d.exclusive().alliedReputation()).orElse(50);
    }

    // ------------------------------------------------------------------ access checks (both sides; owner has full sync)

    public static Access access(PlayerEntity player, ItemStack stack) {
        DynamicRegistryManager rm = player.getWorld().getRegistryManager();
        Optional<Requirement> req = requirement(rm, stack);
        if (req.isEmpty()) return Access.FULL;
        Optional<FactionDefinition> def = AnimeRegistries.faction(rm, req.get().faction());
        if (def.isEmpty()) return Access.FULL;
        Optional<AnimeModContext> ctx = AnimeModRegistry.get(def.get().system());
        if (ctx.isEmpty()) return Access.FULL;
        PlayerPowerComponent power = ctx.get().power(player);
        return allowed(power, req.get()) ? Access.FULL : Access.RESTRICTED;
    }

    public static boolean allowed(PlayerPowerComponent power, Requirement req) {
        if (power.rogue()) {
            if (req.rogueOk()) return true;
            // missing-nin keep nothing of their former village beyond the scratched headband
        } else if (power.faction().map(req.faction()::equals).orElse(false)) {
            return true;
        }
        return power.reputation(req.faction()) >= req.minReputation();
    }

    /** Moves / infusion gate for weapon {@code use}: warns on the server, returns false when blocked. */
    public static boolean canUseAbilities(PlayerEntity player, ItemStack stack) {
        if (access(player, stack) == Access.FULL) return true;
        if (player instanceof ServerPlayerEntity sp) {
            requirement(sp.getRegistryManager(), stack).ifPresent(r ->
                    warn(sp, "message.animecore.village.gear_locked", factionName(sp, r.faction())));
        }
        return false;
    }

    /** Damage multiplier for a weapon in the attacker's hands (outsiders: 50%). */
    public static float damageMultiplier(@Nullable Entity attacker, ItemStack weapon) {
        return attacker instanceof PlayerEntity p && access(p, weapon) == Access.RESTRICTED ? 0.5f : 1f;
    }

    /** May this player cast / keep casting {@code ability}? */
    public static boolean allowJutsu(ServerPlayerEntity player, PlayerPowerComponent power, Identifier ability) {
        DynamicRegistryManager rm = player.getRegistryManager();
        Identifier owner = owner(rm, ability);
        if (owner == null) return true;
        if (!power.rogue() && power.faction().map(owner::equals).orElse(false)) return true;
        if (power.formerFaction().map(owner::equals).orElse(false)) return true;   // missing-nin keep what they learned
        if (power.clanGrantedAbilities().contains(ability)) return true;            // clan techniques are the clan's, not the village's
        Optional<FactionDefinition> def = AnimeRegistries.faction(rm, owner);
        if (def.isEmpty()) return true;
        return power.tier() >= def.get().exclusive().outsiderTier() || power.reputation(owner) >= def.get().exclusive().alliedReputation();
    }

    private static boolean allowUnlock(ServerPlayerEntity player, PlayerPowerComponent power, Identifier ability, String source) {
        if (allowJutsu(player, power, ability)) return true;
        Identifier owner = owner(player.getRegistryManager(), ability);
        int tier = AnimeRegistries.faction(player.getRegistryManager(), owner).map(d -> d.exclusive().outsiderTier()).orElse(5);
        warn(player, "message.animecore.village.scroll_locked", factionName(player, owner), tier);
        return false;
    }

    // ------------------------------------------------------------------ village jutsu progression

    /**
     * Grants the member's village techniques that their rank allows: the first two at once, then one more every two
     * tiers (never before the ability's own required tier). Idempotent; called on origin selection and tier change.
     */
    public static void evaluatePool(ServerPlayerEntity player, PlayerPowerComponent power) {
        if (power.rogue() || power.faction().isEmpty()) return;
        DynamicRegistryManager rm = player.getRegistryManager();
        FactionDefinition def = AnimeRegistries.faction(rm, power.faction().get()).orElse(null);
        if (def == null) return;
        List<Identifier> pool = def.jutsuPool();
        for (int i = 0; i < pool.size(); i++) {
            int abilityTier = AnimeRegistries.ability(rm, pool.get(i)).map(e -> e.value().requiredTier()).orElse(1);
            int gate = Math.max(abilityTier, i < 2 ? 1 : 2 * i - 1);
            if (power.tier() < gate) continue;
            if (power.unlockAbility(pool.get(i))) {
                AnimeRegistries.ability(rm, pool.get(i)).map(e -> e.value().name()).ifPresent(n -> player.sendMessage(
                        Text.translatable("message.animecore.village.jutsu_learned", n).formatted(Formatting.AQUA), false));
            }
        }
    }

    // ------------------------------------------------------------------ equipment enforcement

    private static void tick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) enforceEquipment(player);
    }

    /** Takes off foreign village uniforms (survival / adventure only). @return number of pieces removed */
    public static int enforceEquipment(ServerPlayerEntity player) {
        if (!player.interactionManager.getGameMode().isSurvivalLike()) return 0;   // creative / spectator exempt
        int removed = 0;
        {
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack worn = player.getEquippedStack(slot);
                if (!worn.contains(AnimeDataComponents.FACTION_BADGE) || access(player, worn) == Access.FULL) continue;
                ItemStack moved = worn.copy();
                player.equipStack(slot, ItemStack.EMPTY);
                if (!player.getInventory().insertStack(moved)) player.dropItem(moved, false);
                removed++;
                requirement(player.getRegistryManager(), moved).ifPresent(r ->
                        warn(player, "message.animecore.village.uniform_locked", factionName(player, r.faction())));
            }
        }
        return removed;
    }

    // ------------------------------------------------------------------ rogue scratch

    /**
     * Scratches a village headband: the badge is defaced, the wearer becomes missing-nin (their system's rogue
     * faction), home reputation drops by 100 and every ally of the home village loses 25.
     */
    public static boolean scratch(ServerPlayerEntity player, ItemStack headband) {
        AnimeDataComponents.FactionBadge badge = headband.get(AnimeDataComponents.FACTION_BADGE);
        if (badge == null || badge.scratched()) return false;
        Optional<FactionDefinition> def = AnimeRegistries.faction(player.getRegistryManager(), badge.faction());
        if (def.isEmpty() || def.get().rogue()) return false;
        AnimeModContext ctx = AnimeModRegistry.get(def.get().system()).orElse(null);
        if (ctx == null) return false;
        PlayerPowerComponent power = ctx.power(player);
        if (!power.faction().map(badge.faction()::equals).orElse(false) && !player.isCreative()) {
            warn(player, "message.animecore.village.not_yours", def.get().name());
            return false;
        }
        headband.set(AnimeDataComponents.FACTION_BADGE, badge.scratch());
        OriginSelectionService.goRogue(player, power, badge.faction());
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.PLAYERS, 0.8f, 1.4f);
        return true;
    }

    // ------------------------------------------------------------------ helpers

    static Text factionName(PlayerEntity player, @Nullable Identifier faction) {
        if (faction == null) return Text.literal("?");
        return AnimeRegistries.faction(player.getWorld().getRegistryManager(), faction).map(FactionDefinition::name)
                .orElse(Text.translatable(Ids.translationKey("faction", faction)));
    }

    private static void warn(ServerPlayerEntity player, String key, Object... args) {
        player.sendMessage(Text.translatable(key, args).formatted(Formatting.RED), true);
    }
}
