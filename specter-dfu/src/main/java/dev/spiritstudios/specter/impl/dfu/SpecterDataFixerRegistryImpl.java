package dev.spiritstudios.specter.impl.dfu;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import dev.spiritstudios.specter.mixin.dfu.DataFixTypesAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

import java.util.Map;

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

	public NbtCompound update(
		DataFixTypes types,
		NbtCompound compound
	) {
		Dynamic<NbtElement> dynamic = new Dynamic<>(NbtOps.INSTANCE, compound);

		NbtCompound specterDataVersions = compound.contains("SpecterDataVersions", NbtElement.COMPOUND_TYPE) ?
			compound.getCompound("SpecterDataVersions") :
			new NbtCompound();

		for (Map.Entry<String, VersionedDataFixer> entry : dataFixers.entrySet()) {
			String modId = entry.getKey();
			VersionedDataFixer value = entry.getValue();

			int dataVersion = specterDataVersions.getInt(modId);

			dynamic = value.dataFixer().update(
				((DataFixTypesAccessor) (Object) types).getTypeReference(),
				dynamic,
				dataVersion, value.currentDataVersion()
			);
		}

		return (NbtCompound) dynamic.getValue();
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
