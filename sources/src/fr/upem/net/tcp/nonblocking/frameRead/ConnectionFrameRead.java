package fr.upem.net.tcp.nonblocking.frameRead;

import java.util.Objects;

/**
 * Represents a client connection frame read
 * Could be visited by a FrameReadVisitor
 */
public class ConnectionFrameRead implements FrameRead{
	
	private final String login;
	private final String password;
	
	
	public ConnectionFrameRead(String login, String password) {
		this.login = Objects.requireNonNull(login);
		this.password = Objects.requireNonNull(password);
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
	
	/**
	 * Gets the password
	 * @return a String which represents the password
	 */
	public String getPassword() {
		return password;
	}


	
}
