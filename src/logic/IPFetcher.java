package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.validator.routines.InetAddressValidator;

/**
 * @author Alexandros Lekkas
 * Class that retrieves a valid users IP public address, the class validates the IP so either a valid IP or a special message is returned
 */
public class IPFetcher {

	/**
	 * @return a String representing a valid IP or "NULL" in case of an invalid IP
	 * @throws Exception
	 */
	public static String[] fetchIP() {
		String[] ip = {"N/A", "N/A"};
		URL url1;
		URL url2;
        BufferedReader in = null;
        try {
        	 url1 = new URL("http://checkip.amazonaws.com");
    		 url2 = new URL("http://www.trackip.net/ip");
            in = new BufferedReader(new InputStreamReader(
                    url1.openStream()));
             ip[0] = in.readLine();
             if(!isValidIP(ip[0])){
             	
             in = new BufferedReader(new InputStreamReader(
                     url2.openStream()));
              ip[1] = in.readLine();
             }
        }catch (Exception e){
        	e.printStackTrace();//TODO add alternate IP retrieval here
        	 try {
        		 url2 = new URL("http://www.trackip.net/ip");
        		 in = new BufferedReader(new InputStreamReader(
                         url2.openStream()));
                  ip[1] = in.readLine();
             } catch(Exception e2){
             }
        
        }finally {
            if(!isValidIP(ip[0])){
            	ip[0] = "N/A";
            }
        	if(!isValidIP(ip[1])){
        		ip[1] = "N/A";
        	}
            if (in != null) {
                try {
                    in.close();//close the bufferedreader
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return ip;
	}
    
        
     
        

	
/**
 * Validates a provided IP address
 * @param ip the address to be validated
 * @return true if the address is a valid IP address
 */
private static boolean isValidIP(String ip){
	return InetAddressValidator.getInstance().isValid(ip);
}
	
	
}
