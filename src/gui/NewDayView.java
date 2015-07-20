package gui;

import java.awt.FlowLayout;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;

import logic.BrainwavesEvent;

import javax.swing.JLabel;

/**
 * @author Alexandros Lekkas Event window that allows the user to add one new
 *         event to the DB
 */
public class NewDayView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel boxDate;
	private BrainwavesEvent event;
	private JSpinner daySpinner;
	private JLabel boxDay;


	/**
	 * Create the dialog.
	 */
	public NewDayView(BrainwavesEvent event) {
		this.event = event;
		setTitle("Date");
		setResizable(false);
		setBounds(100, 100, 225, 163);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar cal = Calendar.getInstance();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(6, 90, 205, 33);
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
					// day spinner
					SpinnerModel dayModel = new SpinnerNumberModel(1, 1, 31, 1);
					if(!event.getDay().equals("EMPTY")){
						 dayModel = new SpinnerNumberModel(Integer.parseInt(event.getDay()), 1, 31, 1);
					}
					
					daySpinner = new JSpinner(dayModel);
					boxDay = new JLabel("Day:");
					boxDay.setBounds(6, 11, 57, 20);
					getContentPane().add(boxDay);
					daySpinner.setBounds(123, 11, 88, 26);
					NumberEditor ne_daySpinner = new JSpinner.NumberEditor(
							daySpinner, "00");
					ne_daySpinner.setToolTipText("");
					daySpinner.setEditor(ne_daySpinner);
					getContentPane().add(daySpinner);

					
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

				Integer day2 = (Integer) daySpinner.getValue();
				String dayString = String.format("%02d", day2);
				event.setDay(dayString);

				setVisible(false);
				dispose();

			}
		}
	}


}
