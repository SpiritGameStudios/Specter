package testmod.gui;

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
		super.init();
	}
}
