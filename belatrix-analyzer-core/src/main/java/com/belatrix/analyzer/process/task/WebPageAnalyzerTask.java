package com.belatrix.analyzer.process.task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.belatrix.analyzer.config.enums.FileFormatType;
import com.belatrix.analyzer.process.task.results.TaskResult;
import com.belatrix.analyzer.utils.AnalyzerAppUtils;

/**
 * Task component that will be executed by a thread. It will perform the
 * analysis of the Web Page as well as the persistence of results in file.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class WebPageAnalyzerTask implements Callable<TaskResult> {

	/**
	 * The logger for this class
	 */
	private static Logger logger = LogManager.getLogger(WebPageAnalyzerTask.class);

	/**
	 * The {@link String} instance representing the URL to be accessed for
	 * analysis.
	 */
	private String targetUrl;

	/**
	 * The {@link Pattern} instance that will be used for analysis.
	 */
	private Pattern searchPattern;

	/**
	 * Map containing the headers required for connection.
	 */
	private Map<String, String> headersMap;

	/**
	 * Constructor for the task that will hold required information for
	 * processing.
	 * 
	 * @param targetUrl
	 *            The url to be analyzed during the process.
	 * @param searchPattern
	 *            The pattern to be applied for search and analysis of web
	 *            content of the URL provided.
	 * @param headersMap
	 *            The map with headers for HTTP connection.
	 */
	public WebPageAnalyzerTask(String targetUrl, Pattern searchPattern, Map<String, String> headersMap) {
		this.targetUrl = targetUrl;
		this.searchPattern = searchPattern;
		this.headersMap = headersMap;
	}

	/**
	 * Will perform URL connection to retrieve content, search for a pattern and
	 * save results in case of success generating result objects for further
	 * information.
	 */
	@Override
	public TaskResult call() throws Exception {
		long startTime = System.nanoTime();
		TaskResult taskResultObj = null;
		String htmlContent = null;
		List<String> resultContent = null;

		String outputDirectory = null;
		String fileName = null;

		String contentAsString = null;
		
		try {
			htmlContent = this.getHtmlBodyContent();
			resultContent = this.getAnalysisResults(htmlContent);
			outputDirectory = AnalyzerAppUtils.getOutputDirectory();
			fileName = AnalyzerAppUtils.generateFileName(FileFormatType.PLAIN_TEXT_FILE);
			contentAsString = this.parseListToLine(resultContent);
			
			if (contentAsString == null || contentAsString.isEmpty()) {
				long endTime = System.nanoTime();
				taskResultObj = TaskResult.createSuccessfulEmptyTaskResult(startTime, endTime, this.targetUrl);
			} else {
				try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputDirectory, fileName),
						StandardCharsets.UTF_8)) {
					bw.write(contentAsString);
					long endTime = System.nanoTime();
					taskResultObj = TaskResult.createSuccessfulTaskResult(startTime, endTime, this.targetUrl,
							resultContent.size(), outputDirectory + fileName);
				} catch (IOException e) {
					throw e;
				}
			}
		} catch (Exception e) {
			logger.error(String.format("Exception thrown during analysis task process of URL: %s. Main Cause: %s.", this.targetUrl, e.getMessage()));
			long endTime = System.nanoTime();
			taskResultObj = TaskResult.createFailedTaskResult(startTime, endTime, this.targetUrl, e);
		}

		return taskResultObj;
	}

	/**
	 * Converts a list of string elements to a single String text.
	 * 
	 * @param resultContent
	 *            List of string elements to be processed.
	 * @return Plain String text separated by lines.
	 */
	private String parseListToLine(List<String> resultContent) {
		final StringBuilder sb = new StringBuilder();

		if (resultContent != null && !resultContent.isEmpty()) {
			resultContent.forEach(line -> sb.append(line).append(System.lineSeparator()));
		}
		return sb.toString();
	}

	/**
	 * Gets all the words that matches a pattern in an HTML body content.
	 * 
	 * @param htmlBodyContent
	 *            The body content of HTML.
	 * @return A list of words found based on a specific pattern.
	 */
	private List<String> getAnalysisResults(String htmlBodyContent) {
		List<String> analysisContentList = new ArrayList<>();
		Matcher matcher = this.searchPattern.matcher(htmlBodyContent);

		while (matcher.find()) {
			analysisContentList.add(matcher.group(1));
		}

		return analysisContentList;
	}

	/**
	 * Retrieves the body text content from HTML page based on a connection to
	 * an URL.
	 * 
	 * @return Text content of body inside HTML document.
	 * @throws IOException
	 *             Whenever some exception occurs during connection to the URL.
	 */
	private String getHtmlBodyContent() throws IOException {
		Connection connection = Jsoup.connect(this.targetUrl).maxBodySize(0)
				.timeout(AnalyzerAppUtils.getApplicationIntProperty("url.connection.timeout"));
		if (this.headersMap == null || this.headersMap.size() > 0) {
			this.headersMap = AnalyzerAppUtils.buildDefaultHttpHeaders();
		}
		connection.headers(this.headersMap);
		Document htmlDocument = connection.get();
		return htmlDocument.body().text();
	}

}
