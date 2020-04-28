package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import fr.upem.net.tcp.nonblocking.frameRead.ConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.ConnectionWithoutPasswordFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.GlobalMessageFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.PrivateMessageAskingFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.PrivateMessageDenyFrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.PrivateMessageValidationFrameRead;
import fr.upem.net.tcp.nonblocking.reader.FrameReader;
import fr.upem.net.tcp.nonblocking.reader.Reader.ProcessStatus;

class FrameReaderTest {

	@Test
	void testShouldReturnsProcessStatusErrorBecauseFrameIsToLong() {
		var bb = ByteBuffer.allocate(1025);
		for (int i = 0; i < 1025; i++) {
			bb.put((byte) 1);
		}
		assertEquals(ProcessStatus.ERROR, new FrameReader(bb).process());
	}

	@Test
	void testShouldReturnsProcessStatusErrorBecauseOpCodeIsUnknown() {
		var bb = ByteBuffer.allocate(1025);
		bb.put((byte) 7);
		assertEquals(ProcessStatus.ERROR, new FrameReader(bb).process());
	}

	@Test
	void testShouldReturnsProcessStatusRefillBecauseByteBuffersAreUncomplete() {
		var bb1 = ByteBuffer.allocate(1).put((byte) 1);
		var bb2 = ByteBuffer.allocate(1).put((byte) 2);
		var bb3 = ByteBuffer.allocate(1).put((byte) 3);
		var bb4 = ByteBuffer.allocate(1).put((byte) 4);
		var bb5 = ByteBuffer.allocate(1).put((byte) 5);
		var bb6 = ByteBuffer.allocate(1).put((byte) 6);
		assertAll(() -> assertEquals(ProcessStatus.REFILL, new FrameReader(bb1).process()),
				() -> assertEquals(ProcessStatus.REFILL, new FrameReader(bb2).process()),
				() -> assertEquals(ProcessStatus.REFILL, new FrameReader(bb3).process()),
				() -> assertEquals(ProcessStatus.REFILL, new FrameReader(bb4).process()),
				() -> assertEquals(ProcessStatus.REFILL, new FrameReader(bb5).process()),
				() -> assertEquals(ProcessStatus.REFILL, new FrameReader(bb6).process())
		);
	}

	
	@Test
	void testShouldReturnsProcessStatusDoneBecauseByteBufferIsCompleteCase1() {
		var BBlogin = StandardCharsets.UTF_8.encode("TestMan");
		var BBpsw = StandardCharsets.UTF_8.encode("Password");
		var buff = ByteBuffer.allocate(Byte.BYTES + 2*Integer.BYTES + BBlogin.remaining() + BBpsw.remaining());
		buff.put((byte) 1).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBpsw.remaining()).put(BBpsw);
		var frameReader = new FrameReader(buff);
		assertAll(() -> assertEquals(ProcessStatus.DONE, frameReader.process()), 
				() -> assertTrue(frameReader.get() instanceof ConnectionFrameRead)
		);
	}
	
	@Test
	void testShouldReturnsProcessStatusDoneBecauseByteBufferIsCompleteCase2() {
		var login = "TestMan";
		var BBlogin = StandardCharsets.UTF_8.encode(login);
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES + BBlogin.remaining());
		buff.put((byte) 2).putInt(BBlogin.remaining()).put(BBlogin);
		var frameReader = new FrameReader(buff);
		assertAll(() -> assertEquals(ProcessStatus.DONE, frameReader.process()), 
				() -> assertTrue(frameReader.get() instanceof ConnectionWithoutPasswordFrameRead)
		);
	}
	
	
	@Test
	void testShouldReturnsProcessStatusDoneBecauseByteBufferIsCompleteCase3() {
		var BBlogin = StandardCharsets.UTF_8.encode("TestMan");
		var BBmessage = StandardCharsets.UTF_8.encode("Hello word");
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES * 2 + BBlogin.remaining() + BBmessage.remaining());
		buff.put((byte) 3).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		var frameReader = new FrameReader(buff);
		assertAll(() -> assertEquals(ProcessStatus.DONE, frameReader.process()), 
				() -> assertTrue(frameReader.get() instanceof GlobalMessageFrameRead)
		);
	}
	
	@Test
	void testShouldReturnsProcessStatusDoneBecauseByteBufferIsCompleteCase4() {
		var BBsenderLogin = StandardCharsets.UTF_8.encode("TestMan1");
		var BBreceiverLogin = StandardCharsets.UTF_8.encode("TestMan2");
		var buff = ByteBuffer.allocate(Byte.BYTES + 2 * Integer.BYTES + BBsenderLogin.remaining() + BBreceiverLogin.remaining());
		buff.put((byte) 4).putInt(BBsenderLogin.remaining()).put(BBsenderLogin).putInt(BBreceiverLogin.remaining()).put(BBreceiverLogin);
		var frameReader = new FrameReader(buff);
		assertAll(() -> assertEquals(ProcessStatus.DONE, frameReader.process()), 
				() -> assertTrue(frameReader.get() instanceof PrivateMessageAskingFrameRead)
		);
	}
	
	@Test
	void testShouldReturnsProcessStatusDoneBecauseByteBufferIsCompleteCase5() {
		var inetAddress = InetAddress.getLoopbackAddress();
		var address = inetAddress.getAddress();
		var BBsender = StandardCharsets.UTF_8.encode("TestMan1");
		var BBreceiver = StandardCharsets.UTF_8.encode("TestMan2");
		var buff = ByteBuffer.allocate(Byte.BYTES * 2 + Integer.BYTES * 3 + BBsender.remaining() + BBreceiver.remaining() + Long.BYTES + address.length);
		buff.put((byte) 5).putInt(BBsender.remaining()).put(BBsender).putInt(BBreceiver.remaining()).
			put(BBreceiver).putInt(7777).put((byte) 1).put(inetAddress.getAddress()).putLong(3);
		var frameReader = new FrameReader(buff);
		assertAll(() -> assertEquals(ProcessStatus.DONE, frameReader.process()), 
				() -> assertTrue(frameReader.get() instanceof PrivateMessageValidationFrameRead)
		);
	}
	
	
	@Test
	void testShouldReturnsProcessStatusDoneBecauseByteBufferIsCompleteCase6() {
		var BBsender = StandardCharsets.UTF_8.encode("TestMan1");
		var BBreceiver = StandardCharsets.UTF_8.encode("TestMan2");
		var buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES * 2 + BBsender.remaining() + BBreceiver.remaining());
		buff.put((byte) 6).putInt(BBsender.remaining()).put(BBsender).putInt(BBreceiver.remaining()).put(BBreceiver);
		var frameReader = new FrameReader(buff);
		assertAll(() -> assertEquals(ProcessStatus.DONE, frameReader.process()), 
				() -> assertTrue(frameReader.get() instanceof PrivateMessageDenyFrameRead)
		);
	}

	

}
