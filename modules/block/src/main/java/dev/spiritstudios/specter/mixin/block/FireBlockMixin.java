package dev.spiritstudios.specter.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.block.FlammableBlockData;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {
	@WrapOperation(method = "getIgniteOdds(Lnet/minecraft/world/level/block/state/BlockState;)I", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;getInt(Ljava/lang/Object;)I", remap = false))
	private int getBurnChanceFromMetatag(
			Object2IntMap<Block> instance,
			Object value,
			Operation<Integer> original,
			@Local(argsOnly = true) BlockState state
	) {
		return BlockMetatags.FLAMMABLE.get((state).getBlock())
				.map(FlammableBlockData::igniteOdds)
				.orElseGet(() -> original.call(instance, value));
	}

	@WrapOperation(method = "getBurnOdds", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;getInt(Ljava/lang/Object;)I", remap = false))
	private int getSpreadChanceFromMetatag(
			Object2IntMap<Block> instance,
			Object value,
			Operation<Integer> original,
			@Local(argsOnly = true) BlockState state
	) {
		return BlockMetatags.FLAMMABLE.get((state).getBlock())
				.map(FlammableBlockData::burnOdds)
				.orElseGet(() -> original.call(instance, value));
	}
}
