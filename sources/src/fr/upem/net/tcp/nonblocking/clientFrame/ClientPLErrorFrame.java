package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;

/**
 * This Class allows to get a ByteBuffer corresponding to the Password or Login Error Frame
 */
public class ClientPLErrorFrame implements ClientFrame {

	/**
	 * Returns a ByteBuffer corresponding to the Password or Login Error Frame
	 */
	@Override
	public ByteBuffer get() {
		ByteBuffer bb = ByteBuffer.allocate(Byte.BYTES);
		return bb.put((byte) 2);
	}

}
