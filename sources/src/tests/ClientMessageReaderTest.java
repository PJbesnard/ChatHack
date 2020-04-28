package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientConnectedFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientDenyPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientGlobalMessageFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientLoginAEFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPLErrorFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPacketErrorFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientPrivateMessageAskingFrameRead;
import fr.upem.net.tcp.nonblocking.clientFrameRead.ClientValidationPrivateConnectionFrameRead;
import fr.upem.net.tcp.nonblocking.clientReader.ClientFrameReader;
import fr.upem.net.tcp.nonblocking.reader.Reader.ProcessStatus;

class ClientMessageReaderTest {

	@Test
	void testShouldReturnsProcessStatusErrorBecauseFrameIsToLong() {
		var bb = ByteBuffer.allocate(1025);
		for(int i = 0; i < 1025; i++) {
			bb.put((byte) 1);
		}
		assertEquals(ProcessStatus.ERROR, new ClientFrameReader(bb).process());
	}
	
	@Test
	void testShouldReturnsProcessStatusErrorBecauseOpcodeIsUnknown() {
		var bb = ByteBuffer.allocate(1024);
		bb.put((byte) 98);
		assertEquals(ProcessStatus.ERROR, new ClientFrameReader(bb).process());
	}
	
	@Test
	void testShouldReturnsProcessStatusRefill() {
		var bb5 = ByteBuffer.allocate(1024).put((byte) 5);
		var bb6 = ByteBuffer.allocate(1024).put((byte) 6);
		var bb7 = ByteBuffer.allocate(1024).put((byte) 7);
		var bb8 = ByteBuffer.allocate(1024).put((byte) 8);
		assertAll(
				() -> assertEquals(ProcessStatus.REFILL, new ClientFrameReader(bb5).process()),
				() -> assertEquals(ProcessStatus.REFILL, new ClientFrameReader(bb6).process()),
				() -> assertEquals(ProcessStatus.REFILL, new ClientFrameReader(bb7).process()),
				() -> assertEquals(ProcessStatus.REFILL, new ClientFrameReader(bb8).process())
		);
	}
	
	@Test
	void testShouldReturnsProcessStatusDone() {
		var inetAdress = InetAddress.getLoopbackAddress();
		var address = inetAdress.getAddress();
		long l = 9000;
		var BBlogin = StandardCharsets.UTF_8.encode("Bernard");
		var BBmessage = StandardCharsets.UTF_8.encode("coucou");
		var bb1 = ByteBuffer.allocate(1024).put((byte) 1);
		var bb2 = ByteBuffer.allocate(1024).put((byte) 2);
		var bb3 = ByteBuffer.allocate(1024).put((byte) 3);
		var bb4 = ByteBuffer.allocate(1024).put((byte) 4);
		var bb5 = ByteBuffer.allocate(1024).put((byte) 5).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		var bb6 = ByteBuffer.allocate(1024).put((byte) 6).putInt(BBlogin.remaining()).put(BBlogin);
		var bb7 = ByteBuffer.allocate(1024).put((byte) 7).putInt(BBlogin.remaining()).put(BBlogin).putInt(7777).put((byte) 1).put(address).putLong(l);
		var bb8 = ByteBuffer.allocate(1024).put((byte) 8).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBlogin.remaining()).put(BBlogin);
		
		assertAll(
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb1).process()),
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb2).process()),
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb3).process()),
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb4).process()),
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb5).process()),
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb6).process()),
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb7).process()),
				() -> assertEquals(ProcessStatus.DONE, new ClientFrameReader(bb8).process())
		);
	}
	
	@Test
	void testShouldReturnsCorrectInstanceInGetMethod() {
		var inetAdress = InetAddress.getLoopbackAddress();
		var address = inetAdress.getAddress();
		long l = 9000;
		var BBlogin = StandardCharsets.UTF_8.encode("Bernard");
		var BBmessage = StandardCharsets.UTF_8.encode("coucou");
		var bb1 = ByteBuffer.allocate(1024).put((byte) 1);
		var bb2 = ByteBuffer.allocate(1024).put((byte) 2);
		var bb3 = ByteBuffer.allocate(1024).put((byte) 3);
		var bb4 = ByteBuffer.allocate(1024).put((byte) 4);
		var bb5 = ByteBuffer.allocate(1024).put((byte) 5).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBmessage.remaining()).put(BBmessage);
		var bb6 = ByteBuffer.allocate(1024).put((byte) 6).putInt(BBlogin.remaining()).put(BBlogin);
		var bb7 = ByteBuffer.allocate(1024).put((byte) 7).putInt(BBlogin.remaining()).put(BBlogin).putInt(7777).put((byte) 1).put(address).putLong(l);
		var bb8 = ByteBuffer.allocate(1024).put((byte) 8).putInt(BBlogin.remaining()).put(BBlogin).putInt(BBlogin.remaining()).put(BBlogin);
		
		var t1 = new ClientFrameReader(bb1);
		var t2 = new ClientFrameReader(bb2);
		var t3 = new ClientFrameReader(bb3);
		var t4 = new ClientFrameReader(bb4);
		var t5 = new ClientFrameReader(bb5);
		var t6 = new ClientFrameReader(bb6);
		var t7 = new ClientFrameReader(bb7);
		var t8 = new ClientFrameReader(bb8);
		t1.process();
		t2.process();
		t3.process();
		t4.process();
		t5.process();
		t6.process();
		t7.process();
		t8.process();
		assertAll(
				() -> assertTrue(t1.get() instanceof ClientPacketErrorFrameRead),
				() -> assertTrue(t2.get() instanceof ClientPLErrorFrameRead),
				() -> assertTrue(t3.get() instanceof ClientConnectedFrameRead),
				() -> assertTrue(t4.get() instanceof ClientLoginAEFrameRead),
				() -> assertTrue(t5.get() instanceof ClientGlobalMessageFrameRead),
				() -> assertTrue(t6.get() instanceof ClientPrivateMessageAskingFrameRead),
				() -> assertTrue(t7.get() instanceof ClientValidationPrivateConnectionFrameRead),
				() -> assertTrue(t8.get() instanceof ClientDenyPrivateConnectionFrameRead)
		);
	}
}
