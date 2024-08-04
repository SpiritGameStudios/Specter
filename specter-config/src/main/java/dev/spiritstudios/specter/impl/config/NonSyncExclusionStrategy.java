package dev.spiritstudios.specter.impl.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import dev.spiritstudios.specter.api.config.annotations.Sync;

public class NonSyncExclusionStrategy implements ExclusionStrategy {
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(Sync.class) == null;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
}
