package dev.spiritstudios.specter.impl.render.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import dev.spiritstudios.specter.api.render.client.shake.Screenshake;
import dev.spiritstudios.specter.api.render.client.shake.ScreenshakeManager;
import dev.spiritstudios.specter.api.render.shake.ScreenshakeS2CPayload;

public class SpecterRenderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ScreenshakeS2CPayload.ID, (payload, context) -> {
			Screenshake screenshake = new Screenshake(payload.duration(), payload.posIntensity(), payload.rotationIntensity());
			context.client().execute(() -> ScreenshakeManager.addScreenshake(screenshake));
		});
	}
}
