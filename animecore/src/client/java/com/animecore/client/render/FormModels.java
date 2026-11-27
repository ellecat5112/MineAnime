package com.animecore.client.render;

import com.animecore.AnimeCore;
import com.animecore.platform.Ids;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.model.ModelPart;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data-driven low-poly models ({@code assets/<ns>/form_models/<path>.json}) for forms and story characters: tailed
 * beasts, Susanoo... Each model is a tree of named parts made of boxes (vanilla box-UV on one texture), with a procedural
 * animation tag per part (legs walk, tails sway, wings flap, jaw snaps, head looks). Built straight into vanilla
 * {@link ModelPart}s - no extra libraries.
 *
 * <pre>{
 *   "texture": "narutorpg:textures/entity/form/shukaku.png", "texture_size": [128, 128], "translucent": false,
 *   "parts": [
 *     {"name": "body", "pivot": [0, 10, 0], "cubes": [{"from": [-12, -10, -10], "size": [24, 20, 22], "uv": [0, 0]}], "anim": "bob"},
 *     {"name": "head", "parent": "body", "pivot": [0, -4, -10], "rotation": [0, 0, 0], "cubes": [...], "anim": "head"},
 *     {"name": "tail0", "parent": "body", "pivot": [0, -2, 11], "rotation": [-0.8, 0, 0], "cubes": [...], "anim": "tail", "phase": 0.0}
 *   ]
 * }</pre>
 * Coordinates are vanilla entity-model pixels: y grows downward, the ground is y = 24, the front faces -z.
 */
public final class FormModels {
    private FormModels() {}

    public enum Anim { NONE, HEAD, LEG_A, LEG_B, ARM_A, ARM_B, TAIL, WING, JAW, BOB, SWAY }

    public record Part(ModelPart part, Anim anim, float phase, float amp, float pitch, float yaw, float roll, float pivotY) {}

    public record Model(Identifier texture, boolean translucent, ModelPart root, List<Part> parts) {}

    private static final Map<Identifier, Model> MODELS = new HashMap<>();

    public static @Nullable Model get(Identifier id) { return MODELS.get(id); }

    public static void register() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override public Identifier getFabricId() { return Ids.core("form_models"); }

            @Override public void reload(ResourceManager manager) {
                MODELS.clear();
                for (Map.Entry<Identifier, Resource> e : manager.findResources("form_models", id -> id.getPath().endsWith(".json")).entrySet()) {
                    Identifier file = e.getKey();
                    Identifier id = Identifier.of(file.getNamespace(), file.getPath().substring("form_models/".length(), file.getPath().length() - 5));
                    try (Reader r = e.getValue().getReader()) {
                        MODELS.put(id, parse(JsonParser.parseReader(r).getAsJsonObject()));
                    } catch (Exception ex) {
                        AnimeCore.LOGGER.error("Bad form model {}", file, ex);
                    }
                }
                AnimeCore.LOGGER.info("Loaded {} form models", MODELS.size());
            }
        });
    }

    private static float[] vec(JsonObject o, String key, float... def) {
        if (!o.has(key)) return def;
        JsonArray a = o.getAsJsonArray(key);
        float[] v = new float[a.size()];
        for (int i = 0; i < v.length; i++) v[i] = a.get(i).getAsFloat();
        return v;
    }

    static Model parse(JsonObject json) {
        float[] ts = vec(json, "texture_size", 64, 64);
        Map<String, List<ModelPart.Cuboid>> cubes = new LinkedHashMap<>();
        Map<String, JsonObject> specs = new LinkedHashMap<>();
        for (JsonElement el : json.getAsJsonArray("parts")) {
            JsonObject p = el.getAsJsonObject();
            String name = p.get("name").getAsString();
            specs.put(name, p);
            List<ModelPart.Cuboid> list = new ArrayList<>();
            if (p.has("cubes")) for (JsonElement ce : p.getAsJsonArray("cubes")) {
                JsonObject c = ce.getAsJsonObject();
                float[] from = vec(c, "from", 0, 0, 0), size = vec(c, "size", 1, 1, 1), uv = vec(c, "uv", 0, 0);
                float inflate = c.has("inflate") ? c.get("inflate").getAsFloat() : 0f;
                boolean mirror = c.has("mirror") && c.get("mirror").getAsBoolean();
                list.add(new ModelPart.Cuboid((int) uv[0], (int) uv[1], from[0], from[1], from[2], size[0], size[1], size[2],
                        inflate, inflate, inflate, mirror, ts[0], ts[1], EnumSet.allOf(Direction.class)));
            }
            cubes.put(name, list);
        }
        // build children bottom-up
        Map<String, Map<String, ModelPart>> children = new HashMap<>();
        Map<String, ModelPart> built = new HashMap<>();
        List<String> order = new ArrayList<>(specs.keySet());
        for (int i = order.size() - 1; i >= 0; i--) {
            String name = order.get(i);
            ModelPart part = new ModelPart(cubes.get(name), children.getOrDefault(name, new HashMap<>()));
            built.put(name, part);
            JsonObject p = specs.get(name);
            String parent = p.has("parent") && !p.get("parent").isJsonNull() ? p.get("parent").getAsString() : "";
            children.computeIfAbsent(parent, k -> new HashMap<>()).put(name, part);
        }
        ModelPart root = new ModelPart(List.of(), children.getOrDefault("", new HashMap<>()));
        List<Part> parts = new ArrayList<>();
        for (String name : order) {
            JsonObject p = specs.get(name);
            ModelPart part = built.get(name);
            float[] pivot = vec(p, "pivot", 0, 0, 0), rot = vec(p, "rotation", 0, 0, 0);
            part.setPivot(pivot[0], pivot[1], pivot[2]);
            part.setAngles(rot[0], rot[1], rot[2]);
            Anim anim = p.has("anim") ? Anim.valueOf(p.get("anim").getAsString().toUpperCase(java.util.Locale.ROOT)) : Anim.NONE;
            parts.add(new Part(part, anim, p.has("phase") ? p.get("phase").getAsFloat() : 0f, p.has("amp") ? p.get("amp").getAsFloat() : 1f,
                    rot[0], rot[1], rot[2], pivot[1]));
        }
        boolean translucent = json.has("translucent") && json.get("translucent").getAsBoolean();
        return new Model(Identifier.of(json.get("texture").getAsString()), translucent, root, parts);
    }
}
