package sxs.api.attachments;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.arguments.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.neoforged.neoforge.server.command.EnumArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

@EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void registerAttachmentTypes(RegisterAttachmentTypesEvent event) {
		// Primitive
		event.registerArg(Boolean.class, Codec.BOOL, ByteBufCodecs.BOOL, () -> false, (value) -> value ? "true" : "false", (context) -> BoolArgumentType.getBool(context, "value"), BoolArgumentType.bool());
		event.registerArg(Byte.class, Codec.BYTE, ByteBufCodecs.BYTE, () -> (byte) 0, (value) -> value + "b", (context) -> (byte) IntegerArgumentType.getInteger(context, "value"), IntegerArgumentType.integer(Byte.MIN_VALUE, Byte.MAX_VALUE));
		event.registerArg(Short.class, Codec.SHORT, ByteBufCodecs.SHORT, () -> (short) 0, (value) -> value + "s", (context) -> (short) IntegerArgumentType.getInteger(context, "value"), IntegerArgumentType.integer(Short.MIN_VALUE, Short.MAX_VALUE));
		event.registerArg(Integer.class, Codec.INT, ByteBufCodecs.INT, () -> 0, (context) -> IntegerArgumentType.getInteger(context, "value"), IntegerArgumentType.integer());
		event.registerArg(Long.class, Codec.LONG, ByteBufCodecs.LONG, () -> 0l, (value) -> value + "l", (context) -> LongArgumentType.getLong(context, "value"), LongArgumentType.longArg());
		event.registerArg(Float.class, Codec.FLOAT, ByteBufCodecs.FLOAT, () -> 0f, (value) -> value + "f", (context) -> FloatArgumentType.getFloat(context, "value"), FloatArgumentType.floatArg());
		event.registerArg(Double.class, Codec.DOUBLE, ByteBufCodecs.DOUBLE, () -> 0d, (value) -> value + "d", (context) -> DoubleArgumentType.getDouble(context, "value"), DoubleArgumentType.doubleArg());
		event.registerArg(String.class, Codec.STRING, ByteBufCodecs.STRING_UTF8, () -> "", (context) -> StringArgumentType.getString(context, "value"), StringArgumentType.greedyString());

		// Location
		event.register(GlobalPos.class, GlobalPos.CODEC, GlobalPos.STREAM_CODEC, () -> null);
		event.register(ChunkPos.class, ChunkPos.CODEC, ChunkPos.STREAM_CODEC, () -> null);
		event.registerArg(BlockPos.class, BlockPos.CODEC, BlockPos.STREAM_CODEC, () -> null, (context) -> BlockPosArgument.getBlockPos(context, "value"), BlockPosArgument.blockPos());
		event.registerArg(Vec3.class, Vec3.CODEC, Vec3.STREAM_CODEC, () -> null, (context) -> Vec3Argument.getVec3(context, "value"), Vec3Argument.vec3());

		// Data
		event.registerArg(UUID.class, UUIDUtil.CODEC, UUIDUtil.STREAM_CODEC, () -> null, (context) -> UuidArgument.getUuid(context, "value"), UuidArgument.uuid());
		event.register(VillagerData.class, VillagerData.CODEC, VillagerData.STREAM_CODEC, () -> null);
		event.registerArg(CompoundTag.class, CompoundTag.CODEC, ByteBufCodecs.COMPOUND_TAG, () -> null, (context) -> CompoundTagArgument.getCompoundTag(context, "value"), CompoundTagArgument.compoundTag());
		event.registerBuildArg(Component.class, ComponentSerialization.CODEC, ComponentSerialization.STREAM_CODEC, () -> null, (value) -> value.getString(), (context) -> ComponentArgument.getRawComponent(context, "value"), (buildContext) -> ComponentArgument.textComponent(buildContext));
		event.registerBuildArg(BlockState.class, BlockState.CODEC, ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), () -> null, (value) -> value.getBlock().toString(), (context) -> BlockStateArgument.getBlock(context, "value").getState(), (buildContext) -> BlockStateArgument.block(buildContext));
		event.registerArg(Direction.class, Direction.CODEC, Direction.STREAM_CODEC, () -> null, (context) -> (Direction) context.getArgument("value", Direction.class), EnumArgument.enumArgument(Direction.class));
		event.registerArg(DyeColor.class, DyeColor.CODEC, DyeColor.STREAM_CODEC, () -> null, (context) -> (DyeColor) context.getArgument("value", DyeColor.class), EnumArgument.enumArgument(DyeColor.class));
		event.registerBuildArg(ItemStack.class, ItemStack.CODEC, ItemStack.STREAM_CODEC, () -> ItemStack.EMPTY, (context) -> { try { return ItemArgument.getItem(context, "value").createItemStack(1, false); } catch(CommandSyntaxException e) { e.printStackTrace(); } return ItemStack.EMPTY; }, (buildContext) -> ItemArgument.item(buildContext));
	}
	
	@SubscribeEvent
	public static void trackEntity(PlayerEvent.StartTracking event) {
		if(event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof AttachmentHolder holder)
			PacketDistributor.sendToPlayer(player, new ClientboundAttachmentsPacket(holder.getId(), holder.getSyncableAttachments()));
	}
	
	@SubscribeEvent
	public static void joinServer(PlayerEvent.PlayerLoggedInEvent event) {
		if(event.getEntity() instanceof ServerPlayer player && player instanceof AttachmentHolder holder)
			PacketDistributor.sendToPlayer(player, new ClientboundAttachmentsPacket(holder.getId(), holder.getSyncableAttachments()));
	}
	
	@SubscribeEvent
	public static void clonePlayer(PlayerEvent.Clone event) {
		if(event.getEntity() instanceof AttachmentHolder newHolder && event.getOriginal() instanceof AttachmentHolder oldHolder) {
			newHolder.copyFrom(oldHolder, event.isWasDeath());
			PacketDistributor.sendToPlayersTrackingEntityAndSelf((ServerPlayer) event.getEntity(), new ClientboundAttachmentsPacket(newHolder.getId(), newHolder.getSyncableAttachments()));
		}
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("attachments").requires(Commands.hasPermission(2));
		AttachmentRegistrationType.registerCommands(base, event.getBuildContext());

		base.then(Commands.literal("list").then(Commands.argument("target", EntityArgument.entity()).executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				StringBuilder builder = new StringBuilder();
				for(AttachmentEntry<?> entry : holder.getAttachments()) {
					builder.append('\n');
					builder.append(" - [");
					builder.append(entry.getStoredClass().getSimpleName());
					builder.append("] ");
					builder.append(entry.getId().toString());
				}
				
				context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.list", entity.getDisplayName().getString(), builder.toString()), false);
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})));
		
		event.getDispatcher().register(base);
	}
}