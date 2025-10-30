package dev.spiritstudios.specter.mixin.entity;

import java.util.Map;
import java.util.Optional;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

import dev.spiritstudios.specter.api.entity.DataAttributeContainer;
import dev.spiritstudios.specter.api.entity.EntityMetatags;
import dev.spiritstudios.specter.impl.entity.DataAttributeContainerImpl;

@Mixin(DefaultAttributes.class)
public abstract class DefaultAttributeRegistryMixin {
	@ModifyReturnValue(method = "getSupplier", at = @At("RETURN"))
	private static AttributeSupplier get(AttributeSupplier original, @Local(argsOnly = true) EntityType<? extends LivingEntity> type) {
		Optional<AttributeSupplier> originalAttributes = Optional.ofNullable(original);
		return EntityMetatags.DEFAULT_ATTRIBUTES.get(type)
				.map(builder ->
						originalAttributes
								.<DataAttributeContainer>map(attributes -> DataAttributeContainerImpl.with(builder, attributes))
								.orElse(builder)
								.build()
				).orElse(original);
	}

	@WrapOperation(method = "hasSupplier", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
	private static <K, V> boolean containsKey(Map<K, V> instance, Object o, Operation<Boolean> original) {
		if (!(o instanceof EntityType<?> entityType)) return original.call(instance, o);
		return EntityMetatags.DEFAULT_ATTRIBUTES.containsKey(entityType) || original.call(instance, o);
	}
}
