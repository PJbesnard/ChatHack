package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;

/**
 * This class allows to get a ByteBuffer corresponding to the Client Connected Frame
 */
public class ClientConnectedFrame implements ClientFrame {

	/**
	 * Returns a ByteBuffer corresponding to the Client Connected Frame
	 */
	@Override
	public ByteBuffer get() {
		ByteBuffer bb = ByteBuffer.allocate(Byte.BYTES);
		return bb.put((byte) 3);
	}

}
