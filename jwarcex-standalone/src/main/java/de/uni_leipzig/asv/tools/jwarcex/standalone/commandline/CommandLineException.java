package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline;

/**
 * A custom RuntimeException, which is used to indicate non-recoverable errors.
 */
public class CommandLineException extends RuntimeException {

	private static final long serialVersionUID = 1L;


	public CommandLineException(String message, Throwable cause) {

		super(message, cause);
	}
}
