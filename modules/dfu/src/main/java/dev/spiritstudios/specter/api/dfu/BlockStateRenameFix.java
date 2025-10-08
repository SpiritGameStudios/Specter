package dev.spiritstudios.specter.api.dfu;

import java.util.Objects;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;

public class BlockStateRenameFix extends DataFix {
	private final String name;
	private final String blockId;
	private final String defaultValue;
	private final String oldName, newName;

	public BlockStateRenameFix(String name, Identifier blockId, String oldName, String newName, String defaultValue, Schema outputSchema) {
		super(outputSchema, false);
		this.name = name;
		this.defaultValue = defaultValue;
		this.blockId = blockId.toString();
		this.oldName = oldName;
		this.newName = newName;
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
				this.name,
				this.getInputSchema().getType(TypeReferences.BLOCK_STATE),
				typed -> typed.update(
						DSL.remainderFinder(),
						state -> {
							// BLOCK_STATE object is
							// {
							//     "Name": "namespace:path",
							//     "Properties": {
							//         "propertyName": "propertyValue"
							//     }
							// }

							var name = state.get("Name").asString().result();
							if (name.isEmpty() || !Objects.equals(blockId, name.get())) return state;

							return state.update(
									"Properties",
									properties -> {
										// Get the value from the old name, or the default value of our new state
										String value = properties.get(oldName).asString(defaultValue);
										// Removing a non-existent value doesn't fail on any vanilla DynamicOps. They will just ignore the remove request.
										return properties.remove(oldName).set(newName, properties.createString(value));
									}
							);
						}
				)
		);
	}
}
