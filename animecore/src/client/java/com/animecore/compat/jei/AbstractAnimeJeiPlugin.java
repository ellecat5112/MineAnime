package com.animecore.compat.jei;

import com.animecore.api.AnimeModContext;
import com.animecore.item.AbilityScrollItem;
import com.animecore.item.AnimeComponentItem;
import com.animecore.item.AnimeItemVariants;
import com.animecore.item.AnimeWeaponItem;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AffinityDefinition;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base JEI plugin. A content mod only subclasses it and lists the class under the {@code jei_mod_plugin}
 * entrypoint; everything else is discovered from the mod's namespace:
 * <ul>
 *   <li>subtype interpreters for every {@link AnimeComponentItem} of the mod (scrolls, badges, infused weapons)</li>
 *   <li>a per-mod "Technique Scrolls" category ({@code <modId>:ability_scroll}) from the synced ability registry</li>
 *   <li>a per-mod "Energy Infusion" category ({@code <modId>:energy_infusion}) for every weapon × base affinity</li>
 * </ul>
 * Lives in the client source set (JEI is client-only), so it is never loaded on dedicated servers; JEI itself is an
 * optional dependency and this class is only touched through JEI's entrypoint.
 */
public abstract class AbstractAnimeJeiPlugin implements IModPlugin {
    private RecipeType<AbilityScrollCategory.Recipe> scrollType;
    private RecipeType<EnergyInfusionCategory.Recipe> infusionType;

    protected abstract AnimeModContext context();

    @Override
    public Identifier getPluginUid() {
        return context().id("jei_plugin");
    }

    protected RecipeType<AbilityScrollCategory.Recipe> scrollType() {
        if (scrollType == null) scrollType = RecipeType.create(context().modId(), "ability_scroll", AbilityScrollCategory.Recipe.class);
        return scrollType;
    }

    protected RecipeType<EnergyInfusionCategory.Recipe> infusionType() {
        if (infusionType == null) infusionType = RecipeType.create(context().modId(), "energy_infusion", EnergyInfusionCategory.Recipe.class);
        return infusionType;
    }

    /** All items registered under this mod's namespace. */
    protected List<Item> modItems() {
        List<Item> out = new ArrayList<>();
        for (Item item : Registries.ITEM) {
            if (Registries.ITEM.getId(item).getNamespace().equals(context().modId())) out.add(item);
        }
        return out;
    }

    protected <T> List<T> modItems(Class<T> type) {
        return modItems().stream().filter(type::isInstance).map(type::cast).toList();
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        for (Item item : modItems()) {
            if (item instanceof AnimeComponentItem componentItem) {
                registration.registerSubtypeInterpreter(item, new AnimeComponentSubtypeInterpreter(componentItem.subtypeComponents()));
            }
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        Optional<AbilityScrollItem> scroll = modItems(AbilityScrollItem.class).stream().findFirst();
        Optional<AnimeWeaponItem> weapon = modItems(AnimeWeaponItem.class).stream().findFirst();
        if (scroll.isPresent()) registration.addRecipeCategories(new AbilityScrollCategory(scrollType(), gui, new ItemStack(scroll.get())));
        if (weapon.isPresent()) registration.addRecipeCategories(new EnergyInfusionCategory(infusionType(), gui, new ItemStack(weapon.get())));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var world = MinecraftClient.getInstance().world;
        if (world == null) return;
        DynamicRegistryManager rm = world.getRegistryManager();
        String system = context().modId();

        List<AbilityScrollCategory.Recipe> scrolls = new ArrayList<>();
        for (AbilityScrollItem scroll : modItems(AbilityScrollItem.class)) {
            for (ItemStack stack : AnimeItemVariants.scrolls(scroll, system, rm)) {
                AbilityScrollItem.ability(stack).flatMap(id -> AnimeRegistries.ability(rm, id))
                        .ifPresent(e -> scrolls.add(new AbilityScrollCategory.Recipe(stack, e.value())));
            }
        }
        if (!scrolls.isEmpty()) registration.addRecipes(scrollType(), scrolls);

        List<EnergyInfusionCategory.Recipe> infusions = new ArrayList<>();
        for (AnimeWeaponItem weapon : modItems(AnimeWeaponItem.class)) {
            for (Identifier affinity : AnimeItemVariants.baseAffinities(system, rm)) {
                Optional<AffinityDefinition> def = AnimeRegistries.affinities(rm).getOrEmpty(affinity);
                def.ifPresent(d -> infusions.add(new EnergyInfusionCategory.Recipe(new ItemStack(weapon), d,
                        AnimeItemVariants.infused(weapon, affinity, true))));
            }
        }
        if (!infusions.isEmpty()) registration.addRecipes(infusionType(), infusions);

        // Village headbands: one info page per village (techniques, exclusive gear, standings).
        for (com.animecore.item.FactionHeadbandItem band : modItems(com.animecore.item.FactionHeadbandItem.class)) {
            for (ItemStack stack : AnimeItemVariants.factionBound(band, system, rm, false)) {
                var badge = stack.get(com.animecore.item.AnimeDataComponents.FACTION_BADGE);
                if (badge == null) continue;
                AnimeRegistries.faction(rm, badge.faction()).ifPresent(def -> registration.addItemStackInfo(stack, factionInfo(rm, def)));
            }
        }

        // Clan items: one info page per clan listing its default + progression techniques and clan items.
        for (com.animecore.item.ClanEmblemItem emblem : modItems(com.animecore.item.ClanEmblemItem.class)) {
            for (ItemStack stack : AnimeItemVariants.clanBound(emblem, system, rm)) {
                com.animecore.clan.ClanRollService.binding(stack).flatMap(id -> AnimeRegistries.lineage(rm, id))
                        .ifPresent(def -> registration.addItemStackInfo(stack, clanInfo(rm, def)));
            }
        }
    }

    private static net.minecraft.text.Text[] factionInfo(DynamicRegistryManager rm, com.animecore.registry.definition.FactionDefinition def) {
        List<net.minecraft.text.Text> lines = new ArrayList<>();
        lines.add(def.name().copy().styled(s -> s.withColor(def.color()).withBold(true)));
        if (!def.description().getString().isEmpty()) lines.add(def.description());
        net.minecraft.text.MutableText jutsu = net.minecraft.text.Text.empty();
        for (int i = 0; i < def.jutsuPool().size(); i++) {
            if (i > 0) jutsu.append(", ");
            Identifier id = def.jutsuPool().get(i);
            jutsu.append(AnimeRegistries.ability(rm, id).map(e -> e.value().name()).orElse(net.minecraft.text.Text.literal(id.getPath())));
        }
        if (!def.jutsuPool().isEmpty()) lines.add(com.animecore.api.AnimeText.tr(def.system(), "jei.animecore.village.jutsu", jutsu));
        net.minecraft.text.MutableText gear = net.minecraft.text.Text.empty();
        for (int i = 0; i < def.exclusive().items().size(); i++) {
            if (i > 0) gear.append(", ");
            gear.append(Registries.ITEM.get(def.exclusive().items().get(i)).getName());
        }
        if (!def.exclusive().items().isEmpty()) lines.add(net.minecraft.text.Text.translatable("jei.animecore.village.gear", gear,
                def.exclusive().alliedReputation()));
        def.reputation().forEach((other, standing) -> AnimeRegistries.faction(rm, other).ifPresent(o ->
                lines.add(net.minecraft.text.Text.literal(" " + (standing >= 0 ? "+" : "") + standing + " ").append(o.name()))));
        return lines.toArray(net.minecraft.text.Text[]::new);
    }

    private static net.minecraft.text.Text[] clanInfo(DynamicRegistryManager rm, com.animecore.registry.definition.LineageDefinition def) {
        List<net.minecraft.text.Text> lines = new ArrayList<>();
        lines.add(def.name().copy().styled(s -> s.withColor(def.color()).withBold(true)));
        if (!def.description().getString().isEmpty()) lines.add(def.description());
        lines.add(com.animecore.api.AnimeText.tr(def.system(), "jei.animecore.clan.header"));
        for (Identifier id : def.grantedAbilities()) {
            AnimeRegistries.ability(rm, id).ifPresent(a -> lines.add(net.minecraft.text.Text.translatable("jei.animecore.clan.entry", 1, a.value().name())));
        }
        for (var e : def.progression()) {
            net.minecraft.text.Text name = e.ability().flatMap(id -> AnimeRegistries.ability(rm, id)).<net.minecraft.text.Text>map(a -> a.value().name())
                    .or(() -> e.transformation().flatMap(id -> AnimeRegistries.transformation(rm, id)).map(f -> f.value().name()))
                    .orElse(net.minecraft.text.Text.literal("?"));
            lines.add(net.minecraft.text.Text.translatable("jei.animecore.clan.entry", e.skillLevel(), name));
        }
        if (!def.clanItems().isEmpty()) {
            net.minecraft.text.MutableText items = net.minecraft.text.Text.empty();
            for (int i = 0; i < def.clanItems().size(); i++) {
                if (i > 0) items.append(", ");
                items.append(Registries.ITEM.get(def.clanItems().get(i)).getName());
            }
            lines.add(com.animecore.api.AnimeText.tr(def.system(), "jei.animecore.clan.items", items));
        }
        return lines.toArray(net.minecraft.text.Text[]::new);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (AbilityScrollItem scroll : modItems(AbilityScrollItem.class)) registration.addRecipeCatalyst(scroll, scrollType());
        for (AnimeWeaponItem weapon : modItems(AnimeWeaponItem.class)) registration.addRecipeCatalyst(weapon, infusionType());
    }
}
