package logic;

import gui.MainView;

import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public final class EventScheduler extends TimerTask {
	private EventRepository eventRepo;
	private ArrayList<BrainwavesEvent> activeEvents;

	public EventScheduler(EventRepository eventRepo) {
		super();
		this.eventRepo = eventRepo;
		activeEvents = new ArrayList<BrainwavesEvent>();
	}

	@Override
	public void run() {
		eventRepo.fetchEventsFromDB();
		activeEvents = eventRepo.getActiveEvents();
		MainView.addActiveEvents(activeEvents);
	}
}
