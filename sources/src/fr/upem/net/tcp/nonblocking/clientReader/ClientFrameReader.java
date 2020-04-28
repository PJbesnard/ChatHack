package fr.upem.net.tcp.nonblocking.clientReader;

import java.nio.ByteBuffer;
import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.reader.CodeReader;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * This class is a reader of ByteBuffer and check if it correspond to a Client Frame
 */
public class ClientFrameReader implements Reader {
	
	private final static int SIZEMAX = 1_024;
	
	private enum State {
		DONE, WAITINGLOGIN, WAITINGMSG, ERROR
	};

	private State state = State.WAITINGLOGIN;
	private Reader reader;

	private CodeReader OpCodeReader;
	private ByteBuffer bb;

	private FrameRead frame;

	public ClientFrameReader(ByteBuffer bb) {
		Objects.requireNonNull(bb);
		OpCodeReader = new CodeReader(bb);
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
		int ack;

		if (FrameTooLong()) {
			return ProcessStatus.ERROR;
		}
		
		var process1 = OpCodeReader.process();
		if (process1 != ProcessStatus.DONE) {
			return process1;
		}

		ack = (int) OpCodeReader.get();

		ProcessStatus process2;

		switch (ack) {
		case 1: {
			reader = new ClientPacketErrorReader();
			state = State.DONE;
			return ProcessStatus.DONE;
		}

		case 2: {
			reader = new ClientPLErrorReader();
			state = State.DONE;
			return ProcessStatus.DONE;
		}

		case 3: {
			reader = new ClientConnectedReader();
			state = State.DONE;
			return ProcessStatus.DONE;
		}

		case 4: {
			reader = new ClientLoginAEReader();
			state = State.DONE;
			return ProcessStatus.DONE;
		}

		case 5: {
			reader = new ClientGlobalMessageReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}
		case 6: {
			reader = new ClientPrivateMessageAskingReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		case 7: {
			reader = new ClientValidationPrivateConnectionReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		case 8: {
			reader = new ClientDenyPrivateConnectionReader(bb);
			process2 = reader.process();
			if (process2 == ProcessStatus.DONE) {
				state = State.DONE;
			}
			return process2;
		}

		default:
			System.out.println("Unexpected value: " + ack);
			return ProcessStatus.ERROR;
		}
	}

	/**
	 * Gets the Client Frame which represents the ByteBuffer that have been proceed
	 * @return a ClientFrameRed which represents the ByteBuffer that have been proceed
	 */
	@Override
	public FrameRead get() {
		if (state != State.DONE) {
			throw new IllegalStateException();
		}
		frame = (FrameRead) reader.get();
		reader.reset();
		OpCodeReader.reset();
		return frame;
	}

	/**
	 * Check is the frame is too long
	 * @return true if the frame is too long, false either
	 */
	private boolean FrameTooLong() {
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
		state = State.WAITINGLOGIN;
	}
}