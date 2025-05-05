package dev.spiritstudios.specter.impl.debug.command;

import java.util.Objects;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ComponentsCommand {
	private static final SimpleCommandExceptionType NO_COMPONENTS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.components.no_components"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("components")
			.requires(source -> source.hasPermissionLevel(2))
			.executes(context -> components(Objects.requireNonNull(context.getSource().getPlayer()).getMainHandStack(), context.getSource()))
			.then(CommandManager.argument("target", EntityArgumentType.player())
				.executes(context -> components(EntityArgumentType.getPlayer(context, "target").getMainHandStack(), context.getSource()))
			)
		);
	}

	private static int components(ItemStack stack, ServerCommandSource source) throws CommandSyntaxException {
		ComponentChanges components = stack.getComponentChanges();
		if (components.isEmpty())
			throw NO_COMPONENTS_EXCEPTION.create();


		NbtElement output = ComponentChanges.CODEC.encodeStart(NbtOps.INSTANCE, components).getOrThrow();

		source.sendFeedback(() -> NbtHelper.toPrettyPrintedText(output), false);

		return Command.SINGLE_SUCCESS;
	}
}
