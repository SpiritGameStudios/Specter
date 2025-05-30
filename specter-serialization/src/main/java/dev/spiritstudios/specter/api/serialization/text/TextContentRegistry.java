package dev.spiritstudios.specter.api.serialization.text;

import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextContent;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;
import dev.spiritstudios.specter.impl.serialization.text.TextContentRegistryImpl;

/**
 * Allows adding your own {@link TextContent.Type} to be encoded and decoded by {@link TextCodecs}.
 */
public final class TextContentRegistry {
	private TextContentRegistry() {
		throw new UnreachableException();
	}

	/**
	 * Registers a new {@link TextContent.Type}.
	 * This type will be used when either the type field is set to the id of your type, or it contains a field with the name you provided.
	 *
	 * @param field The field name
	 * @param type  The type
	 */
	public static void register(String field, TextContent.Type<?> type) {
		TextContentRegistryImpl.register(field, type);
	}
}
