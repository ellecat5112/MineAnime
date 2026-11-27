package com.animecore.manifestation;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/** Timed attribute modifiers (AttributeShiftAction): applied now, removed when they expire or the entity is gone. */
public final class TemporaryModifiers {
    private TemporaryModifiers() {}

    private record Entry(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier id, long end) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ENTRIES.isEmpty()) return;
            ENTRIES.removeIf(e -> {
                if (e.entity.isAlive() && !e.entity.isRemoved() && e.entity.getWorld().getTime() < e.end) return false;
                EntityAttributeInstance inst = e.entity.getAttributeInstance(e.attribute);
                if (inst != null) inst.removeModifier(e.id);
                if (e.entity.getHealth() > e.entity.getMaxHealth()) e.entity.setHealth(e.entity.getMaxHealth());
                return true;
            });
        });
    }

    public static void apply(LivingEntity e, RegistryEntry<EntityAttribute> attribute, double amount, EntityAttributeModifier.Operation op, int ticks) {
        EntityAttributeInstance inst = e.getAttributeInstance(attribute);
        if (inst == null) return;
        Identifier id = Identifier.of("animecore", "shift/" + attribute.getKey().map(k -> k.getValue().getPath()).orElse("x").replace('.', '_'));
        inst.removeModifier(id);
        inst.addTemporaryModifier(new EntityAttributeModifier(id, amount, op));
        ENTRIES.removeIf(x -> x.entity == e && x.id.equals(id));
        ENTRIES.add(new Entry(e, attribute, id, e.getWorld().getTime() + ticks));
        if (e.getHealth() > e.getMaxHealth()) e.setHealth(e.getMaxHealth());
    }
}
