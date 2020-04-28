package fr.upem.net.tcp.nonblocking.reader;

import java.nio.ByteBuffer;
import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;

/**
 * Represents a ByteBuffer reader that contains a frame that could be received from a client connected to a server
 */
public class FrameReader implements Reader {
	
	public FrameReader(ByteBuffer bb) {
		Objects.requireNonNull(bb);
		opCodeReader = new CodeReader(bb);
		this.bb = bb;
	}
	
	private final static int SIZEMAX = 1_024;
	
	private enum State {
		DONE, WAITINGLOGIN, WAITINGMSG, ERROR
	};

	private State state = State.WAITINGLOGIN;
	private Reader reader;

	private CodeReader opCodeReader;
	private ByteBuffer bb;

	private FrameRead frame;

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
		
		if (FrameToLong()) {
			return ProcessStatus.ERROR;
		}
		
		int Opcode;

		var process1 = opCodeReader.process();
		if (process1 != ProcessStatus.DONE) {
			return process1;
		}
		

		Opcode = (int) opCodeReader.get();
		opCodeReader.reset();
		ProcessStatus process2;

		switch (Opcode) {
		case 1: {
			reader = new ConnectionReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		case 2: {
			reader = new ConnectionWithoutPasswordReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		case 3: {
			reader = new GlobalMessageReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		case 4: {
			reader = new PrivateMessageAskingReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		case 5: {
			reader = new PrivateMessageValidationReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}
		case 6: {
			reader = new PrivateMessageDenydReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		default:
			return ProcessStatus.ERROR;
		}
	}

	/**
	 * Checks if the frame is too long
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
	 * Gets the Frame which represents the ByteBuffer that have been proceed
	 * @return a FrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public FrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		
		frame = (FrameRead) reader.get();
		reader.reset();
		opCodeReader.reset();
		return frame;
	}

	/**
	 * Resets the reader
	 */
	@Override
	public void reset() {
		state = State.WAITINGLOGIN;
	}
}