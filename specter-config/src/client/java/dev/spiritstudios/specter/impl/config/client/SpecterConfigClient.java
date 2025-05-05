package dev.spiritstudios.specter.impl.config.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.impl.config.ConfigHolderRegistry;
import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;

public class SpecterConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConfigHolderRegistry.reload());

		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPayload.ID, (payload, context) -> {
			SpecterGlobals.debug("Received config sync packet");
			SpecterGlobals.debug("Payload: %s".formatted(payload));
		});
	}
}
