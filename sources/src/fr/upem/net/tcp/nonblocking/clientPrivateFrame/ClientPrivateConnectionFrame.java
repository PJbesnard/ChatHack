package fr.upem.net.tcp.nonblocking.clientPrivateFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * This class allows to get a ByteBuffer and informations corresponding to the Private Connection Frame
 */
public class ClientPrivateConnectionFrame implements ClientPrivateFrame {
	private final long id;
	private final String sender;
	
	public ClientPrivateConnectionFrame(String login, long id) {
		this.sender = login;
		this.id = id;
	}
	
	/**
	 * Gets the id
	 * @return a long which represents the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the login
	 * @return a String which represents a login
	 */
	public String getLogin() {
		return sender;
	}

	/**
	 * Returns a ByteBuffer corresponding to the Private Connection Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBlogin = StandardCharsets.UTF_8.encode(sender);
		ByteBuffer bb = ByteBuffer.allocate(Byte.BYTES + BBlogin.remaining() + Integer.BYTES + Long.BYTES);
		return bb.put((byte) 9).putInt(BBlogin.remaining()).put(BBlogin).putLong(id);
	}
}
