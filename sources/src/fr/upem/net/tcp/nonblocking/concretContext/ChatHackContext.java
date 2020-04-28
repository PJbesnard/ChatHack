package fr.upem.net.tcp.nonblocking.concretContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import fr.upem.net.tcp.nonblocking.ContextManager;
import fr.upem.net.tcp.nonblocking.contextVisitor.NeutralFrameReadVisitor;
import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;
import fr.upem.net.tcp.nonblocking.reader.Reader;

public class ChatHackContext implements Context {
	private SelectionKey key;
	SocketChannel sc;
	private final ByteBuffer bbin;
	private final ByteBuffer bbout;
	private final Queue<ByteBuffer> queue = new LinkedList<>();
	private final ContextManager contextManager;
	private boolean closed = false;
	private Reader frameReader;
	private String login;
	private final ContextType contexttype;
	private FrameReadVisitor visitor = new NeutralFrameReadVisitor();

	ChatHackContext(ContextManager contextManager, SelectionKey key, String login, ContextType type, ByteBuffer bbin,
			Reader frameReader) {
		this.key = Objects.requireNonNull(key);
		this.sc = (SocketChannel) key.channel();
		this.contextManager = Objects.requireNonNull(contextManager);
		this.contexttype = Objects.requireNonNull(type);
		this.bbin = Objects.requireNonNull(bbin);
		this.login = login;
		this.bbout = ByteBuffer.allocate(bbin.capacity());
		this.frameReader = Objects.requireNonNull(frameReader);
	}


	/**
	 * Performs the read action on sc
	 *
	 * The convention is that both buffers are in write-mode before the call to
	 * doRead and after the call
	 *
	 * @throws IOException if I/O exceptions occurred during SocketChannel.read method
	 */
	public void doRead() throws IOException {
		if (-1 == sc.read(bbin)) {
			closed = true;
		}
		processIn();
		if (key.isValid()) {
			updateInterestOps();
		}
	}

	/**
	 * Performs the write action on sc
	 *
	 * The convention is that both buffers are in write-mode before the call to
	 * doWrite and after the call
	 *
	 * @throws IOException if I/O exceptions occurred during SocketChannel.write method
	 */
	public void doWrite() throws IOException {
		bbout.flip();
		sc.write(bbout);
		bbout.compact();
		processOut();
		updateInterestOps();
	}

	/**
	 * Process the content of bbin
	 *
	 * The convention is that bbin is in write-mode before the call to process and
	 * after the call
	 *
	 */
	public void processIn() {
		for (;;) {
			Reader.ProcessStatus status = frameReader.process();
			switch (status) {
			case DONE:
				var frame = (FrameRead) frameReader.get();
				frame.accept(visitor);
				frameReader.reset();
				return;
			case REFILL:
				return;
			case ERROR:
				silentlyClose();
				return;
			}
		}
	}

	/**
	 * Try to fill bbout from the message queue
	 *
	 */
	public void processOut() {
		while (!queue.isEmpty() && bbout.remaining() >= queue.peek().capacity()) {
			var bb = queue.poll();
			bb.flip();
			bbout.put(bb);
		}
	}

	/**
	 * Updates the interestOps of the key looking only at values of the boolean
	 * closed and of both ByteBuffers.
	 *
	 * The convention is that both buffers are in write-mode before the call to
	 * updateInterestOps and after the call. Also it is assumed that process has
	 * been be called just before updateInterestOps.
	 */
	public void updateInterestOps() {
		int newInterestOps = 0;
		if (bbin.hasRemaining() && !closed) {
			newInterestOps |= SelectionKey.OP_READ;
		}
		if (bbout.position() > 0 && !closed) {
			newInterestOps |= SelectionKey.OP_WRITE;
		}
		if (newInterestOps == 0) {
			silentlyClose();
		} else {
			key.interestOps(newInterestOps);
		}
	}

	/**
	 * Adds a message to the message queue, tries to fill bbOut and updateInterestOps
	 *
	 * @param frame The ByteBuffer which contains the frame
	 */
	public void queueMessage(ByteBuffer frame) {
		queue.add(frame);
		processOut();
		updateInterestOps();
	}

	public void silentlyClose() {
		try {
			sc.close();
			contextManager.disconnectedClient(login, contexttype);
		} catch (IOException e) {
			// ignore exception
		}
	}

	/**
	 * Disconnects the context
	 */
	public void disconnect() {
		closed = true;
		updateInterestOps();
	}

	/**
	 * Gets the key
	 * @return the key
	 */
	public SelectionKey getKey() {
		return key;
	}

	/**
	 * Gets the SocketChannel
	 * @return the SocketChannel
	 */
	public SocketChannel getsc() {
		return sc;
	}

	/**
	 * Gets the login
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Gets the reader
	 * @return the reader
	 */
	public Reader getReader() {
		return frameReader;
	}

	/**
	 * Sets the login
	 * @param login the new login
	 */
	public void setLogin(String login) {
		this.login = Objects.requireNonNull(login);
	}

	/**
	 * Gets the ContextType
	 * @return the ContextType
	 */
	public ContextType getContextType() {
		return contexttype;
	}

	/**
	 * Sets the key
	 * @param key the new key
	 */
	public void setKey(SelectionKey key) {
		this.key = Objects.requireNonNull(key);
	}

	/**
	 * Sets the visitor
	 * @param visitor the new visitor
	 */
	public void setVisitor(FrameReadVisitor visitor) {
		this.visitor = Objects.requireNonNull(visitor);
	}
}
