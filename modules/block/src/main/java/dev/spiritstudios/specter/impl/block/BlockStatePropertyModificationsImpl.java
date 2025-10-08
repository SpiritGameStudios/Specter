//package dev.spiritstudios.specter.impl.block;
//
//import java.util.Map;
//import java.util.function.Consumer;
//
//import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
//import org.jetbrains.annotations.Nullable;
//
//import net.minecraft.block.Block;
//
//import dev.spiritstudios.specter.api.block.BlockStatePropertyModificationContext;
//
//public final class BlockStatePropertyModificationsImpl {
//	private static final Map<Block, Consumer<BlockStatePropertyModificationContext>> modifiers = new Object2ObjectOpenHashMap<>();
//
//	public static void add(Block block, Consumer<BlockStatePropertyModificationContext> modifier) {
//		modifiers.put(block, modifier);
//	}
//
//	public static @Nullable Consumer<BlockStatePropertyModificationContext> getModifier(Block block) {
//		return modifiers.get(block);
//	}
//}
