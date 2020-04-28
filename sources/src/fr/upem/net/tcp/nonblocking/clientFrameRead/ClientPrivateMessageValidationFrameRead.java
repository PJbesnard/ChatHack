package fr.upem.net.tcp.nonblocking.clientFrameRead;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a private message validation Frame that have been read by the client
 */
public class ClientPrivateMessageValidationFrameRead implements FrameRead{
	private final String sender;
	private final String receiver;
	private final int port;
	private final byte[] address;
	private final long id;

	public ClientPrivateMessageValidationFrameRead(String sender, String receiver, int port, byte[] address, long id) {
		this.sender = sender;
		this.receiver = receiver;
		this.port = port;
		this.address = address;
		this.id = id;
	}
	
	/**
	 * Gets the sender
	 * @return a String which represents the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * Gets the receiver
	 * @return a String which represents the receiver
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * Gets the port
	 * @return an int which represents the port
	 */
	public int getPort() {
		return port;
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
	 * Accepts the frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
	}
}
