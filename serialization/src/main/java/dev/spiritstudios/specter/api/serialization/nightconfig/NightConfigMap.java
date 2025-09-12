package dev.spiritstudios.specter.api.serialization.nightconfig;

import java.util.ArrayList;
import java.util.List;

import com.electronwill.nightconfig.core.CommentedConfig;

public class NightConfigMap extends NightConfigElement {
	private final CommentedConfig config;

	public NightConfigMap(NightConfigMap other) {
		super(new ArrayList<>(other.comments()));
		this.config = CommentedConfig.copy(other.config());
	}

	public NightConfigMap(CommentedConfig config, List<String> comments) {
		super(comments);
		this.config = config;
	}

	public CommentedConfig config() {
		return config;
	}

	public void put(String key, NightConfigElement value) {
		config.set(key, value.toObject());
		config.setComment(key, String.join("\n", value.comments()));
	}

	public void putAll(NightConfigMap value) {
		config.putAll(value.config);
	}

	@Override
	public Object toObject() {
		return config;
	}
}
