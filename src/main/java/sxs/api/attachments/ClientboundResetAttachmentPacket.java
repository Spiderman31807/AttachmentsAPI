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
public record ClientboundResetAttachmentPacket(int entityId, AttachmentType<?> attachment) implements CustomPacketPayload {
	public static final Type<ClientboundResetAttachmentPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AttachmentsApiMod.MODID, "clientbound_reset_attachment"));
	public static final StreamCodec<FriendlyByteBuf, ClientboundResetAttachmentPacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ClientboundResetAttachmentPacket packet) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		registryBuffer.writeInt(packet.entityId());
		ResourceLocation.STREAM_CODEC.encode(registryBuffer, packet.attachment.id());
	}, (FriendlyByteBuf buffer) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		int entityId = registryBuffer.readInt();
		AttachmentType<?> attachment = AttachmentTypeCache.lookup(ResourceLocation.STREAM_CODEC.decode(registryBuffer));
		return new ClientboundResetAttachmentPacket(entityId, attachment);
	});

	@Override
	public Type<ClientboundResetAttachmentPacket> type() {
		return TYPE;
	}

	public static void handleData(final ClientboundResetAttachmentPacket packet, final IPayloadContext context) {
		if (context.player().level().getEntity(packet.entityId) instanceof AttachmentHolder holder)
			packet.attachment.update(holder, null);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AttachmentsApiMod.addNetworkMessage(ClientboundResetAttachmentPacket.TYPE, ClientboundResetAttachmentPacket.STREAM_CODEC, ClientboundResetAttachmentPacket::handleData);
	}
}