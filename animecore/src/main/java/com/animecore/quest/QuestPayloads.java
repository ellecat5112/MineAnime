package com.animecore.quest;

import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

/** Quest networking: journal actions (C2S), open journal and dialogue (S2C). */
public final class QuestPayloads {
    private QuestPayloads() {}

    /** C2S: {@code action} = accept | abandon | summon (respawn the current objective's characters). */
    public record Action(String action, Identifier quest) implements CustomPayload {
        public static final Id<Action> ID = AnimeNetworking.payloadId("animecore", "quest_action");
        public static final PacketCodec<RegistryByteBuf, Action> CODEC = PacketCodec.tuple(
                PacketCodecs.STRING, Action::action, AnimePacketCodecs.IDENTIFIER, Action::quest, Action::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    /** S2C: open the quest journal (quest scroll used). */
    public record OpenJournal(String system) implements CustomPayload {
        public static final Id<OpenJournal> ID = AnimeNetworking.payloadId("animecore", "open_journal");
        public static final PacketCodec<RegistryByteBuf, OpenJournal> CODEC = PacketCodec.tuple(
                PacketCodecs.STRING, OpenJournal::system, OpenJournal::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    /** S2C: show a dialogue box: {@code speaker} says {@code lines}, one per click. */
    public record Dialogue(Text speaker, List<Text> lines) implements CustomPayload {
        public static final Id<Dialogue> ID = AnimeNetworking.payloadId("animecore", "dialogue");
        public static final PacketCodec<RegistryByteBuf, Dialogue> CODEC = PacketCodec.tuple(
                TextCodecs.REGISTRY_PACKET_CODEC, Dialogue::speaker,
                TextCodecs.REGISTRY_PACKET_CODEC.collect(PacketCodecs.toList()), Dialogue::lines, Dialogue::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    static void register() {
        AnimeNetworking.registerC2S(Action.ID, Action.CODEC);
        AnimeNetworking.registerS2C(OpenJournal.ID, OpenJournal.CODEC);
        AnimeNetworking.registerS2C(Dialogue.ID, Dialogue.CODEC);
    }
}
