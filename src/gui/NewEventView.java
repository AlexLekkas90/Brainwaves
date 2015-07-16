package gui;

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
		setBounds(100, 100, 340, 354);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar cal = Calendar.getInstance();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(28, 282, 296, 33);
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
						String month = parent.getMonth(monthNumber);
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
