package fr.upem.net.tcp.nonblocking.contextVisitor;

import fr.upem.net.tcp.nonblocking.ClientChatHack;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateFileFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateMessageFrameRead;
import fr.upem.net.tcp.nonblocking.concretContext.ChatHackContext;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * Represents a client ConnectionContext of a frame read by using visitor pattern
 */
public class ConnectionContextFrameReadVisitor implements FrameReadVisitor {
	private final ClientChatHack client;
	private final ChatHackContext context;
	
	public ConnectionContextFrameReadVisitor(ClientChatHack client, ChatHackContext privateConnectionContext) {
		this.client = client;
		this.context = privateConnectionContext;
	}
	
	@Override
	public void visit(ClientPrivateConnectionFrameRead clientPrivateConnectionFrame) {
		System.out.println("Bad frame received. Connection closed.");
		context.silentlyClose();
	}

	@Override
	public void visit(ClientPrivateMessageFrameRead clientPrivateMessageFrame) {
		System.out.println("private message " + clientPrivateMessageFrame.getLogin() + " : "
				+ clientPrivateMessageFrame.getMessage());
	}

	@Override
	public void visit(ClientPrivateFileFrameRead frame) {
		client.createFile(frame.getFileName(), frame.getFile());
	}
}
