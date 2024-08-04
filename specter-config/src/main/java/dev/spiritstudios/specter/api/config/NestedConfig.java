package dev.spiritstudios.specter.api.config;

public interface NestedConfig extends Config {
	default void save() {
		throw new UnsupportedOperationException("Nested configs cannot be saved");
	}
}
