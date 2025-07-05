package wintracker.service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import wintracker.infrastructure.WindowListener;

@Service
public class IgnoreList {
	private Logger log = LoggerFactory.getLogger(WindowListener.class);
	/** File to read the list of ignored titles from. */
	public final String ignoreListFile = "ignore-list.txt";
	/** List of RegEx's for titles to ignore. */
	private Set<String> ignoredTitles = new HashSet<>();
	
	/**
	 * Loads the RegEx list of ignored titles from a file on initialization.
	 * @see #ignoreListFile
	 */
	public IgnoreList() {
		ignoredTitles = read();
	}
	
	public Set<String> get() {
		return ignoredTitles;
	}
	
	/**
	 * Checks if a title should be ignored, matching it against RegEx patterns.
	 * @param title Full name of the title.
	 * @return true if the title must be ignored, false otherwise.
	 */
	public boolean anyMatch(String title) {
		for (String regex : ignoredTitles) {
			if (title.matches(regex)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Reads the list of ignored titles from a file.
	 * @return HashSet, containing the RegEx list of ignored titles.
	 * @see #ignoreListFile
	 */
	public Set<String> read() {
		Set<String> ignoredTitles = new HashSet<>();
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
			log.error("Could not load ignored titles list: {}", e.getLocalizedMessage());
		}
		return ignoredTitles;
	}
	
	/**
	 * Refreshes the list of ignored titles, also saving it to a file.
	 * @param ignoredTitles Set of ignored titles.
	 */
	public void write(Set<String> ignoredTitles) {
		try (FileWriter writer = new FileWriter(ignoreListFile, false)) {
			String[] titles = ignoredTitles.toArray(String[]::new);
			for (int i = 0; i < titles.length; i++) {
				writer.append(titles[i]);
				if (i < titles.length - 1) {
					writer.append('\n');
				}
			}
			this.ignoredTitles = ignoredTitles;
			log.info("Updated ignore list.");
		} catch (IOException e) {
			log.error("Could not save updated ignored titles list: {}", e.getLocalizedMessage());
		}
	}
}
