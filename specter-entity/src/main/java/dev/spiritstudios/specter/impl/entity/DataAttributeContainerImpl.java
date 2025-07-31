package dev.spiritstudios.specter.impl.entity;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;

import dev.spiritstudios.specter.api.entity.DataAttributeContainer;
import dev.spiritstudios.specter.mixin.entity.DefaultAttributeContainerAccessor;

// Yes, this is a builder builder. I love codecs.
public record DataAttributeContainerImpl(Object2DoubleMap<RegistryEntry<EntityAttribute>> attributes) implements DataAttributeContainer {
	public static DataAttributeContainerImpl with(DataAttributeContainer original, DefaultAttributeContainer attributes) {
		Object2DoubleMap<RegistryEntry<EntityAttribute>> newAttributes = new Object2DoubleOpenHashMap<>();

		((DefaultAttributeContainerAccessor) attributes)
				.getInstances()
				.forEach((attribute, value) ->
						newAttributes.put(attribute, value.getBaseValue()));

		newAttributes.putAll(original.attributes());
		return new DataAttributeContainerImpl(newAttributes);
	}

	public DefaultAttributeContainer build() {
		DefaultAttributeContainer.Builder builder = DefaultAttributeContainer.builder();
		attributes.forEach(builder::add);
		return builder.build();
	}
}
