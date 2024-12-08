package dev.spiritstudios.specter.impl.serialization.format;

import com.mojang.serialization.DynamicOps;
import dev.spiritstudios.specter.api.serialization.format.DynamicFormat;
import net.minecraft.util.dynamic.ForwardingDynamicOps;

import java.io.Reader;
import java.io.Writer;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WrappedDynamicFormat<T> extends ForwardingDynamicOps<T> implements DynamicFormat<T> {
	private final BiConsumer<Writer, T> write;
	private final Function<Reader, T> read;
	private final String name;

	public WrappedDynamicFormat(DynamicOps<T> ops, BiConsumer<Writer, T> write, Function<Reader, T> read, String name) {
		super(ops);
		this.write = write;
		this.read = read;
		this.name = name;
	}

	@Override
	public void write(Writer writer, T value) {
		write.accept(writer, value);
	}

	@Override
	public T read(Reader reader) {
		return read.apply(reader);
	}

	@Override
	public String name() {
		return name;
	}
}
