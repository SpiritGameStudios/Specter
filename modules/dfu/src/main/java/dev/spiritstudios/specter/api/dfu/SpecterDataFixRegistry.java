package dev.spiritstudios.specter.api.dfu;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;

import dev.spiritstudios.specter.impl.dfu.SpecterDataFixerRegistryImpl;

public final class SpecterDataFixRegistry {
	public static void register(
		String modId,
		int currentDataVersion,
		DataFixer dataFixer
	) {
		SpecterDataFixerRegistryImpl.get().register(modId, currentDataVersion, dataFixer);
	}

	public static Schema createRootSchema() {
		return SpecterDataFixerRegistryImpl.get().createRootSchema();
	}
}
