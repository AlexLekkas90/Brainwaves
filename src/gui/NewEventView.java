package gui;

import gui.MainView.MyFocusListener;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JCheckBox;

import logic.BrainwavesEvent;
import logic.EventRepository;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

/**
 * @author Alexandros Lekkas Event window that allows the user to add one new
 *         event to the DB
 */
public class NewEventView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JCheckBox boxDate;
	private JCheckBox boxDay;
	private JCheckBox boxTime;
	private JSpinner dateSpinner;
	private JSpinner daySpinner;
	private JSpinner hourSpinner;
	private JSpinner minuteSpinner;
	private JSpinner symbolSpinner;
	private JSpinner tempSpinner;
	private JCheckBox boxTemp; // temperature selection box
	private boolean selection = false; // checks whether at least one important
										// element selected when trying to add
										// an event
	private JCheckBox boxLoc;
	private JTextField locField;
	private JTextField nameField;
	private JTextField descField;
	private JCheckBox boxDesc;
	private MainView parent;
	private JCheckBox boxAltEntry;
	private JTextArea altEntryField;
	private JTextField stockNameField;
	private JCheckBox boxStock;
	private JSpinner stockSymbolSpinner;
	private JFormattedTextField stockValueField;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// try {
	// NewEventView dialog = new NewEventView();
	// dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	// dialog.setVisible(true);
	// dialog.requestFocus();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Create the dialog.
	 */
	public NewEventView(MainView parent) {
		setTitle("New Event");
		this.parent = parent;
		setResizable(false);
		setBounds(100, 100, 596, 354);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar cal = Calendar.getInstance();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(284, 282, 296, 33);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton addButton = new JButton("Add");
				addButton.setActionCommand("Add");
				buttonPane.add(addButton);
				addButton.addActionListener(new MyActionListener());
				// getRootPane().setDefaultButton(addButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new MyActionListener());
				{
					// date spinner
					cal.add(Calendar.MONTH, +1);
					Date initDate = cal.getTime();
					cal.add(Calendar.MONTH, -2);
					Date earliestDate = cal.getTime();
					cal.add(Calendar.MONTH, +2);
					cal.add(Calendar.YEAR, 10);
					Date latestDate = cal.getTime();
					SpinnerModel dateModel = new SpinnerDateModel(initDate,
							earliestDate, latestDate, Calendar.YEAR);
					dateSpinner = new JSpinner(dateModel);
					boxDate = new JCheckBox("Date:");
					boxDate.setBounds(10, 59, 57, 20);
					getContentPane().add(boxDate);
					dateSpinner.setBounds(127, 59, 88, 26);
					dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner,
							"MM/yyyy"));
					getContentPane().add(dateSpinner);

					// day spinner
					SpinnerModel dayModel = new SpinnerNumberModel(1, 1, 31, 1);
					daySpinner = new JSpinner(dayModel);
					boxDay = new JCheckBox("Day:");
					boxDay.setBounds(10, 90, 57, 20);
					getContentPane().add(boxDay);
					daySpinner.setBounds(127, 90, 88, 26);
					NumberEditor ne_daySpinner = new JSpinner.NumberEditor(
							daySpinner, "00");
					ne_daySpinner.setToolTipText("");
					daySpinner.setEditor(ne_daySpinner);
					getContentPane().add(daySpinner);

					// time spinner
					SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
					SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59,
							1);
					hourSpinner = new JSpinner(hourModel);
					minuteSpinner = new JSpinner(minuteModel);
					boxTime = new JCheckBox("Time:");
					boxTime.setBounds(10, 121, 57, 20);
					getContentPane().add(boxTime);
					hourSpinner.setBounds(127, 121, 42, 26);
					minuteSpinner.setBounds(173, 121, 42, 26);
					NumberEditor ne_hourSpinner = new JSpinner.NumberEditor(
							hourSpinner, "#");
					ne_hourSpinner.setToolTipText("");
					hourSpinner.setEditor(ne_hourSpinner);
					getContentPane().add(hourSpinner);
					NumberEditor ne_minuteSpinner = new JSpinner.NumberEditor(
							minuteSpinner, "00");
					ne_minuteSpinner.setToolTipText("");
					minuteSpinner.setEditor(ne_minuteSpinner);
					getContentPane().add(minuteSpinner);

					// location textfield
					CustomDocument document2 = new CustomDocument(30);
					locField = new JTextField(document2, "", 0);
					locField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyTyped(KeyEvent key) {
							char c = key.getKeyChar();
							if (!(Character.isLetter(c)
									|| (c == KeyEvent.VK_BACK_SPACE)
									|| (c == KeyEvent.VK_DELETE) || (Character
									.isSpaceChar(c)))) {
								getToolkit().beep();
								key.consume();
							}

						}
					});
					boxLoc = new JCheckBox("Location:");
					boxLoc.setBounds(10, 152, 88, 20);
					getContentPane().add(boxLoc);
					locField.setBounds(127, 152, 111, 26);
					getContentPane().add(locField);

					// temperature spinner
					SpinnerModel symbolModel = new SpinnerListModel(
							new ArrayList<String>(Arrays.asList("<", ">")));
					SpinnerModel tempModel = new SpinnerNumberModel(0, -30, 60,
							1);
					symbolSpinner = new JSpinner(symbolModel);
					tempSpinner = new JSpinner(tempModel);
					boxTemp = new JCheckBox("Temperature:");
					boxTemp.setBounds(10, 183, 111, 20);
					getContentPane().add(boxTemp);
					symbolSpinner.setBounds(127, 183, 42, 26);
					tempSpinner.setBounds(173, 183, 42, 26);
					getContentPane().add(symbolSpinner);
					NumberEditor ne_tempSpinner = new JSpinner.NumberEditor(
							tempSpinner, "#");
					ne_tempSpinner.setToolTipText("");
					tempSpinner.setEditor(ne_tempSpinner);
					getContentPane().add(tempSpinner);

					// Name textfield
					CustomDocument document1 = new CustomDocument(20);
					nameField = new JTextField(document1, "", 0);
					nameField.setText("Event name");
					nameField.setBounds(10, 11, 191, 26);
					nameField.addFocusListener(new MyFocusListener());
					getContentPane().add(nameField);
					nameField.setColumns(10);
					nameField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyTyped(KeyEvent key) {
							char c = key.getKeyChar();
							if (!(Character.isLetter(c)
									|| (c == KeyEvent.VK_BACK_SPACE)
									|| (c == KeyEvent.VK_DELETE) || (Character
									.isSpaceChar(c)) || Character.isDigit(c))) {
								getToolkit().beep();
								key.consume();
							}

						}
					});

					// Description textfield
					CustomDocument document3 = new CustomDocument(50);
					descField = new JTextField(document3, "", 0);
					boxDesc = new JCheckBox("Description:");
					boxDesc.setBounds(10, 245, 97, 20);
					getContentPane().add(boxDesc);
					descField.setBounds(127, 245, 111, 26);
					getContentPane().add(descField);
					
					
					//Stock data
					CustomDocument document4 = new CustomDocument(5);
					stockNameField = new JTextField(document4, "", 0);
					stockNameField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyTyped(KeyEvent key) {
							char c = key.getKeyChar();
							if (!(Character.isLetter(c) || Character.isDigit(c)
									|| (c == KeyEvent.VK_BACK_SPACE)
									|| (c == KeyEvent.VK_DELETE) || (Character
									.isSpaceChar(c)))) {
								getToolkit().beep();
								key.consume();
							}

						}
					});
					CustomDocument document5 = new CustomDocument(10);
					stockValueField = new JFormattedTextField(NumberFormat.getNumberInstance());
					stockValueField.setDocument(document5);
					stockValueField .setValue(new Double(0));
					
					stockValueField .setColumns(2);
					
					boxStock = new JCheckBox("Stock:");
					boxStock.setBounds(10, 214, 88, 20);
					getContentPane().add(boxStock);
					stockNameField.setBounds(127, 214, 42, 26);
					getContentPane().add(stockNameField);
					stockValueField.setBounds(219, 214, 42, 26);
					getContentPane().add(stockValueField);
					SpinnerModel stockSymbolModel = new SpinnerListModel(
							new ArrayList<String>(Arrays.asList("<", ">")));
					stockSymbolSpinner = new JSpinner(stockSymbolModel);
					stockSymbolSpinner.setBounds(173, 214, 42, 26);
					getContentPane().add(stockSymbolSpinner);
	
				}
			}
		}

		boxAltEntry = new JCheckBox("Alternate entry:");
		boxAltEntry.setBounds(284, 14, 105, 20);
		getContentPane().add(boxAltEntry);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(284, 55, 296, 114);
		getContentPane().add(scrollPane);

		altEntryField = new JTextArea();
		altEntryField.setLineWrap(true);
		altEntryField.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		scrollPane.setViewportView(altEntryField);

		JLabel lblExampleEntrytime = new JLabel(
				"Example: #time 11:45 #name MyEvent #temperature >10");
		lblExampleEntrytime.setBounds(284, 186, 296, 14);
		getContentPane().add(lblExampleEntrytime);
	}


	/**
	 * Converts a number to a month String
	 * 
	 * @param currentMonth
	 *            the current month ranging from 0-11
	 * @return String representing the month
	 */
	private String getMonth(int currentMonth) {
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

	/**
	 * @author Alexandros Lekkas Action listener class for the add event and
	 *         cancel buttons.
	 */
	private class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals("Cancel")) {
				setVisible(false);
				dispose();
			} else if (action.equals("Add")) {
				if (boxAltEntry.isSelected()) {
					BrainwavesEvent event = new BrainwavesEvent();
					String[] splitRawInputTemp = altEntryField.getText().split(
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
						parent.addEventToRepo(event);
						if (EventRepository.checkUpcomingEvent(event)) {
							MainView.addToUpcomingEvents(event); // add event to
																	// the
																	// upcoming
																	// events
						}
						setVisible(false);
						dispose();
					} catch (SQLException sqle) {
						JOptionPane.showMessageDialog(null,
								"Please enter a unique name", "Warning",
								JOptionPane.WARNING_MESSAGE);
					}

				} else {
					Integer dayInteger = (Integer) daySpinner.getValue();
					int day = dayInteger.intValue();
					// first check validity of date if date/day combo has been
					// selected
					if (boxDate.isSelected() && boxDay.isSelected() && day > 10) {
						// get date and day values from the spinners
						Date date = (Date) dateSpinner.getValue();
						Calendar cal = new GregorianCalendar();
						cal.setTime(date);
						SimpleDateFormat sf = new SimpleDateFormat("ddMMMyyyy");
						sf.setLenient(false);
						int monthNumber = cal.get(Calendar.MONTH);
						String month = getMonth(monthNumber);
						int year = cal.get(Calendar.YEAR);
						String formatted = "" + daySpinner.getValue()
								+ month.charAt(0) + month.charAt(1)
								+ month.charAt(2) + year;
						try {
							// parse formatted string, if incorrect catch
							// exception
							sf.parse(formatted);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(null,
									"Invalid Date & Day combination", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					BrainwavesEvent event = new BrainwavesEvent(); // empty
																	// event
																	// with all
																	// fields
																	// set to
																	// EMPTY by
																	// default

					// set event fields according to selected spinners
					if (boxDate.isSelected()) {
						// note: the calendar month representation is 0-11, the
						// event class representation however stores a month as
						// 1-12
						Date date2 = (Date) dateSpinner.getValue();
						Calendar cal2 = new GregorianCalendar();
						cal2.setTime(date2);
						int monthNumber2 = cal2.get(Calendar.MONTH) + 1;
						int year2 = cal2.get(Calendar.YEAR);
						if (monthNumber2 < 10) {
							event.setDate("0" + monthNumber2 + "/" + year2);
						} else {// preserve the 0
							event.setDate("" + monthNumber2 + "/" + year2);
						}
						selection = true;
					}
					if (boxDay.isSelected()) {
						Integer day2 = (Integer) daySpinner.getValue();
						String dayString = String.format("%02d", day2);
						event.setDay(dayString);
						selection = true;
					}
					if (boxTime.isSelected()) {
						Integer hour = (Integer) hourSpinner.getValue();
						Integer minute = (Integer) minuteSpinner.getValue();
						String timeString = "" + hour.intValue() + ":"
								+ String.format("%02d", minute);
						event.setTime(timeString);
						selection = true;
					}
					if (boxTemp.isSelected()) {
						String symbol = (String) symbolSpinner.getValue();
						Integer temp = (Integer) tempSpinner.getValue();
						String tempString = "" + symbol + ":" + temp.intValue();
						event.setTemperature(tempString);
						selection = true;
					}
					if (boxLoc.isSelected()) {
						String location = (String) locField.getText();
						event.setLocation(location);
						selection = true;
					}
					if(boxStock.isSelected()){
						String stockName = stockNameField.getText();
						stockName = stockName.trim();
						String stockSymbol = (String) stockSymbolSpinner.getValue();
						String stockValue = stockValueField.getText();
						event.setStock(stockName + ":" + stockSymbol + ":" + stockValue);
						selection = true;
					}
					if (boxDesc.isSelected()) {
						String description = (String) descField.getText();
						event.setDescription(description);
					}

					if (selection) {// make sure at least one element selected
						String name = nameField.getText().trim();
						if (name.equals("Event name") || name.equals("")) {
							JOptionPane.showMessageDialog(null,
									"Please enter a name for the event",
									"Warning", JOptionPane.WARNING_MESSAGE);
						} else if(boxStock.isSelected() && (event.getStock().split(":")[0].equals("") || event.getStock().split(":")[0].equals(" "))){
							JOptionPane.showMessageDialog(null,
									"Please enter a name for the stock",
									"Warning", JOptionPane.WARNING_MESSAGE);
						
						}else {
						
							try {
								event.setName(name);
								event.sendToDB(); // send the event to the
													// database
								parent.addEventToRepo(event);
								if (EventRepository.checkUpcomingEvent(event)) {
									MainView.addToUpcomingEvents(event); // add
																			// event
																			// to
																			// the
																			// upcoming
																			// events
								}
								setVisible(false);
								dispose();
							} catch (SQLException sqle) {
								JOptionPane.showMessageDialog(null,
										"Please enter a unique name",
										"Warning", JOptionPane.WARNING_MESSAGE);
							}
						}
					} else {
						JOptionPane.showMessageDialog(null,
								"Please select at least one condition",
								"Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}
	}

	/**
	 * @author Alexandros Lekkas Focus listener for the name field, adds focus
	 *         behaviour
	 */
	class MyFocusListener implements FocusListener {
		@Override
		public void focusGained(FocusEvent arg0) {
			if (nameField.getText().equals("Event name")) {
				nameField.setText("");
			}
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			if (nameField.getText().equals("")) {
				nameField.setText("Event name");
			}
		}
	}


}
