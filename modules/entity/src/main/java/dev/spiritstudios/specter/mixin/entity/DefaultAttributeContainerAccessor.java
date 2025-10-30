package dev.spiritstudios.specter.mixin.entity;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AttributeSupplier.class)
public interface DefaultAttributeContainerAccessor {
	@Accessor
	Map<Holder<Attribute>, AttributeInstance> getInstances();
}
