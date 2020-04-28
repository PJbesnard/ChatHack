package fr.upem.net.tcp.nonblocking.frameRead;

import java.util.Objects;

/**
 * Represents a client private message deny frame read
 * Could be visited by a FrameReadVisitor
 */
public class PrivateMessageDenyFrameRead implements FrameRead {
	
	private final String sender;
	private final String receiver;
	
	public PrivateMessageDenyFrameRead(String sender, String receiver) {
		this.sender = Objects.requireNonNull(sender);
		this.receiver = Objects.requireNonNull(receiver);
	}

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		Objects.requireNonNull(visitor);
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


}
