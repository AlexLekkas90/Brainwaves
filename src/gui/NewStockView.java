package gui;

import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSpinner;

import logic.BrainwavesEvent;

import javax.swing.JLabel;

/**
 * @author Alexandros Lekkas Stock window that allows the user to add a stock to the event
 */
public class NewStockView extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private BrainwavesEvent event;
	private JTextField stockNameField;
	private JFormattedTextField stockValueField;
	private JLabel boxStock;
	private JSpinner stockSymbolSpinner;


	/**
	 * Create the dialog.
	 */
	public NewStockView(BrainwavesEvent event) {
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.event = event;
		setTitle("Date");
		setResizable(false);
		setBounds(100, 100, 267, 162);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 0, 0);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		Calendar.getInstance();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(6, 90, 245, 33);
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
			
					//Stock data
					CustomDocument document4 = new CustomDocument(5);
					stockNameField = new JTextField(document4, "", 0);
					stockNameField.addKeyListener(new KeyAdapter() {
						@Override
						public void keyTyped(KeyEvent key) {
							char c = key.getKeyChar();
							if (!(Character.isLetter(c) || Character.isDigit(c)
									|| (c == KeyEvent.VK_BACK_SPACE)
									|| (c == KeyEvent.VK_DELETE) || (Character
									.isSpaceChar(c)))) {
								getToolkit().beep();
								key.consume();
							}

						}
					});
					CustomDocument document5 = new CustomDocument(10);
					stockValueField = new JFormattedTextField(NumberFormat.getNumberInstance());
					stockValueField.setDocument(document5);
					stockValueField .setValue(new Double(0));
					
					stockValueField .setColumns(2);
					
					boxStock = new JLabel("Stock:");
					boxStock.setBounds(6, 11, 88, 20);
					getContentPane().add(boxStock);
					stockNameField.setBounds(117, 11, 42, 26);
					getContentPane().add(stockNameField);
					stockValueField.setBounds(209, 11, 42, 26);
					getContentPane().add(stockValueField);
					SpinnerModel stockSymbolModel = new SpinnerListModel(
							new ArrayList<String>(Arrays.asList("<", ">")));
					stockSymbolSpinner = new JSpinner(stockSymbolModel);
					stockSymbolSpinner.setBounds(163, 11, 42, 26);
					getContentPane().add(stockSymbolSpinner);

					
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
				if(stockNameField.getText().equals("") ||stockNameField.getText().equals(" ")){
					JOptionPane.showMessageDialog(null,
							"Please enter a name for the stock",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}

				String stockName = stockNameField.getText();
				stockName = stockName.trim();
				String stockSymbol = (String) stockSymbolSpinner.getValue();
				String stockValue = stockValueField.getText();
				event.setStock(stockName + ":" + stockSymbol + ":" + stockValue);

				setVisible(false);
				dispose();

			}
		}
	}


}
