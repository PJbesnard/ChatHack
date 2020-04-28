package fr.upem.net.tcp.nonblocking.clientFrame;

import java.nio.ByteBuffer;

/**
 * This interface represents Frame that implements a get method which allow to get the ByteBuffer corresponding to this frame
 */
public interface ClientFrame {
	public ByteBuffer get();
}
