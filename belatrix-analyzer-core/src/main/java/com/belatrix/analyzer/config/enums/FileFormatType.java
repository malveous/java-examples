package com.belatrix.analyzer.config.enums;

/**
 * Enum component that will hold the type of files supported.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public enum FileFormatType {

	PLAIN_TEXT_FILE("txt"), XML_FILE("xml"), CSV_TEXT_FILE("csv");

	/**
	 * The {@link String} instance representing file extension.
	 */
	private String fileExtension;

	/**
	 * Constructor for enum based on the file extension.
	 * 
	 * @param fileExtension
	 *            Is the file extension to be used.
	 */
	private FileFormatType(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * Gets the file extension.
	 * 
	 * @return the extension name.
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * Sets the file extension.
	 * 
	 * @param fileExtension
	 *            To be set.
	 */
	public void setProtocolName(String fileExtension) {
		this.fileExtension = fileExtension;
	}

}
