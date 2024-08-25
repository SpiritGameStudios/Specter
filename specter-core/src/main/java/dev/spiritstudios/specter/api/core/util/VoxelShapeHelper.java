package dev.spiritstudios.specter.api.core.util;

import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * A helper class for VoxelShapes
 */
public final class VoxelShapeHelper {
	/**
	 * Rotates a VoxelShape to the specified direction
	 *
	 * @param to    The direction to rotate to
	 * @param from  The direction to rotate from
	 * @param shape The VoxelShape to rotate
	 * @return The rotated VoxelShape
	 */
	public static VoxelShape rotateHorizontal(Direction to, Direction from, VoxelShape shape) {
		VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};
		int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;

		for (int i = 0; i < times; i++) {
			buffer[0].forEachBox(((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combine(
				buffer[1],
				VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX),
				BooleanBiFunction.OR
			)));

			buffer[0] = buffer[1];
			buffer[1] = VoxelShapes.empty();
		}

		return buffer[0];
	}
}
