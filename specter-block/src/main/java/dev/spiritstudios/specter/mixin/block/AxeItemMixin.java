package dev.spiritstudios.specter.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.impl.block.SpecterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {
	@Shadow
	@Final
	public static Map<Block, Block> STRIPPED_BLOCKS;

	@WrapOperation(method = "tryStrip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/AxeItem;getStrippedState(Lnet/minecraft/block/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> getStrippedState(AxeItem instance, BlockState state, Operation<Optional<BlockState>> original) {
		Optional<Block> strippedBlock = BlockMetatags.STRIPPABLE.get(state.getBlock());
		if (strippedBlock.isEmpty()) strippedBlock = Optional.ofNullable(STRIPPED_BLOCKS.get(state.getBlock()));

		return strippedBlock.map(block -> block.getStateWithProperties(state));
	}

	@Inject(method = "tryStrip", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"), cancellable = true)
	private void tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
		Optional<BlockState> unwaxedBlockState = Optional.ofNullable(SpecterBlock.WAXED_TO_UNWAXED_BLOCKS.get(state.getBlock())).map(unwaxed -> unwaxed.getStateWithProperties(state));
		if (unwaxedBlockState.isPresent()) cir.setReturnValue(unwaxedBlockState);
	}
}
