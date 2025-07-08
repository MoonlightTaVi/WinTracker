package wintracker.db;

import org.springframework.data.jpa.repository.JpaRepository;

import wintracker.model.WindowEntry;
/**
 * Database contains the history of open
 * windows since the first launch.
 * The titles for each entry are unique
 * (they are being overridden on update)
 */
public interface WinHistoryRepo extends JpaRepository<WindowEntry, Long> {

}
