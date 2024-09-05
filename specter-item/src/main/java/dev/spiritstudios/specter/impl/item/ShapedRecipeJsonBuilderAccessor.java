package dev.spiritstudios.specter.impl.item;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface ShapedRecipeJsonBuilderAccessor {
	Map<String, AdvancementCriterion<?>> getCriteria();

	String getGroup();

	RecipeCategory getCategory();

	int getCount();

	boolean getShowNotification();

	RawShapedRecipe callValidate(Identifier recipeId);
}
