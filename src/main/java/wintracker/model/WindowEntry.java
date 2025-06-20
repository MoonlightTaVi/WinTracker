package wintracker.model;

import java.time.LocalDate;

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
	private String title;
	@Setter
	@Getter
	private Integer secondsOpened;
	@Setter
	@Getter
	private Integer currentSessionSeconds;
	@Setter
	@Getter
	private LocalDate lastDate;
	@Setter
	@Getter
	private String meta;
	
}
