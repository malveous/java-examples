package com.belatrix.analyzer.exceptions;

/**
 * Thrown when the application attempts to use an invalid file as input for the
 * process. These includes:
 * <p>
 * <ul>
 * <li>The path or the file is null.
 * <li>The path or the file is empty String.
 * </ul>
 * <p>
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class InvalidFilePathException extends Exception {

	/**
	 * Serial version UID for this exception.
	 */
	private static final long serialVersionUID = -8616340445166162258L;

	/**
	 * Default message for this exception.
	 */
	private static final String DEFAULT_MSG = "The input file path provided is not valid.";

	/**
	 * Constructor that will create the exception using default standard
	 * message.
	 */
	public InvalidFilePathException() {
		super(DEFAULT_MSG);
	}

}
