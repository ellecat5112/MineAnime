package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.clan.ClanAbilityManager;
import com.animecore.component.EnergyPool;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.origin.OriginSelectionService;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import com.animecore.technique.TechniqueMastery;
import com.animecore.technique.TechniqueTokenItem;
import com.animecore.technique.TechniqueTokens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Admin / cheat seals: instant progression for testing and server staff. Usable only by operators (permission level 2)
 * or players in creative mode; never consumed. Right-click = small step, sneak + right-click = big step.
 */
public class AdminSealItem extends Item {
    public enum Action {
        RANK_UP, CLAN_LEVEL, MASTERY, SKILL_POINTS, NATURES, REPUTATION, REFILL, MAX_ALL;
        public String key() { return name().toLowerCase(java.util.Locale.ROOT); }
    }

    private final AnimeModContext context;
    private final Action action;

    public AdminSealItem(AnimeModContext context, Action action, Settings settings) {
        super(settings.maxCount(1).rarity(Rarity.EPIC));
        this.context = context;
        this.action = action;
    }

    public Action action() { return action; }

    @Override public boolean hasGlint(ItemStack stack) { return true; }

    public static boolean allowed(PlayerEntity player) {
        return player.hasPermissionLevel(2) || player.isCreative();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        if (!allowed(player)) {
            player.sendMessage(Text.translatable("message.animecore.admin.denied").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }
        boolean big = player.isSneaking();
        Text result = apply(player, context.power(player), big, player.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND));
        if (result == null) return TypedActionResult.fail(stack);
        player.sendMessage(result.copy().formatted(Formatting.LIGHT_PURPLE), true);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.8f, 1.2f);
        return TypedActionResult.success(stack, false);
    }

    /** @return feedback, or null when nothing applied */
    public Text apply(ServerPlayerEntity player, PlayerPowerComponent power, boolean big, ItemStack otherHand) {
        var rm = player.getRegistryManager();
        switch (action) {
            case RANK_UP -> {
                power.setTier(power.tier() + (big ? 10 : 1));
                return Text.translatable("message.animecore.admin.rank", power.tier());
            }
            case CLAN_LEVEL -> {
                Identifier clan = power.lineage().orElse(null);
                if (clan == null) return Text.translatable("message.animecore.admin.no_clan");
                int max = AnimeRegistries.lineage(rm, clan).map(LineageDefinition::maxSkillLevel).orElse(LineageDefinition.DEFAULT_MAX_SKILL_LEVEL);
                int level = big ? max : Math.min(max, power.clanSkillLevel(clan) + 1);
                power.setClanSkill(clan, level, 0);
                ClanAbilityManager.evaluate(player, power);
                ClanAbilityManager.refreshHeldSkillItems(player, power);
                return Text.translatable("message.animecore.admin.clan", level, max);
            }
            case MASTERY -> {
                if (otherHand.getItem() instanceof TechniqueTokenItem token) {
                    return master(player, power, token.token(), big);
                }
                int n = 0;
                for (Identifier t : TechniqueTokens.tokenIds(power.context().modId())) {
                    if (power.tokenLevel(t) > 0 || power.effectiveAffinities().contains(t)) { master(player, power, t, big); n++; }
                }
                return n == 0 ? Text.translatable("message.animecore.admin.no_token") : Text.translatable("message.animecore.admin.mastery_all", n);
            }
            case SKILL_POINTS -> {
                power.addSkillPoints(big ? 50 : 5);
                return Text.translatable("message.animecore.admin.points", power.skillPoints());
            }
            case NATURES -> {
                int n = 0;
                for (var e : AnimeRegistries.affinities(rm).getEntrySet()) {
                    if (!e.getValue().system().equals(power.context().modId()) || (!big && !e.getValue().basic())) continue;
                    if (power.addPersonalAffinity(e.getKey().getValue())) n++;
                }
                power.forceRefreshTraits(player);
                TechniqueMastery.evaluateAll(player, power);
                return Text.translatable("message.animecore.admin.natures", n);
            }
            case REPUTATION -> {
                int n = 0;
                for (var e : OriginSelectionService.factions(rm, power.context().modId())) {
                    power.setReputation(e.getKey(), big ? Math.max(100, power.reputation(e.getKey())) : power.reputation(e.getKey()) + 50);
                    n++;
                }
                return Text.translatable("message.animecore.admin.reputation", n);
            }
            case REFILL -> {
                refill(power);
                return Text.translatable("message.animecore.admin.refill");
            }
            case MAX_ALL -> {
                power.setTier(PlayerPowerComponent.MAX_TIER);
                power.lineage().ifPresent(clan -> {
                    int max = AnimeRegistries.lineage(rm, clan).map(LineageDefinition::maxSkillLevel).orElse(LineageDefinition.DEFAULT_MAX_SKILL_LEVEL);
                    power.setClanSkill(clan, max, 0);
                    ClanAbilityManager.evaluate(player, power);
                });
                for (Identifier t : TechniqueTokens.tokenIds(power.context().modId())) {
                    if (power.tokenLevel(t) > 0 || power.effectiveAffinities().contains(t)) master(player, power, t, true);
                }
                refill(power);
                return Text.translatable("message.animecore.admin.max");
            }
        }
        return null;
    }

    private static Text master(ServerPlayerEntity player, PlayerPowerComponent power, Identifier token, boolean big) {
        int size = TechniqueMastery.list(player.getRegistryManager(), token).size();
        int level = big ? size : Math.min(size, power.tokenLevel(token) + 1);
        power.setTokenMastery(token, level, 0);
        TechniqueMastery.evaluate(player, power, token);
        return Text.translatable("message.animecore.admin.mastery", TechniqueTokens.item(power.context().modId(), token)
                .map(i -> (Text) i.getName()).orElse(Text.literal(token.getPath())), level, size);
    }

    private static void refill(PlayerPowerComponent power) {
        for (EnergyPool pool : power.pools()) power.setEnergy(pool.id(), pool.max());
        power.clearCooldowns();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.animecore.admin." + action.key()).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.animecore.admin.only").formatted(Formatting.DARK_RED));
    }
}
