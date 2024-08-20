package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.spiritstudios.specter.impl.item.SpecterItem;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ComposterBlock.ComposterInventory.class)
public class ComposterBlock$ComposterInventoryMixin {
	@WrapOperation(method = "canInsert", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z"))
	private boolean canInsert(Object2FloatMap<ItemConvertible> instance, Object o, Operation<Boolean> original) {
		ItemConvertible itemConvertible = (ItemConvertible) o;
		if (SpecterItem.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemConvertible)) return true;

		return original.call(instance, o);
	}
}