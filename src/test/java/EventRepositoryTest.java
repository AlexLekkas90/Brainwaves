package test.java;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			System.out.println(currentDate);
			currentDay = "" + currentCal.get(Calendar.DAY_OF_MONTH);
			currentTime = "" +  currentCal.get(Calendar.HOUR_OF_DAY) + ":" +  currentCal.get(Calendar.MINUTE);
			futureCal.set(Calendar.MONTH,
					Calendar.getInstance().get(Calendar.MONTH) + 1);
			futureDate = futureCal.get(Calendar.MONTH) + 1 + "/" + futureCal.get(Calendar.YEAR);
			futureCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH) + 1);
			futureDay = "" + futureCal.get(Calendar.DAY_OF_MONTH);
			futureCal.set(Calendar.HOUR, currentCal.get(Calendar.HOUR) + 1);
			futureTime = "" + futureCal.get(Calendar.HOUR) + ":" + futureCal.get(Calendar.MINUTE);
			
			currentCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH) - 1);
			pastDay = "" + currentCal.get(Calendar.DAY_OF_MONTH);
			currentCal.set(Calendar.HOUR, currentCal.get(Calendar.HOUR) - 1);
			pastTime = "" + currentCal.get(Calendar.HOUR) + ":" + currentCal.get(Calendar.MINUTE);
			
			stocks = new HashMap<String, Stock>();
			String[] stockNamesArray = new String[]{"INTC"};
			stocks = YahooFinance.get(stockNamesArray);
			temperature = -1000;
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
		System.out.println(event.getActiveConditions());
		event.setDate(currentDate);
		System.out.println(event.getActiveConditions());
		System.out.println(repo.checkEvent(event, location, temperature, stocks));
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
		
		//
	}
	

}
