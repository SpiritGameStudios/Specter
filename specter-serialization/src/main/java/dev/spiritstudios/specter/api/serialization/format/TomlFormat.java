package dev.spiritstudios.specter.api.serialization.format;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import dev.spiritstudios.specter.api.serialization.toml.*;
import org.tomlj.Toml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

public class TomlFormat implements DynamicFormat<TomlElement> {
	public static final TomlFormat INSTANCE = new TomlFormat();

	@Override
	public TomlElement empty() {
		return TomlNull.INSTANCE;
	}

	@Override
	public <U> U convertTo(DynamicOps<U> outOps, TomlElement input) {
		return switch (input) {
			case TomlTableElement ignored -> convertMap(outOps, input);
			case TomlArray ignored -> convertList(outOps, input);
			case TomlNull ignored -> outOps.empty();
			case TomlPrimitive primitive -> switch (primitive.value()) {
				case String string -> outOps.createString(string);
				case Boolean bool -> outOps.createBoolean(bool);
				case Number number -> outOps.createNumeric(number);
				case OffsetDateTime dateTime -> outOps.createString(dateTime.toString());
				case LocalDateTime dateTime -> outOps.createString(dateTime.toString());
				case LocalDate date -> outOps.createString(date.toString());
				case LocalTime time -> outOps.createString(time.toString());
				case null -> outOps.empty();
				default -> throw new IllegalStateException("Unexpected value: " + primitive.value());
			};
			default -> throw new IllegalStateException("Unexpected value: " + input);
		};
	}

	@Override
	public DataResult<Number> getNumberValue(TomlElement input) {
		if (!(input instanceof TomlPrimitive primitive) || !(primitive.value() instanceof Number number))
			return DataResult.error(() -> "Not a number: " + input);

		return DataResult.success(number);
	}

	@Override
	public TomlElement createNumeric(Number i) {
		return TomlPrimitive.of(i);
	}

	@Override
	public DataResult<String> getStringValue(TomlElement input) {
		if (!(input instanceof TomlPrimitive primitive) || !(primitive.value() instanceof String string))
			return DataResult.error(() -> "Not a string: " + input);

		return DataResult.success(string);
	}

	@Override
	public TomlElement createString(String value) {
		return TomlPrimitive.of(value);
	}

	@Override
	public DataResult<Boolean> getBooleanValue(TomlElement input) {
		if (!(input instanceof TomlPrimitive primitive) || !(primitive.value() instanceof Boolean bool))
			return DataResult.error(() -> "Not a boolean: " + input);

		return DataResult.success(bool);
	}

	@Override
	public TomlElement createBoolean(boolean value) {
		return TomlPrimitive.of(value);
	}

	@Override
	public DataResult<TomlElement> mergeToList(TomlElement list, TomlElement value) {
		if (list == empty())
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		if (!(list instanceof TomlArray listArray))
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		TomlArray array = new TomlArray();
		listArray.forEach(array::add);
		array.add(value);

		return DataResult.success(array);
	}

	@Override
	public DataResult<TomlElement> mergeToList(TomlElement list, List<TomlElement> values) {
		if (list == empty())
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		if (!(list instanceof TomlArray listArray))
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		TomlArray array = new TomlArray();
		listArray.forEach(array::add);
		array.addAll(values);

		return DataResult.success(array);
	}

	@Override
	public DataResult<TomlElement> mergeToMap(TomlElement map, TomlElement key, TomlElement value) {
		if (map == empty())
			map = new TomlTableElement();

		if (!(map instanceof TomlTableElement mapTable)) {
			TomlElement finalMap = map;
			return DataResult.error(() -> "mergeToMap called with not a map: " + finalMap, map);
		}

		if (!(key instanceof TomlPrimitive keyPrimitive) || !(keyPrimitive.value() instanceof String keyString))
			return DataResult.error(() -> "Not a string key: " + key, map);

		TomlTableElement table = new TomlTableElement(mapTable);
		table.put(keyString, value);

		return DataResult.success(table);
	}

	@Override
	public DataResult<TomlElement> mergeToMap(TomlElement map, MapLike<TomlElement> values) {
		if (map == empty())
			map = new TomlTableElement();

		if (!(map instanceof TomlTableElement mapTable)) {
			TomlElement finalMap = map;
			return DataResult.error(() -> "mergeToMap called with not a map: " + finalMap, map);
		}

		TomlTableElement table = new TomlTableElement(mapTable);
		values.entries().forEach(entry -> {
			if (!(entry.getFirst() instanceof TomlPrimitive key) || !(key.value() instanceof String keyString))
				return;

			table.put(keyString, entry.getSecond());
		});

		return DataResult.success(table);
	}

	@Override
	public DataResult<Stream<Pair<TomlElement, TomlElement>>> getMapValues(TomlElement input) {
		if (!(input instanceof TomlTableElement table)) return DataResult.error(() -> "Not a toml table: " + input);
		return DataResult.success(table.entrySet().stream()
			.map(entry -> Pair.of(TomlPrimitive.of(entry.getKey()), TomlElement.of(entry.getValue()))));
	}

	@Override
	public TomlElement createMap(Stream<Pair<TomlElement, TomlElement>> map) {
		TomlTableElement table = new TomlTableElement();
		map.forEach(pair -> {
			if (!(pair.getFirst() instanceof TomlPrimitive key) || !(key.value() instanceof String keyString))
				return;

			table.put(keyString, pair.getSecond());
		});
		return table;
	}

	@Override
	public DataResult<Stream<TomlElement>> getStream(TomlElement input) {
		if (!(input instanceof TomlArray array)) return DataResult.error(() -> "Not a toml array: " + input);
		return DataResult.success(array.stream());
	}

	@Override
	public TomlElement createList(Stream<TomlElement> input) {
		TomlArray array = new TomlArray();
		input.forEach(array::add);
		return array;
	}

	@Override
	public TomlElement remove(TomlElement input, String key) {
		if (input instanceof TomlTableElement table) table.remove(key);
		return input;
	}

	@Override
	public void write(Writer writer, TomlElement value) throws IOException {
		if (!(value instanceof TomlTableElement table))
			throw new IllegalArgumentException("Top level element must be a table");

		TomlWriter tomlWriter = new TomlWriter(writer);
		tomlWriter.write(table, "");
		tomlWriter.flush();
	}

	@Override
	public TomlElement read(Reader reader) throws IOException {
		return new TomlTableElement(Toml.parse(reader));
	}

	@Override
	public String name() {
		return "toml";
	}
}
