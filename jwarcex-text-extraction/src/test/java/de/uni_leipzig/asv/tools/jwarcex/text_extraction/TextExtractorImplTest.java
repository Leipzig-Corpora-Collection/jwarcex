package de.uni_leipzig.asv.tools.jwarcex.text_extraction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Strings;

public class TextExtractorImplTest {
	
	@Test
	public void testGetters() {
		
		int minLineLength = 20;
		int minDocumentLength = 40;
		boolean extractTitle = false;
		
		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(minLineLength, minDocumentLength, extractTitle, false);
		
		Assert.assertEquals(minLineLength, textExtractorImpl.getMineLineLength());
		Assert.assertEquals(minDocumentLength, textExtractorImpl.getMinDocumentLength());
		Assert.assertFalse(textExtractorImpl.extractTitle());
	}
	
	@Test
	public void testGetTextWithMinLineLengthParameter() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(10, 0, false, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = "<p>" + Strings.repeat("a", 10)
				+ "</p>\n" + "<p>" + Strings.repeat("a", 9) + "</p>";
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		String expectedText = Strings.repeat("a", 10);

		Assert.assertNotNull(processedWarcDocument);
		Assert.assertEquals(expectedText, processedWarcDocument.getContent());
	}


	@Test
	public void testGetTextWithMinDocumenLengthParameter() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 10, false,false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = "<p>" + Strings.repeat("a", 10) + "</p>\n";
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		String expectedText = Strings.repeat("a", 10);

		Assert.assertNotNull(processedWarcDocument);
		Assert.assertEquals(expectedText, processedWarcDocument.getContent());
	}


	@Test
	public void testGetTextWithMinDocumenLengthParameterAndTextIsTooShort() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 10, false, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = "<p>" + Strings.repeat("a", 9) + "</p>";
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		Assert.assertNull(processedWarcDocument);
	}


	@Test
	public void testGetTextWithTable() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/table1.txt")));
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		String expected = "C1 C2\na b\nc d";
		Assert.assertEquals(expected, processedWarcDocument.getContent());
	}


	@Test
	public void testGetTextWithTableSimple() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/table2.txt")));
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		String expected = "a b\nc d\ne f";
		Assert.assertEquals(expected, processedWarcDocument.getContent());
	}


	@Test
	public void testGetTextWithLinks() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/links.txt")));
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		String expected = "FirstLink SecondLink";
		Assert.assertEquals(expected, processedWarcDocument.getContent());
	}


	@Test
	public void testGetTextWithTitle() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, true, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/document_title.txt")));
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		// leading \n is probably superfluous
		String expected = "\ntitle\ntext";
		Assert.assertEquals(expected, processedWarcDocument.getContent());
	}


	@Test
	public void testGetTextWithoutTitle() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/document_title.txt")));
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		String expected = "text";
		Assert.assertEquals(expected, processedWarcDocument.getContent());
	}


	@Test
	public void testWhitespaceHandling() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false, false);
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		String htmlMinified = new String(
				Files.readAllBytes(Paths.get("src/test/resources/whitespace_test/table1_minified.txt")));
		Document documentMinified = Jsoup.parse(htmlMinified);
		ProcessedWarcDocument processedWarcDocumentMinified = textExtractorImpl.getText(
				this.toRawWarcDocument(htmlMinified),
				StandardCharsets.UTF_8);

		String htmlNormal = new String(
				Files.readAllBytes(Paths.get("src/test/resources/whitespace_test/table1_normal.txt")));
		ProcessedWarcDocument processedWarcDocumentNormal = textExtractorImpl.getText(
				this.toRawWarcDocument(htmlNormal),
				StandardCharsets.UTF_8);

		String textMinified = processedWarcDocumentMinified.getContent();
		String textNormal = processedWarcDocumentNormal.getContent();

		Assert.assertEquals(textMinified, textNormal);
	}
	
	
	@Test
	public void testClone() {
		
		int minLineLength = 20;
		int minDocumentLength = 40;
		boolean extractTitle = false;
		
		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(minLineLength, minDocumentLength, extractTitle, false);
		
		TextExtractorImpl clonedTextEctractor = (TextExtractorImpl) textExtractorImpl.clone();
		Assert.assertNotEquals(textExtractorImpl, clonedTextEctractor);
		
		Assert.assertEquals(minLineLength, clonedTextEctractor.getMineLineLength());
		Assert.assertEquals(minDocumentLength, clonedTextEctractor.getMinDocumentLength());
		Assert.assertFalse(clonedTextEctractor.extractTitle());
	}


	@Test
	public void testWithContentExtraction() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false, true);

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/document4_content_extraction.txt")));
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		Assert.assertFalse(processedWarcDocument.getContent().contains("link with long text"));
		Assert.assertNotNull(textExtractorImpl.getDomContentExtrator());
	}


	@Test
	public void testSemanticElements() throws IOException {

		TextExtractorImpl textExtractorImpl = new TextExtractorImpl(0, 0, false, true);

		String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/expected/document5_semantic_elements.txt")));

		String html = new String(Files.readAllBytes(Paths.get("src/test/resources/document5_semantic_elements.txt")));
		ProcessedWarcDocument processedWarcDocument = textExtractorImpl.getText(this.toRawWarcDocument(html),
				StandardCharsets.UTF_8);

		Assert.assertEquals(expected, processedWarcDocument.getContent());
	}


	protected RawWarcDocument toRawWarcDocument(String html) {

		return new RawWarcDocument("warc record id",
				"any location", "2020-02-02", html.getBytes(StandardCharsets.UTF_8));
	}


}
