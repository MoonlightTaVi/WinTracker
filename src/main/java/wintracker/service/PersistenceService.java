package wintracker.service;

import java.util.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Setter;
import wintracker.db.WinHistoryRepo;
import wintracker.model.WindowEntry;

/**
 * Service to work with the database.
 * Entities are returned from cache
 * (fetched upon application start),
 * but saving is performed upon DB itself.
 */
@Service
public class PersistenceService implements InitializingBean {
	@Autowired
	@Setter
	private WinHistoryRepo repository;
	/** Cache of entries from the DB. */
	private Set<WindowEntry> entries = new HashSet<>();
	
	public Optional<WindowEntry> findByTitle(String title) {
		return entries.stream().parallel()
				.filter(entry -> entry.getTitle().equals(title))
				.findFirst();
	}
	
	public void save(WindowEntry entry) {
		repository.save(entry);
		entries.add(entry);
	}
	public void deleteByNames(Collection<String> titles) {
		for (WindowEntry entry : entries.toArray(WindowEntry[]::new)) {
			if (titles.contains(entry.getTitle())) {
				entries.remove(entry);
				repository.deleteById(entry.getId());
			}
		}
	}
	
	public List<WindowEntry> getAll() {
		return new ArrayList<>(entries);
	}

	/**
	 * Fetch Entities from the DB and cache them.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		entries.addAll(repository.findAll());
	}
	
}
