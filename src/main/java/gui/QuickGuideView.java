package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JEditorPane;

public class QuickGuideView extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JEditorPane aboutInfo;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			QuickGuideView dialog = new QuickGuideView();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public QuickGuideView() {
		setResizable(false);
		setBounds(100, 100, 576, 503);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setTitle("Quick Guide");
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			contentPanel.setLayout(null);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			scrollPane.setBounds(10, 11, 550, 381);
			contentPanel.add(scrollPane);

				aboutInfo = new JEditorPane("text/html", "");
				aboutInfo.setEditable(false);
				aboutInfo
				.setBackground(UIManager.getColor("Button.background"));
				aboutInfo.setMargin(new Insets(5, 5, 5, 5));
				scrollPane.setViewportView(aboutInfo);
				aboutInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
				aboutInfo.setText("<p><b>Main interface</b></p>\r\n\r\n<p>The main interface of Brainwaves contains information about current date, time, location and temperature. The quick search and event features allow the user to quickly find or add a new event to the system. The upcoming events list displays events that might become active within the next week, while the active events list shows all currently active events. Events are checked after opening the application and every half an hour afterwards.</p>\r\n\r\n<p><b>Creating an event</b></p>\r\n<p>There are two options when creating a new event, by clicking on the event menu item and selecting the new event option, or using the quick event feature on the main interface.</p> \r\n\r\n<p>When creating a new event through the new event window, select the desired conditions from the left list and add them to the right list. Go through each selected condition and fill in the data. Finally press the create event button, if everything is correctly set the new event will be created. Note each event requires a unique name and at least one condition, with the description being optional.</p>\r\n\r\n<p>To use the quick event feature, type the hash key followed by an event condition, a space and the data for that condition. You can chain several conditions in this way in any order. Note the same rules about event names and number of conditions apply here too. An example event using the quick event feature follows:  #name MyDoctorAppointment #date 7/2015 #day 5 #time 11:45</p>\r\n\r\n<p><b>Deleting an event</b></p>\r\n<p>To delete an event, search for it through the quick or advanced search options, or through the listing of all events under the event submenu. Select the event to be deleted and click on the delete button. Alternatively press the delete key on your keyboard. A confirmation window will pop up to ensure events aren't accidentally deleted. Note that events that depend on a specific date will be removed automatically 3 days after the specified date</p>");
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));


			
			
			{
				JButton okButton = new JButton("Close");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(new MyActionListener());
				getRootPane().setDefaultButton(okButton);
			}

		}
	}
	
	/**
	 * @author Alexandros Lekkas
	 * Action listener for the OK button
	 */
	private class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals("OK")) {
				setVisible(false);
				dispose();
			}
		}
	}

}
