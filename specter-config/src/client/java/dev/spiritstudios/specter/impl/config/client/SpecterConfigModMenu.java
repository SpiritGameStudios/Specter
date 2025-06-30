package dev.spiritstudios.specter.impl.config.client;

import java.util.Map;
import java.util.stream.Collectors;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.spiritstudios.specter.api.config.ConfigHolder;
import dev.spiritstudios.specter.api.config.client.ModMenuHelper;
import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;
import dev.spiritstudios.specter.impl.config.ConfigHolderRegistry;

public class SpecterConfigModMenu implements ModMenuApi {
	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ModMenuHelper.getConfigScreens().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry ->
								parent -> {
									ConfigHolder<?, ?> holder = ConfigHolderRegistry.get(entry.getValue());
									return new TabbedListConfigScreen(holder, parent);
								}
				));
	}
}
