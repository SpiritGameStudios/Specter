package dev.spiritstudios.testmod.entity.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import dev.spiritstudios.specter.api.entity.PartHolder;
import dev.spiritstudios.testmod.entity.SilverfishPart;

@Mixin(Silverfish.class)
public class SilverfishEntityMixin extends Monster implements PartHolder<Silverfish> {
	@Unique
	private final List<SilverfishPart> parts = List.of(new SilverfishPart(
			(Silverfish) (Object) this,
			EntityDimensions.scalable(2f, 1f),
			new Vec3(0, 1, 0)
	));

	protected SilverfishEntityMixin(EntityType<? extends Monster> entityType, Level world) {
		super(entityType, world);
	}

	@Override
	public List<? extends SilverfishPart> getSpecterEntityParts() {
		return parts;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		parts.getFirst().setPos(parts.getFirst().getRelativePos().add(this.position()));
	}
}
