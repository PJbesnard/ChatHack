package fr.upem.net.tcp.nonblocking.DBreader;

import java.nio.ByteBuffer;
import java.util.Objects;

import fr.upem.net.tcp.nonblocking.DBframeRead.DBResponseFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;
/**
 * This class is a reader of ByteBuffer and check if it correspond to a Database Response Frame
 */
public class DBResponseReader implements Reader {
	private enum State {
		DONE, WRESPONSE, WID, ERROR
	};

	private State state = State.WRESPONSE;
	private byte response;
	private long id;
	private ByteBuffer bb;

	public DBResponseReader(ByteBuffer bb) {
		this.bb = Objects.requireNonNull(bb);
	}

	/**
	 * Read the ByteBuffer
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
				case WRESPONSE:
					if (bb.remaining() >= 1) {
						response = bb.get();
						state = State.WID;
					}else {
						return ProcessStatus.REFILL;
					}
					continue;
					
				case WID:
					if (bb.remaining() >= Long.BYTES) {
						id = bb.getLong();
						state = State.DONE;
						return ProcessStatus.DONE;
					}else {
						return ProcessStatus.REFILL;
					}

				default:
					return ProcessStatus.ERROR;
				}
			}
		} finally {
			bb.compact();
		}

	}

	/**
	 * Reset the reader
	 */
	@Override
	public void reset() {
		state = State.WRESPONSE;
	}

	/**
	 * Get the Frame which represents the ByteBuffer that have been proceed
	 * @return a DBResponseFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public FrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return new DBResponseFrameRead(response, id);
	}


}
