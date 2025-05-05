package dev.spiritstudios.specter.impl.render.shake;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import dev.spiritstudios.specter.api.render.shake.ScreenshakeS2CPayload;

public class ScreenshakeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
		dispatcher.register(CommandManager.literal("screenshake")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
						CommandManager.argument("viewers", EntityArgumentType.players()).then(
								CommandManager.argument("duration", DoubleArgumentType.doubleArg()).then(
										CommandManager.argument("posIntensity", DoubleArgumentType.doubleArg()).then(
												CommandManager.argument(
														"rotationIntensity",
														DoubleArgumentType.doubleArg()
												).executes(context ->
														run(context,
																DoubleArgumentType.getDouble(context, "duration"),
																DoubleArgumentType.getDouble(context, "posIntensity"),
																DoubleArgumentType.getDouble(context, "rotationIntensity"),
																EntityArgumentType.getPlayers(context, "viewers")))
										)
								)
						)
				));
	}

	private static int run(CommandContext<ServerCommandSource> context, double duration, double posIntensity, double rotationIntensity, Collection<ServerPlayerEntity> viewers) {
		ScreenshakeS2CPayload payload = new ScreenshakeS2CPayload(duration, posIntensity, rotationIntensity);
		viewers.forEach(player -> ServerPlayNetworking.send(player, payload));
		return Command.SINGLE_SUCCESS;
	}
}
