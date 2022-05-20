package de.uni_leipzig.asv.tools.jwarcex.standalone;

import java.io.IOException;

import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.CommandLineHandler;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.CommandLineHandlerImpl;

/**
 * This class is inteded to use {@link ParallelWarcExtractor} from the command line. For
 * programmatic use simply create an instance of {@link ParallelWarcExtractor}.
 */
public class JWarcEx {

	/**
	 * See {@link CommandLineHandlerImpl} for possible command line arguments.
	 */
	public static void main(String[] args) throws IOException {

		CommandLineHandler commandLineHandler = new CommandLineHandlerImpl();
		commandLineHandler.handleArgs(args);
	}
}
