package dev.spiritstudios.specter.api.item.datagen;

import dev.spiritstudios.specter.impl.item.ShapedRecipeJsonBuilderAccessor;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * A shaped recipe builder that takes an {@link ItemStack} as output.
 * This allows for output items with components.
 */
public class SpecterShapedRecipeJsonBuilder extends ShapedRecipeJsonBuilder {
	private final ItemStack output;

	public SpecterShapedRecipeJsonBuilder(RecipeCategory category, ItemStack output, int count) {
		super(category, output.getItem(), count);
		this.output = output;
	}

	public static SpecterShapedRecipeJsonBuilder create(RecipeCategory category, ItemStack output) {
		return create(category, output, 1);
	}

	public static SpecterShapedRecipeJsonBuilder create(RecipeCategory category, ItemStack output, int count) {
		return new SpecterShapedRecipeJsonBuilder(category, output, count);
	}

	@Override
	public void offerTo(RecipeExporter exporter, Identifier recipeId) {
		ShapedRecipeJsonBuilderAccessor accessor = (ShapedRecipeJsonBuilderAccessor) this;

		RawShapedRecipe rawShapedRecipe = this.validate(recipeId);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		accessor.getCriteria().forEach(builder::criterion);
		ShapedRecipe shapedRecipe = new ShapedRecipe(
			Objects.requireNonNullElse(accessor.getGroup(), ""),
			CraftingRecipeJsonBuilder.toCraftingCategory(accessor.getCategory()),
			rawShapedRecipe,
			this.output.copyWithCount(accessor.getCount()),
			accessor.getShowNotification()
		);
		exporter.accept(recipeId, shapedRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + accessor.getCategory().getName() + "/")));
	}
}
