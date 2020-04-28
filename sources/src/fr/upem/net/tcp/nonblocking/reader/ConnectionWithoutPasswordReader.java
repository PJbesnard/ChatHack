package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.frameRead.ConnectionWithoutPasswordFrameRead;

/**
 * Represents a ByteBuffer reader that contains a frame of a connection without password
 */
public class ConnectionWithoutPasswordReader implements Reader{
	private final Reader loginReader;
	
	public ConnectionWithoutPasswordReader(ByteBuffer bb) {
		this.loginReader = new StringReader(bb);
	}

	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.REFILL if some operations are missing, ProcessStatus.ERROR if an error
	 * occurred and ProcessStatus.DONE if all informations are correct
	 */
	@Override
	public ProcessStatus process() {
		return loginReader.process();
	}

	/**
	 * Gets the Frame which represents the ByteBuffer that have been proceed
	 * @return a ConnectionWithoutPasswordFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ConnectionWithoutPasswordFrameRead get() {
		return new ConnectionWithoutPasswordFrameRead((String) loginReader.get());
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		loginReader.reset();		
	}
}
