package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * This Class allows to get a ByteBuffer corresponding to the Private Message Acceptation Frame
 */
public class ClientPrivateMessageAcceptationFrame implements ClientFrame {
	private final String sender;
	private final String receiver;
	private final int port;
	private final byte[] address;
	private final long id;

	public ClientPrivateMessageAcceptationFrame(String sender, String receiver, int port, byte[] address, long id) {
		this.sender = sender;
		this.receiver = receiver;
		this.port = port;
		this.address = address;
		this.id = id;
	}

	/**
	 * Returns a ByteBuffer corresponding to the Private Message Acceptation Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBsender = StandardCharsets.UTF_8.encode(sender);
		var BBreceiver = StandardCharsets.UTF_8.encode(receiver);
		var buff = ByteBuffer.allocate(Byte.BYTES * 2 + Integer.BYTES * 3 + BBsender.remaining() + BBreceiver.remaining() + Long.BYTES + address.length);
		buff.put((byte) 5).putInt(BBsender.remaining()).put(BBsender).putInt(BBreceiver.remaining()).
			put(BBreceiver).putInt(port).put((byte) 1).put(address).putLong(id);
		return buff;
	}
}
