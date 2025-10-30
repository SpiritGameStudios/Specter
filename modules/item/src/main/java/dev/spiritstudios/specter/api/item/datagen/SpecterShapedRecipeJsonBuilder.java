package dev.spiritstudios.specter.api.item.datagen;

import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import dev.spiritstudios.specter.mixin.item.ShapedRecipeJsonBuilderAccessor;

/**
 * A shaped recipe builder that takes an {@link ItemStack} as output.
 * This allows for output items with components.
 */
public class SpecterShapedRecipeJsonBuilder extends ShapedRecipeBuilder {
	private final ItemStack output;

	public SpecterShapedRecipeJsonBuilder(HolderGetter<Item> registryLookup, RecipeCategory category, ItemStack output, int count) {
		super(registryLookup, category, output.getItem(), count);
		this.output = output;
	}

	public static SpecterShapedRecipeJsonBuilder create(HolderGetter<Item> registryLookup, RecipeCategory category, ItemStack output) {
		return create(registryLookup, category, output, 1);
	}

	public static SpecterShapedRecipeJsonBuilder create(HolderGetter<Item> registryLookup, RecipeCategory category, ItemStack output, int count) {
		return new SpecterShapedRecipeJsonBuilder(registryLookup, category, output, count);
	}

	@Override
	public void save(RecipeOutput exporter, ResourceKey<Recipe<?>> recipeKey) {
		ShapedRecipeJsonBuilderAccessor accessor = (ShapedRecipeJsonBuilderAccessor) this;

		ShapedRecipePattern rawShapedRecipe = accessor.callEnsureValid(recipeKey);
		Advancement.Builder builder = exporter.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.requirements(AdvancementRequirements.Strategy.OR);
		accessor.getCriteria().forEach(builder::addCriterion);
		ShapedRecipe shapedRecipe = new ShapedRecipe(
			Objects.requireNonNullElse(accessor.getGroup(), ""),
			RecipeBuilder.determineBookCategory(accessor.getCategory()),
			rawShapedRecipe,
			this.output.copyWithCount(accessor.getCount()),
			accessor.getShowNotification()
		);
		exporter.accept(
			recipeKey,
			shapedRecipe,
			builder.build(recipeKey.location().withPrefix("recipes/" + accessor.getCategory().getFolderName() + "/"))
		);
	}
}
