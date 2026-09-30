package sxs.api.attachments;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;

@EventBusSubscriber(modid = AttachmentsApiMod.MODID)
public record ClientboundSyncAttachmentPacket(int entityId, AttachmentType<?> attachment, Object value) implements CustomPacketPayload {
	public static final Type<ClientboundSyncAttachmentPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AttachmentsApiMod.MODID, "clientbound_sync_attachment"));
	public static final StreamCodec<FriendlyByteBuf, ClientboundSyncAttachmentPacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ClientboundSyncAttachmentPacket packet) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		registryBuffer.writeInt(packet.entityId());
		ResourceLocation.STREAM_CODEC.encode(registryBuffer, packet.attachment.id());
		packet.attachment.encode(registryBuffer, packet.value);
	}, (FriendlyByteBuf buffer) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		int entityId = registryBuffer.readInt();
		AttachmentType<?> attachment = AttachmentTypeCache.lookup(ResourceLocation.STREAM_CODEC.decode(registryBuffer));
		Object value = attachment.sync().decode(registryBuffer);
		return new ClientboundSyncAttachmentPacket(entityId, attachment, value);
	});

	@Override
	public Type<ClientboundSyncAttachmentPacket> type() {
		return TYPE;
	}

	public static void handleData(final ClientboundSyncAttachmentPacket packet, final IPayloadContext context) {
		if (context.player().level().getEntity(packet.entityId) instanceof AttachmentHolder holder)
			packet.attachment.update(holder, packet.value);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AttachmentsApiMod.addNetworkMessage(ClientboundSyncAttachmentPacket.TYPE, ClientboundSyncAttachmentPacket.STREAM_CODEC, ClientboundSyncAttachmentPacket::handleData);
	}
}