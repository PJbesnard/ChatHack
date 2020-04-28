package fr.upem.net.tcp.nonblocking.contextVisitor;

import java.util.Objects;
import java.util.Random;

import fr.upem.net.tcp.nonblocking.ServerChatHack;
import fr.upem.net.tcp.nonblocking.concretContext.ChatHackContext;
import fr.upem.net.tcp.nonblocking.frame.ConnectionResponseFrame;
import fr.upem.net.tcp.nonblocking.frame.DBAskingFrame;
import fr.upem.net.tcp.nonblocking.frameRead.ConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.ConnectionWithoutPasswordFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;

/**
 * Represents a server ServerContextNI frame read by using visitor pattern
 */
public class ServerContextNIFrameReadVisitor implements FrameReadVisitor{
	
	private final ServerChatHack server;
	private final ChatHackContext context;
	
	public ServerContextNIFrameReadVisitor(ServerChatHack server, ChatHackContext context) {
		this.server = Objects.requireNonNull(server);
		this.context = Objects.requireNonNull(context);
	}
	
	@Override
	public void visit(ConnectionWithoutPasswordFrameRead frame) {
		var login = frame.getLogin();
		if (server.loginConnected(login) || !server.dataBaseOnline()) {
			context.queueMessage(new ConnectionResponseFrame("badlog").get());
			return;
		}
		context.setLogin(frame.getLogin());
		var id = Math.abs(new Random().nextLong());
		server.addDBRequest(new DBAskingFrame(id, frame).get(), context, id, 1);
	}

	@Override
	public void visit(ConnectionFrameRead frame) {
		context.setLogin(frame.getLogin());
		if (!server.dataBaseOnline()) {
			context.queueMessage(new ConnectionResponseFrame("badlog").get());
			return;
		}
		var id = Math.abs(new Random().nextLong());
		server.addDBRequest(new DBAskingFrame(id, frame).get(), context, id, 0);
	}
}
