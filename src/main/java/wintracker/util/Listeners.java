package wintracker.util;

import java.awt.event.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.SwingUtilities;
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
	
	public static MouseListener getMouseListenerMenu(Map<String, Runnable> options) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					JPopupMenu popupMenu = new JPopupMenu();
					for (Map.Entry<String, Runnable> option : options.entrySet()) {
						JMenuItem item = new JMenuItem(option.getKey());
						item.addActionListener(event -> option.getValue().run());
						popupMenu.add(item);
					}
					popupMenu.show(e.getComponent(), 0, 0);
				}
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
