package wintracker.infrastructure;

import java.util.*;

import com.sun.jna.*;
import com.sun.jna.platform.win32.User32;

import wintracker.service.IgnoreList;

public class WindowListener {
	private IgnoreList ignoreList;
	
	public WindowListener(IgnoreList ignoreList) {
		this.ignoreList = ignoreList;
	}
	
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
