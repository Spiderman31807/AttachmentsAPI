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

import java.util.Set;
import java.util.HashSet;

@EventBusSubscriber(modid = AttachmentsApiMod.MODID)
public record ClientboundAttachmentsPacket(int entityId, Set<AttachmentEntry<?>> entries) implements CustomPacketPayload {
	public static final Type<ClientboundAttachmentsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AttachmentsApiMod.MODID, "clientbound_attachments"));
	public static final StreamCodec<FriendlyByteBuf, ClientboundAttachmentsPacket> STREAM_CODEC = StreamCodec.of((FriendlyByteBuf buffer, ClientboundAttachmentsPacket packet) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		registryBuffer.writeInt(packet.entityId());
		registryBuffer.writeInt(packet.entries().size());
		for (AttachmentEntry<?> entry : packet.entries()) {
			AttachmentEntry.STREAM_CODEC.encode(registryBuffer, entry);
		}

	}, (FriendlyByteBuf buffer) -> {
		RegistryFriendlyByteBuf registryBuffer = RegistryFriendlyByteBuf.decorator(ServerLifecycleHooks.getCurrentServer().registryAccess()).apply(buffer);
		int entityId = registryBuffer.readInt();

		int entriesSize = registryBuffer.readInt();
		Set<AttachmentEntry<?>> entries = new HashSet();
		for (int index = 0; index < entriesSize; index++) {
			entries.add(AttachmentEntry.STREAM_CODEC.decode(registryBuffer));
		}

		return new ClientboundAttachmentsPacket(entityId, entries);
	});

	@Override
	public Type<ClientboundAttachmentsPacket> type() {
		return TYPE;
	}

	public static void handleData(final ClientboundAttachmentsPacket packet, final IPayloadContext context) {
		if (context.player().level().getEntity(packet.entityId) instanceof AttachmentHolder holder)
			holder.syncWith(packet.entries);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AttachmentsApiMod.addNetworkMessage(ClientboundAttachmentsPacket.TYPE, ClientboundAttachmentsPacket.STREAM_CODEC, ClientboundAttachmentsPacket::handleData);
	}
}