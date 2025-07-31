package dev.spiritstudios.specter.api.render.client.shake;

import dev.spiritstudios.specter.impl.render.client.ScreenshakeManager;

public record Screenshake(float duration, float posIntensity, float rotationIntensity) {
	public static final Screenshake NONE = new Screenshake(0, 0, 0);

	public void start() {
		ScreenshakeManager.addScreenshake(this);
	}
}
