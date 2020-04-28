package fr.upem.net.tcp.nonblocking.clientReader;

import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientConnectedFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Connected Frame
 */
public class ClientConnectedReader implements Reader{
	
	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.DONE
	 */
	@Override
	public ProcessStatus process() {
		return ProcessStatus.DONE;
	}

	/**
	 * Gets the Client Connected Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientConnectedFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientConnectedFrameRead get() {
		return new ClientConnectedFrameRead();
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
	}
}
