package dev.spiritstudios.specter.impl.config;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public final class ConfigManager {
	private static final Map<Identifier, Config<?>> configs = new Object2ObjectOpenHashMap<>();

	public static void registerConfig(Identifier id, Config<?> config) {
		configs.put(id, config);
	}

	public static Config<?> getConfig(Identifier id) {
		return configs.get(id);
	}

	public static void reload() {
		configs.values().forEach(Config::load);
		ConfigSyncS2CPayload.clearCache();
	}


	public static List<Config<?>> getConfigs() {
		return List.copyOf(configs.values());
	}
}
