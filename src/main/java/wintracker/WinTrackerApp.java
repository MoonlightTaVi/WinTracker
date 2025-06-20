package wintracker;

import wintracker.service.TrackerDaemon;
import wintracker.ui.CommandLine;

public class WinTrackerApp {

	public static void main(String[] args) {
		CommandLine cli = new CommandLine(new TrackerDaemon());
		cli.run();
	}
	
}
