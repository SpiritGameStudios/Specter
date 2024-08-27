package dev.spiritstudios.specter.api.item.datagen;

import dev.spiritstudios.specter.api.item.DataItemGroup;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;


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
					data.items().stream().map(ItemConvertible::asItem).map(Item::getDefaultStack).toList()
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

	protected record ItemGroupData(Identifier id, ItemConvertible icon, List<ItemConvertible> items) {
	}
}
