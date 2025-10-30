package dev.spiritstudios.specter.impl.config;

import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;

public final class ConfigHolderRegistry {
	private static final Map<ResourceLocation, ConfigHolder<?, ?>> holders = new Object2ObjectOpenHashMap<>();

	public static void register(ResourceLocation id, ConfigHolder<?, ?> holder) {
		holders.put(id, holder);
	}

	public static ConfigHolder<?, ?> get(ResourceLocation id) {
		return holders.get(id);
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
