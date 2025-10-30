package dev.spiritstudios.specter.impl.render.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import dev.spiritstudios.specter.api.render.client.shake.Screenshake;
import dev.spiritstudios.specter.api.render.shake.ScreenshakeS2CPayload;

public class SpecterRenderClient implements ClientModInitializer {
	public static final OptionInstance<Double> SCREENSHAKE_INTENSITY = new OptionInstance<>(
			"options.specter.screenshake_intensity",
			OptionInstance.cachedConstantTooltip(Component.translatable("options.specter.screenshake_intensity.description")),
			(optionText, value) -> value == 0.0 ? CommonComponents.optionStatus(optionText, false) : getPercentValueText(optionText, value),
			OptionInstance.UnitDouble.INSTANCE,
			1.0,
			value -> {}
	);

	private static Component getPercentValueText(Component prefix, double value) {
		return Component.translatable("options.percent_value", prefix, (int)(value * 100.0));
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ScreenshakeS2CPayload.ID, (payload, context) -> {
			Screenshake screenshake = new Screenshake(payload.duration(), payload.posIntensity(), payload.rotationIntensity());
			context.client().execute(screenshake::start);
		});
	}
}
