package main.java.gui;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.JSpinner;
import javax.swing.JLabel;

import main.java.logic.BrainwavesEvent;

/**
 * @author Alexandros Lekkas Time window that allows the user to add a time to the event
 */
public class NewTimeView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private BrainwavesEvent event;
	private JSpinner hourSpinner;
	private JSpinner minuteSpinner;
	private JLabel boxTime;


	/**
	 * Create the dialog.
	 */
	public NewTimeView(BrainwavesEvent event) {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.event = event;
		setTitle("Time");
		setResizable(false);
		setBounds(100, 100, 225, 163);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar.getInstance();
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
					// time spinner
					SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
					SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59,
							1);
					if(!event.getTime().equals("EMPTY")){
						String[] timeString = event.getTime().split(":");
						hourModel = new SpinnerNumberModel(Integer.parseInt(timeString[0]), 0, 23, 1);
						 minuteModel = new SpinnerNumberModel(Integer.parseInt(timeString[1]), 0, 59,	1);
					}
					
					hourSpinner = new JSpinner(hourModel);
					minuteSpinner = new JSpinner(minuteModel);
					boxTime = new JLabel("Time (24h):");
					boxTime.setBounds(6, 11, 57, 20);
					getContentPane().add(boxTime);
					hourSpinner.setBounds(123, 11, 42, 26);
					minuteSpinner.setBounds(169, 11, 42, 26);
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

				Integer hour = (Integer) hourSpinner.getValue();
				Integer minute = (Integer) minuteSpinner.getValue();
				String timeString = "" + hour.intValue() + ":"
						+ String.format("%02d", minute);
				event.setTime(timeString);

				setVisible(false);
				dispose();

			}
		}
	}


}
