package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config.WarcExtractorAdditionalParameters;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options.CommandLineOptions;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options.JWarcExCommandLineOptions;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.CorrectingTextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;

@RunWith(MockitoJUnitRunner.class)
public class WarcExtractorActionTest {

	@Spy
	private WarcExtractorAction warcExtractorActionSpy;

	@Mock
	private ParallelWarcExtractor warcExtractorMock;

	private final CommandLineParser parser = new DefaultParser();

	private final CommandLineOptions options = new JWarcExCommandLineOptions();


	private CommandLine parse(String[] args) throws ParseException {

		return this.parser.parse(this.options.getOptions(), args);
	}


	@Test
	public void testWithPaths() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/ch_web_2016.00046.3records.test.warc",
				"target/out.source" };
		CommandLine commandLine = this.parse(args);

		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(Path.class), Mockito.any(Path.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertEquals(false, additionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				additionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				additionalParameters.getNumberOfThreads());
	}


	@Test
	public void testWithPathsAndThreadsParam() throws IOException, ParseException {

		int numThreads = 3;
		String[] args = new String[] { "src/test/resources/warc/ch_web_2016.00046.3records.test.warc", "target/", "-t",
				String.valueOf(numThreads) };
		CommandLine commandLine = this.parse(args);
		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(Path.class), Mockito.any(Path.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertEquals(false, additionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				additionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(numThreads, additionalParameters.getNumberOfThreads());
	}


	@Test
	public void testWithPathsAndInvalidThreadsParam() throws IOException, ParseException {

		int numThreads = 0;
		String[] args = new String[] { "src/test/resources/warc/empty.warc", "target/", "-t",
				String.valueOf(numThreads) };
		CommandLine commandLine = this.parse(args);
		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(Path.class), Mockito.any(Path.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertEquals(false, additionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				additionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(WarcExtractorAction.MINIMUM_NUMBER_OF_THREADS, additionalParameters.getNumberOfThreads());
	}


	@Test
	public void testUsingStreams() throws IOException, ParseException {

		String[] args = new String[] { "-s" };
		CommandLine commandLine = this.parse(args);
		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(InputStream.class), Mockito.any(OutputStream.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertEquals(false, additionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				additionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				additionalParameters.getNumberOfThreads());
	}


	@Test
	public void testWithCompression() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/ch_web_2016.00046.3records.test.warc", "target/",
				"-c" };
		CommandLine commandLine = this.parse(args);
		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(Path.class), Mockito.any(Path.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertTrue(additionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				additionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				additionalParameters.getNumberOfThreads());
	}


	@Test
	public void testWithMinLineLength() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/ch_web_2016.00046.3records.test.warc",
				"target/out.source", "-m", "3" };
		CommandLine commandLine = this.parse(args);

		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(Path.class), Mockito.any(Path.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertEquals(false, additionalParameters.isCompressed());
		Assert.assertEquals(3, additionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				additionalParameters.getNumberOfThreads());
	}


	@Test
	public void testWithMinDocumentLength() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/ch_web_2016.00046.3records.test.warc",
				"target/out.source", "-n", "100" };
		CommandLine commandLine = this.parse(args);

		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(Path.class), Mockito.any(Path.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertEquals(false, additionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				additionalParameters.getMinLineLength());
		Assert.assertEquals(100,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				additionalParameters.getNumberOfThreads());
	}


	@Test
	public void testWithMaxEncodingErrors() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/ch_web_2016.00046.3records.test.warc",
				"target/out.source", "-e", "20" };
		CommandLine commandLine = this.parse(args);

		ArgumentCaptor<WarcExtractorAdditionalParameters> additionalParametersCaptor = ArgumentCaptor
				.forClass(WarcExtractorAdditionalParameters.class);

		Mockito.doReturn(this.warcExtractorMock).when(this.warcExtractorActionSpy)
				.createNewWarcExtractor(additionalParametersCaptor.capture());

		this.warcExtractorActionSpy.execute(commandLine, args);

		Mockito.verify(this.warcExtractorMock).extract(Mockito.any(Path.class), Mockito.any(Path.class));

		WarcExtractorAdditionalParameters additionalParameters = additionalParametersCaptor.getValue();

		Assert.assertNotNull(additionalParameters);
		Assert.assertEquals(false, additionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				additionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				additionalParameters.getMinDocumentLength());
		Assert.assertEquals(20,
				additionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				additionalParameters.getNumberOfThreads());
	}
}
