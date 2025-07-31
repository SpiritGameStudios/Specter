package dev.spiritstudios.specter.impl.dfu;

import java.util.Map;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;

import dev.spiritstudios.specter.mixin.dfu.DataFixTypesAccessor;

public final class SpecterDataFixerRegistryImpl {
	private static SpecterDataFixerRegistryImpl INSTANCE;

	private final Schema vanillaSchema;
	private final Map<String, VersionedDataFixer> dataFixers = new Object2ObjectOpenHashMap<>();

	public SpecterDataFixerRegistryImpl(Schema vanillaSchema) {
		this.vanillaSchema = vanillaSchema;
	}

	public static SpecterDataFixerRegistryImpl get() {
		if (INSTANCE != null) return INSTANCE;

		Schema vanillaSchema = Schemas.getFixer()
				.getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getSaveVersion().getId()));

		INSTANCE = new SpecterDataFixerRegistryImpl(vanillaSchema);
		return INSTANCE;
	}

	public Schema createRootSchema() {
		return new Schema(0, vanillaSchema);
	}

	public void register(
			String modId,
			int currentDataVersion,
			DataFixer dataFixer
	) {
		if (dataFixers.containsKey(modId))
			throw new IllegalArgumentException("Mod with id %s attempted to register multiple DataFixers".formatted(modId));

		dataFixers.put(modId, new VersionedDataFixer(dataFixer, currentDataVersion));
	}

	public <T> T update(
			DataFixTypes types,
			Dynamic<T> dynamic
	) {
		OptionalDynamic<T> specterDataVersions = dynamic.get("SpecterDataVersions");

		for (Map.Entry<String, VersionedDataFixer> entry : dataFixers.entrySet()) {
			String modId = entry.getKey();
			VersionedDataFixer value = entry.getValue();

			int dataVersion = specterDataVersions.get(modId).asInt(0);

			dynamic = value.dataFixer().update(
					((DataFixTypesAccessor) (Object) types).getTypeReference(),
					dynamic,
					dataVersion, value.currentDataVersion()
			);
		}

		return dynamic.getValue();
	}

	public NbtCompound writeDataVersions(NbtCompound compound) {
		NbtCompound dataVersions = new NbtCompound();
		dataFixers.forEach((key, value) -> {
			dataVersions.putInt(key, value.currentDataVersion());
		});

		compound.put("SpecterDataVersions", dataVersions);
		return compound;
	}

	record VersionedDataFixer(DataFixer dataFixer, int currentDataVersion) {
	}
}
