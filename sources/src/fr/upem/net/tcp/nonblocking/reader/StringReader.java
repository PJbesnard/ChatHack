package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Represents a ByteBuffer reader that contains a string
 */
public class StringReader implements Reader {

	private enum State {
		DONE, WAITINGINT, WAITINGMSG, ERROR
	};

	private final ByteBuffer bb;
	private State state = State.WAITINGINT;
	private String value;
	private int size;

	public StringReader(ByteBuffer bb) {
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
				case WAITINGINT: {

					if (bb.remaining() >= Integer.BYTES) {
						size = bb.getInt();
						state = State.WAITINGMSG;
					} else {
						return ProcessStatus.REFILL;
					}
				}

				case WAITINGMSG: {
					if (bb.remaining() >= size) {
						var lim = bb.limit();
						bb.limit(bb.position() + size);
						value = StandardCharsets.UTF_8.decode(bb).toString();
						state = State.DONE;
						bb.limit(lim);
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
	 * Gets the String which represents the ByteBuffer that have been proceed
	 * @return a String which represents the ByteBuffer that have been proceed
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
		state = State.WAITINGINT;
	}

}