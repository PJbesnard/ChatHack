package fr.upem.net.tcp.nonblocking.reader;

/**
 * The Reader interface represents ByteBuffer reader and allows to process them for checking if 
 * informations they contains are valid and provides methods to get informations decoded and reset the reader
 */
public interface Reader {

	/**
	 * Represents a process state:
	 * DONE, the process have been completed without any error
	 * REFILL, the process can't be done because informations are missing
	 * ERROR, an error has been found 
	 */
    public static enum ProcessStatus {DONE,REFILL,ERROR};

    /**
	 * Read the ByteBuffer
	 * @return the process status
	 */
    public ProcessStatus process();

    /**
	 * Gets the Object which represents the ByteBuffer that have been proceed
	 * @return an Object corresponding to the frame read
	 */
    public Object get();

    /**
	 * Resets the reader
	 */
    public void reset();

}