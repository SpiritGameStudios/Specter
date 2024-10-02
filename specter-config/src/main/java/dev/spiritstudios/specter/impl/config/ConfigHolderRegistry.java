package dev.spiritstudios.specter.impl.config;

import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public final class ConfigHolderRegistry {
	private static final Map<Identifier, ConfigHolder<?, ?>> holders = new Object2ObjectOpenHashMap<>();

	public static void register(Identifier id, ConfigHolder<?, ?> holder) {
		holders.put(id, holder);
	}

	public static ConfigHolder<?, ?> get(Identifier id) {
		return holders.get(id);
	}


	public static void reload() {
		holders.values().forEach(ConfigHolder::load);
		ConfigSyncS2CPayload.clearCache();
	}

	public static List<ConfigSyncS2CPayload> createPayloads() {
		return holders.values().stream()
			.map(ConfigSyncS2CPayload::new)
			.toList();
	}
}
