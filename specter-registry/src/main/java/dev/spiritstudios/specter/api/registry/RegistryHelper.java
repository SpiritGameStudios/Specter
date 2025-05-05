package dev.spiritstudios.specter.api.registry;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.api.registry.annotations.Name;
import dev.spiritstudios.specter.api.registry.annotations.NoBlockItem;

public final class RegistryHelper {
	public static <T> void registerFields(Registry<T> registry, Class<T> toRegister, Class<?> clazz, String namespace) {
		ReflectionHelper.getStaticFields(clazz, toRegister).forEach(pair -> {
				String objectName = ReflectionHelper.getAnnotation(pair.field(), Name.class)
					.map(Name::value)
					.orElseGet(() -> pair.field().getName().toLowerCase());

				Registry.register(registry, Identifier.of(namespace, objectName), pair.value());
			}
		);
	}

	/**
	 * Obsolete, 1.21.4 makes this impossible.
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.Obsolete
	public static void registerItems(Class<?> clazz, String namespace) {
		registerFields(Registries.ITEM, Item.class, clazz, namespace);
	}

	public static void registerDataComponentTypes(Class<?> clazz, String namespace) {
		registerFields(Registries.DATA_COMPONENT_TYPE, fixGenerics(ComponentType.class), clazz, namespace);
	}

	/**
	 * Registers all blocks contained within a class
	 * All blocks will be given a block item by default,
	 * to override this behavior, user {@link NoBlockItem}
	 * <p>
	 * Obsolete, 1.21.4 makes this impossible.
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.Obsolete
	public static void registerBlocks(Class<?> clazz, String namespace) {
		ReflectionHelper.getStaticFields(clazz, Block.class).forEach(pair -> {
				String objectName = ReflectionHelper.getAnnotation(pair.field(), Name.class)
					.map(Name::value)
					.orElseGet(() -> pair.field().getName().toLowerCase());

				Registry.register(Registries.BLOCK, Identifier.of(namespace, objectName), pair.value());

				if (pair.field().isAnnotationPresent(NoBlockItem.class)) return;
				BlockItem item = new BlockItem(pair.value(), new Item.Settings());
				Registry.register(Registries.ITEM, Identifier.of(namespace, objectName), item);
			}
		);
	}

	public static void registerBlockEntityTypes(Class<?> clazz, String namespace) {
		registerFields(Registries.BLOCK_ENTITY_TYPE, fixGenerics(BlockEntityType.class), clazz, namespace);
	}

	public static void registerEntityTypes(Class<?> clazz, String namespace) {
		registerFields(Registries.ENTITY_TYPE, fixGenerics(EntityType.class), clazz, namespace);
	}

	public static void registerSoundEvents(Class<?> clazz, String namespace) {
		registerFields(Registries.SOUND_EVENT, SoundEvent.class, clazz, namespace);
	}

	public static void registerParticleTypes(Class<?> clazz, String namespace) {
		registerFields(Registries.PARTICLE_TYPE, fixGenerics(ParticleType.class), clazz, namespace);
	}

	/**
	 * Workaround for Java's type erasure.
	 * Use this if {@link T} has a generic type of ?.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> fixGenerics(Class<?> clazz) {
		return (Class<T>) clazz;
	}
}
