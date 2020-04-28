package fr.upem.net.tcp.nonblocking.frameRead;

import java.util.Objects;

/**
 * Represents a client connection without password frame read
 * Could be visited by a FrameReadVisitor
 */
public class ConnectionWithoutPasswordFrameRead implements FrameRead{
	private final String login;
	
	public ConnectionWithoutPasswordFrameRead(String login) {
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
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}


}
