package dev.spiritstudios.testmod.gui;

import dev.spiritstudios.specter.api.gui.client.widget.SpecterDirectoryChooserWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SpecterGuiTestScreen extends Screen {
	public SpecterGuiTestScreen() {
		super(Component.nullToEmpty("Spectre GUI Test"));
	}

	@Override
	public void init() {
		SpecterGuiTestMod.LOGGER.info("Opened test screen");

		this.addRenderableWidget(SpecterDirectoryChooserWidget.builder(
				null, SpecterDirectoryChooserWidget.DirectoryChooserType.FILE
		).position(0, 0).build());

		super.init();
	}
}
