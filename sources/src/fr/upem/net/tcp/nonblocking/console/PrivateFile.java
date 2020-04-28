package fr.upem.net.tcp.nonblocking.console;

/**
 * This class allows to call an accept method on a private file message
 */
public class PrivateFile implements ConsoleMessageFrame {
	private final String login; 
	private final String fileName;
	
	public PrivateFile(String login, String filename) {
		this.login = login;
		this.fileName = filename;
	}

	/**
	 * Gets the login
	 * @return a String which represents the login
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Gets the file name
	 * @return a String which represents a file name 
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(ConsoleMessageVisitor visitor) {
		visitor.consoleVisit(this);
	}
}
