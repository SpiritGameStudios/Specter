package dev.spiritstudios.specter.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.spiritstudios.specter.api.block.BlockMetatags;
import net.minecraft.block.Block;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

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
