package fr.upem.net.tcp.nonblocking.console;

/**
 * This class allows to call an accept method on a Decline Connection message
 */
public class DeclineConnection implements ConsoleMessageFrame {
	private final String loginDecline;
	private final String login;
	
	public DeclineConnection(String login, String loginDecline) {
		this.login = login;
		this.loginDecline = loginDecline;
	}
	
	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Gets the login that have been decline
	 * @return a String which represents the login that have been decline
	 */
	public String getLoginDecline() {
		return loginDecline;
	}

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(ConsoleMessageVisitor visitor) {
		visitor.consoleVisit(this);
	}
}
