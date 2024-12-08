package dev.spiritstudios.specter.api.serialization.jsonc;

import com.google.gson.*;
import dev.spiritstudios.specter.api.serialization.format.JsonCFormat;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * A JSON writer that supports comments.
 *
 * @see JsonCFormat
 */
public class JsonCWriter implements Closeable, Flushable {
	private final Writer writer;
	private final Deque<Boolean> stack = new ArrayDeque<>();
	private List<String> comments;
	private String key;

	public JsonCWriter(Writer out) {
		this.writer = Objects.requireNonNull(out);
	}

	/**
	 * Writes the given {@link JsonCElement} to the underlying writer.
	 *
	 * @param element The element to write.
	 */
	public void write(JsonCElement element) throws IOException {
		comments = element.comments();

		if (element instanceof JsonCObject object) {
			begin("{");
			for (Map.Entry<String, JsonCElement> entry : object.members().entrySet()) {
				key = entry.getKey();
				write(entry.getValue());
			}
			end("}");
			return;
		}

		switch (element.element()) {
			case JsonNull ignored -> value("\"null\"");
			case JsonPrimitive primitive -> {
				if (primitive.isNumber()) value(primitive.getAsNumber().toString());
				else if (primitive.isBoolean()) value(primitive.getAsBoolean() ? "true" : "false");
				else value('"' + primitive.getAsString() + '"');
			}
			case JsonArray array -> {
				begin("[");
				for (JsonElement value : array) write(new JsonCElement(value));
				end("]");
			}
			case JsonObject object -> {
				begin("{");
				for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
					key = entry.getKey();
					write(new JsonCElement(entry.getValue()));
				}
				end("}");
			}
			default -> throw new IllegalArgumentException("Unsupported JsonElement type: " + element.element());
		}
	}

	private void begin(String character) throws IOException {
		writeSeparator();
		writeComments();
		writeKey();
		writer.append(character).append("\n");

		stack.push(false);
	}

	private void end(String character) throws IOException {
		if (peek()) writer.append("\n");
		stack.pop();
		if (!stack.isEmpty()) {
			stack.pop();
			stack.push(true);
		}

		indent();
		writer.append(character);
	}

	private void writeKey() throws IOException {
		indent();

		if (key == null) return;
		writer.append('"').append(key).append("\": ");
		key = null;
	}

	private void writeSeparator() throws IOException {
		if (peek()) writer.append(",\n");
	}

	private void writeComments() throws IOException {
		if (comments == null) return;
		for (String comment : comments) {
			indent();
			writer.append("// ").append(comment).append("\n");
		}
		this.comments = null;
	}

	private void value(String value) throws IOException {
		writeSeparator();
		writeComments();
		writeKey();
		writer.append(value);
		stack.pop();
		stack.push(true);
	}

	private void indent() throws IOException {
		writer.append("    ".repeat(stack.size()));
	}

	private boolean peek() {
		return Optional.ofNullable(stack.peek()).orElse(false);
	}

	@Override
	public String toString() {
		return writer.toString();
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}
}
