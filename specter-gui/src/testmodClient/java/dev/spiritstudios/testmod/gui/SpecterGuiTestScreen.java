package dev.spiritstudios.testmod.gui;

import dev.spiritstudios.specter.api.gui.client.bridget.Bridget;
import dev.spiritstudios.specter.api.gui.client.bridget.TextBridget;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.gui.client.widget.SpecterDirectoryChooserWidget;

public class SpecterGuiTestScreen extends Screen {
	public SpecterGuiTestScreen() {
		super(Text.of("Spectre GUI Test"));
	}

	@Override
	public void init() {
		SpecterGuiTestMod.LOGGER.info("Opened test screen");

		/*this.addDrawableChild(SpecterDirectoryChooserWidget.builder(
				null, SpecterDirectoryChooserWidget.DirectoryChooserType.FILE
		).position(0, 0).build());*/

		this.addDrawable(new TextBridget.Builder().setContent(Text.of("hi")).setX(0).setY(0).build());

		super.init();
	}
}
