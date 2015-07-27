package gui;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import logic.BrainwavesEvent;
import logic.EventRepository;

import javax.swing.JLabel;


/**
 * @author Alexandros Lekkas
 *  Event window that allows the user to add one new
 *         event to the DB
 */
public class NewEventView2 extends JDialog {
	private JTextField nameField;
	private DefaultListModel<String> leftListModel;
	private JList<String> leftList; // holds conditions that have not been selected
	private DefaultListModel<String> rightListModel;
	private JList<String> rightList; // holds selected conditions
	BrainwavesEvent event;
	private MainView parent;
	private boolean selection = false; // checks whether at least one important
	// element selected when trying to add
	// an event
	private String dateSide;
	private String daySide;
	private String timeSide;
	private String temperatureSide;
	private String locationSide;
	private String stockSide;
	private String descriptionSide;

	/**
	 * Create the dialog.
	 */
	public NewEventView2(MainView parent) {
		setResizable(false);
		this.parent = parent;
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("New Event");
		setBounds(100, 100, 499, 372);
		getContentPane().setLayout(null);
		event = new BrainwavesEvent();

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
				if (!(Character.isLetter(c) || (c == KeyEvent.VK_BACK_SPACE)
						|| (c == KeyEvent.VK_DELETE)
						|| (Character.isSpaceChar(c)) || Character.isDigit(c))) {
					getToolkit().beep();
					key.consume();
				}

			}
		});

		// Left List
		JScrollPane leftListPane = new JScrollPane();
		leftListPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		leftListPane.setBounds(10, 84, 191, 172);
		getContentPane().add(leftListPane);

		leftListModel = new DefaultListModel<String>();
		leftList = new JList<String>(leftListModel);
		leftList.addMouseListener(new MyMouseListener());
		leftList.addKeyListener(new LeftListKeyListener(leftList));
		leftListPane.setViewportView(leftList);

		// Right List
		JScrollPane rightListPane = new JScrollPane();
		rightListPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		rightListPane.setBounds(293, 84, 191, 172);
		getContentPane().add(rightListPane);

		rightListModel = new DefaultListModel<String>();
		rightList = new JList<String>(rightListModel);
		rightList.addMouseListener(new MyMouseListener());
		rightList.addKeyListener(new RightListKeyListener(rightList));
		rightListPane.setViewportView(rightList);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(10, 300, 474, 33);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);

			{
				JButton createButton = new JButton("Create");
				createButton.setActionCommand("Create");
				buttonPane.add(createButton);
				createButton.addActionListener(new MyActionListener());
				// getRootPane().setDefaultButton(addButton);
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new MyActionListener());
				buttonPane.add(cancelButton);
			}
			{

				// Buttons
				JButton btnAdd = new JButton("Add");
				btnAdd.setActionCommand("Add");
				btnAdd.setBounds(211, 84, 72, 23);
				getContentPane().add(btnAdd);
				btnAdd.addActionListener(new MyActionListener());

				JButton btnRemove = new JButton("Remove");
				btnRemove.setActionCommand("Remove");
				btnRemove.setBounds(211, 117, 72, 23);
				getContentPane().add(btnRemove);
				btnRemove.addActionListener(new MyActionListener());

				JButton btnClear = new JButton("Clear");
				btnClear.setActionCommand("Clear");
				btnClear.setBounds(211, 233, 72, 23);
				getContentPane().add(btnClear);
				btnClear.addActionListener(new MyActionListener());

				JButton btnEdit = new JButton("Edit");
				btnEdit.setActionCommand("Edit");
				btnEdit.setBounds(211, 151, 72, 23);
				getContentPane().add(btnEdit);

				JLabel lblAvailableConditions = new JLabel(
						"Available conditions");
				lblAvailableConditions.setBounds(10, 59, 122, 14);
				getContentPane().add(lblAvailableConditions);
				
				JLabel lblSelectedConditions = new JLabel("Selected conditions");
				lblSelectedConditions.setBounds(293, 59, 122, 14);
				getContentPane().add(lblSelectedConditions);
				btnEdit.addActionListener(new MyActionListener());

			}
		}

		// initialise elements
		iniLists();

	}

	private class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals("Cancel")) {
				setVisible(false);
				dispose();
			} else if (action.equals("Add")) {//add element from left to right list

				int index = 0;

				index = leftList.getSelectedIndex();
				if (!(index == -1)) {// if not empty list
					// remove from leftlist
					String leftListElement = leftList.getSelectedValue();
					leftListModel.remove(index);
					if (leftListElement.equals("Date")) {
						dateSide = "right";
					} else if (leftListElement.equals("Day")) {
						daySide = "right";
					} else if (leftListElement.equals("Time")) {
						timeSide = "right";
					} else if (leftListElement.equals("Temperature")) {
						temperatureSide = "right";
					} else if (leftListElement.equals("Location")) {
						locationSide = "right";
					} else if (leftListElement.equals("Stock price")) {
						stockSide = "right";
					} else if (leftListElement.equals("Description")) {
						descriptionSide = "right";
					}

					// add to rightlist
					int pos = rightList.getModel().getSize();
					rightListModel.add(pos, leftListElement);
				}

			} else if (action.equals("Remove")) {//remove element from right list, add to left list
				int index = 0;

				index = rightList.getSelectedIndex();
				if (!(index == -1)) {// if not empty list
					// remove from rightlist
					String rightListElement = rightList.getSelectedValue();
					rightListModel.remove(index);
					if (rightListElement.equals("Date")) {
						event.setDate("EMPTY");
						dateSide = "left";
					} else if (rightListElement.equals("Day")) {
						event.setDay("EMPTY");
						daySide = "left";
					} else if (rightListElement.equals("Time")) {
						event.setTime("EMPTY");
						timeSide = "left";
					} else if (rightListElement.equals("Temperature")) {
						event.setTemperature("EMPTY");
						temperatureSide = "left";
					} else if (rightListElement.equals("Location")) {
						event.setLocation("EMPTY");
						locationSide = "left";
					} else if (rightListElement.equals("Stock price")) {
						event.setStock("EMPTY");
						stockSide = "left";
					} else if (rightListElement.equals("Description")) {
						event.setDescription("EMPTY");
						descriptionSide = "left";
					}

					if (rightList.getModel().getSize() == 0
							|| (rightList.getModel().getSize() == 1 && rightListElement
									.equals("Description"))) {
						selection = false;
					}

					// add to leftlist
					int pos = leftList.getModel().getSize();
					leftListModel.add(pos, rightListElement);
				}

			} else if (action.equals("Clear")) {//clear all elements off right list
				iniLists();
				event = new BrainwavesEvent();

			} else if (action.equals("Edit")) {//edit element in right list, used to add data to an event
				int index = 0;

				index = rightList.getSelectedIndex();
				if (!(index == -1)) {// if not empty list
					openWindow(rightList);
				}
			} else if (action.equals("Create")) {//create the event if all selected right list items have been filled in

				// check whether date & day combo is not in the past
				if (event.getDate() != "EMPTY" && event.getDay() != "EMPTY") {
					Integer dayInteger = Integer.parseInt(event.getDay());
					int day = dayInteger.intValue();

					// check whether date & day combo is not in the past
					Calendar todayCal = new GregorianCalendar();
					todayCal = Calendar.getInstance();
					int thisYear = todayCal.get(Calendar.YEAR);
					int thisMonth = todayCal.get(Calendar.MONTH); // 0-11
					int thisDay = todayCal.get(Calendar.DAY_OF_MONTH);

					String date = event.getDate();
					String[] splitDate = date.split("/");
					Calendar selectedCal = new GregorianCalendar();
					selectedCal.set(Calendar.MONTH,
							Integer.parseInt(splitDate[0]) - 1);
					selectedCal.set(Calendar.YEAR,
							Integer.parseInt(splitDate[1]));
					selectedCal.set(Calendar.DAY_OF_MONTH, day);
					int selectedYear = selectedCal.get(Calendar.YEAR);
					int selectedMonth = selectedCal.get(Calendar.MONTH);
					int selectedDay = selectedCal.get(Calendar.DAY_OF_MONTH);

					if (thisYear == selectedYear && thisMonth == selectedMonth
							&& thisDay > selectedDay) {
						JOptionPane.showMessageDialog(null,
								"Please set a future date and day combination",
								"Warning", JOptionPane.WARNING_MESSAGE);
						return;
					}

					// first check validity of date if date/day combo has been
					// selected
					if (day > 10) {
						// get date and day values from the spinners

						Calendar cal = new GregorianCalendar();
						cal.set(Calendar.MONTH,
								Integer.parseInt(splitDate[0]) - 1);
						cal.set(Calendar.YEAR, Integer.parseInt(splitDate[1]));
						SimpleDateFormat sf = new SimpleDateFormat("ddMMMyyyy");
						sf.setLenient(false);
						int monthNumber = cal.get(Calendar.MONTH);
						String month = parent.getMonth(monthNumber);
						int year = cal.get(Calendar.YEAR);
						String formatted = "" + day + month.charAt(0)
								+ month.charAt(1) + month.charAt(2) + year;
						try {
							// parse formatted string, if incorrect catch
							// exception
							sf.parse(formatted);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(null,
									"Invalid Date & Day combination",
									"Warning", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}

				}

				// check whether all elements in right list have been filled in
				if (event.getDate() != "EMPTY") {
					selection = true;
				} else if (dateSide.equals("right")) {
					JOptionPane.showMessageDialog(null, "Please set the date",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (event.getDay() != "EMPTY") {
					selection = true;
				} else if (daySide.equals("right")) {
					JOptionPane.showMessageDialog(null, "Please set the day",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (event.getTime() != "EMPTY") {
					selection = true;
				} else if (timeSide.equals("right")) {
					JOptionPane.showMessageDialog(null, "Please set the time",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (event.getTemperature() != "EMPTY") {
					selection = true;
				} else if (temperatureSide.equals("right")) {
					JOptionPane.showMessageDialog(null,
							"Please set the temperature", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (event.getLocation() != "EMPTY") {
					selection = true;
				} else if (locationSide.equals("right")) {
					JOptionPane.showMessageDialog(null,
							"Please set the location", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (event.getStock() != "EMPTY") {
					selection = true;
				} else if (stockSide.equals("right")) {
					JOptionPane.showMessageDialog(null, "Please set the stock",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (event.getDescription() == "EMPTY"
						&& descriptionSide.equals("right")) {
					JOptionPane.showMessageDialog(null,
							"Please set the description", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (selection) {// make sure at least one element selected
					String name = nameField.getText().trim();
					if (name.equals("Event name") || name.equals("")) {
						JOptionPane.showMessageDialog(null,
								"Please enter a name for the event", "Warning",
								JOptionPane.WARNING_MESSAGE);
					} else {

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
									"Please enter a unique name", "Warning",
									JOptionPane.WARNING_MESSAGE);
						}
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Please select at least one condition", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}

			}
		}
	}

	
	
	/**
	 * Initialise left list
	 */
	private void iniLists() {
		rightListModel.clear();
		leftListModel.clear();
		leftListModel.addElement("Date");
		leftListModel.addElement("Day");
		leftListModel.addElement("Time");
		leftListModel.addElement("Location");
		leftListModel.addElement("Temperature");
		leftListModel.addElement("Stock price");
		leftListModel.addElement("Description");
		dateSide = "left";
		daySide = "left";
		timeSide = "left";
		temperatureSide = "left";
		locationSide = "left";
		stockSide = "left";
		descriptionSide = "left";

	}

	/**
	 * 
	 * @author Alexandros Lekkas
	 * Adds focus behaviour to textfields
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

	/**
	 * 
	 * @author Alexandros Lekkas
	 *	Main clicking behaviour for left and right lists
	 */
	class MyMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2 && !e.isConsumed()) {
				e.consume();
				JList list = (JList) e.getSource();
				int index = 0;
				if (list.equals(leftList)) {
					index = leftList.locationToIndex(e.getPoint());
					if (!(index == -1)) {// if not empty list
						// remove from leftlist
						String leftListElement = leftList.getSelectedValue();
						leftListModel.remove(index);
						if (leftListElement.equals("Date")) {
							dateSide = "right";
						} else if (leftListElement.equals("Day")) {
							daySide = "right";
						} else if (leftListElement.equals("Time")) {
							timeSide = "right";
						} else if (leftListElement.equals("Temperature")) {
							temperatureSide = "right";
						} else if (leftListElement.equals("Location")) {
							locationSide = "right";
						} else if (leftListElement.equals("Stock price")) {
							stockSide = "right";
						} else if (leftListElement.equals("Description")) {
							descriptionSide = "right";
						}

						// add to rightlist
						int pos = rightList.getModel().getSize();
						rightListModel.add(pos, leftListElement);

					}
				} else {
					index = rightList.locationToIndex(e.getPoint());
					// open window with selected condition to enter data
					openWindow(rightList);

				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	/**
	 * 
	 * @author Alexandros Lekkas
	 *	Key behaviour for left list
	 */
	public class LeftListKeyListener extends KeyAdapter {
		private JList<String> list = null;

		public LeftListKeyListener(JList<String> leftList) {
			super();
			this.list = leftList;
		}

		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				String selectionItem = (String) list.getSelectedValue();
				// remove from leftlist
				String leftListElement = leftList.getSelectedValue();
				leftListModel.removeElement(leftListElement); // TODO test
				if (leftListElement.equals("Date")) {
					dateSide = "right";
				} else if (leftListElement.equals("Day")) {
					daySide = "right";
				} else if (leftListElement.equals("Time")) {
					timeSide = "right";
				} else if (leftListElement.equals("Temperature")) {
					temperatureSide = "right";
				} else if (leftListElement.equals("Location")) {
					locationSide = "right";
				} else if (leftListElement.equals("Stock price")) {
					stockSide = "right";
				} else if (leftListElement.equals("Description")) {
					descriptionSide = "right";
				}

				// add to rightlist
				int pos = rightList.getModel().getSize();
				rightListModel.add(pos, leftListElement);

			}
		}
	}

	/**
	 * 
	 * @author Alexandros Lekkas
	 * Key behaviour for right list
	 */
	public class RightListKeyListener extends KeyAdapter {
		private JList<String> list = null;

		public RightListKeyListener(JList<String> leftList) {
			super();
			this.list = rightList;
		}

		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				String selectionItem = (String) list.getSelectedValue();
				openWindow(rightList);

			}
		}
	}

	/**
	 * Opens a window when activating a right list element in order to fill in the condition data
	 * @param rightList the list of selected conditions
	 */
	private void openWindow(JList<String> rightList) {
		String rightListElement = rightList.getSelectedValue();
		if (rightListElement.equals("Date")) {
			NewDateView newDateView = new NewDateView(event);
			newDateView.setVisible(true);
			newDateView.requestFocus();

		} else if (rightListElement.equals("Day")) {
			NewDayView newDayView = new NewDayView(event);
			newDayView.setVisible(true);
			newDayView.requestFocus();

		} else if (rightListElement.equals("Time")) {
			NewTimeView newTimeView = new NewTimeView(event);
			newTimeView.setVisible(true);
			newTimeView.requestFocus();

		} else if (rightListElement.equals("Location")) {
			NewLocationView newLocationView = new NewLocationView(event);
			newLocationView.setVisible(true);
			newLocationView.requestFocus();

		} else if (rightListElement.equals("Temperature")) {
			NewTemperatureView newTemperatureView = new NewTemperatureView(
					event);
			newTemperatureView.setVisible(true);
			newTemperatureView.requestFocus();

		} else if (rightListElement.equals("Stock price")) {
			NewStockView newStockView = new NewStockView(event);
			newStockView.setVisible(true);
			newStockView.requestFocus();

		} else if (rightListElement.equals("Description")) {
			NewDescriptionView newDescriptionView = new NewDescriptionView(
					event);
			newDescriptionView.setVisible(true);
			newDescriptionView.requestFocus();

		}

	}
}
