package dev.spiritstudios.specter.api.core.util;


import dev.spiritstudios.specter.impl.core.ClientKeybindEventsImpl;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public final class ClientKeybindEvents {
	public static Event<KeybindListener> pressed(KeyBinding keybind) {
		return ClientKeybindEventsImpl.pressed(keybind);
	}
	
	@FunctionalInterface
	public interface KeybindListener {
		void onKeybind(MinecraftClient client);
	}
}
