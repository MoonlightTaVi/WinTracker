package wintracker.ui;

import java.util.*;
import java.util.Scanner;
import java.util.function.Supplier;

import wintracker.service.TrackerDaemon;

public class CommandLine implements Runnable {
	private final TrackerDaemon daemon;
	private Map<String, CliCommand> commands = new LinkedHashMap<>();
	
	public CommandLine(TrackerDaemon daemon) {
		this.daemon = daemon;
		commands.put(
				"/list",
				new CliCommand(
						"see tracked time",
						() -> list()
						)
				);
		commands.put(
				"/gui",
				new CliCommand(
						"open the app window",
						() -> openFrame()
						)
				);
		commands.put(
				"/quit",
				new CliCommand(
						"quit",
						() -> quit()
						)
				);
	}

	@Override
	public void run() {
		// Show help tip
		List<String> commandsHelp = new ArrayList<>();
		for (String command : commands.keySet()) {
			commandsHelp.add(
					String.format(
							"%s to %s",
							command,
							commands.get(command).description
							)
					);
		}
		System.out.printf("Type %s%n", String.join(", ", commandsHelp));
		
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.print(">: ");
				String input = scanner.nextLine();
				if (commands.containsKey(input)) {
					// If user quits, command returns true
					if (!commands.get(input).command.get()) {
						break;
					}
					continue;
				}
				System.out.println("[Unknown command]");
			}
		}
		System.out.println("[App stopped]");
	}
	
	private boolean list() {
		System.out.println("\nTime spent in windows:");
		daemon.getTimeSpent().entrySet().forEach(entry -> {
			System.out.printf("(%d sec) %s%n", entry.getValue(), entry.getKey());
		});
		return true;
	}
	
	private boolean openFrame() {
		new TrackerFrame(daemon);
		return true;
	}
	
	private boolean quit() {
		return false;
	}
	
	private record CliCommand(
			String description,
			Supplier<Boolean> command
			) { }

}
