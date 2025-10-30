package dev.spiritstudios.specter.api.registry.metatag.data;

import java.util.List;
import net.minecraft.resources.ResourceKey;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;

public record MetatagResource<R, V>(boolean replace, List<Pair<ResourceKey<R>, V>> entries) {
	public static <R, V> Codec<MetatagResource<R, V>> resourceCodecOf(Metatag<R, V> metatag) {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("replace", false).forGetter(MetatagResource::replace),
				Codec.compoundList(
						ResourceKey.codec(metatag.registryKey()),
						metatag.codec()
				).fieldOf("values").forGetter(MetatagResource::entries)
		).apply(instance, MetatagResource::new));
	}
}
