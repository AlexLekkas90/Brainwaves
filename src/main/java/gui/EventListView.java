package main.java.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import main.java.logic.BrainwavesEvent;
import main.java.logic.EventRepository;

/**
 * @author Alexandros Lekkas
 * Window that shows the provided events as a list, allows for deletion of events
 */
public class EventListView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private DefaultListModel<BrainwavesEvent> resultsModel;
	private JList<BrainwavesEvent> resultsList;
	private ArrayList<BrainwavesEvent> events;
	private EventRepository repo;

	// public static void main(String[] args) {
	// try {
	// SearchResultsView dialog = new SearchResultsView();
	// dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	// dialog.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * @param events the events that resulted from the search, events is always non empty
	 */
	public EventListView(ArrayList<BrainwavesEvent> events, EventRepository repo) {
		setResizable(false);
		this.repo = repo;
		setTitle("Events Listing");
		this.events = events;
		setBounds(100, 100, 390, 247);
		getContentPane().setLayout(new BorderLayout());
		{
			JScrollPane upcomingEvents = new JScrollPane();
			upcomingEvents.setBorder(new SoftBevelBorder(BevelBorder.LOWERED,
					null, null, null, null));
			upcomingEvents.setBounds(10, 181, 290, 138);
			resultsModel = new DefaultListModel<BrainwavesEvent>();
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JButton okButton = new JButton("Close");
				okButton.setActionCommand("Close");
				okButton.addActionListener(new MyActionListener());
				
				JButton btnDelete = new JButton("Delete");
				btnDelete.setActionCommand("Delete");
				btnDelete.addActionListener(new MyActionListener());
				buttonPane.add(btnDelete);
				getRootPane().setDefaultButton(btnDelete);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		JLabel lblNewLabel = new JLabel("Results");
		contentPanel.add(lblNewLabel);
		lblNewLabel.setBounds(10, 11, 109, 14);
		// Results list
		JScrollPane searchResults = new JScrollPane();
		contentPanel.add(searchResults);
		searchResults.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		searchResults.setBounds(10, 31, 364, 144);
		resultsList = new JList<BrainwavesEvent>(resultsModel);
		resultsList.addMouseListener(new MyMouseListener());
		resultsList.addKeyListener(new MyKeyListener(resultsList));
		searchResults.setViewportView(resultsList);

		addToList();
	}

	/**
	 * Add the events to the list
	 */
	private void addToList() {
		Iterator<BrainwavesEvent> it = events.iterator();
		while (it.hasNext()) {
			BrainwavesEvent e = it.next();
			resultsModel.addElement(e);
		}
	}

	/**
	 * @author Alexandros Lekkas
	 * Mouse listener that opens a new window with event information upon double clicking an event
	 */
	class MyMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getClickCount() == 2 && !e.isConsumed()) {
				e.consume();
				int index = 0;
				index = resultsList.locationToIndex(e.getPoint());
				EventInfoView eventInfoView = new EventInfoView(
						(BrainwavesEvent) resultsModel
								.getElementAt(index));
				eventInfoView.setVisible(true);
				eventInfoView.requestFocus();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	/**
	 * @author Alexandros Lekkas
	 * Key listener that opens a new window with information about the event once entered is pressed with a selected event
	 */
	public class MyKeyListener extends KeyAdapter {
		private JList list = null;

		public MyKeyListener(JList list) {
			super();
			this.list = list;
		}

		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
				BrainwavesEvent event = (BrainwavesEvent) list
						.getSelectedValue();
				EventInfoView eventInfoView = new EventInfoView(event);
				eventInfoView.setVisible(true);
				eventInfoView.requestFocus();
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
			if (action.equals("Close")) {
				setVisible(false);
				dispose();
			} else if (action.equals("Delete")){
				BrainwavesEvent event = (BrainwavesEvent) resultsList.getSelectedValue();
				DeleteConfirmationView deleteConfirm = new DeleteConfirmationView(event, repo, resultsList, resultsModel);
				deleteConfirm.setVisible(true);
				deleteConfirm.requestFocus();
				
				
			}
		}
	}
}
