package de.uni_leipzig.asv.tools.jwarcex.text_extraction;

import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

/**
 * Tries to spot encoding errors and drops documents which provide enough evidence to be suspected
 * of having encoding issues.
 * <p>
 * The detection works simply by counting the occurrences of the unicode replacement character (�).
 * The parameter additional parameter `PARAMETER_MAX_OCCURRENCES` can be passed to the constructors
 * `additionalParameters` Map to configure the maximum tolerable number up to which the document is
 * still valid.
 */
public class CorrectingTextExtractor extends TextExtractorImpl implements TextExtractor {

	private static final Logger LOGGER = LogManager.getLogger(CorrectingTextExtractor.class);

	/**
	 * "Maximum number of replacement chars"-Parameter name.
	 */
	public static final String PARAMETER_MAX_OCCURRENCES = "maxReplacementCharsOccurrences";

	/**
	 * Default value for parameter "maxReplacementCharOccurrences".
	 */
	public static final int PARAMETER_MAX_OCCURRENCES_DEFAULT = 3;

	/**
	 * The maximum tolerable threshold of encountered replacement characters.
	 */
	private final int maximumReplacementCharsOccurrences;

	/**
	 * The question mark enclosed in a black rectangle containing a question mark, which is shown
	 * whenever encoding issues occur.
	 *
	 * E.g., "f�r" instead of "für".
	 *
	 * Note: This is the UTF-16 value for the replacement character because the Java strings are stored
	 * as UTF-16 internally.
	 */
	private static final int REPLACEMENT_CHARACTER = 0xFFFD;

	private final TextExtractor baseTextExtractor;


	/**
	 * For testing purposes only.
	 */
	protected CorrectingTextExtractor() {

		this(new TextExtractorImpl(), PARAMETER_MAX_OCCURRENCES_DEFAULT);
	}


	public CorrectingTextExtractor(TextExtractor baseTextExtractor, int maximumReplacementCharsOccurrences) {

		this.baseTextExtractor = baseTextExtractor;
		this.maximumReplacementCharsOccurrences = maximumReplacementCharsOccurrences;

		this.validateMaxReplacementChars();
	}


	private void validateMaxReplacementChars() {

		if (this.maximumReplacementCharsOccurrences < -1) {

			throw new IllegalArgumentException("Invalid paramater value for " + this.maximumReplacementCharsOccurrences
					+ " (" + String.valueOf(this.maximumReplacementCharsOccurrences) + ")");
		}
	}


	@Override
	public ProcessedWarcDocument getText(RawWarcDocument rawWarcDocument, Charset charset) {

		ProcessedWarcDocument processedWarcDocument = this.baseTextExtractor.getText(rawWarcDocument, charset);

		if (processedWarcDocument == null) {

			return null;
		} else if (this.maximumReplacementCharsOccurrences == -1) {

			return processedWarcDocument;
		}

		String extractedText = processedWarcDocument.getContent();
		// TODO: is the check "extractedText != null" still necessary?
		if (extractedText != null
				&& this.containsEncodingErrors(extractedText, this.maximumReplacementCharsOccurrences)) {

			int max = Math.min(100, extractedText.length());
			LOGGER.warn("Dropping document due to suspected encoding issues: {}.",
					extractedText.substring(0, max) + "...");

			return null;
		}

		return processedWarcDocument;
	}


	protected boolean containsEncodingErrors(String text, int threshold) {

		int occurrences = 0;
		int lastIndex = 0;

		while ((lastIndex = text.indexOf(REPLACEMENT_CHARACTER, lastIndex + 1)) != -1) {

			occurrences++;

			if (occurrences >= threshold) {

				return true;
			}
		}

		return false;
	}

}
