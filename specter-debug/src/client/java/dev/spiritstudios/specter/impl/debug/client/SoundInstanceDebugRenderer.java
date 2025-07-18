package dev.spiritstudios.specter.impl.debug.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;

public class SoundInstanceDebugRenderer implements DebugRenderer.Renderer, SoundInstanceListener {
	private final List<SoundEntry> sounds = new ArrayList<>();

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		MinecraftClient client = MinecraftClient.getInstance();

		long time = Util.getMeasuringTimeMs();
		this.sounds.removeIf(sound ->
				time - sound.time() > (3000 * client.options.getNotificationDisplayTime().getValue()));

		VertexConsumer boxLayer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
		for (SoundEntry entry : this.sounds) {
			SoundInstance instance = entry.instance;

			VertexRendering.drawFilledBox(
					matrices,
					boxLayer,
					instance.getX() - 0.25 - cameraX,
					instance.getY() - 0.25 - cameraY,
					instance.getZ() - 0.25 - cameraZ,
					instance.getX() + 0.25 - cameraX,
					instance.getY() - cameraY + 0.25,
					instance.getZ() + 0.25 - cameraZ,
					1.0F, 1.0F, 0.0F, 0.35F
			);
		}

		for (SoundEntry entry : this.sounds) {
			SoundInstance instance = entry.instance;

			DebugRenderer.drawString(
					matrices, vertexConsumers,
					instance.getId().toString(),
					instance.getX(), instance.getY() + 0.15F, instance.getZ(),
					Colors.WHITE, 0.01F,
					true, 0.0F, true
			);

			DebugRenderer.drawString(
					matrices, vertexConsumers,
					instance.getSound().getIdentifier().toString(),
					instance.getX(), instance.getY(), instance.getZ(),
					Colors.WHITE, 0.01F,
					true, 0.0F, true
			);

			DebugRenderer.drawString(
					matrices, vertexConsumers,
					"Pitch: " + instance.getPitch() + ", Volume: " + instance.getVolume(),
					instance.getX(), instance.getY() - 0.15F, instance.getZ(),
					Colors.WHITE, 0.01F,
					true, 0.0F, true
			);
		}
	}

	@Override
	public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
		sounds.add(new SoundEntry(
				sound,
				Util.getMeasuringTimeMs()
		));
	}

	record SoundEntry(SoundInstance instance, long time) {
	}
}
