package dev.spiritstudios.specter.api.entity;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.entity.DataDefaultAttributeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static dev.spiritstudios.specter.api.core.SpecterGlobals.MODID;

public final class EntityMetatags {
	public static final Metatag<EntityType<?>, DataDefaultAttributeBuilder> DEFAULT_ATTRIBUTES = Metatag.builder(
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

	private EntityMetatags() {
	}
}
