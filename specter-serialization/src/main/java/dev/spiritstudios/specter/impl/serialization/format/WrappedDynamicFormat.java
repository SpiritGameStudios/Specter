package dev.spiritstudios.specter.impl.serialization.format;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.spiritstudios.specter.api.serialization.format.DynamicFormat;

import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Welcome to hell! This is the worst class in the entirety of specter.
 */
public class WrappedDynamicFormat<T> implements DynamicFormat<T> {
	private final DynamicOps<T> ops;
	private final BiConsumer<Writer, T> write;
	private final Function<Reader, T> read;
	private final String name;

	public WrappedDynamicFormat(DynamicOps<T> ops, BiConsumer<Writer, T> write, Function<Reader, T> read, String name) {
		this.ops = ops;
		this.write = write;
		this.read = read;
		this.name = name;
	}

	@Override
	public void write(Writer writer, T value) {
		this.write.accept(writer, value);
	}

	@Override
	public T read(Reader reader) {
		return this.read.apply(reader);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public T empty() {
		return ops.empty();
	}

	@Override
	public T emptyMap() {
		return ops.emptyMap();
	}

	@Override
	public T emptyList() {
		return ops.emptyList();
	}

	@Override
	public <U> U convertTo(DynamicOps<U> outOps, T input) {
		return ops.convertTo(outOps, input);
	}

	@Override
	public DataResult<Number> getNumberValue(T input) {
		return ops.getNumberValue(input);
	}

	@Override
	public Number getNumberValue(T input, Number defaultValue) {
		return ops.getNumberValue(input, defaultValue);
	}

	@Override
	public T createNumeric(Number i) {
		return ops.createNumeric(i);
	}

	@Override
	public T createByte(byte value) {
		return ops.createByte(value);
	}

	@Override
	public T createShort(short value) {
		return ops.createShort(value);
	}

	@Override
	public T createInt(int value) {
		return ops.createInt(value);
	}

	@Override
	public T createLong(long value) {
		return ops.createLong(value);
	}

	@Override
	public T createFloat(float value) {
		return ops.createFloat(value);
	}

	@Override
	public T createDouble(double value) {
		return ops.createDouble(value);
	}

	@Override
	public DataResult<String> getStringValue(T input) {
		return ops.getStringValue(input);
	}

	@Override
	public T createString(String value) {
		return ops.createString(value);
	}

	@Override
	public DataResult<Boolean> getBooleanValue(T input) {
		return ops.getBooleanValue(input);
	}

	@Override
	public T createBoolean(boolean value) {
		return ops.createBoolean(value);
	}

	@Override
	public DataResult<T> mergeToList(T list, T value) {
		return ops.mergeToList(list, value);
	}

	@Override
	public DataResult<T> mergeToList(T list, List<T> values) {
		return ops.mergeToList(list, values);
	}

	@Override
	public DataResult<T> mergeToMap(T map, T key, T value) {
		return ops.mergeToMap(map, key, value);
	}

	@Override
	public DataResult<T> mergeToMap(T map, Map<T, T> values) {
		return ops.mergeToMap(map, values);
	}

	@Override
	public DataResult<T> mergeToMap(T map, MapLike<T> values) {
		return ops.mergeToMap(map, values);
	}

	@Override
	public DataResult<T> mergeToPrimitive(T prefix, T value) {
		return ops.mergeToPrimitive(prefix, value);
	}

	@Override
	public DataResult<Stream<Pair<T, T>>> getMapValues(T input) {
		return ops.getMapValues(input);
	}

	@Override
	public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T input) {
		return ops.getMapEntries(input);
	}

	@Override
	public T createMap(Stream<Pair<T, T>> map) {
		return ops.createMap(map);
	}

	@Override
	public DataResult<MapLike<T>> getMap(T input) {
		return ops.getMap(input);
	}

	@Override
	public T createMap(Map<T, T> map) {
		return ops.createMap(map);
	}

	@Override
	public DataResult<Stream<T>> getStream(T input) {
		return ops.getStream(input);
	}

	@Override
	public DataResult<Consumer<Consumer<T>>> getList(T input) {
		return ops.getList(input);
	}

	@Override
	public T createList(Stream<T> input) {
		return ops.createList(input);
	}

	@Override
	public DataResult<ByteBuffer> getByteBuffer(T input) {
		return ops.getByteBuffer(input);
	}

	@Override
	public T createByteList(ByteBuffer input) {
		return ops.createByteList(input);
	}

	@Override
	public DataResult<IntStream> getIntStream(T input) {
		return ops.getIntStream(input);
	}

	@Override
	public T createIntList(IntStream input) {
		return ops.createIntList(input);
	}

	@Override
	public DataResult<LongStream> getLongStream(T input) {
		return ops.getLongStream(input);
	}

	@Override
	public T createLongList(LongStream input) {
		return ops.createLongList(input);
	}

	@Override
	public T remove(T input, String key) {
		return ops.remove(input, key);
	}

	@Override
	public boolean compressMaps() {
		return ops.compressMaps();
	}

	@Override
	public DataResult<T> get(T input, String key) {
		return ops.get(input, key);
	}

	@Override
	public DataResult<T> getGeneric(T input, T key) {
		return ops.getGeneric(input, key);
	}

	@Override
	public T set(T input, String key, T value) {
		return ops.set(input, key, value);
	}

	@Override
	public T update(T input, String key, Function<T, T> function) {
		return ops.update(input, key, function);
	}

	@Override
	public T updateGeneric(T input, T key, Function<T, T> function) {
		return ops.updateGeneric(input, key, function);
	}

	@Override
	public ListBuilder<T> listBuilder() {
		return ops.listBuilder();
	}

	@Override
	public RecordBuilder<T> mapBuilder() {
		return ops.mapBuilder();
	}

	@Override
	public <E> Function<E, DataResult<T>> withEncoder(Encoder<E> encoder) {
		return ops.withEncoder(encoder);
	}

	@Override
	public <E> Function<T, DataResult<Pair<E, T>>> withDecoder(Decoder<E> decoder) {
		return ops.withDecoder(decoder);
	}

	@Override
	public <E> Function<T, DataResult<E>> withParser(Decoder<E> decoder) {
		return ops.withParser(decoder);
	}

	@Override
	public <U> U convertList(DynamicOps<U> outOps, T input) {
		return ops.convertList(outOps, input);
	}

	@Override
	public <U> U convertMap(DynamicOps<U> outOps, T input) {
		return ops.convertMap(outOps, input);
	}
}
