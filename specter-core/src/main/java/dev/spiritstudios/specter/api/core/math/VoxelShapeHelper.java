package dev.spiritstudios.specter.api.core.math;

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

	public static VoxelShape mirror(VoxelShape shape, Direction.Axis axis) {
		final VoxelShape[] newShape = new VoxelShape[]{VoxelShapes.empty()};

		switch (axis) {
			case X -> shape.forEachBox(((minX, minY, minZ, maxX, maxY, maxZ) -> newShape[0] = VoxelShapes.combine(
				newShape[0],
				VoxelShapes.cuboid(
					Math.min(1 - minX, 1 - maxX), minY, minZ,
					Math.max(1 - minX, 1 - maxX), maxY, maxZ
				),
				BooleanBiFunction.OR
			)));
			case Y -> shape.forEachBox(((minX, minY, minZ, maxX, maxY, maxZ) -> newShape[0] = VoxelShapes.combine(
				newShape[0],
				VoxelShapes.cuboid(
					minX, Math.min(1 - minY, 1 - maxY), minZ,
					maxX, Math.max(1 - minY, 1 - maxY), maxZ
				),
				BooleanBiFunction.OR
			)));
			case Z -> shape.forEachBox(((minX, minY, minZ, maxX, maxY, maxZ) -> newShape[0] = VoxelShapes.combine(
				newShape[0],
				VoxelShapes.cuboid(
					minX, minY, Math.min(1 - minZ, 1 - maxZ),
					maxX, maxY, Math.max(1 - minZ, 1 - maxZ)
				),
				BooleanBiFunction.OR
			)));
		}

		return newShape[0];
	}
}
