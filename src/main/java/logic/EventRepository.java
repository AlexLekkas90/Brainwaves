package main.java.logic;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * @author Alexandros Lekkas
 * Repository class that holds the events and contains various methods to gain information on the events.
 */
public class EventRepository {
	private String databaseName;
	private ArrayList<BrainwavesEvent> events;
	private ArrayList<BrainwavesEvent> pastEvents;
	private ArrayList<String> stockNames;

	/**
	 * Create a repository and fetch the events from the DB, delete past events
	 */
	public EventRepository(String databaseName) {
		events = new ArrayList<BrainwavesEvent>();
		stockNames = new ArrayList<String>();
		this.databaseName = databaseName;
//		fetchEventsFromDB();
//		removePastEvents();
	}

	/**
	 * Read events in database and load them into event objects
	 */
	public void fetchEventsFromDB() {
		events = new ArrayList<BrainwavesEvent>();
		pastEvents = new ArrayList<BrainwavesEvent>();
		stockNames = new ArrayList<String>();
		Connection c = null;
		Statement stmt = null;
		BrainwavesEvent event;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Events;");
			String name, day, date, time, location, temperature, stock, description = "";
			while (rs.next()) {
				name = rs.getString("name");
				day = rs.getString("day");
				date = rs.getString("date");
				time = rs.getString("time");
				location = rs.getString("location");
				temperature = rs.getString("temperature");
				stock = rs.getString("stock");
				description = rs.getString("description");
				event = new BrainwavesEvent(name, date, day, time, location,
						temperature, stock, description);
				
				if(!checkPastEvent(event)){ //check if event active more than 3 days ago
					events.add(event);
					//Add Stock data to repo
					if(!stock.equals("EMPTY")){
						stockNames.add(stock.split(":")[0]);
					}
				} else {
					pastEvents.add(event);
				}
				
				
			}
			rs.close();
			stmt.close();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * TODO
	 */
	public boolean checkEvent(BrainwavesEvent currentEvent, String location, double currentTemp, Map<String, Stock> stocks) {

		int conditionsLeft = currentEvent.getActiveConditions();
		Calendar currentCal = Calendar.getInstance();
		Calendar eventCal = Calendar.getInstance();
		;
		currentCal.set(Calendar.HOUR_OF_DAY, 0);
		currentCal.set(Calendar.MINUTE, 0);
		currentCal.set(Calendar.SECOND, 0);
		// Date given, no day or time given
		if (!currentEvent.getDate().equals("EMPTY")
				&& currentEvent.getDay().equals("EMPTY")
				&& currentEvent.getTime().equals("EMPTY")) {
			String[] dateString = currentEvent.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			if (eventCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)
					&& eventCal.get(Calendar.YEAR) == currentCal
							.get(Calendar.YEAR)) {
				conditionsLeft--;
			}

		}// Day given, no date or time given
		else if (currentEvent.getDate().equals("EMPTY")
				&& !currentEvent.getDay().equals("EMPTY")
				&& currentEvent.getTime().equals("EMPTY")) {
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(currentEvent.getDay()));
			if (eventCal.get(Calendar.DAY_OF_MONTH) == currentCal
					.get(Calendar.DAY_OF_MONTH)) {
				conditionsLeft--;
			}
		} // Time given, no date or day given
		else if (currentEvent.getDate().equals("EMPTY")
				&& currentEvent.getDay().equals("EMPTY")
				&& !currentEvent.getTime().equals("EMPTY")) {
			String[] timeString = currentEvent.getTime().split(":");
			String eventTimeConcat = timeString[0] + timeString[1];
			currentCal = Calendar.getInstance();
			int currentMin = currentCal.get(Calendar.MINUTE);
			String currentMinString = "" + currentMin;
			if(currentMin < 10){
				currentMinString = "0" + currentMin;
			}
			String currentTimeConcat = currentCal.get(Calendar.HOUR_OF_DAY)
					+ currentMinString;
			if (Integer.parseInt(eventTimeConcat) <= Integer
					.parseInt(currentTimeConcat)) {
				conditionsLeft--;
			}

		} // Date and day given
		else if (!currentEvent.getDate().equals("EMPTY")
				&& !currentEvent.getDay().equals("EMPTY")
				&& currentEvent.getTime().equals("EMPTY")) {
			String[] dateString = currentEvent.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(currentEvent.getDay()));
			if (eventCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)
					&& eventCal.get(Calendar.YEAR) == currentCal
							.get(Calendar.YEAR)
					&& eventCal.get(Calendar.DAY_OF_MONTH) == currentCal
							.get(Calendar.DAY_OF_MONTH)) {
				conditionsLeft = conditionsLeft - 2;
			}

		} // Date and time given
		else if (!currentEvent.getDate().equals("EMPTY")
				&& currentEvent.getDay().equals("EMPTY")
				&& !currentEvent.getTime().equals("EMPTY")) {
			String[] dateString = currentEvent.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			String[] timeString = currentEvent.getTime().split(":");
			String eventTimeConcat = timeString[0] + timeString[1];
			currentCal = Calendar.getInstance();
			int currentMin = currentCal.get(Calendar.MINUTE);
			String currentMinString = "" + currentMin;
			if(currentMin < 10){
				currentMinString = "0" + currentMin;
			}
			String currentTimeConcat = currentCal.get(Calendar.HOUR_OF_DAY)
					+ currentMinString;
			if (eventCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)
					&& eventCal.get(Calendar.YEAR) == currentCal
							.get(Calendar.YEAR)
					&& Integer.parseInt(eventTimeConcat) <= Integer
							.parseInt(currentTimeConcat)) {
				conditionsLeft = conditionsLeft - 2;
			}

		} // Day and time given
		else if (currentEvent.getDate().equals("EMPTY")
				&& !currentEvent.getDay().equals("EMPTY")
				&& !currentEvent.getTime().equals("EMPTY")) {
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(currentEvent.getDay()));
			String[] timeString = currentEvent.getTime().split(":");
			String eventTimeConcat = timeString[0] + timeString[1];
			currentCal = Calendar.getInstance();
			int currentMin = currentCal.get(Calendar.MINUTE);
			String currentMinString = "" + currentMin;
			if(currentMin < 10){
				currentMinString = "0" + currentMin;
			}
			String currentTimeConcat = currentCal.get(Calendar.HOUR_OF_DAY)
					+ currentMinString;
			if (eventCal.get(Calendar.DAY_OF_MONTH) == currentCal
					.get(Calendar.DAY_OF_MONTH)
					&& Integer.parseInt(eventTimeConcat) <= Integer
							.parseInt(currentTimeConcat)) {
				conditionsLeft = conditionsLeft - 2;
			}

		} // Date day and time given
		else if (!currentEvent.getDate().equals("EMPTY")
				&& !currentEvent.getDay().equals("EMPTY")
				&& !currentEvent.getTime().equals("EMPTY")) {
			String[] dateString = currentEvent.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(currentEvent.getDay()));
			String[] timeString = currentEvent.getTime().split(":");
			String eventTimeConcat = timeString[0] + timeString[1];
			currentCal = Calendar.getInstance();
			int currentMin = currentCal.get(Calendar.MINUTE);
			String currentMinString = "" + currentMin;
			if(currentMin < 10){
				currentMinString = "0" + currentMin;
			}
			String currentTimeConcat = currentCal.get(Calendar.HOUR_OF_DAY)
					+ currentMinString;
			if (eventCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)
					&& eventCal.get(Calendar.YEAR) == currentCal
							.get(Calendar.YEAR)
					&& eventCal.get(Calendar.DAY_OF_MONTH) == currentCal
							.get(Calendar.DAY_OF_MONTH)
					&& Integer.parseInt(eventTimeConcat) <= Integer
							.parseInt(currentTimeConcat)) {
				conditionsLeft = conditionsLeft - 3;
			}
		}
		if (!currentEvent.getLocation().equals("EMPTY")) {
			if(location == null || "N/A".equals(location)){
				return false; //there has been an error in reading the location
			}
			String eventLoc = currentEvent.getLocation();
				if(location.contains(eventLoc) || location.toLowerCase().contains(eventLoc.toLowerCase())){
					conditionsLeft = conditionsLeft - 1;
				}
		}
		if (!currentEvent.getTemperature().equals("EMPTY")) {
			if(currentTemp == -1000){
				return false; //there has been an error in reading the temperature
			}
			String[] tempString = currentEvent.getTemperature().split(":");
			String symbol = tempString[0];
			double eventTemp = Double.parseDouble(tempString[1]);
				if(symbol.equals("<")){
					if(currentTemp <= eventTemp){
						conditionsLeft = conditionsLeft -1;
					}
				} else {
					if(currentTemp >= eventTemp){
						conditionsLeft = conditionsLeft -1;
					}
				}
		}
		if(!currentEvent.getStock().equals("EMPTY")){
			if(stocks.isEmpty()){
				return false;
			}

			String stockName = currentEvent.getStock().split(":")[0];
			String stockSymbol = currentEvent.getStock().split(":")[1];
			double eventStockPrice = Double.parseDouble(currentEvent.getStock().split(":")[2]);
			Stock stock = stocks.get(stockName);

			if(stock.getQuote().getAsk().doubleValue() == 0 && stock.getQuote().getBid().doubleValue() == 0 && stock.getQuote().getPrice().doubleValue() == 0
					 && stock.getQuote().getPreviousClose().doubleValue() == 0){ // 4 stock values at 0 most likely invalid stock
				return false;
			}
			double stockPrice = stock.getQuote().getPrice().doubleValue();
			if(stockSymbol.equals("<")){
				if(stockPrice <= eventStockPrice){
					conditionsLeft = conditionsLeft -1;
				}
			} else {
				if(stockPrice >= eventStockPrice){
					conditionsLeft = conditionsLeft -1;
				}
			}
			
			
		}

		if (conditionsLeft == 0) {// all active conditions are true, return true
									// for event active
			return true;

		}
		return false;
	}

	/**
	 * Returns all events that are active at the time of this methods' invocation
	 * @return an ArrayList of active events
	 */
	public ArrayList<BrainwavesEvent> getActiveEvents() {

		String[] ipPair; // pair of 2 potential IP addresses
		ipPair = IPFetcher.fetchIP();
		String ip = "N/A"; // ip chosen
		

		if(!ipPair[0].equals("N/A")){ // select pair
			ip = ipPair[0];
		} else if(!ipPair[1].equals("N/A")){
			ip = ipPair[1];
		}

		
		String[] loc = {"N/A", "N/A"};// country/city pair
		double temp = -1000;
		if (ip != null && !ip.equals("N/A")) { // if IP service unnavailable do not attempt to
									// look up location or temperature
			try {
				loc = fetchLocation(ip);
			} catch (ConnectException ce) {
				ce.printStackTrace();
				System.out.println("Connect exception");
			}
			if( loc[1] != null && !"N/A".equals(loc[1]) && loc[1].length() >= 1){// if loc service unnavailable do not attempt to look up temperature
				try{
					 temp = fetchTemperature(loc);
					}catch(NullPointerException npe){
						npe.printStackTrace();
					}
			}
		}
		
		
		//get stock data in a map of <String, Stock> where String is the stock name
		Map<String, Stock> stocks = new HashMap<String, Stock>();
		stocks.put("EMPTY", new Stock("EMPTY"));
		String[] stockNamesArray = new String[stockNames.size()];
		stockNamesArray = stockNames.toArray(stockNamesArray);
		if(stockNames.size() > 0){
		try {
			stocks = YahooFinance.get(stockNamesArray);// single request for all stocks
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		}
		

	
		
		
		ArrayList<BrainwavesEvent> activeEvents = new ArrayList<BrainwavesEvent>();
		Iterator<BrainwavesEvent> it = events.iterator();
		while (it.hasNext()) {
			BrainwavesEvent e = it.next();
			if (checkEvent(e, loc[1], temp, stocks)) {
				activeEvents.add(e);
			}
		}
		return activeEvents;
	}
	

//	private String[] fetchLocation(String ip){
//		JSONParser jsonParser = new JSONParser();
//		JSONObject json;
//		String[] loc = {"N/A", "N/A"};// country/city pair
//		try {	
//			json = (JSONObject) jsonParser.parse(URLReader.read("http://freegeoip.net/json/" + ip));
//			loc[0] = (String) json.get("country_code");
//			loc[1] =  (String) json.get("city");
//			loc[0] = loc[0].replaceAll(" ", "_");
//			loc[1] = loc[1].replaceAll(" ", "_");
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return loc;
//	}
	
	//fetchlocation alternate code with exception handling attempting a second location service
	private String[] fetchLocation(String ip) throws ConnectException {
		JSONParser jsonParser = new JSONParser();
		JSONObject json;
		String city = "N/A"; //unrealistic loc value
		String[] loc = {"N/A", "N/A"};// country/city pair
		try {	
			json = (JSONObject) jsonParser.parse(URLReader.read("http://freegeoip.net/json/" + ip));
			loc[0] = (String) json.get("country_code");
			loc[1] =  (String) json.get("city");
			if (loc[0]!=null){
			loc[0] = loc[0].replaceAll(" ", "_");
			}
			if (loc[1]!=null){
			loc[1] = loc[1].replaceAll(" ", "_");
			}

			
		}catch (Exception e) {
			e.printStackTrace();
			try {	
				json = (JSONObject) jsonParser.parse(URLReader.read("http://www.telize.com/geoip/" + ip));
				loc[0] = (String) json.get("country_code");
				loc[1] =  (String) json.get("city");
				if (loc[0]!=null){
					loc[0] = loc[0].replaceAll(" ", "_");
					}
					if (loc[1]!=null){
					loc[1] = loc[1].replaceAll(" ", "_");
					}
				//TODO del
				System.out.println(" Country is " + loc[0] + " City is " + loc[1]);
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
//		loc[0] = loc[0].replaceAll(" ", "_");
//		loc[1] = loc[1].replaceAll(" ", "_");
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
//			loc[0] = loc[0].replaceAll(" ", "_");
//			loc[1] = loc[1].replaceAll(" ", "_");
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
			json = (JSONObject) jsonParser.parse(URLReader.read("http://api.wunderground.com/api/0bc60dabe2f15156/conditions/q/" + loc[0] + "/" + loc[1] +".json"));
			JSONObject currentObsData =  (JSONObject) json.get("current_observation");//current observation data refer to above url for data formatting
			 currentTemp  =  (double) currentObsData.get("temp_c");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return currentTemp;
	}
	

	/** 
	 * Checks whether a given event takes place in the next 7 days
	 * @param e the BrainwavesEvent to be checked
	 * @return true if the event is taking place within the next 7 days
	 */
	public static boolean checkUpcomingEvent(BrainwavesEvent e) {
		BrainwavesEvent event = e;
		Calendar futureCal = Calendar.getInstance();
		Calendar eventCal = Calendar.getInstance();
		Calendar currentCal;
		futureCal.set(Calendar.HOUR_OF_DAY, 0);
		futureCal.set(Calendar.MINUTE, 0);
		futureCal.set(Calendar.SECOND, 0);
		eventCal.set(Calendar.HOUR_OF_DAY, 0);
		eventCal.set(Calendar.MINUTE, 0);
		eventCal.set(Calendar.SECOND, 0);
		int year, month, day, hour, minute, second;
		year = Calendar.getInstance().get(Calendar.YEAR);
		month = Calendar.getInstance().get(Calendar.MONTH);
		day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		minute = Calendar.getInstance().get(Calendar.MINUTE);
		second = Calendar.getInstance().get(Calendar.SECOND);
		// Date given, no day or time given
		if (!event.getDate().equals("EMPTY") && event.getDay().equals("EMPTY")
				&& event.getTime().equals("EMPTY")) {
			futureCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			if (eventCal.before(futureCal)) {
				return true;
			}

		}// Day given, no date or time given
		else if (event.getDate().equals("EMPTY")
				&& !event.getDay().equals("EMPTY")
				&& event.getTime().equals("EMPTY")) {
			futureCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7);
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(event.getDay()));
			if (eventCal.before(futureCal)
					&& eventCal.after(Calendar.getInstance())) {
				return true;
			}
		} // Time given, no date or day given
		else if (event.getDate().equals("EMPTY")
				&& event.getDay().equals("EMPTY")
				&& !event.getTime().equals("EMPTY")) {
			String[] timeString = event.getTime().split(":");
			eventCal.set(year, month, day, Integer.parseInt(timeString[0]), Integer.parseInt(timeString[1]), second);
			if(eventCal.after(Calendar.getInstance())){
				return true;
			}

		} // Date and day given
		else if (!event.getDate().equals("EMPTY")
				&& !event.getDay().equals("EMPTY")
				&& event.getTime().equals("EMPTY")) {
			futureCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(event.getDay()));
			if (eventCal.before(futureCal)
					&& eventCal.after(Calendar.getInstance())) {
				return true;
			}

		} // Date and time given
		else if (!event.getDate().equals("EMPTY")
				&& event.getDay().equals("EMPTY")
				&& !event.getTime().equals("EMPTY")) {
			futureCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			String[] timeString = event.getTime().split(":");
			String eventTimeConcat = timeString[0] + timeString[1];
			currentCal = Calendar.getInstance();
			String currentTimeConcat = currentCal.get(Calendar.HOUR_OF_DAY)
					+ "" + currentCal.get(Calendar.MINUTE);
			if (eventCal.before(futureCal) && Integer.parseInt(eventTimeConcat) > Integer
					.parseInt(currentTimeConcat)) {
				return true;
			}

		} // Day and time given
		else if (event.getDate().equals("EMPTY")
				&& !event.getDay().equals("EMPTY")
				&& !event.getTime().equals("EMPTY")) {
			futureCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7);
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(event.getDay()));
			String[] timeString = event.getTime().split(":");
			eventCal.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(timeString[0]));
			eventCal.set(Calendar.MINUTE,
					Integer.parseInt(timeString[1]));
			if (eventCal.before(futureCal)
					&& eventCal.after(Calendar.getInstance())) {
				return true;
			}

		} // Date day and time given
		else if (!event.getDate().equals("EMPTY")
				&& !event.getDay().equals("EMPTY")
				&& !event.getTime().equals("EMPTY")) {
			futureCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 7);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(event.getDay()));
			String[] timeString = event.getTime().split(":");
			eventCal.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(timeString[0]));
			eventCal.set(Calendar.MINUTE,
					Integer.parseInt(timeString[1]));
			if (eventCal.before(futureCal)
					&& eventCal.after(Calendar.getInstance())) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * Checks whether a given event takes place more than 3 days prior
	 * @param e the BrainwavesEvent to be checked
	 * @return true if the event took place more than 3 days ago
	 */
	public static boolean checkPastEvent(BrainwavesEvent e){
		BrainwavesEvent event = e;
		Calendar pastCal = Calendar.getInstance();
		Calendar eventCal = Calendar.getInstance();
		Calendar currentCal;
		pastCal.set(Calendar.HOUR_OF_DAY, 0);
		pastCal.set(Calendar.MINUTE, 0);
		pastCal.set(Calendar.SECOND, 0);
		eventCal.set(Calendar.HOUR_OF_DAY, 0);
		eventCal.set(Calendar.MINUTE, 0);
		eventCal.set(Calendar.SECOND, 0);
		int year, month, day,second;
		year = Calendar.getInstance().get(Calendar.YEAR);
		month = Calendar.getInstance().get(Calendar.MONTH);
		day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		second = Calendar.getInstance().get(Calendar.SECOND);
		// Date given, no day or time given
		if (!event.getDate().equals("EMPTY") && event.getDay().equals("EMPTY")
				&& event.getTime().equals("EMPTY")) {
			pastCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) -3);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			if (eventCal.before(pastCal)) {
				return true;
			}

		} // Date and day given
		else if (!event.getDate().equals("EMPTY")
				&& !event.getDay().equals("EMPTY")
				&& event.getTime().equals("EMPTY")) {
			pastCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) -3);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(event.getDay()));
			if (eventCal.before(pastCal)) {
				return true;
			}

		} // Date and time given
		else if (!event.getDate().equals("EMPTY")
				&& event.getDay().equals("EMPTY")
				&& !event.getTime().equals("EMPTY")) {
			pastCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) -3);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			String[] timeString = event.getTime().split(":");
			String eventTimeConcat = timeString[0] + timeString[1];
			currentCal = Calendar.getInstance();
			String currentTimeConcat = currentCal.get(Calendar.HOUR_OF_DAY)
					+ "" + currentCal.get(Calendar.MINUTE);
			if (eventCal.before(pastCal) && Integer.parseInt(eventTimeConcat) < Integer
					.parseInt(currentTimeConcat)) {
				return true;
			}

		} // Date day and time given
		else if (!event.getDate().equals("EMPTY")
				&& !event.getDay().equals("EMPTY")
				&& !event.getTime().equals("EMPTY")) {
			pastCal.set(Calendar.DATE,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 3);
			String[] dateString = event.getDate().split("/");
			eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
			eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
			eventCal.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(event.getDay()));
			String[] timeString = event.getTime().split(":");
			eventCal.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(timeString[0]));
			eventCal.set(Calendar.MINUTE,
					Integer.parseInt(timeString[1]));
			if (eventCal.before(pastCal)) {
				return true;
			}
		}
		return false;
		
		
	}

	/**
	 * Returns all events that will take place within the next 7 days
	 * @return an ArrayList of upcoming events
	 */
	public ArrayList<BrainwavesEvent> getUpcomingEvents() {
		ArrayList<BrainwavesEvent> futureEvents = new ArrayList<BrainwavesEvent>();
		Iterator<BrainwavesEvent> it = events.iterator();
		while (it.hasNext()) {
			BrainwavesEvent e = it.next();
			if (checkUpcomingEvent(e)){
				futureEvents.add(e);
			}
		}
		return futureEvents;
	}
	
	/**
	 * Returns all events that were active more than 3 days ago
	 * @return an ArrayList of old events
	 */
	public ArrayList<BrainwavesEvent> getPastEvents() {
		ArrayList<BrainwavesEvent> pastEvents = new ArrayList<BrainwavesEvent>();
		Iterator<BrainwavesEvent> it = events.iterator();
		while (it.hasNext()) {
			BrainwavesEvent e = it.next();
			if (checkPastEvent(e)){
				pastEvents.add(e);
			}
		}
		return pastEvents;
	}

	/**
	 * Takes a String and searches each field of each event for a match
	 * @param searchString the String with which to search each event for a match
	 * @return an ArrayList of events that match the given String
	 */
	public ArrayList<BrainwavesEvent> searchRepo(String searchString) {
		ArrayList<BrainwavesEvent> results = new ArrayList<BrainwavesEvent>();
		Iterator<BrainwavesEvent> it = events.iterator();
		BrainwavesEvent currentEvent;
		while (it.hasNext()) {
			currentEvent = it.next();
			if (searchEvent(currentEvent, searchString)) {
				results.add(currentEvent);
			}
		}
		return results;
	}

	/**
	 * Checks whether a given event contains the provided String in any of its fields
	 * @param currentEvent the event being searched
	 * @param searchString the String being matched
	 * @return true if a match is found within the event
	 */
	public boolean searchEvent(BrainwavesEvent currentEvent, String searchString) {

		if (currentEvent.getName().contains(searchString) || currentEvent.getName().toLowerCase().contains(searchString.toLowerCase())) {
			return true;
		}
		if (!currentEvent.getDate().equals("EMPTY")) {
			if (currentEvent.getDate().contains(searchString)|| currentEvent.getDate().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
		}
		if (!currentEvent.getDay().equals("EMPTY")) {
			if (currentEvent.getDay().contains(searchString)|| currentEvent.getDay().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
		}
		if (!currentEvent.getTime().equals("EMPTY")) {
			if (currentEvent.getTime().contains(searchString)|| currentEvent.getTime().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
		}
		if (!currentEvent.getLocation().equals("EMPTY")) {
			if (currentEvent.getLocation().contains(searchString)|| currentEvent.getLocation().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
		}
		if (!currentEvent.getTemperature().equals("EMPTY")) {
			if (currentEvent.getTemperature().contains(searchString)|| currentEvent.getTemperature().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
		}
		if (!currentEvent.getStock().equals("EMPTY")) {
			if (currentEvent.getStock().contains(searchString)|| currentEvent.getStock().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
		}
		if (!currentEvent.getDescription().equals("EMPTY")) {
			if (currentEvent.getDescription().contains(searchString)|| currentEvent.getDescription().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the size of the repository
	 * @return the size of this repository
	 */
	public int getRepoSize() {
		return events.size();
	}
	

	/**
	 * Returns the events contained in the repository
	 * @return an ArrayList of this repository's events
	 */
	public ArrayList<BrainwavesEvent> getEvents(){
		return new ArrayList<BrainwavesEvent>(events);
	}

	/**
	 * Deletes the provided event from the repository and database
	 * @param event the event to be deleted
	 */
	public void deleteEvent(BrainwavesEvent event) {
		deleteEventFromDB(event);
		int count=0;
		int index = -1;
		Iterator<BrainwavesEvent> it = events.iterator();
		while (it.hasNext()){
			BrainwavesEvent e = it.next();
			if (e.getName().equalsIgnoreCase(event.getName())){
				index = count;
			}
			count++;
		}
		if(index > -1){
			events.remove(index);
		}
	}
	
	/**
	 * Deleted the provided event from the database
	 * @param event the event to be deleted
	 */
	private void deleteEventFromDB(BrainwavesEvent event){
		 Connection c = null;
		    Statement stmt = null;
		    try {
		    	Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
				c.setAutoCommit(false);
				stmt = c.createStatement();
		      String sql = "DELETE from EVENTS where NAME='" + event.getName() + "';";
		      stmt.executeUpdate(sql);
		      c.commit();
		      stmt.close();
		      c.close();
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
	}

	/**
	 * Adds the specified event to the event list
	 * @param event the event to be added
	 */
	public void addEvent(BrainwavesEvent event) {
		events.add(event);
		
	}
	
	/**
	 * Check which events were active more than 3 days ago and permenantly remove them from the database. 
	 */
	public void removePastEvents(){
		Iterator<BrainwavesEvent> it = pastEvents.iterator();
		while(it.hasNext()){
			deleteEventFromDB(it.next());
		}
	}
	
	/**
	 * This method sends the data contained in this event to the database,
	 * assume the databse has been created upon starting the program
	 * @param event the event to be send to the database
	 * @throws SQLException 
	 */
	public void sendToDB(BrainwavesEvent event) throws SQLException {
		Connection c = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			c.setAutoCommit(false);
			stmt = c.prepareStatement("INSERT INTO EVENTS (NAME,DATE,DAY,TIME,LOCATION,TEMPERATURE,STOCK,DESCRIPTION) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setString(1, event.getName());
			stmt.setString(2, event.getDate());
			stmt.setString(3, event.getDay());
			stmt.setString(4, event.getTime());
			stmt.setString(5, event.getLocation());
			stmt.setString(6, event.getTemperature());
			stmt.setString(7, event.getStock());
			stmt.setString(8, event.getDescription());
			
			stmt.executeUpdate();
			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			c.close();
//			System.exit(0);
			throw new SQLException();
		}
	}

}
