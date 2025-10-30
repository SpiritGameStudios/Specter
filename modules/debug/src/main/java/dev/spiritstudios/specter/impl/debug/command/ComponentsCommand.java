package dev.spiritstudios.specter.impl.debug.command;

import java.util.Objects;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

public class ComponentsCommand {
	private static final SimpleCommandExceptionType NO_COMPONENTS_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.components.no_components"));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("components")
				.requires(source -> source.hasPermission(2))
				.executes(context -> components(Objects.requireNonNull(context.getSource().getPlayer()).getMainHandItem(), context.getSource()))
				.then(Commands.argument("target", EntityArgument.player())
						.executes(context -> components(EntityArgument.getPlayer(context, "target").getMainHandItem(), context.getSource()))
				)
		);
	}

	private static int components(ItemStack stack, CommandSourceStack source) throws CommandSyntaxException {
		DataComponentPatch components = stack.getComponentsPatch();
		if (components.isEmpty())
			throw NO_COMPONENTS_EXCEPTION.create();

		RegistryOps<Tag> ops = source.registryAccess().createSerializationContext(NbtOps.INSTANCE);
		Tag output = DataComponentPatch.CODEC.encodeStart(ops, components).getOrThrow();

		source.sendSuccess(() -> NbtUtils.toPrettyComponent(output), false);

		return Command.SINGLE_SUCCESS;
	}
}
