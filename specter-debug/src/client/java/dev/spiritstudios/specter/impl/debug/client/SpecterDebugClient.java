package dev.spiritstudios.specter.impl.debug.client;

import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import net.minecraft.block.Block;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.core.client.debug.DebugRendererRegistry;
import dev.spiritstudios.specter.api.item.ItemMetatags;
import dev.spiritstudios.specter.impl.core.Specter;

public class SpecterDebugClient implements ClientModInitializer {
	private static void addMetatagLine(List<Text> lines, String key, Object... values) {
		lines.add(Text.translatable("tooltip.metatag." + key));
		lines.add(Text.translatable("tooltip.metatag." + key + ".text", values));
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

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			DebugRenderCommand.register(dispatcher);
		});

		SoundInstanceDebugRenderer soundInstanceDebugRenderer = new SoundInstanceDebugRenderer();

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			client.getSoundManager().registerListener(soundInstanceDebugRenderer);
		});

		DebugRendererRegistry.register(Specter.id("sound_instance"), soundInstanceDebugRenderer);
	}
}
