package dev.spiritstudios.specter.api.serialization.jsonc;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import dev.spiritstudios.specter.api.serialization.Commentable;

/**
 * A {@link JsonElement} with comments.
 */
public class JsonCElement implements Commentable {
	public static final JsonCElement NULL = new JsonCElement(JsonNull.INSTANCE);

	private final JsonElement element;
	private List<String> comments;

	public JsonCElement(JsonElement element, String... comments) {
		this.element = element;
		this.comments = List.of(comments);
	}

	public JsonCElement(JsonElement element, List<String> comments) {
		this.element = element;
		this.comments = comments;
	}

	public JsonElement element() {
		return element;
	}

	@Override
	public List<String> comments() {
		return comments;
	}

	@Override
	public void setComments(List<String> comments) {
		this.comments = comments;
	}
}
