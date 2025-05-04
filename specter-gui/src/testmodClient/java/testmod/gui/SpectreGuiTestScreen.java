package testmod.gui;

import dev.spiritstudios.specter.api.gui.widget.SpecterDirectoryChooserWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import testmod.SpectreGuiTestMod;

public class SpectreGuiTestScreen extends Screen {

	public SpectreGuiTestScreen() {
		super(Text.of("Spectre GUI Test"));
	}

	@Override
	public void init() {
		SpectreGuiTestMod.LOGGER.info("Opened test screen");

		this.addDrawableChild(SpecterDirectoryChooserWidget.builder(
			null, SpecterDirectoryChooserWidget.DirectoryChooserType.FILE
		).position(0, 0).build());

		super.init();
	}
}
