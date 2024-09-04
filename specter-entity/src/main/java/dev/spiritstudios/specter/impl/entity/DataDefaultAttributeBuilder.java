package dev.spiritstudios.specter.impl.entity;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Map;

// Yes, this is a builder builder. I love codecs.
public record DataDefaultAttributeBuilder(Map<RegistryEntry<EntityAttribute>, Double> attributes) {
	public DefaultAttributeContainer build() {
		DefaultAttributeContainer.Builder builder = DefaultAttributeContainer.builder();
		attributes.forEach(builder::add);
		return builder.build();
	}

	public static final Codec<DataDefaultAttributeBuilder> CODEC = Codec.unboundedMap(
		Registries.ATTRIBUTE.getEntryCodec(),
		Codec.DOUBLE
	).xmap(DataDefaultAttributeBuilder::new, DataDefaultAttributeBuilder::attributes);

	public static final PacketCodec<RegistryByteBuf, DataDefaultAttributeBuilder> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.map(
			Object2DoubleOpenHashMap::new,
			PacketCodecs.registryEntry(RegistryKeys.ATTRIBUTE),
			PacketCodecs.DOUBLE
		),
		DataDefaultAttributeBuilder::attributes,
		DataDefaultAttributeBuilder::new
	);

	public static DataDefaultAttributeBuilder with(DataDefaultAttributeBuilder original, DefaultAttributeContainer attributes) {
		Map<RegistryEntry<EntityAttribute>, Double> newAttributes = new Object2DoubleOpenHashMap<>();
		attributes.instances.forEach((attribute, value) -> newAttributes.put(attribute, value.getBaseValue()));
		newAttributes.putAll(original.attributes);
		return new DataDefaultAttributeBuilder(newAttributes);
	}
}
