package com.animecore.datagen;

import com.animecore.ability.AbilityDamage;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/** {@code ./gradlew :animecore:runDatagen} -> src/main/generated */
public final class AnimeCoreDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(DamageTypes::new);
        pack.addProvider(English::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder builder) {
        builder.addRegistry(RegistryKeys.DAMAGE_TYPE, ctx -> ctx.register(AbilityDamage.ABILITY,
                new DamageType("animecore.ability", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f)));
    }

    private static final class DamageTypes extends FabricDynamicRegistryProvider {
        DamageTypes(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
            super(output, registries);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            entries.add(registries.getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE), AbilityDamage.ABILITY);
        }

        @Override public String getName() { return "animecore damage types"; }
    }

    private static final class English extends FabricLanguageProvider {
        English(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
            super(output, "en_us", registries);
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup registries, TranslationBuilder t) {
            t.add("entity.animecore.energy_projectile", "Energy Projectile");
            t.add("death.attack.animecore.ability", "%1$s was overwhelmed by a technique");
            t.add("death.attack.animecore.ability.player", "%1$s fell to %2$s's technique");
            t.add("death.attack.animecore.ability.item", "%1$s fell to %2$s's technique");
            t.add("message.animecore.cast.success", "Technique released");
            t.add("message.animecore.cast.charging", "Charging...");
            t.add("message.animecore.cast.armed", "Ready to counter");
            t.add("message.animecore.cast.unknown_ability", "Unknown technique");
            t.add("message.animecore.cast.unknown_system", "That technique belongs to another power system");
            t.add("message.animecore.cast.locked", "You have not learned that yet");
            t.add("message.animecore.cast.tier_too_low", "Your rank is too low");
            t.add("message.animecore.cast.wrong_form", "Requires a transformation");
            t.add("message.animecore.cast.on_cooldown", "Still recovering");
            t.add("message.animecore.cast.no_energy", "Not enough energy");
            t.add("message.animecore.cast.cancelled", "Cancelled");
            t.add("message.animecore.cast.no_match", "That sequence does nothing");
            t.add("message.animecore.reactive_armed", "%s ready");
            t.add("message.animecore.transformed", "%s activated");
            t.add("message.animecore.transformation_ended", "%s faded");
            t.add("message.animecore.evolved", "Your power evolves: %s unlocked!");
            t.add("message.animecore.no_transformation", "No transformation available");
            t.add("message.animecore.no_higher_stage", "No higher stage unlocked");
            t.add("message.animecore.stage_dropped", "Energy failing - dropped to %s");
            t.add("message.animecore.movement.on", "%s enabled");
            t.add("message.animecore.movement.off", "%s disabled");
            t.add("message.animecore.movement.exhausted", "Too exhausted for %s");
            t.add("movement.animecore.surface_adhesion", "Surface Walking");
            t.add("movement.animecore.omnidirectional_flight", "Flight");
            t.add("movement.animecore.burst_step", "Burst Step");
            t.add("screen.animecore.combo_wheel", "Choose a sign");
            t.add("screen.animecore.combo_wheel.hint", "Click / 1-9: input  ·  release key or right-click: perform");
            t.add("item.animecore.ability_scroll.named", "%s: %s");
            t.add("message.animecore.scroll.learned", "Learned %s!");
            t.add("message.animecore.scroll.known", "You already know this technique");
            t.add("message.animecore.scroll.unreadable", "The scroll's writing is unreadable");
            t.add("message.animecore.badge.pledged", "You already belong to a faction");
            t.add("message.animecore.weapon.infused", "Weapon infused: %s");
            t.add("message.animecore.weapon.dormant", "Weapon infusion faded");
            t.add("tooltip.animecore.cost", "Cost: %s %s");
            t.add("tooltip.animecore.tier", "Requires rank %s");
            t.add("tooltip.animecore.affinity", "Element: %s");
            t.add("tooltip.animecore.combo", "Sequence: %s");
            t.add("tooltip.animecore.cast_type.instant", "Instant");
            t.add("tooltip.animecore.cast_type.charged", "Hold to charge");
            t.add("tooltip.animecore.cast_type.reactive", "Reactive counter");
            t.add("tooltip.animecore.pure_energy", "Pure Energy");
            t.add("tooltip.animecore.weapon.active", "Infused: %s");
            t.add("tooltip.animecore.weapon.dormant", "Dormant (%s)");
            t.add("tooltip.animecore.weapon.bonus", "+%s elemental damage per hit (%s energy)");
            t.add("tooltip.animecore.weapon.controls", "Sneak+Use: infuse · Hold Use: charged slash");
            t.add("jei.animecore.ability_scroll", "Technique Scrolls");
            t.add("jei.animecore.energy_infusion", "Energy Infusion");
            t.add("jei.animecore.infusion.hint", "Sneak + Use while attuned to");
            t.add("move.animecore.throw", "Throw");
            t.add("move.animecore.trap", "Scatter Traps");
            t.add("move.animecore.ward", "Place Seal Tag");
            t.add("move.animecore.consume", "Use / Consume");
            t.add("move.animecore.charge_slash", "Charged Slash");
            t.add("move.animecore.dash", "Lunge");
            t.add("move.animecore.chain", "Chain / Grapple");
            t.add("move.animecore.guard", "Guard");
            t.add("move.animecore.gust", "Gust");
            t.add("move.animecore.blast", "Blast");
            t.add("move.animecore.seal", "Seal");
            t.add("move.animecore.summon", "Summon");
            t.add("move.animecore.sand", "Sand Wave");
            t.add("move.animecore.mark", "Flying Thunder God Mark");
            t.add("move.animecore.recall", "Warp to Mark");
            t.add("move.animecore.anchor", "Gate Anchor");
            t.add("move.animecore.vortex", "Space-Time Vortex");
            t.add("move.animecore.glide", "Glide");
            t.add("move.animecore.sonic", "Sound Wave");
            t.add("move.animecore.scan", "Scan");
            t.add("move.animecore.buff", "Empower");
            t.add("move.animecore.wall", "Raise Wall");
            t.add("move.animecore.slam", "Slam");
            t.add("move.animecore.infuse", "Energy Infusion");
            t.add("item.animecore.clan_item.named", "%1$s (%2$s)");
            t.add("item.animecore.bloodline_scroll.sealed", "%s (%s)");
            t.add("tooltip.animecore.clan_skill.level", "Lineage Skill Level %s");
            t.add("tooltip.animecore.clan_skill.progress", "%s%% to next level");
            t.add("tooltip.animecore.clan_skill.hint", "Hold (main or off hand) while defeating enemies: their experience becomes lineage skill");
            t.add("tooltip.animecore.clan_skill.unbound", "Use to attune to your lineage");
            t.add("tooltip.animecore.bloodline_scroll.random", "Use to reroll your lineage (never your current lineage)");
            t.add("tooltip.animecore.bloodline_scroll.sealed", "Use to awaken this lineage");
            t.add("message.animecore.clan.assigned", "Your lineage awakens: %s");
            t.add("message.animecore.clan.unlocked", "Lineage technique unlocked: %s");
            t.add("message.animecore.clan.level_up", "%s skill reached level %s!");
            t.add("message.animecore.clan.status", "%s - skill level %s/%s (%s/%s xp)");
            t.add("message.animecore.clan.next", "Next: %s at level %s");
            t.add("message.animecore.clan.rare", "A rare lineage!");
            t.add("message.animecore.clan.already", "That lineage already flows in you");
            t.add("message.animecore.clan.foreign_emblem", "This emblem belongs to another lineage");
            t.add("message.animecore.clan.choose_hint", "You have no lineage. Use /clan list and /clan choose <lineage> to join one.");
            t.add("commands.animecore.clan.cannot_choose", "You can only choose a selectable lineage while you have none");
            t.add("commands.animecore.clan.progress", "%s/%s lineage techniques unlocked, %s rerolls used");
            t.add("jei.animecore.clan.header", "Lineage techniques (first one known at once, the rest unlock with lineage skill):");
            t.add("jei.animecore.clan.entry", "Lv %s: %s");
            t.add("jei.animecore.clan.items", "Lineage items: %s");
            t.add("gamerule.animecoreClanAutoRoll", "Roll a clan for new players");
            t.add("item.animecore.headband.named", "%2$s %1$s");
            t.add("item.animecore.headband.scratched", "Scratched %2$s %1$s");
            t.add("tooltip.animecore.headband.scratch_hint", "Sneak + use with a blade in the other hand to scratch it (go rogue)");
            t.add("tooltip.animecore.headband.rogue", "Outcast: the faction symbol is cut through");
            t.add("tooltip.animecore.origin_scroll", "Use to forge new papers: choose your faction and background again");
            t.add("tooltip.animecore.village.requires", "Faction gear: %s (members, or reputation %s+)");
            t.add("tooltip.animecore.village.allowed", "You can use it fully");
            t.add("tooltip.animecore.village.restricted", "Outsider: half damage, no techniques or energy infusion");
            t.add("message.animecore.village.gear_locked", "Only %s members can draw out this weapon's power");
            t.add("message.animecore.village.uniform_locked", "You cannot wear %s's colours");
            t.add("message.animecore.village.jutsu_locked", "That is a secret technique of %s");
            t.add("message.animecore.village.scroll_locked", "A secret of %s - you need rank %s or allied standing to decipher it");
            t.add("message.animecore.village.jutsu_learned", "Faction technique learned: %s");
            t.add("message.animecore.village.not_yours", "This is not your faction's emblem (%s)");
            t.add("message.animecore.village.went_rogue", "You cut through the symbol of %s. You are now an outcast.");
            t.add("message.animecore.origin.hint", "Choose your faction and background (/origin open)");
            t.add("message.animecore.origin.chosen", "Origin: %s");
            t.add("message.animecore.origin.ok", "Origin set");
            t.add("message.animecore.origin.already_chosen", "Your origin is already set - use forged papers or ask an operator to reset it");
            t.add("message.animecore.origin.unknown_faction", "Unknown faction");
            t.add("message.animecore.origin.not_selectable", "That faction cannot be chosen at the start");
            t.add("message.animecore.origin.unknown_background", "Unknown background");
            t.add("message.animecore.origin.background_not_allowed", "That background is not possible in this faction");
            t.add("message.animecore.origin.wrong_system", "That faction belongs to another world");
            t.add("commands.animecore.origin.failed", "Origin not changed");
            t.add("commands.animecore.origin.info", "Faction: %s · Background: %s %s");
            t.add("commands.animecore.origin.rogue", "(outcast)");
            t.add("screen.animecore.origin.title", "Choose Your Origin");
            t.add("screen.animecore.origin.subtitle", "Your faction shapes your techniques, gear and allies; your background shapes you");
            t.add("screen.animecore.origin.villages", "Faction");
            t.add("screen.animecore.origin.backgrounds", "Upbringing");
            t.add("screen.animecore.origin.random", "Randomize Origin");
            t.add("screen.animecore.origin.confirm", "Confirm");
            t.add("screen.animecore.origin.pick_background", "Pick an upbringing");
            t.add("screen.animecore.origin.jutsu", "Faction techniques (%s): %s");
            t.add("screen.animecore.origin.exclusive", "Faction-exclusive gear: %s");
            t.add("screen.animecore.origin.kit", "Starting kit: %s");
            t.add("screen.animecore.origin.rogue_warning", "Hunted by every faction: all hidden factions start hostile");
            t.add("screen.animecore.origin.bonus", "+%s skill points, +%s rank. Items: %s");
            t.add("screen.animecore.origin.bg_jutsu", "Known techniques: %s");
            t.add("screen.animecore.origin.kind.hidden_village", "Hidden faction");
            t.add("screen.animecore.origin.kind.village", "Faction");
            t.add("screen.animecore.origin.kind.rogue", "Outcast");
            t.add("screen.animecore.origin.kind.sage_realm", "Sage realm");
            t.add("screen.animecore.origin.kind.faction", "Faction");
            t.add("jei.animecore.village.jutsu", "Faction techniques: %s");
            t.add("jei.animecore.village.gear", "Exclusive gear: %s (outsiders need reputation %s)");
            t.add("gamerule.animecoreOriginScreen", "Show the origin screen to new players");
            t.add("message.animecore.token.received", "New technique item: %s");
            t.add("message.animecore.token.selected", "Selected: %s");
            t.add("message.animecore.token.empty", "You know no techniques of this kind yet");
            t.add("tooltip.animecore.token.controls", "Right-click: use · hold: charge · Sneak + scroll / Sneak + left-click: switch");
            t.add("tooltip.animecore.token.charge", "  (hold)");
            t.add("tooltip.animecore.token.form", "  (eyes / form)");
            t.add("hud.animecore.token.hint", "Right-click to use · Sneak + scroll to switch");
            t.add("message.animecore.mastery.level", "%s mastery reached level %s!");
            t.add("message.animecore.mastery.learned", "Technique mastered: %s");
            t.add("message.animecore.mastery.awakened", "A new affinity awakens within you: %s!");
            t.add("message.animecore.mastery.nature", "Your affinity: %s");
            t.add("tooltip.animecore.token.mastery", "Mastery level %s (%s/%s xp) · %s/%s techniques · hold while defeating enemies to train");
            t.add("tooltip.animecore.token.off_affinity", "Not your affinity: training takes 2.5x longer");
            t.add("message.animecore.admin.denied", "Only operators or creative-mode players can use admin seals");
            t.add("message.animecore.admin.rank", "Rank is now %s");
            t.add("message.animecore.admin.clan", "Lineage skill level %s/%s");
            t.add("message.animecore.admin.no_clan", "You have no lineage");
            t.add("message.animecore.admin.mastery", "%s mastery %s/%s");
            t.add("message.animecore.admin.mastery_all", "Raised mastery of %s techniques");
            t.add("message.animecore.admin.no_token", "Hold a technique item in your other hand");
            t.add("message.animecore.admin.points", "Skill points: %s");
            t.add("message.animecore.admin.natures", "%s affinities granted");
            t.add("message.animecore.admin.reputation", "Reputation raised with %s factions");
            t.add("message.animecore.admin.refill", "Energy refilled, cooldowns cleared");
            t.add("message.animecore.admin.max", "Everything maxed: rank, lineage, affinities, energy");
            t.add("tooltip.animecore.admin.rank_up", "Use: +1 rank · Sneak + use: +10 ranks");
            t.add("tooltip.animecore.admin.clan_level", "Use: +1 lineage skill level · Sneak + use: max lineage level");
            t.add("tooltip.animecore.admin.mastery", "Use with a technique item in the other hand: +1 mastery · Sneak: master it fully (empty hand: all your items)");
            t.add("tooltip.animecore.admin.skill_points", "Use: +5 skill points · Sneak + use: +50");
            t.add("tooltip.animecore.admin.natures", "Use: all basic affinities · Sneak + use: every affinity");
            t.add("tooltip.animecore.admin.reputation", "Use: +50 reputation with every faction · Sneak + use: allied with all");
            t.add("tooltip.animecore.admin.refill", "Use: refill all energy and clear every cooldown");
            t.add("tooltip.animecore.admin.max_all", "Use: max rank, max lineage level, master all your affinities, refill");
            t.add("tooltip.animecore.admin.only", "Admin item: operators / creative mode only");
            t.add("entity.animecore.story_npc", "Story Character");
            t.add("message.animecore.trait.sunlight", "The sunlight is burning you - get to the shade!");
            t.add("message.animecore.lineage.confirm", "This would replace your current lineage with %s - sneak + use to confirm");
            t.add("tooltip.animecore.lineage_awaken", "Use: become this (sneak + use replaces your current lineage)");
            t.add("message.animecore.awaken.known", "You already have %s");
            t.add("message.animecore.awaken.rank", "Requires rank %s (you are rank %s)");
            t.add("message.animecore.awaken.done", "Awakened: %s! It is on your technique item - forms grow stronger with mastery.");
            t.add("tooltip.animecore.awaken", "Use: awaken this power (later stages evolve with mastery)");
            t.add("message.animecore.admin.story", "Story: %s quest(s) completed");
            t.add("message.animecore.admin.story_none", "No story quest available");
            t.add("tooltip.animecore.admin.story", "Use: complete your story quest; sneak + use: the rest of its arc");
            t.add("item.animecore.quest_scroll.tooltip", "Use: open the story journal");
            t.add("screen.animecore.journal", "Story Journal");
            t.add("screen.animecore.journal.accept", "Accept");
            t.add("screen.animecore.journal.abandon", "Abandon");
            t.add("screen.animecore.journal.summon", "Call again");
            t.add("screen.animecore.journal.rank", "  \u00b7 rank %s");
            t.add("screen.animecore.journal.rewards", "Rewards:");
            t.add("screen.animecore.journal.reward_rank", "rank %s");
            t.add("screen.animecore.journal.reward_sp", "%s skill points");
            t.add("screen.animecore.journal.reward_jutsu", "%s techniques");
            t.add("screen.animecore.journal.locked", "Locked: finish the previous chapter first");
            t.add("screen.animecore.journal.done", "Completed");
            t.add("hud.animecore.quest.site", "%s blocks");
            t.add("message.animecore.quest.busy", "Finish or abandon your current quest first (quest scroll).");
            t.add("message.animecore.quest.locked", "That chapter is not available yet.");
            t.add("message.animecore.quest.started", "Quest started: %s");
            t.add("message.animecore.quest.underleveled", "Recommended rank %s - you are rank %s. Be careful!");
            t.add("message.animecore.quest.abandoned", "Quest abandoned.");
            t.add("message.animecore.quest.objective", "Next: %s");
            t.add("message.animecore.quest.progress", "%s (%s/%s)");
            t.add("message.animecore.quest.arrived", "You reached the place.");
            t.add("message.animecore.quest.completed", "Quest complete: %s");
            t.add("message.animecore.quest.next", "Next chapter available: %s");
            t.add("quest.animecore.objective.talk", "Talk to %s");
            t.add("quest.animecore.objective.defeat", "Defeat %s");
            t.add("quest.animecore.objective.kill", "Defeat %2$s %1$s");
            t.add("quest.animecore.objective.collect", "Gather %2$s %1$s");
            t.add("quest.animecore.objective.use_ability", "Use %2$s techniques");
            t.add("quest.animecore.objective.travel", "Travel to the marked place");
            t.add("quest.animecore.objective.rank", "Reach rank %2$s");
            t.add("message.animecore.mastery.learned_many", "Mastered %s %s techniques");
            t.add("message.animecore.clan.unlocked_many", "%s %s techniques unlocked");
            t.add("entity.animecore.puppet.wood", "Puppet");
            t.add("entity.animecore.puppet.metal", "Metal Puppet");
            t.add("entity.animecore.puppet.crimson", "Human Puppet");
            t.add("entity.animecore.puppet.crow", "Karasu");
            t.add("entity.animecore.puppet.ant", "Kuroari");
            t.add("entity.animecore.puppet.lizard", "Salamander Puppet");
            t.add("entity.animecore.puppet.bee", "Bee Drone");
            t.add("entity.animecore.puppet.drone", "Drone");
            t.add("entity.animecore.puppet.demon", "Summoned Demon");
            t.add("entity.animecore.puppet.toad", "Mechanical Toad");
            t.add("entity.animecore.puppet", "Puppet");
            t.add("entity.animecore.thrown_tool", "Thrown Tool");
            t.add("block.animecore.ward_tag", "Seal Tag");
            t.add("block.animecore.chakra_wall", "Chakra Wall");
            t.add("block.animecore.barrier_gate", "Barrier Gate");
            t.add("tooltip.animecore.tool.primary", "Right-click: %s");
            t.add("tooltip.animecore.tool.secondary", "Sneak + Right-click: %s");
            t.add("message.animecore.ward.wall", "Barrier raised (%s more in sequence for a Barrier Gate)");
            t.add("message.animecore.ward.gate", "Barrier Gate formed from %s tags!");
            t.add("message.animecore.tool.marked", "Seal placed");
            t.add("message.animecore.tool.no_mark", "No Flying Thunder God seal placed");
            t.add("message.animecore.tool.no_target", "No target");
            t.add("message.animecore.tool.sealed", "Sealed %s!");
            t.add("message.animecore.tool.scan", "%s presences detected");
            t.add("message.animecore.tool.anchor_set", "Gate anchor set");
            t.add("message.animecore.tool.no_anchor", "No gate anchor in this dimension");
            t.add("commands.animecore.unknown", "Unknown id: %s");
            t.add("commands.animecore.lineage", "%s now belongs to %s");
            t.add("commands.animecore.faction", "%s joined %s");
            t.add("commands.animecore.unlock", "%s learned %s");
        }
    }
}
