package dev.spiritstudios.specter.mixin.block;

import java.util.Map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import dev.spiritstudios.specter.api.block.BlockMetatags;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {
	@SuppressWarnings("unchecked")
	@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private <V> V get(Map<Block, BlockState> instance, V o, Operation<V> original) {
		return BlockMetatags.FLATTENABLE.get((Block) o)
				.map(blockState -> (V) blockState)
				.orElseGet(() -> original.call(instance, o));
	}
}
