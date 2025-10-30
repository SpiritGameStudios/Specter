package dev.spiritstudios.specter.mixin.item;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeJsonBuilderAccessor {
	@Accessor
	Map<String, Criterion<?>> getCriteria();

	@Accessor
	String getGroup();

	@Accessor
	RecipeCategory getCategory();

	@Accessor
	int getCount();

	@Accessor
	boolean getShowNotification();

	@Invoker
	ShapedRecipePattern callEnsureValid(ResourceKey<Recipe<?>> recipeKey);
}
