package sxs.api.attachments;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Collectors;
import java.util.Set;
import java.util.Optional;

public interface AttachmentHolder {
	static Logger logger = LoggerFactory.getLogger(AttachmentHolder.class);

	abstract Set<AttachmentEntry<?>> getAttachments();

	default Set<AttachmentEntry<?>> getAttachments(Class<?> storedClass) {
		return this.getAttachments().stream().filter(entry -> entry.getStoredClass() == storedClass).collect(Collectors.toSet());
	}

	default Set<AttachmentEntry<?>> getSyncableAttachments() {
		return this.getAttachments().stream().filter(entry -> entry.get().sync() != null).collect(Collectors.toSet());
	}

	default void syncWith(Set<AttachmentEntry<?>> entries) {
		for(AttachmentEntry<?> entry : entries) {
			this.setAttachmentValue(entry.getId(), entry.getValue());
		}
	}

	default void copyFrom(AttachmentHolder holder, boolean wasDeath) {
		for(AttachmentEntry<?> entry : this.getAttachments()) {
			if(!wasDeath || entry.get().copyOnDeath())
				entry.copyValue(holder.getAttachmentEntry(entry.getId()));
		}
	}

	default boolean hasAttachmentEntry(ResourceLocation location) {
		return this.getAttachmentEntry(location) != null;
	}

	default <T> AttachmentEntry<T> getAttachmentEntry(ResourceLocation location) {
		for (AttachmentEntry<?> entry : this.getAttachments()) {
			if (entry.getId().equals(location))
				return (AttachmentEntry<T>) entry;
		}
		
		return null;
	}

	default void registerAttachments() {
		NeoForge.EVENT_BUS.post(new RegisterAttachmentsEvent(this));
	}

	default <T> boolean register(Class<T> clazz, AttachmentType<T> attachment) {
		if(this.hasAttachmentEntry(attachment.id()))
			return false;
		
		if(!this.getAttachments().add(new AttachmentEntry(clazz, attachment)))
			return false;

		AttachmentTypeCache.cache(attachment);
		return true;
	}

	default <T> boolean onAttachmentUpdated(AttachmentEntry<T> entry, T newValue) {
		if (entry == null) {
			logger.warn("Ignoring invalid update attachment call");
			return false;
		}
		
		if(newValue == null && !entry.hasValue())
			return false;
			
		T oldValue = entry.getValue();
		if(newValue != null && newValue.equals(oldValue))
			return false;
		
		NeoForge.EVENT_BUS.post(new UpdateAttachmentEvent.Pre<T>(this, entry.get(), oldValue, newValue));
		if (newValue == null)
			entry.resetValue();
		else
			entry.setValue(newValue);
			
		NeoForge.EVENT_BUS.post(new UpdateAttachmentEvent.Post<T>(this, entry.get(), oldValue, newValue));
		return true;
	}

	default <T> T getAttachmentValue(ResourceLocation location, T fallback) {
		try { return this.getAttachmentValue(location); }
		catch (Exception e) { e.printStackTrace(); }
		return fallback;
	}

	default <T> Optional<T> tryGetAttachmentValue(ResourceLocation location) {
		AttachmentEntry<T> entry = this.<T>getAttachmentEntry(location);
		return entry == null ? Optional.empty() : Optional.of(entry.getValue());
	}

	default <T> T getAttachmentValue(ResourceLocation location) throws Exception {
		AttachmentEntry<T> entry = this.<T>getAttachmentEntry(location);
		if (entry == null)
			throw new Exception("Trying to access unregistered attachment -> " + location);
		return entry.getValue();
	}

	default <T> boolean setAttachmentValue(ResourceLocation location, T newValue) {
		AttachmentEntry<T> entry = this.<T>getAttachmentEntry(location);
		if (entry == null) {
			logger.warn("Ignoring invalid set attachment call");
			return false;
		}

		boolean clientSide = this.level().isClientSide;
		if (!entry.hasAuthority(clientSide)) {
			logger.warn("Attempted to set attachment from unauthorised enviroment -> {}", entry.getId().toString());
			return false;
		}

		if(!this.onAttachmentUpdated(entry, newValue))
			return false;

		if (entry.get().sync() != null) {
			if (clientSide)
				ClientPacketDistributor.sendToServer(newValue == null ? new ServerboundResetAttachmentPacket(this.getId(), entry.get()) : new ServerboundSyncAttachmentPacket(this.getId(), entry.get(), newValue));
			else
				PacketDistributor.sendToPlayersTrackingEntityAndSelf((Entity) this, newValue == null ? new ClientboundResetAttachmentPacket(this.getId(), entry.get()) : new ClientboundSyncAttachmentPacket(this.getId(), entry.get(), newValue));
		}

		;
		return true;
	}

	default boolean resetAttachmentValue(ResourceLocation location) {
		return this.setAttachmentValue(location, null);
	}

	int getId();

	Level level();
}