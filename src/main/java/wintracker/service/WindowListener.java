package wintracker.service;

import java.util.*;

import com.sun.jna.*;
import com.sun.jna.platform.win32.User32;

public class WindowListener {
	private Set<String> ignoredTitles = new HashSet<>();
	
	public WindowListener() { }
	public WindowListener(String... ignoredTitles) {
		ignoreTitles(ignoredTitles);
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
}
