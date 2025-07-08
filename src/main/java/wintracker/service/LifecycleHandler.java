package wintracker.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.Setter;

/**
 * Launches the daemon. <br>
 * Also checks if the context has been closed, then initiates
 * daemon shutdown.
 */
@Component
public class LifecycleHandler implements InitializingBean, ApplicationListener<ContextClosedEvent> {
	@Autowired
	@Setter
	private ThreadPoolTaskExecutor executor;
	@Autowired
	@Setter
	private TrackerDaemon daemon;
	@Autowired
	@Setter
	private ConfigurableApplicationContext context;

	@Override
	public void afterPropertiesSet() throws Exception {
		executor.execute(daemon);
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		System.err.println("\nShutting down...");
		daemon.setRunning(false);
		executor.shutdown();
		System.err.println("Finished.");
		System.exit(0);
	}

}
