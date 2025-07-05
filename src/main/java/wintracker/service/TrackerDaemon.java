package wintracker.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import wintracker.infrastructure.WindowListener;
import wintracker.model.WindowEntry;

@Service
public class TrackerDaemon implements Runnable {
	/** Reset currentSession to 0 seconds after 20 minutes. */
	private final int CLEANUP_TIME = 1200;
	@Getter
	private final LocalDateTime startedAt;
	@Autowired
	@Setter
	private PersistenceService service;
	@Getter
	private volatile boolean running = true;
	
	private WindowListener listener = new WindowListener();
	/** Set of currently tracked windows */
	private Set<String> trackedWindows = new HashSet<>();
	/** 
	 * Time for which the windows were open since program start. 
	 * The value resets when some time passes after closing the window.
	 */
	private volatile Map<String, Integer> currentSession = new ConcurrentHashMap<>();
	/** 
	 * If a windows is closed during the Application run,
	 * its currentSession is queued to be reset
	 * after some time.
	 * @see #currentSession
	 */
	private volatile Map<String, Integer> currentSessionCleanupQueue = new ConcurrentHashMap<>();
	
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

	@Override
	public void run() {
		int seconds = 0;
		while (running) {
			try {
				Set<String> currentlyOpen =  listener.getWindows();
				// Update already opened windows
				for (String title : currentlyOpen) {
					// Do not clean open windows
					currentSessionCleanupQueue.remove(title);
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
				// Reset current session when time passes
				for (String title : List.of(currentSession.keySet().toArray(String[]::new))) {
					if (!currentlyOpen.contains(title)) {
						int cleanupTimer = currentSessionCleanupQueue.getOrDefault(title, 0);
						cleanupTimer += seconds;
						currentSessionCleanupQueue.put(title, cleanupTimer);
						if (cleanupTimer >= CLEANUP_TIME) {
							currentSession.remove(title);
						}
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
