package wintracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import wintracker.service.TrackerDaemon;

@SpringBootApplication
public class WinTrackerApp {
	private static Logger log = LoggerFactory.getLogger(WinTrackerApp.class);

	public static void main(String[] args) {
		try {
			Files.createDirectories(Path.of("data"));
		} catch (IOException e) {
			List<String> stacktrace = Arrays.stream(e.getStackTrace()).map(st -> st.toString()).toList();
			log.error(
					"Could not create \"/data\" directory: {}\n{}",
					e.getLocalizedMessage(),
					String.join("\n", stacktrace)
					);
			e.printStackTrace();
		}

		SpringApplication application = new SpringApplication(WinTrackerApp.class);
        application.setHeadless(false); // Disable headless mode
        application.setWebApplicationType(WebApplicationType.NONE);
        
        ConfigurableApplicationContext context = application.run(args);
        // Wait for daemon to be sure it has stopped
        TrackerDaemon daemon = context.getBean(TrackerDaemon.class);
        try {
			while (daemon.isRunning()) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			log.error("Application interrupted with failure");
		} finally {
			context.close();
		}
	}
	
}
