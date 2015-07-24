package gui;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;

import logic.BrainwavesEvent;

import javax.swing.JLabel;

/**
 * @author Alexandros Lekkas Date window that allows the user to add a date to the event
 */
public class NewDateView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel boxDate;
	private JSpinner dateSpinner;
	private BrainwavesEvent event;


	/**
	 * Create the dialog.
	 */
	public NewDateView(BrainwavesEvent event) {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.event = event;
		setTitle("Date");
		setResizable(false);
		setBounds(100, 100, 253, 163);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar cal = Calendar.getInstance();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(6, 88, 235, 41);
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
					if(!event.getDate().equals("EMPTY")){
						String[] dateString = event.getDate().split("/");
						cal.set(Calendar.MONTH, Integer.parseInt(dateString[0]) - 1);
						cal.set(Calendar.YEAR, Integer.parseInt(dateString[1]));
						initDate = cal.getTime();
					}
					
					cal = Calendar.getInstance();
					cal.add(Calendar.MONTH, +1);
					cal.add(Calendar.MONTH, -2);
					Date earliestDate = cal.getTime();
					cal.add(Calendar.MONTH, +2);
					cal.add(Calendar.YEAR, 10);
					Date latestDate = cal.getTime();
					SpinnerModel dateModel = new SpinnerDateModel(initDate,
							earliestDate, latestDate, Calendar.YEAR);
					dateSpinner = new JSpinner(dateModel);
					boxDate = new JLabel("Date:");
					boxDate.setBounds(6, 17, 57, 20);
					getContentPane().add(boxDate);
					dateSpinner.setBounds(127, 11, 114, 33);
					dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner,
							"MM/yyyy"));
					getContentPane().add(dateSpinner);

					
				}
			}
		}
	}



	/**
	 * @author Alexandros Lekkas Action listener class for the add and
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
						setVisible(false);
						dispose();
					

			}
		}
	}


}
