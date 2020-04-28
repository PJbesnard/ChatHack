package fr.upem.net.tcp.nonblocking.frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides method to create a ByteBuffer representation of a client private message deny frame
 */
public class PrivateMessageDenyFrame implements Frame{
	
	private final String sender;
	private final String receiver;
	
	public PrivateMessageDenyFrame(String sender, String receiver) {
		this.sender = Objects.requireNonNull(sender);
		this.receiver = Objects.requireNonNull(receiver);
	}
	
	/**
	 * Gets the frame sender login
	 * @return The frame sender login
	 */
	public String getSenderLogin() {
		return sender;
	}
	
	/**
	 * Gets the frame receiver login
	 * @return The frame receiver login
	 */
	public String getReceiverLogin() {
		return receiver;
	}

	/**
	 * Gets a ByteBuffer representation of the frame
	 * @return ByteBuffer representation of the frame
	 */
	public ByteBuffer get() {
		var BBsender = StandardCharsets.UTF_8.encode(sender);
		var buff = ByteBuffer.allocate(1 + Integer.BYTES + BBsender.remaining());
		buff.put((byte) 8).putInt(BBsender.remaining()).put(BBsender);
		return buff;
	}

}
