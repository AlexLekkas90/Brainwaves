package test.java;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import main.java.logic.BrainwavesEvent;
import main.java.logic.EventRepository;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EventRepositoryTest {
	private static Connection con;
	private Statement stmt;
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

}
