package wintracker.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "history")
public class WindowEntry {
	@Id
	@Getter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Setter
	@Getter
	private String title = "<nil>";
	@Setter
	@Getter
	private Integer secondsOpened = 0;
	@Getter
	private LocalDateTime createdDate = LocalDateTime.now();
	@Setter
	@Getter
	private LocalDateTime lastDate = LocalDateTime.now();
	@Setter
	@Getter
	private String meta;
	
	public WindowEntry() {}
	public WindowEntry(String title) {
		this.title = title;
	}
	
	public void updateFrom(WindowEntry anotherEntry) {
		id = anotherEntry.id;
		title = anotherEntry.title;
		secondsOpened = anotherEntry.secondsOpened;
		createdDate = anotherEntry.createdDate;
		lastDate = anotherEntry.lastDate;
		meta = anotherEntry.meta;
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
