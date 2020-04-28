package fr.upem.net.tcp.nonblocking.censorship;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This class is a Chat Censorship and allows to censor bad messages
 */
public class ChatCensorship {
	
	private final HashMap<String, String> censorshipSet = new HashMap<>();

	public ChatCensorship(String fileName) throws FileNotFoundException, IOException {
		Objects.requireNonNull(fileName);
		try(FileInputStream fstream = new FileInputStream(fileName)){
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
			  censorshipSet.put(strLine, replaceToStars(strLine.length()));
			}
		} 
	}
	
	/**
	 * Returns a string which contains the desired number of star
	 * @param size an int which represents the desired number of stars
	 * @return a String whith the desired number of stars
	 */
	private String replaceToStars(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size can't be less or equals than 0");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append('*');
		}
		return sb.toString();
	}
	
	/**
	 * Gets the censored word
	 * @param string the String which will be censored if he is bad 
	 * @return a String which represents the censored word if this word is bad
	 */
	public String getCensorship(String string) {
		var newString = string;
		for (Entry<String, String> word : censorshipSet.entrySet()) {
			newString = newString.replaceAll("\\b" + "(?i)" + word.getKey() + "\\b", word.getValue());
		}
		return newString;
	}
}
