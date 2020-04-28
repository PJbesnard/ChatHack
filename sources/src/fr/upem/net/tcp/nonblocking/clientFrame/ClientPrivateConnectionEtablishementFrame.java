package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This Class allows to get a ByteBuffer and informations corresponding to the Private Connection Establishment Frame
 */
public class ClientPrivateConnectionEtablishementFrame implements ClientFrame{
private final String senderLogin;
private final String receiverLogin;
	
	public ClientPrivateConnectionEtablishementFrame(String senderLogin, String receiverLogin) {
		this.senderLogin = Objects.requireNonNull(senderLogin);
		this.receiverLogin = Objects.requireNonNull(receiverLogin);
	}
	
	/**
	 * Gets the receiver login
	 * @return a String which represents the receiver login
	 */
	public String getReceiverLogin() {
		return receiverLogin;
	}
	
	/**
	 * Gets the sender login
	 * @return a String which represents the sender login
	 */
	public String getSenderLogin() {
		return senderLogin;
	}
	
	/**
	 * Returns a ByteBuffer corresponding to the Private Connection Establishment Frame
	 */
	@Override
	public ByteBuffer get() {
		var BBsenderLogin = StandardCharsets.UTF_8.encode(senderLogin);
		var BBreceiverLogin = StandardCharsets.UTF_8.encode(receiverLogin);
		var buff = ByteBuffer.allocate(Byte.BYTES + 2 * Integer.BYTES + BBsenderLogin.remaining() + BBreceiverLogin.remaining());
		buff.put((byte) 4).putInt(BBsenderLogin.remaining()).put(BBsenderLogin).putInt(BBreceiverLogin.remaining()).put(BBreceiverLogin);
		return buff;
	}
}
