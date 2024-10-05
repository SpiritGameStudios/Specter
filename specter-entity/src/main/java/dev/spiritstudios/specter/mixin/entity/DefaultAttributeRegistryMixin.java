package dev.spiritstudios.specter.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.specter.api.entity.EntityMetatags;
import dev.spiritstudios.specter.impl.entity.DataDefaultAttributeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(DefaultAttributeRegistry.class)
public class DefaultAttributeRegistryMixin {
	@ModifyReturnValue(method = "get", at = @At("RETURN"))
	private static DefaultAttributeContainer get(DefaultAttributeContainer original, @Local(argsOnly = true) EntityType<? extends LivingEntity> type) {
		Optional<DefaultAttributeContainer> originalAttributes = Optional.ofNullable(original);
		return EntityMetatags.DEFAULT_ATTRIBUTES.get(type)
			.map(builder ->
				originalAttributes
					.map(attributes -> DataDefaultAttributeBuilder.with(builder, attributes))
					.orElse(builder)
					.build()
			).orElse(original);
	}

	@WrapOperation(method = "hasDefinitionFor", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
	private static <K, V> boolean containsKey(Map<K, V> instance, Object o, Operation<Boolean> original) {
		if (!(o instanceof EntityType<?> entityType)) return original.call(instance, o);
		return Objects.nonNull(EntityMetatags.DEFAULT_ATTRIBUTES.get(entityType)) || original.call(instance, o);
	}
}
