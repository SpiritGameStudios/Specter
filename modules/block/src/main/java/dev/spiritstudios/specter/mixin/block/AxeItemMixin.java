package dev.spiritstudios.specter.mixin.block;

import java.util.Optional;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.impl.block.SpecterBlock;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {
	@WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AxeItem;getStripped(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> getStrippedState(AxeItem instance, BlockState state, Operation<Optional<BlockState>> original) {
		return BlockMetatags.STRIPPABLE.get(state.getBlock())
				.map(block -> block.withPropertiesOf(state))
				.or(() -> original.call(instance, state));
	}

	@WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private Optional<BlockState> tryStrip(
			Object value,
			Operation<Optional<BlockState>> original,
			@Local(argsOnly = true) BlockState state
	) {
		return Optional.ofNullable(SpecterBlock.WAXED_TO_UNWAXED_BLOCKS.get(state.getBlock()))
				.map(block -> block.withPropertiesOf(state))
				.or(() -> original.call(value));
	}
}
