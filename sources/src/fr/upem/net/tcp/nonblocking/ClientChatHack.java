package fr.upem.net.tcp.nonblocking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.upem.net.tcp.nonblocking.clientFrame.ClientConnectionFrame;
import fr.upem.net.tcp.nonblocking.clientFrame.ClientConnectionWithPswFrame;
import fr.upem.net.tcp.nonblocking.clientFrame.ClientDenyConnectionFrame;
import fr.upem.net.tcp.nonblocking.clientFrame.ClientGlobalMessageFrame;
import fr.upem.net.tcp.nonblocking.clientFrame.ClientPrivateConnectionEtablishementFrame;
import fr.upem.net.tcp.nonblocking.clientFrame.ClientPrivateMessageAcceptationFrame;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientConnectedFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientDenyPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientGlobalMessageFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientLoginAEFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPLErrorFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPacketErrorFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPrivateMessageValidationFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPrivateMessageAskingFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientValidationPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrame.ClientPrivateConnectionFrame;
import fr.upem.net.tcp.nonblocking.clientPrivateFrame.ClientPrivateFileFrame;
import fr.upem.net.tcp.nonblocking.clientPrivateReader.PrivateMessageReader;
import fr.upem.net.tcp.nonblocking.clientReader.ClientFrameReader;
import fr.upem.net.tcp.nonblocking.concretContext.ChatHackContext;
import fr.upem.net.tcp.nonblocking.concretContext.ContextFactory;
import fr.upem.net.tcp.nonblocking.concretContext.ContextType;
import fr.upem.net.tcp.nonblocking.console.AcceptConnection;
import fr.upem.net.tcp.nonblocking.console.CloseConnection;
import fr.upem.net.tcp.nonblocking.console.ConsoleMessage;
import fr.upem.net.tcp.nonblocking.console.ConsoleMessageVisitor;
import fr.upem.net.tcp.nonblocking.console.DeclineConnection;
import fr.upem.net.tcp.nonblocking.console.GlobalMessageConsole;
import fr.upem.net.tcp.nonblocking.console.PrivateFile;
import fr.upem.net.tcp.nonblocking.console.PrivateMessage;
import fr.upem.net.tcp.nonblocking.contextVisitor.ClientServerContextFrameReadVisitor;
import fr.upem.net.tcp.nonblocking.contextVisitor.ConnectionContextFrameReadVisitor;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;
import fr.upem.net.tcp.nonblocking.reader.Reader;

/**
 * Represents a client which will be able to be connected to a server or another client. 
 * Orders are processed in a console. 
 */
public class ClientChatHack implements ConsoleMessageVisitor, ContextManager {
	class NonBlockingClientFrameRedVisitor implements FrameReadVisitor {
		private final ClientChatHack client;
		private final ChatHackContext context;
		
		public NonBlockingClientFrameRedVisitor(ClientChatHack client, ChatHackContext context) {
			this.client = client;
			this.context = context;
		}

		@Override
		public void visit(ClientPacketErrorFrameRead clientPacketErrorFrame) {
			System.out.println("Packet Error");
			Thread.currentThread().interrupt();
		}

		@Override
		public void visit(ClientPLErrorFrameRead clientPLErrorReader) {
			System.out.println("Password or login Error");
			Thread.currentThread().interrupt();
		}

		@Override
		public void visit(ClientConnectedFrameRead clientConnectedFrame) {
			System.out.println("Connected to the server");
		}

		@Override
		public void visit(ClientLoginAEFrameRead clientLoginAEFrame) {
			System.out.println("Login already exist");
			Thread.currentThread().interrupt();
		}

		@Override
		public void visit(ClientGlobalMessageFrameRead clientGlobalMessageReceivedFrame) {
			System.out.println(
					clientGlobalMessageReceivedFrame.getLogin() + " : " + clientGlobalMessageReceivedFrame.getMessage());
		}

		@Override
		public void visit(ClientPrivateMessageAskingFrameRead clientPrivateMessageAskingFrame) {
			var login = clientPrivateMessageAskingFrame.getLogin();
			privateConnectionWaiters.add(login);
			System.out.println("private connection asking from " + login);
			System.out.println("tap +" + login + " for accept the connection");
			System.out.println("tap -" + login + " for deny the connection");
		}
		

		@Override
		public void visit(ClientValidationPrivateConnectionFrameRead clientValidationPrivateConnectionFrame) {
			var login = clientValidationPrivateConnectionFrame.getLogin();
			ChatHackContext privateConnectionContext;
			connectionInWaiting.remove(login);

			try {
				var userAdress = new InetSocketAddress(InetAddress.getByAddress(clientValidationPrivateConnectionFrame.getAddress()), clientValidationPrivateConnectionFrame.getPort());
				var sc = SocketChannel.open();
				sc.connect(userAdress);
				sc.configureBlocking(false);
				var key = sc.register(selector, SelectionKey.OP_CONNECT);

				privateConnectionContext = factory.getContext("Connection", client, key, login);
				clientsContexts.put(login, privateConnectionContext);
				key.attach(privateConnectionContext);
				privateConnectionContext.queueMessage(new ClientPrivateConnectionFrame(client.getLogin(), clientValidationPrivateConnectionFrame.getId()).get());
				if (msgInWaiting.containsKey(login)) {
					privateConnectionContext.queueMessage(msgInWaiting.get(login));
					msgInWaiting.remove(login);
				}
				if (fileInWaiting.containsKey(login)) {
					sendingFile(privateConnectionContext, fileInWaiting.get(login));
					fileInWaiting.remove(login);
				}
			} catch (IOException e) {
				System.out.println(e);
			}
			System.out.println(login + " accepted your connection request");
		}

		@Override
		public void visit(ClientDenyPrivateConnectionFrameRead clientDenyPrivateConnectionFrame) {
			var login = clientDenyPrivateConnectionFrame.getLogin();
			System.out.println("connection denied by " + login);
			msgInWaiting.remove(login);
			fileInWaiting.remove(login);
			connectionInWaiting.remove(login);
		}

		@Override
		public void visit(ClientPrivateMessageValidationFrameRead clientPrivateMessageAcceptationFrame) {
			System.out.println("bad frame");
			Thread.currentThread().interrupt();
		}
		
		ChatHackContext getContext() {
			return context;
		}
	}

	static int BUFFER_SIZE = 1_024;
	static private Logger logger = Logger.getLogger(ServerChatHack.class.getName());
	private final String login;
	private final String mdp;
	private final InetSocketAddress serverAddress;
	private ServerSocketChannel serverSocketChannel;
	private final Selector selector;
	private int serverPort = -1;
	private final String path;
	private final Lock lock = new ReentrantLock();
	private ChatHackContext context;
	private final HashMap<String, ByteBuffer> msgInWaiting = new HashMap<String, ByteBuffer>();
	private final HashMap<String, ByteBuffer> fileInWaiting = new HashMap<String, ByteBuffer>();
	private final HashSet<String> privateConnectionWaiters = new HashSet<String>();
	private final BlockingQueue<ConsoleMessage> consoleQueue = new LinkedBlockingQueue<ConsoleMessage>();
	private final HashSet<String> connectionInWaiting = new HashSet<String>();
	private final HashMap<String, ChatHackContext> clientsContexts = new HashMap<String, ChatHackContext>();
	private byte[] address;
	private final Map<String, Long> privateConnectionClients = new HashMap<>();
	private final ContextFactory<ClientChatHack> factory;
	private final SocketChannel sc;
	
	public ClientChatHack(String path, String login, String host, int port) throws IOException {
		this.login = login;
		this.mdp = null;
		this.path = path;
		sc = SocketChannel.open();
		serverAddress = new InetSocketAddress(host, port);
		selector = Selector.open();
		var inetAddress = InetAddress.getLoopbackAddress();
		address = inetAddress.getAddress();
		factory = initFactory();
	}

	public ClientChatHack(String path, String login, String mdp, String host, int port) throws IOException {
		this.login = login;
		this.path = path;
		this.mdp = mdp;
		sc = SocketChannel.open();
		serverAddress = new InetSocketAddress(host, port);
		selector = Selector.open();
		var inetAddress = InetAddress.getLoopbackAddress();
		address = inetAddress.getAddress();
		factory = initFactory();
	}
	
	/**
	 * Initiates a new Context Factory which will create contexts associated with the client
	 * @return a new ContextFactory of NonBlockingClient 
	 */
	private ContextFactory<ClientChatHack> initFactory(){
		Map<String, ContextType> types = Map.of("ClientServer", ContextType.CLIENTSERVERCONTEXT, "Connection", ContextType.CONNECTIONCONTEXT, "NonBlockingClient", ContextType.CLIENTCONTEXT);
		Map<String, BiFunction<ClientChatHack, ChatHackContext, FrameReadVisitor>> visitorConstructors;
		visitorConstructors = Map.of("ClientServer", ClientServerContextFrameReadVisitor::new, "Connection", ConnectionContextFrameReadVisitor::new, "NonBlockingClient", NonBlockingClientFrameRedVisitor::new);
		Map<String, Function<ByteBuffer, Reader>> frameReaderConstructors;
		frameReaderConstructors = Map.of("ClientServer", PrivateMessageReader::new, "Connection", PrivateMessageReader::new, "NonBlockingClient", ClientFrameReader::new);
		return new ContextFactory<ClientChatHack>(types, visitorConstructors, frameReaderConstructors);
	}
	
	/**
	 * Gets the login
	 * @return a String corresponding to the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Checks if the id of the sender login corresponds to an existing private connection
	 * @param login the login which corresponds to a private connection
	 * @param id the id to be checked
	 * @return true if the id corresponds to an existing connection, false either
	 */
	public boolean checkIdLoginSender(String login, long id) {
		if (privateConnectionClients.containsKey(login)) {
			if (privateConnectionClients.get(login) == id) {
				privateConnectionClients.remove(login);
				return true;
			}
		}
		return false;
	}

	/**
	 * Starts the user console Thread
	 */
	public void console() {
		Thread thread = new Thread(() -> {
			while (!Thread.interrupted()) {
				try (Scanner scan = new Scanner(System.in)) {
					while (scan.hasNextLine()) {
						lock.lock();
						try {
							var str = scan.nextLine();
							if("".equals(str)) {
								continue;
							}
							consoleQueue.put(new ConsoleMessage(str));
							selector.wakeup();
						} catch (InterruptedException e) {
							System.out.println("Error while reading keyboard input... Interuption");
							Thread.currentThread().interrupt();
						} finally {
							lock.unlock();
						}
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Creates and open a server
	 * @throws IOException if the creation/opening failed
	 */
	private void openServer() throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(null);
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		serverPort = serverSocketChannel.socket().getLocalPort();
	}

	/**
	 * Accepts a new connection with a login
	 * @param login the login to which a new connection will be created
	 * @throws IOException if creation of the connection failed
	 */
	private void acceptConnection(String login) throws IOException {
		if (serverPort == -1) {
			openServer();
		}
		long id = new Random().nextLong();
		if (privateConnectionClients.containsKey(login)) {
			return;
		}
		privateConnectionClients.put(login, id);
		context.queueMessage(new ClientPrivateMessageAcceptationFrame(this.login, login, serverPort, address, id).get());
	}
	
	/**
	 * Creates a new File
	 * @param fileName the name of the file which will be created
	 * @param file the file data
	 */
	public void createFile(String fileName, byte[] file) {
		File imageFile = new File(path, "received_" + fileName);
		try (OutputStream output = new FileOutputStream(imageFile)) {
			output.write(file);
		} catch (Exception e) {
			System.out.println("\n Error while reading message \n");
			return;
		}
		System.out.println("Private file received placed in " + path + " with name received_" + fileName);
	}
	
	/**
	 * Sends a file
	 * @param context the context which correspond to the connection
	 * @param frame the frame corresponding to the file
	 */
	private void sendingFile(ChatHackContext context, ByteBuffer frame) {
		var bb = frame;
		if(bb.capacity() <= BUFFER_SIZE) {
			context.queueMessage(frame);
		}
		else {
			bb.flip();
			var lim = bb.limit();
			var actualLim = BUFFER_SIZE;
			while(actualLim < lim) {
				bb.limit(actualLim);
				var bbSend = ByteBuffer.allocate(BUFFER_SIZE);
				bbSend.put(bb);
				context.queueMessage(bbSend);
				bb.position(actualLim);
				actualLim += BUFFER_SIZE;
			}
			bb.limit(lim);
			var bbSend = ByteBuffer.allocate(bb.remaining());
			bbSend.put(bb);
			context.queueMessage(bbSend);
		}
	}
	
	/**
	 * Treats a message frame by adding it in the correct queue
	 * @param receiverLogin the receiver login of the frame
	 * @param frame the frame which will be send
	 */
	private void treatMsgFrame(String receiverLogin, ByteBuffer frame) {
		if(clientsContexts.containsKey(receiverLogin)){
			clientsContexts.get(receiverLogin).queueMessage(frame);
		}
		else {
			connectionInWaiting.add(receiverLogin);
			context.queueMessage(new ClientPrivateConnectionEtablishementFrame(login, receiverLogin).get());
			msgInWaiting.put(receiverLogin, frame);
		}
	}
	
	/**
	 * Treats a file frame by adding it in the correct queue
	 * @param receiverLogin the receiver login of the frame
	 * @param frame the frame which will be send
	 */
	private void treatFileFrame(String receiverLogin, ByteBuffer frame) {
		if(clientsContexts.containsKey(receiverLogin)){
			sendingFile(clientsContexts.get(receiverLogin), frame);
		}
		else {
			connectionInWaiting.add(receiverLogin);
			context.queueMessage(new ClientPrivateConnectionEtablishementFrame(login, receiverLogin).get());
			fileInWaiting.put(receiverLogin, frame);
		}
	}
	
	/**
	 * Treats a frame by adding it in the correct queue
	 * @param receiverLogin the receiver login of the frame
	 * @param frame the frame which will be send
	 */
	private void treatFrame(String receiverLogin, ByteBuffer frame) {
		var pos = frame.position();
		frame.flip();
		var b = frame.get();
		frame.position(pos);
		if(b == (byte)7) {
			treatMsgFrame(receiverLogin, frame);
		}
		else if(b == (byte)8) {
			treatFileFrame(receiverLogin, frame);
		}
		else {
			System.out.println("Invalid Frame Error");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Establish a connection
	 * @param receiverLogin the receiver login
	 * @param frame the frame which will be send
	 */
	private void connectionEtablishement(String receiverLogin, ByteBuffer frame) {
		if (receiverLogin.equals(this.login)) {
			System.out.println("You can't etablish a private connection with yourself");
			return;
		}
		if (connectionInWaiting.contains(receiverLogin)) {
			System.out.println("you already ask for this connection");
			return;
		}
		treatFrame(receiverLogin, frame);
	}
	
	/**
	 * Treats the console queue
	 * @throws IOException if the operation failed
	 */
	private void treatConsoleQueue() throws IOException {
		lock.lock();
		try {
			if (consoleQueue.isEmpty()) {
				return;
			}
			var frame = consoleQueue.poll().treat(login);
			if(frame == null) {
				return;
			}
			frame.accept(this);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Adds a connection frame to the queue
	 */
	public void connection() {
		ByteBuffer bbConnect;
		if (mdp == null) {
			bbConnect = new ClientConnectionFrame(login).get();
		} else {
			bbConnect = new ClientConnectionWithPswFrame(login, mdp).get();
		}
		context.queueMessage(bbConnect);
	}
	
	/**
	 * Adds a new context to the context HashMap
	 * @param login the login to which the connection was created
	 * @param context the context
	 */
	public void addNewContext(String login, ChatHackContext context) {
		clientsContexts.put(login, context);
	}

	/**
	 * Launch the client
	 * @throws IOException if the launch failed
	 */
	public void launch() throws IOException {
		try {
			sc.configureBlocking(true);
			sc.connect(serverAddress);
			sc.configureBlocking(false);
			var key = sc.register(selector, SelectionKey.OP_WRITE);
			context = factory.getContext("NonBlockingClient", this, key, login);
			key.attach(context);
			connection();
		} catch (Exception e) {
			System.out.println("Unable to connect to the ServerChatHack, you should check if the server is online");
			return;
		}
		System.out.println("Client launched");
		while (!Thread.interrupted()) {
			//printKeys(); // for debug
			try {
				selector.select(this::treatKey);
				treatConsoleQueue();
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
			if (key.isValid() && key.isConnectable()) {
				doConnect(key);
			}
		} catch (Exception e) {
			System.out.println("Unable to connect to the server, you should try if the server is online");
			silentlyClose(key);
		}
		try {
			if (key.isValid() && key.isAcceptable()) {
				doAccept(key);
			}
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
		try {
			if (key.isValid() && key.isWritable()) {
				((ChatHackContext) key.attachment()).doWrite();
			}
			if (key.isValid() && key.isReadable()) {
				((ChatHackContext) key.attachment()).doRead();
			}
		} catch (IOException e) {
			logger.log(Level.INFO, "Connection closed with client due to IOException", e);
			silentlyClose(key);
		}
	}
	
	/**
	 * Adds a ClientServerContext to the clients context HashMap
	 * @param login the login to which the connection was created
	 * @param csc the context
	 */
	public void addClientServerContext(String login, ChatHackContext csc) {
		clientsContexts.put(login, csc);
	}

	/**
	 * Accepts a new connection on the specified key
	 * @param key the key 
	 * @throws IOException if the acceptance failed
	 */
	public void doAccept(SelectionKey key) throws IOException {
		SocketChannel sc = serverSocketChannel.accept();
		if (sc == null) {
			return;
		}
		sc.configureBlocking(false);
		var s = sc.register(selector, SelectionKey.OP_READ);
		var newCtxt = factory.getContext("ClientServer", this, s, null);
		s.attach(newCtxt);
	}
	
	/**
	 * Connect the specified key
	 * @param key the key
	 * @throws IOException if the connection failed
	 */
	public void doConnect(SelectionKey key) throws IOException {
        var sc = (SocketChannel) key.channel();
        if (!sc.finishConnect()) {
            return;
        }
        ((ChatHackContext) key.attachment()).doWrite();
    }

	/**
	 * Silently Close the key
	 * @param key the key
	 */
	private void silentlyClose(SelectionKey key) {
		Channel sc = (Channel) key.channel();
		try {
			System.out.println("disconnected from server...");
			sc.close();
		} catch (IOException e) {
			// ignore exception
		}
	}
	
	/**
	 * Treat client disconnection in the specified context type
	 * @param loginContext the login
	 * @param contextType the context type
	 */
	@Override
	public void disconnectedClient(String loginContext, ContextType contextType) {
		if(contextType.equals(ContextType.CONNECTIONCONTEXT) || contextType.equals(ContextType.CLIENTSERVERCONTEXT)) {
			System.out.println("connection closed with " + loginContext);
			clientsContexts.remove(loginContext);
		}
		else if(contextType.equals(ContextType.CLIENTCONTEXT)) {
			System.out.println("disconnected from server");
			clientsContexts.remove(login);
		}
		else {
			throw new IllegalArgumentException("unexpected value " + contextType);
		}
	}
	
	/**
	 * Prints the command usage
	 */
	private static void usage() {
		System.out.println("Usage : Client path login host port");
		System.out.println("Usage : Client path login password host port");
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		ClientChatHack client = null;
		try {
			if (args.length == 4) {
				client = new ClientChatHack(args[0], args[1], args[2], Integer.parseInt(args[3]));
			} else if (args.length == 5) {
				client = new ClientChatHack(args[0], args[1], args[2], args[3], Integer.parseInt(args[4]));
			} else {
				usage();
				return;
			}
		} catch (Exception e) {
			System.out.println("Invalid argument");
			usage();
			return;
		}
		client.console();
		client.launch();
		
	}
	
	@Override
	public void consoleVisit(PrivateMessage privateMessage) {
		connectionEtablishement(privateMessage.getLogin(), privateMessage.getFrame().get());
	}

	@Override
	public void consoleVisit(AcceptConnection acceptConnection) {
		var login = acceptConnection.getLogin();
		if (privateConnectionWaiters.contains(login)) {
			privateConnectionWaiters.remove(login);
			try {
				acceptConnection(login);
			} catch (IOException e) {
				Thread.currentThread().interrupt();
			}
		} else {
			System.out.println("this user isn't asking for a private connection");
		}
	}

	@Override
	public void consoleVisit(DeclineConnection declineConnection) {
		var login1 = declineConnection.getLoginDecline();
		if (privateConnectionWaiters.contains(login1)) {
			privateConnectionWaiters.remove(login1);
			context.queueMessage(new ClientDenyConnectionFrame(this.login, login1).get());
		} else {
			System.out.println("this user isn't asking for a private connection");
		}
	}

	@Override
	public void consoleVisit(PrivateFile privateFile) {
		var login = privateFile.getLogin();
		var fileName = privateFile.getFileName();
		byte[] file = null;
		try {
			file = Files.readAllBytes(Paths.get(path + "/" + fileName));
		} catch (Exception e) {
			System.out.println("can't read file");
			return;
		}
		connectionEtablishement(login, new ClientPrivateFileFrame(this.login, fileName, file).get());
	}

	@Override
	public void consoleVisit(GlobalMessageConsole globalMessage) {
		context.queueMessage(new ClientGlobalMessageFrame(this.login, globalMessage.getMsg()).get());
	}

	@Override
	public void consoleVisit(CloseConnection closeConnection) {
		var login = closeConnection.getLogin();
		if(clientsContexts.containsKey(login)) {
			clientsContexts.get(login).silentlyClose();
		}
		else {
			System.out.println("this connection doesn't exist");
		}
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
		if ((interestOps & SelectionKey.OP_CONNECT) != 0)
			list.add("OP_CONNECT");
		if ((interestOps & SelectionKey.OP_READ) != 0)
			list.add("OP_READ");
		if ((interestOps & SelectionKey.OP_WRITE) != 0)
			list.add("OP_WRITE");
		return String.join("|", list);
	}

	/**
	 * Prints the keys
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