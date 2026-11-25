package com.animecore.compat.jei;

import com.animecore.registry.definition.AffinityDefinition;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** "Elemental / Energy Infusion" category: blank weapon + wielder's affinity -> attuned weapon. */
public final class EnergyInfusionCategory implements IRecipeCategory<EnergyInfusionCategory.Recipe> {
    public record Recipe(ItemStack input, AffinityDefinition affinity, ItemStack output) {}

    private final RecipeType<Recipe> type;
    private final IDrawable icon;

    public EnergyInfusionCategory(RecipeType<Recipe> type, IGuiHelper gui, ItemStack iconStack) {
        this.type = type;
        this.icon = gui.createDrawableItemStack(iconStack);
    }

    @Override public RecipeType<Recipe> getRecipeType() { return type; }
    @Override public Text getTitle() { return Text.translatable("jei.animecore.energy_infusion"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 150; }
    @Override public int getHeight() { return 40; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 12).setStandardSlotBackground().addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 12).setStandardSlotBackground().addItemStack(recipe.output());
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView slots, DrawContext context, double mouseX, double mouseY) {
        var font = MinecraftClient.getInstance().textRenderer;
        int color = 0xFF000000 | recipe.affinity().color();
        Text hint = Text.translatable("jei.animecore.infusion.hint");
        context.drawText(font, hint, (getWidth() - font.getWidth(hint)) / 2, 2, 0xFF606060, false);
        Text name = recipe.affinity().name();
        context.drawText(font, name, (getWidth() - font.getWidth(name)) / 2, 16, color, true);
        context.fill(24, 30, 126, 32, color);
        context.fill(122, 27, 126, 35, color);
    }
}
