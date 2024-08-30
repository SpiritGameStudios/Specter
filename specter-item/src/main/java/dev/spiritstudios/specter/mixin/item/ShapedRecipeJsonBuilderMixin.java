package dev.spiritstudios.specter.mixin.item;

import dev.spiritstudios.specter.impl.item.ShapedRecipeJsonBuilderAccessor;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ShapedRecipeJsonBuilder.class)
public abstract class ShapedRecipeJsonBuilderMixin implements ShapedRecipeJsonBuilderAccessor {
	@Override
	@Accessor
	public abstract Map<String, AdvancementCriterion<?>> getCriteria();

	@Override
	@Accessor
	public abstract String getGroup();

	@Override
	@Accessor
	public abstract RecipeCategory getCategory();

	@Override
	@Accessor
	public abstract int getCount();

	@Override
	@Accessor
	public abstract boolean getShowNotification();
}
