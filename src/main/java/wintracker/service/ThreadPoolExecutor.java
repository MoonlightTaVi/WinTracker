package wintracker.service;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ThreadPoolExecutor {

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(5);
	    executor.setMaxPoolSize(10);
	    executor.setWaitForTasksToCompleteOnShutdown(false);
	    return executor;
	}
	
}
