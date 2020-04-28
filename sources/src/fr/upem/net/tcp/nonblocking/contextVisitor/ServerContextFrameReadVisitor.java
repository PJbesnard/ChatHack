package fr.upem.net.tcp.nonblocking.contextVisitor;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.ServerChatHack;
import fr.upem.net.tcp.nonblocking.concretContext.ChatHackContext;
import fr.upem.net.tcp.nonblocking.frame.GlobalMessageFrame;
import fr.upem.net.tcp.nonblocking.frame.PrivateMessageAskingFrame;
import fr.upem.net.tcp.nonblocking.frame.PrivateMessageDenyFrame;
import fr.upem.net.tcp.nonblocking.frame.PrivateMessageValidationFrame;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;
import fr.upem.net.tcp.nonblocking.frameRead.GlobalMessageFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.PrivateMessageAskingFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.PrivateMessageDenyFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.PrivateMessageValidationFrameRead;

/**
 * Represents a server ServerContext frame read by using visitor pattern
 */
public class ServerContextFrameReadVisitor implements FrameReadVisitor{

	private final ServerChatHack server;
	private final ChatHackContext context;
	
	public ServerContextFrameReadVisitor(ServerChatHack server, ChatHackContext context) {
		this.server = Objects.requireNonNull(server);
		this.context = Objects.requireNonNull(context);
	}
	
	/**
	 * Checks if the login is valid
	 * @param login the login which will be checked
	 * @return true if the login is valid, false either
	 */
	private boolean checkLogin(String login) {
		if (login.equals(login)) {
			return true;
		}
		context.silentlyClose();
		return false;
	}
	
	@Override
	public void visit(GlobalMessageFrameRead frame) {
		if (checkLogin(frame.getLogin())) {
			var message = server.censure(frame.getMessage());
			server.broadcast(new GlobalMessageFrame(frame.getLogin(), message).get(), message, context.getLogin());
		}
	}

	@Override
	public void visit(PrivateMessageDenyFrameRead frame) {
		if (checkLogin(frame.getSenderLogin())
				&& server.alreadyRequested(frame.getReceiverLogin(), context.getLogin())) {
			server.deleteRequest(frame.getReceiverLogin(), context.getLogin());
			server.sendToUser(new PrivateMessageDenyFrame(frame.getSenderLogin(), frame.getReceiverLogin()).get(),
					frame.getReceiverLogin());
		}

	}

	@Override
	public void visit(PrivateMessageAskingFrameRead frame) {
		if (checkLogin(frame.getSenderLogin()) && !server.alreadyRequested(context.getLogin(), frame.getReceiverLogin())) {
			server.addPrivateChatters(context.getLogin(), frame.getReceiverLogin());
			server.sendToUser(new PrivateMessageAskingFrame(frame.getSenderLogin(), frame.getReceiverLogin()).get(),
					frame.getReceiverLogin());
		}
	}

	@Override
	public void visit(PrivateMessageValidationFrameRead frame) {
		if (checkLogin(frame.getSenderLogin()) && server.alreadyRequested(frame.getReceiverLogin(), context.getLogin())) {
			server.deleteRequest(frame.getReceiverLogin(), context.getLogin());
			server.sendToUser(new PrivateMessageValidationFrame(frame.getSenderLogin(), frame.getReceiverLogin(),
					frame.getPort(), frame.getAddress(), frame.getId()).get(), frame.getReceiverLogin());
		}

	}
}
