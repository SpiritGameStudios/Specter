package dev.spiritstudios.specter.impl.block;

import dev.spiritstudios.specter.api.item.SpecterItemGroup;
import net.minecraft.block.AbstractBlock;
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

		public void group(SpecterItemGroup group) {
			this.group = group;
		}

		public @Nullable SpecterItemGroup getGroup() {
			return group;
		}
	}
}
