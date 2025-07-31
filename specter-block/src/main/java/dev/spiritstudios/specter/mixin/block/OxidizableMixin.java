package dev.spiritstudios.specter.mixin.block;

import java.util.Optional;

import com.google.common.collect.BiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import dev.spiritstudios.specter.api.block.BlockMetatags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;

import dev.spiritstudios.specter.impl.block.SpecterBlock;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
	@WrapOperation(method = "getIncreasedOxidationBlock", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Block> getIncreasedOxidationBlock(
		Object value,
		Operation<Optional<Block>> original,
		@Local(argsOnly = true) Block block
	) {
		return BlockMetatags.OXIDIZABLE.get(block)
				.or(() -> original.call(value));
	}

	@WrapOperation(method = "getDecreasedOxidationBlock", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Block> getDecreasedOxidationBlock(
		Object value,
		Operation<Optional<Block>> original,
		@Local(argsOnly = true) Block block
	) {
		return Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get(block))
				.or(() -> original.call(value));
	}

	@WrapOperation(method = "getUnaffectedOxidationBlock", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
	private static <K, V> Object getUnaffectedOxidationBlock(BiMap<K, V> instance, Object value, Operation<Object> original) {
		//noinspection SuspiciousMethodCalls
		return Optional.<Object>ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get(value))
				.orElseGet(() -> original.call(instance, value));
	}
}
