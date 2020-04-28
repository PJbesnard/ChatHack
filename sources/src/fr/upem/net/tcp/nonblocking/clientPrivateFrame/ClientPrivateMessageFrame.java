package fr.upem.net.tcp.nonblocking.clientPrivateFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This class allows to get a ByteBuffer and informations corresponding to the Private Message Frame
 */
public class ClientPrivateMessageFrame implements ClientPrivateFrame{
	
	private final String message;
	private final String login;
	
	public ClientPrivateMessageFrame(String login, String message) {
		this.login = Objects.requireNonNull(login);
		this.message = Objects.requireNonNull(message);
	}

	/**
	 * Gets the message
	 * @return a String which represents the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Returns a ByteBuffer corresponding to the Private Message Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var BBmessage = StandardCharsets.UTF_8.encode(message);
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES * 2 + BBlogin.remaining() + BBmessage.remaining());
		buff.put((byte) 7).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		return buff;
	}
}
