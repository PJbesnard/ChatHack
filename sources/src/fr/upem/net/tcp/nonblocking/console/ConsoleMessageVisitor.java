package fr.upem.net.tcp.nonblocking.console;

/*
 * This interface represent the Console Message visitor by using the visitor design pattern
 */
public interface ConsoleMessageVisitor {

	/**
	 * This method allow to treat the Private Messages by using the visitor design pattern
	 * @param privateMessage the object corresponding to private message sending order
	 */
	void consoleVisit(PrivateMessage privateMessage);

	/**
	 * This method allow to treat acceptance of connection by using the visitor design pattern
	 * @param acceptConnection the object corresponding to accept connection order
	 */
	void consoleVisit(AcceptConnection acceptConnection);

	/**
	 * This method allow to treat declined connections by using the visitor design pattern
	 * @param declineConnection the object corresponding to decline connection order
	 */
	void consoleVisit(DeclineConnection declineConnection);

	/**
	 * This method allow to treat private files by using the visitor design pattern
	 * @param privateFile the object corresponding to private file sending order
	 */
	void consoleVisit(PrivateFile privateFile);

	/**
	 * This method allow to treat global messages by using the visitor design pattern
	 * @param globalMessage the object corresponding to global message sending order
	 */
	void consoleVisit(GlobalMessageConsole globalMessage);

	/**
	 * This method allow to treat closing connection by using the visitor design pattern
	 * @param closeConnection the object corresponding to close connection order
	 */
	void consoleVisit(CloseConnection closeConnection);

}
