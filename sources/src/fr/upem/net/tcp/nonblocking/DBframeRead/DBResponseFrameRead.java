package fr.upem.net.tcp.nonblocking.DBframeRead;

import java.util.Objects;

import fr.upem.net.tcp.nonblocking.frameRead.FrameRead;
import fr.upem.net.tcp.nonblocking.frameRead.FrameReadVisitor;


/**
 * This class allows to get informations from a DataBase Response Frame that have been read by the server
 */
public class DBResponseFrameRead implements FrameRead{
	private final byte response;
	private final long id; 
	
	public DBResponseFrameRead(byte response, long id) {
		this.response = Objects.requireNonNull(response);
		this.id = id;
	}
	
	/**
	 * Get the id
	 * @return a long which represents the id
	 */
	public long getId(){
		return id;
	}
	
	/**
	 * Get the response
	 * @return true if the response is true, false either
	 */
	public boolean getResponse() {
		return response == 1 ? true : false;
	}


	@Override
	public void accept(FrameReadVisitor visitor) {
		visitor.visit(this);	
	}

}
