package dev.spiritstudios.specter.api.item.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.data.DataOutput;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import dev.spiritstudios.specter.impl.item.DataItemGroup;


public abstract class SpecterItemGroupProvider extends FabricCodecDataProvider<DataItemGroup> {
	public SpecterItemGroupProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(dataOutput, registriesFuture, DataOutput.OutputType.DATA_PACK, "item_group", DataItemGroup.CODEC);
	}

	@ApiStatus.Internal
	@Override
	protected void configure(BiConsumer<Identifier, DataItemGroup> provider, RegistryWrapper.WrapperLookup lookup) {
		generate((id, data) -> provider.accept(
				id,
				new DataItemGroup(
					id.toTranslationKey("item_group"),
					data.icon(),
					data.items()
				)
			),
			lookup
		);
	}

	protected abstract void generate(BiConsumer<Identifier, ItemGroupData> provider, RegistryWrapper.WrapperLookup lookup);

	@Override
	public String getName() {
		return "Specter Item Groups";
	}

	public record ItemGroupData(Identifier id, ItemConvertible icon, List<ItemStack> items) {
		public static ItemGroupData of(Identifier id, ItemConvertible icon, List<ItemStack> items) {
			return new ItemGroupData(id, icon, items);
		}
	}
}
