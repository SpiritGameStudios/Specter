package dev.spiritstudios.specter.impl.config;

import dev.spiritstudios.specter.impl.config.network.ConfigSyncS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.List;

public class SpecterConfig implements ModInitializer {
	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(
			ConfigSyncS2CPayload.ID,
			ConfigSyncS2CPayload.CODEC
		);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			List<ConfigSyncS2CPayload> payloads = ConfigSyncS2CPayload.createPayloads();
			payloads.forEach(sender::sendPacket);
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
			ConfigManager.reload();
			ConfigSyncS2CPayload.sendPayloadsToAll(server);
		});
	}
}
