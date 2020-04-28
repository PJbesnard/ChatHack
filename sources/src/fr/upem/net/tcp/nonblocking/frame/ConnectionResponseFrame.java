package fr.upem.net.tcp.nonblocking.frame;

import java.nio.ByteBuffer;

/**
 * Provides method to create a ByteBuffer representation of a client connection frame
 */
public class ConnectionResponseFrame implements Frame {

	private final int response;

	public ConnectionResponseFrame(String response) {
		switch (response) {
		case "good": {
			this.response = 3;
			break;
		}
		case "badlog": {
			this.response = 2;
			break;
		}
		case "logtaken": {
			this.response = 4;
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + response);
		}
	}

	/**
	 * Gets a ByteBuffer representation of the frame
	 * @return ByteBuffer representation of the frame
	 */
	public ByteBuffer get() {
		var buff = ByteBuffer.allocate(1);
		return buff.put((byte) response);
	}

}
