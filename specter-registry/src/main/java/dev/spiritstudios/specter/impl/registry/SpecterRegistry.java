package dev.spiritstudios.specter.impl.registry;

import dev.spiritstudios.specter.impl.registry.metatag.data.MetatagReloader;
import dev.spiritstudios.specter.impl.registry.metatag.network.MetatagSyncS2CPayload;
import dev.spiritstudios.specter.impl.registry.reloadable.SpecterReloadableRegistriesImpl;
import dev.spiritstudios.specter.impl.registry.reloadable.network.ReloadableRegistrySyncS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class SpecterRegistry implements ModInitializer {
	private static MinecraftServer server;

	@ApiStatus.Internal
	public static MinecraftServer getServer() {
		return server;
	}


	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new MetatagReloader(ResourceType.SERVER_DATA));

		PayloadTypeRegistry.playS2C().register(MetatagSyncS2CPayload.ID, MetatagSyncS2CPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(ReloadableRegistrySyncS2CPayload.ID, ReloadableRegistrySyncS2CPayload.CODEC);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			MetatagSyncS2CPayload.getOrCreatePayloads()
				.forEach(sender::sendPacket);

			ReloadableRegistrySyncS2CPayload.getOrCreatePayloads(server)
				.forEach(sender::sendPacket);
		});

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			SpecterReloadableRegistriesImpl.setLookup((RegistryWrapper.WrapperLookup) server.getReloadableRegistries().createRegistryLookup());

			MetatagSyncS2CPayload.clearCache();
			List<MetatagSyncS2CPayload<?, ?>> metatagPayloads = MetatagSyncS2CPayload.getOrCreatePayloads();

			ReloadableRegistrySyncS2CPayload.clearCache();
			List<ReloadableRegistrySyncS2CPayload> reloadableRegistryPayloads = ReloadableRegistrySyncS2CPayload.getOrCreatePayloads(server);

			PlayerLookup.all(server).forEach(player -> {
				metatagPayloads.forEach(payload -> ServerPlayNetworking.send(player, payload));

				reloadableRegistryPayloads.forEach(payload -> ServerPlayNetworking.send(player, payload));
			});
		});

		ServerLifecycleEvents.SERVER_STARTING.register(server -> SpecterRegistry.server = server);
		ServerLifecycleEvents.SERVER_STARTED.register(server -> SpecterReloadableRegistriesImpl.setLookup((RegistryWrapper.WrapperLookup) server.getReloadableRegistries().createRegistryLookup()));
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			if (SpecterRegistry.server == server) SpecterRegistry.server = null;
		});
	}
}

