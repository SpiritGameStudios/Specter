package dev.spiritstudios.specter.api.config;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public final class ConfigManager {
	private static final Map<Identifier, Config<?>> configs = new Object2ObjectOpenHashMap<>();

	/**
	 * Get a config file. If the file does not exist, it will be created and saved.
	 * Could also be described as a load function.
	 *
	 * @param clazz The class of the config file
	 * @param <T>   The type of the config file
	 * @return The config file
	 */
	public static <T extends Config<T>> T getConfig(Class<T> clazz) {
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
		JsonObject json = JsonHelper.deserialize(stringBuilder.toString());
		T loadedConfig = config.parse(JsonOps.INSTANCE, json).getOrThrow();

		// Save to make sure any new fields are added
		loadedConfig.save();
		ConfigSyncS2CPayload.clearCache();

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
	public static <T extends Config<T>> T getConfigById(Identifier id) {
		return (T) configs.get(id);
	}

	@SuppressWarnings("unchecked")
	public static void reloadConfigs() {
		Map<Identifier, Config<?>> oldConfigs = new Object2ObjectOpenHashMap<>(configs);
		oldConfigs.values().stream().map(Config::getClass).forEach(ConfigManager::getConfig);

		ConfigSyncS2CPayload.clearCache();
	}

	public static void reloadConfigs(MinecraftServer server) {
		reloadConfigs();
		List<ConfigSyncS2CPayload> payloads = ConfigSyncS2CPayload.createPayloads();

		server.getPlayerManager().getPlayerList().forEach(
			player -> payloads.forEach(payload -> ServerPlayNetworking.send(player, payload)));
	}

	@ApiStatus.Internal
	public static Map<Identifier, Config<?>> getConfigs() {
		return configs;
	}
}
