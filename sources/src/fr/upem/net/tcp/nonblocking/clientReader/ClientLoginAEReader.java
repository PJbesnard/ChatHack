package fr.upem.net.tcp.nonblocking.clientReader;

import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientLoginAEFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Login Already Exit Error Frame
 */
public class ClientLoginAEReader implements Reader {

	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.DONE
	 */
	@Override
	public ProcessStatus process() {
		return ProcessStatus.DONE;
	}

	/**
	 * Gets the Client Login Already Exit Error Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientLoginAEFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientLoginAEFrameRead get() {
		return new ClientLoginAEFrameRead();
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
	}
}
