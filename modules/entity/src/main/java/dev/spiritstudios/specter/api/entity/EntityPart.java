package dev.spiritstudios.specter.api.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public abstract class EntityPart<T extends Entity> extends Entity {
	protected final T owner;
	protected final EntityDimensions dimensions;
	private Vec3d relativePos = new Vec3d(0, 0, 0);

	public EntityPart(T owner, EntityDimensions dimensions) {
		super(owner.getType(), owner.getEntityWorld());
		this.owner = owner;
		this.dimensions = dimensions;
		this.calculateDimensions();
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	protected void readCustomData(ReadView view) {

	}

	@Override
	protected void writeCustomData(WriteView view) {

	}

	public Vec3d getRelativePos() {
		return relativePos;
	}

	@SuppressWarnings("unused")
	public void setRelativePos(Vec3d relativePos) {
		this.relativePos = relativePos;
	}

	@Override
	public boolean canHit() {
		return true;
	}

	@Override
	protected Box calculateDefaultBoundingBox(Vec3d pos) {
		return this.dimensions == null ? super.calculateDefaultBoundingBox(pos) : this.dimensions.getBoxAt(pos);
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return this.owner.damage(world, source, amount);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.owner == entity;
	}

	@Override
	public @Nullable ItemStack getPickBlockStack() {
		return this.owner.getPickBlockStack();
	}

	@Override
	public boolean shouldSave() {
		return false;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
		throw new UnsupportedOperationException();
	}

	public T getOwner() {
		return owner;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return this.dimensions;
	}
}
