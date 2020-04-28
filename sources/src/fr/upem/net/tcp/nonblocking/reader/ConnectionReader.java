package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;

import fr.upem.net.tcp.nonblocking.frameRead.ConnectionFrameRead;

/**
 *  Represents a ByteBuffer reader that contains a connection frame
 */
public class ConnectionReader implements Reader {
	private enum State {
		DONE, WAITINGLOGIN, WAITINGPSWD, ERROR
	};

	private State state = State.WAITINGLOGIN;
	private StringReader sr;
	private String login;
	private String password;

	public ConnectionReader(ByteBuffer bb) {
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
									   state = State.WAITINGPSWD;
									   continue;
					case WAITINGPSWD: password = (String) sr.get();
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
	 * @return a ConnectionFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ConnectionFrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return new ConnectionFrameRead(login, password);
	}
	
	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WAITINGLOGIN;
	}
}