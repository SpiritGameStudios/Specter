package dev.spiritstudios.specter.api.registry;

import dev.spiritstudios.specter.api.base.util.ReflectionHelper;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Automatically run a function on each object using reflection.
 * Objects are defined as static fields in the implementing class.
 *
 * @param <T> Type of object to register
 */
public interface Registrar<T> {
	static <T> void process(Class<? extends Registrar<T>> clazz, String namespace) {
		Registrar<T> registrar = ReflectionHelper.instantiate(clazz);
		registrar.init(namespace);
	}

	/**
	 * Register an object to the registry.
	 *
	 * @param name      Name of the object
	 * @param namespace Namespace to register the object in
	 * @param object    Object to register
	 * @param field     Field the object is stored in
	 */
	void register(String name, String namespace, T object, Field field);

	/**
	 * Initialize the registrar and register all objects.
	 * Do not call this method directly, use {@link Registrar#process(Class, String)} instead.
	 *
	 * @param namespace Namespace to register objects in
	 */
	@ApiStatus.Internal
	default void init(String namespace) {
		for (var field : this.getClass().getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) continue;
			if (field.isAnnotationPresent(Ignore.class)) continue;

			T value = ReflectionHelper.getFieldValue(field);

			if (value != null && field.getType().isAssignableFrom(value.getClass())) {
				String name = field.getName().toLowerCase();
				if (field.isAnnotationPresent(Name.class)) {
					Name nameAnnotation = field.getAnnotation(Name.class);
					name = nameAnnotation.value();
				}

				register(name, namespace, value, field);
			}
		}
	}

	/**
	 * Ignore a field when registering objects.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Ignore {
	}

	/**
	 * Set the name of the object to register.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Name {
		String value();
	}
}
