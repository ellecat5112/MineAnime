package com.animecore.manifestation;

import com.animecore.clan.IClanTrait;
import com.animecore.quest.StoryNpcDefinition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.List;
import java.util.Map;

/**
 * A manifested fighting spirit ({@code data/<ns>/animecore/manifestation/<id>.json}): JoJo Stands, and equally Personas,
 * Susanoo-like avatars or Bankai spirits. Its user calls it out as a real entity ({@link com.animecore.entity.ManifestationEntity})
 * that hovers at their shoulder, joins their punches, performs their techniques (rushes, remote strikes...) and grants
 * {@code traits} while it is out.
 *
 * <pre>{
 *   "system": "jojorpg", "name": {"text": "Star Platinum"}, "kind": "close_range",
 *   "appearance": {"skin": 8280002, "hair": 1315860, "hair_style": "long", "outfit": "bodysuit", ...},  // or "model": "ns:form_model"
 *   "scale": 1.05, "alpha": 0.8, "color": 9055202, "range": 2, "punch": 7,
 *   "stats": {"power": "A", "speed": "A", "range": "C", "stamina": "A", "precision": "A", "potential": "A"},
 *   "abilities": ["jojorpg:star_platinum_manifest", "jojorpg:ora_ora", ...],
 *   "traits": [{"type": "animecore:time_immune"}], "weight": 2
 * }</pre>
 * {@code weight} > 0 makes it one of the possible results of the system's random awakening item (a Stand Arrow).
 */
public record ManifestationDefinition(
        String system,
        Text name,
        Text description,
        Kind kind,
        StoryNpcDefinition.Appearance appearance,
        float scale,
        float alpha,
        int color,
        float range,
        float punch,
        Map<String, String> stats,
        List<Identifier> abilities,
        List<IClanTrait> traits,
        int weight
) {
    public enum Kind implements StringIdentifiable {
        /** Fights at its user's side (Star Platinum, The World). */
        CLOSE_RANGE("close_range"),
        /** Flies out to strike from afar (Hierophant Green, Tower of Gray). */
        LONG_RANGE("long_range"),
        /** Acts on its own once released (Anubis, Osiris). */
        AUTOMATIC("automatic"),
        /** Bound to an object, the user's body or a substance (Emperor, The Fool, Yellow Temperance). */
        BOUND("bound");
        public static final Codec<Kind> CODEC = StringIdentifiable.createCodec(Kind::values);
        private final String name;
        Kind(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }

    public static final Codec<ManifestationDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(ManifestationDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(ManifestationDefinition::name),
            TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(ManifestationDefinition::description),
            Kind.CODEC.optionalFieldOf("kind", Kind.CLOSE_RANGE).forGetter(ManifestationDefinition::kind),
            StoryNpcDefinition.Appearance.CODEC.optionalFieldOf("appearance", StoryNpcDefinition.Appearance.DEFAULT).forGetter(ManifestationDefinition::appearance),
            Codec.floatRange(0.1f, 8f).optionalFieldOf("scale", 1f).forGetter(ManifestationDefinition::scale),
            Codec.floatRange(0.05f, 1f).optionalFieldOf("alpha", 0.8f).forGetter(ManifestationDefinition::alpha),
            Codec.INT.optionalFieldOf("color", 0xB39DDB).forGetter(ManifestationDefinition::color),
            Codec.floatRange(0f, 256f).optionalFieldOf("range", 2f).forGetter(ManifestationDefinition::range),
            Codec.floatRange(0f, 200f).optionalFieldOf("punch", 3f).forGetter(ManifestationDefinition::punch),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("stats", Map.of()).forGetter(ManifestationDefinition::stats),
            Identifier.CODEC.listOf().optionalFieldOf("abilities", List.of()).forGetter(ManifestationDefinition::abilities),
            IClanTrait.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(ManifestationDefinition::traits),
            Codec.intRange(0, 1000).optionalFieldOf("weight", 0).forGetter(ManifestationDefinition::weight)
    ).apply(i, ManifestationDefinition::new));

    /** Humanoid stands are drawn with the tinted story-character layers; others with their low-poly model. */
    public boolean humanoid() { return appearance.model().isEmpty(); }
}
