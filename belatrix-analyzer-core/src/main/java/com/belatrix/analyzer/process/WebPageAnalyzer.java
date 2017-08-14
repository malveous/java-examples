package com.belatrix.analyzer.process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.belatrix.analyzer.config.enums.SearchPatternType;
import com.belatrix.analyzer.exceptions.InvalidFilePathException;
import com.belatrix.analyzer.exceptions.NoContentToProcessException;
import com.belatrix.analyzer.exceptions.NullPatternException;
import com.belatrix.analyzer.process.task.WebPageAnalyzerTask;
import com.belatrix.analyzer.process.task.results.TaskResult;
import com.belatrix.analyzer.utils.AnalyzerAppUtils;

/**
 * The component that will perform analysis of web pages based on the input file
 * containing the URLs and a given pattern.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class WebPageAnalyzer {

	/**
	 * Default connection supported protocols
	 */
	private static String[] DEFAULT_CONNECTION_SUPPORTED_PROTOCOLS = new String[] { "html" };

	/**
	 * The logger for this class
	 */
	private static Logger logger = LogManager.getLogger(WebPageAnalyzer.class);

	/**
	 * The size of the thread pool.
	 */
	private static final int THREAD_POOL_SIZE = 3;

	/**
	 * The {@link String} instance representing the absolute path of the file
	 * holding the URLs.
	 */
	private String inputPath;

	/**
	 * The {@link Pattern} instance that will hold compiled regex.
	 */
	private Pattern searchPattern;

	/**
	 * The {@link Map} instance that will hold headers for HTTP URL connection
	 * through Jsoup.
	 */
	private Map<String, String> headersMap;

	/**
	 * Constructor of Web Analyzer component.
	 * 
	 * @param inputPath
	 *            The absolute path of the file that must be provided to get
	 *            URLs to be analyzed.
	 * @param patternType
	 *            The type of pattern that will be searched through the Web
	 *            pages.
	 * @param requiredConnectionHeaders
	 *            List of required HTTP connection headers to access Web Pages.
	 * @throws InvalidFilePathException
	 *             Thrown if the file provided is not valid.
	 * @throws NullPatternException
	 *             Thrown if the pattern provided is null.
	 */
	public WebPageAnalyzer(String inputPath, SearchPatternType patternType, Map<String, String> headersMap)
			throws InvalidFilePathException, NullPatternException {

		if (inputPath == null || inputPath.isEmpty() || !AnalyzerAppUtils.isValidFile(inputPath)) {
			throw new InvalidFilePathException();
		} else {
			this.inputPath = inputPath;
		}

		if (patternType == null) {
			throw new NullPatternException();
		} else {
			this.searchPattern = patternType.getRegexPattern();
		}

		this.headersMap = headersMap;
	}

	/**
	 * Starts the analysis of the Web Pages retrieved from the input text file
	 * using the provided pattern.
	 */
	public void startAnalysis() {
		Set<String> urlsToBeProcessed = null;

		try (Stream<String> stream = Files.lines(Paths.get(this.inputPath))) {

			urlsToBeProcessed = stream.collect(Collectors.toSet());

			if (urlsToBeProcessed != null && urlsToBeProcessed.size() > 0) {
				List<Callable<TaskResult>> pageAnalysisTasks = this.buildPageAnalysisTasksList(urlsToBeProcessed);
				ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
				List<Future<TaskResult>> results = executor.invokeAll(pageAnalysisTasks);
				executor.shutdown();
				boolean consoleReportEnabled = AnalyzerAppUtils
						.getApplicationBooleanProperty("analyzer.final.console.report.enabled");
				if (consoleReportEnabled) {
					logger.info(generateFinalTextReport(results));
				}
			} else {
				logger.warn("No content found in file. Throwing exception to caller method.");
				throw new NoContentToProcessException();
			}

		} catch (Exception e) {
			logger.error(
					"The analysis could not be performed successfully because of exception. Check logs for further information.",
					e);
		}
	}

	/**
	 * Generates a final text report that is logged by default or could be
	 * persisted through rolling file appender configured in log4j file
	 * 
	 * @param results
	 *            The list of task results of the processing.
	 * @return Text based process information consolidated.
	 * @throws ExecutionException
	 *             Whenever attempting to retrieve the result of a task that
	 *             aborted its process.
	 * @throws InterruptedException
	 *             Whenever the thread that is occupied or suspended is
	 *             interrupted.
	 */
	private String generateFinalTextReport(List<Future<TaskResult>> results)
			throws InterruptedException, ExecutionException {
		StringBuilder reportBuilder = new StringBuilder();
		for (Future<TaskResult> result : results) {
			TaskResult currentTaskResult = result.get();
			if (currentTaskResult != null) {
				reportBuilder.append(currentTaskResult.getMessage()).append(System.lineSeparator());
			} else {
				reportBuilder.append(TaskResult
						.createUnknownTaskResult(new NullPointerException("No task result found")).getMessage())
						.append(System.lineSeparator());
			}
		}
		return reportBuilder.toString();
	}

	/**
	 * Builds a list of tasks in which each task will be filled with data
	 * required for the analysis processing.
	 * 
	 * @param urlsToBeProcessed
	 *            The list of URLs to be processed.
	 * @return A set of tasks that must be handled by an executor service for
	 *         asynchronous processing. We use a set since we do not want to analyze an URL again.
	 * @throws IOException
	 *             Whenever the property called is not present.
	 */
	private List<Callable<TaskResult>> buildPageAnalysisTasksList(Set<String> urlsToBeProcessed) throws IOException {
		List<Callable<TaskResult>> tasks = new ArrayList<>();
		String[] supportedProtocols = AnalyzerAppUtils
				.getApplicationStringArrayCsvProperty("url.connection.supported.protocols");

		UrlValidator urlValidator = new UrlValidator(supportedProtocols != null && supportedProtocols.length > 0
				? supportedProtocols : DEFAULT_CONNECTION_SUPPORTED_PROTOCOLS);

		urlsToBeProcessed.forEach(urlAddress -> {
			if (urlValidator.isValid(urlAddress)) {
				tasks.add(new WebPageAnalyzerTask(urlAddress, this.searchPattern, this.headersMap));
			}
		});

		return tasks;
	}

}
