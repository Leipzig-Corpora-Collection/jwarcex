package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;

public class SourceFormatWarcWriterImplTest {

	@Test
	public void testWrite() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"2017-01-01",
				"abc",
				"UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);
		SourceFormatWarcWriterImpl warcWriter = new SourceFormatWarcWriterImpl(outputStream);

		warcWriter.write(processedWarcDocument);
		warcWriter.flush();

		warcWriter.close();

		String result = outputStream.toString();
		Assert.assertEquals("<source>"
				+ "<location>http://localhost/</location><date>2017-01-01</date><encoding>UTF-8</encoding></source>\n\n"
				+ "abc\n\n", result);

	}


	@Test
	public void testWriteAsRessource() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"2017-01-01",
				"abc",
				"UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);

		try (SourceFormatWarcWriterImpl warcWriter = new SourceFormatWarcWriterImpl(outputStream)) {
			warcWriter.write(processedWarcDocument);
			warcWriter.flush();
		}

		String result = outputStream.toString();
		Assert.assertEquals("<source>"
				+ "<location>http://localhost/</location><date>2017-01-01</date><encoding>UTF-8</encoding></source>\n\n"
				+ "abc\n\n", result);

	}

}
