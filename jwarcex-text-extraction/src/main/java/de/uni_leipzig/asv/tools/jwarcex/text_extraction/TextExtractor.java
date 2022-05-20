package de.uni_leipzig.asv.tools.jwarcex.text_extraction;

import java.nio.charset.Charset;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;
import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.RawWarcDocument;

/**
 * A TextExtractor parses HTML strings, extracts relevant text and returns it as a contiguous text.
 */
public interface TextExtractor {

	/**
	 * Extracts the text from a given warc document.
	 *
	 * @param rawWarcDocument
	 *            a raw warc documnet
	 * @return the processed warc document containing extracted text and meta data
	 */
	ProcessedWarcDocument getText(RawWarcDocument rawWarcDocument, Charset charset);
}
