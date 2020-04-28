package fr.upem.net.tcp.nonblocking.clientReader;


import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPLErrorFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Password or Login Error Frame
 */
public class ClientPLErrorReader implements Reader {

	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.DONE
	 */
	@Override
	public ProcessStatus process() {
		return ProcessStatus.DONE;
	}

	/**
	 * Gets the Client Password or Login Error Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientPLErrorFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientPLErrorFrameRead get() {
		return new ClientPLErrorFrameRead();
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
	}
}
