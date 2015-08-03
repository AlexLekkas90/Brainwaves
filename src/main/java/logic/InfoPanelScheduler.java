package main.java.logic;

import java.util.TimerTask;

import javax.swing.JLabel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author Alexandros Lekkas
 * Scheduler class that manages the information panel of the MainView. Runs every hour and collects data on the users location, temperature etc.
 */
public class InfoPanelScheduler extends TimerTask {
	private JLabel lblTemp;
	private JLabel lblLoc;

	/**
	 * Constructor
	 * @param lblTemp the label for the temperature
	 * @param lblLoc the label for the location
	 */
	public InfoPanelScheduler(JLabel lblTemp, JLabel lblLoc) {
		super();
		this.lblLoc = lblLoc;
		this.lblTemp = lblTemp;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		String[] ipPair; // pair of 2 potential IP addresses
		ipPair = IPFetcher.fetchIP();
		String ip = "N/A"; // ip chosen
		
		System.out.println("REACHED STAGE 2.1");

		if(!ipPair[0].equals("N/A")){ // select pair
			ip = ipPair[0];
		} else if(!ipPair[1].equals("N/A")){
			ip = ipPair[1];
		}
		System.out.println("REACHED STAGE 2.2");
		
		String[] loc = {"N/A", "N/A"};// country/city pair
		double temp = -1000;
		if(ip != null && !ip.equals("N/A")){ // if IP service unnavailable do not attempt to look up location or temperature
			loc = fetchLocation(ip);
			if( loc[1] != null && !"N/A".equals(loc[1]) && loc[1].length() >= 1){// if loc service unnavailable do not attempt to look up temperature
				try{
				 temp = fetchTemperature(loc);
				}catch(NullPointerException npe){
					npe.printStackTrace();
				}
			}
		}
		
		System.out.println("REACHED STAGE 2.3");
		// TODO delete testing
		System.out.println("Loc is := " + loc[1]);
		if (loc[1] == null || "N/A".equals(loc[1]) || loc[1].length() <= 1) {
			lblLoc.setText("Location: N/A");
		} else {
			System.out.println("setting loc");
			lblLoc.setText("Location: " + loc[1]);
		}
		System.out.println("REACHED STAGE 2.4");
		if (temp == -1000) {
			lblTemp.setText("Temperature: N/A");
		} else {
			lblTemp.setText("Temperature: " + temp + " °C");
		}
		System.out.println("REACHED STAGE 2.5");
	}
	
//	private String[] fetchLocation(String ip){
//		JSONParser jsonParser = new JSONParser();
//		JSONObject json;
//		String city = "N/A"; //unrealistic loc value
//		String[] loc = {"N/A", "N/A"};// country/city pair
//		try {	
//			json = (JSONObject) jsonParser.parse(URLReader.read("http://freegeoip.net/json/" + ip));
//			loc[0] = (String) json.get("country_code");
//			loc[1] =  (String) json.get("city");
//			//TODO del
//			System.out.println(" Country is " + loc[0]);
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return loc;
//	}
	
	//fetchlocation alternate code with exception handling attempting a second location service
	private String[] fetchLocation(String ip){
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		String city = "N/A"; //unrealistic loc value
		String[] loc = {"N/A", "N/A"};// country/city pair
		try {	
			json = (JSONObject) jsonParser.parse(URLReader.read("http://freegeoip.net/json/" + ip));
			loc[0] = (String) json.get("country_code");
			loc[1] =  (String) json.get("city");
			//TODO del
			System.out.println(" Country is " + loc[0]);
			
		}catch (Exception e) {
			e.printStackTrace();
			try {	
				json = (JSONObject) jsonParser.parse(URLReader.read("http://www.telize.com/geoip/" + ip));
				loc[0] = (String) json.get("country_code");
				loc[1] =  (String) json.get("city");
				//TODO del
				System.out.println(" Country is " + loc[0]);
			}catch (Exception e2) {
				e.printStackTrace();
			}
		}
		return loc;
	}
	
	//alternate code to use with database
//	private String[] fetchLocation(String ip){
//		File database = new File("\\GeoLite2-City.mmdb");
//		String[] loc = {"N/A", "N/A"};// country/city pair
//		DatabaseReader reader = new DatabaseReader.Builder(database).build();
//
//		InetAddress ipAddress = InetAddress.getByName(ip);
//
//		CityResponse response = reader.city(ipAddress);
//
//		Country country = response.getCountry();
//		loc[0] = country.getIsoCode();      
//
//		City city = response.getCity();
//		loc[1] = city.getName();
//		return loc;
//	}
	
	//alt code using telize
//	private String[] fetchLocation(String ip){
//		JSONParser jsonParser = new JSONParser();
//		JSONObject json;
//		String city = "N/A"; //unrealistic loc value
//		String[] loc = {"N/A", "N/A"};// country/city pair
//		try {	
//			json = (JSONObject) jsonParser.parse(URLReader.read("http://www.telize.com/geoip/" + ip));
//			loc[0] = (String) json.get("country_code");
//			loc[1] =  (String) json.get("city");
//			//TODO del
//			System.out.println(" Country is " + loc[0]);
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return loc;
//	}
	
	
	
	private double fetchTemperature(String[] loc){
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		double currentTemp = -1000; //unrealistic temp value
		try {	
			json = (JSONObject) jsonParser.parse(URLReader.read("http://api.wunderground.com/api/0bc60dabe2f15156/conditions/q/" + loc[0].replaceAll(" ", "_") + "/" + loc[1].replaceAll(" ", "_") +".json"));
			JSONObject currentObsData =  (JSONObject) json.get("current_observation");//current observation data refer to above url for data formatting
			 currentTemp  =  (double) currentObsData.get("temp_c");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return currentTemp;
	}
}

