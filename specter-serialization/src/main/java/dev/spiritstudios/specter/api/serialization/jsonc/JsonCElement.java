package dev.spiritstudios.specter.api.serialization.jsonc;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import dev.spiritstudios.specter.api.serialization.Commentable;

/**
 * A {@link JsonElement} with comments.
 */
public class JsonCElement implements Commentable {
	public static final JsonCElement NULL = new JsonCElement(JsonNull.INSTANCE);

	private final JsonElement element;
	private String[] comments;

	public JsonCElement(JsonElement element, String... comments) {
		this.element = element;
		this.comments = comments;
	}

	public JsonElement element() {
		return element;
	}

	@Override
	public String[] comments() {
		return comments;
	}

	@Override
	public void setComments(String... comments) {
		this.comments = comments;
	}
}
