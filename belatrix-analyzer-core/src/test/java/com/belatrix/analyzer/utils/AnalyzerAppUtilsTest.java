package com.belatrix.analyzer.utils;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.belatrix.analyzer.config.enums.FileFormatType;

public class AnalyzerAppUtilsTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Test
	public void testIsValidFile() {
		try {
			boolean isValid = AnalyzerAppUtils.isValidFile(tempFolder.newFile());
			assertTrue(isValid);
		} catch (IOException e) {
			fail("Exception on validation of file");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateFileName() {
		String generatedTextFileName = AnalyzerAppUtils.generateFileName(FileFormatType.PLAIN_TEXT_FILE);
		assertNotNull(generatedTextFileName);
		assertTrue(generatedTextFileName.endsWith(FileFormatType.PLAIN_TEXT_FILE.getFileExtension()));
		
		String generatedCsvFileName = AnalyzerAppUtils.generateFileName(FileFormatType.CSV_TEXT_FILE);
		assertNotNull(generatedCsvFileName);
		assertTrue(generatedCsvFileName.endsWith(FileFormatType.CSV_TEXT_FILE.getFileExtension()));
		
		String generatedXmlFileName = AnalyzerAppUtils.generateFileName(FileFormatType.XML_FILE);
		assertNotNull(generatedXmlFileName);
		assertTrue(generatedXmlFileName.endsWith(FileFormatType.XML_FILE.getFileExtension()));
	}
	
}
