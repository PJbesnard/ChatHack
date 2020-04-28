package fr.upem.net.tcp.nonblocking.frameRead;

import java.util.Objects;

/**
 * Represents a client private message validation frame read
 * Could be visited by a FrameReadVisitor
 */
public class PrivateMessageValidationFrameRead implements FrameRead{
	
	private final String sender;
	private final String receiver;
	private final int port;
	private final byte[] address;
	private final long id;
	
	public PrivateMessageValidationFrameRead(String sender, String receiver, int port, byte[] address, long id) {
		if(port < 0) {
			throw new IllegalArgumentException();
		}
		this.sender = Objects.requireNonNull(sender);
		this.receiver = Objects.requireNonNull(receiver);
		this.port = port;
		this.address = Objects.requireNonNull(address);
		this.id = id;
	}

	/**
	 * Accept the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
	}
	
	/**
	 * Gets the sender login
	 * @return a String which represents the sender login
	 */
	public String getSenderLogin() {
		return sender;
	}
	
	/**
	 * Gets the receiver login
	 * @return a String which represents the receiver login
	 */
	public String getReceiverLogin() {
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
	 * @return a long which represent the id
	 */
	public long getId() {
		return id;
	}
	


}
