package com.animecore.component;

import com.animecore.api.EnergyPoolSpec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/** Mutable runtime state for one energy pool. */
public final class EnergyPool {
    private final EnergyPoolSpec spec;
    private float current;
    private float baseMax;
    private float baseRegen;
    // derived from traits, never persisted
    private float bonusFlat;
    private float maxMultiplier = 1f;
    private float regenMultiplier = 1f;

    public EnergyPool(EnergyPoolSpec spec) {
        this.spec = spec;
        this.baseMax = spec.baseMax();
        this.baseRegen = spec.regenPerTick();
        this.current = baseMax;
    }

    public Identifier id() { return spec.id(); }
    public EnergyPoolSpec spec() { return spec; }
    public float current() { return current; }
    public float max() { return Math.max(1f, (baseMax + bonusFlat) * maxMultiplier); }
    public float baseMax() { return baseMax; }
    public float regenPerTick() { return baseRegen * regenMultiplier; }
    public float ratio() { return current / max(); }

    public void setCurrent(float value) { this.current = MathHelper.clamp(value, 0f, max()); }
    public void setBaseMax(float value) { this.baseMax = Math.max(1f, value); clamp(); }
    public void setBaseRegen(float value) { this.baseRegen = Math.max(0f, value); }

    void applyTraits(float flat, float maxMult, float regenMult) {
        this.bonusFlat = flat;
        this.maxMultiplier = maxMult;
        this.regenMultiplier = regenMult;
        clamp();
    }

    /** @return true if anything changed */
    boolean regen() {
        if (current >= max()) return false;
        setCurrent(current + regenPerTick());
        return true;
    }

    boolean has(float amount) { return current + 1.0E-4f >= amount; }

    void drain(float amount) { setCurrent(current - amount); }

    private void clamp() { current = MathHelper.clamp(current, 0f, max()); }

    NbtCompound write() {
        NbtCompound tag = new NbtCompound();
        tag.putFloat("current", current);
        tag.putFloat("base_max", baseMax);
        tag.putFloat("base_regen", baseRegen);
        writeDerived(tag);
        return tag;
    }

    void read(NbtCompound tag) {
        if (tag.contains("base_max")) baseMax = tag.getFloat("base_max");
        if (tag.contains("base_regen")) baseRegen = tag.getFloat("base_regen");
        // Trait-derived values: authoritative on the server (recomputed on refresh), needed by the client HUD.
        if (tag.contains("bonus_flat")) {
            bonusFlat = tag.getFloat("bonus_flat");
            maxMultiplier = tag.getFloat("max_mult");
            regenMultiplier = tag.getFloat("regen_mult");
        }
        current = tag.contains("current") ? tag.getFloat("current") : max();
        clamp();
    }

    private void writeDerived(NbtCompound tag) {
        tag.putFloat("bonus_flat", bonusFlat);
        tag.putFloat("max_mult", maxMultiplier);
        tag.putFloat("regen_mult", regenMultiplier);
    }

    /** Public (peer-visible) snapshot: only the ratio, enough for aura intensity and nameplates. */
    void writePublic(NbtCompound tag) {
        tag.putFloat("current", current);
        tag.putFloat("base_max", baseMax);
        writeDerived(tag);
    }
}
