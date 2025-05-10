package dev.spiritstudios.testmod.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.gui.client.bridget.Bridgets;
import dev.spiritstudios.specter.api.gui.client.bridget.layout.GridBridget;
import dev.spiritstudios.specter.api.gui.client.bridget.layout.HorizontalAlign;

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

		GridBridget grid = new GridBridget()
				.rows(3).columns(1);

		GridBridget grid2 = new GridBridget()
				.rows(3).columns(3);

		this.addDrawable(Bridgets.color(width, height)
				.color(0x11FF0000)
				.child(Bridgets.color(400, 400)
						.color(0x1100FF00)
						.horizontalAlign(HorizontalAlign.LEFT)
						.child(grid.size(350, 350).pos(0, 0)
								.child(Bridgets.color(300, 100)
										.child(Bridgets.text("Test String").color(0xFF000000)))
								.child(Bridgets.black(300, 100)
										.child(Bridgets.text("Test String 2")))
								.child(Bridgets.color(300, 100)
										.child(Bridgets.text("Test String 3").color(0xFF000000)))))
				.child(Bridgets.color(400, 400)
						.color(0x110000FF)
						.horizontalAlign(HorizontalAlign.RIGHT)
						.child(grid2.size(350, 350).pos(0, 0)
								.child(Bridgets.color(100, 100)
										.child(Bridgets.text("Test String").color(0xFF000000)))
								.child(Bridgets.black(100, 100)
										.child(Bridgets.text("Test String 2")))
								.child(Bridgets.color(100, 100)
										.child(Bridgets.text("Test String 3").color(0xFF000000)))
								.child(Bridgets.black(100, 100)
										.child(Bridgets.text("Test String")))
								.child(Bridgets.color(100, 100)
										.child(Bridgets.text("Test String 2").color(0xFF000000)))
								.child(Bridgets.black(100, 100)
										.child(Bridgets.text("Test String 3")))
								.child(Bridgets.color(300, 100)
										.child(Bridgets.text("Test String").color(0xFF000000)))))
		);

		grid.layoutChildren();
		grid2.layoutChildren();

		super.init();
	}
}
