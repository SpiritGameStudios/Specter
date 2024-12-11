package dev.spiritstudios.specter.api.registry.metatag;

import dev.spiritstudios.specter.impl.registry.metatag.MetatagEventsImpl;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resource.ResourceManager;

public final class MetatagEvents {
	public static Event<MetatagLoaded> metatagLoadedEvent(Metatag<?, ?> metatag) {
		return MetatagEventsImpl.getOrCreateLoadedEvent(metatag);
	}

	@FunctionalInterface
	public interface MetatagLoaded {
		void onMetatagLoaded(ResourceManager resourceManager);
	}
}
