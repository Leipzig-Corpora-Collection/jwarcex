package de.uni_leipzig.asv.tools.jwarcex.text_extraction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Strings;

public class TextExtractorImplTest {

	@Test
	public void testGetTextWithMinLineLengthParameter() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(10, 0, false);

		Document document = Jsoup.parse("<p>" + Strings.repeat("a", 10)
				+ "</p>\n" + "<p>" + Strings.repeat("a", 9) + "</p>");
		String expectedText = Strings.repeat("a", 10);

		String returnedText = textExtractorImpl.extractText(document);

		Assert.assertNotNull(returnedText);
		Assert.assertEquals(expectedText, returnedText);
	}


	@Test
	public void testGetTextWithMinDocumenLengthParameter() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 10, false);

		Document document = Jsoup.parse("<p>" + Strings.repeat("a", 10) + "</p>\n");
		String expectedText = Strings.repeat("a", 10);

		String returnedText = textExtractorImpl.extractText(document);

		Assert.assertNotNull(returnedText);
		Assert.assertEquals(expectedText, returnedText);
	}


	@Test
	public void testGetTextWithMinDocumenLengthParameterAndTextIsTooShort() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 10, false);

		Document document = Jsoup.parse("<p>" + Strings.repeat("a", 9) + "</p>");
		String returnedText = textExtractorImpl.extractText(document);

		Assert.assertNull(returnedText);
	}


	@Test
	public void testGetTextWithTable() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false);

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/table1.txt")));
		Document document = Jsoup.parse(html);
		String text = textExtractorImpl.extractText(document);

		String expected = "C1 C2\na b\nc d";
		Assert.assertEquals(expected, text);
	}


	@Test
	public void testGetTextWithTableSimple() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false);

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/table2.txt")));
		Document document = Jsoup.parse(html);
		String text = textExtractorImpl.extractText(document);

		String expected = "a b\nc d\ne f";
		Assert.assertEquals(expected, text);
	}


	@Test
	public void testGetTextWithLinks() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false);

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/links.txt")));
		Document document = Jsoup.parse(html);
		String text = textExtractorImpl.extractText(document);

		String expected = "FirstLink SecondLink";
		Assert.assertEquals(expected, text);
	}


	@Test
	public void testGetTextWithTitle() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, true);

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/document_title.txt")));
		Document document = Jsoup.parse(html);
		String text = textExtractorImpl.extractText(document);

		// leading \n is probably superfluous
		String expected = "\ntitle\ntext";
		Assert.assertEquals(expected, text);
	}


	@Test
	public void testGetTextWithoutTitle() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false);

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/document_title.txt")));
		Document document = Jsoup.parse(html);
		String text = textExtractorImpl.extractText(document);

		String expected = "text";
		Assert.assertEquals(expected, text);
	}


	@Test
	public void testWhitespaceHandling() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false);

		String htmlMinified = new String(
				Files.readAllBytes(Paths.get("src/test/resources/whitespace_test/table1_minified.txt")));
		Document documentMinified = Jsoup.parse(htmlMinified);

		String htmlNormal = new String(
				Files.readAllBytes(Paths.get("src/test/resources/whitespace_test/table1_normal.txt")));
		Document documentNormal = Jsoup.parse(htmlNormal);

		String textMinified = textExtractorImpl.extractText(documentMinified);
		String textNormal = textExtractorImpl.extractText(documentNormal);

		Assert.assertEquals(textMinified, textNormal);
	}

}
