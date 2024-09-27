package dev.spiritstudios.specter.api.serialization.jsonc;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link JsonObject} with comments.
 */
public class JsonCObject extends JsonCElement {
	private final Map<String, JsonCElement> members = new LinkedHashMap<>();

	public JsonCObject(Map<String, JsonCElement> members, String... comments) {
		super(new JsonObject(), comments);
		this.members.putAll(members);
	}

	public JsonCObject(JsonObject object, String... comments) {
		super(object, comments);

		object.entrySet().forEach(entry -> members.put(
			entry.getKey(),
			entry.getValue().isJsonObject() ?
				new JsonCObject(entry.getValue().getAsJsonObject()) :
				new JsonCElement(entry.getValue())
		));
	}

	public JsonCObject(String... comments) {
		super(new JsonObject(), comments);
	}

	public Map<String, JsonCElement> members() {
		return ImmutableMap.copyOf(members);
	}

	public void put(String key, JsonCElement value) {
		members.put(key, value);
	}

	public void putAll(Map<String, JsonCElement> members) {
		this.members.putAll(members);
	}
}
