package dev.spiritstudios.specter.api.serialization.nightconfig;

import java.util.List;

public class NightConfigPrimitive extends NightConfigElement {
	private final Object value;

	public NightConfigPrimitive(Object value, List<String> comments) {
		super(comments);
		this.value = value;
	}

	@Override
	public Object toObject() {
		return value;
	}
}
