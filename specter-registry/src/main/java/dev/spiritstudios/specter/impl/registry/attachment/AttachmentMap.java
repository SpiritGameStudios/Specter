package dev.spiritstudios.specter.impl.registry.attachment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AttachmentMap<R, V> {
	private final Registry<R> registry;
	private final Attachment<R, V> attachment;
	private final Map<Identifier, Object> values;

	public AttachmentMap(Registry<R> registry, Attachment<R, V> attachment) {
		this.registry = registry;
		this.attachment = attachment;
		this.values = new HashMap<>();
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

	public void processResource(Identifier id, Resource resource) {
		try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
			JsonObject obj = JsonHelper.deserialize(reader);
			boolean replace = JsonHelper.getBoolean(obj, "replace", false);
			JsonElement values = obj.get("values");

			if (values == null)
				throw new JsonSyntaxException("Attachment " + id + " does not have a 'values' field.");
			else if (!values.isJsonObject())
				throw new JsonSyntaxException("Attachment " + id + " 'values' field must be an object.");

			if (replace) this.values.clear();

			this.processObject(id, values.getAsJsonObject());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void processObject(Identifier id, JsonObject values) {
		for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
			String idString = entry.getKey();
			Identifier valueId = Identifier.tryParse(idString);

			if (valueId == null)
				throw new JsonSyntaxException("Failed to parse key for attachment " + id + " with key " + idString);

			JsonElement value = entry.getValue();
			this.processEntry(id, valueId, value);
		}
	}

	private void processEntry(Identifier id, Identifier keyId, JsonElement value) {
		if (!this.registry.containsId(keyId)) return;

		DataResult<?> result = this.attachment.getCodec().parse(JsonOps.INSTANCE, value);

		if (result.result().isEmpty())
			throw new JsonSyntaxException("Failed to parse value for attachment " + id + " with key " + keyId);

		Object valueObject = result.result().get();
		this.put(keyId, valueObject);
	}
}
