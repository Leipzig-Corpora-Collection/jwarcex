package de.uni_leipzig.asv.tools.jwarcex.core.extract;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;


import de.uni_leipzig.asv.tools.jwarcex.core.writer.JsonlWriterImpl;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.SourceFormatWarcWriterImpl;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.WarcWriter;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.WetWriterImpl;
import de.uni_leipzig.asv.tools.jwarcex.core.writer.XmlWarcWriterImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.OutputFormat;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetectorImpl;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

public class ParallelWarcExtractorTest {

	private final ParallelWarcExtractor parallelWarcExtractor = Mockito.spy(ParallelWarcExtractor.class);

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private final Path warcPath = Paths.get("src/test/resources/warc/za_web_2015.00206.10records.test.warc");

	private Path outPath;


	@Before
	public void before() throws IOException {

		this.outPath = new File(this.tempFolder.getRoot(), "out.source").toPath();
	}


	@Test
	public void testCreateWarcWriterSource() throws IOException {

		OutputStream outputStreamSpy = Mockito.spy(FileUtils.openOutputStream(this.outPath.toFile()));

		parallelWarcExtractor.setOutputFormat(OutputFormat.SOURCE);
		WarcWriter warcWriter = this.parallelWarcExtractor.createWarcWriter(outputStreamSpy);

		Assert.assertTrue(warcWriter.getClass().equals(SourceFormatWarcWriterImpl.class));
	}


	@Test
	public void testCreateWarcWriterWet() throws IOException {

		OutputStream outputStreamSpy = Mockito.spy(FileUtils.openOutputStream(this.outPath.toFile()));

		parallelWarcExtractor.setOutputFormat(OutputFormat.WET);
		WarcWriter warcWriter = this.parallelWarcExtractor.createWarcWriter(outputStreamSpy);
		Assert.assertTrue(warcWriter.getClass().equals(WetWriterImpl.class));
	}


	@Test
	public void testCreateWarcWriterJsonl() throws IOException {

		OutputStream outputStreamSpy = Mockito.spy(FileUtils.openOutputStream(this.outPath.toFile()));

		parallelWarcExtractor.setOutputFormat(OutputFormat.JSONL);
		WarcWriter warcWriter = this.parallelWarcExtractor.createWarcWriter(outputStreamSpy);
		Assert.assertTrue(warcWriter.getClass().equals(JsonlWriterImpl.class));
	}


	@Test
	public void testCreateWarcWriterXml() throws IOException {

		OutputStream outputStreamSpy = Mockito.spy(FileUtils.openOutputStream(this.outPath.toFile()));

		parallelWarcExtractor.setOutputFormat(OutputFormat.XML);
		WarcWriter warcWriter = this.parallelWarcExtractor.createWarcWriter(outputStreamSpy);
		Assert.assertTrue(warcWriter.getClass().equals(XmlWarcWriterImpl.class));
	}


	@Test
	public void testExtractCallsPrepareProcessCleanup() throws IOException {
		Mockito.doNothing().when((AbstractWarcExtractor)this.parallelWarcExtractor)
				.extract(this.warcPath, this.outPath);
		Mockito.doNothing().when(this.parallelWarcExtractor).prepare();
		Mockito.doNothing().when(this.parallelWarcExtractor).processWarc(Mockito.any(RawWarcDocument.class));
		Mockito.doNothing().when(this.parallelWarcExtractor).cleanup();

		this.parallelWarcExtractor.extract(this.warcPath, this.outPath);
	}


	@Test
	public void testExtractArgumentsWithNumThreadsArgument() throws IOException {

		int numThreads = 4;

		this.parallelWarcExtractor.setNumberOfThreads(numThreads);

		Mockito.doNothing().when((AbstractWarcExtractor)this.parallelWarcExtractor)
				.extract(this.warcPath, this.outPath);
		Mockito.doNothing().when(this.parallelWarcExtractor).prepare();
		Mockito.doNothing().when(this.parallelWarcExtractor).processWarc(Mockito.any(RawWarcDocument.class));
		Mockito.doNothing().when(this.parallelWarcExtractor).cleanup();

		this.parallelWarcExtractor.extract(this.warcPath, this.outPath);

		Mockito.verify(this.parallelWarcExtractor).setNumberOfThreads(numThreads);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testExtractArgumentsWithInvalidNumThreadsArgument() throws IOException {

		int numThreads = 0;
		new ParallelWarcExtractor(new EncodingDetectorImpl(),
				new TextExtractorImpl(0, 0, false, false),
				OutputFormat.SOURCE, numThreads);
	}


	@Test
	public void testProcessWarcWithRobotsTxt() throws UnsupportedEncodingException {

		RawWarcDocument rawWarcDocument = new RawWarcDocument("<urn:uuid:00000000-0000-0000-0000-000000000000>",
				"http://www.example.com/robots.txt",
				"2016-09-13T23:59:38Z",
				"This is my content".getBytes("UTF-8"));

		this.parallelWarcExtractor.processWarc(rawWarcDocument);

		Mockito.verify(this.parallelWarcExtractor, Mockito.times(0)).addToQueue(rawWarcDocument);
	}


	@Test
	public void testGettersAndSetters() {

		Assert.assertEquals(OutputFormat.SOURCE, parallelWarcExtractor.getOutputFormat());

		parallelWarcExtractor.setOutputFormat(OutputFormat.XML);
		Assert.assertEquals(OutputFormat.XML, parallelWarcExtractor.getOutputFormat());
	}
}
