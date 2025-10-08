package dev.spiritstudios.specter.api.config;

/**
 * A config that can be nested inside another config.
 * This is effectively a marker class.
 *
 * @param <T>
 */
public abstract class NestedConfig<T extends NestedConfig<T>> extends Config<T> {
}
