package dev.spiritstudios.specter.impl.render.client;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectFloatMutablePair;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

import dev.spiritstudios.specter.api.render.client.shake.Screenshake;

public final class ScreenshakeManager {
	private static final List<ObjectFloatMutablePair<Screenshake>> screenshakes = new ArrayList<>();

	public static void addScreenshake(Screenshake screenshake) {
		screenshakes.add(ObjectFloatMutablePair.of(screenshake, 0.0F));
	}

	public static void update(float delta, MatrixStack matrices, Random random) {
		List<ObjectFloatMutablePair<Screenshake>> screenshakes = new ArrayList<>(ScreenshakeManager.screenshakes);
		for (ObjectFloatMutablePair<Screenshake> pair : screenshakes) {
			Screenshake screenshake = pair.first();
			float progress = pair.secondFloat();

			if (progress < screenshake.duration()) {
				progress += delta;
				pair.second(progress);
			} else {
				ScreenshakeManager.screenshakes.remove(pair);
			}
		}

		float intensity = SpecterRenderClient.SCREENSHAKE_INTENSITY.getValue().floatValue();

		if (ScreenshakeManager.screenshakes.isEmpty() || intensity == 0.0F) return;

		float x = 0;
		float y = 0;
		float rotationZ = 0;
		float rotationX = 0;

		for (ObjectFloatMutablePair<Screenshake> pair : ScreenshakeManager.screenshakes) {
			Screenshake screenshake = pair.first();
			float progress = pair.secondFloat();

			float posIntensity = screenshake.posIntensity() * (1 - progress / screenshake.duration());
			float rotationIntensity = screenshake.rotationIntensity() * (1 - progress / screenshake.duration());

			float angle = random.nextFloat() * MathHelper.TAU;
			float sin = MathHelper.sin(angle);
			float cos = MathHelper.cos(angle);

			x += cos * posIntensity;
			y += sin * posIntensity;

			rotationZ += cos * rotationIntensity;
			rotationX += sin * rotationIntensity;
		}

		x *= intensity;
		y *= intensity;
		rotationX *= intensity;
		rotationZ *= intensity;

		matrices.translate(x, y, 0);

		matrices.multiply(Math.random() > 0.5 ? RotationAxis.POSITIVE_Z.rotation(rotationZ) : RotationAxis.NEGATIVE_Z.rotation(rotationZ));
		matrices.multiply(Math.random() > 0.5 ? RotationAxis.POSITIVE_X.rotation(rotationX) : RotationAxis.NEGATIVE_X.rotation(rotationX));
	}
}
