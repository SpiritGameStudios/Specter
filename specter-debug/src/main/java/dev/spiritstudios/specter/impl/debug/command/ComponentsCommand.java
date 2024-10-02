package dev.spiritstudios.specter.impl.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
		if (components.isEmpty() || !(stack.encode(source.getRegistryManager()) instanceof NbtCompound nbt) || !nbt.contains("components"))
			throw NO_COMPONENTS_EXCEPTION.create();

		NbtCompound componentsNbt = nbt.getCompound("components");
		List<MutableText> componentsText = new ArrayList<>();
		for (int i = 0; i < componentsNbt.getKeys().size(); i++) {
			String key = componentsNbt.getKeys().toArray(new String[0])[i];

			MutableText text = Text.empty();
			NbtElement tag = componentsNbt.get(key);

			if (tag == null) tag = new NbtCompound();

			text.append(Text.literal(key).formatted(Formatting.DARK_AQUA));
			text.append(Text.literal("="));
			text.append(NbtHelper.toPrettyPrintedText(tag));

			if (i != componentsNbt.getKeys().size() - 1)
				text.append(Text.literal(", "));

			componentsText.add(text);
		}

		MutableText output = Text.literal("[");
		componentsText.forEach(output::append);
		output.append(Text.literal("]"));

		source.sendFeedback(() -> output, false);

		return Command.SINGLE_SUCCESS;
	}
}
