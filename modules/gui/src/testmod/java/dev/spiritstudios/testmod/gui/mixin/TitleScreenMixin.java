package dev.spiritstudios.testmod.gui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.spiritstudios.testmod.gui.SpecterGuiTestScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Component title) {
		super(title);
	}

	@Inject(
		method = "init",
		at = @At("HEAD")
	)
	protected void init(CallbackInfo ci) {
		if (this.minecraft == null) return;

		this.addRenderableWidget(Button.builder(
				Component.nullToEmpty("SPECTRE GUI TEST"),
				button -> this.minecraft.setScreen(new SpecterGuiTestScreen()))
			.bounds(0, 0, 100, 20)
			.build()
		);
	}
}
