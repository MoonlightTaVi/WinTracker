package wintracker.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jna.*;
import com.sun.jna.platform.win32.User32;

@Service
public class WindowListener {
	@Autowired
	private IgnoreList ignoreList;
	
	/**
	 * Scans for open windows, using win32.User32, and returns
	 * corresponding set of titles.
	 * @return Set of String representations of window titles
	 * @see com.sun.jna.platform.win32.User32
	 */
	public Set<String> getWindows() {
		Set<String> windowTitles = new HashSet<>();
	    
	    User32.INSTANCE.EnumWindows((hWnd, userData) -> {
	        if (User32.INSTANCE.IsWindowVisible(hWnd)) {
	            char[] buffer = new char[1024];
	            User32.INSTANCE.GetWindowText(hWnd, buffer, buffer.length);
	            String title = Native.toString(buffer);
	            if (!title.isEmpty() && !ignoreList.anyMatch(title)) {
	                windowTitles.add(title);
	            }
	        }
	        return true;
	    }, null);
	    
	    return windowTitles;
	}
}
