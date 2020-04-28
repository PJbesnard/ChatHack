package fr.upem.net.tcp.nonblocking.clientFrameRead;

import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This interface represents Frame that have been read by the client
 */
public interface ClientFrameRead {

	/**
	 * Accepts the frame by using the visitor design pattern
	 * @param visitor the visitor
	 */
	public void accept(FrameReadVisitor visitor);
	
}
