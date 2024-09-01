package dev.spiritstudios.specter.impl.item;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.Map;

public interface ShapedRecipeJsonBuilderAccessor {
	Map<String, AdvancementCriterion<?>> getCriteria();

	String getGroup();

	RecipeCategory getCategory();

	int getCount();

	boolean getShowNotification();
}