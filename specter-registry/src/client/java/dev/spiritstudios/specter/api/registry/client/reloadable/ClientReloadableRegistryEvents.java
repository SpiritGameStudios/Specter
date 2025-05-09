package dev.spiritstudios.specter.api.registry.client.reloadable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientReloadableRegistryEvents {
	public static final Event<SyncFinished> SYNC_FINISHED = EventFactory.createArrayBacked(SyncFinished.class, callbacks -> (client, manager) -> {
		for (SyncFinished callback : callbacks) callback.onSyncFinished(client, manager);
	});

	@FunctionalInterface
	public interface SyncFinished {
		void onSyncFinished(MinecraftClient client, DynamicRegistryManager.Immutable manager);
	}
}
