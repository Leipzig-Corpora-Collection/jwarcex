package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options;

import org.apache.commons.cli.Options;

import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions.WarcExtractorAction;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.CorrectingTextExtractor;

/**
 * Defines the command line options which will be provided to conviently access
 * {@link ParallelWarcExtractor} using the command line.
 */
public class JWarcExCommandLineOptions implements CommandLineOptions {

	@Override
	public Options getOptions() {

		Options options = new Options();

		options.addOption("t", "threads", true,
				"Number of threads. (Default: " + ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT + ")");
		options.addOption("o", "output_format", true,
				"Number of threads. (Possible values: [source, jsonl, wet, xml]; Default: source)");
		options.addOption("h", "help", false, "Prints this help.");
		options.addOption("c", "compressed", false, "Enables reading from gzip compressed input. (Default: "
				+ WarcExtractorAction.DEFAULT_IS_COMPRESSED + ")");
		options.addOption("s", "streaming_mode", false,
				"Enables reading and writing from System.in to System.out instead of using files.");
		options.addOption("l", "log_level", true,
				"Sets the log level for all loggers belonging to JWarcEx (Default: info).");
		options.addOption("f", "log_file", true, "Redirects all logging output to this file.");
		options.addOption("m", "line_length", true,
				"The minimum number of characters, which a single block-level element must contain. " + "(Default: "
						+ WarcExtractorAction.DEFAULT_MIN_LINE_LENGTH + ")");
		options.addOption("n", "document_length", true,
				"The minimum number of characters a document's text must have. "
						+ "This count refers to the actual length of the resulting text. Lines dropped by the "
						+ "line_length parameter do not count towards the total number of characters. (Default: "
						+ WarcExtractorAction.DEFAULT_MIN_DOCUMENT_LENGTH + ")");
		options.addOption("e", "max_encoding_errors", true,
				"The maximum number of encoding errors per document (= number of encountered replacement characters) "
						+ "that will be tolerated. Documents crossing this threshold will be dropped. "
						+ "Setting this parameter to -1 will completely disable this functionality. (Default: "
						+ CorrectingTextExtractor.PARAMETER_MAX_OCCURRENCES_DEFAULT + ")");

		options.addOption("X", true,
				"Sets a list of experimental options (comma-separated keys or key-values, e.g. '-X a,b,c=1').");

		return options;
	}
}
