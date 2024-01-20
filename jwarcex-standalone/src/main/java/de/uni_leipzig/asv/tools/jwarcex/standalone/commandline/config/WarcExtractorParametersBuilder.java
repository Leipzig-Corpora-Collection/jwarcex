package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config;

import org.apache.commons.cli.CommandLine;

import de.uni_leipzig.asv.tools.jwarcex.core.constant.OutputFormat;
import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.CorrectingTextExtractor;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;

public class WarcExtractorParametersBuilder {

	/**
	 * Default value for the number of threads parameter (= number of warc extraction threads).
	 */
	public static final int DEFAULT_NUMBER_OF_THREADS = ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT;


	private WarcExtractorParametersBuilder() {

		// prevent initialization
	}


	public static WarcExtractorAdditionalParameters buildParameterObjectFromCommandLine(CommandLine commandLine) {

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = buildParametersObject(commandLine);

		int numberOfThreads = DEFAULT_NUMBER_OF_THREADS;

		if (commandLine.hasOption("t")) {

			numberOfThreads = ParameterUtil.parseNumberOfThreads(commandLine.getOptionValue("t"));
		}

		warcExtractorAdditionalParameters.setNumberOfThreads(numberOfThreads);

		if (commandLine.hasOption("x")) {
			warcExtractorAdditionalParameters.setContentExtractionEnabled(true);
		}

		return warcExtractorAdditionalParameters;
	}


	protected static WarcExtractorAdditionalParameters buildParametersObject(CommandLine commandLine) {

		boolean isCompressed = commandLine.hasOption("c");

		Integer minLineLength = TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT;
		if (commandLine.hasOption("m")) {

			minLineLength = ParameterUtil.parseIntParameter(commandLine.getOptionValue("m"), "minLineLength");
		}

		Integer minDocumentLength = TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT;
		if (commandLine.hasOption("n")) {

			minDocumentLength = ParameterUtil.parseIntParameter(commandLine.getOptionValue("n"), "minDocumentLength");
		}

		Integer maxEncodingErrors = CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT;
		if (commandLine.hasOption("e")) {

			maxEncodingErrors = ParameterUtil.parseIntParameter(commandLine.getOptionValue("e"),
					CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES);
		}

		WarcExtractorAdditionalParameters warcExtractorAdditionalParameters = new WarcExtractorAdditionalParameters(
				isCompressed, minLineLength, minDocumentLength, maxEncodingErrors);

		if (commandLine.hasOption("o")) {

			String optionValue = commandLine.getOptionValue("o");
			if (optionValue.equals("source")) {

				warcExtractorAdditionalParameters.setOutputFormat(OutputFormat.SOURCE);
			} else if (optionValue.equals("jsonl")) {

				warcExtractorAdditionalParameters.setOutputFormat(OutputFormat.JSONL);
			} else if (optionValue.equals("wet")) {

				warcExtractorAdditionalParameters.setOutputFormat(OutputFormat.WET);
			} else if (optionValue.equals("xml")) {

				warcExtractorAdditionalParameters.setOutputFormat(OutputFormat.XML);
			}  else {
				throw new IllegalArgumentException("Invalid argument for output format: " + optionValue);
			}

		}

		return warcExtractorAdditionalParameters;
	}

}
