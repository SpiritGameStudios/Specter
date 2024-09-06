package dev.spiritstudios.specter.impl.config;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SpecterConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConfigManager.reload());

		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPayload.ID, (payload, context) -> {
			SpecterGlobals.LOGGER.info("Received config sync packet");
			SpecterGlobals.LOGGER.info("Payload: {}", payload);
		});
	}
}
