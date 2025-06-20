package wintracker.ui;

import java.awt.*;
import java.awt.event.*;

public class InTrayIcon {

	public InTrayIcon(Runnable onClick) {
		TrayIcon trayIcon = null;
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("tray-icon.png");
		    PopupMenu popup = new PopupMenu();
		    MenuItem openItem = new MenuItem("Open window");
		    openItem.addActionListener(getListener(onClick));
		    MenuItem quitItem = new MenuItem("Quit");
		    quitItem.addActionListener(getListener(() -> System.exit(0)));
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
	
}
