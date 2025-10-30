package dev.spiritstudios.specter.impl.registry.metatag.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;
import dev.spiritstudios.specter.impl.core.Specter;

public class MetatagContent<R, V> {
	private final ResourceKey<Registry<R>> registryKey;
	private final Metatag<R, V> metatag;
	private final ObjectArrayList<Pair<R, V>> values;
	private final Codec<MetatagResource<R, V>> resourceCodec;

	public MetatagContent(ResourceKey<Registry<R>> registryKey, Metatag<R, V> metatag) {
		this.registryKey = registryKey;
		this.metatag = metatag;
		this.values = new ObjectArrayList<>();
		this.resourceCodec = MetatagResource.resourceCodecOf(this.metatag);
	}

	public Metatag<R, V> getMetatag() {
		return metatag;
	}

	public List<Pair<R, V>> getValues() {
		return Collections.unmodifiableList(this.values);
	}

	public void parseAndAddResource(HolderLookup.Provider wrapperLookup, ResourceLocation id, Resource resource) {
		try (BufferedReader resourceReader = resource.openAsReader()) {
			DataResult<MetatagResource<R, V>> result = resourceCodec.parse(JsonOps.INSTANCE, JsonParser.parseReader(resourceReader));

			if (result.error().isPresent()) {
				Specter.LOGGER.error("Couldn't parse metatag file '{}': {}", id, result.error().get());
				return;
			}

			MetatagResource<R, V> parsed = result.getOrThrow();
			if (parsed.replace()) {
				this.values.clear();
				this.values.trim(parsed.entries().size());
			}

			HolderGetter<R> lookup = wrapperLookup.lookupOrThrow(registryKey);

			for (Pair<ResourceKey<R>, V> pair : parsed.entries()) {
				lookup.get(pair.getFirst()).ifPresent(entry -> {
					values.add(Pair.of(entry.value(), pair.getSecond()));
				});
			}
		} catch (IOException e) {
			throw new JsonSyntaxException("Failed to read metatag %s from resource.".formatted(id));
		}
	}
}
