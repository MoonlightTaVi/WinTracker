package wintracker;

import java.util.Scanner;

import wintracker.service.*;

public class WinTrackerApp {

	public static void main(String[] args) {
		TrackerDaemon daemon = new TrackerDaemon();
		System.out.println("Type /list to see tracked time or /bye to quit");
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.print(">: ");
				String input = scanner.nextLine();
				if (input.equals("/list")) {
					System.out.println("\nTime spent in windows:");
					daemon.getTimeSpent().entrySet().forEach(entry -> {
						System.out.printf("(%d sec) %s%n", entry.getValue(), entry.getKey());
					});
					continue;
				} else if (input.equals("/bye")) {
					System.out.println(daemon.stop());
					break;
				}
				System.out.println("[Unknown command]");
			}
		}
		System.out.println("[App stopped]");
	}
	
}
