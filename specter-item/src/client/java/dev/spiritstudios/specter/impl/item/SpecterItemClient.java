package dev.spiritstudios.specter.impl.item;

import dev.spiritstudios.specter.api.registry.reloadable.ClientReloadableRegistryEvents;
import net.fabricmc.api.ClientModInitializer;

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
