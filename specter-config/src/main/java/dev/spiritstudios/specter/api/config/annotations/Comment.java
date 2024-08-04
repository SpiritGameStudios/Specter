package dev.spiritstudios.specter.api.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A comment for a field in a config file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Comment {
	/**
	 * The comment text. Can contain newlines using \n.
	 */
	String value() default "";
}
