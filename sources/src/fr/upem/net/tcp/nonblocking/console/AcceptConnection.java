package fr.upem.net.tcp.nonblocking.console;

/**
 * This class allows to call an accept method on an Accept Connection message
 */
public class AcceptConnection implements ConsoleMessageFrame {
	private final String login;
	
	public AcceptConnection(String login) {
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
