package wintracker.db;

import org.springframework.data.jpa.repository.JpaRepository;

import wintracker.model.WindowEntry;

public interface WinHistoryRepo extends JpaRepository<WindowEntry, Long> {

}
