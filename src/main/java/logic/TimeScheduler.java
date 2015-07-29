package main.java.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;

import javax.swing.JLabel;

public final class TimeScheduler extends TimerTask {
	private JLabel lblDate;
	private JLabel lblTime;
	private String currentDate;

	public TimeScheduler(JLabel lblDate, JLabel lblTime) {
		super();
		this.lblDate = lblDate;
		this.lblTime = lblTime;
		currentDate = "";
	}

	@Override
	public void run() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Calendar cal = Calendar.getInstance();
		currentDate = dateFormat.format(cal.getTime());
		String[] splitDate = currentDate.split(" ");
		lblDate.setText("Date: " + splitDate[0]);
		lblTime.setText("Time: " + splitDate[1]);
	}
}
