package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.api.item.ItemMetatags;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	@ModifyReturnValue(method = "canUseAsFuel", at = @At("RETURN"))
	private static boolean canUseAsFuel(boolean original, @Local(argsOnly = true) ItemStack stack) {
		if (stack.isEmpty()) return original;

		boolean hasFuelMetatag = ItemMetatags.FUEL.get(stack.getItem()).isPresent();
		return original || hasFuelMetatag;
	}

	@ModifyReturnValue(method = "getFuelTime", at = @At("RETURN"))
	private int getFuelTime(int original, @Local(argsOnly = true) ItemStack fuel) {
		if (fuel.isEmpty()) return original;
		return ItemMetatags.FUEL.get(fuel.getItem()).orElse(original);
	}
}
