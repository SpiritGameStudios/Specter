package dev.spiritstudios.specter.api.config;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.api.serialization.format.DynamicFormat;
import dev.spiritstudios.specter.api.serialization.format.JsonCFormat;
import dev.spiritstudios.specter.impl.config.ConfigHolderRegistry;

/**
 * A class that holds config data and provides methods to save and load the config.
 *
 * @param <T> The type of the config.
 * @param <F> The type of the format used for serialization.
 */
public class ConfigHolder<T extends Config, F> {
	private final T config;
	private final Identifier id;
	private final String path;
	private final DynamicFormat<F> format;

	protected ConfigHolder(DynamicFormat<F> language, Identifier id, String path, Class<T> clazz) {
		this.format = language;
		this.id = id;
		this.path = path;

		if (Config.SubConfig.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("Nested configs cannot be registered with config holders");
		}

		ConfigHolder<?, ?> existing = ConfigHolderRegistry.get(id);
		if (existing != null) throw new IllegalStateException("Config with id %s already exists".formatted(id));

		ConfigHolderRegistry.register(id, this);

		this.config = ReflectionHelper.instantiate(clazz);

		if (!load()) {
			SpecterGlobals.LOGGER.error("Failed to load config file: {}, default values will be used", path());
		} else {
			save(); // Save the config to disk to ensure it's up to date
		}
	}

	/**
	 * Creates a new config holder builder.
	 *
	 * @param id    The identifier of the config holder.
	 * @param clazz The class of the config.
	 * @param <T>   The type of the config.
	 * @return A new config holder builder.
	 */
	public static <T extends Config> Builder<T> builder(Identifier id, Class<T> clazz) {
		return new Builder<>(id, clazz);
	}

	/**
	 * Saves the config to disk.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void save() {
		RecordBuilder<F> builder = format.mapBuilder();
		this.encodeConfig(builder, config);

		DataResult<F> result = builder.build(format.empty());

		if (result.error().isPresent()) {
			SpecterGlobals.LOGGER.error("Failed to encode config: {}", id);
			SpecterGlobals.LOGGER.error(result.error().toString());
			return;
		}

		F element = result.result().orElseThrow();

		StringWriter writer = new StringWriter();

		try {
			format.write(writer, element);
		} catch (Exception e) {
			SpecterGlobals.LOGGER.error("Failed to write config: {}", id);
			SpecterGlobals.LOGGER.error(e.toString());
			return;
		}

		List<String> lines = Arrays.asList(writer.toString().split("\n"));

		Path path = this.path();
		path.toFile().getParentFile().mkdirs();

		try {
			Files.write(path, lines);
		} catch (IOException e) {
			SpecterGlobals.LOGGER.error("Failed to save config file: {}", path, e);
		}
	}

	/**
	 * Loads the config from disk.
	 * If the config does not exist, or is invalid, the default values will be used and saved to disk.
	 *
	 * @return Whether the config was successfully loaded.
	 */
	public boolean load() {
		if (!Files.exists(path())) {
			save();
			return true;
		}

		List<String> lines;
		try {
			lines = Files.readAllLines(path());
		} catch (IOException e) {
			SpecterGlobals.LOGGER.error("Failed to load config file {}. Default values will be used instead.", path());
			return false;
		}

		F element;
		try {
			element = format.read(String.join("\n", lines));
		} catch (Exception e) {
			SpecterGlobals.LOGGER.error("Failed to parse config file: {}", path());
			SpecterGlobals.LOGGER.error(e.toString());
			return false;
		}

		this.decodeConfig(element, config);

		return true;
	}

	private void encodeConfig(RecordBuilder<F> builder, Config config) {
		config.values().forEach((key, either) -> {
			either
					.ifLeft(value -> encodeField(format, builder, key, value))
					.ifRight(subConfig -> {
						RecordBuilder<F> subConfigBuilder = format.mapBuilder();
						encodeConfig(subConfigBuilder, subConfig);
						builder.add(key, subConfigBuilder.build(format.empty()));
					});
		});
	}


	private void decodeConfig(F input, Config config) {
		config.values().forEach((key, either) -> {
			either
					.ifLeft(value -> decodeField(format, input, key, value))
					.ifRight(subConfig -> {
						format.getMap(input)
								.ifSuccess(map -> decodeConfig(map.get(key), subConfig))
								.ifError(error -> {
									SpecterGlobals.LOGGER.error(
											"Failed to decode config value \"{}\". Something is very wrong.",
											key
									);
									SpecterGlobals.LOGGER.error(error.message());
								});
					});
		});
	}

	private <V, T1> void encodeField(DynamicOps<T1> ops, RecordBuilder<T1> builder, String key, Value<V> value) {
		builder.add(key, value.codec().encodeStart(ops, value.get()));
	}

	private <V, T1> void decodeField(DynamicOps<T1> ops, T1 input, String key, Value<V> value) {
		value.set(ops.getMap(input).flatMap(map -> value.codec().decode(ops, map.get(key)))
				.mapOrElse(
						Pair::getFirst,
						error -> {
							SpecterGlobals.LOGGER.error(
									"Failed to decode config value \"{}\". Resetting to default value",
									key
							);
							SpecterGlobals.LOGGER.error(error.message());

							return value.defaultValue();
						}
				));
	}

	private Path path() {
		return Paths.get(
				FabricLoader.getInstance().getConfigDir().toString(),
				"",
				path
		);
	}

	@Override
	public String toString() {
		return "ConfigHolder[" + id.toString() + "]";
	}

	/**
	 * Get the config.
	 *
	 * @return The held config.
	 */
	public T get() {
		return config;
	}

	/**
	 * Get the identifier of this holder.
	 *
	 * @return The identifier of this holder.
	 */
	public Identifier id() {
		return id;
	}

	/**
	 * A builder for a new config holder.
	 * The format will default to {@link JsonCFormat}.
	 *
	 * @param <T> The type of the config.
	 */
	public static class Builder<T extends Config> {
		private final Identifier id;
		private final Class<T> clazz;

		private DynamicFormat<?> format = JsonCFormat.INSTANCE;
		private String path;

		protected Builder(Identifier id, Class<T> clazz) {
			this.id = id;
			this.clazz = clazz;

			this.path = id.getPath();
		}

		/**
		 * Set the format of the config holder.
		 *
		 * @param format The format of the config holder.
		 * @return This builder.
		 */
		public Builder<T> format(DynamicFormat<?> format) {
			this.format = format;
			return this;
		}

		/**
		 * Set the path of the config holder.
		 *
		 * @param path The path of the config holder.
		 * @return This builder.
		 */
		public Builder<T> path(String path) {
			this.path = path;
			return this;
		}

		/**
		 * Build the config holder.
		 *
		 * @return The built config holder.
		 */
		public ConfigHolder<T, ?> build() {
			return new ConfigHolder<>(format, id, "%s.%s".formatted(path, format.name()), clazz);
		}
	}
}
