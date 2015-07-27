package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class GenericInfoView extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextArea aboutInfo;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		try {
//			AboutView dialog = new AboutView();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public GenericInfoView(String message) {
		setResizable(false);
		setTitle("Info Panel");
		setBounds(100, 100, 528, 245);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			contentPanel.setLayout(null);
			{
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBorder(null);
				scrollPane.setBounds(10, 11, 502, 162);
				contentPanel.add(scrollPane);
				{
					aboutInfo = new JTextArea();
					aboutInfo.setLineWrap(true);
					aboutInfo.setWrapStyleWord(true);
					aboutInfo.setEditable(false);
					aboutInfo
					.setBackground(UIManager.getColor("Button.background"));
					aboutInfo.setMargin(new Insets(5, 5, 5, 5));
					scrollPane.setViewportView(aboutInfo);
					aboutInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
					aboutInfo.setText(message);//TODO more info
				}
			}
			
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JButton okButton = new JButton("Close");
				okButton.setActionCommand("Close");
				okButton.addActionListener(new MyActionListener());
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	/**
	 * @author Alexandros Lekkas
	 * Action listener for the Close button
	 */
	private class MyActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			if (action.equals("Close")) {
				setVisible(false);
				dispose();
			}
		}
	}

}
