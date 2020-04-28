package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;

/**
 * Represents a ByteBuffer reader that contains an Opcode
 */
public class CodeReader implements Reader {

	private enum State {
		DONE, WBYTES, ERROR
	};

	private final ByteBuffer bb;
	private State state = State.WBYTES;
	private int value;

	public CodeReader(ByteBuffer bb) {
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
			System.out.println("state: " + state);
			throw new IllegalStateException();
		}
		bb.flip();
		try {
			for (;;) {
				switch (state) {
				
				case WBYTES: {
					if (bb.remaining() >= 1) {
						value = bb.get();
						state = State.DONE;
						return ProcessStatus.DONE;
					}
					return ProcessStatus.REFILL;
				}
				default:
					System.out.println("Unexpected value: " + state);
					return ProcessStatus.ERROR;
				}
			}
		} finally {
			bb.compact();
		}

	}

	/**
	 * Gets the integer which represents the ByteBuffer that have been proceed
	 * @return an int which represents the ByteBuffer that have been proceed
	 */
	@Override
	public Object get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return value;
	}

	/**
	 * Reset the reader
	 */
	@Override
	public void reset() {
		state = State.WBYTES;
	}

}