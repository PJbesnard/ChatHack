package fr.upem.net.tcp.nonblocking.console;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.clientPrivateFrame.ClientPrivateMessageFrame;

/**
 * This treat message that have been write by a client
 */
public class ConsoleMessage{
	private final String command;
	
	public ConsoleMessage(String command) {
		Objects.requireNonNull(command);
		if(command.length() <= 0) {
			throw new IllegalArgumentException("Invalid console message");
		}
		this.command = command;
	}

	/**
	 * Treats a message that have been write by an user
	 * @param login the login of the user
	 * @return ConsoleMessageFrame which correspond to the treated message
	 */
	public ConsoleMessageFrame treat(String login) {
		var firstChar = command.charAt(0);
		switch (firstChar) {
		case '@':
			var txt = command.replaceFirst("@", "").split(" ", 2);
			if (txt.length != 2) {System.out.println("\n Unknown request, please use :\n - @login message -> to send a message\n - /login fileName -> to send a file\\n - +login -> to accept private connextion\n - -login to decline\n #login for close connection");
				return null;}
			return new PrivateMessage(txt[0], new ClientPrivateMessageFrame(login, txt[1]));

		case '+':
			return new AcceptConnection(command.replaceFirst("\\+", ""));

		case '-':	
			return new DeclineConnection(login, command.replaceFirst("-", ""));
			
		case '/':
			var txt2 = command.replaceFirst("/", "").split(" ", 2);
			if (txt2.length != 2) {System.out.println("\nUnknown request, please use :\n - @login message -> to send a message\n - /login fileName -> to send a file\\n - +login -> to accept private connextion\n - -login to decline\n #login for close connection");
				return null;}
			return new PrivateFile(txt2[0], txt2[1]);	
		case '#':	
			return new CloseConnection(command.replaceFirst("#", ""));
			
		default:
			return new GlobalMessageConsole(login, command);
		}
	}
}
