package fr.upem.net.tcp.nonblocking.clientReader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientValidationPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.reader.AddressReader;
import fr.upem.net.tcp.nonblocking.reader.Reader;
import fr.upem.net.tcp.nonblocking.reader.StringReader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Validation Private Connection Frame
 */
public class ClientValidationPrivateConnectionReader implements Reader {
	private enum State {
		DONE, WSENDERLOGIN, WAITINGPORT, WAITINGADRESS, WAITINGID, ERROR
	};

	private final ByteBuffer bb;
	private State state = State.WSENDERLOGIN;
	private String sender;
	private byte[] address;
	private int port;
	private long id;

	public ClientValidationPrivateConnectionReader(ByteBuffer bb) {
		this.bb = bb;
	}

	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.REFILL if some operations are missing, ProcessStatus.ERROR if an error
	 * occurred and ProcessStatus.DONE if all informations are correct
	 */
	@Override
	public ProcessStatus process() {
		if (state == State.DONE || state == State.ERROR) {
			throw new IllegalStateException();
		}

		try {
			for (;;) {
				switch (state) {
				
				case WSENDERLOGIN: {
					var stringReader = new StringReader(bb);
					var process = stringReader.process();
					if(process == ProcessStatus.REFILL) {
						return process;
					}
					sender = (String) stringReader.get();
					state = State.WAITINGPORT;
				}

				case WAITINGPORT: {
					try {
						bb.flip();
						if (bb.remaining() >= Integer.BYTES) {
							port = bb.getInt();
							state = State.WAITINGADRESS;
						}else {
							return ProcessStatus.REFILL;
						}
						
					} finally {
						bb.compact();
					}
					
				}
				
				case WAITINGADRESS: {
					var addressReader = new AddressReader(bb);
					var process = addressReader.process();
					if(process == ProcessStatus.REFILL) {
						return process;
					}
					address = (byte[]) addressReader.get();
					state = State.WAITINGID;
				}
				
				case WAITINGID: {
					try {
						bb.flip();
						if (bb.remaining() >= Long.BYTES) {
							id = bb.getLong();
							state = State.DONE;
							return ProcessStatus.DONE;
						}
						return ProcessStatus.REFILL;
						
					} finally {
						bb.compact();
					}
					
				}
				default:
					throw new IllegalArgumentException("Unexpected value: ");
				}
			}
		} finally {

		}

	}

	/**
	 * Gets the Client Validation Private Connection Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientValidationPrivateConnectionFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientValidationPrivateConnectionFrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return new ClientValidationPrivateConnectionFrameRead(sender, port, address, id);
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WSENDERLOGIN;
	}
}
