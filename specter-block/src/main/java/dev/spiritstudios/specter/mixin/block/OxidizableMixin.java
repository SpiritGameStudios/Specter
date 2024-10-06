package dev.spiritstudios.specter.mixin.block;

import com.google.common.collect.BiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.impl.block.SpecterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
	@WrapOperation(method = "getIncreasedOxidationBlock", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Object> getIncreasedOxidationBlock(
		Object value,
		Operation<Optional<Object>> original,
		@Local(argsOnly = true) Block block
	) {
		Optional<Object> increasedOxidationBlock = Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_INCREASES.get(block));
		return increasedOxidationBlock.or(() -> original.call(value));
	}

	@WrapOperation(method = "getDecreasedOxidationBlock", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Object> getDecreasedOxidationBlock(
		Object value,
		Operation<Optional<Object>> original,
		@Local(argsOnly = true) Block block
	) {
		Optional<Object> decreasedOxidationBlock = Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get(block));
		return decreasedOxidationBlock.or(() -> original.call(value));
	}

	@WrapOperation(method = "getUnaffectedOxidationBlock", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
	private static <K, V> Object getUnaffectedOxidationBlock(BiMap<K, V> instance, Object value, Operation<Object> original) {
		Optional<Object> unaffectedOxidationBlock = Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get((Block) value));
		return unaffectedOxidationBlock.orElseGet(() -> original.call(instance, value));
	}
}

