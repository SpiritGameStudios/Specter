package dev.spiritstudios.testmod.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.math.Vec3d;

import dev.spiritstudios.specter.api.entity.EntityPart;

public class SilverfishPart extends EntityPart<SilverfishEntity> {
	public SilverfishPart(SilverfishEntity owner, EntityDimensions dimensions, Vec3d offset) {
		super(owner, dimensions);

		setRelativePos(offset);
		this.calculateBoundingBox();
	}

	@Override
	public boolean isCollidable(@Nullable Entity entity) {
		return true;
	}
}
