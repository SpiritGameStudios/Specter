package dev.spiritstudios.specter.impl.serialization;

import dev.spiritstudios.specter.api.serialization.text.DynamicTextContent;
import dev.spiritstudios.specter.api.serialization.text.TextContentRegistry;
import net.fabricmc.api.ClientModInitializer;

public class SpecterSerializationClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TextContentRegistry.register("index", DynamicTextContent.TYPE);
	}
}
