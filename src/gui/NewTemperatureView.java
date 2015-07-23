package gui;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.JSpinner;

import logic.BrainwavesEvent;

import javax.swing.JLabel;

/**
 * @author Alexandros Lekkas Temperature window that allows the user to add a temperature to the event
 */
public class NewTemperatureView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private BrainwavesEvent event;
	private JSpinner symbolSpinner;
	private JLabel boxTemp;
	private JSpinner tempSpinner;


	/**
	 * Create the dialog.
	 */
	public NewTemperatureView(BrainwavesEvent event) {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.event = event;
		setTitle("Date");
		setResizable(false);
		setBounds(100, 100, 251, 162);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar.getInstance();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(6, 90, 229, 33);
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
			
					// temperature spinner
					SpinnerModel symbolModel = new SpinnerListModel(
							new ArrayList<String>(Arrays.asList("<", ">")));
					SpinnerModel tempModel = new SpinnerNumberModel(0, -30, 60,
							1);
					symbolSpinner = new JSpinner(symbolModel);
					tempSpinner = new JSpinner(tempModel);
					boxTemp = new JLabel("Temperature:");
					boxTemp.setBounds(6, 11, 111, 20);
					getContentPane().add(boxTemp);
					symbolSpinner.setBounds(123, 11, 42, 26);
					tempSpinner.setBounds(169, 11, 42, 26);
					getContentPane().add(symbolSpinner);
					NumberEditor ne_tempSpinner = new JSpinner.NumberEditor(
							tempSpinner, "#");
					ne_tempSpinner.setToolTipText("");
					tempSpinner.setEditor(ne_tempSpinner);
					getContentPane().add(tempSpinner);

					
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

				String symbol = (String) symbolSpinner.getValue();
				Integer temp = (Integer) tempSpinner.getValue();
				String tempString = "" + symbol + ":" + temp.intValue();
				event.setTemperature(tempString);

				setVisible(false);
				dispose();

			}
		}
	}


}
