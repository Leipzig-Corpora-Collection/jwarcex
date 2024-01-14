package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.OutputFormat;
import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options.JWarcExCommandLineOptions;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.CorrectingTextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;

public class WarcExtractorParametersBuilderTest {

	private CommandLineParser parser = new DefaultParser();


	@Test
	public void testBuildParameterObjectFromCommandLine() throws ParseException {

		CommandLine commandLine = parser.parse(new JWarcExCommandLineOptions().getOptions(),
				new String[] {});

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);

		Assert.assertFalse(warcExtractorAdditionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				warcExtractorAdditionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				warcExtractorAdditionalParameters.getNumberOfThreads());
	}


	@Test
	public void testBuildParameterObjectFromCommandLineWithCompression() throws ParseException {

		CommandLine commandLine = parser.parse(new JWarcExCommandLineOptions().getOptions(),
				new String[] { "-c" });

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);

		Assert.assertTrue(warcExtractorAdditionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				warcExtractorAdditionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				warcExtractorAdditionalParameters.getNumberOfThreads());
	}


	@Test
	public void testBuildParameterObjectFromCommandLineWithMinLineLength() throws ParseException {

		int newMinLineLengthValue = 10;

		Assert.assertNotEquals(newMinLineLengthValue, TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT);

		CommandLine commandLine = parser.parse(new JWarcExCommandLineOptions().getOptions(),
				new String[] { "-m", String.valueOf(newMinLineLengthValue) });

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);

		Assert.assertFalse(warcExtractorAdditionalParameters.isCompressed());
		Assert.assertEquals(10, warcExtractorAdditionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				warcExtractorAdditionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				warcExtractorAdditionalParameters.getNumberOfThreads());
	}


	@Test
	public void testBuildParameterObjectFromCommandLineWithMinDocumentLength() throws ParseException {

		int newMinDocumentLengthValue = 40;

		Assert.assertNotEquals(newMinDocumentLengthValue, TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT);

		CommandLine commandLine = parser.parse(new JWarcExCommandLineOptions().getOptions(),
				new String[] { "-n", String.valueOf(newMinDocumentLengthValue) });

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);

		Assert.assertFalse(warcExtractorAdditionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinLineLength());
		Assert.assertEquals(newMinDocumentLengthValue,
				warcExtractorAdditionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				warcExtractorAdditionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				warcExtractorAdditionalParameters.getNumberOfThreads());
	}


	@Test
	public void testBuildParameterObjectFromCommandLineWithOutputFormatXml() throws ParseException {

		Assert.assertNotEquals("xml", ParallelWarcExtractor.PARAMETER_OUTPUT_FORMAT_DEFAULT.toString());

		CommandLine commandLine = parser.parse(new JWarcExCommandLineOptions().getOptions(),
				new String[] { "-o", "xml" });

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);

		Assert.assertFalse(warcExtractorAdditionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				warcExtractorAdditionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(OutputFormat.XML, warcExtractorAdditionalParameters.getOutputFormat());
	}


	@Test
	public void testBuildParameterObjectFromCommandLineWithOutputFormatWet() throws ParseException {

		Assert.assertNotEquals("wet", ParallelWarcExtractor.PARAMETER_OUTPUT_FORMAT_DEFAULT.toString());

		CommandLine commandLine = parser.parse(new JWarcExCommandLineOptions().getOptions(),
				new String[] { "-o", "wet" });

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);

		Assert.assertFalse(warcExtractorAdditionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				warcExtractorAdditionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(OutputFormat.WET, warcExtractorAdditionalParameters.getOutputFormat());
	}


	@Test
	public void testBuildParameterObjectFromCommandLineWithNumThreads() throws ParseException {

		int newNumThreadsValue = 4;

		Assert.assertNotEquals(newNumThreadsValue, ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS);

		CommandLine commandLine = parser.parse(new JWarcExCommandLineOptions().getOptions(),
				new String[] { "-t", String.valueOf(newNumThreadsValue) });

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);

		Assert.assertFalse(warcExtractorAdditionalParameters.isCompressed());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinLineLength());
		Assert.assertEquals(TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT,
				warcExtractorAdditionalParameters.getMinDocumentLength());
		Assert.assertEquals(CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT,
				warcExtractorAdditionalParameters.getMaxEncodingErrors());
		Assert.assertEquals(newNumThreadsValue, warcExtractorAdditionalParameters.getNumberOfThreads());
	}
}
