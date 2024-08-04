package dev.spiritstudios.specter.api.base.util;

import org.objectweb.asm.tree.AnnotationNode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * A bunch of utilities to reduce boilerplate reflection code.
 */
public final class ReflectionHelper {
	private ReflectionHelper() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	/**
	 * Instantiate a class.
	 *
	 * @param clazz Class to instantiate
	 * @param args  Arguments to pass to the constructor
	 * @param <T>   The type of the class
	 * @return A new instance of the class
	 */
	public static <T> T instantiate(Class<T> clazz, Object... args) {
		T instance;
		try {
			instance = clazz.getConstructor().newInstance(args);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
				 NoSuchMethodException e) {
			throw new RuntimeException(
				(e instanceof NoSuchMethodException ?
					"No constructor without arguments found for class " :
					"Failed to instantiate class "
				) + clazz.getName(),
				e
			);
		}

		return instance;
	}

	/**
	 * Get the value of a field.
	 *
	 * @param instance Instance to get the field from
	 * @param field    Field to get the value of
	 * @param <T>      Type of the field
	 * @return Value of the field
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object instance, Field field) {
		try {
			Object value = field.get(instance);
			if (value == null) return null;

			return (T) value;
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to access field " + field.getName(), e);
		}
	}

	/**
	 * Get the value of a static field.
	 *
	 * @param field Field to get the value of
	 * @param <T>   Type of the field
	 * @return Value of the field
	 */
	public static <T> T getFieldValue(Field field) {
		return getFieldValue(null, field);
	}

	/**
	 * Set the value of a field.
	 *
	 * @param instance Instance to set the field on
	 * @param field    Field to set the value of
	 * @param value    Value to set the field to
	 */
	public static void setFieldValue(Object instance, Field field, Object value) {
		try {
			field.set(instance, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to set field " + field.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAnnotationValue(AnnotationNode annotation, String key, T defaultValue) {
		if (annotation == null || annotation.values == null) return defaultValue;

		for (int i = 0; i < annotation.values.size(); i += 2)
			if (annotation.values.get(i).equals(key)) return (T) annotation.values.get(i + 1);

		return defaultValue;
	}
}
