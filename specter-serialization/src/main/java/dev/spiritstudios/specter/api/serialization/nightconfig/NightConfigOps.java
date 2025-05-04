package dev.spiritstudios.specter.api.serialization.nightconfig;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NightConfigOps implements DynamicOps<NightConfigElement> {
	public static final NightConfigOps INSTANCE = new NightConfigOps();

	@Override
	public NightConfigElement empty() {
		return NightConfigNull.INSTANCE;
	}

	@Override
	public <U> U convertTo(DynamicOps<U> outOps, NightConfigElement input) {
		return switch (input) {
			case NightConfigMap ignored -> convertMap(outOps, input);
			case NightConfigList ignored -> convertList(outOps, input);
			case NightConfigNull ignored -> outOps.empty();
			case NightConfigPrimitive primitive -> switch (primitive.toObject()) {
				case String string -> outOps.createString(string);
				case Boolean bool -> outOps.createBoolean(bool);
				case Number number -> outOps.createNumeric(number);
				case null -> outOps.empty();
				default -> throw new IllegalStateException("Unexpected value: " + primitive.toObject());
			};
			default -> throw new IllegalStateException("Unexpected value: " + input);
		};
	}

	@Override
	public DataResult<Number> getNumberValue(NightConfigElement input) {
		if (!(input.toObject() instanceof Number number))
			return DataResult.error(() -> "Not a number: " + input);

		return DataResult.success(number);
	}

	@Override
	public NightConfigElement createNumeric(Number value) {
		return new NightConfigPrimitive(value, Collections.emptyList());
	}

	@Override
	public DataResult<String> getStringValue(NightConfigElement input) {
		if (!(input.toObject() instanceof String string))
			return DataResult.error(() -> "Not a string: " + input);

		return DataResult.success(string);
	}

	@Override
	public NightConfigElement createString(String value) {
		return new NightConfigPrimitive(value, Collections.emptyList());
	}

	@Override
	public DataResult<Boolean> getBooleanValue(NightConfigElement input) {
		if (!(input.toObject() instanceof Boolean bool))
			return DataResult.error(() -> "Not a boolean: " + input);

		return DataResult.success(bool);
	}

	@Override
	public NightConfigElement createBoolean(boolean value) {
		return new NightConfigPrimitive(value, Collections.emptyList());
	}

	@Override
	public DataResult<NightConfigElement> mergeToList(NightConfigElement list, NightConfigElement value) {
		if (list == empty())
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		if (!(list instanceof NightConfigList array))
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		NightConfigList output = new NightConfigList(array);
		output.add(value);

		return DataResult.success(output);
	}

	@Override
	public DataResult<NightConfigElement> mergeToList(NightConfigElement list, List<NightConfigElement> values) {
		if (!(list instanceof NightConfigList) && list != empty())
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		NightConfigList output = list instanceof NightConfigList l ?
			new NightConfigList(l) :
			new NightConfigList(new ArrayList<>(), list.comments());

		output.addAll(values);

		return DataResult.success(output);
	}

	@Override
	public DataResult<NightConfigElement> mergeToMap(NightConfigElement map, NightConfigElement key, NightConfigElement value) {
		if (!(map instanceof NightConfigMap) && map != empty())
			return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

		if (!(key.toObject() instanceof String keyString))
			return DataResult.error(() -> "Not a string key: " + key, map);

		NightConfigMap output = map instanceof NightConfigMap m ?
			new NightConfigMap(m) :
			new NightConfigMap(CommentedConfig.inMemory(), map.comments());

		output.put(keyString, value);

		return DataResult.success(output);
	}

	@Override
	public DataResult<NightConfigElement> mergeToMap(NightConfigElement map, MapLike<NightConfigElement> values) {
		if (map == empty())
			map = new NightConfigMap(CommentedConfig.inMemory(), Collections.emptyList());

		if (!(map instanceof NightConfigMap object)) {
			NightConfigElement finalMap = map;
			return DataResult.error(() -> "mergeToMap called with not a map: " + finalMap, map);
		}

		NightConfigMap output = new NightConfigMap(object);
		values.entries().forEach(entry -> {
			if (!(entry.getFirst() instanceof NightConfigPrimitive key) || !(key.toObject() instanceof String keyString))
				return;

			output.put(keyString, entry.getSecond());
		});

		return DataResult.success(output);
	}

	@Override
	public DataResult<Stream<Pair<NightConfigElement, NightConfigElement>>> getMapValues(NightConfigElement input) {
		if (!(input instanceof NightConfigMap object)) return DataResult.error(() -> "Not a map: " + input);

		List<Pair<NightConfigElement, NightConfigElement>> elements = new ArrayList<>();
		for (CommentedConfig.Entry entry : object.config().entrySet()) {
			String comment = entry.getComment();
			List<String> comments = comment != null ?
				Arrays.asList(comment.split("\n")) :
				Collections.emptyList();

			elements.add(Pair.of(
				new NightConfigPrimitive(entry.getKey(), Collections.emptyList()),
				NightConfigElement.ofObject(entry.getValue(), comments)
			));
		}

		return DataResult.success(elements.stream());
	}

	@Override
	public DataResult<Consumer<BiConsumer<NightConfigElement, NightConfigElement>>> getMapEntries(NightConfigElement input) {
		if (!(input instanceof NightConfigMap object)) return DataResult.error(() -> "Not an object: " + input);

		return DataResult.success(consumer ->
			object.config().entrySet().forEach(entry -> {
				String comment = entry.getComment();
				List<String> comments = comment != null ?
					Arrays.asList(comment.split("\n")) :
					Collections.emptyList();

				consumer.accept(createString(entry.getKey()), NightConfigElement.ofObject(entry.getValue(), comments));
			}));
	}

	@Override
	public DataResult<MapLike<NightConfigElement>> getMap(NightConfigElement input) {
		if (!(input instanceof NightConfigMap object))
			return DataResult.error(() -> "Not a object: " + input);

		return DataResult.success(new MapLike<>() {
			@Override
			public NightConfigElement get(NightConfigElement key) {
				if (!(key.toObject() instanceof String keyString)) return null;
				NightConfigElement element = NightConfigElement.ofObject(object.config().get(keyString));
				if (element instanceof NightConfigNull) return null;

				return element;
			}

			@Override
			public NightConfigElement get(String key) {
				NightConfigElement element = NightConfigElement.ofObject(object.config().get(key));
				if (element == null) return null;
				if (element instanceof NightConfigNull) return null;

				return element;
			}

			@Override
			public Stream<Pair<NightConfigElement, NightConfigElement>> entries() {
				return object.config().entrySet().stream()
					.map(entry -> {
						String comment = entry.getComment();
						List<String> comments = comment != null ?
							Arrays.asList(comment.split("\n")) :
							Collections.emptyList();

						return Pair.of(
							new NightConfigPrimitive(entry.getKey(), Collections.emptyList()),
							NightConfigElement.ofObject(entry.getValue(), comments)
						);
					});
			}

			@Override
			public String toString() {
				return "MapLike[" + object + "]";
			}
		});
	}

	@Override
	public NightConfigElement createMap(Stream<Pair<NightConfigElement, NightConfigElement>> map) {
		NightConfigMap object = new NightConfigMap(CommentedConfig.inMemory(), Collections.emptyList());
		map.forEach(pair -> {
			if (!(pair.getFirst() instanceof NightConfigPrimitive key) || !(key.toObject() instanceof String keyString))
				return;

			object.put(keyString, pair.getSecond());
		});
		return object;
	}

	@Override
	public DataResult<Stream<NightConfigElement>> getStream(NightConfigElement input) {
		if (!(input instanceof NightConfigList array)) return DataResult.error(() -> "Not a array: " + input);
		return DataResult.success(array.toElements().stream());
	}

	@Override
	public DataResult<Consumer<Consumer<NightConfigElement>>> getList(NightConfigElement input) {
		if (!(input instanceof NightConfigList array)) return DataResult.error(() -> "Not a array: " + input);

		return DataResult.success(consumer -> array.toElements().forEach(object -> {
			if (object == null) consumer.accept(null);
			consumer.accept(object);
		}));
	}

	@Override
	public NightConfigElement createList(Stream<NightConfigElement> input) {
		return new NightConfigList(input.collect(Collectors.toList()), Collections.emptyList());
	}

	@Override
	public NightConfigElement remove(NightConfigElement input, String key) {
		if (input instanceof NightConfigMap object) object.config().remove(key);
		return input;
	}

	@Override
	public String toString() {
		return "NightConfig";
	}

	@Override
	public ListBuilder<NightConfigElement> listBuilder() {
		return new ArrayBuilder();
	}

	@Override
	public RecordBuilder<NightConfigElement> mapBuilder() {
		return new NightConfigRecordBuilder();
	}

	private static final class ArrayBuilder implements ListBuilder<NightConfigElement> {
		private DataResult<NightConfigList> builder = DataResult.success(
			new NightConfigList(new ArrayList<>(), new ArrayList<>()),
			Lifecycle.stable()
		);

		@Override
		public DynamicOps<NightConfigElement> ops() {
			return NightConfigOps.INSTANCE;
		}

		@Override
		public ListBuilder<NightConfigElement> add(final NightConfigElement value) {
			builder = builder.map(array -> {
				array.add(value);
				return array;
			});
			return this;
		}

		@Override
		public ListBuilder<NightConfigElement> add(final DataResult<NightConfigElement> value) {
			builder = builder.apply2stable((array, element) -> {
				array.add(element);
				return array;
			}, value);
			return this;
		}

		@Override
		public ListBuilder<NightConfigElement> withErrorsFrom(final DataResult<?> result) {
			builder = builder.flatMap(array -> result.map(v -> array));
			return this;
		}

		@Override
		public ListBuilder<NightConfigElement> mapError(final UnaryOperator<String> onError) {
			builder = builder.mapError(onError);
			return this;
		}

		@Override
		public DataResult<NightConfigElement> build(final NightConfigElement prefix) {
			DataResult<NightConfigElement> result = builder.flatMap(b -> {
				if (!(prefix instanceof NightConfigList) && prefix != ops().empty())
					return DataResult.error(() -> "Cannot append a list to not a list: " + prefix, prefix);

				final NightConfigList output = new NightConfigList(b);
				if (prefix != ops().empty()) {
					output.addAll((NightConfigList) prefix);
				}
				output.addAll(b);
				return DataResult.success(output, Lifecycle.stable());
			});

			builder = DataResult.success(
				new NightConfigList(new ArrayList<>(), Collections.emptyList()),
				Lifecycle.stable()
			);

			return result;
		}
	}

	private class NightConfigRecordBuilder extends RecordBuilder.AbstractStringBuilder<NightConfigElement, NightConfigMap> {
		protected NightConfigRecordBuilder() {
			super(NightConfigOps.this);
		}

		@Override
		protected NightConfigMap initBuilder() {
			return new NightConfigMap(CommentedConfig.inMemory(), Collections.emptyList());
		}

		@Override
		protected NightConfigMap append(final String key, final NightConfigElement value, final NightConfigMap builder) {
			builder.put(key, value);
			return builder;
		}

		@Override
		protected DataResult<NightConfigElement> build(final NightConfigMap builder, final NightConfigElement prefix) {
			if (prefix == null || prefix instanceof NightConfigNull) return DataResult.success(builder);
			if (!(prefix instanceof NightConfigMap prefixMap))
				return DataResult.error(() -> "mergeToMap called with not a map: " + prefix, prefix);

			NightConfigMap result = new NightConfigMap(CommentedConfig.inMemory(), Collections.emptyList());

			for (CommentedConfig.Entry entry : prefixMap.config().entrySet())
				result.put(entry.getKey(), NightConfigElement.ofObject(entry.getValue()));

			for (CommentedConfig.Entry entry : builder.config().entrySet())
				result.put(entry.getKey(), NightConfigElement.ofObject(entry.getValue()));

			return DataResult.success(result);
		}
	}
}
