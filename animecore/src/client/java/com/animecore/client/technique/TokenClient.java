package com.animecore.client.technique;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.movement.MotionUtil;
import com.animecore.movement.MovementMode;
import com.animecore.network.MovementPayloads;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.CycleTechniquePayload;
import com.animecore.technique.TechniqueTokenItem;
import com.animecore.technique.TechniqueTokens;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.Optional;

/**
 * Keybind-free controls (AHZNB-style): technique tokens (right-click / hold to charge, sneak + scroll or sneak + left-click
 * to switch), a HUD line naming the selected technique, token tooltips, and the sneak + jump burst step.
 */
public final class TokenClient {
    private TokenClient() {}

    private static boolean jumpWasDown;
    private static long lastCycle;

    public static void init() {
        ClientPreAttackCallback.EVENT.register((client, player, clicks) -> {
            if (!player.isSneaking()) return false;
            Optional<Hand> hand = tokenHand(player);
            if (hand.isEmpty()) return false;
            cycle(hand.get(), 1);
            return true;   // swallow the swing
        });
        ItemTooltipCallback.EVENT.register((stack, ctx, type, lines) -> {
            ClientPlayerEntity p = MinecraftClient.getInstance().player;
            if (p != null && stack.getItem() instanceof TechniqueTokenItem token) lines.addAll(1, token.describe(p, stack));
        });
        HudRenderCallback.EVENT.register(TokenClient::hud);
        ClientTickEvents.END_CLIENT_TICK.register(TokenClient::tick);
    }

    static Optional<Hand> tokenHand(ClientPlayerEntity p) {
        if (p.getMainHandStack().getItem() instanceof TechniqueTokenItem) return Optional.of(Hand.MAIN_HAND);
        if (p.getOffHandStack().getItem() instanceof TechniqueTokenItem) return Optional.of(Hand.OFF_HAND);
        return Optional.empty();
    }

    /** @return true if the scroll was consumed (sneaking with a token in hand, no screen open) */
    public static boolean onScroll(double vertical) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null || c.currentScreen != null || !c.player.isSneaking() || vertical == 0) return false;
        Optional<Hand> hand = tokenHand(c.player);
        if (hand.isEmpty()) return false;
        cycle(hand.get(), vertical > 0 ? -1 : 1);
        return true;
    }

    private static void cycle(Hand hand, int delta) {
        long now = System.currentTimeMillis();
        if (now - lastCycle < 80) return;   // smooth-scrolling touchpads fire many events
        lastCycle = now;
        AnimeClientNetworking.send(new CycleTechniquePayload(hand, delta));
    }

    /** Sneak + jump on the ground: burst step (Shunshin) for systems with passive movement. */
    private static void tick(MinecraftClient c) {
        if (c.player == null || c.currentScreen != null) {
            jumpWasDown = false;
            return;
        }
        boolean down = c.options.jumpKey.isPressed();
        if (down && !jumpWasDown && c.player.isSneaking() && c.player.isOnGround()) {
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                if (!ctx.passiveMovement() || ctx.movement(MovementMode.BURST_STEP).isEmpty()) continue;
                float fwd = c.player.input.movementForward, strafe = c.player.input.movementSideways;
                if (fwd == 0 && strafe == 0) fwd = 1;
                AnimeClientNetworking.send(new MovementPayloads.BurstStep(ctx.modId(), fwd, strafe));
                break;
            }
        }
        jumpWasDown = down;
    }

    /** Selected technique above the hotbar while a token is held. */
    private static void hud(DrawContext ctx, RenderTickCounter counter) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null || c.options.hudHidden) return;
        Optional<Hand> hand = tokenHand(c.player);
        if (hand.isEmpty()) return;
        ItemStack stack = c.player.getStackInHand(hand.get());
        TechniqueTokenItem token = (TechniqueTokenItem) stack.getItem();
        var sel = token.selected(c.player, stack);
        int w = ctx.getScaledWindowWidth(), y = ctx.getScaledWindowHeight() - 96;   // above the action bar and held-item name
        Text line;
        if (sel.isEmpty()) {
            line = Text.translatable("message.animecore.token.empty").formatted(Formatting.GRAY);
        } else {
            var e = sel.get();
            int count = token.entries(c.player).size();
            int idx = token.entries(c.player).indexOf(e) + 1;
            var rm = c.world.getRegistryManager();
            var power = token.context().power(c.player);
            String cd = "";
            if (!e.form()) {
                int ticks = power.cooldownRemaining(e.id());
                if (ticks > 0) cd = String.format("  (%.1fs)", ticks / 20f);
            }
            line = Text.literal("◆ ").append(TechniqueTokenItem.displayName(rm, e)).formatted(Formatting.GOLD)
                    .append(Text.literal(cd).formatted(Formatting.RED))
                    .append(Text.literal("  " + idx + "/" + count).formatted(Formatting.DARK_GRAY));
        }
        ctx.drawCenteredTextWithShadow(c.textRenderer, line, w / 2, y, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(c.textRenderer, Text.translatable("hud.animecore.token.hint").formatted(Formatting.DARK_GRAY), w / 2, y + 10, 0x888888);
    }
}
