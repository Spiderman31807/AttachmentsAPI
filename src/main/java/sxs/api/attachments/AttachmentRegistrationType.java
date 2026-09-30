package sxs.api.attachments;

import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;

import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;

public class AttachmentRegistrationType<T> {
	private static final Map<Class<?>, AttachmentRegistrationType<?>> registrationTypes = new HashMap();
	static { NeoForge.EVENT_BUS.post(new RegisterAttachmentTypesEvent((clazz) -> registrationTypes.containsKey(clazz), (clazz, type) -> registrationTypes.putIfAbsent(clazz, type))); }

	public static <T> AttachmentRegistrationType<T> getRegistrationFor(Class<T> targetClass) {
		return (AttachmentRegistrationType<T>) registrationTypes.getOrDefault(targetClass, null);
	}

	public static void registerCommands(LiteralArgumentBuilder<CommandSourceStack> base, CommandBuildContext buildContext) {
		registrationTypes.values().forEach((type) -> type.registerCommand(base, buildContext));
	}

	private final Supplier<T> defaultValue;
	private final StreamCodec<? extends ByteBuf, T> sync;
	private final Codec<T> serializer;
	private final Class<T> clazz;
	private final Function<T, String> getter;
	private final Function<CommandContext, T> setter;
	private final Function<CommandBuildContext, ArgumentType<?>> argument;

	public AttachmentRegistrationType(Class<T> clazz, Codec<T> serializer, StreamCodec<? extends ByteBuf, T> sync, Supplier<T> defaultValue, Function<T, String> getter, Function<CommandContext, T> setter, Function<CommandBuildContext, ArgumentType<?>> argument) {
		this.defaultValue = defaultValue;
		this.serializer = serializer;
		this.clazz = clazz;
		this.sync = sync;
		this.getter = getter;
		this.setter = setter;
		this.argument = argument;
	}

	public AttachmentRegistrationType(Class<T> clazz, Codec<T> serializer, StreamCodec<? extends ByteBuf, T> sync, Supplier<T> defaultValue) {
		this(clazz, serializer, sync, defaultValue, null, null, null);
	}

	public boolean register(RegisterAttachmentsEvent event, ResourceLocation location, Supplier<T> defaultSupplier, boolean serializable, boolean syncable, Authority authority, boolean copyOnDeath) {
		try {
			AttachmentType.Builder<T> builder = new AttachmentType.Builder<T>(location, defaultSupplier == null ? this.defaultValue : defaultSupplier);
			if (serializable)
				builder.serializable(this.serializer);
			if (copyOnDeath)
				builder.copyOnDeath();
			if (syncable) {
				builder.sync(this.sync);
				builder.authority(authority);
			}
			return event.register(this.clazz, builder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static SuggestionProvider<CommandSourceStack> getAttachmentSuggestions(Class<?> storedClass) {
		return (context, builder) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder)
				holder.getAttachments(storedClass).forEach((entry) -> builder.suggest(entry.getId().toString()));
			return builder.buildFuture();
		};
	}

	public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> base, CommandBuildContext buildContext) {
		ArgumentBuilder<CommandSourceStack, ?> branch = Commands.argument("id", ResourceLocationArgument.id()).suggests(getAttachmentSuggestions(this.clazz));
		branch.then(Commands.literal("reset").executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				if (holder.resetAttachmentValue(location)) {
					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.reset", location.toString(), entity.getDisplayName().getString()), true);
					return 15;
				}
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.reset", location.toString(), entity.getDisplayName().getString()));
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}
			return 0;
		}));
		
		if (this.getter != null) {
			branch.then(Commands.literal("query").executes((context) -> {
				Entity entity = EntityArgument.getEntity(context, "target");
				if (entity instanceof AttachmentHolder holder) {
					ResourceLocation location = ResourceLocationArgument.getId(context, "id");
					String displayValue = holder.<T>tryGetAttachmentValue(location).map((value) -> this.getter.apply(value)).orElse(null);
					if (displayValue == null) {
						context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.get", location.toString(), entity.getDisplayName().getString()));
						return 0;
					}

					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.get", entity.getDisplayName().getString(), displayValue, location.toString()), false);
					return 15;
				} else {
					context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
				}

				return 0;
			}));
			if (this.argument != null && this.setter != null) {
				branch.then(Commands.literal("set").then(Commands.argument("value", this.argument.apply(buildContext)).executes((context) -> {
					Entity entity = EntityArgument.getEntity(context, "target");
					if (entity instanceof AttachmentHolder holder) {
						ResourceLocation location = ResourceLocationArgument.getId(context, "id");
						T value = this.setter.apply(context);
						String displayValue = this.getter.apply(value);
						if (holder.setAttachmentValue(location, value)) {
							context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.set", location.toString(), displayValue, entity.getDisplayName().getString()), true);
							return 15;
						}

						context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.set", location.toString(), displayValue, entity.getDisplayName().getString()));
					} else {
						context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
					}

					return 0;
				})));
			}
		}
		base.then(Commands.argument("target", EntityArgument.entity()).then(Commands.literal(this.clazz.getSimpleName().toLowerCase()).then(branch)));
	}
}