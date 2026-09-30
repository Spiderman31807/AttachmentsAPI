package sxs.api.attachments.mixin;

import sxs.api.attachments.AttachmentHolder;
import sxs.api.attachments.AttachmentEntry;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.HashSet;

@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentHolder {
	@Unique
	private Set<AttachmentEntry<?>> attachments;

	@Override
	public Set<AttachmentEntry<?>> getAttachments() {
		if (this.attachments == null)
			this.attachments = new HashSet();
		return this.attachments;
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V"))
	private void init(CallbackInfo callback) {
		this.registerAttachments();
	}

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V"))
	private void saveWithoutId(ValueOutput output, CallbackInfo callback) {
		ValueOutput attachmentsOutput = output.child("attachments");
		this.getAttachments().forEach((entry) -> entry.save(attachmentsOutput));
		if (attachmentsOutput.isEmpty())
			output.discard("attachments");
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V"))
	private void load(ValueInput input, CallbackInfo callback) {
		input.child("attachments").ifPresent((attachmentsInput) -> {
			for (String id : attachmentsInput.keySet()) {
				AttachmentEntry<?> entry = this.getAttachmentEntry(ResourceLocation.parse(id));
				if(entry != null)
					entry.load(attachmentsInput);
			}
		});
	}
}