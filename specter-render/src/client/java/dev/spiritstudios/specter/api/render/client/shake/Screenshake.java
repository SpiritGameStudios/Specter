package dev.spiritstudios.specter.api.render.client.shake;

public record Screenshake(double duration, double posIntensity, double rotationIntensity) {
	public static final Screenshake NONE = new Screenshake(0, 0, 0);
}
