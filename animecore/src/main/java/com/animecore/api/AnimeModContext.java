package com.animecore.api;

import com.animecore.component.PlayerPowerComponent;
import com.animecore.movement.MovementMode;
import com.animecore.movement.MovementSpec;
import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.Ids;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Everything the engine needs to know about one content mod ("power system"). Every id that the engine creates on
 * behalf of a mod (CCA component, payloads, keybind category, HUD layer, data components) is derived from
 * {@link #modId()}, so any number of anime mods can run side by side.
 *
 * <pre>{@code
 * public static final AnimeModContext CTX = AnimeModContext.builder("narutorpg")
 *         .primaryEnergy("chakra", 100, 0.15f, 0x3FA9F5)
 *         .secondaryEnergy("stamina", 100, 0.4f, 0x8BC34A)
 *         .register();
 * }</pre>
 * The mod must declare {@code "<modId>:power"} under {@code custom.cardinal-components} in its fabric.mod.json and
 * add an {@link com.animecore.component.AnimeComponentInitializer} subclass to its {@code cardinal-components} entrypoint.
 */
public final class AnimeModContext {
    public static final int DEFAULT_HOTBAR_SIZE = 6;

    private final String modId;
    private final List<EnergyPoolSpec> pools;
    private final List<MovementSpec> movement;
    private final int hotbarSize;
    private final Function<Identifier, Identifier> comboAnimation;
    private final ComponentKey<PlayerPowerComponent> powerKey;
    private final boolean passiveMovement;

    private AnimeModContext(Builder b) {
        this.passiveMovement = b.passiveMovement;
        this.modId = b.modId;
        this.pools = List.copyOf(b.pools);
        this.movement = List.copyOf(b.movement);
        this.hotbarSize = b.hotbarSize;
        this.comboAnimation = b.comboAnimation;
        this.powerKey = ComponentRegistry.getOrCreate(Ids.of(modId, "power"), PlayerPowerComponent.class);
    }

    public static Builder builder(String modId) {
        return new Builder(modId);
    }

    public String modId() { return modId; }
    public List<EnergyPoolSpec> pools() { return pools; }
    public int hotbarSize() { return hotbarSize; }
    public List<MovementSpec> movementSpecs() { return movement; }

    public Optional<MovementSpec> movement(MovementMode mode) {
        return movement.stream().filter(m -> m.mode() == mode).findFirst();
    }
    public ComponentKey<PlayerPowerComponent> powerKey() { return powerKey; }
    /** Keybind-free movement: automatic water walking, jump-at-a-wall-looking-up climbing, sneak + jump burst step. */
    public boolean passiveMovement() { return passiveMovement; }

    public Identifier id(String path) { return Ids.of(modId, path); }

    public <T extends CustomPayload> CustomPayload.Id<T> payloadId(String path) {
        return AnimeNetworking.payloadId(modId, path);
    }

    /** {@code key.categories.<modId>} - isolates keybinds per anime mod in the Controls screen. */
    public String keyCategory() { return "key.categories." + modId; }

    public String keyTranslation(String name) { return "key." + modId + "." + name; }

    /** PlayerAnimator animation played when a combo token is entered (e.g. a hand sign). */
    public Identifier comboAnimation(Identifier token) { return comboAnimation.apply(token); }

    public Optional<EnergyPoolSpec> pool(Identifier id) {
        return pools.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    public Optional<EnergyPoolSpec> pool(EnergyPoolSpec.Role role) {
        return pools.stream().filter(p -> p.role() == role).findFirst();
    }

    public PlayerPowerComponent power(PlayerEntity player) { return powerKey.get(player); }

    public Optional<PlayerPowerComponent> maybePower(Entity entity) { return powerKey.maybeGet(entity); }

    @Override public String toString() { return "AnimeModContext[" + modId + "]"; }

    public static final class Builder {
        private final String modId;
        private final List<EnergyPoolSpec> pools = new ArrayList<>();
        private final List<MovementSpec> movement = new ArrayList<>();
        private int hotbarSize = DEFAULT_HOTBAR_SIZE;
        private Function<Identifier, Identifier> comboAnimation;
        private boolean passiveMovement;

        private Builder(String modId) {
            if (!modId.matches("[a-z0-9_.-]+")) throw new IllegalArgumentException("Invalid mod id: " + modId);
            this.modId = modId;
            this.comboAnimation = token -> Ids.of(token.getNamespace(), "combo_" + token.getPath());
        }

        public Builder primaryEnergy(String path, float max, float regenPerTick, int color) {
            return pool(path, EnergyPoolSpec.Role.PRIMARY, max, regenPerTick, color);
        }

        public Builder secondaryEnergy(String path, float max, float regenPerTick, int color) {
            return pool(path, EnergyPoolSpec.Role.SECONDARY, max, regenPerTick, color);
        }

        public Builder extraEnergy(String path, float max, float regenPerTick, int color) {
            return pool(path, EnergyPoolSpec.Role.EXTRA, max, regenPerTick, color);
        }

        private Builder pool(String path, EnergyPoolSpec.Role role, float max, float regen, int color) {
            pools.add(new EnergyPoolSpec(Ids.of(modId, path), role, max, regen, color));
            return this;
        }

        /** Tree / wall / water walking. Drains {@code drainPerTick} while in use (20% while idle). */
        public Builder surfaceAdhesion(String poolPath, float drainPerTick, int requiredTier) {
            return movement(new MovementSpec(MovementMode.SURFACE_ADHESION, Ids.of(modId, poolPath), 0f, drainPerTick, requiredTier, 0f, 0));
        }

        /** Free flight. {@code flySpeed} uses vanilla units (creative = 0.05). */
        public Builder flight(String poolPath, float activationCost, float drainPerTick, float flySpeed, int requiredTier) {
            return movement(new MovementSpec(MovementMode.OMNIDIRECTIONAL_FLIGHT, Ids.of(modId, poolPath), activationCost, drainPerTick, requiredTier, flySpeed, 0));
        }

        /** Short blink with afterimages (Shunshin / Flash Step / Z-Dash). */
        public Builder burstStep(String poolPath, float cost, float distance, int cooldownTicks, int requiredTier) {
            return movement(new MovementSpec(MovementMode.BURST_STEP, Ids.of(modId, poolPath), cost, 0f, requiredTier, distance, cooldownTicks));
        }

        public Builder movement(MovementSpec spec) {
            movement.removeIf(m -> m.mode() == spec.mode());
            movement.add(spec);
            return this;
        }

        /**
         * Movement without keybinds (AHZNB-style): surface adhesion switches itself on while the pool can pay
         * (water walking; climb by jumping at a wall while looking up; sneak to dive / let go) and sneak + jump performs
         * the burst step. The toggle keybinds keep working for players who bind them.
         */
        public Builder passiveMovement() {
            this.passiveMovement = true;
            return this;
        }

        public Builder hotbarSize(int size) {
            this.hotbarSize = Math.max(1, Math.min(10, size));
            return this;
        }

        public Builder comboAnimations(Function<Identifier, Identifier> mapper) {
            this.comboAnimation = mapper;
            return this;
        }

        /** Builds the context and registers it with the engine. Safe to call from a static initializer. */
        public AnimeModContext register() {
            if (pools.stream().noneMatch(p -> p.role() == EnergyPoolSpec.Role.PRIMARY)) {
                throw new IllegalStateException(modId + " must declare a primary energy pool");
            }
            return AnimeModRegistry.register(new AnimeModContext(this));
        }
    }
}
