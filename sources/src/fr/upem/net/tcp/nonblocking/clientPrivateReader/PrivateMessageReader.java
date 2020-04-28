package fr.upem.net.tcp.nonblocking.clientPrivateReader;

import java.nio.ByteBuffer;
import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.reader.CodeReader;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a private communication Frame
 */
public class PrivateMessageReader implements Reader {
	
	private final static int SIZEMAX = 1_024;
	
	private enum State {
		DONE, WAITINGOPCODE, WAITINGMSG, ERROR
	};

	private State state = State.WAITINGOPCODE;
	private final ByteBuffer bb;
	private Reader reader;
	private FrameRead frame;
	private ClientPrivateFileReader fileReader;

	public PrivateMessageReader(ByteBuffer bb) {
		Objects.requireNonNull(bb);
		this.bb = bb;
	}

	/**
	 * Reads the ByteBuffer
	 * @return ProcessStatus.REFILL if some operations are missing, ProcessStatus.ERROR if an error
	 * occurred and ProcessStatus.DONE if all informations are correct
	 */
	public ProcessStatus process() {
		if (state == State.DONE || state == State.ERROR) {
			throw new IllegalStateException();
		}
		
		if (fileReader != null && fileReader.isWaitingFile()) {
			var fileStatus = fileReader.process();
			if (fileStatus == ProcessStatus.DONE) {
				state = State.DONE;
				return ProcessStatus.DONE;
			}
			return ProcessStatus.REFILL;
		}
		
		if (FrameToLong()) {
			return ProcessStatus.ERROR;
		}
		int Opcode;
		var OpCodeReader = new CodeReader(bb);
		var process1 = OpCodeReader.process();
		if (process1 != ProcessStatus.DONE) {
			return process1;
		}

		Opcode = (int) OpCodeReader.get();
		ProcessStatus process2;	
				
		switch (Opcode) {
			case 7: {
				reader = new ClientPrivateMessageReader(bb);
				process2 = reader.process();
				if (process2 == ProcessStatus.DONE) {
					state = State.DONE;
				}
				return process2;
			}
	
			case 8: {
				fileReader = new ClientPrivateFileReader(bb);
				process2 = fileReader.process();
				if (process2 == ProcessStatus.DONE) {
					state = State.DONE;
				}
				return process2;
			}
			
			case 9: {
				reader = new ClientPrivateConnectionReader(bb);
				process2 = reader.process();
				if (process2 == ProcessStatus.DONE) {
					state = State.DONE;
				}
				return process2;
			}
	
			default:
				System.out.println("Unexpected value: " + Opcode);
				return ProcessStatus.ERROR;
		}
	}

	/**
	 * Gets the Private Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientPrivateFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public FrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		if (fileReader == null){
			frame = (FrameRead) reader.get();
			reader.reset();
		}
		else {
			frame = fileReader.get();
			fileReader.reset();
		}
		return frame;
	}
	
	/**
	 * Check if the frame is too long or not
	 * @return true if the frame is too long, false either
	 */
	private boolean FrameToLong() {
		bb.flip();
		if (bb.remaining() > SIZEMAX) {
			bb.compact();
			return true;
		}
		bb.compact();
		return false;
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WAITINGOPCODE;
		if(fileReader != null && !fileReader.isWaitingFile()) {
			fileReader = null;
		}
	}
}
