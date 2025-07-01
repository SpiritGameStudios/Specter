package dev.spiritstudios.testmod.config.mixin;

import dev.spiritstudios.specter.api.config.client.TabbedListConfigScreen;
import dev.spiritstudios.testmod.config.TestConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

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
						Text.of("SPECTRE CONFIG TEST"),
						button -> this.client.setScreen(new TabbedListConfigScreen(TestConfig.TOML_HOLDER, this)))
				.dimensions(0, 30, 100, 20)
				.build()
		);
	}
}
