package fr.upem.net.tcp.nonblocking;

import fr.upem.net.tcp.nonblocking.concretContext.ContextType;

/**
 * This interface represents a context manager which implements a method which allow to treat deconnections
 */
public interface ContextManager {

	public void disconnectedClient(String login, ContextType contexttype);
	
}
