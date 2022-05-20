package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config.WarcExtractorAdditionalParameters;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config.WarcExtractorBuilder;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config.WarcExtractorParametersBuilder;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.TextExtractorImpl;

public class WarcExtractorAction implements CommandLineAction {

	private static final Logger LOGGER = LogManager.getLogger(WarcExtractorAction.class);

	/**
	 * Default value for the minimum line length parameter.
	 */
	public static final int DEFAULT_MIN_LINE_LENGTH = TextExtractorImpl.PARAMETER_MIN_LINE_LENGTH_DEFAULT;

	/**
	 * Default value for the minimum document length parameter.
	 */
	public static final int DEFAULT_MIN_DOCUMENT_LENGTH = TextExtractorImpl.PARAMETER_MIN_DOCUMENT_LENGTH_DEFAULT;

	/**
	 * Smallest valid number to accept as a number of threads value.
	 */
	public static final int MINIMUM_NUMBER_OF_THREADS = 1;

	/**
	 * Default value for the compression parameter.
	 */
	public static final boolean DEFAULT_IS_COMPRESSED = false;


	@Override
	public void execute(CommandLine commandLine, String[] remainingArgs) {

		try {

			this.processArgsForWarcExtraction(commandLine, remainingArgs);

		} catch (Exception e) {

			LOGGER.error("Uncaught exception during warcExtraction: {}", e);
		}
	}


	private void processArgsForWarcExtraction(CommandLine commandLine, String[] remainingArgs) {

		WarcExtractorAdditionalParameters additionalParameters = WarcExtractorParametersBuilder
				.buildParameterObjectFromCommandLine(commandLine);
		ParallelWarcExtractor warcExtractor = this.createNewWarcExtractor(additionalParameters);

		if (commandLine.hasOption("s")) {

			// stream-based warc processing
			warcExtractor.extract(System.in, System.out);

		} else {

			// file-based warc processing
			Path warcPath = Paths.get(remainingArgs[0]);
			Path extractedFilePath = Paths.get(remainingArgs[1]);
			warcExtractor.extract(warcPath, extractedFilePath);
		}
	}


	protected ParallelWarcExtractor createNewWarcExtractor(WarcExtractorAdditionalParameters additionalParameters) {

		return WarcExtractorBuilder.buildWarcExtractor(additionalParameters);
	}
}
