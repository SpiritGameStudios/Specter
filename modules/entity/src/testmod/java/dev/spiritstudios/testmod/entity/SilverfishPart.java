package dev.spiritstudios.testmod.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.phys.Vec3;

import dev.spiritstudios.specter.api.entity.EntityPart;

public class SilverfishPart extends EntityPart<Silverfish> {
	public SilverfishPart(Silverfish owner, EntityDimensions dimensions, Vec3 offset) {
		super(owner, dimensions);

		setRelativePos(offset);
		this.makeBoundingBox();
	}

	@Override
	public boolean canBeCollidedWith(@Nullable Entity entity) {
		return true;
	}
}
