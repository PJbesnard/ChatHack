package fr.upem.net.tcp.nonblocking.contextVisitor;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.ServerChatHack;
import fr.upem.net.tcp.nonblocking.DBframeRead.DBResponseFrameRead;
import fr.upem.net.tcp.nonblocking.concretContext.ChatHackContext;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * Represents a server ClientDataBase frame read by using the visitor pattern
 */
public class ClientDataBaseFrameReadVisitor implements FrameReadVisitor{
	
	private final ServerChatHack server;
	private final ChatHackContext context;
	
	public ClientDataBaseFrameReadVisitor(ServerChatHack server, ChatHackContext context) {
		this.server = Objects.requireNonNull(server);
		this.context = Objects.requireNonNull(context);
	}

	@Override 
	public void visit(DBResponseFrameRead frame) {
		server.updateDBRequest(frame.getId(), frame.getResponse());
	}
	
	/**
	 * Gets the context
	 * @return the context
	 */
	public ChatHackContext getContext() {
		return context;
	}
}
