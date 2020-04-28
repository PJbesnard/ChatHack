package fr.upem.net.tcp.nonblocking.console;

/**
 * This class allows to call an accept method on a Global message
 */
public class GlobalMessageConsole implements ConsoleMessageFrame{
	private final String login; 
	private final String msg;
	
	public GlobalMessageConsole(String login, String msg) {
		this.login = login; 
		this.msg = msg;
	}
	
	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Gets the message
	 * @return a String which represents the message
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(ConsoleMessageVisitor visitor) {
		visitor.consoleVisit(this);
	}
}
