package com.animecore.client.combo;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.element.ComboTrailElement;
import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.network.ComboInputPayload;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Radial combo input. Hold the wheel key, point at a sign and click (or press 1-9) to perform it; releasing the
 * key "releases" the combo. Sectors that continue a learned sequence glow, and matching techniques are listed in
 * the centre. Works for Naruto hand signs, Kido incantations or Dragon Ball directional inputs alike.
 */
public class ComboWheelScreen extends Screen {
    private static final int SEGMENTS_PER_SECTOR = 16;

    private final AnimeModContext ctx;
    private final List<Identifier> tokens;
    private final KeyBinding wheelKey;
    private final List<Identifier> echo = new ArrayList<>();
    private int hovered = -1;
    private int lastServerSize;

    public ComboWheelScreen(AnimeModContext ctx, List<Identifier> tokens, KeyBinding wheelKey) {
        super(Text.translatable("screen.animecore.combo_wheel"));
        this.ctx = ctx;
        this.tokens = List.copyOf(tokens);
        this.wheelKey = wheelKey;
    }

    @Override public boolean shouldPause() { return false; }

    /** Keep the world visible (no 1.21 blur). */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, width, height, 0x30000000, 0x50000000);
    }

    @Override
    public void tick() {
        // Server auto-casts on an unambiguous match and clears its buffer -> clear our echo too.
        int serverSize = power().map(p -> p.comboBuffer().size()).orElse(0);
        if (lastServerSize > 0 && serverSize == 0) echo.clear();
        lastServerSize = serverSize;
    }

    private Optional<PlayerPowerComponent> power() {
        return client == null || client.player == null ? Optional.empty() : ctx.maybePower(client.player);
    }

    private float outerRadius() { return Math.min(width, height) * 0.27f; }
    private float innerRadius() { return outerRadius() * 0.42f; }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        float cx = width / 2f, cy = height / 2f;
        float outer = outerRadius(), inner = innerRadius();
        hovered = sectorAt(mouseX, mouseY);

        Set<Identifier> continuations = new HashSet<>();
        List<Text> candidates = new ArrayList<>();
        collectCandidates(continuations, candidates);

        int accent = ctx.pools().get(0).color();
        context.draw(); // flush batched GUI before raw geometry
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        int n = tokens.size();
        for (int i = 0; i < n; i++) {
            int color;
            if (i == hovered) color = 0xD0000000 | lighten(accent);
            else if (continuations.contains(tokens.get(i))) color = 0xA0000000 | accent;
            else color = 0x90202028;
            ring(matrix, cx, cy, inner, outer, sectorStart(i, n) + 0.02f, sectorStart(i + 1, n) - 0.02f, color);
        }
        RenderSystem.disableBlend();

        for (int i = 0; i < n; i++) {
            float mid = (sectorStart(i, n) + sectorStart(i + 1, n)) / 2f;
            float r = (inner + outer) / 2f;
            int tx = Math.round(cx + MathHelper.cos(mid) * r);
            int ty = Math.round(cy + MathHelper.sin(mid) * r);
            context.drawCenteredTextWithShadow(textRenderer, ComboTrailElement.tokenLabel(tokens.get(i)), tx, ty - 8, 0xFFFFFFFF);
            context.drawCenteredTextWithShadow(textRenderer, String.valueOf(i + 1), tx, ty + 3, 0xFF9090A0);
        }

        // Centre: the sequence so far (last tokens if it would not fit the hole)
        int hole = Math.round(inner * 1.8f);
        String seq = "";
        for (int i = echo.size() - 1; i >= 0; i--) {
            String next = ComboTrailElement.tokenLabel(echo.get(i)).getString() + (seq.isEmpty() ? "" : " › " + seq);
            if (textRenderer.getWidth(next) > hole) { seq = "… " + seq; break; }
            seq = next;
        }
        context.drawCenteredTextWithShadow(textRenderer, seq.isEmpty() ? title.getString() : seq, Math.round(cx), Math.round(cy) - 4, 0xFFFFFFFF);

        // Below the wheel: techniques still reachable from this sequence, then the hint
        int y = Math.round(cy + outer) + 6;
        for (Text candidate : candidates.subList(0, Math.min(3, candidates.size()))) {
            context.drawCenteredTextWithShadow(textRenderer, candidate, Math.round(cx), y, 0xFF000000 | lighten(accent));
            y += 10;
        }
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.animecore.combo_wheel.hint"),
                Math.round(cx), y + 2, 0xFFB0B0B0);
    }

    private void collectCandidates(Set<Identifier> continuations, List<Text> candidates) {
        if (client == null || client.world == null) return;
        PlayerPowerComponent power = power().orElse(null);
        if (power == null) return;
        var rm = client.world.getRegistryManager();
        for (Identifier id : power.castableAbilities()) {
            Optional<AbilityDefinition> def = AnimeRegistries.ability(rm, id).map(RegistryEntry::value);
            if (def.isEmpty() || def.get().combo().isEmpty() || !def.get().system().equals(ctx.modId())) continue;
            List<Identifier> combo = def.get().combo();
            if (combo.size() < echo.size() || !combo.subList(0, echo.size()).equals(echo)) continue;
            if (combo.size() > echo.size()) continuations.add(combo.get(echo.size()));
            candidates.add(def.get().name());
        }
    }

    // ---------------------------------------------------------------- input

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && hovered >= 0) {
            input(tokens.get(hovered));
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            commitAndClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int index = keyCode - GLFW.GLFW_KEY_1;
        if (index >= 0 && index < Math.min(9, tokens.size())) {
            input(tokens.get(index));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (wheelKey.matchesKey(keyCode, scanCode)) {
            commitAndClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void input(Identifier token) {
        echo.add(token);
        AnimeClientNetworking.send(new ComboInputPayload(ctx.modId(), token, false));
    }

    private void commitAndClose() {
        if (!echo.isEmpty()) AnimeClientNetworking.send(new ComboInputPayload(ctx.modId(), ctx.id("release"), true));
        close();
    }

    // ---------------------------------------------------------------- geometry

    /** Sector 0 is centred at the top, going clockwise. */
    private static float sectorStart(int i, int n) {
        return (float) (-Math.PI / 2 - Math.PI / n + i * (2 * Math.PI / n));
    }

    private int sectorAt(double mx, double my) {
        double dx = mx - width / 2.0, dy = my - height / 2.0;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < innerRadius() * 0.6 || tokens.isEmpty()) return -1;
        double angle = Math.atan2(dy, dx) - sectorStart(0, tokens.size());
        angle = (angle % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI);
        return (int) (angle / (2 * Math.PI / tokens.size())) % tokens.size();
    }

    private static void ring(Matrix4f m, float cx, float cy, float inner, float outer, float a0, float a1, int argb) {
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int s = 0; s <= SEGMENTS_PER_SECTOR; s++) {
            float a = a0 + (a1 - a0) * s / SEGMENTS_PER_SECTOR;
            float cos = MathHelper.cos(a), sin = MathHelper.sin(a);
            buf.vertex(m, cx + cos * outer, cy + sin * outer, 0).color(argb);
            buf.vertex(m, cx + cos * inner, cy + sin * inner, 0).color(argb);
        }
        BufferRenderer.drawWithGlobalProgram(buf.end());
    }

    private static int lighten(int rgb) {
        int r = Math.min(255, ((rgb >> 16) & 0xFF) + 70);
        int g = Math.min(255, ((rgb >> 8) & 0xFF) + 70);
        int b = Math.min(255, (rgb & 0xFF) + 70);
        return (r << 16) | (g << 8) | b;
    }
}
