package com.animecore.manifestation;

import com.animecore.ability.AbilityDamage;
import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.api.AnimeText;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.entity.AnimeEntities;
import com.animecore.entity.ManifestationEntity;
import com.animecore.entity.StoryNpcEntity;
import com.animecore.registry.AnimeRegistries;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Who has which {@link ManifestationDefinition} out, and the live {@link ManifestationEntity} for each user.
 * <ul>
 *   <li>Players keep one manifestation per anime mod ({@link PlayerPowerComponent#manifestation()}); its abilities are
 *       castable while it is assigned and casting one calls the Stand out automatically.</li>
 *   <li>Story characters with a {@code manifestation} call theirs out while they have a target.</li>
 *   <li>A manifested Stand joins its user's melee hits ({@code punch} damage) and performs their techniques.</li>
 * </ul>
 */
public final class ManifestationService {
    private ManifestationService() {}

    private static final Map<UUID, ManifestationEntity> LIVE = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) tickPlayer(p);
            LIVE.entrySet().removeIf(e -> e.getValue().isRemoved());
        });
        AnimeCoreEvents.PRE_ABILITY_CAST.register((player, ability, context) -> {
            var key = ability.getKey().map(k -> k.getValue()).orElse(null);
            PlayerPowerComponent power = context.power();
            if (key == null || power == null) return ActionResult.PASS;
            Optional<ManifestationDefinition> def = definition(player, power);
            if (def.isPresent() && def.get().abilities().contains(key)) {
                if (!power.manifested()) setManifested(player, power, true);
                ManifestationEntity stand = standOf(player);
                if (stand != null && stand.state() == ManifestationEntity.State.IDLE) stand.act(ManifestationEntity.State.CAST, 12);
            }
            return ActionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient || !(player instanceof ServerPlayerEntity sp) || !(entity instanceof LivingEntity target)) return ActionResult.PASS;
            ManifestationEntity stand = standOf(sp);
            if (stand == null) return ActionResult.PASS;
            ManifestationDefinition def = stand.definition().orElse(null);
            if (def == null || def.punch() <= 0f || def.kind() == ManifestationDefinition.Kind.AUTOMATIC) return ActionResult.PASS;
            if (stand.state() == ManifestationEntity.State.IDLE) stand.act(ManifestationEntity.State.PUNCH, 8);
            float dmg = def.punch() * userPower(sp);
            com.animecore.tool.ToolScheduler.run(3, t -> {
                if (t == 1 && target.isAlive()) {
                    AbilityDamage.apply((ServerWorld) world, sp, sp, target, dmg, null, 0, 0.4f, sp.getPos());
                    ((ServerWorld) world).spawnParticles(net.minecraft.particle.ParticleTypes.CRIT, target.getX(), target.getBodyY(0.6), target.getZ(),
                            6, 0.2, 0.2, 0.2, 0.3);
                }
                return true;
            });
            return ActionResult.PASS;
        });
    }

    // ------------------------------------------------------------------ queries

    public static Optional<ManifestationDefinition> definition(LivingEntity user, PlayerPowerComponent power) {
        return power.manifestation().flatMap(id -> AnimeRegistries.manifestation(user.getRegistryManager(), id));
    }

    /** The Stand currently out for {@code user} (player or story character), if any. */
    public static @Nullable ManifestationEntity standOf(LivingEntity user) {
        ManifestationEntity e = LIVE.get(user.getUuid());
        return e != null && !e.isRemoved() ? e : null;
    }

    /** The definition of the Stand {@code user} currently has out (players: any mod; NPCs: their own). */
    public static Optional<ManifestationDefinition> activeDefinition(LivingEntity user) {
        ManifestationEntity e = standOf(user);
        return e == null ? Optional.empty() : e.definition();
    }

    /** Whether {@code user} still wants {@code stand} out (checked by the entity every tick). */
    public static boolean isManifesting(LivingEntity user, ManifestationEntity stand) {
        return LIVE.get(user.getUuid()) == stand;
    }

    /** Trait multiplier used for a player's Stand punches (ability power of the owning mod), 1 for others. */
    private static float userPower(ServerPlayerEntity player) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            var power = ctx.maybePower(player).orElse(null);
            if (power != null && power.manifested()) return power.traits().abilityPower(null);
        }
        return 1f;
    }

    // ------------------------------------------------------------------ players

    /** Gives {@code player} the manifestation {@code id} (null removes it). Its abilities become castable. */
    public static void assign(ServerPlayerEntity player, PlayerPowerComponent power, @Nullable Identifier id) {
        if (power.manifested()) setManifested(player, power, false);
        power.setManifestation(id);
        if (id != null) {
            com.animecore.technique.TechniqueTokens.ensureTokens(player, power);
            Text name = AnimeRegistries.manifestation(player.getRegistryManager(), id).map(ManifestationDefinition::name).orElse(Text.literal(id.toString()));
            player.sendMessage(AnimeText.tr(power.context().modId(), "message.animecore.manifestation.awakened", name).formatted(Formatting.LIGHT_PURPLE), false);
        }
    }

    public static boolean toggle(ServerPlayerEntity player, PlayerPowerComponent power) {
        if (power.manifestation().isEmpty()) return false;
        setManifested(player, power, !power.manifested());
        return true;
    }

    public static void setManifested(ServerPlayerEntity player, PlayerPowerComponent power, boolean out) {
        if (out && power.manifestation().isEmpty()) return;
        // only one Stand at a time across anime mods
        if (out) for (AnimeModContext ctx : AnimeModRegistry.all()) {
            ctx.maybePower(player).filter(p -> p != power && p.manifested()).ifPresent(p -> p.setManifested(false));
        }
        power.setManifested(out);
        if (out) {
            summon(player, power.manifestation().get());
            definition(player, power).ifPresent(d -> player.sendMessage(d.name().copy().formatted(Formatting.BOLD, Formatting.LIGHT_PURPLE), true));
        } else {
            dismiss(player);
        }
    }

    private static void tickPlayer(ServerPlayerEntity player) {
        boolean wants = false;
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            var power = ctx.maybePower(player).orElse(null);
            if (power == null || !power.manifested()) continue;
            if (power.manifestation().isEmpty()) { power.setManifested(false); continue; }
            wants = true;
            ManifestationEntity cur = standOf(player);
            if (cur == null && player.isAlive() && !player.isSpectator()) summon(player, power.manifestation().get());
            else if (cur != null && !cur.definitionId().equals(power.manifestation().get())) summon(player, power.manifestation().get());
        }
        if (!wants && LIVE.containsKey(player.getUuid())) dismiss(player);
    }

    // ------------------------------------------------------------------ story characters

    /** Called every tick by a story character with a manifestation: out while it fights, back after 5 s of peace. */
    public static void tickNpc(StoryNpcEntity npc, Identifier manifestation, boolean fighting) {
        ManifestationEntity cur = standOf(npc);
        if (fighting && cur == null && npc.isAlive()) summon(npc, manifestation);
        else if (!fighting && cur != null && npc.age % 100 == 0) dismiss(npc);
    }

    // ------------------------------------------------------------------ entity

    private static void summon(LivingEntity user, Identifier id) {
        ManifestationEntity old = LIVE.remove(user.getUuid());
        if (old != null) old.discard();
        if (!(user.getWorld() instanceof ServerWorld world)) return;
        ManifestationEntity e = AnimeEntities.MANIFESTATION.create(world);
        if (e == null) return;
        e.setup(id, user);
        LIVE.put(user.getUuid(), e);
        world.spawnEntity(e);
        int color = e.definition().map(ManifestationDefinition::color).orElse(0xB39DDB);
        world.spawnParticles(new DustParticleEffect(new Vector3f(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f), 1.4f),
                e.getX(), e.getBodyY(0.5), e.getZ(), 30, 0.4, 0.8, 0.4, 0.05);
        world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 1f, 0.6f);
    }

    private static void dismiss(LivingEntity user) {
        ManifestationEntity e = LIVE.remove(user.getUuid());
        if (e == null) return;
        if (e.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(net.minecraft.particle.ParticleTypes.END_ROD, e.getX(), e.getBodyY(0.5), e.getZ(), 10, 0.3, 0.6, 0.3, 0.02);
        }
        e.discard();
    }

    /** Where a manifestation's technique comes from: the Stand's chest if it is out, else the user's eyes. */
    public static Vec3d origin(LivingEntity user) {
        ManifestationEntity e = standOf(user);
        return e != null ? e.getPos().add(0, e.getHeight() * 0.7, 0) : user.getEyePos();
    }

    /** All traits granted by the Stand {@code user} has out right now. */
    public static List<com.animecore.clan.IClanTrait> activeTraits(LivingEntity user) {
        return activeDefinition(user).map(ManifestationDefinition::traits).orElse(List.of());
    }

    public static boolean hasTrait(Entity entity, Class<? extends com.animecore.clan.IClanTrait> type) {
        if (!(entity instanceof LivingEntity user)) return false;
        if (user instanceof ManifestationEntity m) {
            LivingEntity owner = m.owner();
            return owner != null && hasTrait(owner, type);
        }
        if (user instanceof StoryNpcEntity npc) {
            Optional<Identifier> id = npc.definition().flatMap(com.animecore.quest.StoryNpcDefinition::manifestation);
            return id.flatMap(i -> AnimeRegistries.manifestation(npc.getRegistryManager(), i))
                    .map(d -> d.traits().stream().anyMatch(type::isInstance)).orElse(false);
        }
        return activeTraits(user).stream().anyMatch(type::isInstance);
    }
}
