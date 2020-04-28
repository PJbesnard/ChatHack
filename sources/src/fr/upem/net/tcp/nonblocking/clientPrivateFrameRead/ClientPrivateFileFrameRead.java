package fr.upem.net.tcp.nonblocking.clientPrivateFrameRead;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a Private Connection Frame that have been read by the client
 */
public class ClientPrivateFileFrameRead implements FrameRead{
	
	private final byte[] file;
	private final String fileName;
	private final String login;
	
	public ClientPrivateFileFrameRead(String login, String fileName, byte[] message) {
		this.login = Objects.requireNonNull(login);
		this.file = Objects.requireNonNull(message);
		this.fileName = Objects.requireNonNull(fileName);
	}

	/**
	 * Gets the file
	 * @return a byte array which represents the file
	 */
	public byte[] getFile() {
		return file;
	}
	
	/**
	 * Gets the file name
	 * @return a String which represents the file name
	 */
	public String getFileName() {
		return fileName;
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
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);
	}
}