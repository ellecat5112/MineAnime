package com.animecore.compat.jei;

import com.animecore.item.AbilityScrollItem;
import com.animecore.registry.definition.AbilityDefinition;
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

import java.util.ArrayList;
import java.util.List;

/** "Ability Scroll / Technique" category: what each scroll teaches (cost, rank, element, hand-sign sequence). */
public final class AbilityScrollCategory implements IRecipeCategory<AbilityScrollCategory.Recipe> {
    public record Recipe(ItemStack scroll, AbilityDefinition ability) {}

    private final RecipeType<Recipe> type;
    private final IDrawable icon;

    public AbilityScrollCategory(RecipeType<Recipe> type, IGuiHelper gui, ItemStack iconStack) {
        this.type = type;
        this.icon = gui.createDrawableItemStack(iconStack);
    }

    @Override public RecipeType<Recipe> getRecipeType() { return type; }
    @Override public Text getTitle() { return Text.translatable("jei.animecore.ability_scroll"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 170; }
    @Override public int getHeight() { return 84; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).setStandardSlotBackground().addItemStack(recipe.scroll());
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView slots, DrawContext context, double mouseX, double mouseY) {
        var font = MinecraftClient.getInstance().textRenderer;
        var title = font.wrapLines(recipe.ability().name(), getWidth() - 22);
        for (int i = 0; i < Math.min(2, title.size()); i++) {
            context.drawText(font, title.get(i), 22, title.size() == 1 ? 5 : 1 + i * 9, 0xFF404040, false);
        }
        List<Text> lines = new ArrayList<>();
        AbilityScrollItem.AbilityTooltips.append(recipe.ability(), lines);
        int y = 21;
        for (Text styled : lines) {
            Text line = Text.literal(styled.getString()); // tooltip colours are made for dark backgrounds
            for (var wrapped : font.wrapLines(line, getWidth() - 2)) {
                if (y > getHeight() - 9) return;
                context.drawText(font, wrapped, 1, y, 0xFF555555, false);
                y += 9;
            }
        }
    }
}
