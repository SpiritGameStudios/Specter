package dev.spiritstudios.specter.impl.render;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import dev.spiritstudios.specter.api.render.shake.ScreenshakeS2CPayload;
import dev.spiritstudios.specter.impl.render.shake.ScreenshakeCommand;

public class SpecterRender implements ModInitializer {
	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(
				ScreenshakeS2CPayload.ID,
				ScreenshakeS2CPayload.CODEC
		);

		CommandRegistrationCallback.EVENT.register(ScreenshakeCommand::register);
	}
}
