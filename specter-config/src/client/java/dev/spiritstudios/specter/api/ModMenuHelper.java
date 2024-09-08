package dev.spiritstudios.specter.api;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Helper class for ModMenu integration without having to depend on ModMenu directly.
 */
public class ModMenuHelper {
	private static final Map<String, Identifier> screens = new Object2ObjectOpenHashMap<>();
	private static final boolean modMenuLoaded = FabricLoader.getInstance().isModLoaded("modmenu");

	/**
	 * Adds a config screen to ModMenu.
	 *
	 * @param modid    The modid of the mod that owns the config screen.
	 * @param configId The identifier of the config screen.
	 */
	public static void addConfig(String modid, Identifier configId) {
		if (!modMenuLoaded) return;
		screens.put(modid, configId);
	}

	@ApiStatus.Internal
	public static Map<String, Identifier> getConfigScreens() {
		return screens;
	}
}
