package dev.spiritstudios.specter.impl.registry.attachment.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class AttachmentMap<R, V> {
	private final Registry<R> registry;
	private final Attachment<R, V> attachment;
	private final Map<Identifier, Object> values;

	public AttachmentMap(Registry<R> registry, Attachment<R, V> attachment) {
		this.registry = registry;
		this.attachment = attachment;
		this.values = new Object2ObjectOpenHashMap<>();
	}

	public void put(Identifier id, Object value) {
		values.put(id, value);
	}

	public Registry<R> getRegistry() {
		return registry;
	}

	public Attachment<R, V> getAttachment() {
		return attachment;
	}

	public Map<Identifier, Object> getValues() {
		return values;
	}

	public void parseResource(Identifier id, Resource resource) {
		try (InputStreamReader resourceReader = new InputStreamReader(resource.getInputStream())) {
			JsonObject resourceJson = JsonHelper.deserialize(resourceReader);

			boolean replace = JsonHelper.getBoolean(resourceJson, "replace", false);
			JsonElement entryValues = resourceJson.get("values");

			if (entryValues == null)
				throw new JsonSyntaxException("Attachment " + id + " does not have a 'entryValues' field.");

			if (!entryValues.isJsonObject())
				throw new JsonSyntaxException("Attachment " + id + " 'entryValues' field must be an object.");

			if (replace) this.values.clear();

			for (Map.Entry<String, JsonElement> entry : entryValues.getAsJsonObject().entrySet()) {
				String entryIdString = entry.getKey();
				Identifier entryId = Identifier.tryParse(entryIdString);

				if (entryId == null)
					throw new JsonSyntaxException("Failed to parse key for attachment %s with key %s.".formatted(id, entryIdString));

				JsonElement entryValue = entry.getValue();
				if (!this.registry.containsId(entryId)) return;

				DataResult<?> decodedEntry = this.attachment.getCodec().parse(JsonOps.INSTANCE, entryValue);

				if (decodedEntry.result().isEmpty())
					throw new JsonSyntaxException("Failed to parse value for attachment %s with key %s.".formatted(id, entryIdString));

				this.put(entryId, decodedEntry.result().get());
			}
		} catch (IOException e) {
			throw new JsonSyntaxException("Failed to read attachment " + id + " from resource.");
		}
	}
}
