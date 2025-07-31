package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;

import dev.spiritstudios.specter.api.item.ItemMetatags;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {
	@WrapOperation(method = "addToComposter", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;getFloat(Ljava/lang/Object;)F", remap = false))
	private static float addToComposter(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Float> original) {
		return ItemMetatags.COMPOSTING_CHANCE.get(((ItemConvertible) o).asItem())
				.orElseGet(() -> original.call(instance, o));
	}

	@WrapOperation(method = {"compost", "onUseWithItem"}, at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z", remap = false))
	private static boolean compost(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Boolean> original) {
		ItemConvertible itemConvertible = (ItemConvertible) o;
		return ItemMetatags.COMPOSTING_CHANCE.containsKey(itemConvertible.asItem()) || original.call(instance, o);
	}
}
