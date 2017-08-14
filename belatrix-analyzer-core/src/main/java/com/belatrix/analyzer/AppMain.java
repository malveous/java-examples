package com.belatrix.analyzer;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.belatrix.analyzer.config.enums.SearchPatternType;
import com.belatrix.analyzer.exceptions.InvalidFilePathException;
import com.belatrix.analyzer.exceptions.NullPatternException;
import com.belatrix.analyzer.process.WebPageAnalyzer;

/**
 * This class contains a main method to call the Web Analyzer component that
 * will perform an analysis of content of Web Pages looking for certain patterns
 * with minimal configuration or complex requirements.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class AppMain {

	/**
	 * Logger for the main class.
	 */
	private static Logger logger = LogManager.getLogger(AppMain.class);

	/**
	 * Main method that will call the {@link WebPageAnalyzer} instance of an
	 * analyzer component and perform analysis using an absolute path provided
	 * as parameter, as well as the search pattern to be used (defined by an
	 * {@link SearchPatternType} enum) and an optional {@link HashMap} }map of
	 * headers for the HTTP connection.
	 * 
	 * @param args
	 *            Whenever this application is being run from console, the first
	 *            and unique argument (args[0]) should be the absolute path of a
	 *            file holding a list of URLs the user wish to search a pattern
	 *            on their context. If running from this main in an IDE, you can
	 *            assign args[0] the path in which your file is located.
	 * @throws InvalidFilePathException
	 *             Thrown whenever the file provided is not valid (file name or
	 *             path is incorrect or file does not exist)
	 * @throws NullPatternException
	 *             Thrown whenever the pattern is not provided, you must select
	 *             either HASHTAG or TWITTER ACCOUNT, but never set null.
	 */
	public static void main(String[] args) {
		/*
		 * If you're not running from console/terminal but running instead from
		 * an IDE like Eclipse/Netbeans/Others just take the assigned path in
		 * which your file locates. The provided one is the path in which I
		 * store my siteList.txt file that contains a list of URLs.
		 */
		if (args == null || args.length == 0) {
			args = new String[1];
			// This has been set by default in order to call this application.
			args[0] = "c:/data/siteList.txt";
		}

		try {
			WebPageAnalyzer webAnalyzer = new WebPageAnalyzer(args[0], SearchPatternType.HASHTAG_PATTERN, null);
			webAnalyzer.startAnalysis();
		} catch (InvalidFilePathException | NullPatternException e) {
			logger.error("Exception thrown in main method. Check log and stacktrace for further information.", e);
		}
	}
}
