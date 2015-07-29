package main.java.gui;



import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import main.java.logic.BrainwavesEvent;
import main.java.logic.EventRepository;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DeleteConfirmationView extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private BrainwavesEvent event; // The event to be deleted
	private EventRepository repo;
	private JList<BrainwavesEvent> resultsList;
	private DefaultListModel<BrainwavesEvent> resultsModel;
	private JButton cancelButton;


	/**
	 * Create the dialog.
	 */
	public DeleteConfirmationView(BrainwavesEvent event, EventRepository repo, JList<BrainwavesEvent> resultsList,  DefaultListModel<BrainwavesEvent> resultsModel) {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Delete event");
		setResizable(false);
		this.event = event;
		this.repo = repo;
		this.resultsList = resultsList;
		this.resultsModel = resultsModel;
		setBounds(100, 100, 346, 147);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Are you sure you want to delete the event "+ event.getName() + "?");
		lblNewLabel.setBounds(10, 11, 320, 14);
		contentPanel.add(lblNewLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnDelete = new JButton("Delete");
				btnDelete.setActionCommand("Delete");
				btnDelete.addActionListener(new MyActionListener());
				buttonPane.add(btnDelete);
				getRootPane().setDefaultButton(btnDelete);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new MyActionListener());
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * @author Alexandros Lekkas
	 * Action listener for the Delete and Cancel buttons
	 */
	private class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equalsIgnoreCase("Cancel")) {
				DeleteConfirmationView.this.setVisible(false);
				DeleteConfirmationView.this.dispose();
			} else if (action.equals("Delete")){
				int index = resultsList.getSelectedIndex();
				repo.deleteEvent(event);
				resultsModel.remove(index);
				MainView.removeUpcomingListElement(event.getName());
				MainView.removeActiveListElement(event.getName());
				setVisible(false);
				dispose();
			}
		}
	}
}
