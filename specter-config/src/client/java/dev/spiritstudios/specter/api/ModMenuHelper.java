package dev.spiritstudios.specter.api;

import dev.spiritstudios.specter.api.config.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Helper class for ModMenu integration without having to depend on ModMenu directly.
 */
public class ModMenuHelper {
	private static final Map<String, Class<? extends Config>> screens = new Object2ObjectOpenHashMap<>();
	private static final boolean modMenuLoaded = FabricLoader.getInstance().isModLoaded("modmenu");

	/**
	 * Adds a config screen to ModMenu.
	 *
	 * @param modid       The modid of the mod that owns the config screen.
	 * @param configClass The class of the config screen.
	 */
	public static void addConfig(String modid, Class<? extends Config> configClass) {
		if (!modMenuLoaded) return;
		screens.put(modid, configClass);
	}

	@ApiStatus.Internal
	public static Map<String, Class<? extends Config>> getConfigScreens() {
		return screens;
	}
}
