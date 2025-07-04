package dev.spiritstudios.specter.api.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.config.gui.GuiHint;
import dev.spiritstudios.specter.api.core.reflect.Ignore;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.api.core.util.SpecterPacketCodecs;
import dev.spiritstudios.specter.api.serialization.SpecterCodecs;

/**
 * A configuration class that can be saved and loaded from disk.
 * <p>
 * To create a new configuration class, extend this class and add final fields of type {@link Value} to it.
 * You can use the provided static methods to create values of different types, or create your own with the {@link #value(Object, Codec)} method.
 * Once you have created your configuration class, you can save and load it using {@link ConfigHolder}.
 * </p>
 */
public abstract class Config {
	private @Nullable Map<String, Either<Value<?>, SubConfig>> values = null;
	private @Nullable Boolean shouldSync = null;

	// region Value builders

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
		return value(defaultValue, SpecterCodecs.enumCodec(clazz))
				.packetCodec(SpecterPacketCodecs.enumCodec(clazz).cast());
	}

	/**
	 * Creates a new boolean value with the given default value.
	 * The codec and packet codec are set to {@link Codec#BOOL} and {@link PacketCodecs#BOOLEAN} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static Value.Builder<Boolean> booleanValue(boolean defaultValue) {
		return value(defaultValue, Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN.cast());
	}

	/**
	 * Creates a new integer value with the given default value.
	 * The codec and packet codec are set to {@link Codec#INT} and {@link PacketCodecs#INTEGER} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static Value.Builder<Integer> intValue(int defaultValue) {
		return new Value.Builder<>(defaultValue, Codec.INT).packetCodec(PacketCodecs.INTEGER.cast());
	}

	/**
	 * Creates a new float value with the given default value.
	 * The codec and packet codec are set to {@link Codec#FLOAT} and {@link PacketCodecs#FLOAT} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static Value.Builder<Float> floatValue(float defaultValue) {
		return new Value.Builder<>(defaultValue, Codec.FLOAT).packetCodec(PacketCodecs.FLOAT.cast());
	}

	/**
	 * Creates a new double value with the given default value.
	 * The codec and packet codec are set to {@link Codec#DOUBLE} and {@link PacketCodecs#DOUBLE} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static Value.Builder<Double> doubleValue(double defaultValue) {
		return new Value.Builder<>(defaultValue, Codec.DOUBLE).packetCodec(PacketCodecs.DOUBLE.cast());
	}

	/**
	 * Creates a new string value with the given default value.
	 * The codec and packet codec are set to {@link Codec#STRING} and {@link PacketCodecs#STRING} respectively.
	 *
	 * @param defaultValue The default value.
	 * @return A new value builder.
	 */
	protected static Value.Builder<String> stringValue(String defaultValue) {
		return new Value.Builder<>(defaultValue, Codec.STRING).packetCodec(PacketCodecs.STRING.cast());
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
		return value(defaultValue, registry.getCodec())
				.packetCodec(PacketCodecs.registryValue(registry.getKey()));
	}
	// endregion

	public boolean shouldSync() {
		if (shouldSync == null) {
			shouldSync = values().values()
					.stream()
					.anyMatch(either -> either.map(
							Value::sync,
							Config::shouldSync
					));
		}

		return shouldSync;
	}

	public Map<String, Either<Value<?>, SubConfig>> values() {
		if (this.values == null) {
			ImmutableMap.Builder<String, Either<Value<?>, SubConfig>> builder = ImmutableMap.builder();

			List<Field> fields = Arrays.stream(this.getClass().getDeclaredFields())
					.filter(field ->
							Value.class.isAssignableFrom(field.getType()) ||
									SubConfig.class.isAssignableFrom(field.getType()))
					.filter(field -> !field.isAnnotationPresent(Ignore.class))
					.filter(field -> !Modifier.isStatic(field.getModifiers()) &&
							Modifier.isFinal(field.getModifiers()) &&
							!Modifier.isTransient(field.getModifiers()) &&
							!field.isSynthetic())
					.toList();

			for (Field field : fields) {
				Optional<Object> fieldValue = ReflectionHelper.getFieldValue(this, field);
				if (fieldValue.isEmpty()) continue;

				if (fieldValue.get() instanceof Value<?> value) {
					builder.put(field.getName(), Either.left(value));
				} else if (fieldValue.get() instanceof SubConfig subConfig) {
					builder.put(field.getName(), Either.right(subConfig));
				}
			}

			this.values = builder.build();
		}

		return values;
	}

	/**
	 * A config that can be nested inside another config.
	 */
	public abstract static class SubConfig extends Config {
		private final Map<Class<?>, GuiHint<SubConfig>> guiHints;
		private final @Nullable String comment;

		public SubConfig(@Nullable String comment) {
			this.guiHints = Collections.emptyMap();
			this.comment = comment;
		}

		@SafeVarargs
		public SubConfig(GuiHint<SubConfig>... guiHints) {
			this(null, guiHints);
		}

		@SafeVarargs
		public SubConfig(@Nullable String comment, GuiHint<SubConfig>... guiHints) {
			if (guiHints.length == 0) {
				this.guiHints = Collections.emptyMap();
			} else {
				ImmutableMap.Builder<Class<?>, GuiHint<SubConfig>> builder = ImmutableMap.builder();

				for (GuiHint<SubConfig> guiHint : guiHints) {
					builder.put(guiHint.getClass(), guiHint);
				}

				this.guiHints = builder.build();
			}

			this.comment = comment;
		}

		public Optional<String> comment() {
			return Optional.ofNullable(comment);
		}

		public <H extends GuiHint<SubConfig>> Optional<H> hint(Class<H> clazz) {
			GuiHint<SubConfig> guiHint = guiHints.get(clazz);
			if (guiHint == null) return Optional.empty();
			return ReflectionHelper.cast(guiHint, clazz);
		}
	}
}
