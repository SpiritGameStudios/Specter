package dev.spiritstudios.testmod.core;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

import dev.spiritstudios.specter.api.core.client.event.ClientKeybindEvents;

public class SpecterCoreTestModClient implements ClientModInitializer {
	public static final KeyMapping TEST_KEYBIND = KeyBindingHelper.registerKeyBinding(
			new KeyMapping(
					"specter.test",
					GLFW.GLFW_KEY_B,
					KeyMapping.Category.MISC
			)
	);

	@Override
	public void onInitializeClient() {
		ClientKeybindEvents.pressed(TEST_KEYBIND).register(client -> {
			if (client.player != null) client.player.displayClientMessage(Component.literal("Test"), false);
		});
	}
}
