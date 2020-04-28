package fr.upem.net.tcp.nonblocking.console;

/**
 * This class allows to call an accept method on a close connection operation
 */
public class CloseConnection implements ConsoleMessageFrame {
	private final String login;
	
	public CloseConnection(String login) {
		this.login = login;
	}
	
	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(ConsoleMessageVisitor visitor) {
		visitor.consoleVisit(this);
	}
}
