package dev.spiritstudios.specter.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.impl.block.SpecterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@WrapOperation(method = "tryStrip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/AxeItem;getStrippedState(Lnet/minecraft/block/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> getStrippedState(AxeItem instance, BlockState state, Operation<Optional<BlockState>> original) {
		return BlockMetatags.STRIPPABLE.get(state.getBlock())
			.map(block -> block.getStateWithProperties(state))
			.or(() -> original.call(instance, state));
	}

	@WrapOperation(method = "tryStrip", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private Optional<Object> tryStrip(
		Object value,
		Operation<Optional<Object>> original,
		@Local(argsOnly = true) BlockState state
	) {
		Optional<Object> unwaxedBlock = Optional.ofNullable(SpecterBlock.WAXED_TO_UNWAXED_BLOCKS.get(state.getBlock()))
			.map(block -> block.getStateWithProperties(state));

		return unwaxedBlock.or(() -> original.call(value));
	}
}
