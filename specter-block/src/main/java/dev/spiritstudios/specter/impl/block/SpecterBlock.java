package dev.spiritstudios.specter.impl.block;

import dev.spiritstudios.specter.api.block.BlockAttachments;
import net.fabricmc.api.ModInitializer;

public class SpecterBlock implements ModInitializer {
	@Override
	public void onInitialize() {
		BlockAttachments.init();
	}
}
