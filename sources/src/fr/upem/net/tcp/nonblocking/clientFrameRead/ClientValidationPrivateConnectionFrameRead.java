package fr.upem.net.tcp.nonblocking.clientFrameRead;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a Validation of Private Connection Frame that have been read by the client
 */
public class ClientValidationPrivateConnectionFrameRead implements FrameRead {
	private final String login;
	private final int port;
	private final byte[] address;
	private final long id;

	public ClientValidationPrivateConnectionFrameRead(String login, int port, byte[] address, long id) {
		this.login = login;
		this.port = port;
		this.address = address;
		this.id = id;
	}
	
	/**
	 * Gets the address
	 * @return a byte array which represents the address
	 */
	public byte[] getAddress() {
		return address;
	}
	
	/**
	 * Gets the id
	 * @return a long which represents the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Gets the port
	 * @return an int which represents the port
	 */
	public int getPort() {
		return port;
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
