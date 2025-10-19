package dev.spiritstudios.testmod.core;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.core.client.event.ClientKeybindEvents;

public class SpecterCoreTestModClient implements ClientModInitializer {
	public static final KeyBinding TEST_KEYBIND = KeyBindingHelper.registerKeyBinding(
			new KeyBinding(
					"specter.test",
					GLFW.GLFW_KEY_B,
					KeyBinding.Category.MISC
			)
	);

	@Override
	public void onInitializeClient() {
		ClientKeybindEvents.pressed(TEST_KEYBIND).register(client -> {
			if (client.player != null) client.player.sendMessage(Text.of("Test"), false);
		});
	}
}
