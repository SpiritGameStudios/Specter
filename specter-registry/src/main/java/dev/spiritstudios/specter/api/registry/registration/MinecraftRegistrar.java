package dev.spiritstudios.specter.api.registry.registration;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

/**
 * Automatically register each object using reflection.
 * Objects are defined as static fields in the implementing class.
 *
 * @param <T> Type of object to register
 */
public interface MinecraftRegistrar<T> extends Registrar<T> {
	/**
	 * Get the registry to register objects in.
	 *
	 * @return The registry to register objects in
	 */
	Registry<T> getRegistry();

	/**
	 * Register an object to the registry.
	 *
	 * @param name      Name of the object
	 * @param namespace Namespace to register the object in
	 * @param object    Object to register
	 * @param field     Field the object is stored in
	 */
	@Override
	default void register(String name, String namespace, T object, Field field) {
		Registry.register(getRegistry(), Identifier.of(namespace, name), object);
	}
}
