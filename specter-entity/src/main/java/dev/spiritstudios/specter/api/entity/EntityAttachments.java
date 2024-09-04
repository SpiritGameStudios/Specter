package dev.spiritstudios.specter.api.entity;

import dev.spiritstudios.specter.api.registry.attachment.Attachment;
import dev.spiritstudios.specter.impl.entity.DataDefaultAttributeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

public final class EntityAttachments {
	public static final Attachment<EntityType<?>, DataDefaultAttributeBuilder> DEFAULT_ATTRIBUTES = Attachment.builder(
		Registries.ENTITY_TYPE,
		Identifier.of(MODID, "default_attributes"),
		DataDefaultAttributeBuilder.CODEC,
		DataDefaultAttributeBuilder.PACKET_CODEC
	).build();

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	public static void init() {
		// NO-OP
	}

	private EntityAttachments() {
	}
}
