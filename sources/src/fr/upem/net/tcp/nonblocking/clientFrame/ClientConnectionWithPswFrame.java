package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This Class allows to get a ByteBuffer corresponding to the Client Connection With Password Frame
 */
public class ClientConnectionWithPswFrame implements ClientFrame {
	private final String login;
	private final String psw;
	
	public ClientConnectionWithPswFrame(String login, String psw) {
		this.login = Objects.requireNonNull(login);
		this.psw = Objects.requireNonNull(psw);
	}
	
	/**
	 * Returns a ByteBuffer corresponding to the Client Connection With Password Frame
	 */
	@Override
	public ByteBuffer get(){
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var BBpsw = StandardCharsets.UTF_8.encode(psw);
		var buff = ByteBuffer.allocate(Byte.BYTES + 2*Integer.BYTES + BBlogin.remaining() + BBpsw.remaining());
		buff.put((byte) 1).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBpsw.remaining()).put(BBpsw);
		return buff;
	}
}
