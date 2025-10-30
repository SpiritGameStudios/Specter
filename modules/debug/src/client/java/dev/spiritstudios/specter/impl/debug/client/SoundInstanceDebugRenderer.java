package dev.spiritstudios.specter.impl.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.util.CommonColors;
import net.minecraft.util.debug.DebugValueAccess;

public class SoundInstanceDebugRenderer implements DebugRenderer.SimpleDebugRenderer, SoundEventListener {
	private final List<SoundEntry> sounds = new ArrayList<>();

	@Override
	public void onPlaySound(SoundInstance sound, WeighedSoundEvents soundSet, float range) {
		sounds.add(new SoundEntry(
				sound,
				Util.getMillis()
		));
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ, DebugValueAccess store, Frustum frustum) {
		Minecraft client = Minecraft.getInstance();

		long time = Util.getMillis();
		this.sounds.removeIf(sound ->
				time - sound.time() > (3000 * client.options.notificationDisplayTime().get()));

		VertexConsumer boxLayer = vertexConsumers.getBuffer(RenderType.debugFilledBox());
		for (SoundEntry entry : this.sounds) {
			SoundInstance instance = entry.instance;

			ShapeRenderer.addChainedFilledBoxVertices(
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

			DebugRenderer.renderFloatingText(
					matrices, vertexConsumers,
					instance.getLocation().toString(),
					instance.getX(), instance.getY() + 0.15F, instance.getZ(),
					CommonColors.WHITE, 0.01F,
					true, 0.0F, true
			);

			DebugRenderer.renderFloatingText(
					matrices, vertexConsumers,
					instance.getSound().getLocation().toString(),
					instance.getX(), instance.getY(), instance.getZ(),
					CommonColors.WHITE, 0.01F,
					true, 0.0F, true
			);

			DebugRenderer.renderFloatingText(
					matrices, vertexConsumers,
					"Pitch: " + instance.getPitch() + ", Volume: " + instance.getVolume(),
					instance.getX(), instance.getY() - 0.15F, instance.getZ(),
					CommonColors.WHITE, 0.01F,
					true, 0.0F, true
			);
		}
	}

	record SoundEntry(SoundInstance instance, long time) {
	}
}
