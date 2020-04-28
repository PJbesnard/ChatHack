package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;

/**
 * This Class allows to get a ByteBuffer corresponding to the Login Already Exist Frame
 */
public class ClientLoginAEFrame implements ClientFrame {

	/**
	 * Returns a ByteBuffer corresponding to the Login Already Exist Frame
	 */
	@Override
	public ByteBuffer get() {
		ByteBuffer bb = ByteBuffer.allocate(Byte.BYTES);
		return bb.put((byte) 4);
	}

}
