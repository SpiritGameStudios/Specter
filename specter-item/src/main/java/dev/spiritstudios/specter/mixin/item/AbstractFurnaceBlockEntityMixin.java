package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.api.item.ItemAttachments;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	@Inject(method = "getFuelTime", at = @At("HEAD"), cancellable = true)
	private void getFuelTime(ItemStack fuel, CallbackInfoReturnable<Integer> cir) {
		if (fuel.isEmpty()) return;

		Optional<Integer> fuelTime = ItemAttachments.FUEL.get(fuel.getItem());
		fuelTime.ifPresent(cir::setReturnValue);
	}

	@ModifyReturnValue(method = "canUseAsFuel", at = @At("RETURN"))
	private static boolean canUseAsFuel(boolean original, @Local(argsOnly = true) ItemStack stack) {
		if (stack.isEmpty()) return original;

		boolean hasFuelAttachment = ItemAttachments.FUEL.get(stack.getItem()).isPresent();
		return original || hasFuelAttachment;
	}
}
