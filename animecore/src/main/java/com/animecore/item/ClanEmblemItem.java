package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.clan.ClanRollService;
import com.animecore.clan.ClanSkill;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * The clan "skill" item. Bound to one lineage ({@code animecore:clan_binding}); while its owner holds it in either
 * hand, the experience of every enemy they defeat becomes clan skill experience (see {@code ClanTraitHooks}).
 * The item bar mirrors progress to the next clan level. Use it to print clan progress and the next unlock.
 */
public class ClanEmblemItem extends Item implements AnimeComponentItem {
    private final AnimeModContext context;

    public ClanEmblemItem(AnimeModContext context, Settings settings) {
        super(settings.maxCount(1));
        this.context = context;
    }

    public AnimeModContext context() { return context; }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.CLAN_BINDING);
    }

    @Override
    public Text getName(ItemStack stack) {
        return ClanRollService.binding(stack)
                .<Text>map(id -> Text.translatable("item.animecore.clan_item.named", super.getName(stack),
                        Text.translatable(Ids.translationKey("lineage", id))))
                .orElseGet(() -> super.getName(stack));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.contains(AnimeDataComponents.CLAN_SKILL);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        AnimeDataComponents.ClanSkill s = stack.get(AnimeDataComponents.CLAN_SKILL);
        return s == null ? 0 : Math.round(13f * MathHelper.clamp(s.progress(), 0f, 1f));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xFFC940;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        AnimeDataComponents.ClanSkill s = stack.get(AnimeDataComponents.CLAN_SKILL);
        if (s != null) {
            tooltip.add(com.animecore.api.AnimeText.tr(this.context.modId(), "tooltip.animecore.clan_skill.level", s.level()).formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.animecore.clan_skill.progress", Math.round(s.progress() * 100)).formatted(Formatting.GRAY));
        }
        if (ClanRollService.binding(stack).isPresent()) {
            tooltip.add(com.animecore.api.AnimeText.tr(this.context.modId(), "tooltip.animecore.clan_skill.hint").formatted(Formatting.DARK_AQUA));
        } else {
            tooltip.add(com.animecore.api.AnimeText.tr(this.context.modId(), "tooltip.animecore.clan_skill.unbound").formatted(Formatting.DARK_GRAY));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        PlayerPowerComponent power = context.power(player);
        Optional<Identifier> bound = ClanRollService.binding(stack);
        if (bound.isEmpty()) {
            // An unbound emblem attunes to the user's current clan.
            power.lineage().ifPresent(id -> stack.set(AnimeDataComponents.CLAN_BINDING, new AnimeDataComponents.ClanBinding(id)));
            bound = power.lineage();
        }
        if (bound.isEmpty()) return TypedActionResult.fail(stack);
        if (!bound.equals(power.lineage())) {
            player.sendMessage(com.animecore.api.AnimeText.tr(context.modId(), "message.animecore.clan.foreign_emblem").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }
        Identifier lineage = bound.get();
        Optional<LineageDefinition> def = AnimeRegistries.lineage(player.getRegistryManager(), lineage);
        if (def.isEmpty()) return TypedActionResult.fail(stack);
        int level = power.clanSkillLevel(lineage);
        long xp = power.clanSkillXp(lineage);
        player.sendMessage(Text.translatable("message.animecore.clan.status", def.get().name(), level, def.get().maxSkillLevel(),
                xp, ClanSkill.experienceForNext(level)).formatted(Formatting.GOLD), false);
        def.get().progression().stream()
                .filter(e -> e.skillLevel() > level || power.tier() < e.requiredTier())
                .findFirst()
                .ifPresent(e -> {
                    Text what = e.ability().flatMap(id -> AnimeRegistries.ability(player.getRegistryManager(), id)).<Text>map(a -> a.value().name())
                            .or(() -> e.transformation().flatMap(id -> AnimeRegistries.transformation(player.getRegistryManager(), id)).map(f -> f.value().name()))
                            .orElse(Text.literal("?"));
                    player.sendMessage(Text.translatable("message.animecore.clan.next", what, e.skillLevel()).formatted(Formatting.AQUA), false);
                });
        com.animecore.clan.ClanAbilityManager.refreshHeldSkillItems(player, power);
        return TypedActionResult.success(stack, false);
    }
}
