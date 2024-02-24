package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import com.google.common.base.Charsets;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonlWriterImplTest {
	private Path expectedXmlPath = Paths.get("src/test/resources/writer/sample.jsonl");

	private Path expectedMultiXmlPath = Paths.get("src/test/resources/writer/sample-multi.jsonl");


	@Test
	public void testWrite() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"http://localhost/",
				"Title",
				"2017-01-01",
				"abc",
				"UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);
		JsonlWriterImpl warcWriter = new JsonlWriterImpl(outputStream);

		warcWriter.write(processedWarcDocument);
		warcWriter.flush();

		warcWriter.close();

		String result = outputStream.toString();
		String expected = FileUtils.readFileToString(expectedXmlPath.toFile(), Charsets.UTF_8);
		Assert.assertEquals(expected, result);
	}


	@Test
	public void testWriteMulti() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"http://localhost/",
				"Title",
				"2017-01-01",
				"abc",
				"UTF-8");

		ProcessedWarcDocument processedWarcDocumentTwo = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"http://localhost/",
				"Title",
				"2017-01-01",
				"def",
				"UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);
		JsonlWriterImpl warcWriter = new JsonlWriterImpl(outputStream);

		warcWriter.write(processedWarcDocument);
		warcWriter.write(processedWarcDocumentTwo);
		warcWriter.flush();

		warcWriter.close();

		String result = outputStream.toString();
		String expected = FileUtils.readFileToString(expectedMultiXmlPath.toFile(), Charsets.UTF_8);
		Assert.assertEquals(expected, result);
	}


	@Test
	public void testWriteAsResource() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"http://localhost/",
				"Title",
				"2017-01-01",
				"abc",
				"UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);

		try (JsonlWriterImpl warcWriter = new JsonlWriterImpl(outputStream)) {
			warcWriter.write(processedWarcDocument);
			warcWriter.flush();
		}

		String result = outputStream.toString();
		String expected = FileUtils.readFileToString(expectedXmlPath.toFile(), Charsets.UTF_8);
		Assert.assertEquals(expected, result);
	}

}
