package dev.spiritstudios.specter.impl.debug;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.item.ItemMetatags;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.Block;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class SpecterDebugClient implements ClientModInitializer {
	private static Text getMetatagHeader(String key) {
		return Text.translatable("tooltip.metatag." + key).formatted(Formatting.DARK_PURPLE);
	}

	private static Text getMetatagText(String key, Object... values) {
		return Text.translatable("tooltip.metatag." + key + ".text", values).formatted(Formatting.BLUE);
	}

	private static void addMetatagLine(List<Text> lines, String key, Object... values) {
		lines.add(getMetatagHeader(key));
		lines.add(getMetatagText(key, values));
	}

	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
			if (tooltipType != TooltipType.ADVANCED) return;

			ItemMetatags.FUEL.get(stack.getItem()).ifPresent(entry -> addMetatagLine(lines, "fuel", entry));
			ItemMetatags.COMPOSTING_CHANCE.get(stack.getItem()).ifPresent(entry -> addMetatagLine(lines, "composting_chance", entry * 100));

			Block block = Block.getBlockFromItem(stack.getItem());

			BlockMetatags.FLAMMABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "flammable", entry.burn(), entry.spread()));
			BlockMetatags.FLATTENABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "flattenable", entry.getBlock().getName()));
			BlockMetatags.OXIDIZABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "oxidizable", entry.getName()));
			BlockMetatags.STRIPPABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "strippable", entry.getName()));
			BlockMetatags.WAXABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "waxable", entry.getName()));
		});
	}
}
