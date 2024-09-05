package dev.spiritstudios.specter.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.spiritstudios.specter.api.entity.EntityMetatags;
import dev.spiritstudios.specter.impl.entity.DataDefaultAttributeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Mixin(DefaultAttributeRegistry.class)
public class DefaultAttributeRegistryMixin {
	@SuppressWarnings("unchecked")
	@WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static <K, V> V get(Map<K, V> instance, Object o, Operation<V> original) {
		if (!(o instanceof EntityType<?> entityType)) return original.call(instance, o);

		Optional<DataDefaultAttributeBuilder> attributeBuilder = EntityMetatags.DEFAULT_ATTRIBUTES.get(entityType);
		if (attributeBuilder.isEmpty()) return original.call(instance, o);

		DefaultAttributeContainer originalAttributes = (DefaultAttributeContainer) original.call(instance, o);
		if (originalAttributes == null) return (V) attributeBuilder.get().build();

		return (V) DataDefaultAttributeBuilder.with(attributeBuilder.get(), originalAttributes).build();
	}

	@WrapOperation(method = "hasDefinitionFor", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
	private static <K, V> boolean containsKey(Map<K, V> instance, Object o, Operation<Boolean> original) {
		if (!(o instanceof EntityType<?> entityType)) return original.call(instance, o);

		boolean hasDefinition = Objects.nonNull(EntityMetatags.DEFAULT_ATTRIBUTES.get(entityType));
		return hasDefinition || original.call(instance, o);
	}
}
