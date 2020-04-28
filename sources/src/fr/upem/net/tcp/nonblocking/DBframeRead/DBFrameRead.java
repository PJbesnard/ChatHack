package fr.upem.net.tcp.nonblocking.DBframeRead;

/**
 * The DBFrameRead interface represents frames sended by a database
 *
 */
public interface DBFrameRead {
	
	/**
	 * Accepts the frame by using the visitor design pattern
	 * @param visitor the FrameVisitor
	 */
	public void accept(DBFrameVisitor visitor);
	
}
