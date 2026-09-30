package sxs.api.attachments;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.Optional;
import io.netty.buffer.ByteBuf;

public class AttachmentEntry<C, T extends C> {
	public static final StreamCodec<RegistryFriendlyByteBuf, AttachmentEntry<?, ?>> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public void encode(RegistryFriendlyByteBuf buf, AttachmentEntry<?, ?> value) {
			ResourceLocation.STREAM_CODEC.encode(buf, value.getId());
			ByteBufCodecs.STRING_UTF8.encode(buf, value.storedClassType.getName());
			value.encode(buf);
		}

		@Override
		public AttachmentEntry<?, ?> decode(RegistryFriendlyByteBuf buf) {
			try {
				AttachmentType type = AttachmentTypeCache.lookup(ResourceLocation.STREAM_CODEC.decode(buf));
				Class<?> clazz = Class.forName(ByteBufCodecs.STRING_UTF8.decode(buf));
				AttachmentEntry<?, ?> entry = new AttachmentEntry(clazz, type);
				entry.decode(buf);
				return entry;
			} catch (ClassNotFoundException e) { e.printStackTrace(); }
			return null;
		}
	};

	private final StreamCodec<? extends ByteBuf, Optional<T>> streamCodec;
	private final ResourceLocation location;
	private final AttachmentType<T> attachment;
	private final Class<C> storedClassType;
	private Optional<T> value = Optional.empty();

	public AttachmentEntry(Class<C> storedClassType, AttachmentType<T> attachment) {
		this.storedClassType = storedClassType;
		this.streamCodec = attachment.sync() == null ? null : ByteBufCodecs.optional(attachment.sync());
		this.location = attachment.id();
		this.attachment = attachment;
	}

	public ResourceLocation getId() {
		return this.location;
	}

	public AttachmentType<T> get() {
		return this.attachment;
	}

	public boolean hasValue() {
		return this.value.isPresent();
	}

	public Class<C> getStoredClass() {
		return storedClassType;
	}

	public T getValue() {
		return this.hasValue() ? this.value.get() : this.attachment.defaultSupplier().get();
	}

	public T getValue(T fallback) {
		return this.hasValue() ? this.value.get() : fallback;
	}

	public void resetValue() {
		this.value = Optional.empty();
	}

	public void copyValue(AttachmentEntry<?, ?> entry) {
		try { this.value = ((AttachmentEntry<C, T>) entry).value; }
		catch (Exception e) { e.printStackTrace(); }
	}

	public void setValue(Optional<T> value) {
		this.value = value;
	}

	public void setValue(T value) {
		this.value = Optional.ofNullable(value);
	}

	public boolean hasAuthority(boolean clientSide) {
		Authority auth = this.attachment.syncAuthority();
		return clientSide ? auth != Authority.SERVER : auth != Authority.CLIENT;
	}

	public void decode(RegistryFriendlyByteBuf buf) {
		if (this.streamCodec != null)
			this.value = ((StreamCodec<RegistryFriendlyByteBuf, Optional<T>>) this.streamCodec).decode(buf);
	}

	public void encode(RegistryFriendlyByteBuf buf) {
		if (this.streamCodec != null)
			((StreamCodec<RegistryFriendlyByteBuf, Optional<T>>) this.streamCodec).encode(buf, this.value);
	}

	public void load(ValueInput input) {
		if (this.attachment.serializer() != null)
			this.setValue(input.read(this.location.toString(), this.attachment.serializer()));
	}

	public void save(ValueOutput output) {
		if (this.hasValue() && this.attachment.serializer() != null)
			output.store(this.location.toString(), this.attachment.serializer(), this.value.get());
	}
}