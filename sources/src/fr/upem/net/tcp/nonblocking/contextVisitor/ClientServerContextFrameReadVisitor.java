package fr.upem.net.tcp.nonblocking.contextVisitor;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.ClientChatHack;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateFileFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateMessageFrameRead;
import fr.upem.net.tcp.nonblocking.concretContext.ChatHackContext;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * Represents a client ServerContext frame read by using visitor pattern
 */
public class ClientServerContextFrameReadVisitor implements FrameReadVisitor {
	private final ClientChatHack client;
	private final ChatHackContext context;
	
	public ClientServerContextFrameReadVisitor(ClientChatHack client, ChatHackContext context) {
		this.client = Objects.requireNonNull(client);
		this.context = Objects.requireNonNull(context);
	}
	
	@Override
	public void visit(ClientPrivateConnectionFrameRead frame) {
		if (client.checkIdLoginSender(frame.getLogin(), frame.getId())) {
			context.setLogin(frame.getLogin());
			client.addNewContext(frame.getLogin(), context);
			context.getReader().reset();
			context.processIn();
		} else {
			context.silentlyClose();
		}
		System.out.println("connection established");
	}

	@Override
	public void visit(ClientPrivateMessageFrameRead clientPrivateMessageFrame) {
		if (context.getLogin() == null) {
			context.silentlyClose();
			return;
		}
		if (context.getLogin().equals(clientPrivateMessageFrame.getLogin())) {
			System.out.println("private message " + context.getLogin() + " : " + clientPrivateMessageFrame.getMessage());
		} else {
			context.silentlyClose();
		}
	}

	@Override
	public void visit(ClientPrivateFileFrameRead frame) {
		client.createFile(frame.getFileName(), frame.getFile());
	}
}
