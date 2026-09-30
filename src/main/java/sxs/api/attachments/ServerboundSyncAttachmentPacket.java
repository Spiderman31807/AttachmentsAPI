package sxs.api.attachments;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;

@EventBusSubscriber(modid = AttachmentsApiMod.MODID)
public record ServerboundSyncAttachmentPacket(int entityId, AttachmentType<?> attachment, Object value) implements CustomPacketPayload {
	public static final Type<ServerboundSyncAttachmentPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AttachmentsApiMod.MODID, "serverbound_sync_attachment"));
	public static final StreamCodec<FriendlyByteBuf, ServerboundSyncAttachmentPacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ServerboundSyncAttachmentPacket packet) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		registryBuffer.writeInt(packet.entityId());
		ResourceLocation.STREAM_CODEC.encode(registryBuffer, packet.attachment.id());
		packet.attachment.encode(registryBuffer, packet.value);
	}, (FriendlyByteBuf buffer) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		int entityId = registryBuffer.readInt();
		AttachmentType<?> attachment = AttachmentTypeCache.lookup(ResourceLocation.STREAM_CODEC.decode(registryBuffer));
		Object value = attachment.sync().decode(registryBuffer);
		return new ServerboundSyncAttachmentPacket(entityId, attachment, value);
	});

	@Override
	public Type<ServerboundSyncAttachmentPacket> type() {
		return TYPE;
	}

	public static void handleData(final ServerboundSyncAttachmentPacket packet, final IPayloadContext context) {
		if (context.player().level().getEntity(packet.entityId) instanceof AttachmentHolder holder) {
			PacketDistributor.sendToPlayersTrackingEntity((Entity) holder, new ClientboundSyncAttachmentPacket(packet.entityId, packet.attachment, packet.value));
			packet.attachment.update(holder, packet.value);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AttachmentsApiMod.addNetworkMessage(ServerboundSyncAttachmentPacket.TYPE, ServerboundSyncAttachmentPacket.STREAM_CODEC, ServerboundSyncAttachmentPacket::handleData);
	}
}