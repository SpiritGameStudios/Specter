package dev.spiritstudios.testmod;

import dev.spiritstudios.specter.api.core.util.ClientKeybindEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SpecterCoreTestmodClient implements ClientModInitializer {
	public static final KeyBinding TEST_KEYBIND = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"specter.test",
			GLFW.GLFW_KEY_B,
			KeyBinding.MISC_CATEGORY
		)
	);

	@Override
	public void onInitializeClient() {
		ClientKeybindEvents.pressed(TEST_KEYBIND).register(client -> {
			if (client.player != null) client.player.sendMessage(Text.of("Test"));
		});
	}
}
