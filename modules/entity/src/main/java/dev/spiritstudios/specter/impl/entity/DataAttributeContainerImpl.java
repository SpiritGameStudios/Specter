package dev.spiritstudios.specter.impl.entity;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import dev.spiritstudios.specter.api.entity.DataAttributeContainer;
import dev.spiritstudios.specter.mixin.entity.DefaultAttributeContainerAccessor;

// Yes, this is a builder builder. I love codecs.
public record DataAttributeContainerImpl(Object2DoubleMap<Holder<Attribute>> attributes) implements DataAttributeContainer {
	public static DataAttributeContainerImpl with(DataAttributeContainer original, AttributeSupplier attributes) {
		Object2DoubleMap<Holder<Attribute>> newAttributes = new Object2DoubleOpenHashMap<>();

		((DefaultAttributeContainerAccessor) attributes)
				.getInstances()
				.forEach((attribute, value) ->
						newAttributes.put(attribute, value.getBaseValue()));

		newAttributes.putAll(original.attributes());
		return new DataAttributeContainerImpl(newAttributes);
	}

	public AttributeSupplier build() {
		AttributeSupplier.Builder builder = AttributeSupplier.builder();
		attributes.forEach(builder::add);
		return builder.build();
	}
}
