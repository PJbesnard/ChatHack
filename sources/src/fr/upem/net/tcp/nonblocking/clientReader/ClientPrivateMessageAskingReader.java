package fr.upem.net.tcp.nonblocking.clientReader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPrivateMessageAskingFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;
import fr.upem.net.tcp.nonblocking.reader.StringReader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Private Message Asking Frame
 */
public class ClientPrivateMessageAskingReader implements Reader{
	private final StringReader reader;

	public ClientPrivateMessageAskingReader(ByteBuffer bb) {
		reader = new StringReader(bb);
	}
	
	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.DONE
	 */
	@Override
	public ProcessStatus process() {
		return reader.process();
	}

	/**
	 * Gets the Client Private Message Asking Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientPrivateMessageAskingFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientPrivateMessageAskingFrameRead get() {
		return new ClientPrivateMessageAskingFrameRead((String) reader.get());
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		reader.reset();	
	}
}
