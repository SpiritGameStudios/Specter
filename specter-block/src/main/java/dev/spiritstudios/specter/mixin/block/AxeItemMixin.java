package dev.spiritstudios.specter.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@Shadow
	@Final
	protected static Map<Block, Block> STRIPPED_BLOCKS;

	@WrapOperation(method = "tryStrip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/AxeItem;getStrippedState(Lnet/minecraft/block/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> getStrippedState(AxeItem instance, BlockState state, Operation<Optional<BlockState>> original) {
		return Optional.ofNullable(STRIPPED_BLOCKS.get(state.getBlock())).map(block -> block.getStateWithProperties(state));
	}
}
