package fr.upem.net.tcp.nonblocking.frameRead;

/**
 * The FrameRead interface represents a frame decoded from a ByteBuffer,
 *  All implementations must be used with a FrameRedVisitor implementation with which visit and treat the frame
 */
public interface FrameRead {
	
	/**
	 * Accepts the frame by using the visitor design pattern
	 * @param visitor the visitor
	 */
	public void accept(FrameReadVisitor visitor);
	
}
