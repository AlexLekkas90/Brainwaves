package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JLabel;

import logic.BrainwavesEvent;
import logic.EventRepository;

/**
 * @author Alexandros Lekkas
 *	Advanced search class, this class performs a more detailed search on the provided EventRepository and displays the results in the same way that regular search does.
 */
public class AdvancedSearchView extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JCheckBox boxDate;
	private JSpinner daySpinner;
	private JCheckBox boxDay;
	private SpinnerModel dateModel;
	private SpinnerModel dayModel;
	private JCheckBox boxDateRange;
	private JSpinner dateRangeSpinner;
	private JSpinner dateRangeSpinner2;
	private JTextField locField;
	private JCheckBox boxLoc;
	private JSpinner symbolSpinner;
	private JSpinner tempSpinner;
	private JCheckBox boxTemp;
	private ButtonGroup group;
	private EventRepository repo;
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			AdvancedSearchView dialog = new AdvancedSearchView();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	private JTextField stockNameField;
	private JFormattedTextField stockValueField;
	private JCheckBox boxStockName;
	private JSpinner stockSymbolSpinner;
	private JCheckBox boxStockValue;


	/**
	 * Create the search view
	 * @param repo the event repository on which to perform the search
	 */
	public AdvancedSearchView(EventRepository repo) {
		setResizable(false);
		this.repo = repo;
		setTitle("Advanced Search");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, +1);
		Date initDate = cal.getTime();
		cal.add(Calendar.MONTH, -2);
		Date earliestDate = cal.getTime();
		cal.add(Calendar.MONTH, +2);
		cal.add(Calendar.YEAR, 10);
		Date latestDate = cal.getTime();

		{
			SpinnerModel dateModel = new SpinnerDateModel(initDate,
					earliestDate, latestDate, Calendar.YEAR);
			SpinnerModel dateModel2 = new SpinnerDateModel(initDate,
					earliestDate, latestDate, Calendar.YEAR);
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, +2);
			Date initDate2 = cal.getTime();
			cal.add(Calendar.MONTH, -2);
			Date earliestDate2 = cal.getTime();
			cal.add(Calendar.MONTH, +3);
			cal.add(Calendar.YEAR, 10);
			Date latestDate2 = cal.getTime();
			SpinnerModel dateModel3 = new SpinnerDateModel(initDate2,
					earliestDate2, latestDate2, Calendar.YEAR);
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			// date spinner
//			boxDate = new JCheckBox("Date:");
//			contentPanel.add(boxDate);
//			boxDate.setBounds(6, 11, 57, 23);
//			boxDate.setActionCommand("BoxDate");
////			boxDate.addItemListener(new MyItemListener());
//			dateSpinner = new JSpinner(dateModel);
//			contentPanel.add(dateSpinner);
//			dateSpinner.setBounds(132, 11, 88, 26);
//			dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner,
//					"MM/yyyy"));


			// date range spinner
			boxDateRange = new JCheckBox("Date range:");
			contentPanel.add(boxDateRange);
			boxDateRange.setBounds(6, 13, 105, 23);
			dateRangeSpinner = new JSpinner(dateModel2);
			contentPanel.add(dateRangeSpinner);
			dateRangeSpinner.setBounds(132, 11, 88, 26);
			dateRangeSpinner.setEditor(new JSpinner.DateEditor(
					dateRangeSpinner, "MM/yyyy"));
			dateRangeSpinner2 = new JSpinner(dateModel3);
			contentPanel.add(dateRangeSpinner2);
			dateRangeSpinner2.setBounds(249, 11, 88, 26);
			dateRangeSpinner2.setEditor(new JSpinner.DateEditor(
					dateRangeSpinner2, "MM/yyyy"));
			cal.add(Calendar.MONTH, +1);
			Date initDate1 = cal.getTime();
			cal.add(Calendar.MONTH, -2);
			Date earliestDate1 = cal.getTime();
			cal.add(Calendar.MONTH, +2);
			cal.add(Calendar.YEAR, 10);
			Date latestDate1 = cal.getTime();
			SpinnerModel dateRangeModel = new SpinnerDateModel(initDate1,
					earliestDate1, latestDate1, Calendar.YEAR);

			  
			
			// day spinner
			SpinnerModel dayModel = new SpinnerNumberModel(1, 1, 31, 1);
			boxDay = new JCheckBox("Day:");
			contentPanel.add(boxDay);
			boxDay.setBounds(6, 50, 57, 23);
			daySpinner = new JSpinner(dayModel);
			contentPanel.add(daySpinner);
			daySpinner.setBounds(132, 48, 88, 26);
			NumberEditor ne_daySpinner = new JSpinner.NumberEditor(daySpinner,
					"00");
			ne_daySpinner.setToolTipText("");
			daySpinner.setEditor(ne_daySpinner);
			boxLoc = new JCheckBox("Location:");
			contentPanel.add(boxLoc);
			boxLoc.setBounds(6, 87, 88, 23);

			// location textfield
			CustomDocument document2 = new CustomDocument(30);
			locField = new JTextField(document2, "", 0);
			contentPanel.add(locField);
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
			locField.setBounds(132, 85, 88, 26);
			
			
			// temperature spinner
			SpinnerModel symbolModel = new SpinnerListModel(
					new ArrayList<String>(Arrays.asList("<", ">")));
			SpinnerModel tempModel = new SpinnerNumberModel(0, -30, 70,
					1);
			symbolSpinner = new JSpinner(symbolModel);
			contentPanel.add(symbolSpinner);
			symbolSpinner.setBounds(132, 122, 42, 26);
			tempSpinner = new JSpinner(tempModel);
			contentPanel.add(tempSpinner);
			tempSpinner.setBounds(178, 122, 42, 26);
			NumberEditor ne_tempSpinner = new JSpinner.NumberEditor(
					tempSpinner, "#");
			ne_tempSpinner.setToolTipText("");
			tempSpinner.setEditor(ne_tempSpinner);
			boxTemp = new JCheckBox("Temperature:");
			contentPanel.add(boxTemp);
			boxTemp.setBounds(6, 124, 105, 23);
			
			JLabel lblTo = new JLabel("to");
			lblTo.setBounds(230, 11, 25, 26);
			contentPanel.add(lblTo);
//			group = new ButtonGroup();
//		    group.add(boxDate);
//		    group.add(boxDateRange);
			
			
			
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
			
			boxStockName = new JCheckBox("Stock Name:");
			boxStockName.setBounds(6, 161, 88, 20);
			contentPanel.add(boxStockName);
			boxStockValue = new JCheckBox("Stock Value:");
			boxStockValue.setBounds(6, 198, 88, 20);
			contentPanel.add(boxStockValue);
			stockNameField.setBounds(132, 161, 42, 26);
			contentPanel.add(stockNameField);
			stockValueField.setBounds(178, 198, 42, 26);
			contentPanel.add(stockValueField);
			SpinnerModel stockSymbolModel = new SpinnerListModel(
					new ArrayList<String>(Arrays.asList("<", ">")));
			stockSymbolSpinner = new JSpinner(stockSymbolModel);
			stockSymbolSpinner.setBounds(132, 198, 42, 26);
			contentPanel.add(stockSymbolSpinner);
			
			// button menu
			{
				JButton searchButton = new JButton("Search");
				searchButton.setActionCommand("Search");
				buttonPane.add(searchButton);
				getRootPane().setDefaultButton(searchButton);
				searchButton.addActionListener(new MyActionListener());
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new MyActionListener());
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * @author Alexandros Lekkas
	 *	Action listener class for the buttons and search
	 */
	private class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals("Cancel")) {
				setVisible(false);
				dispose();
			} else if (action.equals("Search")) {
				int selectionMin = 0;
				if(boxDay.isSelected()){
					selectionMin++;
				}
				if(boxDateRange.isSelected()){
					selectionMin++;
				}
				if(boxLoc.isSelected()){
					
					selectionMin++;
				}
				if(boxTemp.isSelected()){
					selectionMin++;
				}
				if(boxStockName.isSelected()){
					selectionMin++;
				}
				if(boxStockValue.isSelected()){
					selectionMin++;
				}
				
				if(selectionMin == 0){
					JOptionPane.showMessageDialog(null,
							"Please select at least one condition", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}else{
				ArrayList<BrainwavesEvent> searchResults = new ArrayList<BrainwavesEvent>();
				BrainwavesEvent currentEvent;
				int numOfMatches = 0;
				Iterator<BrainwavesEvent> it = repo.getEvents().iterator();
				while (it.hasNext()){
					currentEvent = it.next();
					numOfMatches = 0;
					if(boxDay.isSelected()){
						if (!currentEvent.getDay().equals("EMPTY")) {
							if (currentEvent.getDay().contains(daySpinner.getValue() + "")) {
								numOfMatches ++;
							}
						}
					}
					if(boxDateRange.isSelected()){
						if (!currentEvent.getDate().equals("EMPTY")) {
							Calendar eventCal = Calendar.getInstance();
							Calendar cal1, cal2;
							cal1 = new GregorianCalendar();
							cal1.setTime((Date)dateRangeSpinner.getValue());
							cal2= new GregorianCalendar();
							cal2.setTime((Date)dateRangeSpinner2.getValue());
							cal1.set(Calendar.HOUR_OF_DAY, 0);
							cal1.set(Calendar.MINUTE, 0);
							cal1.set(Calendar.SECOND, 0);
							cal1.set(Calendar.DAY_OF_MONTH, 1);
							cal2.set(Calendar.HOUR_OF_DAY, 0);
							cal2.set(Calendar.MINUTE, 0);
							cal2.set(Calendar.SECOND, 0);
							cal2.set(Calendar.DAY_OF_MONTH, 1);
							String[] dateString = currentEvent.getDate().split("/");
							eventCal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
							eventCal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
							if (eventCal.after(cal1) && eventCal.before(cal2)) {
								numOfMatches ++;
							}}
						}
						
						

					if(boxLoc.isSelected()){
						if (!currentEvent.getLocation().equals("EMPTY")) {
							
							if (currentEvent.getLocation().contains(locField.getText())) {
								
								numOfMatches ++;
							}
						}
						
					}
					if(boxTemp.isSelected()){
						if (!currentEvent.getLocation().equals("EMPTY")) {
							String[] temp = currentEvent.getTemperature().split(":");
							if (temp[0].equals(symbolSpinner.getValue()) && temp[1].equals(tempSpinner.getValue())) {
								numOfMatches ++;
								
							}
						}
					}
					if(boxStockName.isSelected()){
						if (!currentEvent.getStock().equals("EMPTY")) {
							String stockName = currentEvent.getStock().split(":")[0];
							if(stockName.contains(stockNameField.getText())){
								numOfMatches ++;
							}
						}
					}
					
						if (boxStockValue.isSelected()) {
							if (!currentEvent.getStock().equals("EMPTY")) {
								String symbol = currentEvent.getStock().split(":")[1];
								String stockVal = currentEvent.getStock().split(":")[2];
								if (symbol.equals(stockSymbolSpinner.getValue()) && stockVal.equals(stockValueField.getValue())) {
									numOfMatches ++;
									
								}
							}
						}
					
					if(numOfMatches == selectionMin){
					searchResults.add(currentEvent);
					}
				}
				if(searchResults.size() > 0){
					EventListView searchResultsView = new EventListView(
							searchResults, repo);
					searchResultsView
							.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					searchResultsView.setVisible(true);
					searchResultsView.requestFocus();
				} else {
					JOptionPane.showMessageDialog(null, "No events found.",
							"Search Results" + numOfMatches + " " + selectionMin, JOptionPane.INFORMATION_MESSAGE);
				}
				}

		}
		}
	}
}

