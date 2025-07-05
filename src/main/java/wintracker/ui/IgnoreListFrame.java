package wintracker.ui;

import java.util.*;

import javax.swing.*;
import wintracker.service.IgnoreList;
import wintracker.util.Listeners;

public class IgnoreListFrame {
	private IgnoreList ignoreList;
	private JTextArea textArea;
	
	public IgnoreListFrame(IgnoreList ignoreList) {
		this.ignoreList = ignoreList;
		
		JFrame frame = new JFrame();
		frame.setTitle("Specify titles to ignore (regular expressions), then close");
		frame.setSize(640, 480);
		frame.addWindowListener(
				Listeners.getWindowClosingListener(
						frame,
						() -> dispose()
						)
				);

		textArea = new JTextArea();
		textArea.setText(setToString(ignoreList.get()));
		textArea.setEditable(true);
		textArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		frame.add(scrollPane);
		frame.setVisible(true);
		frame.repaint();
	}

	public void dispose() {
		ignoreList.write(stringToSet(textArea.getText()));
	}
	
	private String setToString(Set<String> stringSet) {
		return String.join("\n", stringSet);
	}
	
	private Set<String> stringToSet(String text) {
		return new HashSet<String>(List.of(text.split("\n")));
	}
	

}
