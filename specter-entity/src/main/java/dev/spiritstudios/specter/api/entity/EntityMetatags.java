package dev.spiritstudios.specter.api.entity;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;

import dev.spiritstudios.specter.api.registry.metatag.Metatag;
import dev.spiritstudios.specter.impl.core.Specter;
import dev.spiritstudios.specter.impl.entity.DataDefaultAttributeBuilder;

public final class EntityMetatags {
	/**
	 * A metatag that specifies the default attributes of an entity type.
	 *
	 * @implNote If an entity type already has default attributes, then the contents of this metatag will be appended to the existing attributes.
	 * @apiNote This metatag does not include default attributes set by vanilla or other mods that use the vanilla attribute system.
	 */
	public static final Metatag<EntityType<?>, DataDefaultAttributeBuilder> DEFAULT_ATTRIBUTES = Metatag.builder(
			RegistryKeys.ENTITY_TYPE,
			Specter.id("default_attributes"),
			DataDefaultAttributeBuilder.CODEC
	).packetCodec(DataDefaultAttributeBuilder.PACKET_CODEC).build();

	private EntityMetatags() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	/**
	 * Hacky workaround to force class loading.
	 */
	@SuppressWarnings("EmptyMethod")
	@ApiStatus.Internal
	public static void init() {
		// NO-OP
	}
}
