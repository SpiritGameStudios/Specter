package dev.spiritstudios.specter.impl.registry.metatag;

import java.util.Map;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.api.registry.metatag.MetatagEvents;

public final class MetatagEventsImpl {
	private static final Map<Metatag<?, ?>, Event<MetatagEvents.MetatagLoaded>> METATAG_LOADED_MAP = new Reference2ObjectOpenHashMap<>();

	public static Event<MetatagEvents.MetatagLoaded> getOrCreateLoadedEvent(Metatag<?, ?> metatag) {
		return METATAG_LOADED_MAP.computeIfAbsent(metatag, ignored -> EventFactory.createArrayBacked(MetatagEvents.MetatagLoaded.class, callbacks -> (resourceManager) -> {
			for (MetatagEvents.MetatagLoaded callback : callbacks) {
				callback.onMetatagLoaded(resourceManager);
			}
		}));
	}

	public static Optional<Event<MetatagEvents.MetatagLoaded>> getLoadedEvent(Metatag<?, ?> metatag) {
		return Optional.ofNullable(METATAG_LOADED_MAP.get(metatag));
	}

}
