package dev.spiritstudios.specter.mixin.block;

import java.util.Map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.item.ShovelItem;

import dev.spiritstudios.specter.api.block.BlockMetatags;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {
	@SuppressWarnings("unchecked")
	@WrapOperation(method = "useOnBlock", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private <V> V get(Map<Block, BlockState> instance, V o, Operation<V> original) {
		return BlockMetatags.FLATTENABLE.get((Block) o)
				.map(blockState -> (V) blockState)
				.orElseGet(() -> original.call(instance, o));
	}
}
