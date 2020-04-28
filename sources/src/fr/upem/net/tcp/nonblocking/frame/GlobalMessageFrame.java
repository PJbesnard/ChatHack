package fr.upem.net.tcp.nonblocking.frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GlobalMessageFrame implements Frame {
	
	private final String message;
	private String login;
	
	public GlobalMessageFrame(String login, String message) {
		this.message = Objects.requireNonNull(message);
		this.login = Objects.requireNonNull(login);
	}

	/**
	 * Gets the frame message
	 * @return The frame message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the frame login
	 * @return The frame login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Gets a ByteBuffer representation of the frame
	 * @return ByteBuffer representation of the frame
	 */
	public ByteBuffer get() {
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var BBmessage = StandardCharsets.UTF_8.encode(message);
		var buff = ByteBuffer.allocate(1 + Integer.BYTES * 2 + BBlogin.remaining() + BBmessage.remaining());
		buff.put((byte) 5);
		buff.putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		return buff;
	}
}
