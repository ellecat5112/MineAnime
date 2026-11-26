package com.animecore.client.quest;

import com.animecore.quest.QuestPayloads;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/** Story dialogue box: the speaker's lines one at a time (click, space or enter to continue; escape skips). */
public class DialogueScreen extends Screen {
    private QuestPayloads.Dialogue dialogue;
    private int line;
    private float shown;   // typewriter effect

    public DialogueScreen(QuestPayloads.Dialogue dialogue) {
        super(dialogue.speaker());
        this.dialogue = dialogue;
    }

    @Override public boolean shouldPause() { return false; }

    private void next() {
        String full = dialogue.lines().get(line).getString();
        if (shown < full.length()) {   // first click finishes the line
            shown = full.length();
            return;
        }
        line++;
        shown = 0;
        if (line >= dialogue.lines().size()) {
            QuestPayloads.Dialogue queued = QuestClient.pollPending();
            if (queued != null) {
                dialogue = queued;
                line = 0;
            } else {
                close();
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        next();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 32 || keyCode == 257 || keyCode == 335) {   // space, enter, keypad enter
            next();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) { }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (line >= dialogue.lines().size()) return;
        shown += delta * 1.5f;
        int w = Math.min(width - 20, 360);
        int x = (width - w) / 2;
        int h = 64;
        int y = height - h - 40;
        ctx.fill(x, y, x + w, y + h, 0xD0101018);
        ctx.fill(x, y, x + w, y + 1, 0xFFE0B040);
        ctx.fill(x, y + h - 1, x + w, y + h, 0xFFE0B040);
        ctx.drawTextWithShadow(textRenderer, dialogue.speaker().copy().formatted(Formatting.GOLD, Formatting.BOLD), x + 8, y + 6, 0xFFFFFF);
        String full = dialogue.lines().get(line).getString();
        String visible = full.substring(0, Math.min(full.length(), (int) shown));
        List<OrderedText> lines = textRenderer.wrapLines(Text.literal(visible), w - 16);
        int ly = y + 20;
        for (OrderedText l : lines.subList(0, Math.min(4, lines.size()))) {
            ctx.drawTextWithShadow(textRenderer, l, x + 8, ly, 0xF0F0F0);
            ly += 10;
        }
        String hint = (line + 1) + "/" + dialogue.lines().size() + "  ▶";
        ctx.drawTextWithShadow(textRenderer, Text.literal(hint).formatted(Formatting.GRAY), x + w - 8 - textRenderer.getWidth(hint), y + h - 11, 0xFFFFFF);
    }
}
