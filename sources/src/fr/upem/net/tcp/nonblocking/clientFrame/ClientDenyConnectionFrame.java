package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * This Class allows to get a ByteBuffer corresponding to the Deny Connection Frame
 */
public class ClientDenyConnectionFrame implements ClientFrame {
	private final String sender;
	private final String receiver;
	
	public ClientDenyConnectionFrame(String sender, String receiver) {
		this.sender = sender;
		this.receiver = receiver;
	}
	
	/**
	 * Returns a ByteBuffer corresponding to the Deny Connection Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBsender = StandardCharsets.UTF_8.encode(sender);
		var BBreceiver = StandardCharsets.UTF_8.encode(receiver);
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES * 2 + BBsender.remaining() + BBreceiver.remaining());
		buff.put((byte) 6).putInt(BBsender.remaining()).put(BBsender).putInt(BBreceiver.remaining()).put(BBreceiver);
		return buff;
	}
}
