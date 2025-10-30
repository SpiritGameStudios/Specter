package dev.spiritstudios.specter.impl.core.client;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import dev.spiritstudios.specter.api.core.client.event.ClientKeybindEvents;

public class ClientKeybindEventsImpl {
	private static final Map<KeyMapping, Event<ClientKeybindEvents.KeybindListener>> EVENTS = new Object2ObjectOpenHashMap<>();

	public static Event<ClientKeybindEvents.KeybindListener> pressed(KeyMapping keybind) {
		return EVENTS.computeIfAbsent(
				keybind,
				(key) -> EventFactory.createArrayBacked(
						ClientKeybindEvents.KeybindListener.class,
						(listeners) -> (client) -> {
							for (ClientKeybindEvents.KeybindListener listener : listeners) listener.onKeybind(client);
						})
		);
	}

	public static void tick(Minecraft client) {
		EVENTS.forEach((key, value) -> {
			while (key.consumeClick()) value.invoker().onKeybind(client);
		});
	}
}
