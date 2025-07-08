package wintracker.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import wintracker.model.WindowEntry;

/**
 * Main daemon process of the WinTracker application.
 */
@Service
public class TrackerDaemon implements Runnable {
	/** Time when the app has been started. Not used currently IIRC. */
	@Getter
	private final LocalDateTime startedAt;
	@Autowired
	@Setter
	private PersistenceService service;
	@Autowired
	@Setter
	private IgnoreList ignoreList;
	@Autowired
	private WindowListener listener;
	@Setter
	@Getter
	private volatile boolean running = true;
	/** Set of currently tracked windows */
	private Set<String> trackedWindows = new HashSet<>();
	/** 
	 * Time for which the windows were open since program start. 
	 * The value resets after program restart.
	 */
	private volatile Map<String, Integer> currentSession = new ConcurrentHashMap<>();
	/**
	 * Titles inside this set will be removed on the next daemon iteration.
	 */
	private volatile Set<String> removalQueue = ConcurrentHashMap.newKeySet();
	
	public TrackerDaemon() {
		startedAt = LocalDateTime.now();
	}
	
	
	public Map<String, Integer> getTimeSpent() {
		Map<String, Integer> timeSpent = new HashMap<>();
		for (WindowEntry entry : service.getAll()) {
			timeSpent.put(entry.getTitle(), entry.getSecondsOpened());
		}
		return timeSpent;
	}
	
	public LocalDateTime getCreatedDate(String forWindowsTile) {
		WindowEntry entry = service.getAll().stream()
				.filter(e -> e.getTitle().equals(forWindowsTile))
				.findFirst()
				.get();
		LocalDateTime createdDate = null;
		if (entry != null) {
			createdDate = entry.getCreatedDate();
		}
		return createdDate;
	}
	
	public LocalDateTime getLastDate(String forWindowsTile) {
		WindowEntry entry = service.getAll().stream()
				.filter(e -> e.getTitle().equals(forWindowsTile))
				.findFirst()
				.get();
		LocalDateTime lastDate = null;
		if (entry != null) {
			lastDate = entry.getLastDate();
		}
		return lastDate;
	}
	
	public Integer getSession(String forWindowsTile) {
		return currentSession.getOrDefault(forWindowsTile, 0);
	}
	
	public void deleteTitle(String title) {
		removalQueue.add(title);
	}
	public void unqueDeletionOfTitle(String title) {
		removalQueue.remove(title);
	}
	private void launchRemoval() {
		trackedWindows.removeAll(removalQueue);
		for (String title : removalQueue) {
			currentSession.remove(title);
		}
		service.deleteByNames(removalQueue);
		removalQueue.clear();
	}

	@Override
	public void run() {
		int seconds = 0;
		while (running) {
			
			launchRemoval();
			try {
				Set<String> currentlyOpen =  listener.getWindows();
				// Update already opened windows
				for (String title : currentlyOpen) {
					// Update time for tracked windows
					if (trackedWindows.contains(title)) {
						WindowEntry entry = service.findByTitle(title).orElseGet(() -> new WindowEntry(title));
						int openedFor = seconds;
						openedFor += entry.getSecondsOpened();
						entry.setSecondsOpened(openedFor);
						service.save(entry);
						
						int session = currentSession.getOrDefault(title, 0);
						session += seconds;
						currentSession.put(title, session);
						
					}
					// Start tracking untracked windows
					else {
						trackedWindows.add(title);
					}
				}
				// Stop tracking closed windows
				for (String title : List.of(trackedWindows.toArray(String[]::new))) {
					if (!currentlyOpen.contains(title)) {
						trackedWindows.remove(title);
						WindowEntry entry = service.findByTitle(title).orElseGet(() -> new WindowEntry(title));
						entry.setLastDate(LocalDateTime.now());
						service.save(entry);
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
