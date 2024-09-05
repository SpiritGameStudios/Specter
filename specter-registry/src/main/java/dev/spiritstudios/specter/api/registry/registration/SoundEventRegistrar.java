package dev.spiritstudios.specter.api.registry.registration;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

/**
 * Automatically register each sound event using reflection.
 * Unlike normal registrars, <code>-</code> in the field name is replaced with <code>.</code> in the registry to allow for easier naming.
 */
public interface SoundEventRegistrar extends MinecraftRegistrar<SoundEvent> {
	@Override
	default void register(String name, String namespace, SoundEvent object, Field field) {
		Registry.register(getRegistry(), Identifier.of(namespace, name.replace('-', '.')), object);
	}

	@Override
	default Registry<SoundEvent> getRegistry() {
		return Registries.SOUND_EVENT;
	}

	@Override
	default Class<SoundEvent> getObjectType() {
		return SoundEvent.class;
	}
}
