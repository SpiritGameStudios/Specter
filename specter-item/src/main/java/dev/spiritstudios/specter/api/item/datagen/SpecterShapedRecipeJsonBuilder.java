package dev.spiritstudios.specter.api.item.datagen;

import dev.spiritstudios.specter.mixin.item.ShapedRecipeJsonBuilderAccessor;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;

import java.util.Objects;

/**
 * A shaped recipe builder that takes an {@link ItemStack} as output.
 * This allows for output items with components.
 */
public class SpecterShapedRecipeJsonBuilder extends ShapedRecipeJsonBuilder {
	private final ItemStack output;

	public SpecterShapedRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemStack output, int count) {
		super(registryLookup, category, output.getItem(), count);
		this.output = output;
	}

	public static SpecterShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemStack output) {
		return create(registryLookup, category, output, 1);
	}

	public static SpecterShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemStack output, int count) {
		return new SpecterShapedRecipeJsonBuilder(registryLookup, category, output, count);
	}

	@Override
	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
		ShapedRecipeJsonBuilderAccessor accessor = (ShapedRecipeJsonBuilderAccessor) this;

		RawShapedRecipe rawShapedRecipe = accessor.callValidate(recipeKey);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		accessor.getCriteria().forEach(builder::criterion);
		ShapedRecipe shapedRecipe = new ShapedRecipe(
			Objects.requireNonNullElse(accessor.getGroup(), ""),
			CraftingRecipeJsonBuilder.toCraftingCategory(accessor.getCategory()),
			rawShapedRecipe,
			this.output.copyWithCount(accessor.getCount()),
			accessor.getShowNotification()
		);
		exporter.accept(
			recipeKey,
			shapedRecipe,
			builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + accessor.getCategory().getName() + "/"))
		);
	}
}
