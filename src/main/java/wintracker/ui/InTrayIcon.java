package wintracker.ui;

import java.awt.*;
import java.awt.event.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import lombok.Setter;
import wintracker.service.TrackerDaemon;

@Component
public class InTrayIcon implements InitializingBean {
	@Autowired
	@Setter
	private ConfigurableApplicationContext context;
	@Autowired
	@Setter
	private TrackerDaemon daemon;
	
	/** "Open" option in tray. */
	private Runnable onClick = () -> context.getBeanFactory().getBean(TrackerFrame.class);

	public InTrayIcon() {
		TrayIcon trayIcon = null;
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("tray-icon.png");
		    PopupMenu popup = new PopupMenu();
		    MenuItem openItem = new MenuItem("Open window");
		    openItem.addActionListener(getListener(onClick));
		    MenuItem quitItem = new MenuItem("Quit");
		    quitItem.addActionListener(getListener(() -> {
		    	SpringApplication.exit(context, () -> 0);
		    	}));
		    popup.add(openItem);
		    popup.add(quitItem);
		    trayIcon = new TrayIcon(image, "Win Tracker", popup);
		    trayIcon.setImageAutoSize(true);
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println(e);
		    }
		} else {
			System.err.println("System tray is not supported.");
		}
	}
	
	private ActionListener getListener(Runnable actionPerformed) {
		return new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	actionPerformed.run();
	        }
	    };
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Open Application frame on start-up
		onClick.run();
	}
	
}
