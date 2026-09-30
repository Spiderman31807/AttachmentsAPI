package sxs.api.attachments;

import net.neoforged.bus.api.Event;

import net.minecraft.resources.ResourceLocation;

public class UpdateAttachmentEvent<T> extends Event {
	private final AttachmentType<T> attachment;
	private final AttachmentHolder holder;
	private final T oldValue;
	private final T newValue;

	public UpdateAttachmentEvent(AttachmentHolder holder, AttachmentType<T> attachment, T oldValue, T newValue) {
		this.attachment = attachment;
		this.holder = holder;
		this.oldValue = oldValue == null ? attachment.defaultSupplier().get() : oldValue;
		this.newValue = newValue == null ? attachment.defaultSupplier().get() : newValue;
	}

	public ResourceLocation getId() {
		return this.attachment.id();
	}

	public boolean is(ResourceLocation id) {
		return this.attachment.id().equals(id);
	}

	public boolean is(AttachmentType<?> atttachment) {
		return this.is(atttachment.id());
	}

	public AttachmentHolder getHolder() {
		return this.holder;
	}

	public T getOldValue() {
		return this.oldValue;
	}

	public T getNewValue() {
		return this.newValue;
	}

	public static class Pre<T> extends UpdateAttachmentEvent<T> {
		public Pre(AttachmentHolder holder, AttachmentType<T> attachment, T oldValue, T newValue) {
			super(holder, attachment, oldValue, newValue);
		}
	}

	public static class Post<T> extends UpdateAttachmentEvent<T> {
		public Post(AttachmentHolder holder, AttachmentType<T> attachment, T oldValue, T newValue) {
			super(holder, attachment, oldValue, newValue);
		}
	}
}