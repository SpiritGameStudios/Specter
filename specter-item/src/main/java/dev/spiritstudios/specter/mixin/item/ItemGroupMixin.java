package dev.spiritstudios.specter.mixin.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import dev.spiritstudios.specter.api.item.SpecterItemRegistryKeys;
import dev.spiritstudios.specter.api.registry.reloadable.SpecterReloadableRegistries;

@Mixin(ItemGroup.class)
public abstract class ItemGroupMixin {
	@Shadow
	public abstract ItemGroup.Type getType();

	@ModifyReturnValue(method = {"getDisplayStacks", "getSearchTabStacks"}, at = @At("RETURN"))
	private Collection<ItemStack> getDisplayStacks(Collection<ItemStack> original) {
		if (this.getType() != ItemGroup.Type.SEARCH) return original;

		List<ItemStack> stacks = new ArrayList<>(original);
		SpecterReloadableRegistries.lookup().ifPresent(manager ->
			manager.getOrThrow(SpecterItemRegistryKeys.ITEM_GROUP).streamEntries()
				.map(r -> r.value().getSearchTabStacks())
				.filter(searchTabStacks -> !searchTabStacks.isEmpty())
				.forEach(stacks::addAll));

		return stacks;
	}
}
