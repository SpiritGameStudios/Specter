package dev.spiritstudios.specter.impl.render.shake;

import static net.minecraft.server.command.CommandManager.argument;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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
						argument("viewers", EntityArgumentType.players()).then(
								argument("duration", FloatArgumentType.floatArg(0.0F)).then(
										argument("posIntensity", FloatArgumentType.floatArg(0.0F)).then(
												argument("rotationIntensity", FloatArgumentType.floatArg(0.0F))
														.executes(context ->
																run(context,
																		FloatArgumentType.getFloat(context, "duration"),
																		FloatArgumentType.getFloat(context, "posIntensity"),
																		FloatArgumentType.getFloat(context, "rotationIntensity"),
																		EntityArgumentType.getPlayers(context, "viewers")))
										)
								)
						)
				));
	}

	private static int run(CommandContext<ServerCommandSource> context, float duration, float posIntensity, float rotationIntensity, Collection<ServerPlayerEntity> viewers) {
		ScreenshakeS2CPayload payload = new ScreenshakeS2CPayload(duration, posIntensity, rotationIntensity);
		viewers.forEach(player -> ServerPlayNetworking.send(player, payload));
		return Command.SINGLE_SUCCESS;
	}
}
