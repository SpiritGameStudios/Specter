package dev.spiritstudios.specter.impl.config.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import dev.spiritstudios.specter.impl.config.ConfigHolderRegistry;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import dev.spiritstudios.specter.impl.core.Specter;

public class SpecterConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConfigHolderRegistry.reload());

		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPayload.ID, (payload, context) -> {
			Specter.debug("Received config sync packet");
			Specter.debug("Payload: %s".formatted(payload));
		});
	}
}
