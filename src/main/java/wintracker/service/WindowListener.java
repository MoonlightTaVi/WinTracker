package wintracker.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.*;
import com.sun.jna.platform.win32.User32;

public class WindowListener {
	private Logger log = LoggerFactory.getLogger(WindowListener.class);
	private Set<String> ignoredTitles = new HashSet<>();
	
	public WindowListener() {
		init();
	}
	public WindowListener(String... ignoredTitles) {
		ignoreTitles(ignoredTitles);
		init();
	}
	public void ignoreTitles(String... ignoredTitles) {
		this.ignoredTitles.addAll(List.of(ignoredTitles));
	}
	public void stopIgnoringTitles(String... ignoredTitles) {
		this.ignoredTitles.removeAll(List.of(ignoredTitles));
	}
	
	public Set<String> getWindows() {
		Set<String> windowTitles = new HashSet<>();
	    
	    User32.INSTANCE.EnumWindows((hWnd, userData) -> {
	        if (User32.INSTANCE.IsWindowVisible(hWnd)) {
	            char[] buffer = new char[1024];
	            User32.INSTANCE.GetWindowText(hWnd, buffer, buffer.length);
	            String title = Native.toString(buffer);
	            
	            if (!title.isEmpty() && !ignoredTitles.contains(title)) {
	                windowTitles.add(title);
	            }
	        }
	        return true;
	    }, null);
	    
	    return windowTitles;
	}
	
	public void init() {
		String ignoreListFile = "ignore-list.txt";
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						Files.newInputStream(
								Paths.get(ignoreListFile)
								)
						)
				)
				) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				ignoredTitles.add(line);
				log.info("Add ignored file: {}", line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
