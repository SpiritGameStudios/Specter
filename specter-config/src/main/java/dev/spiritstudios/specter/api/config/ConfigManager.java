package dev.spiritstudios.specter.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.NonSyncExclusionStrategy;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public final class ConfigManager {
	@ApiStatus.Internal
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@ApiStatus.Internal
	public static final Gson GSON_NON_SYNC = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(new NonSyncExclusionStrategy()).create();

	private static final Map<Identifier, Config> configs = new Object2ObjectOpenHashMap<>();

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
			configs.put(config.getId(), config);
			return config;
		}

		List<String> lines;
		try {
			lines = Files.readAllLines(config.getPath());
		} catch (IOException e) {
			SpecterGlobals.LOGGER.error("Failed to load config file {}. Default values will be used instead.", config.getPath().toString());
			configs.put(config.getId(), config);
			return config;
		}

		lines.removeIf(line -> line.trim().startsWith("//"));
		StringBuilder stringBuilder = new StringBuilder();
		lines.forEach(stringBuilder::append);

		T loadedConfig = GSON.fromJson(stringBuilder.toString(), clazz);

		// Save to make sure any new fields are added
		loadedConfig.save();
		CACHED_PAYLOAD = null;

		T existingConfig = getConfigById(loadedConfig.getId());
		if (existingConfig != null) {
			for (Field field : clazz.getDeclaredFields())
				ReflectionHelper.setFieldValue(existingConfig, field, ReflectionHelper.getFieldValue(loadedConfig, field));

			return existingConfig;
		}

		configs.put(loadedConfig.getId(), loadedConfig);
		return loadedConfig;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Config> T getConfigById(Identifier id) {
		return (T) configs.get(id);
	}

	public static void reloadConfigs() {
		Map<Identifier, Config> oldConfigs = new Object2ObjectOpenHashMap<>(configs);
		oldConfigs.values().stream().map(Config::getClass).forEach(ConfigManager::getConfig);

		CACHED_PAYLOAD = null;
	}

	public static void reloadConfigs(MinecraftServer server) {
		reloadConfigs();
		ConfigSyncS2CPayload payload = ConfigManager.createSyncPayload();
		server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, payload));
	}

	@ApiStatus.Internal
	public static Map<Identifier, Config> getConfigs() {
		return configs;
	}

	private static ConfigSyncS2CPayload CACHED_PAYLOAD;

	@ApiStatus.Internal
	public static ConfigSyncS2CPayload createSyncPayload() {
		if (CACHED_PAYLOAD != null) return CACHED_PAYLOAD;

		Map<Identifier, String> configs = getConfigs().values().stream().collect(Object2ObjectOpenHashMap::new, (map, config) -> map.put(config.getId(), GSON_NON_SYNC.toJson(config)), Map::putAll);
		return CACHED_PAYLOAD = new ConfigSyncS2CPayload(configs);
	}
}
