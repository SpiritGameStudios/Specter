package dev.spiritstudios.testmod.entity.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.spiritstudios.specter.api.entity.PartHolder;
import dev.spiritstudios.testmod.entity.SilverfishPart;

@Mixin(SilverfishEntity.class)
public class SilverfishEntityMixin extends HostileEntity implements PartHolder<SilverfishEntity> {
	@Unique
	private final List<SilverfishPart> parts = List.of(new SilverfishPart(
			(SilverfishEntity) (Object) this,
			EntityDimensions.changing(2f, 1f),
			new Vec3d(0, 1, 0)
	));

	protected SilverfishEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public List<? extends SilverfishPart> getSpecterEntityParts() {
		return parts;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		parts.getFirst().setPosition(parts.getFirst().getRelativePos().add(this.getEntityPos()));
	}
}
