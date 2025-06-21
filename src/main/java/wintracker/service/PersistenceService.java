package wintracker.service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Setter;
import wintracker.db.WinHistoryRepo;
import wintracker.model.WindowEntry;

@Service
public class PersistenceService {
	@Autowired
	@Setter
	private WinHistoryRepo repository;
	
	public WindowEntry findByTitle(String title) {
		return repository.findAll().stream().parallel()
				.filter(entry -> entry.getTitle().equals(title))
				.findFirst()
				.orElse(null);
	}

	public void put(WindowEntry entry) {
		repository.save(entry);
	}

	public void put(long id, int seconds, LocalDateTime lastDate) {
		Optional<WindowEntry> entryOpt = repository.findById(id);
		entryOpt.ifPresentOrElse(entry -> {
			entry.setSecondsOpened(seconds);
			entry.setLastDate(lastDate);
			repository.save(entry);
		}, () -> {
			WindowEntry newEntry = new WindowEntry();
			newEntry.setSecondsOpened(seconds);
			newEntry.setLastDate(lastDate);
			repository.save(newEntry);
		});
	}

	public void put(String title, int seconds, LocalDateTime lastDate) {
		repository.findAll()
		.stream()
		.filter(entry -> entry.getTitle().equals(title))
		.findFirst()
		.ifPresentOrElse(entry -> {
			entry.setSecondsOpened(seconds);
			entry.setLastDate(lastDate);
			repository.save(entry);
		}, () -> {
			WindowEntry newEntry = new WindowEntry();
			newEntry.setSecondsOpened(seconds);
			newEntry.setLastDate(lastDate);
			repository.save(newEntry);
		});
	}

	public void update(long id, String meta) {
		Optional<WindowEntry> entryOpt = repository.findById(id);
		entryOpt.ifPresent(entry -> {
			entry.setMeta(meta);
			repository.save(entry);
		});
	}
	
	public List<WindowEntry> getAll() {
		return repository.findAll();
	}
	
}
