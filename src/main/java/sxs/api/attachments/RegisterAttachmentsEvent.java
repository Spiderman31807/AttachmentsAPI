package sxs.api.attachments;

import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.stream.LongStream;
import java.util.stream.IntStream;
import java.util.function.Supplier;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;

public class RegisterAttachmentsEvent extends Event {
	private final AttachmentHolder holder;

	public RegisterAttachmentsEvent(AttachmentHolder holder) {
		this.holder = holder;
	}

	public EntityType getType() {
		return this.holder instanceof Entity entity ? entity.getType() : null;
	}

	public boolean is(EntityType type) {
		return type.equals(this.getType());
	}

	public ResourceLocation register(AttachmentType<?> attachment) {
		return holder.register(attachment) ? attachment.id() : null;
	}

	public ResourceLocation register(AttachmentType.Builder<?> builder) {
		return this.register(builder.build());
	}

	public <T> ResourceLocation register(String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec, Authority authority, boolean copyOnDeath) {
		try {
			AttachmentType.Builder<T> builder = new AttachmentType.Builder<T>(namespace, id, defaultSupplier);
			if (codec != null) {
				builder.serializable(codec);
				if (copyOnDeath)
					builder.copyOnDeath();
			}

			if (streamCodec != null) {
				builder.sync(streamCodec);
				builder.authority(authority);
			}

			return this.register(builder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> ResourceLocation register(String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, codec, streamCodec, Authority.SHARED, copyOnDeath);
	}

	public <T> ResourceLocation register(String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec, Authority authority) {
		return this.register(namespace, id, defaultSupplier, codec, streamCodec, authority, true);
	}

	public <T> ResourceLocation register(String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
		return this.register(namespace, id, defaultSupplier, codec, streamCodec, Authority.SHARED, true);
	}

	// BOOL
	public ResourceLocation registerBoolean(String namespace, String id, Supplier<Boolean> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.BOOL : null, sync ? ByteBufCodecs.BOOL : null, authority, copyOnDeath);
	}

	public ResourceLocation registerBoolean(String namespace, String id, Supplier<Boolean> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerBoolean(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerBoolean(String namespace, String id, Supplier<Boolean> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerBoolean(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerBoolean(String namespace, String id, Supplier<Boolean> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerBoolean(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}

	// BYTE
	public ResourceLocation registerByte(String namespace, String id, Supplier<Byte> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.BYTE : null, sync ? ByteBufCodecs.BYTE : null, authority, copyOnDeath);
	}

	public ResourceLocation registerByte(String namespace, String id, Supplier<Byte> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerByte(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerByte(String namespace, String id, Supplier<Byte> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerByte(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerByte(String namespace, String id, Supplier<Byte> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerByte(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}

	// SHORT
	public ResourceLocation registerShort(String namespace, String id, Supplier<Short> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.SHORT : null, sync ? ByteBufCodecs.SHORT : null, authority, copyOnDeath);
	}

	public ResourceLocation registerShort(String namespace, String id, Supplier<Short> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerShort(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerShort(String namespace, String id, Supplier<Short> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerShort(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerShort(String namespace, String id, Supplier<Short> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerShort(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}

	// LONG
	public ResourceLocation registerLong(String namespace, String id, Supplier<Long> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.LONG : null, sync ? ByteBufCodecs.LONG : null, authority, copyOnDeath);
	}

	public ResourceLocation registerLong(String namespace, String id, Supplier<Long> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerLong(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerLong(String namespace, String id, Supplier<Long> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerLong(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerLong(String namespace, String id, Supplier<Long> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerLong(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}

	// INTEGER
	public ResourceLocation registerInt(String namespace, String id, Supplier<Integer> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.INT : null, sync ? ByteBufCodecs.INT : null, authority, copyOnDeath);
	}

	public ResourceLocation registerInt(String namespace, String id, Supplier<Integer> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerInt(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerInt(String namespace, String id, Supplier<Integer> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerInt(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerInt(String namespace, String id, Supplier<Integer> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerInt(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}

	// FLOAT
	public ResourceLocation registerFloat(String namespace, String id, Supplier<Float> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.FLOAT : null, sync ? ByteBufCodecs.FLOAT : null, authority, copyOnDeath);
	}

	public ResourceLocation registerFloat(String namespace, String id, Supplier<Float> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerFloat(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerFloat(String namespace, String id, Supplier<Float> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerFloat(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerFloat(String namespace, String id, Supplier<Float> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerFloat(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}

	// DOUBLE
	public ResourceLocation registerDouble(String namespace, String id, Supplier<Double> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.DOUBLE : null, sync ? ByteBufCodecs.DOUBLE : null, authority, copyOnDeath);
	}

	public ResourceLocation registerDouble(String namespace, String id, Supplier<Double> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerDouble(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerDouble(String namespace, String id, Supplier<Double> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerDouble(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerDouble(String namespace, String id, Supplier<Double> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerDouble(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}

	// STRING
	public ResourceLocation registerString(String namespace, String id, Supplier<String> defaultSupplier, boolean serializable, boolean sync, Authority authority, boolean copyOnDeath) {
		return this.register(namespace, id, defaultSupplier, serializable ? Codec.STRING : null, sync ? ByteBufCodecs.STRING_UTF8 : null, authority, copyOnDeath);
	}

	public ResourceLocation registerString(String namespace, String id, Supplier<String> defaultSupplier, boolean serializable, boolean sync, boolean copyOnDeath) {
		return this.registerString(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, copyOnDeath);
	}

	public ResourceLocation registerString(String namespace, String id, Supplier<String> defaultSupplier, boolean serializable, boolean sync, Authority authority) {
		return this.registerString(namespace, id, defaultSupplier, serializable, sync, authority, true);
	}

	public ResourceLocation registerString(String namespace, String id, Supplier<String> defaultSupplier, boolean serializable, boolean sync) {
		return this.registerString(namespace, id, defaultSupplier, serializable, sync, Authority.SHARED, true);
	}
}