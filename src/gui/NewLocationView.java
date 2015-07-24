package gui;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import logic.BrainwavesEvent;

import javax.swing.JLabel;

/**
 * @author Alexandros Lekkas Location window that allows the user to add a location to the event
 */
public class NewLocationView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private BrainwavesEvent event;
	private JTextField locField;
	private JLabel boxLoc;


	/**
	 * Create the dialog.
	 */
	public NewLocationView(BrainwavesEvent event) {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.event = event;
		setTitle("Date");
		setResizable(false);
		setBounds(100, 100, 256, 162);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar.getInstance();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(6, 90, 235, 41);
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
			
					CustomDocument document2 = new CustomDocument(30);
					locField = new JTextField(document2, "", 0);
					if(!event.getLocation().equals("EMPTY")){
						locField = new JTextField(document2, event.getLocation(), 0);
					}
					
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
					boxLoc = new JLabel("Location:");
					boxLoc.setBounds(10, 10, 88, 20);
					getContentPane().add(boxLoc);
					locField.setBounds(130, 7, 111, 26);
					getContentPane().add(locField);

					
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
				String location = (String) locField.getText();
				if(location.equals("") || location.equals(" ")){
					JOptionPane.showMessageDialog(null,
							"Please enter a location",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				
				event.setLocation(location);

				setVisible(false);
				dispose();

			}
		}
	}


}
