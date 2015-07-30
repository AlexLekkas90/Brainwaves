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
	public static String read(String urlStr) {
		StringBuilder response = new StringBuilder();
		URLConnection con;
		
		URL website;
		try{
			 website = new URL(urlStr);
	         con = website.openConnection();
	        BufferedReader input = new BufferedReader(
	                                new InputStreamReader(
	                                    con.getInputStream()));
	        
	        String inputLine;
	        inputLine = input.readLine();
	        while (inputLine != null){ 
	            response.append(inputLine);
	            inputLine = input.readLine();
	        }
	        input.close();
		}catch(Exception e){
			e.printStackTrace();
		} 

	        return response.toString();
	}
	
	
}
