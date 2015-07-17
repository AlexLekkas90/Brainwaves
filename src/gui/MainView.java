package gui;

import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Timer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import logic.BrainwavesEvent;
import logic.EventRepository;
import logic.EventScheduler;
import logic.InfoPanelScheduler;
import logic.TimeScheduler;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JButton;

/**
 * 
 * @author Alexandros Lekkas. Main GUI class that loads events from the database
 *         and monitors them. In order to modify the program and add a new event
 *         task the following steps need to be taken: Modify the
 *         NewEventVew.java class to add the new event to the GUI, add event
 *         handlers for the new task. Then modify the BrainwavesEvent class to
 *         add new fields for the new task as well as modify its methods to
 *         account for the new task. Need to also modify the EventRepository
 *         class where the majority of event checking takes place, for each new
 *         event task added. Edit the SQLite statements that create a table,
 *         send and receive data from the DB. Modify the search function in the
 *         repository to account for the new task. Change the advanced search class.
 */
public class MainView {

	private JFrame frmBrainwaves;
	private static EventRepository eventRepo; //holds all the events taken from DB, refreshed according to eventScheduler
	private static JList<BrainwavesEvent> activeEventsList; //list of active events
	private JTextField txtSearch; // search text field
	private JTextField txtNewEvent;
	private static JLabel lblDate;
	private static JLabel lblTime;
	private static JLabel lblLocation;
	private static JLabel lblTemperature;
	private static DefaultListModel<BrainwavesEvent> activeEventsModel; //active events model for the active events list
	private static JList<BrainwavesEvent> upcomingEventsList; // list of events within the next week
	private static DefaultListModel<BrainwavesEvent> upcomingEventsModel; //upcoming events model for the upcoming events list
	private JButton btnNewEvent;
	private JButton btnGo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView window = new MainView();
					window.frmBrainwaves.setVisible(true);
					window.frmBrainwaves.requestFocus();
					eventRepo = new EventRepository(); // to hold the events
					addUpcomingEvents(); // adds any upcoming events to the list
					Timer time = new Timer();
					EventScheduler eventScheduler = new EventScheduler(// checks active events every specified time period
							eventRepo);
					time.schedule(eventScheduler, 0, 1800000); // set to desired
																// miliseconds,
																// 3600000 = 1h, 60000 = 1m
					TimeScheduler timeScheduler = new TimeScheduler(lblDate, // makes sure date display information is accurate
							lblTime);

					time.schedule(timeScheduler, 0, 2000);
					
					InfoPanelScheduler infoPanelScheduler = new InfoPanelScheduler(lblTemperature, lblLocation);
					
					
					time.schedule(infoPanelScheduler, 0, 3600000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainView() {
		initialize();
		createDBTable();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBrainwaves = new JFrame();
		frmBrainwaves.setTitle("Brainwaves");
		frmBrainwaves.setResizable(false);
		frmBrainwaves.setBounds(100, 100, 616, 380);
		frmBrainwaves.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//menu
		JMenuBar menuBar = new JMenuBar();
		frmBrainwaves.setJMenuBar(menuBar);
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		// Quit button
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setActionCommand("Quit");
		mntmQuit.addActionListener(new MyActionListener());
		mnFile.add(mntmQuit);
		
		// New Event button
		JMenu mnEvent = new JMenu("Event");
		menuBar.add(mnEvent);
		JMenuItem mntmNewEvent = new JMenuItem("New event");
		mntmNewEvent.setActionCommand("New event");
		mntmNewEvent.addActionListener(new MyActionListener());
		mnEvent.add(mntmNewEvent);
		frmBrainwaves.getContentPane().setLayout(null);
		
		// List all events button
		JMenuItem mntmListEvents = new JMenuItem("List events");
		mntmListEvents.setActionCommand("List events");
		mntmListEvents.addActionListener(new MyActionListener());
		mnEvent.add(mntmListEvents);
		frmBrainwaves.getContentPane().setLayout(null);
		
		// Advanced search button
		JMenuItem mntmAdvancedSearch = new JMenuItem("Advanced search");
		mntmAdvancedSearch.setActionCommand("Advanced search");
		mntmAdvancedSearch.addActionListener(new MyActionListener());
		mnEvent.add(mntmAdvancedSearch);
		
		//Help menu button
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		mntmAbout.setActionCommand("About");
		mntmAbout.addActionListener(new MyActionListener());
		
		JMenuItem mntmQuickGuide = new JMenuItem("Quick guide");
		mnHelp.add(mntmQuickGuide);
		mntmQuickGuide.setActionCommand("Quick guide");
		mntmQuickGuide.addActionListener(new MyActionListener());
		frmBrainwaves.getContentPane().setLayout(null);
		
		// Active events
		activeEventsModel = new DefaultListModel<BrainwavesEvent>();
		JScrollPane activeEvents = new JScrollPane();
		activeEvents.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		activeEvents.setBounds(310, 181, 290, 138);
		frmBrainwaves.getContentPane().add(activeEvents);

		activeEventsList = new JList<BrainwavesEvent>(activeEventsModel);
		activeEventsList.addMouseListener(new MyMouseListener());
		activeEventsList
				.addKeyListener(new EventsKeyListener(activeEventsList));
		activeEvents.setViewportView(activeEventsList);
		JLabel lblActiveEvents = new JLabel("Active events");
		lblActiveEvents.setBounds(310, 155, 109, 14);
		frmBrainwaves.getContentPane().add(lblActiveEvents);
		
		// Upcoming events
		JScrollPane upcomingEvents = new JScrollPane();
		upcomingEvents.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		upcomingEvents.setBounds(10, 181, 290, 138);
		frmBrainwaves.getContentPane().add(upcomingEvents);

		upcomingEventsModel = new DefaultListModel<BrainwavesEvent>();
		upcomingEventsList = new JList<BrainwavesEvent>(upcomingEventsModel);
		upcomingEventsList.addMouseListener(new MyMouseListener());
		upcomingEventsList.addKeyListener(new EventsKeyListener(
				upcomingEventsList));
		upcomingEvents.setViewportView(upcomingEventsList);
		JLabel lblNewLabel = new JLabel("Upcoming events");
		lblNewLabel.setBounds(10, 155, 109, 14);
		frmBrainwaves.getContentPane().add(lblNewLabel);
		
		// Information panel
		JPanel infoPanel = new JPanel();
		infoPanel.setBounds(10, 11, 224, 138);
		frmBrainwaves.getContentPane().add(infoPanel);
		infoPanel.setLayout(null);

		lblDate = new JLabel("Date:");
		lblDate.setBounds(0, 5, 122, 14);
		infoPanel.add(lblDate);

		lblTime = new JLabel("Time:");
		lblTime.setBounds(0, 30, 106, 14);
		infoPanel.add(lblTime);

		lblLocation = new JLabel("Location:");
		lblLocation.setBounds(0, 55, 214, 14);
		infoPanel.add(lblLocation);

		lblTemperature = new JLabel("Temperature:");
		lblTemperature.setBounds(0, 80, 134, 14);
		infoPanel.add(lblTemperature);

		// search
		txtSearch = new JTextField();
		txtSearch.setText("Search");
		txtSearch.setBounds(438, 11, 96, 27);

		txtSearch.addFocusListener(new MySearchFocusListener());
		frmBrainwaves.getContentPane().add(txtSearch);
		txtSearch.setColumns(10);
		txtSearch.addKeyListener(new SearchKeyListener());

		btnGo = new JButton("Go");
		btnGo.setActionCommand("Search");
		btnGo.setBounds(544, 11, 56, 27);
		btnGo.addActionListener(new MyActionListener());
		frmBrainwaves.getContentPane().add(btnGo);
		
		//new event alt
		txtNewEvent = new JTextField();
		txtNewEvent.setText("Quick event");
		txtNewEvent.setBounds(396, 49, 138, 27);

		txtNewEvent.addFocusListener(new MyAddFocusListener());
		frmBrainwaves.getContentPane().add(txtNewEvent);
		txtNewEvent.setColumns(10);
		txtNewEvent.addKeyListener(new NewEventKeyListener());
		btnNewEvent = new JButton("Add");
		btnNewEvent.setActionCommand("Add");
		btnNewEvent.setBounds(544, 49, 56, 27);
		btnNewEvent.addActionListener(new MyActionListener());
		frmBrainwaves.getContentPane().add(btnNewEvent);
		JLabel exampleLbl = new JLabel("Example: #time 11:45 #name MyEvent ");
		exampleLbl.setBounds(396, 87, 204, 14);
		frmBrainwaves.getContentPane().add(exampleLbl);
				
	}
	/**
	 * Create a DB table if it doesn't exist
	 * TODO edit to add new conditions, required destruction of previous table + data
	 */
	private void createDBTable(){
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:BrainwavesDB.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();

			// check that table exists, create if not TODO edit when adding new
			// condition
			Statement stmt2 = c.createStatement();
			String createTableSQL = "CREATE TABLE IF NOT EXISTS `EVENTS` (`NAME` VARCHAR(40) PRIMARY KEY,`DATE` VARCHAR(10) NULL"
					+ ",`DAY` VARCHAR(3) NULL,`TIME` VARCHAR(10) NULL,`LOCATION` VARCHAR(40) NULL,`TEMPERATURE` VARCHAR(10) NULL,`STOCK` VARCHAR(18) NULL,`DESCRIPTION` VARCHAR(60) NULL)";
			stmt2.executeUpdate(createTableSQL);
			stmt2.close();
			c.commit();
			stmt.close();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * @author Alexandros Lekkas
	 * Action listener class to handle behaviour of buttons
	 */
	class MyActionListener implements ActionListener {
		private String quickGuideMsg;
		private String aboutMsg;

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals("Quit")) {
				System.exit(0);
			} else if (action.equals("New event")) {
				NewEventView newEventWindow = new NewEventView(MainView.this);
				newEventWindow.setVisible(true);
				newEventWindow.requestFocus();
			} else if (action.equals("List events")){
				if (eventRepo.getRepoSize() > 0) {
					EventListView allEventsView = new EventListView(
							eventRepo.getEvents(), eventRepo);
					allEventsView
							.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					allEventsView.setVisible(true);
					allEventsView.requestFocus();
				} else {
					JOptionPane.showMessageDialog(null, "No events to list.",
							"Search Results", JOptionPane.INFORMATION_MESSAGE);
				}
			} else if (action.equals("Advanced search")){
				AdvancedSearchView advancedSearchView = new AdvancedSearchView(eventRepo);
				advancedSearchView.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				advancedSearchView.setVisible(true);
				advancedSearchView.requestFocus();
				
			}
			else if (action.equals("About")){
				aboutMsg = "Author: Alexander Lekkas\nEmail: alexlekkas90@gmail.com"; //TODO make into final and initialise in fields
				GenericInfoView aboutView = new GenericInfoView(aboutMsg);
				aboutView.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				aboutView.setVisible(true);
				aboutView.requestFocus();
				
				
			}else if (action.equals("Quick guide")){
				quickGuideMsg = "The quick event feature allows event entry entirely via text. Simply append a hashtag # to the start of a condition "
						+ "followed by the condition data. An example event for 11:45 on the 5th of the month with temperature larger than 10 degrees would be "
						+ "#name ExampleEvent #time 11:45 #day 5 #temperature>10. Note the order of the conditions does not matter but the name is required.";//TODO add more, fix up
				GenericInfoView aboutView = new GenericInfoView(quickGuideMsg);
				aboutView.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				aboutView.setVisible(true);
				aboutView.requestFocus();
				
				
			}else if(action.equals("Search")) {
	
				if (!txtSearch.getText().equals("Search")
						&& !txtSearch.getText().equals("")) {
					search(txtSearch.getText());
				}
			} else if (action.equals("Add")){
				if (!txtNewEvent.getText().equals("Quick event")
						&& !txtNewEvent.getText().equals("")) {
					BrainwavesEvent event = new BrainwavesEvent();
					String[] splitRawInputTemp = txtNewEvent.getText().split(
							"#");
					String[] splitRawInput = new String[splitRawInputTemp.length - 1];
					for (int i = 1; i < splitRawInputTemp.length; i++) {
						splitRawInput[i - 1] = splitRawInputTemp[i];
					}
					int length = splitRawInput.length;
					int count = 0;
					while (count < length) {
						String[] splitInput = new String[] { "", "" };
						int charCount = 0;
						while (charCount < splitRawInput[count].length()
								&& splitRawInput[count].charAt(charCount) != (' ')) {
							splitInput[0] = splitInput[0]
									+ splitRawInput[count].charAt(charCount);
							charCount++;
						}
						splitInput[1] = splitRawInput[count]
								.substring(charCount + 1);// splitInput[0]
															// contains hash
															// data type while
															// [1] contains
															// actual data
						splitInput[1] = splitInput[1].trim();
						if(splitInput[1].equals("")){
							JOptionPane
							.showMessageDialog(
									null,
									"Please enter a value after a condition",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
							return;
						}
						if (splitInput[0].equalsIgnoreCase("name")) { // check
																		// for
																		// name
							if (splitInput[1].length() > 20) {
								JOptionPane
										.showMessageDialog(
												null,
												"Please enter a name under 20 characters",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
								return;
							}
							for (int i = 0; i < splitInput[1].length(); i++) {
								if ((!Character.isLetter(splitInput[1]
										.charAt(i)))
										&& !(splitInput[1].charAt(i) == ' ') && (!Character.isDigit(splitInput[1]
										.charAt(i)))) {
									JOptionPane.showMessageDialog(null,
											"Please enter a valid name",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
									return;
								}
							}
							
							event.setName(splitInput[1]);
						} else if (splitInput[0].equalsIgnoreCase("date")) { // check
																				// for
																				// date
							if (splitInput[1].charAt(1) != '/'
									&& splitInput[1].charAt(2) != '/') {
								JOptionPane
										.showMessageDialog(
												null,
												"Please enter a date in the format MM/yyyy",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
								return;
							}
							String[] splitDate = splitInput[1].split("/");
							try {
								int monthNum = Integer.parseInt(splitDate[0]);
								int yearNum = Integer.parseInt(splitDate[1]);
								String monthString = monthNum + "";
								if (monthNum < 10) {
									monthString = "0" + monthNum;
								}
								if (monthNum > 0
										&& monthNum < 13
										&& yearNum >= Calendar.getInstance()
												.get(Calendar.YEAR)
										&& yearNum < Calendar.getInstance()
												.get(Calendar.YEAR) + 10) {
									event.setDate(monthString + "/" + yearNum);
								} else {
									JOptionPane.showMessageDialog(null,
											"Please enter a valid date",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
									return;
								}

							} catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null,
										"Please enter a valid date", "Warning",
										JOptionPane.WARNING_MESSAGE);
								return;
							}

						} else if (splitInput[0].equalsIgnoreCase("day")) { // check
																			// for
																			// day
							try {
								int dayNum = Integer.parseInt(splitInput[1]);
								if (dayNum > 0 && dayNum < 32) {
									String dayString = "" + dayNum;
									if (dayNum < 10) {
										dayString = "0" + dayNum;
									}
									event.setDay(dayString);
								} else {
									JOptionPane.showMessageDialog(null,
											"Please enter a valid day", "Warning",
											JOptionPane.WARNING_MESSAGE);
									return;
								}

							} catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null,
										"Please enter a valid day", "Warning",
										JOptionPane.WARNING_MESSAGE);
								return;
							}

						} else if (splitInput[0].equalsIgnoreCase("time")) { // check
																				// for
																				// time
							if (splitInput[1].charAt(1) != ':'
									&& splitInput[1].charAt(2) != ':') {
								JOptionPane
										.showMessageDialog(
												null,
												"Please enter a time in the format hh:mm",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
								return;
							}
							String[] timeString = splitInput[1].split(":");
							try {
								int hourNum = Integer.parseInt(timeString[0]);
								int minNum = Integer.parseInt(timeString[1]);
								if (hourNum > -1 && hourNum < 24 && minNum > -1
										&& minNum < 60) {
									event.setTime(hourNum + ":" + minNum);
								} else {
									JOptionPane.showMessageDialog(null,
											"Please enter a valid time",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
									return;
								}

							} catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null,
										"Please enter a valid time", "Warning",
										JOptionPane.WARNING_MESSAGE);
								return;
							}

						} else if (splitInput[0].equalsIgnoreCase("location")) { // check
																					// for
																					// location
							if (splitInput[1].length() > 30) {
								JOptionPane
										.showMessageDialog(
												null,
												"Please enter a location under 30 characters",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
								return;
							}
							for (int i = 0; i < splitInput[1].length(); i++) {
								if ((!Character.isLetter(splitInput[1]
										.charAt(i)))
										&& !(splitInput[1].charAt(i) == ' ')) {
									JOptionPane.showMessageDialog(null,
											"Please enter a valid location",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
									return;
								}
							}
							event.setLocation(splitInput[1]);

						} else if (splitInput[0]
								.equalsIgnoreCase("temperature")) { // check for
																	// temperature
							String temp = splitInput[1].replaceAll("\\s+", "");// remove
																				// whitespace
							char symbol = temp.charAt(0);
							String tempString = temp.substring(1);
							if (symbol != '<' && symbol != '>') {
								JOptionPane.showMessageDialog(null,
										"Please enter < or >", "Warning",
										JOptionPane.WARNING_MESSAGE);
								return;
							}
							try {
								int tempNum = Integer.parseInt(tempString);
								if (tempNum >= -30 && tempNum <= 60) {
									event.setTemperature("" + symbol + ":"
											+ tempNum);
								} else {
									JOptionPane
											.showMessageDialog(
													null,
													"Please enter a valid temperature between -30 and 60 degrees",
													"Warning",
													JOptionPane.WARNING_MESSAGE);
									return;
								}

							} catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null,
										"Please enter a valid temperature",
										"Warning", JOptionPane.WARNING_MESSAGE);
								return;
							}

						} else if (splitInput[0]
								.equalsIgnoreCase("description")) { // check for
																	// description
							if (splitInput[1].length() > 50) {
								JOptionPane
										.showMessageDialog(
												null,
												"Please enter a description under 50 characters",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
								return;
							}
							event.setDescription(splitInput[1]);
						} else if (splitInput[0].equalsIgnoreCase("stock")){
							//TODO
							String tempStock = splitInput[1].replaceAll("\\s+", "");// remove
							// whitespace
							//look for first < or >
							int counter = 0;
							boolean notFound = true;
							String symbol = "EMPTY";
							while (counter < tempStock.length() && notFound){
								if(tempStock.charAt(counter) == '<' || tempStock.charAt(counter) == '>'){
								notFound = false;
								symbol = tempStock.charAt(counter) + "";
								}
								counter++;
							}
							
							//check whether < or > found
							if(symbol.equals("EMPTY")){
								JOptionPane
								.showMessageDialog(
										null,
										"Please enter a valid stock",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
						return;
							}
							
							//< or > found now check whether name and value are valid
							String[] splitStock = tempStock.split(symbol);
							if (splitStock.length > 2){ // check there was only one > or <
								JOptionPane
								.showMessageDialog(
										null,
										"Please enter a valid stock",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
						return;
							}
							//check stock name
							if(splitStock[0].length() > 5){
								JOptionPane
								.showMessageDialog(
										null,
										"Please enter a shorter stock name",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
						return;
							}
							for (int i = 0; i < splitStock[0].length(); i++) {
								if ((!Character.isLetter(splitStock[0]
										.charAt(i)))
										&& !(Character.isDigit(splitStock[0].charAt(i)))) {
									JOptionPane.showMessageDialog(null,
											"Please enter a valid stock name",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
									return;
								}
							}
							
							
							//check stock value
							if(splitStock[1].length() > 10){
								JOptionPane.showMessageDialog(null,
										"Please enter a shorter stock value",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
								return;
							}
							
							try {
								double tempStockValue = Double.parseDouble(splitStock[1]);

							} catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null,
										"Please enter a valid stock price",
										"Warning", JOptionPane.WARNING_MESSAGE);
								return;
							}
							
							event.setStock(splitStock[0] + ":" + symbol + ":" + splitStock[1]);
						}
						
						
						
						else { // hash input not valid
							JOptionPane
									.showMessageDialog(
											null,
											"Please enter a valid condition following the hash key",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
							return;
						}
						count++;
					}
					if (event.getName().equals("EMPTY") || event.getName().equals(" ")) {
						JOptionPane.showMessageDialog(null,
								"Please enter a name for the event", "Warning",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					if (event.getActiveConditions() == 0) {
						JOptionPane.showMessageDialog(null,
								"Please add at least one condition", "Warning",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					if(event.getDate() != "EMPTY" && event.getDay() != "EMPTY"){
					
					Integer dayInteger = Integer.parseInt(event.getDay());
					int day = dayInteger.intValue();
					// first check validity of date if date/day combo has been
					// selected
					if (day > 10) {
						// get date and day values from the spinners
						String date = event.getDate();
						String[] splitDate = date.split("/");
						
						Calendar cal = new GregorianCalendar();
						cal.set(Calendar.MONTH, Integer.parseInt(splitDate[0])-1);
						cal.set(Calendar.YEAR, Integer.parseInt(splitDate[1]));
						SimpleDateFormat sf = new SimpleDateFormat("ddMMMyyyy");
						sf.setLenient(false);
						int monthNumber = cal.get(Calendar.MONTH);
						String month = getMonth(monthNumber);
						int year = cal.get(Calendar.YEAR);
						String formatted = "" + day
								+ month.charAt(0) + month.charAt(1)
								+ month.charAt(2) + year;
						try {
							// parse formatted string, if incorrect catch
							// exception
							sf.parse(formatted);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(null,
									"Invalid Date & Day combination", "Warning",
									JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
					}
					
					try {
						event.sendToDB(); // send the event to the database
						addEventToRepo(event);
						if (EventRepository.checkUpcomingEvent(event)) {
							addToUpcomingEvents(event); // add event to
																	// the
																	// upcoming
					
							// events
						}
						txtNewEvent.setText("Quick event");
						JOptionPane.showMessageDialog(null,
								"Added event " + event.toString(), "Information",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (SQLException sqle) {
						JOptionPane.showMessageDialog(null,
								"Please enter a unique name", "Warning",
								JOptionPane.WARNING_MESSAGE);
					}

			}
		}
		}
	}
	
	/**
	 * @author Alexandros Lekkas
	 * Focus listener class to add focus behaviour to the search textfield
	 */
	class MySearchFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) {
			if (txtSearch.getText().equals("Search")) {
				txtSearch.setText("");
			}
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			if (txtSearch.getText().equals("")) {
				txtSearch.setText("Search");
			}
		}
	}
	
	/**
	 * @author Alexandros Lekkas
	 * Focus listener class to add focus behaviour to the new event textfield
	 */
	class MyAddFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) {
			if (txtNewEvent.getText().equals("Quick event")) {
				txtNewEvent.setText("");
			}
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			if (txtNewEvent.getText().equals("")) {
				txtNewEvent.setText("Quick event");
			}
		}
	}

	/**
	 * @author Alexandros Lekkas
	 * Mouse listener class to create EventInfoView popup when double clicking an event from a list
	 */
	class MyMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2 && !e.isConsumed()) {
				e.consume();
				JList list = (JList) e.getSource();
				int index = 0;
				if (list.equals(upcomingEventsList)) {
					index = upcomingEventsList.locationToIndex(e.getPoint());
					EventInfoView eventInfoView = new EventInfoView(
							(BrainwavesEvent) upcomingEventsModel
									.getElementAt(index));
					eventInfoView.setVisible(true);
					eventInfoView.requestFocus();

				} else {
					index = activeEventsList.locationToIndex(e.getPoint());
					EventInfoView eventInfoView = new EventInfoView(
							(BrainwavesEvent) activeEventsModel
									.getElementAt(index));
					eventInfoView.setVisible(true);
					eventInfoView.requestFocus();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}

	/**
	 * @author Alexandros Lekkas
	 * Key listener that pops up a new EventInfoView for the selected event on a list when enter is pressed
	 */
	public class EventsKeyListener extends KeyAdapter {
		private JList<BrainwavesEvent> list = null;
		public EventsKeyListener(JList<BrainwavesEvent> list) {
			super();
			this.list = list;
		}

		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				BrainwavesEvent event = (BrainwavesEvent) list
						.getSelectedValue();
				EventInfoView eventInfoView = new EventInfoView(event);
				eventInfoView.setVisible(true);
				eventInfoView.requestFocus();
			}
		}
	}

	/**
	 * @author Alexandros Lekkas
	 * Key listener class to perform a search when focused on the search field and pressing enter
	 */
	public class SearchKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!txtSearch.getText().equals("Search")
						&& !txtSearch.getText().equals("")) {
					btnGo.doClick();
				}
			}
		}
	}
	
	/**
	 * @author Alexandros Lekkas
	 * Key listener class to add a new event when focused on the new event field and pressing enter
	 */
	public class NewEventKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!txtNewEvent.getText().equals("Quick event")
						&& !txtNewEvent.getText().equals("")) {
					btnNewEvent.doClick();
				}
			}
		}
	}

	/**
	 * Adds an event to the active events list
	 * @param e the BrainwavesEvent
	 */
	public static void addToActiveEvents(BrainwavesEvent e) {
		int pos = activeEventsList.getModel().getSize();
		activeEventsModel.add(pos, e);
	}

	/**
	 * Adds an event to the upcoming events list
	 * @param e the BrainwavesEvent
	 */
	public static void addToUpcomingEvents(BrainwavesEvent e) {
		int pos = upcomingEventsList.getModel().getSize();
		upcomingEventsModel.add(pos, e);
	}
	
	/**
	 * Check which events are upcoming and add them to the list for upcoming events
	 */
	private static void addUpcomingEvents() {
		    	ArrayList<BrainwavesEvent> futureEvents;
				futureEvents = eventRepo.getUpcomingEvents();
				if (futureEvents.size() > 0){
					Iterator<BrainwavesEvent> it = futureEvents.iterator();
					while(it.hasNext()){
						addToUpcomingEvents(it.next());
					}
				}
	}
	


	/**
	 * Check which events are active when this method is invoked and add them to the list for active events
	 * @param activeEvents the array list of active events to be added to the list
	 */
	public static void addActiveEvents(final ArrayList<BrainwavesEvent> activeEvents) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	activeEventsModel.removeAllElements();
		    	if (activeEvents.size() > 0){
					Iterator<BrainwavesEvent> it = activeEvents.iterator();
					while(it.hasNext()){
						addToActiveEvents(it.next());
					}
				}
		    }
		});
//		activeEventsModel.removeAllElements();
//		if (activeEvents.size() > 0){
//			Iterator<BrainwavesEvent> it = activeEvents.iterator();
//			while(it.hasNext()){
//				addToActiveEvents(it.next());
//			}
//		}
	}

	/**
	 * Search the events based on what the user typed and return the results in a new SearchResults window
	 * @param s the String representing what the user typed in
	 */
	private void search(String s) {
		ArrayList<BrainwavesEvent> searchResults;
		searchResults = eventRepo.searchRepo(s);
		if (searchResults.size() > 0) {
			EventListView searchResultsView = new EventListView(
					searchResults, eventRepo);
			searchResultsView
					.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			searchResultsView.setVisible(true);
			searchResultsView.requestFocus();
		} else {
			JOptionPane.showMessageDialog(null, "No events found.",
					"Search Results", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/**
	 * TODO fill in
	 * @param eventName
	 */
	public static void removeActiveListElement(String eventName){
		 int index = -1;
		for(int i=0; i < activeEventsModel.getSize(); i++){
		     BrainwavesEvent e =  activeEventsModel.getElementAt(i);  
		     if(e.getName().equalsIgnoreCase(eventName)){
		    	 index = i;
		     }
		}
		if (index > -1){
			activeEventsModel.remove(index);
		}
	}
	/**
	 * TODO fill in
	 * @param eventName
	 */
	public static void removeUpcomingListElement(String eventName){
		 int index = -1;
		for(int i=0; i < upcomingEventsModel.getSize(); i++){
		     BrainwavesEvent e =  upcomingEventsModel.getElementAt(i);  
		     if(e.getName().equalsIgnoreCase(eventName)){
		    	 index = i;
		     }
		}
		if (index > -1){
			upcomingEventsModel.remove(index);
		}
	}
	
	/**
	 * TODO fill in
	 * @param event
	 */
	public void addEventToRepo(BrainwavesEvent event){
		eventRepo.addEvent(event);
	}
	
	/**
	 * Converts a number to a month String
	 * 
	 * @param currentMonth
	 *            the current month ranging from 0-11
	 * @return String representing the month
	 */
	public String getMonth(int currentMonth) {
		String month = "";
		switch (currentMonth) {
		case 0:
			month = "January";
			break;
		case 1:
			month = "February";
			break;
		case 2:
			month = "March";
			break;
		case 3:
			month = "April";
			break;
		case 4:
			month = "May";
			break;
		case 5:
			month = "June";
			break;
		case 6:
			month = "July";
			break;
		case 7:
			month = "August";
			break;
		case 8:
			month = "September";
			break;
		case 9:
			month = "October";
			break;
		case 10:
			month = "November";
			break;
		case 11:
			month = "December";
			break;
		default:
			month = "Invalid";
			break;
		}
		return month;
	}
}
