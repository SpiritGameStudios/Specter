package dev.spiritstudios.specter.mixin.debug;

import dev.spiritstudios.specter.api.core.exception.UnreachableException;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.custom.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(DebugInfoSender.class)
public abstract class DebugInfoSenderMixin {
	@Shadow
	private static List<String> listMemories(LivingEntity entity, long currentTime) {
		throw new UnreachableException();
	}

	@Shadow
	private static void sendToAll(ServerWorld world, CustomPayload payload) {
		throw new UnreachableException();
	}

	@SuppressWarnings("deprecation")
	@Inject(method = "sendPoiAddition", at = @At("HEAD"))
	private static void sendPoiAddition(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		world.getPointOfInterestStorage().getType(pos)
			.map(RegistryEntry::getIdAsString)
			.ifPresent(name -> sendToAll(
				world,
				new DebugPoiAddedCustomPayload(
					pos,
					name,
					world.getPointOfInterestStorage().getFreeTickets(pos)
				))
			);
	}

	@Inject(method = "sendPoiRemoval", at = @At("HEAD"))
	private static void sendPoiRemoval(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		sendToAll(world, new DebugPoiRemovedCustomPayload(pos));
	}

	// dumbest yarn mapping ive ever seen
	@SuppressWarnings("deprecation")
	@Inject(method = "sendPointOfInterest", at = @At("HEAD"))
	private static void sendPoiTicketsCount(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		sendToAll(world, new DebugPoiTicketCountCustomPayload(pos, world.getPointOfInterestStorage().getFreeTickets(pos)));
	}

	@Inject(method = "sendPoi", at = @At("HEAD"))
	private static void sendPoi(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		Registry<Structure> structures = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);

		for (RegistryEntry<Structure> entry : structures.iterateEntries(StructureTags.VILLAGE)) {
			if (world.getStructureAccessor().getStructureStarts(chunkSectionPos, entry.value()).isEmpty()) continue;

			sendToAll(world, new DebugVillageSectionsCustomPayload(Set.of(chunkSectionPos), Set.of()));
			return;
		}

		sendToAll(world, new DebugVillageSectionsCustomPayload(Set.of(), Set.of(chunkSectionPos)));
	}

	@Inject(method = "sendPathfindingData", at = @At("HEAD"))
	private static void sendPathfindingData(World world, MobEntity mob, @Nullable Path path, float nodeReachProximity, CallbackInfo ci) {
		if (path == null || world.isClient()) return;
		sendToAll((ServerWorld) world, new DebugPathCustomPayload(mob.getId(), path, nodeReachProximity));
	}

	@Inject(method = "sendNeighborUpdate", at = @At("HEAD"))
	private static void sendNeighborUpdate(World world, BlockPos pos, CallbackInfo ci) {
		sendToAll((ServerWorld) world, new DebugNeighborsUpdateCustomPayload(world.getTime(), pos));
	}

	@SuppressWarnings("deprecation")
	@Inject(method = "sendStructureStart", at = @At("HEAD"))
	private static void sendStructureStart(StructureWorldAccess world, StructureStart structureStart, CallbackInfo ci) {
		ServerWorld serverWorld = null;
		if (world instanceof ChunkRegion region) serverWorld = region.toServerWorld();
		else if (world instanceof ServerWorld s) serverWorld = s;

		if (serverWorld == null) return;

		sendToAll(
			serverWorld,
			new DebugStructuresCustomPayload(
				serverWorld.getRegistryKey(),
				structureStart.getBoundingBox(),
				structureStart.getChildren().stream()
					.map(piece -> new DebugStructuresCustomPayload.Piece(
						piece.getBoundingBox(),
						piece.getChainLength() == 0)
					).toList()
			)
		);
	}

	@Inject(method = "sendGoalSelector", at = @At("HEAD"))
	private static void sendGoalSelector(World world, MobEntity mob, GoalSelector goalSelector, CallbackInfo ci) {
		sendToAll(
			(ServerWorld) world,
			new DebugGoalSelectorCustomPayload(
				mob.getId(),
				mob.getBlockPos(),
				goalSelector.getGoals().stream()
					.map(goal -> new DebugGoalSelectorCustomPayload.Goal(
						goal.getPriority(),
						goal.isRunning(),
						goal.getGoal().toString()
					)).toList()
			)
		);
	}

	@Inject(method = "sendRaids", at = @At("HEAD"))
	private static void sendRaids(ServerWorld server, Collection<Raid> raids, CallbackInfo ci) {
		sendToAll(server, new DebugRaidsCustomPayload(raids.stream().map(Raid::getCenter).toList()));
	}

	@SuppressWarnings("deprecation")
	@Inject(method = "sendBrainDebugData", at = @At("HEAD"))
	private static void sendBrainDebugData(LivingEntity living, CallbackInfo ci) {
		Brain<?> brain = living.getBrain();

		String profession = "";
		int experience = 0;
		boolean wantsGolem = false;
		List<String> gossips = List.of();
		Set<BlockPos> pois = Set.of();
		Set<BlockPos> potentialPois = Set.of();

		if (living instanceof VillagerEntity villager) {
			profession = villager.getVillagerData().getProfession().toString();
			experience = villager.getExperience();
			wantsGolem = villager.canSummonGolem(villager.getWorld().getTime());

			List<String> newGossips = new ArrayList<>();
			villager.getGossip().getEntityReputationAssociatedGossips().forEach((key, value) -> value.forEach((villageGossipType, integer) -> newGossips.add(
				"%s: %s: %d".formatted(NameGenerator.name(key), villageGossipType, integer)
			)));
			gossips = newGossips;
			pois = Stream.of(MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT)
				.map(brain::getOptionalMemory)
				.filter(Objects::nonNull)
				.flatMap(Optional::stream)
				.map(GlobalPos::pos)
				.collect(Collectors.toSet());

			potentialPois = Optional.ofNullable(brain.getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE)).orElse(Optional.empty())
				.map(site -> Set.of(site.pos())).orElse(Set.of());
		}

		sendToAll(
			(ServerWorld) living.getWorld(),
			new DebugBrainCustomPayload(new DebugBrainCustomPayload.Brain(
				living.getUuid(),
				living.getId(),
				living.getDisplayName() != null ? living.getDisplayName().getString() : "",
				profession,
				experience,
				living.getHealth(),
				living.getMaxHealth(),
				living.getPos(),
				living instanceof InventoryOwner inventoryOwner ? inventoryOwner.getInventory().toString() : "",
				Optional.ofNullable(brain.getOptionalMemory(MemoryModuleType.PATH)).orElse(Optional.empty()).orElse(null),
				wantsGolem,
				living instanceof WardenEntity warden ? warden.getAnger() : -1,
				brain.getPossibleActivities().stream().map(Activity::getId).toList(),
				brain.getRunningTasks().stream().map(Task::getName).toList(),
				listMemories(living, living.getWorld().getTime()),
				gossips,
				pois,
				potentialPois
			))
		);
	}

	@Inject(method = "sendBeeDebugData", at = @At("HEAD"))
	private static void sendBeeDebugData(BeeEntity bee, CallbackInfo ci) {
		sendToAll(
			(ServerWorld) bee.getWorld(),
			new DebugBeeCustomPayload(new DebugBeeCustomPayload.Bee(
				bee.getUuid(),
				bee.getId(),
				bee.getPos(),
				Optional.ofNullable(bee.getBrain().getOptionalMemory(MemoryModuleType.PATH)).orElse(Optional.empty()).orElse(null),
				bee.getHivePos(),
				bee.getFlowerPos(),
				bee.getMoveGoalTicks(),
				bee.getGoalSelector().getGoals().stream()
					.map(goal -> goal.getGoal().toString())
					.collect(Collectors.toSet()),
				bee.getPossibleHives()
			))
		);
	}

	@Inject(method = "sendBreezeDebugData", at = @At("HEAD"))
	private static void sendBreezeDebugData(BreezeEntity breeze, CallbackInfo ci) {
		sendToAll(
			(ServerWorld) breeze.getWorld(),
			new DebugBreezeCustomPayload(new DebugBreezeCustomPayload.BreezeInfo(
				breeze.getUuid(),
				breeze.getId(),
				Optional.ofNullable(breeze.getTarget()).map(Entity::getId).orElse(null),
				Optional.ofNullable(breeze.getBrain().getOptionalMemory(MemoryModuleType.BREEZE_JUMP_TARGET)).orElse(Optional.empty()).orElse(null)
			))
		);
	}

	@Inject(method = "sendGameEvent", at = @At("HEAD"))
	private static void sendGameEvent(World world, RegistryEntry<GameEvent> event, Vec3d pos, CallbackInfo ci) {
		event.getKey().ifPresent(key -> sendToAll(
			(ServerWorld) world,
			new DebugGameEventCustomPayload(key, pos)
		));
	}

	@Inject(method = "sendGameEventListener", at = @At("HEAD"))
	private static void sendGameEventListener(World world, GameEventListener eventListener, CallbackInfo ci) {
		sendToAll((ServerWorld) world, new DebugGameEventListenersCustomPayload(eventListener.getPositionSource(), eventListener.getRange()));
	}

	@Inject(method = "sendBeehiveDebugData", at = @At("HEAD"))
	private static void sendBeehiveDebugData(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity, CallbackInfo ci) {
		sendToAll(
			(ServerWorld) world,
			new DebugHiveCustomPayload(new DebugHiveCustomPayload.HiveInfo(
				pos,
				Registries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString(),
				blockEntity.getBeeCount(),
				state.get(BeehiveBlock.HONEY_LEVEL),
				blockEntity.isSmoked()
			))
		);
	}
}
