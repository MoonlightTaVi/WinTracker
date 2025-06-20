package wintracker.service;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Setter;
import wintracker.model.WindowEntry;

@Service
public class TrackerDaemon implements Runnable {
	@Autowired
	@Setter
	private PersistenceService service;
	private volatile boolean running = true;
	
	private WindowListener listener = new WindowListener(
			"Параметры", "Интерфейс ввода Windows", "Program Manager"
			);
	private Set<String> openedWindows = new HashSet<>();
	
	public Map<String, Integer> getTimeSpent() {
		Map<String, Integer> timeSpent = new HashMap<>();
		for (WindowEntry entry : service.getAll()) {
			timeSpent.put(entry.getTitle(), entry.getSecondsOpened());
		}
		return timeSpent;
	}
	
	public LocalDate getLastDate(String forWindowsTile) {
		WindowEntry entry = service.getAll().stream()
				.filter(e -> e.getTitle().equals(forWindowsTile))
				.findFirst()
				.get();
		LocalDate lastDate = null;
		if (entry != null) {
			lastDate = entry.getLastDate();
		}
		return lastDate;
	}

	@Override
	public void run() {
		int seconds = 0;
		while (running) {
			try {
				Set<String> currentlyOpened =  listener.getWindows();
				// Update already opened windows
				for (String title : currentlyOpened) {
					if (openedWindows.contains(title)) {
						WindowEntry entry = service.findByTitle(title);
						int openedFor = seconds;
						if (entry != null) {
							openedFor += entry.getSecondsOpened();
						} else {
							entry = new WindowEntry();
							entry.setTitle(title);
						}
						entry.setLastDate(LocalDate.now());
						entry.setSecondsOpened(openedFor);
						service.put(entry);
					} else {
						openedWindows.add(title);
					}
				}
				// Start observing newly opened windows
				for (String title : List.of(openedWindows.toArray(String[]::new))) {
					if (!currentlyOpened.contains(title)) {
						openedWindows.remove(title);
					}
				}
				// Lazily wait for 10-30 seconds until next update
				seconds = (int) (Math.random() * 20) + 10;
				Thread.sleep((long) seconds * 1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Daemon was interrupted.");
				running = false;
			}
		}
	}

}
