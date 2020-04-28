package fr.upem.net.tcp.nonblocking.clientReader;

import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPacketErrorFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Packet Error Frame
 */
public class ClientPacketErrorReader implements Reader {

	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.DONE
	 */
	@Override
	public ProcessStatus process() {
		return ProcessStatus.DONE;
	}

	/**
	 * Gets the Client Packet Error Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientPacketErrorFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientPacketErrorFrameRead get() {
		return new ClientPacketErrorFrameRead();
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
	}
}
