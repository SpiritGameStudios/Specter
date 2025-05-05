package dev.spiritstudios.specter.api.serialization.format;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import dev.spiritstudios.specter.api.serialization.jsonc.JsonCElement;
import dev.spiritstudios.specter.api.serialization.jsonc.JsonCObject;
import dev.spiritstudios.specter.api.serialization.jsonc.JsonCWriter;

public class JsonCFormat implements DynamicFormat<JsonCElement> {
	public static final JsonCFormat INSTANCE = new JsonCFormat();

	@Override
	public JsonCElement empty() {
		return JsonCElement.NULL;
	}

	@Override
	public <U> U convertTo(DynamicOps<U> outOps, JsonCElement input) {
		if (!(input instanceof JsonCObject object)) return JsonOps.INSTANCE.convertTo(outOps, input.element());

		Map<U, U> members = object.members().entrySet().stream()
			.collect(
				Object2ObjectOpenHashMap::new,
				(map, entry) -> map.put(outOps.createString(entry.getKey()), JsonOps.INSTANCE.convertTo(outOps, input.element())),
				Map::putAll
			);

		return outOps.createMap(members);
	}

	@Override
	public DataResult<Number> getNumberValue(JsonCElement input) {
		if (input instanceof JsonCObject object) return DataResult.error(() -> "Not a number: " + object);

		return JsonOps.INSTANCE.getNumberValue(input.element());
	}

	@Override
	public JsonCElement createNumeric(Number i) {
		return new JsonCElement(JsonOps.INSTANCE.createNumeric(i));
	}

	@Override
	public DataResult<String> getStringValue(JsonCElement input) {
		if (input instanceof JsonCObject object) return DataResult.error(() -> "Not a string: " + object);

		return JsonOps.INSTANCE.getStringValue(input.element());
	}

	@Override
	public JsonCElement createString(String value) {
		return new JsonCElement(JsonOps.INSTANCE.createString(value));
	}

	@Override
	public DataResult<Boolean> getBooleanValue(JsonCElement input) {
		if (input instanceof JsonCObject object) return DataResult.error(() -> "Not a boolean: " + object);

		return JsonOps.INSTANCE.getBooleanValue(input.element());
	}

	@Override
	public JsonCElement createBoolean(boolean value) {
		return new JsonCElement(JsonOps.INSTANCE.createBoolean(value));
	}

	@Override
	public DataResult<JsonCElement> mergeToList(JsonCElement list, JsonCElement value) {
		if (!(list.element() instanceof JsonArray) && list != empty())
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		JsonArray array = new JsonArray();
		if (list != empty()) array.addAll(list.element().getAsJsonArray());

		array.add(value.element());
		return DataResult.success(new JsonCElement(array, list.comments()));
	}

	@Override
	public DataResult<JsonCElement> mergeToList(JsonCElement list, List<JsonCElement> values) {
		if (!(list.element() instanceof JsonArray) && list != empty())
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		JsonArray array = new JsonArray();
		if (list != empty()) array.addAll(list.element().getAsJsonArray());

		values.forEach(value -> array.add(value.element()));
		return DataResult.success(new JsonCElement(array, list.comments()));
	}

	@Override
	public DataResult<JsonCElement> mergeToMap(JsonCElement map, JsonCElement key, JsonCElement value) {
		if (!(map.element() instanceof JsonObject) && map != empty())
			return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

		if (!(key.element() instanceof JsonPrimitive primitive) || !primitive.isString())
			return DataResult.error(() -> "key is not a string: " + key, map);

		Map<String, JsonCElement> members = new LinkedHashMap<>();
		if (map != empty()) members.putAll(((JsonCObject) map).members());

		members.put(primitive.getAsString(), value);
		return DataResult.success(new JsonCObject(members, map.comments()));
	}

	@Override
	public DataResult<JsonCElement> mergeToMap(JsonCElement map, MapLike<JsonCElement> values) {
		if (!(map.element() instanceof JsonObject) && map != empty())
			return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

		JsonCObject output = new JsonCObject(new LinkedHashMap<>(), map.comments());
		if (map != empty())
			output.putAll(((JsonCObject) map).members());

		List<JsonElement> missed = new ArrayList<>();
		values.entries().forEach(entry -> {
			JsonElement key = entry.getFirst().element();
			if (!(key instanceof JsonPrimitive primitive) || !primitive.isString()) {
				missed.add(key);
				return;
			}

			output.put(key.getAsString(), entry.getSecond());
		});

		if (!missed.isEmpty()) return DataResult.error(() -> "some keys are not strings: " + missed, output);
		return DataResult.success(output);
	}

	@Override
	public DataResult<Stream<Pair<JsonCElement, JsonCElement>>> getMapValues(JsonCElement input) {
		if (!(input instanceof JsonCObject object)) return DataResult.error(() -> "Not an object: " + input);
		return DataResult.success(object.members().entrySet().stream()
			.map(entry -> Pair.of(createString(entry.getKey()), entry.getValue())));
	}

	@Override
	public DataResult<Consumer<BiConsumer<JsonCElement, JsonCElement>>> getMapEntries(JsonCElement input) {
		if (!(input instanceof JsonCObject object)) return DataResult.error(() -> "Not an object: " + input);

		return DataResult.success(consumer -> object.members().forEach((key, value) -> consumer.accept(createString(key), value)));
	}

	@Override
	public DataResult<MapLike<JsonCElement>> getMap(JsonCElement input) {
		if (!(input instanceof JsonCObject object))
			return DataResult.error(() -> "Not a JSON object: " + input);

		return DataResult.success(new MapLike<>() {
			@Override
			public JsonCElement get(JsonCElement key) {
				if (!(key.element() instanceof JsonPrimitive primitive) || !primitive.isString()) return null;
				JsonCElement element = object.members().get(primitive.getAsString());
				if (element.element() instanceof JsonNull) return null;

				return element;
			}

			@Override
			public JsonCElement get(String key) {
				JsonCElement element = object.members().get(key);
				if (element == null) return null;
				if (element.element() instanceof JsonNull) return null;

				return element;
			}

			@Override
			public Stream<Pair<JsonCElement, JsonCElement>> entries() {
				return object.members().entrySet().stream()
					.map(entry -> Pair.of(createString(entry.getKey()), entry.getValue()));
			}

			@Override
			public String toString() {
				return "MapLike[" + object + "]";
			}
		});
	}

	@Override
	public JsonCElement createMap(Stream<Pair<JsonCElement, JsonCElement>> map) {
		JsonCObject object = new JsonCObject(new LinkedHashMap<>(), Collections.emptyList());
		map.forEach(pair -> object.put(pair.getFirst().element().getAsString(), pair.getSecond()));
		return object;
	}

	@Override
	public DataResult<Stream<JsonCElement>> getStream(JsonCElement input) {
		if (!(input.element() instanceof JsonArray array)) return DataResult.error(() -> "Not a json array: " + input);

		return DataResult.success(array.asList().stream().map(JsonCElement::new));
	}

	@Override
	public DataResult<Consumer<Consumer<JsonCElement>>> getList(JsonCElement input) {
		if (!(input.element() instanceof JsonArray array)) return DataResult.error(() -> "Not a json array: " + input);

		return DataResult.success(consumer -> {
			for (JsonElement element : array) consumer.accept(new JsonCElement(element));
		});

	}

	@Override
	public JsonCElement createList(Stream<JsonCElement> input) {
		JsonArray array = new JsonArray();
		input.forEach(element -> array.add(element.element()));
		return new JsonCElement(array);
	}

	@Override
	public JsonCElement remove(JsonCElement input, String key) {
		if (input.element() instanceof JsonObject object) {
			JsonObject result = new JsonObject();
			object.entrySet().stream()
				.filter(entry -> !Objects.equals(entry.getKey(), key))
				.forEach(entry -> result.add(entry.getKey(), entry.getValue()));

			return new JsonCElement(result, input.comments());
		}

		return input;
	}

	@Override
	public ListBuilder<JsonCElement> listBuilder() {
		return new ArrayBuilder();
	}

	@Override
	public RecordBuilder<JsonCElement> mapBuilder() {
		return new JsonCRecordBuilder();
	}

	@Override
	public void write(Writer writer, JsonCElement value) throws IOException {
		JsonCWriter jsonWriter = new JsonCWriter(writer);
		jsonWriter.write(value);
		jsonWriter.flush();
	}

	@Override
	public JsonCElement read(Reader reader) throws JsonSyntaxException {
		JsonElement element = JsonParser.parseReader(reader);
		return element.isJsonObject() ?
			new JsonCObject(element.getAsJsonObject()) :
			new JsonCElement(element);
	}

	@Override
	public String name() {
		return "json";
	}

	private static final class ArrayBuilder implements ListBuilder<JsonCElement> {
		private DataResult<JsonCElement> builder = DataResult.success(new JsonCElement(new JsonArray()), Lifecycle.stable());

		@Override
		public DynamicOps<JsonCElement> ops() {
			return INSTANCE;
		}

		@Override
		public ListBuilder<JsonCElement> add(JsonCElement value) {
			builder = builder.map(element -> {
				if (!(element.element() instanceof JsonArray array))
					throw new IllegalStateException("Not a json array: " + element);

				array.add(value.element());
				return element;
			});

			return this;
		}

		@Override
		public ListBuilder<JsonCElement> add(DataResult<JsonCElement> value) {
			builder = builder.apply2stable((b, element) -> {
				if (!(b.element() instanceof JsonArray array))
					throw new IllegalStateException("Not a json array: " + b);

				array.add(element.element());
				return b;
			}, value);
			return this;
		}

		@Override
		public ListBuilder<JsonCElement> withErrorsFrom(final DataResult<?> result) {
			builder = builder.flatMap(r -> result.map(v -> r));
			return this;
		}

		@Override
		public ListBuilder<JsonCElement> mapError(final UnaryOperator<String> onError) {
			builder = builder.mapError(onError);
			return this;
		}

		@Override
		public DataResult<JsonCElement> build(final JsonCElement prefix) {
			DataResult<JsonCElement> result = builder.flatMap(b -> {
				if (!(prefix.element() instanceof JsonArray) && prefix != ops().empty())
					return DataResult.error(() -> "Cannot append a list to not a list: " + prefix, prefix);

				JsonArray array = new JsonArray();
				if (prefix != ops().empty()) array.addAll(prefix.element().getAsJsonArray());
				array.addAll(b.element().getAsJsonArray());

				return DataResult.success(new JsonCElement(array), Lifecycle.stable());
			});

			builder = result;
			return result;
		}
	}

	private class JsonCRecordBuilder extends RecordBuilder.AbstractStringBuilder<JsonCElement, JsonCObject> {
		protected JsonCRecordBuilder() {
			super(JsonCFormat.this);
		}

		@Override
		protected JsonCObject initBuilder() {
			return new JsonCObject();
		}

		@Override
		protected JsonCObject append(final String key, final JsonCElement value, final JsonCObject builder) {
			builder.put(key, value);
			return builder;
		}

		@Override
		protected DataResult<JsonCElement> build(final JsonCObject builder, final JsonCElement prefix) {
			if (prefix == null || prefix.element() instanceof JsonNull) return DataResult.success(builder);

			if (prefix instanceof JsonCObject object) {
				JsonCObject result = new JsonCObject();

				object.members().forEach(result::put);
				builder.members().forEach(result::put);

				return DataResult.success(result);
			}

			return DataResult.error(() -> "mergeToMap called with not a map: " + prefix, prefix);
		}
	}
}
