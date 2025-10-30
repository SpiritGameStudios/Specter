package dev.spiritstudios.specter.impl.render.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import it.unimi.dsi.fastutil.objects.ObjectFloatMutablePair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.spiritstudios.specter.api.render.client.shake.Screenshake;

public final class ScreenshakeManager {
	private static final List<ObjectFloatMutablePair<Screenshake>> screenshakes = new ArrayList<>();

	public static void addScreenshake(Screenshake screenshake) {
		screenshakes.add(ObjectFloatMutablePair.of(screenshake, 0.0F));
	}

	public static void update(float delta, PoseStack matrices, RandomSource random) {
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

		float intensity = SpecterRenderClient.SCREENSHAKE_INTENSITY.get().floatValue();

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

			float angle = random.nextFloat() * Mth.TWO_PI;
			float sin = Mth.sin(angle);
			float cos = Mth.cos(angle);

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

		matrices.mulPose(Math.random() > 0.5 ? Axis.ZP.rotation(rotationZ) : Axis.ZN.rotation(rotationZ));
		matrices.mulPose(Math.random() > 0.5 ? Axis.XP.rotation(rotationX) : Axis.XN.rotation(rotationX));
	}
}
