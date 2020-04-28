package fr.upem.net.tcp.nonblocking.clientPrivateFrameRead;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a Private Connection Frame that have been read by the client
 */
public class ClientPrivateConnectionFrameRead implements FrameRead {
	private final long id;
	private final String sender;
	
	public ClientPrivateConnectionFrameRead(String login, long id) {
		this.sender = login;
		this.id = id;
	}
	
	/**
	 * Gets the id
	 * @return a String which represents the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return sender;
	}
	
	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
	}
}
