package dev.spiritstudios.specter.api.registry;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import dev.spiritstudios.specter.api.core.reflect.ReflectionHelper;
import dev.spiritstudios.specter.api.registry.annotations.Name;
import dev.spiritstudios.specter.api.registry.annotations.NoBlockItem;

public final class RegistryHelper {
	public static <T> void registerFields(Registry<T> registry, Class<T> toRegister, Class<?> clazz, String namespace) {
		ReflectionHelper.getStaticFields(clazz, toRegister).forEach(pair -> {
				String objectName = ReflectionHelper.getAnnotation(pair.field(), Name.class)
					.map(Name::value)
					.orElseGet(() -> pair.field().getName().toLowerCase());

				Registry.register(registry, ResourceLocation.fromNamespaceAndPath(namespace, objectName), pair.value());
			}
		);
	}

	/**
	 * Obsolete, 1.21.4 makes this impossible.
	 */
	@Deprecated(forRemoval = true)
	@ApiStatus.Obsolete
	public static void registerItems(Class<?> clazz, String namespace) {
		registerFields(BuiltInRegistries.ITEM, Item.class, clazz, namespace);
	}

	public static void registerDataComponentTypes(Class<?> clazz, String namespace) {
		registerFields(BuiltInRegistries.DATA_COMPONENT_TYPE, fixGenerics(DataComponentType.class), clazz, namespace);
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

				Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(namespace, objectName), pair.value());

				if (pair.field().isAnnotationPresent(NoBlockItem.class)) return;
				BlockItem item = new BlockItem(pair.value(), new Item.Properties());
				Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, objectName), item);
			}
		);
	}

	public static void registerBlockEntityTypes(Class<?> clazz, String namespace) {
		registerFields(BuiltInRegistries.BLOCK_ENTITY_TYPE, fixGenerics(BlockEntityType.class), clazz, namespace);
	}

	public static void registerEntityTypes(Class<?> clazz, String namespace) {
		registerFields(BuiltInRegistries.ENTITY_TYPE, fixGenerics(EntityType.class), clazz, namespace);
	}

	public static void registerSoundEvents(Class<?> clazz, String namespace) {
		registerFields(BuiltInRegistries.SOUND_EVENT, SoundEvent.class, clazz, namespace);
	}

	public static void registerParticleTypes(Class<?> clazz, String namespace) {
		registerFields(BuiltInRegistries.PARTICLE_TYPE, fixGenerics(ParticleType.class), clazz, namespace);
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
