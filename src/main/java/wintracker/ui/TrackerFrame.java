package wintracker.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import wintracker.service.TrackerDaemon;

public class TrackerFrame {
	
	public TrackerFrame(final TrackerDaemon daemon) {
		Map<String, Integer> seconds = daemon.getTimeSpent();
		List<String> titles = new ArrayList<>(seconds.keySet());
		// Latest first
		titles.sort((a, b) -> daemon.getLastDate(b).compareTo(daemon.getLastDate(a)));
		// Content panel with a single column
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		for (String title : titles) {
			// Entry row (title, time spent, last date)
			JPanel row = new JPanel(new GridLayout(1, 3));
			row.add(getLabel(title));
			row.add(getLabel(parseTime(seconds.get(title))));
			row.add(getLabel(daemon.getLastDate(title).toString()));
			row.setVisible(true);
			panel.add(row);
		}
		panel.setVisible(true);
		
		JFrame frame = new JFrame();
		frame.setTitle("Windows Time Tracker ⏱️");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setMinimumSize(new Dimension(100, 100));
		frame.setVisible(true);
	}
	
	public JLabel getLabel(String text) {
		JLabel label = new JLabel();
		label.setToolTipText(text);
		// Limit maximum length of label to 30 characters
		if (text.length() > 30) {
			text = text.substring(0, 25) + "...";
		}
		label.setText(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		return label;
	}
	
	public String parseTime(int seconds) {
		String result = null;
		if (seconds < 60) {
			result = String.format("%ds", seconds);
		} else if (seconds < 60 * 60) {
			int minutes = seconds / 60;
			seconds %= minutes * 60;
			result = String.format("%dm%ds", minutes, seconds);
		} else {
			int hours = seconds / 3600;
			int minutes = seconds / 60;
			minutes %= hours * 60;
			result = String.format("%dh%dm", hours, minutes);
		}
		return result;
	}

}
