package fr.upem.net.tcp.nonblocking.clientFrameRead;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a Private Message Asking Frame that have been read by the client
 */
public class ClientPrivateMessageAskingFrameRead implements FrameRead{
	
	private final String login;
	
	public ClientPrivateMessageAskingFrameRead(String login) {
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
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
	}
}
