package sxs.api.attachments;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;

public record AttachmentType<T>(ResourceLocation id, Supplier<T> defaultSupplier, Codec<T> serializer, StreamCodec<? extends ByteBuf, T> sync, Authority syncAuthority, boolean copyOnDeath) {
	public void update(AttachmentHolder holder, Object value) {
		holder.<T, T>onAttachmentUpdated(holder.<T, T>getAttachmentEntry(this.id), (T) value);
	}

	public T decode(RegistryFriendlyByteBuf buf) {
		return ((StreamCodec<RegistryFriendlyByteBuf, T>) this.sync).decode(buf);
	}

	public void encode(RegistryFriendlyByteBuf buf, Object value) {
		((StreamCodec<RegistryFriendlyByteBuf, T>) this.sync).encode(buf, (T) value);
	}

	public static class Builder<T> {
		private final ResourceLocation id;
		private final Supplier<T> defaultSupplier;
		private Authority syncAuthority = Authority.SHARED;
		private Codec<T> serializer;
		private StreamCodec<? extends ByteBuf, T> sync;
		private boolean copyOnDeath;

		public Builder(ResourceLocation id, Supplier<T> defaultSupplier) {
			this.defaultSupplier = defaultSupplier;
			this.id = id;
		}

		public Builder(String namespace, String id, Supplier<T> defaultSupplier) {
			this.defaultSupplier = defaultSupplier;
			this.id = ResourceLocation.fromNamespaceAndPath(namespace, id);
		}

		public Builder(String id, Supplier<T> defaultSupplier) {
			this.defaultSupplier = defaultSupplier;
			this.id = ResourceLocation.parse(id);
		}

		public Builder serializable(Codec<T> serializer) throws Exception {
			if (this.serializer != null)
				throw new IllegalStateException("Serializer already set");
			this.serializer = serializer;
			return this;
		}

		public Builder copyOnDeath() throws Exception {
			this.copyOnDeath = true;
			return this;
		}

		public Builder sync(StreamCodec<? extends ByteBuf, T> sync) {
			this.sync = sync;
			return this;
		}

		public Builder authority(Authority authority) {
			this.syncAuthority = authority;
			return this;
		}

		public Builder clientAuthority() {
			return this.authority(Authority.CLIENT);
		}

		public Builder serverAuthority() {
			return this.authority(Authority.SERVER);
		}

		public AttachmentType<T> build() {
			return new AttachmentType<T>(this.id, this.defaultSupplier, this.serializer, this.sync, this.syncAuthority, this.copyOnDeath);
		}
	}
}