package dev.spiritstudios.specter.api.entity;

import java.util.List;
import net.minecraft.world.entity.Entity;

public interface PartHolder<T extends Entity> {
	List<? extends EntityPart<T>> getSpecterEntityParts();
}
