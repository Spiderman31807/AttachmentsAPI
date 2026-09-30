package sxs.api.attachments;

import net.minecraft.resources.ResourceLocation;
import java.util.Optional;

public class Attachments {
	public static boolean has(Object object, String namespace, String id) {
		return has(object, ResourceLocation.fromNamespaceAndPath(namespace, id));
	}

	public static <T> boolean reset(Object object, String namespace, String id) {
		return reset(object, ResourceLocation.fromNamespaceAndPath(namespace, id));
	}

	public static <T> boolean set(Object object, String namespace, String id, T value) {
		return set(object, ResourceLocation.fromNamespaceAndPath(namespace, id), value);
	}

	public static <T> Optional<T> get(Object object, String namespace, String id) {
		return get(object, ResourceLocation.fromNamespaceAndPath(namespace, id));
	}

	public static <T> T orElse(Object object, String namespace, String id, T fallback) {
		return orElse(object, ResourceLocation.fromNamespaceAndPath(namespace, id), fallback);
	}

	public static boolean has(Object object, ResourceLocation location) {
		return object instanceof AttachmentHolder holder ? holder.hasAttachmentEntry(location) : false;
	}

	public static <T> boolean reset(Object object, ResourceLocation location) {
		return object instanceof AttachmentHolder holder ? holder.resetAttachmentValue(location) : false;
	}

	public static <T> boolean set(Object object, ResourceLocation location, T value) {
		return object instanceof AttachmentHolder holder ? holder.setAttachmentValue(location, value) : false;
	}

	public static <T> Optional<T> get(Object object, ResourceLocation location) {
		return object instanceof AttachmentHolder holder ? holder.tryGetAttachmentValue(location) : Optional.empty();
	}

	public static <T> T orElse(Object object, ResourceLocation location, T fallback) {
		return object instanceof AttachmentHolder holder ? holder.getAttachmentValue(location, fallback) : fallback;
	}
}