package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.actions;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Provides options to set the log level and to define an output log file in which all output will
 * be redirected.
 */
public class LoggingCommandLineAction implements CommandLineAction {

	/**
	 * Log pattern (see log4j2 docs for details).
	 */
	private static final String LOG_PATTERN = "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n";

	/**
	 * Name of the base logger.
	 */
	private static final String BASE_LOGGER_NAME = "de.uni_leipzig.asv.tools.jwarcex";

	/**
	 * The name of the default appender (defined in src/main/resources/log4j2.xml).
	 */
	private static final String DEFAULT_APPENDER_NAME = "Console";


	@Override
	public void execute(CommandLine commandLine, String[] remainingArgs) {

		if (commandLine.hasOption("l")) {

			this.updateLogLevel(commandLine);
		}

		if (commandLine.hasOption("f")) {

			this.redirectLoggingOutput(commandLine);
		}
	}


	protected void updateLogLevel(CommandLine commandLine) {

		// we do not to check this, since Level.valueOf throws suitable exceptions
		String logLevelString = commandLine.getOptionValue("l");

		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		Level level = Level.valueOf(logLevelString);

		LoggerConfig loggerConfig = config.getRootLogger();
		loggerConfig.setLevel(level);

		LoggerConfig basePackageLoggerConfig = config.getLoggerConfig(BASE_LOGGER_NAME);
		basePackageLoggerConfig.setLevel(level);

		ctx.updateLoggers();
	}


	private void redirectLoggingOutput(CommandLine commandLine) {
		String logFileString = commandLine.getOptionValue("f");

		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();

		// There are two logger which must be altered, 1) the root logger...
		LoggerConfig rootLoggerConfig = config.getRootLogger();
		rootLoggerConfig.removeAppender(DEFAULT_APPENDER_NAME);

		Appender appender = FileAppender.newBuilder().setName(DEFAULT_APPENDER_NAME).withFileName(logFileString)
				.setLayout(PatternLayout.newBuilder().withPattern(LOG_PATTERN).build())
				.withAppend(false).build();
		appender.start();

		rootLoggerConfig.addAppender(appender, rootLoggerConfig.getLevel(), null);

		// ..and 2) the logger for the base package
		LoggerConfig basePackageLoggerConfig = config.getLoggerConfig(BASE_LOGGER_NAME);
		basePackageLoggerConfig.removeAppender(DEFAULT_APPENDER_NAME);
		basePackageLoggerConfig.addAppender(appender, rootLoggerConfig.getLevel(), null);

		ctx.updateLoggers();
	}

}
