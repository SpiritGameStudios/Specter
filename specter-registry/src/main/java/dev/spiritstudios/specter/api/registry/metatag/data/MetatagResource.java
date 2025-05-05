package dev.spiritstudios.specter.api.registry.metatag.data;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public record MetatagResource<V>(boolean replace, List<Pair<Identifier, V>> entries) {
	public static <V> Codec<MetatagResource<V>> resourceCodecOf(Metatag<?, V> metatag) {
		return RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(MetatagResource::replace),
			Codec.compoundList(
				Identifier.CODEC,
				metatag.codec()
			).fieldOf("values").forGetter(MetatagResource::entries)
		).apply(instance, MetatagResource::new));
	}
}
