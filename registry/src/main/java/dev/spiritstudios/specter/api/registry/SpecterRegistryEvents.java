package dev.spiritstudios.specter.api.registry;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class SpecterRegistryEvents {
	public static final Event<RegistriesFrozenCallback> REGISTRIES_FROZEN = EventFactory.createArrayBacked(
			RegistriesFrozenCallback.class,
			callbacks -> () -> {
				for (RegistriesFrozenCallback callback : callbacks) {
					callback.onRegistriesFrozen();
				}
			}
	);

	@FunctionalInterface
	public interface RegistriesFrozenCallback {
		void onRegistriesFrozen();
	}
}
