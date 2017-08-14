package com.belatrix.analyzer.process.task.results;

import java.io.Serializable;

/**
 * Class that will manage objects with information about task execution.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class TaskResult implements Serializable {

	/**
	 * Generated serial version UID for this component
	 */
	private static final long serialVersionUID = -5065587996342304836L;

	/**
	 * Constant for success message
	 */
	private static final String SUCCESS_MSG = "Success";

	/**
	 * Constant for fail message
	 */
	private static final String FAIL_MSG = "Failed. Check logs for further details on the process. Exception cause: ";

	/**
	 * Constant when file is not found.
	 */
	private static final String NOT_GENERATED_MSG = "Not Generated";

	/**
	 * Constant for unknown property or resource related to process.
	 */
	private static final String UNKNOWN_MSG = "Unknown";

	/**
	 * Total time of processing displayed in seconds.
	 */
	private double processTotalTimeInSecs;
	/**
	 * The URL analyzed during the process.
	 */
	private String analyzedUrl;
	/**
	 * Determines if the result of analysis was successful or failed.
	 */
	private boolean successfulResult;
	/**
	 * Message to be displayed, logged or persisted for auditing purposes.
	 */
	private String message;
	/**
	 * Number of matches found.
	 */
	private int matchesFound;
	/**
	 * Generated output file absolute path
	 */
	private String generatedOutputFileAbsolutePath;

	/**
	 * Private constructor to avoid creating objects directly. The instances
	 * must not be able to change, just be initialized with certain values
	 * through statics methods defined.
	 * 
	 * @param startTime
	 *            The time in which the process starts.
	 * @param endTime
	 *            The time in which the process ends in case of success or fail.
	 * @param analyzedUrl
	 *            The URL that was processed.
	 * @param e
	 *            Throwable cause of exception (if occurs)
	 * @param matchesFound
	 *            The number of matches found in the Web Page.
	 */
	private TaskResult(long startTime, long endTime, String analyzedUrl, Throwable e, int matchesFound,
			String generatedOutputFileAbsolutePath) {
		this.processTotalTimeInSecs = (endTime - startTime) / 1000000000.0;
		this.analyzedUrl = analyzedUrl;
		this.successfulResult = e == null;
		this.matchesFound = matchesFound;
		this.generatedOutputFileAbsolutePath = generatedOutputFileAbsolutePath;
		this.message = String.format(
				"%s processed in %.4f seconds. Matches found: %d - Result status is: %s. Output file is: %s",
				this.analyzedUrl, processTotalTimeInSecs, matchesFound,
				successfulResult ? SUCCESS_MSG : FAIL_MSG + e.getClass() + ":" + e.getMessage(),
				successfulResult && generatedOutputFileAbsolutePath != null ? generatedOutputFileAbsolutePath
						: NOT_GENERATED_MSG);
	}

	/**
	 * Creates a successful task result.
	 * 
	 * @param startTime
	 *            The time in which the process starts.
	 * @param endTime
	 *            The time in which the process ends in case of success or fail.
	 * @param analyzedUrl
	 *            The URL that was processed.
	 * @param matchesFound
	 *            The number of matches found in the Web Page.
	 * @return instance of TaskResult for successful scenarios.
	 */
	public static TaskResult createSuccessfulTaskResult(long startTime, long endTime, String analyzedUrl,
			int matchesFound, String generatedFileAbsolutePath) {
		return new TaskResult(startTime, endTime, analyzedUrl, null, matchesFound, generatedFileAbsolutePath);
	}

	/**
	 * Creates a successful task result with no matches.
	 * 
	 * @param startTime
	 *            The time in which the process starts.
	 * @param endTime
	 *            The time in which the process ends in case of success or fail.
	 * @param targetUrl
	 *            The URL that was processed.
	 * @return instance of TaskResult for successful scenarios but zero matches.
	 */
	public static TaskResult createSuccessfulEmptyTaskResult(long startTime, long endTime, String analyzedUrl) {
		return new TaskResult(startTime, endTime, analyzedUrl, null, 0, null);
	}

	/**
	 * Creates a fail task result.
	 * 
	 * @param startTime
	 *            The time in which the process starts.
	 * @param endTime
	 *            The time in which the process ends in case of success or fail.
	 * @param targetUrl
	 *            The URL that was processed.
	 * @param e
	 *            Throwable cause of exception (if occurs)
	 * @return instance of TaskResult for failed scenarios.
	 */
	public static TaskResult createFailedTaskResult(long startTime, long endTime, String targetUrl, Throwable e) {
		return new TaskResult(startTime, endTime, targetUrl, e, 0, null);
	}

	public static TaskResult createUnknownTaskResult(Throwable e) {
		return new TaskResult(0, 0, UNKNOWN_MSG, e, 0, UNKNOWN_MSG);
	}

	/**
	 * Gets the task result message.
	 * 
	 * @return Result message as simple string.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the number of matches found.
	 * 
	 * @return Number of matches found, zero in case of fail.
	 */
	public int getMatchesFound() {
		return matchesFound;
	}

	/**
	 * Gets success or Fail status of processing
	 * 
	 * @return True if success, false otherwise.
	 */
	public boolean isSuccessFulResult() {
		return successfulResult;
	}

	/**
	 * Gets the duration of the process in seconds.
	 * 
	 * @return Process time in seconds.
	 */
	public double getProcessTotalTimeInSecs() {
		return processTotalTimeInSecs;
	}

	/**
	 * Gets the URL that was analyzed.
	 * 
	 * @return The analyzed URL.
	 */
	public String getAnalyzedUrl() {
		return analyzedUrl;
	}

	/**
	 * Gets the generated output file absolute path.
	 * 
	 * @return Output file path including all involved directories, file name
	 *         and extension if present.
	 */
	public String getGeneratedOutputFileAbsolutePath() {
		return generatedOutputFileAbsolutePath;
	}

}
