package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options.CommandLineOptions;
import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.options.JWarcExCommandLineOptions;

public class LoggingCommandLineActionTest {

	private LoggingCommandLineAction loggingCommandLineAction = new LoggingCommandLineAction();

	private CommandLineParser parser = new DefaultParser();

	private CommandLineOptions options = new JWarcExCommandLineOptions();


	private CommandLine parse(String[] args) throws ParseException {

		return this.parser.parse(this.options.getOptions(), args);
	}


	@Test
	public void testSetLogLevel() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/empty.warc", "target/out.source",
				"-l", "trace" };
		CommandLine commandLine = this.parse(args);

		this.loggingCommandLineAction.execute(commandLine, args);

		// no exception here
	}


	@Test
	public void testSetLogLevelUppercase() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/empty.warc", "target/out.source",
				"-l", "TRACE" };
		CommandLine commandLine = this.parse(args);

		this.loggingCommandLineAction.execute(commandLine, args);

		// no exception here
	}


	@Test(expected = IllegalArgumentException.class)
	public void testSetLogLevelInvalidString() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/empty.warc", "target/out.source",
				"-l", "ABC" };
		CommandLine commandLine = this.parse(args);

		this.loggingCommandLineAction.execute(commandLine, args);
	}


	@Test
	public void testSetLogFile() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/empty.warc", "target/out.source",
				"-f", "target/out.log" };
		CommandLine commandLine = this.parse(args);

		this.loggingCommandLineAction.execute(commandLine, args);

		// no exception here
	}


	@Test(expected = IllegalStateException.class)
	public void testSetLogFileInvalidArgs() throws IOException, ParseException {

		String[] args = new String[] { "src/test/resources/warc/empty.warc", "target/out.source",
				"-f", "target/" };
		CommandLine commandLine = this.parse(args);

		this.loggingCommandLineAction.execute(commandLine, args);
	}
}
