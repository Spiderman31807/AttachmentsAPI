package sxs.api.attachments;

import net.neoforged.bus.api.Event;

import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;

import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.CommandBuildContext;

public class RegisterAttachmentTypesEvent extends Event {
	private final BiConsumer<Class<?>, AttachmentRegistrationType<?>> addFunc;
	private final Function<Class<?>, Boolean> hasFunc;

	public RegisterAttachmentTypesEvent(Function<Class<?>, Boolean> hasFunc, BiConsumer<Class<?>, AttachmentRegistrationType<?>> addFunc) {
		this.addFunc = addFunc;
		this.hasFunc = hasFunc;
	}

	public boolean has(Class<?> clazz) {
		return this.hasFunc.apply(clazz);
	}

	public <T> boolean register(Class<T> clazz, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Supplier<T> defaultValue) {
		if (this.has(clazz))
			return false;
		this.addFunc.accept(clazz, new AttachmentRegistrationType<T>(clazz, codec, streamCodec, defaultValue));
		return true;
	}

	public <T> boolean registerArg(Class<T> clazz, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Supplier<T> defaultValue, Function<CommandContext, T> setter, ArgumentType<?> argument) {
		return this.registerBuildArg(clazz, codec, streamCodec, defaultValue, (value) -> value.toString(), setter, (buildContext) -> argument);
	}

	public <T> boolean registerArg(Class<T> clazz, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Supplier<T> defaultValue, Function<T, String> getter, Function<CommandContext, T> setter, ArgumentType<?> argument) {
		return this.registerBuildArg(clazz, codec, streamCodec, defaultValue, getter, setter, (buildContext) -> argument);
	}

	public <T> boolean registerBuildArg(Class<T> clazz, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Supplier<T> defaultValue, Function<CommandContext, T> setter, Function<CommandBuildContext, ArgumentType<?>> argument) {
		return this.registerBuildArg(clazz, codec, streamCodec, defaultValue, (value) -> value.toString(), setter, argument);
	}

	public <T> boolean registerBuildArg(Class<T> clazz, Codec<T> codec, StreamCodec<? extends ByteBuf, T> streamCodec, Supplier<T> defaultValue, Function<T, String> getter, Function<CommandContext, T> setter, Function<CommandBuildContext, ArgumentType<?>> argument) {
		if (this.has(clazz))
			return false;
		this.addFunc.accept(clazz, new AttachmentRegistrationType<T>(clazz, codec, streamCodec, defaultValue, getter, setter, argument));
		return true;
	}
}