package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.impl.block.SpecterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {
	@Inject(method = "getWaxedState", at = @At("HEAD"), cancellable = true)
	private static void getWaxedState(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
		Optional<BlockState> waxedBlockState = Optional.ofNullable(SpecterBlock.UNWAXED_TO_WAXED_BLOCKS.get(state.getBlock())).map(block -> block.getStateWithProperties(state));
		if (waxedBlockState.isPresent()) cir.setReturnValue(waxedBlockState);
	}
}
