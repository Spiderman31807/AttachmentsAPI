package sxs.api.attachments;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.HashMap;

public class AttachmentTypeCache {
	private static final Map<ResourceLocation, AttachmentType<?>> cached = new HashMap();

	public static AttachmentType<?> lookup(ResourceLocation location) {
		return cached.getOrDefault(location, null);
	}

	public static void cache(AttachmentType<?> attachment) {
		cached.putIfAbsent(attachment.id(), attachment);
	}
}