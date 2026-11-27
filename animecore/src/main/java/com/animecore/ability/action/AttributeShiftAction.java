package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.entity.ManifestationEntity;
import com.animecore.manifestation.TemporaryModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Temporarily shifts an attribute of everything in range: Sethan turns people into children ({@code generic.scale} -0.5,
 * {@code generic.attack_damage} -50%), Strength or Yellow Temperance harden the caster ({@code self}), Bastet's
 * magnetism drags... Reverted after {@code duration_ticks} ({@link TemporaryModifiers}).
 */
public record AttributeShiftAction(RegistryEntry<EntityAttribute> attribute, double amount, EntityAttributeModifier.Operation operation,
                                   int durationTicks, float radius, float coneDegrees, boolean self) implements IAbilityAction {
    public static final MapCodec<AttributeShiftAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(AttributeShiftAction::attribute),
            Codec.DOUBLE.fieldOf("amount").forGetter(AttributeShiftAction::amount),
            EntityAttributeModifier.Operation.CODEC.optionalFieldOf("operation", EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).forGetter(AttributeShiftAction::operation),
            Codec.intRange(1, 12000).optionalFieldOf("duration_ticks", 300).forGetter(AttributeShiftAction::durationTicks),
            Codec.floatRange(0f, 64f).optionalFieldOf("radius", 6f).forGetter(AttributeShiftAction::radius),
            Codec.floatRange(1f, 360f).optionalFieldOf("cone_degrees", 360f).forGetter(AttributeShiftAction::coneDegrees),
            Codec.BOOL.optionalFieldOf("self", false).forGetter(AttributeShiftAction::self)
    ).apply(i, AttributeShiftAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.ATTRIBUTE_SHIFT; }

    @Override
    public void execute(AbilityContext ctx) {
        var caster = ctx.caster();
        if (self) {
            TemporaryModifiers.apply(caster, attribute, amount, operation, durationTicks);
            return;
        }
        Vec3d origin = caster.getPos().add(0, 0.9, 0), look = ctx.look();
        double cosHalf = Math.cos(Math.toRadians(coneDegrees / 2.0));
        for (LivingEntity e : ctx.world().getEntitiesByClass(LivingEntity.class, new Box(origin, origin).expand(radius),
                t -> t != caster && t.isAlive() && !(t instanceof ManifestationEntity) && t.squaredDistanceTo(origin) <= radius * radius)) {
            Vec3d to = e.getPos().add(0, e.getHeight() / 2, 0).subtract(origin);
            if (coneDegrees < 360f && to.lengthSquared() > 1e-4 && to.normalize().dotProduct(look) < cosHalf) continue;
            TemporaryModifiers.apply(e, attribute, amount, operation, durationTicks);
            ctx.world().spawnParticles(ParticleTypes.SMOKE, e.getX(), e.getBodyY(0.5), e.getZ(), 10, 0.3, 0.5, 0.3, 0.02);
        }
    }
}
