package com.animecore.qa;

import com.animecore.ability.action.AbilityActions;
import com.animecore.api.AnimeModContext;
import com.animecore.quest.StoryNpcDefinition;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueTokens;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The production quality bar, checked automatically (CLAUDE.md "Production standard"). Every content mod runs
 * {@link #audit} from a GameTest; a non-empty result fails the build of that mod. For each piece of content of the mod:
 * <ul>
 *   <li>techniques: a translated name and description, a cast animation that exists, a technique item to cast it from,
 *       an energy pool of the mod, at least one action;</li>
 *   <li>forms and fighting styles / lineages: translated name (+ description for lineages), a technique item for forms;</li>
 *   <li>manifestations (Stands): translated name, a call-out technique, a low-poly model file when one is named;</li>
 *   <li>story characters: translated name; enemies and bosses fight with at least one technique;</li>
 *   <li>quests: translated name, objectives, a giver or an intro; energy pools and items: translations, item models.</li>
 * </ul>
 */
public final class PolishAudit {
    private PolishAudit() {}

    public static Map<String, List<String>> audit(DynamicRegistryManager rm, AnimeModContext ctx) {
        String ns = ctx.modId();
        Map<String, List<String>> issues = new LinkedHashMap<>();
        Language lang = Language.getInstance();
        Set<Identifier> animations = animations();

        for (var e : rm.get(AnimeRegistries.ABILITY).getEntrySet()) {
            var def = e.getValue();
            if (!def.system().equals(ns)) continue;
            String id = e.getKey().getValue().toString();
            name(issues, "technique name", id, def.name(), lang);
            String key = key(def.name());
            if (key != null && !lang.hasTranslation(key + ".desc")) add(issues, "technique description", id);
            def.castAnimation().ifPresent(a -> { if (!animations.contains(a)) add(issues, "technique animation", id + " -> " + a); });
            if (def.actions().isEmpty()) add(issues, "technique without actions", id);
            if (TechniqueTokens.item(ns, TechniqueTokens.tokenOf(def)).isEmpty()) add(issues, "technique item", id + " -> " + TechniqueTokens.tokenOf(def));
            if (def.cost().amount() > 0 && ctx.pool(def.cost().pool()).isEmpty()) add(issues, "technique energy pool", id + " -> " + def.cost().pool());
        }
        for (var e : rm.get(AnimeRegistries.TRANSFORMATION).getEntrySet()) {
            var def = e.getValue();
            if (!def.system().equals(ns)) continue;
            name(issues, "form name", e.getKey().getValue().toString(), def.name(), lang);
        }
        for (var e : rm.get(AnimeRegistries.LINEAGE).getEntrySet()) {
            var def = e.getValue();
            if (!def.system().equals(ns)) continue;
            name(issues, "lineage name", e.getKey().getValue().toString(), def.name(), lang);
            name(issues, "lineage description", e.getKey().getValue().toString(), def.description(), lang);
        }
        for (var e : rm.get(AnimeRegistries.MANIFESTATION).getEntrySet()) {
            var def = e.getValue();
            if (!def.system().equals(ns)) continue;
            String id = e.getKey().getValue().toString();
            name(issues, "manifestation name", id, def.name(), lang);
            boolean callOut = def.abilities().stream().anyMatch(a -> AnimeRegistries.ability(rm, a)
                    .map(x -> x.value().actions().stream().anyMatch(act -> act.type() == AbilityActions.MANIFEST)).orElse(false));
            if (!callOut) add(issues, "manifestation call-out technique", id);
            def.appearance().model().ifPresent(m -> { if (!asset(m.getNamespace(), "form_models/" + m.getPath() + ".json")) add(issues, "manifestation model", id + " -> " + m); });
        }
        for (var e : rm.get(AnimeRegistries.STORY_NPC).getEntrySet()) {
            var def = e.getValue();
            if (!def.system().equals(ns)) continue;
            String id = e.getKey().getValue().toString();
            name(issues, "character name", id, def.name(), lang);
            if (def.role().hostile() && def.abilities().isEmpty()) add(issues, "enemy without techniques", id);
            def.appearance().model().ifPresent(m -> { if (!asset(m.getNamespace(), "form_models/" + m.getPath() + ".json")) add(issues, "character model", id + " -> " + m); });
        }
        for (var e : rm.get(AnimeRegistries.QUEST).getEntrySet()) {
            var def = e.getValue();
            if (!def.system().equals(ns)) continue;
            String id = e.getKey().getValue().toString();
            name(issues, "quest name", id, def.name(), lang);
            if (def.objectives().isEmpty()) add(issues, "quest without objectives", id);
            if (def.giver().isEmpty() && def.intro().isEmpty()) add(issues, "quest without giver or intro", id);
        }
        for (var spec : ctx.pools()) {
            String key = "pool." + spec.id().getNamespace() + "." + spec.id().getPath();
            if (!lang.hasTranslation(key)) add(issues, "energy pool name", key);
        }
        for (Identifier itemId : Registries.ITEM.getIds()) {
            if (!itemId.getNamespace().equals(ns)) continue;
            if (!lang.hasTranslation(Registries.ITEM.get(itemId).getTranslationKey())) add(issues, "item name", itemId.toString());
            if (!asset(ns, "models/item/" + itemId.getPath() + ".json")) add(issues, "item model", itemId.toString());
        }
        return issues;
    }

    /** "category: n (first few)" lines, for a readable failure message. */
    public static String report(Map<String, List<String>> issues) {
        StringBuilder sb = new StringBuilder();
        issues.forEach((k, v) -> sb.append(k).append(": ").append(v.size()).append(" ").append(v.subList(0, Math.min(5, v.size()))).append("\n"));
        return sb.toString();
    }

    private static void name(Map<String, List<String>> issues, String what, String id, Text text, Language lang) {
        String key = key(text);
        if (text.getString().isBlank() || (key != null && !lang.hasTranslation(key))) add(issues, what, id);
    }

    private static String key(Text text) {
        return text.getContent() instanceof TranslatableTextContent t ? t.getKey() : null;
    }

    private static void add(Map<String, List<String>> issues, String what, String detail) {
        issues.computeIfAbsent(what, k -> new ArrayList<>()).add(detail);
    }

    private static boolean asset(String ns, String path) {
        for (var mod : FabricLoader.getInstance().getAllMods()) {
            Optional<Path> p = mod.findPath("assets/" + ns + "/" + path);
            if (p.isPresent() && Files.exists(p.get())) return true;
        }
        return false;
    }

    /** Every PlayerAnimator animation shipped by any mod ({@code assets/<ns>/player_animations/*.json}). */
    private static Set<Identifier> animations() {
        Set<Identifier> out = new HashSet<>();
        for (var mod : FabricLoader.getInstance().getAllMods()) {
            String ns = mod.getMetadata().getId();
            mod.findPath("assets/" + ns + "/player_animations").filter(Files::isDirectory).ifPresent(dir -> {
                try (var files = Files.list(dir)) {
                    for (Path f : (Iterable<Path>) files::iterator) {
                        try (Reader r = Files.newBufferedReader(f)) {
                            JsonParser.parseReader(r).getAsJsonObject().getAsJsonObject("animations").keySet()
                                    .forEach(a -> out.add(Identifier.of(ns, a)));
                        }
                    }
                } catch (Exception ignored) { }
            });
        }
        return out;
    }
}
