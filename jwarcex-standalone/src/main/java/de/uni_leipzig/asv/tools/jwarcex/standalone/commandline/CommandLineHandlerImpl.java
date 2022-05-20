package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import com.google.common.collect.Lists;

import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions.CommandLineAction;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions.LoggingCommandLineAction;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions.WarcExtractorAction;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options.CommandLineOptions;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options.JWarcExCommandLineOptions;

/**
 * Handles the command line arguments provided by this class in order to use
 * {@link ParallelWarcExtractor}.
 */
public class CommandLineHandlerImpl implements CommandLineHandler {

	/**
	 * The CommandLineParser, which manages arguments, options and the command line help.
	 */
	private final CommandLineParser parser = new DefaultParser();

	/**
	 * Holds available command line options for JWarcEx.
	 */
	private final CommandLineOptions commandLineOptions = new JWarcExCommandLineOptions();

	/**
	 * A list of command line actions, which allow for the handling of custom options. They will be
	 * executed in the list's order.
	 */
	private final List<CommandLineAction> commandLineActions = new ArrayList<CommandLineAction>();


	public CommandLineHandlerImpl() {

		this(Lists.newArrayList(new LoggingCommandLineAction(), new WarcExtractorAction()));
	}


	/**
	 * Fur testing purposes only
	 */
	protected CommandLineHandlerImpl(List<CommandLineAction> actions) {

		for (CommandLineAction action : actions) {

			this.commandLineActions.add(action);
		}
	}


	@Override
	public void handleArgs(String[] args) {

		this.parseArgs(args, this.parser);
	}


	private void parseArgs(String[] args, CommandLineParser commandLineParser) {

		try {

			CommandLine line = commandLineParser.parse(this.commandLineOptions.getOptions(), args);
			this.tryToParseArgs(args, line);

		} catch (Throwable e) {

			throw new CommandLineException("Non-recoverable error encountered with processing command line arguments",
					e);
		}
	}


	private void tryToParseArgs(String[] args, CommandLine line) throws ParseException {

		String[] remainingArgs = line.getArgs();

		if (line.hasOption("h")) {

			this.printHelp();
			return;
		}

		if (this.validateArgs(remainingArgs, line)) {

			for (CommandLineAction commandLineAction : this.commandLineActions) {

				commandLineAction.execute(line, remainingArgs);
			}
		}
	}


	/**
	 * Validates the given combination of arguments, which must either provide input and output files,
	 * or define the streaming flag (-s).
	 *
	 * @param args
	 *            the remaining unused arguments
	 * @param commandLine
	 *            command line object
	 * @return true, if the arguments are valid, false otherwise.
	 */
	protected boolean validateArgs(String[] args, CommandLine commandLine) {

		String[] remainingArgs = commandLine.getArgs();

		if (!commandLine.hasOption("s") && remainingArgs.length < 2) {

			// Use System.err on purpose here (instead if a logger): when this occurs the warc
			// extraction will not start.
			System.err.println("Error: Required arguments 'warcFile', 'sourceFile' missing");
			this.printHelp();

			return false;
		}

		return true;
	}


	protected void printHelp() {

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("JWarcEx [warcFile] [sourceFile] [<args>]\n"
				+ "(If streaming mode is used, the arguments [warcFile] and [sourceFile] are ommitted. System.in and "
				+ "System.out will be used instead.)\n\n", this.commandLineOptions.getOptions());
	}

}
