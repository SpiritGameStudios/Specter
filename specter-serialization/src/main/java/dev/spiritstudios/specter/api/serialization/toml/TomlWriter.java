package dev.spiritstudios.specter.api.serialization.toml;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.serialization.format.TomlFormat;
import org.tomlj.Toml;

import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A writer for TOML files, with added comment support.
 *
 * @see TomlFormat
 */
public class TomlWriter implements AutoCloseable, Flushable {
	private final Writer writer;
	private List<String> comments;
	private String key;
	private boolean noKey;
	private int indent;

	/**
	 * Writes the given {@link TomlElement} to the underlying writer.
	 *
	 * @param element The element to write.
	 * @param path    The path of the element.
	 */
	public void write(TomlElement element, String path) throws IOException {
		comments = element.comments();

		indent();
		if (!noKey && !(element instanceof TomlTableElement) && key != null) writer.append(key).append('=');
		switch (element) {
			case TomlTableElement table -> {
				if (!path.isEmpty()) {
					writeComments();
					writer.append('\n');

					indent();
					writer.append('[').append(path).append(']').append('\n');
					indent++;
				}

				List<Map.Entry<String, TomlElement>> sorted = table.members().entrySet().stream()
					.sorted(Comparator.comparing(entry -> (entry.getValue() instanceof TomlTableElement || (entry.getValue() instanceof TomlArray array && getArrayType(array) == TomlTableElement.class)) ? 1 : 0))
					.toList();

				SpecterGlobals.debug(sorted.toString());

				for (Map.Entry<String, TomlElement> entry : sorted) {
					key = entry.getKey();
					key = Toml.tomlEscape(key).toString();
					if (!key.matches("[a-zA-Z0-9_-]*")) key = "\"%s\"".formatted(key);
					TomlElement value = entry.getValue();

					write(value, (!path.isEmpty() ? "%s.".formatted(path) : "") + Toml.tomlEscape(key));
				}

				if (indent > 0) indent--;
			}
			case TomlNull ignored -> {
				writer.write("null");
				writeComments();
			}
			case TomlPrimitive primitive -> {
				if (primitive.value() instanceof Number number) writer.append(number.toString());
				else if (primitive.value() instanceof Boolean bool) writer.append(bool ? "true" : "false");
				else writer.append('"').append(primitive.toString()).append('"');
				writer.append(' ');
				writeComments();
				writer.append('\n');
			}
			case TomlArray array -> {
				writeComments();
				writer.append('\n');
				indent();

				noKey = true;
				writer.write('[');
				for (TomlElement val : array) write(val, path);
				writer.write(']');
				noKey = false;
			}
			default -> throw new IllegalArgumentException("Unsupported TomlElement type: " + element);
		}
	}

	public TomlWriter(Writer writer) {
		this.writer = writer;
	}

	private void writeComments() throws IOException {
		if (comments == null) return;
		for (int i = 0; i < comments.size(); i++) {
			String comment = comments.get(i);
			if (i > 1) indent();
			writer.append("# ").append(comment);
			if (i < comments.size() - 1) writer.append('\n');
		}
		this.comments = null;
	}

	private void indent() throws IOException {
		writer.append("\t".repeat(indent));
	}

	private Class<? extends TomlElement> getArrayType(TomlArray array) {
		return array.elements().getFirst().getClass();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void close() throws Exception {
		writer.close();
	}
}
