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

    /**
     * Faction a headband / badge / village uniform belongs to. {@code scratched} = defaced rogue headband (a line cut
     * through the symbol): it no longer pledges to the village and marks a missing-nin.
     */
    public record FactionBadge(Identifier faction, boolean scratched) {
        public FactionBadge(Identifier faction) { this(faction, false); }
        public static final Codec<FactionBadge> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.fieldOf("faction").forGetter(FactionBadge::faction),
                Codec.BOOL.optionalFieldOf("scratched", false).forGetter(FactionBadge::scratched)).apply(i, FactionBadge::new));
        public static final PacketCodec<ByteBuf, FactionBadge> PACKET_CODEC = PacketCodec.tuple(
                AnimePacketCodecs.IDENTIFIER, FactionBadge::faction, AnimePacketCodecs.BOOL, FactionBadge::scratched, FactionBadge::new);
        public FactionBadge scratch() { return new FactionBadge(faction, true); }
    }

    /**
     * Village-exclusive weapon / tool: full use (chakra infusion, moves, damage) only for members of {@code faction},
     * players whose reputation with it reaches {@code min_reputation}, or (when {@code rogue_ok}) missing-nin.
     */
    public record FactionAlignment(Identifier faction, int minReputation, boolean rogueOk) {
        public static final Codec<FactionAlignment> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.fieldOf("faction").forGetter(FactionAlignment::faction),
                Codec.INT.optionalFieldOf("min_reputation", 50).forGetter(FactionAlignment::minReputation),
                Codec.BOOL.optionalFieldOf("rogue_ok", false).forGetter(FactionAlignment::rogueOk)).apply(i, FactionAlignment::new));
        public static final PacketCodec<ByteBuf, FactionAlignment> PACKET_CODEC = PacketCodec.tuple(
                AnimePacketCodecs.IDENTIFIER, FactionAlignment::faction, AnimePacketCodecs.VAR_INT, FactionAlignment::minReputation,
                AnimePacketCodecs.BOOL, FactionAlignment::rogueOk, FactionAlignment::new);
    }

    public static final ComponentType<FactionAlignment> FACTION_ALIGNMENT =
            register(Ids.core("faction_alignment"), FactionAlignment.CODEC, FactionAlignment.PACKET_CODEC);

    /** Lineage (clan) an emblem / garb / sealed bloodline scroll belongs to. */
    public record ClanBinding(Identifier lineage) {
        public static final Codec<ClanBinding> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.fieldOf("lineage").forGetter(ClanBinding::lineage)).apply(i, ClanBinding::new));
        public static final PacketCodec<ByteBuf, ClanBinding> PACKET_CODEC =
                AnimePacketCodecs.IDENTIFIER.xmap(ClanBinding::new, ClanBinding::lineage);
    }

    /**
     * Display mirror of the owner's clan skill, written by the server whenever skill experience is gained while the
     * item is held. The authoritative value lives in the player's power component.
     */
    public record ClanSkill(int level, float progress) {
        public static final Codec<ClanSkill> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("level").forGetter(ClanSkill::level),
                Codec.FLOAT.optionalFieldOf("progress", 0f).forGetter(ClanSkill::progress)
        ).apply(i, ClanSkill::new));
        public static final PacketCodec<ByteBuf, ClanSkill> PACKET_CODEC = PacketCodec.tuple(
                net.minecraft.network.codec.PacketCodecs.VAR_INT, ClanSkill::level,
                net.minecraft.network.codec.PacketCodecs.FLOAT, ClanSkill::progress,
                ClanSkill::new);
    }

    public static final ComponentType<ClanBinding> CLAN_BINDING =
            register(Ids.core("clan_binding"), ClanBinding.CODEC, ClanBinding.PACKET_CODEC);
    public static final ComponentType<ClanSkill> CLAN_SKILL =
            register(Ids.core("clan_skill"), ClanSkill.CODEC, ClanSkill.PACKET_CODEC);

    public static final ComponentType<AbilityScroll> ABILITY_SCROLL =
            register(Ids.core("ability_scroll"), AbilityScroll.CODEC, AbilityScroll.PACKET_CODEC);
    public static final ComponentType<WeaponInfusion> WEAPON_INFUSION =
            register(Ids.core("weapon_infusion"), WeaponInfusion.CODEC, WeaponInfusion.PACKET_CODEC);
    public static final ComponentType<FactionBadge> FACTION_BADGE =
            register(Ids.core("faction_badge"), FactionBadge.CODEC, FactionBadge.PACKET_CODEC);

    /** Space-Time Gate anchor (dimension + position) stored on the tool. */
    public static final ComponentType<net.minecraft.util.math.GlobalPos> TOOL_ANCHOR =
            register(Ids.core("tool_anchor"), net.minecraft.util.math.GlobalPos.CODEC, net.minecraft.util.math.GlobalPos.PACKET_CODEC);

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
