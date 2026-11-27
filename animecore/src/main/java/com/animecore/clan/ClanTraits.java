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
    public static final ClanTraitType<Undead> UNDEAD = register(Ids.core("undead"), Undead.CODEC);
    public static final ClanTraitType<SunlightWeakness> SUNLIGHT_WEAKNESS = register(Ids.core("sunlight_weakness"), SunlightWeakness.CODEC);
    public static final ClanTraitType<Breathing> BREATHING = register(Ids.core("breathing"), Breathing.CODEC);
    public static final ClanTraitType<Lifesteal> LIFESTEAL = register(Ids.core("lifesteal"), Lifesteal.CODEC);
    public static final ClanTraitType<UndeadBane> UNDEAD_BANE = register(Ids.core("undead_bane"), UndeadBane.CODEC);
    public static final ClanTraitType<SunlightImmunity> SUNLIGHT_IMMUNITY = register(Ids.core("sunlight_immunity"), SunlightImmunity.CODEC);

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

    // ------------------------------------------------------------------ undead / sunlight / breathing / blood

    /** Marks the bearer as undead (vampires, zombies, Pillar Men): weak to affinities with an undead multiplier. */
    public record Undead() implements IClanTrait {
        public static final MapCodec<Undead> CODEC = MapCodec.unit(new Undead());
        @Override public ClanTraitType<?> type() { return UNDEAD; }
    }

    /**
     * Direct sunlight harms the bearer: {@code burn} (vampires crumble to ash) or {@code petrify} (Pillar Men turn to
     * stone: rooted and taking damage). A helmet or standing in shade / water / rain protects.
     */
    public record SunlightWeakness(float damagePerSecond, String effect) implements IClanTrait {
        public static final MapCodec<SunlightWeakness> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.floatRange(0f, 100f).optionalFieldOf("damage_per_second", 3f).forGetter(SunlightWeakness::damagePerSecond),
                Codec.STRING.optionalFieldOf("effect", "burn").forGetter(SunlightWeakness::effect)
        ).apply(i, SunlightWeakness::new));
        @Override public ClanTraitType<?> type() { return SUNLIGHT_WEAKNESS; }

        public static boolean inSunlight(net.minecraft.entity.LivingEntity e) {
            var w = e.getWorld();
            if (!w.isDay() || w.isRaining() || e.isTouchingWater() || e.inPowderSnow) return false;
            if (!e.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD).isEmpty()) return false;
            var pos = net.minecraft.util.math.BlockPos.ofFloored(e.getX(), e.getEyeY(), e.getZ());
            return w.isSkyVisible(pos) && w.getLightLevel(net.minecraft.world.LightType.SKY, pos) >= 13;
        }

        @Override public void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
            if (player.isCreative() || player.isSpectator() || !inSunlight(player)) return;
            if (ClanTraitHooks.traits(player, power).stream().anyMatch(t -> t instanceof SunlightImmunity)) return;
            if ("petrify".equals(effect)) {
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SLOWNESS, 30, 6, false, false, true));
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE, 30, 3, false, false, true));
                player.getServerWorld().spawnParticles(new net.minecraft.particle.BlockStateParticleEffect(net.minecraft.particle.ParticleTypes.BLOCK,
                        net.minecraft.block.Blocks.STONE.getDefaultState()), player.getX(), player.getBodyY(0.5), player.getZ(), 10, 0.3, 0.6, 0.3, 0.1);
            } else {
                player.setOnFireFor(3);
                player.getServerWorld().spawnParticles(net.minecraft.particle.ParticleTypes.ASH, player.getX(), player.getBodyY(0.5), player.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
            }
            player.damage(player.getDamageSources().onFire(), damagePerSecond);
            player.sendMessage(net.minecraft.text.Text.translatable("message.animecore.trait.sunlight").formatted(net.minecraft.util.Formatting.GOLD), true);
        }
    }

    /**
     * Breathing technique (the Ripple): while not sprinting and not under water the bearer's {@code pool} refills
     * {@code bonus} extra per second; under water breathing stops and the pool drains instead.
     */
    public record Breathing(Identifier pool, float bonus) implements IClanTrait {
        public static final MapCodec<Breathing> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.fieldOf("pool").forGetter(Breathing::pool),
                Codec.floatRange(0f, 100f).optionalFieldOf("bonus", 3f).forGetter(Breathing::bonus)
        ).apply(i, Breathing::new));
        @Override public ClanTraitType<?> type() { return BREATHING; }

        @Override public void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
            if (player.isSubmergedInWater()) {
                power.restore(pool, -bonus);
            } else if (!player.isSprinting()) {
                power.restore(pool, player.isSneaking() ? bonus * 2f : bonus);   // focused breathing while crouching
            }
        }
    }

    /** Drinking blood: dealing damage heals {@code healFraction} of it and refills {@code pool} by {@code energy}. */
    public record Lifesteal(float healFraction, Optional<Identifier> pool, float energy) implements IClanTrait {
        public static final MapCodec<Lifesteal> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.floatRange(0f, 4f).optionalFieldOf("heal_fraction", 0.25f).forGetter(Lifesteal::healFraction),
                Identifier.CODEC.optionalFieldOf("pool").forGetter(Lifesteal::pool),
                Codec.floatRange(0f, 100f).optionalFieldOf("energy", 3f).forGetter(Lifesteal::energy)
        ).apply(i, Lifesteal::new));
        @Override public ClanTraitType<?> type() { return LIFESTEAL; }

        @Override public void onDealtDamage(ServerPlayerEntity player, PlayerPowerComponent power, LivingEntity target, DamageSource source, float amount) {
            if (com.animecore.api.AnimeCoreApi.isUndead(target) || target.getType().isIn(net.minecraft.registry.tag.EntityTypeTags.ARTHROPOD)) return;
            player.heal(amount * healFraction);
            pool.ifPresent(p -> power.restore(p, energy));
        }
    }

    /** Ripple-charged blows: hitting an undead target adds {@code multiplier} x the damage as searing energy. */
    public record UndeadBane(float multiplier, int fireTicks) implements IClanTrait {
        private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
        public static final MapCodec<UndeadBane> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.floatRange(0f, 20f).optionalFieldOf("multiplier", 1f).forGetter(UndeadBane::multiplier),
                Codec.intRange(0, 600).optionalFieldOf("fire_ticks", 0).forGetter(UndeadBane::fireTicks)
        ).apply(i, UndeadBane::new));
        @Override public ClanTraitType<?> type() { return UNDEAD_BANE; }

        @Override public void onDealtDamage(ServerPlayerEntity player, PlayerPowerComponent power, LivingEntity target, DamageSource source, float amount) {
            if (ACTIVE.get() || !com.animecore.api.AnimeCoreApi.isUndead(target) || !target.isAlive()) return;
            ACTIVE.set(true);
            try {
                target.timeUntilRegen = 0;
                target.damage(player.getDamageSources().indirectMagic(player, player), amount * multiplier);
                if (fireTicks > 0) target.setOnFireForTicks(fireTicks);
                player.getServerWorld().spawnParticles(net.minecraft.particle.ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getBodyY(0.5), target.getZ(),
                        14, 0.3, 0.5, 0.3, 0.2);
            } finally {
                ACTIVE.set(false);
            }
        }
    }

    /** Overrides {@link SunlightWeakness} (the Ultimate Life Form walks in daylight). */
    public record SunlightImmunity() implements IClanTrait {
        public static final MapCodec<SunlightImmunity> CODEC = MapCodec.unit(new SunlightImmunity());
        @Override public ClanTraitType<?> type() { return SUNLIGHT_IMMUNITY; }
    }
}
