package de.uni_leipzig.asv.tools.jwarcex.standalone.commandline.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_leipzig.asv.tools.jwarcex.core.extract.ParallelWarcExtractor;

public class ParameterUtil {

	private static final Logger LOGGER = LogManager.getLogger(ParameterUtil.class);

	/**
	 * Smallest valid number to accept as a number of threads value.
	 */
	public static final int MINIMUM_NUMBER_OF_THREADS = 1;


	private ParameterUtil() {

		// prevent initialization
	}


	public static int parseNumberOfThreads(String numberOfThreadsString) {

		int numThreads = ParallelWarcExtractor.PARAMETER_NUMBER_OF_THREADS_DEFAULT;

		try {

			numThreads = Integer.parseInt(numberOfThreadsString);
		} catch (Exception e) {

			LOGGER.info("Could not parse threads parameter. Using default value.");
		}

		if (numThreads < MINIMUM_NUMBER_OF_THREADS) {

			LOGGER.info("Invalid value for parameter 'number of threads': {}. Using {} as new value.",
					Integer.valueOf(numThreads), Integer.valueOf(MINIMUM_NUMBER_OF_THREADS));
			numThreads = MINIMUM_NUMBER_OF_THREADS;
		}

		return numThreads;
	}


	public static Integer parseIntParameter(String value, String parameterDescription) {

		try {

			return Integer.parseInt(value);
		} catch (Exception e) {

			LOGGER.error("Could not parse '" + parameterDescription + "' parameter. Using default value.");
			return null;
		}
	}

}
