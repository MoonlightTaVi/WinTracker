package wintracker.ui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import lombok.Setter;
import wintracker.service.IgnoreList;
import wintracker.service.TrackerDaemon;
import wintracker.util.Listeners;

@Component
@Scope("prototype")
public class TrackerFrame implements InitializingBean {
	@Autowired
	private ConfigurableApplicationContext context;
	@Autowired
	@Setter
	private TrackerDaemon daemon;
	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm");
	private JPanel contentPanel = new JPanel();
	private JTextField filterField = new JTextField();
	
	public void fetchData() {
		fetchData(null);
	}
	public void fetchData(final String titleContains) {
		contentPanel.removeAll();
		
		Map<String, Integer> seconds = daemon.getTimeSpent();
		// Latest first
		List<String> titles = new ArrayList<>(seconds.keySet())
				.stream()
				.filter(t -> {
					return titleContains == null ||
							titleContains.isBlank() ||
							titleContains.length() < 4 ||
							t.toLowerCase().contains(titleContains.toLowerCase());
				})
				.sorted(
						(a, b) -> daemon.getLastDate(b).compareTo(
								daemon.getLastDate(a))
						)
				.limit(20)
				.toList();
		for (String title : titles) {
			// Entry row (title, time spent, last date)
			JPanel row = new JPanel(new GridLayout(1, 4));
			JLabel titleLabel = getLabel(title);
			row.add(titleLabel);
			row.add(getLabel(parseTime(daemon.getSession(title))));
			row.add(getLabel(parseTime(seconds.get(title))));
			row.add(getLabel(dateFormat.format(daemon.getCreatedDate(title))));
			row.add(getLabel(dateFormat.format(daemon.getLastDate(title))));
			row.setVisible(true);
			row.setMaximumSize(new Dimension(800, 20));
			titleLabel.addMouseListener(
					Listeners.getMouseListenerMenu(
							getMenuItems(titleLabel)
							)
					);
			
			contentPanel.add(row);
		}
		contentPanel.add(Box.createGlue());
		contentPanel.revalidate();
		contentPanel.repaint();
	}
	
	@Override
	public void afterPropertiesSet() {
		// Filter entries by title bar
		JPanel headerPanel = new JPanel(new FlowLayout());
		headerPanel.add(new JLabel("Filter by title:"));
		filterField.getDocument().addDocumentListener(
				Listeners.getDocumentListener(
						() -> fetchData(filterField.getText())
						));
		filterField.setToolTipText("Filter entries by title.");
		filterField.setPreferredSize(new Dimension(300, 20));
		headerPanel.add(filterField);
		JButton btnShowIgnoreList = new JButton("Ignore list");
		btnShowIgnoreList.addActionListener(
				Listeners.getActionListener(
						() -> new IgnoreListFrame(
								context.getBean(IgnoreList.class)
								)
						)
				);
		headerPanel.add(btnShowIgnoreList);
		
		// Content panel with a single column
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setPreferredSize(new Dimension(800, 400));
		fetchData();
		contentPanel.setVisible(true);
		
		// Group two panels in one
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(headerPanel);
		mainPanel.add(contentPanel);
		
		// Application window frame
		JFrame frame = new JFrame();
		frame.setTitle("Windows Time Tracker ⏱️");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.setMinimumSize(new Dimension(800, 420));
		frame.setVisible(true);
	}
	
	private JLabel getLabel(String text) {
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
	
	private Map<String, Runnable> getMenuItems(JLabel forTitleLabel) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(forTitleLabel.getText());
		final String title = forTitleLabel.getText();
		Map<String, Runnable> options = new TreeMap<>();
		options.put("Copy title", () -> clipboard.setContents(stringSelection, null));
		options.put("Completely delete", () -> {
			daemon.deleteTitle(title);
			forTitleLabel.setText("<queued for removal, wait ~30sec>");
			});
		options.put("Undo deletion", () -> {
			daemon.unqueDeletionOfTitle(title);
			forTitleLabel.setText(title);
			});
		return options;
	}
	
	private String parseTime(Integer seconds) {
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
