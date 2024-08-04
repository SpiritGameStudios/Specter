package dev.spiritstudios.specter.impl.block;

import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

public class SpecterBlockImpl {
	private static final WeakHashMap<AbstractBlock.Settings, SpecterBlockImpl.Data> DATA = new WeakHashMap<>();

	public static SpecterBlockImpl.Data computeIfAbsent(AbstractBlock.Settings settings) {
		return DATA.computeIfAbsent(settings, s -> new SpecterBlockImpl.Data());
	}

	public static SpecterBlockImpl.Data get(AbstractBlock.Settings settings) {
		return DATA.get(settings);
	}

	public static final class Data {
		@Nullable
		private SpecterItemGroup group;

		@Nullable
		private Block strippedBlock;

		private boolean flammable;

		private int burn = 5;
		private int spread = 5;

		public void group(SpecterItemGroup group) {
			this.group = group;
		}

		public @Nullable SpecterItemGroup getGroup() {
			return group;
		}

		public void strippedBlock(Block strippedBlock) {
			this.strippedBlock = strippedBlock;
		}

		public @Nullable Block getStrippedBlock() {
			return strippedBlock;
		}

		public void flammable() {
			this.flammable = true;
		}

		public boolean isFlammable() {
			return flammable;
		}

		public void burn(int burn) {
			this.burn = burn;
		}

		public int getBurn() {
			return burn;
		}

		public void spread(int spread) {
			this.spread = spread;
		}

		public int getSpread() {
			return spread;
		}
	}
}
