package com.belatrix.analyzer.exceptions;

/**
 * Thrown when the application reads the main input file but there is no content
 * to be processed.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class NoContentToProcessException extends Exception {

	/**
	 * Serial version UID for this exception.
	 */
	private static final long serialVersionUID = -6634149302742570578L;
	/**
	 * Default message for this exception.
	 */
	private static final String DEFAULT_MSG = "The input file provided does not contain relevant information for analysis.";

	/**
	 * Constructor that will create the exception using default standard
	 * message.
	 */
	public NoContentToProcessException() {
		super(DEFAULT_MSG);
	}

}
