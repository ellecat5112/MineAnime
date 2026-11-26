package com.animecore.component;

import com.animecore.AnimeCore;
import com.animecore.ability.AbilityExecutor;
import com.animecore.affinity.AffinityMatrix;
import com.animecore.api.AnimeModContext;
import com.animecore.api.EnergyPoolSpec;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.clan.ClanAbilityManager;
import com.animecore.clan.ClanTraitHooks;
import com.animecore.movement.AnimeMovementHandler;
import com.animecore.movement.MovementMode;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.FactionDefinition;
import com.animecore.registry.definition.LineageDefinition;
import com.animecore.registry.definition.TransformationDefinition;
import com.animecore.transformation.TransformationService;
import com.animecore.trait.ITraitModifier;
import com.animecore.trait.TraitAccumulator;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.sync.PlayerSyncPredicate;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Per-player RPG state for <b>one</b> power system. Each anime mod gets its own instance under its own CCA key
 * ({@code narutorpg:power}, {@code bleachrpg:power}...), so Chakra and Reiatsu never share storage or sync traffic.
 * <p>
 * Sync model: the owner receives the full state; peers (Essential P2P guests included) only receive the public
 * subset needed for rendering (lineage, faction, tier, active transformation, pool ratios, affinities).
 */
public class PlayerPowerComponent implements AutoSyncedComponent, ServerTickingComponent {
    /** Bump when the NBT layout changes and add a step to {@link #migrate(NbtCompound, int)}. */
    public static final int SCHEMA_VERSION = 2;
    public static final int MAX_TIER = 100;
    public static final int COMBO_TIMEOUT_TICKS = 40;
    public static final int MAX_COMBO_LENGTH = 12;
    private static final int PRIVATE_SYNC_INTERVAL = 5;

    private final PlayerEntity player;
    private final AnimeModContext context;

    // ---- persistent ----
    private final Map<Identifier, EnergyPool> pools = new LinkedHashMap<>();
    private int tier = 1;
    private long experience;
    private int skillPoints;
    private final Set<Identifier> unlockedAbilities = new LinkedHashSet<>();
    private final Set<Identifier> unlockedTransformations = new LinkedHashSet<>();
    private final Map<Identifier, Integer> transformationMastery = new HashMap<>();
    private @Nullable Identifier lineage;
    private @Nullable Identifier faction;
    private final Map<Identifier, Integer> reputation = new HashMap<>();
    /** ability/transformation id -> absolute world time at which the cooldown ends. */
    private final Map<Identifier, Long> cooldownEnds = new HashMap<>();
    private @Nullable Identifier activeTransformation;
    private int transformationTicks;
    private final Identifier[] hotbar;
    /** We switched on vanilla flight and must switch it off again (survives relog, unlike the toggle). */
    private boolean flightGranted;

    // ---- clan / bloodline (schema v2) ----
    /** lineage -> clan skill level (kept per clan, so switching back restores progress). */
    private final Map<Identifier, Integer> clanSkillLevels = new HashMap<>();
    /** lineage -> experience towards the next clan skill level. */
    private final Map<Identifier, Long> clanSkillXp = new HashMap<>();
    /** Abilities that exist only because the current clan granted them (removed on clan switch). */
    private final Set<Identifier> clanGrantedAbilities = new LinkedHashSet<>();
    private final Set<Identifier> clanGrantedForms = new LinkedHashSet<>();
    /** Clans whose starter kit was already handed out (no farming through rerolls). */
    private final Set<Identifier> clanKitsGiven = new LinkedHashSet<>();
    /** Rolls since the last rare result (pity counter). */
    private int clanPity;
    private int clanRerolls;
    /** First-join clan assignment happened. */
    private boolean clanInitialized;

    // ---- synced, derived / session-only ----
    private final EnumSet<MovementMode> activeMovement = EnumSet.noneOf(MovementMode.class);
    private Set<Identifier> effectiveAffinities = Set.of();

    // ---- transient (server) ----
    private TraitAccumulator traits = new TraitAccumulator();
    private final List<AppliedModifier> appliedModifiers = new ArrayList<>();
    private boolean traitsDirty = true;
    private boolean privateDirty;
    private boolean publicDirty;
    private boolean energyPublicDirty;
    private int privateSyncCooldown;
    private final List<Identifier> comboBuffer = new ArrayList<>();
    private long lastComboInput;
    private @Nullable Identifier chargingAbility;
    private int chargeTicks;
    private @Nullable Identifier armedReactive;
    private int reactiveWindow;

    private record AppliedModifier(RegistryEntry<EntityAttribute> attribute, Identifier id) {}

    public PlayerPowerComponent(PlayerEntity player, AnimeModContext context) {
        this.player = player;
        this.context = context;
        this.hotbar = new Identifier[context.hotbarSize()];
        for (EnergyPoolSpec spec : context.pools()) pools.put(spec.id(), new EnergyPool(spec));
    }

    // =====================================================================================
    // Accessors
    // =====================================================================================

    public PlayerEntity player() { return player; }
    public AnimeModContext context() { return context; }
    public Collection<EnergyPool> pools() { return Collections.unmodifiableCollection(pools.values()); }
    public Optional<EnergyPool> pool(Identifier id) { return Optional.ofNullable(pools.get(id)); }
    public Optional<EnergyPool> primaryPool() { return context.pool(EnergyPoolSpec.Role.PRIMARY).map(s -> pools.get(s.id())); }
    public int tier() { return tier; }
    public long experience() { return experience; }
    public int skillPoints() { return skillPoints; }
    public Set<Identifier> unlockedAbilities() { return Collections.unmodifiableSet(unlockedAbilities); }
    public Set<Identifier> unlockedTransformations() { return Collections.unmodifiableSet(unlockedTransformations); }
    public Optional<Identifier> lineage() { return Optional.ofNullable(lineage); }
    public Optional<Identifier> faction() { return Optional.ofNullable(faction); }
    public Optional<Identifier> activeTransformation() { return Optional.ofNullable(activeTransformation); }
    public int transformationTicks() { return transformationTicks; }
    public int mastery(Identifier form) { return transformationMastery.getOrDefault(form, 0); }
    public int reputation(Identifier faction) { return reputation.getOrDefault(faction, 0); }
    public TraitAccumulator traits() { return traits; }
    public Set<Identifier> effectiveAffinities() { return effectiveAffinities; }
    public List<Identifier> comboBuffer() { return Collections.unmodifiableList(comboBuffer); }
    public Optional<Identifier> chargingAbility() { return Optional.ofNullable(chargingAbility); }
    public int chargeTicks() { return chargeTicks; }
    public Optional<Identifier> armedReactive() { return reactiveWindow > 0 ? Optional.ofNullable(armedReactive) : Optional.empty(); }

    public boolean isMovementActive(MovementMode mode) { return activeMovement.contains(mode); }
    public Set<MovementMode> activeMovement() { return Collections.unmodifiableSet(activeMovement); }
    public boolean flightGranted() { return flightGranted; }

    // ---- clan accessors (logic lives in ClanAbilityManager / ClanRollService) ----
    public int clanSkillLevel(Identifier lineage) { return clanSkillLevels.getOrDefault(lineage, 1); }
    public long clanSkillXp(Identifier lineage) { return clanSkillXp.getOrDefault(lineage, 0L); }
    public void setClanSkill(Identifier lineage, int level, long xp) {
        clanSkillLevels.put(lineage, Math.max(1, level));
        clanSkillXp.put(lineage, Math.max(0L, xp));
        markDirty(false);
    }
    public Set<Identifier> clanGrantedAbilities() { return Collections.unmodifiableSet(clanGrantedAbilities); }
    public Set<Identifier> clanGrantedForms() { return Collections.unmodifiableSet(clanGrantedForms); }
    /** Unlocks and remembers it as clan-granted; an ability the player already knew stays "learned" (not clan-owned). */
    public boolean grantClanAbility(Identifier id) {
        if (!unlockAbility(id)) return false;
        clanGrantedAbilities.add(id);
        return true;
    }
    public boolean grantClanForm(Identifier id) {
        if (!unlockTransformation(id)) return false;
        clanGrantedForms.add(id);
        return true;
    }
    /** Removes every clan-granted ability/form; learned (scroll, faction, command) abilities are untouched. */
    public void revokeClanGrants() {
        for (Identifier id : List.copyOf(clanGrantedAbilities)) lockAbility(id);
        for (Identifier id : List.copyOf(clanGrantedForms)) {
            unlockedTransformations.remove(id);
            transformationMastery.remove(id);
        }
        clanGrantedAbilities.clear();
        clanGrantedForms.clear();
        markDirty(false);
    }
    public boolean markClanKitGiven(Identifier lineage) {
        boolean added = clanKitsGiven.add(lineage);
        if (added) markDirty(false);
        return added;
    }
    public int clanPity() { return clanPity; }
    public void setClanPity(int pity) { clanPity = Math.max(0, pity); markDirty(false); }
    public int clanRerolls() { return clanRerolls; }
    public void incrementClanRerolls() { clanRerolls++; markDirty(false); }
    public boolean clanInitialized() { return clanInitialized; }
    public void setClanInitialized(boolean value) { clanInitialized = value; markDirty(false); }

    public void setMovementActive(MovementMode mode, boolean active) {
        if (active ? activeMovement.add(mode) : activeMovement.remove(mode)) markDirty(true);
    }

    public void setFlightGranted(boolean granted) {
        this.flightGranted = granted;
        markDirty(false);
    }

    public List<Optional<Identifier>> hotbar() {
        return Arrays.stream(hotbar).map(Optional::ofNullable).toList();
    }

    public Optional<Identifier> hotbarSlot(int slot) {
        return slot >= 0 && slot < hotbar.length ? Optional.ofNullable(hotbar[slot]) : Optional.empty();
    }

    // =====================================================================================
    // Energy
    // =====================================================================================

    /**
     * Consume energy, firing {@link AnimeCoreEvents#ENERGY_CONSUME} so other mechanics can discount/raise the cost.
     * @return false (and consume nothing) if the pool is missing or too low
     */
    public boolean tryConsume(Identifier poolId, float amount) {
        EnergyPool pool = pools.get(poolId);
        if (pool == null) return amount <= 0f;
        float cost = amount;
        if (player instanceof ServerPlayerEntity sp) {
            cost = AnimeCoreEvents.ENERGY_CONSUME.invoker().modifyCost(sp, this, poolId, amount);
        }
        if (cost <= 0f) return true;
        if (!pool.has(cost)) return false;
        pool.drain(cost);
        energyChanged();
        return true;
    }

    public void restore(Identifier poolId, float amount) {
        EnergyPool pool = pools.get(poolId);
        if (pool == null) return;
        pool.setCurrent(pool.current() + amount);
        energyChanged();
    }

    public void setEnergy(Identifier poolId, float amount) {
        EnergyPool pool = pools.get(poolId);
        if (pool == null) return;
        pool.setCurrent(amount);
        energyChanged();
    }

    // =====================================================================================
    // Progression
    // =====================================================================================

    public static long experienceForNextTier(int tier) {
        return (long) (100 * Math.pow(tier, 1.5));
    }

    public void addExperience(long amount) {
        if (amount <= 0 || tier >= MAX_TIER) return;
        experience += amount;
        while (tier < MAX_TIER && experience >= experienceForNextTier(tier)) {
            experience -= experienceForNextTier(tier);
            tier++;
            skillPoints++;
            for (EnergyPool pool : pools.values()) pool.setBaseMax(pool.baseMax() + pool.spec().baseMax() * 0.05f);
        }
        markDirty(true);
        onTierChanged();
    }

    public void setTier(int tier) {
        this.tier = Math.max(1, Math.min(MAX_TIER, tier));
        markDirty(true);
        onTierChanged();
    }

    /** Rank-gated clan progression entries are re-evaluated whenever the tier moves. */
    private void onTierChanged() {
        if (player instanceof ServerPlayerEntity sp && lineage != null) ClanAbilityManager.evaluate(sp, this);
    }

    public void addSkillPoints(int amount) {
        skillPoints = Math.max(0, skillPoints + amount);
        markDirty(false);
    }

    public boolean unlockAbility(Identifier id) {
        boolean added = unlockedAbilities.add(id);
        if (added) {
            autoFillHotbar(id);
            markDirty(false);
        }
        return added;
    }

    public boolean lockAbility(Identifier id) {
        boolean removed = unlockedAbilities.remove(id);
        if (removed) {
            for (int i = 0; i < hotbar.length; i++) if (id.equals(hotbar[i])) hotbar[i] = null;
            markDirty(false);
        }
        return removed;
    }

    /** Unlocked abilities plus those temporarily granted by the active transformation. */
    public Set<Identifier> castableAbilities() {
        Set<Identifier> out = new LinkedHashSet<>(unlockedAbilities);
        if (activeTransformation != null) {
            AnimeRegistries.transformation(registries(), activeTransformation)
                    .ifPresent(e -> out.addAll(e.value().grantedAbilities()));
        }
        return out;
    }

    public boolean unlockTransformation(Identifier id) {
        boolean added = unlockedTransformations.add(id);
        if (added) markDirty(false);
        return added;
    }

    public void setHotbarSlot(int slot, @Nullable Identifier ability) {
        if (slot < 0 || slot >= hotbar.length) return;
        hotbar[slot] = ability;
        markDirty(false);
    }

    private void autoFillHotbar(Identifier ability) {
        for (Identifier id : hotbar) if (ability.equals(id)) return;
        for (int i = 0; i < hotbar.length; i++) {
            if (hotbar[i] == null) {
                hotbar[i] = ability;
                return;
            }
        }
    }

    // =====================================================================================
    // Lineage / Faction
    // =====================================================================================

    /**
     * Switches clan through {@link ClanAbilityManager#assign} on the server (old clan grants and modifiers are removed,
     * defaults granted, progression evaluated). On the client this only mirrors the value.
     * Caller validates ownership ({@code system}).
     */
    public void setLineage(@Nullable Identifier id) {
        if (player instanceof ServerPlayerEntity sp) ClanAbilityManager.assign(sp, this, id, ClanAbilityManager.Reason.COMMAND);
        else setLineageRaw(id);
    }

    /** Low-level setter used by {@link ClanAbilityManager}: no grants, no events. */
    public void setLineageRaw(@Nullable Identifier id) {
        this.lineage = id;
        traitsDirty = true;
        markDirty(true);
    }

    public void setFaction(@Nullable Identifier id) {
        this.faction = id;
        if (id != null) {
            AnimeRegistries.faction(registries(), id).ifPresent(def -> {
                def.startingAbilities().forEach(this::unlockAbility);
                reputation.putIfAbsent(id, def.startingReputation());
            });
        }
        traitsDirty = true;
        markDirty(true);
    }

    public void addReputation(Identifier faction, int delta) {
        reputation.merge(faction, delta, Integer::sum);
        markDirty(false);
    }

    // =====================================================================================
    // Cooldowns
    // =====================================================================================

    private long now() { return player.getWorld().getTime(); }

    public boolean isOnCooldown(Identifier id) { return cooldownRemaining(id) > 0; }

    public int cooldownRemaining(Identifier id) {
        Long end = cooldownEnds.get(id);
        return end == null ? 0 : (int) Math.max(0, end - now());
    }

    public void setCooldown(Identifier id, int ticks) {
        if (ticks <= 0) cooldownEnds.remove(id);
        else cooldownEnds.put(id, now() + ticks);
        markDirty(false);
    }

    // =====================================================================================
    // Transformation state (logic lives in TransformationService)
    // =====================================================================================

    public void setActiveTransformation(@Nullable Identifier id) {
        this.activeTransformation = id;
        this.transformationTicks = 0;
        traitsDirty = true;
        markDirty(true);
    }

    public void tickTransformation() { transformationTicks++; }

    public int addMastery(Identifier form, int amount) {
        return transformationMastery.merge(form, amount, Integer::sum);
    }

    // =====================================================================================
    // Combo / Charge / Reactive (logic lives in AbilityExecutor)
    // =====================================================================================

    public void pushComboToken(Identifier token) {
        if (comboBuffer.size() >= MAX_COMBO_LENGTH) comboBuffer.remove(0);
        comboBuffer.add(token);
        lastComboInput = now();
    }

    public void clearCombo() { comboBuffer.clear(); }

    public void beginCharge(Identifier ability) {
        chargingAbility = ability;
        chargeTicks = 0;
    }

    public void incrementCharge(int max) { chargeTicks = Math.min(max, chargeTicks + 1); }

    public void clearCharge() {
        chargingAbility = null;
        chargeTicks = 0;
    }

    public void armReactive(Identifier ability, int windowTicks) {
        armedReactive = ability;
        reactiveWindow = windowTicks;
        markDirty(false);
    }

    public Optional<Identifier> consumeReactive() {
        Optional<Identifier> out = armedReactive();
        armedReactive = null;
        reactiveWindow = 0;
        if (out.isPresent()) markDirty(false);
        return out;
    }

    // =====================================================================================
    // Traits
    // =====================================================================================

    public void markTraitsDirty() { traitsDirty = true; }

    private void refreshTraits(ServerPlayerEntity sp) {
        DynamicRegistryManager rm = registries();
        TraitAccumulator acc = new TraitAccumulator();
        Set<Identifier> affinities = new LinkedHashSet<>();

        if (lineage != null) {
            Optional<LineageDefinition> def = AnimeRegistries.lineage(rm, lineage);
            def.ifPresent(d -> {
                d.traits().forEach(t -> t.accumulate(acc));
                affinities.addAll(d.innateAffinities());
            });
        }
        if (faction != null) {
            AnimeRegistries.faction(rm, faction).map(FactionDefinition::traits)
                    .ifPresent(list -> list.forEach(t -> t.accumulate(acc)));
        }
        if (activeTransformation != null) {
            AnimeRegistries.transformation(rm, activeTransformation).map(RegistryEntry::value)
                    .map(TransformationDefinition::modifiers)
                    .ifPresent(list -> list.forEach((ITraitModifier t) -> t.accumulate(acc)));
        }
        affinities.addAll(AffinityMatrix.resolveFusions(AnimeRegistries.affinities(rm), affinities, lineage));

        for (EnergyPool pool : pools.values()) {
            pool.applyTraits(acc.capacityFlat(pool.id()), acc.capacityMultiplier(pool.id()), acc.regenMultiplier(pool.id()));
        }

        for (AppliedModifier applied : appliedModifiers) {
            EntityAttributeInstance inst = sp.getAttributeInstance(applied.attribute());
            if (inst != null) inst.removeModifier(applied.id());
        }
        appliedModifiers.clear();
        int index = 0;
        for (TraitAccumulator.AttributeBonus bonus : acc.attributes()) {
            EntityAttributeInstance inst = sp.getAttributeInstance(bonus.attribute());
            if (inst == null) continue;
            Identifier id = context.id("trait/" + index++);
            inst.removeModifier(id);
            inst.addTemporaryModifier(new EntityAttributeModifier(id, bonus.amount(), bonus.operation()));
            appliedModifiers.add(new AppliedModifier(bonus.attribute(), id));
        }
        if (sp.getHealth() > sp.getMaxHealth()) sp.setHealth(sp.getMaxHealth());

        this.traits = acc;
        this.effectiveAffinities = Set.copyOf(affinities);
        this.traitsDirty = false;
        markDirty(true);
    }

    private DynamicRegistryManager registries() {
        return player.getWorld().getRegistryManager();
    }

    // =====================================================================================
    // Ticking & sync
    // =====================================================================================

    /** @param publicChange true if peers must also be told (aura, transformation, lineage, pool ratios). */
    public void markDirty(boolean publicChange) {
        privateDirty = true;
        if (publicChange) publicDirty = true;
    }

    /** Owner sees energy changes within {@value #PRIVATE_SYNC_INTERVAL} ticks; peers (aura intensity) once per second. */
    private void energyChanged() {
        privateDirty = true;
        energyPublicDirty = true;
    }

    @Override
    public void serverTick() {
        if (!(player instanceof ServerPlayerEntity sp)) return;
        if (traitsDirty) refreshTraits(sp);

        boolean regenChanged = false;
        Identifier chargingPool = null;
        if (chargingAbility != null) chargingPool = AbilityExecutor.tickCharge(sp, this);
        for (EnergyPool pool : pools.values()) {
            if (pool.id().equals(chargingPool)) continue; // no regen on the pool being channelled
            regenChanged |= pool.regen();
        }
        if (regenChanged) energyChanged();

        if (activeTransformation != null) TransformationService.tick(sp, this);
        AnimeMovementHandler.tick(sp, this);
        if (lineage != null && player.age % 20 == 0) ClanTraitHooks.tick(sp, this);

        if (reactiveWindow > 0 && --reactiveWindow == 0) {
            armedReactive = null;
            privateDirty = true;
        }
        if (!comboBuffer.isEmpty() && now() - lastComboInput > COMBO_TIMEOUT_TICKS) {
            comboBuffer.clear();
            privateDirty = true;
        }
        if (!cooldownEnds.isEmpty()) {
            long t = now();
            Iterator<Map.Entry<Identifier, Long>> it = cooldownEnds.entrySet().iterator();
            while (it.hasNext()) if (it.next().getValue() <= t) it.remove();
        }

        if (energyPublicDirty && player.age % 20 == 0) {
            publicDirty = true;
            energyPublicDirty = false;
        }
        if (publicDirty) {
            context.powerKey().sync(player);
            publicDirty = false;
            privateDirty = false;
            privateSyncCooldown = PRIVATE_SYNC_INTERVAL;
        } else if (privateDirty && --privateSyncCooldown <= 0) {
            context.powerKey().sync(player, this, PlayerSyncPredicate.only(player));
            privateDirty = false;
            privateSyncCooldown = PRIVATE_SYNC_INTERVAL;
        }
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        boolean full = recipient == player;
        NbtCompound tag = new NbtCompound();
        if (full) writeToNbt(tag, buf.getRegistryManager());
        else writePublic(tag);
        tag.putInt("movement", movementMask()); // session state: synced, never saved
        buf.writeBoolean(full);
        buf.writeNbt(tag);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        boolean full = buf.readBoolean();
        NbtCompound tag = buf.readNbt();
        if (tag == null) return;
        if (full) readFromNbt(tag, buf.getRegistryManager());
        else readPublic(tag);
        activeMovement.clear();
        int mask = tag.getInt("movement");
        for (MovementMode m : MovementMode.values()) if ((mask & (1 << m.ordinal())) != 0) activeMovement.add(m);
    }

    private int movementMask() {
        int mask = 0;
        for (MovementMode m : activeMovement) mask |= 1 << m.ordinal();
        return mask;
    }

    // =====================================================================================
    // NBT (schema-versioned)
    // =====================================================================================

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        tag.putInt("schema_version", SCHEMA_VERSION);
        NbtCompound poolsTag = new NbtCompound();
        pools.forEach((id, pool) -> poolsTag.put(id.toString(), pool.write()));
        tag.put("pools", poolsTag);
        tag.putInt("tier", tier);
        tag.putLong("experience", experience);
        tag.putInt("skill_points", skillPoints);
        tag.put("abilities", writeIds(unlockedAbilities));
        tag.put("transformations", writeIds(unlockedTransformations));
        tag.put("mastery", writeIntMap(transformationMastery));
        tag.put("reputation", writeIntMap(reputation));
        if (lineage != null) tag.putString("lineage", lineage.toString());
        if (faction != null) tag.putString("faction", faction.toString());
        if (activeTransformation != null) {
            tag.putString("active_form", activeTransformation.toString());
            tag.putInt("form_ticks", transformationTicks);
        }
        NbtCompound cds = new NbtCompound();
        long t = now();
        cooldownEnds.forEach((id, end) -> { if (end > t) cds.putInt(id.toString(), (int) (end - t)); });
        tag.put("cooldowns", cds);
        NbtList bar = new NbtList();
        for (Identifier id : hotbar) bar.add(NbtString.of(id == null ? "" : id.toString()));
        tag.put("hotbar", bar);
        tag.put("affinities", writeIds(effectiveAffinities));
        if (flightGranted) tag.putBoolean("flight_granted", true);
        NbtCompound clan = new NbtCompound();
        clan.put("levels", writeIntMap(clanSkillLevels));
        NbtCompound xp = new NbtCompound();
        clanSkillXp.forEach((k, v) -> xp.putLong(k.toString(), v));
        clan.put("xp", xp);
        clan.put("granted_abilities", writeIds(clanGrantedAbilities));
        clan.put("granted_forms", writeIds(clanGrantedForms));
        clan.put("kits", writeIds(clanKitsGiven));
        clan.putInt("pity", clanPity);
        clan.putInt("rerolls", clanRerolls);
        clan.putBoolean("initialized", clanInitialized);
        tag.put("clan", clan);
        if (armedReactive != null && reactiveWindow > 0) {
            tag.putString("armed_reactive", armedReactive.toString());
            tag.putInt("reactive_window", reactiveWindow);
        }
        tag.put("combo", writeIds(comboBuffer));
        if (chargingAbility != null) {
            tag.putString("charging", chargingAbility.toString());
            tag.putInt("charge_ticks", chargeTicks);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        int version = tag.contains("schema_version") ? tag.getInt("schema_version") : 0;
        if (version > SCHEMA_VERSION) {
            AnimeCore.LOGGER.warn("[{}] Player data schema v{} is newer than supported v{}; loading best-effort",
                    context.modId(), version, SCHEMA_VERSION);
        }
        migrate(tag, version);

        NbtCompound poolsTag = tag.getCompound("pools");
        pools.forEach((id, pool) -> {
            if (poolsTag.contains(id.toString(), NbtElement.COMPOUND_TYPE)) pool.read(poolsTag.getCompound(id.toString()));
        });
        tier = Math.max(1, tag.getInt("tier"));
        experience = tag.getLong("experience");
        skillPoints = tag.getInt("skill_points");
        readIds(tag.getList("abilities", NbtElement.STRING_TYPE), unlockedAbilities);
        readIds(tag.getList("transformations", NbtElement.STRING_TYPE), unlockedTransformations);
        readIntMap(tag.getCompound("mastery"), transformationMastery);
        readIntMap(tag.getCompound("reputation"), reputation);
        lineage = readId(tag, "lineage");
        faction = readId(tag, "faction");
        activeTransformation = readId(tag, "active_form");
        transformationTicks = tag.getInt("form_ticks");
        cooldownEnds.clear();
        NbtCompound cds = tag.getCompound("cooldowns");
        long t = now();
        for (String key : cds.getKeys()) {
            Identifier id = Identifier.tryParse(key);
            if (id != null) cooldownEnds.put(id, t + cds.getInt(key));
        }
        Arrays.fill(hotbar, null);
        NbtList bar = tag.getList("hotbar", NbtElement.STRING_TYPE);
        for (int i = 0; i < Math.min(bar.size(), hotbar.length); i++) {
            String s = bar.getString(i);
            hotbar[i] = s.isEmpty() ? null : Identifier.tryParse(s);
        }
        Set<Identifier> aff = new LinkedHashSet<>();
        readIds(tag.getList("affinities", NbtElement.STRING_TYPE), aff);
        effectiveAffinities = Set.copyOf(aff);
        flightGranted = tag.getBoolean("flight_granted");
        NbtCompound clan = tag.getCompound("clan");
        readIntMap(clan.getCompound("levels"), clanSkillLevels);
        clanSkillXp.clear();
        NbtCompound xp = clan.getCompound("xp");
        for (String key : xp.getKeys()) {
            Identifier id = Identifier.tryParse(key);
            if (id != null) clanSkillXp.put(id, xp.getLong(key));
        }
        readIds(clan.getList("granted_abilities", NbtElement.STRING_TYPE), clanGrantedAbilities);
        readIds(clan.getList("granted_forms", NbtElement.STRING_TYPE), clanGrantedForms);
        readIds(clan.getList("kits", NbtElement.STRING_TYPE), clanKitsGiven);
        clanPity = clan.getInt("pity");
        clanRerolls = clan.getInt("rerolls");
        clanInitialized = clan.getBoolean("initialized");
        armedReactive = readId(tag, "armed_reactive");
        reactiveWindow = tag.getInt("reactive_window");
        comboBuffer.clear();
        readIds(tag.getList("combo", NbtElement.STRING_TYPE), comboBuffer);
        chargingAbility = readId(tag, "charging");
        chargeTicks = tag.getInt("charge_ticks");
        traitsDirty = true;
    }

    /** Peer-visible subset. */
    private void writePublic(NbtCompound tag) {
        if (lineage != null) tag.putString("lineage", lineage.toString());
        if (faction != null) tag.putString("faction", faction.toString());
        if (activeTransformation != null) tag.putString("active_form", activeTransformation.toString());
        tag.putInt("tier", tier);
        NbtCompound poolsTag = new NbtCompound();
        pools.forEach((id, pool) -> {
            NbtCompound p = new NbtCompound();
            pool.writePublic(p);
            poolsTag.put(id.toString(), p);
        });
        tag.put("pools", poolsTag);
        tag.put("affinities", writeIds(effectiveAffinities));
        if (chargingAbility != null) tag.putString("charging", chargingAbility.toString());
    }

    private void readPublic(NbtCompound tag) {
        lineage = readId(tag, "lineage");
        faction = readId(tag, "faction");
        activeTransformation = readId(tag, "active_form");
        tier = Math.max(1, tag.getInt("tier"));
        NbtCompound poolsTag = tag.getCompound("pools");
        pools.forEach((id, pool) -> {
            if (poolsTag.contains(id.toString(), NbtElement.COMPOUND_TYPE)) pool.read(poolsTag.getCompound(id.toString()));
        });
        Set<Identifier> aff = new LinkedHashSet<>();
        readIds(tag.getList("affinities", NbtElement.STRING_TYPE), aff);
        effectiveAffinities = Set.copyOf(aff);
        chargingAbility = readId(tag, "charging");
    }

    /**
     * Upgrades older layouts in place. Add one {@code if (from < N)} block per schema bump, never edit old ones.
     */
    protected void migrate(NbtCompound tag, int from) {
        if (from < 1) {
            // v0 (pre-release) stored a single float "energy" for the primary pool.
            if (tag.contains("energy", NbtElement.FLOAT_TYPE)) {
                context.pool(EnergyPoolSpec.Role.PRIMARY).ifPresent(spec -> {
                    NbtCompound poolsTag = tag.getCompound("pools");
                    NbtCompound p = new NbtCompound();
                    p.putFloat("current", tag.getFloat("energy"));
                    poolsTag.put(spec.id().toString(), p);
                    tag.put("pools", poolsTag);
                });
                tag.remove("energy");
            }
        }
        if (from < 2) {
            // v1 had no clan bookkeeping. A v1 lineage counts as an already-made first-join choice, and the abilities
            // it granted are kept as "learned" (clan_granted stays empty), so nothing a player owned disappears.
            NbtCompound clan = new NbtCompound();
            clan.putBoolean("initialized", tag.contains("lineage", NbtElement.STRING_TYPE));
            tag.put("clan", clan);
        }
    }

    // ---- NBT helpers ----

    private static NbtList writeIds(Collection<Identifier> ids) {
        NbtList list = new NbtList();
        for (Identifier id : ids) list.add(NbtString.of(id.toString()));
        return list;
    }

    private static void readIds(NbtList list, Collection<Identifier> out) {
        out.clear();
        for (int i = 0; i < list.size(); i++) {
            Identifier id = Identifier.tryParse(list.getString(i));
            if (id != null) out.add(id);
        }
    }

    private static NbtCompound writeIntMap(Map<Identifier, Integer> map) {
        NbtCompound c = new NbtCompound();
        map.forEach((k, v) -> c.putInt(k.toString(), v));
        return c;
    }

    private static void readIntMap(NbtCompound c, Map<Identifier, Integer> out) {
        out.clear();
        for (String key : c.getKeys()) {
            Identifier id = Identifier.tryParse(key);
            if (id != null) out.put(id, c.getInt(key));
        }
    }

    private static @Nullable Identifier readId(NbtCompound tag, String key) {
        return tag.contains(key, NbtElement.STRING_TYPE) ? Identifier.tryParse(tag.getString(key)) : null;
    }
}
