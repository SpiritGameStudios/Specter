package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;

import dev.spiritstudios.specter.impl.item.SpecterItem;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {
	@WrapOperation(method = "addToComposter", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;getFloat(Ljava/lang/Object;)F", remap = false))
	private static float addToComposter(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Float> original) {
		ItemConvertible itemConvertible = (ItemConvertible) o;
		if (SpecterItem.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemConvertible))
			return SpecterItem.ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(itemConvertible);

		return original.call(instance, o);
	}

	@WrapOperation(method = "compost", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z", remap = false))
	private static boolean compost(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Boolean> original) {
		ItemConvertible itemConvertible = (ItemConvertible) o;
		if (SpecterItem.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemConvertible)) return true;

		return original.call(instance, o);
	}

	@WrapOperation(method = "onUseWithItem", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z", remap = false))
	private boolean onUseWithItem(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Boolean> original) {
		ItemConvertible itemConvertible = (ItemConvertible) o;
		if (SpecterItem.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemConvertible)) return true;

		return original.call(instance, o);
	}
}
