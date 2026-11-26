package com.animecore.clan;

import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import com.animecore.registry.definition.TransformationDefinition;
import com.animecore.transformation.TransformationService;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Server-authoritative clan lifecycle:
 * <ol>
 *   <li>{@link #assign}: removes everything the previous clan granted (abilities, forms, the active clan form,
 *       behavioural traits; stat modifiers are re-derived by the component and removed by id, so they never stack),
 *       keeps unrelated learned abilities, sets the lineage, grants its defaults, hands out the starter kit once and
 *       fires {@link AnimeCoreEvents#CLAN_ASSIGNED}.</li>
 *   <li>{@link #evaluate}: unlocks every progression entry whose skill level and tier requirement is met. Called on
 *       assignment, clan skill level-up and tier change.</li>
 *   <li>{@link #addSkillExperience}: clan skill levelling (kill experience while holding the clan's skill item).</li>
 * </ol>
 * Everything ends in the player's {@link PlayerPowerComponent}, which CCA syncs to the owner (full) and to peers
 * (lineage only).
 */
public final class ClanAbilityManager {
    private ClanAbilityManager() {}

    public enum Reason { FIRST_JOIN, ROLL, REROLL_ITEM, SEALED_SCROLL, CHOICE, COMMAND }

    /** @return false if {@code lineage} is unknown or belongs to another system */
    public static boolean assign(ServerPlayerEntity player, PlayerPowerComponent power, @Nullable Identifier lineage, Reason reason) {
        String system = power.context().modId();
        Optional<LineageDefinition> next = lineage == null ? Optional.empty() : AnimeRegistries.lineage(player.getRegistryManager(), lineage);
        if (lineage != null && (next.isEmpty() || !next.get().system().equals(system))) return false;

        Identifier previous = power.lineage().orElse(null);
        if (Objects.equals(previous, lineage)) {
            // Same clan: just make sure defaults & progression are present (e.g. datapack added entries).
            next.ifPresent(def -> grantDefaults(player, power, lineage, def));
            evaluate(player, power);
            power.setClanInitialized(true);
            return true;
        }

        // ---- tear down the old clan ----
        if (previous != null) {
            AnimeRegistries.lineage(player.getRegistryManager(), previous)
                    .ifPresent(def -> def.clanTraits().forEach(t -> t.onRemoved(player, power)));
        }
        power.activeTransformation().ifPresent(active -> {
            boolean clanForm = power.clanGrantedForms().contains(active)
                    || AnimeRegistries.transformation(player.getRegistryManager(), active)
                    .map(RegistryEntry::value).flatMap(TransformationDefinition::requiredLineage)
                    .map(req -> !req.equals(lineage)).orElse(false);
            if (clanForm) TransformationService.revert(player, power, false);
        });
        power.revokeClanGrants();

        // ---- set up the new one ----
        power.setLineageRaw(lineage);   // marks traits dirty: old attribute modifiers are removed by id next tick
        power.setClanInitialized(true);
        if (next.isPresent()) {
            LineageDefinition def = next.get();
            grantDefaults(player, power, lineage, def);
            def.clanTraits().forEach(t -> t.onAssigned(player, power));
            if (power.markClanKitGiven(lineage)) giveStarterKit(player, power, lineage, def);
            evaluate(player, power);
            player.sendMessage(Text.translatable("message.animecore.clan.assigned", def.name())
                    .styled(s -> s.withColor(def.color())), false);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.5f, 1.2f);
            AnimeCoreEvents.CLAN_ASSIGNED.invoker().onAssigned(player, power, previous, lineage, reason);
        }
        return true;
    }

    private static void grantDefaults(ServerPlayerEntity player, PlayerPowerComponent power, Identifier lineage, LineageDefinition def) {
        for (Identifier id : def.grantedAbilities()) {
            if (power.grantClanAbility(id)) AnimeCoreEvents.CLAN_UNLOCK.invoker().onUnlock(player, power, lineage, id, false);
        }
        for (Identifier id : def.grantedTransformations()) {
            if (power.grantClanForm(id)) AnimeCoreEvents.CLAN_UNLOCK.invoker().onUnlock(player, power, lineage, id, true);
        }
    }

    /** Unlocks progression entries whose requirements are met. Idempotent. */
    public static void evaluate(ServerPlayerEntity player, PlayerPowerComponent power) {
        Identifier lineage = power.lineage().orElse(null);
        if (lineage == null) return;
        Optional<LineageDefinition> def = AnimeRegistries.lineage(player.getRegistryManager(), lineage);
        if (def.isEmpty()) return;
        int level = power.clanSkillLevel(lineage);
        for (LineageDefinition.ProgressionEntry entry : def.get().progression()) {
            if (level < entry.skillLevel() || power.tier() < entry.requiredTier()) continue;
            entry.ability().ifPresent(id -> {
                if (power.grantClanAbility(id)) {
                    AnimeCoreEvents.CLAN_UNLOCK.invoker().onUnlock(player, power, lineage, id, false);
                    AnimeRegistries.ability(player.getRegistryManager(), id).ifPresent(a -> player.sendMessage(
                            Text.translatable("message.animecore.clan.unlocked", a.value().name()).formatted(Formatting.AQUA), false));
                }
            });
            entry.transformation().ifPresent(id -> {
                if (power.grantClanForm(id)) {
                    AnimeCoreEvents.CLAN_UNLOCK.invoker().onUnlock(player, power, lineage, id, true);
                    AnimeRegistries.transformation(player.getRegistryManager(), id).ifPresent(f -> player.sendMessage(
                            Text.translatable("message.animecore.clan.unlocked", f.value().name()).formatted(Formatting.LIGHT_PURPLE), false));
                }
            });
        }
    }

    /**
     * Adds clan skill experience to the player's <b>current</b> clan, levelling up (and evaluating progression) as
     * needed. Clan trait multipliers apply.
     * @return levels gained
     */
    public static int addSkillExperience(ServerPlayerEntity player, PlayerPowerComponent power, long amount) {
        Identifier lineage = power.lineage().orElse(null);
        if (lineage == null || amount <= 0) return 0;
        Optional<LineageDefinition> def = AnimeRegistries.lineage(player.getRegistryManager(), lineage);
        if (def.isEmpty()) return 0;
        float mult = 1f;
        for (IClanTrait t : def.get().clanTraits()) mult *= t.skillExperienceMultiplier();
        long gained = Math.max(1, Math.round(amount * mult));

        int max = def.get().maxSkillLevel();
        int level = power.clanSkillLevel(lineage);
        long xp = power.clanSkillXp(lineage) + gained;
        int startLevel = level;
        while (level < max && xp >= ClanSkill.experienceForNext(level)) {
            xp -= ClanSkill.experienceForNext(level);
            level++;
        }
        if (level >= max) xp = 0;
        power.setClanSkill(lineage, level, xp);
        for (int l = startLevel + 1; l <= level; l++) {
            AnimeCoreEvents.CLAN_SKILL_LEVEL_UP.invoker().onLevelUp(player, power, lineage, l);
        }
        if (level > startLevel) {
            player.sendMessage(Text.translatable("message.animecore.clan.level_up", def.get().name(), level).formatted(Formatting.GOLD), true);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.6f, 1.6f);
            evaluate(player, power);
        }
        refreshHeldSkillItems(player, power);
        return level - startLevel;
    }

    /** Re-writes the display component of held skill items bound to the current clan. */
    public static void refreshHeldSkillItems(ServerPlayerEntity player, PlayerPowerComponent power) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);
            if (ClanRollService.isSkillItemFor(stack, power)) writeSkillDisplay(stack, player, power);
        }
    }

    static void writeSkillDisplay(ItemStack stack, ServerPlayerEntity player, PlayerPowerComponent power) {
        power.lineage().ifPresent(lineage -> {
            int max = AnimeRegistries.lineage(player.getRegistryManager(), lineage).map(LineageDefinition::maxSkillLevel)
                    .orElse(LineageDefinition.DEFAULT_MAX_SKILL_LEVEL);
            int level = power.clanSkillLevel(lineage);
            stack.set(AnimeDataComponents.CLAN_SKILL,
                    new AnimeDataComponents.ClanSkill(level, ClanSkill.progress(level, power.clanSkillXp(lineage), max)));
        });
    }

    // ------------------------------------------------------------------ starter kit

    /** Bound skill item + dyed garb + every {@code clan_items} entry. */
    public static void giveStarterKit(ServerPlayerEntity player, PlayerPowerComponent power, Identifier lineage, LineageDefinition def) {
        ClanConfig config = ClanRollService.config(power.context().modId()).orElse(null);
        if (config == null || !config.giveStarterKit()) return;
        give(player, ClanRollService.boundStack(config.skillItem().get(), lineage, def));
        if (config.garbItem() != null) give(player, ClanRollService.boundStack(config.garbItem().get(), lineage, def));
        for (Identifier itemId : def.clanItems()) {
            Item item = Registries.ITEM.get(itemId);
            if (item != net.minecraft.item.Items.AIR) give(player, new ItemStack(item));
        }
        writeSkillDisplayInInventory(player, power);
    }

    private static void writeSkillDisplayInInventory(ServerPlayerEntity player, PlayerPowerComponent power) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (ClanRollService.isSkillItemFor(s, power)) writeSkillDisplay(s, player, power);
        }
    }

    private static void give(ServerPlayerEntity player, ItemStack stack) {
        if (!player.getInventory().insertStack(stack)) player.dropItem(stack, false);
    }

    /** Clan colour as a dye component (garb). */
    static void dye(ItemStack stack, int color) {
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color & 0xFFFFFF, false));
    }
}
