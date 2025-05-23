package dev.spiritstudios.specter.api.registry.metatag;

import net.minecraft.resource.ResourceManager;

import net.fabricmc.fabric.api.event.Event;

import dev.spiritstudios.specter.impl.registry.metatag.MetatagEventsImpl;

public final class MetatagEvents {
	/**
	 * Returns an event that is invoked when the supplied metatag is loaded or reloaded.
	 *
	 * @implNote This event will be invoked on the logical side of the metatag passed to it.
	 */
	public static Event<MetatagLoaded> metatagLoadedEvent(Metatag<?, ?> metatag) {
		return MetatagEventsImpl.getOrCreateLoadedEvent(metatag);
	}

	@FunctionalInterface
	public interface MetatagLoaded {
		void onMetatagLoaded(ResourceManager resourceManager);
	}
}
