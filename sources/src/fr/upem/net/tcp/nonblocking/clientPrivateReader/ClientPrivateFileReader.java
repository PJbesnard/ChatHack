package fr.upem.net.tcp.nonblocking.clientPrivateReader;

import java.nio.ByteBuffer;
import java.util.Objects;

import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateFileFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.reader.Reader;
import fr.upem.net.tcp.nonblocking.reader.StringReader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Private File Frame
 */
public class ClientPrivateFileReader implements Reader {
	private enum State {
		DONE, WAITINGLOGIN, WAITINGFILENAME, WAITINGSIZE, WAITINGFILE, ERROR
	};

	private State state = State.WAITINGLOGIN;
	private StringReader sr;
	private String login;
	private String fileName;
	private byte[] file;
	private int actualFileSize = 0;
	
	private int remainingSize;
	
	private ByteBuffer bb;
	

	public ClientPrivateFileReader(ByteBuffer bb) {
		Objects.requireNonNull(bb);
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
			switch (state) {

			case WAITINGLOGIN:
				var process = sr.process();
				if (process != ProcessStatus.DONE) {
					return process;
				}
				login = (String) sr.get();

				sr.reset();
				state = State.WAITINGFILENAME;
				continue;

			case WAITINGFILENAME:
				process = sr.process();
				if (process != ProcessStatus.DONE) {
					return process;
				}
				fileName = (String) sr.get();

				sr.reset();
				state = State.WAITINGSIZE;
				continue;
				
			case WAITINGSIZE: {
				
				bb.flip();
				try {
					if (bb.remaining() >= Integer.BYTES) {
						int size = bb.getInt();
						file = new byte[size];
						remainingSize = size;
						state = State.WAITINGFILE;
					} else {
						return ProcessStatus.REFILL;
					}
				}finally {
					bb.compact();
				}
			}

			case WAITINGFILE:
				bb.flip();
				try {
					var remaining = bb.remaining();
					if (bb.remaining() <= remainingSize) {
						bb.get(file, actualFileSize, bb.remaining());
						actualFileSize += remaining;
					}else {
						bb.get(file, actualFileSize, remainingSize);
						actualFileSize += remainingSize;
					}
					if (remainingSize == actualFileSize) {
						// reset ici
						state = State.DONE;
						return ProcessStatus.DONE;
					}
					return ProcessStatus.REFILL;
				}finally {
					bb.compact();
				}
				

			case ERROR:
				return ProcessStatus.ERROR;
			default:
				process = ProcessStatus.DONE;
			}
		}

	}

	/**
	 * Gets the Private File Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientPrivateFileFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public FrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		return new ClientPrivateFileFrameRead(login, fileName, file);
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {		
		state = State.WAITINGLOGIN;
	}

	/**
	 * Returns a boolean which say if a file is in waiting or not
	 * @return true if a file is in waiting, false either
	 */
	public boolean isWaitingFile() {
		return state == State.WAITINGFILE;
	}
}
