package dev.spiritstudios.specter.mixin.block;

import java.util.Optional;

import com.google.common.collect.BiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.impl.block.SpecterBlock;

@Mixin(WeatheringCopper.class)
public interface OxidizableMixin {
	@WrapOperation(method = "getNext(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Block> getIncreasedOxidationBlock(
		Object value,
		Operation<Optional<Block>> original,
		@Local(argsOnly = true) Block block
	) {
		return BlockMetatags.OXIDIZABLE.get(block)
				.or(() -> original.call(value));
	}

	@WrapOperation(method = "getPrevious(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ofNullable(Ljava/lang/Object;)Ljava/util/Optional;"))
	private static Optional<Block> getDecreasedOxidationBlock(
		Object value,
		Operation<Optional<Block>> original,
		@Local(argsOnly = true) Block block
	) {
		return Optional.ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get(block))
				.or(() -> original.call(value));
	}

	@WrapOperation(method = "getFirst(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/BiMap;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
	private static <K, V> Object getUnaffectedOxidationBlock(BiMap<K, V> instance, Object value, Operation<Object> original) {
		//noinspection SuspiciousMethodCalls
		return Optional.<Object>ofNullable(SpecterBlock.OXIDATION_LEVEL_DECREASES.get(value))
				.orElseGet(() -> original.call(instance, value));
	}
}
