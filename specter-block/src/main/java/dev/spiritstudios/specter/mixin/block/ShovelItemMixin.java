package dev.spiritstudios.specter.mixin.block;

import java.util.Map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.item.ShovelItem;

import dev.spiritstudios.specter.api.block.BlockMetatags;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {
	@SuppressWarnings("unchecked")
	@WrapOperation(method = "useOnBlock", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private <K, V> V get(Map<K, V> instance, Object o, Operation<V> original) {
		if (!(o instanceof Block block)) return original.call(instance, o);

		return BlockMetatags.FLATTENABLE.get(block)
			.map(blockState -> (V) blockState)
			.orElseGet(() -> original.call(instance, o));
	}
}
