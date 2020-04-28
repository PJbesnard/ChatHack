package fr.upem.net.tcp.nonblocking.frameRead;

import java.util.Objects;

/**
 * Represents a global message frame read
 * Could be visited by a FrameReadVisitor
 */
public class GlobalMessageFrameRead implements FrameRead{
	
	private final String message;
	private String login;
	
	public GlobalMessageFrameRead(String login, String message) {
		this.message = Objects.requireNonNull(message);
		this.login = Objects.requireNonNull(login);
	}

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
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



}
