package dev.spiritstudios.specter.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.block.FlammableBlockData;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	@WrapOperation(method = "getBurnChance(Lnet/minecraft/block/BlockState;)I", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;getInt(Ljava/lang/Object;)I", remap = false))
	private int getBurnChanceFromMetatag(
		Object2IntMap<Block> instance,
		Object value,
		Operation<Integer> original,
		@Local(argsOnly = true) BlockState state
	) {
		return BlockMetatags.FLAMMABLE.get((state).getBlock())
			.map(FlammableBlockData::burn)
			.orElseGet(() -> original.call(instance, value));
	}

	@WrapOperation(method = "getSpreadChance(Lnet/minecraft/block/BlockState;)I", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;getInt(Ljava/lang/Object;)I", remap = false))
	private int getSpreadChanceFromMetatag(
		Object2IntMap<Block> instance,
		Object value,
		Operation<Integer> original,
		@Local(argsOnly = true) BlockState state
	) {
		return BlockMetatags.FLAMMABLE.get((state).getBlock())
			.map(FlammableBlockData::spread)
			.orElseGet(() -> original.call(instance, value));
	}
}
