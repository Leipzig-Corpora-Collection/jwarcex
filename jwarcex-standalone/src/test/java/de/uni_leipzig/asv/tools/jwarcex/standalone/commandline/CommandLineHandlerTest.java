package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions.CommandLineAction;

@RunWith(MockitoJUnitRunner.class)
public class CommandLineHandlerTest {

	private CommandLineHandlerImpl commandLineHandlerImplOriginal = new CommandLineHandlerImpl();

	private CommandLineHandlerImpl commandLineHandlerImplSpy;

	@Mock
	private CommandLineAction commandLineActionMock;


	@Before
	public void before() {

		this.commandLineHandlerImplSpy = new CommandLineHandlerImpl(Arrays.asList(this.commandLineActionMock));
		this.commandLineHandlerImplSpy = Mockito.spy(this.commandLineHandlerImplSpy);
	}


	@Test
	public void testPrintHelp() throws IOException {

		String[] args = new String[] { "-h" };

		Mockito.doCallRealMethod().when(this.commandLineHandlerImplSpy).handleArgs(args);

		this.commandLineHandlerImplSpy.handleArgs(args);

		Mockito.verify(this.commandLineHandlerImplSpy).printHelp();
	}


	@Test
	public void testPrintHelpWithWrongArgs() throws IOException {

		// -h should win
		String[] args = new String[] { "-t", "3", "-h" };

		Mockito.doCallRealMethod().when(this.commandLineHandlerImplSpy).handleArgs(args);

		this.commandLineHandlerImplSpy.handleArgs(args);

		Mockito.verify(this.commandLineHandlerImplSpy).printHelp();
	}


	@Test
	public void testWithPathsAndTooFewArguments() throws IOException {

		String[] args = new String[] { "src/test/resources/warc/ch_web_2016.00046.3records.test.warc" };

		Mockito.doNothing().when(this.commandLineHandlerImplSpy).printHelp();

		this.commandLineHandlerImplSpy.handleArgs(args);

		Mockito.verify(this.commandLineHandlerImplSpy).printHelp();
		Mockito.verify(this.commandLineActionMock, Mockito.never())
				.execute(Mockito.any(CommandLine.class), Mockito.any(String[].class));
	}


	// exception should only be caught, do not test System.err here
	@Test
	public void testWithPathsAndParseException() throws IOException {

		String[] args = new String[] { "src/test/resources/warc/empty.warc", "target/out.source",
				"-t", "--invalid_param" };

		this.commandLineHandlerImplOriginal.handleArgs(args);
	}


	@Test(expected = CommandLineException.class)
	public void testWithPathsAndFileDoesNotExist() throws IOException {

		String[] args = new String[] { "src/test/resources/warc/ad.warc", "target/out.source", "-t" };
		this.commandLineHandlerImplOriginal.handleArgs(args);
	}
}
