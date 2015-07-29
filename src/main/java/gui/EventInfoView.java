package main.java.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.UIManager;

import main.java.logic.BrainwavesEvent;

/**
 * @author Alexandros Lekkas
 * Event information window
 */
public class EventInfoView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextArea eventInfo;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// try {
	// EventInfoView dialog = new EventInfoView();
	// dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	// dialog.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	
	/**
	 * Create the window
	 */
	public EventInfoView() {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Event Information");
		setBounds(100, 100, 450, 204);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			scrollPane.setBounds(10, 10, 424, 122);
			contentPanel.add(scrollPane);
			{
				eventInfo = new JTextArea();
				eventInfo.setEditable(false);
				eventInfo.setBorder(null);
				scrollPane.setViewportView(eventInfo);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(new MyActionListener());
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	/**
	 * @param event the Brainwaves event that this window shows the information of
	 */
	public EventInfoView(BrainwavesEvent event) {
		setResizable(false);
		setTitle("Event Information");
		setBounds(100, 100, 472, 225);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			scrollPane.setBounds(0, 0, 466, 158);
			contentPanel.add(scrollPane);
			{
				eventInfo = new JTextArea();
				eventInfo
						.setBackground(UIManager.getColor("Button.background"));
				eventInfo.setMargin(new Insets(5, 5, 5, 5));
				eventInfo.setEditable(false);
				scrollPane.setViewportView(eventInfo);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new EmptyBorder(3, 3, 3, 3));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(new MyActionListener());
				getRootPane().setDefaultButton(okButton);
			}
		}
		eventInfo.setText(event.print());
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
