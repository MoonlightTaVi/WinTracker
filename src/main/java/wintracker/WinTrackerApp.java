package wintracker;

import wintracker.service.TrackerDaemon;
import wintracker.ui.*;

public class WinTrackerApp {

	public static void main(String[] args) {
		TrackerDaemon daemon = new TrackerDaemon();
		CommandLine cli = new CommandLine(daemon);
		new InTrayIcon(() -> new TrackerFrame(daemon));
		cli.run();
	}
	
}
