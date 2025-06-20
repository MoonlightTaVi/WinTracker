package wintracker.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import lombok.Setter;
import wintracker.service.TrackerDaemon;

@Component
@Scope("prototype")
@Lazy
public class TrackerFrame implements InitializingBean {
	@Autowired
	@Setter
	private TrackerDaemon daemon;
	
	@Override
	public void afterPropertiesSet() {
		Map<String, Integer> seconds = daemon.getTimeSpent();
		// Latest first
		List<String> titles = new ArrayList<>(seconds.keySet())
				.stream()
				.sorted(
						(a, b) -> {
							int result = daemon.getLastDate(b).compareTo(
									daemon.getLastDate(a)
									);
							if (result == 0) {
								result = daemon.getSession(b)
										.compareTo(daemon.getSession(a));
							}
							if (result == 0) {
								result = seconds.getOrDefault(b, 0)
										.compareTo(seconds.getOrDefault(a, 0));
							}
							return result;
						}
						)
				.limit(20)
				.toList();
		// Content panel with a single column
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		for (String title : titles) {
			// Entry row (title, time spent, last date)
			JPanel row = new JPanel(new GridLayout(1, 4));
			row.add(getLabel(title));
			row.add(getLabel(parseTime(daemon.getSession(title))));
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
	
	public String parseTime(Integer seconds) {
		if (seconds == null) {
			return "...";
		}
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
