package sxs.api.attachments;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class AttachmentEntry<T> {
	private final ResourceLocation location;
	private final AttachmentType<T> attachment;
	private final Class<T> storedClassType;
	private Optional<T> value = Optional.empty();

	public AttachmentEntry(AttachmentType<T> attachment) {
		this.storedClassType = (Class<T>) attachment.defaultSupplier().get().getClass();
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

	public Class<T> getStoredClass() {
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

	public void load(ValueInput input) {
		if (this.attachment.serializer() != null)
			this.setValue(input.read(this.location.toString(), this.attachment.serializer()));
	}

	public void save(ValueOutput output) {
		if (this.hasValue() && this.attachment.serializer() != null)
			output.store(this.location.toString(), this.attachment.serializer(), this.value.get());
	}
}