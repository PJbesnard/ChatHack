package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;

/**
 * This Class allows to get a ByteBuffer corresponding to the Packet Error Frame
 */
public class ClientPacketErrorFrame implements ClientFrame {
	
	/**
	 * Returns a ByteBuffer corresponding to the Packet Error Frame
	 */
	@Override
	public ByteBuffer get() {
		ByteBuffer bb = ByteBuffer.allocate(Byte.BYTES);
		return bb.put((byte) 1);
	}

}
