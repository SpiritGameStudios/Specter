package dev.spiritstudios.specter.impl.render.shake;

import static net.minecraft.commands.Commands.argument;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import dev.spiritstudios.specter.api.render.shake.ScreenshakeS2CPayload;

public class ScreenshakeCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
		dispatcher.register(Commands.literal("screenshake")
				.requires(source -> source.hasPermission(2))
				.then(
						argument("viewers", EntityArgument.players()).then(
								argument("duration", FloatArgumentType.floatArg(0.0F)).then(
										argument("posIntensity", FloatArgumentType.floatArg(0.0F)).then(
												argument("rotationIntensity", FloatArgumentType.floatArg(0.0F))
														.executes(context ->
																run(context,
																		FloatArgumentType.getFloat(context, "duration"),
																		FloatArgumentType.getFloat(context, "posIntensity"),
																		FloatArgumentType.getFloat(context, "rotationIntensity"),
																		EntityArgument.getPlayers(context, "viewers")))
										)
								)
						)
				));
	}

	private static int run(CommandContext<CommandSourceStack> context, float duration, float posIntensity, float rotationIntensity, Collection<ServerPlayer> viewers) {
		ScreenshakeS2CPayload payload = new ScreenshakeS2CPayload(duration, posIntensity, rotationIntensity);
		viewers.forEach(player -> ServerPlayNetworking.send(player, payload));
		return Command.SINGLE_SUCCESS;
	}
}
