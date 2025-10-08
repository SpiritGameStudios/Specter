package dev.spiritstudios.specter.api.serialization.nightconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.electronwill.nightconfig.core.CommentedConfig;

import dev.spiritstudios.specter.api.serialization.Commentable;

public abstract class NightConfigElement implements Commentable {
	private List<String> comments;

	public NightConfigElement() {
		this.comments = Collections.emptyList();
	}

	public NightConfigElement(List<String> comments) {
		this.comments = comments;
	}

	public static NightConfigElement ofObject(Object object, List<String> comments) {
		return switch (object) {
			case CommentedConfig config -> new NightConfigMap(config, comments);
			case List<?> array -> new NightConfigList(new ArrayList<>(array), comments);
			case null -> NightConfigNull.INSTANCE;
			default -> new NightConfigPrimitive(object, comments);
		};
	}

	public static NightConfigElement ofObject(Object object) {
		return ofObject(object, Collections.emptyList());
	}

	@Override
	public List<String> comments() {
		return comments;
	}

	@Override
	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public abstract Object toObject();
}
