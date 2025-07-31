package dev.spiritstudios.specter.impl.registry;

import net.minecraft.resource.ResourceType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import dev.spiritstudios.specter.api.serialization.SplitPayloadHandler;
import dev.spiritstudios.specter.impl.core.Specter;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagsS2CPayload;
import dev.spiritstudios.specter.impl.registry.metatag.data.MetatagReloader;

public class SpecterRegistry implements ModInitializer {
	public static final SplitPayloadHandler<MetatagsS2CPayload> METATAGS_SYNC = new SplitPayloadHandler<>(
			Specter.id("metatags"),
			MetatagsS2CPayload.CODEC
	);


	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
				MetatagReloader.ID,
				MetatagReloader::new
		);

		METATAGS_SYNC.register(PayloadTypeRegistry.playS2C());

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			METATAGS_SYNC.send(
					MetatagsS2CPayload.getOrCreatePayload(server.getRegistryManager()),
					sender::sendPacket,
					server.getRegistryManager()
			);
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			MetatagsS2CPayload.clearCache();
			MetatagsS2CPayload metatagPayload = MetatagsS2CPayload.getOrCreatePayload(server.getRegistryManager());

			PlayerLookup.all(server).forEach(player -> {
				METATAGS_SYNC.send(
						metatagPayload,
						payload -> ServerPlayNetworking.send(player, payload),
						server.getRegistryManager()
				);
			});
		});
	}
}
