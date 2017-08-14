package com.belatrix.analyzer.config.enums;

import java.util.regex.Pattern;

/**
 * Enum component that will hold the supported patterns of the application.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public enum SearchPatternType {

	HASHTAG_PATTERN("(#\\w+)\\b"), TWITTER_ACCOUNT_PATTERN("(@\\w+)\\b");

	/**
	 * The {@link String} instance representing the regex required pattern.
	 */
	private String regex;

	/**
	 * Constructor for enum based on the regex String value.
	 * 
	 * @param regex
	 *            Is the regex to be used
	 */
	private SearchPatternType(String regex) {
		this.regex = regex;
	}

	/**
	 * Gets the regex as String.
	 * 
	 * @return the regex.
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * Sets the regex.
	 * 
	 * @param regex
	 *            To be set.
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * Gets a compiled regex pattern based on type of search selected.
	 * 
	 * @return The compiled pattern to be used.
	 */
	public Pattern getRegexPattern() {
		return Pattern.compile(this.regex);
	}

}
