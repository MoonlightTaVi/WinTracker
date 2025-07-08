package wintracker.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * An entry (entity model) from the database.
 * @see wintracker.db.WinHistoryRepo
 */
@Entity
@Table(name = "history")
public class WindowEntry {
	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** Window title. */
	@Setter
	@Getter
	private String title = "<nil>";
	/** Time (in sec) the window has been open for. */
	@Setter
	@Getter
	private Integer secondsOpened = 0;
	/** Time since tracking has started. */
	@Getter
	private LocalDateTime createdDate = LocalDateTime.now();
	/** Last time secondsOpened has been updated. */
	@Setter
	@Getter
	private LocalDateTime lastDate = LocalDateTime.now();
	/** Not used ATM. */
	@Setter
	@Getter
	private String meta;
	
	public WindowEntry() {}
	public WindowEntry(String title) {
		this.title = title;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean equals = false;
		if (o instanceof WindowEntry e) {
			equals = id.equals(e.id);
		}
		return equals;
	}
	
	@Override
	public int hashCode() {
		return id.intValue();
	}
	
}
