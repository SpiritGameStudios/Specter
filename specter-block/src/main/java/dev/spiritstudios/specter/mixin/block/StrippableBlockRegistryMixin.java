package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.api.block.BlockAttachments;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Map;

@Mixin(StrippableBlockRegistry.class)
public class StrippableBlockRegistryMixin {
	@Redirect(method = "requireNonNullAndAxisProperty", at = @At(value = "INVOKE", target = "Ljava/util/Collection;contains(Ljava/lang/Object;)Z"))
	private static boolean skipAxisCheck(Collection<Property<?>> instance, Object o) {
		return true;
	}

	@Redirect(method = "register", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private static <K, V> Object redirectToAttachments(Map<K, V> instance, K k, V v) {
		BlockAttachments.STRIPPABLE.put((Block) k, (Block) v);
		return null;
	}
}
