package fr.upem.net.tcp.nonblocking.clientPrivateFrame;

import java.nio.ByteBuffer;

/**
 * This interface represents clients private communications Frame that implements a get method which allow to get the ByteBuffer corresponding to this frame
 */
public interface ClientPrivateFrame {
	public ByteBuffer get();
}
