package fr.upem.net.tcp.nonblocking.clientPrivateReader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;
import fr.upem.net.tcp.nonblocking.reader.StringReader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Private Connection Frame
 */
public class ClientPrivateConnectionReader implements Reader {

	private enum State {
		DONE, WAITINGLOGIN, WAITINGID, ERROR
	};

	private State state = State.WAITINGLOGIN;
	private StringReader sr;
	private String login;
	private long id;
	private final ByteBuffer bb;

	public ClientPrivateConnectionReader(ByteBuffer bb) {
		sr = new StringReader(bb);
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
		for (;;) {
			var process = sr.process();
			if (process == ProcessStatus.DONE) {
				switch (state) {
				case WAITINGLOGIN:
					login = (String) sr.get();
					sr.reset();
					state = State.WAITINGID;
					
				case WAITINGID:
					try {
						bb.flip();
						if (bb.remaining() >= Long.BYTES) {
							id = bb.getLong();
							state = State.DONE;
							return ProcessStatus.DONE;
						} else {
							return ProcessStatus.REFILL;
						}
					} finally {
						bb.compact();
					}
					
				case ERROR:
					return ProcessStatus.ERROR;
				default:
					process = ProcessStatus.DONE;
				}
			}
			return process;
		}
	}

	/**
	 * Gets the Private Connection Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientPrivateConnectionFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientPrivateConnectionFrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return new ClientPrivateConnectionFrameRead(login, id);
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WAITINGLOGIN;
	}

}
