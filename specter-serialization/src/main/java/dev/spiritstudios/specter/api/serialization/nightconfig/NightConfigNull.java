package dev.spiritstudios.specter.api.serialization.nightconfig;

public class NightConfigNull extends NightConfigElement {
	public static final NightConfigNull INSTANCE = new NightConfigNull();

	@Override
	public Object toObject() {
		return null;
	}
}
