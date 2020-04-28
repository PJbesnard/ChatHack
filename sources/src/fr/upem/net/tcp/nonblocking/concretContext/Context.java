package fr.upem.net.tcp.nonblocking.concretContext;

import java.io.IOException;

/**
 * This interface represents a Context
 */
public interface Context {

	/**
	 * Reads the SocketChannel
	 * @throws IOException if reading failed
	 */
	void doRead() throws IOException;

	/**
	 * Writes on the SocketChannel
	 * @throws IOException if writing failed
	 */
	void doWrite() throws IOException;
}
