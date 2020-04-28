package fr.upem.net.tcp.nonblocking.console;

/**
 * This interface represents Console Message 
 */
public interface ConsoleMessageFrame {
	
	/**
	 * Accepts the frame by using the visitor design pattern
	 * @param visitor the visitor
	 */
	public void accept(ConsoleMessageVisitor visitor);
}
