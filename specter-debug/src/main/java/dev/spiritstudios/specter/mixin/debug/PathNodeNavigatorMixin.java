package dev.spiritstudios.specter.mixin.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.util.math.BlockPos;

@Mixin(PathNodeNavigator.class)
public abstract class PathNodeNavigatorMixin {
	@ModifyReturnValue(method = "findPathToAny(Lnet/minecraft/world/chunk/ChunkCache;Lnet/minecraft/entity/mob/MobEntity;Ljava/util/Set;FIF)Lnet/minecraft/entity/ai/pathing/Path;", at = @At("TAIL"))
	private Path setPathDebugInfo(Path original, @Local Map<TargetPathNode, BlockPos> map) {
		List<PathNode> unvisited = new ArrayList<>();
		List<PathNode> visited = new ArrayList<>();

		for (int i = 0; i < original.getLength(); i++) {
			PathNode node = original.getNode(i);
			(node.visited ? visited : unvisited).add(node);
		}

		original.setDebugInfo(
				unvisited.toArray(PathNode[]::new),
				visited.toArray(PathNode[]::new),
				map.keySet()
		);

		return original;
	}
}
