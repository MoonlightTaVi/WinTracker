package wintracker.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Listeners {
	
	public static ActionListener getActionListener(Runnable actionToPerform) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionToPerform.run();
			}
		};
	}

	public static DocumentListener getDocumentListener(Runnable actionToPerform) {
		return new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				actionToPerform.run();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				actionToPerform.run();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				actionToPerform.run();
			}
		};
	}

	public static WindowListener getWindowClosingListener(JFrame frameToDispose, Runnable actionToPerform) {
		return new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
				actionToPerform.run();
		        frameToDispose.dispose();
		    }
		};
	}
	
}
