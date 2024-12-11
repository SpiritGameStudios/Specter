package dev.spiritstudios.specter.api.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.reflect.Ignore;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.api.serialization.SpecterCodecs;
import dev.spiritstudios.specter.api.serialization.SpecterPacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A configuration class that can be saved and loaded from disk.
 * <p>
 * To create a new configuration class, extend this class and add final fields of type {@link Value} to it.
 * You can use the provided static methods to create values of different types, or create your own with the {@link #value(Object, Codec)} method.
 * Once you have created your configuration class, you can save and load it using {@link ConfigHolder}.
 * </p>
 *
 * @param <T> The type of the configuration class. This must be the same as the class that extends this class.
 */
public abstract class Config<T extends Config<T>> implements Codec<T> {
	private List<ReflectionHelper.FieldValuePair<Value<?>>> fields;

	/**
	 * Creates a new value with the given default value and codec.
	 *
	 * @param defaultValue The default value.
	 * @param codec        The codec used to serialize and deserialize the value.
	 * @param <T>          The type of the value.
	 * @return A new value builder.
	 */
	protected static <T> Value.Builder<T> value(T defaultValue, Codec<T> codec) {
		return new Value.Builder<>(defaultValue, codec);
	}

	/**
	 * Creates a new enum value with the given default value, creating a codec and packet codec using the given enum class.
	 *
	 * @param defaultValue The default value.
	 * @param clazz        The enum class used to create the codec and packet codec.
	 * @param <T>          The type of the enum.
	 * @return A new value builder.
	 */
	protected static <T extends Enum<T>> Value.Builder<T> enumValue(T defaultValue, Class<T> clazz) {
		return value(defaultValue, SpecterCodecs.enumCodec(clazz)).packetCodec(SpecterPacketCodecs.enumCodec(clazz));
	}

	/**
	 * Creates a new boolean value with the given default value.
	 * The codec and packet codec are set to {@link Codec#BOOL} and {@link PacketCodecs#BOOL} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static Value.Builder<Boolean> booleanValue(boolean defaultValue) {
		return value(defaultValue, Codec.BOOL).packetCodec(PacketCodecs.BOOL);
	}

	/**
	 * Creates a new integer value with the given default value and a default range of 0 to 100.
	 * The codec and packet codec are set to {@link Codec#INT} and {@link PacketCodecs#INTEGER} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static NumericValue.Builder<Integer> intValue(int defaultValue) {
		return new NumericValue.Builder<>(defaultValue, Codec.INT)
			.codecRange(SpecterCodecs::clampedRange)
			.range(0, 100)
			.packetCodec(PacketCodecs.INTEGER);
	}

	/**
	 * Creates a new float value with the given default value and a default range of 0 to 1.
	 * The codec and packet codec are set to {@link Codec#FLOAT} and {@link PacketCodecs#FLOAT} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static NumericValue.Builder<Float> floatValue(float defaultValue) {
		return new NumericValue.Builder<>(defaultValue, Codec.FLOAT)
			.codecRange(SpecterCodecs::clampedRange)
			.range(0.0F, 1.0F)
			.packetCodec(PacketCodecs.FLOAT);
	}

	/**
	 * Creates a new double value with the given default value and a default range of 0 to 1.
	 * The codec and packet codec are set to {@link Codec#DOUBLE} and {@link PacketCodecs#DOUBLE} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static NumericValue.Builder<Double> doubleValue(double defaultValue) {
		return new NumericValue.Builder<>(defaultValue, Codec.DOUBLE)
			.codecRange(SpecterCodecs::clampedRange)
			.range(0.0, 1.0)
			.packetCodec(PacketCodecs.DOUBLE);
	}

	/**
	 * Creates a new string value with the given default value.
	 * The codec and packet codec are set to {@link Codec#STRING} and {@link PacketCodecs#STRING} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static Value.Builder<String> stringValue(String defaultValue) {
		return new Value.Builder<>(defaultValue, Codec.STRING).packetCodec(PacketCodecs.STRING);
	}

	/**
	 * Creates a new value for a registry entry with the given default value and registry.
	 * This will be stored in the config as an {@link Identifier}.
	 *
	 * @param defaultValue The default value.
	 * @param registry     The registry used to create the codec.
	 * @param <T>          The type of the value.
	 * @return A new value builder.
	 */
	protected static <T> Value.Builder<T> registryValue(T defaultValue, Registry<T> registry) {
		return value(defaultValue, registry.getCodec());
	}

	/**
	 * Creates a new nested config value with the given class.
	 *
	 * @param clazz The class of the nested config.
	 * @param <T>   The type of the nested config.
	 * @return A new nested value builder.
	 */
	protected static <T extends NestedConfig<T>> Value.NestedBuilder<T> nestedValue(Class<T> clazz) {
		return new Value.NestedBuilder<>(clazz);
	}

	@ApiStatus.Internal
	public List<ReflectionHelper.FieldValuePair<Value<?>>> fields() {
		if (fields == null) {
			fields = Arrays.stream(this.getClass().getDeclaredFields()).map(field -> {
				if (field.isAnnotationPresent(Ignore.class)) return null;

				if (!Value.class.isAssignableFrom(field.getType()) ||
					Modifier.isStatic(field.getModifiers()) ||
					!Modifier.isFinal(field.getModifiers())) return null;

				Value<?> value = ReflectionHelper.getFieldValue(this, field);
				if (value == null) return null;

				return new ReflectionHelper.FieldValuePair<Value<?>>(
					field,
					value
				);
			}).filter(Objects::nonNull).toList();
		}
		return fields;
	}

	@ApiStatus.Internal
	@SuppressWarnings("unchecked")
	public PacketCodec<ByteBuf, T> packetCodec() {
		return PacketCodec.of(
			(value, buf) -> value.fields().forEach(pair -> {
				if (!pair.value().sync()) return;
				pair.value().packetEncode(buf);
			}),
			(buf) -> {
				fields().forEach(pair -> {
					if (!pair.value().sync()) return;
					pair.value().packetDecode(buf);
				});
				return (T) this;
			}
		);
	}

	@Override
	public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
		RecordBuilder<T1> builder = ops.mapBuilder();
		for (ReflectionHelper.FieldValuePair<Value<?>> value : fields()) builder = value.value().encode(ops, builder);
		return builder.build(prefix);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
		for (ReflectionHelper.FieldValuePair<Value<?>> pair : fields()) {
			if (pair.value().decode(ops, input)) continue;
			SpecterGlobals.LOGGER.error(
				"Failed to decode config value \"{}\". Resetting to default value",
				pair.value().name()
			);
			pair.value().reset();
		}

		return DataResult.success(Pair.of((T) this, input));
	}
}
