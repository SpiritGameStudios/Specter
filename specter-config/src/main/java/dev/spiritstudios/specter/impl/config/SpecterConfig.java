package dev.spiritstudios.specter.impl.config;

import dev.spiritstudios.specter.api.config.ConfigManager;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SpecterConfig implements ModInitializer {
	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(
			ConfigSyncS2CPayload.ID,
			ConfigSyncS2CPayload.CODEC
		);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Map<Identifier, String> configs = new Object2ObjectOpenHashMap<>();
			ConfigManager.getConfigs().forEach(config -> configs.put(config.getId(), ConfigManager.GSON_NON_SYNC.toJson(config)));
			ConfigSyncS2CPayload payload = new ConfigSyncS2CPayload(configs);
			ServerPlayNetworking.send(handler.getPlayer(), payload);
		});

		// TODO: Reload configs when datapacks are reloaded
	}
}
