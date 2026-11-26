package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/** Technique scroll (Naruto jutsu scroll, Bleach Kido tome, DB technique manual). Use to learn the stored ability. */
public class AbilityScrollItem extends Item implements AnimeComponentItem {
    public AbilityScrollItem(Settings settings) {
        super(settings.maxCount(16));
    }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.ABILITY_SCROLL);
    }

    public static Optional<Identifier> ability(ItemStack stack) {
        AnimeDataComponents.AbilityScroll data = stack.get(AnimeDataComponents.ABILITY_SCROLL);
        return data == null ? Optional.empty() : Optional.of(data.ability());
    }

    @Override
    public Text getName(ItemStack stack) {
        return ability(stack)
                .<Text>map(id -> Text.translatable("item.animecore.ability_scroll.named",
                        super.getName(stack), Text.translatable(Ids.translationKey("ability", id))))
                .orElseGet(() -> super.getName(stack));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return ability(stack).isPresent();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        Optional<Identifier> id = ability(stack);
        if (id.isEmpty()) return TypedActionResult.pass(stack);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);

        Optional<RegistryEntry.Reference<AbilityDefinition>> entry = AnimeRegistries.ability(player.getRegistryManager(), id.get());
        Optional<AnimeModContext> ctx = entry.flatMap(e -> AnimeModRegistry.get(e.value().system()));
        if (entry.isEmpty() || ctx.isEmpty()) return fail(player, stack, "message.animecore.scroll.unreadable");
        PlayerPowerComponent power = ctx.get().power(player);
        if (power.tier() < entry.get().value().requiredTier()) return fail(player, stack, "message.animecore.cast.tier_too_low");
        if (power.unlockedAbilities().contains(id.get())) return fail(player, stack, "message.animecore.scroll.known");
        if (!com.animecore.api.event.AnimeCoreEvents.ALLOW_ABILITY_UNLOCK.invoker().allow(player, power, id.get(), "scroll")) {
            return TypedActionResult.fail(stack);
        }
        power.unlockAbility(id.get());

        stack.decrementUnlessCreative(1, player);
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1f, 1f);
        player.sendMessage(Text.translatable("message.animecore.scroll.learned", entry.get().value().name()).formatted(Formatting.GOLD), true);
        return TypedActionResult.consume(stack);
    }

    private static TypedActionResult<ItemStack> fail(ServerPlayerEntity player, ItemStack stack, String key) {
        player.sendMessage(Text.translatable(key).formatted(Formatting.RED), true);
        return TypedActionResult.fail(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Optional<Identifier> id = ability(stack);
        if (id.isEmpty() || context.getRegistryLookup() == null) return;
        context.getRegistryLookup().getOptionalWrapper(AnimeRegistries.ABILITY)
                .flatMap(w -> w.getOptional(RegistryKey.of(AnimeRegistries.ABILITY, id.get())))
                .ifPresent(entry -> AbilityTooltips.append(entry.value(), tooltip));
    }

    /** Shared ability description lines (also used by JEI). */
    public static final class AbilityTooltips {
        private AbilityTooltips() {}

        public static void append(AbilityDefinition def, List<Text> tooltip) {
            tooltip.add(Text.translatable("tooltip.animecore.cost", fmt(def.cost().amount()),
                    Text.translatable("pool." + def.cost().pool().getNamespace() + "." + def.cost().pool().getPath())).formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("tooltip.animecore.tier", def.requiredTier()).formatted(Formatting.GRAY));
            def.affinity().ifPresent(a -> tooltip.add(Text.translatable("tooltip.animecore.affinity",
                    Text.translatable(Ids.translationKey("affinity", a))).formatted(Formatting.GOLD)));
            if (!def.combo().isEmpty()) {
                MutableText combo = Text.empty();
                for (int i = 0; i < def.combo().size(); i++) {
                    Identifier token = def.combo().get(i);
                    if (i > 0) combo.append(" › ");
                    combo.append(Text.translatable("combo." + token.getNamespace() + "." + token.getPath()));
                }
                tooltip.add(Text.translatable("tooltip.animecore.combo", combo).formatted(Formatting.LIGHT_PURPLE));
            }
            tooltip.add(Text.translatable("tooltip.animecore.cast_type." + def.castType().asString()).formatted(Formatting.DARK_GRAY));
        }

        private static String fmt(float v) {
            return v == Math.floor(v) ? String.valueOf((int) v) : String.format("%.1f", v);
        }
    }
}
