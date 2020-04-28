package fr.upem.net.tcp.nonblocking;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import fr.upem.net.tcp.nonblocking.DBreader.DBResponseReader;
import fr.upem.net.tcp.nonblocking.censorship.ChatCensorship;
import fr.upem.net.tcp.nonblocking.concretContext.ChatHackContext;
import fr.upem.net.tcp.nonblocking.concretContext.Context;
import fr.upem.net.tcp.nonblocking.concretContext.ContextFactory;
import fr.upem.net.tcp.nonblocking.concretContext.ContextType;
import fr.upem.net.tcp.nonblocking.contextVisitor.ClientDataBaseFrameReadVisitor;
import fr.upem.net.tcp.nonblocking.contextVisitor.ServerContextFrameReadVisitor;
import fr.upem.net.tcp.nonblocking.contextVisitor.ServerContextNIFrameReadVisitor;
import fr.upem.net.tcp.nonblocking.frame.ConnectionResponseFrame;
import fr.upem.net.tcp.nonblocking.frame.Frame;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;
import fr.upem.net.tcp.nonblocking.reader.FrameReader;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * Represents a server which will be able to accept connections and treats frames received
 */
public class ServerChatHack implements ContextManager {

	static int BUFFER_SIZE = 1_024;
	static private Logger logger = Logger.getLogger(ServerChatHack.class.getName());
	private final ServerSocketChannel serverSocketChannel;
	private final Selector selector;
	private final SocketChannel databaseSocketChannel;
	private final InetSocketAddress dataBaseAddress;
	private final Map<String, ChatHackContext> connectedClients = new HashMap<>();
	private ChatHackContext dataBaseContext;
	private boolean dataBaseInOnline = true;
	private final Map<Long, Entry<ChatHackContext, Integer>> DBRequests = new HashMap<>();
	private final Map<String, HashSet<String>> privateChatRequests = new HashMap<>();
	private final ContextFactory<ServerChatHack> factory;

	private final ChatCensorship chatCensorShip;


	public ServerChatHack(int port, int dbport, String dbaddress, String moderationFile) throws IOException {
		Objects.requireNonNull(dbaddress);
		if (port < 0 || dbport < 0) {
			throw new IllegalArgumentException();
		}
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		selector = Selector.open();
		databaseSocketChannel = SocketChannel.open();
		dataBaseAddress = new InetSocketAddress(dbaddress, dbport);
		factory = initFactory();
		Objects.requireNonNull(moderationFile);
		ChatCensorship censorship;
		try {
			censorship = new ChatCensorship(moderationFile);
		}catch (FileNotFoundException e) {
			System.out.println("File path : " + moderationFile + " not found, default file has been loaded as censorship file");
			censorship = new ChatCensorship("censure.txt");
		}catch (IOException e2) {
			System.out.println("Error encountered during file opening, default file has been loaded as censorship file");
			censorship = new ChatCensorship("censure.txt");
		}
		chatCensorShip = censorship;
		
	}
	
	public ServerChatHack(int port, int dbport, String dbaddress) throws IOException {
		this(port, dbport, dbaddress, "censure.txt");
	}

	/**
	 * Initiates a new Context Factory which will create contexts associated with the client
	 * @return a new ContextFactory of ServerChatHack 
	 */
	private ContextFactory<ServerChatHack> initFactory() {
		Map<String, ContextType> types = Map.of("BDD", ContextType.SERVERCONTEXTDATABASE, "ClientNI",
				ContextType.SERVERCONTEXTNOTIDENTIFIED, "Client", ContextType.SERVERCONTEXT);
		Map<String, BiFunction<ServerChatHack, ChatHackContext, FrameReadVisitor>> visitorConstructors;
		visitorConstructors = Map.of("BDD", ClientDataBaseFrameReadVisitor::new, "ClientNI",
				ServerContextNIFrameReadVisitor::new, "Client", ServerContextFrameReadVisitor::new);
		Map<String, Function<ByteBuffer, Reader>> frameReaderConstructors;
		frameReaderConstructors = Map.of("BDD", DBResponseReader::new, "ClientNI", FrameReader::new, "Client",
				FrameReader::new);
		return new ContextFactory<ServerChatHack>(types, visitorConstructors, frameReaderConstructors);
	}

	/**
	 * Treat client disconnection in the specified context type
	 * @param login the login
	 * @param contexttype the context type
	 */
	@Override
	public void disconnectedClient(String login, ContextType contexttype) {
		if (contexttype.equals(ContextType.SERVERCONTEXTDATABASE)) {
			dataBaseInOnline = false;
			return;
		}
		if (contexttype.equals(ContextType.SERVERCONTEXT) && connectedClients.containsKey(login)) {
			System.out.println("Client " + login + " disconnected");
			connectedClients.remove(login);
		}
	}

	/**
	 * Disconnects the specified login
	 * @param login the login which will be disconnected
	 */
	public void disconnectClient(String login) {
		if (connectedClients.containsKey(login)) {
			var ctxt = connectedClients.remove(login);
			ctxt.silentlyClose();
		}
	}

	/**
	 * Checks if the specified login is connected
	 * @param login the login
	 * @return true if the login is connected, false either
	 */
	public boolean loginConnected(String login) {
		return connectedClients.containsKey(login);
	}

	/**
	 * Adds Private chatters to the privateChatRequests HashMap
	 * @param login the sender login
	 * @param receiverLogin the receiver login
	 */
	public void addPrivateChatters(String login, String receiverLogin) {
		if (privateChatRequests.containsKey(login)) {
			privateChatRequests.get(login).add(receiverLogin);
		}
	}

	/**
	 * Checks if the private chat request has already been requested by the sender login
	 * @param login the sender login
	 * @param receiverLogin the receiver login
	 * @return false if the private chat request has already been requested, true either
	 */
	public boolean alreadyRequested(String login, String receiverLogin) {
		if (!privateChatRequests.containsKey(login)) {
			return false;
		}
		return privateChatRequests.get(login).contains(receiverLogin);
	}

	/**
	 * Deletes the specified private chat request
	 * @param login the login corresponding to the private chat request to be deleted
	 * @param receiverLogin the receiverLogin corresponding to the private chat request to be deleted
	 */
	public void deleteRequest(String login, String receiverLogin) {
		if (privateChatRequests.containsKey(login)) {
			privateChatRequests.get(login).remove(receiverLogin);
		}
	}

	/**
	 * Checks if the database is online
	 * @return true if the database is online, false either
	 */
	public boolean dataBaseOnline() {
		return dataBaseInOnline;
	}

	/**
	 * Adds a database request to the database requests list
	 * @param frame the frame
	 * @param context the context of the request
	 * @param id the id 
	 * @param status the status
	 */
	public void addDBRequest(ByteBuffer frame, ChatHackContext context, long id, int status) {
		DBRequests.put(id, new SimpleEntry<ChatHackContext, Integer>(context, status));
		dataBaseContext.queueMessage(frame);
		var key = context.getKey();
		key.cancel();
	}

	/**
	 * Treats and attach a context for a database validation
	 * @param key the key to which the context will be associated
	 * @param context the context
	 * @throws ClosedChannelException if the channel has been closed
	 */
	private void dbValidation(SelectionKey key, ChatHackContext context) throws ClosedChannelException {
		System.out.println("Client " + context.getLogin() + " logged");
		var newContext = factory.getContext("Client", this, key, context.getLogin());
		key.attach(newContext);
		privateChatRequests.put(context.getLogin(), new HashSet<>());
		disconnectClient(context.getLogin());
		registerClient(context.getLogin(), newContext);
		newContext.queueMessage(new ConnectionResponseFrame("good").get());
	}

	/**
	 * Treats the database deny operation
	 * @param key the key to which the context will be associated
	 * @param context the context
	 * @param frame the frame which will be added to the queue
	 * @throws ClosedChannelException if the channel closed
	 */
	private void dbDeny(SelectionKey key, ChatHackContext context, Frame frame) throws ClosedChannelException {
		context.setKey(key);
		key.attach(context);
		context.queueMessage(frame.get());
	}

	/**
	 * Updates the specified database request
	 * @param id the if which corresponds to a request
	 * @param response the database response
	 */
	public void updateDBRequest(Long id, boolean response) {
		var entry = DBRequests.remove(id);
		response = entry.getValue() == 1 ? !response : response;
		try {
			var key = entry.getKey().getsc().register(selector, SelectionKey.OP_WRITE);
			if (response) {
				dbValidation(key, entry.getKey());
				return;
			}
			Frame frame;
			if (entry.getValue() == 1) {
				frame = new ConnectionResponseFrame("logtaken");
			} else {
				frame = new ConnectionResponseFrame("badlog");
			}
			dbDeny(key, entry.getKey(), frame);
		} catch (ClosedChannelException e) {
			entry.getKey().silentlyClose();
			return;
		}
	}

	/**
	 * Launch the server
	 * @throws IOException if the launch failed
	 */
	public void launch() throws IOException {
		System.out.println("Server launched");
		try {
			databaseSocketChannel.configureBlocking(true);
			databaseSocketChannel.connect(dataBaseAddress);
			databaseSocketChannel.configureBlocking(false);
			var key = databaseSocketChannel.register(selector, SelectionKey.OP_WRITE);
			dataBaseContext = factory.getContext("BDD", this, key, "BDD");
			key.attach(dataBaseContext);
		} catch (IOException e) {
			System.out.println("DataBase connexion failed, please check if the database server is online");
			return;
		}
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		while (!Thread.interrupted()) {
			// printKeys();
			try {
				selector.select(this::treatKey);
			} catch (UncheckedIOException tunneled) {
				throw tunneled.getCause();
			}
		}
	}

	/**
	 * Treats the key
	 * @param key the key which will be treated
	 */
	private void treatKey(SelectionKey key) {
		//printSelectedKey(key); // for debug
		try {
			if (key.isValid() && key.isAcceptable()) {
				doAccept(key);
			}
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
		try {
			if (key.isValid() && key.isWritable()) {
				((Context) key.attachment()).doWrite();
			}
			if (key.isValid() && key.isReadable()) {
				((Context) key.attachment()).doRead();
			}
		} catch (IOException e) {
			logger.log(Level.INFO, "Connection closed with client due to IOException", e);
			silentlyClose(key);
		}
	}

	/**
	 * Accepts a new connection on the specified key
	 * @param key the key 
	 * @throws IOException if the acceptance failed
	 */
	private void doAccept(SelectionKey key) throws IOException {
		SocketChannel sc = serverSocketChannel.accept();
		if (sc == null) {
			return;
		}
		sc.configureBlocking(false);
		var s = sc.register(selector, SelectionKey.OP_READ);
		var newCtxt = factory.getContext("ClientNI", this, s, "Undefined");
		s.attach(newCtxt);
		newCtxt.setVisitor(new ServerContextNIFrameReadVisitor(this, newCtxt));
	}

	/**
	 * Silently Close the key
	 * @param key the key
	 */
	private void silentlyClose(SelectionKey key) {
		Channel sc = (Channel) key.channel();
		try {
			sc.close();
		} catch (IOException e) {
			// ignore exception
		}
	}
	
	/**
	 * Censors the specified message
	 * @param message the message
	 * @return a String which correspond the censored message
	 */
	public String censure(String message) {
		return chatCensorShip.getCensorship(message);
	}

	/**
	 * Adds a message to login connected clients queue
	 * @param frame the frame contained the message
	 * @param login the login
	 */
	public void sendToUser(ByteBuffer frame, String login) {
		Objects.requireNonNull(frame);
		Objects.requireNonNull(login);
		if (connectedClients.containsKey(login)) {
			var ctxt = connectedClients.get(login);
			ctxt.queueMessage(frame);
		}
	}

	/**
	 * Register the specified login
	 * @param login the login which will be register
	 * @param context the context
	 */
	public void registerClient(String login, ChatHackContext context) {
		connectedClients.put(Objects.requireNonNull(login), Objects.requireNonNull(context));
	}

	/**
	 * Adds a message to all connected clients queue
	 * @param frame the frame contained the message
	 * @param message the message
	 * @param login the login
	 */
	public void broadcast(ByteBuffer frame, String message, String login) {
		System.out.println(login + " : " + message);
		for (ChatHackContext ctxt : connectedClients.values()) {
			ctxt.queueMessage(frame);
		}
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		try {
			if (args.length == 3) {
				new ServerChatHack(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]).launch();
			} else if (args.length == 4) {
				new ServerChatHack(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3]).launch();
			} else {
				usage();
				return;
			}
		} catch (Exception e) {
			System.out.println("Invalid arguments");
			usage();
		}
	}

	/**
	 * Prints usage of this class
	 */
	private static void usage() {
		System.out.println("Usage :\n\nServerChatHack ->\n - Port DataBasePort DataBaseAddress CensorshipPathFiles\n - Port DataBasePort DataBaseAddress");
	}

	/* -------------------------------------- */
	/* --------- debugging methods  --------- */
	/* -------------------------------------- */

	/**
	 * Creates a String which represents the interestOps of the corresponding key
	 * @param key the key
	 * @return a String which represents the interestOps of the corresponding key
	 */
	private String interestOpsToString(SelectionKey key) {
		if (!key.isValid()) {
			return "CANCELLED";
		}
		int interestOps = key.interestOps();
		ArrayList<String> list = new ArrayList<>();
		if ((interestOps & SelectionKey.OP_ACCEPT) != 0)
			System.out.println("New connexion accepted");
			list.add("OP_ACCEPT");
		if ((interestOps & SelectionKey.OP_READ) != 0)
			list.add("OP_READ");
		if ((interestOps & SelectionKey.OP_WRITE) != 0)
			list.add("OP_WRITE");
		return String.join("|", list);
	}

	/**
	 * Prints keys
	 */
	public void printKeys() {
		Set<SelectionKey> selectionKeySet = selector.keys();
		if (selectionKeySet.isEmpty()) {
			System.out.println("The selector contains no key : this should not happen!");
			return;
		}
		System.out.println("The selector contains:");
		for (SelectionKey key : selectionKeySet) {
			SelectableChannel channel = key.channel();
			if (channel instanceof ServerSocketChannel) {
				System.out.println("\tKey for ServerSocketChannel : " + interestOpsToString(key));
			} else {
				SocketChannel sc = (SocketChannel) channel;
				System.out.println("\tKey for Client " + remoteAddressToString(sc) + " : " + interestOpsToString(key));
			}
		}
	}

	/**
	 * Creates a String which represents a remote address to a String
	 * @param sc the SocketChannel
	 * @return a String which represents a remote address to a String
	 */
	private String remoteAddressToString(SocketChannel sc) {
		try {
			return sc.getRemoteAddress().toString();
		} catch (IOException e) {
			return "???";
		}
	}

	/**
	 * Prints the specified key
	 * @param key the key
	 */
	public void printSelectedKey(SelectionKey key) {
		SelectableChannel channel = key.channel();
		if (channel instanceof ServerSocketChannel) {
			System.out.println("\tServerSocketChannel can perform : " + possibleActionsToString(key));
		} else {
			SocketChannel sc = (SocketChannel) channel;
			System.out.println(
					"\tClient " + remoteAddressToString(sc) + " can perform : " + possibleActionsToString(key));
		}
	}

	/**
	 * Creates a String which represents the possible actions on a key
	 * @param key the key
	 * @return a String which represents the possible actions on a key
	 */
	private String possibleActionsToString(SelectionKey key) {
		if (!key.isValid()) {
			return "CANCELLED";
		}
		ArrayList<String> list = new ArrayList<>();
		if (key.isAcceptable())
			list.add("ACCEPT");
		if (key.isReadable())
			list.add("READ");
		if (key.isWritable())
			list.add("WRITE");
		return String.join(" and ", list);
	}
}
