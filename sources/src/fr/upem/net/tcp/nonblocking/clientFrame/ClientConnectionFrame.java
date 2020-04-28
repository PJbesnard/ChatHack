package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This Class allows to get a ByteBuffer corresponding to the Connection Without Password Frame
 */
public class ClientConnectionFrame implements ClientFrame {
	private final String login;
	
	public ClientConnectionFrame(String login) {
		this.login = Objects.requireNonNull(login);
	}
	
	/**
	 * Returns a ByteBuffer corresponding to the Connection Without Password Frame
	 */
	@Override
	public ByteBuffer get(){
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES + BBlogin.remaining());
		buff.put((byte) 2).putInt(BBlogin.remaining()).put(BBlogin);
		return buff;
	}
}
