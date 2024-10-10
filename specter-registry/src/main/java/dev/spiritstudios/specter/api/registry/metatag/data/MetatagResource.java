package dev.spiritstudios.specter.api.registry.metatag.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import net.minecraft.util.Identifier;

import java.util.List;

public record MetatagResource<V>(boolean replace, List<Pair<Identifier, V>> entries) {
	public static <V> Codec<MetatagResource<V>> resourceCodecOf(Metatag<?, V> metatag) {
		return RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(MetatagResource::replace),
			Codec.compoundList(
				Identifier.CODEC,
				metatag.getCodec()
			).fieldOf("values").forGetter(MetatagResource::entries)
		).apply(instance, MetatagResource::new));
	}
}
