package com.animecore.clan;

import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeBuiltinRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.Optional;

/** Built-in, anime-agnostic clan trait types. Content mods register more with {@link #register(Identifier, MapCodec)}. */
public final class ClanTraits {
    private ClanTraits() {}

    // must precede the record codecs below (static init order)
    private static final Codec<RegistryEntry<StatusEffect>> EFFECT = Registries.STATUS_EFFECT.getEntryCodec();

    public static final ClanTraitType<EnergyDiscount> ENERGY_DISCOUNT = register(Ids.core("energy_discount"), EnergyDiscount.CODEC);
    public static final ClanTraitType<Evasion> EVASION = register(Ids.core("evasion"), Evasion.CODEC);
    public static final ClanTraitType<PassiveEffect> PASSIVE_EFFECT = register(Ids.core("passive_effect"), PassiveEffect.CODEC);
    public static final ClanTraitType<Regeneration> REGENERATION = register(Ids.core("regeneration"), Regeneration.CODEC);
    public static final ClanTraitType<OnHitEffect> ON_HIT_EFFECT = register(Ids.core("on_hit_effect"), OnHitEffect.CODEC);
    public static final ClanTraitType<SkillExperienceBoost> SKILL_EXPERIENCE_BOOST = register(Ids.core("skill_experience_boost"), SkillExperienceBoost.CODEC);
    public static final ClanTraitType<FatalSave> FATAL_SAVE = register(Ids.core("fatal_save"), FatalSave.CODEC);
    public static final ClanTraitType<Sense> SENSE = register(Ids.core("sense"), Sense.CODEC);

    public static <T extends IClanTrait> ClanTraitType<T> register(Identifier id, MapCodec<T> codec) {
        return Registry.register(AnimeBuiltinRegistries.CLAN_TRAIT_TYPES, id, new ClanTraitType<>(codec));
    }

    public static void init() {}

    /** Cheaper techniques: Uzumaki sealing, Nara shadow control. {@code pool} empty = every pool. */
    public record EnergyDiscount(Optional<Identifier> pool, float multiplier) implements IClanTrait {
        public static final MapCodec<EnergyDiscount> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.optionalFieldOf("pool").forGetter(EnergyDiscount::pool),
                Codec.floatRange(0f, 4f).fieldOf("multiplier").forGetter(EnergyDiscount::multiplier)
        ).apply(i, EnergyDiscount::new));

        @Override public ClanTraitType<?> type() { return ENERGY_DISCOUNT; }

        @Override
        public float modifyEnergyCost(ServerPlayerEntity player, PlayerPowerComponent power, Identifier p, float amount) {
            return pool.map(p::equals).orElse(true) ? amount * multiplier : amount;
        }
    }

    /** Chance to fully dodge a hit (Hōzuki hydrification, Iburi smoke body). */
    public record Evasion(float chance, boolean projectilesOnly) implements IClanTrait {
        public static final MapCodec<Evasion> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.floatRange(0f, 1f).fieldOf("chance").forGetter(Evasion::chance),
                Codec.BOOL.optionalFieldOf("projectiles_only", false).forGetter(Evasion::projectilesOnly)
        ).apply(i, Evasion::new));

        @Override public ClanTraitType<?> type() { return EVASION; }

        @Override
        public boolean tryEvade(ServerPlayerEntity player, PlayerPowerComponent power, DamageSource source, float amount) {
            if (projectilesOnly && !source.isIn(net.minecraft.registry.tag.DamageTypeTags.IS_PROJECTILE)) return false;
            if (source.isIn(net.minecraft.registry.tag.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
            if (player.getRandom().nextFloat() >= chance) return false;
            player.getServerWorld().spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getBodyY(0.5), player.getZ(), 8, 0.3, 0.5, 0.3, 0.02);
            return true;
        }
    }

    /** Permanent status effect refreshed every second (Akimichi resistance, Inuzuka night vision). */
    public record PassiveEffect(RegistryEntry<StatusEffect> effect, int amplifier) implements IClanTrait {
        public static final MapCodec<PassiveEffect> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                EFFECT.fieldOf("effect").forGetter(PassiveEffect::effect),
                Codec.intRange(0, 9).optionalFieldOf("amplifier", 0).forGetter(PassiveEffect::amplifier)
        ).apply(i, PassiveEffect::new));

        @Override public ClanTraitType<?> type() { return PASSIVE_EFFECT; }

        @Override
        public void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
            // night vision flickers below 200 ticks, so keep a comfortable margin
            int duration = effect.equals(StatusEffects.NIGHT_VISION) ? 400 : 60;
            StatusEffectInstance cur = player.getStatusEffect(effect);
            if (cur == null || cur.getDuration() < duration - 40) {
                player.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier, true, false, true));
            }
        }

        @Override
        public void onRemoved(ServerPlayerEntity player, PlayerPowerComponent power) {
            StatusEffectInstance cur = player.getStatusEffect(effect);
            if (cur != null && cur.isAmbient() && cur.getAmplifier() == amplifier) player.removeStatusEffect(effect);
        }
    }

    /** Heals {@code amount} HP per second (Uzumaki vitality, Senju life force); optionally only below a health ratio. */
    public record Regeneration(float amount, float belowHealthRatio) implements IClanTrait {
        public static final MapCodec<Regeneration> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.floatRange(0f, 20f).fieldOf("amount").forGetter(Regeneration::amount),
                Codec.floatRange(0f, 1f).optionalFieldOf("below_health_ratio", 1f).forGetter(Regeneration::belowHealthRatio)
        ).apply(i, Regeneration::new));

        @Override public ClanTraitType<?> type() { return REGENERATION; }

        @Override
        public void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
            if (player.getHealth() < player.getMaxHealth() * belowHealthRatio) player.heal(amount);
        }
    }

    /** Applies a status effect to whatever the player damages (Aburame parasitic insects, Kamizuru bee venom). */
    public record OnHitEffect(RegistryEntry<StatusEffect> effect, int duration, int amplifier, float chance) implements IClanTrait {
        public static final MapCodec<OnHitEffect> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                EFFECT.fieldOf("effect").forGetter(OnHitEffect::effect),
                Codec.intRange(1, 6000).optionalFieldOf("duration", 60).forGetter(OnHitEffect::duration),
                Codec.intRange(0, 9).optionalFieldOf("amplifier", 0).forGetter(OnHitEffect::amplifier),
                Codec.floatRange(0f, 1f).optionalFieldOf("chance", 1f).forGetter(OnHitEffect::chance)
        ).apply(i, OnHitEffect::new));

        @Override public ClanTraitType<?> type() { return ON_HIT_EFFECT; }

        @Override
        public void onDealtDamage(ServerPlayerEntity player, PlayerPowerComponent power, LivingEntity target, DamageSource source, float amount) {
            if (player.getRandom().nextFloat() < chance) target.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier), player);
        }
    }

    /** Faster clan skill growth (prodigy clans). */
    public record SkillExperienceBoost(float multiplier) implements IClanTrait {
        public static final MapCodec<SkillExperienceBoost> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.floatRange(0f, 10f).fieldOf("multiplier").forGetter(SkillExperienceBoost::multiplier)
        ).apply(i, SkillExperienceBoost::new));

        @Override public ClanTraitType<?> type() { return SKILL_EXPERIENCE_BOOST; }
        @Override public float skillExperienceMultiplier() { return multiplier; }
    }

    /**
     * Survive a fatal hit once per {@code cooldown_ticks} (Uzumaki life force, Jūgo's sage transformation).
     * The cooldown is stored in the power component under the trait's own id, so it survives relogs.
     */
    public record FatalSave(int cooldownTicks, float healTo, Identifier cooldownId) implements IClanTrait {
        public static final MapCodec<FatalSave> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.intRange(20, 1_728_000).optionalFieldOf("cooldown_ticks", 12_000).forGetter(FatalSave::cooldownTicks),
                Codec.floatRange(1f, 40f).optionalFieldOf("heal_to", 6f).forGetter(FatalSave::healTo),
                Identifier.CODEC.optionalFieldOf("cooldown_id", Ids.core("fatal_save")).forGetter(FatalSave::cooldownId)
        ).apply(i, FatalSave::new));

        @Override public ClanTraitType<?> type() { return FATAL_SAVE; }

        @Override
        public boolean interceptDeath(ServerPlayerEntity player, PlayerPowerComponent power, DamageSource source) {
            if (power.isOnCooldown(cooldownId)) return false;
            power.setCooldown(cooldownId, cooldownTicks);
            player.setHealth(healTo);
            player.getServerWorld().spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getBodyY(0.5), player.getZ(), 30, 0.4, 0.8, 0.4, 0.3);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 0.6f, 1.3f);
            return true;
        }
    }

    /** Sensory perception: nearby living entities glow for the player's clan (Aburame insects, Hyūga, Karin's chakra sense). */
    public record Sense(float radius, boolean hostileOnly) implements IClanTrait {
        public static final MapCodec<Sense> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.floatRange(1f, 64f).fieldOf("radius").forGetter(Sense::radius),
                Codec.BOOL.optionalFieldOf("hostile_only", true).forGetter(Sense::hostileOnly)
        ).apply(i, Sense::new));

        @Override public ClanTraitType<?> type() { return SENSE; }

        @Override
        public void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
            if (player.age % 60 != 0) return; // every 3 s
            Box box = player.getBoundingBox().expand(radius);
            for (LivingEntity e : player.getWorld().getEntitiesByClass(LivingEntity.class, box,
                    e -> e != player && e.isAlive() && (!hostileOnly || e instanceof net.minecraft.entity.mob.Monster))) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 70, 0, true, false), player);
            }
        }
    }
}
