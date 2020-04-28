package fr.upem.net.tcp.nonblocking.clientReader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientDenyPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;
import fr.upem.net.tcp.nonblocking.reader.StringReader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Deny Private Connection Frame
 */
public class ClientDenyPrivateConnectionReader implements Reader{
	private final StringReader reader;

	public ClientDenyPrivateConnectionReader(ByteBuffer bb) {
		reader = new StringReader(bb);
	}
	
	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.REFILL if some operations are missing, ProcessStatus.ERROR if an error
	 * occurred and ProcessStatus.DONE if all informations are correct
	 */
	@Override
	public ProcessStatus process() {
		return reader.process();
	}

	/**
	 * Gets the Client Deny Private Connection Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientDenyPrivateConnectionFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientDenyPrivateConnectionFrameRead get() {
		return new ClientDenyPrivateConnectionFrameRead((String) reader.get());
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		reader.reset();	
	}
}
