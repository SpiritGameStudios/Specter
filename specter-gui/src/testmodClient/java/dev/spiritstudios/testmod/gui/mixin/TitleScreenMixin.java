package dev.spiritstudios.testmod.gui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import dev.spiritstudios.testmod.gui.SpecterGuiTestScreen;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(
			method = "init",
			at = @At("HEAD")
	)
	protected void init(CallbackInfo ci) {
		if (this.client == null) return;

		this.addDrawableChild(ButtonWidget.builder(
						Text.of("SPECTRE GUI TEST"),
						button -> this.client.setScreen(new SpecterGuiTestScreen()))
				.dimensions(0, 10, 100, 20)
				.build()
		);
	}
}
