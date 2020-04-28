package fr.upem.net.tcp.nonblocking.frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides method to create a ByteBuffer representation of a private message asking frame
 */
public class PrivateMessageAskingFrame implements Frame{
	private final String sender;
	private final String receiver;
	
	public PrivateMessageAskingFrame(String sender, String receiver) {
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
		var BBlogin = StandardCharsets.UTF_8.encode(sender);
		var buff = ByteBuffer.allocate(1 + Integer.BYTES + BBlogin.remaining());
		buff.put((byte) 6).putInt(BBlogin.remaining()).put(BBlogin);
		return buff;
	}
}
