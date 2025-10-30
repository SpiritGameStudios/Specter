package dev.spiritstudios.specter.api.entity;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import dev.spiritstudios.specter.impl.entity.DataAttributeContainerImpl;

public interface DataAttributeContainer {
	Codec<DataAttributeContainer> CODEC = Codec.unboundedMap(
					BuiltInRegistries.ATTRIBUTE.holderByNameCodec(),
					Codec.DOUBLE
			)
			.<Object2DoubleMap<Holder<Attribute>>>xmap(Object2DoubleOpenHashMap::new, Object2DoubleOpenHashMap::new)
			.xmap(DataAttributeContainerImpl::new, DataAttributeContainer::attributes);

	StreamCodec<RegistryFriendlyByteBuf, DataAttributeContainer> PACKET_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(
					Object2DoubleOpenHashMap::new,
					ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE),
					ByteBufCodecs.DOUBLE
			),
			DataAttributeContainer::attributes, DataAttributeContainerImpl::new
	);

	Object2DoubleMap<Holder<Attribute>> attributes();
	AttributeSupplier build();

	static DataAttributeContainer of(Object2DoubleMap<Holder<Attribute>> map) {
		return new DataAttributeContainerImpl(map);
	}
}
