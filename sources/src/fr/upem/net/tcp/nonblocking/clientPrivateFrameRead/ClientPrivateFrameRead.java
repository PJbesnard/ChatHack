package fr.upem.net.tcp.nonblocking.clientPrivateFrameRead;

import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This interface represents private communication Frame that have been read by the client
 */
public interface ClientPrivateFrameRead {

	/**
	 * Accepts the frame by using the visitor design pattern
	 * @param visitor the visitor
	 */
	public void accept(FrameReadVisitor visitor);
	
}
