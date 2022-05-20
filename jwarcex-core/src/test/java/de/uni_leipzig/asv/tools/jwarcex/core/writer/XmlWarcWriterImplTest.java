package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;

public class XmlWarcWriterImplTest {

	private Path expectedXmlPath = Paths.get("src/test/resources/writer/sample.xml");


	@Test
	public void testWrite() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument("http://localhost/", "2017-01-01",
				"abc", "UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);
		XmlWarcWriterImpl warcWriter = new XmlWarcWriterImpl(outputStream);

		warcWriter.write(processedWarcDocument);
		warcWriter.flush();

		warcWriter.close();

		String result = outputStream.toString();
		String expected = FileUtils.readFileToString(expectedXmlPath.toFile(), Charsets.UTF_8);
		Assert.assertEquals(expected, result);

	}


	@Test
	public void testWriteAsRessource() throws Exception {

		ProcessedWarcDocument processedWarcDocument = new ProcessedWarcDocument("http://localhost/", "2017-01-01",
				"abc", "UTF-8");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);

		try (XmlWarcWriterImpl warcWriter = new XmlWarcWriterImpl(outputStream)) {
			warcWriter.write(processedWarcDocument);
			warcWriter.flush();
		}

		String result = outputStream.toString();
		String expected = FileUtils.readFileToString(expectedXmlPath.toFile(), Charsets.UTF_8);
		Assert.assertEquals(expected, result);

	}

}
