package sxs.api.attachments;

import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.resources.ResourceLocation;

public class ChangeAttachmentEvent<T> extends Event implements ICancellableEvent {
	private final AttachmentType<T> attachment;
	private final AttachmentHolder holder;
	private final T originalValue;
	private T currentValue;

	public ChangeAttachmentEvent(AttachmentHolder holder, AttachmentType<T> attachment, T value) {
		this.attachment = attachment;
		this.holder = holder;
		this.originalValue = value;
		this.currentValue = value;
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

	public T getOriginalValue() {
		return this.originalValue;
	}

	public T getCurrentValue() {
		return this.currentValue;
	}

	public void setValue(T value) {
		this.currentValue = value;
	}
}