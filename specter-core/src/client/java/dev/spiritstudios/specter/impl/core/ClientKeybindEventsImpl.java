package dev.spiritstudios.specter.impl.core;

import dev.spiritstudios.specter.api.core.util.ClientKeybindEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.Map;

public class ClientKeybindEventsImpl {
	private static final Map<KeyBinding, Event<ClientKeybindEvents.KeybindListener>> EVENTS = new Object2ObjectOpenHashMap<>();

	public static Event<ClientKeybindEvents.KeybindListener> pressed(KeyBinding keybind) {
		return EVENTS.computeIfAbsent(
			keybind,
			(key) -> EventFactory.createArrayBacked(
				ClientKeybindEvents.KeybindListener.class,
				(listeners) -> (client) -> {
					for (ClientKeybindEvents.KeybindListener listener : listeners) listener.onKeybind(client);
				})
		);
	}

	public static void tick(MinecraftClient client) {
		EVENTS.forEach((key, value) -> {
			while (key.wasPressed()) value.invoker().onKeybind(client);
		});
	}
}
