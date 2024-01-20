package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import com.google.common.base.Charsets;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WetWriterImplTest {

	private Path expectedWetPath = Paths.get("src/test/resources/writer/sample.wet");

	private Path expectedMultiWetPath = Paths.get("src/test/resources/writer/sample-multi.wet");


	@Test
	public void testWrite() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"2017-01-01",
				"abc",
				"UTF-8");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);

		WetWriterImpl warcWriter = Mockito.spy(new WetWriterImpl(outputStream));

		warcWriter.write(processedWarcDocument);
		warcWriter.flush();

		warcWriter.close();

		String result = this.replaceUrnsAndDates(outputStream.toString());
		String expected = this.loadTestFile(expectedWetPath);
		Assert.assertEquals(expected, result);
	}


	@Test
	public void testWriteMulti() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"2017-01-01",
				"abc",
				"UTF-8");

		ProcessedWarcDocument processedWarcDocumentTwo = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"2017-01-01",
				"def",
				"UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);
		WetWriterImpl warcWriter = Mockito.spy(new WetWriterImpl(outputStream));

		warcWriter.write(processedWarcDocument);
		warcWriter.write(processedWarcDocumentTwo);
		warcWriter.flush();

		warcWriter.close();

		String result = this.replaceUrnsAndDates(outputStream.toString());
		String expected = this.loadTestFile(expectedMultiWetPath);
		Assert.assertEquals(expected, result);
	}


	@Test
	public void testWriteAsResource() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument(
				"<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://localhost/",
				"2017-01-01",
				"abc",
				"UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);

		try (WetWriterImpl warcWriter = new WetWriterImpl(outputStream)) {

			warcWriter.write(processedWarcDocument);
			warcWriter.flush();
		}

		String result = this.replaceUrnsAndDates(outputStream.toString());
		String expected = this.loadTestFile(expectedWetPath);
		Assert.assertEquals(expected, result);
	}


	protected String loadTestFile(Path testFilePath) throws IOException {
		String content = FileUtils.readFileToString(testFilePath.toFile(), Charsets.UTF_8);
		return this.replaceUrnsAndDates(content);
	}


	// This replaces all occurrences of URNs and dates.
	// TODO: test that all occurring URNs are different
	protected String replaceUrnsAndDates(String text) {
		text = text.replaceAll("<urn:uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}>",
				"<urn:uuid:00000000-0000-0000-0001-000000000001>");
		text = text.replaceAll("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ",
				"2024-01-14T15:20:06Z");
		text = text.replaceAll("(Mon|Tue|Wed|Thu|Fri|Sat|Sun), " +
						"\\d\\d? (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) " +
						"\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d GMT",
				"Sat, 20 Jan 2024 11:51:58 GMT");
		return text;
	}

}
