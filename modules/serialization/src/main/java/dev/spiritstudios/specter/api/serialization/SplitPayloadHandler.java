package dev.spiritstudios.specter.api.serialization;

import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class SplitPayloadHandler<T> {
	private final StreamCodec<FriendlyByteBuf, PayloadPart> partCodec = ByteBufCodecs.BYTE_ARRAY.map(
			PayloadPart::new,
			PayloadPart::bytes
	).cast();
	private final Event<ReceiveCallback<T>> receiveCallback = EventFactory.createArrayBacked(
			ReceiveCallback.class,
			consumers -> (t, registryManager) -> {
				for (ReceiveCallback<T> consumer : consumers) consumer.receive(t, registryManager);
			}
	);
	private final CustomPacketPayload.Type<PayloadPart> payloadId;
	private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;
	private @Nullable RegistryFriendlyByteBuf receivedData;

	public SplitPayloadHandler(ResourceLocation id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		this.payloadId = new CustomPacketPayload.Type<>(id);
		this.codec = codec;
	}

	public void send(T payload, Consumer<CustomPacketPayload> sender, RegistryAccess manager) {
		RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), manager);
		codec.encode(buf, payload);

		int readableBytes = buf.readableBytes();
		int index = 0;

		while (index < readableBytes) {
			int partSize = Math.min(readableBytes - index, 1048576);
			sender.accept(new PayloadPart(PacketByteBufs.slice(buf, index, partSize).array()));
			index += partSize;
		}

		sender.accept(new PayloadPart(new byte[0]));
	}

	public void receive(PayloadPart part, RegistryAccess registryManager) {
		if (receivedData == null) receivedData = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryManager);

		if (part.bytes.length == 0) {
			T payload = codec.decode(receivedData);
			receivedData = null;
			receiveCallback.invoker().receive(payload, registryManager);
			return;
		}

		receivedData.writeBytes(part.bytes);
	}

	public CustomPacketPayload.Type<PayloadPart> payloadId() {
		return payloadId;
	}

	public Event<ReceiveCallback<T>> receiveCallback() {
		return receiveCallback;
	}

	public void register(PayloadTypeRegistry<? extends ByteBuf> registry) {
		registry.register(payloadId, partCodec);
	}

	public interface ReceiveCallback<T> {
		void receive(T payload, RegistryAccess registryManager);
	}

	public class PayloadPart implements CustomPacketPayload {
		private final byte[] bytes;

		private PayloadPart(byte[] bytes) {
			this.bytes = bytes;
		}

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return SplitPayloadHandler.this.payloadId;
		}

		public byte[] bytes() {
			return bytes;
		}
	}
}
