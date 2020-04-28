package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This Class allows to get a ByteBuffer and informations corresponding to the Global Message Frame
 */
public class ClientGlobalMessageFrame implements ClientFrame {
	
	private final String message;
	private final String login;
	
	public ClientGlobalMessageFrame(String login, String message) {
		this.login = Objects.requireNonNull(login);
		this.message = Objects.requireNonNull(message);
	}

	/**
	 * Gets message
	 * @return a String corresponding to the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets login
	 * @return a String corresponding to the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Returns a ByteBuffer corresponding to the Global Message Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var BBmessage = StandardCharsets.UTF_8.encode(message);
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES * 2 + BBlogin.remaining() + BBmessage.remaining());
		buff.put((byte) 3).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		return buff;
	}
}
