package fr.upem.net.tcp.nonblocking.clientPrivateFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This class allows to get a ByteBuffer and informations corresponding to the Private File Frame
 */
public class ClientPrivateFileFrame implements ClientPrivateFrame{
	
	private final byte[] file;
	private final String fileName;
	private final String login;
	
	public ClientPrivateFileFrame(String login, String fileName, byte[] message) {
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
	 * Returns a ByteBuffer corresponding to the Private File Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var BBfileName = StandardCharsets.UTF_8.encode(fileName);
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES * 3 + BBlogin.remaining()  + BBfileName.remaining() + file.length);
		buff.put((byte) 8).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBfileName.remaining()).put(BBfileName).putInt(file.length).put(file);
		return buff;
	}
}