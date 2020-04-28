package fr.upem.net.tcp.nonblocking.frameRead;

import fr.upem.net.tcp.nonblocking.DBframeRead.DBResponseFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientConnectedFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientDenyPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientGlobalMessageFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientLoginAEFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPLErrorFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPacketErrorFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPrivateMessageValidationFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPrivateMessageAskingFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientValidationPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateFileFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateMessageFrameRead;

/*
 * The FrameRedVisitor interface represents a visitor that will be accepted by a FrameRed object to treat it.
 * Each methods are defined by default and contains a non side effect code
 */
public interface FrameReadVisitor {

	/**
	 * This method allows to treat a client connection without password frame
	 * @param frame the FrameRead
	 */
	default public void visit(ConnectionWithoutPasswordFrameRead frame) {
		return;
	}
	
	/**
	 * This method allows to treat a client connection frame
	 * @param frame the FrameRead
	 */
	default public void visit(ConnectionFrameRead frame) {
		return;
	}
	
	/**
	 * This method allows to treat a client global message frame
	 * @param frame the FrameRead
	 */
	default public void visit(GlobalMessageFrameRead frame) {
		return;
	}
	
	/**
	 * This method allows to treat a client private message deny frame
	 * @param frame the FrameRead
	 */
	default public void visit(PrivateMessageDenyFrameRead frame) {
		return;
	}

	/**
	 * This method allows to treat a client private message request frame
	 * @param frame the FrameRead
	 */
	default public void visit(PrivateMessageAskingFrameRead frame) {
		return;
	}

	/**
	 * This method allows to treat a client private message validation frame
	 * @param frame the FrameRead
	 */
	default public void visit(PrivateMessageValidationFrameRead frame) {
		return;
	}
	
	/**
	 * This method allows to treat a database response frame
	 * @param frame the FrameRead
	 */
	default public void visit(DBResponseFrameRead frame) {
		return;
	}

	/**
	 * This method allows to treat a server private connection request frame 
	 * @param frame the FrameRead
	 */
	default public void visit(ClientPrivateConnectionFrameRead frame) {
		return;
	}

	/**
	 * This method allows to treat a client private message frame 
	 * @param frame the FrameRead
	 */
	default public void visit(ClientPrivateMessageFrameRead frame) {
		return;
	}

	/**
	 * This method allows to treat a client private file frame 
	 * @param frame the FrameRead
	 */
	default public void visit(ClientPrivateFileFrameRead frame) {
		return;
	}

	/**
	 * This method allows to treat a server error packet frame 
	 * @param frame the FrameRead
	 */
	default public void visit(ClientPacketErrorFrameRead frame) {
		return;
	}

	/**
	 * This method allows to treat a server password or login error frame 
	 * @param frame the FrameRead
	 */
	default public void visit(ClientPLErrorFrameRead frame) {
		return;
	}

	/*
	 * This method allows to treat a server connection accepted frame
	 * @param frame the FrameRead
	 */
	default public void visit(ClientConnectedFrameRead frame) {
		return;
	}

	/*
	 * This method allows to treat a server frame that means already existing login
	 * @param frame the FrameRead
	 */
	default public void visit(ClientLoginAEFrameRead frame) {
		return;
	}
	
	/*
	 * This method allows to treat a server global message frame
	 * @param frame the FrameRead
	 */
	default public void visit(ClientGlobalMessageFrameRead frame) {
		return;
	}

	/*
	 * This method allows to treat a server private message asking frame
	 * @param frame the FrameRead
	 */
	default public void visit(ClientPrivateMessageAskingFrameRead frame) {
		return;
	}

	/*
	 * This method allows to treat a server private message validation frame
	 * @param frame the FrameRead
	 */
	default public void visit(ClientValidationPrivateConnectionFrameRead frame) {
		return;
	}

	/*
	 * This method allows to treat a private message deny frame
	 * @param frame the FrameRead
	 */
	default public void visit(ClientDenyPrivateConnectionFrameRead frame) {
		return;
	}

	/*
	 * This method allows to treat a private message validation frame
	 * @param frame the FrameRead
	 */
	default public void visit(ClientPrivateMessageValidationFrameRead frame) {
		return;
	}

}
