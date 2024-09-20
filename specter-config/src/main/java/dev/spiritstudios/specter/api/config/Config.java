package dev.spiritstudios.specter.api.config;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.CodecHelper;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.ConfigManager;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
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
	private static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.create();

	/**
	 * Do not call this constructor directly, use {@link #create(Class)} instead.
	 */
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
		instance.getValuesFields().forEach(field -> {
			Value<?> value = ReflectionHelper.getFieldValue(instance, field);
			if (value == null) return;

			value.init(field.getName());
			SpecterGlobals.debug("Registered config value: %s".formatted(value.translationKey(instance.getId())));
		});

		if (!instance.load())
			SpecterGlobals.LOGGER.error("Failed to load config file: {}, default values will be used", instance.getPath());
		else
			instance.save(); // Save the config to disk to ensure it's up to date

		return instance;
	}

	protected static <T> Value.Builder<T> value(T defaultValue, Codec<T> codec) {
		return new Value.Builder<>(defaultValue, codec);
	}

	protected static <T extends Enum<T>> Value.Builder<T> enumValue(T defaultValue, Class<T> clazz) {
		return new Value.Builder<>(defaultValue, CodecHelper.createEnumCodec(clazz)).packetCodec(
			CodecHelper.createEnumPacketCodec(clazz)
		);
	}

	protected static Value.Builder<Boolean> booleanValue(boolean defaultValue) {
		return new Value.Builder<>(defaultValue, Codec.BOOL).packetCodec(PacketCodecs.BOOL);
	}

	protected static NumericValue.Builder<Integer> intValue(int defaultValue) {
		return new NumericValue.Builder<>(defaultValue, Codec.INT)
			.codecRange(CodecHelper::clampedRangeInt)
			.range(0, 100)
			.packetCodec(PacketCodecs.INTEGER);
	}

	protected static NumericValue.Builder<Float> floatValue(float defaultValue) {
		return new NumericValue.Builder<>(defaultValue, Codec.FLOAT)
			.codecRange(CodecHelper::clampedRangeFloat)
			.range(0.0F, 1.0F)
			.packetCodec(PacketCodecs.FLOAT);
	}

	protected static NumericValue.Builder<Double> doubleValue(double defaultValue) {
		return new NumericValue.Builder<>(defaultValue, Codec.DOUBLE)
			.codecRange(CodecHelper::clampedRangeDouble)
			.range(0.0, 1.0)
			.packetCodec(PacketCodecs.DOUBLE);
	}

	protected static Value.Builder<String> stringValue(String defaultValue) {
		return new Value.Builder<>(defaultValue, Codec.STRING).packetCodec(PacketCodecs.STRING);
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

		JsonElement element = result.result().orElseThrow();
		String json = GSON.toJson(element);


		Map<String, String> comments = new Object2ObjectOpenHashMap<>();

		getValuesFields().forEach(field -> {
			Value<?> value = ReflectionHelper.getFieldValue(this, field);
			if (value == null) return;

			String comment = value.comment().orElse(null);
			if (comment == null) return;

			comments.put(field.getName(), comment);
		});

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
		String json = String.join("\n", lines);

		JsonElement element;
		try {
			element = JsonParser.parseString(json);
		} catch (JsonSyntaxException e) {
			SpecterGlobals.LOGGER.error("Failed to parse config file: {}", getPath());
			SpecterGlobals.LOGGER.error(e.toString());
			return false;
		}

		DataResult<T> result = parse(JsonOps.INSTANCE, element);
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
	public Stream<Field> getValuesFields() {
		return Arrays.stream(this.getClass().getDeclaredFields())
			.filter(field ->
				Value.class.isAssignableFrom(field.getType()) &&
					!Modifier.isStatic(field.getModifiers()) &&
					!Modifier.isFinal(field.getModifiers())
			);
	}

	@ApiStatus.Internal
	public Stream<Value<?>> getValues() {
		return getValuesFields()
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
}
