package dev.spiritstudios.specter.api.core.client.event;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import net.fabricmc.fabric.api.event.Event;

import dev.spiritstudios.specter.impl.core.client.ClientKeybindEventsImpl;

/**
 * Allows for listening to keybind events instead of having to check for pressed keys in the tick event.
 */
public final class ClientKeybindEvents {
	private ClientKeybindEvents() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static Event<KeybindListener> pressed(KeyBinding keybind) {
		return ClientKeybindEventsImpl.pressed(keybind);
	}

	@FunctionalInterface
	public interface KeybindListener {
		void onKeybind(MinecraftClient client);
	}
}
