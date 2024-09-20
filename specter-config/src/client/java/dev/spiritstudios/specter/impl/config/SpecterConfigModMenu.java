package dev.spiritstudios.specter.impl.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.spiritstudios.specter.api.config.ConfigScreen;
import dev.spiritstudios.specter.api.config.ModMenuHelper;

import java.util.Map;
import java.util.stream.Collectors;

public class SpecterConfigModMenu implements ModMenuApi {
	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ModMenuHelper.getConfigScreens()
			.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry ->
					parent -> new ConfigScreen(ConfigManager.getConfig(entry.getValue()), parent)
			));
	}
}
