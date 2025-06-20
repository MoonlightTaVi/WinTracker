package wintracker.service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrackerDaemon implements Runnable {
	private final Thread thread;
	private WindowListener listener = new WindowListener(
			"Параметры", "Интерфейс ввода Windows", "Program Manager"
			);
	private Set<String> openedWindows = new HashSet<>();
	private Map<String, Integer> timeSpent = new ConcurrentHashMap<>();
	private Map<String, LocalDate> lastDates = new ConcurrentHashMap<>();
	
	public TrackerDaemon() {
		thread = new Thread(this, this.getClass().getName());
		thread.start();
	}
	
	public Map<String, Integer> getTimeSpent() {
		return Map.copyOf(timeSpent);
	}
	
	public LocalDate getLastDate(String forWindowsTile) {
		return lastDates.get(forWindowsTile);
	}
	
	public String stop() {
		String[] args = new String[2];
		args[0] = thread.getName();
		try {
			thread.interrupt();
			args[1] = "Success";
		} catch (SecurityException e) {
			args[1] = "Failed (SecurityException): " + e.getLocalizedMessage();
		} catch (Exception e) {
			args[1] = "Failed (unknown Exception): " + e.getLocalizedMessage();
		}
		return String.format("%s interruption status: %s", args[0], args[1]);
	}

	@Override
	public void run() {
		try {
			int seconds = 0;
			while (true) {
				Set<String> currentlyOpened =  listener.getWindows();
				for (String title : currentlyOpened) {
					if (openedWindows.contains(title)) {
						int openedFor = timeSpent.getOrDefault(title, 0);
						openedFor += seconds;
						timeSpent.put(title, openedFor);
						lastDates.put(title, LocalDate.now());
					} else {
						openedWindows.add(title);
					}
				}
				for (String title : List.of(openedWindows.toArray(String[]::new))) {
					if (!currentlyOpened.contains(title)) {
						openedWindows.remove(title);
					}
				}
				
				seconds = (int) (Math.random() * 20) + 10;
				Thread.sleep((long) seconds * 1000);
			}
		} catch (InterruptedException e) {
			System.err.println("Daemon was interrupted.");
		}
	}

}
