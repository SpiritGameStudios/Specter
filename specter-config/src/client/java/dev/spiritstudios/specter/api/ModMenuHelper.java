package dev.spiritstudios.specter.api;

import dev.spiritstudios.specter.api.config.Config;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

/**
 * Helper class for ModMenu integration without having to depend on ModMenu directly.
 */
public class ModMenuHelper {
	private static final HashMap<String, Class<? extends Config>> screens = new HashMap<>();

	/**
	 * Adds a config screen to ModMenu.
	 *
	 * @param modid       The modid of the mod that owns the config screen.
	 * @param configClass The class of the config screen.
	 */
	public static void addConfig(String modid, Class<? extends Config> configClass) {
		if (!FabricLoader.getInstance().isModLoaded("modmenu")) return;
		screens.put(modid, configClass);
	}

	@ApiStatus.Internal
	public static HashMap<String, Class<? extends Config>> getConfigScreens() {
		return screens;
	}
}
