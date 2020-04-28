package fr.upem.net.tcp.nonblocking.DBframeRead;

/**
 * The DBFrameVisitor interface allows to treat database frames by using the visitor design pattern
 *
 */
public interface DBFrameVisitor {

	/**
	 * This method allows to treat a database response frame 
	 * @param frame the FrameRead
	 */
	default public void visit(DBResponseFrameRead frame) {
		return;
	}
	
}
