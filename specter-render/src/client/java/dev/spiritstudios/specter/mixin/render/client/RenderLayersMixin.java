package dev.spiritstudios.specter.mixin.render.client;

import java.util.Map;
import java.util.Optional;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayers;

import dev.spiritstudios.specter.api.render.client.BlockRenderLayer;
import dev.spiritstudios.specter.api.render.client.RenderMetatags;

@Mixin(RenderLayers.class)
public class RenderLayersMixin {
	@SuppressWarnings("unchecked")
	@WrapOperation(method = "getBlockLayer", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static <K, V> V get(Map<K, V> instance, Object o, Operation<V> original) {
		if (!(o instanceof Block block)) return original.call(instance, o);

		Optional<BlockRenderLayer> renderLayer = RenderMetatags.RENDER_LAYER.get(block);
		return renderLayer.map(layer -> (V) layer.getLayer()).orElseGet(() -> original.call(instance, o));
	}
}
