package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;

import dev.spiritstudios.specter.api.item.ItemMetatags;

@Mixin(FuelRegistry.class)
public abstract class FuelRegistryMixin {
	@ModifyReturnValue(method = "isFuel", at = @At("RETURN"))
	private boolean canUseAsFuel(boolean original, @Local(argsOnly = true) ItemStack stack) {
		if (stack.isEmpty()) return original;

		boolean hasFuelMetatag = ItemMetatags.FUEL.containsKey(stack.getItem());
		return original || hasFuelMetatag;
	}

	@ModifyReturnValue(method = "getFuelTicks", at = @At("RETURN"))
	private int getFuelTime(int original, @Local(argsOnly = true) ItemStack fuel) {
		if (fuel.isEmpty()) return original;
		return ItemMetatags.FUEL.get(fuel.getItem()).orElse(original);
	}
}
