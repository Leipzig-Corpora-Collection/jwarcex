package de.uni_leipzig.asv.tools.jwarcex.text_extraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

@RunWith(Parameterized.class)
public class TextExtractorTest {

	private final TextExtractor textExtractor;


	public TextExtractorTest(TextExtractor textExtractor) {

		this.textExtractor = textExtractor;
	}


	@Parameters
	public static List<TextExtractor> buildTextExtractors() {

		return Arrays.asList((TextExtractor) new TextExtractorImpl(), (TextExtractor) new CorrectingTextExtractor());
	}


	protected RawWarcDocument getRawWarcDocumentFromPath(String pathStr) throws IOException {
		return new RawWarcDocument("<urn:uuid:00000000-0000-0000-0000-000000000000>","http://any-location", "any-date",
				Files.readAllBytes(Paths.get(pathStr)));
	}


	protected RawWarcDocument getRawWarcDocumentFromString(String str) {
		return new RawWarcDocument("<urn:uuid:00000000-0000-0000-0000-000000000000>","http://any-location", "any-date", str.getBytes(Charsets.UTF_8));
	}


	@Test
	public void testGetTextSimple() throws IOException {

		String html = "  " + Strings.repeat("ab", 100) + "\n  ";
		RawWarcDocument rawWarcDocument = getRawWarcDocumentFromString(html);
		String expectedHtml = Strings.repeat("ab", 100);
		Assert.assertEquals(expectedHtml, this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8).getContent());
	}


	@Test
	public void testGetTextEmpty() throws IOException {

		String html = "  \n  ";
		RawWarcDocument rawWarcDocument = getRawWarcDocumentFromString(html);
		Assert.assertNull(this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8));
	}


	@Test
	public void testGetTextWithInvalidHtmlInHeadNoscript() throws IOException {

		String html = "<html>" + "<head><noscript><link /><style></style> <meta />" + "<div style=\"display:inline;\">"
				+ Strings.repeat("ab", 100) + "</div></noscript>" + "</head>" + "<body>" + Strings.repeat("ab", 100)
				+ "</body>" + "</html>";
		RawWarcDocument rawWarcDocument = getRawWarcDocumentFromString(html);
		String expected = Strings.repeat("ab", 100);
		Assert.assertEquals(expected, this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8).getContent());
	}


	@Test
	public void testGetTextWithEscapedSpecialChars() throws IOException {

		String html = "&lt;" + Strings.repeat("ab", 100) + "&gt;";
		RawWarcDocument rawWarcDocument = getRawWarcDocumentFromString(html);
		String expected = "<" + Strings.repeat("ab", 100) + ">";
		Assert.assertEquals(expected, this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8).getContent());
	}


	@Test
	public void testGetTextFull() throws IOException {

		String html = "<div><p>" + Strings.repeat("ab", 100) + "</p>" + "<p>" + Strings.repeat("cd", 100)
				+ "</p></div>";
		RawWarcDocument rawWarcDocument = getRawWarcDocumentFromString(html);
		String expected = Strings.repeat("ab", 100) + "\n" + Strings.repeat("cd", 100);
		Assert.assertEquals(expected, this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8).getContent());
	}


	// contains one line with too few characters
	@Test
	public void testGetTextWithLessThanMinimumDocumentLength() throws IOException {

		RawWarcDocument rawWarcDocument = getRawWarcDocumentFromPath("src/test/resources/document_short.txt");
		Assert.assertNull(this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8));
	}


	// contains multiple lines which have too few characters by itself
	@Test
	public void testGetTextWithManyShortLines() throws IOException {

		RawWarcDocument rawWarcDocument = getRawWarcDocumentFromPath("src/test/resources/document_short2.txt");
		Assert.assertNull(this.textExtractor.getText(rawWarcDocument, Charsets.UTF_8));
	}

}
