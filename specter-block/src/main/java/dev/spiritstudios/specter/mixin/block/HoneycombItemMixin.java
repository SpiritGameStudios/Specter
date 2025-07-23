package dev.spiritstudios.specter.mixin.block;

import java.util.Optional;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;

import dev.spiritstudios.specter.impl.block.SpecterBlock;

@Mixin(HoneycombItem.class)
public abstract class HoneycombItemMixin {
	@WrapOperation(method = "getWaxedState", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Object> getWaxedState(
			Object value,
			Operation<Optional<Object>> original,
			@Local(argsOnly = true) BlockState state
	) {
		Optional<Object> waxedBlockState = Optional.ofNullable(SpecterBlock.UNWAXED_TO_WAXED_BLOCKS.get(state.getBlock()));

		return waxedBlockState.or(() -> original.call(value));
	}
}
