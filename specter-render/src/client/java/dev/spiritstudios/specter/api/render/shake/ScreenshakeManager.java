package dev.spiritstudios.specter.api.render.shake;

import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public final class ScreenshakeManager {
	private static final List<ObjectObjectMutablePair<Screenshake, Double>> screenshakes = new ArrayList<>();

	public static void addScreenshake(Screenshake screenshake) {
		screenshakes.add(ObjectObjectMutablePair.of(screenshake, 0.0));
	}

	@ApiStatus.Internal
	public static void update(double delta, MatrixStack matrices) {
		List<ObjectObjectMutablePair<Screenshake, Double>> screenshakes = new ArrayList<>(ScreenshakeManager.screenshakes);
		for (ObjectObjectMutablePair<Screenshake, Double> pair : screenshakes) {
			Screenshake screenshake = pair.first();
			double progress = pair.second();

			if (progress < screenshake.duration()) {
				progress += delta;
				pair.second(progress);
			} else ScreenshakeManager.screenshakes.remove(pair);
		}

		if (ScreenshakeManager.screenshakes.isEmpty()) return;
		double x = 0;
		double y = 0;
		double rotationZ = 0;
		double rotationX = 0;

		for (ObjectObjectMutablePair<Screenshake, Double> pair : ScreenshakeManager.screenshakes) {
			Screenshake screenshake = pair.first();
			double progress = pair.second();

			double posIntensity = screenshake.posIntensity() * (1 - progress / screenshake.duration());
			double rotationIntensity = screenshake.rotationIntensity() * (1 - progress / screenshake.duration());

			double angle = Math.random() * Math.PI * 2;
			x += Math.cos(angle) * posIntensity;
			y += Math.sin(angle) * posIntensity;

			rotationZ += Math.sin(angle) * rotationIntensity;
			rotationX += Math.cos(angle) * rotationIntensity;
		}

		matrices.translate(x, y, 0);

		matrices.multiply(Math.random() > 0.5 ? RotationAxis.POSITIVE_Z.rotationDegrees((float) rotationZ) : RotationAxis.NEGATIVE_Z.rotationDegrees((float) rotationZ));
		matrices.multiply(Math.random() > 0.5 ? RotationAxis.POSITIVE_X.rotationDegrees((float) rotationX) : RotationAxis.NEGATIVE_X.rotationDegrees((float) rotationX));
	}
}
