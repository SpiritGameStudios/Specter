package dev.spiritstudios.specter.api.registry.metatag.data;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.registry.RegistryKey;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public record MetatagResource<R, V>(boolean replace, List<Pair<RegistryKey<R>, V>> entries) {
	public static <R, V> Codec<MetatagResource<R, V>> resourceCodecOf(Metatag<R, V> metatag) {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("replace", false).forGetter(MetatagResource::replace),
				Codec.compoundList(
						RegistryKey.createCodec(metatag.registryKey()),
						metatag.codec()
				).fieldOf("values").forGetter(MetatagResource::entries)
		).apply(instance, MetatagResource::new));
	}
}
