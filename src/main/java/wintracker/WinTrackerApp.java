package wintracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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
		context.close();
	}
	
	/**
	 * Retrieves database connection credentials
	 */
	@Autowired
	private Environment env;
	
	/**
	 * Sets SQLite configuration
	 * @return DataSource configuration bean for SQLite
	 */
	@Bean
	public DataSource dataSource() {
	    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setDriverClassName(env.getProperty("driverClassName"));
	    dataSource.setUrl(env.getProperty("url"));
	    dataSource.setUsername(env.getProperty("user"));
	    dataSource.setPassword(env.getProperty("password"));
	    return dataSource;
	}
	
}
