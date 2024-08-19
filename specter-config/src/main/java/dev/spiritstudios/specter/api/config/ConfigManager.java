package dev.spiritstudios.specter.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.NonSyncExclusionStrategy;
import dev.spiritstudios.specter.impl.core.Specter;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {
	@ApiStatus.Internal
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@ApiStatus.Internal
	public static final Gson GSON_NON_SYNC = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(new NonSyncExclusionStrategy()).create();

	private static final List<Config> configs = new ArrayList<>();

	/**
	 * Get a config file. If the file does not exist, it will be created and saved.
	 * Could also be described as a load function.
	 *
	 * @param clazz The class of the config file
	 * @param <T>   The type of the config file
	 * @return The config file
	 */
	public static <T extends Config> T getConfig(Class<T> clazz) {
		T config = ReflectionHelper.instantiate(clazz);

		if (!Files.exists(config.getPath())) {
			config.save();
			configs.add(config);
			return config;
		}

		List<String> lines;
		try {
			lines = Files.readAllLines(config.getPath());
		} catch (IOException e) {
			Specter.LOGGER.error("Failed to load config file {}. Default values will be used instead.", config.getPath().toString());
			configs.add(config);
			return config;
		}

		lines.removeIf(line -> line.trim().startsWith("//"));
		StringBuilder stringBuilder = new StringBuilder();
		lines.forEach(stringBuilder::append);

		T loadedConfig = GSON.fromJson(stringBuilder.toString(), clazz);

		// Save to make sure any new fields are added
		loadedConfig.save();


		T existingConfig = getConfigById(loadedConfig.getId());
		if (existingConfig != null) {
			for (Field field : clazz.getDeclaredFields())
				ReflectionHelper.setFieldValue(existingConfig, field, ReflectionHelper.getFieldValue(loadedConfig, field));

			return existingConfig;
		}

		configs.add(loadedConfig);
		return loadedConfig;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Config> T getConfigById(Identifier id) {
		for (Config config : configs)
			if (config.getId().equals(id)) return (T) config;

		return null;
	}

	public static void reloadConfigs() {
		List<Config> oldConfigs = new ArrayList<>(configs);
		for (Config config : oldConfigs) getConfig(config.getClass());
	}

	@ApiStatus.Internal
	public static List<Config> getConfigs() {
		return configs;
	}
}
