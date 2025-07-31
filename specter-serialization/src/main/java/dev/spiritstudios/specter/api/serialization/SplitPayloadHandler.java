package dev.spiritstudios.specter.api.serialization;

import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class SplitPayloadHandler<T> {
	private final PacketCodec<PacketByteBuf, PayloadPart> partCodec = PacketCodecs.BYTE_ARRAY.xmap(
			PayloadPart::new,
			PayloadPart::bytes
	).cast();
	private final Event<ReceiveCallback<T>> receiveCallback = EventFactory.createArrayBacked(
			ReceiveCallback.class,
			consumers -> (t, registryManager) -> {
				for (ReceiveCallback<T> consumer : consumers) consumer.receive(t, registryManager);
			}
	);
	private final CustomPayload.Id<PayloadPart> payloadId;
	private final PacketCodec<? super RegistryByteBuf, T> codec;
	private @Nullable RegistryByteBuf receivedData;

	public SplitPayloadHandler(Identifier id, PacketCodec<? super RegistryByteBuf, T> codec) {
		this.payloadId = new CustomPayload.Id<>(id);
		this.codec = codec;
	}

	public void send(T payload, Consumer<CustomPayload> sender, DynamicRegistryManager manager) {
		RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), manager);
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

	public void receive(PayloadPart part, DynamicRegistryManager registryManager) {
		if (receivedData == null) receivedData = new RegistryByteBuf(Unpooled.buffer(), registryManager);

		if (part.bytes.length == 0) {
			T payload = codec.decode(receivedData);
			receivedData = null;
			receiveCallback.invoker().receive(payload, registryManager);
			return;
		}

		receivedData.writeBytes(part.bytes);
	}

	public CustomPayload.Id<PayloadPart> payloadId() {
		return payloadId;
	}

	public Event<ReceiveCallback<T>> receiveCallback() {
		return receiveCallback;
	}

	public void register(PayloadTypeRegistry<? extends ByteBuf> registry) {
		registry.register(payloadId, partCodec);
	}

	public interface ReceiveCallback<T> {
		void receive(T payload, DynamicRegistryManager registryManager);
	}

	public class PayloadPart implements CustomPayload {
		private final byte[] bytes;

		private PayloadPart(byte[] bytes) {
			this.bytes = bytes;
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return SplitPayloadHandler.this.payloadId;
		}

		public byte[] bytes() {
			return bytes;
		}
	}
}
