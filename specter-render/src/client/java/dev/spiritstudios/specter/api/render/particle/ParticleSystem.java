package dev.spiritstudios.specter.api.render.particle;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleSystem {
	public Vec3d velocity;
	public Vec3d velocityDeviation;

	public Vec3d offset;
	public Vec3d positionDeviation;

	public int particleCount;
	public int spawnRate;

	private int age;

	private final ParticleEffect type;
	private final Random random;

	protected ParticleSystem(Vec3d velocity, Vec3d velocityDeviation, Vec3d offset, Vec3d positionDeviation, int particleCount, int spawnRate, ParticleEffect type, Random random) {
		this.velocity = velocity;
		this.velocityDeviation = velocityDeviation;
		this.offset = offset;
		this.positionDeviation = positionDeviation;
		this.particleCount = particleCount;
		this.spawnRate = spawnRate;
		this.type = type;
		this.random = random;
	}

	/**
	 * Tick the particle system, spawning particles if necessary.
	 *
	 * @param world The world.
	 * @param pos   The position where the particles should spawn.
	 */
	public void tick(World world, BlockPos pos) {
		age++;

		if (age % spawnRate != 0) return;

		for (int i = 0; i < particleCount; i++) {
			Vec3d velocity = this.velocity.add(
				random.nextGaussian() * velocityDeviation.x,
				random.nextGaussian() * velocityDeviation.y,
				random.nextGaussian() * velocityDeviation.z
			);

			Vec3d position = new Vec3d(
				pos.getX() + offset.x + random.nextGaussian() * positionDeviation.x,
				pos.getY() + offset.y + random.nextGaussian() * positionDeviation.y,
				pos.getZ() + offset.z + random.nextGaussian() * positionDeviation.z
			);

			world.addParticle(type, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
		}
	}

	public static class Builder {
		private Vec3d velocity = Vec3d.ZERO;
		private Vec3d velocityDeviation = Vec3d.ZERO;
		private Vec3d offset = Vec3d.ZERO;
		private Vec3d positionDeviation = Vec3d.ZERO;
		private int particleCount = 1;
		private int spawnRate = 1;

		public Builder velocity(Vec3d velocity) {
			this.velocity = velocity;
			return this;
		}

		public Builder velocityDeviation(Vec3d velocityDeviation) {
			this.velocityDeviation = velocityDeviation;
			return this;
		}

		public Builder offset(Vec3d offset) {
			this.offset = offset;
			return this;
		}

		public Builder positionDeviation(Vec3d positionDeviation) {
			this.positionDeviation = positionDeviation;
			return this;
		}

		public Builder particleCount(int particleCount) {
			this.particleCount = particleCount;
			return this;
		}

		public Builder spawnRate(int spawnRate) {
			this.spawnRate = spawnRate;
			return this;
		}

		public ParticleSystem build(ParticleEffect type, Random random) {
			return new ParticleSystem(velocity, velocityDeviation, offset, positionDeviation, particleCount, spawnRate, type, random);
		}
	}
}
