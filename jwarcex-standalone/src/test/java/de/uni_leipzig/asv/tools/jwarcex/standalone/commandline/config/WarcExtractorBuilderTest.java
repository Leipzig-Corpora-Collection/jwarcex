package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.CorrectingTextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.OutputFormat;
import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;

public class WarcExtractorBuilderTest {

	@Test
	public void testBuildWarcExtractor() {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				false, 10, 100, 3);
		Assert.assertFalse(warcExtractorAdditionalParameters.isContentExtractionEnabled());

		ParallelWarcExtractor parallelWarcExtractor = WarcExtractorBuilder
				.buildWarcExtractor(warcExtractorAdditionalParameters);

		Assert.assertNotNull(parallelWarcExtractor);

		TextExtractorImpl textExtractorImpl = (TextExtractorImpl) parallelWarcExtractor.getTextExtractor();

		Assert.assertFalse(parallelWarcExtractor.isCompressed());
		Assert.assertNull(textExtractorImpl.getDomContentExtrator());

		// TODO: test minLineLength, minDocumentLength
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				parallelWarcExtractor.getNumberOfThreads());
	}


	@Test
	public void testBuildWarcExtractorWithCompression() {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				true, 10, 100, 3);
		Assert.assertFalse(warcExtractorAdditionalParameters.isContentExtractionEnabled());

		ParallelWarcExtractor parallelWarcExtractor = WarcExtractorBuilder
				.buildWarcExtractor(warcExtractorAdditionalParameters);

		Assert.assertNotNull(parallelWarcExtractor);

		Assert.assertTrue(parallelWarcExtractor.isCompressed());
		// TODO: test minLineLength, minDocumentLength
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				parallelWarcExtractor.getNumberOfThreads());
	}


	@Test
	public void testBuildWarcExtractorWithOutputFormat() throws ParseException {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				false, 10, 100, 3);
		warcExtractorAdditionalParameters.setOutputFormat(OutputFormat.XML);
		Assert.assertFalse(warcExtractorAdditionalParameters.isContentExtractionEnabled());

		ParallelWarcExtractor parallelWarcExtractor = WarcExtractorBuilder
				.buildWarcExtractor(warcExtractorAdditionalParameters);

		Assert.assertNotNull(parallelWarcExtractor);

		Assert.assertFalse(parallelWarcExtractor.isCompressed());
		// TODO: test minLineLength, minDocumentLength
		Assert.assertEquals(OutputFormat.XML, parallelWarcExtractor.getOutputFormat());
	}


	@Test
	public void testBuildWarcExtractorWithNumThreads() throws ParseException {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				false, 10, 100, 3);
		warcExtractorAdditionalParameters.setNumberOfThreads(10);
		Assert.assertFalse(warcExtractorAdditionalParameters.isContentExtractionEnabled());

		ParallelWarcExtractor parallelWarcExtractor = WarcExtractorBuilder
				.buildWarcExtractor(warcExtractorAdditionalParameters);

		Assert.assertNotNull(parallelWarcExtractor);

		Assert.assertFalse(parallelWarcExtractor.isCompressed());
		// TODO: test minLineLength, minDocumentLength
		Assert.assertEquals(10, parallelWarcExtractor.getNumberOfThreads());
	}

	@Test
	public void testBuildWarcExtractorWithContentExtractionEnabled() {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				false, 10, 100, 3);
		warcExtractorAdditionalParameters.setContentExtractionEnabled(true);

		ParallelWarcExtractor parallelWarcExtractor = WarcExtractorBuilder
				.buildWarcExtractor(warcExtractorAdditionalParameters);

		Assert.assertNotNull(parallelWarcExtractor);

		CorrectingTextExtractor CorrectingTextExtractor =
				(CorrectingTextExtractor) parallelWarcExtractor.getTextExtractor();
		TextExtractorImpl textExtractorImpl = (TextExtractorImpl) CorrectingTextExtractor.getBaseTextExtractor();

		Assert.assertFalse(parallelWarcExtractor.isCompressed());
		Assert.assertNotNull(textExtractorImpl.getDomContentExtrator());

		// TODO: test minLineLength, minDocumentLength
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				parallelWarcExtractor.getNumberOfThreads());
	}

}
