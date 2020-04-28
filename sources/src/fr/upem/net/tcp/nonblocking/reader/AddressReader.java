package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;

/**
 * Represents a ByteBuffer reader that contains an address
 */
public class AddressReader implements Reader {

	private enum State {
		DONE, WBYTES, WADRESS, ERROR
	};

	private final ByteBuffer bb;
	private State state = State.WBYTES;
	private byte[] value;
	private short type;

	public AddressReader(ByteBuffer bb) {
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
		bb.flip();
		try {
			for (;;) {
				switch (state) {
				case WBYTES: {

					if (bb.remaining() >= 1) {
						type = bb.get();
						state = State.WADRESS;
					} else {
						return ProcessStatus.REFILL;
					}
				}

				case WADRESS: {
					var bytes = 0;
					if(type == 1) {
						bytes = 4;
					}
					else if(type == 2) {
						bytes = 16;
					}
					else {
						return ProcessStatus.ERROR;
					}
					value = new byte[bytes];
					if (bb.remaining() >= bytes) {
						bb.get(value, 0, bytes);
						state = State.DONE;
						return ProcessStatus.DONE;
					}
					return ProcessStatus.REFILL;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: ");
				}
			}
		} finally {
			bb.compact();
		}

	}

	/**
	 * Gets the byte array which represents the ByteBuffer that have been proceed
	 * @return a byte array which represents the ByteBuffer that have been proceed
	 */
	@Override
	public Object get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return value;
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WBYTES;
	}

}