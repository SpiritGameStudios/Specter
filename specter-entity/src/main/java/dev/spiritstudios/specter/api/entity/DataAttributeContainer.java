package dev.spiritstudios.specter.api.entity;

import com.mojang.serialization.Codec;

import dev.spiritstudios.specter.impl.entity.DataAttributeContainerImpl;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public interface DataAttributeContainer {
	Codec<DataAttributeContainer> CODEC = Codec.unboundedMap(
					Registries.ATTRIBUTE.getEntryCodec(),
					Codec.DOUBLE
			)
			.<Object2DoubleMap<RegistryEntry<EntityAttribute>>>xmap(Object2DoubleOpenHashMap::new, Object2DoubleOpenHashMap::new)
			.xmap(DataAttributeContainerImpl::new, DataAttributeContainer::attributes);

	PacketCodec<RegistryByteBuf, DataAttributeContainer> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.map(
					Object2DoubleOpenHashMap::new,
					PacketCodecs.registryEntry(RegistryKeys.ATTRIBUTE),
					PacketCodecs.DOUBLE
			),
			DataAttributeContainer::attributes, DataAttributeContainerImpl::new
	);

	Object2DoubleMap<RegistryEntry<EntityAttribute>> attributes();
	DefaultAttributeContainer build();

	static DataAttributeContainer of(Object2DoubleMap<RegistryEntry<EntityAttribute>> map) {
		return new DataAttributeContainerImpl(map);
	}
}
