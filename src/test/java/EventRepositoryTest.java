package test.java;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import main.java.logic.BrainwavesEvent;
import main.java.logic.EventRepository;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class EventRepositoryTest {
	private static Connection con;
	private Statement stmt;
	private static String currentDate;
	private static String futureDate;
	private static String currentDay;
	private static String futureDay;
	private static String pastDay;
	private static String currentTime;
	private static String futureTime;
	private static String pastTime;
	private static String location;
	private static double temperature;
	private static Stock stock;
	private static Map<String, Stock> stocks;
	private static double stockPrice;
	private static String pastDate;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 con = null;
			try {
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:testDB.db");
				con.setAutoCommit(false);
				Statement stmt;
				stmt = con.createStatement();
				String createTableSQL = "CREATE TABLE IF NOT EXISTS `EVENTS` (`NAME` VARCHAR(40) PRIMARY KEY,`DATE` VARCHAR(10) NULL"
						+ ",`DAY` VARCHAR(3) NULL,`TIME` VARCHAR(10) NULL,`LOCATION` VARCHAR(40) NULL,`TEMPERATURE` VARCHAR(10) NULL,`STOCK` VARCHAR(18) NULL,`DESCRIPTION` VARCHAR(60) NULL)";
				stmt.executeUpdate(createTableSQL);
				stmt.close();
				con.commit();
				
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);

			}
			
			Calendar currentCal = Calendar.getInstance();
			Calendar futureCal = Calendar.getInstance();
			currentDate = currentCal.get(Calendar.MONTH)+ 1 + "/" + currentCal.get(Calendar.YEAR);
			currentDay = "" + currentCal.get(Calendar.DAY_OF_MONTH);
			currentTime = "" +  currentCal.get(Calendar.HOUR_OF_DAY) + ":" +  currentCal.get(Calendar.MINUTE);
			futureCal.set(Calendar.MONTH,
					Calendar.getInstance().get(Calendar.MONTH) + 1);
			futureDate = futureCal.get(Calendar.MONTH) + 1 + "/" + futureCal.get(Calendar.YEAR);
			futureCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH) + 8);
			futureDay = "" + futureCal.get(Calendar.DAY_OF_MONTH);
			futureCal.set(Calendar.HOUR, currentCal.get(Calendar.HOUR) + 1);
			futureTime = "" + futureCal.get(Calendar.HOUR_OF_DAY) + ":" + futureCal.get(Calendar.MINUTE);
			
			currentCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH) - 1);
			pastDate = currentCal.get(Calendar.MONTH)+ 1 + "/" + currentCal.get(Calendar.YEAR);
			currentCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH) - 4);
			pastDay = "" + currentCal.get(Calendar.DAY_OF_MONTH);
			currentCal.set(Calendar.HOUR, currentCal.get(Calendar.HOUR) - 1);
			pastTime = "" + currentCal.get(Calendar.HOUR_OF_DAY) + ":" + currentCal.get(Calendar.MINUTE);
			
			stocks = new HashMap<String, Stock>();
			String[] stockNamesArray = new String[]{"INTC"};
			stocks = YahooFinance.get(stockNamesArray);
			temperature = -1000;
			stock = stocks.get("INTC");
			stockPrice =  stock.getQuote().getPrice().doubleValue();
			if(Integer.parseInt(pastDay) > Integer.parseInt(futureDay)){
				pastDay = "1";
			}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() throws Exception {
		try {
			stmt = con.createStatement();
			String createTableSQL = "CREATE TABLE IF NOT EXISTS `EVENTS` (`NAME` VARCHAR(40) PRIMARY KEY,`DATE` VARCHAR(10) NULL"
					+ ",`DAY` VARCHAR(3) NULL,`TIME` VARCHAR(10) NULL,`LOCATION` VARCHAR(40) NULL,`TEMPERATURE` VARCHAR(10) NULL,`STOCK` VARCHAR(18) NULL,`DESCRIPTION` VARCHAR(60) NULL)";
			stmt.executeUpdate(createTableSQL);
			stmt.close();
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		
		try {
			stmt = con.createStatement();
			String dropTableSQL = "DROP TABLE EVENTS";
			stmt.executeUpdate(dropTableSQL);
			stmt.close();
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void EventRepoShouldRetrieveEventsFromDB(){
		Connection c = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:testDB.db");
			c.setAutoCommit(false);
			stmt = c.prepareStatement("INSERT INTO EVENTS (NAME,DATE,DAY,TIME,LOCATION,TEMPERATURE,STOCK,DESCRIPTION) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setString(1, "Test");
			stmt.setString(2, "EMPTY");
			stmt.setString(3, "EMPTY");
			stmt.setString(4, "EMPTY");
			stmt.setString(5, "EMPTY");
			stmt.setString(6, "EMPTY");
			stmt.setString(7, "EMPTY");
			stmt.setString(8, "EMPTY");
			
			stmt.executeUpdate();
			stmt.close();
			c.commit();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
		
		EventRepository repo = new EventRepository("testDB.db");
		repo.fetchEventsFromDB();
		assertEquals("The repo should now load the single event in the database and have a size of 1", 1, repo.getRepoSize());
		Iterator<BrainwavesEvent> it = repo.getEvents().iterator();
		BrainwavesEvent e = new BrainwavesEvent();
		while (it.hasNext()){
			e = it.next();
		}
		assertEquals("The event retrieved is the only event currently in the test database and has the name set to (unique constraint) Test", "Test", e.getName());
		
	}
	
	@Test
	public void checkEventShouldCheckActiveEventsCorrectly(){
		//date
		EventRepository repo = new EventRepository("testDB.db");
		BrainwavesEvent event;
		event = new BrainwavesEvent("Test1","1/2014","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		
		assertFalse("Past date event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDate(currentDate);
//		System.out.println(event.getActiveConditions());
//		System.out.println(repo.checkEvent(event, location, temperature, stocks));
		assertTrue("Current date event returns true and is active", repo.checkEvent(event, location, temperature, stocks));
		event.setDate(futureDate);
		assertFalse("Future date event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		//day
		event = new BrainwavesEvent("Test1","EMPTY","" + pastDay,"EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Past day event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(currentDay);
		assertTrue("Current day event returns true and is active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(futureDay);
		assertFalse("Future day event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		//time
		event = new BrainwavesEvent("Test1","EMPTY","EMPTY",pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertTrue("Past time event returns true and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(currentTime);
		assertTrue("Current time event returns true and is active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		//date & day
		event = new BrainwavesEvent("Test1",currentDate,pastDay,pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date past day event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(currentDay);
		assertTrue("Current date current day event returns true and is active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(futureDay);
		assertFalse("Current date future day event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDate(futureDate);
		event.setDay(pastDay);
		assertFalse("Future date past day event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(currentDay);
		assertFalse("Future date current day event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(futureDay);
		assertFalse("Future date future day event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		//date & time
		event = new BrainwavesEvent("Test1",currentDate,"EMPTY",pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertTrue("Current date past time event returns true and is active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Current date future time time returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDate(futureDate);
		event.setTime(pastTime);
		assertFalse("Future date past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Future date future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		//day & time
		event = new BrainwavesEvent("Test1","EMPTY",pastDay,pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Past day past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Past day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(currentDay);
		event.setTime(pastTime);
		assertTrue("Current day past time event returns true and is active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Current day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(futureDay);
		event.setTime(pastTime);
		assertFalse("future day past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Future day Future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		//date & day & time
		event = new BrainwavesEvent("Test1",currentDate,pastDay,pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date past day past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Current date past day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(currentDay);
		event.setTime(pastTime);
		assertTrue("Current date current day past time event returns true and is active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Current date current day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(futureDay);
		event.setTime(pastTime);
		assertFalse("Current date future day past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Current date future day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDate(futureDate);
		event.setDay(pastDay);
		event.setTime(pastTime);
		assertFalse("Future date past day past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Future date past day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(currentDay);
		event.setTime(pastTime);
		assertFalse("Future date current day past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Future date current day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setDay(futureDay);
		event.setTime(pastTime);
		assertFalse("Future date future day past time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		event.setTime(futureTime);
		assertFalse("Future date future day future time event returns false and is not active", repo.checkEvent(event, location, temperature, stocks));
		
		//location
		event = new BrainwavesEvent("Test1","EMPTY","EMPTY","EMPTY","New","EMPTY","EMPTY","EMPTY");
		location = "Newcastle";
		assertTrue("Current location is Newcastle event location is new so return true", repo.checkEvent(event, location, temperature, stocks));
		event.setLocation("newcastle");
		assertTrue("Current location is Newcastle event location is newcastle so return true", repo.checkEvent(event, location, temperature, stocks));
		event.setLocation("Oldcastle");
		assertFalse("Current location is Newcastle event location is Oldcastle so return false", repo.checkEvent(event, location, temperature, stocks));
		location = "N/A";
		
		//temperature
		temperature = -1000;
		event = new BrainwavesEvent("Test1","EMPTY","EMPTY","EMPTY","EMPTY","<:20","EMPTY","EMPTY");	
		assertFalse("Current temperatue is -1000 so returns false due to temperature fetching failure", repo.checkEvent(event, location, temperature, stocks));
		temperature = 19;
		assertTrue("Current temperatue is 19 and event temp is < 20 so true", repo.checkEvent(event, location, temperature, stocks));
		temperature = 20;
		assertTrue("Current temperatue is 20 and event temp is < 20 so true", repo.checkEvent(event, location, temperature, stocks));
		temperature = 21;
		assertFalse("Current temperatue is 21 and event temp is < 20 so false", repo.checkEvent(event, location, temperature, stocks));
		event.setTemperature(">:20");
		assertTrue("Current temperatue is 21 and event temp is > 20 so true", repo.checkEvent(event, location, temperature, stocks));
		temperature = 20;
		assertTrue("Current temperatue is 20 and event temp is > 20 so true", repo.checkEvent(event, location, temperature, stocks));
		temperature = 19;
		assertFalse("Current temperatue is 19 and event temp is > 20 so false", repo.checkEvent(event, location, temperature, stocks));
		temperature = -1000;
		
		//stock
		event = new BrainwavesEvent("Test1","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","INTC:<:" + stockPrice,"EMPTY");	
		assertTrue("INTC stock smaller than the returned value so true", repo.checkEvent(event, location, temperature, stocks));
		event.setStock("INTC:>:" + stockPrice);
		assertTrue("INTC stock larger than the returned value so true", repo.checkEvent(event, location, temperature, stocks));
	}
	
	@Test
	public void checkUpcomingEventShouldCheckUpcomingEventsCorrectly(){
		BrainwavesEvent event;
		
		//date
		event = new BrainwavesEvent("Test1",currentDate,"EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		assertTrue("Current date event is upcoming", EventRepository.checkUpcomingEvent(event));
		//day
		event = new BrainwavesEvent("Test1","EMPTY",currentDay,"EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		event.setDay(futureDay);
		assertFalse("Future day event is not upcoming", EventRepository.checkUpcomingEvent(event));
		//time
		event = new BrainwavesEvent("Test1","EMPTY","EMPTY",futureTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertTrue("Future time event is upcoming", EventRepository.checkUpcomingEvent(event));
		event.setTime(pastTime);
		assertFalse("Past time event is not upcoming", EventRepository.checkUpcomingEvent(event));
		//date & day
		event = new BrainwavesEvent("Test1",currentDate,currentDay,"EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date current day event is not upcoming", EventRepository.checkUpcomingEvent(event));

		
		//date & time
		event = new BrainwavesEvent("Test1",currentDate,"EMPTY",pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date past time event is not upcoming", EventRepository.checkUpcomingEvent(event));
		
		event.setTime(futureTime);
		assertTrue("Current date future time event is upcoming", EventRepository.checkUpcomingEvent(event));
		//day & time
		event = new BrainwavesEvent("Test1","EMPTY",currentDay,pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current day past time event is not upcoming", EventRepository.checkUpcomingEvent(event));
		
		event.setTime(futureTime);
		assertTrue("Current day future time event is upcoming", EventRepository.checkUpcomingEvent(event));
		
		
		//date & day & time
		event = new BrainwavesEvent("Test1",currentDate,currentDay,pastTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date current day past time event is not upcoming", EventRepository.checkUpcomingEvent(event));
		event.setTime(futureTime);
		assertTrue("Current date current day future time event is upcoming", EventRepository.checkUpcomingEvent(event));
		
	}

	@Test
	public void checkPastEventShouldCheckPastEventsCorrectly(){
		BrainwavesEvent event;
		
		//date
		event = new BrainwavesEvent("Test1",currentDate,"EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date event is not past", EventRepository.checkPastEvent(event));
		event.setDate(pastDate);
		assertTrue("Past date event is past", EventRepository.checkPastEvent(event));
		//date & day
		event = new BrainwavesEvent("Test1",currentDate,currentDay,"EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date current day event is not past", EventRepository.checkPastEvent(event));
		event.setDate(pastDate);
		assertTrue("Past date current day event is past", EventRepository.checkPastEvent(event));
	
		//date & day & time
		event = new BrainwavesEvent("Test1",currentDate,currentDay,currentTime,"EMPTY","EMPTY","EMPTY","EMPTY");
		assertFalse("Current date event is not past", EventRepository.checkPastEvent(event));
		event.setDay(pastDay);
		event.setDate(pastDate);
		assertTrue("Past date past day current time event is past", EventRepository.checkPastEvent(event));
		
	}
	
	@Test
	public void searchEventShouldCorrectlyFindMatches(){
		BrainwavesEvent event = new BrainwavesEvent("Test1","11/2015","5","11:50","Newcastle",">:20","INTC:>:25","a description");
		EventRepository repo = new EventRepository("testDB.db");
		repo.addEvent(event);
		assertTrue("Correct name search", repo.searchEvent(event, "test1"));
		assertFalse("Incorrect name search", repo.searchEvent(event, "test2"));
		assertTrue("Correct date search", repo.searchEvent(event, "11/2015"));
		assertFalse("Incorrect date search", repo.searchEvent(event, "12/2015"));
		assertTrue("Correct day search", repo.searchEvent(event, "5"));
		assertTrue("Incorrect day search, however returns true since it matches date", repo.searchEvent(event, "11"));
		assertTrue("Correct time search", repo.searchEvent(event, "11:50"));
		assertFalse("Incorrect time search", repo.searchEvent(event, "12:50"));
		assertTrue("Correct location search", repo.searchEvent(event, "newc"));
		assertFalse("Incorrect location search", repo.searchEvent(event, "oldcastle"));
		assertTrue("Correct temperature search", repo.searchEvent(event, "20"));
		assertFalse("Incorrect temperature search", repo.searchEvent(event, ">20"));
		assertTrue("Correct stock search", repo.searchEvent(event, "INTC"));
		assertFalse("Incorrect stock search", repo.searchEvent(event, "INTC>25"));
		
	}
	
	@Test
	public void deleteEVentShouldDeleteEventsFromRepoAndDB(){
		BrainwavesEvent event = new BrainwavesEvent("Test1","11/2015","5","11:50","Newcastle",">:20","INTC:>:25","a description");
		EventRepository repo = new EventRepository("testDB.db");
		try {
			repo.sendToDB(event);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repo.fetchEventsFromDB();
		assertEquals("The repo should now load the single event in the database and have a size of 1", 1, repo.getRepoSize());
		Iterator<BrainwavesEvent> it = repo.getEvents().iterator();
		BrainwavesEvent e = new BrainwavesEvent();
		while (it.hasNext()){
			e = it.next();
		}
		assertEquals("The event retrieved is the only event currently in the test database and has the name set to (unique constraint) Test1", "Test1", e.getName());
		
		repo.deleteEvent(event);
		
		assertEquals("The repo should now have size 0", 0,  repo.getRepoSize());
		
		repo.fetchEventsFromDB();
		
		assertEquals("The repo should now have size 0", 0,  repo.getRepoSize());
		try {
		stmt = con.createStatement();
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
			event = new BrainwavesEvent("EventShouldNotExist", date, day, time, location,
					temperature, stock, description);
		}
		stmt.close();
		} catch (Exception ex) {
			System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
			System.exit(0);
		}
		
		assertEquals("The event object still holds the old Test1 event and not the overwriten EventShouldNotExist event", "Test1", event.getName());
		
	}
	
	@Test
	public void sendEventToDBShouldSendAnEvent(){
		BrainwavesEvent event = new BrainwavesEvent("Test1","11/2015","5","11:50","Newcastle",">:20","INTC:>:25","a description");
		EventRepository repo = new EventRepository("testDB.db");
		repo.addEvent(event);
		int count = 0;
		try {
			repo.sendToDB(event);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Events;");
			String name, day, date, time, location, temperature, stock, description = "";
			while (rs.next()) {
				count++;
				name = rs.getString("name");
				day = rs.getString("day");
				date = rs.getString("date");
				time = rs.getString("time");
				location = rs.getString("location");
				temperature = rs.getString("temperature");
				stock = rs.getString("stock");
				description = rs.getString("description");
				event = new BrainwavesEvent("EventShouldExist", date, day, time, location,
						temperature, stock, count + "");
			}
			stmt.close();
			} catch (Exception ex) {
				System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
				System.exit(0);
			}
		assertEquals("The event should be the EventShouldExist event", "EventShouldExist", event.getName());
		assertTrue("The description should be equal to 1", event.getDescription().equals("1"));

	}
	
	
}
