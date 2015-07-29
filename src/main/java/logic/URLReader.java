package main.java.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * @author Alexandros Lekkas
 * Class that reads a url from a url String and returns the url data as a String
 */
public class URLReader {
	
	
	/** 
	 * Read the url provided as a String and return the contents as a String
	 * @param urlStr the url to be read represented as a String
	 * @return the url contents as a String
	 * @throws Exception
	 */
	public static String read(String urlStr) throws Exception {
			URL website = new URL(urlStr);
	        URLConnection con = website.openConnection();
	        BufferedReader input = new BufferedReader(
	                                new InputStreamReader(
	                                    con.getInputStream()));
	        StringBuilder response = new StringBuilder();
	        String inputLine;
	        inputLine = input.readLine();
	        while (inputLine != null){ 
	            response.append(inputLine);
	            inputLine = input.readLine();
	        }
	        input.close();

	        return response.toString();
	}
	
	
}
