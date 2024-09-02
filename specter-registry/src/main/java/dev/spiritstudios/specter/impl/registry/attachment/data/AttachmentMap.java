package dev.spiritstudios.specter.impl.registry.attachment.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;

public class AttachmentMap<R, V> {
	private final Registry<R> registry;
	private final Attachment<R, V> attachment;
	private final Object2ObjectOpenHashMap<Identifier, V> values;
	private final Codec<AttachmentResource<V>> resourceCodec;

	public AttachmentMap(Registry<R> registry, Attachment<R, V> attachment) {
		this.registry = registry;
		this.attachment = attachment;
		this.values = new Object2ObjectOpenHashMap<>();
		this.resourceCodec = AttachmentResource.resourceCodecOf(this.attachment);
	}

	public void put(Identifier id, V value) {
		values.put(id, value);
	}

	public Registry<R> getRegistry() {
		return registry;
	}

	public Attachment<R, V> getAttachment() {
		return attachment;
	}

	public Map<Identifier, V> getValues() {
		return Collections.unmodifiableMap(this.values);
	}

	public void parseResource(Identifier id, Resource resource) {
		try (InputStreamReader resourceReader = new InputStreamReader(resource.getInputStream())) {
			JsonObject resourceJson = JsonHelper.deserialize(resourceReader);

			AttachmentResource<V> parsed = resourceCodec.parse(JsonOps.INSTANCE, resourceJson).getOrThrow();
			if (parsed.replace()) {
				this.values.clear();
				this.values.trim(parsed.entries().size());
			}

			for (Pair<Identifier, V> entry : parsed.entries()) {
				if (!this.registry.containsId(entry.getFirst())) continue;
				this.put(entry.getFirst(), entry.getSecond());
			}

		} catch (IOException e) {
			throw new JsonSyntaxException("Failed to read attachment " + id + " from resource.");
		}
	}
}
