package dev.spiritstudios.specter.api.item.datagen;

import dev.spiritstudios.specter.impl.item.DataItemGroup;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public abstract class SpecterItemGroupProvider extends FabricCodecDataProvider<DataItemGroup> {
	public SpecterItemGroupProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(dataOutput, registriesFuture, DataOutput.OutputType.DATA_PACK, "item_group", DataItemGroup.CODEC);
	}

	@Override
	public String getName() {
		return "Specter Item Groups";
	}
}
