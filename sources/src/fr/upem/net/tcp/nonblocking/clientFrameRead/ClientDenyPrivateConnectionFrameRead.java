package fr.upem.net.tcp.nonblocking.clientFrameRead;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a Deny Private Connection Frame that have been read by the client
 */
public class ClientDenyPrivateConnectionFrameRead implements FrameRead {
	
	private final String login;

	public ClientDenyPrivateConnectionFrameRead(String login) {
		this.login = login;
	}
	
	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
	}

}
