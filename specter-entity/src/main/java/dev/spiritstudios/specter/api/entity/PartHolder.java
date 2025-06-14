package dev.spiritstudios.specter.api.entity;

import java.util.List;

import net.minecraft.entity.Entity;

public interface PartHolder<T extends Entity> {
	List<EntityPart<T>> getEntityParts();
}
