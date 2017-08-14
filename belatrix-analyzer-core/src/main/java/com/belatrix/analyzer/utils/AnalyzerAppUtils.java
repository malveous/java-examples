package com.belatrix.analyzer.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.belatrix.analyzer.config.enums.FileFormatType;

/**
 * Component that manage useful methods for application processing.
 * 
 * @author <a href="mailto:marcelo.tataje@gmail.com">Marcelo Tataje</a>
 *
 */
public class AnalyzerAppUtils {

	/**
	 * The logger for this class
	 */
	private static Logger logger = LogManager.getLogger(AnalyzerAppUtils.class);

	/**
	 * Default empty string constant value.
	 */
	private static final String EMPTY_STRING = "";

	/**
	 * Constant for application properties file name
	 */
	private static final String APP_PROPERTIES_FILE_NAME = "application.properties";

	/**
	 * Constant for default user-agent header key
	 */
	private static final String DEFAULT_USER_AGENT_HEADER_KEY = "User-Agent";

	/**
	 * Constant for default accept-encoding header key
	 */
	private static final String DEFAULT_ACCEPT_ENCODING_HEADER_KEY = "Accept-Encoding";

	/**
	 * The properties required for the application processing
	 */
	private static Properties appProps;

	/**
	 * Private constructor to avoid instances creation
	 */
	private AnalyzerAppUtils() {

	}

	/**
	 * This method checks if a file is valid and exists.
	 * 
	 * @param targetToValidate
	 *            Could be a {@link File} File instance or String representing
	 *            file path.
	 * @return True if the file is valid, false otherwise.
	 */
	public static boolean isValidFile(Object targetToValidate) {
		File fileRef = null;

		if (targetToValidate instanceof File) {
			fileRef = (File) targetToValidate;
		} else if (targetToValidate instanceof String) {
			fileRef = new File((String) targetToValidate);
		} else {
			return false;
		}

		return fileRef.exists() && fileRef.isFile();
	}

	/**
	 * Generate a file name using UUID to avoid repeated file names (include
	 * extension based on enum)
	 * 
	 * @param fileType
	 *            The type of the file so the extension can be set. If no file
	 *            type specified, then no extension is added.
	 * @return The generated file name.
	 */
	public static String generateFileName(FileFormatType fileType) {
		String extension = fileType.getFileExtension();
		return String.format("%s.%s", UUID.randomUUID().toString(), extension != null ? extension : EMPTY_STRING);
	}

	/**
	 * Gets an application property as String from the properties source file
	 * making sure the file is loaded once.
	 * 
	 * @param key
	 *            The key of the property to be retrieved.
	 * @return The value of the property if exists as String, null otherwise
	 * @throws IOException
	 *             Whenever the properties file cannot be loaded due incorrect
	 *             properties file name or missing file
	 */
	public static String getApplicationStringProperty(String key) throws IOException {
		if (appProps == null) {
			try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(APP_PROPERTIES_FILE_NAME)) {
				appProps = new Properties();
				appProps.load(inputStream);
				logger.debug(String.format("Properties file: '%s' was loaded", APP_PROPERTIES_FILE_NAME));
				inputStream.close();
			}
		}
		return appProps.getProperty(key);
	}

	/**
	 * Gets an application property as int primitive type from the properties
	 * source file wrapping the base
	 * {@link #getApplicationStringProperty(String)} method
	 * 
	 * @param key
	 *            The key of the property to be retrieved.
	 * @return The value of the property if exists as int, null otherwise an
	 *         exception must be thrown.
	 * @throws NumberFormatException
	 *             Whenever the value cannot be converted to an int number.
	 * @throws IOException
	 *             Whenever the properties file cannot be loaded due incorrect
	 *             properties file name or missing file
	 */
	public static int getApplicationIntProperty(String key) throws NumberFormatException, IOException {
		return Integer.parseInt(getApplicationStringProperty(key));
	}

	/**
	 * Gets an application property as int primitive type from the properties
	 * source file wrapping the base
	 * {@link #getApplicationStringProperty(String)} method
	 * 
	 * @param key
	 *            The key of the property to be retrieved.
	 * @return The value of the property if exists as boolean, false in case of
	 *         property not found.
	 * @throws IOException
	 *             Whenever the properties file cannot be loaded due incorrect
	 *             properties file name or missing file
	 */
	public static boolean getApplicationBooleanProperty(String key) throws IOException {
		return Boolean.valueOf(getApplicationStringProperty(key));
	}

	/**
	 * Gets an application property that has CSV format (comma separated values)
	 * as String array type from the properties source file wrapping the source
	 * file wrapping the base {@link #getApplicationStringProperty(String)}
	 * method
	 * 
	 * @param key
	 *            The key of the property to be retrieved.
	 * @returnThe value of the property if exists as String array.
	 * @throws IOException
	 *             Whenever the properties file cannot be loaded due incorrect
	 *             properties file name or missing file
	 */
	public static String[] getApplicationStringArrayCsvProperty(String key) throws IOException {
		String[] csvs = null;
		String rawValue = getApplicationStringProperty(key);
		if (rawValue != null && !rawValue.isEmpty()) {
			csvs = rawValue.split(",");
		}
		return csvs;
	}

	/**
	 * This method will create an output directory to put all the results.
	 * 
	 * @return Absolute path of the output directory
	 * @throws IOException
	 *             Whenever the properties required cannot be loaded.
	 */
	public static String getOutputDirectory() throws IOException {
		String outputDir = "";
		boolean useDefaultOutputFolder = getApplicationBooleanProperty("analyzer.default.folder.enabled");

		if (useDefaultOutputFolder) {
			outputDir = System.getProperty("user.home") + File.separator
					+ getApplicationStringProperty("analyzer.default.folder.name") + File.separator;
			logger.debug(String.format("Using default output directory for processing: %s", outputDir));
		} else {
			outputDir = getApplicationStringProperty("analyzer.custom.absolute.folder.path");
			logger.debug(String.format("Using custom output directory for processing: %s", outputDir));
		}
		
		File outputDirectoryReference = new File(outputDir);
		
		if (!outputDirectoryReference.exists() || !outputDirectoryReference.isDirectory()) {
			outputDirectoryReference.mkdirs();
		}
		
		return outputDir;
	}

	/**
	 * Build the HTTP default headers map.
	 * 
	 * @return The headers map required by default.
	 * @throws IOException
	 *             Whenever the properties required cannot be loaded.
	 */
	public static Map<String, String> buildDefaultHttpHeaders() throws IOException {
		Map<String, String> defaultHttpHeadersMap = new HashMap<>();
		defaultHttpHeadersMap.put(DEFAULT_USER_AGENT_HEADER_KEY,
				getApplicationStringProperty("http.headers.user.agent"));
		defaultHttpHeadersMap.put(DEFAULT_ACCEPT_ENCODING_HEADER_KEY,
				getApplicationStringProperty("http.headers.accept.encoding"));
		return defaultHttpHeadersMap;
	}

}
