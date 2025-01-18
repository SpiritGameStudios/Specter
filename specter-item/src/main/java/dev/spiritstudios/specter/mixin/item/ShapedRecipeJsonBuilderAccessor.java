package dev.spiritstudios.specter.mixin.item;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapedRecipeJsonBuilder.class)
public interface ShapedRecipeJsonBuilderAccessor {
	@Accessor
	Map<String, AdvancementCriterion<?>> getCriteria();

	@Accessor
	String getGroup();

	@Accessor
	RecipeCategory getCategory();

	@Accessor
	int getCount();

	@Accessor
	boolean getShowNotification();

	@Invoker
	RawShapedRecipe callValidate(RegistryKey<Recipe<?>> recipeKey);
}
