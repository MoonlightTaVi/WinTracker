package wintracker.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.shell.standard.*;

import lombok.Setter;
import wintracker.service.TrackerDaemon;

@ShellComponent
@ShellCommandGroup("[Window Tracker commands]")
public class CommandLine {
	@Autowired
	@Setter
	private ConfigurableApplicationContext context;
	@Autowired
	@Setter
	private TrackerDaemon daemon;
	
	@ShellMethod(value = "list time spent using windows")
	private String list() {
		StringBuilder builder = new StringBuilder("\nTime spent in windows:\n");
		System.out.println("\nTime spent in windows:");
		daemon.getTimeSpent().entrySet().forEach(entry -> {
			builder.append(String.format("(%d sec) %s", entry.getValue(), entry.getKey()));
			builder.append("\n");
		});
		return builder.toString();
	}
	
	@ShellMethod(value = "open GUI application")
	private void openFrame() {
		context.getBeanFactory().getBean(TrackerFrame.class);
	}

}
