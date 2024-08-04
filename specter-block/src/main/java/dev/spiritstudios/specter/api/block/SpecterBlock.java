package dev.spiritstudios.specter.api.block;

import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import dev.spiritstudios.specter.impl.block.SpecterBlockImpl;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of {@link Block} that allows for additional properties to be set.
 */
public interface SpecterBlock {
	@Nullable
	Block specter$getStrippedBlock();

	@Nullable
	SpecterItemGroup specter$getGroup();

	int specter$getSpread();

	int specter$getBurn();

	boolean specter$isFlammable();

	/**
	 * Extension of {@link AbstractBlock.Settings} that allows for additional properties to be set.
	 * Do not use this interface directly, it is injected into {@link AbstractBlock.Settings} for you.
	 */
	interface Settings {
		default AbstractBlock.Settings strippedBlock(Block strippedBlock) {
			SpecterBlockImpl.computeIfAbsent((AbstractBlock.Settings) this).strippedBlock(strippedBlock);
			return (AbstractBlock.Settings) this;
		}

		default AbstractBlock.Settings flammable() {
			SpecterBlockImpl.computeIfAbsent((AbstractBlock.Settings) this).flammable();
			return (AbstractBlock.Settings) this;
		}

		default AbstractBlock.Settings spread(int spread) {
			SpecterBlockImpl.computeIfAbsent((AbstractBlock.Settings) this).spread(spread);
			return (AbstractBlock.Settings) this;
		}

		default AbstractBlock.Settings burn(int burn) {
			SpecterBlockImpl.computeIfAbsent((AbstractBlock.Settings) this).burn(burn);
			return (AbstractBlock.Settings) this;
		}

		default AbstractBlock.Settings group(SpecterItemGroup group) {
			SpecterBlockImpl.computeIfAbsent((AbstractBlock.Settings) this).group(group);
			return (AbstractBlock.Settings) this;
		}
	}
}
