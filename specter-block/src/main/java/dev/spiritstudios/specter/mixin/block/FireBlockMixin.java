package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.block.FlammableBlockData;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	@Inject(method = "getBurnChance(Lnet/minecraft/block/BlockState;)I", at = @At("HEAD"), cancellable = true)
	private void getBurnChanceFromMetatag(BlockState state, CallbackInfoReturnable<Integer> cir) {
		Optional<FlammableBlockData> data = BlockMetatags.FLAMMABLE.get(state.getBlock());
		data.ifPresent(flammableBlockData -> cir.setReturnValue(flammableBlockData.burn()));
	}

	@Inject(method = "getSpreadChance(Lnet/minecraft/block/BlockState;)I", at = @At("HEAD"), cancellable = true)
	private void getSpreadChanceFromMetatag(BlockState state, CallbackInfoReturnable<Integer> cir) {
		Optional<FlammableBlockData> data = BlockMetatags.FLAMMABLE.get(state.getBlock());
		data.ifPresent(flammableBlockData -> cir.setReturnValue(flammableBlockData.spread()));
	}
}
