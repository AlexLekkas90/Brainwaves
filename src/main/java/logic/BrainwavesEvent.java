package main.java.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Alexandros Lekkas
 * Class to describe the program logic of an event
 */
public class BrainwavesEvent {
	private String name; //mandatory field
	private String date;
	private String day;
	private String time; // TODO specify formats
	private String temperature;
	private String location;
	private String stock;
	private String description;
	private int activeConditions;
	private static int totalConditions = 6;

	/**
	 * Constructor for an empty event
	 */
	public BrainwavesEvent() {
		// initialise all fields to some default value
		name = "EMPTY";
		date = "EMPTY";
		day = "EMPTY";
		time = "EMPTY";
		temperature = "EMPTY";
		location = "EMPTY";
		stock = "EMPTY";
		description = "EMPTY";
		activeConditions = 0;
	}

	/**
	 * Construct an event
	 * @param name the event name
	 * @param date the event date in the format MM/yyyy
	 * @param day the event day
	 * @param time the event time in the format hh:mm
	 * @param location the location of the event
	 * @param temperature the temperature in the format >:temp
	 * @param stock the stock in the format stockname:>:stockvalue
	 * @param description a short description of the event
	 */
	public BrainwavesEvent(String name, String date, String day, String time,
			String location, String temperature, String stock, String description) {
		this.name = name;
		this.date = date;
		this.day = day;
		this.time = time;
		this.location = location;
		this.temperature = temperature;
		this.stock = stock;
		this.description = description;
		updateActiveConditions();
	}

	/**
	 * Calculates how many active conditions this event has
	 */
	private void updateActiveConditions() {
		activeConditions = 0;
		if (!date.equals("EMPTY")) {
			activeConditions++;
		}
		if (!day.equals("EMPTY")) {
			activeConditions++;
		}
		if (!time.equals("EMPTY")) {
			activeConditions++;
		}
		if (!location.equals("EMPTY")) {
			activeConditions++;
		}
		if (!temperature.equals("EMPTY")) {
			activeConditions++;
		}
		if (!stock.equals("EMPTY")) {
			activeConditions++;
		}
	}

	/**
	 * @return the event name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the event name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param date the event date
	 */
	public void setDate(String date) {
		this.date = date;
		updateActiveConditions();
	}

	/**
	 * @param day the event day
	 */
	public void setDay(String day) {
		this.day = day;
		updateActiveConditions();
	}

	/**
	 * @param time the event time
	 */
	public void setTime(String time) {
		this.time = time;
		updateActiveConditions();
	}

	/**
	 * @param temperature the event temperature
	 */
	public void setTemperature(String temperature) {
		this.temperature = temperature;
		updateActiveConditions();
	}

	/**
	 * @param location the event location
	 */
	public void setLocation(String location) {
		this.location = location;
		updateActiveConditions();
	}
	
	/**
	 * 
	 * @param stock the stock data
	 */
	public void setStock(String stock){
		this.stock = stock;
		updateActiveConditions();
	}

	/**
	 * @return the event date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the event day
	 */
	public String getDay() {
		return day;
	}

	/**
	 * @return the event time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @return the event temperature
	 */
	public String getTemperature() {
		return temperature;
	}

	/**
	 * @return the event location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * 
	 * @return the event stock data
	 */
	public String getStock(){
		return stock;
	}

	/**
	 * @return the event description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the event description
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return String representation of this event with elements separated by a
	 * semicolon
	 */
	public String toStringFull() {
		return name + ";" + date + ";" + day + ";" + time + ";" + location
				+ ";" + temperature + ";" + stock + ";" + description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.name;
	}
//
//	/** TODO del
//	 * This method sends the data contained in this event to the database,
//	 * assume the databse has been created upon starting the program
//	 * @throws SQLException 
//	 */
//	public void sendToDB() throws SQLException {
//		Connection c = null;
//		PreparedStatement stmt = null;
//		try {
//			Class.forName("org.sqlite.JDBC");
//			c = DriverManager.getConnection(DATABASENAME);
//			c.setAutoCommit(false);
//			stmt = c.prepareStatement("INSERT INTO EVENTS (NAME,DATE,DAY,TIME,LOCATION,TEMPERATURE,STOCK,DESCRIPTION) "
//					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
//			stmt.setString(1, getName());
//			stmt.setString(2, getDate());
//			stmt.setString(3, getDay());
//			stmt.setString(4, getTime());
//			stmt.setString(5, getLocation());
//			stmt.setString(6, getTemperature());
//			stmt.setString(7, getStock());
//			stmt.setString(8, getDescription());
//			
//			stmt.executeUpdate();
//			stmt.close();
//			c.commit();
//			c.close();
//		} catch (Exception e) {
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			c.close();
////			System.exit(0);
//			throw new SQLException();
//		}
//	}

	/**
	 * @return the number of active conditions (non EMPTY)
	 */
	public int getActiveConditions() {
		return activeConditions;
	}


	/**
	 * Returns a string containing a sentence describing the event
	 * @return Sentence with event data
	 */
	public String printAlt() {
		String output = "The event " + name + " is active";
		if (!date.equals("EMPTY")) {
			output = output + " on " + date;
		}
		if (!day.equals("EMPTY")) {
			output = output + " on day " + Integer.parseInt(day);
		}
		if (!time.equals("EMPTY")) {
			output = output + " at " + time;
		}
		if (!location.equals("EMPTY")) {
			output = output + " when in " + location;
		}
		if (!temperature.equals("EMPTY")) {
			output = output + " with temperature " + temperature.split(":")[0]
					+ " " + temperature.split(":")[1];
		}
		if (!stock.equals("EMPTY")) {
			output = output + " with stock " + stock.split(":")[0]
					+ " " + stock.split(":")[1] + " " + stock.split(":")[2];
		}
		if (!description.equals("EMPTY")) {
			output = output + ", additional information: " + description;
		}
		return output;
	}

	/**
	 * Returns a String with the event data formatted
	 * @return String representing event data formatted
	 */
	public String print() {
		String output = name + "\n";
		if (!date.equals("EMPTY")) {
			output = output + "Date: " + date + "\n";
		}
		if (!day.equals("EMPTY")) {
			output = output + "Day: " + Integer.parseInt(day) + "\n";
		}
		if (!time.equals("EMPTY")) {
			output = output + "Time: " + time + "\n";
		}
		if (!location.equals("EMPTY")) {
			output = output + "Location: " + location + "\n";
		}
		if (!temperature.equals("EMPTY")) {
			output = output + "Temperature: " + temperature.split(":")[0] + " "
					+ temperature.split(":")[1] + "\n";
		}
		if (!stock.equals("EMPTY")) {
			output = output + "Stock: " + stock.split(":")[0] + " "
					+ stock.split(":")[1] + " "  + stock.split(":")[2] + "\n";
		}
		if (!description.equals("EMPTY")) {
			output = output + "Description: " + description + "\n";
		}
		return output;
	}
	

}
