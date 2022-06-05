package de.uni_leipzig.asv.tools.jwarcex.text_extraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

public class CorrectingTextExtractorImplTest {

	private final CorrectingTextExtractor textExtractor = new CorrectingTextExtractor();


	protected RawWarcDocument getRawWarcDocumentFromPath(String pathStr) throws IOException {
		return new RawWarcDocument("http://any-location", "any-date",
				Files.readAllBytes(Paths.get(pathStr)));
	}


	protected RawWarcDocument getRawWarcDocumentFromString(String str) {
		return new RawWarcDocument("http://any-location", "any-date", str.getBytes(Charsets.UTF_8));
	}
	
	@Test
	public void testGettersAndDefaultSettings() {

		CorrectingTextExtractor customCorrectingTextExtractor = new CorrectingTextExtractor();
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT, 
				customCorrectingTextExtractor.getMaximumReplacementCharsOccurrences());
		
		int maximumReplacementCharsOccurrences = 4;
		CorrectingTextExtractor customCorrectingTextExtractorTwo = new CorrectingTextExtractor(new TextExtractorImpl(), maximumReplacementCharsOccurrences);
		Assert.assertEquals(maximumReplacementCharsOccurrences, 
				customCorrectingTextExtractorTwo.getMaximumReplacementCharsOccurrences());
	}

	@Test
	public void testGetTextWithTooMuchEncodingErrors() throws IOException {

		RawWarcDocument rawWarcDocument = this.getRawWarcDocumentFromPath("src/test/resources/document1.txt");
		Assert.assertNull(this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8));
	}


	@Test
	public void testGetTextWithTolerableAmountOfEncodingErrors() throws IOException {

		RawWarcDocument rawWarcDocument = this.getRawWarcDocumentFromPath("src/test/resources/document2.txt");
		Assert.assertNotNull(this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8));
	}


	@Test
	public void testContainsEncodingErrors() {

		String html = "<body>Text with visible encoding errors ���</body>";

		Assert.assertTrue(this.textExtractor.containsEncodingErrors(html, 3));
		Assert.assertFalse(this.textExtractor.containsEncodingErrors(html, 4));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithInvalidParameters() {

		new CorrectingTextExtractor(new TextExtractorImpl(), -2);
	}


	@Test
	public void testGetTextDefaultParameters() {

		CorrectingTextExtractor customCorrectingTextExtractor = new CorrectingTextExtractor();

		String html = "<body>Text with visible encoding errors ����</body>";
		RawWarcDocument rawWarcDocument = new RawWarcDocument("http://any-location", "any-date",
				html.getBytes(Charsets.UTF_8));
		Assert.assertNull(customCorrectingTextExtractor.getText(rawWarcDocument, Charsets.UTF_8));
	}


	@Test
	public void testGetTextWithCustomThreshold() {

		CorrectingTextExtractor customCorrectingTextExtractor = new CorrectingTextExtractor(
				new TextExtractorImpl(0, 0, false), 2);

		String html = "<body>Text with visible encoding errors �</body>";
		RawWarcDocument rawWarcDocument = this.getRawWarcDocumentFromString(html);
		Assert.assertNotNull(customCorrectingTextExtractor.getText(rawWarcDocument, Charsets.UTF_8));

		String html2 = "<body>Text with visible encoding errors ��</body>";
		RawWarcDocument rawWarcDocument2 = this.getRawWarcDocumentFromString(html2);
		Assert.assertNull(customCorrectingTextExtractor.getText(rawWarcDocument2, Charsets.UTF_8));
	}


	@Test
	public void testGetTextWithDisabledFunctionality() {

		CorrectingTextExtractor customCorrectingTextExtractor = new CorrectingTextExtractor(
				new TextExtractorImpl(0, 0, false), -1);

		String html = "<body>Text with visible encoding errors �</body>";
		RawWarcDocument rawWarcDocument = this.getRawWarcDocumentFromString(html);
		Assert.assertNotNull(customCorrectingTextExtractor.getText(rawWarcDocument, Charsets.UTF_8));

		String html2 = "<body>Text with visible encoding errors ���������</body>";
		RawWarcDocument rawWarcDocument2 = this.getRawWarcDocumentFromString(html2);
		Assert.assertNotNull(customCorrectingTextExtractor.getText(rawWarcDocument2, Charsets.UTF_8));
	}
	
	
	@Test
	public void testClone() {
		
		CorrectingTextExtractor customCorrectingTextExtractor = new CorrectingTextExtractor(
				new TextExtractorImpl(0, 0, false), 2);
		
		CorrectingTextExtractor clonedTextExtractor = (CorrectingTextExtractor) customCorrectingTextExtractor.clone();
		Assert.assertNotEquals(customCorrectingTextExtractor, clonedTextExtractor);
		Assert.assertNotEquals(customCorrectingTextExtractor.getBaseTextExtractor(), 
				clonedTextExtractor.getBaseTextExtractor());
		
		Assert.assertEquals(2, clonedTextExtractor.getMaximumReplacementCharsOccurrences());
	}

}
