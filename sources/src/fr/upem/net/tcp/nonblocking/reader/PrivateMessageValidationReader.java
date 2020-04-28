package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.frameRead.PrivateMessageValidationFrameRead;

/**
 * Represents a ByteBuffer reader that contains a private message validation frame
 */
public class PrivateMessageValidationReader implements Reader {

	private enum State {
		DONE, WSENDERLOGIN, WRECEIVERLOGIN, WAITINGPORT, WAITINGADRESS, WAITINGID, ERROR
	};

	private final ByteBuffer bb;
	private State state = State.WSENDERLOGIN;
	private String receiver;
	private String sender;
	private byte[] address;
	private int port;
	private long id;

	public PrivateMessageValidationReader(ByteBuffer bb) {
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
					state = State.WRECEIVERLOGIN;
				}
				
				case WRECEIVERLOGIN: {
					var stringReader = new StringReader(bb);
					var process = stringReader.process();
					if(process == ProcessStatus.REFILL) {
						return process;
					}
					receiver = (String) stringReader.get();
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
					}finally {
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
					}finally {
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
	 * Gets the Frame which represents the ByteBuffer that have been proceed
	 * @return a PrivateMessageValidationFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public PrivateMessageValidationFrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		
		return new PrivateMessageValidationFrameRead(sender, receiver, port, address, id);
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WSENDERLOGIN;
	}

}