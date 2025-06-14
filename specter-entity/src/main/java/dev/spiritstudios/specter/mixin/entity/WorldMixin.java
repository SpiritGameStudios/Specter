package dev.spiritstudios.specter.mixin.entity;

import java.util.List;
import java.util.function.Predicate;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import dev.spiritstudios.specter.api.entity.EntityPart;
import dev.spiritstudios.specter.api.entity.PartHolder;
import dev.spiritstudios.specter.impl.entity.EntityPartWorld;

@Mixin(World.class)
public abstract class WorldMixin implements EntityPartWorld {

	@Unique
	protected final Int2ObjectMap<EntityPart<?>> specter$parts = new Int2ObjectOpenHashMap<>();

	@Inject(method = "method_47576", at = @At("TAIL"), cancellable = true)
	private static <T extends Entity> void collectEntitiesByTypeLambda(
			Predicate<? super T> predicate,
			List<? super T> result,
			int limit,
			TypeFilter<Entity, T> filter,
			Entity entity,
			CallbackInfoReturnable<LazyIterationConsumer.NextIteration> cir
	) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getEntityParts()) {
				T partCasted = filter.downcast(part);

				if (partCasted != null && predicate.test(partCasted)) {
					result.add(partCasted);
					if (result.size() >= limit) {
						cir.setReturnValue(LazyIterationConsumer.NextIteration.ABORT);
					}
				}
			}
		}
	}

	@WrapMethod(method = "getOtherEntities")
	private List<Entity> getOtherEntities(Entity except, Box box, Predicate<? super Entity> predicate, Operation<List<Entity>> original) {
		List<Entity> list = original.call(except, box, predicate);

		for (EntityPart<?> part : this.specter$parts.values()) {
			if (part != except && part.getOwner() != except && predicate.test(part) && box.intersects(part.getBoundingBox())) {
				list.add(part);
			}
		}

		return list;
	}

	@Override
	public Int2ObjectMap<EntityPart<?>> specter$getParts() {
		return specter$parts;
	}
}
