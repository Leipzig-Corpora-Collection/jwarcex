package de.uni_leipzig.asv.tools.jwarcex.core.writer;

import java.io.IOException;

import de.uni_leipzig.asv.tools.jwarcex.text_extraction.structures.ProcessedWarcDocument;

/**
 * Converts {@link ProcessedWarcDocument} into an arbitrary string representation and write it to a
 * stream.
 */
public interface WarcWriter {

	/**
	 * Writes a the string representation of a single {@link ProcessedWarcDocument}..
	 *
	 * @param processedWarcDocument
	 *            a processed warc document
	 * @throws IOException
	 */
	void write(ProcessedWarcDocument processedWarcDocument) throws IOException;


	/**
	 * Flushes the stream.
	 */
	void flush();
}
