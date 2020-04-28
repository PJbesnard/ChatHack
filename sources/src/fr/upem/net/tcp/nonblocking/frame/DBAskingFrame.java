package fr.upem.net.tcp.nonblocking.frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import fr.upem.net.tcp.nonblocking.frameRead.ConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.ConnectionWithoutPasswordFrameRead;

/**
 * Provides method to create a ByteBuffer representation of a database request frame
 */
public class DBAskingFrame implements Frame{
	private final long id;
	private final String login;
	private String password = null;


	public DBAskingFrame(long id, ConnectionWithoutPasswordFrameRead frame) {
		this.id = id;
		this.login = frame.getLogin();
	}
	
	public DBAskingFrame(long id, ConnectionFrameRead frame) {
		this.id = id;
		this.login = frame.getLogin();
		this.password = frame.getPassword();
	}

	/**
	 * Gets a ByteBuffer representation of the frame
	 * @return ByteBuffer representation of the frame
	 */
	public ByteBuffer get() {
		ByteBuffer buff;
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		if (password == null) {
			buff = ByteBuffer.allocate(1 + Integer.BYTES + Long.BYTES + BBlogin.remaining());
			return buff.put((byte) 2).putLong(id).putInt(BBlogin.remaining()).put(BBlogin);
		}
		ByteBuffer BBpswd = StandardCharsets.UTF_8.encode(password);
		buff = ByteBuffer.allocate(1 + Long.BYTES + Integer.BYTES * 2 + BBlogin.remaining() + BBpswd.remaining());
		return buff.put((byte) 1).putLong(id).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBpswd.remaining()).put(BBpswd);
	}
}
