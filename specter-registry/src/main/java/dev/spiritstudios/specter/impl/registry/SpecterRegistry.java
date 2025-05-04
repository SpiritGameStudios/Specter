package dev.spiritstudios.specter.impl.registry;

import dev.spiritstudios.specter.api.core.SpecterGlobals;
import dev.spiritstudios.specter.api.serialization.SplitPayloadHandler;
import dev.spiritstudios.specter.impl.registry.metatag.MetatagsS2CPayload;
import dev.spiritstudios.specter.impl.registry.metatag.data.MetatagReloader;
import dev.spiritstudios.specter.impl.registry.reloadable.ReloadableRegistriesS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class SpecterRegistry implements ModInitializer {
	public static final SplitPayloadHandler<ReloadableRegistriesS2CPayload> RELOADABLE_REGISTRIES_SYNC = new SplitPayloadHandler<>(
		Identifier.of(SpecterGlobals.MODID, "reloadable_registries"),
		ReloadableRegistriesS2CPayload.CODEC
	);

	public static final SplitPayloadHandler<MetatagsS2CPayload> METATAGS_SYNC = new SplitPayloadHandler<>(
		Identifier.of(SpecterGlobals.MODID, "metatags"),
		MetatagsS2CPayload.CODEC
	);


	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MetatagReloader(ResourceType.SERVER_DATA));

		METATAGS_SYNC.register(PayloadTypeRegistry.playS2C());
		RELOADABLE_REGISTRIES_SYNC.register(PayloadTypeRegistry.playS2C());

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			METATAGS_SYNC.send(
				MetatagsS2CPayload.getOrCreatePayload(),
				sender::sendPacket,
				server.getRegistryManager()
			);

			RELOADABLE_REGISTRIES_SYNC.send(
				ReloadableRegistriesS2CPayload.getOrCreatePayload(),
				sender::sendPacket,
				server.getRegistryManager()
			);
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			MetatagsS2CPayload.clearCache();
			MetatagsS2CPayload metatagPayload = MetatagsS2CPayload.getOrCreatePayload();

			ReloadableRegistriesS2CPayload.clearCache();
			ReloadableRegistriesS2CPayload reloadableRegistryPayload = ReloadableRegistriesS2CPayload.getOrCreatePayload();

			PlayerLookup.all(server).forEach(player -> {
				METATAGS_SYNC.send(
					metatagPayload,
					payload -> ServerPlayNetworking.send(player, payload),
					server.getRegistryManager()
				);

				RELOADABLE_REGISTRIES_SYNC.send(
					reloadableRegistryPayload,
					payload -> ServerPlayNetworking.send(player, payload),
					server.getRegistryManager()
				);
			});
		});
	}
}

