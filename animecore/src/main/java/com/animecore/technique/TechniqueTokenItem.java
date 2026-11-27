package com.animecore.technique;

import com.animecore.ability.AbilityExecutor;
import com.animecore.api.AnimeModContext;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.CastType;
import com.animecore.transformation.TransformationService;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Technique token (Fire Release, Ninjutsu, Dōjutsu...): hold and <b>right-click</b> to use the selected technique
 * (<b>hold</b> right-click to charge CHARGED techniques, release to fire), <b>sneak + scroll</b> or <b>sneak + left-click</b>
 * to switch technique. A transformation entry toggles that form. The selection is stored on the stack
 * ({@code animecore:token_selection}).
 */
public class TechniqueTokenItem extends Item {
    private final AnimeModContext context;
    private final Identifier token;
    private final int color;

    public TechniqueTokenItem(AnimeModContext context, Identifier token, int color, Settings settings) {
        super(settings.maxCount(1));
        this.context = context;
        this.token = token;
        this.color = color;
    }

    public AnimeModContext context() { return context; }
    public Identifier token() { return token; }
    public int color() { return color; }

    // ------------------------------------------------------------------ selection

    public List<TechniqueTokens.Entry> entries(PlayerEntity player) {
        return TechniqueTokens.entries(player.getWorld().getRegistryManager(), context.power(player), token);
    }

    public Optional<TechniqueTokens.Entry> selected(PlayerEntity player, ItemStack stack) {
        List<TechniqueTokens.Entry> list = entries(player);
        if (list.isEmpty()) return Optional.empty();
        Identifier sel = stack.get(AnimeDataComponents.TOKEN_SELECTION);
        return list.stream().filter(e -> e.id().equals(sel)).findFirst().or(() -> Optional.of(list.get(0)));
    }

    public void cycle(ServerPlayerEntity player, ItemStack stack, int delta) {
        List<TechniqueTokens.Entry> list = entries(player);
        if (list.isEmpty()) {
            player.sendMessage(Text.translatable("message.animecore.token.empty").formatted(Formatting.GRAY), true);
            return;
        }
        int idx = selected(player, stack).map(list::indexOf).orElse(0);
        TechniqueTokens.Entry next = list.get(Math.floorMod(idx + Integer.signum(delta == 0 ? 1 : delta), list.size()));
        stack.set(AnimeDataComponents.TOKEN_SELECTION, next.id());
        player.sendMessage(Text.translatable("message.animecore.token.selected", displayName(player.getRegistryManager(), next))
                .formatted(Formatting.GOLD), true);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 0.5f, 1.6f);
    }

    public static Text displayName(DynamicRegistryManager rm, TechniqueTokens.Entry e) {
        if (e.form()) return AnimeRegistries.transformation(rm, e.id()).map(f -> (Text) f.value().name()).orElse(Text.literal(e.id().getPath()));
        return AnimeRegistries.ability(rm, e.id()).map(a -> (Text) a.value().name()).orElse(Text.literal(e.id().getPath()));
    }

    private @Nullable AbilityDefinition ability(PlayerEntity player, TechniqueTokens.Entry e) {
        return e.form() ? null : AnimeRegistries.ability(player.getWorld().getRegistryManager(), e.id()).map(r -> r.value()).orElse(null);
    }

    // ------------------------------------------------------------------ use

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        Optional<TechniqueTokens.Entry> sel = selected(user, stack);
        if (sel.isEmpty()) {
            if (user instanceof ServerPlayerEntity sp) sp.sendMessage(Text.translatable("message.animecore.token.empty").formatted(Formatting.GRAY), true);
            return TypedActionResult.fail(stack);
        }
        TechniqueTokens.Entry e = sel.get();
        if (e.form()) {
            if (user instanceof ServerPlayerEntity sp) TransformationService.toggle(sp, context.power(sp), e.id());
            return TypedActionResult.success(stack, world.isClient());
        }
        AbilityDefinition def = ability(user, e);
        if (def == null) return TypedActionResult.fail(stack);
        if (user instanceof ServerPlayerEntity sp) AbilityExecutor.handleInput(sp, e.id(), true);
        if (def.castType() == CastType.CHARGED) {
            user.setCurrentHand(hand);               // keep "using" while right-click is held; release fires
            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof ServerPlayerEntity sp) {
            PlayerPowerComponent power = context.power(sp);
            power.chargingAbility().ifPresent(id -> AbilityExecutor.handleInput(sp, id, false));
        }
    }

    @Override public int getMaxUseTime(ItemStack stack, LivingEntity user) { return 72000; }
    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.NONE; }

    // ------------------------------------------------------------------ display

    @Override
    public boolean hasGlint(ItemStack stack) { return false; }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.animecore.token.controls").formatted(Formatting.DARK_GRAY));
    }

    /** Lines listing the player's techniques on this token (client tooltip / HUD use it with the local player). */
    public List<Text> describe(PlayerEntity player, ItemStack stack) {
        DynamicRegistryManager rm = player.getWorld().getRegistryManager();
        List<TechniqueTokens.Entry> list = entries(player);
        Optional<TechniqueTokens.Entry> sel = selected(player, stack);
        List<Text> out = new java.util.ArrayList<>();
        PlayerPowerComponent power = context.power(player);
        List<Identifier> mastery = TechniqueMastery.list(rm, token);
        if (!mastery.isEmpty()) {
            int lvl = power.tokenLevel(token);
            out.add(com.animecore.api.AnimeText.tr(context.modId(), "tooltip.animecore.token.mastery", lvl, power.tokenXp(token),
                    com.animecore.clan.ClanSkill.experienceForNext(Math.max(1, lvl)), Math.min(lvl, mastery.size()), mastery.size()).formatted(Formatting.DARK_AQUA));
            if (AnimeRegistries.affinities(rm).getOrEmpty(token).isPresent() && !power.effectiveAffinities().contains(token)) {
                out.add(com.animecore.api.AnimeText.tr(context.modId(), "tooltip.animecore.token.off_affinity").formatted(Formatting.RED));
            }
        }
        if (list.isEmpty()) out.add(Text.translatable("message.animecore.token.empty").formatted(Formatting.GRAY));
        for (TechniqueTokens.Entry e : list) {
            boolean s = sel.map(e::equals).orElse(false);
            MutableText line = Text.literal(s ? "▶ " : "  ").append(displayName(rm, e));
            AbilityDefinition def = ability(player, e);
            if (def != null) {
                line.append(Text.literal("  " + Math.round(def.cost().amount()) + " ").formatted(Formatting.DARK_AQUA))
                        .append(Text.translatable("pool." + def.cost().pool().getNamespace() + "." + def.cost().pool().getPath()).formatted(Formatting.DARK_AQUA));
                if (def.castType() == CastType.CHARGED) line.append(Text.translatable("tooltip.animecore.token.charge").formatted(Formatting.GRAY));
            } else {
                line.append(Text.translatable("tooltip.animecore.token.form").formatted(Formatting.LIGHT_PURPLE));
            }
            out.add(line.formatted(s ? Formatting.YELLOW : Formatting.GRAY));
        }
        // the selected technique explains itself: wiki description + rank / cooldown
        sel.map(e -> ability(player, e)).ifPresent(def -> {
            if (!(def.name().getContent() instanceof net.minecraft.text.TranslatableTextContent t)) return;
            String key = t.getKey() + ".desc";
            if (!net.minecraft.util.Language.getInstance().hasTranslation(key)) return;
            out.add(Text.empty());
            for (String l : wrap(Text.translatable(key).getString(), 44)) out.add(Text.literal(l).formatted(Formatting.GRAY, Formatting.ITALIC));
            out.add(Text.translatable("tooltip.animecore.token.stats", def.requiredTier(), String.format(java.util.Locale.ROOT, "%.1f", def.cooldownTicks() / 20f))
                    .formatted(Formatting.DARK_GRAY));
        });
        return out;
    }

    static List<String> wrap(String text, int width) {
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String w : text.split(" ")) {
            if (cur.length() + w.length() + 1 > width && cur.length() > 0) { lines.add(cur.toString()); cur.setLength(0); }
            if (cur.length() > 0) cur.append(' ');
            cur.append(w);
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines.size() > 6 ? lines.subList(0, 6) : lines;
    }
}
