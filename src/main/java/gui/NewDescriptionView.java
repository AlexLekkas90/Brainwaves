package main.java.gui;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import main.java.logic.BrainwavesEvent;

/**
 * @author Alexandros Lekkas Description window that allows the user to add a description to the event
  */
public class NewDescriptionView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private BrainwavesEvent event;
	private JLabel boxDesc;
	private JTextField descField;


	/**
	 * Create the dialog.
	 */
	public NewDescriptionView(BrainwavesEvent event) {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.event = event;
		setTitle("Description");
		setResizable(false);
		setBounds(100, 100, 242, 163);
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
					// Description textfield
					CustomDocument document3 = new CustomDocument(50);
					descField = new JTextField(document3, "", 0);
					boxDesc = new JLabel("Description:");
					boxDesc.setBounds(6, 11, 97, 20);
					getContentPane().add(boxDesc);
					descField.setBounds(117, 11, 111, 26);
					getContentPane().add(descField);

					
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

				String description = (String) descField.getText();
				event.setDescription(description);

				setVisible(false);
				dispose();

			}
		}
	}


}
