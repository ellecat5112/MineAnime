package com.animecore.client.quest;

import com.animecore.api.AnimeModRegistry;
import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.quest.QuestDefinition;
import com.animecore.quest.QuestLog;
import com.animecore.quest.QuestManager;
import com.animecore.quest.QuestPayloads;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Story journal opened by the quest scroll: series tabs on top, the arcs of the series (click to expand) with their
 * quests on the left, the selected quest's story, objectives and rewards on the right.
 */
public class QuestJournalScreen extends Screen {
    private final String system;
    private final List<Map.Entry<Identifier, QuestDefinition>> all;
    private final List<String> series = new ArrayList<>();
    private String tab;
    private final Set<String> expanded = new LinkedHashSet<>();
    private @Nullable Identifier selected;
    private int scroll;
    private final List<Row> rows = new ArrayList<>();
    private int left, top, w, h, listW;
    private ButtonWidget accept, abandon, summon;
    private static @Nullable String lastTab;

    private record Row(@Nullable String arc, @Nullable Identifier quest, @Nullable QuestDefinition def) {}

    public QuestJournalScreen(String system) {
        super(Text.translatable("screen.animecore.journal"));
        this.system = system;
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        this.all = QuestManager.quests(mc.player.networkHandler.getRegistryManager(), system);
        for (var e : all) if (!series.contains(e.getValue().series())) series.add(e.getValue().series());
        QuestLog log = log();
        // open on the active quest, else the first one not yet done
        Map.Entry<Identifier, QuestDefinition> focus = all.stream().filter(e -> log.active().map(e.getKey()::equals).orElse(false)).findFirst()
                .or(() -> all.stream().filter(e -> !log.isCompleted(e.getKey())).findFirst())
                .orElse(all.isEmpty() ? null : all.get(0));
        tab = focus != null ? focus.getValue().series() : (series.isEmpty() ? "" : series.get(0));
        if (lastTab != null && series.contains(lastTab) && focus == null) tab = lastTab;
        if (focus != null) {
            selected = focus.getKey();
            expanded.add(arcKey(focus.getValue()));
        }
    }

    private QuestLog log() {
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        return AnimeModRegistry.get(system).orElseThrow().power(mc.player).quests();
    }

    private static String arcKey(QuestDefinition q) { return q.series() + "/" + q.arc().getString(); }

    @Override public boolean shouldPause() { return false; }

    @Override
    protected void init() {
        w = Math.min(width - 16, 440);
        h = Math.min(height - 16, 270);
        left = (width - w) / 2;
        top = (height - h) / 2;
        listW = (int) (w * 0.42);
        int tx = left + 6;
        for (String s : series) {
            Text label = seriesName(s);
            int share = (w - 10) / Math.max(1, series.size()) - 2;   // tabs share the width ("Stardust Crusaders" fits)
            int bw = Math.min(Math.max(90, share), textRenderer.getWidth(label) + 10);
            if (tx + bw > left + w - 4) break;
            addDrawableChild(ButtonWidget.builder(label, b -> { tab = s; lastTab = s; scroll = 0; rebuild(); })
                    .dimensions(tx, top + 16, bw, 14).build());
            tx += bw + 2;
        }
        int bx = left + listW + 10, by = top + h - 22;
        accept = addDrawableChild(ButtonWidget.builder(Text.translatable("screen.animecore.journal.accept"), b -> send("accept")).dimensions(bx, by, 70, 18).build());
        abandon = addDrawableChild(ButtonWidget.builder(Text.translatable("screen.animecore.journal.abandon"), b -> send("abandon")).dimensions(bx + 74, by, 70, 18).build());
        summon = addDrawableChild(ButtonWidget.builder(Text.translatable("screen.animecore.journal.summon"), b -> send("summon")).dimensions(bx + 148, by, 70, 18).build());
        rebuild();
    }

    private void send(String action) {
        if (selected == null) return;
        AnimeClientNetworking.send(new QuestPayloads.Action(action, selected));
        if (action.equals("accept")) close();
    }

    private Text seriesName(String s) {
        String key = "quest.series." + system + "." + s;
        return Text.translatableWithFallback(key, Character.toUpperCase(s.charAt(0)) + s.substring(1).replace('_', ' '));
    }

    private void rebuild() {
        rows.clear();
        Map<String, List<Map.Entry<Identifier, QuestDefinition>>> arcs = new LinkedHashMap<>();
        for (var e : all) if (e.getValue().series().equals(tab)) arcs.computeIfAbsent(arcKey(e.getValue()), k -> new ArrayList<>()).add(e);
        arcs.forEach((arc, list) -> {
            rows.add(new Row(arc, null, list.get(0).getValue()));
            if (expanded.contains(arc)) for (var e : list) rows.add(new Row(null, e.getKey(), e.getValue()));
        });
        updateButtons();
    }

    private void updateButtons() {
        QuestLog log = log();
        QuestDefinition q = selectedDef();
        boolean isActive = selected != null && log.active().map(selected::equals).orElse(false);
        accept.visible = q != null && !isActive && !log.isCompleted(selected) && QuestManager.available(log, q);
        accept.active = log.active().isEmpty();
        abandon.visible = isActive;
        summon.visible = isActive;
    }

    private @Nullable QuestDefinition selectedDef() {
        if (selected == null) return null;
        for (var e : all) if (e.getKey().equals(selected)) return e.getValue();
        return null;
    }

    private int rowsVisible() { return (h - 40) / 11; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        scroll = Math.max(0, Math.min(Math.max(0, rows.size() - rowsVisible()), scroll - (int) Math.signum(vertical) * 3));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        int lx = left + 4, ly = top + 34;
        if (mouseX >= lx && mouseX < lx + listW && mouseY >= ly) {
            int i = (int) ((mouseY - ly) / 11) + scroll;
            if (i >= 0 && i < rows.size() && i - scroll < rowsVisible()) {
                Row r = rows.get(i);
                if (r.arc() != null) {
                    if (!expanded.remove(r.arc())) expanded.add(r.arc());
                } else {
                    selected = r.quest();
                }
                rebuild();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        QuestLog log = log();
        ctx.fill(left, top, left + w, top + h, 0xE0141018);
        ctx.fill(left, top, left + w, top + 1, 0xFFE0B040);
        ctx.fill(left, top + h - 1, left + w, top + h, 0xFFE0B040);
        ctx.drawTextWithShadow(textRenderer, title.copy().formatted(Formatting.GOLD, Formatting.BOLD), left + 6, top + 4, 0xFFFFFF);
        String done = log.completed().size() + " / " + all.size();
        ctx.drawTextWithShadow(textRenderer, Text.literal(done).formatted(Formatting.GRAY), left + w - 6 - textRenderer.getWidth(done), top + 4, 0xFFFFFF);
        // buttons drawn over the panel
        for (var child : children()) if (child instanceof ButtonWidget b) b.render(ctx, mouseX, mouseY, delta);

        // list
        int lx = left + 4, ly = top + 34;
        ctx.fill(lx - 1, ly - 2, lx + listW, top + h - 4, 0x60000000);
        int vis = rowsVisible();
        for (int i = scroll; i < Math.min(rows.size(), scroll + vis); i++) {
            Row r = rows.get(i);
            int y = ly + (i - scroll) * 11;
            if (r.arc() != null) {
                Text t = Text.literal(expanded.contains(r.arc()) ? "▾ " : "▸ ").append(r.def().arc()).formatted(Formatting.AQUA);
                ctx.drawTextWithShadow(textRenderer, trim(t, listW - 6), lx + 2, y, 0xFFFFFF);
            } else {
                boolean sel = r.quest().equals(selected);
                if (sel) ctx.fill(lx, y - 1, lx + listW - 1, y + 10, 0x50E0B040);
                ctx.drawTextWithShadow(textRenderer, trim(Text.literal(status(log, r.quest(), r.def()) + " ").append(r.def().name()), listW - 14),
                        lx + 10, y, color(log, r.quest(), r.def()));
            }
        }
        if (rows.size() > vis) {
            int barH = Math.max(10, (h - 40) * vis / rows.size());
            int barY = ly + (h - 40 - barH) * scroll / Math.max(1, rows.size() - vis);
            ctx.fill(lx + listW - 3, barY, lx + listW - 1, barY + barH, 0xFFAAAAAA);
        }

        // details
        QuestDefinition q = selectedDef();
        int dx = left + listW + 10, dy = top + 34, dw = w - listW - 16;
        if (q == null) return;
        ctx.drawTextWithShadow(textRenderer, q.name().copy().formatted(Formatting.GOLD, Formatting.BOLD), dx, dy, 0xFFFFFF);
        dy += 11;
        ctx.drawTextWithShadow(textRenderer, trim(q.arc().copy().formatted(Formatting.AQUA)
                .append(Text.translatable("screen.animecore.journal.rank", q.recommendedTier()).formatted(Formatting.GRAY)), dw), dx, dy, 0xFFFFFF);
        dy += 13;
        int maxY = top + h - 26;
        for (OrderedText line : textRenderer.wrapLines(q.description(), dw)) {
            if (dy > maxY - 60) break;
            ctx.drawTextWithShadow(textRenderer, line, dx, dy, 0xDDDDDD);
            dy += 10;
        }
        dy += 4;
        boolean isActive = log.active().map(selected::equals).orElse(false);
        boolean isDone = log.isCompleted(selected);
        for (int i = 0; i < q.objectives().size() && dy < maxY - 12; i++) {
            QuestDefinition.Objective o = q.objectives().get(i);
            String mark = isDone || (isActive && i < log.step()) ? "✔ " : isActive && i == log.step() ? "▶ " : "○ ";
            Formatting f = isDone || (isActive && i < log.step()) ? Formatting.GREEN : isActive && i == log.step() ? Formatting.YELLOW : Formatting.GRAY;
            Text t = Text.literal(mark).append(QuestManager.objectiveText(o));
            if (isActive && i == log.step() && o.count() > 1 && o.type() != QuestDefinition.ObjectiveType.TRAVEL) t = t.copy().append(" " + log.progress() + "/" + o.count());
            ctx.drawTextWithShadow(textRenderer, trim(t.copy().formatted(f), dw), dx, dy, 0xFFFFFF);
            dy += 10;
        }
        QuestDefinition.Rewards r = q.rewards();
        List<Text> parts = new ArrayList<>();
        if (r.tier() > 0) parts.add(Text.translatable("screen.animecore.journal.reward_rank", r.tier()));
        if (r.skillPoints() > 0) parts.add(Text.translatable("screen.animecore.journal.reward_sp", r.skillPoints()));
        for (ItemStack s : r.items()) parts.add(Text.literal(s.getCount() + "x ").append(s.getName()));
        if (!r.abilities().isEmpty()) parts.add(com.animecore.api.AnimeText.tr(system, "screen.animecore.journal.reward_jutsu", r.abilities().size()));
        if (!parts.isEmpty() && dy < maxY) {
            Text rt = Text.translatable("screen.animecore.journal.rewards").formatted(Formatting.LIGHT_PURPLE);
            for (int i = 0; i < parts.size(); i++) rt = rt.copy().append(Text.literal(i == 0 ? " " : ", ")).append(parts.get(i));
            ctx.drawTextWithShadow(textRenderer, trim(rt, dw), dx, Math.max(dy + 2, maxY - 10), 0xFFFFFF);
        }
        if (!isDone && !isActive && !QuestManager.available(log, q)) {
            ctx.drawTextWithShadow(textRenderer, Text.translatable("screen.animecore.journal.locked").formatted(Formatting.RED), dx, top + h - 18, 0xFFFFFF);
        }
        if (isDone) ctx.drawTextWithShadow(textRenderer, Text.translatable("screen.animecore.journal.done").formatted(Formatting.GREEN), dx, top + h - 18, 0xFFFFFF);
    }

    private String status(QuestLog log, Identifier id, QuestDefinition q) {
        if (log.isCompleted(id)) return "✔";
        if (log.active().map(id::equals).orElse(false)) return "▶";
        return QuestManager.available(log, q) ? "●" : "✖";
    }

    private int color(QuestLog log, Identifier id, QuestDefinition q) {
        if (log.isCompleted(id)) return 0x66BB6A;
        if (log.active().map(id::equals).orElse(false)) return 0xFFEB3B;
        return QuestManager.available(log, q) ? 0xFFFFFF : 0x777777;
    }

    private OrderedText trim(Text t, int width) {
        List<OrderedText> l = textRenderer.wrapLines(t, width);
        return l.isEmpty() ? OrderedText.EMPTY : l.get(0);
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x80000000);
    }
}
