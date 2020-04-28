package fr.upem.net.tcp.nonblocking.console;

import fr.upem.net.tcp.nonblocking.clientPrivateFrame.ClientPrivateMessageFrame;

/**
 * This class allows to call an accept method on a private message
 */
public class PrivateMessage implements ConsoleMessageFrame {
	private final String login;
	private final ClientPrivateMessageFrame frame;
	
	public PrivateMessage(String login, ClientPrivateMessageFrame frame) {
		this.login = login;
		this.frame = frame;
	}
	
	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Gets the frame
	 * @return a ClientPrivateMessageFrame which represents the frame
	 */
	public ClientPrivateMessageFrame getFrame() {
		return frame;
	}

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(ConsoleMessageVisitor visitor) {
		visitor.consoleVisit(this);
	}
}
