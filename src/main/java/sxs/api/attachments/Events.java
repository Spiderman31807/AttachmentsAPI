package sxs.api.attachments;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.arguments.*;

@EventBusSubscriber
public class Events {
	private static SuggestionProvider<CommandSourceStack> getAttachmentSuggestions(Class<?> storedClass) {
		return (context, builder) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder)
				holder.getAttachments(storedClass).forEach((entry) -> builder.suggest(entry.getId().toString()));
			return builder.buildFuture();
		};
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("attachments").requires(Commands.hasPermission(2)).then(Commands.argument("target", EntityArgument.entity()).then(Commands.literal("boolean").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(getAttachmentSuggestions(Boolean.class)).then(Commands.literal("query").executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				boolean value = holder.getAttachmentValue(location, false);
				context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.get", entity.getDisplayName().getString(), (value ? "true" : "false"), location.toString()), false);
				return 15;
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})).then(Commands.literal("reset").executes((context) -> {
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
		})).then(Commands.argument("value", BoolArgumentType.bool()).executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				boolean value = BoolArgumentType.getBool(context, "value");
				if (holder.setAttachmentValue(location, value)) {
					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.set", location.toString(), (value ? "true" : "false"), entity.getDisplayName().getString()), true);
					return 15;
				}

				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.set", location.toString(), (value ? "true" : "false"), entity.getDisplayName().getString()));
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})))).then(Commands.literal("long").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(getAttachmentSuggestions(Long.class)).then(Commands.literal("query").executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				long value = holder.getAttachmentValue(location, 0l);
				context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.get", entity.getDisplayName().getString(), value + "l", location.toString()), false);
				return 15;
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})).then(Commands.literal("reset").executes((context) -> {
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
		})).then(Commands.argument("value", LongArgumentType.longArg()).executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				long value = LongArgumentType.getLong(context, "value");
				if (holder.setAttachmentValue(location, value)) {
					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.set", location.toString(), value + "l", entity.getDisplayName().getString()), true);
					return 15;
				}

				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.set", location.toString(), value + "l", entity.getDisplayName().getString()));
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})))).then(Commands.literal("integer").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(getAttachmentSuggestions(Integer.class)).then(Commands.literal("query").executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				int value = holder.getAttachmentValue(location, 0);
				context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.get", entity.getDisplayName().getString(), value, location.toString()), false);
				return 15;
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})).then(Commands.literal("reset").executes((context) -> {
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
		})).then(Commands.argument("value", IntegerArgumentType.integer()).executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				int value = IntegerArgumentType.getInteger(context, "value");
				if (holder.setAttachmentValue(location, value)) {
					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.set", location.toString(), value, entity.getDisplayName().getString()), true);
					return 15;
				}

				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.set", location.toString(), value, entity.getDisplayName().getString()));
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})))).then(Commands.literal("float").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(getAttachmentSuggestions(Float.class)).then(Commands.literal("query").executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				float value = holder.getAttachmentValue(location, 0f);
				context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.get", entity.getDisplayName().getString(), value + "f", location.toString()), false);
				return 15;
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})).then(Commands.literal("reset").executes((context) -> {
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
		})).then(Commands.argument("value", FloatArgumentType.floatArg()).executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				float value = FloatArgumentType.getFloat(context, "value");
				if (holder.setAttachmentValue(location, value)) {
					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.set", location.toString(), value + "f", entity.getDisplayName().getString()), true);
					return 15;
				}

				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.set", location.toString(), value + "f", entity.getDisplayName().getString()));
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})))).then(Commands.literal("double").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(getAttachmentSuggestions(Double.class)).then(Commands.literal("query").executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				double value = holder.getAttachmentValue(location, 0d);
				context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.get", entity.getDisplayName().getString(), value + "d", location.toString()), false);
				return 15;
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})).then(Commands.literal("reset").executes((context) -> {
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
		})).then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				double value = DoubleArgumentType.getDouble(context, "value");
				if (holder.setAttachmentValue(location, value)) {
					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.set", location.toString(), value + "d", entity.getDisplayName().getString()), true);
					return 15;
				}

				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.set", location.toString(), value + "d", entity.getDisplayName().getString()));
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})))).then(Commands.literal("string").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(getAttachmentSuggestions(String.class)).then(Commands.literal("query").executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				String value = holder.getAttachmentValue(location, "");
				context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.get", entity.getDisplayName().getString(), "\"" + value + "\"", location.toString()), false);
				return 15;
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		})).then(Commands.literal("reset").executes((context) -> {
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
		})).then(Commands.argument("value", StringArgumentType.greedyString()).executes((context) -> {
			Entity entity = EntityArgument.getEntity(context, "target");
			if (entity instanceof AttachmentHolder holder) {
				ResourceLocation location = ResourceLocationArgument.getId(context, "id");
				String value = StringArgumentType.getString(context, "value");
				if (holder.setAttachmentValue(location, value)) {
					context.getSource().sendSuccess(() -> Component.translatable("api.attachments.command.success.set", location.toString(), "\"" + value + "\"", entity.getDisplayName().getString()), true);
					return 15;
				}

				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.set", location.toString(), "\"" + value + "\"", entity.getDisplayName().getString()));
			} else {
				context.getSource().sendFailure(Component.translatable("api.attachments.command.fail.invalid", entity.getDisplayName().getString()));
			}

			return 0;
		}))))));
	}
}