package dev.spiritstudios.specter.impl.render.client;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import dev.spiritstudios.specter.api.render.client.shake.Screenshake;
import dev.spiritstudios.specter.api.render.shake.ScreenshakeS2CPayload;

public class SpecterRenderClient implements ClientModInitializer {
	public static final SimpleOption<Double> SCREENSHAKE_INTENSITY = new SimpleOption<>(
			"options.specter.screenshake_intensity",
			SimpleOption.constantTooltip(Text.translatable("options.specter.screenshake_intensity.description")),
			(optionText, value) -> value == 0.0 ? ScreenTexts.composeToggleText(optionText, false) : getPercentValueText(optionText, value),
			SimpleOption.DoubleSliderCallbacks.INSTANCE,
			1.0,
			value -> {}
	);

	private static Text getPercentValueText(Text prefix, double value) {
		return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ScreenshakeS2CPayload.ID, (payload, context) -> {
			Screenshake screenshake = new Screenshake(payload.duration(), payload.posIntensity(), payload.rotationIntensity());
			context.client().execute(screenshake::start);
		});
	}
}
