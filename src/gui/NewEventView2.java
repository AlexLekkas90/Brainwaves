package gui;



import gui.MainView.EventsKeyListener;
import gui.MainView.MyMouseListener;
import gui.NewEventView.MyFocusListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import logic.BrainwavesEvent;
import logic.EventRepository;

public class NewEventView2 extends JDialog {
	private JTextField nameField;
	private DefaultListModel<String> leftListModel;
	private JList<String> leftList;
	private DefaultListModel<String> rightListModel;
	private JList<String> rightList;
	BrainwavesEvent event;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		try {
//			NewEventView2 dialog = new NewEventView2();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public NewEventView2() {
		setBounds(100, 100, 544, 532);
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
				if (!(Character.isLetter(c)
						|| (c == KeyEvent.VK_BACK_SPACE)
						|| (c == KeyEvent.VK_DELETE) || (Character
						.isSpaceChar(c)) || Character.isDigit(c))) {
					getToolkit().beep();
					key.consume();
				}

			}
		});
		
		
		// Left List
				JScrollPane leftListPane = new JScrollPane();
				leftListPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
						null, null, null));
				leftListPane.setBounds(10, 84, 191, 250);
				getContentPane().add(leftListPane);

				leftListModel = new DefaultListModel<String>();
				leftList = new JList<String>(leftListModel);
				leftList.addMouseListener(new MyMouseListener());
				leftList.addKeyListener(new EventsKeyListener(
						leftList));
				leftListPane.setViewportView(leftList);

				// Right List
				JScrollPane rightListPane = new JScrollPane();
				rightListPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
						null, null, null));
				rightListPane.setBounds(293, 84, 191, 250);
				getContentPane().add(rightListPane);

				rightListModel = new DefaultListModel<String>();
				rightList = new JList<String>(rightListModel);
				rightList.addMouseListener(new MyMouseListener());
				rightList.addKeyListener(new EventsKeyListener(
						rightList));
				rightListPane.setViewportView(rightList);
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(10, 450, 508, 33);
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
				
				//Buttons
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
				btnClear.setBounds(211, 311, 72, 23);
				getContentPane().add(btnClear);
				btnClear.addActionListener(new MyActionListener());
				
				JButton btnEdit = new JButton("Edit");
				btnEdit.setActionCommand("Edit");	
				btnEdit.setBounds(412, 341, 72, 23);
				getContentPane().add(btnEdit);
				btnEdit.addActionListener(new MyActionListener());
				
			}
		}
		
		//initialise elements 
		iniLists();
		
	}
	

	private class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals("Cancel")) {
				setVisible(false);
				dispose();
			} else if (action.equals("Add")) {

				int index = 0;

					index = leftList.getSelectedIndex();
					if (!(index == -1)){//if not empty list
					//remove from leftlist
					String leftListElement = leftList.getSelectedValue();
					leftListModel.remove(index);
					
					//add to rightlist
					int pos = rightList.getModel().getSize();
					rightListModel.add(pos, leftListElement);
					}

				
			}else if (action.equals("Remove")) {
				int index = 0;

				index = rightList.getSelectedIndex();
				if (!(index == -1)){//if not empty list
				//remove from rightlist
				String rightListElement = rightList.getSelectedValue();
				rightListModel.remove(index);
				
				//add to leftlist
				int pos = leftList.getModel().getSize();
				leftListModel.add(pos, rightListElement);
				}
				
				
			}else if (action.equals("Clear")) {
				iniLists();
				event = new BrainwavesEvent();
				
			}else if (action.equals("Edit")) {
				int index = 0;

				index = rightList.getSelectedIndex();
				if (!(index == -1)){//if not empty list
					String rightListElement = rightList.getSelectedValue();
					if(rightListElement.equals("Date")){
						NewDateView newDateView = new NewDateView(event);
						newDateView.setVisible(true);
						newDateView.requestFocus();
						
						
					} else if(rightListElement.equals("Day")){
						NewDayView newDayView = new NewDayView(event);
						newDayView.setVisible(true);
						newDayView.requestFocus();
						
						
					} else if(rightListElement.equals("Time")){
						NewTimeView newTimeView = new NewTimeView(event);
						newTimeView.setVisible(true);
						newTimeView.requestFocus();
						
						
					} else if(rightListElement.equals("Location")){
						NewLocationView newLocationView = new NewLocationView(event);
						newLocationView.setVisible(true);
						newLocationView.requestFocus();
						
					} else if(rightListElement.equals("Temperature")){
//						NewTemperatureView newTemperatureView = new NewTemperatureView(event);
//						newTemperatureView.setVisible(true);
//						newTemperatureView.requestFocus();
						
					} else if(rightListElement.equals("Stock price")){
//						NewStockView newStockView = new NewStockView(event);
//						newStockView.setVisible(true);
//						newStockView.requestFocus();
						
					} else if(rightListElement.equals("Description")){
//						NewDescriptionView newDescriptionView = new NewDescriptionView(event);
//						newDescriptionView.setVisible(true);
//						newDescriptionView.requestFocus();
						
					}
					
					
					
					
					
				}
				
				
				
			}else if (action.equals("Create")) {
				
			}
		}
	}
	
	private void iniLists(){
		rightListModel.clear();
		leftListModel.clear();
		leftListModel.addElement("Date");
		leftListModel.addElement("Day");
		leftListModel.addElement("Time");
		leftListModel.addElement("Location");
		leftListModel.addElement("Temperature");
		leftListModel.addElement("Stock price");
		leftListModel.addElement("Description");
		
	}
	

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
				if (list.equals(leftList)) {
					index = leftList.locationToIndex(e.getPoint());
					if (!(index == -1)){//if not empty list
					//remove from leftlist
					String leftListElement = leftList.getSelectedValue();
					leftListModel.remove(index);
					
					//add to rightlist
					int pos = rightList.getModel().getSize();
					rightListModel.add(pos, leftListElement);
					
					}
				} else {
					index = rightList.locationToIndex(e.getPoint());
					//open window with selected condition to enter data
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
	public class EventsKeyListener extends KeyAdapter {
		private JList<String> list = null;
		public EventsKeyListener(JList<String> leftList) {
			super();
			this.list = leftList;
		}

		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				String selectionItem = (String) list
						.getSelectedValue();

			}
		}
	}
}
