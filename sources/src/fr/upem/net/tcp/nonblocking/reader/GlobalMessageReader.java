package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.frameRead.GlobalMessageFrameRead;

/**
 * Represents a ByteBuffer reader that contains a global message frame
 */
public class GlobalMessageReader implements Reader {
	private enum State {
		DONE, WAITINGLOGIN, WAITINGMSG, ERROR
	};

	private State state = State.WAITINGLOGIN;
	private StringReader sr;
	private String login;
	private String message;

	public GlobalMessageReader(ByteBuffer bb) {
		sr = new StringReader(bb);
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
		for(;;) {
			var process = sr.process();
			if(process == ProcessStatus.DONE) {
				switch(state) {
					case WAITINGLOGIN: login = (String) sr.get();
									   sr.reset();
									   state = State.WAITINGMSG;
									   continue;
					case WAITINGMSG: message = (String) sr.get();
					   				 sr.reset();
					   				 state = State.DONE;
					   				 break;
					case ERROR: return ProcessStatus.ERROR;
					default: process = ProcessStatus.DONE;
				}
			}
			return process;
		}	
	}

	/**
	 * Gets the Frame which represents the ByteBuffer that have been proceed
	 * @return a GlobalMessageFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public GlobalMessageFrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return new GlobalMessageFrameRead(login, message);
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WAITINGLOGIN;
	}
}
