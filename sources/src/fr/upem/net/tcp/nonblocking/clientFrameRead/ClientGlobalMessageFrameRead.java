package fr.upem.net.tcp.nonblocking.clientFrameRead;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a Global Message Frame that have been read by the client
 */
public class ClientGlobalMessageFrameRead implements FrameRead{
	
	private final String message;
	private final String login;
	
	public ClientGlobalMessageFrameRead(String login, String message) {
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
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
	}
}
