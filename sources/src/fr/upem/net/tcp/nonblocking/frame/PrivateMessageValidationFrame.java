package fr.upem.net.tcp.nonblocking.frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides method to create a ByteBuffer representation of a client private
 * message validation frame
 */
public class PrivateMessageValidationFrame implements Frame {

	private final String sender;
	private final String receiver;
	private final int port;
	private final byte[] address;
	private final long id;

	public PrivateMessageValidationFrame(String sender, String receiver, int port, byte[] address, long id) {
		if (port < 0) {
			throw new IllegalArgumentException();
		}
		this.sender = Objects.requireNonNull(sender);
		this.receiver = Objects.requireNonNull(receiver);
		this.port = port;
		this.address = Objects.requireNonNull(address);
		this.id = id;
	}

	/**
	 * Gets the frame sender login
	 * 
	 * @return The frame sender login
	 */
	public String getSenderLogin() {
		return sender;
	}

	/**
	 * Gets the frame receiver login
	 * 
	 * @return The frame receiver login
	 */
	public String getReceiverLogin() {
		return receiver;
	}

	/**
	 * Gets a ByteBuffer representation of the frame
	 * 
	 * @return ByteBuffer representation of the frame
	 */
	public ByteBuffer get() {
		var BBsender = StandardCharsets.UTF_8.encode(sender);
		var buff = ByteBuffer
				.allocate(1 + Integer.BYTES + BBsender.remaining() + Integer.BYTES + 1 + address.length + Long.BYTES);
		buff.put((byte) 7).putInt(BBsender.remaining()).put(BBsender).putInt(port);
		if (address.length == 4) {
			buff.put((byte) 1);
		} else {
			buff.put((byte) 2);
		}
		buff.put(address);
		buff.putLong(id);
		return buff;
	}

}
