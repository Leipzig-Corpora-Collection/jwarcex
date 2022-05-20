package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config;

import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.OutputFormat;
import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;

public class WarcExtractorBuilderTest {

	@Test
	public void testBuildWarcExtractor() throws ParseException {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				false, 10, 100, 3);

		ParallelWarcExtractor parallelWarcExtractor = WarcExtractorBuilder
				.buildWarcExtractor(warcExtractorAdditionalParameters);

		Assert.assertNotNull(parallelWarcExtractor);

		Assert.assertFalse(parallelWarcExtractor.isCompressed());
		// TODO: test minLineLength, minDocumentLength
		Assert.assertEquals(ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT,
				parallelWarcExtractor.getNumberOfThreads());
	}


	@Test
	public void testBuildWarcExtractorWithCompression() throws ParseException {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				true, 10, 100, 3);

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

		ParallelWarcExtractor parallelWarcExtractor = WarcExtractorBuilder
				.buildWarcExtractor(warcExtractorAdditionalParameters);

		Assert.assertNotNull(parallelWarcExtractor);

		Assert.assertFalse(parallelWarcExtractor.isCompressed());
		// TODO: test minLineLength, minDocumentLength
		Assert.assertEquals(10, parallelWarcExtractor.getNumberOfThreads());
	}

}
