package dev.spiritstudios.specter.impl.registry.metatag.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.data.MetatagResource;

public class MetatagContent<R, V> {
	private final Registry<R> registry;
	private final Metatag<R, V> metatag;
	private final Object2ObjectOpenHashMap<Identifier, V> values;
	private final Codec<MetatagResource<V>> resourceCodec;

	public MetatagContent(Registry<R> registry, Metatag<R, V> metatag) {
		this.registry = registry;
		this.metatag = metatag;
		this.values = new Object2ObjectOpenHashMap<>();
		this.resourceCodec = MetatagResource.resourceCodecOf(this.metatag);
	}

	public void put(Identifier id, V value) {
		values.put(id, value);
	}

	public Registry<R> getRegistry() {
		return registry;
	}

	public Metatag<R, V> getMetatag() {
		return metatag;
	}

	public Map<Identifier, V> getValues() {
		return Collections.unmodifiableMap(this.values);
	}

	public void parseAndAddResource(Identifier id, Resource resource) {
		try (InputStreamReader resourceReader = new InputStreamReader(resource.getInputStream())) {
			JsonObject resourceJson = JsonHelper.deserialize(resourceReader);

			MetatagResource<V> parsed = resourceCodec.parse(JsonOps.INSTANCE, resourceJson).getOrThrow();
			if (parsed.replace()) {
				this.values.clear();
				this.values.trim(parsed.entries().size());
			}

			parsed.entries().stream()
				.filter(pair -> this.registry.containsId(pair.getFirst()))
				.forEach(pair -> this.put(pair.getFirst(), pair.getSecond()));
		} catch (IOException e) {
			throw new JsonSyntaxException("Failed to read metatag %s from resource.".formatted(id));
		}
	}
}
