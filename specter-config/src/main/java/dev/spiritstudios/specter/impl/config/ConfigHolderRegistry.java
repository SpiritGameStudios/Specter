package dev.spiritstudios.specter.impl.config;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.config.Config;
import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;

public final class ConfigHolderRegistry {
	private static final Map<Identifier, ConfigHolder<?, ?>> holders = new Object2ObjectOpenHashMap<>();

	public static void register(Identifier id, ConfigHolder<?, ?> holder) {
		holders.put(id, holder);
	}

	public static ConfigHolder<?, ?> get(Identifier id) {
		return holders.get(id);
	}

	public static void clearOverrides() {
		holders.values().forEach(holder -> clearOverrides(holder.get()));
	}

	private static void clearOverrides(Config config) {
		config.values().values().forEach(either -> either
				.ifLeft(value -> value.override(null))
				.ifRight(ConfigHolderRegistry::clearOverrides));
	}


	public static void reload() {
		holders.values().forEach(ConfigHolder::load);
		ConfigSyncS2CPayload.clearCache();
	}

	public static List<ConfigSyncS2CPayload> createPayloads() {
		return holders.values().stream()
				.filter(holder -> holder.get().shouldSync())
				.map(ConfigSyncS2CPayload::new)
				.toList();
	}
}
