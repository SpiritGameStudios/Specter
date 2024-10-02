package dev.spiritstudios.specter.impl.debug;

import dev.spiritstudios.specter.impl.debug.command.ComponentsCommand;
import dev.spiritstudios.specter.impl.debug.command.HealCommand;
import dev.spiritstudios.specter.impl.debug.command.MetatagCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class SpecterDebug implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			MetatagCommand.register(dispatcher);
			HealCommand.register(dispatcher);
			ComponentsCommand.register(dispatcher);
		});
	}
}
