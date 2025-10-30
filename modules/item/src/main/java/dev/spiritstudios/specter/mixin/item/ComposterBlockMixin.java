package dev.spiritstudios.specter.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

import dev.spiritstudios.specter.api.item.ItemMetatags;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {
	@WrapOperation(method = "addItem", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;getFloat(Ljava/lang/Object;)F", remap = false))
	private static float addToComposter(Object2FloatMap<ItemLike> instance, Object o, Operation<Float> original) {
		return ItemMetatags.COMPOSTING_CHANCE.get(((ItemLike) o).asItem())
				.orElseGet(() -> original.call(instance, o));
	}

	@WrapOperation(method = {"insertItem", "useItemOn"}, at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2FloatMap;containsKey(Ljava/lang/Object;)Z", remap = false))
	private static boolean compost(Object2FloatMap<ItemLike> instance, Object o, Operation<Boolean> original) {
		ItemLike itemConvertible = (ItemLike) o;
		return ItemMetatags.COMPOSTING_CHANCE.containsKey(itemConvertible.asItem()) || original.call(instance, o);
	}
}
