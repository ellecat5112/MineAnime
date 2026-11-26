package com.animecore.client.quest;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.quest.QuestDefinition;
import com.animecore.quest.QuestLog;
import com.animecore.quest.QuestManager;
import com.animecore.quest.QuestPayloads;
import com.animecore.registry.AnimeRegistries;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.Deque;

/** Client side of story mode: opens the journal / dialogue boxes and draws the objective tracker with a waypoint. */
public final class QuestClient {
    private QuestClient() {}

    private static final Deque<QuestPayloads.Dialogue> PENDING = new ArrayDeque<>();
    private static final String[] ARROWS = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};

    public static void init() {
        AnimeClientNetworking.registerReceiver(QuestPayloads.OpenJournal.ID, (payload, client) ->
                client.setScreen(new QuestJournalScreen(payload.system())));
        AnimeClientNetworking.registerReceiver(QuestPayloads.Dialogue.ID, (payload, client) -> {
            if (client.currentScreen instanceof DialogueScreen) PENDING.add(payload);
            else client.setScreen(new DialogueScreen(payload));
        });
        HudRenderCallback.EVENT.register(QuestClient::renderTracker);
    }

    /** Next queued dialogue, if any (DialogueScreen chains them). */
    static QuestPayloads.Dialogue pollPending() { return PENDING.poll(); }

    private static void renderTracker(DrawContext ctx, RenderTickCounter counter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden || mc.currentScreen != null) return;
        for (AnimeModContext mod : AnimeModRegistry.all()) {
            QuestLog log = mod.power(mc.player).quests();
            Identifier id = log.active().orElse(null);
            if (id == null) continue;
            QuestDefinition q = AnimeRegistries.quest(mc.player.networkHandler.getRegistryManager(), id).orElse(null);
            if (q == null || log.step() >= q.objectives().size()) continue;
            QuestDefinition.Objective o = q.objectives().get(log.step());
            TextRenderer tr = mc.textRenderer;
            int w = ctx.getScaledWindowWidth();
            int x = w - 6;
            int y = 6;
            Text title = q.name().copy().formatted(Formatting.GOLD, Formatting.BOLD);
            Text step = Text.literal((log.step() + 1) + "/" + q.objectives().size() + "  ").formatted(Formatting.GRAY)
                    .append(QuestManager.objectiveText(o).copy().formatted(Formatting.WHITE));
            if (o.count() > 1 && o.type() != QuestDefinition.ObjectiveType.TRAVEL && o.type() != QuestDefinition.ObjectiveType.RANK) {
                step = step.copy().append(Text.literal("  " + log.progress() + "/" + o.count()).formatted(Formatting.AQUA));
            }
            int boxW = Math.min(w / 2, Math.max(tr.getWidth(title), tr.getWidth(step)) + 8);
            var lines = tr.wrapLines(step, boxW - 8);
            int boxH = 14 + lines.size() * 10 + (log.site().isPresent() ? 11 : 0);
            ctx.fill(x - boxW, y - 3, x + 3, y + boxH, 0x88000000);
            ctx.drawTextWithShadow(tr, title, x - boxW + 2, y, 0xFFFFFF);
            int ly = y + 12;
            for (var line : lines) {
                ctx.drawTextWithShadow(tr, line, x - boxW + 2, ly, 0xFFFFFF);
                ly += 10;
            }
            log.site().ifPresent(site -> drawWaypoint(ctx, tr, mc, site, x - boxW + 2, y + boxH - 11));
            return;   // one tracker is enough
        }
    }

    private static void drawWaypoint(DrawContext ctx, TextRenderer tr, MinecraftClient mc, BlockPos site, int x, int y) {
        double dx = site.getX() + 0.5 - mc.player.getX(), dz = site.getZ() + 0.5 - mc.player.getZ();
        int dist = (int) Math.sqrt(dx * dx + dz * dz);
        float targetYaw = (float) (MathHelper.atan2(dz, dx) * MathHelper.DEGREES_PER_RADIAN) - 90f;
        float rel = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
        int idx = Math.floorMod(Math.round(rel / 45f), 8);
        Text t = Text.literal(ARROWS[idx] + " ").formatted(Formatting.YELLOW, Formatting.BOLD)
                .append(Text.translatable("hud.animecore.quest.site", dist).formatted(Formatting.YELLOW));
        ctx.drawTextWithShadow(tr, t, x, y, 0xFFFFFF);
    }
}
