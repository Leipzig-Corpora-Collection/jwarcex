package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.OutputFormat;
import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;

public class WarcExtractorAdditionalParameters {

	// core settings
	private final boolean isCompressed;
	private final int minLineLength;
	private final int minDocumentLength;
	private final int maxEncodingErrors;

	// additional settings
	private int numberOfThreads = ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT;
	private OutputFormat outputFormat = OutputFormat.SOURCE;


	public WarcExtractorAdditionalParameters(boolean isCompressed, int minLineLength, int minDocumentLength,
			int maxEncodingErrors) {

		this.isCompressed = isCompressed;
		this.minLineLength = minLineLength;
		this.minDocumentLength = minDocumentLength;
		this.maxEncodingErrors = maxEncodingErrors;
	}


	public boolean isCompressed() {

		return this.isCompressed;
	}


	public int getMinLineLength() {

		return this.minLineLength;
	}


	public int getMinDocumentLength() {

		return this.minDocumentLength;
	}


	public int getMaxEncodingErrors() {

		return this.maxEncodingErrors;
	}


	public int getNumberOfThreads() {

		return numberOfThreads;
	}


	public void setNumberOfThreads(int numberOfThreads) {

		this.numberOfThreads = numberOfThreads;
	}


	public OutputFormat getOutputFormat() {

		return outputFormat;
	}


	public void setOutputFormat(OutputFormat outputFormat) {

		this.outputFormat = outputFormat;
	}

}
