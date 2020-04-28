package fr.upem.net.tcp.nonblocking.clientPrivateReader;

import java.nio.ByteBuffer;
import java.util.Objects;

import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateMessageFrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;
import fr.upem.net.tcp.nonblocking.reader.StringReader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Private Message Frame
 */
public class ClientPrivateMessageReader implements Reader {
	private enum State {
		DONE, WAITINGLOGIN, WAITINGMSG, ERROR
	};

	private State state = State.WAITINGLOGIN;
	private StringReader sr;
	private String login;
	private String msg;

	public ClientPrivateMessageReader(ByteBuffer bb) {
		Objects.requireNonNull(bb);
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
					case WAITINGMSG: msg = (String) sr.get();
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
	 * Gets the Private Message Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientPrivateMessageFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public ClientPrivateMessageFrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return new ClientPrivateMessageFrameRead(login, msg);
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WAITINGLOGIN;
	}
}
