package dev.spiritstudios.specter.mixin.block;

import dev.spiritstudios.specter.api.block.SpecterBlock;
import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Registry.class)
public interface RegistryMixin {
	@Inject(method = "register(Lnet/minecraft/registry/Registry;Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("RETURN"))
	private static <V, T extends V> void register(Registry<V> registry, RegistryKey<V> key, T entry, CallbackInfoReturnable<T> cir) {
		if (Objects.requireNonNull(entry) instanceof Block block) {
			Block strippedBlock = ((SpecterBlock) block).specter$getStrippedBlock();
			if (strippedBlock != null) StrippableBlockRegistry.register(block, strippedBlock);

			if (((SpecterBlock) block).specter$isFlammable())
				FlammableBlockRegistry.getDefaultInstance().add(
					block,
					((SpecterBlock) block).specter$getBurn(),
					((SpecterBlock) block).specter$getSpread()
				);
		}

		if (Objects.requireNonNull(entry) instanceof BlockItem item) {
			SpecterItemGroup group = ((SpecterBlock) item.getBlock()).specter$getGroup();
			if (group != null) group.addItem(item);
		}
	}
}
