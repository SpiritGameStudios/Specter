package dev.spiritstudios.specter.mixin.block;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(StrippableBlockRegistry.class)
public class StrippableBlockRegistryMixin {
	@Redirect(method = "requireNonNullAndAxisProperty", at = @At(value = "INVOKE", target = "Ljava/util/Collection;contains(Ljava/lang/Object;)Z"))
	private static boolean skipAxisCheck(Collection<Property<?>> instance, Object o) {
		return true;
	}
}
