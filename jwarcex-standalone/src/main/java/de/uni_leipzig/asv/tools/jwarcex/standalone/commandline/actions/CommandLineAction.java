package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions;

import org.apache.commons.cli.CommandLine;

/**
 * A CommandLineAction is a general action which uses the information from the command line input to
 * perform specific tasks.
 */
public interface CommandLineAction {

	/**
	 * Executes an specific task using the data command line parameters.
	 *
	 * @param commandLine
	 *            a CommandLine object
	 * @param remainingArgs
	 *            the remaining args
	 */
	void execute(CommandLine commandLine, String[] remainingArgs);
}
