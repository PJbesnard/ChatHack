package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateFileFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateFrameRead.ClientPrivateMessageFrameRead;
import fr.upem.net.tcp.nonblocking.clientPrivateReader.PrivateMessageReader;
import fr.upem.net.tcp.nonblocking.reader.Reader.ProcessStatus;

class PrivateMessageReaderTest {

	@Test
	void testShouldReturnsProcessStatusErrorBecauseFrameIsToLong() {
		var bb = ByteBuffer.allocate(1025);
		for(int i = 0; i < 1025; i++) {
			bb.put((byte) 1);
		}
		assertEquals(ProcessStatus.ERROR, new PrivateMessageReader(bb).process());
	}
	
	@Test
	void testShouldReturnsProcessStatusErrorBecauseOpcodeIsUnknown() {
		var bb = ByteBuffer.allocate(1024);
		bb.put((byte) 98);
		assertEquals(ProcessStatus.ERROR, new PrivateMessageReader(bb).process());
	}
	
	@Test
	void testShouldReturnsProcessStatusRefill() {
		var bb1 = ByteBuffer.allocate(1024).put((byte) 7);
		var bb2 = ByteBuffer.allocate(1024).put((byte) 8);
		var bb3 = ByteBuffer.allocate(1024).put((byte) 9);
		assertAll(
				() -> assertEquals(ProcessStatus.REFILL, new PrivateMessageReader(bb1).process()),
				() -> assertEquals(ProcessStatus.REFILL, new PrivateMessageReader(bb2).process()),
				() -> assertEquals(ProcessStatus.REFILL, new PrivateMessageReader(bb3).process())
		);
	}
	
	@Test
	void testShouldReturnsProcessStatusDone() {
		long l = 9000;
		var BBlogin = StandardCharsets.UTF_8.encode("Bernard");
		var BBmessage = StandardCharsets.UTF_8.encode("coucou");
		var bb1 = ByteBuffer.allocate(1024).put((byte) 7).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		var bb2 = ByteBuffer.allocate(1024).put((byte) 8).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage).putInt(BBmessage.remaining()).put(BBmessage);
		var bb3 = ByteBuffer.allocate(1024).put((byte) 9).putInt(BBlogin.remaining()).put(BBlogin).putLong(l);
		
		assertAll(
				() -> assertEquals(ProcessStatus.DONE, new PrivateMessageReader(bb1).process()),
				() -> assertEquals(ProcessStatus.DONE, new PrivateMessageReader(bb2).process()),
				() -> assertEquals(ProcessStatus.DONE, new PrivateMessageReader(bb3).process())
		);
	}
	
	@Test
	void testShouldReturnsCorrectInstanceInGetMethod() {
		long l = 9000;
		var BBlogin = StandardCharsets.UTF_8.encode("Bernard");
		var BBmessage = StandardCharsets.UTF_8.encode("coucou");
		var bb1 = ByteBuffer.allocate(1024).put((byte) 7).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		var bb2 = ByteBuffer.allocate(1024).put((byte) 8).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage).putInt(BBmessage.remaining()).put(BBmessage);
		var bb3 = ByteBuffer.allocate(1024).put((byte) 9).putInt(BBlogin.remaining()).put(BBlogin).putLong(l);
		
		var t1 = new PrivateMessageReader(bb1);
		var t2 = new PrivateMessageReader(bb2);
		var t3 = new PrivateMessageReader(bb3);
		t1.process();
		t2.process();
		t3.process();
		assertAll(
				() -> assertTrue(t1.get() instanceof ClientPrivateMessageFrameRead),
				() -> assertTrue(t2.get() instanceof ClientPrivateFileFrameRead),
				() -> assertTrue(t3.get() instanceof ClientPrivateConnectionFrameRead)
		);
	}
}
