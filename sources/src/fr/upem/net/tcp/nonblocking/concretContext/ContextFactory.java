package fr.upem.net.tcp.nonblocking.concretContext;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import fr.upem.net.tcp.nonblocking.ContextManager;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * Allows to create a factory of Contexts
 * @param <T> a type extends of a ContextManager
 */
public class ContextFactory<T extends ContextManager> {

	
	private final Map<String, ContextType> types;
	private final Map<String, BiFunction<T, ChatHackContext, FrameReadVisitor>> visitorConstructors;
	private final Map<String, Function<ByteBuffer, Reader>> frameReaderConstructors;
	
	/**
	 * Checks if elements of the specified map aren't null
	 * @param <K> the key type
	 * @param <V> the value type
	 * @param map the map
	 */
	private <K, V> void checksNullObject(Map<K, V> map) {
		for (var entry : map.entrySet()) {
			Objects.requireNonNull(entry.getKey());
			Objects.requireNonNull(entry.getValue());
		}
	}
	
	public ContextFactory(Map<String, ContextType> types, 
			 Map<String, BiFunction<T, ChatHackContext, FrameReadVisitor>> visitorConstructors, Map<String, Function<ByteBuffer, Reader>> frameReaderConstructors) {
		this.types = Objects.requireNonNull(types);
		this.visitorConstructors = Objects.requireNonNull(visitorConstructors);
		this.frameReaderConstructors = Objects.requireNonNull(frameReaderConstructors);
		checksNullObject(types);
		checksNullObject(visitorConstructors);
		checksNullObject(frameReaderConstructors);
		if (!checksEntryMatchs(types, visitorConstructors, frameReaderConstructors)) {
			throw new IllegalArgumentException("Error during ContextFactory construction, string are not matching between Maps");
		}
	}
	
	/**
	 * Checks if specified parameters matches
	 * @param types the map of types
	 * @param visitorConstructors the map of visitor constructors
	 * @param frameReaderConstructors the map of frameReader constructors
	 * @return true if specified parameters matches, false either
	 */
	private boolean checksEntryMatchs(Map<String, ContextType> types,
			Map<String, BiFunction<T, ChatHackContext, FrameReadVisitor>> visitorConstructors,
			Map<String, Function<ByteBuffer, Reader>> frameReaderConstructors) {
		if (types.size() != visitorConstructors.size() || types.size() != frameReaderConstructors.size()) {
			return false;
		}
		for (String key : types.keySet()) {
			if ((!visitorConstructors.containsKey(key)) || (!frameReaderConstructors.containsKey(key))) {
				return false;
			}
		}
		return true;
		
	}

	/**
	 * Creates a Context
	 * @param contextType the context type
	 * @param manager the manager of the context
	 * @param key the key
	 * @param login the login
	 * @return a ChatHackContext which correspond to the desired context
	 */
	private ChatHackContext createContext(String contextType, T manager, SelectionKey key, String login) {
		var bbin = ByteBuffer.allocate(1024);
		var context = new ChatHackContext(manager, key, login, types.get(contextType), bbin, frameReaderConstructors.get(contextType).apply(bbin));
		var visitor = visitorConstructors.get(contextType).apply(manager, context);
		context.setVisitor(visitor);
		return context;
	}
	
	/**
	 * Gets a Context
	 * @param contextType the context type
	 * @param manager the manager of the context
	 * @param key the key
	 * @param login the login
	 * @return a ChatHackContext which correspond to the desired context
	 */
	public ChatHackContext getContext(String contextType, T manager, SelectionKey key, String login) {
		Objects.requireNonNull(contextType);
		Objects.requireNonNull(manager);
		Objects.requireNonNull(key);
		return createContext(contextType, manager, key, login);
	}



	
}