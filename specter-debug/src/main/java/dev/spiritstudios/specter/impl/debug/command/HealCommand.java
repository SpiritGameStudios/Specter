package dev.spiritstudios.specter.impl.debug.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class HealCommand {
	private static final SimpleCommandExceptionType NOT_LIVING_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.heal.not_living"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("heal")
			.requires(source -> source.hasPermissionLevel(2))
			.executes(context -> fullHeal(context.getSource().getEntityOrThrow(), context.getSource()))
			.then(CommandManager.argument("target", EntityArgumentType.entity())
				.executes(context -> fullHeal(EntityArgumentType.getEntity(context, "target"), context.getSource()))
				.then(CommandManager.argument("amount", FloatArgumentType.floatArg(0))
					.executes(context -> heal(EntityArgumentType.getEntity(context, "target"), FloatArgumentType.getFloat(context, "amount"), context.getSource()))
				)
			)
		);
	}

	private static int fullHeal(Entity entity, ServerCommandSource source) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity livingEntity))
			throw NOT_LIVING_EXCEPTION.create();

		return heal(entity, livingEntity.getMaxHealth(), source);
	}

	private static int heal(Entity entity, float amount, ServerCommandSource source) throws CommandSyntaxException {
		if (!(entity instanceof LivingEntity livingEntity))
			throw NOT_LIVING_EXCEPTION.create();

		float oldHealth = livingEntity.getHealth();
		livingEntity.heal(amount);

		source.sendFeedback(() -> Text.translatable("commands.heal.success", livingEntity.getDisplayName(), livingEntity.getHealth() - oldHealth), true);

		return Command.SINGLE_SUCCESS;
	}
}
