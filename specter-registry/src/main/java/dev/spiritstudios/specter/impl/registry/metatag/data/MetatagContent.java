package dev.spiritstudios.specter.impl.registry.metatag.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;

public class MetatagContent<R, V> {
	private final RegistryKey<Registry<R>> registryKey;
	private final Metatag<R, V> metatag;
	private final ObjectArrayList<Pair<RegistryEntry.Reference<R>, V>> values;
	private final Codec<MetatagResource<R, V>> resourceCodec;

	public MetatagContent(RegistryKey<Registry<R>> registryKey, Metatag<R, V> metatag) {
		this.registryKey = registryKey;
		this.metatag = metatag;
		this.values = new ObjectArrayList<>();
		this.resourceCodec = MetatagResource.resourceCodecOf(this.metatag);
	}

	public Metatag<R, V> getMetatag() {
		return metatag;
	}

	public List<Pair<RegistryEntry.Reference<R>, V>> getValues() {
		return Collections.unmodifiableList(this.values);
	}

	public void parseAndAddResource(RegistryWrapper.WrapperLookup wrapperLookup, Identifier id, Resource resource) {
		try (InputStreamReader resourceReader = new InputStreamReader(resource.getInputStream())) {
			JsonObject resourceJson = JsonHelper.deserialize(resourceReader);

			MetatagResource<R, V> parsed = resourceCodec.parse(JsonOps.INSTANCE, resourceJson).getOrThrow();
			if (parsed.replace()) {
				this.values.clear();
				this.values.trim(parsed.entries().size());
			}

			RegistryEntryLookup<R> lookup = wrapperLookup.getOrThrow(registryKey);

			for (Pair<RegistryKey<R>, V> pair : parsed.entries()) {
				lookup.getOptional(pair.getFirst()).ifPresent(entry -> {
					values.add(Pair.of(entry, pair.getSecond()));
				});
			}

		} catch (IOException e) {
			throw new JsonSyntaxException("Failed to read metatag %s from resource.".formatted(id));
		}
	}
}
