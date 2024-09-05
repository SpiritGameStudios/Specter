package dev.spiritstudios.specter.impl.render;

import dev.spiritstudios.specter.api.render.RenderMetatags;
import dev.spiritstudios.specter.api.render.shake.Screenshake;
import dev.spiritstudios.specter.api.render.shake.ScreenshakeManager;
import dev.spiritstudios.specter.api.render.shake.ScreenshakeS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SpecterRenderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ScreenshakeS2CPayload.ID, (payload, context) -> {
			Screenshake screenshake = new Screenshake(payload.duration(), payload.posIntensity(), payload.rotationIntensity());
			context.client().execute(() -> ScreenshakeManager.addScreenshake(screenshake));
		});

		RenderMetatags.init();
	}
}
