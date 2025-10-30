package dev.spiritstudios.specter.impl.debug.client;

import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import dev.spiritstudios.specter.api.block.BlockMetatags;
import dev.spiritstudios.specter.api.item.ItemMetatags;

public class SpecterDebugClient implements ClientModInitializer {
	private static void addMetatagLine(List<Component> lines, String key, Object... values) {
		lines.add(Component.translatable("tooltip.metatag." + key).withStyle(ChatFormatting.DARK_PURPLE));
		lines.add(Component.translatable("tooltip.metatag." + key + ".text", values).withStyle(ChatFormatting.BLUE));
	}

	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
			if (tooltipType != TooltipFlag.ADVANCED) return;

			ItemMetatags.FUEL.get(stack.getItem()).ifPresent(entry -> addMetatagLine(lines, "fuel", entry));
			ItemMetatags.COMPOSTING_CHANCE.get(stack.getItem()).ifPresent(entry -> addMetatagLine(lines, "composting_chance", entry * 100F));

			Block block = Block.byItem(stack.getItem());

			BlockMetatags.FLAMMABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "flammable", entry.igniteOdds(), entry.burnOdds()));
			BlockMetatags.FLATTENABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "flattenable", entry.getBlock().getName()));
			BlockMetatags.OXIDIZABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "oxidizable", entry.getName()));
			BlockMetatags.STRIPPABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "strippable", entry.getName()));
			BlockMetatags.WAXABLE.get(block).ifPresent(entry -> addMetatagLine(lines, "waxable", entry.getName()));
		});

		SoundInstanceDebugRenderer soundInstanceDebugRenderer = new SoundInstanceDebugRenderer();

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			client.getSoundManager().addListener(soundInstanceDebugRenderer);
		});

//		DebugRenderers.register(Specter.id("sound_instance"), soundInstanceDebugRenderer);
	}
}
