package dev.spiritstudios.specter.impl.item.client;

import net.fabricmc.api.ClientModInitializer;

import dev.spiritstudios.specter.api.registry.client.reloadable.ClientReloadableRegistryEvents;

public class SpecterItemClient implements ClientModInitializer {
	private static boolean justReloaded = false;

	public static boolean justReloaded() {
		return justReloaded;
	}

	public static void reloadDone() {
		SpecterItemClient.justReloaded = false;
	}

	@Override
	public void onInitializeClient() {
		ClientReloadableRegistryEvents.SYNC_FINISHED.register((client, manager) -> justReloaded = true);
	}
}
