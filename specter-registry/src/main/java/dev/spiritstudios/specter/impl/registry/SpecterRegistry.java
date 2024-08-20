package dev.spiritstudios.specter.impl.registry;

import dev.spiritstudios.specter.impl.registry.attachment.data.AttachmentReloader;
import dev.spiritstudios.specter.impl.registry.attachment.network.AttachmentSyncS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

public class SpecterRegistry implements ModInitializer {
	private static MinecraftServer server;

	@ApiStatus.Internal
	public static MinecraftServer getServer() {
		return server;
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new AttachmentReloader());

		PayloadTypeRegistry.playS2C().register(AttachmentSyncS2CPayload.ID, AttachmentSyncS2CPayload.CODEC);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
			AttachmentSyncS2CPayload.createPayloads()
				.forEach(sender::sendPacket));

		ServerLifecycleEvents.SERVER_STARTING.register(server -> SpecterRegistry.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			if (SpecterRegistry.server == server) SpecterRegistry.server = null;
		});
	}
}

