package dev.spiritstudios.specter.mixin.entity;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;

@Mixin(DefaultAttributeContainer.class)
public interface DefaultAttributeContainerAccessor {
	@Accessor
	Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> getInstances();
}
