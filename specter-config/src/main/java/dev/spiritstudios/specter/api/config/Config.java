package dev.spiritstudios.specter.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.CodecHelper;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.ConfigManager;
import dev.spiritstudios.specter.impl.config.ValueImpl;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * A configuration file that can be saved to disk.
 */
public abstract class Config<T extends Config<T>> implements Codec<T> {
	@ApiStatus.Internal
	protected Config() {
	}

	public static <T extends Config<T>> T create(Class<T> clazz) {
		T instance = ReflectionHelper.instantiate(clazz);
		Config<?> existing = ConfigManager.getConfig(instance.getId());
		if (existing != null) {
			if (existing.getClass() != clazz)
				throw new IllegalArgumentException("Config with id %s already exists with a different class".formatted(instance.getId()));

			throw new RuntimeException("Config with id %s already exists".formatted(instance.getId()));
		}

		ConfigManager.registerConfig(instance.getId(), instance);
		for (Field field : clazz.getDeclaredFields()) {
			if (!Value.class.isAssignableFrom(field.getType())) continue;
			if (Modifier.isStatic(field.getModifiers())) continue;
			if (Modifier.isFinal(field.getModifiers())) continue;

			Value<?> value = ReflectionHelper.getFieldValue(instance, field);
			if (value == null) continue;

			value.init(field.getName());
			SpecterGlobals.debug("Registered config value: %s".formatted(value.translationKey(instance.getId())));
		}

		if (!instance.load())
			SpecterGlobals.LOGGER.error("Failed to load config file: {}, default values will be used", instance.getPath());
		else
			instance.save(); // Save the config to disk to ensure it's up to date

		return instance;
	}

	protected static <T> ValueBuilder<T> value(T defaultValue, Codec<T> codec) {
		return new ValueBuilder<>(defaultValue, codec);
	}

	protected static <T extends Enum<T>> ValueBuilder<T> enumValue(T defaultValue, Class<T> clazz) {
		return new ValueBuilder<>(defaultValue, CodecHelper.createEnumCodec(clazz)).packetCodec(
			CodecHelper.createEnumPacketCodec(clazz)
		);
	}

	protected static ValueBuilder<Boolean> booleanValue(boolean defaultValue) {
		return new ValueBuilder<>(defaultValue, Codec.BOOL).packetCodec(PacketCodecs.BOOL);
	}

	protected static RangedValueBuilder<Integer> intValue(int defaultValue) {
		return new RangedValueBuilder<>(defaultValue, Codec.INT, CodecHelper::clampedRangeInt).packetCodec(PacketCodecs.INTEGER);
	}

	protected static RangedValueBuilder<Float> floatValue(float defaultValue) {
		return new RangedValueBuilder<>(defaultValue, Codec.FLOAT, CodecHelper::clampedRangeFloat).packetCodec(PacketCodecs.FLOAT);
	}

	protected static RangedValueBuilder<Double> doubleValue(double defaultValue) {
		return new RangedValueBuilder<>(defaultValue, Codec.DOUBLE, CodecHelper::clampedRangeDouble).packetCodec(PacketCodecs.DOUBLE);
	}

	protected static ValueBuilder<String> stringValue(String defaultValue) {
		return new ValueBuilder<>(defaultValue, Codec.STRING).packetCodec(PacketCodecs.STRING);
	}

	public abstract Identifier getId();

	@Override
	public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
		RecordBuilder<T1> builder = ops.mapBuilder();
		for (Value<?> value : getValues().toList()) builder = value.encode(ops, builder);
		return builder.build(prefix);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
		for (Value<?> value : getValues().toList()) {
			if (value.decode(ops, input)) continue;

			SpecterGlobals.LOGGER.error("Failed to decode config value: {}", value.translationKey(getId()));
			return DataResult.error(() -> "Failed to decode config value: %s".formatted(value.translationKey(getId())));
		}

		return DataResult.success(Pair.of((T) this, input));
	}

	/**
	 * Saves the config to disk.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void save() {
		@SuppressWarnings("unchecked")
		DataResult<JsonElement> result = encodeStart(JsonOps.INSTANCE, (T) this);

		if (result.error().isPresent()) {
			SpecterGlobals.LOGGER.error("Failed to encode config: {}", getId());
			SpecterGlobals.LOGGER.error(result.error().toString());
			return;
		}

		JsonObject object = result.result().orElseThrow().getAsJsonObject();
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);

		jsonWriter.setLenient(true);
		jsonWriter.setSerializeNulls(false);
		jsonWriter.setIndent("	");

		try {
			Streams.write(object, jsonWriter);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String json = stringWriter.toString();

		Map<String, String> comments = new Object2ObjectOpenHashMap<>();

		for (Field field : this.getClass().getDeclaredFields()) {
			if (!Value.class.isAssignableFrom(field.getType())) continue;
			if (Modifier.isStatic(field.getModifiers())) continue;
			if (Modifier.isFinal(field.getModifiers())) continue;

			Value<?> value = ReflectionHelper.getFieldValue(this, field);
			if (value == null) continue;

			String comment = value.comment().orElse(null);
			if (comment == null) continue;

			comments.put(field.getName(), comment);
		}

		List<String> newLines = new ArrayList<>();
		for (String line : json.split("\n")) {
			if (!line.trim().startsWith("\"")) {
				newLines.add(line);
				continue;
			}

			String comment = comments.get(line.split("\"")[1]);
			if (comment == null) {
				newLines.add(line);
				continue;
			}

			String whitespaces = line.substring(0, line.indexOf("\""));
			comment = whitespaces + "// " + (comment.contains("\n") ? String.join("\n" + whitespaces + "// ", comment.split("\n")) : comment);

			newLines.add(comment);
			newLines.add(line);
		}

		Path path = this.getPath();
		path.toFile().getParentFile().mkdirs();

		try {
			Files.write(path, newLines);
		} catch (IOException e) {
			SpecterGlobals.LOGGER.error("Failed to save config file: {}", path, e);
		}
	}

	public boolean load() {
		if (!Files.exists(getPath())) {
			save();
			return true;
		}

		List<String> lines;
		try {
			lines = Files.readAllLines(getPath());
		} catch (IOException e) {
			SpecterGlobals.LOGGER.error("Failed to load config file {}. Default values will be used instead.", getPath().toString());
			return false;
		}

		lines.removeIf(line -> line.trim().startsWith("//"));
		String json = String.join("\n", lines);

		JsonElement jsonElement;
		try {
			jsonElement = JsonParser.parseString(json);
		} catch (JsonSyntaxException e) {
			SpecterGlobals.LOGGER.error("Failed to parse config file: {}", getPath());
			SpecterGlobals.LOGGER.error(e.toString());
			return false;
		}

		DataResult<T> result = parse(JsonOps.INSTANCE, jsonElement);
		if (result.error().isPresent()) {
			SpecterGlobals.LOGGER.error("Failed to decode config file: {}", getPath());
			SpecterGlobals.LOGGER.error(result.error().toString());

			return false;
		}

		return true;
	}

	public Path getPath() {
		return Paths.get(
			FabricLoader.getInstance().getConfigDir().toString(),
			"",
			String.format("%s.%s", getId().getPath(), "json")
		);
	}

	@ApiStatus.Internal
	public Stream<Value<?>> getValues() {
		return Arrays.stream(this.getClass().getDeclaredFields())
			.filter(field ->
				Value.class.isAssignableFrom(field.getType()) &&
					!Modifier.isStatic(field.getModifiers()) &&
					!Modifier.isFinal(field.getModifiers())
			)
			.<Value<?>>map(field -> ReflectionHelper.getFieldValue(this, field))
			.filter(Objects::nonNull);
	}

	@SuppressWarnings("unchecked")
	public T packetDecode(ByteBuf buf) {
		getValues()
			.filter(Value::sync)
			.forEach(value -> value.packetDecode(buf));

		return (T) this;
	}

	public void packetEncode(ByteBuf buf) {
		Identifier.PACKET_CODEC.encode(buf, getId());
		getValues()
			.filter(Value::sync)
			.forEach(value -> value.packetEncode(buf));
	}

	public interface Value<T> {
		T get();

		T defaultValue();

		void set(T value);

		@ApiStatus.Internal
		void init(String name);

		<T1> RecordBuilder<T1> encode(DynamicOps<T1> ops, RecordBuilder<T1> builder);

		<T1> boolean decode(DynamicOps<T1> ops, T1 input);

		void packetDecode(ByteBuf buf);

		void packetEncode(ByteBuf buf);

		Optional<String> comment();

		boolean sync();

		Pair<T, T> range();

		String translationKey(Identifier configId);
	}

	protected static class ValueBuilder<T> {
		protected final T defaultValue;
		protected final Codec<T> codec;
		protected String comment;
		protected boolean sync;
		protected PacketCodec<ByteBuf, T> packetCodec;

		public ValueBuilder(T defaultValue, Codec<T> codec) {
			this.defaultValue = defaultValue;
			this.codec = codec;
		}

		public ValueBuilder<T> comment(String comment) {
			this.comment = comment;
			return this;
		}

		public ValueBuilder<T> sync() {
			if (packetCodec == null) throw new IllegalStateException("Packet codec must be set to enable syncing");
			this.sync = true;
			return this;
		}

		public ValueBuilder<T> packetCodec(PacketCodec<ByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public ValueBuilder<List<T>> toList() {
			return new ValueBuilder<>(List.of(defaultValue), Codec.list(codec));
		}

		public Value<T> build() {
			return new ValueImpl<>(defaultValue, codec, packetCodec, comment, sync, null);
		}
	}

	protected static class RangedValueBuilder<T extends Number> {
		protected final T defaultValue;
		protected final Codec<T> codec;
		private final RangeFunction<T, Codec<T>> codecRange;
		protected String comment;
		protected boolean sync;
		protected PacketCodec<ByteBuf, T> packetCodec;
		protected Pair<T, T> range;

		public RangedValueBuilder(T defaultValue, Codec<T> codec, RangeFunction<T, Codec<T>> codecRange) {
			this.defaultValue = defaultValue;
			this.codec = codec;
			this.codecRange = codecRange;
		}

		public RangedValueBuilder<T> range(T min, T max) {
			this.range = Pair.of(min, max);
			return this;
		}

		public RangedValueBuilder<T> comment(String comment) {
			this.comment = comment;
			return this;
		}

		public RangedValueBuilder<T> sync() {
			if (packetCodec == null) throw new IllegalStateException("Packet codec must be set to enable syncing");
			this.sync = true;
			return this;
		}

		public RangedValueBuilder<T> packetCodec(PacketCodec<ByteBuf, T> packetCodec) {
			this.packetCodec = packetCodec;
			return this;
		}

		public Value<T> build() {
			Codec<T> rangeCodec = range == null ? codec :
				Optional.ofNullable(codecRange).map(f -> f.apply(range.getFirst(), range.getSecond())).orElse(codec);
			return new ValueImpl<>(defaultValue, rangeCodec, packetCodec, comment, sync, range);
		}

		@FunctionalInterface
		public interface RangeFunction<T, R> {
			R apply(T min, T max);
		}
	}
}
