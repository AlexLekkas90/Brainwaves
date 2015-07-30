package test.java;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.logic.BrainwavesEvent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BrainwavesEventTest {
	 private static Connection con;
	
	
	@BeforeClass
	public static void ini(){
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
	public  static void cleanUp(){
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Before
	public  void iniDBTable(){
		Statement stmt;
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
	public  void cleanDBTable(){
		Statement stmt;
		try {
			stmt = con.createStatement();
			String dropTableSQL = "DROP TABLE 'EVENTS'";
			stmt.executeUpdate(dropTableSQL);
			stmt.close();
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Test
	public  void emptyConstructorShouldInitialiseEmptyFields() {
		BrainwavesEvent e = new BrainwavesEvent();
		
		assertEquals("Name field is EMPTY", "EMPTY", e.getName());
		assertEquals("Day field is EMPTY", "EMPTY", e.getDay());
		assertEquals("Date field is EMPTY", "EMPTY", e.getDate());
		assertEquals("Time field is EMPTY", "EMPTY", e.getTime());
		assertEquals("Temperature field is EMPTY", "EMPTY", e.getTemperature());
		assertEquals("Location field is EMPTY", "EMPTY", e.getLocation());
		assertEquals("Stock field is EMPTY", "EMPTY", e.getStock());
		assertEquals("Description field is EMPTY", "EMPTY", e.getDescription());
		assertEquals("Number of active conditions is 0", 0, e.getActiveConditions());
	}
	
	
	@Test
	public  void normalConstructorShouldInitialiseEmptyFields() {
		BrainwavesEvent e = new BrainwavesEvent("EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
		assertEquals("Name field is EMPTY", "EMPTY", e.getName());
		assertEquals("Day field is EMPTY", "EMPTY", e.getDay());
		assertEquals("Date field is EMPTY", "EMPTY", e.getDate());
		assertEquals("Time field is EMPTY", "EMPTY", e.getTime());
		assertEquals("Temperature field is EMPTY", "EMPTY", e.getTemperature());
		assertEquals("Location field is EMPTY", "EMPTY", e.getLocation());
		assertEquals("Stock field is EMPTY", "EMPTY", e.getStock());
		assertEquals("Description field is EMPTY", "EMPTY", e.getDescription());
		assertEquals("Number of active conditions is 0", 0, e.getActiveConditions());
	}
	
	@Test
	public  void updateActiveConditionsShouldUpdateNumber(){
		BrainwavesEvent e = new BrainwavesEvent();
		assertEquals("Number of active conditions is 0", 0, e.getActiveConditions());
		e.setLocation("Newcastle");
		assertEquals("Number of active conditions is 1", 1, e.getActiveConditions());
		e.setLocation("EMPTY");
		assertEquals("Number of active conditions is 1", 1, e.getActiveConditions());
	}
	
//	@Test
//	public  void sentToDBShouldSendEventCorrectly(){
//		BrainwavesEvent e = new BrainwavesEvent("TEST1","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY","EMPTY");
//		try {
//			e.setDBName("jdbc:sqlite:testDB.db");
//			e.sendToDB();
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//	}


}
