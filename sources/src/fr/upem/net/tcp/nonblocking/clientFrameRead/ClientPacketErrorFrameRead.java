package fr.upem.net.tcp.nonblocking.clientFrameRead;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * This class allows to call an accept method on a Packet Error Frame that have been read by the client
 */
public class ClientPacketErrorFrameRead implements FrameRead {

	/**
	 * Accepts the Frame by using the visitor design pattern
	 */
	@Override
	public void accept(FrameReadVisitor visitor) {
		Objects.requireNonNull(visitor);
		visitor.visit(this);
	}

}
