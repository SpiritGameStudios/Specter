package dev.spiritstudios.specter.api.registry.registration;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public interface BlockRegistrar extends MinecraftRegistrar<Block> {
	@Override
	default Registry<Block> getRegistry() {
		return Registries.BLOCK;
	}

	@Override
	default void register(String name, String namespace, Block object, Field field) {
		Registry.register(getRegistry(), Identifier.of(namespace, name), object);

		if (field.isAnnotationPresent(NoBlockItem.class)) return;
		registerBlockItem(object, namespace, name);
	}

	default void registerBlockItem(Block block, String namespace, String name) {
		BlockItem item = new BlockItem(block, new Item.Settings());
		Registry.register(Registries.ITEM, Identifier.of(namespace, name), item);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface NoBlockItem {
	}
}
