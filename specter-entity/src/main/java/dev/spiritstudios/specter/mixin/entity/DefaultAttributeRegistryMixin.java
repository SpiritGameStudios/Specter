package dev.spiritstudios.specter.mixin.entity;

import java.util.Map;
import java.util.Optional;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;

import dev.spiritstudios.specter.api.entity.DataAttributeContainer;
import dev.spiritstudios.specter.api.entity.EntityMetatags;
import dev.spiritstudios.specter.impl.entity.DataAttributeContainerImpl;

@Mixin(DefaultAttributeRegistry.class)
public abstract class DefaultAttributeRegistryMixin {
	@ModifyReturnValue(method = "get", at = @At("RETURN"))
	private static DefaultAttributeContainer get(DefaultAttributeContainer original, @Local(argsOnly = true) EntityType<? extends LivingEntity> type) {
		Optional<DefaultAttributeContainer> originalAttributes = Optional.ofNullable(original);
		return EntityMetatags.DEFAULT_ATTRIBUTES.get(type)
				.map(builder ->
						originalAttributes
								.<DataAttributeContainer>map(attributes -> DataAttributeContainerImpl.with(builder, attributes))
								.orElse(builder)
								.build()
				).orElse(original);
	}

	@WrapOperation(method = "hasDefinitionFor", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
	private static <K, V> boolean containsKey(Map<K, V> instance, Object o, Operation<Boolean> original) {
		if (!(o instanceof EntityType<?> entityType)) return original.call(instance, o);
		return EntityMetatags.DEFAULT_ATTRIBUTES.containsKey(entityType) || original.call(instance, o);
	}
}
