package sxs.api.attachments;

import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;
import java.util.UUID;
import java.util.Map;

import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;

public class RegisterAttachmentsEvent extends Event {
	private final AttachmentHolder holder;

	public RegisterAttachmentsEvent(AttachmentHolder holder) {
		this.holder = holder;
	}

	public Entity getEntity() {
		return (Entity) this.holder;
	}

	public EntityType getType() {
		return this.holder instanceof Entity entity ? entity.getType() : null;
	}

	public boolean is(EntityType type) {
		return type.equals(this.getType());
	}

	// Direct Registration
	public <T> boolean register(Class<T> clazz, AttachmentType<T> attachment) {
		return holder.register(clazz, attachment);
	}

	public <T> boolean register(Class<T> clazz, AttachmentType.Builder<T> builder) {
		return this.register(clazz, builder.build());
	}

	// Raw Registration
	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Authority authority, boolean copyOnDeath) {
		try {
			AttachmentType.Builder<T> builder = new AttachmentType.Builder<T>(location, defaultSupplier);
			if (codec != null) {
				builder.serializable(codec);
			}
			if (copyOnDeath)
				builder.copyOnDeath();
			if (streamCodec != null) {
				builder.sync(streamCodec);
				builder.authority(authority);
			}

			return this.register(targetClass, builder);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, boolean copyOnDeath) {
		return this.register(targetClass, location, defaultSupplier, codec, streamCodec, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Authority authority) {
		return this.register(targetClass, location, defaultSupplier, codec, streamCodec, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec) {
		return this.register(targetClass, location, defaultSupplier, codec, streamCodec, Authority.SHARED, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, boolean copyOnDeath) {
		return this.register(targetClass, location, defaultSupplier, (Codec<T>) null, (StreamCodec<? extends ByteBuf, T>) null, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Authority authority) {
		return this.register(targetClass, location, defaultSupplier, (Codec<T>) null, (StreamCodec<? extends ByteBuf, T>) null, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier) {
		return this.register(targetClass, location, defaultSupplier, (Codec<T>) null, (StreamCodec<? extends ByteBuf, T>) null, Authority.SHARED, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Codec<T> codec, boolean copyOnDeath) {
		return this.register(targetClass, location, defaultSupplier, codec, (StreamCodec<? extends ByteBuf, T>) null, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Codec<T> codec, Authority authority) {
		return this.register(targetClass, location, defaultSupplier, codec, (StreamCodec<? extends ByteBuf, T>) null, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, Codec<T> codec) {
		return this.register(targetClass, location, defaultSupplier, codec, (StreamCodec<? extends ByteBuf, T>) null, Authority.SHARED, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, StreamCodec<? extends ByteBuf, T> streamCodec, boolean copyOnDeath) {
		return this.register(targetClass, location, defaultSupplier, (Codec<T>) null, streamCodec, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, StreamCodec<? extends ByteBuf, T> streamCodec, Authority authority) {
		return this.register(targetClass, location, defaultSupplier, (Codec<T>) null, streamCodec, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, StreamCodec<? extends ByteBuf, T> streamCodec) {
		return this.register(targetClass, location, defaultSupplier, (Codec<T>) null, streamCodec, Authority.SHARED, true);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Authority authority, boolean copyOnDeath) {
		return this.register(targetClass, ResourceLocation.fromNamespaceAndPath(namespace, id), defaultSupplier, codec, streamCodec, authority, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, boolean copyOnDeath) {
		return this.register(targetClass, namespace, id, defaultSupplier, codec, streamCodec, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Authority authority) {
		return this.register(targetClass, namespace, id, defaultSupplier, codec, streamCodec, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec) {
		return this.register(targetClass, namespace, id, defaultSupplier, codec, streamCodec, Authority.SHARED, true);
	}

	// Supported Registration Types
	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, boolean serializable, boolean syncable, Authority authority, boolean copyOnDeath) {
		AttachmentRegistrationType<T> type = AttachmentRegistrationType.getRegistrationFor(targetClass);
		return type == null ? false : type.register(this, location, defaultSupplier, serializable, syncable, authority, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, boolean serializable, boolean syncable, boolean copyOnDeath) {
		return this.register(targetClass, location, defaultSupplier, serializable, syncable, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, boolean serializable, boolean syncable, Authority authority) {
		return this.register(targetClass, location, defaultSupplier, serializable, syncable, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, Supplier<T> defaultSupplier, boolean serializable, boolean syncable) {
		return this.register(targetClass, location, defaultSupplier, serializable, syncable, Authority.SHARED, true);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, boolean serializable, boolean syncable, Authority authority, boolean copyOnDeath) {
		return this.register(targetClass, ResourceLocation.fromNamespaceAndPath(namespace, id), defaultSupplier, serializable, syncable, authority, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, boolean serializable, boolean syncable, boolean copyOnDeath) {
		return this.register(targetClass, namespace, id, defaultSupplier, serializable, syncable, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, boolean serializable, boolean syncable, Authority authority) {
		return this.register(targetClass, namespace, id, defaultSupplier, serializable, syncable, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, Supplier<T> defaultSupplier, boolean serializable, boolean syncable) {
		return this.register(targetClass, namespace, id, defaultSupplier, serializable, syncable, Authority.SHARED, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, boolean serializable, boolean syncable, Authority authority, boolean copyOnDeath) {
		return this.register(targetClass, location, null, serializable, syncable, authority, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, boolean serializable, boolean syncable, boolean copyOnDeath) {
		return this.register(targetClass, location, serializable, syncable, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, boolean serializable, boolean syncable, Authority authority) {
		return this.register(targetClass, location, serializable, syncable, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, ResourceLocation location, boolean serializable, boolean syncable) {
		return this.register(targetClass, location, serializable, syncable, Authority.SHARED, true);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, boolean serializable, boolean syncable, Authority authority, boolean copyOnDeath) {
		return this.register(targetClass, ResourceLocation.fromNamespaceAndPath(namespace, id), serializable, syncable, authority, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, boolean serializable, boolean syncable, boolean copyOnDeath) {
		return this.register(targetClass, namespace, id, serializable, syncable, Authority.SHARED, copyOnDeath);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, boolean serializable, boolean syncable, Authority authority) {
		return this.register(targetClass, namespace, id, serializable, syncable, authority, true);
	}

	public <T> boolean register(Class<T> targetClass, String namespace, String id, boolean serializable, boolean syncable) {
		return this.register(targetClass, namespace, id, serializable, syncable, Authority.SHARED, true);
	}
}
