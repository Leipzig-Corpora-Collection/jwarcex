package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config;

import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetector;
import de.uni_leipzig.asv.tools.jwarcex.encoding_detection.EncodingDetectorImpl;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.CorrectingTextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;

public class WarcExtractorBuilder {

	private WarcExtractorBuilder() {

		// prevent initialization
	}


	public static ParallelWarcExtractor buildWarcExtractor(WarcExtractorAdditionalParameters additionalParameters) {

		EncodingDetector encodingDetector = new EncodingDetectorImpl();

		TextExtractor baseTextExtractor = new TextExtractorImpl(additionalParameters.getMinLineLength(),
				additionalParameters.getMinDocumentLength(),
				false,
				additionalParameters.isContentExtractionEnabled());
		TextExtractor textExtractor = new CorrectingTextExtractor(baseTextExtractor,
				additionalParameters.getMaxEncodingErrors());

		// TODO: handle numThreads=1 separately?
		ParallelWarcExtractor warcExtractor = new ParallelWarcExtractor(encodingDetector, textExtractor,
				additionalParameters.getOutputFormat(),
				additionalParameters.getNumberOfThreads());

		// TODO: test
		warcExtractor.setCompressed(additionalParameters.isCompressed());

		return warcExtractor;
	}

}
