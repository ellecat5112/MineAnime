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
            t.add("move.animecore.infuse", "Chakra Infusion");
            t.add("item.animecore.clan_item.named", "%1$s (%2$s)");
            t.add("item.animecore.bloodline_scroll.sealed", "%s (%s)");
            t.add("tooltip.animecore.clan_skill.level", "Clan Skill Level %s");
            t.add("tooltip.animecore.clan_skill.progress", "%s%% to next level");
            t.add("tooltip.animecore.clan_skill.hint", "Hold (main or off hand) while defeating enemies: their experience becomes clan skill");
            t.add("tooltip.animecore.clan_skill.unbound", "Use to attune to your clan");
            t.add("tooltip.animecore.bloodline_scroll.random", "Use to reroll your bloodline (never your current clan)");
            t.add("tooltip.animecore.bloodline_scroll.sealed", "Use to awaken this bloodline");
            t.add("message.animecore.clan.assigned", "Your bloodline awakens: %s");
            t.add("message.animecore.clan.unlocked", "Clan technique unlocked: %s");
            t.add("message.animecore.clan.level_up", "%s skill reached level %s!");
            t.add("message.animecore.clan.status", "%s - skill level %s/%s (%s/%s xp)");
            t.add("message.animecore.clan.next", "Next: %s at level %s");
            t.add("message.animecore.clan.rare", "A rare bloodline!");
            t.add("message.animecore.clan.already", "That bloodline already flows in you");
            t.add("message.animecore.clan.foreign_emblem", "This emblem belongs to another clan");
            t.add("message.animecore.clan.choose_hint", "You are clanless. Use /clan list and /clan choose <clan> to join one.");
            t.add("commands.animecore.clan.cannot_choose", "You can only choose a selectable clan while clanless");
            t.add("commands.animecore.clan.progress", "%s/%s clan techniques unlocked, %s rerolls used");
            t.add("jei.animecore.clan.header", "Clan techniques (first one known at once, the rest unlock with clan skill):");
            t.add("jei.animecore.clan.entry", "Lv %s: %s");
            t.add("jei.animecore.clan.items", "Clan items: %s");
            t.add("gamerule.animecoreClanAutoRoll", "Roll a clan for new players");
            t.add("item.animecore.headband.named", "%2$s %1$s");
            t.add("item.animecore.headband.scratched", "Scratched %2$s %1$s");
            t.add("tooltip.animecore.headband.scratch_hint", "Sneak + use with a blade in the other hand to scratch it (go rogue)");
            t.add("tooltip.animecore.headband.rogue", "Missing-nin: the village symbol is cut through");
            t.add("tooltip.animecore.origin_scroll", "Use to forge new papers: choose your village and upbringing again");
            t.add("tooltip.animecore.village.requires", "Village gear: %s (members, or reputation %s+)");
            t.add("tooltip.animecore.village.allowed", "You can use it fully");
            t.add("tooltip.animecore.village.restricted", "Outsider: half damage, no techniques or chakra infusion");
            t.add("message.animecore.village.gear_locked", "Only %s shinobi can draw out this weapon's power");
            t.add("message.animecore.village.uniform_locked", "You cannot wear %s's colours");
            t.add("message.animecore.village.jutsu_locked", "That is a secret technique of %s");
            t.add("message.animecore.village.scroll_locked", "A secret of %s - you need rank %s or allied standing to decipher it");
            t.add("message.animecore.village.jutsu_learned", "Village technique learned: %s");
            t.add("message.animecore.village.not_yours", "This is not your village's headband (%s)");
            t.add("message.animecore.village.went_rogue", "You cut through the symbol of %s. You are now a missing-nin.");
            t.add("message.animecore.origin.hint", "Choose your village and upbringing (/origin open)");
            t.add("message.animecore.origin.chosen", "Origin: %s");
            t.add("message.animecore.origin.ok", "Origin set");
            t.add("message.animecore.origin.already_chosen", "Your origin is already set - use forged papers or ask an operator to reset it");
            t.add("message.animecore.origin.unknown_faction", "Unknown village");
            t.add("message.animecore.origin.not_selectable", "That village cannot be chosen at the start");
            t.add("message.animecore.origin.unknown_background", "Unknown background");
            t.add("message.animecore.origin.background_not_allowed", "That upbringing is not possible in this village");
            t.add("message.animecore.origin.wrong_system", "That village belongs to another world");
            t.add("commands.animecore.origin.failed", "Origin not changed");
            t.add("commands.animecore.origin.info", "Village: %s · Background: %s %s");
            t.add("commands.animecore.origin.rogue", "(missing-nin)");
            t.add("screen.animecore.origin.title", "Choose Your Origin");
            t.add("screen.animecore.origin.subtitle", "Your village shapes your techniques, gear and allies; your upbringing shapes you");
            t.add("screen.animecore.origin.villages", "Village");
            t.add("screen.animecore.origin.backgrounds", "Upbringing");
            t.add("screen.animecore.origin.random", "Randomize Origin");
            t.add("screen.animecore.origin.confirm", "Confirm");
            t.add("screen.animecore.origin.pick_background", "Pick an upbringing");
            t.add("screen.animecore.origin.jutsu", "Village techniques (%s): %s");
            t.add("screen.animecore.origin.exclusive", "Village-exclusive gear: %s");
            t.add("screen.animecore.origin.kit", "Starting kit: %s");
            t.add("screen.animecore.origin.rogue_warning", "Hunted by every village: all hidden villages start hostile");
            t.add("screen.animecore.origin.bonus", "+%s skill points, +%s rank. Items: %s");
            t.add("screen.animecore.origin.bg_jutsu", "Known techniques: %s");
            t.add("screen.animecore.origin.kind.hidden_village", "Hidden village");
            t.add("screen.animecore.origin.kind.village", "Village");
            t.add("screen.animecore.origin.kind.rogue", "Missing-nin");
            t.add("screen.animecore.origin.kind.sage_realm", "Sage realm");
            t.add("screen.animecore.origin.kind.faction", "Faction");
            t.add("jei.animecore.village.jutsu", "Village techniques: %s");
            t.add("jei.animecore.village.gear", "Exclusive gear: %s (outsiders need reputation %s)");
            t.add("gamerule.animecoreOriginScreen", "Show the origin screen to new players");
            t.add("message.animecore.token.received", "New jutsu item: %s");
            t.add("message.animecore.token.selected", "Selected: %s");
            t.add("message.animecore.token.empty", "You know no techniques of this kind yet");
            t.add("tooltip.animecore.token.controls", "Right-click: use · hold: charge · Sneak + scroll / Sneak + left-click: switch");
            t.add("tooltip.animecore.token.charge", "  (hold)");
            t.add("tooltip.animecore.token.form", "  (eyes / form)");
            t.add("hud.animecore.token.hint", "Right-click to use · Sneak + scroll to switch");
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
