package dev.spiritstudios.specter.api.registry.registration;

import dev.spiritstudios.specter.api.core.util.ReflectionHelper;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Automatically run a function on each object using reflection.
 * Objects are defined as static fields in the implementing class.
 *
 * @param <T> Type of object to register
 * @see MinecraftRegistrar
 */
public interface Registrar<T> {
	/**
	 * Obsolete. Please use the <code>specter:registrars</code> metadata tag instead.
	 * <p>
	 * Process a registrar class and register all objects.
	 *
	 * @param clazz     Registrar class to process
	 * @param namespace Namespace to register objects in
	 */
	@ApiStatus.Obsolete
	static <T> void process(Class<? extends Registrar<T>> clazz, String namespace) {
		Registrar<T> registrar = ReflectionHelper.instantiate(clazz);
		registrar.init(namespace);
	}

	/**
	 * Workaround for Java's type erasure.
	 * Use this if {@link T} has a generic type of ?.
	 */
	@SuppressWarnings("unchecked")
	static <T> Class<T> fixGenerics(Class<?> clazz) {
		return (Class<T>) clazz;
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
	 * Get the type of object to register.
	 * If {@link T} has a generic type of ?, use {@link Registrar#fixGenerics(Class)} to force the correct type.
	 *
	 * @return The type of object to register
	 */
	Class<T> getObjectType();

	/**
	 * Initialize the registrar and register all objects.
	 * Do not call this method directly, use {@link Registrar#process(Class, String)} instead.
	 *
	 * @param namespace Namespace to register objects in
	 */
	@ApiStatus.Internal
	default void init(String namespace) {
		ReflectionHelper.forEachStaticField(this.getClass(), getObjectType(), (value, name, field) -> {
			if (field.isAnnotationPresent(Ignore.class)) return;

			String objectName = ReflectionHelper.getAnnotation(field, Name.class)
				.map(Name::value)
				.orElseGet(name::toLowerCase);

			register(objectName, namespace, value, field);
		});
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
