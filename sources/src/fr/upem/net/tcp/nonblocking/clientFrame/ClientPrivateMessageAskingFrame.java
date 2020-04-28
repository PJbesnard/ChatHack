package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This Class allows to get a ByteBuffer and informations corresponding to the Private Message Asking Frame
 */
public class ClientPrivateMessageAskingFrame implements ClientFrame {
	
	private final String login;
	
	public ClientPrivateMessageAskingFrame(String login) {
		this.login = Objects.requireNonNull(login);
	}
	
	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Returns a ByteBuffer corresponding to the Private Message Asking Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES + BBlogin.remaining());
		buff.putShort((byte) 6).putInt(BBlogin.remaining()).put(BBlogin);
		return buff;
	}

}
