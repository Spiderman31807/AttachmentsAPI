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
public record ServerboundResetAttachmentPacket(int entityId, AttachmentType<?> attachment) implements CustomPacketPayload {
	public static final Type<ServerboundResetAttachmentPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AttachmentsApiMod.MODID, "serverbound_reset_attachment"));
	public static final StreamCodec<FriendlyByteBuf, ServerboundResetAttachmentPacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ServerboundResetAttachmentPacket packet) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		registryBuffer.writeInt(packet.entityId());
		ResourceLocation.STREAM_CODEC.encode(registryBuffer, packet.attachment.id());
	}, (FriendlyByteBuf buffer) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		int entityId = registryBuffer.readInt();
		AttachmentType<?> attachment = AttachmentTypeCache.lookup(ResourceLocation.STREAM_CODEC.decode(registryBuffer));
		return new ServerboundResetAttachmentPacket(entityId, attachment);
	});

	@Override
	public Type<ServerboundResetAttachmentPacket> type() {
		return TYPE;
	}

	public static void handleData(final ServerboundResetAttachmentPacket packet, final IPayloadContext context) {
		if (context.player().level().getEntity(packet.entityId) instanceof AttachmentHolder holder) {
			PacketDistributor.sendToPlayersTrackingEntity((Entity) holder, new ClientboundResetAttachmentPacket(packet.entityId, packet.attachment));
			packet.attachment.update(holder, null);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AttachmentsApiMod.addNetworkMessage(ServerboundResetAttachmentPacket.TYPE, ServerboundResetAttachmentPacket.STREAM_CODEC, ServerboundResetAttachmentPacket::handleData);
	}
}