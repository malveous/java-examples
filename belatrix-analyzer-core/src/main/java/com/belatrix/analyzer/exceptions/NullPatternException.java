package com.belatrix.analyzer.exceptions;

/**
 * Thrown when the application attempts to use a null value instead of enum.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class NullPatternException extends Exception {

	/**
	 * Serial version UID for this exception.
	 */
	private static final long serialVersionUID = -1761114583037604065L;

	/**
	 * Default message for this exception.
	 */
	private static final String DEFAULT_MSG = "The pattern selected is not valid. You'd have probably set as null. Provide a valid pattern";

	/**
	 * Constructor that will create the exception using default standard
	 * message.
	 */
	public NullPatternException() {
		super(DEFAULT_MSG);
	}

}
