package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.platform.AnimePacketCodecs;
import com.animecore.platform.Ids;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * 1.21.1 Data Components. The three engine components are shared infrastructure ({@code animecore:*}, one engine per
 * pack) whose <i>values</i> carry namespaced ids, so JEI, Lootr loot functions and tooltips work for every anime mod
 * generically. Mod-specific components go through {@link #register(AnimeModContext, String, Codec, PacketCodec)}
 * and are namespaced {@code <modId>:<path>}.
 */
public final class AnimeDataComponents {
    private AnimeDataComponents() {}

    /** Which ability a scroll teaches. */
    public record AbilityScroll(Identifier ability) {
        public static final Codec<AbilityScroll> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.fieldOf("ability").forGetter(AbilityScroll::ability)).apply(i, AbilityScroll::new));
        public static final PacketCodec<ByteBuf, AbilityScroll> PACKET_CODEC =
                AnimePacketCodecs.IDENTIFIER.xmap(AbilityScroll::new, AbilityScroll::ability);
    }

    /** Elemental / energy infusion state of an {@link AnimeWeaponItem}. */
    public record WeaponInfusion(Optional<Identifier> affinity, boolean active) {
        public static final WeaponInfusion NONE = new WeaponInfusion(Optional.empty(), false);
        public static final Codec<WeaponInfusion> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.optionalFieldOf("affinity").forGetter(WeaponInfusion::affinity),
                Codec.BOOL.optionalFieldOf("active", false).forGetter(WeaponInfusion::active)
        ).apply(i, WeaponInfusion::new));
        public static final PacketCodec<ByteBuf, WeaponInfusion> PACKET_CODEC = PacketCodec.tuple(
                AnimePacketCodecs.OPTIONAL_IDENTIFIER, WeaponInfusion::affinity,
                AnimePacketCodecs.BOOL, WeaponInfusion::active,
                WeaponInfusion::new);

        public WeaponInfusion withActive(boolean value) { return new WeaponInfusion(affinity, value); }
        public WeaponInfusion withAffinity(Identifier value) { return new WeaponInfusion(Optional.of(value), active); }
    }

    /** Faction a headband / badge belongs to. */
    public record FactionBadge(Identifier faction) {
        public static final Codec<FactionBadge> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.fieldOf("faction").forGetter(FactionBadge::faction)).apply(i, FactionBadge::new));
        public static final PacketCodec<ByteBuf, FactionBadge> PACKET_CODEC =
                AnimePacketCodecs.IDENTIFIER.xmap(FactionBadge::new, FactionBadge::faction);
    }

    public static final ComponentType<AbilityScroll> ABILITY_SCROLL =
            register(Ids.core("ability_scroll"), AbilityScroll.CODEC, AbilityScroll.PACKET_CODEC);
    public static final ComponentType<WeaponInfusion> WEAPON_INFUSION =
            register(Ids.core("weapon_infusion"), WeaponInfusion.CODEC, WeaponInfusion.PACKET_CODEC);
    public static final ComponentType<FactionBadge> FACTION_BADGE =
            register(Ids.core("faction_badge"), FactionBadge.CODEC, FactionBadge.PACKET_CODEC);

    /** Mod-specific component: {@code <modId>:<path>}. */
    public static <T> ComponentType<T> register(AnimeModContext ctx, String path, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
        return register(ctx.id(path), codec, packetCodec);
    }

    private static <T> ComponentType<T> register(Identifier id, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id,
                ComponentType.<T>builder().codec(codec).packetCodec(packetCodec).build());
    }

    public static void init() {}
}
