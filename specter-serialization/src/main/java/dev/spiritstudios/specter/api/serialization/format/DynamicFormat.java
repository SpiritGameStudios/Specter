package dev.spiritstudios.specter.api.serialization.format;

import com.mojang.serialization.DynamicOps;
import dev.spiritstudios.specter.impl.serialization.format.WrappedDynamicFormat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An adapter for a hierarchical serialization format, with support for reading and writing values.
 * <p>
 * If you already have a {@link DynamicOps} implementation, you can use {@link #of(DynamicOps, BiConsumer, Function, String)} to create a {@link DynamicFormat} from it.
 *
 * @param <T> The type this format serializes and deserializes.
 */
public interface DynamicFormat<T> extends DynamicOps<T> {
	static <T> DynamicFormat<T> of(DynamicOps<T> ops, BiConsumer<Writer, T> write, Function<Reader, T> read, String name) {
		return new WrappedDynamicFormat<>(ops, write, read, name);
	}

	void write(Writer writer, T value) throws IOException;

	T read(Reader reader) throws IOException;

	String name();

	default T read(String string) throws IOException {
		return read(new StringReader(string));
	}
}
